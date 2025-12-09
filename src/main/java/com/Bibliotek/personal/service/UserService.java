package com.bibliotek.personal.service;

import com.bibliotek.personal.dto.user.UserDTO;
import com.bibliotek.personal.dto.user.UserRegistrationDTO;
import com.bibliotek.personal.entity.*;
import com.bibliotek.personal.mapper.UserMapper;
import com.bibliotek.personal.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepository;



    @Value("${FrontEnd.url}")
    private String FrontEndUrl;

    @Value("${Smtp.email}")
    private String smtpEmail;

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ExchangeRepository exchangeRepository;

    @Autowired
    private FriendshipRepository friendshipRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserBookStatusRepository userBookStatusRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;

    }

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::toDTO)
                .toList();
    }


    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User findByOauthProviderId(String oauthProviderId) {
        return userRepository.findByOauthProviderId(oauthProviderId);
    }

    public UserDTO getUserById(int id) {
        Optional<User> user = userRepository.findById(id);
        return user.map(UserMapper::toDTO).orElse(null);
    }
    public List<UserDTO> getUsersByEmailOrUsername(String search) {
        // Call the updated repository query method
        List<User> users = userRepository.findByUsernameOrEmailContaining(search);

        // Convert User entities to UserDTOs
        return users.stream().map(UserMapper::toDTO).collect(Collectors.toList());
    }





    public UserDTO createUser(UserRegistrationDTO userDTO) {
        // Check if user exists by email
        User existingUser = userRepository.findByEmail(userDTO.getEmail());
        if (existingUser != null) {
            throw new RuntimeException("User with this email already exists");
        }

        // Convert DTO to Entity
        User user = new User();
        user.setEmail(userDTO.getEmail());
        user.setUsername(userDTO.getUsername());

        // Handle password (required for regular users)
        if (userDTO.getPassword() == null || userDTO.getPassword().isEmpty()) {
            String defaultPassword = "OAuthGeneratedPassword123"; // Fallback for OAuth users
            user.setPassword(new BCryptPasswordEncoder().encode(defaultPassword));
        } else {
            user.setPassword(new BCryptPasswordEncoder().encode(userDTO.getPassword()));
        }


        userRepository.save(user);


        return UserMapper.toDTO(user);
    }


    public UserDTO updateUser(int id, UserDTO userDTO) {
        Optional<User> existingUser = userRepository.findById(id);
        if (existingUser.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        User user = existingUser.get();
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        userRepository.save(user);
        return UserMapper.toDTO(user);
    }

    public UserDTO updateCurrentUser(String email, String username) {
        if (username == null || username.isBlank()) {
            throw new RuntimeException("Username cannot be empty");
        }

        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        user.setUsername(username);
        userRepository.save(user);
        return UserMapper.toDTO(user);
    }

    public void updateCurrentUserPassword(String email, String oldPassword, String newPassword) {
        if (oldPassword == null || oldPassword.isBlank() || newPassword == null || newPassword.isBlank()) {
            throw new RuntimeException("Password fields cannot be empty");
        }

        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if (!encoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("Old password is incorrect");
        }

        user.setPassword(encoder.encode(newPassword));
        userRepository.save(user);
    }

    public void deleteUser(int id) {
        Optional<User> existingUser = userRepository.findById(id);
        if (existingUser.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        userRepository.delete(existingUser.get());
    }

    @Transactional
    public void deleteUserByEmail(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("User not found with email: " + email);
        }

        int userId = user.getId();
        System.out.println("🗑️ Deleting user account: " + email);
        System.out.println("   - User ID: " + userId);
        System.out.println("   - This will permanently delete:");
        System.out.println("     * All user's books (user_books entries only, book_details preserved)");
        System.out.println("     * All user's reviews");
        System.out.println("     * All user's exchanges");
        System.out.println("     * All user's friendships");
        System.out.println("     * All user's notifications");
        System.out.println("     * All user's book statuses");
        System.out.println("     * User account");

        try {
            // 1. Delete all user's books (user_books entries) - but NOT book_details
            List<Book> userBooks = bookRepository.findByOwner(user);
            System.out.println("   - Found " + userBooks.size() + " books to delete (user_books entries)");
            for (Book book : userBooks) {
                // Delete reviews for this book (cascade will handle this, but being explicit)
                reviewRepository.findReviewsByBook(book).forEach(reviewRepository::delete);
                // Delete exchanges for this book
                exchangeRepository.findExchangesByBook(book).forEach(exchangeRepository::delete);
                // Delete user book statuses for this book
                userBookStatusRepository.findAllByBookId(book.getId()).forEach(userBookStatusRepository::delete);
            }
            bookRepository.deleteAll(userBooks);
            System.out.println("   ✅ Deleted " + userBooks.size() + " user_books entries (book_details preserved)");

            // 2. Delete all reviews created by this user (for books they don't own)
            List<Review> userReviews = reviewRepository.findByUser(user);
            System.out.println("   - Found " + userReviews.size() + " reviews to delete");
            reviewRepository.deleteAll(userReviews);
            System.out.println("   ✅ Deleted " + userReviews.size() + " reviews");

            // 3. Delete all exchanges where user is borrower
            List<Exchange> userExchanges = exchangeRepository.findByBorrowerId(userId);
            System.out.println("   - Found " + userExchanges.size() + " exchanges to delete");
            exchangeRepository.deleteAll(userExchanges);
            System.out.println("   ✅ Deleted " + userExchanges.size() + " exchanges");

            // 4. Delete all friendships (both as user and as friend)
            List<Friendship> userFriendships = friendshipRepository.findAllByUser(user);
            List<Friendship> friendFriendships = friendshipRepository.findAllByFriend(user);
            System.out.println("   - Found " + userFriendships.size() + " friendships as user");
            System.out.println("   - Found " + friendFriendships.size() + " friendships as friend");
            friendshipRepository.deleteAll(userFriendships);
            friendshipRepository.deleteAll(friendFriendships);
            System.out.println("   ✅ Deleted " + (userFriendships.size() + friendFriendships.size()) + " friendships");

            // 5. Delete all notifications
            List<Notification> userNotifications = notificationRepository.findByUserOrderByCreatedAtDesc(user);
            System.out.println("   - Found " + userNotifications.size() + " notifications to delete");
            notificationRepository.deleteAll(userNotifications);
            System.out.println("   ✅ Deleted " + userNotifications.size() + " notifications");

            // 6. Delete all user book statuses (in case any remain)
            List<UserBookStatus> userBookStatuses = userBookStatusRepository.findByUserId(userId);
            System.out.println("   - Found " + userBookStatuses.size() + " user book statuses to delete");
            userBookStatusRepository.deleteAll(userBookStatuses);
            System.out.println("   ✅ Deleted " + userBookStatuses.size() + " user book statuses");

            // 7. Delete password reset tokens
            tokenRepository.deleteAll(tokenRepository.findAll().stream()
                    .filter(token -> token.getUser().getId() == userId)
                    .toList());
            System.out.println("   ✅ Deleted password reset tokens");

            // 8. Finally, delete the user
            userRepository.delete(user);
            userRepository.flush(); // Force immediate database write
            
            System.out.println("✅ User account and all associated data deleted successfully");
            System.out.println("✅ IMPORTANT: book_details entries were preserved (shared book information remains)");
        } catch (Exception e) {
            System.out.println("❌ ERROR deleting user account: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to delete user account: " + e.getMessage(), e);
        }
    }

    public UserDTO login(String email, String password) {
        System.out.println("UserService.login called for email: " + email);
        User user = userRepository.findByEmail(email);
        
        if (user == null) {
            System.out.println("Login failed: User not found for email: " + email);
            throw new RuntimeException("Invalid credentials");
        }
        
        System.out.println("User found: " + user.getEmail() + ", checking password...");
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        boolean passwordMatches = encoder.matches(password, user.getPassword());
        
        if (!passwordMatches) {
            System.out.println("Login failed: Password does not match for email: " + email);
            throw new RuntimeException("Invalid credentials");
        }
        
        System.out.println("Login successful for email: " + email);
        return UserMapper.toDTO(user);
    }

    @Transactional
    public User save(User user) {
        User savedUser = userRepository.save(user);
        userRepository.flush(); // Force immediate database write
        return savedUser;
    }

    public boolean sendPasswordResetEmail(String email) {
        User user = userRepository.findByEmail(email);

        if (user == null) return false; // User not found



        // Generate reset token
        String token = UUID.randomUUID().toString();


        PasswordResetToken resetToken = new PasswordResetToken(user, token);

        tokenRepository.save(resetToken);


        String resetLink = FrontEndUrl+"/reset-password?token=" + token;

        String subject = "Reset Your Password";
        String body = "Click here to reset your password: " + resetLink;

        return sendEmail(email, subject, body);
    }

    private boolean sendEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(smtpEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);

            mailSender.send(message);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }
    public PasswordResetToken validatePasswordResetToken(String token) {
        return tokenRepository.findByToken(token);
    }

    public void updateUserPassword(User user, String newPassword) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }




}
