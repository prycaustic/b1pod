package b1pod.commands.UserTags3;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;

import static b1pod.commands.UserTags3.UserTags3.*;

public class TagListener extends ListenerAdapter
{
    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event)
    {
        if (event.getAuthor().isBot()) return;
        String guildId = event.getGuild().getId();

        try
        {
            if (tagsEnabled(guildId))
            {
                String tag = getTag(guildId, event.getMessage().getContentRaw());

                if (tag != null)
                    event.getChannel().sendMessage(tag).queue();
            }
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
}
