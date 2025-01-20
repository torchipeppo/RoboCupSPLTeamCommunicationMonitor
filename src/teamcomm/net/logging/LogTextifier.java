package teamcomm.net.logging;

import common.net.logging.Logger;
import common.net.GameControlReturnDataPackage;
import common.net.SPLTeamMessagePackage;
import data.GameControlData;
import data.GameControlReturnData;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import teamcomm.data.GameState;
import teamcomm.net.GameControlReturnDataReceiverTCM;
import teamcomm.net.SPLTeamMessageReceiverTCM;
import java.util.Deque;
import java.util.LinkedList;
import java.io.PrintWriter;
import java.nio.ByteBuffer;

/**
 * Singleton class for I dunno
 *
 * @author Francesco Petri
 */
public class LogTextifier {

    private static final LogTextifier instance = new LogTextifier();

    /**
     * Returns the only instance of this class.
     *
     * @return instance
     */
    public static LogTextifier getInstance() {
        return instance;
    }

    /**
     * Opens a log file.
     *
     * @param logfile file
     * @throws FileNotFoundException if the file could not be found
     * @throws IOException if an other I/O error happened
     */
    public void open(final File logfile, boolean thenExit) throws FileNotFoundException, IOException {
        System.out.println("hi");

        // Drain package queue of SPLTeamMessageReceiver and GameControlReturnDataReceiver
        SPLTeamMessageReceiverTCM.getInstance().clearPackageQueue();
        GameControlReturnDataReceiverTCM.getInstance().clearPackageQueue();

        // Reset GameState
        GameState.getInstance().reset();

        // Prevent the logger from logging (disableLogging can't be used for that)
        Logger.getInstance().setIsReplaying(true);

        Deque<LogReplayTask.LoggedObject> nextItems = new LinkedList<>();

        // Open new log
        if (logfile.getName().endsWith(".yaml")) {
            LogYamlLoader.load(logfile, nextItems);
        } else {
            throw new IOException("Non Eurosedia");
        }


        int sectionNumber = 0;
        final String CSV_HEADER = "playing,player,team,fallen,x,y,theta,ballage,ballx,bally,secsremaining\n";
        String sectionAccumulator = CSV_HEADER;
        for (LogReplayTask.LoggedObject obj = nextItems.pollFirst(); obj != null; obj = nextItems.pollFirst()) {
            if (obj.typeid == 14383421) {
                try (PrintWriter out = new PrintWriter(logfile.getAbsolutePath() + "__section_" + sectionNumber + ".csv")) {
                    out.println(sectionAccumulator);
                    System.out.println("Written to " + logfile.getAbsolutePath() + "__section_" + sectionNumber + ".csv");
                }
                sectionAccumulator = CSV_HEADER;
                sectionNumber++;
            }
            else if (obj.object != null) {
                if (obj.object instanceof SPLTeamMessagePackage) {
                    ;  // nulla
                } else if (obj.object instanceof GameControlReturnDataPackage) {
                    GameControlReturnDataPackage the_package = (GameControlReturnDataPackage) obj.object;
                    final GameControlReturnData message = new GameControlReturnData();
                    message.fromByteArray(ByteBuffer.wrap(the_package.message));
                    if (!(message.headerValid && message.versionValid && message.playerNumValid && message.teamNumValid)) {
                        continue;
                    }
                    message.playing = (obj.gameState == GameControlData.STATE_PLAYING);
                    message.secsRemaining = obj.secsRemaining;
                    sectionAccumulator += message.toCSVLine();
                } else if (obj.object instanceof GameControlData) {
                    ;  // nulla
                }
            }
        }

        this.close();

        System.out.println("bye");
        if (thenExit) {
            System.exit(0);
        }
    }

    /**
     * Closes the currently opened log file.
     */
    public void close() {
        // Drain package queue of SPLTeamMessageReceiver and GameControlReturnDataReceiver
        SPLTeamMessageReceiverTCM.getInstance().clearPackageQueue();
        GameControlReturnDataReceiverTCM.getInstance().clearPackageQueue();

        // Reset GameState
        GameState.getInstance().reset();

        // Tell the logger that it can log again
        Logger.getInstance().setIsReplaying(false);
    }
}
