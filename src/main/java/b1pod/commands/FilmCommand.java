package b1pod.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class FilmCommand extends ListenerAdapter
{
    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event)
    {
        if (event.getMessage().getContentRaw().startsWith("/film") && !event.getAuthor().isBot())
        {
            String query = event.getMessage().getContentRaw().substring(6).replace(" ", "%20");
            String imdbID;
            String imdbURL = "https://www.imdb.com/title/";
            JSONObject filmObj;

            try {
                if (query.startsWith("tt"))
                {
                    imdbID = query;
                    imdbURL += imdbID;

                    // Get the film object directly from the ID
                    HttpResponse<String> response = getFilmById(query);
                    filmObj = new JSONObject(response.body());
                }
                else
                {
                    HttpResponse<String> searchResponse = getFilmSearch(query);

                    // Parse the search response using org.json
                    JSONObject searchObj = new JSONObject(searchResponse.body());
                    JSONObject result = searchObj.getJSONArray("Search").getJSONObject(0);

                    // Set the ID to whatever the first result is
                    imdbID = result.getString("imdbID");
                    imdbURL += imdbID;

                    // Get the film object of the first result
                    HttpResponse<String> film = getFilmById(imdbID);
                    filmObj = new JSONObject(film.body());
                }

                // Create the message embed with all the data
                EmbedBuilder filmEmbed = new EmbedBuilder()
                        .setTitle(filmObj.getString("Title") + " (" + filmObj.getString("Year") + ")", imdbURL)
                        .setDescription(filmObj.getString("Plot"))
                        .addField("Genre", filmObj.getString("Genre"), true)
                        .addField("Rated", filmObj.getString("Rated"), true)
                        .addField("Runtime", filmObj.getString("Runtime"), true)
                        .setImage(filmObj.getString("Poster"))
                        .setFooter("via https://www.imdb.com/", "https://ia.media-imdb.com/images/M/MV5BMTczNjM0NDY0Ml5BMl5BcG5nXkFtZTgwMTk1MzQ2OTE@._V1_.png")
                        .setColor(0xf3ce13);
                event.getMessage().replyEmbeds(filmEmbed.build()).mentionRepliedUser(false).queue();
            }
            catch (Exception e)
            {
                event.getMessage().reply("Movie not found!").mentionRepliedUser(false).queue();
                e.printStackTrace();
            }
        }
    }

    public HttpResponse<String> getFilmSearch(String query) throws IOException, InterruptedException
    {
        // Request for search
        HttpRequest search = HttpRequest.newBuilder()
                .uri(URI.create("https://movie-database-imdb-alternative.p.rapidapi.com/?s=" + query + "&r=json&type=movie&page=1"))
                .header("x-rapidapi-key", "138d252103msh56bca906f17f49ep113fd3jsn92039632baad")
                .header("x-rapidapi-host", "movie-database-imdb-alternative.p.rapidapi.com")
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        return HttpClient.newHttpClient().send(search, HttpResponse.BodyHandlers.ofString());
    }

    public HttpResponse<String> getFilmById(String id) throws IOException, InterruptedException
    {
        // Request for specific movie
        HttpRequest tagRequest = HttpRequest.newBuilder()
                .uri(URI.create("https://movie-database-imdb-alternative.p.rapidapi.com/?plot=short&r=json&i=" + id))
                .header("x-rapidapi-key", "138d252103msh56bca906f17f49ep113fd3jsn92039632baad")
                .header("x-rapidapi-host", "movie-database-imdb-alternative.p.rapidapi.com")
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();

        return HttpClient.newHttpClient().send(tagRequest, HttpResponse.BodyHandlers.ofString());
    }
}