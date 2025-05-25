package com.shopsphere.productservice.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@Entity
@Table(name = "tbl_product")
@EntityListeners(value = {AuditingEntityListener.class})
public class ProductEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    private Long categoryId;

    private String productImage;

    @Column(unique = true, nullable = false, updatable = false)
    private String productName;

    private String productDescription;

    private Integer productQuantity;

    private Double productPrice;

    private Integer minimumThreshHoldCount;

    private Double productSpecialPrice;
}
