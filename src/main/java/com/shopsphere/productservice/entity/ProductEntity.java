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
    @Column(name = "product_id")
    private Long productId;

    @Column(name = "category_id")
    private Long categoryId;

    @Column(name = "product_image")
    private String productImage;

    @Column(name = "product_name", unique = true, nullable = false, updatable = false)
    private String productName;

    @Column(name = "product_description")
    private String productDescription;

    @Column(name = "product_quantity")
    private Integer productQuantity;

    @Column(name = "product_price")
    private Double productPrice;

    @Column(name = "minimum_thresh_hold_count")
    private Integer minimumThreshHoldCount;

    @Column(name = "product_special_price")
    private Double productSpecialPrice;
}