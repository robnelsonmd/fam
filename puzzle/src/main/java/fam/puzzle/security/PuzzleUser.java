package fam.puzzle.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;

public class PuzzleUser extends User implements Serializable {
    private static final long serialVersionUID = -8010167368134812725L;

    public PuzzleUser(String username) {
        super(username, "{noop}", Collections.emptyList());
    }

    @Override
    @JsonIgnore
    public Collection<GrantedAuthority> getAuthorities() {
        return super.getAuthorities();
    }

    @Override
    @JsonIgnore
    public String getPassword() {
        return "";
    }

    @Override
    @JsonIgnore
    public String getUsername() {
        return super.getUsername();
    }

    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return super.isEnabled();
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return super.isAccountNonExpired();
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return super.isAccountNonLocked();
    }

    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return super.isCredentialsNonExpired();
    }
}
