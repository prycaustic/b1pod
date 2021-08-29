package b1pod.Commands.PingPong;

import b1pod.Commands.core.ExecutionResult;
import b1pod.Commands.core.Command;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Arrays;

public class Ping extends Command
{
    public Ping()
    {
        this.name = "Ping";
        this.description = "Returns pong!";
        this.triggers = Arrays.asList("ping", "p", "test");
        this.children = new Command[] {new ChildTest(this)};
    }

    @Override
    protected ExecutionResult execute(MessageReceivedEvent event, String[] args)
    {
        if (args.length > 1) return null;
        return new ExecutionResult("", "Pong!");
    }
}
