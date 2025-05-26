package com.hoaithidev.vidsonet_backend.service;

import com.hoaithidev.vidsonet_backend.dto.user.UserDTO;
import com.hoaithidev.vidsonet_backend.dto.user.UpdateProfileRequest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface UserService {
    @Transactional(readOnly = true)
    UserDTO getUserById(Long id);

    @Transactional(readOnly = true)
    UserDTO getChannelByUserId(Long id);

    @Transactional
    UserDTO updateProfile(Long id, UpdateProfileRequest request);

    @Transactional(readOnly = true)
    List<UserDTO> getAllUser();

}
