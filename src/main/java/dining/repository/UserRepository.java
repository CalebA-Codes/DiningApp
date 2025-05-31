package dining.repository;

import java.util.Optional;


import dining.model.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Integer> {
    Optional<User> findByName(String name);
}
