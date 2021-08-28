package b1pod.Commands;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.sql.*;

import static b1pod.Bot.getEmote;

public class UserTags2 extends ListenerAdapter
{
    private final String MYSQL_PWD;

    public UserTags2(String password)
    {
        MYSQL_PWD = password;
    }

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event)
    {
        if (event.getAuthor().isBot()) return;
        String content = event.getMessage().getContentRaw();

        if (content.startsWith("/tag"))
        {
            String[] args = content.split(" (?=([^\"]*\"[^\"]*\")*[^\"]*$)");
            for (int i = 0; i < args.length; i++)
            {
                args[i] = args[i].replace("\"", "");
            }

            if (tagsEnabled(event.getGuild()))
            {
                try
                {
                    switch (args[1])
                    {
                        case "add":
                            addTag(event.getMessage(), args[2], args[3]);
                            break;
                        case "remove":
                            removeTag(args[2]);
                            break;
                    }
                }
                catch (IndexOutOfBoundsException e)
                {
                    event.getMessage().reply("Incorrect syntax, use `/tag help` for more info.")
                            .mentionRepliedUser(false).queue();
                }
            }
            else if (args[1].equals("enable"))
            {
                enableTags(event.getMessage());
            }
            else
            {
                event.getMessage()
                        .reply("Tags are not enabled in this server. Use `/tag enable` to enable.")
                        .mentionRepliedUser(false).queue();
            }
        }
        else
        {
            if (tagsEnabled(event.getGuild()))
            {
                String tag = searchForTag(event.getChannel(), content);

                if (tag != null)
                    event.getChannel().sendMessage(tag).queue();
            }
        }
    }

    private void enableTags(Message message)
    {
        try
        {
            Connection conn = connect();

            if (conn != null)
            {
                String query = "CREATE TABLE `test_schema`.`g" + message.getGuild().getId() + "` (\n" +
                        "  `id` INT NOT NULL AUTO_INCREMENT,\n" +
                        "  `name` VARCHAR(99) NOT NULL,\n" +
                        "  `value` VARCHAR(200) NOT NULL,\n" +
                        "  PRIMARY KEY (`id`));\n";

                conn.prepareStatement(query).executeUpdate();
                message.addReaction(getEmote("success")).queue();
            }
        }
        catch (SQLException e)
        {
            message.addReaction(getEmote("failure")).queue();
            e.printStackTrace();
        }
    }

    private Connection connect() throws SQLException
    {
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/test_schema", "root", MYSQL_PWD);
    }

    private ResultSet getSearchResult(Connection conn, String table, String message) throws SQLException
    {
        String query = "SELECT * FROM " + table + " WHERE name=\"" + message + "\"";

        return conn.prepareStatement(query).executeQuery();
    }

    private void addTag(Message message, String name, String tag)
    {
        try
        {
            Connection conn = connect();

            if (conn != null)
            {
                if (searchForTag(message.getTextChannel(), name) == null)
                {
                    String query = "INSERT INTO g" + message.getGuild().getId() + " (id, name, value)" +
                            "\nVALUES (NULL, \"" + name + "\", \"" + tag + "\");";

                    conn.prepareStatement(query).executeUpdate();
                    message.addReaction(getEmote("success")).queue();
                }
                else
                {
                    message.reply("Tag already exists. Please delete the original first.")
                            .mentionRepliedUser(false).queue();
                }
            }
        }
        catch (SQLException e)
        {
            message.addReaction(getEmote("failure")).queue();
            e.printStackTrace();
        }
    }

    private void removeTag(String name)
    {
        System.out.println("removing...");
    }

    private String searchForTag(TextChannel channel, String content)
    {
        Connection conn = null;

        try
        {
            conn = connect();
        }
        catch (SQLException e)
        {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
        }

        if (conn != null)
        {
            try
            {
                ResultSet result = getSearchResult(conn, "g" + channel.getGuild().getId(), content);

                if (result.next())
                    return result.getString("value");
            }
            catch (SQLException ignored) { }
        }
        return null;
    }

    private boolean tagsEnabled(Guild guild)
    {
        try
        {
            Connection conn = connect();

            if (conn != null)
            {
                DatabaseMetaData meta = conn.getMetaData();
                ResultSet result = meta.getTables(null, null, "g" + guild.getId(), null);

                if (result.next())
                    return true;
            }
        }
        catch (SQLException ignored) { }

        return false;
    }
}
