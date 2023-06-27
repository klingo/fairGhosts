package pacman.optimizer.ga;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jgap.FitnessFunction;
import org.jgap.IChromosome;

import pacman.entries.ghosts.fair.calcutil.Parameters;

/**
 * The FairGhostsFitnessFunction calculates the fitness value for the genetic algorithm by averaging
 * all scores form several trials with the same parameters
 * 
 * @author Fabian Schaer
 *
 */
public class FairGhostsFitnessFunction extends FitnessFunction {

	private static final long serialVersionUID = 1L;

	private static int chromosomeCounter = 0;

	protected static List<Double> scoreList = new ArrayList<Double>();

	@Override
	protected double evaluate(IChromosome aChromosome) {

		double minPheromoneLevel = ((Double) aChromosome.getGene(0).getAllele()).doubleValue();
		int maxHunterAnts = ((Integer) aChromosome.getGene(1).getAllele()).intValue();
		int maxDistancePerHunterAnt = ((Integer) aChromosome.getGene(2).getAllele()).intValue();
		double updatingRateHunterAnt = ((Double) aChromosome.getGene(3).getAllele()).doubleValue();
		int maxExplorerAnts = ((Integer) aChromosome.getGene(4).getAllele()).intValue();
		int maxDistancePerExplorerAnt = ((Integer) aChromosome.getGene(5).getAllele()).intValue();
		double explorationRateExplorerAnt = ((Double) aChromosome.getGene(6).getAllele()).doubleValue();

		/**
		 * IMPORTANT: This works only if the "Parameters" interface is changed to a regular class
		 * (public class Parameters) and all 'final' modifiers have to be removed from the
		 * constants! In addition, also don't forget to set Parameters.EXPERIMENTAL_MODE to 'true'
		 * to avoid unnecessary drawings.
		 */
//		Parameters.MINIMUM_PHEROMONE_LEVEL = minPheromoneLevel;
//		Parameters.MAX_HUNTER = maxHunterAnts;
//		Parameters.MAX_DISTANCE_PER_HUNTER = maxDistancePerHunterAnt;
//		Parameters.UPDATING_RATE_HUNTER = updatingRateHunterAnt;
//		Parameters.MAX_EXPLORER = maxExplorerAnts;
//		Parameters.MAX_DISTANCE_PER_EXPLORER = maxDistancePerExplorerAnt;
//		Parameters.EXPLORATION_RATE_EXPLORER = explorationRateExplorerAnt;

		println("Start evaluation of chromosome " + chromosomeCounter + "!");
		double fitnessValue = runExperiment();

		chromosomeCounter++;

		return fitnessValue;
	}

	private static double runExperiment() {

		ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		for (int i = 0; i < FairGhostsMain.NUM_TRIALS_EXPERIMENT; i++) {
			Runnable worker = new FairGhostsWorkerThread(i);
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

		double averageScore = totalScore / FairGhostsMain.NUM_TRIALS_EXPERIMENT;
		println("Chromosome " + chromosomeCounter + " achieved an average score of " + averageScore + "!");
		// since the fitness value has to be as big as possible, but the averageScore as small as
		// possible, subtract it from 100k
		return 100000 - averageScore;
	}

	protected static void println(String text) {
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
		String formattedDate = sdf.format(date);
		System.out.println(formattedDate + " : " + text);
		// "free" up the object
		date = null;
	}
}
