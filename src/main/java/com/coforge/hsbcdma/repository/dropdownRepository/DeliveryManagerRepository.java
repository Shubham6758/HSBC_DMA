package com.coforge.hsbcdma.repository.dropdownRepository;

import com.coforge.hsbcdma.entity.dropdownEntities.DeliveryManager;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DeliveryManagerRepository extends JpaRepository<DeliveryManager,Long> {
//    import
    Optional<DeliveryManager> findByDeliveryManagerIgnoreCase(String deliveryManager);
}
