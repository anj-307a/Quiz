import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
public class QuizLeaderboard {
    static final String BASE_URL = "https://devapigw.vidalhealthtpa.com/srm-quiz-task";
    static final String reg = "2024CS101"; 
    public static void main(String[] args) throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        Set<String> seen = new HashSet<>();
        Map<String, Integer> scores = new HashMap<>();
        for (int poll = 0; poll < 10; poll++) {
            System.out.println("Polling: " + poll);
            String url = BASE_URL + "/quiz/messages?regNo=" + reg + "&poll=" + poll;
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();
            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());

            String body = response.body();
            String[] parts = body.split("\\{");
            for (String part : parts) {
                if (part.contains("roundId") &&
                    part.contains("participant") &&
                    part.contains("score")) {
                    try {
                        String roundId = part.split("\"roundId\":\"")[1].split("\"")[0];
                        String participant = part.split("\"participant\":\"")[1].split("\"")[0];
                        int score = Integer.parseInt(
                                part.split("\"score\":")[1].split("[,}]")[0].trim()
                        );

                        String key = roundId + "_" + participant;
                        if (seen.contains(key)) {
                            System.out.println("Duplicate skipped: " + key);
                            continue;
                        }

                        seen.add(key);
                        scores.put(participant,
                                scores.getOrDefault(participant, 0) + score);

                        System.out.println("Added: " + key + " -> " + score);

                    } catch (Exception e) {
                    }
                }
            }

            if (poll < 9) Thread.sleep(5000);
        }
        List<Map<String, Object>> leaderboard = new ArrayList<>();

        for (Map.Entry<String, Integer> entry : scores.entrySet()) {
            Map<String, Object> obj = new HashMap<>();
            obj.put("participant", entry.getKey());
            obj.put("totalScore", entry.getValue());
            leaderboard.add(obj);
        }
        leaderboard.sort((a, b) ->
                (int) b.get("totalScore") - (int) a.get("totalScore"));
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("{\"regNo\":\"").append(reg).append("\",\"leaderboard\":[");

        for (int i = 0; i < leaderboard.size(); i++) {
            Map<String, Object> p = leaderboard.get(i);
            jsonBuilder.append("{\"participant\":\"")
                    .append(p.get("participant"))
                    .append("\",\"totalScore\":")
                    .append(p.get("totalScore"))
                    .append("}");

            if (i != leaderboard.size() - 1) jsonBuilder.append(",");
        }

        jsonBuilder.append("]}");

        System.out.println("\nFinal Leaderboard: " + leaderboard);
        HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/quiz/submit"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBuilder.toString()))
                .build();

        HttpResponse<String> postResponse =
                client.send(postRequest, HttpResponse.BodyHandlers.ofString());

        System.out.println("\nFinal Response: " + postResponse.body());
    }
}