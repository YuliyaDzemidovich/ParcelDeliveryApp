package com.github.yuliyadzemidovich.parceldeliveryapp.service.impl;

import com.github.yuliyadzemidovich.parceldeliveryapp.dto.CourierDto;
import com.github.yuliyadzemidovich.parceldeliveryapp.entity.CourierInfo;
import com.github.yuliyadzemidovich.parceldeliveryapp.repository.CourierInfoRepository;
import com.github.yuliyadzemidovich.parceldeliveryapp.service.CourierService;
import com.github.yuliyadzemidovich.parceldeliveryapp.service.DtoMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourierServiceImpl implements CourierService {

    private final CourierInfoRepository courierRepo;

    @Override
    public List<CourierDto> getAllCouriers() {
        List<CourierInfo> couriers = courierRepo.findAll();
        return couriers.stream()
                .map(DtoMapper::mapToCourierDto)
                .collect(Collectors.toList());
    }
}
