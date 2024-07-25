package com.zufar.icedlatte.user.api;

import com.zufar.icedlatte.openapi.dto.AddressDto;
import com.zufar.icedlatte.user.converter.AddressDtoConverter;
import com.zufar.icedlatte.user.repository.AddressRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AddressProvider {

    private final AddressRepository addressRepository;
    private final AddressDtoConverter addressDtoConverter;

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public UUID addNewAddress(final AddressDto dto) {
        var address = addressDtoConverter.toEntity(dto);
        addressRepository.saveAndFlush(address);
        return address.getAddressId();
    }
}
