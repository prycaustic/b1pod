package b1pod.Commands;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class DumbStuff extends ListenerAdapter
{
    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event)
    {
        if (event.getMessage().getContentRaw().startsWith("elon") && !event.getMessage().getAuthor().isBot())
        {
            event.getChannel().sendMessage("So you're going by \"elon\" now nerd? Haha whats up douche bag, it's Tony from Highschool. Remember me? Me and the guys used to give you a hard time in school. Sorry you were just an easy target lol. I can see not much has changed. Remember Sarah the girl you had a crush on? Yeah we're married now. I make over 200k a year and drive a mustang GT. I guess some things never change huh loser? Nice catching up lol. Pathetic..").queue();
        }
        else if (event.getMessage().getContentRaw().equalsIgnoreCase("my internet is back"))
        {
            event.getChannel().sendMessage("nobody cares about your internet.").queue();
        }
    }
}
