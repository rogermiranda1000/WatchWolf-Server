package com.rogermiranda1000.watchwolf.server;

public interface ServerPetition {
    void opPlayer(String nick);
    void whitelistPlayer(String nick);
    void stopServer(ServerStopNotifier onServerStop);
}
