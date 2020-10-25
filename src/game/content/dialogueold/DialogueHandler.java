package game.content.dialogueold;

import core.ServerConstants;
import game.content.achievement.PlayerTitle;
import game.content.bank.BankPin;
import game.content.miscellaneous.PlayerGameTime;
//import game.content.quicksetup.Presets;
import game.content.skilling.Skilling;
import game.content.starter.GameMode;
import game.content.starter.NewPlayerContent;
import game.item.ItemAssistant;
import game.npc.data.NpcDefinition;
import game.player.Player;
import utility.Misc;

public class DialogueHandler
{

		private Player player;

		public DialogueHandler(Player client)

		{
				this.player = client;
		}

		/**
		 * Reset dialogue variables after using a dialogue action.
		 * @param player
		 */
		public void dialogueActionReset()
		{
				if (System.currentTimeMillis() - player.lastDialogueAction > 10)
				{
						player.setDialogueAction(0);
				}
		}

		public void dialogueWalkingReset()
		{
				player.nextDialogue = 0;
				player.setDialogueAction(0);
		}

		/**
		 * Dialogue packet.
		 * @param player
		 * 			The associated player.
		 */
		public void dialoguePacketAction()
		{
				player.lastDialogueOptionString = "";
				if (player.nextDialogue > 0)
				{
						player.getDH().sendDialogues(player.nextDialogue);
				}
				else
				{
						if (player.getDialogueAction() > 0)
						{
								return;
						}
						player.getDH().sendDialogues(0);
				}

		}

		/**
		 * Handles all talking
		 * @param dialogue The dialogue you want to use
		 * @param npcId The npc id that the chat will focus on during the chat
		 */

