package app.exceptions;
public class CityNotFoundException extends CityAppException {

    public CityNotFoundException(Long id) {
        super("City with id " + id + " not found",
                CityExcCode.CITY_NOT_FOUND);
    }
}
