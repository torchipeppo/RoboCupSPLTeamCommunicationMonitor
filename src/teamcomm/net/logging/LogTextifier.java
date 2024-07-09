package teamcomm.net.logging;

import common.net.logging.Logger;
import common.net.GameControlReturnDataPackage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import javax.swing.event.EventListenerList;
import teamcomm.data.GameState;
import teamcomm.net.GameControlReturnDataReceiverTCM;
import teamcomm.net.SPLTeamMessageReceiverTCM;

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
    public void open(final File logfile) throws FileNotFoundException, IOException {

        // Drain package queue of SPLTeamMessageReceiver and GameControlReturnDataReceiver
        SPLTeamMessageReceiverTCM.getInstance().clearPackageQueue();
        GameControlReturnDataReceiverTCM.getInstance().clearPackageQueue();

        // Reset GameState
        GameState.getInstance().reset();

        // Prevent the logger from logging (disableLogging can't be used for that)
        Logger.getInstance().setIsReplaying(true);

        Deque<LoggedObject> nextItems = new LinkedList<>();

        // Open new log
        if (logfile.getName().endsWith(".yaml")) {
            LogYamlLoader.load(logfile, nextItems);
        } else {
            throw new IOException("Non Eurosedia");
        }


        int sectionNumber = 0;
        String sectionAccumulator = "";
        for (LoggedObject obj = nextItems.pollFirst(); obj != null; obj = nextItems.pollFirst()) {
            if (obj.typeid == 14383421) {
                try (PrintWriter out = new PrintWriter(logfile.getName() + "__section_" + sectionNumber + ".csv")) {
                    out.println(text);
                }
                sectionNumber++;
            }
            else if (obj.object != null) {
                if (obj.object instanceof SPLTeamMessagePackage) {
                    ;  // nulla
                } else if (obj.object instanceof GameControlReturnDataPackage) {
                    GameControlReturnDataPackage package = (GameControlReturnDataPackage) obj.object;
                    final GameControlReturnData message = new GameControlReturnData();
                    message.fromByteArray(ByteBuffer.wrap(package.message));
                    if (!(message.headerValid && message.versionValid && message.playerNumValid && message.teamNumValid)) {
                        return;
                    }
                    message.playing = (obj.gameState == STATE_PLAYING);
                    sectionAccumulator += message.toCSVLine();
                } else if (obj.object instanceof GameControlData) {
                    ;  // nulla
                }
            }
        }




        this.close();
    }

    /**
     * Closes the currently opened log file.
     */
    public void close() {
        if (task != null) {
            // Drain package queue of SPLTeamMessageReceiver and GameControlReturnDataReceiver
            SPLTeamMessageReceiverTCM.getInstance().clearPackageQueue();
            GameControlReturnDataReceiverTCM.getInstance().clearPackageQueue();

            // Reset GameState
            GameState.getInstance().reset();

            // Tell the logger that it can log again
            Logger.getInstance().setIsReplaying(false);
        }
    }
}
