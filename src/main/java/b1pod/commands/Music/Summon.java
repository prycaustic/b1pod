package b1pod.commands.Music;

import b1pod.core.Command;
import b1pod.core.ExecutionResult;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Arrays;
import java.util.List;

import static b1pod.commands.Music.Music.NotInVoiceResult;
import static b1pod.commands.Music.Music.getGuildAudioPlayer;

public class Summon extends Command
{
    public Summon()
    {
        this.name = "Summon";
        this.description = "Have the music bot move to your voice channel.";
        this.triggers = List.of("summon");
    }


    @Override
    protected ExecutionResult execute(MessageReceivedEvent event, String[] args) throws Exception
    {
        if (!event.getMember().getVoiceState().inVoiceChannel()) return NotInVoiceResult;

        event.getGuild().getAudioManager().openAudioConnection(event.getMember().getVoiceState().getChannel());
        return new ExecutionResult("success");
    }
}
