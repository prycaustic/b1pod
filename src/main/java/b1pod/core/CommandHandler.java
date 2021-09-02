package b1pod.core;

import net.dv8tion.jda.api.JDABuilder;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CommandHandler
{
    protected List<Command> commands = new ArrayList<>();
    protected List<Category> categories = new ArrayList<>();
    protected JDABuilder jdaBuilder;

    public CommandHandler(JDABuilder jda, @NotNull Command... listeners)
    {
        jdaBuilder = jda;

        for (Command cmd : listeners)
        {
            commands.add(cmd);
            jdaBuilder.addEventListeners(cmd);

            if (cmd.getChildren() == null) break;
            for (Command child : cmd.getChildren())
            {
                jdaBuilder.addEventListeners(child);
                child.setParent(cmd);
                child.setGuildOnly(cmd.getGuildOnly());
            }
        }
    }

    public CommandHandler addCategories(@NotNull Category... categories)
    {
        for (Category cat : categories)
        {
            this.categories.add(cat);
            jdaBuilder.addEventListeners(cat);

            for (Command cmd : cat.getCommands())
                jdaBuilder.addEventListeners(cmd);
        }
        return this;
    }

    public List<Command> getCommands()
    {
        return commands;
    }

    public List<Category> getCategories()
    {
        return categories;
    }
}
