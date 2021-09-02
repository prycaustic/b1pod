package b1pod.core;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static b1pod.Bot.getEmbedColor;
import static b1pod.Bot.getPrefix;

public abstract class Category extends ListenerAdapter
{
    protected String name = "Misc.";
    protected String description = "Miscellaneous commands.";
    protected List<String> triggers = new ArrayList<>();
    protected Command[] commands;

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event)
    {
        if (event.getAuthor().isBot()) return;
        String content = event.getMessage().getContentRaw();
        if (!content.equalsIgnoreCase(getPrefix() + name)) return;

        event.getMessage().replyEmbeds(getHelp()).mentionRepliedUser(false).queue();
    }

    public Command[] getCommands()
    {
        return commands;
    }

    public String getName()
    {
        return name;
    }

    public String getDescription()
    {
        return description;
    }

    public MessageEmbed getHelp()
    {
        EmbedBuilder helpEmbed = new EmbedBuilder()
                .setTitle(name + " Commands")
                .setDescription(description)
                .setColor(getEmbedColor());

        for (Command cmd : getCommands())
            helpEmbed.addField(cmd.getName(), cmd.getSyntax() + "\n" + cmd.getDescription(), false);

        return helpEmbed.build();
    }
}
