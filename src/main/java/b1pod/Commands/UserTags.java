package b1pod.Commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
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
        String content = event.getMessage().getContentRaw();

        if (content.startsWith("/tag") && !event.getMessage().getAuthor().isBot())
        {
            String[] args = content.split("\\s+");

            try
            {
                if (args[1].equalsIgnoreCase("add") && args[3].startsWith("https://"))
                {
                    String name = args[2].replace("_", " ");

                    try
                    {
                        // Get JSON file
                        JSONObject json = parseJSONFile();
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
                        JSONObject tags = parseJSONFile();
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
                else if (args[1].equalsIgnoreCase("remove"))
                {
                    if (args[2] != null)
                    {
                        JSONObject tags = parseJSONFile();
                        String name = args[2].replace("_", " ");

                        if (!tags.isNull(name))
                        {
                            JSONArray tagArray = tags.getJSONArray(name);

                            if (tagArray.length() > 1)
                            {
                                String[] links = new String[tagArray.length()];
                                EmbedBuilder tagList = new EmbedBuilder()
                                        .setTitle("List of links for tag " + name + ":")
                                        .setDescription("Reply with the number of which link you would like to remove." +
                                                " Type \"cancel\" to stop.");

                                for (int i = 0; i < tagArray.length(); i++)
                                {
                                    tagList.addField(String.valueOf(i), tagArray.getString(i), false);
                                    links[i] = tagArray.getString(i);
                                }

                                event.getMessage().replyEmbeds(tagList.build()).mentionRepliedUser(false).queue();
                                RemoveListener(event, name, tags, links);
                            }
                            else
                            {
                                tags.remove(name);

                                // Write to JSON file
                                FileWriter file = new FileWriter("gifs.json");
                                file.write(tags.toString());
                                file.flush();

                                event.getMessage().addReaction("\u2705").queue();
                            }
                        }
                        else
                        {
                            event.getMessage().reply("Could not find tag.").mentionRepliedUser(false).queue();
                        }
                    }
                    else
                    {
                        event.getMessage().reply("Please enter a tag name.").mentionRepliedUser(false).queue();
                    }
                }
                else if (args[1].equalsIgnoreCase("help"))
                {
                    EmbedBuilder helpEmbed = new EmbedBuilder()
                            .setTitle("User Tags")
                            .setDescription("Create custom tags with gifs, videos, images, whatever just needs to be a link.")
                            .addField("Help", "Shows this help message.", false)
                            .addField("Add", "``/tag add <name> <link>``\nTag names cannot contain spaces, please use \"_\".", false)
                            .addField("Remove", "``/tag remove <name>``\n", false)
                            .addField("List", "``/tag list``\nReturns a list of useable tags.", false)
                            .addField("Usage", "Just type the name of a tag into chat.", false);

                    event.getMessage().replyEmbeds(helpEmbed.build()).mentionRepliedUser(false).queue();
                }
            }
            catch (Exception e)
            {
                event.getMessage().reply("ERROR: Try /tag help for more help.").mentionRepliedUser(false).queue();
                e.printStackTrace();
            }
        }
        else if (content.startsWith("/gif") && !event.getMessage().getAuthor().isBot())
        {
            event.getMessage().reply("Please use /tag instead").mentionRepliedUser(false).queue();
        }
        else if (!event.getMessage().getAuthor().isBot())
        {
            String name = content.toLowerCase(Locale.ROOT);

            try
            {
                // Get JSON file
                JSONObject json = parseJSONFile();
                JSONArray gifArray = json.getJSONArray(name);
                String link = gifArray.getString((int) (Math.random() * gifArray.length()));

                event.getChannel().sendMessage(link).queue();
            }
            catch (Exception ignored) { }
        }
    }

    private JSONObject parseJSONFile() throws IOException, JSONException
    {
        InputStream in = new FileInputStream("gifs.json");
        String jsonString = new String(in.readAllBytes(), StandardCharsets.UTF_8);

        if (jsonString.startsWith("{"))
        {
            return new JSONObject(jsonString);
        } else
        {
            return new JSONObject();
        }
    }
    
    private void RemoveListener(GuildMessageReceivedEvent event1, String name, JSONObject json, String[] links)
    {
        ListenerAdapter remove = new ListenerAdapter()
        {
            @Override
            public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event2)
            {
                if (event2.getChannel() == event1.getChannel() && event2.getAuthor() == event1.getAuthor())
                {
                    String content = event2.getMessage().getContentRaw();

                    try
                    {
                        int index = Integer.parseInt(content);

                        if (index > -1 && index < links.length + 1)
                        {
                            JSONArray array = new JSONArray();

                            for (int i = 0; i < links.length; i++)
                            {
                                if (i != Integer.parseInt(content))
                                {
                                    array.put(links[i]);
                                }
                            }

                            try
                            {
                                json.put(name, array);

                                // Write to JSON file
                                FileWriter file = new FileWriter("gifs.json");
                                file.write(json.toString());
                                file.flush();
                            } catch (IOException | JSONException e)
                            {
                                e.printStackTrace();
                            }

                            event2.getMessage().addReaction("\u2705").queue();
                            event2.getJDA().removeEventListener(this);
                        }
                        else
                        {
                            event2.getMessage().addReaction("\u274c").queue();
                        }
                    }
                    catch (NumberFormatException e)
                    {
                        if (content.equalsIgnoreCase("cancel"))
                        {
                            event2.getMessage().addReaction("\u2705").queue();
                            event2.getJDA().removeEventListener(this);
                        }
                        else
                        {
                            event2.getMessage().addReaction("\u274c").queue();
                        }
                    }
                }
            }
        };

        event1.getJDA().addEventListener(remove);
    }
}
