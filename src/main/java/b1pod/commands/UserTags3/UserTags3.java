package b1pod.commands.UserTags3;

import b1pod.core.Command;
import b1pod.core.ExecutionResult;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        this.name = "Tag";
        this.description = "Create custom tags!";
        this.guildOnly = true;
        this.triggers = List.of("tag");
        this.children = new Command[] {new TagAdd(), new TagRemove(), new TagList(), new TagEnable(), new TagDisable()};

        try { conn = connect(); } catch (SQLException e) { e.printStackTrace(); }
    }

    @Override
    protected ExecutionResult execute(MessageReceivedEvent event, String[] args)
    {
        return this.getHelp();
    }

    // Command Utilities
    public static Connection connect() throws SQLException
    {
        Logger logger = LoggerFactory.getLogger(UserTags3.class);
        logger.info("Logging into mariadb.");

        return DriverManager.getConnection("jdbc:mariadb://localhost/" + DB_NAME, "root", getSQLPassword());
    }

    public static ResultSet retrieve(String query) throws SQLException
    {
        return conn.prepareStatement(query).executeQuery();
    }

    public static void update(String query) throws SQLException
    {
        conn.prepareStatement(query).executeUpdate();
    }

    public static String getTag(String guildId, String name) throws SQLException
    {
        String query = "SELECT * FROM g" + guildId + " WHERE name='" + name + "'";
        ResultSet result = retrieve(query);

        if (result.next())
            return result.getString("value");

        return null;
    }

    public static boolean tagsEnabled(String guildId) throws SQLException
    {
        return tableExists("g" + guildId);
    }

    public static boolean disabledTableExists(String guildId) throws SQLException
    {
        return tableExists("d" + guildId);
    }

    private static boolean tableExists(String tableName) throws SQLException
    {
        return conn.getMetaData().getTables(null, null, tableName, null).next();
    }
}
