package edu.upb.barber.repository.entity;

import java.io.Serializable;
import java.time.LocalDate;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class AuditableEntity implements Serializable {

    @CreatedDate
    protected LocalDate createdDate;

    @LastModifiedDate
    protected LocalDate modifiedDate;

    @CreatedBy
    protected String createdBy;

    @LastModifiedBy
    protected String modifiedBy;

    @Version
    protected Integer version;
//    @Column(name = "created_at", nullable = false, updatable = false)
//    private OffsetDateTime createdAt;
//
//    @Column(name = "updated_at", nullable = false)
//    private OffsetDateTime updatedAt;
//
//    @PrePersist
//    protected void onCreate() {
//        OffsetDateTime now = OffsetDateTime.now();
//        this.createdAt = now;
//        this.updatedAt = now;
//    }
//
//    @PreUpdate
//    protected void onUpdate() {
//        this.updatedAt = OffsetDateTime.now();
//    }
//
//    public OffsetDateTime getCreatedAt() {
//        return createdAt;
//    }
//
//    public OffsetDateTime getUpdatedAt() {
//        return updatedAt;
//    }
}
