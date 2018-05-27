import java.io.IOException;
import java.net.*;

public class NetworkUtils {

    private static final String PROTOCOL = "http";
    private static final String HOST = "crypto-class.appspot.com";
    private static final String AUTH = null;
    private static final String FRAGMENT = null;
    private static final int PORT = 80;
    private static final String PATH = "/po";

    public static int getResponseCode (String ciphertext) {
        String query = "er=" + ciphertext;
        URL url = null;
        int response = -1;

        try { //building the URL using URI
            URI uri = new URI(PROTOCOL, AUTH, HOST, PORT, PATH, query, FRAGMENT);
            url = uri.toURL();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        if (null == url) return -1; //wtf syntax :P

        try {
            URLConnection connection = url.openConnection();
            HttpURLConnection httpURLConnection = (HttpURLConnection) connection;
            response = httpURLConnection.getResponseCode();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return response;

    }
}
