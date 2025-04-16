CREATE TABLE categories
(
    id            BIGINT AUTO_INCREMENT NOT NULL,
    name          VARCHAR(50) NOT NULL,
    `description` VARCHAR(255) NULL,
    CONSTRAINT pk_categories PRIMARY KEY (id)
);

CREATE TABLE comment_reactions
(
    id            BIGINT AUTO_INCREMENT NOT NULL,
    user_id       BIGINT       NOT NULL,
    comment_id    BIGINT       NOT NULL,
    reaction_type VARCHAR(255) NOT NULL,
    created_at    datetime     NOT NULL,
    CONSTRAINT pk_comment_reactions PRIMARY KEY (id)
);

CREATE TABLE comments
(
    id            BIGINT AUTO_INCREMENT NOT NULL,
    content       TEXT   NOT NULL,
    created_at    datetime NULL,
    updated_at    datetime NULL,
    like_count    BIGINT NULL,
    dislike_count BIGINT NULL,
    is_pinned     BIT(1) NULL,
    is_hearted    BIT(1) NULL,
    hearted_at    datetime NULL,
    user_id       BIGINT NOT NULL,
    video_id      BIGINT NOT NULL,
    parent_id     BIGINT NULL,
    CONSTRAINT pk_comments PRIMARY KEY (id)
);

CREATE TABLE membership_tiers
(
    id            BIGINT AUTO_INCREMENT NOT NULL,
    name          VARCHAR(100) NOT NULL,
    `description` VARCHAR(255) NULL,
    price         DECIMAL      NOT NULL,
    duration_days INT          NOT NULL,
    user_id       BIGINT       NOT NULL,
    CONSTRAINT pk_membership_tiers PRIMARY KEY (id)
);

CREATE TABLE memberships
(
    id                 BIGINT AUTO_INCREMENT NOT NULL,
    start_date         date   NOT NULL,
    end_date           date   NOT NULL,
    is_active          BIT(1) NULL,
    user_id            BIGINT NOT NULL,
    membership_tier_id BIGINT NOT NULL,
    payment_id         BIGINT NULL,
    CONSTRAINT pk_memberships PRIMARY KEY (id)
);

CREATE TABLE notifications
(
    id                BIGINT AUTO_INCREMENT NOT NULL,
    notification_type VARCHAR(255) NOT NULL,
    created_at        datetime     NOT NULL,
    is_read           BIT(1)       NOT NULL,
    content           TEXT         NOT NULL,
    entity_id         BIGINT NULL,
    entity_type       VARCHAR(255) NULL,
    user_id           BIGINT       NOT NULL,
    actor_id          BIGINT       NOT NULL,
    CONSTRAINT pk_notifications PRIMARY KEY (id)
);

CREATE TABLE payments
(
    id             BIGINT AUTO_INCREMENT NOT NULL,
    payment_date   datetime     NOT NULL,
    amount         DECIMAL      NOT NULL,
    payment_method VARCHAR(255) NOT NULL,
    transaction_id VARCHAR(255) NOT NULL,
    payment_status VARCHAR(255) NOT NULL,
    CONSTRAINT pk_payments PRIMARY KEY (id)
);

CREATE TABLE playlists
(
    id            BIGINT AUTO_INCREMENT NOT NULL,
    name          VARCHAR(100) NOT NULL,
    `description` VARCHAR(255) NULL,
    created_at    datetime NULL,
    updated_at    datetime NULL,
    type          VARCHAR(255) NULL,
    user_id       BIGINT       NOT NULL,
    CONSTRAINT pk_playlists PRIMARY KEY (id)
);

CREATE TABLE post_comment_reactions
(
    id            BIGINT AUTO_INCREMENT NOT NULL,
    user_id       BIGINT       NOT NULL,
    comment_id    BIGINT       NOT NULL,
    reaction_type VARCHAR(255) NOT NULL,
    created_at    datetime NULL,
    CONSTRAINT pk_post_comment_reactions PRIMARY KEY (id)
);

CREATE TABLE post_comments
(
    id            BIGINT AUTO_INCREMENT NOT NULL,
    content       TEXT   NOT NULL,
    created_at    datetime NULL,
    updated_at    datetime NULL,
    like_count    BIGINT NULL,
    dislike_count BIGINT NULL,
    is_hearted    BIT(1) NULL,
    hearted_at    datetime NULL,
    user_id       BIGINT NOT NULL,
    post_id       BIGINT NOT NULL,
    parent_id     BIGINT NULL,
    CONSTRAINT pk_post_comments PRIMARY KEY (id)
);

CREATE TABLE post_reactions
(
    id            BIGINT AUTO_INCREMENT NOT NULL,
    user_id       BIGINT       NOT NULL,
    post_id       BIGINT       NOT NULL,
    reaction_type VARCHAR(255) NOT NULL,
    created_at    datetime NULL,
    CONSTRAINT pk_post_reactions PRIMARY KEY (id)
);

