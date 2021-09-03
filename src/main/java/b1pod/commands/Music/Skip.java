package b1pod.commands.Music;

import b1pod.core.Command;
import b1pod.core.ExecutionResult;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Arrays;

import static b1pod.commands.Music.Music.NotInVoiceResult;
import static b1pod.commands.Music.Music.getGuildAudioPlayer;

public class Skip extends Command
{
    public Skip()
    {
        this.name = "Skip";
        this.syntax = "<number>";
        this.description = "Skip one or more songs in the queue." +
                "\n``<number>`` is the number of songs to skip (default 1).";
        this.triggers = Arrays.asList("skip", "s");
    }

    @Override
    protected ExecutionResult execute(MessageReceivedEvent event, String[] args)
    {
        if (!event.getMember().getVoiceState().inVoiceChannel()) return NotInVoiceResult;
        int number = 1;
        if (args.length > 1)
            number = Integer.parseInt(args[1]);
        if (number < 1) return new ExecutionResult("warning", "``<number>`` must be greater than 0.");

        GuildMusicManager musicManager = getGuildAudioPlayer(event.getGuild());

        for (int i = 0; i < number; i++)
            musicManager.scheduler.nextTrack();
        return new ExecutionResult("success");
    }
}
