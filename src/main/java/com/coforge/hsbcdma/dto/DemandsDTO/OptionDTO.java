package com.coforge.hsbcdma.dto.DemandsDTO;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class OptionDTO {
    private Long id;      // Use Long to normalize across tables
    private String name;  // Display label
}
