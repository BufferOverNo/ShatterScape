package game.content.packet;

import core.ServerConfiguration;
import core.ServerConstants;
import game.content.achievement.Achievements;
import game.content.achievement.PlayerTitle;
import game.content.bank.Bank;
import game.content.bank.BankButtons;
import game.content.bank.BankPin;
import game.content.buttons.SpellBookButton;
import game.content.clanchat.ClanChatHandler;
import game.content.combat.Combat;
import game.content.combat.CombatInterface;
import game.content.combat.EdgeAndWestsRule;
import game.content.combat.vsplayer.magic.AutoCast;
import game.content.commands.AdministratorCommand;
import game.content.dialogueold.options.FiveOptions;
import game.content.dialogueold.options.FourOptions;
import game.content.dialogueold.options.ThreeOptions;
import game.content.dialogueold.options.TwoOptions;
import game.content.highscores.HighscoresHallOfFame;
import game.content.highscores.HighscoresInterface;
import game.content.interfaces.InterfaceAssistant;
import game.content.interfaces.ItemsKeptOnDeath;
import game.content.miscellaneous.CompletionistCape;
import game.content.miscellaneous.DungeoneeringCape;
import game.content.miscellaneous.EditCombatSkill;
import game.content.miscellaneous.GnomeGlider;
import game.content.miscellaneous.GuideBook;
import game.content.miscellaneous.ItemTransferLog;
import game.content.miscellaneous.LootingBag;
import game.content.miscellaneous.NpcDropTableInterface;
import game.content.miscellaneous.PvpBlacklist;
import game.content.miscellaneous.QuestTab;
import game.content.miscellaneous.RunePouch;
import game.content.miscellaneous.TeleportInterface;
import game.content.miscellaneous.Wolpertinger;
import game.content.miscellaneous.XpLamp;
import game.content.music.MusicTab;
import game.content.prayer.PrayerBook;
import game.content.prayer.QuickPrayers;
import game.content.profile.Profile;
import game.content.profile.ProfileBiography;
//import game.content.quicksetup.QuickSetUp;
import game.content.skilling.Cooking;
import game.content.skilling.SkillMenu;
import game.content.skilling.Skilling;
import game.content.skilling.Skilling.SkillCapeMasterData;
import game.content.skilling.agility.AgilityAssistant;
import game.content.skilling.crafting.EnchantJewelry;
import game.content.skilling.crafting.GemCrafting;
import game.content.skilling.crafting.JewelryCrafting;
import game.content.skilling.crafting.LeatherCrafting;
import game.content.skilling.fletching.BowStringFletching;
import game.content.skilling.fletching.Fletching;
import game.content.skilling.herblore.Herblore;
import game.content.skilling.prayer.BoneOnAltar;
import game.content.skilling.smithing.Smithing;
import game.content.starter.GameMode;
import game.content.starter.NewPlayerContent;
import game.item.DestroyItem;
import game.item.GameItem;
import game.item.ItemAssistant;
import game.npc.pet.Pet;
import game.player.Area;
import game.player.LogOutUpdate;
import game.player.Player;
import game.player.PlayerHandler;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;
import game.player.movement.Movement;
import network.packet.PacketHandler;
import network.packet.PacketType;
import utility.FileUtility;
import utility.Misc;

public class ClickingButtonPacket implements PacketType
{



