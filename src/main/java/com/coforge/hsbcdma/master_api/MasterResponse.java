package com.coforge.hsbcdma.master_api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MasterResponse {

    private int successCount;

    private int duplicateCount;

    private List<String> messages;
}