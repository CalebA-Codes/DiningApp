package dining.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Getter
@Setter
@ToString(exclude = {
        "peanutAllergyScore",
        "eggAllergyScore",
        "dairyAllergyScore",
        "overallScore"
})
@Table(name = "RESTAURANTS")
public class Restaurant {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "LOCATION")
    private String location;

    @Column(name = "ZIPCODE")
    private String zipCode;


    @Column(name = "PEANUT_ALLERGY_SCORE")
    private Float peanutAllergyScore;


    @Column(name = "EGG_ALLERGY_SCORE")
    private Float eggAllergyScore;


    @Column(name = "DAIRY_ALLERGY_SCORE")
    private Float dairyAllergyScore;


    @Column(name = "OVERALL_SCORE")
    private Float overallScore;

    // Formatted getters for JSON output

    @JsonProperty("peanutAllergyScore")
    public String getPeanutAllergyScoreFormatted() {
        return format(peanutAllergyScore);
    }

    @JsonProperty("eggAllergyScore")
    public String getEggAllergyScoreFormatted() {
        return format(eggAllergyScore);
    }

    @JsonProperty("dairyAllergyScore")
    public String getDairyAllergyScoreFormatted() {
        return format(dairyAllergyScore);
    }

    @JsonProperty("overallScore")
    public String getOverallScoreFormatted() {
        return format(overallScore);
    }

    // Format pattern
    private String format(Float score) {
        return score == null ? null : String.format("%.2f", score);
    }
}