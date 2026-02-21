package app.exceptions;

public class CitySavingException extends CityAppException {

    public CitySavingException(Throwable cause) {
        super("Failed to save city",
                CityExcCode.CITY_SAVING_ERROR,
                cause);
    }
}

