package b1pod;

import b1pod.commands.*;
import b1pod.commands.Help.Help;
import b1pod.commands.Music.Music;
import b1pod.commands.UserTags3.TagListener;
import b1pod.commands.UserTags3.UserTags3;
import b1pod.core.CommandHandler;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class Bot
{
    protected static String youtubeApiKey;
    protected static String mySqlPassword;
    protected static String prefix;
    protected static int embedColor;
    protected static CommandHandler commandHandler;

    public static void main(String[] args) throws Exception
    {
        youtubeApiKey = args[1];
        mySqlPassword = args[2];
        prefix = "b-";
        embedColor = 0xFFCB77;

        JDABuilder jda = JDABuilder.createDefault(args[0])
                .enableIntents(GatewayIntent.GUILD_MESSAGES, GatewayIntent.DIRECT_MESSAGES, GatewayIntent.GUILD_VOICE_STATES)
                .addEventListeners(
                        new FilmCommand(),
                        new HypeManListener(),
                        new NASACommands(),
                        new Kanye(),
                        new DumbStuff(),
                        new WikipediaSearch(),
                        new TagListener(),
                        new Shutdown()
                );

        commandHandler = new CommandHandler(jda, new UserTags3(), new Help())
                .addCategories(new Music());

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
        return prefix;
    }

    public static String getYouTubeApiKey()
    {
        return youtubeApiKey;
    }

    public static String getSQLPassword()
    {
        return mySqlPassword;
    }

    public static int getEmbedColor()
    {
        return embedColor;
    }

    public static CommandHandler getCommandHandler()
    {
        return commandHandler;
    }
}
