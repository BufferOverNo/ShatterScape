package tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import core.ServerConfiguration;
import game.item.BloodMoneyPrice;
import game.item.ItemAssistant;
import game.item.ItemDefinition;
import utility.FileUtility;
import utility.Misc;


public class EconomyScan
{

		private final static String charactersLocation = "backup/characters/players";

		private final static String administrators[] = {"mgt madness", "ronald", "connor"};

		public static final void main(String[] args)
		{
				long start = System.currentTimeMillis();
				Misc.printDontSave("Scanning started.");
				if (args.length == 1)
				{
						wealthScan = true;
				}
				else
				{
						itemToSearchFor = Integer.parseInt(args[0]);
						bankValueFlag = Integer.parseInt(args[1]);
				}
				if (itemToSearchFor > 0)
				{
						Misc.printDontSave("Scanning for item: " + itemToSearchFor);
				}
				else
				{
						Misc.printDontSave("Scanning for bank wealth above: " + bankValueFlag);
				}
				ItemDefinition.loadItemDefinitions();
				BloodMoneyPrice.loadBloodMoneyPrice();
				scanFolder();
				Misc.printDontSave("Scanning finished took: " + (System.currentTimeMillis() - start) + " milliseconds");
				System.exit(0);
		}

		private static boolean wealthScan = false;

		private static int bankValueFlag = 0;

		private static int itemToSearchFor = 0; // Change to 0 to not search for any item.

		private static long economyTotalBloodMoney;

		private static long totalOfItemSearchedInEconomy;

		public static void scanAccount(File paramFile)
		{
				long bankWorth = 0;
				String name = paramFile.getName().replaceAll(".txt", "");
				for (int i = 0; i < administrators.length; i++)
				{
						if (administrators[i].equalsIgnoreCase(name))
						{
								return;
						}
				}
				String str1 = "";
				String str2 = "";
				String str3 = "";
				String[] arrayOfString = new String[3];
				int j = 0;
				int k = 0;
				int[] lootingBagStorageItemId = new int[28];
				int[] lootingBagStorageItemAmount = new int[28];
				BufferedReader localBufferedReader = null;
				try
				{
						localBufferedReader = new BufferedReader(new FileReader(paramFile));
				}
				catch (FileNotFoundException localFileNotFoundException)
				{
				}
				long amountFound = 0;
				while ((j == 0) && (str1 != null))
				{
						str1 = str1.trim();
						int n = str1.indexOf("=");
						if (n > -1)
						{
								str2 = str1.substring(0, n);
								str2 = str2.trim();
								str3 = str1.substring(n + 1);
								str3 = str3.trim();
								arrayOfString = str3.split("\t");
								int id = 0;
								long value = 0;
								long amount = 0;
								switch (k)
								{
										case 1:
												id = Integer.parseInt(arrayOfString[1]);
												amount = Integer.parseInt(arrayOfString[2]);
												value = BloodMoneyPrice.getBloodMoneyPrice(id);
												if (id == itemToSearchFor && itemToSearchFor != 0)
												{
														if (bankValueFlag == 0)
														{
																Misc.printDontSave(name + " has x" + amount + " " + ItemAssistant.getItemName(id));
														}
														totalOfItemSearchedInEconomy += amount;
														amountFound += amount;
												}
												else
												{
														if (str2.equals("character-equip") && value > 0)
														{
																bankWorth += value;
														}
												}
												break;
										case 2:
												id = Integer.parseInt(arrayOfString[1]) - 1;
												id = ItemAssistant.getUnNotedItem(id);
												amount = Integer.parseInt(arrayOfString[2]);
												value = BloodMoneyPrice.getBloodMoneyPrice(id) * amount;
												if (id == itemToSearchFor && itemToSearchFor != 0)
												{
														if (bankValueFlag == 0)
														{
																Misc.printDontSave(name + " has x" + amount + " " + ItemAssistant.getItemName(id));
														}
														totalOfItemSearchedInEconomy += amount;
														amountFound += amount;
												}
												else
												{
														if (str2.equals("inventory-slot") && value > 0)
														{
																bankWorth += value;
														}
												}
												break;
										case 3:
												id = Integer.parseInt(arrayOfString[1]) - 1;
												amount = Integer.parseInt(arrayOfString[2]);
												value = BloodMoneyPrice.getBloodMoneyPrice(id) * amount;
												if (id == itemToSearchFor && itemToSearchFor != 0)
												{
														if (bankValueFlag == 0)
														{
																Misc.printDontSave(name + " has x" + amount + " " + ItemAssistant.getItemName(id));
														}
														totalOfItemSearchedInEconomy += amount;
														amountFound += amount;
												}
												else
												{
														if (str2.startsWith("character-bank") && value > 0)
														{
																bankWorth += value;
														}
												}
												break;

										//[OTHER] to scan at lootingbag.
										case 4:
												if (str2.equals("lootingBagStorageItemId"))
												{
														for (int z = 0; z < arrayOfString.length; z++)
														{
																lootingBagStorageItemId[z] = Integer.parseInt(arrayOfString[z]);
														}
												}
												if (str2.equals("lootingBagStorageItemAmount"))
												{
														for (int a = 0; a < arrayOfString.length; a++)
														{
																lootingBagStorageItemAmount[a] = Integer.parseInt(arrayOfString[a]);
														}
												}
												break;
								}
						}
						else if (str1.equals("[CREDENTIALS]"))
						{
								k = -1;
						}
						else if (str1.equals("[APPEARANCE]"))
						{
								k = -1;
						}
						else if (str1.equals("[OTHER]"))
						{
								k = 4;
						}
						else if (str1.equals("[EQUIPMENT]"))
						{
								k = 1;
						}
						else if (str1.equals("[LOOK]"))
						{
								k = -1;
						}
						else if (str1.equals("[SKILLS]"))
						{
								k = -1;
						}
						else if (str1.equals("[INVENTORY]"))
						{
								k = 2;
						}
						else if (str1.equals("[BANK]"))
						{
								k = 3;
						}
						else if (str1.equals("[FRIENDS]"))
						{
								k = -1;
						}
						else if (str1.equals("[IGNORES]"))
						{
								k = -1;
						}
						else if (str1.equals("[EOF]"))
						{
								try
								{
										localBufferedReader.close();
								}
								catch (IOException localIOException2)
								{
								}
						}
						try
						{
								str1 = localBufferedReader.readLine();
						}
						catch (IOException localIOException3)
						{
								j = 1;
						}
				}
				try
				{
						localBufferedReader.close();
				}
				catch (IOException localIOException1)
				{
				}
				for (int index = 0; index < lootingBagStorageItemId.length; index++)
				{
						int itemId = lootingBagStorageItemId[index];
						long amount = lootingBagStorageItemAmount[index];
						if (itemId > 0 && lootingBagStorageItemAmount[index] > 0)
						{
								if (itemId == itemToSearchFor && itemToSearchFor != 0)
								{
										if (bankValueFlag == 0)
										{
												Misc.printDontSave(name + " has x" + amount + " " + ItemAssistant.getItemName(itemId));
										}
										totalOfItemSearchedInEconomy += amount;
										amountFound += amount;
								}
								else
								{
										bankWorth += BloodMoneyPrice.getBloodMoneyPrice(itemId) * amount;
								}
						}
				}
				if (bankWorth > bankValueFlag && itemToSearchFor == 0)
				{
						Misc.printDontSave("Bank value: " + Misc.formatRunescapeStyle(bankWorth) + " of: " + name);
				}
				if (amountFound >= bankValueFlag && itemToSearchFor > 0)
				{
						Misc.printDontSave(name + " has x" + amountFound + " " + ItemAssistant.getItemName(itemToSearchFor));
				}
				economyTotalBloodMoney += bankWorth;
		}


