package com.twozo.exception.customer;

public class CustomerNotFoundException extends Exception {

    public CustomerNotFoundException(final String message) {
        super(message);
    }
}
