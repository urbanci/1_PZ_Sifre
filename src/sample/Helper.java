package sample;

import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Helper {

//    Add txt file to textarea
    public static void addTextFile(TextArea textArea){

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Text Files", "*.txt"));

        File selectedFile = fileChooser.showOpenDialog(null);;
        if (selectedFile != null) {
            readFile(selectedFile,textArea);
        }
    }

//    Read file from filechooser
    private static void readFile(File file, TextArea textArea){
        if(textArea != null){
            textArea.clear();
        }

        try (Scanner scanner = new Scanner(file)) {

            while (scanner.hasNext())
                if (scanner.hasNextInt()) { // check if next token is an int
                    textArea.appendText(scanner.nextInt() + " "); // display the found integer
                } else {
                    textArea.appendText(scanner.next() + " "); // else read the next token
                }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
