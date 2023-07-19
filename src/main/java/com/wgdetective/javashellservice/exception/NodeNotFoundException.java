package com.wgdetective.javashellservice.exception;

public class NodeNotFoundException extends RuntimeException {

    public NodeNotFoundException(final String msg) {
        super(msg);
    }
}
