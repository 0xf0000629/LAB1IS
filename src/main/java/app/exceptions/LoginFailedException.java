package app.exceptions;

public class LoginFailedException extends CityAppException{
    public LoginFailedException() {
        super("Passwords do not match.",
                CityExcCode.LOGIN_FAILED);
    }
}
