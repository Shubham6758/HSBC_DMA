package com.coforge.hsbcdma.dto.DemandsDTO;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PageResponse {
    private List<?> content;
    private boolean empty;
    private boolean first;
    private boolean last;
    private int size;
    private long totalElements;
    private int totalPages;
}
