package com.github.yuliyadzemidovich.parceldeliveryapp.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParcelDto {

    private Long id;

    @NotBlank
    private BigDecimal weight;
}
