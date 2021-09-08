package b1pod.commands.Music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static b1pod.Bot.getEmbedColor;

/**
 * This class schedules tracks for the audio player. It contains the queue of tracks.
 */
public class TrackScheduler extends AudioEventAdapter
{
    private final AudioPlayer player;
    private final BlockingQueue<AudioTrack> queue;
    private boolean looping;
    private final Guild guild;

    /**
     * @param player The audio player this scheduler uses
     * @param guild The guild that this player is part of
     */
    public TrackScheduler(AudioPlayer player, Guild guild) {
        this.player = player;
        this.guild = guild;
        this.queue = new LinkedBlockingQueue<>();
        this.looping = false;
    }

    /**
     * Add the next track to queue or play right away if nothing is in the queue.
     *
     * @param track The track to play or add to queue.
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void queue(AudioTrack track) {
        // Calling startTrack with the noInterrupt set to true will start the track only if nothing is currently playing. If
        // something is playing, it returns false and does nothing. In that case the player was already playing so this
        // track goes to the queue instead.
        if (!player.startTrack(track, true)) {
            queue.offer(track);
        }
    }

    /**
     * Start the next track, stopping the current one if it is playing.
     */
    public void nextTrack() {
        // Start the next track, regardless of if something is already playing or not. In case queue was empty, we are
        // giving null to startTrack, which is a valid argument and will simply stop the player.
        player.startTrack(queue.poll(), false);
    }

    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track)
    {
        EmbedBuilder playingEmbed = new EmbedBuilder()
                .setTitle("Now Playing")
                .setDescription("[" + track.getInfo().title + "](" + track.getInfo().uri + ")")
                .setColor(getEmbedColor());

        Music.getGuildMusicChannel(guild).sendMessageEmbeds(playingEmbed.build()).queue();
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        // Only start the next track if the end reason is suitable for it (FINISHED or LOAD_FAILED)
        if (endReason.mayStartNext)
        {
            if (looping)
                player.startTrack(track.makeClone(), false);
            else
                nextTrack();
        }
    }

    public BlockingQueue<AudioTrack> getQueue()
    {
        return queue;
    }

    public void setLooping(boolean looping)
    {
        this.looping = looping;
    }

    public boolean isLooping()
    {
        return looping;
    }
}

