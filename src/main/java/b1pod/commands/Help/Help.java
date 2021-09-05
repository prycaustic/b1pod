package b1pod.commands.Help;

import b1pod.core.Category;
import b1pod.core.Command;
import b1pod.core.ExecutionResult;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import okhttp3.internal.http2.Http2Connection;

import java.util.List;

import static b1pod.Bot.*;

public class Help extends ListenerAdapter
{
    @Override
    public void onMessageReceived(MessageReceivedEvent event)
    {
        if (event.getAuthor().isBot()) return;
        if (!event.getMessage().getContentRaw().equalsIgnoreCase(getPrefix() + "help")) return;
        EmbedBuilder helpEmbed = new EmbedBuilder()
                .setTitle("bent-bot Manual")
                .setDescription("Use ``" + getPrefix() + "<command> help`` to get help with a specific command.\n" +
                        "Use ``" + getPrefix() + "<category>`` to list the commands in a category.")
                .setColor(getEmbedColor());

        // Add commands
        for (Command cmd : getCommandHandler().getCommands())
            helpEmbed.addField(cmd.getName(), cmd.getSyntax() + "\n" + cmd.getDescription(), false);

        //Add categories
        StringBuilder catValue = new StringBuilder();

        for (Category cat : getCommandHandler().getCategories())
            catValue.append("\n**").append(cat.getName()).append("** — ").append(cat.getDescription());

        helpEmbed.addField("Categories", catValue.toString(), false);

        event.getMessage().replyEmbeds(helpEmbed.build()).mentionRepliedUser(false).queue();
    }
}
