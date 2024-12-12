import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GeneticAlgorithm {
    private List<Chromosome> population;
    private int populationSize;
    private double mutationProbability;
    private int maxGenerations;
    private double fitnessThreshold;

    public GeneticAlgorithm(int populationSize, double mutationProbability, int maxGenerations, double fitnessThreshold) {
        this.populationSize = populationSize;
        this.mutationProbability = mutationProbability;
        this.maxGenerations = maxGenerations;
        this.fitnessThreshold = fitnessThreshold;
        this.population = new ArrayList<>();
    }

    // innit the population with valid Chromosomes
    public void initializePopulation(List<Lesson> lessons, List<Teacher> teachers) {
        for (int i = 0; i < populationSize; i++) {
            Chromosome chromosome = new Chromosome(lessons, teachers);
            chromosome.calculateFitness(); 
            population.add(chromosome);
        }
        
        population.sort((a, b) -> Double.compare(b.getFitness(), a.getFitness()));
    }

    // Run 
    public Chromosome run(List<Lesson> lessons, List<Teacher> teachers) {
        for (int generation = 0; generation < maxGenerations; generation++) {
            //check if the best chromosome meets the fitness threshold
            if (population.get(0).getFitness() >= fitnessThreshold) {
                return population.get(0);
            }

            List<Chromosome> newPopulation = new ArrayList<>();
            Random random = new Random();

            //generate the next generation
            while (newPopulation.size() < populationSize) {
                // Select parents from the top half of the population
                Chromosome parent1 = population.get(random.nextInt((populationSize / 2)));    
                Chromosome parent2 = population.get(random.nextInt((populationSize / 2)));

                // Perform crossover and produce offspring
                Chromosome[] offspring = parent1.crossover(parent2, lessons, teachers);
                offspring[0].calculateFitness(); 
                offspring[1].calculateFitness(); 

                //mutation with the given probability
                if (random.nextDouble() < mutationProbability) {
                    mutateWithValidation(offspring[0], lessons, teachers);
                    mutateWithValidation(offspring[1], lessons, teachers);
                }

                //offspring to the new population
                newPopulation.add(offspring[0]);
                newPopulation.add(offspring[1]);
            }

            //replace the old population with the new one and sort by fitness
            population = new ArrayList<>(newPopulation);
            population.sort((a, b) -> Double.compare(b.getFitness(), a.getFitness()));
        }

        // best chromosome found
        return population.get(0);
    }

   
    private void mutateWithValidation(Chromosome chromosome, List<Lesson> lessons, List<Teacher> teachers) {
        Random random = new Random();
        int section = random.nextInt(9); //sections from 0 to 8 (A1 to C3)
        int slot = random.nextInt(5 * 7); //total time slots (5 days * 7 hours)

        String sectionName = chromosome.getSectionName(section);
        List<Lesson> validLessons = chromosome.getValidLessonsForSection(lessons, sectionName);

        Lesson randomLesson;
        Teacher randomTeacher;
        boolean isValid;

        //teacher is qualified to teach the chosen lesson
        do {
            randomLesson = validLessons.get(random.nextInt(validLessons.size()));
            randomTeacher = teachers.get(random.nextInt(teachers.size()));
            isValid = randomTeacher.canTeach(randomLesson.getSubjectID());
        } while (!isValid);

        //update the timetable slot with a valid lesson and teacher
        chromosome.getTimetable()[section][slot] = new TimetableSlot(randomLesson, randomTeacher, sectionName, slot / 7, slot % 7);
        chromosome.calculateFitness();
    }

}
