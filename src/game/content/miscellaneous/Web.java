package game.content.miscellaneous;



import java.util.ArrayList;

import game.content.combat.vsplayer.melee.MeleeData;
import game.item.ItemAssistant;
import game.object.clip.Region;
import game.object.custom.Object;
import game.player.Player;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;
import utility.Misc;

/**
 * Slashing web.
 * @author MGT Madness, created on 20-03-2015.
 */
public class Web
{

		/**
		 * Web list, x, y, z, direction, state.
		 * State is either CUT or UNCUT.
		 */
		public static ArrayList<String> webList = new ArrayList<String>();

		public final static int WEB_OBJECT_ID = 733;

		private final static int WEB_RESPAWN_TICKS = 60;

		private final static int KNIFE_ITEM_ID = 946;

		/**
		 * Items used for slicing through webs at Magebank.
		 * @return
		 * 			True, if the player has a sharp weapon wielded.
		 */
		private static boolean wieldingSharpWeapon(Player player)
		{
				String s = ItemAssistant.getItemName(player.getWieldedWeapon()).toLowerCase();
				if (s.contains("staff of light") || s.contains("2h") || s.contains("sword") || s.contains("dagger") || s.contains("rapier") || s.contains("scimitar") || s.contains("halberd") || s.contains("spear") || s.contains("axe") || s.contains("claws") || s.contains("whip") || s.contains("abyssal tentacle"))
				{
						return true;
				}
				return false;
		}

		/**
		 * @param player
		 * 			The associated player.
		 * @return
		 * 			True, if the player has the requirements to slash the web.
		 */
		private static boolean hasRequirements(Player player, int itemId)
		{
				if (itemId != 946)
				{
						return false;
				}
				if (System.currentTimeMillis() - player.lastWebCut < 1100)
				{
						return false;
				}
				player.lastWebCut = System.currentTimeMillis();
				if (!wieldingSharpWeapon(player) && !ItemAssistant.hasItemInInventory(player, KNIFE_ITEM_ID))
				{
						player.playerAssistant.sendMessage("You need a sharp blade to cut the web.");
						return false;
				}
				return true;
		}

		/**
		 * Slash web feature.
		 * @param player
		 */
		public static void slash(Player player, int itemId)
		{
				if (!hasRequirements(player, itemId))
				{
						return;
				}
				player.startAnimation(MeleeData.getWeaponAnimation(player, ItemAssistant.getItemName(player.getWieldedWeapon()).toLowerCase()));
				slashWebEvent(player);
		}

