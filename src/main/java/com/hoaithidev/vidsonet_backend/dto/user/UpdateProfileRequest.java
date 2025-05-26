package com.hoaithidev.vidsonet_backend.dto.user;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileRequest {
    private String username;
    private String email;

    @JsonIgnore
    private MultipartFile profilePicture;
    private String channelName;
    private String channelDescription;

    @JsonIgnore
    private MultipartFile channelPicture;

    @JsonIgnore
    private MultipartFile bannerImage;
}