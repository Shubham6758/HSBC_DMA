package com.coforge.hsbcdma.entity.dropdownEntities;

import com.coforge.hsbcdma.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "delivery_manager")
public class DeliveryManager extends BaseEntity {

    @Column(name = "DELIVERY_MANAGER", nullable = false)
    private String deliveryManager;


    @Transient
    public String getName() {
        return deliveryManager;
    }

}
