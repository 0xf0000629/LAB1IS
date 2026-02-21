package app.exceptions;
public class CoordinatesNotFoundException extends CityAppException {

    public CoordinatesNotFoundException(Long id) {
        super("Coordinates with id " + id + " not found",
                CityExcCode.COORDS_NOT_FOUND);
    }
}