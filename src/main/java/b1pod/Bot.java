package b1pod;

import b1pod.Commands.*;
import b1pod.Commands.Music.Music;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class Bot
{
    public static void main(String[] args) throws Exception
    {
        JDA jda = JDABuilder.createDefault(args[0])
                .enableIntents(GatewayIntent.GUILD_MESSAGES, GatewayIntent.DIRECT_MESSAGES, GatewayIntent.GUILD_VOICE_STATES)
                .addEventListeners(
                        new PingCommand(),
                        new FilmCommand(),
                        new HypeManListener(),
                        new NASACommands(),
                        new Kanye(),
                        new DumbStuff(),
                        new UserTags(),
                        new WikipediaSearch(),
                        new UserTags2(),
                        new Music(args[1]),
                        new Shutdown()
                )
                //.setActivity(Activity.playing("CURRENTLY TESTING...COMMANDS MIGHT NOT WORK"))
                .build().awaitReady();
    }
}
