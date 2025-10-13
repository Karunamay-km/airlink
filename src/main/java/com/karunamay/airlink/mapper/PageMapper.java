package com.karunamay.airlink.mapper;

import com.karunamay.airlink.dto.pagination.PageResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class PageMapper {

    public <T, S> PageResponseDTO<T> toPageResponse(Page<S> page, Function<S, T> contentMapper) {
        return PageResponseDTO.<T>builder()
                .content(page.getContent()
                        .stream()
                        .map(contentMapper)
                        .collect(Collectors.toList()))
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .empty(page.isEmpty())
                .build();
    }

}
