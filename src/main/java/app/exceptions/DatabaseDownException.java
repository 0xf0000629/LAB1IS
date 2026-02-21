package app.exceptions;

public class DatabaseDownException extends CityAppException {

    public DatabaseDownException() {
        super("Database is currently unavailable",
                CityExcCode.DATABASE_DOWN);
    }
}

