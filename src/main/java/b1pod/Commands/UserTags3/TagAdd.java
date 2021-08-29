package b1pod.Commands.UserTags3;

import b1pod.Commands.core.Command;
import b1pod.Commands.core.ExecutionResult;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.sql.SQLException;
import java.util.Arrays;

import static b1pod.Bot.getPrefix;
import static b1pod.Commands.UserTags3.UserTags3.*;

public class TagAdd extends Command
{
    public TagAdd(Command parent)
    {
        this.parent = parent;
        this.name = "Add";
        this.syntax = "``" + getPrefix() + "tag add <name> <value>``";
        this.description = "Tag names and values with more than one word should be surrounded with quotes.";
        this.triggers = Arrays.asList("add", "-a");
    }

    @Override
    protected ExecutionResult execute(MessageReceivedEvent event, String[] args) throws SQLException
    {
        String guildId = event.getGuild().getId();
        if (args.length != 4) return new ExecutionResult("warning", "Incorrect syntax, use `"
                + getPrefix() + "tag help` for more info.");
        String name = args[2], value = args[3];

        if (getTag(getConn(), guildId, name) != null) return new ExecutionResult("warning", "Tag already exists.");
        String query = "INSERT INTO g" + guildId + " VALUES ('" + name + "', '" + value + "');";

        update(getConn(), query);
        return new ExecutionResult("success");
    }
}
