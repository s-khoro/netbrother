package me.khoro.main.model.entity;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Table(name = "domains")
public class Domain {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "domain", nullable = false)
    private String domain;

    @Column(name = "reg_date")
    private Date regDate;
    @Column(name = "upd_date")
    private Date updDate;
    @Column(name = "exp_date")
    private Date expDate;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Domain domain = (Domain) o;

        return Objects.equals(id, domain.id);
    }

    @Override
    public int hashCode() {
        return 460566625;
    }
}
