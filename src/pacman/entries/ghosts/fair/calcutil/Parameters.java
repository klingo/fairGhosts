package pacman.entries.ghosts.fair.calcutil;


/**
 * The Parameters define the given variables needed for the ACO algorithm.
 *  
 * @author Iris Hunkeler
 */
public interface Parameters {
	
	public final static boolean EXPERIMENTAL_MODE = true;
		
	/** Minimum Pheromone Level f�r Edges. Wert zuf�llig gew�hlt */
	public final static double MINIMUM_PHEROMONE_LEVEL = 1;
	
	
	
	/* HUNTER ANTS */

	/** Maximum Number of HunterAnts created (maxAnt ^h) */
	public final static int MAX_HUNTER = 15; // original: 7
	
	/** Maximum distance a HunterAnt is allowed to travel before stopping   (maxDist ^h) */
	public final static int MAX_DISTANCE_PER_HUNTER= 98;

	/** (q0 ^h) */
//	public final static double EXPLORATION_RATE_HUNTER = 0.16;

	/** (p ^h) */
//	public final static double EVAPORATION_RATE_HUNTER = 0.3;

	/** (alpha ^h) */
	public final static double UPDATING_RATE_HUNTER = 0.61;
	
	
	
	
	/* EXPLORER ANTS */
	
	/**  Maximum Number of ExplorerAnts created (maxAnt ^e) */
	public final static int MAX_EXPLORER = 13;
	
	/** Maximum distance a ExplorerAnt is allowed to travel before stopping (maxDist ^e) */
	public final static int MAX_DISTANCE_PER_EXPLORER = 36; 

	/** (q0 ^e) */
	public final static double EXPLORATION_RATE_EXPLORER = 0.73;

	/** (p ^e) */
//	public final static double EVAPORATION_RATE_EXPLORER = 0.87;

	/** (alpha ^e) */
//	public final static double UPDATING_RATE_EXPLORER = 0.84;
	
}
