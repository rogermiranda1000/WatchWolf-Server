package dev.watchwolf.server;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;

/**
 * @author MineX <a href="https://www.spigotmc.org/threads/capturing-console-output.307132/">https://www.spigotmc.org/threads/capturing-console-output.307132/</a>
 */
public class LogAppender extends AbstractAppender implements CommandRunner {
    private static final int WAIT_FOR_MESSAGE_TIMEOUT = 4000;
    private static final int WAIT_BETWEEN_MESSAGES_TIMEOUT = 400;

    private final JavaPlugin plugin;
    private final ArrayList<String> logs = new ArrayList<>();

    public LogAppender(JavaPlugin plugin) {
        // do your calculations here before starting to capture
        super("MyLogAppender", null, null);
        this.plugin = plugin;
        start();
    }

    @Override
    public void append(LogEvent event) {
        // if you don`t make it immutable, then you may have some unexpected behaviours
        LogEvent log = event.toImmutable();

        String msg = log.getMessage().getFormattedMessage();
        synchronized (this) {
            this.logs.add(msg);
        }
    }

    /**
     * Important: this needs to be runned from a sepparate thread
     * @param cmd Command to run
     * @return Response to the command
     */
    @Override
    public String runCommand(String cmd) {
        // clear the previous logs
        synchronized (this) {
            this.logs.clear(); // TODO send a signal to start capturing now
        }

        // run the command
        Bukkit.dispatchCommand(this.plugin.getServer().getConsoleSender(), cmd);

        // get the command log
        try {
            Thread.sleep(400); // TODO implement as did in WatchWolf Client
        } catch (InterruptedException e) {}
        synchronized (this) {
            System.out.println(this.logs.size());
            return String.join("\n", this.logs);
        }
    }
}