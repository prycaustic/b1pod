package b1pod.Commands;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserTags2 extends ListenerAdapter
{
    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event)
    {
        String content = event.getMessage().getContentRaw();

        if (!event.getAuthor().isBot())
        {
            if (content.startsWith("/tag"))
            {
                String[] args = content.split(" (?=([^\"]*\"[^\"]*\")*[^\"]*$)");

                switch (args[1])
                {
                    case "add":
                        addTag(args[2], args[3]);
                        break;
                    case "remove":
                        removeTag(args[2]);
                        break;
                }
            }
            else
            {
                searchForTag(content);
            }
        }
    }

    private static Connection connect(String host, String username, String password) throws SQLException
    {
        return DriverManager.getConnection(host, username, password);
    }

    private static ResultSet getSearchResult(Connection conn, String message) throws SQLException
    {
        String query = "SELECT * FROM tags" +
                "WHERE name=\"" + message + "\"";

        return conn.prepareStatement(query).executeQuery();
    }

    private static void addTag(String name, String tag)
    {

    }

    private static void removeTag(String name)
    {
        System.out.println("removing...");
    }

    private static void searchForTag(String content)
    {
        Connection conn = null;

        try
        {
            conn = connect("jdbc:mysql://localhost", "root", "");
        }
        catch (SQLException e)
        {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
        }

        if (conn != null)
        {
            try
            {
                ResultSet result = getSearchResult(conn, content);
            }
            catch (SQLException ignored) { }
        }
    }
}
