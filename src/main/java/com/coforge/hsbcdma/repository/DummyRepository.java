package com.coforge.hsbcdma.repository;

import com.coforge.hsbcdma.entity.DummyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

//This is dummy repository for testing purpose only. Not to be used in application.
//Created by : pratish.b
public interface DummyRepository extends JpaRepository<DummyEntity,Long> {

}
