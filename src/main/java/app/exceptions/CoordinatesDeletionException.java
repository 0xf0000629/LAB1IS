package app.exceptions;
public class CoordinatesDeletionException extends CityAppException {

    public CoordinatesDeletionException(Long id) {
        super("Failed to delete coordinates with id " + id + " ",
                CityExcCode.COORDS_DELETION_ERROR);
    }
}