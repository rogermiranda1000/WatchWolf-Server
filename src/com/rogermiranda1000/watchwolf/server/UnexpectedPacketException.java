package com.rogermiranda1000.watchwolf.server;

public class UnexpectedPacketException extends RuntimeException {
    public UnexpectedPacketException(String msg) {
        super(msg);
    }
}