CREATE TABLE posts
(
    id            BIGINT AUTO_INCREMENT NOT NULL,
    title         VARCHAR(200) NOT NULL,
    content       TEXT         NOT NULL,
    created_at    datetime NULL,
    updated_at    datetime NULL,
    like_count    BIGINT NULL,
    dislike_count BIGINT NULL,
    user_id       BIGINT       NOT NULL,
    CONSTRAINT pk_posts PRIMARY KEY (id)
);

CREATE TABLE subscriptions
(
    id                   BIGINT AUTO_INCREMENT NOT NULL,
    subscribed_at        datetime NULL,
    notification_enabled BIT(1) NULL,
    user_id              BIGINT NOT NULL,
    channel_id           BIGINT NOT NULL,
    CONSTRAINT pk_subscriptions PRIMARY KEY (id)
);

CREATE TABLE users
(
    id                  BIGINT AUTO_INCREMENT NOT NULL,
    username            VARCHAR(50)  NOT NULL,
    email               VARCHAR(100) NOT NULL,
    password            VARCHAR(255) NOT NULL,
    profile_picture     VARCHAR(255) NULL,
    created_at          datetime NULL,
    `role`              VARCHAR(255) NOT NULL,
    channel_name        VARCHAR(100) NULL,
    channel_description TEXT NULL,
    channel_picture     VARCHAR(255) NULL,
    banner_image        VARCHAR(255) NULL,
    channel_created_at  datetime NULL,
    CONSTRAINT pk_users PRIMARY KEY (id)
);

CREATE TABLE video_categories
(
    id          BIGINT AUTO_INCREMENT NOT NULL,
    video_id    BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    added_at    datetime NULL,
    CONSTRAINT pk_video_categories PRIMARY KEY (id)
);

CREATE TABLE video_playlists
(
    id             BIGINT AUTO_INCREMENT NOT NULL,
    video_id       BIGINT NOT NULL,
    playlist_id    BIGINT NOT NULL,
    added_at       datetime NULL,
    position       INT NULL,
    watch_position INT NULL,
    last_watched   datetime NULL,
    is_completed   BIT(1) NULL,
    percentage DOUBLE NULL,
    CONSTRAINT pk_video_playlists PRIMARY KEY (id)
);

CREATE TABLE video_progress
(
    id           BIGINT AUTO_INCREMENT NOT NULL,
    user_id      BIGINT   NOT NULL,
    video_id     BIGINT   NOT NULL,
    watch_time   INT      NOT NULL,
    duration     INT      NOT NULL,
    percentage DOUBLE NOT NULL,
    last_watched datetime NOT NULL,
    is_completed BIT(1)   NOT NULL,
    CONSTRAINT pk_video_progress PRIMARY KEY (id)
);

CREATE TABLE video_reactions
(
    id            BIGINT AUTO_INCREMENT NOT NULL,
    user_id       BIGINT       NOT NULL,
    video_id      BIGINT       NOT NULL,
    reaction_type VARCHAR(255) NOT NULL,
    created_at    datetime     NOT NULL,
    CONSTRAINT pk_video_reactions PRIMARY KEY (id)
);

CREATE TABLE videos
(
    id            BIGINT AUTO_INCREMENT NOT NULL,
    title         VARCHAR(200) NOT NULL,
    `description` TEXT NULL,
    video_url     VARCHAR(255) NOT NULL,
    thumbnail_url VARCHAR(255) NULL,
    duration      INT          NOT NULL,
    view_count    BIGINT NULL,
    like_count    BIGINT NULL,
    dislike_count BIGINT NULL,
    is_premium    BIT(1) NULL,
    published_at  datetime NULL,
    user_id       BIGINT       NOT NULL,
    CONSTRAINT pk_videos PRIMARY KEY (id)
);

ALTER TABLE subscriptions
    ADD CONSTRAINT uc_39b1e297be3505d115f2ffcd1 UNIQUE (user_id, channel_id);

ALTER TABLE video_reactions
    ADD CONSTRAINT uc_47c5cd217277c687441a874ce UNIQUE (user_id, video_id);

ALTER TABLE video_progress
    ADD CONSTRAINT uc_822b34f75b583d3712f1051f5 UNIQUE (user_id, video_id);

ALTER TABLE comment_reactions
    ADD CONSTRAINT uc_9a8ba08bd55edde0434791367 UNIQUE (user_id, comment_id);

ALTER TABLE categories
    ADD CONSTRAINT uc_categories_name UNIQUE (name);

ALTER TABLE memberships
    ADD CONSTRAINT uc_memberships_payment UNIQUE (payment_id);

