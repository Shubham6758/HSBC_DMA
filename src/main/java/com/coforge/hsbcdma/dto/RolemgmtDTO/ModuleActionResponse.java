package com.coforge.hsbcdma.dto.RolemgmtDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModuleActionResponse {
//    String childmodule;
//    String module;

    String module;
    List<Child> childmodules;


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Child {
        private String childmodule;
        private boolean action;
    }
}
