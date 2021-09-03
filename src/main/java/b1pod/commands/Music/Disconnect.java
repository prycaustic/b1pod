package b1pod.commands.Music;

import b1pod.core.Command;
import b1pod.core.ExecutionResult;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.managers.AudioManager;

import java.util.Arrays;

import static b1pod.Bot.getEmote;
import static b1pod.commands.Music.Music.NotInVoiceResult;
import static b1pod.commands.Music.Music.getGuildAudioPlayer;

public class Disconnect extends Command
{
    public Disconnect()
    {
        this.name = "Disconnect";
        this.description = "Leave the voice channel.";
        this.triggers = Arrays.asList("disconnect", "dc", "leave");
    }

    @Override
    protected ExecutionResult execute(MessageReceivedEvent event, String[] args) throws Exception
    {
        if (!event.getMember().getVoiceState().inVoiceChannel()) return NotInVoiceResult;
        AudioManager audioManager = event.getGuild().getAudioManager();

        if (audioManager.isConnected())
        {
            audioManager.closeAudioConnection();
            return new ExecutionResult("wave");
        }
        return null;
    }
}
