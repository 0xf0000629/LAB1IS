package app.exceptions;

public class UnauthorizedRequestException extends CityAppException {

    public UnauthorizedRequestException() {
        super("Unauthorized request",
                CityExcCode.UNAUTHORIZED_REQUEST);
    }
}

