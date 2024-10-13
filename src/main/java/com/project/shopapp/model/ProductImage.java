package com.project.shopapp.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "product_images")
@Builder
public class ProductImage {

    public static final int MAXIMUM_IMAGE_PER_PRODUCT = 5;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "image_url", length = 300)
    private String image_url;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
}
