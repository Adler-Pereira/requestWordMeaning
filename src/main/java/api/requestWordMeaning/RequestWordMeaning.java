package api.requestWordMeaning;


import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.URLDecoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RequestWordMeaning {
    public Map<String, Object> requestMeaning(String word){
        final String url = "https://api.dicionario-aberto.net/word/" + word;
        String xml = "";
        String decodedXml;
        String meaning = "";

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Accept", "application/json")
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JSONArray jsonArrayResponse = new JSONArray(response.body());
                JSONObject jsonResponse = jsonArrayResponse.getJSONObject(0);
                xml = jsonResponse.getString("xml");
            } else{
                return createResponse(500, "Erro: " + response.statusCode());
            }
        } catch (Exception e){
            e.printStackTrace();
            return createResponse(500, "Erro: " + e.getMessage());
        }

        decodedXml = URLDecoder.decode(xml, StandardCharsets.UTF_8);

        Pattern pattern = Pattern.compile("<def>(.*?)</def>", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(decodedXml);

        if (matcher.find()){
            meaning = matcher
                    .group(1)
                    .replaceAll("\n", " ")
                    .replaceAll("\r", "")
                    .replaceAll("_", "")
                    .trim();
        }

        return createResponse(200, meaning);
    }

    private Map<String, Object> createResponse(int statusCode, String body) {
        Map<String, Object> response = new HashMap<>();
        response.put("statusCode", statusCode);
        response.put("body", body); // Apenas a palavra, sem JSON extra

        // Cabeçalhos para permitir CORS
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "text/plain"); // Define o retorno como texto puro
        headers.put("Access-Control-Allow-Origin", "*");
        headers.put("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        headers.put("Access-Control-Allow-Headers", "Content-Type");

        response.put("headers", headers);
        return response;
    }
}
