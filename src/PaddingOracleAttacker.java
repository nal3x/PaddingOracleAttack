import java.net.HttpURLConnection;

public class PaddingOracleAttacker {
    private final static int AES_BLOCK_SIZE = 16;
    private final static String challengeCiphertext = "f20bdba6ff29eed7b046d1df9fb7000058b1ffb4210a580f748b4ac714c001bd4a61044426fb515dad3f21f18aa577c0bdf302936266926ff37dbf7035d5eeb4";

    private final static StringBuilder plaintext = new StringBuilder(); //place to hold the disclosed plaintext

    public static void main (String ... args) {

        /* Before starting the Padding Oracle Attacker, make sure to split the above
         * ciphertext into AES blocks (16 bytes, 2 hex digits per byte => 32 hex digits.
         */

        Ciphertext ciphertext = new Ciphertext(challengeCiphertext);
        System.out.println(ciphertext);
        int ciphertextSizeinBytes = ciphertext.toString().length() / 2;
        System.out.println("Ciphertext size in bytes: " + ciphertextSizeinBytes);

        //we need an original ciphertext in order to decrypt, unless we make decryption later
        Ciphertext initialCiphertext = new Ciphertext(challengeCiphertext);

        //targeting previous ciphertext block means we have to forge it 16 bytes earlier
        int initialPosition = ciphertextSizeinBytes - AES_BLOCK_SIZE - 1;

        for (int position = initialPosition ; position > initialPosition - AES_BLOCK_SIZE; position--) {

            //counting from the end of ciphertext in order to get proper pad
            int targetByte = (AES_BLOCK_SIZE - (position % AES_BLOCK_SIZE));
            String currentPad = Utils.numberToHexValue(targetByte);
            String nextPad = Utils.getNextHexValue(currentPad);

            System.out.println("Current pad: " + currentPad + " Next pad: " + nextPad);

            for (int i = 0; i < 256; i++) { //HTTP tries
                String tryValue = Utils.numberToHexValue(i);

                ciphertext.substituteHexAtPosition(tryValue, position);
                int responseCode = NetworkUtils.getResponseCode(ciphertext.toString());
                System.out.println("Trying value " + tryValue + " at byte position " + position + ". Server response: " + responseCode);
                /* When response is 404, a valid pad for the ciphertext is achieved.
                 * We have to take into account that the ciphertext might not give an error at all
                 * This happens when we produce the original pad. Then the server returns a 200 OK
                 */
                if (responseCode == HttpURLConnection.HTTP_NOT_FOUND || (responseCode == HttpURLConnection.HTTP_OK) && (targetByte != 1)) {
                    System.out.println("\nValid pad for position " + position + " is " + tryValue);
                    //to get the plaintext byte: current pad value XOR forged ciphertext byte XOR ciphertext byte
                    String hexInPlaintext = Utils.xorHex(currentPad, tryValue);
                    hexInPlaintext = Utils.xorHex(hexInPlaintext,
                            initialCiphertext.getHexAtPosition(position));
                    plaintext.insert(0, hexInPlaintext);
                    System.out.println("PLAINTEXT: " + plaintext.toString());
                     //all previous bytes including current must be padded with currentPad xor nextPad
                    String newForger = Utils.xorHex(currentPad, nextPad);
                    for (int j = position ; j <= initialPosition; j++) {
                        ciphertext.xorWithHexAtPosition(newForger, j);
                    }
                    break;
                }
            }//for HTTP tries
        }//for block forge
    }//main
}