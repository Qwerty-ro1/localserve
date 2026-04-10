package com.localserve.localserve.service.impl;

import com.localserve.localserve.dto.UserAddressRequest;
import com.localserve.localserve.dto.UserAddressResponse;
import com.localserve.localserve.entity.User;
import com.localserve.localserve.entity.UserAddress;
import com.localserve.localserve.exception.BadRequestException;
import com.localserve.localserve.repository.BookingRepository;
import com.localserve.localserve.repository.UserAddressRepository;
import com.localserve.localserve.repository.UserRepository;
import com.localserve.localserve.service.UserAddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserAddressServiceImpl implements UserAddressService {

    private final UserAddressRepository userAddressRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;


    @Override
    public List<UserAddressResponse> getAddresses(String email) {
        User user = getUser(email);
        return userAddressRepository.findByUserId(user.getId())
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserAddressResponse addAddress(String email, UserAddressRequest request) {
        User user = getUser(email);
        boolean isFirst = userAddressRepository.countByUserId(user.getId()) == 0;

        if (request.isDefault() || isFirst) {
            unsetAllDefaults(user.getId());
        }

        UserAddress address = UserAddress.builder()
                .user(user)
                .label(request.getLabel())
                .addressLine(request.getAddressLine())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .isDefault(request.isDefault() || isFirst)
                .build();

        return toResponse(userAddressRepository.save(address));
    }

    @Override
    @Transactional
    public UserAddressResponse updateAddress(String email, Long addressId, UserAddressRequest request) {
        User user = getUser(email);
        UserAddress address = getAddressOwned(user, addressId);

        if (request.isDefault()) {
            unsetAllDefaults(user.getId());
        }

        address.setLabel(request.getLabel());
        address.setAddressLine(request.getAddressLine());
        address.setLatitude(request.getLatitude());
        address.setLongitude(request.getLongitude());
        address.setDefault(request.isDefault());

        return toResponse(userAddressRepository.save(address));
    }

    @Transactional
    public void deleteAddress(String email, Long addressId) {
        User user = getUser(email);
        UserAddress address = getAddressOwned(user, addressId);

        List<UserAddress> allAddresses = userAddressRepository.findByUserId(user.getId());

        // block delete if it's the only address
        if (allAddresses.size() == 1) {
            throw new BadRequestException("Cannot delete your only saved address");
        }

        // nullify any booking references first
        bookingRepository.nullifyServiceAddress(addressId);

        // if deleting default, promote next available address
        if (address.isDefault()) {
            allAddresses.stream()
                    .filter(a -> !a.getId().equals(addressId))
                    .findFirst()
                    .ifPresent(next -> {
                        next.setDefault(true);
                        userAddressRepository.save(next);
                    });
        }


        userAddressRepository.delete(address);
    }

    @Override
    @Transactional
    public UserAddressResponse setDefault(String email, Long addressId) {
        User user = getUser(email);
        unsetAllDefaults(user.getId());
        UserAddress address = getAddressOwned(user, addressId);
        address.setDefault(true);
        return toResponse(userAddressRepository.save(address));
    }


    private void unsetAllDefaults(Long userId) {
        List<UserAddress> defaults = userAddressRepository.findByUserId(userId);
        defaults.forEach(a -> a.setDefault(false));
        userAddressRepository.saveAll(defaults);
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private UserAddress getAddressOwned(User user, Long addressId) {
        UserAddress address = userAddressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Address not found"));
        if (!address.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }
        return address;
    }

    private UserAddressResponse toResponse(UserAddress a) {
        return UserAddressResponse.builder()
                .id(a.getId())
                .label(a.getLabel())
                .addressLine(a.getAddressLine())
                .latitude(a.getLatitude())
                .longitude(a.getLongitude())
                .isDefault(a.isDefault())
                .build();
    }
}