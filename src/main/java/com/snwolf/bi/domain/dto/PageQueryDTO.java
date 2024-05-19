package com.snwolf.bi.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageQueryDTO {
    private Long current = 1L;
    private Long pageSize = 10L;
}
