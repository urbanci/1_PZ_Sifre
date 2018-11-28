package sample.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import org.apache.commons.io.FilenameUtils;
import sample.Helper;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;

public class ControllerAES {
    private int forLoop;
    private int forLoopTemp;
    private int keyLength;
    private int bytes; // counting checked bytes
    private int fileSize = 0;
    private int leftOverBits = 0;
    private String extension = "";

    private ArrayList<Integer> fileBytesPlain = new ArrayList<>();
    private ArrayList<Integer> encryptedFile = new ArrayList<>();

    private int[][] plainkey;
    private int[][] state;
    private int[][][] expandedKeys;

    //    FXML -------------------------------------
    @FXML
    private ComboBox<String> cb_bitrate = new ComboBox();

    @FXML
    private Text t_filename;

    @FXML
    private Text t_message_3;

    @FXML
    private TextField tf_key_4;

//    PROCESSES ------------------------------------------------------------------------------------

//    AES encrypt
    public void AESEncrypt(){
        StringBuilder sb = new StringBuilder();
        bytes = 0;
        fileSize = fileBytesPlain.size()/16;
        leftOverBits = fileBytesPlain.size()%16;
        encryptedFile = new ArrayList<>();

        for (int i = 0; i < fileSize+1; i++) {
            state = new int[4][4];

            getState(i);
            encrypt();

            for (int j = 0; j < 4; j++) {
                for (int k = 0; k < 4; k++) {
                    encryptedFile.add(state[k][j]);
                    sb.append(((char) state[k][j]));
                }
            }
        }

        try (PrintStream out = new PrintStream(new FileOutputStream("filename.txt"))) {
            out.print(sb);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

//    AES Decrypt
    public void AESDecrypt(){
        StringBuilder sb = new StringBuilder();
        bytes = 0;
        fileSize ++;
        leftOverBits = encryptedFile.size()%16;
        fileBytesPlain = new ArrayList<>();

        for (int i = 0; i < fileSize; i++) {
            state = new int[4][4];

            getEncryptedState(i);
            decrypt();

            if(i==fileSize-1){
                int p = 0;
                for (int j = 3; j < -1; j--) {
                    for (int k = 3; k < -1; k--) {
                      if(state[j][k]==0){
                          p++;
                      }else if(state[j][k]==1){
                          break;
                      }
                    }
                }

                int c = 0;
                for (int j = 0; j < 4; j++) {
                    for (int k = 0; k < 4; k++) {
                        if(c==16-p){
                            fileBytesPlain.add(state[k][j]);
                            sb.append(((char) state[k][j]));
                            c++;
                        }
                    }
                }
            }

            for (int j = 0; j < 4; j++) {
                for (int k = 0; k < 4; k++) {
                    fileBytesPlain.add(state[k][j]);
                    sb.append(((char) state[k][j]));
                }
            }
        }

        try (PrintStream out = new PrintStream(new FileOutputStream("filename_decrypted.txt"))) {
            out.print(sb);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

//    encrypt
    private void encrypt(){
        forLoopTemp = 0;
        getEncryptionKey();

        if(validateInputs()){
            keyExpension();
            addRoundKey();

            for (int i = 0; i < forLoop; i++) {
                subBytes();
                shiftRows();
                if(i!=forLoop-1){mixColumns(); forLoopTemp ++;}
                addRoundKey();
            }


            t_message_3.setText("Encryption successfull!");
            t_message_3.setFill(Color.GREEN);
        }
    }

//    decrypt
    public void decrypt(){
        getEncryptionKey();
        forLoopTemp = 9;

        if(validateInputs()){
            keyExpension();
            addRoundKey();

            for (int i = 0; i < forLoop; i++) {
                if(i!=0){invMixColumns(); forLoopTemp --;}
                invShiftRows();
                invSubBytes();
                addRoundKey();
            }

            t_message_3.setText("Decription successfull!");
            t_message_3.setFill(Color.GREEN);
        }
    }

//    ENCRYPT -------------------------------------------------------------------------------

    //    key expension
    private void keyExpension(){
        expandedKeys = new int[forLoop][4][4];
        int[] temp = new int[4];
        int rcon_iteration = 1;

        for (int i = 0; i < forLoop; i++) {
            for (int j = 0; j < 4; j++) {
                for (int k = 0; k < 4; k++) {
                    temp[k] = plainkey[j][k];
                }
                expandedKeys[i][j] = keyExpansionCore(temp, rcon_iteration);
            }
            rcon_iteration++;
        }

    }
    //    key expansion core
    private int[] keyExpansionCore(int[] temp, int rcon_iteration){
        int[] temp1 = new int[4];

//        switch to left
        temp1[0] = temp[1];
        temp1[1] = temp[2];
        temp1[2] = temp[3];
        temp1[3] = temp[0];

//        sbox again
        temp1[0] = Helper.s_box[temp1[0]];
        temp1[1] = Helper.s_box[temp1[1]];
        temp1[2] = Helper.s_box[temp1[2]];
        temp1[3] = Helper.s_box[temp1[3]];

//        rcon
        temp1[0] ^= Helper.rcon[rcon_iteration];

        return temp1;
    }

    //    AddRoundKey
    private void addRoundKey(){
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                state[j][i] ^= expandedKeys[forLoopTemp][j][i];
            }
        }
    }

    //    Sub Bytes
    private void subBytes(){
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                state[j][i] = Helper.s_box[state[j][i]];
            }
        }
    }

