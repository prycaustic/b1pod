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

    /*
    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event)
    {
        if (event.getAuthor().isBot()) return;
        String content = event.getMessage().getContentRaw();
        String[] args = content.split(" ");
        boolean inVoice = false;

        if (Objects.requireNonNull(event.getMember()).getVoiceState() != null)
        {
            inVoice = Objects.requireNonNull(event.getMember().getVoiceState()).inVoiceChannel();
        }

        try
        {
            switch (args[0].replace(getPrefix(), ""))
            {
                case "play":
                    if (!inVoice) throw new Exception();
                    String trackUrl = attemptSearch(content.substring(7));
                    if (trackUrl != null)
                        loadAndPlay(event.getChannel(), trackUrl, event.getMessage());
                    else
                        event.getMessage().reply("No results.").mentionRepliedUser(false).queue();
                    break;
                case "skip":
                    if (!inVoice) throw new Exception();
                    skipTrack(event.getChannel());
                    break;
                case "disconnect":
                    if (!inVoice) throw new Exception();
                    leaveAudioChannel(event.getGuild().getAudioManager(), event.getMessage());
                    break;
                case "queue":
                    if (event.getGuild().getAudioManager().isConnected())
                    {
                        VoiceChannel userVoiceChannel = Objects.requireNonNull(event.getMember().getVoiceState()).getChannel();
                        VoiceChannel botVoiceChannel = event.getGuild().getAudioManager().getConnectedChannel();

                        if (userVoiceChannel != botVoiceChannel) throw new Exception();
                        displayQueue(getGuildAudioPlayer(event.getGuild()), event.getMessage());
                    }
                    break;
                case "summon":
                    connectToUserVoiceChannel(event.getGuild().getAudioManager(), event.getMember());
                case "pause":
                    boolean isPaused = getGuildAudioPlayer(event.getGuild()).player.isPaused();
                    getGuildAudioPlayer(event.getGuild()).player.setPaused(!isPaused);
                    event.getMessage().addReaction(getEmote("finger")).queue();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            event.getMessage().reply("You must be in a voice channel to use this command!").mentionRepliedUser(false).queue();
        }

        super.onGuildMessageReceived(event);
    }

    private synchronized GuildMusicManager getGuildAudioPlayer(Guild guild) {
        long guildId = Long.parseLong(guild.getId());
        GuildMusicManager musicManager = musicManagers.get(guildId);

        if (musicManager == null) {
            musicManager = new GuildMusicManager(playerManager);
            musicManagers.put(guildId, musicManager);
        }

        guild.getAudioManager().setSendingHandler(musicManager.getSendHandler());

        return musicManager;
    }

    private void loadAndPlay(TextChannel channel, String trackUrl, Message message) {
        GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());

        playerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                message.reply("Queued: " + track.getInfo().title).mentionRepliedUser(false).queue();

                play(channel.getGuild(), musicManager, track, message.getMember());
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                AudioTrack selectedTrack = playlist.getSelectedTrack();

                if (selectedTrack == null)
                {
                    for (AudioTrack track : playlist.getTracks())
                    {
                        play(channel.getGuild(), musicManager, track, message.getMember());
                    }

                    message.reply("Queued " + playlist.getTracks().size() +
                            " songs from playlist: " + playlist.getName()).mentionRepliedUser(false).queue();
                }
                else
                {
                    play(channel.getGuild(), musicManager, selectedTrack, message.getMember());
                    message.reply("Queued: " + selectedTrack.getInfo().title).mentionRepliedUser(false).queue();
                }
            }

            @Override
            public void noMatches() {
                channel.sendMessage("Nothing found by " + trackUrl).queue();
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                channel.sendMessage("Could not play: " + exception.getMessage()).queue();
            }
        });
    }

    private void play(Guild guild, GuildMusicManager musicManager, AudioTrack track, Member member) {
        connectToUserVoiceChannel(guild.getAudioManager(), member);

        musicManager.scheduler.queue(track);
    }

    private void skipTrack(TextChannel channel) {
        GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());
        musicManager.scheduler.nextTrack();

        channel.sendMessage("Track skipped.").queue();
    }

    private static void connectToUserVoiceChannel(AudioManager audioManager, Member member)
    {
        audioManager.openAudioConnection(Objects.requireNonNull(member.getVoiceState()).getChannel());
    }

    private static void leaveAudioChannel(AudioManager audioManager, Message message)
    {
        if (audioManager.isConnected())
        {
            audioManager.closeAudioConnection();
            message.addReaction(getEmote("wave")).queue();
        }
    }

    private static void displayQueue(GuildMusicManager musicManager, Message message)
    {
        AudioTrack currentTrack = musicManager.player.getPlayingTrack();
        String currentTrackValue = "[" +  currentTrack.getInfo().title + "](" + currentTrack.getInfo().uri + ")";

        EmbedBuilder qEmbed = new EmbedBuilder()
                .setTitle("Music Queue")
                .setDescription("There are currently " + musicManager.scheduler.getQueue().size() + " tracks queued.")
                .addField("Playing", currentTrackValue, false)
                .setColor(getEmbedColor());

        int i = 1;
        for (AudioTrack track : musicManager.scheduler.getQueue())
        {
            String value = "[" + track.getInfo().title + "](" + track.getInfo().uri + ")";
            qEmbed.addField("Track " + i + ": ", value, false);
            i++;
            if (i == 5) break;
        }

        message.replyEmbeds(qEmbed.build()).mentionRepliedUser(false).queue();
    }

    private static String attemptSearch(String query)
    {
        if (query.startsWith("https://"))
            return query;
        else
            return getUrlFromYoutubeApi(query);
    }

    private static String getUrlFromYoutubeApi(String query)
    {
        try
        {
            HttpResponse<String> response = YoutubeAPIRequest(query);
            assert response != null;
            JSONObject json = new JSONObject(response.body());
            JSONArray items = json.getJSONArray("items");
            String videoId = items.getJSONObject(0).getJSONObject("id").getString("videoId");

            return "https://www.youtube.com/watch?v=" + videoId;
        }
        catch (JSONException e)
        {
            return null;
        }
    }

    private static HttpResponse<String> YoutubeAPIRequest(String query)
    {
        try
        {
            URIBuilder URI = new URIBuilder("https://www.googleapis.com/youtube/v3/search")
                    .addParameter("part", "snippet")
                    .addParameter("key", getYouTubeApiKey())
                    .addParameter("q", query);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.build())
                    .build();

            return HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        }
        catch (Exception e)
        {
            return null;
        }
    }*/
}