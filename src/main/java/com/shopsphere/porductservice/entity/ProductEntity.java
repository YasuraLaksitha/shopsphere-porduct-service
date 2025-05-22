package com.shopsphere.porductservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tbl_product")
public class ProductEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    private Long categoryId;

    private String image;

    @Column(unique = true, nullable = false, updatable = false)
    private String productName;

    private String productDescription;

    private Integer productQuantity;

    private Double productPrice;

    private int minimumThreadHoldCount;

    private double productSpecialPrice;
}
