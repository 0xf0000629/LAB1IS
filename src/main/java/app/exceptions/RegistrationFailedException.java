package app.exceptions;

public class RegistrationFailedException extends CityAppException {

    public RegistrationFailedException(Throwable cause) {
        super("Registration failed",
                CityExcCode.REGISTRATION_FAILED,
                cause);
    }
}

