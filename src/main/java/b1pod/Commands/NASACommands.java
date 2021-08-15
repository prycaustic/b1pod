package b1pod.Commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class NASACommands extends ListenerAdapter
{
    private final String API_KEY = "UW5FOx1OWsA3Nw0zggEtytnAUvGqCKTg3c8nqHW9";

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event)
    {
        if (event.getMessage().getContentRaw().startsWith("/nasa") || event.getMessage().getContentRaw().startsWith("/n")  && !event.getAuthor().isBot())
        {
            boolean builderReady = false;
            String[] args = event.getMessage().getContentRaw().split("\\s+");
            EmbedBuilder NASAEmbed = new EmbedBuilder()
                    .setColor(0x105bd8)
                    .setFooter("via https://www.nasa.gov/", "https://raw.githubusercontent.com/bruffridge/nasawds/develop/src/theme/img/favicons/favicon-72.png");

            HttpClient client = HttpClient.newHttpClient();

            try
            {
                switch (args[1])
                {
                    case "apod":
                        String apodURL = "https://api.nasa.gov/planetary/apod?api_key=" + API_KEY;
                        JSONObject obj;
                        if (args.length < 3)
                        {
                            HttpResponse<String> response = client.send(HttpRequest.newBuilder()
                                    .uri(URI.create(apodURL + "&count=1"))
                                    .method("GET", HttpRequest.BodyPublishers.noBody())
                                    .build(), HttpResponse.BodyHandlers.ofString());
                            obj = new JSONObject(response.body().substring(1, response.body().length() - 1));
                        }
                        else if (args[2].equalsIgnoreCase("today"))
                        {
                            obj = getJSONObject(client, apodURL);
                        }
                        else
                        {
                            obj = getJSONObject(client, apodURL + "&date=" + args[1]);
                        }
                        String desc = obj.getString("explanation");

                        NASAEmbed.setTitle("Astronomy Photo of the Day", obj.getString("hdurl"))
                                .setDescription(desc.substring(0, Math.min(desc.length(), 200)) + "...")
                                .addField(obj.getString("title"), "By: " + obj.optString("copyright", "unknown"), true)
                                .addField("Date", obj.getString("date"), true)
                                .setImage(obj.getString("url"));
                        builderReady = true;
                        break;
                    case "neo":

                        break;
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
                // event.getMessage().reply(e.toString()).mentionRepliedUser(false).queue();
            }

            if (builderReady)
            {
                event.getMessage().replyEmbeds(NASAEmbed.build()).mentionRepliedUser(false).queue();
            }
            else
            {
                event.getMessage().reply("Error").mentionRepliedUser(false).queue();
            }
        }
    }

    private JSONObject getJSONObject(HttpClient client, String url) throws JSONException, IOException, InterruptedException
    {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        return new JSONObject(response.body());
    }
}
