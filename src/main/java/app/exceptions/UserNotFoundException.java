package app.exceptions;

public class UserNotFoundException extends CityAppException{
    public UserNotFoundException() {
        super("User doesn't exist.",
                CityExcCode.NOT_A_USER);
    }
}
