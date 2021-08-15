package b1pod;

import b1pod.Commands.*;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class Bot
{
    public static void main(String[] arguments) throws Exception
    {
        JDA jda = JDABuilder.createDefault(arguments[0])
                .enableIntents(GatewayIntent.GUILD_MESSAGES, GatewayIntent.DIRECT_MESSAGES)
                .addEventListeners(
                        new PingCommand(),
                        new FilmCommand(),
                        new HypeManListener(),
                        new NASACommands(),
                        new Kanye(),
                        new DumbStuff(),
                        new UserTags(),
                        new Shutdown()
                )
                .build().awaitReady();
    }
}
