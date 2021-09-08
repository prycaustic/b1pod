package b1pod.commands.Music;

import b1pod.core.Command;
import b1pod.core.ExecutionResult;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Arrays;

import static b1pod.commands.Music.Music.NotInVoiceResult;
import static b1pod.commands.Music.Music.getGuildAudioPlayer;

public class Clear extends Command
{
    public Clear()
    {
        this.name = "Clear";
        this.description = "Clear the music queue.";
        this.triggers = Arrays.asList("clear", "c");
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    protected ExecutionResult execute(MessageReceivedEvent event, String[] args) throws Exception
    {
        if (!event.getMember().getVoiceState().inVoiceChannel()) return NotInVoiceResult;
        GuildMusicManager musicManager = getGuildAudioPlayer(event.getGuild());

        musicManager.scheduler.getQueue().clear();
        return new ExecutionResult("success", "Queue has been cleared.");
    }
}
