package com.example.uploadingfiles;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.UnknownHostException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class RandomUser {

    public static User getRandomUser() throws ProtocolException, IOException {
        RestTemplate restTemplate = new RestTemplate();
        String APIinString = null;
        try {
            URL ApiURL = new URL("https://randomuser.me/api/?results=1"); // tem que estar em URL porque se estiver em string nao le o statudos do HTTP

            HttpURLConnection http = (HttpURLConnection) ApiURL.openConnection();
            int statusCode = http.getResponseCode();
            System.out.println("Status Code: " + statusCode + "!");
            APIinString = ApiURL.toString();

            if (statusCode != 200) {
                //return null;
            }

        } catch (UnknownHostException e) {
            System.out.println("Nao há acesso a net");
            return null;
        }

        
        // Fetch JSON response as String wrapped in ResponseEntity
        ResponseEntity<String> response = restTemplate.getForEntity(APIinString, String.class);

        String JsonUser = response.getBody();

        JSONObject JsonObject = new JSONObject(JsonUser);
        JSONArray JsonResponse = JsonObject.getJSONArray("results");

        User user = new User();

        JSONObject getArrPosition_JsonUser = JsonResponse.getJSONObject(0);

        String firstName = getArrPosition_JsonUser.getJSONObject("name").getString("first");
        String lastName = getArrPosition_JsonUser.getJSONObject("name").getString("last");
        String PhotoURL = getArrPosition_JsonUser.getJSONObject("picture").getString("thumbnail");

        System.out.println("First Name of User: " + firstName);
        System.out.println("Last Name of User:" + lastName);

        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPhotoURL(PhotoURL);

        return user;
    }
}
