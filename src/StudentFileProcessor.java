import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class StudentFileProcessor {
    private final ExecutorService executor = Executors.newFixedThreadPool(10);

    public void processFile(String inputFilePath, String outputFilePath) {
        try {
            List<CompletableFuture<Optional<Student>>> futures = Files.lines(Paths.get(inputFilePath))
                    .skip(1) // Skip header
                    .map(line -> CompletableFuture.supplyAsync(() -> processLine(line), executor))
                    .collect(Collectors.toList());

            List<Student> validStudents = futures.stream()
                    .map(CompletableFuture::join)
                    .filter(Optional::isPresent)// Ensure it's present
                    .map(Optional::get)
                    .collect(Collectors.toList());

            CompletableFuture.runAsync(() -> writeToFile(validStudents, outputFilePath), executor)
                    .thenRun(() -> System.out.println("Processing completed successfully!"))
                    .exceptionally(ex -> {
                        System.err.println("Error writing to output file: " + ex.getMessage());
                        return null;
                    });
        } catch (IOException e) {
            System.err.println("Error reading the input file: " + e.getMessage());
        } finally {
            executor.shutdown();
        }
    }

    private Optional<Student> processLine(String line) {
        String[] fields = line.split(",");

        if (fields.length != 5) {
            System.err.println("Invalid record (wrong number of fields): " + line);
            return Optional.empty();
        }

        try {
            int id = Integer.parseInt(fields[0].replaceAll("\"", "").trim());
            String name = fields[1].replaceAll("\"", "").trim();
            int age = Integer.parseInt(fields[2].replaceAll("\"", "").trim());
            double grade = Double.parseDouble(fields[3].replaceAll("\"", "").trim());
            String course = fields[4].replaceAll("\"", "").trim();

            if (age < 18 || age > 25) {
                System.err.println("Invalid age for student ID " + id + ": " + age);
                return Optional.empty();
            }
            if(grade<0.0 || grade >4.0){
                System.out.println("Invalid grade for student ID "+ id +": "+grade);
                return Optional.empty();
            }
            return Optional.of(new Student(id, name, age, grade, course));
        } catch (NumberFormatException e) {
            System.err.println("Invalid number format in record: " + line + " - " + e.getMessage());
            return Optional.empty();
        }
    }

    private void writeToFile(List<Student> students, String outputFilePath) {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(outputFilePath))) {
            for (Student student : students) {
                writer.write(student.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error writing to output file: " + e.getMessage());
        }
    }
}
