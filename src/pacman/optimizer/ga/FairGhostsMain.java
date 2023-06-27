package pacman.optimizer.ga;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Properties;

import org.jgap.Chromosome;
import org.jgap.Configuration;
import org.jgap.FitnessFunction;
import org.jgap.Gene;
import org.jgap.Genotype;
import org.jgap.IChromosome;
import org.jgap.data.DataTreeBuilder;
import org.jgap.data.IDataCreators;
import org.jgap.impl.DefaultConfiguration;
import org.jgap.impl.DoubleGene;
import org.jgap.impl.IntegerGene;
import org.jgap.xml.XMLDocumentBuilder;
import org.jgap.xml.XMLManager;
import org.w3c.dom.Document;

/**
 * The FairGhostsMain is the main class to be executed that will initiate the whole genetic
 * algorithm to find better solutions for the selection of parameters
 * 
 * @author Fabian Schaer
 *
 */
public class FairGhostsMain {

	/**
	 * The total number of times we'll let the population evolve.
	 */
	private static final int MAX_ALLOWED_EVOLUTIONS = 50;

	/**
	 * How many chromosomes we want in our population. Bigger = larger number of potential solutions,
	 * but it takes significantly longer
	 */
	private static final int POPULATION_SIZE = 50;

	/**
	 * How often a single experiment (ms pacman simulation) shall be executed to evaluate the
	 * average score of the current parameters for the fairGhosts
	 */
	protected static final int NUM_TRIALS_EXPERIMENT = 20;

	/**
	 * To count in which evolution we currently are in
	 */
	protected static int evolutionCounter = 0;

	public static void main(String[] args) throws Exception {

		System.out.println("INFO: This optimizer will run with " + Runtime.getRuntime().availableProcessors() + " parallel Threads!");

		// Start with a DefaultConfiguration, which comes setup with the
		// most common settings.
		// -------------------------------------------------------------
		Configuration conf = new DefaultConfiguration();
		conf.setPreservFittestIndividual(true);
		// Set the fitness function we want to use. We construct it with
		// the target volume passed in to this method.
		// ---------------------------------------------------------
		FitnessFunction myFunc = new FairGhostsFitnessFunction();
		conf.setFitnessFunction(myFunc);
		// Now we need to tell the Configuration object how we want our
		// Chromosomes to be setup. We do that by actually creating a
		// sample Chromosome and then setting it on the Configuration
		// object. As mentioned earlier, we want our Chromosomes to each
		// have as many genes as there are different items available. We want
		// the
		// values (alleles) of those genes to be integers, which represent
		// how many items of that type we have. We therefore use the
		// IntegerGene class to represent each of the genes. That class
		// also lets us specify a lower and upper bound, which we set
		// to senseful values (i.e. maximum possible) for each item type.
		// --------------------------------------------------------------
		Gene[] sampleGenes = new Gene[7]; // 7, since we have 7 parameters to experiment with
		sampleGenes[0] = new DoubleGene(conf, 0d, 2d); // MINIMUM_PHEROMONE_LEVEL
		sampleGenes[1] = new IntegerGene(conf, 5, 40); // MAX_HUNTER
		sampleGenes[2] = new IntegerGene(conf, 50, 200); // MAX_DISTANCE_PER_HUNTER
		sampleGenes[3] = new DoubleGene(conf, 0d, 1d); // UPDATING_RATE_HUNTER
		sampleGenes[4] = new IntegerGene(conf, 5, 30); // MAX_EXPLORER
		sampleGenes[5] = new IntegerGene(conf, 10, 100); // MAX_DISTANCE_PER_EXPLORER
		sampleGenes[6] = new DoubleGene(conf, 0d, 1d); // EXPLORATION_RATE_EXPLORER

		IChromosome sampleChromosome = new Chromosome(conf, sampleGenes);
		conf.setSampleChromosome(sampleChromosome);
		// Finally, we need to tell the Configuration object how many
		// Chromosomes we want in our population. The more Chromosomes,
		// the larger number of potential solutions (which is good for
		// finding the answer), but the longer it will take to evolve
		// the population (which could be seen as bad).
		// ------------------------------------------------------------
		conf.setPopulationSize(POPULATION_SIZE);
		// Create random initial population of Chromosomes.
		// Here we try to read in a previous run via XMLManager.readFile(..)
		// for demonstration purpose!
		// -----------------------------------------------------------------
		Genotype population;
		try {
			Document doc = XMLManager.readFile(new File("fairGhostsJGAP.xml"));
			population = XMLManager.getGenotypeFromDocument(conf, doc);
		} catch (FileNotFoundException fex) {
			population = Genotype.randomInitialGenotype(conf);
		}
		population = Genotype.randomInitialGenotype(conf);
		// Evolve the population. Since we don't know what the best answer
		// is going to be, we just evolve the max number of times.
		// ---------------------------------------------------------------
		for (int i = 0; i < MAX_ALLOWED_EVOLUTIONS; i++) {
			saveAndPrint(population);
			evolutionCounter++;
			population.evolve(1);
		}

		// Display the best solution we found.
		// -----------------------------------
		IChromosome bestSolutionSoFar = population.getFittestChromosome();

		Properties props = new Properties();
		// Pheromones
		props.setProperty("MINIMUM_PHEROMONE_LEVEL", Double.toString(((Double) bestSolutionSoFar.getGene(0).getAllele()).doubleValue()));
		// Hunter Ants
		props.setProperty("MAX_HUNTER", Integer.toString(((Integer) bestSolutionSoFar.getGene(1).getAllele()).intValue()));
		props.setProperty("MAX_DISTANCE_PER_HUNTER", Integer.toString(((Integer) bestSolutionSoFar.getGene(2).getAllele()).intValue()));
		props.setProperty("UPDATING_RATE_HUNTER", Double.toString(((Double) bestSolutionSoFar.getGene(3).getAllele()).doubleValue()));
		// Explorer Ants
		props.setProperty("MAX_EXPLORER", Integer.toString(((Integer) bestSolutionSoFar.getGene(4).getAllele()).intValue()));
		props.setProperty("MAX_DISTANCE_PER_EXPLORER", Integer.toString(((Integer) bestSolutionSoFar.getGene(5).getAllele()).intValue()));
		props.setProperty("EXPLORATION_RATE_EXPLORER", Double.toString(((Double) bestSolutionSoFar.getGene(6).getAllele()).doubleValue()));
		// Score
		props.setProperty("AVG_SCORE", Double.toString(100000 - bestSolutionSoFar.getFitnessValue()));

		File f = new File("fairGhostsJGAP.properties");
		OutputStream out = new FileOutputStream(f);
		props.store(out, "This is an optional header comment string");
		out.close();
	}