    //    Shift Rows
    private void shiftRows(){
        int[][] temp = state;
        state = new int[4][4];

        state[0][0] = temp[0][0];
        state[1][0] = temp[1][1];
        state[2][0] = temp[2][2];
        state[3][0] = temp[3][3];

        state[0][1] = temp[0][1];
        state[1][1] = temp[1][2];
        state[2][1] = temp[2][3];
        state[3][1] = temp[3][0];

        state[0][2] = temp[0][2];
        state[1][2] = temp[1][3];
        state[2][2] = temp[2][0];
        state[3][2] = temp[3][1];

        state[0][3] = temp[0][3];
        state[1][3] = temp[1][0];
        state[2][3] = temp[2][1];
        state[3][3] = temp[3][2];

    }

    //    Mix Columns
    private void mixColumns(){
        int temp[][] = state;
        state = new int[4][4];

        for (int i = 0; i < 4; i++) {
            state[0][i] = Helper.mul2[temp[0][i]] ^ Helper.mul3[temp[1][i]] ^ temp[2][i] ^ temp[3][i];
            state[1][i] = temp[0][i] ^ Helper.mul2[temp[1][i]] ^ Helper.mul3[temp[2][i]] ^ temp[3][i];
            state[2][i] = temp[0][i] ^ temp[1][i] ^ Helper.mul2[temp[2][i]] ^ Helper.mul3[temp[3][i]];
            state[3][i] = Helper.mul3[temp[0][i]] ^ temp[1][i] ^ temp[2][i] ^ Helper.mul2[temp[3][i]];
        }
    }


    //    DECRYPT -------------------------------------------------------------------------------

    //    Inverse Sub Bytes
    private void invSubBytes(){
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                state[j][i] = Helper.inv_s_box[state[j][i]];
            }
        }
    }

    //    Inverse Shift Rows
    private void invShiftRows(){
        int[][] temp = state;
        state = new int[4][4];

        state[0][0] = temp[0][0];
        state[1][0] = temp[1][3];
        state[2][0] = temp[2][2];
        state[3][0] = temp[3][1];

        state[0][1] = temp[0][1];
        state[1][1] = temp[1][0];
        state[2][1] = temp[2][3];
        state[3][1] = temp[3][2];

        state[0][2] = temp[0][2];
        state[1][2] = temp[1][1];
        state[2][2] = temp[2][0];
        state[3][2] = temp[3][3];

        state[0][3] = temp[0][3];
        state[1][3] = temp[1][2];
        state[2][3] = temp[2][1];
        state[3][3] = temp[3][0];

    }

    //    Inverse Mix Columns
    private void invMixColumns(){
        int[][] temp = state;
        state = new int[4][4];

        for (int i = 0; i < 4; i++) {
            state[0][i] = Helper.mul_14[temp[0][i]] ^ Helper.mul_11[temp[1][i]] ^ Helper.mul_13[temp[2][i]] ^ Helper.mul_9[temp[3][i]];
            state[1][i] = Helper.mul_9[temp[0][i]] ^ Helper.mul_14[temp[1][i]] ^ Helper.mul_11[temp[2][i]] ^ Helper.mul_13[temp[3][i]];
            state[2][i] = Helper.mul_13[temp[0][i]] ^ Helper.mul_9[temp[1][i]] ^ Helper.mul_14[temp[2][i]] ^ Helper.mul_11[temp[3][i]];
            state[3][i] = Helper.mul_11[temp[0][i]] ^ Helper.mul_13[temp[1][i]] ^ Helper.mul_9[temp[2][i]] ^ Helper.mul_14[temp[3][i]];
        }
    }

