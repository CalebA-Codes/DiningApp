package dining.controller;

import dining.model.Admin;
import dining.model.DiningReview;
import dining.repository.AdminRepository;
import dining.repository.DiningReviewRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/v1/dining")
public class AdminController {
    //Declare repository for this class
    private final AdminRepository adminRepository;
    private final DiningReviewRepository diningReviewRepository;

    public AdminController(final AdminRepository adminRepository, final DiningReviewRepository diningReviewRepository){
        this.adminRepository = adminRepository;
        this.diningReviewRepository = diningReviewRepository;
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
}