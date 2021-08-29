package b1pod.commands.UserTags3;

import b1pod.core.Command;
import b1pod.core.ExecutionResult;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.sql.SQLException;
import java.util.Arrays;

import static b1pod.Bot.getPrefix;
import static b1pod.commands.UserTags3.UserTags3.*;

public class TagRemove extends Command
{
    public TagRemove(Command parent)
    {
        this.parent = parent;
        this.name = "Remove";
        this.syntax = "``" + getPrefix() + "tag remove <name>``";
        this.description = "Deletes the specified tag.";
        this.triggers = Arrays.asList("remove", "-r");
    }

    @Override
    protected ExecutionResult execute(MessageReceivedEvent event, String[] args) throws SQLException
    {
        String guildId = event.getGuild().getId(), name = args[2];
        if (getTag(getConn(), guildId, name) == null) return new ExecutionResult("failure", "Tag does not exist.");
        String query = "DELETE FROM `g" + guildId + "` WHERE (`name` = '" + name + "');";

        update(getConn(), query);
        return new ExecutionResult("success");
    }
}
