package game.content.miscellaneous;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

import game.content.interfaces.InterfaceAssistant;
import game.player.Player;

/**
 * Guide book interface.
 * @author MGT Madness, created on 08-11-2016.
 */
public class GuideBook
{

		/**
		 * Store the titles of the guides.
		 */
		public static ArrayList<String> titles = new ArrayList<String>();

		/**
		 * Store the content of the guides.
		 */
		public static ArrayList<String> content = new ArrayList<String>();

		public static void loadGuideDataFile()
		{
				try
				{
						BufferedReader file = new BufferedReader(new FileReader("data/content/guide.txt"));
						String line;
						String contentStrings = "";
						while ((line = file.readLine()) != null)
						{
								if (!line.isEmpty())
								{
										if (line.contains("#"))
										{
												if (!contentStrings.isEmpty())
												{
														content.add(contentStrings);
												}
												contentStrings = "";
												titles.add(line.substring(1));
										}
										else
										{
												line = line.replace("--", " ");
												contentStrings = contentStrings + line + ";";
										}
								}
						}
						content.add(contentStrings); // Has to be here so the last guide can be saved properly.
						file.close();
				}
				catch (Exception e)
				{
				}
		}

		public static void displayGuideInterface(Player player)
		{
				for (int index = 0; index < titles.size(); index++)
				{
						player.getPA().sendFrame126(titles.get(index), 22571 + index);
				}
				InterfaceAssistant.setFixedScrollMax(player, 22570, (int) (titles.size() * 15.2));
				player.getPA().displayInterface(22550);
				player.getPA().sendFrame126("", 22556);
				player.getPA().sendFrame126("", 22557);
				player.getPA().sendFrame126("", 22558);
				player.getPA().sendFrame126("", 22559);
				player.getPA().sendFrame126("", 22560);
				player.getPA().sendFrame126("", 22561);
				player.getPA().sendFrame126("", 22562);
				player.getPA().sendFrame126("", 22563);
				player.getPA().sendFrame126("", 22564);
				player.getPA().sendFrame126("", 22565);
				player.getPA().sendFrame126("", 22566);
				player.getPA().sendFrame126("", 22567);
				player.getPA().sendFrame126("", 22568);
				player.getPA().sendFrame126("", 22569);
		}

		public static boolean isGuideInterfaceButton(Player player, int buttonId)
		{
				if (buttonId >= 88043 && buttonId <= 88072)
				{
						// Pvm Rare Drops.
						if (buttonId == 88043)
						{
								NpcDropTableInterface.displayInterface(player);
								return true;
						}
						int indexButton = (buttonId - 88043);
						if (indexButton > titles.size() - 1)
						{
								return true;
						}
						String[] parseContent = content.get(indexButton).split(";");
						int lastIndexUsed = 0;
						for (int index = 0; index < parseContent.length; index++)
						{
								String string = parseContent[index];
								if (string.isEmpty())
								{
										break;
								}
								player.getPA().sendFrame126(string, 22556 + index);
								lastIndexUsed = 22556 + index;
						}
						lastIndexUsed++;
						InterfaceAssistant.clearFrames(player, lastIndexUsed, 22569);
						player.getPA().setTextClicked(22571 + indexButton, true);
				}

				return false;
		}
}
