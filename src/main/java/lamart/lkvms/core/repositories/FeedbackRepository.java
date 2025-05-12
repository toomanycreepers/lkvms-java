package lamart.lkvms.core.repositories;

import java.util.Optional;

import lamart.lkvms.core.entities.logistic.Cargo;
import lamart.lkvms.core.entities.logistic.Feedback;
import lamart.lkvms.core.entities.user.User;

public interface FeedbackRepository extends SoftDeletesRepository<Feedback, Long>{
    Optional<Feedback> findByAuthorAndRelatedCargo(User user, Cargo cargo);
}