		public void sendDialogues(int dialogue)
		{
				player.nextDialogue = 0;
				player.setDialogueAction(dialogue);
				player.lastDialogueSelected = dialogue;
				switch (dialogue)
				{

						case 0:
								player.setDialogueAction(0);
								player.getPA().closeInterfaces();
								break;

						case 13:
								sendOption5("Cape shop #1", "Cape shop #2", "Hats, boots & gloves shop", "Robe shop", "Previous");
								break;
						case 16:
								sendOption5("Armour shop #1", "Armour shop #2", "Armour shop #3", "Weapon shop", "Previous");
								break;
						case 17:
								sendOption3("Runes and weapon shop", "Armour shop", "Previous");
								break;
						case 19:
								sendOption3("Ammo and weapon shop", "Armour shop", "Previous");
								break;

						case 22:
								sendOption3("Modern spellbook", "Ancient magicks", "Lunar spellbook");
								break;
						case 24:
								sendNpcChat2("Eww", "You're ugly, lets change your look", 9764);
								player.nextDialogue = 25;
								break;
						case 25:
								sendOption2("Yes!", "You're ugly");
								break;

						case 39:
								if (player.slayerTaskNpcType <= 0)
								{
										sendStatement("You do not have a task.");
										return;
								}
								sendStatement("You currently have " + player.slayerTaskNpcAmount + " " + NpcDefinition.getDefinitions()[player.slayerTaskNpcType].name + "s left.");
								break;

						case 84:
								sendStatement("@red@This will lead you to level 50 wilderness, are you sure?");
								player.nextDialogue = 85;
								break;
						case 85:
								sendOption2("Yes", "No");
								break;

						case 112:
								sendOption2("Recipe for disaster", "Previous");
								break;

						case 116:
								if (player.setPin)
								{
										sendOption2("Delete bank pin", "Nothing");
										return;
								}
								else
								{
										BankPin.open(player);
								}
								break;

						/* Start of White skull feature. */

						case 136:
								sendStatement("Are you sure you want to activate a white skull?");
								player.nextDialogue = 96;
								break;

						case 96:
								sendOption2("Yes", "No");
								break;

						/* End of White skull feature. */

						/* Start of Red skull feature. */

						case 137:
								sendStatement("Activate a red skull and lose all items on death. Are you sure?");
								player.nextDialogue = 138;
								break;

						case 138:
								sendOption2("Yes", "No");
								break;

						/* End of Red skull feature. */

						case 1:
								sendNpcChat1("Would you like to repair barrows pieces for 100 each?", 9850);
								player.nextDialogue = 2;
								break;

						case 2:
								sendOption2("Yes.", "No.");
								break;

						case 3:
								sendNpcChat1("You do not have enough coins.", 9850);

								break;

						case 4:
								sendNpcChat1("Remember that dungeoneering items have no item bonuses.", 9850);
								player.nextDialogue = 92;
								break;

						case 5:
								sendNpcChat1("Remove your equipment please.", 9850);

								break;

						case 6:
								sendOption3("Permanent mute", "IP-mute", "Timed mute");
								break;

						case 8:
								sendOption5("Permanent ban", "Timed ban", "Un-ban", "Un-mute", "Un-IP-mute");
								break;

						case 9:
								sendOption2("Copy worn equipment.", "Copy inventory.");
								break;

						case 10:
								sendNpcChat1("'Ello, and what are you after then?", 9784);
								player.nextDialogue = 11;
								break;

						case 11:
								sendOption5("I need another assignment", "May you spare me an Enchanted gem?", "Upgrade to Slayer helmet (charged)", "I need a boss assignment!", "Untradeable shop");
								break;

						case 12:
								sendNpcChat2("Excellent, you're doing great. Your new task is to kill", player.slayerTaskNpcAmount + " " + NpcDefinition.getDefinitions()[player.slayerTaskNpcType].name + ".", 9784);

								break;

						case 14:
								sendNpcChat2("Would you like to upgrade to", "Slayer helmet (charged) for 10k blood money?", 9784);
								player.nextDialogue = 21;
								break;

						case 20:
								sendNpcChat("You already have a Slayer helmet (charged).", 9784);
								break;

						case 15:
								sendNpcChat("You need to buy a Slayer helmet from my shop first.", 9784);
								break;

						case 21:
								sendOption2("Yes", "No");
								break;

						case 184:
								sendNpcChat3("You already have an assignment to kill " + player.slayerTaskNpcAmount + " " + NpcDefinition.getDefinitions()[player.slayerTaskNpcType].name + ".", "Would you like to reset this task in return", "for 1k blood money?", 9784);
								player.nextDialogue = 203;
								break;

						case 104:
								sendNpcChat1("Young one, which altar do you seek?", 9850);
								player.nextDialogue = 105;
								break;

						case 105:
								sendOption4("Air altar", "Fire altar", "Cosmic altar", "Next");
								break;

						case 106:
								sendOption4("Nature altar", "Law altar", "Death altar", "Previous");
								break;

						case 140:
								sendNpcChat1("Hello there, need anything?", 9850);
								player.nextDialogue = 141;
								break;

						case 141:
								sendOption2("Ask about Farming rewards", "Farming supplies");
								break;

						case 142:
								sendOption4("Entrana (Skilling)", "Agility", "Mining & Smithing", "Previous");
								break;

						case 143:
								sendOption3("Gnome course", "Barbarian course", "Previous");
								break;

						case 144:
								sendNpcChat1("You do not have 2m coins", 9850);
								break;

						case 159:
								sendNpcChat2("Sorry, only players with Train Combat mode", "have access to player titles.", 9850);
								break;

						case 160:
								sendNpcChat2("Hello there young one.", "Where would you like to be teleported to?", 9850);
								player.nextDialogue = 161;
								break;

						case 161:
								sendOption5("Boss teleports", "Skilling teleports", "Monster teleports", "Minigame teleports", "City teleports");
								break;

						case 163:
								sendStatement("You found a hidden tunnel! Do you want to enter it?");
								player.nextDialogue = 164;
								break;

						case 164:
								sendOption2("Yes I'm fearless!", "No.");
								player.setDialogueAction(164);
								break;

						case 103:
								sendOption5("Dagannoth Kings", "Corporeal Beast", "TzTok-Jad", "Venenatis (28 Wilderness)", "Next");
								break;

						case 101:
								sendOption2("Callisto (44 Wilderness)", "Next");
								break;

						case 69:
								sendOption5("K'ril Tsutsaroth", "Commander Zilyana", "Kree'arra", "General Graardor", "Previous");
								break;

						case 82:
								sendOption5("Ice Strykewyrm (44 Wilderness)", "King Black Dragon (44 Wilderness)", "Chaos Elemental (50 Wilderness)", "Mage arena (55 Wilderness)", "Next");
								break;

						case 174:
								int random = Misc.random(4);
								if (random == 0)
								{
										sendNpcChat1("I am busy right now.", 9850);
								}
								else if (random == 1)
								{
										sendNpcChat1("Come tomorrow.", 9850);
								}
								else if (random == 2)
								{
										sendNpcChat1("Sorry, i am in the middle of something.", 9850);
								}
								else if (random == 3)
								{
										sendNpcChat1("Please visit another time.", 9850);
								}
								else if (random == 4)
								{
										sendNpcChat1("I need to concentrate.", 9850);
								}
								break;

						case 110:
								sendOption5("Rock crabs", "Slayer tower", "Brimhaven dungeon", "Bandit camp", "Next");
								break;

						case 171:
								sendNpcChat2("You have defeated TzTok-Jad. I am most impressed!", "Please accept this gift.", 9850);
								break;

						case 172:
								sendNpcChat2("You're on your own now " + player.getPlayerName() + ", prepare to fight for", "your life!", 9850);
								break;

						case 173:
								sendNpcChat("You may only have one Fire cape at a time.", 9850);
								break;

						case 176:
								sendOption5("Taverly dungeon", "Edgeville dungeon", "Fremennik dungeon", "Tzhaar", "Previous");
								break;

						case 180:
								sendOption5("Small ninja", "Large ninja", "Monkey guard", "Next", "Nothing.");
								break;

						case 181:
								sendOption5("Bearded monkey guard", "Blue face monkey guard", "Small zombie", "Next", "Previous");
								break;

						case 182:
								sendOption3("Large zombie", "Karamja monkey", "Previous");
								break;

						case 183:
								sendOption2("That's okay; I want a party hat!", "Stop, i want to keep my cracker");
								break;

						case 187:
								sendOption2("Yes.", "No.");
								break;

						case 188:
								sendOption2("Claim Member prize!", "Not now.");
								break;

						case 189:
								sendOption2("I want to be a spawner now.", "Not now.");
								break;

						case 190:
								sendNpcChat1("You do not have any broken chaotic equipment.", 9850);
								break;

						case 191:
								sendNpcChat1("Get back to training this instant!", 9850);
								player.nextDialogue = 192;
								break;

						case 192:
								sendOption2("Let me check your store please", "Yes sir!");
								break;

						case 195:
								sendStatement("You are not maxed out in all skills.");
								break;

						case 196:
								sendStatement("Only 'Train Combat' accounts are eligible.");
								break;

						case 197:
								sendStatement("Not enough coins.");
								break;

						case 198:
								sendStatement("You need 2079 total level to claim the Max cape.");
								break;

						case 199:
								sendOption2("Purchase Max cape for 10k blood money.", "No, nevermind.");
								break;

						case 203:
								sendOption2("Yes.", "No, nevermind.");
								break;

						case 204:
								sendNpcChat1("You do not have 20 slayer points.", 9784);
								break;

						case 206:
								sendOption2("Claim Legendary Member prize!", "Not now.");
								break;

						case 207:
								sendOption2("Claim Dicer prize!", "Not now.");
								break;

						case 208:
								sendOption4("How many credits do i have?", "Open credit shop 1", "Open credit shop 2", "Close");
								break;

						case 209:
								sendOption5("Blood money shop", "Untradeables", "Buy-back untradeables", "Auto-buy untradeables on death", "Vote shop");
								break;

						case 211:
								sendNpcChat1("You do not have any artefacts.", 9850);
								break;

						case 212:
								sendNpcChat1("Thank you! Find as many as you can.", 9850);
								break;

						case 214:
								sendNpcChat1("You do not have any barrows equipment.", 9850);
								break;

						case 216:
								sendOption4("Melee shop", "Ranged shop", "Magic shop", "Equipment shop");
								break;

						case 217:
								String option = player.displayBots ? "off" : "on";
								sendOption5("Change password", "Turn profile Pvm & rare drop announcement privacy to " + (player.profilePrivacyOn ? "off" : "on"), "Pet shops", "Turn " + option + " bots", "Next");
								break;

						case 218:
								sendOption3("Animal pets", "Magical pets", "Close");
								break;

						case 219:
								if (GameMode.getGameMode(player, "DEFENDER"))
								{
										sendOption2("Pking achievement shop", "Defender shop");
								}
								else if (GameMode.getGameMode(player, "IRON MAN"))
								{
										sendOption2("Pking achievement shop", "Gloves");
								}
								else
								{
										sendOption2("Pking achievement shop", "Gloves & books");
								}
								break;

						case 221:
								sendOption5("Dark crabs (21 Wilderness)", "Revenants (27 Wilderness)", "Venenatis (28 Wilderness)", "Callisto (44 Wildeness)", "Next");
								break;

						case 220:
								sendOption5("Ice Strykewyrm (44 Wilderness)", "King Black Dragon (44 Wilderness)", "Chaos Elemental (50 Wilderness)", "Mage arena (55 Wilderness)", "Previous");
								break;

						case 222:
								sendNpcChat2("Would you like to be protected by my fellow wizards while", "at Wilderness course? 500k for 30 minutes is a fair deal.", 9850);
								player.nextDialogue = 223;
								break;

						case 223:
								player.getDH().sendOption2("Yes", "No");
								break;

						case 224:
								sendNpcChat("You do not have 500k coins.", 9850);
								break;

						case 225:
								sendOption2("Equipment shop", "Merit shop (Runecrafting)");
								break;

						case 226:
								sendOption5("Empty", "Empty", "Empty", "Turn " + (player.xpLock ? "off" : "on") + " experience lock", "Previous");
								break;

						case 228:
								sendNpcChat("Hello again. How is " + ServerConstants.SKILL_NAME[player.skillCapeMasterSkill] + " going?", player.skillCapeMasterExpression);
								player.nextDialogue = 229;
								break;

						case 229:
								sendOption2("May i buy a Skillcape of " + ServerConstants.SKILL_NAME[player.skillCapeMasterSkill] + ", please?", "May i buy the trimmed Skillcape of " + ServerConstants.SKILL_NAME[player.skillCapeMasterSkill] + ", please?");
								break;

						case 230:
								sendNpcChat4("Most certainly; it has been a pleasure to watch you", "grow. I am privileged to have been", "instrumental in your learning, but i must ask for a", "donation of 99000 coins to cover the expense of the cape.", player.skillCapeMasterExpression);
								player.nextDialogue = 231;
								break;

						case 231:
								sendOption2("I'm afraid that's too much money for me.", "Fair enough.");
								break;

						case 232:
								sendNpcChat("Good luck to you, " + player.getCapitalizedName() + ".", player.skillCapeMasterExpression);
								break;

						case 233:
								sendNpcChat("I'm sorry, you are not yet worthy of wearing the Skillcape.", player.skillCapeMasterExpression);
								break;

						case 234:
								sendNpcChat("You must have achieved 100 million experience in " + ServerConstants.SKILL_NAME[player.skillCapeMasterSkill] + ".", player.skillCapeMasterExpression);
								break;

						case 235:
								sendNpcChat4("I am honoured to have raised a student who surpassed me.", "Use this legendary Skillcape with your head held high.", "A donation of 2 million coins will be just fine", "to cover the cost of this rare cape.", player.skillCapeMasterExpression);
								player.nextDialogue = 236;
								break;

						case 236:
								sendOption2("I'm afraid that's too much money for me.", "Fair enough.");
								break;

						case 237:
								sendNpcChat1("You do not seem to have the correct amount of coins.", player.skillCapeMasterExpression);
								break;

						case 238:
								sendStatement("It looks as if you can climb the cart. Would you like to try?");
								player.nextDialogue = 239;
								break;

						case 239:
								sendOption2("Yes, i am very nimble and agile!", "No, i am happy where i am thanks!");
								break;

						case 240:
								sendNpcChat("You have not achieved 200 million experience in " + ServerConstants.SKILL_NAME[player.skillCapeMasterSkill] + ".", player.skillCapeMasterExpression);
								break;

						case 241:
								sendNpcChat("Your title has been set, do guide and help others in " + ServerConstants.SKILL_NAME[player.skillCapeMasterSkill] + ".", player.skillCapeMasterExpression);
								PlayerTitle.setTitle(player, ServerConstants.SKILL_NAME[player.skillCapeMasterSkill] + " Legend", false);
								ItemAssistant.addItemToInventoryOrDrop(player, Skilling.SkillCapeMasterData.values()[player.skillCapeMasterSkill].getLegendCapeId(), 1);
								break;

						case 242:
								sendStatement("Your Pvm privacy has been turned " + (player.profilePrivacyOn ? "on" : "off"));
								break;

						case 245:
								sendNpcChat2("You have played for " + PlayerGameTime.getHoursOnline(player) + " hours, since", "arriving " + PlayerGameTime.getDaysSinceAccountCreated(player) + " days ago on the " + player.accountDateCreated + ".", 9850);
								break;

						case 246:
								sendNpcChat("You currently have " + player.getDawntainPoints() + " slayer points.", 9784);
								break;

						case 248:
								sendOption2("Upgrade to the next Donator rank", "Keep the Donator tokens");
								break;

						case 249:
								sendOption2("Gain 740k prayer xp from the lamp (70 prayer)", "Keep the Prayer lamp.");
								break;

						case 250:
								sendNpcChat("Hello, you'll need at least 100 tokens to enter.", 9850);
								break;

						case 251:
								sendNpcChat("You are allowed to enter.", 9850);
								break;

						case 252:
								sendOption3("Yes, change game mode to " + Misc.capitalize(player.selectedGameMode), "No, i'll keep my " + Misc.capitalize(player.gameMode) + " game mode", "Back");
								break;

						case 253:
								sendNpcChat("Would you like to have your hides tanned for 100gp each?", 9850);
								player.nextDialogue = 254;
								break;

						case 254:
								sendOption2("Yes", "No");
								break;

						case 256:
								sendOption2("Donator Cosmetics shop #1", "Donator Cosmetics shop #2");
								break;
						case 257:
								sendOption4("Rare shop", "Donator shop", "Legendary Donator Throne", "Open www.shatterscape.com/donate");
								break;

						case 258:
								sendOption4("Iron Man skilling store", "Iron Man equipment store", "Hats & robe sets shop", "Cape shop");
								break;
						case 259:
								sendOption3("Red throne", "White throne", "Orange throne");
								break;

						case 260:
								sendOption3("Claim achievement rewards", "Community event shop", "Open www.shatterscape.com/event");
								break;

						case 261:
								sendOption4("Supplies shop", "Skilling shop", "Hats & robe sets shop", "Cape shop");
								//sendOption3("Supplies shop", "Hats & robe sets shop", "Cape shop");
								break;

//						case 262:
//								sendOption3("Equip " + Presets.getCurrentPresetName(player), "Update " + Presets.getCurrentPresetName(player), "Rename " + Presets.getCurrentPresetName(player));
//								break;

//						case 263:
//								sendOption2("Update the " + Presets.getCurrentPresetName(player) + " preset", "Don't you dare update!");
//								break;

						case 264:
								sendOption3("Toggle boss kill counts to: @blu@" + (player.bossKillCountMessage ? "off" : "on"), "Change loot value notification", "Toggle profile Pvm and rare drop announcement privacy to: @blu@" + (player.profilePrivacyOn ? "off" : "on"));
								break;

						case 265:
								sendOption4("Kills left?", "Obtain a Pvp task", "Claim reward", "What are the rewards for completing a Pvp task?");
								break;

						case 266:
								sendStatement("@red@Picking it up will skull you, teleblock and unable to protect item!");
								player.nextDialogue = 267;
								break;

						case 267:
								sendOption2("Pick up the blood key quick!", "Too risky..");
								break;

						case 268:
								sendOption2("Tournament supplies", "Achievement items");
								break;

						case 269:
								sendOption2("Achievements shop", "Skill cape shop");
								break;

						case 270:
								sendNpcChat2("Welcome to ShatterScape, " + player.getPlayerName() + "!", "You have received 10m, which has been placed in your bank.", 9850);
								player.nextDialogue = 271;
								break;

						case 271:
								sendNpcChat2("Would you like me to show you around?", "I recommend this if you're a new player.", 9850);
								player.nextDialogue = 272;
								break;

						case 272:
								sendOption2("Sure thing!", "No thanks, I know my way around ShatterScape.");
								break;

						case 273:
								sendNpcChat3("This area contains some important features, such as", "altars, the Blood key chest, a Highscores statue and even", "an object to claim a max cape from.", 9850);
								player.nextDialogue = 274;
								player.getPA().movePlayer(3096, 3510, 0);
								break;

						case 274:
								sendNpcChat3("Here you'll find almost anything that you need.", "Spend your Blood money at the Void knight Npc", "which sells a variety of useful items.", 9850);
								player.nextDialogue = 276;
								player.getPA().movePlayer(3083, 3510, 0);
								break;

						case 276:
								sendNpcChat2("By using the Teleport NPC or any spell in a spellbook, ", "you can access all major teleports for ShatterScape", 9850);
								player.nextDialogue = 277;
								player.getPA().movePlayer(3094, 3495, 0);
								break;

						case 277:
								sendNpcChat2("Although ShatterScape primarily focusses on PvP activities, ", "Skillers and PvMers are also welcome!", 9850);
								player.nextDialogue = 278;
								player.getPA().movePlayer(3419, 3572, 2);
								break;

						case 278:
								sendNpcChat2("Slaying Bosses and revenants in the wilderness", "is the best money making method!", 9850);
								player.nextDialogue = 279;
								player.getPA().movePlayer(2978, 3735, 0);
								break;

						case 279:
								sendNpcChat2("Skilling resources such as logs, fish and ore can be ", "sold to the Blood money shop.", 9850);
								player.nextDialogue = 281;
								player.getPA().movePlayer(2857, 3336, 0);
								break;

						case 281:
								sendNpcChat2("Check out the settings tab for many different client options.", "Customise your client to how you like it!", 9850);
								player.nextDialogue = 283;
								player.getPA().movePlayer(3092, 3515, 0);
								player.getPA().sendMessage(":packet:facecompass");
								break;
						case 283:
								sendNpcChat3("Have fun " + player.getPlayerName() + "!", "Remember to invite your friends or create", "your own clan to dominate and survive in the Wilderness.", 9850);
								player.nextDialogue = 284;
								break;
						case 284:
								player.setTutorialComplete(true);
								NewPlayerContent.endTutorial(player);
								break;

						case 285:
								sendOption2("Pick the flowers.", "Leave the flowers.");
								break;

						case 286:
								sendOption2("@red@Delete my inventory", "No!");
								break;
				}

				player.lastDialogueAction = System.currentTimeMillis();
		}

