public class Utils {

    private final static char[] hexArray = "0123456789abcdef".toCharArray();

//    public static void main (String ... args) {
//        System.out.println(xorHex("c037ef0bd2ae514a1f408244ab25fd53",
//                "5968ec548eb9a57e7e59627f5e2a4c68"));
//    }

    public static String hexToAscii (String hex) {
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < hex.length(); i+=2) {
            String str = hex.substring(i, i+2);
            output.append((char)Integer.parseInt(str, 16));
        }
        return output.toString();
    }

    public static String xorHex(String s1, String s2) {
        //TODO: throw an exception or check if the two strings have equal length
        byte[] firstByteArray = hexStringToByteArray(s1);
        byte[] secondByteArray = hexStringToByteArray(s2);
        byte[] resultArray = new byte[firstByteArray.length];

        for (int i = 0; i < firstByteArray.length; i++) {
            resultArray[i] = (byte) (firstByteArray[i] ^ secondByteArray[i]);
        }

        return bytesToHex(resultArray);
    }


    private static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    private static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static String numberToHexValue (int number) {

        String hexValue = Integer.toHexString(number);
        if (hexValue.length() == 1) {
            hexValue = "0" + hexValue;
        }
        return hexValue;
    }

    public static String getNextHexValue (String hexValue) {
        int currentHexValue = Integer.valueOf(hexValue, 16);
        currentHexValue++;

        return numberToHexValue(currentHexValue);
    }



}
