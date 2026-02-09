package vn.travel.booking.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import vn.travel.booking.entity.Notification;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long>, JpaSpecificationExecutor<Notification> {

    List<Notification> findByUserIdAndActiveTrueOrderByCreatedAtDesc(Long userId);

    @Modifying
    @Query("""
        update Notification n
        set n.isRead = true
        where n.user.id = :userId
          and n.isRead = false
    """)
    void markAllAsRead(@Param("userId") Long userId);

}
