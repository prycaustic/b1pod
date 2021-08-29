package b1pod.commands.UserTags3;

import b1pod.core.Command;
import b1pod.core.ExecutionResult;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;

import static b1pod.Bot.getPrefix;
import static b1pod.commands.UserTags3.UserTags3.*;

public class TagDisable extends Command
{
    public TagDisable(Command parent)
    {
        this.parent = parent;
        this.name = "Disable";
        this.syntax = "``" + getPrefix() + "tag disable``";
        this.description = "This will disable the use of tags in the server. All tags will be saved.";
        this.triggers = List.of("disable");
    }

    @Override
    protected ExecutionResult execute(MessageReceivedEvent event, String[] args) throws Exception
    {
        String guildId = event.getGuild().getId();
        if (!tagsEnabled(getConn(), guildId)) return new ExecutionResult("failure", "Tags are already disabled.");
        String query = "RENAME TABLE `g" + guildId +"` TO `d" + guildId + "`;";

        update(getConn(), query);
        return new ExecutionResult("success", "Tags have been disabled, use ``"
                + getPrefix() + "tag enable``  at any time to re-enable them.");
    }
}
