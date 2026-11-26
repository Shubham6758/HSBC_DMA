package com.coforge.hsbcdma.master_api;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MasterValueRequest {

    private String name;

    private String parentName; // for SubLOB / SubDepartment

    private String callingCode; // CountryCode

    private String locationType; // ONSHORE/OFFSHORE

    private Integer priority;
}