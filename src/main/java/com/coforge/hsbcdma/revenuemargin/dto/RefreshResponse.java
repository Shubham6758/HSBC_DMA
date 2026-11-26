package com.coforge.hsbcdma.revenuemargin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RefreshResponse {
    private String refreshTime;
    private long durationSeconds;
    private long rowsInserted;
}
