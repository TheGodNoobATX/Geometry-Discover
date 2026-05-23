package com.example.demo;

import java.security.Principal;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class WebController {

    @Autowired
    private UserService userService;

    @Autowired
    private LevelFeedbackRepository levelFeedbackRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/")
    public String index() {
        return "base";
    }

    @GetMapping("/search")
    public String search(Model model, Authentication authentication) {
        boolean isAdmin = authentication != null
            && authentication.isAuthenticated()
            && authentication.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
        model.addAttribute("isAdmin", isAdmin);
        return "search";
    }

    @GetMapping("/home")
    public String home() {
        return "base";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @GetMapping("/profile")
    public String myProfile(Model model, Principal principal) {
        return renderProfile(principal.getName(), true, model);
    }

    @GetMapping("/profile/{username}")
    public String userProfile(@PathVariable String username, Model model, Principal principal) {
        boolean isOwner = principal != null && username.equals(principal.getName());
        return renderProfile(username, isOwner, model);
    }

    private String renderProfile(String username, boolean isOwner, Model model) {
        boolean isAdmin = UserService.isAdmin(username);

        long ratedCount = levelFeedbackRepository.countByPlayerIdAndRatingIsNotNull(username);

        boolean profileExists = isOwner
            || isAdmin
            || ratedCount > 0
            || userRepository.findByUsername(username) != null
            || userProfileRepository.findByUsername(username).isPresent();

        if (!profileExists) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile not found.");
        }

        List<LevelCatalog.Level> topFiveStarLevels = levelFeedbackRepository
            .findTop3ByPlayerIdAndRatingOrderByUpdatedAtDesc(username, 5)
            .stream()
            .map(fb -> LevelCatalog.find(fb.getLevelKey()).orElse(null))
            .filter(Objects::nonNull)
            .toList();

        LevelCatalog.Level favoriteLevel = userProfileRepository.findByUsername(username)
            .map(UserProfile::getFavoriteLevelKey)
            .flatMap(LevelCatalog::find)
            .orElse(null);

        model.addAttribute("username", username);
        model.addAttribute("ratedCount", ratedCount);
        model.addAttribute("topFiveStarLevels", topFiveStarLevels);
        model.addAttribute("favoriteLevel", favoriteLevel);
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("isOwner", isOwner);
        return "profile";
    }

    @GetMapping("/account")
    public String account(Model model, Principal principal) {
        String username = principal.getName();

        UserProfile profile = userProfileRepository.findByUsername(username)
            .orElseGet(() -> new UserProfile(username));

        List<LevelCatalog.Level> sortedLevels = LevelCatalog.all().stream()
            .sorted(Comparator.comparing(LevelCatalog.Level::name, String.CASE_INSENSITIVE_ORDER))
            .toList();

        model.addAttribute("username", username);
        model.addAttribute("favoriteLevelKey",
            profile.getFavoriteLevelKey() == null ? "" : profile.getFavoriteLevelKey());
        model.addAttribute("allLevels", sortedLevels);
        return "account";
    }

    @PostMapping("/account/favorite")
    public String saveFavorite(
        @RequestParam(value = "favoriteLevelKey", required = false) String favoriteLevelKey,
        Principal principal
    ) {
        String username = principal.getName();
        UserProfile profile = userProfileRepository.findByUsername(username)
            .orElseGet(() -> new UserProfile(username));

        String trimmed = favoriteLevelKey == null ? "" : favoriteLevelKey.trim();
        if (trimmed.isBlank() || LevelCatalog.find(trimmed).isEmpty()) {
            profile.setFavoriteLevelKey(null);
        } else {
            profile.setFavoriteLevelKey(trimmed);
        }
        userProfileRepository.save(profile);

        return "redirect:/profile";
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam String username,
                               @RequestParam String password,
                               @RequestParam String email,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            model.addAttribute("error", "Username and password are required.");
            model.addAttribute("formUsername", username);
            model.addAttribute("formEmail", email);
            return "register";
        }
        if (userService.userExists(username)) {
            model.addAttribute("error", "That username is already taken.");
            model.addAttribute("formUsername", username);
            model.addAttribute("formEmail", email);
            return "register";
        }
        userService.registerUser(username, password, email);
        redirectAttributes.addFlashAttribute("flashSuccess",
            "Account created! Sign in with your new credentials.");
        return "redirect:/login";
    }
}
