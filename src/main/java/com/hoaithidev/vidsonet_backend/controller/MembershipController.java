package com.hoaithidev.vidsonet_backend.controller;

import com.hoaithidev.vidsonet_backend.dto.membership.MembershipDTO;
import com.hoaithidev.vidsonet_backend.dto.membership.MembershipTierCreateRequest;
import com.hoaithidev.vidsonet_backend.dto.membership.MembershipTierDTO;
import com.hoaithidev.vidsonet_backend.dto.membership.MembershipTierUpdateRequest;
import com.hoaithidev.vidsonet_backend.payload.response.ApiResponse;
import com.hoaithidev.vidsonet_backend.service.MembershipService;
import com.hoaithidev.vidsonet_backend.util.CurrentUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/memberships")
@RequiredArgsConstructor
public class MembershipController {

    private final MembershipService membershipService;

    @PostMapping("/tiers")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<MembershipTierDTO>> createMembershipTier(
            @Valid @RequestBody MembershipTierCreateRequest request,
            @CurrentUser Long userId) {

        MembershipTierDTO tier = membershipService.createMembershipTier(userId, request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<MembershipTierDTO>builder()
                        .message("Membership tier created successfully")
                        .data(tier)
                        .build());
    }

    @PutMapping("/tiers/{tierId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<MembershipTierDTO>> updateMembershipTier(
            @PathVariable Long tierId,
            @Valid @RequestBody MembershipTierUpdateRequest request,
            @CurrentUser Long userId) {

        MembershipTierDTO tier = membershipService.updateMembershipTier(tierId, userId, request);

        return ResponseEntity.ok(ApiResponse.<MembershipTierDTO>builder()
                .message("Membership tier updated successfully")
                .data(tier)
                .build());
    }

    @DeleteMapping("/tiers/{tierId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<Void>> deleteMembershipTier(
            @PathVariable Long tierId,
            @CurrentUser Long userId) {

        membershipService.deleteMembershipTier(tierId, userId);

        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .message("Membership tier deleted successfully")
                .build());
    }

    @GetMapping("/tiers/channel/{channelId}")
    public ResponseEntity<ApiResponse<List<MembershipTierDTO>>> getChannelMembershipTiers(
            @PathVariable Long channelId) {

        List<MembershipTierDTO> tiers = membershipService.getChannelMembershipTiers(channelId);

        return ResponseEntity.ok(ApiResponse.<List<MembershipTierDTO>>builder()
                .message("Channel membership tiers retrieved successfully")
                .data(tiers)
                .build());
    }

    @GetMapping("/tiers/{tierId}")
    public ResponseEntity<ApiResponse<MembershipTierDTO>> getMembershipTierById(
            @PathVariable Long tierId) {

        MembershipTierDTO tier = membershipService.getMembershipTierById(tierId);

        return ResponseEntity.ok(ApiResponse.<MembershipTierDTO>builder()
                .message("Membership tier retrieved successfully")
                .data(tier)
                .build());
    }

    @GetMapping("/user")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<List<MembershipDTO>>> getUserMemberships(
            @CurrentUser Long userId) {

        List<MembershipDTO> memberships = membershipService.getUserMemberships(userId);

        return ResponseEntity.ok(ApiResponse.<List<MembershipDTO>>builder()
                .message("User memberships retrieved successfully")
                .data(memberships)
                .build());
    }

    @GetMapping("/channel/{channelId}")
    @PreAuthorize("@userSecurity.isCurrentUser(#channelId)")
    public ResponseEntity<ApiResponse<List<MembershipDTO>>> getChannelMembers(
            @PathVariable Long channelId) {

        List<MembershipDTO> members = membershipService.getChannelMembers(channelId);

        return ResponseEntity.ok(ApiResponse.<List<MembershipDTO>>builder()
                .message("Channel members retrieved successfully")
                .data(members)
                .build());
    }

    @GetMapping("/check")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<Boolean>> checkMembership(
            @RequestParam Long channelId,
            @CurrentUser Long userId) {

        boolean hasMembership = membershipService.hasActiveChannelMembership(userId, channelId);

        return ResponseEntity.ok(ApiResponse.<Boolean>builder()
                .message("Membership status checked")
                .data(hasMembership)
                .build());
    }
}