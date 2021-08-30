package b1pod.commands.UserTags3;

import b1pod.core.Command;
import b1pod.core.ExecutionResult;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;

import static b1pod.commands.UserTags3.UserTags3.*;

public class TagEnable extends Command
{
    public TagEnable()
    {
        this.name = "Enable";
        this.description = "Enable tags in your server.";
        this.triggers = List.of("enable");
    }

    @Override
    protected ExecutionResult execute(MessageReceivedEvent event, String[] args) throws Exception
    {
        String guildId = event.getGuild().getId();
        if (tagsEnabled(guildId)) return new ExecutionResult("failure", "Tags already enabled.");
        if (disabledTableExists(guildId))
        {
            String query = "RENAME TABLE `d" + guildId + "` TO `g" + guildId + "`;";

            update(query);
        }
        else
        {
            String query = "CREATE TABLE `g" + guildId + "` (" +
                    " `name` VARCHAR(99) NOT NULL," +
                    " `value` VARCHAR(200) NOT NULL," +
                    " UNIQUE INDEX `name` (`name`)" +
                    " );";

            update(query);
        }

        return new ExecutionResult("success");
    }
}