		@Override
		public void processPacket(final Player player, int packetType, int packetSize, boolean trackPlayer)
		{
				int buttonId = Misc.hexToInt(player.getInStream().buffer, 0, packetSize);

				if (trackPlayer)
				{
						PacketHandler.saveData(player.getPlayerName(), "buttonId: " + buttonId);
				}

				if (ServerConfiguration.DEBUG_MODE)
				{
						Misc.print("[Button: " + buttonId + "] [Dialogue: " + player.getDialogueAction() + "]");
				}
				if (EnchantJewelry.isEnchantBoltButton(player, buttonId))
				{
						return;
				}

				if (CombatInterface.isCombatInterfaceButton(player, buttonId))
				{
						int id = CombatInterface.sendClickedCombatStyle(player, buttonId);
						if (id != 0)
						{
								player.getPA().sendMessage(":packet:combatstyle " + id);
						}
						return;
				}
				if (AutoCast.isOldAutoCastButton(player, buttonId))
				{
						return;
				}
				else if (AutoCast.assignNewAutocast(player, buttonId))
				{
						return;
				}
				else if (CompletionistCape.isCompletionistCapeButton(player, buttonId))
				{
						return;
				}
				else if (XpLamp.xpLampButton(player, buttonId))
				{
						return;
				}
				else if (ClanChatHandler.isClanChatButton(player, buttonId))
				{
						return;
				}
				else if (HighscoresHallOfFame.isButton(player, buttonId))
				{
						return;
				}
				else if (PlayerTitle.playerTitleInterfaceAction(player, buttonId))
				{
						return;
				}


				if (player.doingAnAction() || player.getDoingAgility() || player.getDead() || player.isTeleporting())
				{
						boolean canUseButtonWhileBusy = false;
						if (buttonId >= 70080 && buttonId <= 70094 || buttonId >= 21233 && buttonId <= 21247 || buttonId >= 2171 && buttonId <= 2173 || buttonId == 89176 || buttonId == 89180 || buttonId == 89178 || buttonId == 150 || buttonId == 3189 || buttonId == 152 || buttonId == 48176 || buttonId == 3147)
						{
								canUseButtonWhileBusy = true;
						}
						for (int i = 0; i < NewPlayerContent.tutorialButtonExceptionList.length; i++)
						{
								if (NewPlayerContent.tutorialButtonExceptionList[i] == buttonId)
								{
										canUseButtonWhileBusy = true;
										break;
								}
						}
						if (!canUseButtonWhileBusy)
						{
								return;
						}
				}
//				if (QuickSetUp.isQuickSetUpButton(player, buttonId))
//				{
//						return;
//				}
				else if (SpellBookButton.isSpellBookButton(player, buttonId))
				{
						return;
				}
				else if (BankButtons.isBankButtons(player, buttonId))
				{
						return;
				}
				else if (GameMode.isGameModeButton(player, buttonId))
				{
						return;
				}
				else if (DestroyItem.isDestroyInterfaceButton(player, buttonId))
				{
						return;
				}
				else if (MusicTab.handleClick(player, buttonId))
				{
						return;
				}
				else if (GnomeGlider.isGnomeGliderButton(player, buttonId))
				{
						return;
				}
				else if (QuickPrayers.clickPray(player, buttonId))
				{
						return;
				}
				else if (Profile.isProfileButton(player, buttonId))
				{
						return;
				}
				else if (ProfileBiography.isBiographyButton(player, buttonId))
				{
						return;
				}
				else if (HighscoresInterface.isHighscoresButton(player, buttonId))
				{
						return;
				}
				else if (LeatherCrafting.isLeatherCraftingButton(player, buttonId))
				{
						return;
				}
				if (Achievements.isAchievementButton(player, buttonId))
				{
						return;
				}
				if (LeatherCrafting.isTanningButton(player, buttonId))
				{
						return;
				}

				if (JewelryCrafting.isJewelryInterfaceButton(player, buttonId))
				{
						return;
				}

				if (TeleportInterface.isTeleportInterfaceButton(player, buttonId))
				{
						return;
				}

				if (Smithing.smithingButtons(player, buttonId, ""))
				{
						return;
				}

				if (NpcDropTableInterface.isNpcDropTableButton(player, buttonId))
				{
						return;
				}

				if (GuideBook.isGuideInterfaceButton(player, buttonId))
				{
						return;
				}

				if (PvpBlacklist.isPvpBlacklistButton(player, buttonId))
				{
						return;
				}

				if (RunePouch.runePouchInterfaceButton(player, buttonId))
				{
						return;
				}

				if (buttonId >= 109133 && buttonId <= 109148)
				{
						int index = buttonId - 109133;
						String[] list = {
							//@formatter:off
							"My kills are: " + player.getWildernessKills(),
							"My deaths are: " + player.getWildernessDeaths(),
							"My kdr is: " + QuestTab.getKDR(player.getWildernessKills(), player.getWildernessDeaths()),
							"My melee main kills are: " + player.getMeleeMainKills(),
							"My hybrid kills are: " + player.getHybridKills(),
							"My berserker pure kills are: " + player.getBerserkerPureKills(),
							"My pure kills are: " + player.getPureKills(),
							"My ranged tank kills are: " + player.getRangedTankKills(),
							"My current killstreak is: " + player.currentKillStreak,
							"My highest killstreak is: " + player.killStreaksRecord,
							"My safe kills are: " + player.safeKills,
							"My safe deaths are: " + player.safeDeaths,
							"My bot kills are: " + player.playerBotKills,
							"My bot deaths are: " + player.playerBotDeaths,
							"My current bot killstreak is: " + player.playerBotCurrentKillstreak,
							"My highest bot killstreak is: " + player.playerBotHighestKillstreak,
							//@formatter:on
						};
						if (index > list.length - 1)
						{
								return;
						}
						player.getPA().quickChat(list[index]);
						return;
				}

				switch (buttonId)
				{
						case 94196:
								Movement.stopMovement(player);
								player.startAnimation(2763);
								break;

						case 94197:
								Movement.stopMovement(player);
								player.startAnimation(2756);
								break;

						case 94198:
								Movement.stopMovement(player);
								player.startAnimation(2761);
								break;

						case 94199:
								Movement.stopMovement(player);
								player.startAnimation(2764);
								break;

//						case 109104:
//								QuickSetUp.displayInterface(player);
//								break;

						case 33214:
								SkillMenu.openInterface(player, 1);
								break;

						case 109108:
								GuideBook.displayGuideInterface(player);
								break;

						case 86231:
								LootingBag.closeLootingBagInterface(player);
								break;

						// Reset session.
						case 19146:
								player.currentSessionExperience = 0;
								break;
						// Show session.
						case 19143:
								player.xpBarShowType = "SESSION";
								Skilling.sendXpToDisplay(player);
								break;

						// Show total.
						case 19140:
								player.xpBarShowType = "TOTAL";
								Skilling.sendXpToDisplay(player);
								break;

						// Show COMBAT.
						case 19147:
								player.xpBarShowType = "COMBAT";
								Skilling.sendXpToDisplay(player);
								break;

						case 15062:
								player.usingShop = false;
								break;

						case 99228:
								player.lastProfileTabText = "WEAPON PVP";
								break;

						// Decline button on first trade screen.
						case 13094:
								player.getTradeAndDuel().declineTrade1(true);
								break;

						// Decline button on second trade screen.
						case 13220:
								player.getTradeAndDuel().declineTrade1(true);
								break;


						case 99056:
								player.lastDialogueOptionString = "";
								player.getDH().sendDialogues(player.lastDialogueSelected);
								break;

						case 10239:
						case 6211:
						case 6212:
						case 10238:
								int amount = 1;
								if (buttonId == 6211)
								{
										amount = 2000;
								}
								else if (buttonId == 6212)
								{
										amount = 100;
								}
								else if (buttonId == 10238)
								{
										amount = 5;
								}
								if (player.skillingInterface.equals(ServerConstants.SKILL_NAME[ServerConstants.HERBLORE]))
								{
										Herblore.herbloreInterfaceAction(player, amount);
								}
								else if (player.skillingInterface.equals(ServerConstants.SKILL_NAME[ServerConstants.CRAFTING]))
								{
										GemCrafting.craftingInterfaceAction(player, amount);
								}
								else if (player.skillingInterface.equals(ServerConstants.SKILL_NAME[ServerConstants.PRAYER]))
								{
										BoneOnAltar.prayerInterfaceAction(player, amount);
								}
								else if (player.skillingInterface.equals(ServerConstants.SKILL_NAME[ServerConstants.FLETCHING]))
								{
										BowStringFletching.fletchingInterfaceAction(player, amount);
								}
								else if (player.skillingInterface.equals("HARD LEATHER BODY"))
								{
										LeatherCrafting.hardLeatherBodyInterfaceAction(player, amount);
								}
								else if (player.skillingInterface.equals("STRINGING AMULET"))
								{
										JewelryCrafting.stringAmuletAmount(player, amount);
								}
								else if (player.skillingInterface.equals("COMBINE ARROWS"))
								{
										Fletching.combineArrowPartsAmount(player, amount);
								}
								else if (player.skillingInterface.equals("CUT GEM INTO BOLT TIPS"))
								{
										Fletching.cutGemAmount(player, amount);
								}
								else if (player.skillingInterface.equals("ATTACH TIPS TO BOLT"))
								{
										Fletching.attachTipToBoltAmount(player, amount);
								}
								break;

						case 67207:
								player.getPA().requestUpdates();
								break;

						case 96239:
						case 97016:
								player.playerAssistant.setSidebarInterface(6, 24818); // COMBAT FIRST.
								player.ancientsInterfaceType = 2;
								break;


						case 97019:
						case 96254:
								player.playerAssistant.setSidebarInterface(6, 24800); // TELEPORT FIRST.
								player.ancientsInterfaceType = 0;
								break;


						case 97001:
						case 96236:
								player.playerAssistant.setSidebarInterface(6, 24836); // DEFAULT.
								player.ancientsInterfaceType = 1;
								break;

						case 19136:
								// Toggle quick prayers
								if (player.duelRule[7])
								{
										player.playerAssistant.sendMessage("Prayer has been disabled in this duel!");
										return;
								}
								if (player.quickPray)
								{
										QuickPrayers.turnOffQuicks(player);
										if (!player.prayerActive[ServerConstants.PROTECT_ITEM])
										{
												ItemsKeptOnDeath.updateInterface(player);
										}
										return;
								}
								if (player.getCurrentCombatSkillLevel(ServerConstants.PRAYER) == 0)
								{
										return;
								}
								if (player.getDead())
								{
										return;
								}
								QuickPrayers.turnOnQuicks(player);
								break;

						case 19137:
								// Select quick prayers
								QuickPrayers.selectQuickInterface(player);
								break;

						case 67079:
								// quick curse confirm
								QuickPrayers.clickConfirm(player);
								break;

						case 5001:
								// select your quick prayers/curses
								QuickPrayers.selectQuickInterface(player);
								player.getPA().sendFrame106(5);
								break;

						// Completionist cape interface, close.
						case 54189:
								player.getPA().closeInterfaces();
								break;

						// Close button in Dawntain Guide interface
						case 102189:
								player.getPA().closeInterfaces();
								break;

						/* Dialogue options */

						/* Two options */

						// First option on a two option dialogue
						case 9157:
								TwoOptions.firstOption(player);
								player.getDH().dialogueActionReset();
								break;

						// Second option on a two option dialogue
						case 9158:
								TwoOptions.secondOption(player);
								player.getDH().dialogueActionReset();
								break;

						/* End of Two options */

						/* Three options */

						// First option on a three option dialogue
						case 9167:
								ThreeOptions.firstOption(player);
								player.getDH().dialogueActionReset();
								break;

						// Second option on a three option dialogue
						case 9168:
								ThreeOptions.secondOption(player);
								player.getDH().dialogueActionReset();
								break;

						// Third option on a three option dialogue
						case 9169:
								ThreeOptions.thirdOption(player);
								player.getDH().dialogueActionReset();
								break;

						/* End of Three options */

						/* Four options */

						// First option on a four option dialogue
						case 9178:
								FourOptions.firstOption(player);
								player.getDH().dialogueActionReset();
								break;

						// Second option on a four option dialogue
						case 9179:
								FourOptions.secondOption(player);
								player.getDH().dialogueActionReset();
								break;

						// Third option on a four option dialogue
						case 9180:
								FourOptions.thirdOption(player);
								player.getDH().dialogueActionReset();
								break;

						// Fourth option on a four option dialogue
						case 9181:
								FourOptions.fourthOption(player);
								player.getDH().dialogueActionReset();
								break;

						/* End of Four options */

						/* Five options */

						// First option on a five option dialogue
						case 9190:
								FiveOptions.firstOption(player);
								player.getDH().dialogueActionReset();
								break;

						// Second option on a five option dialogue
						case 9191:
								FiveOptions.secondOption(player);
								player.getDH().dialogueActionReset();
								break;

						// Third option on a five option dialogue
						case 9192:
								FiveOptions.thirdOption(player);
								player.getDH().dialogueActionReset();
								break;

						// Fourth option on a five option dialogue
						case 9193:
								FiveOptions.fourthOption(player);
								player.getDH().dialogueActionReset();
								break;

						// Fifth option on a five option dialogue
						case 9194:
								FiveOptions.fifthOption(player);
								player.getDH().dialogueActionReset();
								break;

						/* End of Five options */

						/* End of Dialogue options */

						/* Start of Bank pin */

						case 58074:
								BankPin.close(player);
								break;

						case 58025:
						case 58026:
						case 58027:
						case 58028:
						case 58029:
						case 58030:
						case 58031:
						case 58032:
						case 58033:
						case 58034:
								BankPin.pinEnter(player, buttonId);
								break;

						// Return to bank.
						case 82032:
								if (!player.usingEquipmentBankInterface)
								{
										return;
								}
								Bank.openUpBank(player, player.getLastBankTabOpened(), true, false);
								player.usingEquipmentBankInterface = false;
								break;

						case 33206:
								EditCombatSkill.editCombatSkillAction(player, "ATTACK");
								break;

						case 33209:
								EditCombatSkill.editCombatSkillAction(player, "STRENGTH");
								break;

						case 33212:
								EditCombatSkill.editCombatSkillAction(player, "DEFENCE");
								break;

						case 33215:
								EditCombatSkill.editCombatSkillAction(player, "RANGED");
								break;

						case 33218:
								EditCombatSkill.editCombatSkillAction(player, "PRAYER");
								break;

						case 33221:
								EditCombatSkill.editCombatSkillAction(player, "MAGIC");
								break;

						case 33207:
								break; /* End of Click on skill icon to change level */

						// Show equipment stats
						case 112181:

								if (player.clipping)
								{
										FileUtility.addLineOnTxt("./data/world/remove clipped tiles.txt", player.getX() + " " + player.getY() + " " + player.getHeight());
								}
								else if (player.saveNpcText)
								{
										AdministratorCommand.saveNpcText(player);
								}
								else
								{
										player.getPA().displayInterface(15106);
								}
								break;

						// Show items kept on death
						case 112178:
								if (player.clipping)
								{
										FileUtility.addLineOnTxt("./data/world/add clipped tiles.txt", player.getX() + " " + player.getY() + " " + player.getHeight());
								}
								else
								{
										ItemsKeptOnDeath.showDeathInterface(player);
								}
								break;

						case 58253:
								player.setInventoryUpdate(true);
								break;
						case 59004:
								player.getPA().closeInterfaces();
								break;
						case 150:
								// Auto retaliate
								Combat.resetPlayerAttack(player);
								player.setAutoRetaliate((player.getAutoRetaliate() == 0) ? 1 : 0);
								player.getPA().sendMessage(":packet:otherbutton 150");
								break;

						/** Specials **/
						case 29188:
								player.specBarId = 7636;
								player.setUsingSpecial(!player.isUsingSpecial());
								CombatInterface.updateSpecialBar(player);
								Combat.handleGraniteMaulPlayer(player);
								break;
						case 29163:
								player.specBarId = 7611;
								player.setUsingSpecial(!player.isUsingSpecial());
								CombatInterface.updateSpecialBar(player);
								Combat.handleGraniteMaulPlayer(player);
								break;
						case 33033:
								player.specBarId = 8505;
								player.setUsingSpecial(!player.isUsingSpecial());
								CombatInterface.updateSpecialBar(player);
								Combat.handleGraniteMaulPlayer(player);
								break;
						case 29038:
								player.specBarId = 7486;
								if (player.getWieldedWeapon() == 4153 || player.getWieldedWeapon() == 18662)
								{
										Combat.handleGraniteMaulPlayer(player);
								}
								else
								{
										player.setUsingSpecial(!player.isUsingSpecial());
								}
								CombatInterface.updateSpecialBar(player);
								break;
						case 8041:
								player.getPA().closeInterfaces();
								break;
						case 29063:
								if (Combat.checkSpecAmount(player, player.getWieldedWeapon()))
								{
										player.gfx0(246);
										player.forcedChat("Raarrrrrgggggghhhhhhh!");
										player.startAnimation(1056);
										player.currentCombatSkillLevel[ServerConstants.STRENGTH] = player.getBaseStrengthLevel() + (player.getBaseStrengthLevel() * 15 / 100);
										Skilling.updateSkillTabFrontTextMain(player, 2);
										CombatInterface.updateSpecialBar(player);
								}
								else
								{
										player.playerAssistant.sendMessage("You don't have the required special energy to use this attack.");
								}
								break;

						case 30007:
								player.specBarId = 7711;
								player.setUsingSpecial(!player.isUsingSpecial());
								CombatInterface.updateSpecialBar(player);
								Combat.handleGraniteMaulPlayer(player);
								break;
						case 48023:
								player.specBarId = 12335;
								player.setUsingSpecial(!player.isUsingSpecial());
								CombatInterface.updateSpecialBar(player);
								Combat.handleGraniteMaulPlayer(player);
								break;
						case 29138:
								player.specBarId = 7586;
								player.setUsingSpecial(!player.isUsingSpecial());
								CombatInterface.updateSpecialBar(player);
								Combat.handleGraniteMaulPlayer(player);
								break;

						// Toxic blowpipe.
						case 29213:
								player.specBarId = 7661;
								player.setUsingSpecial(!player.isUsingSpecial());
								CombatInterface.updateSpecialBar(player);
								Combat.handleGraniteMaulPlayer(player);
								break;
						case 29113:
								player.specBarId = 7561;
								player.setUsingSpecial(!player.isUsingSpecial());
								CombatInterface.updateSpecialBar(player);
								Combat.handleGraniteMaulPlayer(player);
								break;
						case 29238:
								player.specBarId = 7686;
								player.setUsingSpecial(!player.isUsingSpecial());
								CombatInterface.updateSpecialBar(player);
								Combat.handleGraniteMaulPlayer(player);
								break;
						case 30108:
								// Claws
								player.specBarId = 7812;
								player.setUsingSpecial(!player.isUsingSpecial());
								CombatInterface.updateSpecialBar(player);
								Combat.handleGraniteMaulPlayer(player);
								break;
						/** Dueling **/
						case 26065:
								// no forfeit
						case 26040:
								player.duelSlot = -1;
								player.getTradeAndDuel().selectRule(0);
								break;
						case 26066:
								// no movement
						case 26048:
								player.duelSlot = -1;
								player.getTradeAndDuel().selectRule(1);
								break;
						case 26069:
								// no range
						case 26042:
								player.duelSlot = -1;
								player.getTradeAndDuel().selectRule(2);
								break;
						case 26070:
								// no melee
						case 26043:
								player.duelSlot = -1;
								player.getTradeAndDuel().selectRule(3);
								break;
						case 26071:
								// no mage
						case 26041:
								player.duelSlot = -1;
								player.getTradeAndDuel().selectRule(4);
								break;
						case 26072:
								// no drinks
						case 26045:
								player.duelSlot = -1;
								player.getTradeAndDuel().selectRule(5);
								break;
						case 26073:
								// no food
						case 26046:
								player.duelSlot = -1;
								player.getTradeAndDuel().selectRule(6);
								break;
						case 26074:
								// no prayer
						case 26047:
								player.duelSlot = -1;
								player.getTradeAndDuel().selectRule(7);
								break;
						case 26076:
								// obsticals
						case 26075:
								player.duelSlot = -1;
								player.getTradeAndDuel().selectRule(8);
								break;

						// Aby whip & dds only
						case 2158:
								//	case 2157:
								player.duelSlot = -1;
								player.getTradeAndDuel().selectRule(9);
								break;
						case 30136:
								// sp attack
						case 30137:
								player.duelSlot = -1;
								player.getTradeAndDuel().selectRule(10);
								break;
						case 53245:
								// no helm
								player.duelSlot = 0;
								player.getTradeAndDuel().selectRule(11);
								break;
						case 53246:
								// no cape
								player.duelSlot = 1;
								player.getTradeAndDuel().selectRule(12);
								break;
						case 53247:
								// no ammy
								player.duelSlot = 2;
								player.getTradeAndDuel().selectRule(13);
								break;
						case 53249:
								// no weapon.
								player.duelSlot = 3;
								player.getTradeAndDuel().selectRule(14);
								break;
						case 53250:
								// no body
								player.duelSlot = 4;
								player.getTradeAndDuel().selectRule(15);
								break;
						case 53251:
								// no shield
								player.duelSlot = 5;
								player.getTradeAndDuel().selectRule(16);
								break;
						case 53252:
								// no legs
								player.duelSlot = 7;
								player.getTradeAndDuel().selectRule(17);
								break;
						case 53255:
								// no gloves
								player.duelSlot = 9;
								player.getTradeAndDuel().selectRule(18);
								break;
						case 53254:
								// no boots
								player.duelSlot = 10;
								player.getTradeAndDuel().selectRule(19);
								break;
						case 53253:
								// no rings
								player.duelSlot = 12;
								player.getTradeAndDuel().selectRule(20);
								break;
						case 53248:
								// no arrows
								player.duelSlot = 13;
								player.getTradeAndDuel().selectRule(21);
								break;

						case 26018:
								if (GameMode.getGameMode(player, "IRON MAN"))
								{
										player.getPA().sendMessage("Iron men cannot stake.");
										return;
								}
								if (player.getDuelStatus() != 1)
								{
										return;
								}
								if (Area.inDuelArena(player))
								{
										Player o = player.getTradeAndDuel().getPartner();
										if (o == null)
										{
												player.getTradeAndDuel().declineDuel(false);
												return;
										}

										if (System.currentTimeMillis() - player.timeDuelRuleChanged <= 5000)
										{
												int time = 5 - (int) ((System.currentTimeMillis() - player.timeDuelRuleChanged) / 1000);
												if (time < 0)
												{
														time = 0;
												}
												player.getPA().sendMessage(ServerConstants.RED_COL + "Please check the rules and items staked, last modification: " + time + " seconds ago.");
												return;
										}
										if (System.currentTimeMillis() - o.timeDuelRuleChanged <= 5000)
										{
												int time = 5 - (int) ((System.currentTimeMillis() - o.timeDuelRuleChanged) / 1000);
												if (time < 0)
												{
														time = 0;
												}
												player.getPA().sendMessage(ServerConstants.RED_COL + "Please check the rules and items staked, last modification: " + time + " seconds ago.");
												return;
										}

										if (player.duelRule[2] && player.duelRule[3] && player.duelRule[4])
										{
												player.playerAssistant.sendMessage("You won't be able to attack the player with the rules you have set.");
												break;
										}

										// Weapon rule and Abby whip & dds only rule.
										if (player.duelRule[14] && player.duelRule[9])
										{
												player.playerAssistant.sendMessage("You cannot start the duel with Abyssal whip & Dds enabled and Weapon slot disabled.");
												break;
										}
										if (player.duelRule[9])
										{
												// Weapon rule.
												if (!player.duelRule[14])
												{
														if (!ItemAssistant.hasItemEquippedSlot(player, 4151, ServerConstants.WEAPON_SLOT) && !ItemAssistant.hasItemInInventory(player, 4151))
														{
																player.getPA().sendMessage("You need an Abyssal whip to start this duel.");
																return;
														}
														if (!player.duelRule[10])
														{
																if (!ItemAssistant.hasItemEquippedSlot(player, 5698, ServerConstants.WEAPON_SLOT) && !ItemAssistant.hasItemInInventory(player, 5698))
																{
																		player.getPA().sendMessage("You need a Dragon dagger p++ to start this duel.");
																		return;
																}
														}
												}
										}

										if (!player.getTradeAndDuel().hasRequiredSpaceForDuel())
										{
												return;
										}

										if (player.getCombatLevel() != o.getCombatLevel())
										{
												if (player.getTradeAndDuel().myStakedItems.size() > 0)
												{
														player.getPA().sendMessage("Your combat level is not the same as your opponent.");
														return;
												}
										}

										player.setDuelStatus(2);
										if (player.getDuelStatus() == 2)
										{
												player.getPA().sendFrame126("Waiting for other player...", 6684);
												o.getPA().sendFrame126("Other player has accepted.", 6684);
										}
										if (o.getDuelStatus() == 2)
										{
												o.getPA().sendFrame126("Waiting for other player...", 6684);
												player.getPA().sendFrame126("Other player has accepted.", 6684);
										}

										if (player.getDuelStatus() == 2 && o.getDuelStatus() == 2)
										{
												player.canOffer = false;
												o.canOffer = false;
												player.setDuelStatus(3);
												o.setDuelStatus(3);
												player.getTradeAndDuel().confirmDuel();
												o.getTradeAndDuel().confirmDuel();
										}
								}
								else
								{
										Player o = player.getTradeAndDuel().getPartner();
										player.getTradeAndDuel().declineDuel(false);
										if (o != null)
										{
												o.getTradeAndDuel().declineDuel(false);
										}
								}
								break;

						case 25120:
								if (player.getDuelStatus() != 3)
								{
										return;
								}
								if (Area.inDuelArena(player))
								{
										if (player.getDuelStatus() == 5)
										{
												break;
										}
										Player o2 = player.getTradeAndDuel().getPartner();
										final Player o1 = o2;
										if (o1 == null)
										{
												player.getTradeAndDuel().declineDuel(false);
												return;
										}

										if (!player.getTradeAndDuel().hasRequiredSpaceForDuel())
										{
												return;
										}


										player.setDuelStatus(4);
										if (o1.getDuelStatus() == 4 && player.getDuelStatus() == 4)
										{
												player.getTradeAndDuel().startDuel();
												o1.getTradeAndDuel().startDuel();
												o1.setDuelCount(4);
												player.setDuelCount(4);

												CycleEventHandler.getSingleton().addEvent(player, new CycleEvent()
												{
														@Override
														public void execute(CycleEventContainer container)
														{
																player.duelForceChatCount--;
																player.forcedChat("" + player.duelForceChatCount + "");
																if (player.duelForceChatCount == 0)
																{
																		container.stop();
																}
														}

														@Override
														public void stop()
														{
																player.forcedChat("FIGHT!");
																player.duelForceChatCount = 4;
																player.damageTaken = new int[ServerConstants.MAXIMUM_PLAYERS];
																player.setDuelCount(0);
														}
												}, 2);

												CycleEventHandler.getSingleton().addEvent(o1, new CycleEvent()
												{
														@Override
														public void execute(CycleEventContainer container)
														{
																o1.duelForceChatCount--;
																o1.forcedChat("" + o1.duelForceChatCount + "");
																if (o1.duelForceChatCount == 0)
																{
																		container.stop();
																}
														}

														@Override
														public void stop()
														{
																o1.forcedChat("FIGHT!");
																o1.duelForceChatCount = 4;
																o1.damageTaken = new int[ServerConstants.MAXIMUM_PLAYERS];
																o1.setDuelCount(0);
														}
												}, 2);

										}
										else
										{
												player.getPA().sendFrame126("Waiting for other player...", 6571);
												o1.getPA().sendFrame126("Other player has accepted", 6571);
										}
								}
								else
								{
										Player o = player.getTradeAndDuel().getPartner();
										player.getTradeAndDuel().declineDuel(false);
										if (o != null)
										{
												o.getTradeAndDuel().declineDuel(false);
										}
										player.playerAssistant.sendMessage("You can't stake out of Duel Arena.");
								}
								break;

						// Activate special attack on Summoning orb.
						case 19141:
								Wolpertinger.specialAttackRequirements(player);
								break;

						// Dismiss on Summoning orb.
						case 19142:
								Pet.dismissFamiliar(player, false);
								break;

						case 19145:
								// Call familiar.
								if (!player.getPetSummoned())
								{
										player.playerAssistant.sendMessage("You do not have a familiar summoned.");
								}
								if ((System.currentTimeMillis() - player.callFamiliarTimer) < 3000)
								{
										return;
								}
								player.forceCallFamiliar = true;
								player.callFamiliarTimer = System.currentTimeMillis();
								break;

						case 152:
								if (player.getDoingAgility())
								{
										return;
								}
								if (player.resting)
								{
										AgilityAssistant.stopResting(player);
										return;
								}
								if (player.runModeOn)
								{
										player.runModeOn = false;
										player.getPA().sendFrame36(173, 0);
								}
								else
								{
										player.runModeOn = true;
										player.getPA().sendFrame36(173, 1);
								}
								AgilityAssistant.updateRunEnergyInterface(player);
								break;

						case 153:
								if (!player.resting)
								{
										AgilityAssistant.startResting(player);
								}
								else
								{
										AgilityAssistant.stopResting(player);
								}
								break;
						case 9154:
								LogOutUpdate.manualLogOut(player);
								break;

						case 89176:
								PrayerBook.activatePrayer(player, 26);
								break;

						case 89178:
								PrayerBook.activatePrayer(player, 27);
								break;

						case 89180:
								PrayerBook.activatePrayer(player, 28);
								break;
						case 21233:
								// thick skin
								PrayerBook.activatePrayer(player, 0);
								break;
						case 21234:
								// burst of str
								PrayerBook.activatePrayer(player, 1);
								break;
						case 21235:
								// charity of thought
								PrayerBook.activatePrayer(player, 2);
								break;
						case 70080:
								// range
								PrayerBook.activatePrayer(player, 3);
								break;
						case 70082:
								// mage
								PrayerBook.activatePrayer(player, 4);
								break;
						case 21236:
								// rockskin
								PrayerBook.activatePrayer(player, 5);
								break;
						case 21237:
								// super human
								PrayerBook.activatePrayer(player, 6);
								break;
						case 21238:
								// improved reflexes
								PrayerBook.activatePrayer(player, 7);
								break;
						case 21239:
								// hawk eye
								PrayerBook.activatePrayer(player, 8);
								break;
						case 21240:
								PrayerBook.activatePrayer(player, 9);
								break;
						case 21241:
								PrayerBook.activatePrayer(player, 10);
								ItemsKeptOnDeath.updateInterface(player);
								break;
						case 70084:
								// 26 range
								PrayerBook.activatePrayer(player, 11);
								break;
						case 70086:
								// 27 mage
								PrayerBook.activatePrayer(player, 12);
								break;
						case 21242:
								// steel skin
								PrayerBook.activatePrayer(player, 13);
								break;
						case 21243:
								// ultimate str
								PrayerBook.activatePrayer(player, 14);
								break;
						case 21244:
								// incredible reflex
								PrayerBook.activatePrayer(player, 15);
								break;
						case 21245:
								// protect from magic
								PrayerBook.activatePrayer(player, 16);
								break;
						case 21246:
								// protect from range
								PrayerBook.activatePrayer(player, 17);
								break;
						case 21247:
								// protect from melee
								PrayerBook.activatePrayer(player, 18);
								break;
						case 70088:
								// 44 range
								PrayerBook.activatePrayer(player, 19);
								break;
						case 70090:
								// 45 mystic
								PrayerBook.activatePrayer(player, 20);
								break;
						case 2171:
								// retribution.
								PrayerBook.activatePrayer(player, 21);
								break;
						case 2172:
								// redem
								PrayerBook.activatePrayer(player, 22);
								break;
						case 2173:
								// smite
								PrayerBook.activatePrayer(player, 23);
								break;
						case 70092:
								// chiv
								PrayerBook.activatePrayer(player, 24);
								break;
						case 70094:
								// piety
								PrayerBook.activatePrayer(player, 25);
								break;

						case 13092:
								if (GameMode.getGameMode(player, "IRON MAN"))
								{
										player.getPA().sendMessage("Iron men cannot trade.");
										return;
								}
								Player other = PlayerHandler.players[player.getTradeWith()];
								if (other == null)
								{
										player.getTradeAndDuel().declineTrade1(true);
										break;
								}
								player.getPA().sendFrame126("Waiting for other player...", 3431);
								other.getPA().sendFrame126("Other player has accepted", 3431);
								player.goodTrade = true;
								other.goodTrade = true;
								for (GameItem item : player.getTradeAndDuel().offeredItems)
								{
										if (item.id > 0)
										{
												if (ItemAssistant.getFreeInventorySlots(other) < player.getTradeAndDuel().offeredItems.size())
												{
														player.playerAssistant.sendMessage(other.getCapitalizedName() + " only has " + ItemAssistant.getFreeInventorySlots(other) + " free slots, please remove " + (player.getTradeAndDuel().offeredItems.size() - ItemAssistant.getFreeInventorySlots(other)) + " items.");
														other.playerAssistant.sendMessage(player.getCapitalizedName() + " has to remove " + (player.getTradeAndDuel().offeredItems.size() - ItemAssistant.getFreeInventorySlots(other)) + " items or you could offer them " + (player.getTradeAndDuel().offeredItems.size() - ItemAssistant.getFreeInventorySlots(other)) + " items.");
														player.goodTrade = false;
														other.goodTrade = false;
														player.getPA().sendFrame126("Not enough inventory space...", 3431);
														other.getPA().sendFrame126("Not enough inventory space...", 3431);
														break;
												}
												else
												{
														if (!EdgeAndWestsRule.canPickUpOrReceiveBrew(other, item.id))
														{
																player.goodTrade = false;
																other.goodTrade = false;
																player.getPA().sendFrame126("Cannot give excess brews", 3431);
																other.getPA().sendFrame126("Cannot give excess brews", 3431);
																break;
														}
														else
														{
																player.getPA().sendFrame126("Waiting for other player...", 3431);
																other.getPA().sendFrame126("Other player has accepted", 3431);
																player.goodTrade = true;
																other.goodTrade = true;
														}
												}
										}
								}
								if (player.isInTrade() && !player.tradeConfirmed && other.goodTrade && player.goodTrade)
								{
										player.tradeConfirmed = true;
										if (other.tradeConfirmed)
										{
												player.getTradeAndDuel().confirmScreen();
												other.getTradeAndDuel().confirmScreen();
												break;
										}
								}
								break;
						case 13218:
								Player ot1 = PlayerHandler.players[player.getTradeWith()];
								if (ot1 == null)
								{
										player.getTradeAndDuel().declineTrade1(true);
										break;
								}

								player.tradeAccepted = true;
								if (player.isInTrade() && player.tradeConfirmed && ot1.tradeConfirmed && !player.tradeConfirmed2)
								{
										player.tradeConfirmed2 = true;
										if (ot1.tradeConfirmed2)
										{
												player.ignoreTradeMessage = true;
												player.acceptedTrade = true;
												ot1.acceptedTrade = true;
												ItemTransferLog.tradeCompleted(player, ot1);
												player.getTradeAndDuel().giveItems(ot1);
												ot1.getTradeAndDuel().giveItems(player);

												player.getTradeAndDuel().offeredItems.clear();
												ot1.getTradeAndDuel().offeredItems.clear();

												player.getPA().closeInterfaces();
												player.tradeResetNeeded = true;
												player.getTradeAndDuel().tradeResetRequired();
												ot1.getPA().closeInterfaces();
												ot1.tradeResetNeeded = true;
												ot1.getTradeAndDuel().tradeResetRequired();
												ot1.playerAssistant.sendMessage("You have finished a trade with " + player.getCapitalizedName() + ".");
												player.getPA().sendMessage("You have finished a trade with " + ot1.getCapitalizedName() + ".");
												player.ignoreTradeMessage = false;
												break;
										}
										ot1.getPA().sendFrame126("Other player has accepted.", 3535);
										player.getPA().sendFrame126("Waiting for other player...", 3535);
								}
								break;
						case 74176:
								if (!player.mouseButton)
								{
										player.mouseButton = true;
										player.getPA().sendFrame36(500, 1);
										player.getPA().sendFrame36(170, 1);
								}
								else if (player.mouseButton)
								{
										player.mouseButton = false;
										player.getPA().sendFrame36(500, 0);
										player.getPA().sendFrame36(170, 0);
								}
								break;
						case 3189:
								player.splitChat = !player.splitChat;
								InterfaceAssistant.splitPrivateChat(player);
								break;
						case 74180:
								if (!player.chatEffects)
								{
										player.chatEffects = true;
										player.getPA().sendFrame36(501, 1);
										player.getPA().sendFrame36(171, 0);
								}
								else
								{
										player.chatEffects = false;
										player.getPA().sendFrame36(501, 0);
										player.getPA().sendFrame36(171, 1);
								}
								break;
						case 74188:
								if (!player.acceptAid)
								{
										player.acceptAid = true;
										player.getPA().sendFrame36(503, 1);
										player.getPA().sendFrame36(427, 1);
								}
								else
								{
										player.acceptAid = false;
										player.getPA().sendFrame36(503, 0);
										player.getPA().sendFrame36(427, 0);
								}
								break;
						case 74201:
								// brightness1
								player.getPA().sendFrame36(505, 1);
								player.getPA().sendFrame36(506, 0);
								player.getPA().sendFrame36(507, 0);
								player.getPA().sendFrame36(508, 0);
								player.getPA().sendFrame36(166, 1);
								break;
						case 74203:
								// brightness2
								player.getPA().sendFrame36(505, 0);
								player.getPA().sendFrame36(506, 1);
								player.getPA().sendFrame36(507, 0);
								player.getPA().sendFrame36(508, 0);
								player.getPA().sendFrame36(166, 2);
								break;
						case 74204:
								// brightness3
								player.getPA().sendFrame36(505, 0);
								player.getPA().sendFrame36(506, 0);
								player.getPA().sendFrame36(507, 1);
								player.getPA().sendFrame36(508, 0);
								player.getPA().sendFrame36(166, 3);
								break;
						case 74205:
								// brightness4
								player.getPA().sendFrame36(505, 0);
								player.getPA().sendFrame36(506, 0);
								player.getPA().sendFrame36(507, 0);
								player.getPA().sendFrame36(508, 1);
								player.getPA().sendFrame36(166, 4);
								break;
						case 74206:
								// area1
								player.getPA().sendFrame36(509, 1);
								player.getPA().sendFrame36(510, 0);
								player.getPA().sendFrame36(511, 0);
								player.getPA().sendFrame36(512, 0);
								break;
						case 74207:
								// area2
								player.getPA().sendFrame36(509, 0);
								player.getPA().sendFrame36(510, 1);
								player.getPA().sendFrame36(511, 0);
								player.getPA().sendFrame36(512, 0);
								break;
						case 74208:
								// area3
								player.getPA().sendFrame36(509, 0);
								player.getPA().sendFrame36(510, 0);
								player.getPA().sendFrame36(511, 1);
								player.getPA().sendFrame36(512, 0);
								break;
						case 74209:
								// area4
								player.getPA().sendFrame36(509, 0);
								player.getPA().sendFrame36(510, 0);
								player.getPA().sendFrame36(511, 0);
								player.getPA().sendFrame36(512, 1);
								break;

						/* Emote */

						case 168:
								// Yes
								Movement.stopMovement(player);
								player.startAnimation(855);
								break;
						case 169:
								Movement.stopMovement(player);
								player.startAnimation(856);
								break;
						case 162:
								Movement.stopMovement(player);
								player.startAnimation(857);
								break;
						case 164:
								player.startAnimation(858);
								break;
						case 165:
								Movement.stopMovement(player);
								player.startAnimation(859);
								break;
						case 161:
								Movement.stopMovement(player);
								player.startAnimation(860);
								break;
						case 170:
								Movement.stopMovement(player);
								player.startAnimation(861);
								break;
						case 171:
								Movement.stopMovement(player);
								player.startAnimation(862);
								break;
						case 163:
								Movement.stopMovement(player);
								player.startAnimation(863);
								break;
						case 167:
								Movement.stopMovement(player);
								player.startAnimation(864);
								break;
						case 172:
								Movement.stopMovement(player);
								player.startAnimation(865);
								break;
						case 166:
								Movement.stopMovement(player);
								player.startAnimation(ItemAssistant.hasItemEquippedSlot(player, 10394, ServerConstants.LEG_SLOT) ? 5316 : 866);
								break;
						case 52050:
								Movement.stopMovement(player);
								player.startAnimation(2105);
								break;
						case 52051:
								Movement.stopMovement(player);
								player.startAnimation(2106);
								break;
						case 52052:
								Movement.stopMovement(player);
								player.startAnimation(2107);
								break;
						case 52053:
								Movement.stopMovement(player);
								player.startAnimation(2108);
								break;
						case 52054:
								Movement.stopMovement(player);
								player.startAnimation(2109);
								break;
						case 52055:
								Movement.stopMovement(player);
								player.startAnimation(2110);
								break;
						case 52056:
								Movement.stopMovement(player);
								player.startAnimation(2111);
								break;
						case 52057:
								Movement.stopMovement(player);
								player.startAnimation(2112);
								break;
						case 52058:
								Movement.stopMovement(player);
								player.startAnimation(2113);
								break;
						case 43092:
								player.startAnimation(0x558);
								player.gfx0(574);
								Movement.stopMovement(player);
								break;
						case 2155:
								Movement.stopMovement(player);
								player.startAnimation(0x46B);
								break;
						case 25103:
								Movement.stopMovement(player);
								player.startAnimation(0x46A);
								break;
						case 25106:
								Movement.stopMovement(player);
								player.startAnimation(0x469);
								break;
						case 2154:
								Movement.stopMovement(player);
								player.startAnimation(0x468);
								break;
						case 52071:
								Movement.stopMovement(player);
								player.startAnimation(0x84F);
								break;
						case 52072:
								Movement.stopMovement(player);
								player.startAnimation(0x850);
								break;
						case 59062:
								Movement.stopMovement(player);
								player.startAnimation(2836);
								break;
						case 72032:
								Movement.stopMovement(player);
								player.startAnimation(3544);
								break;
						case 72033:
								Movement.stopMovement(player);
								player.startAnimation(3543);
								break;
						case 72254:
								Movement.stopMovement(player);
								player.startAnimation(6111);
								break;

						case 95080:
								if (player.getTransformed() == 0)
								{
										player.doingActionEvent(7);
										player.startAnimation(11044);
										player.gfx0(1973);
								}
								break;

						case 95081:
								player.startAnimation(10530);
								break;

						case 95082:
								if (player.getTransformed() == 0)
								{
										player.doingActionEvent(4);
										player.startAnimation(8770);
										player.gfx0(1553);
								}
								break;

						case 95083:
								player.startAnimation(7531);
								break;

						case 95084:
								if (player.getTransformed() == 0)
								{
										player.doingActionEvent(9);
										player.startAnimation(9990);
										player.gfx0(1734);
								}
								break;

						case 95085:
								player.startAnimation(4278);
								break;

						case 95086:
								player.startAnimation(4280);
								break;

						case 95087:
								player.startAnimation(4275);
								break;

						case 95088:
								if (player.getTransformed() == 0)
								{
										player.doingActionEvent(3);
										player.startAnimation(7272);
										player.gfx0(1244);
								}
								break;

						case 95089:
								if (player.getTransformed() == 0)
								{
										player.doingActionEvent(6);
										player.startAnimation(2414);
										player.gfx0(1537);
								}
								break;

						// Vengeance
						case 118098:
								if (!player.spellBook.equals("LUNAR"))
								{
										PacketHandler.spellbookLog.add(player.getPlayerName() + " at " + Misc.getDate());
										PacketHandler.spellbookLog.add("Current spellbook: " + player.spellBook + ", abusing: Vengeance");
										return;
								}
								Combat.castVengeance(player);
								break;

						case 154:
								handleSkillCape(player);
								break;

						case 34170:
								player.rawBeefChosen = true;
								Fletching.attemptData(player, 1, false);
								break;
						case 34169:
								player.rawBeefChosen = true;
								Fletching.attemptData(player, 5, false);
								break;
						case 34168:
								player.rawBeefChosen = true;
								Fletching.attemptData(player, 10, false);
								break;
						case 34167:
								player.rawBeefChosen = true;
								Fletching.attemptData(player, 28, false);
								break;
						case 34174:
								Fletching.attemptData(player, 1, true);
								break;
						case 34173:
								Fletching.attemptData(player, 5, true);
								break;
						case 34172:
								Fletching.attemptData(player, 10, true);
								break;
						case 34171:
								Fletching.attemptData(player, 28, true);
								break;
						case 34185:
								if (player.playerFletch)
								{
										Fletching.attemptData(player, 1, 0);
								}
								break;
						case 34184:
								if (player.playerFletch)
								{
										Fletching.attemptData(player, 5, 0);
								}
								break;
						case 34183:
								if (player.playerFletch)
								{
										Fletching.attemptData(player, 10, 0);
								}
								break;
						case 34182:
								if (player.playerFletch)
								{
										Fletching.attemptData(player, 28, 0);
								}
								break;
						case 34189:
								if (player.playerFletch)
								{
										Fletching.attemptData(player, 1, 1);
								}
								break;
						case 34188:
								if (player.playerFletch)
								{
										Fletching.attemptData(player, 5, 1);
								}
								break;
						case 34187:
								if (player.playerFletch)
								{
										Fletching.attemptData(player, 10, 1);
								}
								break;
						case 34186:
								if (player.playerFletch)
								{
										Fletching.attemptData(player, 28, 1);
								}
								break;
						case 34193:
								if (player.playerFletch)
								{
										Fletching.attemptData(player, 1, 2);
								}
								break;
						case 34192:
								if (player.playerFletch)
								{
										Fletching.attemptData(player, 5, 2);
								}
								break;
						case 34191:
								if (player.playerFletch)
								{
										Fletching.attemptData(player, 10, 2);
								}
								break;
						case 34190:
								if (player.playerFletch)
								{
										Fletching.attemptData(player, 28, 2);
								}
								break;

						case 53152:
								Cooking.getAmount(player, 1);
								break;
						case 53151:
								Cooking.getAmount(player, 5);
								break;
						case 53150:
								Cooking.getAmount(player, 10);
								break;
						case 53149:
								Cooking.getAmount(player, 28);
								break;
				}

		}

