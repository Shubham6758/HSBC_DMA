package com.coforge.hsbcdma.dto.UserFilterPreferenceDTO;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class UserFilterPreferencesRequest {

    private Map<String, List<String>> preferences;
}