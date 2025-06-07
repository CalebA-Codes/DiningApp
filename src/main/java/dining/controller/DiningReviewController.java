package dining.controller;

import dining.model.DiningReview;
import dining.model.Restaurant;
import dining.model.User;
import dining.repository.DiningReviewRepository;
import dining.repository.RestaurantRepository;
import dining.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

@RestController
@RequestMapping("api/v1/dining")
public class DiningReviewController {
    //Declare and assign the repository to the class
    private final DiningReviewRepository diningReviewRepository;
    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;
    public DiningReviewController(final DiningReviewRepository diningReviewRepository, final RestaurantRepository restaurantRepository, final UserRepository userRepository){
        this.diningReviewRepository = diningReviewRepository;
        this.restaurantRepository = restaurantRepository;
        this.userRepository = userRepository;
    }

    //Get a list of all the dining reviews in the database
    @GetMapping("/reviews")
    public Iterable<DiningReview> getAllDiningReviews(){
        return this.diningReviewRepository.findAll();
    }

    /*
     * A method meant to verify a user by checking if there are any dining reviews associated with the name
     * @param name  The name that will be used to find related dining reviews
     */
    @GetMapping("/profile/{name}")
    @ResponseStatus(HttpStatus.OK)
    public String verifyUser(@PathVariable("name") String name){

        Optional<DiningReview> optReview = this.diningReviewRepository.findByName(name);

        boolean verified = optReview.isPresent();

        if(verified){
            return "User exists!";
        }
        else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no user associated with any reviews.");
        }
    }
    /*
     * createDiningReview method that takes a diningReview object, verifies that the associated
     * username exists in the database, and saves the new diningReview object to the database.
     * Also updates the individual and overall scores for a restaurant based on the inputs for the
     * dining review.
     *
     * @param diningReview  The review to be validated and added to the database
     */
    @PostMapping("/reviews")
    @ResponseStatus(HttpStatus.CREATED)
    public String createDiningReview(@RequestBody DiningReview diningReview){
        String name = diningReview.getName();
        Long id = diningReview.getRestaurantId();
        if(name.isBlank()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot create a review without a name.");
        }
        Optional<User> optUser = this.userRepository.findByName(name);

        if(optUser.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "This user is not registered. Please register a new user account.");
        }
        Optional<Restaurant> optionalRestaurant = this.restaurantRepository.findById(id);
        if(optionalRestaurant.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no restaurant with that Id");
        }

        this.diningReviewRepository.save(diningReview);

// All reviews for the restaurant
        List<DiningReview> reviews = diningReviewRepository.findByRestaurantId(diningReview.getRestaurantId());

// Peanut Score
        Float avgPeanut = (float) reviews.stream()
                .map(DiningReview::getPeanutScore)
                .filter(Objects::nonNull)
                .mapToDouble(Float::doubleValue)
                .average()
                .orElse(0.0);


// Egg Score
        Float avgEgg = (float) reviews.stream()
                .map(DiningReview::getEggScore)
                .filter(Objects::nonNull)
                .mapToDouble(Float::doubleValue)
                .average()
                .orElse(0.0);


// Dairy Score
        Float avgDairy = (float) reviews.stream()
                .map(DiningReview::getDairyScore)
                .filter(Objects::nonNull)
                .mapToDouble(Float::doubleValue)
                .average()
                .orElse(0.0);

//Overall Score
        Float overall = (float) Stream.of(
                        avgPeanut,
                        avgEgg,
                        avgDairy
                ).filter(Objects::nonNull)
                .mapToDouble(Float::doubleValue)
                .average()
                .orElse(0.0);


        Restaurant restaurant = this.restaurantRepository.findById(diningReview.getRestaurantId()).get();
        restaurant.setPeanutAllergyScore(avgPeanut);
        restaurant.setEggAllergyScore(avgEgg);
        restaurant.setDairyAllergyScore(avgDairy);
        restaurant.setOverallScore(overall);

        this.restaurantRepository.save(restaurant);

        return "Dining review successfully saved!";
    }

    //Method to find all approved reviews associated with a restaurant
    @GetMapping("/restaurants/{id}/approved-reviews")
    public List<DiningReview> findApprovedReviews (@PathVariable("id") Long id){
        Optional<Restaurant> optRestaurant = this.restaurantRepository.findById(id);

        if(optRestaurant.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "This restaurant does not exist.");
        }
        Restaurant restaurant = optRestaurant.get();
        return this.diningReviewRepository.findByRestaurantIdAndReviewStatusTrue(restaurant.getId());
    }
}