		public static void scanFolder()
		{
				File localFile;
				File[] arrayOfFile;
				int i;
				if (wealthScan)
				{
						try
						{
								BufferedReader file = new BufferedReader(new FileReader("backup/characters/wealth scan.txt"));
								String line;
								while ((line = file.readLine()) != null)
								{
										if (!line.isEmpty())
										{
												//cold2.txt contains 'f0-b4-79-15-df-a2'.
												if (line.contains(".txt"))
												{
														line = line.substring(0, line.indexOf(".txt"));
														String nameToScan = line;
														if (!FileUtility.accountExists(charactersLocation + "/" + nameToScan + ".txt"))
														{
																Misc.printDontSave("Account does not exist: " + nameToScan);
																continue;
														}
														scanAccount(localFile = new File(charactersLocation + "/" + nameToScan + ".txt"));
												}
										}
								}
								file.close();
						}
						catch (Exception e)
						{
						}
				}
				else
				{
						localFile = new File(charactersLocation);
						arrayOfFile = localFile.listFiles();
						if (arrayOfFile != null)
						{
								for (i = 0; i < arrayOfFile.length; i++)
								{
										if ((arrayOfFile[i] != null) && (arrayOfFile[i].getName().endsWith(".txt")))
										{
												scanAccount(arrayOfFile[i]);
										}
								}
						}
				}
				if (itemToSearchFor == 0)
				{
						Misc.printDontSave("Economy blood money value: " + Misc.formatRunescapeStyle(economyTotalBloodMoney));
						if (!ServerConfiguration.DEBUG_MODE)
						{
								FileUtility.addLineOnTxt("backup/logs/economy log.txt", Misc.formatRunescapeStyle(economyTotalBloodMoney) + " at: " + Misc.getDate());
						}
				}
				else
				{
						Misc.printDontSave("Total of " + ItemAssistant.getItemName(itemToSearchFor) + " in economy: " + totalOfItemSearchedInEconomy);
				}
		}
}