public class Ciphertext {
    /* Class for Ciphertext. A Ciphertext is an array of Strings. Each element
     * of this array is 2 characters and represents 1 byte in hex. Example:
     * {"4a","c0","ff", ...}
     */

    private final String[] ciphertextAsHexArray; //half size than our ciphertext string
    private final static int BLOCK_SIZE = 16; //AES block size
    private int numberOfBlocks;

    public Ciphertext(String ciphertext) {
        this.ciphertextAsHexArray = new String[ciphertext.length() / 2];
        parseCiphertext(ciphertext); //initialized in method
        this.numberOfBlocks = this.ciphertextAsHexArray.length / BLOCK_SIZE; //#AES blocks

    }

    private void parseCiphertext(String ciphertext) {
      /* Takes a String as input and initializes the String array which represents
      * our ciphertext (ciphertextAsHexArray). Example "4ac0ff" -> {"4a","c0","ff"}
      */
        char[] chars = ciphertext.toCharArray();
        for (int i = 0; i < chars.length - 1; i += 2) {
            this.ciphertextAsHexArray[i / 2] = String.valueOf(chars[i]) + String.valueOf(chars[i + 1]);
        }
    }

    public void substituteBlockAtBlockPosition(String block, int blockPosition) {
        /* Gets a whole block and a block position (one position
         * every BLOCK_SIZE and substitutes it with the provided
         * block. Useful for dummy blocks
         */
        if (block.length() != 2 * BLOCK_SIZE) { //TODO: CHECK! OR??
            if (blockPosition > this.numberOfBlocks - 1) { //position from 0 ... 3
                return;
            }
        }
        StringBuilder ciphertext = new StringBuilder(this.toString());
        ciphertext.replace(blockPosition * 2 * BLOCK_SIZE,
                blockPosition * 2 * BLOCK_SIZE + 2 * BLOCK_SIZE,
                block);
        parseCiphertext(ciphertext.toString());
    }

    public void substituteHexAtPosition(String hexValue, int hexPosition) {
        /* Accepts a String (hexvalue) and substitutes it at
         * the given position. Useful when someone tries blocks
         * sequentially during HTTP requests
         */
        if (hexValue.length() > 2 || hexPosition > ciphertextAsHexArray.length - 1) {
            return;
        }
        StringBuilder ciphertext = new StringBuilder(this.toString());
        ciphertext.replace(hexPosition * 2,
                hexPosition * 2 + 2,
                hexValue);
        parseCiphertext(ciphertext.toString());

    }

    public void xorWithHexAtPosition(String hexValue, int position) {
        /* XORs the hex value at position with given hexvalue
         * Useful when we want to forge the ciphertext
         */
        if (hexValue.length() > 2 || position > ciphertextAsHexArray.length - 1) {
            return;
        }
        String hexAtPosition = ciphertextAsHexArray[position];
        String forgedHexValue = Utils.xorHex(hexAtPosition, hexValue);
        if (forgedHexValue.length() == 1) {
            forgedHexValue = "0" + forgedHexValue;
        }
        this.substituteHexAtPosition(forgedHexValue, position);
    }

    @Override
    public String toString() {
        //return the representation of the ciphertext
        StringBuilder sb = new StringBuilder();
        for (String hexValue : ciphertextAsHexArray) {
            sb.append(hexValue);
        }
        return sb.toString();
    }

    public int getNumberOfBlocks() {
        return this.numberOfBlocks;
    }

    public String getHexAtPosition (int position) {
        return this.ciphertextAsHexArray[position];
    }

}
