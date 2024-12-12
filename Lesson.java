import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Lesson {
    private String subjectID;
    private String name;
    private String classLevels;
    private int[] weeklyHours; //should always be three

    public Lesson(String subjectID, String name, String classLevels, int[] weeklyHours) {
        this.subjectID = subjectID;
        this.name = name;
        this.classLevels = classLevels;
        this.weeklyHours = weeklyHours;
    }

    public String getSubjectID() {
        return subjectID;
    }

    public String getName() {
        return name;
    }

    public String getClassLevels() {
        return classLevels;
    }
    
    public int[] getWeeklyHours() {
        return weeklyHours;
    }

    //parse the lessons.txt file
    public static List<Lesson> parseLessonsFile(String filename) {
        List<Lesson> lessons = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filename), "UTF-8"))) {
            String line;
            boolean isFirstLine = true;

            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                String[] parts = line.split("#");
                if (parts.length == 4) {
                    String subjectID = parts[0].trim();
                    String name = parts[1].trim();
                    String classLevelsStr = parts[2].trim();
                    String hoursStr = parts[3].trim();

                    String[] classLevels = classLevelsStr.split(",");
                    String[] hoursArray = hoursStr.split(",");

                    int[] weeklyHours = new int[3]; // Always length 3 for A, B, C
                    // Initialize to 0 for all class levels
                    Arrays.fill(weeklyHours, 0);

                    for (int i = 0; i < classLevels.length; i++) {
                        String classLevel = classLevels[i].trim();
                        int hours = Integer.parseInt(hoursArray[i].trim());
                        switch (classLevel) {
                            case "A":
                                weeklyHours[0] = hours;
                                break;
                            case "B":
                                weeklyHours[1] = hours;
                                break;
                            case "C":
                                weeklyHours[2] = hours;
                                break;
                            default:
                                System.out.println("Unknown class level: " + classLevel);
                                break;
                        }
                    }

                    lessons.add(new Lesson(subjectID, name, classLevelsStr, weeklyHours));
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Error parsing hours: " + e.getMessage());
        }
        return lessons;
    }

}
