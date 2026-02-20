package com.Bibliotek.personal.service;

import com.Bibliotek.personal.dto.NotificationDTO;
import com.Bibliotek.personal.entity.Book;
import com.Bibliotek.personal.entity.Notification;
import com.Bibliotek.personal.entity.User;
import com.Bibliotek.personal.mapper.NotificationMapper;
import com.Bibliotek.personal.repository.BookRepository;
import com.Bibliotek.personal.repository.NotificationRepository;
import com.Bibliotek.personal.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    @Autowired
    public NotificationService(NotificationRepository notificationRepository,
                              UserRepository userRepository,
                              BookRepository bookRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
    }

    public List<NotificationDTO> getUserNotifications(String userEmail) {
        User user = userRepository.findByEmail(userEmail);
        if (user == null) {
            return List.of();
        }

        List<Notification> notifications = notificationRepository.findByUserOrderByCreatedAtDesc(user);
        return notifications.stream()
                .map(NotificationMapper::toDTO)
                .collect(Collectors.toList());
    }

    public long getUnreadCount(String userEmail) {
        User user = userRepository.findByEmail(userEmail);
        if (user == null) {
            return 0;
        }
        return notificationRepository.countUnreadByUser(user);
    }

    @Transactional
    public void markAsRead(int notificationId, String userEmail) {
        User user = userRepository.findByEmail(userEmail);
        if (user == null) {
            return;
        }

        notificationRepository.findById(notificationId)
                .filter(n -> n.getUser().getId() == user.getId())
                .ifPresent(notification -> {
                    notification.setRead(true);
                    notificationRepository.save(notification);
                });
    }

    @Transactional
    public void markAllAsRead(String userEmail) {
        User user = userRepository.findByEmail(userEmail);
        if (user == null) {
            return;
        }

        List<Notification> unreadNotifications = notificationRepository.findUnreadByUser(user);
        unreadNotifications.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(unreadNotifications);
    }

    @Transactional
    public void deleteNotification(int notificationId, String userEmail) {
        User user = userRepository.findByEmail(userEmail);
        if (user == null) {
            return;
        }

        notificationRepository.findById(notificationId)
                .filter(n -> n.getUser().getId() == user.getId())
                .ifPresent(notificationRepository::delete);
    }

    @Transactional
    public void deleteAllNotifications(String userEmail) {
        User user = userRepository.findByEmail(userEmail);
        if (user == null) {
            return;
        }

        List<Notification> notifications = notificationRepository.findByUserOrderByCreatedAtDesc(user);
        notificationRepository.deleteAll(notifications);
    }

    @Transactional
    public void createNotification(String userEmail, Notification.NotificationType type, String title, String message,
                                   String relatedUserEmail, Integer relatedBookId, Integer relatedId) {
        User user = userRepository.findByEmail(userEmail);
        if (user == null) {
            return;
        }

        Notification notification = new Notification();
        notification.setUser(user);
        notification.setType(type);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setRead(false);
        notification.setRelatedId(relatedId);

        if (relatedUserEmail != null) {
            User relatedUser = userRepository.findByEmail(relatedUserEmail);
            if (relatedUser != null) {
                notification.setRelatedUser(relatedUser);
            }
        }

        if (relatedBookId != null) {
            Book relatedBook = bookRepository.findById(relatedBookId).orElse(null);
            if (relatedBook != null) {
                notification.setRelatedBook(relatedBook);
            }
        }

        notificationRepository.save(notification);
    }
}
