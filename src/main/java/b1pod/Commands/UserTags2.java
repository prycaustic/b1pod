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

        try
        {
            Connection conn = connect();

            if (content.startsWith("/tag"))
            {
                String[] args = content.split(" (?=([^\"]*\"[^\"]*\")*[^\"]*$)");
                for (int i = 0; i < args.length; i++)
                {
                    args[i] = args[i].replace("\"", "");
                }

                try
                {
                    if (tagsEnabled(conn, event.getGuild().getId()))
                    {
                        try
                        {
                            switch (args[1])
                            {
                                case "add":
                                    addTag(conn, event.getGuild().getId(), args[2], args[3]);
                                    break;
                                case "remove":
                                    removeTag(conn, event.getGuild().getId(), args[2]);
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
                        enableTags(conn, event.getMessage());
                    }
                    else
                    {
                        event.getMessage()
                                .reply("Tags are not enabled in this server. Use `/tag enable` to enable.")
                                .mentionRepliedUser(false).queue();
                    }

                    conn.close();
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

    private Connection connect() throws SQLException
    {
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/test_schema", "root", MYSQL_PWD);
    }

    private ResultSet retrieve(Connection conn, String query) throws SQLException
    {
        return conn.prepareStatement(query).executeQuery();
    }

    private void update(Connection conn, String query) throws SQLException
    {
        conn.prepareStatement(query).executeUpdate();
    }

    private void addTag(Connection conn, String guildId, String name, String value) throws SQLException
    {
        String query = "INSERT INTO g" + guildId + " (id, name, value)" +
            "\nVALUES (NULL, \"" + name + "\", \"" + value + "\");";

        update(conn, query);
    }

    private void removeTag(Connection conn, String guildId, String name) throws SQLException
    {
        String query = "SELECT * FROM g" + guildId + " WHERE name=\"" + name + "\"";

        update(conn, query);
    }

    private void enableTags(Connection conn, Message message) throws SQLException
    {
        String query = "CREATE TABLE `test_schema`.`g" + message.getGuild().getId() + "` (\n" +
                "  `id` INT NOT NULL AUTO_INCREMENT,\n" +
                "  `name` VARCHAR(99) NOT NULL,\n" +
                "  `value` VARCHAR(200) NOT NULL,\n" +
                "  PRIMARY KEY (`id`));\n";

        update(conn, query);
    }

    private boolean tagsEnabled(Connection conn, String guildId) throws SQLException
    {
        ResultSet result = retrieve(conn, "SELECT * FROM g" + guildId);

        return result.next();
    }

    private String getTag(Connection conn, String guildId, String name) throws SQLException
    {
        String query = "SELECT * FROM g" + guildId + " WHERE name=\"" + name + "\"";
        ResultSet result = retrieve(conn, query);

        if (result.next())
            return result.getString("value");

        return null;
    }
}
