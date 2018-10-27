package sample.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import sample.Helper;

public class Controller_3 {
    private char[] letters = {' ', 'A', 'B', 'C', 'Č', 'D', 'E', 'F', 'G', 'H',
            'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'R', 'S',
            'Š', 'T', 'U', 'V', 'Z', 'Ž', '0', '1', '2', '3',
            '4', '5', '6', '7', '8', '9', '.', ',', '-', '%', ')', '(', '/'};
    private Helper helper;

    @FXML
    private TextArea ta_3_text;

    @FXML
    private TextField tf_3_key;

    @FXML
    private Text t_message_3;

    //    add txt file to textarea ta_1_hill
    public void addTextFile(){
        helper.addTextFile(ta_3_text);
    }


}
