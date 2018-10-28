package sample.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import sample.Helper;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Controller_2 {
    private Helper helper;
    private char[] letters = {' ', 'A', 'B', 'C', 'Č', 'D', 'E', 'F', 'G', 'H',
            'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'R', 'S',
            'Š', 'T', 'U', 'V', 'Z', 'Ž', '0', '1', '2', '3',
            '4', '5', '6', '7', '8', '9'};
    private char[] randomTable = {' ', 'A', 'B', 'C', 'Č', 'D', 'E', 'F', 'G', 'H',
            'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'R', 'S',
            'Š', 'T', 'U', 'V', 'Z', 'Ž', '0', '1', '2', '3',
            '4', '5', '6', '7', '8', '9'};
    private char[] adfgvx = {'A', 'D', 'F', 'G', 'V', 'X'};

    private char[][] adfgvxTable = new char[6][6];
    private char[][] encryptedADFGVX;
    private List<String> alphabeticalEncryptedADFGVX;

    private String plainText;
    private String plainKey;

    @FXML
    private TextArea ta_text_2;

    @FXML
    private TextField tf_key_2;

    @FXML
    private Text t_message_2;

    @FXML
    private Text t_randomized;

//    add txt file to textarea ta_1_hill
    public void addTextFile(){
        helper.addTextFile(ta_text_2);
    }

//    MAIN --------------------------------------------------------
//    Randomize ADFGVX array
    public void randomizeData(){
        int count = 0;
        shuffleArray();

        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 6; j++) {
                adfgvxTable[i][j] = randomTable[count];
                count++;
            }
        }

        t_randomized.setText(getADFGVX());
    }

    public void encryptText(){
        stripText();
        stripKey();
        encrypt();
    }

    public void decryptText(){
        decrypt();
    }

//    DODATNO ------------------------------------------------------
//    shuffle array
    private void shuffleArray(){
        Random rnd = ThreadLocalRandom.current();

        for (int i = randomTable.length - 1; i > 0; i--) {
            int index = rnd.nextInt(i + 1);
            // Simple swap
            char a = randomTable[index];
            randomTable[index] = randomTable[i];
            randomTable[i] = a;
        }
    }

//    encryption process
    private void encrypt(){
        if(ifUniqueChars()) {
            switchLetters();
            toKeyTable();
            printKeyTable();
            sortKeyTableAlphabetically();
            printKeyTableAlphabetically();

            encryptionSuccesfull();
        }else{
            invalidKey();
        }
    }

//    dencryption process
    private void decrypt(){
        if(ifUniqueChars()) {
            stripKey();
            orderCypherAndTextDecrypt();
            splitTableToLettersDecrypt();

            ta_text_2.clear();
            ta_text_2.setText(plainText);

            decryptionSuccesfull();
        }else{
            invalidKey();
        }
    }


//    spedenaj text
    private void stripText(){
        plainText = ta_text_2.getText().toUpperCase();
        ta_text_2.clear();

        StringBuilder sb = new StringBuilder();

        for (int len = 0; len < plainText.length(); len++) {
            for (int i = 0; i < 36; i++) {
                if(plainText.charAt(len) == letters[i]){
                    sb.append(letters[i]);
                }
            }
        }

        plainText = sb.toString();
        ta_text_2.setText(plainText);
    }

