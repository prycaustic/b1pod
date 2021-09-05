package b1pod.core;

import net.dv8tion.jda.api.EmbedBuilder;
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
    protected String syntax = null;
    protected String description = "No description available.";
    protected List<String> triggers = new ArrayList<>();
    protected Command[] children = null;
    protected Command parent = null;
    protected boolean guildOnly = false;

    protected abstract ExecutionResult execute(MessageReceivedEvent event, String[] args) throws Exception;

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event)
    {
        Message message = event.getMessage();
        String content = message.getContentRaw();
        if (!content.startsWith(getPrefix())) return;
        String[] args = parseInput(content);

        try
        {
            if ((args.length > 1 && parent !=null && parent.getTriggers().contains(args[0]) && triggers.contains(args[1])) || (parent == null && triggers.contains(args[0])))
                checkGuildAndParseResult(event, args);
        }
        catch (IllegalStateException e)
        {
            parseResult(message, new ExecutionResult("warning", e.getMessage()));
        }
        catch (Exception e)
        {
            e.printStackTrace();
            parseResult(message, new ExecutionResult("failure", "Something went wrong."));
        }
    }

    private String[] parseInput(String content)
    {
        String[] args = content.substring(getPrefix().length()).split(" (?=([^\"]*\"[^\"]*\")*[^\"]*$)");
        for (int i = 0; i < args.length; i++)
        {
            args[i] = args[i].replace("\"", "");
        }
        return args;
    }

    private void parseResult(Message message, ExecutionResult result)
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

    private void checkGuildAndParseResult(MessageReceivedEvent event, String[] args) throws Exception
    {
        if (guildOnly && !event.isFromGuild()) throw new IllegalStateException("This command cannot be used in Private Messages.");
        parseResult(event.getMessage(), execute(event, args));
    }

    public void setParent(Command parent)
    {
        this.parent = parent;
    }

    public void setGuildOnly(boolean guildOnly)
    {
        this.guildOnly = guildOnly;
    }

    public boolean getGuildOnly()
    {
        return guildOnly;
    }

    public String getName()
    {
        return name;
    }

    public String getSyntax()
    {
        StringBuilder syntaxBuilder = new StringBuilder("``" + getPrefix());
        if (parent != null) { syntaxBuilder.append(parent.getName().toLowerCase()).append(" "); }
        syntaxBuilder.append(name.toLowerCase());
        if (syntax != null) { syntaxBuilder.append(" ").append(syntax); }
        syntaxBuilder.append("``");

        return syntaxBuilder.toString();
    }

    public String getDescription()
    {
        return description;
    }

    public Command[] getChildren()
    {
        return children;
    }

    public List<String> getTriggers()
    {
        return triggers;
    }

    public ExecutionResult getHelp()
    {
        if (parent != null) return null;
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