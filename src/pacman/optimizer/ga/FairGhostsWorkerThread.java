package pacman.optimizer.ga;

import pacman.Executor_Experiment;
import pacman.entries.ghosts.fair.FairGhosts;
import pacman.entries.wcci2012.pacman.r05_ghostbuster.MyPacMan;

/**
 * The FairGhostsWorkerThread is Runnable class that will be executed parallel in several threads to
 * run the actual experiment
 * 
 * @author Fabian Schaer
 *
 */
public class FairGhostsWorkerThread implements Runnable {

	@SuppressWarnings("unused")
	private int trialNumber;

	FairGhostsWorkerThread(int trialNumber) {
		this.trialNumber = trialNumber;
	}

	@Override
	public void run() {
		// System.out.println(Thread.currentThread().getName() + " Start. Trial = " + trialNumber);

		Executor_Experiment experiment = new Executor_Experiment();
		try {
			double trialScore = experiment.runGameTimedSpeedOptimisedV2(new MyPacMan(), new FairGhosts());

			synchronized (this) {
				FairGhostsFitnessFunction.scoreList.add(trialScore);
			}
		} catch (Exception e) {
			// something went wrong... just try it again!
			FairGhostsFitnessFunction.println("An exception occured in a FairGhostsWorkerThread, re-starting it!");
			run();
		} finally {
			// "free" up the object
			experiment = null;
		}

		// System.out.println(Thread.currentThread().getName() + " End.");
	}
}
