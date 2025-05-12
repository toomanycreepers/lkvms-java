package lamart.lkvms.application.dtos;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class PaginatedResponseDto<T> {
    public List<T> results;
    public int count;
    public int limit;
    public int offset;
}
