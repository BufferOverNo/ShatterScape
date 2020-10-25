package game.item;

import java.io.BufferedReader;
import java.io.FileReader;

public class BloodMoneyPrice
{
		public static final BloodMoneyPrice[] DEFINITIONS = new BloodMoneyPrice[20000];


		public BloodMoneyPrice(int itemId, int bloodMoneyPrice, int harvestedPrice, boolean spawnFree)
		{
				this.itemId = itemId;
				this.bloodMoneyPrice = bloodMoneyPrice;
				this.harvestedPrice = harvestedPrice;
				this.spawnFree = spawnFree;
		}

		public static BloodMoneyPrice[] getDefinitions()
		{
				return DEFINITIONS;
		}

		public final int itemId;

		public final int bloodMoneyPrice;

		public final int harvestedPrice;

		/**
		 * If true, do not withdraw from bank.
		 */
		public final boolean spawnFree;

		public static int getBloodMoneyPrice(int itemId)
		{
				itemId = ItemAssistant.getUnNotedItem(itemId);
				if (getDefinitions()[itemId] == null)
				{
						return 0;
				}
				return getDefinitions()[itemId].bloodMoneyPrice;
		}

		public static void loadBloodMoneyPrice()
		{
				try
				{

						int itemId = 0;
						int bloodMoneyPrice = 0;
						int harvestedSellValue = 0;
						boolean harvested = false;

						BufferedReader file = new BufferedReader(new FileReader("./data/items/blood money price.txt"));
						String line;

						while ((line = file.readLine()) != null)
						{
								if (line.startsWith("// Harvested items"))
								{
										harvested = true;
								}
								if (!line.isEmpty() && !line.startsWith("//"))
								{
										String[] parse = line.split(" ");
										itemId = Integer.parseInt(parse[0]);
										bloodMoneyPrice = 0;
										harvestedSellValue = 0;
										if (harvested)
										{
												harvestedSellValue = Integer.parseInt(parse[1].replaceAll(",", ""));
												bloodMoneyPrice = BloodMoneyPrice.getBloodMoneyPrice(itemId);
										}
										else
										{
												bloodMoneyPrice = Integer.parseInt(parse[1].replaceAll(",", ""));
										}
										BloodMoneyPrice.DEFINITIONS[itemId] = new BloodMoneyPrice(itemId, bloodMoneyPrice, harvestedSellValue, false);
								}
						}

						file.close();
				}
				catch (Exception e)
				{
				}


				try
				{


						BufferedReader file = new BufferedReader(new FileReader("./data/items/free items.txt"));
						String line;
						while ((line = file.readLine()) != null)
						{
								if (!line.isEmpty() && !line.startsWith("//"))
								{
										String[] parse = line.split(" ");
										int itemId = Integer.parseInt(parse[0]);
										int harvestPrice = 0;
										if (BloodMoneyPrice.getDefinitions()[itemId] != null)
										{
												harvestPrice = BloodMoneyPrice.getDefinitions()[itemId].harvestedPrice;
										}
										BloodMoneyPrice.DEFINITIONS[itemId] = new BloodMoneyPrice(itemId, BloodMoneyPrice.getBloodMoneyPrice(itemId), harvestPrice, true);
								}
						}

						file.close();
				}
				catch (Exception e)
				{
				}
		}
}
