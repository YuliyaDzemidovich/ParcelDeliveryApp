package com.github.yuliyadzemidovich.parceldeliveryapp.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryDto {

    private Long id;

    @NotBlank
    private String pickupLatitude;

    @NotBlank
    private String pickupLongitude;

    private String pickupTime;
}
