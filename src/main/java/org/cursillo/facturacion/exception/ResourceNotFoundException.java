// ResourceNotFoundException.java
package org.cursillo.facturacion.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}