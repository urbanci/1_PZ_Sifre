package sample.controllers;

import javafx.fxml.FXML;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import org.apache.commons.io.FilenameUtils;
import sample.Helper;
import sample.controllers.AsymmetricEncryption.GenerateKeys;
import sample.controllers.AsymmetricEncryption.SignData;
import sample.controllers.AsymmetricEncryption.VerifyData;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

public class Controller_DS {

    private byte[] data;
    private String extension;

    @FXML
    private Text t_key_pair;

    @FXML
    private Text t_message_6;

    @FXML
    private Text t_filename;


//    MAIN ------------------------------------------------------------------
//    generate key pair
    public void generateKeyPair(){
        GenerateKeys gk;
        try {
            gk = new GenerateKeys(1024);
            gk.createKeys();
            gk.writeToFile("KeyPair/publicKey", gk.getPublicKey().getEncoded());
            gk.writeToFile("KeyPair/privateKey", gk.getPrivateKey().getEncoded());

            alert(1, 1, "Key pair successfully generated to \"/KeyPair\" folder.");

        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            System.err.println(e.getMessage());
            alert(0, 1, "NoSuchAlgorithmException | NoSuchProviderException");
        } catch (IOException e) {
            System.err.println(e.getMessage());
            alert(0, 1, "IOException");
        }
    }

//    sign data
    public void signData(){
        try {
            new SignData(data, "KeyPair/privateKey").writeToFile("MyData/SignedData."+extension);
            alert(1, 0, "File successfully signed.");
        } catch (Exception e) {
            e.printStackTrace();
            alert(0, 0, "IOException");
        }
    }

//    verify data
    public void verifyData(){
        try {
            new VerifyData("MyData/SignedData."+extension, "KeyPair/publicKey", extension);
            alert(1, 0, "Data succesfully verified.");
        } catch (Exception e) {
            e.printStackTrace();
            alert(0, 0, "IOException");
        }
    }

//    OTHER -------------------------------------------------------------------------------

//    alerts
    public void alert(int color, int messageOrKey, String msg){
        if(messageOrKey == 0){
            if(color == 0){
                t_message_6.setFill(Color.RED);
            }else{
                t_message_6.setFill(Color.GREEN);
            }

            t_message_6.setText(msg);

        }else{
            if(color == 0){
                t_key_pair.setFill(Color.RED);
            }else{
                t_key_pair.setFill(Color.GREEN);
            }

            t_key_pair.setText(msg);
        }

    }

    //    get selected file
    public void getFile() throws IOException {
        File file = Helper.getFile();
        extension = FilenameUtils.getExtension(file.getPath());

        t_filename.setText(file.getName());

        if(file != null){
            data = Files.readAllBytes(file.toPath());
        }
    }

}
