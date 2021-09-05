package b1pod.commands.Music;

import b1pod.core.Command;
import b1pod.core.ExecutionResult;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.managers.AudioManager;
import org.apache.http.client.utils.URIBuilder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.List;

import static b1pod.Bot.*;
import static b1pod.commands.Music.Music.*;

public class Play extends Command
{
    public Play()
    {
        this.name = "Play";
        this.syntax = "<song name>";
        this.description = "Plays a song, currently only YouTube supported." +
                "\n``<song name>`` may be a link or search query.";
        this.triggers = Arrays.asList("play", "p");
    }

    @Override
    protected ExecutionResult execute(MessageReceivedEvent event, String[] args)
    {
        if (!event.getMember().getVoiceState().inVoiceChannel()) return NotInVoiceResult;
        String content = event.getMessage().getContentRaw();
        String trackUrl = attemptSearch(content);

        setGuildMusicChannel(event.getGuild(), event.getTextChannel());
        loadAndPlay(event.getMessage(), trackUrl);

        return null;
    }

    private void loadAndPlay(Message message, String trackUrl)
    {
        GuildMusicManager musicManager = getGuildAudioPlayer(message.getGuild());

        playerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler()
        {
            @Override
            public void trackLoaded(AudioTrack track)
            {
                play(track, message.getMember());
                if (musicManager.scheduler.getQueue().size() < 1) return;
                message.reply("Queued: " + track.getInfo().title).mentionRepliedUser(false).queue();
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist)
            {
                AudioTrack selectedTrack = playlist.getSelectedTrack();

                if (selectedTrack == null)
                {
                    for (AudioTrack track : playlist.getTracks())
                        play(track, message.getMember());

                    if (musicManager.scheduler.getQueue().size() < 1) return;
                    message.reply("Queued " + playlist.getTracks().size() +
                            " songs from playlist: " + playlist.getName()).mentionRepliedUser(false).queue();
                }
                else
                {
                    play(selectedTrack, message.getMember());
                    if (musicManager.scheduler.getQueue().size() < 1) return;
                    message.reply("Queued: " + selectedTrack.getInfo().title).mentionRepliedUser(false).queue();
                }
            }

            @Override
            public void noMatches()
            {
                message.reply("No results.").mentionRepliedUser(false).queue();
            }

            @Override
            public void loadFailed(FriendlyException exception)
            {
                message.reply("Could not play: " + exception.getMessage()).mentionRepliedUser(false).queue();
            }
        });
    }

    private void play(AudioTrack track, Member member)
    {
        Guild guild = member.getGuild();
        connectToUserVoiceChannel(guild.getAudioManager(), member);

        getGuildAudioPlayer(guild).scheduler.queue(track);
    }

    private static void connectToUserVoiceChannel(AudioManager audioManager, Member member)
    {
        audioManager.openAudioConnection(member.getVoiceState().getChannel());
    }

    private static String attemptSearch(String content)
    {
        String query = (content.startsWith(getPrefix() + "p ")) ? content.substring(4) : content.substring(7);

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
    }
}
