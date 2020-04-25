package fam.puzzle.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
        HttpSession session = request.getSession();
        session.setAttribute("player", authentication.getPrincipal());

        boolean hasAdminRole = authentication.getAuthorities().stream()
                .anyMatch(PuzzleGrantedAuthority.ADMIN::equals);

        if (hasAdminRole) {
            redirectStrategy.sendRedirect(request, response, "admin");
            return;
        }

        redirectStrategy.sendRedirect(request, response, "home");
    }
}
