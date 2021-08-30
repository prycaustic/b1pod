package b1pod.core;

import net.dv8tion.jda.api.JDABuilder;

import java.util.ArrayList;
import java.util.List;

public class CommandHandler
{
    protected List<Command> commands = new ArrayList<>();

    public CommandHandler(JDABuilder jda, Command... listeners)
    {
        for (Command command : listeners)
        {
            commands.add(command);
            jda.addEventListeners(command);

            if (command.getChildren() == null) break;
            for (Command child : command.getChildren())
                jda.addEventListeners(child);
        }
    }

    public List<Command> getCommands()
    {
        return commands;
    }
}
