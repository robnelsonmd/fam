package fam.puzzle.service;

import fam.puzzle.domain.Player;

import java.util.List;

public interface PlayerService {
    Player getPlayer(String name);
    List<Player> getPlayers();
    Player incrementCheatCount(Player player);
    Player incrementCorrectGuessCount(Player player);
    Player incrementIncorrectGuessCount(Player player);
    Player incrementShowAnswerCount(Player player);
}
