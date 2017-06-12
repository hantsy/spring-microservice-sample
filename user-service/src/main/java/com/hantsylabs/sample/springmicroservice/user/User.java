package com.hantsylabs.sample.springmicroservice.user;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)
public class User implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Id()
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "email")
    private String email;

    @Column(name = "active")
    @Builder.Default
    private boolean active = true;

    @ElementCollection
    @Builder.Default
    //@CollectionTable(name = "user_roles", joinColumns = {@JoinColumn(name = "user_id")})
    //@Column(name="role_name")
    private List<String> roles = new ArrayList<>();

    @Column(name = "created_date")
    @CreatedDate
    private LocalDateTime createdDate;

    @PrePersist()
    public void beforePersist() {
        if (this.roles.isEmpty()) {
            this.roles.add("USER");
        }
    }

}