		public void sendStartInfo(String title, String text, String text1, String text2, String text3)
		{
				player.getPA().sendFrame126(title, 6180);
				player.getPA().sendFrame126(text, 6181);
				player.getPA().sendFrame126(text1, 6182);
				player.getPA().sendFrame126(text2, 6183);
				player.getPA().sendFrame126(text3, 6184);
				player.getPA().sendFrame164(6179);
		}

		@SuppressWarnings("unused")
		private void sendOption(String s, String s1)
		{
				player.getPA().sendFrame126("Select an Option", 2470);
				player.getPA().sendFrame126(s, 2471);
				player.getPA().sendFrame126(s1, 2472);
				player.getPA().sendFrame126("Click here to continue", 2473);
				player.getPA().sendFrame164(13758);
		}

		public void sendOption2(String s, String s1)
		{
				if (player.hasDialogueOptionOpened && player.lastDialogueOptionString.equals(s))
				{
						player.getPA().closeInterfaces();
						return;
				}
				player.lastDialogueOptionString = s;
				player.getPA().sendFrame126("Select an Option", 2460);
				player.getPA().sendFrame126(s, 2461);
				player.getPA().sendFrame126(s1, 2462);
				player.getPA().sendFrame164(2459);
				player.hasDialogueOptionOpened = true;
		}