ALTER TABLE payments
    ADD CONSTRAINT uc_payments_transaction UNIQUE (transaction_id);

ALTER TABLE users
    ADD CONSTRAINT uc_users_channel_name UNIQUE (channel_name);

ALTER TABLE users
    ADD CONSTRAINT uc_users_email UNIQUE (email);

ALTER TABLE users
    ADD CONSTRAINT uc_users_username UNIQUE (username);

ALTER TABLE comments
    ADD CONSTRAINT FK_COMMENTS_ON_PARENT FOREIGN KEY (parent_id) REFERENCES comments (id);

ALTER TABLE comments
    ADD CONSTRAINT FK_COMMENTS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE comments
    ADD CONSTRAINT FK_COMMENTS_ON_VIDEO FOREIGN KEY (video_id) REFERENCES videos (id);

ALTER TABLE comment_reactions
    ADD CONSTRAINT FK_COMMENT_REACTIONS_ON_COMMENT FOREIGN KEY (comment_id) REFERENCES comments (id);

ALTER TABLE comment_reactions
    ADD CONSTRAINT FK_COMMENT_REACTIONS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE memberships
    ADD CONSTRAINT FK_MEMBERSHIPS_ON_MEMBERSHIP_TIER FOREIGN KEY (membership_tier_id) REFERENCES membership_tiers (id);

ALTER TABLE memberships
    ADD CONSTRAINT FK_MEMBERSHIPS_ON_PAYMENT FOREIGN KEY (payment_id) REFERENCES payments (id);

ALTER TABLE memberships
    ADD CONSTRAINT FK_MEMBERSHIPS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE membership_tiers
    ADD CONSTRAINT FK_MEMBERSHIP_TIERS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE notifications
    ADD CONSTRAINT FK_NOTIFICATIONS_ON_ACTOR FOREIGN KEY (actor_id) REFERENCES users (id);

ALTER TABLE notifications
    ADD CONSTRAINT FK_NOTIFICATIONS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE playlists
    ADD CONSTRAINT FK_PLAYLISTS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE posts
    ADD CONSTRAINT FK_POSTS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE post_comments
    ADD CONSTRAINT FK_POST_COMMENTS_ON_PARENT FOREIGN KEY (parent_id) REFERENCES post_comments (id);

ALTER TABLE post_comments
    ADD CONSTRAINT FK_POST_COMMENTS_ON_POST FOREIGN KEY (post_id) REFERENCES posts (id);

ALTER TABLE post_comments
    ADD CONSTRAINT FK_POST_COMMENTS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE post_comment_reactions
    ADD CONSTRAINT FK_POST_COMMENT_REACTIONS_ON_COMMENT FOREIGN KEY (comment_id) REFERENCES post_comments (id);

ALTER TABLE post_comment_reactions
    ADD CONSTRAINT FK_POST_COMMENT_REACTIONS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE post_reactions
    ADD CONSTRAINT FK_POST_REACTIONS_ON_POST FOREIGN KEY (post_id) REFERENCES posts (id);

ALTER TABLE post_reactions
    ADD CONSTRAINT FK_POST_REACTIONS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE subscriptions
    ADD CONSTRAINT FK_SUBSCRIPTIONS_ON_CHANNEL FOREIGN KEY (channel_id) REFERENCES users (id);

ALTER TABLE subscriptions
    ADD CONSTRAINT FK_SUBSCRIPTIONS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE videos
    ADD CONSTRAINT FK_VIDEOS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE video_categories
    ADD CONSTRAINT FK_VIDEO_CATEGORIES_ON_CATEGORY FOREIGN KEY (category_id) REFERENCES categories (id);

ALTER TABLE video_categories
    ADD CONSTRAINT FK_VIDEO_CATEGORIES_ON_VIDEO FOREIGN KEY (video_id) REFERENCES videos (id);

ALTER TABLE video_playlists
    ADD CONSTRAINT FK_VIDEO_PLAYLISTS_ON_PLAYLIST FOREIGN KEY (playlist_id) REFERENCES playlists (id);

ALTER TABLE video_playlists
    ADD CONSTRAINT FK_VIDEO_PLAYLISTS_ON_VIDEO FOREIGN KEY (video_id) REFERENCES videos (id);

ALTER TABLE video_progress
    ADD CONSTRAINT FK_VIDEO_PROGRESS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE video_progress
    ADD CONSTRAINT FK_VIDEO_PROGRESS_ON_VIDEO FOREIGN KEY (video_id) REFERENCES videos (id);

ALTER TABLE video_reactions
    ADD CONSTRAINT FK_VIDEO_REACTIONS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE video_reactions
    ADD CONSTRAINT FK_VIDEO_REACTIONS_ON_VIDEO FOREIGN KEY (video_id) REFERENCES videos (id);