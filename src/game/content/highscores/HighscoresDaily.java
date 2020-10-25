package game.content.highscores;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import core.ServerConstants;
import game.content.miscellaneous.Announcement;
import game.content.miscellaneous.CommunityEvent;
import game.player.Player;
import utility.FileUtility;

/**
 * Daily highscores.
 * @author MGT Madness, created on 21-03-2017.
 */
public class HighscoresDaily
{
		/**
		 * The class instance.
		 */
		private static final HighscoresDaily instance = new HighscoresDaily();

		/**
		 * Returns a visible encapsulation of the class instance.
		 *
		 * @return The returned encapsulated instance.
		 */
		public final static HighscoresDaily getInstance()
		{
				return instance;
		}

		private final static String HIGHSCORES_LOCATION = "backup/logs/highscores/daily.json";

		public HighscoresDaily[] highscoresList;

		public String name;

		public int main;

		public int extra1;

		public String extra2;

		public HighscoresDaily()
		{
				this.name = "";
				this.main = 0;
				this.extra1 = 0;
				this.extra2 = "";
		}

		public HighscoresDaily(String username, int main, int extra1, String extra2)
		{
				this.name = username;
				this.main = main;
				this.extra1 = extra1;
				this.extra2 = extra2;
		}

		public void initiateHighscoresInstance()
		{
				highscoresList = new HighscoresDaily[ServerConstants.HIGHSCORES_PLAYERS_AMOUNT];
				for (int i = 0; i < ServerConstants.HIGHSCORES_PLAYERS_AMOUNT; i++)
				{
						highscoresList[i] = new HighscoresDaily();
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
								int main = 0;
								int extra1 = 0;
								String extra2 = "";
								if (reader.has("rank " + i + ", name"))
								{
										name = reader.get("rank " + i + ", name").getAsString();
								}
								if (reader.has("rank " + i + ", main"))
								{
										main = reader.get("rank " + i + ", main").getAsInt();
								}
								if (reader.has("rank " + i + ", extra1"))
								{
										extra1 = reader.get("rank " + i + ", extra1").getAsInt();
								}
								if (reader.has("rank " + i + ", extra2"))
								{
										extra2 = reader.get("rank " + i + ", extra2").getAsString();
								}
								if (!name.isEmpty())
								{
										highscoresList[i] = new HighscoresDaily(name, main, extra1, extra2);
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
								object.addProperty("rank " + i + ", main", highscoresList[i].main);
								object.addProperty("rank " + i + ", extra1", highscoresList[i].extra1);
								object.addProperty("rank " + i + ", extra2", highscoresList[i].extra2);
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

		public void sortHighscores(Player player, int mainVariable, int secondVariable, String extra)
		{
				if (player.isAdministratorRank())
				{
						return;
				}

				if (player.isCombatBot())
				{
						return;
				}
				for (int index = 0; index < highscoresList.length; index++)
				{

						// If i am more than my current self on highscores, then overwrite my own self that is own highscores.
						if (highscoresList[index].name.equalsIgnoreCase(player.getPlayerName()))
						{
								highscoresList[index].main += mainVariable;
								highscoresList[index].extra1 = secondVariable;
								highscoresList[index].extra2 = extra;
								break;
						}

						// If i am not on highscores, index 0 is the last spot on the highscores.
						else if (!isOnHighscores(player.getPlayerName()))
						{
								if (mainVariable > highscoresList[0].main)
								{
										highscoresList[0] = new HighscoresDaily(player.getPlayerName(), mainVariable, secondVariable, extra);
										break;
								}
						}

				}
				for (int counter = 0; counter < highscoresList.length - 1; counter++)
				{
						for (int index = 0; index < highscoresList.length - 1 - counter; index++)
						{
								if (highscoresList[index].main > highscoresList[index + 1].main)
								{
										HighscoresDaily temp = highscoresList[index];
										highscoresList[index] = highscoresList[index + 1];
										highscoresList[index + 1] = temp;
								}
						}
				}
		}


		public final String[] DAILY_HIGHSCORES_LIST = {"Boss kills", "F2p kills", "Hybrid kills", "Target kills"};

		public String currentDailyHighscores = DAILY_HIGHSCORES_LIST[0];

		public final String FILE_LOCATION = "backup/logs/highscores/daily type.txt";

		public void saveDailyHighscoresType()
		{
				FileUtility.deleteAllLines(FILE_LOCATION);
				try
				{
						BufferedWriter bw = null;
						bw = new BufferedWriter(new FileWriter(FILE_LOCATION, true));
						bw.write(currentDailyHighscores);
						bw.newLine();
						bw.flush();
						bw.close();
				}
				catch (IOException ioe)
				{
						ioe.printStackTrace();
				}
		}

		public void readDailyHighscoresType()
		{
				try
				{
						BufferedReader file = new BufferedReader(new FileReader(FILE_LOCATION));
						String line;
						while ((line = file.readLine()) != null)
						{
								if (!line.isEmpty())
								{
										currentDailyHighscores = line;
								}
						}
						file.close();
				}
				catch (Exception e)
				{
				}
		}

		public void informHighscores(Player player)
		{
				player.getPA().sendMessage(ServerConstants.DARK_BLUE + "Daily highscores: " + currentDailyHighscores + ". #1 spot receives 25k blood money.");
		}

		public String getTimeLeft()
		{
				//19 hours 40 minutes
				//40 minutes.


				DateFormat dateFormat = new SimpleDateFormat("hh:mm a");
				Calendar cal = Calendar.getInstance();
				String time = dateFormat.format(cal.getTime());
				String[] parseTime = time.split(":");
				String temp = parseTime[0];
				if (temp.startsWith("0"))
				{
						temp = temp.replaceFirst("0", "");
				}
				int hours = Integer.parseInt(temp);
				temp = parseTime[1];
				String another[] = temp.split(" ");
				int minutes = Integer.parseInt(another[0]);
				String day = another[1];

				int hoursResult = 0;
				int minutesResult = 0;

				if (hours == 12)
				{
						hours = 0;
				}

				if (day.equals("AM"))
				{
						hoursResult = (11 - hours) + 12;
						minutesResult = 60 - minutes;
				}
				else if (day.equals("PM"))
				{
						hoursResult = 11 - hours;
						minutesResult = 60 - minutes;
				}

				if (hoursResult > 0)
				{
						if (System.currentTimeMillis() > announceTime + 2000)
						{
								announceTime = System.currentTimeMillis() + (hoursResult * (60000 * 60)) + (minutesResult * 60000);
						}
						return hoursResult + "h " + minutesResult + "m";
				}
				else
				{
						if (System.currentTimeMillis() > announceTime + 2000)
						{
								announceTime = System.currentTimeMillis() + (minutesResult * 60000);
						}
						return minutesResult + "m";
				}
		}

		public long announceTime;

		public boolean claimedReward;

		public void dateChanged()
		{
				if (highscoresList[29].name.isEmpty())
				{
						return;
				}
				claimedReward = true;
				announceTime = 0;
				Announcement.announce(highscoresList[29].name + " has won the daily " + currentDailyHighscores + " highscores and is awarded 25k bm!", ServerConstants.DARK_BLUE);
				CommunityEvent.eventNames.add(highscoresList[29].name + "-25000");

				for (int index = 0; index < DAILY_HIGHSCORES_LIST.length; index++)
				{
						if (DAILY_HIGHSCORES_LIST[index].equals(currentDailyHighscores))
						{
								int newIndex = index + 1;
								if (newIndex > DAILY_HIGHSCORES_LIST.length - 1)
								{
										newIndex = 0;
								}
								currentDailyHighscores = DAILY_HIGHSCORES_LIST[newIndex];
								break;
						}
				}
				Announcement.announce("The new daily highscores is " + currentDailyHighscores + ", the #1 spot will claim 25k bm in 24h!", ServerConstants.DARK_BLUE);

				highscoresList = new HighscoresDaily[ServerConstants.HIGHSCORES_PLAYERS_AMOUNT];
				for (int i = 0; i < ServerConstants.HIGHSCORES_PLAYERS_AMOUNT; i++)
				{
						highscoresList[i] = new HighscoresDaily();
				}
		}
}