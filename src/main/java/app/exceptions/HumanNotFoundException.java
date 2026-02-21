package app.exceptions;

public class HumanNotFoundException extends CityAppException {

    public HumanNotFoundException(Long id) {
        super("Human with id " + id + " not found",
                CityExcCode.HUMAN_NOT_FOUND);
    }
}

