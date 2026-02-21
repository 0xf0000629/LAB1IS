package app.exceptions;
public class CoordinatesSavingException extends CityAppException {

    public CoordinatesSavingException(Throwable cause) {
        super("Failed to save coordinates.",
                CityExcCode.COORDS_SAVING_ERROR, cause);
    }
}