package b1pod.commands.UserTags3;

import b1pod.core.Command;
import b1pod.core.ExecutionResult;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.sql.ResultSet;
import java.util.Arrays;

import static b1pod.Bot.getEmbedColor;
import static b1pod.commands.UserTags3.UserTags3.retrieve;
import static b1pod.commands.UserTags3.UserTags3.tagsEnabled;

public class TagList extends Command
{
    public TagList()
    {
        this.name = "List";
        this.description = "Lists all tags from this server.";
        this.triggers = Arrays.asList("list", "-l");
    }

    @Override
    protected ExecutionResult execute(MessageReceivedEvent event, String[] args) throws Exception
    {
        String guildId = event.getGuild().getId();
        if (!tagsEnabled(guildId)) return new ExecutionResult("failure", "Tags are not enabled in this server.");

        String query = "SELECT * FROM g" + guildId;
        ResultSet result = retrieve(query);
        result.last();
        int pages = (result.getRow() / 9) + 1;
        int page = 1, startIndex = 1;
        if (args.length == 3)
        {
            page = Integer.parseInt(args[2]);
            startIndex = page * 9 - 9;
        }
        if (startIndex < 0 || page > pages)
            return new ExecutionResult("failure", "Page does not exist.");

        EmbedBuilder listEmbed = new EmbedBuilder().setTitle("Tags").setColor(getEmbedColor())
                .setDescription("No tags found")
                .setFooter("Page: " + page + " of " + pages);
        int counter = startIndex;

        // Go to the correct starting position
        result.absolute(startIndex);

         while (result.next() && counter < startIndex + 9)
         {
             listEmbed.setDescription("");
             listEmbed.addField(result.getString("name"), result.getString("value"), true);
             counter++;
         }

        return new ExecutionResult(listEmbed.build());
    }
}
