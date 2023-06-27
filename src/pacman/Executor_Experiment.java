package pacman;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.time.StopWatch;

import pacman.controllers.Controller;
import pacman.controllers.HumanController;
import pacman.game.Game;
import pacman.game.GameView;
import pacman.game.GameViewExt;
import static pacman.game.Constants.*;

/**
 * This class may be used to execute the game in timed or un-timed modes, with or without visuals.
 * Competitors should implement their controllers in game.entries.ghosts and game.entries.pacman
 * respectively. The skeleton classes are already provided. The package structure should not be
 * changed (although you may create sub-packages in these packages).
 * 
 * @author Fabian Schaer
 * 
 */
@SuppressWarnings("unused")
public class Executor_Experiment {

	/**
	 * Defines the scaling factor for the GameViewExt (default = 1)
	 */
	private final int scaleFactor = 2;

	/**
	 * The main method. Several options for controllers are listed - simply remove comments to use
	 * the option you want.
	 *
	 * @param args
	 *            the command line arguments (not used)
	 */
	public static void main(String[] args) {
		Executor_Experiment exec = new Executor_Experiment();

		// run multiple games in batch mode - good for testing.
		int numTrials = 50;

		List<Class<? extends Controller<MOVE>>> pacmanClasses = new ArrayList<Class<? extends Controller<MOVE>>>();
		List<Class<? extends Controller<EnumMap<GHOST, MOVE>>>> ghostClasses = new ArrayList<Class<? extends Controller<EnumMap<GHOST, MOVE>>>>();

		// r01_eiisolver
		//pacmanClasses.add(pacman.entries.wcci2012.pacman.r01_eiisolver.MyPacMan.class);
		// r02_maastricht
		//pacmanClasses.add(pacman.entries.wcci2012.pacman.r02_maastricht.MyPacMan.class);
		// r04_Memetix
		//pacmanClasses.add(pacman.entries.wcci2012.pacman.r04_Memetix.MyPacMan.class);
		// r05_ghostbuster
		//pacmanClasses.add(pacman.entries.wcci2012.pacman.r05_ghostbuster.MyPacMan.class);
		// r09_egreavette
		//pacmanClasses.add(pacman.entries.wcci2012.pacman.r09_egreavette.MyPacMan.class);
		// r10_rcpinto
		//pacmanClasses.add(pacman.entries.wcci2012.pacman.r10_rcpinto.MyPacMan.class);
		// // r11_GreenTea
		// // pacmanClasses.add(pacman.entries.wcci2012.pacman.r11_GreenTea.MyPacMan.class);
		// r15_stefan
		//pacmanClasses.add(pacman.entries.wcci2012.pacman.r15_stefan.MyPacMan.class);
		// r16_MinimaxRampage
		//pacmanClasses.add(pacman.entries.wcci2012.pacman.r16_MinimaxRampage.MyPacMan.class);
		// r22_Kazzim
		//pacmanClasses.add(pacman.entries.wcci2012.pacman.r22_Kazzim.MyPacMan.class);
		// r34_STARTER_PACMAN
		pacmanClasses.add(pacman.controllers.examples.StarterPacMan.class);

		// FairGhosts
		ghostClasses.add(pacman.entries.ghosts.fair.FairGhosts.class);
		// r01_eiisolver
		/*ghostClasses.add(pacman.entries.wcci2012.ghosts.r01_eiisolver.MyGhosts.class);
		// r02_GreenTea
		ghostClasses.add(pacman.entries.wcci2012.ghosts.r02_GreenTea.MyGhosts.class);
		// r03_Memetix
		ghostClasses.add(pacman.entries.wcci2012.ghosts.r03_Memetix.MyGhosts.class);
		// r08_rcpinto
		ghostClasses.add(pacman.entries.wcci2012.ghosts.r08_rcpinto.MyGhosts.class);
		// r09_stefan
		ghostClasses.add(pacman.entries.wcci2012.ghosts.r09_stefan.MyGhosts.class);
		// r11_egreavette
		ghostClasses.add(pacman.entries.wcci2012.ghosts.r11_egreavette.MyGhosts.class);
		// r19_LEGACY_2_GHOSTS
		ghostClasses.add(pacman.controllers.examples.Legacy2TheReckoning.class);
		// r24_FC99
		ghostClasses.add(pacman.entries.wcci2012.ghosts.r24_FC99.MyGhosts.class);
*/
		for (Class<? extends Controller<MOVE>> pacmanClass : pacmanClasses) {
			for (Class<? extends Controller<EnumMap<GHOST, MOVE>>> ghostClass : ghostClasses) {
				StopWatch s = new StopWatch();
				s.start();

				System.out.println("==========================================================================================================");
				System.out.println("= " + pacmanClass.getCanonicalName() + " vs. " + ghostClass.getCanonicalName() + " =");
				System.out.println("----------------------------------------------------------------------------------------------------------");
				exec.runCompetitionExperiment(pacmanClass, ghostClass, numTrials);

				s.stop();
				long msUsed = s.getTime();

				System.out.println("Experiment took: " + (Math.round(msUsed / 1000 / 60)) + " minutes.");
			}

		}

	}

	public void runCompetitionExperiment(Class<? extends Controller<MOVE>> pacmanClass, Class<? extends Controller<EnumMap<GHOST, MOVE>>> ghostClass, int trials) {
		double gameScore = 0;
		double avgScore = 0;

		Random rnd = new Random(0);
		Game game;

		for (int i = 0; i < trials; i++) {
			try {
				gameScore = runGameTimedSpeedOptimisedV2(pacmanClass.newInstance(), ghostClass.newInstance());
				avgScore += gameScore;
				System.out.println(i + "\t" + gameScore);
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}

		}
		System.out.println(avgScore / trials);
	}



	/**
	 * Run the game in asynchronous mode but proceed as soon as both controllers replied. The time
	 * limit still applies so so the game will proceed after 40ms regardless of whether the
	 * controllers managed to calculate a turn. Compared to its original version, this one returns
	 * the score of the current game at the end and makes no use of any GameView at all, to further
	 * speed up the process of experimentation.
	 * 
	 * @param pacManController
	 *            The Pac-Man controller
	 * @param ghostController
	 *            The Ghosts controller
	 * @return score the final score of the game
	 */
	public double runGameTimedSpeedOptimisedV2(Controller<MOVE> pacManController, Controller<EnumMap<GHOST, MOVE>> ghostController) {
		Game game = new Game(0);

		GameView gv=null;

		gv=new GameViewExt(game, scaleFactor).showGame();

		new Thread(pacManController).start();
		new Thread(ghostController).start();

		while (!game.gameOver()) {
			pacManController.update(game.copy(), System.currentTimeMillis() + DELAY);
			ghostController.update(game.copy(), System.currentTimeMillis() + DELAY);

			try {
				int waited = DELAY / INTERVAL_WAIT;

				for (int j = 0; j < DELAY / INTERVAL_WAIT; j++) {
					Thread.sleep(INTERVAL_WAIT);

					if (pacManController.hasComputed() && ghostController.hasComputed()) {
						waited = j;
						break;
					}
				}

				game.advanceGame(pacManController.getMove(), ghostController.getMove());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			gv.repaint();
		}

		pacManController.terminate();
		ghostController.terminate();

		gv.removeAll();
		gv.setVisible(false);
		gv = null;

		return game.getScore();
	}

}