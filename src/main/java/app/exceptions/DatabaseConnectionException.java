package app.exceptions;

public class DatabaseConnectionException extends CityAppException {

    public DatabaseConnectionException(Throwable cause) {
        super("Database connection failed",
                CityExcCode.DATABASE_CONN_FAILED,
                cause);
    }
}

