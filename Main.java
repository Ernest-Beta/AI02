import java.util.List;

public class Main {
    public static void main(String[] args) {
        

        try {
            System.setProperty("file.encoding", "UTF-8");

            //parsing lessons and teachers from files
            List<Lesson> lessons = Lesson.parseLessonsFile("lessons.txt");
            List<Teacher> teachers = Teacher.parseTeachersFile("teachers.txt");

            //intializing genetic algorithm parameters
            int populationSize = 500;
            double mutationProbability = 0.05;
            int maxGenerations = 10000000;
            double fitnessThreshold = 0.95;

            //creating the GeneticAlgorithm object
            GeneticAlgorithm algorithm = new GeneticAlgorithm(populationSize, mutationProbability, maxGenerations, fitnessThreshold);

            //inti the population
            algorithm.initializePopulation(lessons, teachers);

            //run the genetic algorithm and get the best solution
            Chromosome bestSolution = algorithm.run(lessons, teachers);

            System.out.println("\nBest Timetable Found:");
            
            //export the timetable to a file
            Timetable timetable = new Timetable();
            for (int section = 0; section < 9; section++) {
                for (int day = 0; day < 5; day++) {
                    for (int hour = 0; hour < 7; hour++) {
                        TimetableSlot slot = bestSolution.getSlot(section, day, hour);
                        if (slot != null) {
                            timetable.setSlot(section, day, hour, slot.getLesson(), slot.getTeacher());
                        }
                    }
                }
            }

            timetable.exportTimetable("final.txt");
            System.out.println("Timetable exported to final.txt");
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }


}
