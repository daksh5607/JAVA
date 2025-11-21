import java.util.*;

public class StudentManagementApp {

    static abstract class Person {
        protected String name, email;
        Person(String name, String email) { this.name = name; this.email = email; }
        public abstract void displayInfo();
    }
    static class Student extends Person {
        private final int rollNo;
        private String course;
        private double marks;
        private char grade;

        Student(int rollNo, String name, String email, String course, double marks) {
            super(name, email);
            this.rollNo = rollNo;
            this.course = course;
            this.marks = marks;
            calc();
        }

        int getRollNo() { return rollNo; }

        void setMarks(double marks) { this.marks = marks; calc(); }

        private void calc() {
            if (marks >= 90) grade = 'A';
            else if (marks >= 75) grade = 'B';
            else if (marks >= 60) grade = 'C';
            else grade = 'D';
        }

        @Override
        public void displayInfo() {
            System.out.println("Roll: " + rollNo + " Name: " + name + " Email: " + email +
                    " Course: " + course + " Marks: " + marks + " Grade: " + grade);
        }
    }

    interface RecordActions {
        boolean addStudent(Student s);
        boolean deleteStudent(int rollNo);
        boolean updateStudent(int rollNo, Student s);
        Student searchStudent(int rollNo);
        List<Student> viewAllStudents();
    }

    static class StudentManager implements RecordActions {
        private final Map<Integer, Student> map = new LinkedHashMap<>();

        public boolean addStudent(Student s) {
            if (map.containsKey(s.getRollNo())) return false;
            map.put(s.getRollNo(), s);
            return true;
        }

        public boolean deleteStudent(int rollNo) { return map.remove(rollNo) != null; }

        public boolean updateStudent(int rollNo, Student s) {
            if (!map.containsKey(rollNo)) return false;
            map.put(rollNo, s); return true;
        }

        public Student searchStudent(int rollNo) { return map.get(rollNo); }

        public List<Student> viewAllStudents() { return new ArrayList<>(map.values()); }
    }
    
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        StudentManager mgr = new StudentManager();

        while (true) {
            System.out.println("\n1.Add 2.Delete 3.Update 4.MarksUpdate 5.Search 6.ViewAll 7.Exit");
            System.out.print("Choose: ");

            int ch = Integer.parseInt(sc.nextLine().trim());

            if (ch == 1) {
                System.out.print("Roll: "); int r = Integer.parseInt(sc.nextLine());
                System.out.print("Name: "); String n = sc.nextLine();
                System.out.print("Email: "); String e = sc.nextLine();
                System.out.print("Course: "); String c = sc.nextLine();
                System.out.print("Marks: "); double m = Double.parseDouble(sc.nextLine());

                Student s = new Student(r, n, e, c, m);
                System.out.println(mgr.addStudent(s) ? "Added" : "Roll exists");

            } else if (ch == 2) {
                System.out.print("Roll: "); int r = Integer.parseInt(sc.nextLine());
                System.out.println(mgr.deleteStudent(r) ? "Deleted" : "Not found");

            } else if (ch == 3) {
                System.out.print("Roll: "); int r = Integer.parseInt(sc.nextLine());
                System.out.print("Name: "); String n = sc.nextLine();
                System.out.print("Email: "); String e = sc.nextLine();
                System.out.print("Course: "); String c = sc.nextLine();
                System.out.print("Marks: "); double m = Double.parseDouble(sc.nextLine());

                Student s = new Student(r, n, e, c, m);
                System.out.println(mgr.updateStudent(r, s) ? "Updated" : "Not found");

            } else if (ch == 4) {
                System.out.print("Roll: "); int r = Integer.parseInt(sc.nextLine());
                System.out.print("New Marks: "); double m = Double.parseDouble(sc.nextLine());

                Student s = mgr.searchStudent(r);
                if (s == null) System.out.println("Not found");
                else { s.setMarks(m); System.out.println("Marks updated"); }

            } else if (ch == 5) {
                System.out.print("Roll: "); int r = Integer.parseInt(sc.nextLine());
                Student s = mgr.searchStudent(r);
                System.out.println(s == null ? "Not found" : "Found: ");
                if (s != null) s.displayInfo();

            } else if (ch == 6) {
                List<Student> all = mgr.viewAllStudents();
                if (all.isEmpty()) System.out.println("No records");
                else all.forEach(Student::displayInfo);

            } else if (ch == 7) {
                System.out.println("Exit");
                return;
            }
        }
    }
}
