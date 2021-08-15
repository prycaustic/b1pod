package b1pod.Commands;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class PingCommand extends ListenerAdapter
{
    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event)
    {
        if (event.getMessage().getContentRaw().startsWith("/ping") && !event.getAuthor().isBot())
        {
            event.getMessage().reply("pong").queue();
        }
    }
}