		/**
		 * Delete the web 1 game tick later.
		 * @param player
		 * 			The associated player.
		 */
		private static void slashWebEvent(final Player player)
		{
				CycleEventHandler.getSingleton().addEvent(player, new CycleEvent()
				{
						@Override
						public void execute(CycleEventContainer container)
						{
								container.stop();
						}

						@Override
						public void stop()
						{

								// Loop through web list to grab the face.
								for (int index = 0; index < webList.size(); index++)
								{
										String parse[] = webList.get(index).split(" ");
										String old = webList.get(index);
										int x = Integer.parseInt(parse[0]);
										int y = Integer.parseInt(parse[1]);
										int height = Integer.parseInt(parse[2]);
										int face = Integer.parseInt(parse[3]);
										String state = parse[4];
										if (state.equals("CUT"))
										{
												continue;
										}
										// Direction string is where the web coordinate is visually beside the web.
										// So if it says WEST, it means the web coordinate is west of the web.
										if (x == player.getObjectX() && y == player.getObjectY() && height == player.getHeight())
										{

												if (state.equals("CUT"))
												{
														return;
												}
												if (Misc.hasPercentageChance(50))
												{
														player.playerAssistant.sendMessage("You fail to slash the web.");
														return;
												}
												int type = 0;

												String directionString = "EAST";
												if (face == 1)
												{
														directionString = "SOUTH";
												}
												else if (face == 3)
												{
														directionString = "NORTH";
												}
												else if (face == 2)
												{
														directionString = "WEST";
												}
												webList.remove(index);
												webList.add(old.replace("UNCUT", "CUT"));
												new Object(WEB_OBJECT_ID + 1, player.getObjectX(), player.getObjectY(), player.getHeight(), face, type, WEB_OBJECT_ID, WEB_RESPAWN_TICKS);
												player.playerAssistant.sendMessage("You slash the web.");
												if (directionString.equals("WEST"))
												{
														Region.removeFromDynamicTileClipping(x + " " + y + " " + height + " EAST BLOCK");
														Region.removeFromDynamicTileClipping((x + 1) + " " + y + " " + height + " WEST BLOCK");
												}
												else if (directionString.equals("EAST"))
												{
														Region.removeFromDynamicTileClipping(x + " " + y + " " + height + " WEST BLOCK");
														Region.removeFromDynamicTileClipping((x - 1) + " " + y + " " + height + " EAST BLOCK");
												}
												else if (directionString.equals("NORTH"))
												{
														Region.removeFromDynamicTileClipping(x + " " + y + " " + height + " SOUTH BLOCK");
														Region.removeFromDynamicTileClipping(x + " " + (y - 1) + " " + height + " NORTH BLOCK");
												}
												else if (directionString.equals("SOUTH"))
												{
														Region.removeFromDynamicTileClipping(x + " " + y + " " + height + " NORTH BLOCK");
														Region.removeFromDynamicTileClipping(x + " " + (y + 1) + " " + height + " SOUTH BLOCK");
												}
												break;
										}

								}
						}
				}, 1);
		}

		/**
		 * When the web respawns.
		 * @param object
		 */
		public static void webRespawning(Object object)
		{
				if (object.objectId != 734)
				{
						return;
				}
				for (int index = 0; index < Web.webList.size(); index++)
				{
						String parse[] = Web.webList.get(index).split(" ");
						String old = Web.webList.get(index);
						int x = Integer.parseInt(parse[0]);
						int y = Integer.parseInt(parse[1]);
						int height = Integer.parseInt(parse[2]);
						int face = Integer.parseInt(parse[3]);
						// Direction string is where the web coordinate is visually beside the web.
						// So if it says WEST, it means the web coordinate is west of the web.
						if (x == object.objectX && y == object.objectY && height == object.height)
						{
								Web.webList.remove(index);
								Web.webList.add(old.replace("CUT", "UNCUT"));
								String directionString = "EAST";
								if (face == 1)
								{
										directionString = "SOUTH";
								}
								else if (face == 3)
								{
										directionString = "NORTH";
								}
								else if (face == 2)
								{
										directionString = "WEST";
								}
								if (directionString.equals("WEST"))
								{
										Region.dynamicTileClipping.add(x + " " + y + " " + height + " EAST BLOCK");
										Region.dynamicTileClipping.add((x + 1) + " " + y + " " + height + " WEST BLOCK");
								}
								else if (directionString.equals("EAST"))
								{
										Region.dynamicTileClipping.add(x + " " + y + " " + height + " WEST BLOCK");
										Region.dynamicTileClipping.add((x - 1) + " " + y + " " + height + " EAST BLOCK");
								}
								else if (directionString.equals("NORTH"))
								{
										Region.dynamicTileClipping.add(x + " " + y + " " + height + " SOUTH BLOCK");
										Region.dynamicTileClipping.add(x + " " + (y - 1) + " " + height + " NORTH BLOCK");
								}
								else if (directionString.equals("SOUTH"))
								{
										Region.dynamicTileClipping.add(x + " " + y + " " + height + " NORTH BLOCK");
										Region.dynamicTileClipping.add(x + " " + (y + 1) + " " + height + " SOUTH BLOCK");
								}
								break;
						}
				}

		}

}