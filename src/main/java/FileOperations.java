import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

/**
 * Created by IntelliJ IDEA.
 * User: Joshua Campbell
 * Date: 3/21/21
 */
public class FileOperations {

    private FileOperations() {}

    public static String loadDataFromFile() {
        StringBuilder data = new StringBuilder();
        try {
            File file = new File("saveDotaStats.txt");
            Scanner myReader = new Scanner(file);
            while (myReader.hasNextLine()) {
                data.append(myReader.nextLine());
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred reading in data from file.");
            e.printStackTrace();
        }
        return data.toString();
    }

    public static void saveDataToFile(String lastSeqNumber, String data) {
        try (FileWriter myWriter = new FileWriter("saveDotaLastSequenceNumber.txt")) {
            myWriter.write(lastSeqNumber);
            System.out.println("Successfully wrote sequence to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred writing data to file while writing last sequence number.");
            e.printStackTrace();
        }

        try (FileWriter myWriter = new FileWriter("saveDotaStats.txt")) {
            myWriter.write(data);
            System.out.println("Successfully wrote all heroes to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred writing all heroes data to file.");
            e.printStackTrace();
        }
    }

}
