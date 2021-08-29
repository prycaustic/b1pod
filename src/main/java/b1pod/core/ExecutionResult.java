package b1pod.core;

import net.dv8tion.jda.api.entities.MessageEmbed;

public class ExecutionResult
{
    private String Emoji = null;
    private String Reason = null;
    private MessageEmbed Embed = null;

    public ExecutionResult(MessageEmbed embed)
    {
        this.Embed = embed;
    }

    public ExecutionResult(String emoji, String reason)
    {
        this.Emoji = emoji;
        this.Reason = reason;
    }

    public ExecutionResult(String emoji)
    {
        this.Emoji = emoji;
    }


    public String getEmoji()
    {
        return Emoji;
    }

    public String getReason()
    {
        return Reason;
    }

    public MessageEmbed getEmbed()
    {
        return Embed;
    }
}
