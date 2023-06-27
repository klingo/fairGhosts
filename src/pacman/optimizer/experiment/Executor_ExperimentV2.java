package pacman.optimizer.experiment;

import org.apache.commons.lang3.time.StopWatch;
import pacman.controllers.Controller;
import pacman.entries.ghosts.fair.FairGhosts;
import pacman.game.Game;
import pacman.optimizer.ga.FairGhostsMain;
import pacman.optimizer.ga.FairGhostsWorkerThread;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static pacman.game.Constants.*;

/**
 * This class may be used to execute the game in timed or un-timed modes, with or without visuals.
 * Competitors should implement their controllers in game.entries.ghosts and game.entries.pacman
 * respectively. The skeleton classes are already provided. The package structure should not be
 * changed (although you may create sub-packages in these packages).
 *
 * @author Fabian Schaer
 */
@SuppressWarnings("unused")
public class Executor_ExperimentV2 {

    private final int NUM_TRIALS_EXPERIMENT = 50;
    private final int MAX_SIMULTANEOUS_THREADS = Runtime.getRuntime().availableProcessors();
//    private final int MAX_SIMULTANEOUS_THREADS = 1;

    protected static List<Double> scoreList = new ArrayList<Double>();

    /**
     * The main method. Several options for controllers are listed - simply remove comments to use
     * the option you want.
     *
     * @param args the command line arguments (not used)
     */
    public static void main(String[] args) {
        Executor_ExperimentV2 exec = new Executor_ExperimentV2();

        List<Class<? extends Controller<MOVE>>> pacmanClasses = new ArrayList<Class<? extends Controller<MOVE>>>();

        // r01_eiisolver
//        pacmanClasses.add(pacman.entries.wcci2012.pacman.r01_eiisolver.MyPacMan.class);
        // r04_Memetix
//        pacmanClasses.add(pacman.entries.wcci2012.pacman.r04_Memetix.MyPacMan.class);
        // r05_ghostbuster
//        pacmanClasses.add(pacman.entries.wcci2012.pacman.r05_ghostbuster.MyPacMan.class);
        // r08_arthurspooner
//        pacmanClasses.add(pacman.entries.wcci2012.pacman.r08_arthurspooner.MyPacMan.class);
        // r09_egreavette
//        pacmanClasses.add(pacman.entries.wcci2012.pacman.r09_egreavette.MyPacMan.class);
        // r10_rcpinto
//        pacmanClasses.add(pacman.entries.wcci2012.pacman.r10_rcpinto.MyPacMan.class);
        // r15_stefan
//        pacmanClasses.add(pacman.entries.wcci2012.pacman.r15_stefan.MyPacMan.class);
        // r16_MinimaxRampage
//        pacmanClasses.add(pacman.entries.wcci2012.pacman.r16_MinimaxRampage.MyPacMan.class);
        // r22_Kazzim
//        pacmanClasses.add(pacman.entries.wcci2012.pacman.r22_Kazzim.MyPacMan.class);
        // r34_STARTER_PACMAN
        pacmanClasses.add(pacman.controllers.examples.StarterPacMan.class);

        for (Class<? extends Controller<MOVE>> pacmanClass : pacmanClasses) {

            StopWatch s = new StopWatch();
            s.start();

            System.out.println("==========================================================================================================");
            System.out.println("= " + pacmanClass.getCanonicalName() + " vs. " + FairGhosts.class.getCanonicalName() + " =");
            System.out.println("----------------------------------------------------------------------------------------------------------");
            exec.runCompetitionExperiment(pacmanClass, FairGhosts.class);

            s.stop();
            long msUsed = s.getTime();

            System.out.println("Experiment took: " + (Math.round(msUsed / 1000 / 60)) + " minutes.");
        }

    }

    public void runCompetitionExperiment(Class<? extends Controller<MOVE>> pacmanClass, Class<? extends Controller<EnumMap<GHOST, MOVE>>> ghostClass) {
        double gameScore = 0;
        double avgScore = 0;

        Random rnd = new Random(0);
        Game game;

        ExecutorService executor = Executors.newFixedThreadPool(MAX_SIMULTANEOUS_THREADS);
        for (int i = 0; i < NUM_TRIALS_EXPERIMENT; i++) {
            Runnable worker = new ExperimentWorkerThread(i, pacmanClass, ghostClass);
            executor.execute(worker);
        }
        executor.shutdown();
        while (!executor.isTerminated()) {
        }

        // "free" up the object
        executor = null;

        // System.out.println("Finished all threads");
        double totalScore = 0;
        for (int i = 0; i < scoreList.size(); i++) {
            totalScore += scoreList.get(i);
        }
        scoreList.clear();

        double averageScore = totalScore / NUM_TRIALS_EXPERIMENT;

        System.out.println("average score: " + averageScore);

    }

}