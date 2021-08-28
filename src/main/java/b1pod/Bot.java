package b1pod;

import b1pod.Commands.*;
import b1pod.Commands.Music.Music;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.util.Arrays;

public class Bot
{
    public static void main(String[] args) throws Exception
    {
        JDA jda = JDABuilder.createDefault(args[0])
                .enableIntents(GatewayIntent.GUILD_MESSAGES, GatewayIntent.DIRECT_MESSAGES, GatewayIntent.GUILD_VOICE_STATES)
                .addEventListeners(
                        /*new PingCommand(),
                        new FilmCommand(),
                        new HypeManListener(),
                        new NASACommands(),
                        new Kanye(),
                        new DumbStuff(),
                        new UserTags(),
                        new WikipediaSearch(),
                        new Music(args[1]),*/
                        new UserTags2(args[2]),
                        new Shutdown()
                )
                .setActivity(Activity.playing("CURRENTLY TESTING...COMMANDS MIGHT NOT WORK"))
                .build().awaitReady();
    }

    public static String getEmote(String name)
    {
        switch (name)
        {
            case "success":
                return "✅";
            case "warning":
                return "⚠";
            case "failure":
                return "❌";
            case "finger":
                return "\uD83D\uDD95";
            case "wave":
                return "\uD83D\uDC4B";
        }

        return "";
    }
}
