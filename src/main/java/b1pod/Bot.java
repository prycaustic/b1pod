package b1pod;

import b1pod.commands.*;
import b1pod.commands.Music.Music;
import b1pod.commands.PingPong.Ping;
import b1pod.commands.UserTags3.TagListener;
import b1pod.commands.UserTags3.UserTags3;
import b1pod.core.CommandHandler;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class Bot
{
    private static String YOUTUBE_API_KEY;
    private static String MYSQL_PASSWORD;
    private static String PREFIX;
    private static int EMBED_COLOR;

    public static void main(String[] args) throws Exception
    {
        YOUTUBE_API_KEY = args[1];
        MYSQL_PASSWORD = args[2];
        PREFIX = "b-";
        EMBED_COLOR = 0xFFCB77;

        JDABuilder jda = JDABuilder.createDefault(args[0])
                .enableIntents(GatewayIntent.GUILD_MESSAGES, GatewayIntent.DIRECT_MESSAGES, GatewayIntent.GUILD_VOICE_STATES)
                .addEventListeners(
                        new FilmCommand(),
                        new HypeManListener(),
                        new NASACommands(),
                        new Kanye(),
                        new DumbStuff(),
                        new WikipediaSearch(),
                        new Music(),
                        new TagListener(),
                        new Shutdown()
                );

        new CommandHandler(jda,
                new Ping(),
                new UserTags3());

        //jda.setActivity(Activity.playing("CURRENTLY TESTING...COMMANDS MIGHT NOT WORK"));

        jda.build();
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

    public static String getPrefix()
    {
        return PREFIX;
    }

    public static String getYouTubeApiKey()
    {
        return YOUTUBE_API_KEY;
    }

    public static String getSQLPassword()
    {
        return MYSQL_PASSWORD;
    }

    public static int getEmbedColor()
    {
        return EMBED_COLOR;
    }
}
