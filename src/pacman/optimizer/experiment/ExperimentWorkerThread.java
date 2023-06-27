package pacman.optimizer.experiment;

import pacman.Executor_Experiment;
import pacman.controllers.Controller;
import pacman.entries.ghosts.fair.FairGhosts;
import pacman.game.Constants;
import pacman.game.Game;
import pacman.optimizer.ga.FairGhostsFitnessFunction;

import java.util.EnumMap;

import static pacman.game.Constants.DELAY;
import static pacman.game.Constants.INTERVAL_WAIT;

/**
 * The FairGhostsWorkerThread is Runnable class that will be executed parallel in several threads to
 * run the actual experiment
 *
 * @author Fabian Schaer
 *
 */
public class ExperimentWorkerThread implements Runnable {

	@SuppressWarnings("unused")
	private int trialNumber;
	private Class<? extends Controller<Constants.MOVE>> pacmanClass;
	private Class<? extends Controller<EnumMap<Constants.GHOST, Constants.MOVE>>> ghostClass;

	ExperimentWorkerThread(int trialNumber, Class<? extends Controller<Constants.MOVE>> pacmanClass, Class<? extends Controller<EnumMap<Constants.GHOST, Constants.MOVE>>> ghostClass) {
		this.trialNumber = trialNumber;
		this.pacmanClass = pacmanClass;
		this.ghostClass = ghostClass;
	}

	@Override
	public void run() {
		// System.out.println(Thread.currentThread().getName() + " Start. Trial = " + trialNumber);

		try {
			double trialScore = runGameTimedSpeedOptimisedV2(this.pacmanClass.newInstance(), this.ghostClass.newInstance());

			synchronized (this) {
				Executor_ExperimentV2.scoreList.add(trialScore);
                System.out.println("Trial " + this.trialNumber + " achieved an average score of: " + trialScore);
            }
		} catch (Exception e) {
			// something went wrong... just try it again!
			System.out.println("An exception occured in a ExperimentWorkerThread, re-starting it!");
			run();
		} finally {
			// "free" up the object
//			experiment = null;
		}

		// System.out.println(Thread.currentThread().getName() + " End.");
	}

	/**
	 * Run the game in asynchronous mode but proceed as soon as both controllers replied. The time
	 * limit still applies so so the game will proceed after 40ms regardless of whether the
	 * controllers managed to calculate a turn. Compared to its original version, this one returns
	 * the score of the current game at the end and makes no use of any GameView at all, to further
	 * speed up the process of experimentation.
	 *
	 * @param pacManController The Pac-Man controller
	 * @param ghostController  The Ghosts controller
	 * @return score the final score of the game
	 */
	public double runGameTimedSpeedOptimisedV2(Controller<Constants.MOVE> pacManController, Controller<EnumMap<Constants.GHOST, Constants.MOVE>> ghostController) {
		Game game = new Game(0);

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
		}

		pacManController.terminate();
		ghostController.terminate();

		return game.getScore();
	}
}
