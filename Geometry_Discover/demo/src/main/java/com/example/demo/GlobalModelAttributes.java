package com.example.demo;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Adds the current user (and admin flag) to every Model so the shared navbar
 * fragment can render without each controller having to wire the values manually.
 */
@ControllerAdvice
public class GlobalModelAttributes {

    @ModelAttribute
    public void addCurrentUser(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        boolean authenticated = authentication != null
            && authentication.isAuthenticated()
            && !(authentication instanceof AnonymousAuthenticationToken);

        String username = authenticated ? authentication.getName() : null;
        boolean isAdmin = authenticated && UserService.isAdmin(username);

        model.addAttribute("currentUser", username);
        model.addAttribute("currentUserIsAdmin", isAdmin);
        model.addAttribute("isAuthenticated", authenticated);
    }
}
