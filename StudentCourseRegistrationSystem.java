import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Course {
    String courseCode;
    String title;
    String description;
    int capacity;
    int registeredStudents;
    String schedule;

    public Course(String courseCode, String title, String description, int capacity, String schedule) {
        this.courseCode = courseCode;
        this.title = title;
        this.description = description;
        this.capacity = capacity;
        this.registeredStudents = 0;
        this.schedule = schedule;
    }

    public boolean isAvailable() {
        return registeredStudents < capacity;
    }

    public void registerStudent() {
        if (isAvailable()) {
            registeredStudents++;
        }
    }

    public void dropStudent() {
        if (registeredStudents > 0) {
            registeredStudents--;
        }
    }

    public String getAvailableSlots() {
        return (capacity - registeredStudents) + " slots available";
    }

    @Override
    public String toString() {
        return courseCode + " - " + title + " (" + getAvailableSlots() + ")";
    }
}

class Student {
    String studentId;
    String name;
    List<Course> registeredCourses;

    public Student(String studentId, String name) {
        this.studentId = studentId;
        this.name = name;
        this.registeredCourses = new ArrayList<>();
    }

    public void registerForCourse(Course course) {
        if (course.isAvailable() && !registeredCourses.contains(course)) {
            course.registerStudent();
            registeredCourses.add(course);
        } else {
            JOptionPane.showMessageDialog(null, "Course is either full or already registered.");
        }
    }

    public void dropCourse(Course course) {
        if (registeredCourses.contains(course)) {
            course.dropStudent();
            registeredCourses.remove(course);
        } else {
            JOptionPane.showMessageDialog(null, "You are not registered for this course.");
        }
    }
}

public class StudentCourseRegistrationSystem extends JFrame {
    private List<Course> courses;
    private Map<String, Student> studentDatabase;
    private Student currentStudent;
    private JComboBox<Course> courseComboBox;
    private JList<String> registeredCoursesList;
    private DefaultListModel<String> listModel;
    private JTextField studentIdField, studentNameField;

    public StudentCourseRegistrationSystem() {
        setTitle("Student Course Registration System");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(15, 15));
        getContentPane().setBackground(new Color(40, 40, 40)); // Dark theme background

        courses = new ArrayList<>();
        studentDatabase = new HashMap<>();
        listModel = new DefaultListModel<>();

        // Sample course data
        courses.add(new Course("CS101", "Intro to Computer Science", "Basics of computer science.", 30, "Mon 9-11am"));
        courses.add(new Course("MATH201", "Calculus I", "Differential and integral calculus.", 25, "Tue 10-12pm"));
        courses.add(new Course("PHY101", "Physics I", "Fundamentals of physics.", 20, "Wed 1-3pm"));
        courses.add(new Course("ENG102", "English Literature", "Classic and modern literature.", 40, "Thu 11-1pm"));

        createUI();
    }

    private void createUI() {
        JPanel studentPanel = createStudentPanel();
        JPanel coursePanel = createCoursePanel();
        JPanel registeredPanel = createRegisteredCoursesPanel();

        add(studentPanel, BorderLayout.NORTH);
        add(coursePanel, BorderLayout.CENTER);
        add(registeredPanel, BorderLayout.SOUTH);
    }

    private JPanel createStudentPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBackground(new Color(50, 50, 50)); // Dark theme background for panels
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Padding

        JLabel studentIdLabel = new JLabel("Student ID:");
        studentIdLabel.setForeground(Color.WHITE);
        studentIdField = new JTextField();

        JLabel studentNameLabel = new JLabel("Student Name:");
        studentNameLabel.setForeground(Color.WHITE);
        studentNameField = new JTextField();

        JButton registerStudentButton = new JButton("Register Student");
        registerStudentButton.addActionListener(this::registerStudent);

        panel.add(studentIdLabel);
        panel.add(studentIdField);
        panel.add(studentNameLabel);
        panel.add(studentNameField);
        panel.add(registerStudentButton);

        return panel;
    }

    private JPanel createCoursePanel() {
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBackground(new Color(50, 50, 50)); // Dark theme background
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Padding

        JLabel courseLabel = new JLabel("Select Course:");
        courseLabel.setForeground(Color.WHITE);

        courseComboBox = new JComboBox<>();
        updateCourseComboBox();

        JButton registerCourseButton = new JButton("Register for Course");
        registerCourseButton.addActionListener(this::registerForCourse);

        JButton dropCourseButton = new JButton("Drop Course");
        dropCourseButton.addActionListener(this::dropCourse);

        panel.add(courseLabel);
        panel.add(courseComboBox);
        panel.add(registerCourseButton);
        panel.add(dropCourseButton);

        return panel;
    }

    private JPanel createRegisteredCoursesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(50, 50, 50)); // Dark theme background
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Padding

        JLabel registeredLabel = new JLabel("Registered Courses:");
        registeredLabel.setForeground(Color.WHITE);

        registeredCoursesList = new JList<>(listModel);
        registeredCoursesList.setBackground(new Color(60, 60, 60));
        registeredCoursesList.setForeground(Color.WHITE);

        panel.add(registeredLabel, BorderLayout.NORTH);
        panel.add(new JScrollPane(registeredCoursesList), BorderLayout.CENTER);

        return panel;
    }

    private void registerStudent(ActionEvent e) {
        String studentId = studentIdField.getText().trim();
        String studentName = studentNameField.getText().trim();

        if (studentId.isEmpty() || studentName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both Student ID and Name.");
            return;
        }

        currentStudent = studentDatabase.computeIfAbsent(studentId, id -> new Student(studentId, studentName));
        JOptionPane.showMessageDialog(this, "Student Registered: " + studentName);
    }

    private void registerForCourse(ActionEvent e) {
        if (currentStudent == null) {
            JOptionPane.showMessageDialog(this, "Please register a student first.");
            return;
        }

        Course selectedCourse = (Course) courseComboBox.getSelectedItem();
        if (selectedCourse != null) {
            currentStudent.registerForCourse(selectedCourse);
            updateCourseComboBox();
            updateRegisteredCoursesList();
        }
    }

    private void dropCourse(ActionEvent e) {
        if (currentStudent == null) {
            JOptionPane.showMessageDialog(this, "Please register a student first.");
            return;
        }

        Course selectedCourse = (Course) courseComboBox.getSelectedItem();
        if (selectedCourse != null) {
            currentStudent.dropCourse(selectedCourse);
            updateCourseComboBox();
            updateRegisteredCoursesList();
        }
    }

    private void updateCourseComboBox() {
        courseComboBox.removeAllItems();
        for (Course course : courses) {
            courseComboBox.addItem(course);
        }
    }

    private void updateRegisteredCoursesList() {
        listModel.clear();
        if (currentStudent != null) {
            for (Course course : currentStudent.registeredCourses) {
                listModel.addElement(course.courseCode + " - " + course.title + " (" + course.schedule + ") - "
                        + currentStudent.name + " - " + currentStudent.studentId);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            StudentCourseRegistrationSystem frame = new StudentCourseRegistrationSystem();
            frame.setVisible(true);
        });
    }
}
