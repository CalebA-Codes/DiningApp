package dining.repository;

import java.util.Optional;
import java.util.List;

import dining.model.Restaurant;
import org.springframework.data.repository.CrudRepository;


public interface RestaurantRepository extends CrudRepository <Restaurant,Integer> {
    List<Restaurant> findRestaurantsByZipCodeAndPeanutAllergyScoreNotNullOrderByPeanutAllergyScore(String zipcode);
    List<Restaurant> findRestaurantsByZipCodeAndDairyAllergyScoreNotNullOrderByDairyAllergyScore(String zipcode);
    List<Restaurant> findRestaurantsByZipCodeAndEggAllergyScoreNotNullOrderByEggAllergyScore(String zipcode);
    List<Restaurant> findRestaurantsByZipCode(String zipCode);
    Optional<Restaurant> findByNameAndZipCode(String name, String zipCode);
    Optional<Restaurant> findById(Long id);
}