		private void sendOption3(String s, String s1, String s2)
		{
				if (player.getDialogueAction() != 262)
				{
						if (player.hasDialogueOptionOpened && player.lastDialogueOptionString.equals(s))
						{
								player.getPA().closeInterfaces();
								return;
						}
				}
				player.lastDialogueOptionString = s;
				player.getPA().sendFrame126("Select an Option", 2470);
				player.getPA().sendFrame126(s, 2471);
				player.getPA().sendFrame126(s1, 2472);
				player.getPA().sendFrame126(s2, 2473);
				player.getPA().sendFrame164(2469);
				player.hasDialogueOptionOpened = true;
		}

		public void sendOption4(String s, String s1, String s2, String s3)
		{
				if (player.hasDialogueOptionOpened && player.lastDialogueOptionString.equals(s))
				{
						player.getPA().closeInterfaces();
						return;
				}
				player.lastDialogueOptionString = s;
				player.getPA().sendFrame126("Select an Option", 2481);
				player.getPA().sendFrame126(s, 2482);
				player.getPA().sendFrame126(s1, 2483);
				player.getPA().sendFrame126(s2, 2484);
				player.getPA().sendFrame126(s3, 2485);
				player.getPA().sendFrame164(2480);
				player.hasDialogueOptionOpened = true;
		}

