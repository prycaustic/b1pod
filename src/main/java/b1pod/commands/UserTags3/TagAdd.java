package b1pod.commands.UserTags3;

import b1pod.core.Command;
import b1pod.core.ExecutionResult;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.sql.SQLException;
import java.util.Arrays;

import static b1pod.Bot.getPrefix;
import static b1pod.commands.UserTags3.UserTags3.*;

public class TagAdd extends Command
{
    public TagAdd()
    {
        this.name = "Add";
        this.syntax = "<name> <value>";
        this.description = "Add a new tag to this server, each tag must have a unique name." +
                "\n``<name> single word or multiple words in quotes e.g. ``\"tag name\" <value>``" +
                "\n``<value> single word or multiple words in quote e.g. ``<name> \"tag value\"``";
        this.triggers = Arrays.asList("add", "-a");
    }

    @Override
    protected ExecutionResult execute(MessageReceivedEvent event, String[] args) throws SQLException
    {
        String guildId = event.getGuild().getId();
        if (args.length != 4) return new ExecutionResult("warning", "Incorrect syntax, use `"
                + getPrefix() + "tag help` for more info.");
        String name = args[2], value = args[3];

        if (getTag(guildId, name) != null) return new ExecutionResult("warning", "Tag already exists.");
        String query = "INSERT INTO g" + guildId + " VALUES ('" + name + "', '" + value + "');";

        update(query);
        return new ExecutionResult("success");
    }
}
