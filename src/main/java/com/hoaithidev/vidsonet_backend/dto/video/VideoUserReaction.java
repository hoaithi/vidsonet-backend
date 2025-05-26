package com.hoaithidev.vidsonet_backend.dto.video;

import com.hoaithidev.vidsonet_backend.enums.ReactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoUserReaction {
    private boolean hasReacted;
    private ReactionType reactionType;
    LocalDateTime createdAt;
}
