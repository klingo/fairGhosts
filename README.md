# fairGhosts - Ant colony controlled ghosts for Ms. Pac-Man
This repository contains the source code for the 2016 IEEE publication 'fairGhosts — Ant colony controlled ghosts for Ms. Pac-Man' (https://ieeexplore.ieee.org/document/7744325).

The actual controller can be found in ``src/pacman/entries/ghosts/fair/``.

The optimization of the variable parameters was done with a genetic algorithm found in ``src/pacman/optimizer/ga/``

There is also an experiment in ``src/pacman/optimizer/experiment/`` that can run multiple instances (based on number of available threads) of the competitions any number of times. It collects all individual scores, adds them up and calculates the average. This was used to evaluate the effectiveness of the different entries.

## How to run

Please note that all variations of running (the game, the experiment, or the genetic algorithm) always rely on the same settings. Meaning, you need to make sure that for the experiment and the genetic algorithm the "visuals" are disabled for optimal performance.

### How to run the game
The cam can be started by running the ``main method`` in ``src/pacman/Executor.java``.

Different controllers and modes can be configured there as well. The default are:
- **Ghosts:** fairGhosts
- **Ms Pac-Man:** Human interaction (keyboard input)
- Pheromone traces of the ants are visually shown (to disable, open ``Parameters.java`` and change ``EXPERIMENTAL_MODE`` to ``false``)

### How to run the experiment
The built-in experiment can be started by running the ``main method`` in ``src/pacman/Executor_Experiment.java``.

An improved, multi-threaded version can be found in ``src/pacman/optimizer/experiment/Executor_ExperimentV2.java``.

### How to run the genetic algorithm
The genetic algorithm can be started by running the ``main method`` in ``src/pacman/optimizer/ga/FairGhostsMain.java``.
You might want to disable the visualisation first though, to speed up the algorithm.

For every evolution, it prints out the best solution (chromosomes) in the ``fairGhostsJGAP.properties`` file. These can be set in the ``Parameters.java`` file to optimize the ant behavior of fairGhosts.


## Background

### Ant Colony Optimization
Ant colony optimization is a metaheuristic algorithm inspired by the foraging behavior of ants. It uses a probabilistic approach to solve optimization problems, particularly in finding the shortest paths. The algorithm relies on pheromone trails and local heuristics to guide the search process, allowing for efficient exploration and exploitation of the solution space.

See also: [Ant colony optimization](https://ieeexplore.ieee.org/document/4129846) (2016) by Marco Dorigo, Mauro Birattari, and Thomas Stutzle.

### Ms. Pac-Man vs Ghosts League
This league allowed parties to develop AI controllers for the classical arcade game Ms Pac-Man. You could submit Java code for either Ms Pac-Man or for the ghosts! The competition was brought to you by Philipp Rohlfshagen, David Robles and Simon Lucas from the University of Essex.

See also: [Ms Pac-Man vs Ghosts League](https://web.archive.org/web/20150405034716/https://www.pacman-vs-ghosts.net/) (Web Archive from  05 April 2015, last snaphsot before the domain was sold).