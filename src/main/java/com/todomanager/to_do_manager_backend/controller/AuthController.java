package com.todomanager.to_do_manager_backend.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class AuthController {

    private final ClientRegistration registration;

    // Inject the ClientRegistrationRepository/give us access to client details
    @Autowired
    public AuthController(ClientRegistrationRepository registrations) {
        this.registration = registrations.findByRegistrationId("okta");
    }

    // This mapping is needed because if you set the success url in the security config to
    // localhost:3000 the CSRF token will not be set properly.
    // You will NOT be able to log out (CSRF Token is necessary to make POST requests once configured)
    @GetMapping("/")
    public String redirectToFrontend() {
        return "redirect:http://localhost:3000/";
    }

    // Route to provide user data to the frontend - useful to manage role-based access for react-router-dom routes
    @GetMapping("/api/user")
    public ResponseEntity<?> getUser(@AuthenticationPrincipal OAuth2User user) {
        System.out.println(user.getAttributes().toString());
        if (user == null) {
            return new ResponseEntity<>("", HttpStatus.OK);
        } else {
            return ResponseEntity.ok().body(user.getAttributes());
        }
    }

    // The log in route
    // Automatically created by Oauth2 in the Security Config
    // /oauth2/authorization/okta

    // The logout route
    @PostMapping("/api/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, @AuthenticationPrincipal(expression = "idToken") OidcIdToken idToken) {

        // Build the logout details (end session endpoint and id token) to send to the client
        Map<String, String> logoutDetails = new HashMap<>();
        String logoutUrl = this.registration.getProviderDetails().getConfigurationMetadata().get("end_session_endpoint").toString();
        logoutDetails.put("logoutUrl", logoutUrl);
        logoutDetails.put("idToken", idToken.getTokenValue());

        // Log for debugging
        System.out.println("LogoutDetails, logoutURL: " + logoutUrl);
        System.out.println("LogoutDetails, idToken: " + idToken.getTokenValue());

        // Clear session
        if (request.getSession(false) != null) {
            request.getSession(false).invalidate();
        }

        return ResponseEntity.ok().body(logoutDetails);
    }
}
