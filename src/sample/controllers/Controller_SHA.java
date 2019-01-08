package sample.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import sample.Helper;

import javax.xml.bind.DatatypeConverter;
import java.nio.ByteBuffer;

public class Controller_SHA {

    // working arrays
    private int[] W = new int[64];
    private int[] H = new int[8];
    private int[] TEMP = new int[8];

    @FXML
    private Text t_filename;

    @FXML
    private TextField tf_6_key;

    @FXML
    private Text t_message;

    @FXML
    private TextField tf_text;

//    ENCRYPTION -------------------------------------------------------------
    public void encrypt() {
        String msg = tf_text.getText();
        System.out.println(msg);
        byte[] message = msg.getBytes();

        // let H = H0
        System.arraycopy(Helper.H0, 0, H, 0, Helper.H0.length);

        // initialize all words
        int[] words = toIntArray(pad(message));

        // enumerate all blocks (each containing 16 words)
        for (int i = 0, n = words.length / 16; i < n; ++i) {

            // Extend the first 16 words into the remaining 48 words w[16..63] of the message schedule array:
            System.arraycopy(words, i * 16, W, 0, 16);

            for (int t = 16; t < W.length; ++t) {
                int s0 = smallSig0(W[t - 15]);
                int s1 = smallSig1(W[t - 2]);

                W[t] = W[t-16] + s0 + W[t-7] + s1;
            }

            // Initialize working variables to current hash value:
            System.arraycopy(H, 0, TEMP, 0, H.length);

            // Compression function main loop:
            for (int t = 0; t < W.length; ++t) {
                int S1 = bigSig1(TEMP[4]);
                int ch = ch(TEMP[4], TEMP[5], TEMP[6]);
                int temp1 = TEMP[7]+ S1 + ch + Helper.K[t] + W[t];

                int S0 = bigSig0(TEMP[0]);
                int maj = maj(TEMP[0], TEMP[1], TEMP[2]);
                int temp2 = S0 + maj;

                // Switching the temp values
                System.arraycopy(TEMP, 0, TEMP, 1, TEMP.length - 1);
                TEMP[4] += temp1;
                TEMP[0] = temp1 + temp2;
            }

            // Add the compressed chunk to the current hash value:
            for (int t = 0; t < H.length; ++t) {
                H[t] += TEMP[t];
            }

        }

        toByteArray(H);
    }

//    METHODS -------------------------------------------------------------------------

//    padding
    public byte[] pad(byte[] message)
    {
        final int blockBits = 512;
        final int blockBytes = blockBits / 8;

        // new message length: original + 1-bit and padding + 8-byte length
        int newMessageLength = message.length + 1 + 8;
        int padBytes = blockBytes - (newMessageLength % blockBytes);
        newMessageLength += padBytes;

        // copy message to extended array
        final byte[] paddedMessage = new byte[newMessageLength];
        System.arraycopy(message, 0, paddedMessage, 0, message.length);

        // write 1-bit
        paddedMessage[message.length] = (byte) 0b10000000;

        // skip padBytes many bytes (they are already 0)

        // write 8-byte integer describing the original message length
        int lenPos = message.length + 1 + padBytes;
        ByteBuffer.wrap(paddedMessage, lenPos, 8).putLong(message.length * 8);

        return paddedMessage;
    }

//    message to array of int
    public int[] toIntArray(byte[] bytes)
    {
        if (bytes.length % Integer.BYTES != 0) {
            throw new IllegalArgumentException("byte array length");
        }

        ByteBuffer buf = ByteBuffer.wrap(bytes);

        int[] result = new int[bytes.length / Integer.BYTES];
        for (int i = 0; i < result.length; ++i) {
            result[i] = buf.getInt();
        }

        return result;
    }

//    converts end byte array to hex values
    public void toByteArray(int[] ints)
    {
        ByteBuffer buf = ByteBuffer.allocate(ints.length * Integer.BYTES);
        for (int i = 0; i < ints.length; ++i) {
            buf.putInt(ints[i]);
        }

        bytesToHex(buf.array());
    }

//    CALCULATIONS ----------------------------------------------------------------

//    ch
    private int ch(int x, int y, int z)
    {
        return (x & y) ^ ((~x) & z);
    }

//    maj
    private int maj(int x, int y, int z)
    {
        return (x & y) ^ (x & z) ^ (y & z);
    }

//    S0
    private int bigSig0(int x)
    {
        return Integer.rotateRight(x, 2) ^ Integer.rotateRight(x, 13)
                ^ Integer.rotateRight(x, 22);
    }

//    S1
    private int bigSig1(int x)
    {
        return Integer.rotateRight(x, 6) ^ Integer.rotateRight(x, 11)
                ^ Integer.rotateRight(x, 25);
    }

//    s0
    private int smallSig0(int x)
    {
        return Integer.rotateRight(x, 7) ^ Integer.rotateRight(x, 18)
                ^ (x >>> 3);
    }

//    s1
    private int smallSig1(int x)
    {
        return Integer.rotateRight(x, 17) ^ Integer.rotateRight(x, 19)
                ^ (x >>> 10);
    }

//    OTHER ----------------------------------------------------------------

//    convert hash to hex string
    private void bytesToHex(byte[] bytes){
        String hash = "";

        hash = DatatypeConverter.printHexBinary(bytes);

        tf_6_key.setText(hash);
    }

}
