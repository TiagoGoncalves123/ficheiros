/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.uploadingfiles;

import java.io.FileWriter;
import java.io.IOException;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.PathVariable;

public class JsonUtils {

    public static void CreateJSON(@PathVariable String SHA256, String Base64) throws IOException {

        try {
            //Criar um objeto JSON
            User user = RandomUser.getRandomUser();
            JSONObject JsonObject = new JSONObject();

            // Adicionar valores ao objeto json
            JsonObject.put("SHA256", SHA256);
           // JsonObject.put("Base64", Base64);
            JsonObject.put("First Name:", user.getFirstName());
            JsonObject.put("Last Name:", user.getLastName());

            System.out.println(JsonObject);

            // Criar um objeto fileWriter com o nome "text.json"
            FileWriter ficheiro = new FileWriter("text.json");
            // Colocar no ficheiro fileWriter o objeto jsonObject
            ficheiro.write(JsonObject.toString());
            ficheiro.close();
        } catch (IOException | JSONException e) {
        }
    }
}
