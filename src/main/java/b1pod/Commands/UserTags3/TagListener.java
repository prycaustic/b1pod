package b1pod.Commands.UserTags3;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;

import static b1pod.Commands.UserTags3.UserTags3.*;

public class TagListener extends ListenerAdapter
{
    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event)
    {
        try
        {
            if (tagsEnabled(getConn(), event.getGuild().getId()))
            {
                String tag = getTag(getConn(), event.getGuild().getId(), event.getMessage().getContentRaw());

                if (tag != null)
                    event.getChannel().sendMessage(tag).queue();
            }
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
}
