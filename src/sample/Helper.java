package sample;

import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
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

    //    Add file to textarea
    public static File getFile() throws IOException {
        File fileContent = null;

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");

        fileContent = fileChooser.showOpenDialog(null);;

        return fileContent;

    }
	
//    get for loop number
    public static int forLoop(int bitrate){
        int forLoop = 0;

        switch (bitrate){
            case 128:
                forLoop = 10;
                break;

            case 192:
                forLoop = 12;
                break;

            case 256:
                forLoop = 14;
                break;
        }

        return forLoop;
    }
}
