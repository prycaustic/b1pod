package b1pod.commands.Music;

import b1pod.core.Command;
import b1pod.core.ExecutionResult;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;

import static b1pod.commands.Music.Music.NotInVoiceResult;
import static b1pod.commands.Music.Music.getGuildAudioPlayer;

public class FastForward extends Command
{
    public FastForward()
    {
        this.name = "ff";
        this.description = "Fast forward 10 seconds in the current song.";
        this.triggers = List.of("ff");
    }

    @Override
    protected ExecutionResult execute(MessageReceivedEvent event, String[] args) throws Exception
    {
        if (!event.getMember().getVoiceState().inVoiceChannel()) return NotInVoiceResult;
        GuildMusicManager musicManager = getGuildAudioPlayer(event.getGuild());
        AudioTrack playingTrack = musicManager.player.getPlayingTrack();
        long currentPosition = playingTrack.getPosition();

        playingTrack.setPosition(currentPosition + 10000);
        return new ExecutionResult("success");
    }
}
