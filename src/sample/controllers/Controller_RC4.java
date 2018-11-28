package sample.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.math3.util.Precision;
import sample.Helper;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;

public class Controller_RC4 {
    private ArrayList<Integer> filePlain = new ArrayList<>();
    private ArrayList<Integer> fileEncrypted = new ArrayList<>();
    private ArrayList<Integer> plainKey = new ArrayList<>();
    private ArrayList<Integer> s_box = new ArrayList<>();

    private String extension;
    private int keyLength;
    private double plainFileSize;

    @FXML
    private Text t_filename;

    @FXML
    private TextField tf_5_key;

    @FXML
    private Text t_message;

    @FXML
    private Text t_encryption;

    @FXML
    private Text t_decryption;

//    MAIN ----------------------------------------------------------------------------------------

//    CREATION ------------------------------------------------------------------------------------

//    encrypt
    public void encrypt(){
        getKey();
        long startTime = System.nanoTime();

        createSBox();
        printSBox();
        prga();

        long endTime = System.nanoTime();

        double seconds = (double)(endTime-startTime)/ 1_000_000_000.0;
        double time = Precision.round(((plainFileSize/1024)/1024)/seconds, 2);

        setMessages("Encryption successfull!", true, true, time);


    }

    public void decrypt(){
        getKey();
        long startTime = System.nanoTime();

        createSBox();
        printSBox();
        invPrga();

        long endTime = System.nanoTime();

        double seconds = (double)(endTime-startTime)/ 1_000_000_000.0;
        double time = Precision.round(((plainFileSize/1024)/1024)/seconds, 2);

        setMessages("Decryption successfull!", true, false, time);

    }


//    s-box, key schedule
    private void createSBox(){
        s_box.clear();
        for (int i = 0; i < 256; i++) {
            s_box.add(i);
        }

//        key schedule
        int j = 0;

        for (int i = 0; i < 256; i++) {
            j = (j + s_box.get(i) + plainKey.get(i%keyLength)) % 256;
            Collections.swap(s_box, i, j);
        }

    }

//    Pseudo-random generation algorithm (PRGA)
    private void prga(){
        int i = 0;
        int a = 0;
        int b = 0;
        StringBuilder sb = new StringBuilder();
        fileEncrypted.clear();

        while (i<filePlain.size()){
            a = (a + 1) % 256;
            b = (b + s_box.get(a)) % 256;
            Collections.swap(s_box, a, b);

            int key_int = s_box.get((s_box.get(a) + s_box.get(b))% 256);
            int cypher = key_int^filePlain.get(i);

            fileEncrypted.add(cypher);
            sb.append((char)cypher);

            i++;
        }

        System.out.println("aaa");
//        export file
        Helper.exportFile(sb.toString(), extension, true);
    }

//    inverse PRGA
    private void invPrga(){
        int i = 0;
        int a = 0;
        int b = 0;
        StringBuilder sb = new StringBuilder();
        filePlain.clear();

        System.out.println("aaa");
        while (i<fileEncrypted.size()){
            a = (a + 1) % 256;
            b = (b + s_box.get(a)) % 256;
            Collections.swap(s_box, a, b);

            int key_int = s_box.get((s_box.get(a) + s_box.get(b))% 256);
            int cypher = key_int^fileEncrypted.get(i);

            filePlain.add(cypher);
            sb.append((char)cypher);

            i++;
        }

        System.out.println();

//        export file
        Helper.exportFile(sb.toString(), extension, false);
    }



//    get key
    private void getKey(){
        String plainKeyString = tf_5_key.getText();
        keyLength = plainKeyString.length();
        int keyLoop = 256/keyLength;
        int keyLeftOver = 256%keyLength;
        byte[] bytes = plainKeyString.getBytes();

//        add ascii without mode
        for (int i = 0; i < keyLoop; i++) {
            for (int j = 0; j < keyLength; j++) {
                plainKey.add((int)bytes[j]);
            }
        }

//        add ascii mode
        for (int i = 0; i < keyLeftOver; i++) {
            plainKey.add((int)bytes[i]);
        }

    }

//    SUPPORT --------------------------------------------------------------------------------------
//    convert byte to int
    private int byteToInt(byte b) { int i = b & 0xFF; return i; }

//    get selected file
    public void getFile() throws IOException {
        filePlain.clear();
        byte[] bytes;
        File file = Helper.getFile();
        plainFileSize = file.length();
        extension = FilenameUtils.getExtension(file.getName());

        t_filename.setText(file.getName());

        if(file != null){
            bytes = Files.readAllBytes(file.toPath());

            for (int i = 0; i < bytes.length; i++) {
                filePlain.add(byteToInt(bytes[i]));
            }
        }
    }

    public void printSBox(){
        for (int i = 0; i < s_box.size(); i++) {
            if(i%16==0){
                System.out.println();
            }else{
                System.out.print(s_box.get(i)+" ");
            }
        }
    }

    private void setMessages(String message, boolean success, boolean encryption, double time){
        if(success){
            t_message.setText(message);
            t_message.setFill(Color.GREEN);
        }else{
            t_message.setText(message);
            t_message.setFill(Color.RED);
        }

        if(encryption){
            t_encryption.setText(String.valueOf(time));
        }else{
            t_decryption.setText(String.valueOf(time));
        }
    }



}
