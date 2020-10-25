package game.object.click;

import core.Server;
import core.ServerConstants;
import game.content.bank.Bank;
import game.content.combat.Combat;
import game.content.combat.EdgeAndWestsRule;
import game.content.highscores.HighscoresInterface;
import game.content.minigame.barrows.Barrows;
import game.content.minigame.zombie.Zombie;
import game.content.miscellaneous.FightCaves;
import game.content.miscellaneous.PlayerMiscContent;
import game.content.miscellaneous.Teleport;
import game.content.miscellaneous.Web;
import game.content.quicksetup.QuickSetUp;
import game.content.skilling.Farming;
import game.content.skilling.Mining;
import game.content.skilling.Runecrafting;
import game.content.skilling.Woodcutting;
import game.content.skilling.agility.AgilityAssistant;
import game.content.skilling.agility.AgilityShortcuts;
import game.content.skilling.agility.BarbarianCourse;
import game.content.skilling.agility.GnomeCourse;
import game.content.skilling.agility.WildernessCourse;
import game.content.skilling.smithing.Smithing;
import game.item.ItemAssistant;
import game.object.ObjectEvent;
import game.object.areas.BrimhavenDungeon;
import game.object.areas.Karamja;
import game.object.areas.MageBank;
import game.object.areas.SlayerTower;
import game.object.areas.TaverlyDungeon;
import game.object.areas.WildernessObjects;
import game.object.custom.Door;
import game.object.custom.DoorEvent;
import game.player.Player;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;
import game.player.movement.Movement;
import network.packet.PacketHandler;
import utility.Misc;

public class FirstClickObject
{

