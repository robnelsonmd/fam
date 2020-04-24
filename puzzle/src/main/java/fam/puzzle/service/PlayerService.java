package fam.puzzle.service;

import fam.puzzle.domain.Player;

import java.util.List;

public interface PlayerService {
    Player getPlayer(String name);
    List<Player> getPlayerRankings(int puzzleSize);
    Player incrementCheatCount(Player player);
    Player incrementCorrectGuessCount(Player player, int size);
    Player incrementIncorrectGuessCount(Player player, int size);
    Player incrementShowAnswerCount(Player player);
    Player savePlayer(Player player);
}
