package dining.model;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;


@Entity
@Getter
@Setter
@Table(name = "USERS")
public class User {
    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "CITY")
    private String city;

    @Column(name = "STATE")
    private String state;

    @Column(name = "ZIPCODE")
    private String zipCode;

    @Column(name = "PEANUT_ALLERGY")
    private boolean peanutAllergy;

    @Column(name = "EGG_ALLERGY")
    private boolean eggAllergy;

    @Column(name = "DAIRY_ALLERGY")
    private boolean dairyAllergy;

}
