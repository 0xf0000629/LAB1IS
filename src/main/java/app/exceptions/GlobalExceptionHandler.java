package app.exceptions;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CityAppException.class)
    public String handleCityAppException(CityAppException ex, Model model, HttpServletResponse response) {
        // Set HTTP status dynamically
        System.out.println("ERROR: " + ex.getErrorCode());
        response.setStatus(mapStatus(ex.getErrorCode()).value());
        model.addAttribute("errorCode", ex.getErrorCode().name());
        model.addAttribute("errorMessage", ex.getMessage());
        return "oops";
    }

    private HttpStatus mapStatus(CityExcCode code) {
        return switch (code) {
            case CITY_NOT_FOUND, HUMAN_NOT_FOUND, COORDS_NOT_FOUND -> HttpStatus.NOT_FOUND;
            case UNAUTHORIZED_REQUEST, NOT_A_USER, LOGIN_FAILED -> HttpStatus.UNAUTHORIZED;
            case DATABASE_DOWN, MINIO_DOWN -> HttpStatus.SERVICE_UNAVAILABLE;
            default -> HttpStatus.BAD_REQUEST;
        };
    }
}
