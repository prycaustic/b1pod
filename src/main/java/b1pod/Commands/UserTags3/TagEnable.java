package b1pod.Commands.UserTags3;

import b1pod.Commands.core.Command;
import b1pod.Commands.core.ExecutionResult;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;

import static b1pod.Bot.getPrefix;
import static b1pod.Commands.UserTags3.UserTags3.*;

public class TagEnable extends Command
{
    public TagEnable(Command parent)
    {
        this.parent = parent;
        this.name = "Enable";
        this.syntax = "``" + getPrefix() + "tag enable``";
        this.description = "Enable tags in your server.";
        this.triggers = List.of("enable");
    }

    @Override
    protected ExecutionResult execute(MessageReceivedEvent event, String[] args) throws Exception
    {
        String guildId = event.getGuild().getId();
        if (tagsEnabled(getConn(), guildId)) return new ExecutionResult("failure", "Tags already enabled.");
        if (disabledTableExists(getConn(), guildId))
        {
            String query = "RENAME TABLE `d" + guildId + "` TO `g" + guildId + "`;";

            update(getConn(), query);
        }
        else
        {
            String query = "CREATE TABLE `g" + guildId + "` (" +
                    " `name` VARCHAR(99) NOT NULL," +
                    " `value` VARCHAR(200) NOT NULL," +
                    " UNIQUE INDEX `name` (`name`)" +
                    " );";

            update(conn, query);
        }

        return new ExecutionResult("success");
    }
}