		/**
		 * Skill cape emotes.
		 *
		 * @param player
		 *        The player.
		 */
		public void handleSkillCape(Player player)
		{
				if (Combat.inCombatAlert(player))
				{
						return;
				}
				boolean accessed = true;

				// Attack cape.
				if (player.playerEquipment[ServerConstants.CAPE_SLOT] == 9747 || player.playerEquipment[ServerConstants.CAPE_SLOT] == 9748 || player.playerEquipment[ServerConstants.CAPE_SLOT] == SkillCapeMasterData.ATTACK.getLegendCapeId())
				{
						player.startAnimation(4959);
						player.gfx0(823);
						player.doingActionEvent(5);
				}

				// Strength cape.
				else if (player.playerEquipment[ServerConstants.CAPE_SLOT] == 9750 || player.playerEquipment[ServerConstants.CAPE_SLOT] == 9751 || player.playerEquipment[ServerConstants.CAPE_SLOT] == SkillCapeMasterData.STRENGTH.getLegendCapeId())
				{
						player.startAnimation(4981);
						player.gfx0(828);
						player.doingActionEvent(16);
				}

				// Defence cape.
				else if (player.playerEquipment[ServerConstants.CAPE_SLOT] == 9753 || player.playerEquipment[ServerConstants.CAPE_SLOT] == 9754 || player.playerEquipment[ServerConstants.CAPE_SLOT] == SkillCapeMasterData.DEFENCE.getLegendCapeId())
				{
						player.startAnimation(4961);
						player.gfx0(824);
						player.doingActionEvent(9);
				}

				// Ranging cape.
				else if (player.playerEquipment[ServerConstants.CAPE_SLOT] == 9756 || player.playerEquipment[ServerConstants.CAPE_SLOT] == 9757 || player.playerEquipment[ServerConstants.CAPE_SLOT] == SkillCapeMasterData.RANGED.getLegendCapeId())
				{
						player.startAnimation(4973);
						player.gfx0(832);
						player.doingActionEvent(8);
				}

				// Prayer cape.
				else if (player.playerEquipment[ServerConstants.CAPE_SLOT] == 9759 || player.playerEquipment[ServerConstants.CAPE_SLOT] == 9760 || player.playerEquipment[ServerConstants.CAPE_SLOT] == SkillCapeMasterData.PRAYER.getLegendCapeId())
				{
						player.startAnimation(4979);
						player.gfx0(829);
						player.doingActionEvent(10);
				}

				// Magic cape.
				else if (player.playerEquipment[ServerConstants.CAPE_SLOT] == 9762 || player.playerEquipment[ServerConstants.CAPE_SLOT] == 9763 || player.playerEquipment[ServerConstants.CAPE_SLOT] == SkillCapeMasterData.MAGIC.getLegendCapeId())
				{
						player.startAnimation(4939);
						player.gfx0(813);
						player.doingActionEvent(5);
				}

				// Runecrafting cape.
				else if (player.playerEquipment[ServerConstants.CAPE_SLOT] == 9765 || player.playerEquipment[ServerConstants.CAPE_SLOT] == 9766 || player.playerEquipment[ServerConstants.CAPE_SLOT] == SkillCapeMasterData.RUNECRAFTING.getLegendCapeId())
				{
						player.startAnimation(4947);
						player.gfx0(817);
						player.doingActionEvent(10);
				}

				// Hitpoints cape.
				else if (player.playerEquipment[ServerConstants.CAPE_SLOT] == 9768 || player.playerEquipment[ServerConstants.CAPE_SLOT] == 9769 || player.playerEquipment[ServerConstants.CAPE_SLOT] == SkillCapeMasterData.HITPOINTS.getLegendCapeId())
				{
						player.startAnimation(4971);
						player.gfx0(833);
						player.doingActionEvent(6);
				}

				// Agility.
				else if (player.playerEquipment[ServerConstants.CAPE_SLOT] == 9771 || player.playerEquipment[ServerConstants.CAPE_SLOT] == 9772 || player.playerEquipment[ServerConstants.CAPE_SLOT] == SkillCapeMasterData.AGILITY.getLegendCapeId())
				{
						player.startAnimation(4977);
						player.gfx0(830);
						player.doingActionEvent(7);
				}

				// Herblore cape.
				else if (player.playerEquipment[ServerConstants.CAPE_SLOT] == 9774 || player.playerEquipment[ServerConstants.CAPE_SLOT] == 9775 || player.playerEquipment[ServerConstants.CAPE_SLOT] == SkillCapeMasterData.HERBLORE.getLegendCapeId())
				{
						player.startAnimation(4969);
						player.gfx0(835);
						player.doingActionEvent(14);
				}

				// Thieving cape.
				else if (player.playerEquipment[ServerConstants.CAPE_SLOT] == 9777 || player.playerEquipment[ServerConstants.CAPE_SLOT] == 9778 || player.playerEquipment[ServerConstants.CAPE_SLOT] == SkillCapeMasterData.THIEVING.getLegendCapeId())
				{
						player.startAnimation(4965);
						player.gfx0(826);
						player.doingActionEvent(5);
				}

				// Crafting cape.
				else if (player.playerEquipment[ServerConstants.CAPE_SLOT] == 9780 || player.playerEquipment[ServerConstants.CAPE_SLOT] == 9781 || player.playerEquipment[ServerConstants.CAPE_SLOT] == SkillCapeMasterData.CRAFTING.getLegendCapeId())
				{
						player.startAnimation(4949);
						player.gfx0(818);
						player.doingActionEvent(13);
				}

				// Fletching cape.
				else if (player.playerEquipment[ServerConstants.CAPE_SLOT] == 9783 || player.playerEquipment[ServerConstants.CAPE_SLOT] == 9784 || player.playerEquipment[ServerConstants.CAPE_SLOT] == SkillCapeMasterData.FLETCHING.getLegendCapeId())
				{
						player.startAnimation(4937);
						player.doingActionEvent(13);
						player.gfx0(812);
				}

				// Slayer cape.
				else if (player.playerEquipment[ServerConstants.CAPE_SLOT] == 9786 || player.playerEquipment[ServerConstants.CAPE_SLOT] == 9787 || player.playerEquipment[ServerConstants.CAPE_SLOT] == SkillCapeMasterData.SLAYER.getLegendCapeId())
				{
						player.startAnimation(4967);
						player.doingActionEvent(4);
						player.gfx0(827);
				}

				// Mining cape.
				else if (player.playerEquipment[ServerConstants.CAPE_SLOT] == 9792 || player.playerEquipment[ServerConstants.CAPE_SLOT] == 9793)
				{
						player.startAnimation(4941);
						player.gfx0(814);
						player.doingActionEvent(7);
				}

				// Smithing cape.
				else if (player.playerEquipment[ServerConstants.CAPE_SLOT] == 9795 || player.playerEquipment[ServerConstants.CAPE_SLOT] == 9796 || player.playerEquipment[ServerConstants.CAPE_SLOT] == SkillCapeMasterData.SMITHING.getLegendCapeId())
				{
						player.startAnimation(4943);
						player.doingActionEvent(19);
						player.gfx0(815);
				}

				// Fishing cape.
				else if (player.playerEquipment[ServerConstants.CAPE_SLOT] == 9798 || player.playerEquipment[ServerConstants.CAPE_SLOT] == 9799 || player.playerEquipment[ServerConstants.CAPE_SLOT] == SkillCapeMasterData.FISHING.getLegendCapeId())
				{
						player.startAnimation(4951);
						player.doingActionEvent(13);
						player.gfx0(819);
				}

				// Cooking cape.
				else if (player.playerEquipment[ServerConstants.CAPE_SLOT] == 9801 || player.playerEquipment[ServerConstants.CAPE_SLOT] == 9802 || player.playerEquipment[ServerConstants.CAPE_SLOT] == SkillCapeMasterData.COOKING.getLegendCapeId())
				{
						player.startAnimation(4955);
						player.gfx0(821);
						player.doingActionEvent(24);
				}

				// Firemaking cape.
				else if (player.playerEquipment[ServerConstants.CAPE_SLOT] == 9804 || player.playerEquipment[ServerConstants.CAPE_SLOT] == 9805 || player.playerEquipment[ServerConstants.CAPE_SLOT] == SkillCapeMasterData.FIREMAKING.getLegendCapeId())
				{
						player.startAnimation(4975);
						player.gfx0(831);
						player.doingActionEvent(7);
				}

				// Woodcutting cape.
				else if (player.playerEquipment[ServerConstants.CAPE_SLOT] == 9807 || player.playerEquipment[ServerConstants.CAPE_SLOT] == 9808 || player.playerEquipment[ServerConstants.CAPE_SLOT] == SkillCapeMasterData.WOODCUTTING.getLegendCapeId())
				{
						player.startAnimation(4957);
						player.gfx0(822);
						player.doingActionEvent(20);
				}

				// Farming cape.
				else if (player.playerEquipment[ServerConstants.CAPE_SLOT] == 9810 || player.playerEquipment[ServerConstants.CAPE_SLOT] == 9811 || player.playerEquipment[ServerConstants.CAPE_SLOT] == SkillCapeMasterData.FARMING.getLegendCapeId())
				{
						player.startAnimation(4963);
						player.gfx0(825);
						player.doingActionEvent(12);
				}

				// Quest cape.
				else if (player.playerEquipment[ServerConstants.CAPE_SLOT] == 9813)
				{
						player.startAnimation(4945);
						player.gfx0(816);
						player.doingActionEvent(14);
				}

				// Dungeoneering cape.
				else if (player.playerEquipment[ServerConstants.CAPE_SLOT] == 14011)
				{
						if (Misc.hasPercentageChance(50))
						{
								DungeoneeringCape.performAnimation(player);
								player.doingActionEvent(17);
						}
						else
						{
								player.startAnimation(13709);
								player.gfx0(2617);
								player.doingActionEvent(9);
						}
				}

				// Hunter cape.
				else if (player.playerEquipment[ServerConstants.CAPE_SLOT] == 9948 || player.playerEquipment[ServerConstants.CAPE_SLOT] == 9949)
				{
						player.startAnimation(5158);
						player.gfx0(907);
						player.doingActionEvent(11);
				}

				// Max cape/Completionist cape.
				else if (player.playerEquipment[ServerConstants.CAPE_SLOT] == 18674 || player.playerEquipment[ServerConstants.CAPE_SLOT] == 18675 || player.playerEquipment[ServerConstants.CAPE_SLOT] == 18676 || player.playerEquipment[ServerConstants.CAPE_SLOT] == 18677 || player.playerEquipment[ServerConstants.CAPE_SLOT] == 18678 || player.playerEquipment[ServerConstants.CAPE_SLOT] == 18679)
				{
						player.startAnimation(13709);
						player.gfx0(2617);
						player.doingActionEvent(9);
				}
				else
				{
						accessed = false;
						player.playerAssistant.sendMessage("You need a skillcape to perform this emote.");
				}
				if (accessed)
				{
						Achievements.checkCompletionSingle(player, 1042);
				}
		}

}