		public void sendOption5(String s, String s1, String s2, String s3, String s4)
		{
				if (player.hasDialogueOptionOpened && player.lastDialogueOptionString.equals(s))
				{
						player.getPA().closeInterfaces();
						return;
				}
				player.lastDialogueOptionString = s;
				player.getPA().sendFrame126("Select an Option", 2493);
				player.getPA().sendFrame126(s, 2494);
				player.getPA().sendFrame126(s1, 2495);
				player.getPA().sendFrame126(s2, 2496);
				player.getPA().sendFrame126(s3, 2497);
				player.getPA().sendFrame126(s4, 2498);
				player.getPA().sendFrame164(2492);
				player.hasDialogueOptionOpened = true;
		}

		public void sendStatement(String s)
		{ // 1 line click here to continue chat box interface
				player.getPA().sendFrame126(s, 357);
				player.getPA().sendFrame126("Click here to continue", 358);
				player.getPA().sendFrame164(356);
		}

		private void sendNpcChat1(String s, int expression)
		{
				player.getPA().sendFrame200(4883, expression);
				player.getPA().sendFrame126(NpcDefinition.getDefinitions()[player.getNpcType()].name, 4884);
				player.getPA().sendFrame126(s, 4885);
				player.getPA().sendFrame75(player.getNpcType(), 4883);
				player.getPA().sendFrame164(4882);
		}

