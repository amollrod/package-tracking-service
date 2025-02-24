package com.tfg.packagetracking.application.utils;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.function.Function;

/**
 * Utility class for controller-related functions.
 */
public class ControllerUtils {

    private ControllerUtils() { }

    /**
     * Creates a Pageable object from the page and size parameters.
     *
     * @param page Page number
     * @param size Page size
     * @return Pageable object
     */
    public static Pageable createPageable(int page, int size) {
        return PageRequest.of(page, size);
    }

    /**
     * Creates a paged response from a Page of domain objects.
     * Adds the X-Total-Pages and X-Total-Elements headers to the response.
     *
     * @param page Page of domain objects
     * @param converter Function to convert domain objects to response DTOs
     * @param <D> Domain object type
     * @param <R> Response object type
     * @return ResponseEntity with pagination headers
     */
    public static <D, R> ResponseEntity<Page<R>> createPagedResponse(Page<D> page, Function<D, R> converter) {
        Page<R> convertedPage = page.map(converter);
        return ResponseEntity.ok()
                .header("X-Total-Pages", String.valueOf(convertedPage.getTotalPages()))
                .header("X-Total-Elements", String.valueOf(convertedPage.getTotalElements()))
                .body(convertedPage);
    }
}
