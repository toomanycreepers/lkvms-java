package lamart.lkvms.application.dtos;

import java.util.List;

public record PagedCargoResponse(
    List<SlimCargoDto> content,
    int page,
    int size,
    long totalElements,
    int totalPages,
    boolean first,
    boolean last
) {}