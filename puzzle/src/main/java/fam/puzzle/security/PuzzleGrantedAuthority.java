package fam.puzzle.security;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.security.core.GrantedAuthority;

public class PuzzleGrantedAuthority implements GrantedAuthority {
    private final String authority;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public PuzzleGrantedAuthority(@JsonProperty("authority") String authority) {
        this.authority = authority;
    }

    @Override
    public String getAuthority() {
        return authority;
    }
}
