package b1pod.commands.UserTags3;

import b1pod.core.Command;
import b1pod.core.ExecutionResult;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.sql.SQLException;
import java.util.Arrays;

import static b1pod.commands.UserTags3.UserTags3.*;

public class TagRemove extends Command
{
    public TagRemove()
    {
        this.name = "Remove";
        this.syntax = "<name>";
        this.description = "Deletes the specified tag." +
                "``<name> single word or multiple words in quotes, not case-sensitive.";
        this.triggers = Arrays.asList("remove", "-r");
    }

    @Override
    protected ExecutionResult execute(MessageReceivedEvent event, String[] args) throws SQLException
    {
        String guildId = event.getGuild().getId(), name = args[2];
        if (getTag(guildId, name) == null) return new ExecutionResult("failure", "Tag does not exist.");
        String query = "DELETE FROM `g" + guildId + "` WHERE (`name` = '" + name + "');";

        update(query);
        return new ExecutionResult("success");
    }
}
