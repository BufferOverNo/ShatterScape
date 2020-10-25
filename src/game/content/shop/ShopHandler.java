package game.content.shop;

import java.io.BufferedReader;
import java.io.FileReader;

/**
 * Shops
 **/

public class ShopHandler
{

		public static int MaxShops = 90;

		public final static int MAX_ITEM_IN_SHOP = 150;

		public static int[][] ShopItems = new int[MaxShops][MAX_ITEM_IN_SHOP];

		public static int[][] ShopItemsN = new int[MaxShops][MAX_ITEM_IN_SHOP];

		public static int[][] shopPrice = new int[MaxShops][MAX_ITEM_IN_SHOP];

		public static String[] ShopName = new String[MaxShops];


		/**
		 * Items with original price when selling to shop.
		 */
		public final static int[] standardPriceItems = new int[60];


		/**
		 * Items cannot be sold to the shop or price checked to sell to the shop.
		 */
		public final static int[] cannotSellToShopItems = new int[25];

		private static void loadStandardPriceItems()
		{
				String line;
				BufferedReader Checker = null;
				try
				{
						int index = 0;
						Checker = new BufferedReader(new FileReader("./data/items/standard price items.txt"));
						while ((line = Checker.readLine()) != null)
						{
								if (line.startsWith("//") || line.isEmpty())
								{
										continue;
								}
								String[] parse = line.split(" ");
								standardPriceItems[index] = Integer.parseInt(parse[0]);
								index++;
						}
						Checker.close();
				}
				catch (Exception e)
				{
						e.printStackTrace();
				}
		}

		private static void loadCannotSellToShopItems()
		{
				String line;
				BufferedReader Checker = null;
				try
				{
						int index = 0;
						Checker = new BufferedReader(new FileReader("./data/items/items cannot sell.txt"));
						while ((line = Checker.readLine()) != null)
						{
								if (line.startsWith("//") || line.isEmpty())
								{
										continue;
								}
								String parse[] = line.split(" ");
								cannotSellToShopItems[index] = Integer.parseInt(parse[0]);
								index++;
						}
						Checker.close();
				}
				catch (Exception e)
				{
						e.printStackTrace();
				}
		}


		public ShopHandler()
		{
		}

		public static void loadShops()
		{
				for (int i = 0; i < MaxShops; i++)
				{
						for (int j = 0; j < MAX_ITEM_IN_SHOP; j++)
						{
								ResetItem(i, j);
						}
						ShopName[i] = "";
				}
				loadShopsContent("shops.cfg");
				loadStandardPriceItems();
				loadCannotSellToShopItems();
		}

		public static void ResetItem(int ShopID, int ArrayID)
		{
				ShopItems[ShopID][ArrayID] = 0;
				ShopItemsN[ShopID][ArrayID] = 0;
				shopPrice[ShopID][ArrayID] = 0;
		}

		public static void loadShopsContent(String FileName)
		{

				try
				{
						BufferedReader file = new BufferedReader(new FileReader("data/items/shops.txt"));
						String line;
						int shopId = 0;
						String name = "";
						int itemIndex = 0;
						while ((line = file.readLine()) != null)
						{
								if (!line.isEmpty())
								{
										if (line.startsWith("Shop name:"))
										{
												name = line.substring(11);
										}
										else if (line.startsWith("Shop id:"))
										{
												shopId = Integer.parseInt(line.substring(9));
												ShopName[shopId] = name;
												itemIndex = 0;
										}
										else
										{
												String parse[] = line.split(" ");
												ShopItems[shopId][itemIndex] = Integer.parseInt(parse[0]) + 1;
												if (!parse[1].startsWith("//"))
												{
														shopPrice[shopId][itemIndex] = Integer.parseInt(parse[1]);
												}
												itemIndex++;
										}

								}
								else
								{

								}
						}
						file.close();
				}
				catch (Exception e)
				{
				}
		}
}