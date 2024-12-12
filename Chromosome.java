import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class Chromosome implements Comparable<Chromosome> {
    private TimetableSlot[][] timetable;
    private double fitness;
    private static final int SECTIONS = 9;
    private static final int DAYS = 5;
    private static final int HOURS = 7;
    private static final int TIME_SLOTS = DAYS * HOURS;

   
    public Chromosome(List<Lesson> lessons, List<Teacher> teachers) {
        this.timetable = new TimetableSlot[SECTIONS][TIME_SLOTS];
        randomInitialize(lessons, teachers);
        calculateFitness();
    }

    //  method to calculate total weekly hours for a section
    private int calculateTotalWeeklyHours(List<Lesson> lessons, String sectionName) {
        int totalHours = 0;
        List<Lesson> validLessons = getValidLessonsForSection(lessons, sectionName);
        for (Lesson lesson : validLessons) {
            int[] weeklyHours = lesson.getWeeklyHours();
            int index = getClassLevelIndex(sectionName);
            totalHours += weeklyHours[index];
        }
        return totalHours;
    }
    //method to get a TimetableSlot from the timetable based on section, day, and hour
    public TimetableSlot getSlot(int section, int day, int hour) {
        int index = day * HOURS + hour; // get the index based on day and hour
        return timetable[section][index];
    }


    //helper method to get the class level index (A = 0, B = 1, C = 2)
    private int getClassLevelIndex(String sectionName) {
        char classLevel = sectionName.charAt(0); // 'A', 'B', or 'C'
        switch (classLevel) {
            case 'A':
                return 0;
            case 'B':
                return 1;
            case 'C':
                return 2;
            default:
                throw new IllegalArgumentException("Invalid section name: " + sectionName);
        }
    }
    public List<Lesson> getValidLessonsForSection(List<Lesson> lessons, String sectionName) {
        //determine the class level from the section name (A, B, or C)
        char classLevel = sectionName.charAt(0); // A, B, or C
        int classLevelIndex;
    
        //determine the index based on the class level (A = 0, B = 1, C = 2)
        if (classLevel == 'A') {
            classLevelIndex = 0;
        } else if (classLevel == 'B') {
            classLevelIndex = 1;
        } else if (classLevel == 'C') {
            classLevelIndex = 2;
        } else {
            throw new IllegalArgumentException("Invalid section name: " + sectionName);
        }
    
        List<Lesson> validLessons = new ArrayList<>();
    
        //filter lessons based on whether they are taught in the current class level
        for (Lesson lesson : lessons) {
            int[] weeklyHours = lesson.getWeeklyHours();
    
            //check if the lesson is taught in this class level (hours > 0)
            if (classLevelIndex < weeklyHours.length && weeklyHours[classLevelIndex] > 0) {
                validLessons.add(lesson);
            }
        }
    
        return validLessons;
    }
    

    //helper method to distribute total weekly hours across days
    private int[] distributeHours(int totalHours) {
        int[] dailyHours = new int[DAYS];
        int remainingHours = totalHours;
    
        // Create a list of days and shuffle it to randomize the order
        List<Integer> days = new ArrayList<>();
        for (int day = 0; day < DAYS; day++) {
            days.add(day);
        }
    
        // Shuffle the days to randomize distribution
        while (remainingHours > 0) {
            Collections.shuffle(days);
            for (int day : days) {
                if (dailyHours[day] < HOURS && remainingHours > 0) {
                    dailyHours[day]++;
                    remainingHours--;
                }
            }
        }
    
        return dailyHours;
    }
    

    
    // Randomly initialize the timetable with lessons and qualified teachers
    private void randomInitialize(List<Lesson> lessons, List<Teacher> teachers) {
        Random random = new Random();
    
        for (int section = 0; section < SECTIONS; section++) {
            String sectionName = getSectionName(section);
    
            
            List<Lesson> validLessons = getValidLessonsForSection(lessons, sectionName);
    
            //calculate total weekly hours for the section
            int totalHours = calculateTotalWeeklyHours(lessons, sectionName);
    
            //distribute hours across the days
            int[] dailyHours = distributeHours(totalHours);
    
            //map each lesson to the number of hours it needs to be scheduled in this section
            Map<Lesson, Integer> lessonRemainingHours = new HashMap<>();
            int classLevelIndex = getClassLevelIndex(sectionName);
    
            for (Lesson lesson : validLessons) {
                int[] weeklyHours = lesson.getWeeklyHours();
                int requiredHours = weeklyHours[classLevelIndex];
                lessonRemainingHours.put(lesson, requiredHours);
            }
    
            //create a list of lessons to schedule, each repeated as per its required hours
            List<Lesson> lessonsToSchedule = new ArrayList<>();
            //adds lesson multiple times based on its required hours
            for (Map.Entry<Lesson, Integer> entry : lessonRemainingHours.entrySet()) {
                Lesson lesson = entry.getKey();
                int hours = entry.getValue();
                for (int i = 0; i < hours; i++) {
                    lessonsToSchedule.add(lesson);
                }
            }
    
            //Shuffle the lessons to randomize their order
            Collections.shuffle(lessonsToSchedule, random);
    
            int lessonIndex = 0;
    
            //for each day
            for (int day = 0; day < DAYS; day++) {
                int hoursForDay = dailyHours[day];
    
                for (int hour = 0; hour < hoursForDay; hour++) {
                    if (lessonIndex >= lessonsToSchedule.size()) {
                        break; //No more lessons to schedule
                    }
                    //retrieves the next lesson to be scheduled
                    Lesson lesson = lessonsToSchedule.get(lessonIndex++);
                    int slot = day * HOURS + hour;
    
                    //find all teachers who can teach this lesson as we have multiple 
                    List<Teacher> qualifiedTeachers = new ArrayList<>();
                    for (Teacher teacher : teachers) {
                        if (teacher.canTeach(lesson.getSubjectID())) {
                            qualifiedTeachers.add(teacher);
                        }
                    }
    
                    if (qualifiedTeachers.isEmpty()) {
                        System.err.println("Warning: No teacher available to teach " + lesson.getName() + " in section " + sectionName);
                        continue; 
                    }
    
                    //randomly select a teacher from the qualified ones
                    Teacher randomTeacher = qualifiedTeachers.get(random.nextInt(qualifiedTeachers.size()));
    
                    timetable[section][slot] = new TimetableSlot(lesson, randomTeacher, sectionName, day, hour);
    
                    //decrement the remaining hours for the lesson
                    int remainingHours = lessonRemainingHours.get(lesson) - 1;
                    if (remainingHours == 0) {
                        lessonRemainingHours.remove(lesson);
                    } else {
                        lessonRemainingHours.put(lesson, remainingHours);
                    }
                }
    
                //fill remaining slots with null (representing free periods)
                for (int hour = hoursForDay; hour < HOURS; hour++) {
                    int slot = day * HOURS + hour;
                    timetable[section][slot] = null; // Or create a TimetableSlot representing a free period
                }
            }
        }
    }
    
    
    
    public Chromosome[] crossover(Chromosome other, List<Lesson> lessons, List<Teacher> teachers) {
        Random random = new Random();
        int crossoverPoint = random.nextInt(TIME_SLOTS);
    
        TimetableSlot[][] child1Timetable = new TimetableSlot[SECTIONS][TIME_SLOTS];
        TimetableSlot[][] child2Timetable = new TimetableSlot[SECTIONS][TIME_SLOTS];
    
        for (int section = 0; section < SECTIONS; section++) {
            
            for (int slot = 0; slot < TIME_SLOTS; slot++) {
                
                if (slot < crossoverPoint) {
                    child1Timetable[section][slot] = this.timetable[section][slot];
                    child2Timetable[section][slot] = other.timetable[section][slot];
                } else {
                    child1Timetable[section][slot] = other.timetable[section][slot];
                    child2Timetable[section][slot] = this.timetable[section][slot];
                }
            }
        }
    
        Chromosome child1 = new Chromosome(lessons, teachers);
        Chromosome child2 = new Chromosome(lessons, teachers);
        child1.timetable = child1Timetable;
        child2.timetable = child2Timetable;
    
        child1.calculateFitness();
        child2.calculateFitness();
    
        return new Chromosome[]{child1, child2};
    }
    
    //calculate and return fitness based on constraints
  

    public void calculateFitness() {
        int satisfiedConstraints = 0;
        int totalConstraints = 0;
        Map<String, Integer> teacherWeeklyHours = new HashMap<>();
        Map<String, Integer> teacherDailyHours = new HashMap<>();
        Map<String, Set<Integer>> teacherTimeSlots = new HashMap<>();

        for (int section = 0; section < SECTIONS; section++) {
            for (int slot = 0; slot < TIME_SLOTS; slot++) {
                TimetableSlot currentSlot = timetable[section][slot];
                if (currentSlot == null) continue;

                int day = slot / HOURS;
                int hour = slot % HOURS;
                Teacher teacher = currentSlot.getTeacher();

                //update teacher hours
                String teacherID = teacher.getTeacherID();
                teacherWeeklyHours.put(teacherID, teacherWeeklyHours.getOrDefault(teacherID, 0) + 1);
                String dailyKey = teacherID + "-" + day;
                teacherDailyHours.put(dailyKey, teacherDailyHours.getOrDefault(dailyKey, 0) + 1);

                //check if teacher exceeds daily or weekly limits
                if (teacherDailyHours.get(dailyKey) <= teacher.getMaxHoursPerDay()) {
                    satisfiedConstraints++;
                }
                totalConstraints++;

                if (teacherWeeklyHours.get(teacherID) <= teacher.getMaxHoursPerWeek()) {
                    
                    satisfiedConstraints++;
                    
                }
                totalConstraints++;

                //check if teacher is assigned within their qualified subjects
                if (teacher.canTeach(currentSlot.getLesson().getSubjectID())) {
                    satisfiedConstraints++;
                }
                totalConstraints++;

                //check if teacher is not scheduled to teach multiple sections at the same time
                String timeSlotKey = teacherID + "-" + day + "-" + hour;
                teacherTimeSlots.putIfAbsent(timeSlotKey, new HashSet<>());
                teacherTimeSlots.get(timeSlotKey).add(section);

                if (teacherTimeSlots.get(timeSlotKey).size() == 1) {
                    satisfiedConstraints++;
                }
                totalConstraints++;
            }
        }

        //fitness score between 0.0 - 1.0
        double x= (double) satisfiedConstraints / totalConstraints;
        this.fitness = x;
        System.out.println(x);
    }
    
    //helper method to get section name
    public String getSectionName(int sectionIndex) {
        String[] sections = {"A1", "A2", "A3", "B1", "B2", "B3", "C1", "C2", "C3"};
        return sections[sectionIndex];
    }

  
    public TimetableSlot[][] getTimetable() {
        return timetable;
    }

    public double getFitness() {
        return fitness;
    }

    @Override
    public int compareTo(Chromosome other) {
        return Double.compare(this.fitness, other.fitness);
    }
}