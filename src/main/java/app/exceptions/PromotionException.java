package app.exceptions;

public class PromotionException extends CityAppException {

    public PromotionException(Long id, Throwable cause) {
        super("Failed to promote user with id " + id,
                CityExcCode.PROMOTION_FAILED, cause);
    }
}