		public static void sendOption(Player c, String s, String s1)
		{
				c.getPA().sendFrame126("Select an Option", 2460);
				c.getPA().sendFrame126(s, 2461);
				c.getPA().sendFrame126(s1, 2462);
				c.getPA().sendFrame164(2459);
		}

		public static void sendOption(Player c, String s, String s1, String s2)
		{
				c.getPA().sendFrame126("Select an Option", 2470);
				c.getPA().sendFrame126(s, 2471);
				c.getPA().sendFrame126(s1, 2472);
				c.getPA().sendFrame126(s2, 2473);
				c.getPA().sendFrame164(2469);
		}

		public static void sendOption(Player c, String s, String s1, String s2, String s3)
		{
				c.getPA().sendFrame126("Select an Option", 2481);
				c.getPA().sendFrame126(s, 2482);
				c.getPA().sendFrame126(s1, 2483);
				c.getPA().sendFrame126(s2, 2484);
				c.getPA().sendFrame126(s3, 2485);
				c.getPA().sendFrame164(2480);
		}

		public static void sendOption(Player c, String s, String s1, String s2, String s3, String s4)
		{
				c.getPA().sendFrame126("Select an Option", 2493);
				c.getPA().sendFrame126(s, 2494);
				c.getPA().sendFrame126(s1, 2495);
				c.getPA().sendFrame126(s2, 2496);
				c.getPA().sendFrame126(s3, 2497);
				c.getPA().sendFrame126(s4, 2498);
				c.getPA().sendFrame164(2492);
		}

