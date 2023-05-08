package com.github.yuliyadzemidovich.parceldeliveryapp.dto;

import com.github.yuliyadzemidovich.parceldeliveryapp.entity.OrderStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {

    private Long id;

    @NotBlank
    @Size(max = 255)
    private String receiverAddress;

    @NotBlank
    @Size(max = 255)
    private String receiverName;

    @NotBlank
    @Size(max = 20)
    private String receiverPhone;

    @NotNull
    private Long senderId;

    private OrderStatus status;

    private ParcelDto parcelDto;

    private DeliveryDto deliveryDto;

}
