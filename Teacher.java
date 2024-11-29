import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Teacher {
    private String teacherID;          //Unique ID of the teacher
    private String name;               //Name of the teacher
    private List<String> subjects;     //List of subject IDs the teacher can teach
    private int maxHoursPerDay;        //Maximum hours teacher can teach in a day
    private int maxHoursPerWeek;       //Maximum hours teacher can teach per week

    
    public Teacher(String teacherID, String name, List<String> subjects, int maxHoursPerDay, int maxHoursPerWeek) {
        this.teacherID = teacherID;
        this.name = name;
        this.subjects = subjects;
        this.maxHoursPerDay = maxHoursPerDay;
        this.maxHoursPerWeek = maxHoursPerWeek;
    }

    //Getters for each attribute
    public String getTeacherID() {
        return teacherID;
    }

    public String getName() {
        return name;
    }

    public List<String> getSubjects() {
        return subjects;
    }

    public int getMaxHoursPerDay() {
        return maxHoursPerDay;
    }

    public int getMaxHoursPerWeek() {
        return maxHoursPerWeek;
    }

    //Method to check if the teacher can teach a specific subject
    public boolean canTeach(String subjectID) {
        return subjects.contains(subjectID);
    }

    //Static method to parse the teachers.txt file and return a list of Teacher objects
    public static List<Teacher> parseTeachersFile(String filename) {
        List<Teacher> teachers = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("#");
                if (parts.length == 5) {
                    String teacherID = parts[0].trim();
                    String name = parts[1].trim();
                    List<String> subjects = Arrays.asList(parts[2].trim().split(","));
                    int maxHoursPerDay = Integer.parseInt(parts[3].trim());
                    int maxHoursPerWeek = Integer.parseInt(parts[4].trim());

                    teachers.add(new Teacher(teacherID, name, subjects, maxHoursPerDay, maxHoursPerWeek));
                }
            }
        } catch (IOException e) {
            System.out.println("ΕΜΦΑΝΙΣΤΗΚΕ ΛΑΘΟΣ ΚΑΤΑ ΤΗΝ ΕΓΓΡΑΦΗ ΤΟΥ ΑΡΧΕΙΟΥ: " + e.getMessage());
        }
        return teachers;
    }
}
