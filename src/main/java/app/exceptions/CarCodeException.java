package app.exceptions;

public class CarCodeException extends CityAppException{
    public CarCodeException(Integer carcode) {
        super("Car code " + carcode + " is already in use",
                CityExcCode.CAR_CODE_IN_USE);
    }
}
