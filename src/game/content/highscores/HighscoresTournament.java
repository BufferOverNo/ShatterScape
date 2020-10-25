package game.content.highscores;


import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import core.ServerConstants;
import game.player.Player;

/**
 * Tournament highscores.
 * @author MGT Madness, created on 10-03-2017
 */
public class HighscoresTournament
{
		/**
		 * The class instance.
		 */
		private static final HighscoresTournament instance = new HighscoresTournament();

		/**
		 * Returns a visible encapsulation of the class instance.
		 *
		 * @return The returned encapsulated instance.
		 */
		public final static HighscoresTournament getInstance()
		{
				return instance;
		}

		private final static String HIGHSCORES_LOCATION = "backup/logs/highscores/tournament.json";

		public HighscoresTournament[] highscoresList;

		public String name;

		public int hybridWon;

		public int tribridWon;

		public int meleeWon;

		public HighscoresTournament()
		{
				this.name = "";
				this.hybridWon = 0;
				this.tribridWon = 0;
				this.meleeWon = 0;
		}

		public HighscoresTournament(String username, int hybridWon, int tribridWon, int meleeWon)
		{
				this.name = username;
				this.hybridWon = hybridWon;
				this.tribridWon = tribridWon;
				this.meleeWon = meleeWon;
		}

		public void initiateHighscoresInstance()
		{
				highscoresList = new HighscoresTournament[ServerConstants.HIGHSCORES_PLAYERS_AMOUNT];
				for (int i = 0; i < ServerConstants.HIGHSCORES_PLAYERS_AMOUNT; i++)
				{
						highscoresList[i] = new HighscoresTournament();
				}
				loadFile();
		}

		public void loadFile()
		{
				Path path = Paths.get(HIGHSCORES_LOCATION);
				File file = path.toFile();
				try (FileReader fileReader = new FileReader(file))
				{
						JsonParser fileParser = new JsonParser();
						JsonObject reader = (JsonObject) fileParser.parse(fileReader);

						for (int i = 0; i < ServerConstants.HIGHSCORES_PLAYERS_AMOUNT; i++)
						{
								String name = "";
								int hybridWon = 0;
								int tribridWon = 0;
								int meleeWon = 0;
								if (reader.has("rank " + i + ", name"))
								{
										name = reader.get("rank " + i + ", name").getAsString();
								}
								if (reader.has("rank " + i + ", hybridWon"))
								{
										hybridWon = reader.get("rank " + i + ", hybridWon").getAsInt();
								}
								if (reader.has("rank " + i + ", tribridWon"))
								{
										tribridWon = reader.get("rank " + i + ", tribridWon").getAsInt();
								}
								if (reader.has("rank " + i + ", meleeWon"))
								{
										meleeWon = reader.get("rank " + i + ", meleeWon").getAsInt();
								}
								if (!name.isEmpty())
								{
										highscoresList[i] = new HighscoresTournament(name, hybridWon, tribridWon, meleeWon);
								}
						}
				}
				catch (IOException e)
				{
						e.printStackTrace();
				}
		}

		public void saveFile()
		{
				Path path = Paths.get(HIGHSCORES_LOCATION);
				File file = path.toFile();
				file.getParentFile().setWritable(true);
				try (FileWriter writer = new FileWriter(file))
				{

						Gson builder = new GsonBuilder().setPrettyPrinting().create();
						JsonObject object = new JsonObject();
						for (int i = 0; i < highscoresList.length; i++)
						{
								object.addProperty("rank " + i + ", name", highscoresList[i].name);
								object.addProperty("rank " + i + ", hybridWon", highscoresList[i].hybridWon);
								object.addProperty("rank " + i + ", tribridWon", highscoresList[i].tribridWon);
								object.addProperty("rank " + i + ", meleeWon", highscoresList[i].meleeWon);
						}
						writer.write(builder.toJson(object));
						writer.close();
				}
				catch (Exception e)
				{
				}
		}

		public boolean isOnHighscores(String name)
		{
				for (int i = 0; i < highscoresList.length; i++)
				{

						if (highscoresList[i].name.equalsIgnoreCase(name))
						{
								return true;
						}
				}
				return false;
		}

		public void sortHighscores(Player player)
		{

				if (player.isCombatBot())
				{
						return;
				}
				if (player.isAdministratorRank())
				{
						return;
				}
				int totalWins = player.hybridTournamentsWon + player.tribridTournamentsWon + player.meleeTournamentsWon;

				for (int index = 0; index < highscoresList.length; index++)
				{

						// If i am more than my current self on highscores, then overwrite my own self that is own highscores.
						if (highscoresList[index].name.equalsIgnoreCase(player.getPlayerName()))
						{
								highscoresList[index].hybridWon = player.hybridTournamentsWon;
								highscoresList[index].tribridWon = player.tribridTournamentsWon;
								highscoresList[index].meleeWon = player.meleeTournamentsWon;
								break;
						}

						// If i am not on highscores, index 0 is the last spot on the highscores.
						else if (!isOnHighscores(player.getPlayerName()))
						{
								if (totalWins > highscoresList[0].hybridWon + highscoresList[0].meleeWon + highscoresList[0].tribridWon)
								{
										highscoresList[0] = new HighscoresTournament(player.getPlayerName(), player.hybridTournamentsWon, player.tribridTournamentsWon, player.meleeTournamentsWon);
										break;
								}
						}

				}
				for (int counter = 0; counter < highscoresList.length - 1; counter++)
				{
						for (int index = 0; index < highscoresList.length - 1 - counter; index++)
						{
								if (highscoresList[index].hybridWon + highscoresList[index].tribridWon + highscoresList[index].meleeWon > highscoresList[index + 1].hybridWon + highscoresList[index + 1].tribridWon + highscoresList[index + 1].meleeWon)
								{
										HighscoresTournament temp = highscoresList[index];
										highscoresList[index] = highscoresList[index + 1];
										highscoresList[index + 1] = temp;
								}
						}
				}
		}
}