package tn.esprit.tpautoloc.web.controller;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import tn.esprit.tpautoloc.exception.ResourceNotFoundException;

/**
 * Traduit les exceptions métier en réponses JSON : ResourceNotFoundException -> 404,
 * IllegalArgumentException -> 400. Corps : statut, message, horodatage.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(ResourceNotFoundException exception) {
        return reponse(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleBadRequest(IllegalArgumentException exception) {
        return reponse(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalState(IllegalStateException exception) {
        return reponse(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    /**
     * Intercepte les erreurs Bean Validation (@Valid sur @RequestBody).
     * Retourne la liste des champs invalides avec leur message.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException exception) {
        List<String> erreurs = exception.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + " : " + fe.getDefaultMessage())
                .toList();

        Map<String, Object> corps = new LinkedHashMap<>();
        corps.put("statut", HttpStatus.BAD_REQUEST.value());
        corps.put("message", "Validation échouée");
        corps.put("erreurs", erreurs);
        corps.put("horodatage", LocalDateTime.now().toString());
        return ResponseEntity.badRequest().body(corps);
    }

    private ResponseEntity<Map<String, Object>> reponse(HttpStatus statut, String message) {
        Map<String, Object> corps = new LinkedHashMap<>();
        corps.put("statut", statut.value());
        corps.put("message", message);
        corps.put("horodatage", LocalDateTime.now().toString());
        return ResponseEntity.status(statut).body(corps);
    }
}
