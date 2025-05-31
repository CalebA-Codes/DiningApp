package dining.model;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "DINING_REVIEWS")
public class DiningReview {
    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "RESTAURANT_ID")
    private Long restaurantId;

    @Column(name = "PEANUT_SCORE")
    private Float peanutScore;

    @Column(name = "EGG_SCORE")
    private Float eggScore;

    @Column(name = "DAIRY_SCORE")
    private Float dairyScore;

    @Column(name = "REVIEW")
    private String review;

    @Column(name = "REVIEW_STATUS")
    private Boolean reviewStatus;
}
