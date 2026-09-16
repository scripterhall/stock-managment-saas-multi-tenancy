package com.nourallah.saasapp.entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id" ,  updatable = false, nullable = false)
    private String id;

    @CreatedDate
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedBy
    @Column(name = "updated_at" ,  insertable = false )
    private LocalDateTime updatedAt;

    @CreatedBy
    @Column(name = "created_by" ,   updatable = false, nullable = false)
    private String createdBy;

    @LastModifiedBy
    @Column(name = "updated_by" ,  insertable = false )
    private String updatedBy;

    @Column(name = "deleted" , nullable = false)
    private Boolean deleted;

    @PrePersist // avant de persister execute la methode
    protected void onCreate(){
        if(this.deleted == null)
            this.deleted = Boolean.FALSE;
        if(this.createdBy == null) //TODO: this has to be deleted once security is implemented
            this.createdBy = "SYSTEM";
    }
}
