package app.exceptions;

public class MinioConnectionException extends CityAppException {

    public MinioConnectionException(Throwable cause) {
        super("Minio connection failed",
                CityExcCode.MINIO_CONN_FAILED,
                cause);
    }
}

