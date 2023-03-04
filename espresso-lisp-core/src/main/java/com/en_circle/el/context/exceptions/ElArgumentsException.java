package com.en_circle.el.context.exceptions;

import com.en_circle.el.nodes.ElNode;

public class ElArgumentsException extends ElException {

    public ElArgumentsException(String message, ElNode node) {
        super(message, node);
    }
}