		public void sendNpcChat(String s, int expression)
		{
				player.getPA().sendFrame200(4883, expression);
				player.getPA().sendFrame126(NpcDefinition.getDefinitions()[player.getNpcType()].name, 4884);
				player.getPA().sendFrame126(s, 4885);
				player.getPA().sendFrame75(player.getNpcType(), 4883);
				player.getPA().sendFrame164(4882);
		}

		public void sendNpcChat2(String s1, String name, int expression)
		{
				player.getPA().sendFrame200(4888, expression);
				player.getPA().sendFrame126(NpcDefinition.getDefinitions()[player.getNpcType()].name, 4889);
				player.getPA().sendFrame126(s1, 4890);
				player.getPA().sendFrame126(name, 4891);
				player.getPA().sendFrame75(player.getNpcType(), 4888);
				player.getPA().sendFrame164(4887);
		}

		public void sendNpcChat3(String s1, String s2, String s3, int expression)
		{
				player.getPA().sendFrame126(NpcDefinition.getDefinitions()[player.getNpcType()].name, 4895);
				player.getPA().sendFrame200(4894, expression);
				player.getPA().sendFrame126(s1, 4896);
				player.getPA().sendFrame126(s2, 4897);
				player.getPA().sendFrame126(s3, 4898);
				player.getPA().sendFrame75(player.getNpcType(), 4894);
				player.getPA().sendFrame164(4893);
		}

