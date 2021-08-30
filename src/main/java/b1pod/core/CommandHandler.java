package b1pod.core;

import net.dv8tion.jda.api.JDABuilder;

import java.util.ArrayList;
import java.util.List;

public class CommandHandler
{
    protected List<Command> commands = new ArrayList<>();

    public CommandHandler(JDABuilder jda, Command... listeners)
    {
        for (Command parent : listeners)
        {
            commands.add(parent);
            jda.addEventListeners(parent);

            if (parent.getChildren() == null) break;
            for (Command child : parent.getChildren())
            {
                jda.addEventListeners(child);
                child.setParent(parent);
                child.setGuildOnly(parent.getGuildOnly());
            }
        }
    }

    public List<Command> getCommands()
    {
        return commands;
    }
}
