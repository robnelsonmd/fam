package fam.puzzle.service;

import fam.messaging.text.CellCarrier;
import fam.puzzle.domain.Player;

import java.util.List;

public interface PlayerService {
    Player createPlayer(String name, boolean receiveEmails,
                        String emailAddress, boolean receiveTexts,
                        CellCarrier cellCarrier, String cellNumber);
    Player getPlayer(String name);
    List<Player> getPlayers();
    List<Player> getPlayerRankings(int puzzleSize);
    Player incrementCheatCount(Player player);
    Player incrementCorrectGuessCount(Player player, int size);
    Player incrementIncorrectGuessCount(Player player, int size);
    Player incrementShowAnswerCount(Player player);
    Player savePlayer(Player player);
    Player updateEmailSettings(Player player, boolean receiveEmails, String emailAddress);
    Player updateTextSettings(Player player, boolean receiveTexts, CellCarrier cellCarrier, String cellNumber);
}