		public static void firstClickObject(final Player player, int objectId, int objectX, int objectY)
		{
				player.turnPlayerTo(player.getObjectX(), player.getObjectY());
				player.clickObjectType = 0;
				if (WildernessObjects.isWildernessObject(player, objectId))
				{
						WildernessObjects.doWildernessObject(player, objectId);
						return;
				}
				else if (MageBank.isMageBankObject(objectId))
				{
						MageBank.applyObjectAction(player, objectId);
						return;
				}
				else if (BarbarianCourse.isBarbarianCourseObject(player, objectId))
				{
						return;
				}
				else if (GnomeCourse.isGnomeCourseObject(player, objectId))
				{
						return;
				}
				else if (WildernessCourse.isWildernessCourseObject(player, objectId))
				{
						WildernessCourse.startWildernessCourse(player, objectId);
						return;
				}
				else if (Woodcutting.isWoodcuttingObject(player, objectId))
				{
						return;
				}
				else if (Farming.isFarmingObject(player, objectId))
				{
						return;
				}
				else if (Runecrafting.runecraftAltar(player, objectId))
				{
						return;
				}
				else if (Mining.isMiningObject(objectId))
				{
						Mining.doMiningObject(player, objectId);
						return;
				}
				else if (BrimhavenDungeon.isBrimhavenDungeonObject(player, objectId))
				{
						return;
				}
				else if (SlayerTower.isSlayerTowerObject(player, objectId))
				{
						return;
				}
				else if (TaverlyDungeon.isTaverlyDungeonObject(player, objectId))
				{
						return;
				}
				else if (AgilityShortcuts.isMiscAgilityShortcut(player, objectId))
				{
						return;
				}
				else if (Karamja.isKaramjaObject(player, objectId))
				{
						Karamja.karamjaObjectAction(player, objectId);
						return;
				}
				else if (Barrows.isBarrowsObject(player, objectId))
				{
						return;
				}
				else if (Zombie.loadZombieWaveShop(player, objectId, false))
				{
						return;
				}

				else if (Smithing.isFurnace(player, objectId))
				{
						player.setActionIdUsed(5);
						Smithing.sendSmelting(player);
						return;
				}

				else if (Door.isDoor(player, objectId))
				{
						return;
				}

				switch (objectId)
				{

						// Portal at Clan wars.
						case 26646:
								player.getPA().movePlayer(3088, 3512, 0);
								break;

						// Gate of Resource wilderness area
						case 26760:
								boolean canEnter = false;
								if (player.getY() == 3945)
								{
										if (ItemAssistant.checkAndDeleteStackableFromInventory(player, 18644, 350))
										{
												player.getPA().sendMessage("You pay 350 blood money to enter.");
												canEnter = true;
										}
										else
										{
												player.getDH().sendItemChat1("", "You need to pay a fee of 350 blood money to enter.", 18644, 200, 10, 0);
										}
								}
								else
								{
										canEnter = true;
								}
								if (canEnter)
								{
										DoorEvent.canUseAutomaticDoor(player, 1, false, 26760, 3184, 3944, 0, 1);
										player.forceNoClip = true;
										Movement.stopMovementDifferent(player);
										Movement.travelTo(player, 0, player.getY() == 3945 ? -1 : 1);
										player.resetPlayerTurn();
								}
								break;
						// Ladder
						case 16680:
								if (player.getObjectX() == 2884 && player.getObjectY() == 3397)
								{
										ObjectEvent.climbDownLadder(player, 2884, 9798, 0);
								}
								else if (player.getObjectX() == 3088 && player.getObjectY() == 3571)
								{
										ObjectEvent.climbDownLadder(player, 3089, 9971, 0);
								}
								break;

						// Ladder
						case 17385:
								if (player.getObjectX() == 2884 && player.getObjectY() == 9797)
								{
										ObjectEvent.climbUpLadder(player, 2884, 3396, 0);
								}
								else if (player.getObjectX() == 3097 && player.getObjectY() == 9867)
								{
										ObjectEvent.climbUpLadder(player, 3096, 3468, 0);
								}
								else if (player.getObjectX() == 3088 && player.getObjectY() == 9971)
								{
										ObjectEvent.climbUpLadder(player, 3087, 3571, 0);
								}
								else if (player.getObjectX() == 3116 && player.getObjectY() == 9852)
								{
										ObjectEvent.climbUpLadder(player, 3115, 3452, 0);
								}
								else if (player.getObjectX() == 2842 && player.getObjectY() == 9824)
								{
										ObjectEvent.climbUpLadder(player, 2842, 3425, 0);
								}
								break;


						// Runecrafting portals
						case 14841:
						case 14842:
						case 14843:
						case 14844:
						case 14845:
						case 14846:
						case 14847:
						case 14848:
						case 14892:
						case 14893:
						case 14894:
								Teleport.spellTeleport(player, 2858, 3379, 0, false);
								break;

						// Chest at tournament varrock instance.
						case 76:
								if (player.getHeight() == 20)
								{
										player.getDH().sendDialogues(268);
								}
								break;
						// Wilderness ditch.
						case 23271:
								if (player.getObjectX() == 2996)
								{
										return;
								}
								player.setDoingAgility(true);
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

												if (player.getY() <= 3520)
												{
														player.setForceMovement(6132, 0, 3, 0, 60, 0, 3);
												}
												else if (player.getY() >= 3523)
												{
														player.setForceMovement(6132, 0, -3, 0, 60, 2, 3);
												}
										}
								}, 2);
								break;
						// Box of health.
						case 29300:
								if (Combat.inCombatAlert(player))
								{
										return;
								}
								QuickSetUp.heal(player);
								if (player.getHeight() == 20)
								{
										for (int a = 0; a < player.playerItems.length; a++)
										{
												int itemId1 = player.playerItems[a] - 1;
												if (itemId1 == 6685 || itemId1 == 6687 || itemId1 == 6689 || itemId1 == 6691)
												{
														player.playerItems[a] = 6686;
														player.setInventoryUpdate(true);
														break;
												}
										}
								}
								player.getPA().sendMessage("You have been healed.");
								player.getDH().sendItemChat1("", "Your stats have been replenished.", 744, 300, 18, 0);
								break;

						// Farming patch.
						case 8174:
								Farming.rakePatch(player);
								break;

						// Altar of the occult.
						case 29150:
								player.getDH().sendDialogues(22);
								break;
						// Corporeal beast passage.
						case 677:
								if (player.getX() <= 2970)
								{
										player.getPA().movePlayer(2974, player.getY(), player.getHeight());
								}
								else
								{
										player.getPA().movePlayer(2970, player.getY(), player.getHeight());
								}
								break;
						// Ladder climb up.
						case 11794:
						case 11801:
						case 16683:
						case 12964:
						case 11:
								ObjectEvent.climbUpLadder(player, player.getX(), player.getY(), player.getHeight() + 1);
								break;
						// Ladder climb down.
						case 11795:
						case 11802:
						case 16679:
						case 12966:
						case 14746:
								ObjectEvent.climbDownLadder(player, player.getX(), player.getY(), player.getHeight() - 1);
								break;
						// Shantay pass.
						case 4031:
								player.forceNoClip = true;
								Movement.travelTo(player, 0, player.getY() >= 3117 ? -2 : 2);
								player.resetPlayerTurn();
								break;
						// Portal at Rune essence mine.
						case 2492:
								Teleport.startTeleport(player, 3252 + Misc.random(1), 3399 + Misc.random(4), 0, "MODERN");
								break;

						// Deposit box.
						case 6948:
								player.setActionIdUsed((15000 + 6948));
								player.getPA().sendFrame126("                                Deposit Box", 7421);
								player.getPA().sendFrame248(4465, 197);
								ItemAssistant.resetItems(player, 7423);
								break;


						// Lumbridge staircase.
						case 1740:
								player.getPA().movePlayer(player.getX(), player.getY(), 1);
								break;
						// Ladder inside Edgeville men area.
						case 26982:
								ObjectEvent.climbUpLadder(player, player.getX(), player.getY(), 1);
								break;

						// Ladder inside Edgeville men area.
						case 26983:
								ObjectEvent.climbDownLadder(player, player.getX(), player.getY(), 0);
								break;

						// Large door at Edgeville men area.
						case 26913:
						case 26910:
								DoorEvent.canUseAutomaticDoor(player, 1, false, 26913, 3101, 3510, 1, 0);
								DoorEvent.canUseAutomaticDoor(player, 1, false, 26910, 3101, 3509, 3, 0);
								player.forceNoClip = true;
								Movement.travelTo(player, player.getX() >= 3101 ? -1 : 1, 0);
								player.resetPlayerTurn();
								return;

						case 14745:
								ObjectEvent.climbUpLadder(player, player.getX(), player.getY(), 1);
								break;

						// Door at Entrana to access Tanner.
						case 1533:
								DoorEvent.canUseAutomaticDoor(player, 1, false, 1533, 2822, 3354, 3, 2);
								player.forceNoClip = true;
								Movement.travelTo(player, player.getX() == 2823 ? -1 : 1, 0);
								player.resetPlayerTurn();
								break;

						// Warrior's guild door.
						case 15644:
						case 15641:
								// Ground floor.
								if (player.getObjectX() == 2855 || player.getObjectX() == 2854)
								{
										DoorEvent.canUseAutomaticDoor(player, 1, false, 15644, 2855, 3546, 2, 3);
										DoorEvent.canUseAutomaticDoor(player, 1, false, 15641, 2854, 3546, 0, 3);
										player.forceNoClip = true;
										Movement.travelTo(player, 0, player.getY() == 3546 ? -1 : 1);
										player.resetPlayerTurn();
								}
								// Second floor.
								else if (player.getObjectX() == 2847)
								{
										if (!ItemAssistant.hasItemAmountInInventory(player, 8851, 100) && player.getX() <= 2846)
										{
												player.setNpcType(4289);
												player.getDH().sendDialogues(250);
												return;
										}
										DoorEvent.canUseAutomaticDoor(player, 1, false, 15644, 2847, 3541, 1, 0);
										DoorEvent.canUseAutomaticDoor(player, 1, false, 15641, 2847, 3540, 3, 0);
										player.forceNoClip = true;
										Movement.travelTo(player, player.getX() <= 2846 ? 1 : -1, 0);
										player.resetPlayerTurn();
								}
								break;

						// Mysterious cape stand.
						case 7800:
								if (player.getTotalLevel() != 2079)
								{
										player.getDH().sendDialogues(198);
										return;
								}
								player.getDH().sendDialogues(199);
								break;

						// Al-kharid gates.
						case 2882:
						case 2883:
								DoorEvent.canUseAutomaticDoor(player, 1, false, 2883, 3268, 3228, 1, 0);
								DoorEvent.canUseAutomaticDoor(player, 1, false, 2882, 3268, 3227, 3, 0);
								player.forceNoClip = true;
								Movement.travelTo(player, player.getX() == 3267 ? 1 : -1, 0);
								player.resetPlayerTurn();
								break;

						case 4487:
						case 4490:
								DoorEvent.canUseAutomaticDoor(player, 1, false, 4487, 3428, 3535, 0, 1);
								DoorEvent.canUseAutomaticDoor(player, 1, false, 4490, 3429, 3535, 2, 1);
								player.forceNoClip = true;
								Movement.travelTo(player, 0, player.getY() == 3536 ? -1 : 1);
								player.resetPlayerTurn();
								break;

						// Trapdoor on Edgeville surfance.
						case 1579:
								ObjectEvent.climbDownLadder(player, 3096, 9867, 0);
								break;

						// Fremminik dungeon exit.
						case 2141:
								player.getPA().movePlayer(2730, 3713, 0);
								break;

						// Fremminik dungeon entrance.	
						case 5008:
								player.getPA().movePlayer(2808, 10002, 0);
								break;

						// Shortcut inside Freminik dungeon.
						case 16539:
								if (player.getObjectX() == 2731 && player.getObjectY() == 10008)
								{
										if (player.baseSkillLevel[ServerConstants.AGILITY] < 62)
										{
												player.getDH().sendStatement("You need 62 agility to use this shortcut.");
												return;
										}
										player.getPA().movePlayer(2735, 10008, 0);
										player.playerAssistant.sendMessage("You squeeze through the crevice.");
								}
								else if (player.getObjectX() == 2734 && player.getObjectY() == 10008)
								{
										if (player.baseSkillLevel[ServerConstants.AGILITY] < 62)
										{
												player.getDH().sendStatement("You need 62 agility to use this shortcut.");
												return;
										}
										player.getPA().movePlayer(2730, 10008, 0);
										player.playerAssistant.sendMessage("You squeeze through the crevice.");
								}
								break;

						// Shortcut inside Freminik dungeon.
						case 16544:
								if (player.getObjectX() == 2774 && player.getObjectY() == 10003)
								{
										if (player.baseSkillLevel[ServerConstants.AGILITY] < 81)
										{
												player.getDH().sendStatement("You need 81 agility to use this shortcut.");
												return;
										}
										player.getPA().movePlayer(2768, 10002, 0);
										player.playerAssistant.sendMessage("You jump over.");
								}
								else if (player.getObjectX() == 2769 && player.getObjectY() == 10002)
								{
										if (player.baseSkillLevel[ServerConstants.AGILITY] < 81)
										{
												player.getDH().sendStatement("You need 81 agility to use this shortcut.");
												return;
										}
										player.getPA().movePlayer(2775, 10003, 0);
										player.playerAssistant.sendMessage("You jump over.");
								}
								break;


						// Web
						case 733:
								Web.slash(player, 946);
								break;

						// Manhole.
						case 882:
								ObjectEvent.climbDownLadder(player, 3237, 9859, 0);
								break;

						// Passageway, inside Rogue's den.
						case 7258:
								player.getPA().movePlayer(2906, 3537, 0);
								break;

						// Door inside Rogue's den.
						case 7259:
								DoorEvent.canUseAutomaticDoor(player, 1, false, 7259, 3061, 4984, 0, 3);
								player.forceNoClip = true;
								Movement.travelTo(player, 0, player.getY() == 4983 ? 1 : -1);
								player.resetPlayerTurn();
								break;

						// Trapdoor, outside Rogue's den.
						case 7257:
								if (player.getObjectX() != 2905 || player.getObjectY() != 3537)
								{
										return;
								}
								ObjectEvent.climbDownLadder(player, 3061, 4985, 1);
								break;

						// Mining guild ladder.
						case 2113:
								switch (player.getObjectX())
								{
										case 3020:
												ObjectEvent.climbDownLadder(player, 3021, 9739, 0);
												break;
										case 3019:
												if (player.getObjectY() == 3338)
												{
														ObjectEvent.climbDownLadder(player, 3019, 9737, 0);
												}
												else
												{
														ObjectEvent.climbDownLadder(player, 3019, 9741, 0);
												}
												break;
										case 3018:
												ObjectEvent.climbDownLadder(player, 3017, 9739, 0);
												break;
								}
								break;

						case 1804:
								if (player.getObjectX() == 3115)
								{
										if (!ItemAssistant.hasItemInInventory(player, 983))
										{
												player.getPA().sendMessage("You need a brass key.");
												return;
										}
										DoorEvent.canUseAutomaticDoor(player, 1, false, 1804, 3115, 3450, 2, 3);
										player.forceNoClip = true;
										Movement.travelTo(player, 0, player.getY() == 3449 ? 1 : -1);
										player.resetPlayerTurn();
								}
								break;

						case 9558:
								ObjectEvent.climbUpLadder(player, player.getX(), player.getY(), player.getHeight() + 1);
								break;
						case 9559:
						case 9560:
								ObjectEvent.climbDownLadder(player, player.getX(), player.getY(), player.getHeight() - 1);
								break;

						// Mining guild ladder and also Edgeville dungeon ladder to get to Wilderness orb thing.
						case 1755:
								switch (player.getObjectX())
								{

										case 3116:
												ObjectEvent.climbUpLadder(player, 3115, 3452, 0);
												break;
										case 3088:
												ObjectEvent.climbUpLadder(player, 3087, 3571, 0);
												break;
										case 3020:
												ObjectEvent.climbUpLadder(player, 3021, 3339, 0);
												break;
										case 3019:
												if (player.getObjectY() == 3020)
												{
														ObjectEvent.climbUpLadder(player, 3017, 3339, 0);
												}
												else
												{
														ObjectEvent.climbUpLadder(player, 3019, 3341, 0);
												}
												break;
										case 3018:
												ObjectEvent.climbUpLadder(player, 3017, 3339, 0);
												break;

										case 3237:
												ObjectEvent.climbUpLadder(player, 3237, 3459, 0);
												break;

										case 3097:
												ObjectEvent.climbUpLadder(player, 3096, 3468, 0);
												break;

										// Ladder at Black dragons to Catherby island.
										case 2842:
												ObjectEvent.climbUpLadder(player, 2842, 3423, 0);
												break;
								}
								break;

						// Mining guild stairs.
						case 1722:
								if (player.getHeight() == 0)
								{
										player.getPA().movePlayer(2591, 3092, 1);
								}
								else
								{
										player.getPA().movePlayer(2590, 3087, 2);
								}
								break;

						// Mining guild stairs.
						case 1723:
								if (player.getHeight() == 1)
								{
										player.getPA().movePlayer(2591, 3088, 0);
								}
								else
								{
										player.getPA().movePlayer(2591, 3083, 1);
								}
								break;

						// Corporeal beast Gate.
						case 6452:
						case 6451:
								DoorEvent.canUseAutomaticDoor(player, 1, false, 6452, 3305, 9376, 1, 0);
								DoorEvent.canUseAutomaticDoor(player, 1, false, 6451, 3305, 9375, 3, 0);
								player.forceNoClip = true;
								Movement.travelTo(player, player.getX() == 3304 ? 1 : -1, 0);
								player.resetPlayerTurn();
								break;


						// Large door, at the Church next to Smithing cape master around Port sarim.
						case 1519:
						case 1516:
								if (player.getObjectX() == 3001)
								{
										DoorEvent.canUseAutomaticDoor(player, 1, false, 1519, 3001, 3178, 1, 0);
										DoorEvent.canUseAutomaticDoor(player, 1, false, 1516, 3001, 3177, 3, 0);
										player.forceNoClip = true;
										Movement.travelTo(player, player.getX() == 3001 ? -1 : 1, 0);
										player.resetPlayerTurn();
								}
								break;

						// Guild doors at Wizard Guild.
						case 1600:
						case 1601:
								DoorEvent.canUseAutomaticDoor(player, 1, false, 1600, 2597, 3087, 3, 0);
								DoorEvent.canUseAutomaticDoor(player, 1, false, 1601, 2597, 3088, 1, 0);
								player.forceNoClip = true;
								Movement.travelTo(player, player.getX() == 2596 ? 1 : -1, 0);
								player.resetPlayerTurn();
								break;

						case 1596:
						case 1597:
								// Gate at south west Falador, close to Make-over mage.
								if (player.getObjectY() == 3320)
								{
										DoorEvent.canUseAutomaticDoor(player, 1, false, 1596, 2934, 3320, 2, 3);
										DoorEvent.canUseAutomaticDoor(player, 1, false, 1597, 2933, 3320, 0, 3);
										player.forceNoClip = true;
										Movement.travelTo(player, 0, player.getY() == 3320 ? -1 : 1);
										player.resetPlayerTurn();
								}
								else if (player.getObjectX() == 2816)
								{
										DoorEvent.canUseAutomaticDoor(player, 1, false, 1596, 2816, 3182, 3, 0);
										DoorEvent.canUseAutomaticDoor(player, 1, false, 1597, 2816, 3183, 1, 0);
										player.forceNoClip = true;
										Movement.travelTo(player, player.getX() == 2816 ? -1 : 1, 0);
										player.resetPlayerTurn();
								}
								else if (player.getObjectY() == 9917)
								{

										DoorEvent.canUseAutomaticDoor(player, 1, false, 1596, 3131, 9917, 0, 1);
										DoorEvent.canUseAutomaticDoor(player, 1, false, 1597, 3132, 9917, 2, 1);
										player.forceNoClip = true;
										Movement.travelTo(player, 0, player.getY() == 9917 ? 1 : -1);
										player.resetPlayerTurn();
								}
								else if (player.getObjectX() == 2935)
								{

										DoorEvent.canUseAutomaticDoor(player, 1, false, 1596, 2935, 3451, 1, 2);
										DoorEvent.canUseAutomaticDoor(player, 1, false, 1597, 2935, 3450, 3, 2);
										player.forceNoClip = true;
										Movement.travelTo(player, player.getX() <= 2935 ? 1 : -1, 0);
										player.resetPlayerTurn();
								}
								break;

						case 2712:
								// Door, West varrock, Cooking guild.
						case 2025:
						case 2647:
								// Guild door, Crafting guild.
						case 11714:
								// Door at Mining guild.
								DoorEvent.openAutomaticDoor(player);
								break;

						// Cooking guild stairs.
						case 24074:
						case 24073:
								if (player.getHeight() == 1)
								{
										player.getPA().movePlayer(3144, 3449, 0);
								}
								else
								{
										player.getPA().movePlayer(3144, 3449, 1);
								}
								break;

						// Door, Ranging guild.
						case 2514:
								int travelX = 0;
								int travelY = 0;
								if (player.getY() == 3439)
								{
										travelX = 2;
										travelY = -2;
								}
								else if (player.getY() == 3437)
								{
										travelX = -2;
										travelY = 2;
								}
								player.forceNoClip = true;
								Movement.travelTo(player, travelX, travelY);
								player.resetPlayerTurn();
								break;

						// Stair case at Warrior's guild.
						case 16671:
								if (player.getPA().objectIsAt(2839, 3537, 0))
								{
										player.getPA().movePlayer(2840, 3539, 1);
								}
								break;


						// Stair case at Warrior's guild.
						case 24303:
								if (player.getPA().objectIsAt(2840, 3538, 2))
								{
										player.getPA().movePlayer(2840, 3539, 1);
								}
								break;

						// God wars dungeon altar in boss rooms.
						case 26363:
						case 26366:
						case 26364:
						case 26365:
								if (System.currentTimeMillis() - player.timeUsedGodWarsDungeonAltar <= 180000)
								{
										player.getPA().sendMessage("You cannot use this so soon.");
										return;
								}
								player.timeUsedGodWarsDungeonAltar = System.currentTimeMillis();
								PlayerMiscContent.prayAtAltar(player);
								break;

						/*
						//Cave entrance to fight Jad
						case 11833:
						if (player.getObjectX() == 2437 && player.getObjectY() == 5166)
						{
								FightCaves.startFightCaves(player);
						}
						break;
						*/

						//Cave exit inside the Jad cave
						case 11834:
								FightCaves.exitFightCaves(player);
								break;


						// Edgeville dungeon, double gate.
						case 1727:
						case 1728:
								if (player.getObjectX() == 3104)
								{
										DoorEvent.canUseAutomaticDoor(player, 1, false, 1727, 3104, 9909, 3, 0);
										DoorEvent.canUseAutomaticDoor(player, 1, false, 1728, 3104, 9910, 1, 0);
										player.forceNoClip = true;
										Movement.travelTo(player, player.getX() == 3103 ? 1 : -1, 0);
										player.resetPlayerTurn();
								}
								else if (player.getObjectX() == 3146)
								{
										DoorEvent.canUseAutomaticDoor(player, 1, false, 1727, 3146, 9870, 3, 0);
										DoorEvent.canUseAutomaticDoor(player, 1, false, 1728, 3146, 9871, 1, 0);
										player.forceNoClip = true;
										Movement.travelTo(player, player.getX() == 3146 ? -1 : 1, 0);
										player.resetPlayerTurn();
								}
								else if (player.getObjectY() == 9918)
								{
										DoorEvent.canUseAutomaticDoor(player, 1, false, 1728, 3131, 9918, 0, 3);
										DoorEvent.canUseAutomaticDoor(player, 1, false, 1727, 3132, 9918, 2, 3);
										player.forceNoClip = true;
										Movement.travelTo(player, 0, player.getY() == 9917 ? 1 : -1);
										player.resetPlayerTurn();
								}
								else if (player.getObjectY() == 9945)
								{
										DoorEvent.canUseAutomaticDoor(player, 1, false, 1727, 3106, 9945, 2, 3);
										DoorEvent.canUseAutomaticDoor(player, 1, false, 1728, 3105, 9945, 0, 3);
										player.forceNoClip = true;
										Movement.travelTo(player, 0, player.getY() == 9945 ? -1 : 1);
										player.resetPlayerTurn();
								}
								else if (player.getObjectY() == 9944)
								{

										DoorEvent.canUseAutomaticDoor(player, 1, false, 1557, 3105, 9944, 0, 1);
										DoorEvent.canUseAutomaticDoor(player, 1, false, 1558, 3106, 9944, 2, 1);
										player.forceNoClip = true;
										Movement.travelTo(player, 0, player.getY() == 9944 ? 1 : -1);
										player.resetPlayerTurn();
								}
								else if (player.getObjectX() == 3145)
								{

										DoorEvent.canUseAutomaticDoor(player, 1, false, 1557, 3145, 9871, 1, 2);
										DoorEvent.canUseAutomaticDoor(player, 1, false, 1558, 3145, 9870, 3, 2);
										player.forceNoClip = true;
										Movement.travelTo(player, player.getX() == 3146 ? -1 : 1, 0);
										player.resetPlayerTurn();
								}
								break;

						// Draynor door at aggie.
						case 1530:
								if (player.getObjectX() == 2564)
								{
										DoorEvent.canUseAutomaticDoor(player, 1, false, 1530, 2564, 3310, 1, 0);
										player.forceNoClip = true;
										Movement.travelTo(player, player.getX() == 2564 ? -1 : 1, 0);
										player.resetPlayerTurn();
								}
								else if (player.getObjectX() == 2611)
								{
										DoorEvent.canUseAutomaticDoor(player, 1, false, 1530, 2611, 3398, 0, 1);
										player.forceNoClip = true;
										Movement.travelTo(player, 0, player.getY() == 3399 ? -1 : 1);
										player.resetPlayerTurn();
								}
								else
								{
										DoorEvent.canUseAutomaticDoor(player, 1, false, 1530, 3088, 3258, 1, 2);
										player.forceNoClip = true;
										Movement.travelTo(player, player.getX() == 3089 ? -1 : 1, 0);
										player.resetPlayerTurn();
								}
								break;

						// Completionist cape stand.
						case 3515:
								player.getDH().sendDialogues(199);
								break;

						// Staircase that leads to the Varrock west bank dungeon.
						case 11800:
								if (player.getY() == 3434)
								{
										player.getPA().movePlayer(3190, 9834, 0);
								}
								else
								{
										player.getPA().movePlayer(3190, 9833, 0);
								}
								break;

						// Staircase, climb up.
						case 11789:
								// Staircase that is in the building west of Varrock museum.
								if (player.getPA().objectIsAt(3237, 3447))
								{
										player.getPA().movePlayer(3239, 3447, 1);
								}
								// Varrock east bank.
								else if (player.getPA().objectIsAt(3255, 3421))
								{
										player.getPA().movePlayer(3257, 3421, 1);
								}
								break;

						// Staircase climb up.
						case 11790:
								// Church in East varrock.
								if (player.getPA().objectIsAt(3258, 3487))
								{
										player.getPA().movePlayer(3257, 3487, 1);
								}
								break;

						// Staircase climb down.
						case 11793:
								//building west of Varrock museum.
								if (player.getPA().objectIsAt(3237, 3447))
								{
										player.getPA().movePlayer(3239, 3447, 0);
								}
								// Varrock east bank.
								else if (player.getPA().objectIsAt(3255, 3421))
								{
										player.getPA().movePlayer(3256, 3420, 0);
								}

								// Church in East varrock at the top floor.
								else if (player.getPA().objectIsAt(3258, 3487))
								{
										player.getPA().movePlayer(3258, 3486, 2);
								}
								break;

						// Stair case climb up.
						case 11797:
								// Staircase on the first floor of the Varrock museum, staircase leads up.
								if (player.getPA().objectIsAt(3253, 3443))
								{
										player.getPA().movePlayer(3254, 3446, 2);
								}
								// Staircase, building west of Varrock east church.
								else if (player.getPA().objectIsAt(3239, 3489))
								{
										player.getPA().movePlayer(3242, 3489, 1);
								}
								break;

						// Staircase climb up.
						case 11798:
								// Staircase on the ground floor of Varrock museum.
								if (player.getPA().objectIsAt(3266, 3452))
								{
										player.getPA().movePlayer(3266, 3455, 1);
								}
								break;

						// Staircase climb down.
						case 11799:
								// Staircase, building west of Varrock east church.
								if (player.getPA().objectIsAt(3240, 3489))
								{
										player.getPA().movePlayer(3238, 3489, 0);
								}
								// Staircase on the first floor of Varrock museum.
								else if (player.getPA().objectIsAt(3266, 3453))
								{
										player.getPA().movePlayer(3266, 3451, 0);
								}
								// Staircase on the second floor of the Varrock museum.
								else if (player.getPA().objectIsAt(3253, 3444))
								{
										player.getPA().movePlayer(3254, 3442, 1);
								}
								break;

						// Staircase that leads to the surface of Varrock west bank.
						case 11805:
								if (player.getY() == 9834)
								{
										player.getPA().movePlayer(3186, 3434, 0);
								}
								else
								{
										player.getPA().movePlayer(3186, 3433, 0);
								}
								break;

						// Ladder.
						case 1746:
								ObjectEvent.climbDownLadder(player, player.getX(), player.getY(), player.getHeight() - 1);
								break;

						// Highscore statue
						case 563:
								HighscoresInterface.displayHighscoresInterface(player);
								break;

						//Altar
						case 2640:
						case 409:
						case 10638:
						case 14860:
								PlayerMiscContent.prayAtAltar(player);
								break;

						// Lever located at Edgeville
						case 26761:
								player.getDH().sendDialogues(84);
								break;

						// Ladder inside the Dagannoth boss area
						case 10229:
								ObjectEvent.climbUpLadder(player, 1912, 4367, 0);

								break;

						// Ladder to enter the Dagannoth boss area
						case 10230:
								ObjectEvent.climbDownLadder(player, 2899, 4449, 0);
								break;

						// Lever at Kbd
						case 1817:
								if (player.getX() == 2271 && player.getY() == 4680)
								{
										player.turnPlayerTo(2271, 4679);
										if (!EdgeAndWestsRule.canProcessToDestinationWithBrews(player, 3067, 10253))
										{
												return;
										}
										Teleport.startTeleport(player, 3067, 10253, 0, "LEVER");
								}
								else if (player.getX() == 3153 && player.getY() == 3923)
								{
										player.turnPlayerTo(3152, 3923);
										Teleport.startTeleport(player, 2561, 3311, 0, "LEVER");
								}
								break;

						case 1814:
								if (player.getX() == 2561 && player.getY() == 3311)
								{
										player.turnPlayerTo(2560, 3311);
										if (!EdgeAndWestsRule.canProcessToDestinationWithBrews(player, 3153, 3923))
										{
												return;
										}
										Teleport.startTeleport(player, 3153, 3923, 0, "LEVER");
								}
								break;

						// Bank related objects
						case 3194:
						case 4483:
								player.setUsingBankSearch(false);
								Bank.openUpBank(player, player.getLastBankTabOpened(), true, true);
								break;

						//Obelisks
						case 14829:
						case 14830:
						case 14827:
						case 14828:
						case 14826:
						case 14831:
								Server.objectManager.startObelisk(objectId);
								break;

						// Ladder in a shed at west Varrock, that leads to Edgeville dungeon, at Hill giants
						case 17384:
								if (player.getObjectX() == 3116 && player.getObjectY() == 3452)
								{
										ObjectEvent.climbDownLadder(player, 3117, 9852, 0);

								}
								else if (player.getObjectX() == 2842)
								{
										// Ladder at Catherby island.
										ObjectEvent.climbDownLadder(player, 2842, 9825, 0);
								}
								break;

						case 16511:
								if (player.getX() == 3149 && player.getY() == 9906)
								{

										if (player.baseSkillLevel[ServerConstants.AGILITY] < 51)
										{
												player.playerAssistant.sendMessage("You need 51 agility to use this shortcut.");
												return;
										}

										if (System.currentTimeMillis() - player.agility7 < 3000)
										{
												return;
										}
										player.agility7 = System.currentTimeMillis();
										AgilityAssistant.agilityAnimation(player, 844, false, 6, 0);
										CycleEventHandler.getSingleton().addEvent(player, new CycleEvent()
										{
												@Override
												public void execute(CycleEventContainer container)
												{
														if (player.getX() == 3155 && player.getY() == 9906)
														{
																container.stop();
														}
												}

												@Override
												public void stop()
												{
														AgilityAssistant.resetAgilityWalk(player);
														player.setDoingAgility(false);
												}
										}, 1);

								}
								else if (player.getX() == 3155 && player.getY() == 9906)
								{

										if (player.baseSkillLevel[ServerConstants.AGILITY] < 51)
										{
												player.playerAssistant.sendMessage("You need 51 agility to use this shortcut.");
												return;
										}

										if (System.currentTimeMillis() - player.agility7 < 3000)
										{
												return;
										}
										player.agility7 = System.currentTimeMillis();
										AgilityAssistant.agilityAnimation(player, 844, false, -6, 0);
										CycleEventHandler.getSingleton().addEvent(player, new CycleEvent()
										{
												@Override
												public void execute(CycleEventContainer container)
												{
														if (player.getX() == 3149 && player.getY() == 9906)
														{
																container.stop();
														}
												}

												@Override
												public void stop()
												{
														AgilityAssistant.resetAgilityWalk(player);
														player.setDoingAgility(false);
												}
										}, 1);

								}
								break;

						default:
								PacketHandler.unUsedObject.add(player.getPlayerName() + " at " + Misc.getDate());
								PacketHandler.unUsedObject.add("Unused object: " + player.getObjectId() + ", " + player.getObjectX() + ", " + player.getObjectY() + ", " + player.getHeight());
								player.getPA().sendMessage("Nothing interesting happens.");
								break;

				}
		}

}