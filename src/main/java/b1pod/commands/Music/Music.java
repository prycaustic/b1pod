package b1pod.commands.Music;

import b1pod.core.Category;
import b1pod.core.Command;
import b1pod.core.ExecutionResult;
import com.sedmelluq.discord.lavaplayer.player.AudioConfiguration;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;
import org.apache.http.client.utils.URIBuilder;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static b1pod.Bot.*;

public class Music extends Category
{
    public static AudioPlayerManager playerManager;
    public static Map<Long, GuildMusicManager> musicManagers;
    public static Map<Long, TextChannel> musicChannels;
    public static ExecutionResult NotInVoiceResult =
            new ExecutionResult("warning", "You must be in a voice channel to use this command.");

    public Music()
    {
        this.name = "Music";
        this.description = "Music player / on demand ear blaster.";
        this.triggers = List.of("music");
        this.commands = new Command[] {new Play(), new Skip(), new Queue(), new Loop(), new Clear(), new FastForward(),
                new Disconnect()};

        musicManagers = new HashMap<>();
        musicChannels = new HashMap<>();
        playerManager = new DefaultAudioPlayerManager();
        playerManager.getConfiguration().setResamplingQuality(AudioConfiguration.ResamplingQuality.HIGH);
        playerManager.getConfiguration().setOpusEncodingQuality(10);
        playerManager.setFrameBufferDuration(15000);
        playerManager.setPlayerCleanupThreshold(300000);
        AudioSourceManagers.registerRemoteSources(playerManager);
    }

    public static synchronized GuildMusicManager getGuildAudioPlayer(Guild guild) {
        long guildId = Long.parseLong(guild.getId());
        GuildMusicManager musicManager = musicManagers.get(guildId);

        if (musicManager == null) {
            musicManager = new GuildMusicManager(playerManager, guild);
            musicManagers.put(guildId, musicManager);
        }

        guild.getAudioManager().setSendingHandler(musicManager.getSendHandler());

        return musicManager;
    }

    public static TextChannel getGuildMusicChannel(Guild guild)
    {
        long guildId = guild.getIdLong();

        return musicChannels.get(guildId);
    }

    public static void setGuildMusicChannel(Guild guild, TextChannel textChannel)
    {
        long guildId = guild.getIdLong();

        musicChannels.put(guildId, textChannel);
    }
}