package b1pod.Commands.core;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static b1pod.Bot.*;

public abstract class Command extends ListenerAdapter
{
    protected String name = "null";
    protected String syntax = "null";
    protected String description = "No description available.";
    protected List<String> aliases = new ArrayList<>();
    protected Command[] children = null;
    protected Command parent = null;
    protected boolean guildOnly = false;

    protected abstract ExecutionResult execute(String[] args);

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event)
    {
        Message message = event.getMessage();
        String content = message.getContentRaw();
        if (!content.startsWith(getPrefix())) return;
        String[] args = parseInput(content);

        if (guildOnly && !event.isFromGuild())
            parseResult(message, new ExecutionResult("warning", "This command cannot be used in private messages."));
        else if (args.length == 1)
        {
            if (aliases.contains(args[0]))
                parseResult(message, execute(args));
        }
        else if (args.length > 1 && !args[1].equalsIgnoreCase("help"))
        {
            if (parent == null) return;
            if (parent.getAliases().contains(args[0]) && aliases.contains(args[1]))
                parseResult(message, execute(args));
        }
        else if (args.length > 1 && args[1].equalsIgnoreCase("help"))
        {
            parseResult(message, getHelp());
        }
    }

    public final String[] parseInput(String content)
    {
        String[] args = content.substring(getPrefix().length()).split(" (?=([^\"]*\"[^\"]*\")*[^\"]*$)");
        for (int i = 0; i < args.length; i++)
        {
            args[i] = args[i].replace("\"", "");
        }
        return args;
    }

    public final void parseResult(Message message, ExecutionResult result)
    {
        if (result == null) return;
        String emoji = result.getEmoji(), reason = result.getReason();
        MessageEmbed embed = result.getEmbed();

        if (emoji != null && reason == null)
            message.addReaction(getEmote(emoji)).queue();
        if (emoji != null && reason != null)
            message.reply(getEmote(emoji) + " " + reason).mentionRepliedUser(false).queue();
        if (result.getEmbed() != null)
            message.replyEmbeds(embed).mentionRepliedUser(false).queue();
    }

    public String getName()
    {
        return name;
    }

    public String getSyntax()
    {
        return syntax;
    }

    public String getDescription()
    {
        return description;
    }

    public Command[] getChildren()
    {
        return children;
    }

    public List<String> getAliases()
    {
        return aliases;
    }

    public ExecutionResult getHelp()
    {
        EmbedBuilder helpEmbed = new EmbedBuilder()
                .setTitle(this.name)
                .setDescription(this.description)
                .setColor(getEmbedColor())
                .setFooter("If you still need help contact @stronous#3792");

        for (Command child : this.getChildren())
        {
            helpEmbed.addField(child.getName(), child.getSyntax() + "\n" + child.getDescription(), false);
        }

        return new ExecutionResult(helpEmbed.build());
    }
}