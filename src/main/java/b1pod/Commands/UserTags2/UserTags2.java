package b1pod.Commands.UserTags2;

import b1pod.Commands.core.ExecutionResult;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.sql.*;

import static b1pod.Bot.*;

public class UserTags2 extends ListenerAdapter
{
    private static final String DB_NAME = "b1pod_tags";
    private static Message MESSAGE;

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event)
    {
        MESSAGE = event.getMessage();

        if (event.getAuthor().isBot()) return;
        String content = event.getMessage().getContentRaw();
        String guildId = event.getGuild().getId();

        try
        {
            Connection conn = connect();

            if (content.startsWith(getPrefix() + "tag"))
            {
                String[] args = content.split(" (?=([^\"]*\"[^\"]*\")*[^\"]*$)");
                for (int i = 0; i < args.length; i++)
                {
                    args[i] = args[i].replace("\"", "");
                }

                try
                {
                    if (tagsEnabled(conn, guildId))
                    {
                        switch (args[1])
                        {
                            case "add":
                                execute(addTag(conn, guildId, args));
                                break;
                            case "remove":
                                execute(removeTag(conn, guildId, args[2]));
                                break;
                            case "list":
                                execute(listTags(conn, guildId));
                                break;
                            case "help":
                                execute(displayHelp());
                                break;
                            case "enable":
                                event.getMessage().reply(getEmote("warning") +
                                        " Tags already enabled in this server.").mentionRepliedUser(false).queue();
                                break;
                            case "disable":
                                execute(disableTags(conn, guildId));
                                break;
                            default:
                                event.getMessage().reply(getEmote("warning") +
                                        " Unknown command, use `" + getPrefix() + "tag help` for more info.")
                                        .mentionRepliedUser(false).queue();
                                break;
                        }
                    }
                    else if (args[1].equals("enable"))
                    {
                        execute(enableTags(conn, guildId));
                    }
                    else
                    {
                        event.getMessage()
                                .reply("Tags are not enabled in this server. Use `" + getPrefix() + "tag enable` to enable.")
                                .mentionRepliedUser(false).queue();
                    }
                }
                catch (IndexOutOfBoundsException e)
                {
                    event.getMessage().reply("Incorrect syntax, use `" + getPrefix() + "tag help` for more info.")
                            .mentionRepliedUser(false).queue();
                }
                catch (SQLException e)
                {
                    event.getMessage().addReaction(getEmote("failure")).queue();
                    e.printStackTrace();
                }
            }
            else
            {
                if (tagsEnabled(conn, event.getGuild().getId()))
                {
                    String tag = getTag(conn, event.getGuild().getId(), content);

                    if (tag != null)
                        event.getChannel().sendMessage(tag).queue();
                }
            }

            conn.close();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    private void execute(ExecutionResult result)
    {
        String emoji = result.getEmoji(), reason = result.getReason();
        MessageEmbed embed = result.getEmbed();

        if (emoji != null && reason == null)
            MESSAGE.addReaction(getEmote(emoji)).queue();
        if (emoji != null && reason != null)
            MESSAGE.reply(getEmote(emoji) + " " + reason).mentionRepliedUser(false).queue();
        if (result.getEmbed() != null)
            MESSAGE.replyEmbeds(embed).mentionRepliedUser(false).queue();
    }

    private Connection connect() throws SQLException
    {
        return DriverManager.getConnection("jdbc:mariadb://localhost/" + DB_NAME, "root", getSQLPassword());
    }

    private ResultSet retrieve(Connection conn, String query) throws SQLException
    {
        return conn.prepareStatement(query).executeQuery();
    }

    private void update(Connection conn, String query) throws SQLException
    {
        conn.prepareStatement(query).executeUpdate();
    }

    private ExecutionResult enableTags(Connection conn, String guildId) throws SQLException
    {
        if (tagsEnabled(conn, guildId)) return new ExecutionResult("failure", "Tags already enabled.");
        if (disabledTableExists(conn, guildId))
        {
            String query = "RENAME TABLE `d" + guildId + "` TO `g" + guildId + "`;";

            update(conn, query);
        }
        else
        {
            String query = "CREATE TABLE `g" + guildId + "` (" +
                    " `name` VARCHAR(99) NOT NULL," +
                    " `value` VARCHAR(200) NOT NULL," +
                    " UNIQUE INDEX `name` (`name`)" +
                    " );";

            update(conn, query);
        }

        return new ExecutionResult("success");
    }

    private ExecutionResult disableTags(Connection conn, String guildId) throws SQLException
    {
        if (!tagsEnabled(conn, guildId)) return new ExecutionResult("failure", "Tags are already disabled.");
        String query = "RENAME TABLE `g" + guildId +"` TO `d" + guildId + "`;";

        update(conn, query);
        return new ExecutionResult("success", "Tags have been disabled, use ``"
                + getPrefix() + "tag enable``  at any time to re-enable them.");
    }

    private ExecutionResult addTag(Connection conn, String guildId, String[] args) throws SQLException
    {
        if (args.length != 4) return new ExecutionResult("warning", "Incorrect syntax, use `"
                + getPrefix() + "tag help` for more info.");
        String name = args[2], value = args[3];

        if (getTag(conn, guildId, name) != null) return new ExecutionResult("warning", " Tag already exists.");
        String query = "INSERT INTO g" + guildId + " VALUES ('" + name + "', '" + value + "');";

        update(conn, query);
        return new ExecutionResult("success");
    }

    private ExecutionResult removeTag(Connection conn, String guildId, String name) throws SQLException
    {
        if (getTag(conn, guildId, name) == null) return new ExecutionResult("failure", "Tag does not exist.");
        String query = "DELETE FROM `" + DB_NAME + "`.`g" + guildId + "` WHERE (`name` = '" + name + "');";

        update(conn, query);
        return new ExecutionResult("success");
    }

    private ExecutionResult listTags(Connection conn, String guildId) throws SQLException
    {
        if (!tagsEnabled(conn, guildId)) return new ExecutionResult("failure", "No tags found.");
        String query = "SELECT * FROM g" + guildId;
        ResultSet result = retrieve(conn, query);
        EmbedBuilder listEmbed = new EmbedBuilder().setTitle("Tags").setColor(getEmbedColor());

        while (result.next())
        {
            listEmbed.addField(result.getString("name"), result.getString("value"), true);
        }

        return new ExecutionResult(listEmbed.build());
    }

    private ExecutionResult displayHelp()
    {
        EmbedBuilder helpEmbed = new EmbedBuilder()
                .setTitle("Tags Help")
                .setDescription("Create custom tags!")
                .addField("Add", "``" + getPrefix() + "tag add <name> <value>``" +
                        "\n Tag names and values with more than one word should be surrounded with quotes.", false)
                .addField("Remove", "``" + getPrefix() + "tag remove <name>``" +
                        "\nDeletes the specified tag.", false)
                .addField("List", "``" + getPrefix() + "tag list``" +
                        "\nReturns a list of usable tags.", false)
                .addField("Help", "Shows this help message.", false)
                .addField("Usage", "Just type the name of a tag into chat.", false)
                .setColor(getEmbedColor());

        return new ExecutionResult(helpEmbed.build());
    }

    private boolean tagsEnabled(Connection conn, String guildId) throws SQLException
    {
        ResultSet result = conn.getMetaData().getTables(null, null, "g" + guildId, null);

        return result.next();
    }

    private boolean disabledTableExists(Connection conn, String guildId) throws SQLException
    {
        ResultSet result = conn.getMetaData().getTables(null, null, "d" + guildId, null);

        return result.next();
    }

    private String getTag(Connection conn, String guildId, String name) throws SQLException
    {
        String query = "SELECT * FROM g" + guildId + " WHERE name='" + name + "'";
        ResultSet result = retrieve(conn, query);

        if (result.next())
            return result.getString("value");

        return null;
    }
}