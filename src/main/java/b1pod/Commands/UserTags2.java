package b1pod.Commands;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.util.Arrays;

import static b1pod.Bot.*;

public class UserTags2 extends ListenerAdapter
{
    private static final String DB_NAME = "test_schema";

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event)
    {
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
                                execute(event.getMessage(), addTag(conn, guildId, args[2], args[3]));
                                break;
                            case "remove":
                                execute(event.getMessage(), removeTag(conn, guildId, args[2]));
                                break;
                        }
                    }
                    else if (args[1].equals("enable"))
                    {
                        execute(event.getMessage(), enableTags(conn, guildId));
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

    private void execute(Message message, String[] result)
    {
        message.addReaction(getEmote(result[0])).queue();
        if (result[1] != null)
            message.reply(result[1]).mentionRepliedUser(false).queue();
    }

    private Connection connect() throws SQLException
    {
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/" + DB_NAME, "root", getSQLPassword());
    }

    private ResultSet retrieve(Connection conn, String query) throws SQLException
    {
        return conn.prepareStatement(query).executeQuery();
    }

    private void update(Connection conn, String query) throws SQLException
    {
        conn.prepareStatement(query).executeUpdate();
    }

    private String[] addTag(Connection conn, String guildId, String name, String value) throws SQLException
    {
        if (getTag(conn, guildId, name) != null) return new String[] {"failure", "Tag already exists."};
        String query = "INSERT INTO g" + guildId + " (name, value)" +
                "\nVALUES ('" + name + "', '" + value + "');";

        update(conn, query);
        return new String[] {"success", null};
    }

    private String[] removeTag(Connection conn, String guildId, String name) throws SQLException
    {
        if (getTag(conn, guildId, name) == null) return new String[] {"failure", "Tag does not exist."};
        String query = "DELETE FROM `" + DB_NAME + "`.`g" + guildId + "` WHERE (`name` = '" + name + "');";

        update(conn, query);
        return new String[] {"success", null};
    }

    private String[] enableTags(Connection conn, String guildId) throws SQLException
    {
        if (tagsEnabled(conn, guildId)) return new String[] {"failure", "Tags already enabled."};
        String query = "CREATE TABLE `" + DB_NAME + "`.`g" + guildId + "` (\n" +
                "  `name` VARCHAR(99) NOT NULL,\n" +
                "  `value` VARCHAR(200) NOT NULL,\n" +
                "  PRIMARY KEY (`name`),\n" +
                "  UNIQUE INDEX `name_UNIQUE` (`name` ASC) VISIBLE);";

        update(conn, query);
        return new String[] {"success", null};
    }

    private boolean tagsEnabled(Connection conn, String guildId) throws SQLException
    {
        ResultSet result = conn.getMetaData().getTables(null, null, "g" + guildId, null);

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