	private static void saveAndPrint(Genotype population) throws Exception {
		// Save progress to file. A new run of this example will then be able to
		// resume where it stopped before!
		// ---------------------------------------------------------------------

		// represent Genotype as tree with elements Chromomes and Genes
		// ------------------------------------------------------------
		DataTreeBuilder builder = DataTreeBuilder.getInstance();
		IDataCreators doc2 = builder.representGenotypeAsDocument(population);
		// create XML document from generated tree
		// ---------------------------------------
		XMLDocumentBuilder docbuilder = new XMLDocumentBuilder();
		Document xmlDoc = (Document) docbuilder.buildDocument(doc2);
		XMLManager.writeFile(xmlDoc, new File("fairGhostsJGAP.xml"));
		// Display the best solution we found.
		// -----------------------------------
		IChromosome bestSolutionSoFar = population.getFittestChromosome();
		System.out.println("Evolution (" + FairGhostsMain.evolutionCounter + ") completed!");
		System.out.println("---------------------------------------------------------------------------------");
		System.out.println("The best solution so far has a fitness value of " + bestSolutionSoFar.getFitnessValue());
		System.out.println("This means a best average score of " + (100000 - bestSolutionSoFar.getFitnessValue()));
		System.out.println("MINIMUM_PHEROMONE_LEVEL : " + ((Double) bestSolutionSoFar.getGene(0).getAllele()).doubleValue());
		System.out.println("MAX_HUNTER : " + ((Integer) bestSolutionSoFar.getGene(1).getAllele()).intValue());
		System.out.println("MAX_DISTANCE_PER_HUNTER : " + ((Integer) bestSolutionSoFar.getGene(2).getAllele()).intValue());
		System.out.println("UPDATING_RATE_HUNTER : " + ((Double) bestSolutionSoFar.getGene(3).getAllele()).doubleValue());
		System.out.println("MAX_EXPLORER : " + ((Integer) bestSolutionSoFar.getGene(4).getAllele()).intValue());
		System.out.println("MAX_DISTANCE_PER_EXPLORER : " + ((Integer) bestSolutionSoFar.getGene(5).getAllele()).intValue());
		System.out.println("EXPLORATION_RATE_EXPLORER : " + ((Double) bestSolutionSoFar.getGene(6).getAllele()).doubleValue());
		System.out.println("=================================================================================");
	}

}
