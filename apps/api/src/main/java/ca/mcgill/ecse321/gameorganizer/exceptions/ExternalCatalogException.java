package ca.mcgill.ecse321.gameorganizer.exceptions;

import org.springframework.http.HttpStatus;

public class ExternalCatalogException extends RuntimeException {

    private final HttpStatus status;

    public ExternalCatalogException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public ExternalCatalogException(String message, HttpStatus status, Throwable cause) {
        super(message, cause);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
