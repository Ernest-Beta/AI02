import java.util.List;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Timetable {
    private TimetableSlot[][] timetable; // 2D array representing the timetable
    private static final int SECTIONS = 9; // A1, A2, A3, B1, B2, B3, C1, C2, C3
    private static final int DAYS = 5;
    private static final int HOURS = 7;
    private static final int TIME_SLOTS = DAYS * HOURS; // Total time slots per section

    
    public Timetable() {
        this.timetable = new TimetableSlot[SECTIONS][TIME_SLOTS];  // sto timeslot nmz dn xreiazetai na exoume section,day,hour kathos anaparistatai apo mono toy apo thn thesh pou brhskete to slot ston pinaka
    }

    //set a slot in the timetable
    public void setSlot(int section, int day, int hour, Lesson lesson, Teacher teacher) {
        int slot = day * HOURS + hour;
        timetable[section][slot] = new TimetableSlot(lesson, teacher, getSectionName(section), day, hour);
    }

    //get a slot from the timetable
    public TimetableSlot getSlot(int section, int day, int hour) {
        int slot = day * HOURS + hour;
        return timetable[section][slot];
    }

    //get the day name based on index
    private String getDayName(int dayIndex) {
        String[] days = {"ΔΕΥΤΕΡΑ", "ΤΡΙΤΗ", "ΤΕΤΑΡΤΗ", "ΠΕΜΠΤΗ", "ΠΑΡΑΣΚΕΥΗ"};
        if (dayIndex >= 0 && dayIndex < days.length) {
            return days[dayIndex];
        }
        return "Unknown day";
    }

   

    // get the section name based on index
    private String getSectionName(int sectionIndex) {
        String[] sections = {"A1", "A2", "A3", "B1", "B2", "B3", "C1", "C2", "C3"};
        return sections[sectionIndex];
    }

    //export the timetable to a file
    public void exportTimetable(String filename) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename, java.nio.charset.StandardCharsets.UTF_8))) {
            for (int section = 0; section < SECTIONS; section++) {
                writer.write("ΤΜΗΜΑ: " + getSectionName(section) + "\n");
                for (int day = 0; day < DAYS; day++) {
                    writer.write("ΗΜΕΡΑ: " + getDayName(day) + "\n");
                    for (int hour = 0; hour < HOURS; hour++) {
                        TimetableSlot slot = getSlot(section, day, hour);
                        if (slot != null) {
                            writer.write("Η ΩΡΑ " + (hour + 1) + ": " + slot.getLesson().getName() +
                                         " ΔΙΔΑΣΚΕΤΑΙ ΑΠΟ: " + slot.getTeacher().getName() + "\n");
                        } else {
                            writer.write("ΩΡΑ " + (hour + 1) + ": Free\n");
                        }
                    }
                    writer.write("\n");
                }
                writer.write("------------------------------------------------\n");
            }
        } catch (IOException e) {
            System.out.println("Error" + e.getMessage());
            e.printStackTrace();
        }
    }
}
