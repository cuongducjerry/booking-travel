package vn.travel.booking.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.travel.booking.entity.Notification;
import vn.travel.booking.entity.User;
import vn.travel.booking.repository.NotificationRepository;

import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserService userService;

    public NotificationService(
            NotificationRepository notificationRepository,
            UserService userService) {
        this.notificationRepository = notificationRepository;
        this.userService = userService;
    }

    @Transactional
    public void notifyUser(String title, String content, User user) {
        Notification noti = new Notification();
        noti.setTitle(title);
        noti.setContent(content);
        noti.setUser(user);
        this.notificationRepository.save(noti);
    }

    @Transactional
    public void notifyAdmins(String title, String content) {
        List<User> admins = this.userService.getAllAdmins();

        admins.forEach(admin -> {
            Notification noti = new Notification();
            noti.setTitle(title);
            noti.setContent(content);
            noti.setUser(admin);
            this.notificationRepository.save(noti);
        });
    }

}
