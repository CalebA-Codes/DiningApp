package dining.controller;

import dining.model.Admin;
import dining.model.DiningReview;
import dining.model.Restaurant;
import dining.model.User;
import dining.repository.*;


import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


@RestController
@RequestMapping("api/v1/dining")
public class RestaurantController {
    //Declare and assign the repositories to the class
    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;
    private final DiningReviewRepository diningReviewRepository;
    private final AdminRepository adminRepository;

    public RestaurantController(final RestaurantRepository restaurantRepository, final UserRepository userRepository, final DiningReviewRepository diningReviewRepository, final AdminRepository adminRepository){
        this.restaurantRepository = restaurantRepository;
        this.userRepository = userRepository;
        this.diningReviewRepository = diningReviewRepository;
        this.adminRepository = adminRepository;
    }

    //Get a list of all users in the database
    @GetMapping("/users")
    public Iterable<User> getAllUsers(){
        return this.userRepository.findAll();
    }

    //Create a new user
    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    public String createNewUser(@RequestBody User user)
    {
    if(user.getName() == null || user.getName().isBlank()){
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing value in the field:Username!");
    }
    Optional<User> optUser = this.userRepository.findByName(user.getName());
    if(!optUser.isPresent()){
        this.userRepository.save(user);
        return "User successfully created!";
    }
    else {
        throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists, please choose a different one.");
    }
    }

    //Find a user based on their name and then update their profile
    @PutMapping("/users/{name}")
    public String displayUser(@PathVariable("name") String name, @RequestBody User user){
        Optional<User> optUser = this.userRepository.findByName(name);
        if(optUser.isPresent()){
            User newUser = optUser.get();
            newUser.setCity(user.getCity());
            newUser.setState(user.getState());
            newUser.setZipCode(user.getZipCode());
            newUser.setPeanutAllergy(user.isPeanutAllergy());
            newUser.setEggAllergy(user.isEggAllergy());
            newUser.setDairyAllergy(user.isDairyAllergy());
            this.userRepository.save(newUser);
            return "User successfully updated";
        }
        else{
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found! Please try another username.");
        }
    }

    //Find a user based on their name and display their profile
    @GetMapping("/users/{name}")
    @ResponseStatus(HttpStatus.OK)
    public User getUserProfile(@PathVariable("name") String name){
        Optional<User> optUser = this.userRepository.findByName(name);
        if(optUser.isPresent()){
            return optUser.get();
        }
        else{
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User profile not found! Try a different username");
        }
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

    //Delete a user
    @DeleteMapping("/users/{name}")
    public User deleteUser(@PathVariable("name") String name){
        Optional<User> optUser = this.userRepository.findByName(name);

        if(optUser.isPresent()){
            User userToDelete = optUser.get();
            this.userRepository.delete(userToDelete);
            return userToDelete;
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found!");
    }

    //Create an Admin
    @PostMapping("/admins")
    @ResponseStatus(HttpStatus.CREATED)
    public String createAdmin(@RequestBody Admin admin){
    this.adminRepository.save(admin);
    return "Your Admin identification number is: " + admin.getId() +". Admin successfully saved!";
    }

    //Get a list of all Admins in the database
    @GetMapping("/admins")
    public Iterable<Admin> getAllAdmins(){
        return this.adminRepository.findAll();
    }

    //Method to verify admin existence
    public Boolean verifyAdminExists(Long id) {
        Optional<Admin> optAdmin = this.adminRepository.findById(id);

        return (optAdmin.isPresent());

    }

    //Delete an admin
    @DeleteMapping("/admins/{id}")
    public Admin deleteAdmin(@PathVariable("id") Long id){
        Optional<Admin> optAdmin = this.adminRepository.findById(id);

        if(optAdmin.isPresent()){
            Admin adminToDelete = optAdmin.get();
            this.adminRepository.delete(adminToDelete);
            return adminToDelete;
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Admin not found!");
    }


    //Get a list of all the dining reviews in the database
    @GetMapping("/reviews")
    public Iterable<DiningReview> getAllDiningReviews(){
        return this.diningReviewRepository.findAll();
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


    /*
     * Method to simulate simple Admin function. Will verify an admin based on the id provided and allow
     * them to view pending reviews.
     *
     * @param id    The id associated with a valid admin
     */
    @GetMapping("/admins/{id}/pending-reviews")
    @ResponseStatus(HttpStatus.OK)
    public List<DiningReview> getPendingReviews(@PathVariable("id") Long id){
        boolean verified = verifyAdminExists(id);

        if(!verified){
           throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No admins recognized by that Id.");
        }
        return this.diningReviewRepository.findByReviewStatusNull();
    }

    /*
     * Method that verifies an admins status and allows them to update the reviewStatus of a dining review
     *
     * @param adminId   The id associated with a valid admin
     * @param reviewId  The id associated with a specific review
     */
    @PutMapping("/admins/{adminId}/reviews/{reviewId}")
    public String updateReviewStatus(@PathVariable("adminId")Long adminId, @PathVariable("reviewId")Long reviewId, @RequestParam(required = true) boolean admitReview){
        boolean verified = verifyAdminExists(adminId);

        if(!verified){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No admins recognized by that Id.");
        }

        Admin admin = this.adminRepository.findById(adminId).get();
        Optional<DiningReview> optReview = this.diningReviewRepository.findById(reviewId);
        if(optReview.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "This dining review does not exist.");
        }
        else if (optReview.get().getReviewStatus()!= null){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "This dining review has already been evaluated by an admin");
        }
        DiningReview newReview = optReview.get();
        admin.setAcceptReview(admitReview);
        newReview.setReviewStatus(admin.getAcceptReview());
        this.diningReviewRepository.save(newReview);
        this.adminRepository.save(admin);
        return "Review status updated!";
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