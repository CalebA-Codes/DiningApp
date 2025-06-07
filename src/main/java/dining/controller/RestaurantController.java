package dining.controller;

import dining.model.Restaurant;
import dining.repository.*;

import java.util.Collections;
import java.util.Optional;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


@RestController
@RequestMapping("api/v1/dining")
public class RestaurantController {
    //Declare and assign the repositories to the class
    private final RestaurantRepository restaurantRepository;

    public RestaurantController(final RestaurantRepository restaurantRepository){
        this.restaurantRepository = restaurantRepository;
    }

    //Create a new Restaurant
    @PostMapping("/restaurants")
    @ResponseStatus(HttpStatus.CREATED)
    public String createNewRestaurant (@RequestBody Restaurant restaurant){
        String name = restaurant.getName();
        String zipCode = restaurant.getZipCode();
        Optional<Restaurant> optRestaurant = this.restaurantRepository.findByNameAndZipCode(name, zipCode);

        if(optRestaurant.isEmpty()){


            this.restaurantRepository.save(restaurant);
            return "Restaurant successfully saved!";
        }
        else {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Restaurants already exists in the database");
        }


    }

    //Get all restaurants
    @GetMapping("/restaurants")
    public Iterable<Restaurant> getAllRestaurants(){
        return this.restaurantRepository.findAll();
    }

    //Get a restaurants details based of its unique id
    @GetMapping("/restaurants/{id}")
    @ResponseStatus(HttpStatus.OK)
    public String restaurantDetails(@PathVariable("id") Long id){
        Optional<Restaurant> optRestaurant = this.restaurantRepository.findById(id);

        if(optRestaurant.isPresent()){
            Restaurant restaurant = optRestaurant.get();
            return restaurant.toString();
        }

        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No Restaurant associated with that Id.");
    }

    //Get a restaurant based on zipcode and at least 1 valid allergy review
    @GetMapping("/restaurants/search")
    public Iterable<Restaurant> searchRestaurants(@RequestParam String zipcode, @RequestParam String allergy) {
        List<Restaurant> validRestaurants = this.restaurantRepository.findRestaurantsByZipCode(zipcode);
        if(validRestaurants.isEmpty()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No restaurants associated with that zipcode.");
        }

        Iterable<Restaurant> restaurants = Collections.EMPTY_LIST;
        if (allergy.equalsIgnoreCase("peanut")) {
            restaurants = restaurantRepository.findRestaurantsByZipCodeAndPeanutAllergyScoreNotNullOrderByPeanutAllergyScore(zipcode);
        } else if (allergy.equalsIgnoreCase("dairy")) {
            restaurants = restaurantRepository.findRestaurantsByZipCodeAndDairyAllergyScoreNotNullOrderByDairyAllergyScore(zipcode);
        } else if (allergy.equalsIgnoreCase("egg")) {
            restaurants = restaurantRepository.findRestaurantsByZipCodeAndEggAllergyScoreNotNullOrderByEggAllergyScore(zipcode);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Allergy must be one of peanut, egg or dairy.");
        }

        return restaurants;
    }
    //Delete a restaurant
    @DeleteMapping("/restaurants/{id}")
    public Restaurant deleteRestaurant(@PathVariable("id") Long id){
        Optional<Restaurant> optRestaurant = this.restaurantRepository.findById(id);

        if(optRestaurant.isPresent()){
            Restaurant restaurantToDelete = optRestaurant.get();
            this.restaurantRepository.delete(restaurantToDelete);
            return restaurantToDelete;
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Restaurant not found!");
    }

}