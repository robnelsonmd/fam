package fam.puzzle.service;

import fam.messaging.text.CellCarrier;
import fam.puzzle.domain.Player;
import fam.puzzle.domain.Puzzle;

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
    Player removePuzzle(Player player, int puzzleSize);
    Player savePlayer(Player player);
    Player updateEmailSettings(Player player, boolean receiveEmails, String emailAddress);
    Player updatePuzzle(Player player, Puzzle puzzle);
    Player updateTextSettings(Player player, boolean receiveTexts, CellCarrier cellCarrier, String cellNumber);
}
