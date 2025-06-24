# fairGhosts - Ant colony controlled ghosts for Ms. Pac-Man
This repository contains the source code for the 2016 IEEE publication 'fairGhosts — Ant colony controlled ghosts for Ms. Pac-Man' (https://ieeexplore.ieee.org/document/7744325).

The primary controller is located in ``src/pacman/entries/ghosts/fair/``.

Parameter optimization was performed using a genetic algorithm, found in ``src/pacman/optimizer/ga/``

An experiment in ``src/pacman/optimizer/experiment/`` can run multiple instances of the competition (based on the number of available threads) any number of times. It collects individual scores, aggregates them, and calculates the average to evaluate the effectiveness of different entries.

## How to Run

All execution modes (game, experiment, or genetic algorithm) rely on the same settings. For optimal performance, ensure visuals are disabled when running the experiment or genetic algorithm.

### Running the Game
The game can be started by executing the ``main`` method in ``src/pacman/Executor.java``.

Controllers and modes can be configured within this file. The default settings are:
- **Ghosts:** fairGhosts
- **Ms Pac-Man:** Human interaction (keyboard input)
- Any pheromone traces are displayed visually. To disable, open ``Parameters.java`` and set ``EXPERIMENTAL_MODE`` to ``false``.

### Running the Experiment
The experiment can be initiated by running the ``main`` method in ``src/pacman/Executor_Experiment.java``.

A multi-threaded, improved version is available in ``src/pacman/optimizer/experiment/Executor_ExperimentV2.java``.

### Running the Genetic Algorithm
The genetic algorithm can be started by executing the ``main`` method in ``src/pacman/optimizer/ga/FairGhostsMain.java``. For faster execution, disable visualisation first.

For each evolution, the algorithm outputs out the best solution (chromosomes) to the ``fairGhostsJGAP.properties`` file. These can be set in ``Parameters.java`` to optimize the ant behavior of fairGhosts.


## Background

### Ant Colony Optimization
Ant colony optimization is a metaheuristic algorithm inspired by the foraging behavior of ants. It employs a probabilistic approach to solve optimization problems, particularly for finding the shortest paths. The algorithm uses pheromone trails and local heuristics to guide the search process, enabling efficient exploration and exploitation of the solution space.

See also: [Ant colony optimization](https://ieeexplore.ieee.org/document/4129846) (2016) by Marco Dorigo, Mauro Birattari, and Thomas Stützle.

### Ms. Pac-Man vs Ghosts League
This league allowed participants to develop AI controllers for the classic arcade game Ms. Pac-Man. Competitors could submit Java code for either Ms. Pac-Man or the ghosts. The competition was organized by Philipp Rohlfshagen, David Robles, and Simon Lucas from the University of Essex.

See also: [Ms Pac-Man vs Ghosts League](https://web.archive.org/web/20150405034716/https://www.pacman-vs-ghosts.net/) (Web Archive from  05 April 2015, the last capture before the domain was sold).
