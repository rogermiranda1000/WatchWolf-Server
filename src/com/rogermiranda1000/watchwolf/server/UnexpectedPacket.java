package com.rogermiranda1000.watchwolf.server;

public class UnexpectedPacket extends RuntimeException {
    public UnexpectedPacket(String msg) {
        super(msg);
    }
}
