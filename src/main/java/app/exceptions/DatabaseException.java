package app.exceptions;

public class DatabaseException extends CityAppException{
    public DatabaseException(Throwable cause) {
        super("Database returned an error! ",
                CityExcCode.DATABASE_ERROR, cause);
    }
}
