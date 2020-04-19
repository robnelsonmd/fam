package fam.puzzle.security;

import org.springframework.security.core.userdetails.User;

import java.util.Collections;

public class PuzzleUser extends User {
    public PuzzleUser(String username) {
        super(username, "{noop}", Collections.emptyList());
    }

    @Override
    public String getPassword() {
        return "";
    }
}
