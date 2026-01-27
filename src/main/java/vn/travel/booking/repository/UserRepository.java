package vn.travel.booking.repository;

import io.lettuce.core.dynamic.annotation.Param;
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

    @Query("select u.email from User u where u.id = :userId")
    String findEmailById(@Param("userId") Long userId);

    @Query("""
        select u.id
        from User u
        where u.role.name like 'ADMIN%'
           or u.role.name = 'SUPER_ADMIN'
    """)
    List<Long> findAdminIds();


}
