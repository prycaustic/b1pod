package b1pod.commands.Music;

import b1pod.core.Command;
import b1pod.core.ExecutionResult;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Arrays;
import java.util.concurrent.BlockingQueue;

import static b1pod.Bot.getEmbedColor;
import static b1pod.commands.Music.Music.NotInVoiceResult;
import static b1pod.commands.Music.Music.getGuildAudioPlayer;

public class Queue extends Command
{
    public Queue()
    {
        this.name = "Queue";
        this.syntax = "<page>";
        this.description = "Displays the current queue of songs." +
                "\n``<page>`` is the page number to go to (default 1).";
        this.triggers = Arrays.asList("queue", "q");
    }

    @Override
    protected ExecutionResult execute(MessageReceivedEvent event, String[] args)
    {
        if (!event.getMember().getVoiceState().inVoiceChannel()) return NotInVoiceResult;
        GuildMusicManager musicManager = getGuildAudioPlayer(event.getGuild());
        if (musicManager.scheduler.getQueue().size() == 0) return new ExecutionResult("warning", "Not playing anything.");
        // Get currently playing track
        AudioTrack currentTrack = musicManager.player.getPlayingTrack();
        String currentTrackValue = "[" +  currentTrack.getInfo().title + "](" + currentTrack.getInfo().uri + ")";

        // Get queue
        BlockingQueue<AudioTrack> bQueue = musicManager.scheduler.getQueue();
        AudioTrack[] queue = new AudioTrack[bQueue.size()];
        queue = bQueue.toArray(queue);
        int pageNumber = (args.length > 1) ? Integer.parseInt(args[1]) : 1;
        int startIndex = (pageNumber * 5) - 4;
        StringBuilder description = new StringBuilder();
        description.append("There are ").append(bQueue.size()).append(" tracks queued.");
        if (musicManager.scheduler.isLooping())
            description.append("\nPlayer is set to loop current track.");

        if (startIndex < 1 || startIndex > queue.length) return new ExecutionResult("warning", "Page does not exist.");
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
            qEmbed.addField("Track " + i + ": ", value, false);
        }

        return new ExecutionResult(qEmbed.build());
    }
}
