package data;

import data.GameControlData;
import data.GameControlReturnData;
import data.TeamInfo;
import data.PlayerInfo;

/**
 * This class is what robots send to the GameController.
 * It just represents this data, reads and writes between C-structure and
 * Java, nothing more.
 *
 * @author Michel Bartsch
 */
public class GameControlReturnDataForTheTextify extends GameControlReturnData {

    public GameControlData gameControlData;
    public long gcTime;

    private TeamInfo findTeamInfo(int teamNumber) {
        if (teamNumber == gameControlData.team[0].teamNumber) {
            return gameControlData.team[0];
        }
        if (teamNumber == gameControlData.team[1].teamNumber) {
            return gameControlData.team[1];
        }
        // wut?!
        return null;
    }

    public String toCSVLine() {
        boolean playing = (gameControlData.gameState == GameControlData.STATE_PLAYING);
        boolean penalized = (findTeamInfo(teamNum).player[playerNum].penalty != PlayerInfo.PENALTY_NONE);
        return Boolean.toString(playing) + "," +
                Byte.toString(playerNum) + "," +
                Byte.toString(teamNum) + "," +
                Boolean.toString(fallen) + "," +
                Float.toString(pose[0]) + "," + 
                Float.toString(pose[1]) + "," + 
                Float.toString(pose[2]) + "," + 
                Float.toString(ballAge) + "," + 
                Float.toString(ball[0]) + "," + 
                Float.toString(ball[1]) + "," +
                Boolean.toString(penalized) + "," +
                Float.toString(gcTime) + "," +
                Short.toString(gameControlData.secsRemaining) + "\n";
    }
}