		public void sendNpcChat4(String s1, String s2, String s3, String s4, int expression)
		{
				player.getPA().sendFrame200(4901, expression);
				player.getPA().sendFrame126(NpcDefinition.getDefinitions()[player.getNpcType()].name, 4902);
				player.getPA().sendFrame126(s1, 4903);
				player.getPA().sendFrame126(s2, 4904);
				player.getPA().sendFrame126(s3, 4905);
				player.getPA().sendFrame126(s4, 4906);
				player.getPA().sendFrame75(player.getNpcType(), 4901);
				player.getPA().sendFrame164(4900);
		}

		/**
		 * Player talking back
		 **/
		@SuppressWarnings("unused")
		private void sendPlayerChat1(String s)
		{
				player.getPA().sendFrame200(969, 9850); // 9850 is the head animation, use 718 data http://www.rune-server.org/runescape-development/rs-503-client-server/configuration/536545-718-npc-dialogue-expressions.html
				player.getPA().sendFrame126(player.getPlayerName(), 970);
				player.getPA().sendFrame126(s, 971);
				player.getPA().sendFrame185(969);
				player.getPA().sendFrame164(968);
		}

		@SuppressWarnings("unused")
		private void sendPlayerChat2(String s, String s1)
		{
				player.getPA().sendFrame200(974, 9850);
				player.getPA().sendFrame126(player.getPlayerName(), 975);
				player.getPA().sendFrame126(s, 976);
				player.getPA().sendFrame126(s1, 977);
				player.getPA().sendFrame185(974);
				player.getPA().sendFrame164(973);
		}

		@SuppressWarnings("unused")
		private void sendPlayerChat3(String s, String s1, String s2)
		{
				player.getPA().sendFrame200(980, 9850);
				player.getPA().sendFrame126(player.getPlayerName(), 981);
				player.getPA().sendFrame126(s, 982);
				player.getPA().sendFrame126(s1, 983);
				player.getPA().sendFrame126(s2, 984);
				player.getPA().sendFrame185(980);
				player.getPA().sendFrame164(979);
		}

		@SuppressWarnings("unused")
		private void sendPlayerChat4(String s, String s1, String s2, String s3)
		{
				player.getPA().sendFrame200(987, 9850);
				player.getPA().sendFrame126(player.getPlayerName(), 988);
				player.getPA().sendFrame126(s, 989);
				player.getPA().sendFrame126(s1, 990);
				player.getPA().sendFrame126(s2, 991);
				player.getPA().sendFrame126(s3, 992);
				player.getPA().sendFrame185(987);
				player.getPA().sendFrame164(986);
		}

		public void sendItemChat1(String header, String one, int item, int zoom, int offset1, int offset2)
		{
				player.getPA().sendMessage(":packet:senditemchat 307 " + offset1 + " " + offset2);
				player.getPA().sendFrame126(one, 308);
				player.getPA().sendFrame126(header, 4885);
				player.getPA().sendFrame246(307, zoom, item);
				player.getPA().sendFrame164(306);
		}

		public void sendItemChat2(String header, String one, String two, int item, int zoom, int offset1, int offset2)
		{
				player.getPA().sendMessage(":packet:senditemchat 311 " + offset1 + " " + offset2);
				player.getPA().sendFrame126(two, 312);
				player.getPA().sendFrame126(one, 313);
				player.getPA().sendFrame126(header, 4885);
				player.getPA().sendFrame246(311, zoom, item);
				player.getPA().sendFrame164(310);
		}

		public void sendItemChat3(String header, String one, String two, String three, int item, int zoom, int offset1, int offset2)
		{
				player.getPA().sendMessage(":packet:senditemchat 4894 " + offset1 + " " + offset2);
				player.getPA().sendFrame246(4894, zoom, item);
				player.getPA().sendFrame126(header, 4895);
				player.getPA().sendFrame126(one, 4896);
				player.getPA().sendFrame126(two, 4897);
				player.getPA().sendFrame126(three, 4898);
				player.getPA().sendFrame164(4893);
		}

		public void sendItemChat4(String header, String one, String two, String three, String four, int item, int zoom, int offset1, int offset2)
		{
				player.getPA().sendMessage(":packet:senditemchat 4901 " + offset1 + " " + offset2);
				player.getPA().sendFrame246(4901, zoom, item);
				player.getPA().sendFrame126(header, 4902);
				player.getPA().sendFrame126(one, 4903);
				player.getPA().sendFrame126(two, 4904);
				player.getPA().sendFrame126(three, 4905);
				player.getPA().sendFrame126(four, 4906);
				player.getPA().sendFrame164(4900);
		}
}