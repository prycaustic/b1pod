package b1pod.commands.Help;

import b1pod.core.Category;
import b1pod.core.Command;
import b1pod.core.ExecutionResult;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;

import static b1pod.Bot.*;

public class Help extends Command
{
    public Help()
    {
        this.name = "Help";
        this.description = "Display this help message.";
        this.triggers = List.of("help");
    }

    @Override
    protected ExecutionResult execute(MessageReceivedEvent event, String[] args) throws Exception
    {
        EmbedBuilder helpEmbed = new EmbedBuilder()
                .setTitle("bent-bot Manual")
                .setDescription("Use ``" + getPrefix() + "<command>`` to get help with a specific command or" +
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

        return new ExecutionResult(helpEmbed.build());
    }
}
