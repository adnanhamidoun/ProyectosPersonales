package com.mycompany.socketchatV02;

import org.json.*;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class Apis {
    public String getClasificacionSpain(){
        String classificationString ="";
        try {
            // Reemplaza con tu clave de API de Football-Data.org
            String apiKey = ""; // Aquí va tu API Key
            String urlString = "https://api.football-data.org/v4/competitions/PD/standings"; // URL de la API de LaLiga

            // Crear un objeto URL
            URL url = new URL(urlString);

            // Abrir la conexión HTTP
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET"); // Solicitud GET
            connection.setRequestProperty("X-Auth-Token", apiKey); // Autenticación con la API Key

            // Leer la respuesta de la API
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            // Leer línea por línea y agregarla al StringBuilder
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // Llamar al método para procesar la respuesta JSON y extraer la clasificación
            classificationString = displayClassification(response.toString());



        } catch (Exception e) {
            e.printStackTrace();
        }

        return classificationString;
    }

    /**
     * Método que procesa el JSON y devuelve la clasificación de LaLiga como un String.
     * @param jsonResponse JSON de la API con los datos de la clasificación.
     * @return Un String con la clasificación de LaLiga.
     */
    private static String displayClassification(String jsonResponse) {
        // Usamos un StringBuilder para acumular el texto
        StringBuilder result = new StringBuilder();

        // Convertir la respuesta JSON en un objeto JSONObject
        JSONObject jsonObject = new JSONObject(jsonResponse);
        JSONArray standingsArray = jsonObject.getJSONArray("standings");

        // La clasificación está en el primer grupo (en este caso, solo hay un grupo para LaLiga)
        JSONArray table = standingsArray.getJSONObject(0).getJSONArray("table");

        // Acumulamos la clasificación en el StringBuilder
        result.append("Clasificación de LaLiga:\n");
        for (int i = 0; i < table.length(); i++) {
            // Extraemos los datos sin usar el objeto "team"
            JSONObject team = table.getJSONObject(i);
            int position = team.getInt("position");
            String teamName = team.getJSONObject("team").getString("name");
            int playedGames = team.getInt("playedGames");
            int won = team.getInt("won");
            int drawn = team.getInt("draw");
            int lost = team.getInt("lost");
            int goalsFor = team.getInt("goalsFor");
            int goalsAgainst = team.getInt("goalsAgainst");
            int goalDifference = team.getInt("goalDifference");
            int points = team.getInt("points");

            // Acumulamos la información detallada de cada equipo en el StringBuilder
            result.append("Posición: ").append(position).append("\n");
            result.append("Equipo: ").append(teamName).append("\n");
            result.append("Partidos Jugados: ").append(playedGames).append("\n");
            result.append("Victorias: ").append(won).append("\n");
            result.append("Empates: ").append(drawn).append("\n");
            result.append("Derrotas: ").append(lost).append("\n");
            result.append("Goles a Favor: ").append(goalsFor).append("\n");
            result.append("Goles en Contra: ").append(goalsAgainst).append("\n");
            result.append("Diferencia de Goles: ").append(goalDifference).append("\n");
            result.append("Puntos: ").append(points).append("\n");
            result.append("-----------------------------------\n");
        }

        // Convertimos el StringBuilder a String y lo retornamos
        return result.toString();
    }


    public String getCriptoMonedas() {
        // URL de la API de CoinGecko para obtener las 50 principales criptomonedas
        String apiUrl = "https://api.coingecko.com/api/v3/coins/markets?vs_currency=usd&order=market_cap_desc&per_page=50&page=1";
        String cryptoList = "";
        try {
            String cryptoData = getCryptoData(apiUrl);


            cryptoList = displayCryptoList(cryptoData);


        } catch (Exception e) {
            e.printStackTrace();
        }

        return cryptoList;

    }


    private String getCryptoData(String apiUrl) throws Exception {
        // Crear la conexión a la API
        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        // Leer la respuesta de la API
        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) { // Código 200
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            return response.toString();
        } else {
            throw new Exception("Error en la solicitud: Código " + responseCode);
        }
    }

    private String displayCryptoList(String jsonResponse) {
        // Convertir la respuesta JSON en un objeto JSONArray
        JSONArray jsonArray = new JSONArray(jsonResponse);

        // Usar StringBuilder para construir la respuesta en formato String
        StringBuilder result = new StringBuilder();
        result.append("Lista de las 50 principales criptomonedas:\n");

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject crypto = jsonArray.getJSONObject(i);

            // Extraer el nombre, símbolo y precio de cada criptomoneda
            String name = crypto.getString("name");
            String symbol = crypto.getString("symbol");
            double price = crypto.getDouble("current_price");

            // Agregar la información de la criptomoneda al StringBuilder
            result.append((i + 1) + ". " + name + " (" + symbol.toUpperCase() + ")\n");
            result.append("   Precio: $" + price + "\n");
            result.append("-------------------------------\n");
        }

        // Convertir el StringBuilder a String y devolverlo
        return result.toString();
    }


    public String getNoticias() {
        String apiKey = ""; // Reemplaza con tu API Key de GNews
        String apiUrl = "https://gnews.io/api/v4/top-headlines?token=" + apiKey + "&lang=es";
        String newsData = "";
        try {
            newsData = getNewsData(apiUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return newsData;
    }

    private static String getNewsData(String apiUrl) throws Exception {
        // Crear la conexión a la API
        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        // Leer la respuesta de la API
        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) { // Código 200
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // Procesar y formatear las noticias
            return formatNewsData(response.toString());
        } else {
            throw new Exception("Error en la solicitud: Código " + responseCode);
        }
    }

    private static String formatNewsData(String jsonResponse) {
        StringBuilder formattedNews = new StringBuilder();

        if (jsonResponse.contains("\"articles\":")) {
            String[] articles = jsonResponse.split("\"articles\":")[1].split("\\},\\{");
            for (String article : articles) {
                if (article.contains("\"title\":") && article.contains("\"content\":")) {
                    String title = article.split("\"title\":\"")[1].split("\",")[0];
                    String content = article.split("\"content\":\"")[1].split("\",")[0];
                    formattedNews.append("Título: ").append(title).append("\n");
                    formattedNews.append("Contenido: ").append(content).append("\n");
                    formattedNews.append("---------------------------\n");
                }
            }
        } else {
            formattedNews.append("No se encontraron artículos.");
        }

        return formattedNews.toString();
    }
}
