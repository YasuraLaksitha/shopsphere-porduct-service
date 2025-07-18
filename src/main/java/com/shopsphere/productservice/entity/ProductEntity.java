package com.shopsphere.productservice.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@Entity
@Table(name = "tbl_product")
@EntityListeners(value = {AuditingEntityListener.class})
public class ProductEntity extends BaseEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

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
    private BigDecimal productPrice;

    @Column(name = "minimum_thresh_hold_count")
    private Integer minimumThreshHoldCount;

    @Column(name = "product_special_price")
    private BigDecimal productSpecialPrice;
}