package com.example.microblog.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer postId;
    @Size(min = 3, max = 1400)
    private String content;
    @Size(min = 3, max = 40)
    private String title;
    private LocalDate date = LocalDate.now();

    @ManyToOne(
            fetch = FetchType.EAGER,                // FetchType.EAGER - zachłanny
            // FetchType.LAZY - leniwy
            cascade = CascadeType.PERSIST           // CascadeType.ALL - determinuje kaskadę zapytań SQL związachy z powiązanymi rekordami
    )
    private User author;
}
