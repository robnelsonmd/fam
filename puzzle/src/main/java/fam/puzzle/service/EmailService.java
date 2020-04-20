package fam.puzzle.service;

import fam.puzzle.domain.Player;

import java.util.List;

public interface EmailService {
    void sendPuzzleLeaderChangeEmail(List<Player> players);
}