//    RACUNANJE / KONVERZIJE ---------------------------------------------------------------------

//    convert byte to hex
    private int byteToInt(byte b) { int i = b & 0xFF; return i; }

//    WIDGETI  -----------------------------------------------------------------------------------

//    get selected file
    public void getFile() throws IOException {
        fileBytesPlain.clear();
        byte[] bytes;
        File file = Helper.getFile();
        extension = FilenameUtils.getExtension(file.getName());

        t_filename.setText(file.getName());

        if(file != null){
            bytes = Files.readAllBytes(file.toPath());

            for (int i = 0; i < bytes.length; i++) {
                fileBytesPlain.add(byteToInt(bytes[i]));
            }
        }
    }

//    get state
    private void getState(int num) {
        int tempCount = 0;

        if (num == fileSize){
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    if(tempCount==leftOverBits) {
                        state[j][i] = 1;
                    }else if(tempCount > leftOverBits){
                        state[j][i] = 0;
                    }else{
                        state[j][i] = fileBytesPlain.get(bytes);
                    }
                    bytes++;
                    tempCount++;
                }
            }

        }else{
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    state[j][i] = fileBytesPlain.get(bytes);
                    bytes++;
                    tempCount++;
                }
            }
        }
    }

    private void getEncryptedState(int num){
        int tempCount = 0;
            if(num==fileSize-1 || num==fileSize-2){
                int b = bytes;

                for (int i = 0; i < 4; i++) {
                    for (int j = 0; j < 4; j++) {
                        int t = encryptedFile.get(b);
                        b++;
                    }

                    System.out.println();
                }

                System.out.println();
            }
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    int t = encryptedFile.get(bytes);
                    state[j][i] = t;
                    bytes++;
                    tempCount++;
                }
            }
    }

//    get encryption key
    public void getEncryptionKey(){
        int count = 0;
        plainkey = new int[4][4];
        String key = tf_key_4.getText();
        keyLength = key.length();

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                plainkey[j][i] = key.charAt(count);
                count++;
            }
        }
    }

//    select encryption bitrate
    public void selectEncryptionBitrate(){
        String output = cb_bitrate.getSelectionModel().getSelectedItem();
        forLoop = Helper.forLoop(Integer.valueOf(output.substring(0, 3)));
    }

    private boolean validateInputs(){
        boolean next = true;

        if (forLoop == 0){
            t_message_3.setText("Select the bit value!");
            t_message_3.setFill(Color.RED);
            next = false;
        }

        if (keyLength>16 || keyLength<16){
            t_message_3.setText("Incorect encryption key!");
            t_message_3.setFill(Color.RED);
            next = false;
        }

        return next;
    }

//    PRINTS --------------------------------------------------------------------------
    private void printState(){
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                System.out.print(state[i][j]+" ");
            }
            System.out.println();
        }

        System.out.println();
    }

    private void printPlainFile(){
        for (int i = 0; i < fileBytesPlain.size(); i++) {
            System.out.print(fileBytesPlain.get(i)+" ");
        }
        System.out.println();
    }

    private void printEncryptedFile(){
        for (int i = 0; i < encryptedFile.size(); i++) {
            System.out.print(encryptedFile.get(i)+" ");
        }
        System.out.println();
    }
}