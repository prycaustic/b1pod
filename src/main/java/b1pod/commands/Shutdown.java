package b1pod.commands;

import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Shutdown extends ListenerAdapter
{
    @Override
    public void onPrivateMessageReceived(PrivateMessageReceivedEvent event)
    {
        if (event.getMessage().getContentRaw().startsWith("shutdown"))
        {
            if (event.getAuthor().getId().equals("124620492098240513"))
            {
                event.getMessage().addReaction("\u26A0").queue();
                event.getJDA().shutdown();
            }
            else
            {
                event.getMessage().reply("You do not have permission to use this command.").mentionRepliedUser(false).queue();
            }
        }
    }
}
