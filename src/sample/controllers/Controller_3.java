package sample.controllers;

import com.sun.deploy.util.ArrayUtil;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import org.apache.commons.lang3.ArrayUtils;
import sample.Helper;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Controller_3 {
    private Helper helper;
    private char[] letters = {' ', 'A', 'B', 'C', 'Č', 'D', 'E', 'F', 'G', 'H',
            'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'R', 'S',
            'Š', 'T', 'U', 'V', 'Z', 'Ž', '0', '1', '2', '3',
            '4', '5', '6', '7', '8', '9'};
    private char[] charTable = {' ', 'A', 'B', 'C', 'Č', 'D', 'E', 'F', 'G', 'H',
            'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'R', 'S',
            'Š', 'T', 'U', 'V', 'Z', 'Ž', '0', '1', '2', '3',
            '4', '5', '6', '7', '8', '9'};

    private char[][] playfairTable = new char[6][6];

    private String plainText;
    private String plainKey;

    @FXML
    private Text t_randomized_3;

    @FXML
    private TextArea ta_text_3;

    @FXML
    private TextField tf_key_3;

    @FXML
    private Text t_message_3;

    //    add txt file to textarea ta_1_hill
    public void addTextFile(){
        helper.addTextFile(ta_text_3);
    }

//    ENCRYPT ----------------------------------------------------------
//    main()
    public void encryption(){
        stripText();
        stripKey();
        randomizeData();
        encodeText();
        encryptText();
    }

//    encode text
    private void encodeText() {
        StringBuilder sb = new StringBuilder(plainText);

        for (int i = 0; i < sb.length(); i += 2) {

            if (i == sb.length() - 1)
                sb.append(sb.length() % 2 == 1 ? 'X' : "");

            else if (sb.charAt(i) == sb.charAt(i + 1))
                sb.insert(i + 1, 'X');
        }
        plainText = sb.toString();
    }

    private void encryptText(){
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < plainText.length(); i += 2) {
            char a = plainText.charAt(i);
            char b = plainText.charAt(i + 1);

            int rowA = 0, rowB = 0, colA = 0, colB = 0;

//            primerjava crk v tabeli z crkami v besedilu
            for (int j = 0; j < 6; j++) {
                for (int k = 0; k < 6; k++) {
                    if(a == playfairTable[j][k]){
                        rowA = j;
                        colA = k;
                    }

                    if(b == playfairTable[j][k]){
                        rowB = j;
                        colB = k;
                    }
                }
            }

//            zamenjava crk
            if (rowA == rowB) {
                colA = (colA + 1) % 6;
                colB = (colB + 1) % 6;
                sb.append(playfairTable[rowA][colA]+""+playfairTable[rowB][colB]);
                System.out.print(playfairTable[rowA][colA]+""+playfairTable[rowB][colB]);

            } else if (colA == colB) {
                rowA = (rowA + 1) % 6;
                rowB = (rowB + 1) % 6;
                sb.append(playfairTable[rowA][colA]+""+playfairTable[rowB][colB]);
                System.out.print(playfairTable[rowA][colA]+""+playfairTable[rowB][colB]);

            } else{
                int tempCol = colA;
                colA = colB;
                colB = tempCol;
                sb.append(playfairTable[rowA][colA]+""+playfairTable[rowB][colB]);
                System.out.print(playfairTable[rowA][colA]+""+playfairTable[rowB][colB]);
            }
        }

        System.out.println();
        plainText = sb.toString();
        ta_text_3.setText(plainText);
    }

//    DECRYPT ----------------------------------------------------------
//    decrypt
    public void decryption(){
        stripText();
        stripKey();
        decryptText();
    }

//    desifriraj besedilo
    private void decryptText(){
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < plainText.length(); i += 2) {
            char a = plainText.charAt(i);
            char b = plainText.charAt(i + 1);

            int rowA = 0, rowB = 0, colA = 0, colB = 0;

//            primerjava crk v tabeli z crkami v besedilu
            for (int j = 0; j < 6; j++) {
                for (int k = 0; k < 6; k++) {
                    if(a == playfairTable[j][k]){
                        rowA = j;
                        colA = k;
                    }

                    if(b == playfairTable[j][k]){
                        rowB = j;
                        colB = k;
                    }
                }
            }

//            zamenjava crk
            if (rowA == rowB) {
                colA = (colA - 1) % 6;
                colB = (colB - 1) % 6;

                if(colA == -1){
                    colA = 5;
                }

                if(colB == -1){
                    colB = 5;
                }
                sb.append(playfairTable[rowA][colA]+""+playfairTable[rowB][colB]);
                System.out.print(playfairTable[rowA][colA]+""+playfairTable[rowB][colB]);

            } else if (colA == colB) {
                rowA = (rowA - 1) % 6;
                rowB = (rowB - 1) % 6;

                if(rowA == -1){
                    rowA = 5;
                }

                if(rowB == -1){
                    rowB = 5;
                }
                sb.append(playfairTable[rowA][colA]+""+playfairTable[rowB][colB]);
                System.out.print(playfairTable[rowA][colA]+""+playfairTable[rowB][colB]);

            } else{
                int tempCol = colA;
                colA = colB;
                colB = tempCol;
                sb.append(playfairTable[rowA][colA]+""+playfairTable[rowB][colB]);
                System.out.print(playfairTable[rowA][colA]+""+playfairTable[rowB][colB]);
            }
        }
        System.out.println();

        plainText = sb.toString();
        ta_text_3.setText(plainText);
    }

