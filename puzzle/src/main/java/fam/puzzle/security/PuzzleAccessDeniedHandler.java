package fam.puzzle.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class PuzzleAccessDeniedHandler implements AccessDeniedHandler {
    private static final Logger LOG = LoggerFactory.getLogger(PuzzleAccessDeniedHandler.class);

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null) {
            LOG.warn(String.format("User %s attempted to access URL %s without permission",authentication.getName(),request.getRequestURI()));
        }

        response.sendRedirect(request.getContextPath() + "/accessDenied");
    }
}
