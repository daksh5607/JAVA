import java.util.*;

class StudentNotFoundException extends Exception {
    public StudentNotFoundException(String message) { super(message); }
}

interface RecordActions {
    void addStudent();
    void displayStudent(Integer rollNo) throws StudentNotFoundException;
    String calculateGrade(Double marks);
}

class StudentRecord {
    private final Integer rollNo;
    private final String name;
    private final String email;
    private final String course;
    private final Double marks;
    private String grade;

    public StudentRecord(Integer rollNo, String name, String email, String course, Double marks) {
        this.rollNo = rollNo;
        this.name = name;
        this.email = email;
        this.course = course;
        this.marks = marks;
    }

    public Integer getRollNo() { return rollNo; }
    public Double getMarks() { return marks; }
    public void setGrade(String grade) { this.grade = grade; }

    @Override
    public String toString() {
        return "Roll No: " + rollNo +
                "\nName: " + name +
                "\nEmail: " + email +
                "\nCourse: " + course +
                "\nMarks: " + marks +
                "\nGrade: " + grade;
    }
}

class StudentManager implements RecordActions {
    private final Map<Integer, StudentRecord> students = new LinkedHashMap<>();
    private final Scanner scanner;

    public StudentManager(Scanner scanner) { this.scanner = scanner; }

    @Override
    public void addStudent() {
        try {
            System.out.print("Enter Roll No (Integer): ");
            String r = scanner.nextLine().trim();
            if (r.isEmpty()) { System.out.println("Roll number cannot be empty!"); return; }
            int roll = Integer.parseInt(r);
            if (roll <= 0) { System.out.println("Roll must be positive!"); return; }
            if (students.containsKey(roll)) { System.out.println("Roll already exists!"); return; }

            System.out.print("Enter Name: ");
            String name = scanner.nextLine().trim();
            if (name.isEmpty()) { System.out.println("Name cannot be empty!"); return; }

            System.out.print("Enter Email: ");
            String email = scanner.nextLine().trim();
            if (email.isEmpty() || !email.contains("@")) { System.out.println("Invalid email!"); return; }

            System.out.print("Enter Course: ");
            String course = scanner.nextLine().trim();
            if (course.isEmpty()) { System.out.println("Course cannot be empty!"); return; }

            System.out.print("Enter Marks: ");
            String m = scanner.nextLine().trim();
            if (m.isEmpty()) { System.out.println("Marks cannot be empty!"); return; }
            double marks = Double.parseDouble(m);
            if (marks < 0 || marks > 100) { System.out.println("Marks must be 0-100!"); return; }

            StudentRecord s = new StudentRecord(roll, name, email, course, marks);
            s.setGrade(calculateGrade(marks));
            showLoader("Adding student");
            students.put(roll, s);

            System.out.println("Student added:");
            System.out.println(s);
        } catch (NumberFormatException ex) {
            System.out.println("Invalid number format: " + ex.getMessage());
        } catch (InterruptedException ex) {
            System.out.println("Operation interrupted.");
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void displayStudent(Integer rollNo) throws StudentNotFoundException {
        if (rollNo == null) throw new StudentNotFoundException("Roll number cannot be null!");
        StudentRecord s = students.get(rollNo);
        if (s == null) throw new StudentNotFoundException("Student with Roll No " + rollNo + " not found!");
        System.out.println("\n--- Student Found ---\n" + s);
    }

    @Override
    public String calculateGrade(Double marks) {
        if (marks >= 90) return "A+";
        if (marks >= 80) return "A";
        if (marks >= 70) return "B";
        if (marks >= 60) return "C";
        if (marks >= 50) return "D";
        return "F";
    }

    public void displayAllStudents() {
        if (students.isEmpty()) { System.out.println("No students in the system."); return; }
        System.out.println("\n--- All Students ---");
        students.values().forEach(s -> { System.out.println(s); System.out.println("---------------"); });
    }

    private void showLoader(String message) throws InterruptedException {
        System.out.print(message);
        for (int i = 0; i < 5; i++) {
            Thread.sleep(200);
            System.out.print(".");
        }
        System.out.println();
    }
}

public class StudentRegistrationSystem {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        StudentManager manager = new StudentManager(scanner);
        while (true) {
            System.out.println("\n=== Student Management System ===");
            System.out.println("1. Add Student");
            System.out.println("2. Display Student by Roll No");
            System.out.println("3. Display All Students");
            System.out.println("4. Exit");
            System.out.print("Enter choice: ");
            String choice = scanner.nextLine().trim();
            if (choice.isEmpty()) { System.out.println("Choice cannot be empty!"); continue; }
            try {
                switch (Integer.parseInt(choice)) {
                    case 1 -> manager.addStudent();
                    case 2 -> {
                        System.out.print("Enter Roll No to search: ");
                        String r = scanner.nextLine().trim();
                        manager.displayStudent(Integer.parseInt(r));
                    }
                    case 3 -> manager.displayAllStudents();
                    case 4 -> { System.out.println("Exiting..."); scanner.close(); return; }
                    default -> System.out.println("Invalid choice! Enter 1-4.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid number: " + e.getMessage());
            } catch (StudentNotFoundException e) {
                System.out.println(e.getMessage());
            } catch (Exception e) {
                System.out.println("Unexpected error: " + e.getMessage());
            }
        }
    }
}
