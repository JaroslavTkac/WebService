import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

/**
 * Created by jaroslavtkaciuk on 27/04/2017.
 */
class HandleRequests {
    private static final String USER_AGENT = "Mozilla/5.0";

    static Object GET(String urlToRead) throws Exception {
        URL obj = new URL(urlToRead);
        HttpURLConnection connection = (HttpURLConnection) obj.openConnection();

        connection.setRequestProperty("User-Agent", USER_AGENT);
        int responseCode = connection.getResponseCode();

        BufferedReader in = new BufferedReader(
                new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        //System.out.println(response.toString());
        return response;
    }
    static String sendGETRequest(String urlToRead) throws Exception {
        StringBuilder result = new StringBuilder();

        URL url = new URL(urlToRead);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String line;
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }

        rd.close();
        return result.toString();
    }

    static String POSTLoggedData(String urlToRead, String URL, String method, String request, String headerPATH,
                               String headerMETHOD, String headerERROR, int responseCode) throws Exception {
        URL url = new URL(urlToRead);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("POST");
        connection.setRequestProperty("User-Agent", USER_AGENT);
        connection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        connection.setRequestProperty("Content-Type","application/json");

        String postJsonData = "{\"method\": \"" + method + "\",\n" +
                "    \"request\": \"" + request + "\",\n" +
                "    \"URL\": \"" + URL + "\",\n" +
                "    \"headerPATH\": \"" + headerPATH + "\",\n" +
                "    \"headerMETHOD\": \"" + headerMETHOD + "\",\n" +
                "    \"headerERROR\": \"" + headerERROR + "\",\n" +
                "    \"responseCode\": " + responseCode + "}";

        // Send post request
        connection.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
        wr.writeBytes(postJsonData);
        wr.flush();
        wr.close();

        BufferedReader in = new BufferedReader(
                new InputStreamReader(connection.getInputStream()));
        String output;
        StringBuffer response1 = new StringBuffer();

        while ((output = in.readLine()) != null) {
            response1.append(output);
        }
        in.close();


        return "";
    }
    static String POSTBankAccount(String urlToRead, String name, String surname, float balance) throws Exception {
        URL url = new URL(urlToRead);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("POST");
        connection.setRequestProperty("User-Agent", USER_AGENT);
        connection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        connection.setRequestProperty("Content-Type","application/json");

        String postJsonData = "{\"name\": \"" + name + "\",\n" +
                "    \"surname\": \"" + surname + "\",\n" +
                "    \"balance\": " + balance + "}";

        // Send post request
        connection.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
        wr.writeBytes(postJsonData);
        wr.flush();
        wr.close();

        BufferedReader in = new BufferedReader(
                new InputStreamReader(connection.getInputStream()));
        String output;
        StringBuffer response = new StringBuffer();

        while ((output = in.readLine()) != null) {
            response.append(output);
        }
        in.close();

        return connection.getHeaderField(2);
    }
    static String POSTTransaction(String urlToRead, int senderID, int receiverID, float amount) throws Exception {
        URL url = new URL(urlToRead);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("POST");
        connection.setRequestProperty("User-Agent", USER_AGENT);
        connection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        connection.setRequestProperty("Content-Type","application/json");

        String postJsonData = "{\"senderId\": " + senderID + ",\n" +
                "    \"receiverId\": " + receiverID + ",\n" +
                "    \"amount\": " + amount + "}";

        // Send post request
        connection.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
        wr.writeBytes(postJsonData);
        wr.flush();
        wr.close();

        BufferedReader in = new BufferedReader(
                new InputStreamReader(connection.getInputStream()));
        String output;
        StringBuffer response = new StringBuffer();

        while ((output = in.readLine()) != null) {
            response.append(output);
        }
        in.close();

        //System.out.println(connection.getHeaderField(2));

        return senderID + "/" + receiverID + "/" + amount + "/" + Arrays.asList(connection.getHeaderField(2).split("/")).get(2);
    }
    static int PUTBankAccount(String urlToRead, String name, String surname, float balance) throws Exception {
        URL url = new URL(urlToRead);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("PUT");
        connection.setRequestProperty("User-Agent", USER_AGENT);
        connection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        connection.setRequestProperty("Content-Type","application/json");

        String postJsonData = "{\"name\": \"" + name + "\",\n" +
                "    \"surname\": \"" + surname + "\",\n" +
                "    \"balance\": " + balance + "}";

        // Send post request
        connection.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
        wr.writeBytes(postJsonData);
        wr.flush();
        wr.close();

        int responseCode = connection.getResponseCode();

        BufferedReader in = new BufferedReader(
                new InputStreamReader(connection.getInputStream()));
        String output;
        StringBuffer response = new StringBuffer();

        while ((output = in.readLine()) != null) {
            response.append(output);
        }
        in.close();

        return responseCode;
    }
    static void DELETE(String urlToRead) throws Exception {
        URL url = new URL(urlToRead);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("DELETE");

        connection.setDoOutput(true);

        OutputStreamWriter out = new OutputStreamWriter(
                connection.getOutputStream());
        out.close();

        int responseCode = connection.getResponseCode();
        //System.out.println("nSending 'POST' request to URL : " + url);
        //System.out.println("Response Code : " + responseCode);
    }


    static void initMyWebserviceWithPOSTMethod(String urlToRead, int companyId, int bankId, String companyName, int insureEmployees, float reviewRating,
                                               String foundedAt, String founder, String city, String address,
                                               String email, String phoneNumber, float balance) throws Exception {
        URL url = new URL(urlToRead);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("POST");
        connection.setRequestProperty("User-Agent", USER_AGENT);
        connection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        connection.setRequestProperty("Content-Type","application/json");

        String postJsonData = "{\n" +
                "\"companyId\": " + companyId + ",\n" +
                "\"bankId\": " + bankId + ",\n" +
                "\"insureEmployees\": " + insureEmployees + ",\n" +
                "\"reviewRating\": " + reviewRating + ",\n" +
                "\"companyName\": \"" + companyName + "\",\n" +
                "\"foundedAt\": \"" + foundedAt + "\",\n" +
                "\"founder\": \"" + founder + "\",\n" +
                "\"city\": \"" + city + "\",\n" +
                "\"address\": \"" + address + "\",\n" +
                "\"email\": \"" + email + "\",\n" +
                "\"phoneNumber\": \"" + phoneNumber + "\",\n" +
                "\"balance\": " + balance +
                "\n}";

        // Send post request
        connection.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
        wr.writeBytes(postJsonData);
        wr.flush();
        wr.close();

        int responseCode = connection.getResponseCode();
        //System.out.println("nSending 'POST' request to URL : " + url);
        //System.out.println("Post Data : " + postJsonData);
        //System.out.println("Response Code : " + responseCode);
        //System.out.println(connection.getHeaderField(2));


        BufferedReader in = new BufferedReader(
                new InputStreamReader(connection.getInputStream()));
        String output;
        StringBuffer response = new StringBuffer();

        while ((output = in.readLine()) != null) {
            response.append(output);
        }
        in.close();

    }
    static int testURL(String urlAddress) throws Exception {
        URL url = new URL(urlAddress);

        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setRequestMethod("GET");
        connection.connect();

        return connection.getResponseCode();
    }

}
