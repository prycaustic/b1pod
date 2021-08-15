package b1pod.Commands;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.json.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.List;

public class HypeManListener extends ListenerAdapter
{
    private static final List<String> DICT = Arrays.asList("owned", "your mom", "get fucked", "bam", "mic drop", "gratata");
    private static final String API_KEY = "NYGW2FB6S88N";
    private static final int LIMIT = 16;

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event)
    {
        if (DICT.contains(event.getMessage().getContentRaw()))
        {
            int rand = (int) Math.floor(Math.random() * LIMIT);
            JSONObject results = getTenorSearchResults(event.getMessage().getContentRaw());
            if (results != null)
            {
                try
                {
                    String randomURL = results.getJSONArray("results").getJSONObject(rand).getString("url");
                    event.getChannel().sendMessage(randomURL).queue();
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    public static JSONObject getTenorSearchResults(String query)
    {
        final String uri = String.format("https://g.tenor.com/v1/search?q=%1$s&key=%2$s&limit=%3$s", query, API_KEY, LIMIT);

        try
        {
            return getRequest(uri);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }

    private static JSONObject getRequest(String uri)
    {
        try
        {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(uri.replace(" ", "%20"))).method("GET", HttpRequest.BodyPublishers.noBody()).build();
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            return new JSONObject(response.body());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }
}
