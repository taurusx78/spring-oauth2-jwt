package kr.hlbg.patientsmeal.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import org.hibernate.annotations.Comment;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
abstract class BaseDateEntity {

    @CreatedDate
    @Column(nullable = false, columnDefinition = "timestamp default CURRENT_TIMESTAMP") @Comment("생성일시")
    private LocalDateTime createdAt = LocalDateTime.now();

    @LastModifiedDate
    @Column(nullable = false, columnDefinition = "timestamp default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP") @Comment("수정일시")
    private LocalDateTime updatedAt  = LocalDateTime.now();
}
