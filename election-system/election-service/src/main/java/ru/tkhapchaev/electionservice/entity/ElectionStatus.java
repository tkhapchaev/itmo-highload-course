package ru.tkhapchaev.electionservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "election_status")
@Getter
@Setter
public class ElectionStatus {

    @Id
    private Integer id;

    @Column(nullable = false)
    private String name;

    public ElectionStatusCode toCode() {
        return ElectionStatusCode.fromId(id);
    }
}
