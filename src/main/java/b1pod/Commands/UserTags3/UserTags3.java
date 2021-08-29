package b1pod.Commands.UserTags3;

import b1pod.Commands.core.Command;
import b1pod.Commands.core.ExecutionResult;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static b1pod.Bot.getSQLPassword;

public class UserTags3 extends Command
{
    protected static Connection conn;
    protected static String DB_NAME = "b1pod_tags";

    public UserTags3()
    {
        this.name = "User Tags";
        this.syntax = "null";
        this.description = "Create custom tags!";
        this.guildOnly = true;
        this.triggers = List.of("tag");
        this.children = new Command[] {new TagAdd(this), new TagRemove(this), new TagList(this),
                new TagEnable(this), new TagDisable(this)};

        try { conn = connect(); } catch (SQLException e) { e.printStackTrace(); }
    }

    @Override
    protected ExecutionResult execute(MessageReceivedEvent event, String[] args)
    {
        return this.getHelp();
    }

    public static String getDB_NAME()
    {
        return DB_NAME;
    }

    public static Connection getConn()
    {
        return conn;
    }

    // Command Utilities
    public static Connection connect() throws SQLException
    {
        return DriverManager.getConnection("jdbc:mariadb://localhost/" + DB_NAME, "root", getSQLPassword());
    }

    public static ResultSet retrieve(Connection conn, String query) throws SQLException
    {
        return conn.prepareStatement(query).executeQuery();
    }

    public static void update(Connection conn, String query) throws SQLException
    {
        conn.prepareStatement(query).executeUpdate();
    }

    public static String getTag(Connection conn, String guildId, String name) throws SQLException
    {
        String query = "SELECT * FROM g" + guildId + " WHERE name='" + name + "'";
        ResultSet result = retrieve(conn, query);

        if (result.next())
            return result.getString("value");

        return null;
    }

    public static boolean tagsEnabled(Connection conn, String guildId) throws SQLException
    {
        ResultSet result = conn.getMetaData().getTables(null, null, "g" + guildId, null);

        return result.next();
    }

    public static boolean disabledTableExists(Connection conn, String guildId) throws SQLException
    {
        ResultSet result = conn.getMetaData().getTables(null, null, "d" + guildId, null);

        return result.next();
    }
}
