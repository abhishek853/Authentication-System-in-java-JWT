package abhi.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class controller {

    @Autowired
    private Repo userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("")
    public String viewHomePage() {
        return "index";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "signup_form";
    }

    @PostMapping("/process_register")
    public String processRegister(User user) {
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        userRepo.save(user);
        return "register_success";
    }

    @GetMapping("/users")
    public String listUsers(Model model, Authentication authentication) {
        String loggedInUserEmail = authentication.getName();

        List<User> filteredUsers = userRepo.findAll()
                .stream()
                .filter(user -> !user.getEmail().equals(loggedInUserEmail))
                .collect(Collectors.toList());

        model.addAttribute("listUsers", filteredUsers);
        return "users";
    }

    @GetMapping("/login")
    public String showLoginPage(@RequestParam(value = "error", required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("error", "Invalid email or password. Please try again.");
        }
        return "login";
    }

    @PostMapping("/login")
    @ResponseBody
    public ResponseEntity<?> handleLogin(@RequestParam String email, @RequestParam String password) {
        User user = userRepo.findByEmail(email);

        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            // Generate JWT Token
            String token = jwtUtil.generateToken(user.getEmail());
            return ResponseEntity.ok(Map.of(
                    "token", token,
                    "message", "Login successful"
            ));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "error", "Invalid email or password"
            ));
        }
    }

    @GetMapping("/forgot-password")
    public String showForgotPasswordPage() {
        return "forgot_password";
    }

    @PostMapping("/process_forgot_password")
    public String processForgotPassword(@RequestParam String email, @RequestParam String newPassword, Model model) {
        User user = userRepo.findByEmail(email);
        if (user != null) {
            String encodedPassword = passwordEncoder.encode(newPassword);
            user.setPassword(encodedPassword);
            userRepo.save(user);
            model.addAttribute("message", "Password updated successfully!");
            return "password_updated";
        } else {
            model.addAttribute("error", "Email not found.");
            return "forgot_password";
        }
    }
}
