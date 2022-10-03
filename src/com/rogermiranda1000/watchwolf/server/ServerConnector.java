package com.rogermiranda1000.watchwolf.server;

import com.rogermiranda1000.watchwolf.entities.SocketHelper;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ServerConnector implements Runnable, ServerStartNotifier {
    public interface ArrayAdder { public void addToArray(ArrayList<Byte> out, Object []file); }

    /**
     * The only IP allowed to talk to the socket
     */
    private final String allowedIp;

    /**
     * Socket to send server information to the ServersManager (server started/closed)
     */
    private final Socket replySocket;

    /**
     * Socket to receive <allowedIp>'s requests
     */
    private final ServerSocket serverSocket;

    /**
     * Client connected to <serverSocket>
     */
    private Socket clientSocket;

    /**
     * Key used to identify this server, while sending server status replies to the ServersManager
     */
    private final String replyKey;

    /**
     * Minecraft plugin; needed to run sync operations
     */
    private final Plugin plugin;

    /**
     * Implementations of the server petitions
     */
    private final ServerPetition serverPetition;

    public ServerConnector(String allowedIp, int port, Socket reply, String key, Plugin plugin, ServerPetition serverPetition) throws IOException {
        this.allowedIp = allowedIp;
        this.serverSocket = new ServerSocket(port);
        this.plugin = plugin;
        this.serverPetition = serverPetition;

        this.replySocket = reply;
        this.replyKey = key;
    }

    public void close() {
        try {
            this.serverSocket.close();
            if (this.clientSocket != null) this.clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* HELPER FUNCTIONS */
    private static byte []toByteArray(ArrayList<Byte> bytes) {
        byte []r = new byte[bytes.size()];
        for (int x = 0; x < r.length; x++) r[x] = bytes.get(x);
        return r;
    }
    private static void addRaw(ArrayList<Byte> out, Object []file) {
        for (Byte b : (Byte[])file) out.add((byte)b);
    }

    private static void addArray(ArrayList<Byte> out, Object[] array, ArrayAdder arrayAdder) {
        int size = array.length;
        out.add((byte)((size>>8)&0xFF)); // MSB
        out.add((byte)(size&0xFF));      // LSB

        if (size > 0) arrayAdder.addToArray(out, array);
    }

    private static void addString(ArrayList<Byte> out, String str) {
        Byte []arr = new Byte[str.length()];
        for (int n = 0; n < arr.length; n++) arr[n] = (byte)str.charAt(n);
        ServerConnector.addArray(out, arr, ServerConnector::addRaw);
    }

    private static String readString(DataInputStream dis) throws IOException {
        // TODO check if EOF
        // size
        short size = (short) (dis.readByte() << 8); // MSB
        size |= (short) dis.readByte(); // LSB

        // characters
        StringBuilder sb = new StringBuilder();
        for (int n = 0; n < size; n++) sb.append((char)dis.readByte());
        return sb.toString();
    }

    private static short readShort(DataInputStream dis) throws IOException {
        short msb = (short)((short)dis.readByte() << 8);
        return (short)(msb | dis.readByte()); // LSB
    }

    /* server socket */

    @Override
    public void run() {
        while (!this.serverSocket.isClosed()) {
            try {
                this.clientSocket = this.serverSocket.accept();
                System.out.println(this.clientSocket.getInetAddress().getHostAddress() + " - " + this.allowedIp); // TODO deny connection
            } catch (IOException e) {
                e.printStackTrace();
            }

            while (this.clientSocket != null && !this.clientSocket.isClosed()) {
                try {
                    DataInputStream dis = new DataInputStream(this.clientSocket.getInputStream());
                    DataOutputStream dos = new DataOutputStream(this.clientSocket.getOutputStream());

                    byte first = dis.readByte();
                    if (((first) >> 4) != (byte)0b0010) throw new UnexpectedPacketException("The packet must start with '0010', found " + Integer.toBinaryString((first) >> 4) + " (" + Integer.toBinaryString(first) + ")");
                    short group = (short)(dis.readByte() | (((short)first & 0b0000_1111) << 8));
                    this.processGroup(group, dis, dos);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (UnexpectedPacketException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    private void processGroup(short group, DataInputStream dis, DataOutputStream dos) throws IOException, UnexpectedPacketException {
        switch (group) {
            case 0: // NOP
                // TODO extend timeout
                break;

            case 1:
                this.processDefaultGroup(dis, dos);
                break;

            default:
                // TODO send 'unimplemented'
                System.out.println("Unimplemented group: " + group);
        }
    }

    private void processDefaultGroup(DataInputStream dis, DataOutputStream dos) throws IOException, UnexpectedPacketException {
        // TODO implement all
        String nick;
        short operation = SocketHelper.readShort(dis);
        switch (operation) {
            case 0x0001:
                Bukkit.getScheduler().callSyncMethod(this.plugin, () -> {
                    this.serverPetition.stopServer(null);
                    return null;
                }); // TODO notify
                break;

            case 0x0003:
                nick = ServerConnector.readString(dis);
                Bukkit.getScheduler().callSyncMethod(this.plugin, () -> {
                    this.serverPetition.whitelistPlayer(nick);
                    return null;
                });
                break;

            case 0x0004:
                nick = ServerConnector.readString(dis);
                Bukkit.getScheduler().callSyncMethod(this.plugin, () -> {
                    this.serverPetition.opPlayer(nick);
                    return null;
                });
                break;

            default:
                throw new UnexpectedPacketException("Operation " + (int)operation + " from group 1"); // unimplemented by this version, or error
        }
    }

    /* reply interfaces */

    @Override
    public void onServerStart() throws IOException {
        ArrayList<Byte> message = new ArrayList<>();

        // op player header
        message.add((byte) 0b001_1_0000);
        message.add((byte) 0b00000001);
        message.add((byte) 0x00);
        message.add((byte) 0x02);

        ServerConnector.addString(message, replyKey);

        DataOutputStream dos = new DataOutputStream(this.replySocket.getOutputStream());
        dos.write(ServerConnector.toByteArray(message), 0, message.size());
    }
}
