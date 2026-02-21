package app.exceptions;

public class MinioDownException extends CityAppException {

    public MinioDownException() {
        super("Minio service is currently unavailable",
                CityExcCode.MINIO_DOWN);
    }
}
