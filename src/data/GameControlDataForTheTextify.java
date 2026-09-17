package data;

import data.GameControlData;

/**
 * This class is part of the data wich are send to the robots. It just
 * represents this data, reads and writes between C-structure and Java, nothing
 * more.
 *
 * @author Michel Bartsch
 */
public class GameControlDataForTheTextify implements GameControlData {

    public String toCSVLine() {
        String out = "";
        String temp;

        out += GAMECONTROLLER_STRUCT_HEADER + ",";  // header
        out += GAMECONTROLLER_STRUCT_VERSION + ",";  // version
        out += (packetNumber & 0xFF) + ",";  // packet number
        out += playersPerTeam + ",";  // players per team

        switch(competitionType) {
            case COMPETITION_TYPE_SMALL:
                temp = "small";
                break;
            case COMPETITION_TYPE_MIDDLE:
                temp = "middle";
                break;
            case COMPETITION_TYPE_LARGE:
                temp = "large";
                break;
            default:
                temp = "undefined(" + competitionType + ")";
                break;
        }
        out += temp + ",";  // competition type

        switch (stopped) {
            case C_TRUE:
                temp = "true";
                break;
            case C_FALSE:
                temp = "false";
                break;
            default:
                temp = "undefined(" + firstHalf + ")";
        }
        out += "            stopped: " + temp + "\n";
        switch (gamePhase) {
            case GAME_PHASE_NORMAL:
                temp = "normal";
                break;
            case GAME_PHASE_PENALTY_SHOOT_OUT:
                temp = "penalty shoot-out";
                break;
            case GAME_PHASE_EXTRA_TIME:
                temp = "extra time";
                break;
            case GAME_PHASE_TIMEOUT:
                temp = "timeout";
                break;
            default:
                temp = "undefined(" + gamePhase + ")";
        }
        out += "          gamePhase: " + temp + "\n";
        switch (gameState) {
            case STATE_INITIAL:
                temp = "initial";
                break;
            case STATE_READY:
                temp = "ready";
                break;
            case STATE_SET:
                temp = "set";
                break;
            case STATE_PLAYING:
                temp = "playing";
                break;
            case STATE_FINISHED:
                temp = "finish";
                break;
            default:
                temp = "undefined(" + gameState + ")";
        }
        out += "          gameState: " + temp + "\n";
        switch (setPlay) {
            case SET_PLAY_NONE:
                temp = "none";
                break;
            case SET_PLAY_DIRECT_FREE_KICK:
                temp = "direct free kick";
                break;
            case SET_PLAY_INDIRECT_FREE_KICK:
                temp = "indirect free kick";
                break;
            case SET_PLAY_PENALTY_KICK:
                temp = "penalty kick";
                break;
            case SET_PLAY_THROW_IN:
                temp = "throw-in";
                break;
            case SET_PLAY_GOAL_KICK:
                temp = "goal kick";
                break;
            case SET_PLAY_CORNER_KICK:
                temp = "corner kick";
                break;
            default:
                temp = "undefined(" + setPlay + ")";
        }
        out += "            setPlay: " + temp + "\n";
        switch (firstHalf) {
            case C_TRUE:
                temp = "true";
                break;
            case C_FALSE:
                temp = "false";
                break;
            default:
                temp = "undefined(" + firstHalf + ")";
        }
        out += "          firstHalf: " + temp + "\n";
        out += "        kickingTeam: " + kickingTeam + "\n";
        out += "      secsRemaining: " + secsRemaining + "\n";
        out += "      secondaryTime: " + secondaryTime + "\n";
        return out;
    }
}
