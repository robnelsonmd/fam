package fam.puzzle.service;

import fam.puzzle.domain.Player;

import java.util.List;

public interface EmailService {
    void sendAdminEmail(String subject, String text);
    void sendPuzzleLeaderChangeEmail(List<Player> players, int puzzleSize);
}
