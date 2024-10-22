public class Main {
    public static void main(String[] args) {

        String inputFilePath = "/Users/trux/Documents/DemoTest1.csv";
        String outputFilePath = "/Users/trux/Documents/DemoTest3.csv";

        StudentFileProcessor processor = new StudentFileProcessor();
        processor.processFile(inputFilePath, outputFilePath);

        System.out.println("Processing complete. Check output file for results.");
    }
}
