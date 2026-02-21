package app.exceptions;

public class HumanDeletionException extends CityAppException {

    public HumanDeletionException(Long id, Throwable cause) {
        super("Failed to delete human with id " + id,
                CityExcCode.HUMAN_DELETION_ERROR,
                cause);
    }
}