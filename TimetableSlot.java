public class TimetableSlot {
    private Lesson lesson;
    private Teacher teacher;
    private String section;
    private int day;   // 0: Monday, 1: Tuesday, ..., 4: Friday
    private int hour;  // 0: 8:00-9:00, 1: 9:00-10:00, ..., 6: 14:00-15:00

    // Constructor with validation
    public TimetableSlot(Lesson lesson, Teacher teacher, String section, int day, int hour) {
        if (!teacher.canTeach(lesson.getSubjectID())) {
            throw new IllegalArgumentException("Ο ΚΑΘΗΓΗΤΗΣ " + teacher.getName() + " ΔΕΝ ΜΠΟΡΕΙ ΝΑ ΔΙΑΔΑΞΕΙ ΑΥΤΟ ΜΑΘΗΜΑ " + lesson.getName());
        }
        this.lesson = lesson;
        this.teacher = teacher;
        this.section = section;                                  
        this.day = day;
        this.hour = hour;
    }

   
    public Lesson getLesson() {
        return lesson;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public String getSection() {
        return section;
    }

    public int getDay() {
        return day;
    }

    public int getHour() {
        return hour;
    }

    //convert day integer to day name
    public String getDayName() {
        String[] days = {"ΔΕΥΤΕΡΑ", "ΤΡΙΤΗ", "ΤΕΤΑΡΤΗ", "ΠΕΜΠΤΗ", "ΠΑΡΑΣΚΕΥΗ"};
        return days[day];
    }

    //convert hour integer to time range
    public String getHourRange() {
        String[] hours = {
            "8:00-9:00", "9:00-10:00", "10:00-11:00",
            "11:00-12:00", "12:00-13:00", "13:00-14:00", "14:00-15:00"
        };
        return hours[hour];
    }

    
    @Override
    public String toString() {
        return ",ΜΑΘΗΜΑ: " + lesson.getName() +
               ", ΚΑΘΗΓΗΤΗΣ: " + teacher.getName() +
               ", ΤΜΗΜΑ: " + section +
               ", ΗΜΕΡΑ: " + getDayName() +
               ", ΩΡΑ: " + getHourRange();
    }
}
