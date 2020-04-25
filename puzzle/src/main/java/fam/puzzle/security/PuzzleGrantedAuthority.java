package fam.puzzle.security;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.security.core.GrantedAuthority;

import java.io.Serializable;
import java.util.Objects;

public class PuzzleGrantedAuthority implements GrantedAuthority, Serializable {
    private static final long serialVersionUID = 4981263756665020642L;

    public static PuzzleGrantedAuthority ADMIN = new PuzzleGrantedAuthority("ADMIN");
    public static PuzzleGrantedAuthority USER = new PuzzleGrantedAuthority("USER");

    private final String authority;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public PuzzleGrantedAuthority(@JsonProperty("authority") String authority) {
        this.authority = authority;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PuzzleGrantedAuthority that = (PuzzleGrantedAuthority) o;
        return authority.equals(that.authority);
    }

    @Override
    public String getAuthority() {
        return authority;
    }

    @Override
    public int hashCode() {
        return Objects.hash(authority);
    }
}
