package b1pod.commands.Music;

import b1pod.core.Command;
import b1pod.core.ExecutionResult;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Arrays;
import java.util.concurrent.BlockingQueue;

import static b1pod.Bot.getEmbedColor;
import static b1pod.commands.Music.Music.NotInVoiceResult;
import static b1pod.commands.Music.Music.getGuildAudioPlayer;

public class Queue extends Command
{
    private GuildMusicManager musicManager;
    private BlockingQueue<AudioTrack> trackQueue;

    public Queue()
    {
        this.name = "Queue";
        this.syntax = "<page>";
        this.description = "Displays the current queue of songs." +
                "\n``<page>`` is the page number to go to (default 1).";
        this.triggers = Arrays.asList("queue", "q");
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    protected ExecutionResult execute(MessageReceivedEvent event, String[] args)
    {
        if (!event.getMember().getVoiceState().inVoiceChannel()) return NotInVoiceResult;
        musicManager = getGuildAudioPlayer(event.getGuild());
        trackQueue = musicManager.scheduler.getQueue();
        if (musicManager.player.isPaused()) return new ExecutionResult("warning", "Not playing anything.");

        try
        {
            switch (args.length)
            {
                case 1:
                    return new ExecutionResult(buildQueueEmbed(1));
                case 2:
                    try
                    {
                        return new ExecutionResult(buildQueueEmbed(Integer.parseInt(args[1])));
                    }
                    catch (NumberFormatException e)
                    {
                        return new ExecutionResult("warning", "``<track number>`` must be a number.");
                    }
                default:
                    return null;
            }
        }
        catch (IndexOutOfBoundsException e)
        {
            return new ExecutionResult("failure", e.getMessage());
        }
    }

    private MessageEmbed buildQueueEmbed(int pageNumber) throws IndexOutOfBoundsException
    {
        // Get currently playing track
        AudioTrack currentTrack = musicManager.player.getPlayingTrack();
        String currentTrackValue = "[" + currentTrack.getInfo().title + "](" + currentTrack.getInfo().uri + ")";

        // Get queue
        AudioTrack[] queue = new AudioTrack[trackQueue.size()];
        queue = trackQueue.toArray(queue);
        int startIndex = (pageNumber * 5) - 5;
        if (startIndex < 0)
            throw new IndexOutOfBoundsException("Page does not exist.");

        StringBuilder description = new StringBuilder();
        description.append("There are ").append(trackQueue.size()).append(" tracks queued.");
        if (musicManager.scheduler.isLooping())
            description.append("\nPlayer is set to loop current track.");

        EmbedBuilder qEmbed = new EmbedBuilder()
                .setTitle("Music Queue")
                .setDescription(description)
                .addField("Playing", currentTrackValue, false)
                .setFooter("Page: " + pageNumber + " of " + ((queue.length / 5) + 1))
                .setColor(getEmbedColor());

        for (int i = startIndex; i < (startIndex + 5); i++)
        {
            if (i > queue.length - 1) break;
            AudioTrack track = queue[i];
            String value = "[" + track.getInfo().title + "](" + track.getInfo().uri + ")";
            qEmbed.addField("Track " + (i + 1) + ": ", value, false);
        }

        return qEmbed.build();
    }
}
