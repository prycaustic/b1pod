package b1pod.Commands;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

public class UserTags extends ListenerAdapter
{
    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event)
    {
        if (event.getMessage().getContentRaw().startsWith("/tag") && !event.getMessage().getAuthor().isBot())
        {
            String[] args = event.getMessage().getContentRaw().split("\\s+");

            if (args[1].equalsIgnoreCase("add") && args[3].startsWith("https://"))
            {
                String name = args[2].replace("_", " ");

                try
                {
                    // Get JSON file
                    JSONObject json = parseJSONFile("gifs.json");
                    JSONArray gifArray = new JSONArray();
                    try
                    {
                        gifArray = json.getJSONArray(name);
                    }
                    catch (Exception e)
                    {
                        json.put(name, gifArray);
                    }
                    gifArray.put(args[3]);

                    // Write to JSON file
                    FileWriter file = new FileWriter("gifs.json");
                    file.write(json.toString());
                    file.flush();

                    event.getMessage().addReaction("\u2705").queue();
                } catch (IOException | JSONException e)
                {
                    e.printStackTrace();
                    event.getMessage().reply(e.toString()).mentionRepliedUser(false).queue();
                }
            }
            else if (args[1].equalsIgnoreCase("list"))
            {
                try
                {
                    JSONObject tags = parseJSONFile("gifs.json");
                    var keys = tags.keys();
                    StringBuilder list = new StringBuilder("List of tags:\n");

                    while (keys.hasNext())
                    {
                        list.append(keys.next()).append("\n");
                    }

                    event.getMessage().reply(list.toString()).mentionRepliedUser(false).queue();
                } catch (IOException | JSONException e)
                {
                    e.printStackTrace();
                }
            }
        }
        else if (event.getMessage().getContentRaw().startsWith("/gif") && !event.getMessage().getAuthor().isBot())
        {
            event.getMessage().reply("Please use /tag instead").mentionRepliedUser(false).queue();
        }
        else if (!event.getMessage().getAuthor().isBot())
        {
            String name = event.getMessage().getContentRaw().toLowerCase(Locale.ROOT);

            try
            {
                // Get JSON file
                JSONObject json = parseJSONFile("gifs.json");
                JSONArray gifArray = json.getJSONArray(name);
                String link = gifArray.getString((int) (Math.random() * gifArray.length()));

                event.getChannel().sendMessage(link).queue();
            }
            catch (Exception ignored) { }
        }
    }

    public static JSONObject parseJSONFile(String filename) throws IOException, JSONException
    {
        InputStream in = new FileInputStream(filename);
        String jsonString = new String(in.readAllBytes(), StandardCharsets.UTF_8);

        if (jsonString.startsWith("{"))
        {
            return new JSONObject(jsonString);
        }
        else
        {
            return new JSONObject();
        }
    }
}
