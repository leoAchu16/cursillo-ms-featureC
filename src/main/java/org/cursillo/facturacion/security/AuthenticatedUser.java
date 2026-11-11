package org.cursillo.facturacion.security;

public record AuthenticatedUser(Integer id, String email) {
}