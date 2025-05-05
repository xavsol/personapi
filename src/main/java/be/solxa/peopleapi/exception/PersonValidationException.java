package be.solxa.peopleapi.exception;

public class PersonValidationException extends RuntimeException {
    public PersonValidationException(String message) {
        super(message);
    }
}
