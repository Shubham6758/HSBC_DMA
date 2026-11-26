package com.coforge.hsbcdma.master_api;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MasterRequest {

    private List<MasterDataRequest> masters;
}
