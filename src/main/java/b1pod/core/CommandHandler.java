package b1pod.core;

import net.dv8tion.jda.api.JDABuilder;

public class CommandHandler
{
    public CommandHandler(JDABuilder jda, Command... commands)
    {
        for (Command command : commands)
        {
            jda.addEventListeners(command);
            for (Command child : command.getChildren())
            {
                jda.addEventListeners(child);
            }
        }
    }
}
