package edu.sharif.ce.appacman.model;

import edu.sharif.ce.appacman.controller.MapController;
import edu.sharif.ce.appacman.view.GhostPlayer;
import edu.sharif.ce.appacman.view.MapMakerMap;
import edu.sharif.ce.appacman.view.PacmanPlayer;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GameState {

    String map;
    Point pacmanLocation;
    Point[] ghostLocation;
    int score, livesCount;
    float[] invisibleTimes;
    float[] frightTimes;
    String mapName;
    boolean isHard;

    public GameState() {
    }

    public GameState(MapMakerMap gameMap, PacmanPlayer player, GhostPlayer[] ghosts, int score, int livesCount, String mapName, boolean isHard) {
        this.score = score;
        this.livesCount = livesCount;
        this.mapName = mapName;
        this.isHard = isHard;
        ghostLocation = new Point[4];
        invisibleTimes = new float[4];
        frightTimes = new float[4];
        map = MapController.getInstance().mapToString(gameMap);
        pacmanLocation = player.currentPosition;
        for (int i = 0; i < 4; i++) {
            ghostLocation[i] = ghosts[i].currentPosition;
            invisibleTimes[i] = ghosts[i].invisibleTime;
            frightTimes[i] = ghosts[i].frightTime;
        }
    }
}
