package app.exceptions;

public class CityDeletionException extends CityAppException {

    public CityDeletionException(Long id, Throwable cause) {
        super("Failed to delete city with id " + id,
                CityExcCode.CITY_DELETION_ERROR,
                cause);
    }
}
