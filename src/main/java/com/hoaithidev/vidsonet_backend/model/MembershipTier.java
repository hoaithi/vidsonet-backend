package com.hoaithidev.vidsonet_backend.model;
import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;
@Entity
@Table(name = "membership_tiers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MembershipTier {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    private String description;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(name = "duration_days", nullable = false)
    private Integer durationDays;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // Chủ kênh (thay vì channel_id)

    @OneToMany(mappedBy = "membershipTier", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Membership> memberships = new ArrayList<>();
}