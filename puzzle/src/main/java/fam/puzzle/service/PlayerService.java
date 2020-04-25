package fam.puzzle.service;

import fam.messaging.text.CellCarrier;
import fam.puzzle.domain.Player;

import java.util.List;

public interface PlayerService {
    Player getPlayer(String name);
    List<Player> getPlayers();
    List<Player> getPlayerRankings(int puzzleSize);
    Player incrementCheatCount(Player player);
    Player incrementCorrectGuessCount(Player player, int size);
    Player incrementIncorrectGuessCount(Player player, int size);
    Player incrementShowAnswerCount(Player player);
    Player savePlayer(Player player);
    Player updateCellInfo(Player player, CellCarrier cellCarrier, String cellNumber);
}
