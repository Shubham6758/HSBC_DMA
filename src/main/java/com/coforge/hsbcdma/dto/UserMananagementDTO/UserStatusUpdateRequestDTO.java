package com.coforge.hsbcdma.dto.UserMananagementDTO;

import lombok.Data;

// Created by Chetan
@Data
public class UserStatusUpdateRequestDTO {
    private boolean active; // true=activate, false=deactivate
}
