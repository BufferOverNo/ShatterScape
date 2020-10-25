package game.item;

import java.io.BufferedReader;
import java.io.FileReader;

public class ItemDefinition
{
		public static final ItemDefinition[] DEFINITIONS = new ItemDefinition[20000];


		public ItemDefinition(int itemId, String itemName, int[] Bonuses, boolean note, boolean stackable, int price, boolean f2p, boolean random)
		{
				this.itemId = itemId;
				this.name = itemName;
				this.bonuses = Bonuses;
				this.note = note;
				this.stackable = stackable;
				this.price = price;
				this.f2p = f2p;
				this.random = random;
		}

		public static ItemDefinition[] getDefinitions()
		{
				return DEFINITIONS;
		}

		public final int itemId;

		public final String name;

		public int[] bonuses = new int[12];

		public final boolean note;

		public final boolean stackable;

		public final boolean f2p;

		/**
		 * True, if it is an item that is randomized when using quick setup. Such as a randomized Mystic top item.
		 * Random items are not banked when using quick setup button.
		 */
		public final boolean random;

		public final int price;

		public static void loadItemDefinitions()
		{
				try
				{

						String name = "";
						int itemId = 0;
						int[] bonuses = new int[12];
						boolean note = false;
						boolean f2p = false;
						boolean random = false;
						boolean stackable = false;
						int price = 0;

						BufferedReader file = new BufferedReader(new FileReader("./data/items/item definition.txt"));
						String line;

						while ((line = file.readLine()) != null)
						{
								if (!line.isEmpty())
								{
										String[] parse = line.split(" ");
										if (line.startsWith("Id:"))
										{
												itemId = Integer.parseInt(parse[1]);
										}
										else if (line.startsWith("Name:"))
										{
												name = line.replace("Name: ", "");
										}
										else if (line.startsWith("Price:"))
										{
												price = Integer.parseInt(parse[1]);
										}
										else if (line.startsWith("Noted:"))
										{
												note = parse[1].equals("true");
										}
										else if (line.startsWith("Stackable:"))
										{
												stackable = parse[1].equals("true");
										}
										else if (line.startsWith("F2p:"))
										{
												f2p = parse[1].equals("true");
										}
										else if (line.startsWith("Random"))
										{
												random = parse[1].equals("true");
										}
										else if (line.startsWith("Stab attack bonus:"))
										{
												bonuses[0] = Integer.parseInt(parse[3]);
										}
										else if (line.startsWith("Slash attack bonus:"))
										{
												bonuses[1] = Integer.parseInt(parse[3]);
										}
										else if (line.startsWith("Crush attack bonus:"))
										{
												bonuses[2] = Integer.parseInt(parse[3]);
										}
										else if (line.startsWith("Magic attack bonus:"))
										{
												bonuses[3] = Integer.parseInt(parse[3]);
										}
										else if (line.startsWith("Ranged attack bonus:"))
										{
												bonuses[4] = Integer.parseInt(parse[3]);
										}
										else if (line.startsWith("Stab defence bonus:"))
										{
												bonuses[5] = Integer.parseInt(parse[3]);
										}
										else if (line.startsWith("Slash defence bonus:"))
										{
												bonuses[6] = Integer.parseInt(parse[3]);
										}
										else if (line.startsWith("Crush defence bonus:"))
										{
												bonuses[7] = Integer.parseInt(parse[3]);
										}
										else if (line.startsWith("Magic defence bonus:"))
										{
												bonuses[8] = Integer.parseInt(parse[3]);
										}
										else if (line.startsWith("Ranged defence bonus:"))
										{
												bonuses[9] = Integer.parseInt(parse[3]);
										}
										else if (line.startsWith("Strength bonus:"))
										{
												bonuses[10] = Integer.parseInt(parse[2]);
										}
										else if (line.startsWith("Prayer bonus:"))
										{
												bonuses[11] = Integer.parseInt(parse[2]);
												ItemDefinition.DEFINITIONS[itemId] = new ItemDefinition(itemId, name, bonuses, note, stackable, price, f2p, random);
												bonuses = new int[12];
										}
								}
						}

						file.close();
				}
				catch (Exception e)
				{
				}
		}
}
