package dining.repository;

import java.util.List;
import java.util.Optional;

import dining.model.DiningReview;

import org.springframework.data.repository.CrudRepository;

public interface DiningReviewRepository extends CrudRepository<DiningReview,Integer> {
    List<DiningReview> findByReviewStatusNull();
    List<DiningReview> findByRestaurantId(Long restaurantId);
    List<DiningReview> findByRestaurantIdAndReviewStatusTrue(Long restaurantId);
    Optional<DiningReview> findByName(String name);
    Optional<DiningReview> findById(Long id);

}
