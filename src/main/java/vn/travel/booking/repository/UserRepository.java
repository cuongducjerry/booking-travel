package vn.travel.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.travel.booking.entity.User;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    boolean existsByEmail(String email);

    User findByEmail(String username);

    User findByRefreshTokenAndEmail(String token, String email);

    @Query("""
            SELECT DISTINCT u FROM User u
            JOIN u.role r
            WHERE r.name LIKE 'ADMIN%'
        """)
    List<User> findAllAdmins();

}
