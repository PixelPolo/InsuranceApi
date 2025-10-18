package com.ricci.insuranceapi.insurance_api.utils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import com.ricci.insuranceapi.insurance_api.exception.ClientInvalidDataException;

@Component
public class PaginationUtils {

    public PageRequest buildPageRequest(int page, int size, String sortBy, String sortDir) {
        if (page < 0 || size <= 0) {
            throw new ClientInvalidDataException("Page must be >= 0 and size > 0");
        }
        return PageRequest.of(page, size, buildSort(sortBy, sortDir));
    }

    private Sort buildSort(String sortBy, String sortDir) {
        // If weird values like "desccc" -> fallback to "asc"
        return "desc".equalsIgnoreCase(sortDir)
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
    }

}
