package sample.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import sample.Helper;

public class Controller_1 {
    private char[] letters = {' ', 'A', 'B', 'C', 'Č', 'D', 'E', 'F', 'G', 'H',
                                'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'R', 'S',
                                'Š', 'T', 'U', 'V', 'Z', 'Ž', '0', '1', '2', '3',
                                '4', '5', '6', '7', '8', '9', '.', ',', '-', '%', ')', '(', '/'};
    private Helper helper;

    private int[][]tf_1_keyArray;
    private int[][]ta_1_textArray;

    private int ta_1_textLength = 0;
    private int tf_1_keyLength = 0;
    private int divided = 0;

    private String plainText = "";
    private String plainKey = "";

    @FXML
    private TextArea ta_1_text;

    @FXML
    private TextField tf_1_key;

    @FXML
    private Text t_message;

    //    add txt file to textarea ta_1_hill
    public void addTextFile(){
        helper.addTextFile(ta_1_text);
    }

//    PRIPRAVA ---------------------------------------------------------------------
//    uravljanje s besedilom
    private void manipulateText(){
        plainText = ta_1_text.getText();
        ta_1_textLength = plainText.length();
        ta_1_textArray = new int[2][ta_1_textLength];

//        ce je deljivo z dva
        if(ta_1_textLength%2 != 0){
            plainText += "0";
            ta_1_textLength = plainText.length();
            divided = 1;
        }

        manipulateInputText();
    }

    //    upravljanje s key-jem
    private void manipulateKey(){
        tf_1_keyArray = new int[2][tf_1_keyLength];

        manipulateInputKey();

    }

//    ENKRIPCIJA -----------------------------------------------------------------------------
//    enkripcija
    public void encrypt(){
        plainKey = tf_1_key.getText();
        tf_1_keyLength = plainKey.length();
        System.out.println(tf_1_keyLength);
        if(tf_1_keyLength != 4){
            t_message.setText("Need's to be 4 letter key!");
            t_message.setFill(Color.RED);
        }else{
            manipulateText();
            manipulateKey();

            if(multiInverse(determinant()) == -1){
                t_message.setText("Invalid key");
                t_message.setFill(Color.RED);
            }else {
                String encryptetText = encryptingProcess();
                ta_1_text.appendText(encryptetText);
                t_message.setText("Encryption Successfull");
                t_message.setFill(Color.GREEN);
            }
        }
    }

//    dekripcija
    public void decrypt(){
        plainKey = tf_1_key.getText();
        tf_1_keyLength = plainKey.length();
        System.out.println(tf_1_keyLength);
        if(tf_1_keyLength != 4){
            t_message.setText("Need's to be 4 letter key!");
            t_message.setFill(Color.RED);
        }else{
            manipulateText();
            manipulateKey();

            System.out.println("1. ------");
            System.out.println(tf_1_keyArray[0][0]+" "+tf_1_keyArray[0][1] +" "+tf_1_keyArray[1][0] +" "+tf_1_keyArray[1][1]);

            int swampTemp = tf_1_keyArray[0][0];
            tf_1_keyArray[0][0] = tf_1_keyArray[1][1];
            tf_1_keyArray[1][1] = swampTemp;

            tf_1_keyArray[1][0] *= -1;
            tf_1_keyArray[0][1] *= -1;

            tf_1_keyArray[0][1] = mod(tf_1_keyArray[0][1]);
            tf_1_keyArray[1][0] = mod(tf_1_keyArray[1][0]);

            int mulInverse = multiInverse(determinant());

            for (int i = 0; i < 2; i++) {
                for (int j = 0; j < 2; j++) {
                    tf_1_keyArray[i][j] *= mulInverse;
                }
            }

            String decryptedText = encryptingProcess();
            t_message.setText("Decryption Successfull");
            t_message.setFill(Color.GREEN);

            if(divided == 1){
                decryptedText = decryptedText.substring(0, decryptedText.length() - 1);
            }

            ta_1_text.appendText(decryptedText);
        }
    }

    public String encryptingProcess(){
        String encryptetText = "";
        for (int i = 0; i < ta_1_textLength/2; i++) {
            int temp1 = ta_1_textArray[0][i]*tf_1_keyArray[0][0] + ta_1_textArray[1][i]*tf_1_keyArray[0][1];
            encryptetText += letters[temp1%43];
            int temp2 = ta_1_textArray[0][i]*tf_1_keyArray[1][0] + ta_1_textArray[1][i]*tf_1_keyArray[1][1];
            encryptetText += letters[temp2%43];
        }

        ta_1_text.clear();
        return encryptetText;
    }


//    DODATKI --------------------------------------------------------------------------------
//    pretvorba vsebine v metrike
    private void manipulateInputText(){
        int itr1 = 0;
        int itr2 = 0;
        int count = 0;

        plainText = plainText.toUpperCase();

        StringBuilder newString = new StringBuilder();
        char letter;

        for (int i = 0; i < plainText.length(); i++) {
            for (int j=0; j<letters.length; j++) {
                letter = plainText.charAt(i);
                if(letter == letters[j]){
                    newString.append(letter);
                    if(count%2 == 0){
                        ta_1_textArray[0][itr1] = j;
                        itr1++;
                    }else{
                        ta_1_textArray[1][itr2] = j;
                        itr2++;
                    }
                    count++;
                    break;
                }//if
            }//for letters
        }//for stirng
        plainText = String.valueOf(newString);
        System.out.println(plainText);
    }

    private void manipulateInputKey(){
        char letter;
        int[][] key = new int[2][2];
        int counter = 0;
        int itr1 = 0, itr2 = 0;

        plainKey = plainKey.toUpperCase();

        for (int i = 0; i < plainKey.length(); i++) {
            for (int j = 0; j < letters.length; j++) {
                letter = plainKey.charAt(i);
                if(letter == letters[j]){
                    if(counter%2 == 0){
                        key[0][itr1] = j;
                        itr1++;
                    }else{
                        key[1][itr2] = j;
                        itr2++;
                    }
                    counter++;
                }
            }
        }
        izpisi(key);
        tf_1_keyArray = key;
    }


    //    module funkcija
    private int mod(int num){
        int result = num%43;
        if(result < 0){
            result += 43;
            return result;
        }else{
            return result;
        }
    }

//    calculating deter
    private int determinant(){
        return tf_1_keyArray[0][0] * tf_1_keyArray[1][1] - tf_1_keyArray[0][1] * tf_1_keyArray[1][0];
    }

    //    izpisovanje rezultatov
    private void izpisi(int a[][]){
        for (int i = 0; i < 2; i++) {
            System.out.println(i+"------> ");
            for (int j = 0; j < a[0].length; j++) {
                System.out.print(a[i][j]+" ");
            }
            System.out.println();
        }
    }

//    Multiplicative inverse
    private int multiInverse(int deter){
        int mulInverse = -1;
        for (int i = 0; i < 43; i++) {
            int tempInv = deter * i;
            if(mod(tempInv) == 1){
                mulInverse = i;
                break;
            }else{
                continue;
            }
        }
        return mulInverse;
    }

    private String convertToString(int[][] array){
        StringBuilder newString = new StringBuilder();

        for (int i = 0; i < array[0].length; i++) {
            for (int j = 0; j < 2; j++) {
                newString.append(letters[array[j][i]]);
            }
        }

        return newString.toString();
    }
}
