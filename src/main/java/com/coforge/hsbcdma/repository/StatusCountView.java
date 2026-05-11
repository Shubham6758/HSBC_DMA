package com.coforge.hsbcdma.repository;

import com.coforge.hsbcdma.enums.Status;

public interface StatusCountView {

        Status getStatus();
        Integer getCount();
}