//    TEXT AND KEY -----------------------------------------------------
//    spedenaj text
    private void stripText(){
        plainText = ta_text_3.getText().toUpperCase();
        ta_text_3.clear();

        StringBuilder sb = new StringBuilder();

        for (int len = 0; len < plainText.length(); len++) {
            for (int i = 0; i < 36; i++) {
                if(plainText.charAt(len) == letters[i]){
                    sb.append(letters[i]);
                }
            }
        }

        plainText = sb.toString();
    }

    //    spedenaj kljuc
    private void stripKey(){
        plainKey = tf_key_3.getText().toUpperCase();
        tf_key_3.clear();

        StringBuilder sb = new StringBuilder();

        for (int len = 0; len < plainKey.length(); len++) {
            for (int i = 0; i < 36; i++) {
                if(plainKey.charAt(len) == letters[i]){
                    sb.append(letters[i]);
                }
            }
        }

        plainKey = sb.toString();
    }

//    TABLES -----------------------------------------------------------
//    Randomize playfair array
    public void randomizeData() {
        char tempCharTable[] = charTable;
        ArrayList<Character> charTableKey = new ArrayList<>();

        System.out.println("1.");
        for (int i = 0; i < plainKey.length(); i++) {
            for (int j = 0; j < tempCharTable.length; j++) {
                if(plainKey.charAt(i) == tempCharTable[j]){
                    tempCharTable = ArrayUtils.remove(tempCharTable, j);
                    charTableKey.add(plainKey.charAt(i));
                    System.out.print(plainKey.charAt(i));
                }
            }
        }

        System.out.println();

        System.out.println("2.");
        tempCharTable = shuffleArray(tempCharTable);

        String cypher = mergePlayfairTable(tempCharTable, charTableKey);
        System.out.println("3.");
        System.out.println("cypher -> "+cypher);

        System.out.println();
        System.out.println("4.");
        int row = 0, column = 0;
        for (int i = 0; i < cypher.length(); i++) {
             if(i%6 != 0 || i==0){
                 playfairTable[row][column] = cypher.charAt(i);
                 column++;
                 System.out.print(cypher.charAt(i));
            }else if(i%6 == 0) {
                 column = 0;
                 row++;
                 System.out.println(" i%6 => " + i +cypher.charAt(i)+" "+row+" "+column);
                 playfairTable[row][column] = cypher.charAt(i);
                 column++;
                 System.out.print(cypher.charAt(i));
             }
        }

        System.out.println("\n");
        System.out.println("5.");
        getPlayfairTable();

        t_randomized_3.setText(cypher);

    }

    //    shuffle array
    private char[] shuffleArray(char[] randomTable){
        Random rnd = ThreadLocalRandom.current();

        for (int i = randomTable.length - 1; i > 0; i--) {
            int index = rnd.nextInt(i + 1);
            // Simple swap
            char a = randomTable[index];
            randomTable[index] = randomTable[i];
            randomTable[i] = a;
        }

        return randomTable;
    }

//    merge key and leftover keys
    private String mergePlayfairTable(char[] tempCharTable, ArrayList<Character> charTableKey){
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < charTableKey.size(); i++) {
            sb.append(charTableKey.get(i));
        }

        for (int i = 0; i < tempCharTable.length; i++) {
            sb.append(tempCharTable[i]);
        }



        return sb.toString();
    }

//    PRINTS ---------------------------------------------------------
//    print the playfair table
    private String getPlayfairTable(){
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 6; j++) {
                System.out.print(playfairTable[i][j]);
                sb.append(playfairTable[i][j]);
            }
            System.out.println();
        }

        System.out.println();

        return sb.toString();
    }
}
