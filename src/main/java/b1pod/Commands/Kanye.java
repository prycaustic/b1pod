package b1pod.Commands;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Kanye extends ListenerAdapter
{
    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event)
    {
        if (event.getMessage().getContentRaw().startsWith("kanye") && !event.getAuthor().isBot())
        {
            try
            {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("https://api.kanye.rest/")).build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                String quote = new JSONObject(response.body()).getString("quote");

                event.getChannel().sendMessage(quote).queue();
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
}
