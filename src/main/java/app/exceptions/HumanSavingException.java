package app.exceptions;

public class HumanSavingException extends CityAppException {

    public HumanSavingException(Throwable cause) {
        super("Failed to save human",
                CityExcCode.HUMAN_SAVING_ERROR,
                cause);
    }
}
