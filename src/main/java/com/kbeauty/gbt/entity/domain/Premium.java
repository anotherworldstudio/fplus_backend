package com.kbeauty.gbt.entity.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Entity
@Table(name="PREMIUM")
@Data
@EqualsAndHashCode(callSuper = false)
public class Premium extends CommonDomain{
    @Id
    @Column(name="userid") private String userId;
    @Column(name="premiumyn") private String premiumYn;
    @Column(name="premiumstart") private String premiumStart;
    @Column(name="premiumend") private String premiumEnd;

    @Transient private String userName;
}