//    spedenaj kljuc
    private void stripKey(){
        plainKey = tf_key_2.getText().toUpperCase();
        tf_key_2.clear();

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

//    zamenjaj crke s crkami v tabeli
    private void switchLetters(){
        StringBuilder sb = new StringBuilder();

        for (int len = 0; len < plainText.length(); len++) {
            for (int i = 0; i < 6; i++) {
                for (int j = 0; j < 6; j++) {
                    if(adfgvxTable[i][j] == plainText.charAt(len)){
                        sb.append(adfgvx[i]+""+adfgvx[j]);
                    }
                }
            }
        }
        plainText = sb.toString();
    }

//    dodaj kodo v enkriptirano tabelo
    private void toKeyTable(){
        int tableHeight = (plainText.length()/plainKey.length())+2;
        encryptedADFGVX = new char[tableHeight][plainKey.length()];
        int count = 0;

        for (int i = 0; i < tableHeight; i++) {
            for (int j = 0; j < plainKey.length(); j++) {
                if(i == 0){
                    encryptedADFGVX[i][j] = plainKey.charAt(j);
                }else if (plainText.length() == count){
                    encryptedADFGVX[i][j] = '/';
                }else{
                    encryptedADFGVX[i][j] = plainText.charAt(count);
                    count++;
                }
            }
        }
    }

//    sortiraj kriptirano tabelo po abecedi
    private void sortKeyTableAlphabetically(){
        StringBuilder sb;
        alphabeticalEncryptedADFGVX = new ArrayList<String>();

        for (int i = 0; i < encryptedADFGVX[0].length; i++) {
            sb = new StringBuilder();
            for (int j = 0; j < encryptedADFGVX.length; j++) {
                if(encryptedADFGVX[j][i] != '/'){
                    sb.append(encryptedADFGVX[j][i]);
                }
            }
            alphabeticalEncryptedADFGVX.add(sb.toString());
            java.util.Collections.sort(alphabeticalEncryptedADFGVX);
        }

    }

//    print adfgvx table
    private String getADFGVX(){
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            System.out.print(adfgvx[i]+" -> ");
            for (int j = 0; j < 6; j++) {
                System.out.print(adfgvxTable[i][j]);
                sb.append(adfgvxTable[i][j]);
            }
            System.out.println();
        }

        System.out.println();

        return sb.toString();
    }

    private void orderCypherAndTextDecrypt(){
        List<Character> key = new ArrayList<>();
        List<String> text = new ArrayList<>();
        List<String> textAndKey = new ArrayList<>();

        StringBuilder sb = new StringBuilder();

//        posamezne crke kljuca
        for (int i = 0; i < plainKey.length(); i++) {
            key.add(plainKey.charAt(i));
        }
        System.out.println();

        java.util.Collections.sort(key);

//        posamezne kombinacije v sifri med presledki
        for (int i = 0; i < plainText.length(); i++) {
            if(plainText.charAt(i) != ' '){
                sb.append(plainText.charAt(i));

            }else{
                text.add(sb.toString());
                sb = new StringBuilder();
            }
            if(plainText.length()-1 == i){
                text.add(sb.toString());
                sb = new StringBuilder();
            }
        }

//        zdruzevanje crk kljuca in delov sifre
        for (int i = 0; i < plainKey.length(); i++) {
            textAndKey.add(key.get(i)+text.get(i));
        }

        combineKeyAndTextDecrypt(textAndKey);
    }

    private void combineKeyAndTextDecrypt(List<String> textAndKey){
        List<String> list = new ArrayList<>();

        for (int i = 0; i < plainKey.length(); i++) {
            char letter = plainKey.charAt(i);
            for (int j = 0; j < textAndKey.size(); j++) {
                if(letter == textAndKey.get(j).charAt(0)){
                    list.add(textAndKey.get(j));
                }
            }
        }

        System.out.println("PRINT TABLE: ->");
        for (int i = 0; i < list.size(); i++) {
            System.out.println(list.get(i));
        }

        char[][] charList = new char[list.get(0).length()][list.size()];

        for (int i = 0; i < list.size(); i++) {
            for (int j = 0; j < list.get(i).length(); j++) {
                charList[j][i] = list.get(i).charAt(j);
            }
        }

        System.out.println();
        encryptedADFGVX = charList;
        printKeyTable();
    }

    private void splitTableToLettersDecrypt(){
        int count = 0, down = 0, up = 0;
        char[][] crke = new char[2][plainText.length()/2];
        plainText = "";

        for (int i = 1; i < encryptedADFGVX.length; i++) {
            for (int j = 0; j < encryptedADFGVX[i].length; j++) {
                for (int k = 0; k < adfgvx.length; k++) {
                    if (encryptedADFGVX[i][j] == adfgvx[k]){
                        if(count == 0){
                            down = k;
                        }else{
                            up = k;
                        }
                    }
                }
                if(count == 1){
                    plainText += adfgvxTable[down][up];
                    count = 0;
                }else{
                    count++;
                }
            }

        }
    }

    //    PRINTS -----------------------------------------------------------------
//    print key table
    private void printKeyTable(){

        for (int i = 0; i < encryptedADFGVX.length; i++) {
            for (int j = 0; j < encryptedADFGVX[0].length; j++) {
                System.out.print(encryptedADFGVX[i][j]);
            }
            System.out.println();
        }
    }

    private void printKeyTableAlphabetically(){
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < alphabeticalEncryptedADFGVX.size(); i++) {
            if(alphabeticalEncryptedADFGVX.size()-1 == i){
                sb.append(alphabeticalEncryptedADFGVX.get(i).substring(1));
            }else{
                sb.append(alphabeticalEncryptedADFGVX.get(i).substring(1)+" ");
            }
        }

        plainText = sb.toString();
        ta_text_2.setText(plainText);
    }

    private boolean ifUniqueChars(){
        // If at any time we encounter 2
        // same characters, return false
        for (int i = 0; i < plainKey.length(); i++)
            for (int j = i + 1; j < plainKey.length(); j++)
                if (plainKey.charAt(i) == plainKey.charAt(j))
                    return false;

        // If no duplicate characters encountered,
        // return true
        return true;
    }

//    ALERTS -----------------------------------------------------------
//    invalid key
    private void invalidKey(){
        t_message_2.setText("Invalid Key");
        t_message_2.setFill(Color.RED);
    }

//    encryption succesfull
    private void encryptionSuccesfull(){
        t_message_2.setText("Encryption Successfull");
        t_message_2.setFill(Color.GREEN);
    }

//    encryption succesfull
    private void decryptionSuccesfull(){
        t_message_2.setText("Decryption Successfull");
        t_message_2.setFill(Color.GREEN);
    }
}

