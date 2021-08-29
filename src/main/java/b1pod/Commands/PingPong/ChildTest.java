package b1pod.Commands.PingPong;

import b1pod.Commands.core.Command;
import b1pod.Commands.core.ExecutionResult;

import java.util.Arrays;

import static b1pod.Bot.getPrefix;

public class ChildTest extends Command
{

    public ChildTest(Command parent)
    {
        this.name = "Name";
        this.syntax = "``" + getPrefix() + parent.getName().toLowerCase() + " " + this.name.toLowerCase() + " <name>``";
        this.description = "Attach a name to the output.";
        this.aliases = Arrays.asList("name", "-n");
        this.parent = parent;
    }

    @Override
    protected ExecutionResult execute(String[] args)
    {
        if (args.length < 2) return null;
        return new ExecutionResult("", "Pong! " + args[2]);
    }
}
