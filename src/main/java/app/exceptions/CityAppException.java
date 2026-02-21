package app.exceptions;

public abstract class CityAppException extends RuntimeException{
    private final CityExcCode code;
    protected CityAppException(String message, CityExcCode code) {
        super(message);
        this.code = code;
    }
    protected CityAppException(String message, CityExcCode code, Throwable causedBy) {
        super(message, causedBy);
        this.code = code;
    }
    public CityExcCode getErrorCode() {
        return code;
    }
}
