package com.example.microblog.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@EqualsAndHashCode(exclude="follow") // zapobiega powstawaniu nieskończonej "pętli"
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table

public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userId;
    @Size(min = 3, max = 32)
    private String login;
    @JsonIgnore
    @Size(min = 3, max = 60)
    @Column(name = "passhash")
    private String password;
    @Size(min = 3, max = 20)
    private String uniqueName;
    @Size(min = 3, max = 30)
    private String descriptiveName;
    private LocalDate registerDate = LocalDate.now();
    private short status;
    @ToString.Exclude
    @Lob
    @Column(columnDefinition = "MEDIUMBLOB")
    private String avatar;

    @ToString.Exclude
    @ManyToMany(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    @JoinTable(name = "follow")
    private Set<User> follow = new HashSet<>();
}
