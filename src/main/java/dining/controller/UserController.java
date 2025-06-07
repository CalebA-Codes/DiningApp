package dining.controller;

import dining.model.User;
import dining.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@RestController
@RequestMapping("api/v1/dining")
public class UserController {
    //Declare and assign the repository to the class
    private final UserRepository userRepository;
    public UserController(final UserRepository userRepository){
        this.userRepository = userRepository;
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
}
