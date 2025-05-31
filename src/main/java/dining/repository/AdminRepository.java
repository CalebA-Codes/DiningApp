package dining.repository;

import org.springframework.data.repository.CrudRepository;
import java.util.Optional;

import dining.model.Admin;

public interface AdminRepository extends CrudRepository <Admin, Integer>{
    Optional<Admin> findById(Long id);
}
