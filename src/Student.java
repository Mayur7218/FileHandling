public class Student {
    private int id;
    private String name;
    private int age;
    private double grade;
    private String course;
    private String status;

    public Student(int id, String name, int age, double grade, String course) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.grade = grade;
        this.course = course;
        this.status = grade >= 2.0 ? "Pass" : "Fail";
    }

    @Override
    public String toString() {
        return id + "," + name + "," + age + "," + grade + "," + course + "," + status;
    }
}
