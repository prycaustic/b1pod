package b1pod.commands.UserTags3;

import b1pod.core.Command;
import b1pod.core.ExecutionResult;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.sql.ResultSet;
import java.util.Arrays;

import static b1pod.Bot.getEmbedColor;
import static b1pod.Bot.getPrefix;
import static b1pod.commands.UserTags3.UserTags3.*;

public class TagList extends Command
{
    public TagList(Command parent)
    {
        this.parent = parent;
        this.name = "List";
        this.syntax = "``" + getPrefix() + "tag list``";
        this.description = "Lists all tags from this server.";
        this.triggers = Arrays.asList("list", "-l");
    }

    @Override
    protected ExecutionResult execute(MessageReceivedEvent event, String[] args) throws Exception
    {
        String guildId = event.getGuild().getId();
        if (!tagsEnabled(getConn(), guildId)) return new ExecutionResult("failure", "Tags are not enabled in this server.");
        String query = "SELECT * FROM g" + guildId;
        ResultSet result = retrieve(getConn(), query);
        EmbedBuilder listEmbed = new EmbedBuilder().setTitle("Tags").setColor(getEmbedColor());

        if (result.next())
        {
            while (result.next())
            {
                listEmbed.addField(result.getString("name"), result.getString("value"), true);
            }
        }
        else
        {
            listEmbed.setDescription("No tags found.");
        }

        return new ExecutionResult(listEmbed.build());
    }
}
