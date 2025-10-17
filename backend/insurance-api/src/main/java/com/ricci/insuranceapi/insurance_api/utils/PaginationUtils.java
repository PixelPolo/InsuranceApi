package com.ricci.insuranceapi.insurance_api.utils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component
public class PaginationUtils {

    public PageRequest buildPageRequest(int page, int size, String sortBy, String sortDir) {
        return PageRequest.of(page, size, buildSort(sortBy, sortDir));
    }

    private Sort buildSort(String sortBy, String sortDir) {
        // If weird values like "desccc" -> fallback to "asc"
        return "desc".equalsIgnoreCase(sortDir)
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
    }

}
