package b1pod.commands.Music;

import b1pod.core.Command;
import b1pod.core.ExecutionResult;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Arrays;

import static b1pod.commands.Music.Music.NotInVoiceResult;
import static b1pod.commands.Music.Music.getGuildAudioPlayer;

public class Loop extends Command
{
    public Loop()
    {
        this.name = "Loop";
        this.description = "Set the current track to loop or not.";
        this.triggers = Arrays.asList("loop", "l");
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    protected ExecutionResult execute(MessageReceivedEvent event, String[] args)
    {
        if (!event.getMember().getVoiceState().inVoiceChannel()) return NotInVoiceResult;
        GuildMusicManager musicManager = getGuildAudioPlayer(event.getGuild());
        boolean isLooping = musicManager.scheduler.isLooping();
        musicManager.scheduler.setLooping(!isLooping);

        return new ExecutionResult("success", "Music player is now " + ((isLooping) ? "not looping." : "looping."));
    }
}
