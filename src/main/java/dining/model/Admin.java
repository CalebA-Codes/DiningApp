package dining.model;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "ADMIN")
public class Admin {
    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "ACCEPT_REVIEW")
    private Boolean acceptReview;
}
