package kr.hlbg.patientsmeal.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Entity
@Table(name = "user")
@SQLRestriction("deleted_at IS NULL")
@SQLDelete(sql = "UPDATE draft_sales SET deleted_at = NOW() WHERE id = ?")
public class User extends BaseDateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, columnDefinition = "bigint")
    private Long id;

    @Column(nullable = false, columnDefinition = "varchar(50)") @Comment("고유 식별자 (Ex. kakao_123456789)")
    private String username;

    @Column(nullable = false, columnDefinition = "varchar(50)") @Comment("닉네임")
    private String nickname;

    @Column(nullable = true, columnDefinition = "varchar(100)") @Comment("이메일")
    private String email;

    @Column(nullable = false, columnDefinition = "varchar(20)") @Comment("권한")
    private String role;

    @Column(nullable = true, columnDefinition = "timestamp") @Comment("마지막 로그인 일시")
    private LocalDateTime lastLoggedAt;

    @Column(nullable = true, columnDefinition = "varchar(256)") @Comment("Refresh Token 값")
    private String refreshToken;

    @Column(nullable = true, columnDefinition = "timestamp") @Comment("삭제일시")
    private LocalDateTime deletedAt;

    @Builder
    public User(Long id, String username, String nickname, String email, String role, LocalDateTime deletedAt) {
        this.id = id;
        this.username = username;
        this.nickname = nickname;
        this.email = email;
        this.role = role;
        this.deletedAt = deletedAt;
    }

    public void setLastLoggedAt() {
        this.lastLoggedAt = LocalDateTime.now();
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
