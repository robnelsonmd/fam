package fam.puzzle.security;

import fam.puzzle.domain.Player;
import fam.puzzle.service.PlayerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class PuzzleUserDetailsServiceImpl implements PuzzleUserDetailsService {
    private static final Logger LOG = LoggerFactory.getLogger(PuzzleUserDetailsServiceImpl.class);
    private final PlayerService playerService;

    public PuzzleUserDetailsServiceImpl(PlayerService playerService) {
        this.playerService = playerService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Player player = playerService.getPlayer(username);

        if (player != null) {
            LOG.info(String.format("Authenticated user %s",player.getName()));
            return player;
        }

        LOG.warn(String.format("Failed to authenticate user %s",username));
        throw new UsernameNotFoundException(String.format("User %s not found",username));
    }
}
