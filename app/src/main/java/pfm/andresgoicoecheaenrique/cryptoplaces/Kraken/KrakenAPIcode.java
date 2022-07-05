package pfm.andresgoicoecheaenrique.cryptoplaces.Kraken;

import org.json.JSONObject;

import java.util.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.MessageDigest;

public class KrakenAPIcode {

    protected String petBalance (String key, String secret){
        String resul = "Error";
        try {

            System.out.println("|=========================================|");
            System.out.println("|         KRAKEN.COM JAVA TEST APP        |");
            System.out.println("|=========================================|");
            System.out.println();

            /*
             * PRIVATE REST API Examples
             */

            if (1 == 1) {
                String privateResponse = "";

                String privateEndpoint = "Balance";
                String privateInputParameters = "";

                privateResponse = QueryPrivateEndpoint(privateEndpoint,
                        privateInputParameters,
                        key,
                        secret);
                System.out.println("  \n"+privateResponse);
                /*JSONObject json = new JSONObject(privateResponse);
                System.out.println(json.toString());*/
                resul = privateResponse;
            }



            System.out.println("|=======================================|");
            System.out.println("| END OF PROGRAM - HAVE A GOOD DAY :)   |");
            System.out.println("|=======================================|");
            System.out.println("\n");


        } catch (Exception e) {
            System.out.println(e);
        }
        return resul;
    }

    /*
     * Private REST API Endpoints
     */

    public static String QueryPrivateEndpoint(String endPointName,
                                              String inputParameters,
                                              String apiPublicKey,
                                              String apiPrivateKey) {
        String responseJson = "";
        String baseDomain = "https://api.kraken.com";
        String privatePath = "/0/private/";

        String apiEndpointFullURL = baseDomain + privatePath + endPointName + "?" + inputParameters;
        String nonce = String.valueOf(System.currentTimeMillis());
        String apiPostBodyData = "nonce=" + nonce + "&" + inputParameters;

        String signature = CreateAuthenticationSignature(apiPrivateKey,
                privatePath,
                endPointName,
                nonce,
                apiPostBodyData);

        // CREATE HTTP CONNECTION
        try {
            HttpsURLConnection httpConnection = null;

            URL apiUrl = new URL(apiEndpointFullURL);
            httpConnection = (HttpsURLConnection) apiUrl.openConnection();
            httpConnection.setRequestMethod("GET");
            httpConnection.setRequestProperty("API-Key", apiPublicKey);
            httpConnection.setRequestProperty("API-Sign", signature);
            httpConnection.setDoOutput(true);
            DataOutputStream os = new DataOutputStream(httpConnection.getOutputStream());
            os.writeBytes(apiPostBodyData);
            os.flush();
            os.close();
            BufferedReader br = null;

            // GET JSON RESPONSE DATA
            br = new BufferedReader(new InputStreamReader((httpConnection.getInputStream())));
            String line;
            while ((line = br.readLine()) != null) {
                responseJson += line;
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return responseJson;
    }

    /*
     * Authentication Algorithm
     */

    public static String CreateAuthenticationSignature(String apiPrivateKey,
                                                       String apiPath,
                                                       String endPointName,
                                                       String nonce,
                                                       String apiPostBodyData) {

        try {
            // GET 256 HASH
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update((nonce + apiPostBodyData).getBytes());
            byte[] sha256Hash = md.digest();

            // GET 512 HASH
            Mac mac = Mac.getInstance("HmacSHA512");
            mac.init(new SecretKeySpec(Base64.getDecoder().decode(apiPrivateKey.getBytes()), "HmacSHA512"));
            mac.update((apiPath + endPointName).getBytes());

            // CREATE API SIGNATURE
            String signature = new String(Base64.getEncoder().encodeToString(mac.doFinal(sha256Hash)));

            return signature;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}
