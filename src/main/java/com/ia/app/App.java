package com.ia.app;

import com.google.gson.Gson;
import com.ia.app.models.*;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) {

        String urlBase = "https://v3.football.api-sports.io";
        ArrayList<Country> countries = new ArrayList<>();
        //countries.add(new Country(26, "Argentina"));
        //countries.add(new Country(30,"Peru"));
        countries.add(new Country(2383,"Chile"));
        //countries.add(new Country(5529,"Canada"));
       //countries.add(new Country(16,"Mexico"));
        //countries.add(new Country(2382,"Ecuador"));
        //countries.add(new Country(2379,"Venezuela"));
       //countries.add(new Country(2385,"Jamaica"));
        //countries.add(new Country(2384,"USA"));
        //countries.add(new Country(7,"Uruguay"));
        //countries.add(new Country(11,"Panama"));
        //countries.add(new Country(2381,"Bolivia"));
        //countries.add(new Country(6,"Brazil"));
        //countries.add(new Country(8,"Colombia"));
        //countries.add(new Country(2380,"Paraguay")); // o 16941
        //countries.add(new Country(29,"Costa Rica"));

        int last=30;
        String apiKey="0940599c5922c45c912714e9c3bd0780";
        for (Country country : countries) {
            String parametros = "last="+last+"&team=" + country.getId();
            try {
                URL urlFixtures = new URL(urlBase + "/fixtures" + "?" + parametros);

                //Thread.sleep(90 * 1000);
                // Abrir conexión HTTP
                HttpURLConnection conexion = (HttpURLConnection) urlFixtures.openConnection();

                // Establecer el método de solicitud (GET en este caso)
                conexion.setRequestMethod("GET");

                // Establecer encabezados
                conexion.setRequestProperty("x-rapidapi-key", apiKey);
                conexion.setRequestProperty("x-rapidapi-host", "v3.football.api-sports.io");

                // Leer la respuesta
                BufferedReader in = new BufferedReader(new InputStreamReader(conexion.getInputStream()));
                String inputLine;
                StringBuffer respuesta = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    respuesta.append(inputLine);
                }
                in.close();

                String jsonResponse = respuesta.toString();
                Gson gson = new Gson();
                FixtureResponse fixtureResponse = gson.fromJson(jsonResponse, FixtureResponse.class);
                // Cerrar la conexión
                conexion.disconnect();
                int i=0;
                for(FixtureItem fixtureItem : fixtureResponse.getResponse()){
                    if(!fixtureItem.getFixture().getStatus().getShortt().equals("CANC")) {
                        Thread.sleep(8 * 1000);
                        URL urlStatistics = new URL(urlBase + "/fixtures/statistics" + "?" + "fixture=" + fixtureItem.getFixture().getId() + "&team=" + country.getId());

                        // Abrir conexión HTTP
                        HttpURLConnection con = (HttpURLConnection) urlStatistics.openConnection();

                        // Establecer el método de solicitud (GET en este caso)
                        con.setRequestMethod("GET");

                        // Establecer encabezados
                        con.setRequestProperty("x-rapidapi-key",apiKey);
                        con.setRequestProperty("x-rapidapi-host", "v3.football.api-sports.io");

                        // Leer la respuesta
                        BufferedReader ins = new BufferedReader(new InputStreamReader(con.getInputStream()));
                        String inputLinee;
                        StringBuffer respuestaa = new StringBuffer();
                        while ((inputLinee = ins.readLine()) != null) {
                            respuestaa.append(inputLinee);
                        }
                        ins.close();

                        System.out.println("Respuesta: \n" + respuestaa.toString());

                        String jsonResponsee = respuestaa.toString();

                        Gson gsonn = new Gson();
                        StatisticsResponse statisticsResponse = gsonn.fromJson(jsonResponsee, StatisticsResponse.class);

                        if (statisticsResponse.getResponse().isEmpty()) {
                            continue;
                        }
                        StatisticsItem statisticsItem = statisticsResponse.getResponse().get(0);
                        List<Statistic> statistics = statisticsItem.getStatistics();
                        boolean esLocal;
                        String equipoVs;
                        if (country.getNombre().equals(fixtureItem.getTeams().getHome().getName())) {
                            esLocal = true;
                            equipoVs = fixtureItem.getTeams().getAway().getName();
                        } else {
                            esLocal = false;
                            equipoVs = fixtureItem.getTeams().getHome().getName();
                        }

                        // Escribir datos en el archivo CSV
                        String resultado;
                        if(esLocal) {
                            if (fixtureItem.getFixture().getStatus().getShortt().equals("FT")) {

                                if (Integer.parseInt(fixtureItem.getGoals().getHome()) > Integer.parseInt(fixtureItem.getGoals().getAway())) {
                                    resultado = "1";
                                } else if (Integer.parseInt(fixtureItem.getGoals().getHome()) == Integer.parseInt(fixtureItem.getGoals().getAway())) {
                                    resultado = "0.5";
                                } else {
                                    resultado = "0";
                                }
                            }else{
                                int penaltysHome = Integer.parseInt(fixtureItem.getScore().getPenalty().getHome());
                                int penaltysAway = Integer.parseInt(fixtureItem.getScore().getPenalty().getAway());

                                if (penaltysHome > penaltysAway) {
                                    resultado = "1";
                                } else {
                                    resultado = "0";
                                }
                            }
                        }else {

                            if (fixtureItem.getFixture().getStatus().getShortt().equals("FT")) {

                                if (Integer.parseInt(fixtureItem.getGoals().getAway()) > Integer.parseInt(fixtureItem.getGoals().getHome())) {
                                    resultado = "1";
                                } else if (Integer.parseInt(fixtureItem.getGoals().getAway()) == Integer.parseInt(fixtureItem.getGoals().getHome())) {
                                    resultado = "0.5";
                                } else {
                                    resultado = "0";
                                }
                            } else {
                                int penaltysHome = Integer.parseInt(fixtureItem.getScore().getPenalty().getHome());
                                int penaltysAway = Integer.parseInt(fixtureItem.getScore().getPenalty().getAway());

                                if (penaltysHome < penaltysAway) {
                                    resultado = "1";
                                } else {
                                    resultado = "0";
                                }
                            }
                        }
                        String nameLeague = fixtureItem.getLeague().getName();
                        String importanciaLiga = "";
                        String round = fixtureItem.getLeague().getRound();
                        switch (nameLeague) {
                            case "Friendlies":
                                importanciaLiga = "5";
                                break;
                            case "World Cup - Qualification South America":
                                importanciaLiga = "25";
                                break;
                            case "Copa America":
                                if (round.contains("Group") || round.contains("Round of 16") || round.contains("Quarter-finals")) {
                                    importanciaLiga = "35";
                                } else if (round.contains("Final") || round.contains("Semi-finals")) {
                                    importanciaLiga = "40";
                                }
                                break;
                            case "CONMEBOL - UEFA Finalissima":
                                importanciaLiga = "50";
                                break;
                            case "World Cup":
                                if (round.contains("Group") || round.contains("Round of 16") || round.contains("Quarter-finals")) {
                                    importanciaLiga = "50";
                                } else if (round.contains("Final") || round.contains("Semi-finals")) {
                                    importanciaLiga = "60";
                                }
                                break;
                        }

                        // numeroDeGolesLocales,NumeroDeGolesVisitantes,resultado,TipoLiga,ImportanciaLiga,Fecha
                        writeCSV(i, "games.csv", country.getNombre(), String.valueOf(esLocal), equipoVs,
                                statistics, fixtureItem.getGoals().getHome(), fixtureItem.getGoals().getAway(), resultado, nameLeague, importanciaLiga, fixtureItem.getFixture().getDate());
                        i++;
                    }
                }

            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }



    public static void writeCSV(int i,String fileName,String nombreEquipo, String esLocal, String equipoContrario, List<Statistic> statistics,String goalsHome, String goalsAway, String resultado, String nameLeague,String importanciaLiga, String date ) {
        try (FileWriter writer = new FileWriter(fileName, true)) { // Abrir en modo de adjuntar
            // Concatenar todas las estadísticas en una sola fila
            StringBuilder rowBuilder = new StringBuilder();
            rowBuilder.append(i).append(",").append(nombreEquipo).append(",").append(esLocal).append(",").append(equipoContrario).append(",");
            statistics.remove(statistics.size()-1);
            for (Statistic stat : statistics) {
                rowBuilder.append(stat.getValue()).append(",");
            }
            rowBuilder.append(goalsHome).append(",").append(goalsAway).append(",").append(resultado).append(",").append(nameLeague).append(",").append(importanciaLiga).append(",").append(date);
            // Escribir la fila de estadísticas en el archivo
            writer.append(rowBuilder.toString());
            writer.append("\n");
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    }

