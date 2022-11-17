import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.Random;
import java.util.Scanner;


public class Main {
    final static int sizes = 1000000;
    static Random rand = new Random();
    static Scanner scanner = new Scanner(System.in);
    static String algType = "";

    public static void main(String[] args) throws IOException {
        int runSize = sizes;
        int numFiles = 7;
        String tempDir = "",
                outputFileName = "output.txt",
                inputFileName = "input.txt";

        inputFile(inputFileName);


        String[] inputArray = null;
        String[] inputModArray = readModFile(inputFileName);

        try {
            BufferedReader br = new BufferedReader(new FileReader(inputFileName));
            inputArray = readFile(br);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (algType.equals("simple")) {
            assert inputArray != null;
            System.out.println("total lines: " + inputArray.length);
            System.out.println("run size: " + runSize + "\nNum files: " + numFiles);

            long startTimeSimple = System.currentTimeMillis();

            Sorter s = new Sorter(runSize, numFiles, tempDir);
            s.sort(inputArray, outputFileName);
            System.out.println("Total execution time: " + (double) (System.currentTimeMillis() - startTimeSimple) / 1000 + " sec");
        } else {
            System.out.println("total lines: " + inputModArray.length);
            System.out.println("run size: " + runSize + "\nNum files: " + numFiles);

            long startTimeMod = System.currentTimeMillis();

            Sorter s = new Sorter(runSize, numFiles, tempDir);
            s.sort(inputModArray, outputFileName);
            System.out.println("Total execution time: " + (double) (System.currentTimeMillis() - startTimeMod) / 1000 + " sec");
        }
    }

    public static void inputFile(String inputFileName) throws IOException {
        File file = new File(inputFileName);
        FileWriter fileWriter = new FileWriter(inputFileName);
        FileOutputStream fos = new FileOutputStream(inputFileName);
        int i = 0;

        System.out.println("Enter the type of algorithm [simple/modif]: ");
        switch (scanner.nextLine()) {
            case "simple":
                algType = "simple";
                System.out.println("[simple] Running...");
                while (i % 100000 != 0 || file.length() / (1024 * 1024) < 25) {
                    fileWriter.write(String.valueOf(rand.nextInt(10000000)));
                    fileWriter.write("\n");
                    i++;
                }
                break;
            case "modif":
                algType = "modif";
                System.out.println("[modified] Running...");
                while (i % 100000 != 0 || file.length() / (1024 * 1024) < 25) {
                    fos.write(rand.nextInt(10000000));
                    i++;
                }
                break;
        }

        fileWriter.close();
        fos.close();
        System.out.println("Successfully wrote to the file.\n");
        System.out.println("File size: " + (double) file.length() / (1024 * 1024) + " mb\n");
    }

    public static String[] readFile(BufferedReader br) {
        LinkedList<String> linesList = new LinkedList<>();
        try {
            String line = br.readLine();
            while (line != null) {
                linesList.add(line);
                line = br.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return linesList.toArray(new String[0]);
    }

    public static String[] readModFile(String inputFileName) {
        LinkedList<String> linesList = new LinkedList<>();
        try {
            DataInputStream dis = new DataInputStream(
                    new BufferedInputStream(Files.newInputStream(Paths.get(inputFileName))));
            for (int i = 0; i < dis.available()/6.2; i++) {
                linesList.add(String.valueOf(dis.readInt()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return linesList.toArray(new String[0]);
    }
}
