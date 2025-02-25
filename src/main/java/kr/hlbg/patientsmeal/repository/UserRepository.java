package kr.hlbg.patientsmeal.repository;

import kr.hlbg.patientsmeal.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(String username);
}
