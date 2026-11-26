package com.coforge.hsbcdma.dto.UserFilterPreferenceDTO;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class UserFilterPreferencesResponse {

    private Map<String, List<String>> preferences;
}