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
@Table(name = "DEMAND_TIMELINE")
public class DemandTimeLine extends BaseEntity{

        @Column(name = "DEMAND_TIMELINE", nullable = true)
        private String demandTimeLine;

        @Transient
        public String getName() {
                return demandTimeLine;
        }
}
