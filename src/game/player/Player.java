package game.player;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import org.apache.mina.common.IoSession;

import core.ServerConstants;
import game.bot.BotContent;
import game.content.combat.Combat;
import game.content.combat.Death;
import game.content.combat.vsplayer.Effects;
import game.content.dialogueold.DialogueHandler;
import game.content.donator.DonatorTokenUse;
import game.content.miscellaneous.TradeAndDuel;
import game.content.prayer.PrayerBook;
import game.content.prayer.QuickPrayers;
import game.content.shop.ShopAssistant;
import game.content.skilling.HitPointsRegeneration;
import game.content.skilling.Skilling;
import game.item.Item;
import game.npc.Npc;
import game.npc.NpcHandler;
import game.object.custom.Object;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;
import game.player.movement.Movement;
import network.packet.Packet;
import network.packet.PacketHandler;
import network.packet.StaticPacketBuilder;
import network.packet.Stream;
import utility.ISAACRandomGen;
import utility.Misc;

/**
 * Everything declared in this class will belong to the individual player.
 */
public abstract class Player
{
		public int soundToSend;

		/**
		 * Skill ids to update the skilling tab ids of, this is queued till after send item switch update is sent. So the item update packet is reaching the client first.
		 */
		public ArrayList<Integer> skillTabMainToUpdate = new ArrayList<Integer>();

		public int soundDelayToSend;

		public boolean ignoreInCombat;

		/**
		 * Log the total amount of attacks used in a stake.
		 */
		public int stakeAttacks;

		/**
		 * Log the total amount of special attacks used in a stake.
		 */
		public int stakeSpecialAttacks;

		/**
		 * Prevent flush spam.
		 */
		public boolean canFlush;

		/**
		 * The time the player will be un rag banned.
		 */
		public long timeRagUnbanned;

		/**
		 * Save the time the player used the ::risk command.
		 */
		public long timeUsedRiskCommand;

		/**
		 * To prevent players from using presets while skilling.
		 */
		public long timeSkilled;

		/**
		 * True to auto-buy back untradeables from shop.
		 */
		public boolean autoBuyBack;

		/**
		 * Time followed a person into wilderness and was warned.
		 */
		public long timeWildernessFollowWarned;

		/**
		 * Last time random event activated.
		 */
		public long lastRandomEvent;

		/**
		 * True if the player cannot issue a walking packet. So the player does not go through a door then walk which cancels it.
		 */
		public boolean cannotIssueMovement;

		/**
		 * This string is updated on log in after reading the character file and before grabbing the last mac address from the client.
		 */
		public String lastMacAddress = "";

		/**
		 * This string is updated on log in after reading the character file and before grabbing the last uid address from the client.
		 */
		public String lastUidAddress = "";

		/**
		 * Amount of ticks player is speared for.
		 */
		public int dragonSpearTicksLeft;

		/**
		 * True if dragon spear event is active.
		 */
		public boolean dragonSpearEvent;

		/**
		 * Store on each line:
		 * Damage: 5
		 * Smite: 5
		 * Freeze: 15000 (as in 15 seconds extra freeze)
		 * Dragon scimitar special (as in apply dragon scimitar successful special attack)
		 */
		public ArrayList<String> dragonSpearEffectStack = new ArrayList<String>();

		public int flower;

		public int flowerX;

		public int flowerY;

		public int flowerHeight;

		public String lastDueledWithName = "";

		public ArrayList<String> sendGroundItemPacket = new ArrayList<String>();

		public ArrayList<String> sendGroundItemPacketRemove = new ArrayList<String>();

		public int potionCombineLoops;

		/**
		 * True if player is ip-muted, updates on log-in
		 */
		public boolean ipMuted;

		/**
		 * Double death duel arena moving fix.
		 */
		public long timeMovedFromDoubleDuelDeath;

		/**
		 * Used to block the player from entering the wilderness if they recently died.
		 * It takes an average of 45 seconds to gear up in tribrid 4 ways.
		 */
		public long timeDiedInWilderness;

		/**
		 * True if rigour is unlocked.
		 */
		public boolean rigourUnlocked;

		/**
		 * True if augury is unlocked.
		 */
		public boolean auguryUnlocked;

		/**
		 * Time used god wars dungeon altar.
		 */
		public long timeUsedGodWarsDungeonAltar;

		/**
		 * True if item on npc event is being used.
		 */
		public boolean itemOnNpcEvent;

		/**
		 * Save list of resources harvested, item id and amount, used to sell back to shop.
		 */
		public ArrayList<String> resourcesHarvested = new ArrayList<String>();

		/**
		 * True if player is using old school autocast through combat tab.
		 */
		public boolean usingOldAutocast;

		/**
		 * Brew message sent string.
		 */
		public String brewMessageSent = "";

		/**
		 * To prevent brew message spam.
		 */
		public long timeBrewMessageSent;

		/**
		 * Used with timeTriedToAttackPlayer.
		 */
		public String playerTriedToAttack = "";

		/**
		 * Used so both players can attack each other if they do not meet edge and wests requirements.
		 */
		public long timeTriedToAttackPlayer;

		/**
		 * Used to disconnect a player who has dced in combat and then died, so when they log in, they do not log in where they died.
		 * This is to give the player time to respawn then disconnect.
		 */
		public long timeDied;

		/**
		 * Pure tribrid tournament wins.
		 */
		public int hybridTournamentsWon;

		/**
		 * Zerk hybrid tournament wins.
		 */
		public int tribridTournamentsWon;

		/**
		 * Main hybrid tournament wins.
		 */
		public int meleeTournamentsWon;

		/**
		 * Used to know if player will be moving, used for when the player is not moving, then interact with object.
		 * To prevent players from interacting with the object when they are on the other side of the wall.
		 * Player can still interact with object if there is no open path to it.
		 */
		public boolean tempMoving;

		/**
		 * Belongs to tempMoving.
		 */
		public int tempDir1;

		/**
		 * Belongs to tempMoving.
		 */
		public int tempDir2;

		/**
		 * Belongs to tempMoving.
		 */
		public boolean tempRunning;

		/**
		 * Tournament target id.
		 */
		public int tournamentTarget = -1;

		/**
		 * Used to update the bank 
		 */
		public boolean bankUpdated;

		/**
		 * Store player id, splash boolean, damage when casting a magic multi spell in pvp.
		 */
		public ArrayList<String> magicMultiSpell = new ArrayList<String>();

		/**
		 * Force movement update mask boolean, used for cut scenes only because multiplayer does not work for it.
		 */
		public boolean forceMovementUpdate;

		/**
		 * True to not open shop interface, used when updating shop interface.
		 */
		public boolean doNotOpenShopInterface;

		/**
		 * Shop search string.
		 */
		public String shopSearchString = "";

		/**
		 * Total blood keys taken and made out alive.
		 */
		public int bloodKeysCollected;

		/**
		 * Kills in multi wilderness.
		 */
		public int killsInMulti;

		/**
		 * True if the player can claim the pvp task reward.
		 */
		public boolean canClaimPvpTaskReward;

		/**
		 * How many kills in total for the task, used for receiving blood money reward.
		 */
		public int pvpTaskSize;

		/**
		 * Total amount of pvp tasks completed.
		 */
		public int pvpTasksCompleted;

		/**
		 * Store the amount of kills to do for each type.
		 * 0 is pure, 1 is zerker, 2 is ranged tank, 3 is maxed.
		 */
		public int[] pvpTask = new int[4];

		/**
		 * Store time the player left the wilderness while having a target.
		 */
		public long timeExitedWildFromTarget;

		/**
		 * Used to avoid spamming the player with how many seconds they have left to return to wilderness to not lose target.
		 */
		public long targetLeftTime;

		/**
		 * To prevent the target activity gaining to be spammed.
		 */
		public long targetActivityTime;

		/**
		 * At a certain value, the player is eligible for a target.
		 */
		public int targetActivityPoints;

		/**
		 * The player id of my target.
		 */
		public int targetPlayerId = -1;

		/**
		 * True if Toxic blowpipe special attack is active.
		 */
		public boolean blowpipeSpecialAttack;

		/**
		 * The Toxic blowpipe dart type loaded into it.
		 */
		public int blowpipeDartItemId;

		/**
		 * The amount of darts loaded into the Toxic blowpipe.
		 */
		public int blowpipeDartItemAmount;

		/**
		 * Rune pouch item id data.
		 */
		public int[] runePouchItemId = new int[3];

		/**
		 * Rune pouch item amount data.
		 */
		public int[] runePouchItemAmount = new int[3];

		/**
		 * Food dropped while in the Wilderness.
		 */
		public ArrayList<String> droppedFood = new ArrayList<String>();

		/**
		 * Boss kill counts messages.
		 */
		public boolean bossKillCountMessage = true;

		/**
		 * Loot worth to notify to the player.
		 */
		public int valuableLoot = 1;

		/**
		 * Prevent valuable loot spam.
		 */
		public long timeValuableLootNotified;

		/**
		 * Save time of when the valuable loot "more loot" was sent.
		 */
		public long timeValuableLootNotifiedAgain;

		/**
		 * Used to record what time the player exited the player while in-combat, for the City timer at Edgeville.
		 */
		public long timeExitedWilderness;

		/**
		 * Save the time the victim exited wilderness while in combat with the player.
		 */
		public long timeVictimExitedWilderness;

		/**
		 * True if city timer can be triggered.
		 */
		public boolean canTriggerCityTimer;

		/**
		 * Save player id i can attack in safe area.
		 */
		public int playerIdCanAttackInSafe;

		/**
		 * Save player id attacking me in safe area.
		 */
		public int playerIdAttackingMeInSafe;

		/**
		 * Current interface displayed.
		 */
		public int interfaceDisplayed;

		/**
		 * Preset index set.
		 */
		public int presetIndex;

		/**
		 * Store preset data in here.
		 */
		public ArrayList<String> preset1 = new ArrayList<String>();

		/**
		 * Store preset data in here.
		 */
		public ArrayList<String> preset2 = new ArrayList<String>();

		/**
		 * Store preset data in here.
		 */
		public ArrayList<String> preset3 = new ArrayList<String>();

		/**
		 * Store preset data in here.
		 */
		public ArrayList<String> preset4 = new ArrayList<String>();

		/**
		 * Store preset data in here.
		 */
		public ArrayList<String> preset5 = new ArrayList<String>();

		/**
		 * Store preset data in here.
		 */
		public ArrayList<String> preset6 = new ArrayList<String>();

		/**
		 * Store preset data in here.
		 */
		public ArrayList<String> preset7 = new ArrayList<String>();

		/**
		 * Store preset data in here.
		 */
		public ArrayList<String> preset8 = new ArrayList<String>();

		/**
		 * Store preset data in here.
		 */
		public ArrayList<String> preset9 = new ArrayList<String>();

		/**
		 * True if the player has the required gear to cast an ice barrage.
		 */
		public boolean hasMagicEquipment;

		/**
		 * True if the player has the required gear to use ranged.
		 */
		public boolean hasRangedEquipment;

		/**
		 * The amount of risk the player is currently risking, force protect item on.
		 */
		public int wildernessRiskAmount;

		/**
		 * Current carried wealth total.
		 */
		public int carriedWealth;

		/**
		 * Current risked wealth total.
		 */
		public int riskedWealth;

		/**
		 * True if the first log-in teleport update has been completed
		 */
		public boolean logInTeleportCompleted;

		/**
		 * Amount of times the player has tabbed.
		 */
		public int myTabs;

		/**
		 * Amount of times the enemy has tabbed from the player in singles.
		 */
		public int enemyTabs;

		/**
		 * Store the time of when the player got a kill while being under the Edge and Wests protection rule.
		 */
		public long killedPlayerImmuneTime;

		/**
		 * True if the player has excess brews.
		 */
		public boolean excessBrews;

		public int brewCount;

		/**
		 * If the player is using 2 styles, then he is a Hybrid, if 3 styles, then is a Tribrid.
		 */
		public int combatStylesUsed;

		/**
		 * Time scanned for Tribrid gear.
		 */
		public long timeScannedForTribrid;

		/**
		 * List of Pvp blacklisted players.
		 */
		public ArrayList<String> pvpBlacklist = new ArrayList<String>();

		/**
		 * The time the player was warned about fighting another player.
		 */
		public long timeWarned;

		/**
		 * The name of the player and reason to be warned of.
		 */
		public String nameWarnedOf = "";

		/**
		 * True if player is in Edgeville 1-5 wilderness or West dragons wilderness.
		 */
		public boolean inEdgeOrWestArea;

		/**
		 * The time the player went from a dangerous area to the Edgeville 1-5 wilderness or West dragons wilderness.
		 */
		public long timeEnteredEdgeOrWestArea;

		/**
		 * Store time i attacked or was attacked by a player.
		 * This is reset when i get a kill, it is used specifically for me to attack a player right after i get a kill.
		 */
		public long timeInPlayerCombat;

		/**
		 * Time was in combat, this is never reset. Used to know if player was in combat.
		 */
		public long timeInCombat;

		/**
		 * If player sends more than 12 packets per tick, decline.
		 */
		public int packetsSentThisTick;

		/**
		 * True if xp lamp has been used.
		 */
		public boolean xpLampUsed;

		/**
		 * Used to alert the player of when he is eligible to vote.
		 */
		public long timeVoted;

		/**
		 * Total votes claimed to avoid spam.
		 */
		public int votesClaimed;

		/**
		 * Time used xp lamp, can only use Xp lamp every 12 hours.
		 */
		public long xpLampUsedTime;

		/**
		 * True if the player has been alerted once.
		 */
		public boolean voteAlerted;

		/**
		 * True if the player is viewing max hits on npcs instead of max hits on player.s
		 */
		public boolean viewingNpcMaxHits;

		/**
		 * Store the time of when either player a or b changed the rule.
		 */
		public long timeDuelRuleChanged;

		/**
		 * Save rollback debug.
		 */
		public long logOutSaveTime;

		/**
		 * True if enchant bolt event is being used.
		 */
		public boolean enchantEvent;

		/**
		 * Bars to smelt.
		 */
		public int barsToMake;

		/**
		 * Store action id, this is reset when doing any other action like walk etc..
		 * Store a unique id of a specific action, such as opening an interface, this is used to verify that the player is still using the interfac,e to prevent packet exploits.
		 */
		private int actionIdUsed;

		/**
		 * True if using gnome glider interface event.
		 */
		public boolean gnomeGliderEvent;

		/**
		 * True if the player i searched for in the profile system is online.
		 */
		public boolean isProfileSearchOnline;

		/**
		 * The the online player's id.
		 */
		private int profileSearchOnlinePlayerId;

		/**
		 * Save the online player's name.
		 */
		public String profileSearchOnlineName;

		/**
		 * Used to gain merit points.
		 */
		public int runeEssenceCrafted;

		/**
		 * True to not send the packet that closes the pm interface.
		 */
		public boolean doNotClosePmInterface;

		/**
		 * 2007/2009/2010 graphics client.
		 */
		public String graphicsType = "";

		/**
		 * Save total zombie damage in the minigame, this can show who played alot and who didn't.
		 */
		public int totalZombieDamage;

		/**
		 * To prevent ::claim abuse.
		 */
		public long timeClaimedEvent;

		/**
		 * To prevent ::claim abuse.
		 */
		public long timeClaimedDonation;

		/**
		 * This is a tick timer used for finding the other player when i request trade, challenege, duo zombies etc..
		 */
		public int findOtherPlayerId;

		/**
		 * Last teleport used on the teleport interface
		 */
		public String lastTeleport = "";

		/**
		 * True if the player is in the zombies minigame and has not died or logged off.
		 */
		private boolean inZombiesMinigame;

		/**
		 * Save the highest zombie wave the player has reached.
		 */
		public int highestZombieWave;

		/**
		 * Save the partner name, this is used along the highest zombie wave reached.
		 */
		public String zombiePartner = "";

		/**
		 * Zombie wave points to spend at shop.
		 */
		public int zombieWavePoints;

		/**
		 * True if player has clicked ready and is ready.
		 */
		public boolean isReadyForNextZombieWave;

		/**
		 * True to show the interface that has the option to click ready.
		 */
		public boolean waitingForWave;

		/**
		 * Player index of duo partner.
		 */
		private int zombiePartnerId = -1;

		/**
		 * Store name of the player i request a duo from.
		 */
		public String requestDuoName = "";

		/**
		 * Current community event points.
		 */
		public int communityEventPoints;

		/**
		 * Communtiy event points total.
		 */
		public int communityEventPointsTotal;

		/**
		 * Search time of last profile search to prevent abuse to try and lagg the server.
		 */
		public long timeSearchedProfile;

		/**
		 * Time clicked profile button, to prevent lagg abuse.
		 */
		public long timeClickedProfileButton;

		/**
		 * Time stats restored using Donator npc.
		 */
		public long restoreStatsTime;

		/**
		 * This si the throne id, depending on what the donator chose.
		 */
		public int throneId = 1097;

		/**
		 * Save the wilderness risk outcome of the last scan, this is used if the scan occured before it is time for the next scan.
		 */
		public boolean hasWildernessRisk;

		/**
		 * Used to scan the player for wilderness risk every 20 seconds, rather than every npc auto attack.
		 */
		public long timeScannedForWildernessRisk;

		/**
		 * Save items kept on death.
		 */
		public ArrayList<String> itemsKeptOnDeathList = new ArrayList<String>();

		/**
		 * Wilderness Risk items kept on death temporary.
		 */
		public ArrayList<String> wildernessRiskItemsKeptOnDeath = new ArrayList<String>();

		/**
		 * Save displayed hall of fame, to be used for when the player clicks on player name to view profile.
		 */
		public ArrayList<String> currentHallOfFame = new ArrayList<String>();

		/**
		 * Throne chair to remove.
		 */
		public ArrayList<Object> toRemove = new ArrayList<Object>();

		/**
		 * True if player was wearing whip on aggressive style. So if player is using aggressive (controlled) whip and switches to Msb, it doesn't go to long ranged.
		 */
		public boolean wasWearingAggressiveWhip;

		/**
		 * True if the player can use teleport interface, to prevent packet abuse.
		 */
		public boolean canUseTeleportInterface;

		/**
		 * Store coordinates of displayed teleport, to be later used to execute the teleport action.
		 */
		public ArrayList<String> currentTeleports = new ArrayList<String>();

		/**
		 * Teleport tab clicked.
		 */
		public int teleportInterfaceIndex;

		/**
		 * Enter the displayed banned list here, to be used for when a player does the actual unbanning, because another moderator can do a change then instead of me
		 * unbanning Toxic, i unban spammer. Same thing with moderatorList
		 */
		public ArrayList<String> clanChatBannedList = new ArrayList<String>();

		public ArrayList<String> clanChatModeratorList = new ArrayList<String>();

		/**
		 * Hatched used for woodcutting.
		 */
		public int hatchetUsed;

		/**
		 * Pickaxe used for mining.
		 */
		public int pickAxeUsed;

		/**
		 * Save last activity.
		 */
		public String lastActivity = "";

		/**
		 * Last time the activity was done.
		 */
		public long lastActivityTime;

		/**
		 * True if using Poison event.
		 */
		public boolean poisonEvent;

		/**
		 * Used to prevent achievementSaveName to be added twice when it comes to adding to achievements like "1089 1090".
		 */
		public boolean achievementAddedOnce;

		/**
		 * True if using Yew log.
		 */
		public boolean yewLog;

		/**
		 * True if Raw beef is chosen.
		 */
		public boolean rawBeefChosen;

		/**
		 * True if making raw beef.
		 */
		public boolean rawBeef;

		/**
		 * Flax delay.
		 */
		public long flaxDelay;

		/**
		 * Amount of smithing items to make.
		 */
		public int smithingAmountToMake;

		/**
		 * Enchant delay.
		 */
		public long enchantDelay;

		/**
		 * True to save npc text. If true, when i click on equipment button, it will save the npc on a text file and the cooridinates will be where i am standing.
		 */
		public boolean saveNpcText;

		/**
		 * True if using withdraw all but one.
		 */
		public boolean withdrawAllButOne;

		/**
		 * Name of skilling action being performed by a cycle event.
		 */
		public String skillingEvent;

		/**
		 * True if a potion has been decanted.
		 */
		public boolean potionDecanted;

		/**
		 * In seconds.
		 */
		public int barrowsPersonalRecord;

		/**
		 * Save barrows lap timer.
		 */
		public long barrowsTimer;

		/**
		 * 1 to make title appear after name.
		 */
		public int titleSwap;

		/**
		 * Save the index of the last title clicked, the index of it in the TitleDefinitions.
		 */
		public int titleIndexClicked;

		/**
		 * Titles unlocked, pking, skilling & misc.
		 */
		public int[] titleTotal = new int[3];

		/**
		 * Title ids unlocked.
		 */
		public ArrayList<String> titlesUnlocked = new ArrayList<String>();

		/**
		 * Title tab being used.
		 */
		public int titleTab = -1;

		/**
		 * True to display bots.
		 */
		public boolean displayBots = true;

		/**
		 * Last x amount withdrawn from bank.
		 */
		public int lastXAmount = 100;

		/**
		 * Boss score capped result, used only for calculating adventurer rank.
		 */
		public int bossScoreCapped;

		/**
		 * Boss score uncapped result, used for boss score highscores.
		 */
		public int bossScoreUnCapped;

		/**
		 * Death runes crafted.
		 */
		public int deathRunesCrafted;

		/**
		 * Blood money spent history. Needed for acheivement.
		 */
		public int bloodMoneySpent;

		/**
		 * The last time the player receiving the loot of a kill.
		 */
		public long lastKillTime;

		/**
		 * Untradeable items owned by the player that the player can only have 1 quantity of such as Max capes, Clue scroll, God capes, Boss pets etc.
		 * Rather than searching the whole account for the item, just look at this array instead.
		 */
		public ArrayList<String> singularUntradeableItemsOwned = new ArrayList<String>();

		/**
		 * 0 is easy, 4 is elite. Used to check if an achievement difficulty reward has been claimed.
		 */
		public boolean achievementRewardClaimed[] = new boolean[4];

		/**
		 * 0 is easy, 4 is elite. Used to check if an achievement difficulty completion has been announced.
		 */
		public boolean achievementDifficultyCompleted[] = new boolean[4];

		/**
		 * Used to know if an achievement text is clicked on.
		 */
		public int lastAchievementClicked = -1;

		/**
		 * Achievements completed, easy, medium, hard & elite.
		 */
		public int[] achievementTotal = new int[4];

		/**
		 * Achievement progress.
		 */
		public ArrayList<String> achievementProgress = new ArrayList<String>();

		/**
		 * Achievements Ids completed.
		 */
		public ArrayList<String> achievementsCompleted = new ArrayList<String>();

		/**
		 * Store bot total wealth dropped to player.
		 */
		public int victimBotWealth;

		/** 
		 * Potion decanting data
		 */
		public ArrayList<String> potions = new ArrayList<String>();

		/**
		 * True if the players messages are filtered. If true, spam messages such as You get some ores will be not sent.
		 */
		public boolean messageFiltered;

		/**
		 * Farming stage delays between cleaning patch and watering, planting.
		 */
		public long farmingStageDelay;

		/**
		 * Save x coordinate of raked spot.
		 */
		public int farmingXCoordinate;

		/**
		 * Save y coordinate of raked spot.
		 */
		public int farmingYCoordinate;

		/**
		 * True if player is farming.
		 */
		public boolean isFarming;

		/**
		 * True if using a skilling cycle event.
		 */
		public boolean farmingEvent;

		/**
		 * True if using bow string event.
		 */
		public boolean bowStringEvent;

		/**
		 * Save strung bow id.
		 */
		public int strungBowId;

		/**
		 * Amount of bots killed.
		 */
		public int playerBotKills;

		/**
		 * Amount of deaths to bots.
		 */
		public int playerBotDeaths;

		/**
		 * Highest killstreak against bots.
		 */
		public int playerBotHighestKillstreak;

		/**
		 * Current killstreak against bots.
		 */
		public int playerBotCurrentKillstreak;

		/**
		 * Looting bag slot item.
		 */
		public int[] lootingBagStorageItemId = new int[28];

		/**
		 * Looting bag slot amount.
		 */
		public int[] lootingBagStorageItemAmount = new int[28];

		/**
		 * Save the leather item to produce.
		 */
		public int leatherUsed;

		/**
		 * Save the leather item to produce type. Weather it is chaps, body or vambraces.
		 */
		public int leatherItemToProduceType;

		/**
		 * Save the dice result to prevent packet abuse.
		 */
		public String diceResultSaved = "";

		/**
		 * Save the amount of pending achievement popups.
		 */
		public int pendingAchievementPopUps;

		/**
		 * True if the player can use the game mode interface. This is used for switching game modes.
		 */
		public boolean canUseGameModeInterface;

		public String selectedGameMode = "";

		/**
		 * X coordinate of the fire used when cooking. Used for checking if the fire is alive before each cook.
		 */
		public int fireX;

		/**
		 * Y coordinate of the fire used when cooking. Used for checking if the fire is alive before each cook.
		 */
		public int fireY;

		/**
		 * Store npc kills, NAME AMOUNT.
		 */
		public ArrayList<String> npcKills = new ArrayList<String>();

		/**
		 * True if the player has the searched items displayed.
		 */
		private boolean usingBankSearch;

		/**
		 * The string being searched for.
		 */
		public String bankSearchString;

		/**
		 * The slot from where the item is being withdrawn from when using the search feature.
		 */
		public int itemInBankSlot;

		/**
		 * Save the array of searched items and the amount.
		 */
		public ArrayList<String> bankSearchedItems = new ArrayList<String>();

		/**
		 * Items to show in the untradeable shop.
		 */
		public ArrayList<String> itemsToShop = new ArrayList<String>();

		/**
		 * Items to show in the untradeable shop.
		 */
		public ArrayList<String> itemsToInventory = new ArrayList<String>();

		/**
		 * True if the player has too many connections from his address.
		 */
		public boolean hasTooManyConnections;

		/**
		 * True if the player is using the preaching event.
		 */
		public boolean usingPreachingEvent;

		/**
		 * Current rfd wave.
		 */
		public int rfdWave = 0;

		/**
		 * Highest Recipe for disaster wave reache
		 */
		public int highestRfdWave = -1;

		/**
		 * True if the max total level has been announce.
		 */
		public boolean announceMaxLevel;

		/**
		 * Warrior's guild cycle event timer.
		 */
		public int warriorsGuildEventTimer;

		public int warriorsGuildCyclopsTimer;

		/**
		 * Save the index of the Warrior's guild armour data list.
		 */
		public int warriorsGuildArmourIndex;

		/**
		 * True if the player is using the cyclops drain tokens event.
		 */
		public boolean usingCyclopsEvent;

		/**
		 * True if the player summoned the Warrior's guild animator.
		 */
		public boolean summonedAnimator;

		/**
		 * True to 1 hit the npcs.
		 */
		public boolean hit1;

		/**
		 * True, if the player is a bot.
		 */
		public boolean isBot;

		/**
		 * Used for checking if player is a bot for combat related situations.
		 * @return
		 */
		public boolean isCombatBot()
		{
				return isBot;
		}


		public ArrayList<String> botDebug = new ArrayList<String>();

		public ArrayList<String> botItemsToWear = new ArrayList<String>();

		private String botStatus = "";

		/**
		 * The last combat type the bot was attacked in, same value as the overhead prayer.
		 */
		public int botLastDamageTakenType = 18;

		public long botEnemyPrayedTime;

		public int botDiagonalTicks;

		public String botPkType = "";

		public boolean botWornItemThisTick;

		public boolean botRegearEvent;

		public boolean botEarlyRetreat;

		public long botTimeInCombat;

		public int botEatingEventTimer;

		public long botTimeSwitchedItem;

		public boolean botEatingEvent;

		public int[] botEnemyDeathPosition = new int[2];

		/**
		 * The bot may have 1 action per tick.
		 */
		private boolean botActionApplied;


		/**
		 * The bot will have an idea of the enemy's special attack bar percentage, to react accordingly.
		 */
		public int botEnemySpecialAttack;

		public boolean botUsedSpecialAttack;

		public boolean botReAttack = true;

		public boolean botWearingPrimaryWeapon = true;

		public int botSpecialAttackWeapon;

		public int botPrimaryWeapon;

		public int botShield;

		public int botSpecialAttackWeaponShield;

		public int botArrowSpecial;

		public int botArrowPrimary;

		public long botTimePrayerToggled;

		public int botPureWeaponSet;

		/**
		 * The type of experience to show in the xp bar, weather it is COMBAT/SESSION/TOTAL
		 */
		public String xpBarShowType = "COMBAT";

		/**
		 * Save experince gained that belongs to the current log-in session.
		 */
		public int currentSessionExperience;

		/**
		 * Barrows brothers killed progress.
		 */
		public boolean[] barrowsBrothersKilled = new boolean[6];

		/**
		 * The part of the Completionist cape being edited.
		 */
		public String completionistCapePartEdited = "TOP";

		public long vestaLongSwordTime;

		public long vestaLongSwordAttackTime;

		public int vestaLongSwordUses;

		/**
		 * Save all the xp gained this tick and send at the end of the tick to the client.
		 */
		public int xpDropAmount;

		/**
		 * List of skills displayed in order for the xp orb.
		 */
		public String xpDropSkills = "";

		/**
		 * True, to stop gaining experience.
		 */
		public boolean xpLock;

		/**
		 * Save all names on current viewed highscore, to be later used when clicking on highscore name to display the relevant profile.
		 */
		public ArrayList<String> currentHighscoresNameList = new ArrayList<String>();

		/**
		 * Save current adventurer rank, so if it has increased, show achievement popup.
		 */
		public int currentAdventurerRank;

		/**
		 * Save current pker rank, so if it has increased, show achievement popup.
		 */
		public int currentPkerRank;

		/**
		 * The mac address.
		 */
		public String addressMac = "";

		/**
		 * The unique UID address.
		 */
		public String addressUid = "";

		/**
		 * True, if it has been already announced for the player.
		 */
		public boolean[] skillMilestone100mAnnounced = new boolean[25];

		/**
		 * True if the player can verify new objects.
		 */
		public boolean canVerifyMoreObjects;

		/**
		 * Store last non-verified object used, to prevent spam.
		 */
		public String lastNonVerifiedObjectUsed = "";

		/**
		 * True if player is using bank interface, to prevent packet interface abuse.
		 */
		private boolean usingBankInterface;

		/**
		 * True if player is using equipment bank interface, to prevent packet interface abuse.
		 */
		public boolean usingEquipmentBankInterface;

		/**
		 * The highscores main tab clicked by the player.
		 */
		public int highscoresTabClicked = -1;

		/**
		 * Gwd kills.
		 */
		public int gwdKills[] = new int[4];

		/**
		 * Total donator tokens donated for.
		 */
		public int donatorTokensReceived;

		/**
		 * Total amount of Donator tokens used for a rank.
		 */
		public int donatorTokensRankUsed;

		/**
		 * @return
		 * 			True if the player is a Donator.
		 */
		public boolean isDonator()
		{
				return donatorTokensRankUsed >= DonatorTokenUse.DonatorRankSpentData.DONATOR.getSpentRequired();
		}

		/**
		 * @return
		 * 			True if the player is a Super Donator.
		 */
		public boolean isSuperDonator()
		{
				return donatorTokensRankUsed >= DonatorTokenUse.DonatorRankSpentData.SUPER_DONATOR.getSpentRequired();
		}

		/**
		 * @return
		 * 			True if the player is an Extreme Donator.
		 */
		public boolean isExtremeDonator()
		{
				return donatorTokensRankUsed >= DonatorTokenUse.DonatorRankSpentData.EXTREME_DONATOR.getSpentRequired();
		}

		/**
		 * @return
		 * 			True if the player is a Legendary Donator.
		 */
		public boolean isLegendaryDonator()
		{
				return donatorTokensRankUsed >= DonatorTokenUse.DonatorRankSpentData.LEGENDARY_DONATOR.getSpentRequired();
		}

		/**
		 * Save time of an item picked up from floor.
		 */
		public long timePickedUpItem;

		/**
		 * Save last item id picked up, so i can add a delay to it if it is the same item, so it is not too fast.
		 */
		public int lastItemIdPickedUp;

		/**
		 * True if shop interface is opened.
		 */
		public boolean usingShop;

		/**
		 * Text of the first line of profile biography.
		 */
		public String biographyLine1 = "";

		/**
		 * Text of the second line of profile biography.
		 */
		public String biographyLine2 = "";

		/**
		 * Text of the third line of profile biography.
		 */
		public String biographyLine3 = "";

		/**
		 * Text of the fourth line of profile biography.
		 */
		public String biographyLine4 = "";

		/**
		 * Text of the fifth line of profile biography.
		 */
		public String biographyLine5 = "";

		public int deathsToNpc;

		/**
		 * Amount of slayer points.
		 */
		private int dawntainPoints;

		public int dawntainPointsHistory;

		/**
		 * True, if the player has profile privacy on, to hide Pvm stats and to hide rare drop announcements.
		 */
		public boolean profilePrivacyOn;

		/**
		 * Amount of clue scrolls completed.
		 */
		private int clueScrollsCompleted;

		/**
		 * Amount of barrows chests opened.
		 */
		private int barrowsRunCompleted;

		/**
		 * String of the player's name searched.
		 */
		private String profileNameSearched = "";

		public boolean profilePrivacySearched;

		/**
		 * Save time of when profile search was used to prevent spam.
		 */
		public long profileSearchDelay;

		/**
		 * Used to not calculate bonuses or send interface frames if equipment is the same.
		 */
		public int[] playerEquipmentAfterLastTick = new int[14];

		/**
		 * Save the opened tab before pressing bank inventory/equipment button.
		 * Used to deposit new items into this tab number.
		 */
		public int originalTab;

		/**
		 * Used for the God cape claiming system.
		 */
		public int godCapeClaimingTimer;

		/**
		 * Used to store, Pking, Skilling, Pvm activities. Increase each by +1 if activity used, cannot increase for another 10 seconds for all indexes.
		 */
		public int[] timeSpent = new int[3];

		/**
		 * Save time of when timeSpent[] has been last increased.
		 */
		public long lastTimeSpentUsed;

		/**
		 * Store skilling statistics such as bones buried etc..
		 */
		public int[] skillingStatistics = new int[25];

		/**
		 * Store dragon claws damage, used for calculating max dragon claws damage.
		 */
		public int[] storeDragonClawsDamage = new int[4];

		/**
		 * Save time of a dragon claw kill, to stop it from saving 4 times.
		 */
		public long timeDragonClawKill;

		/**
		 * Which difficulty tab was last clicked on the achievement interface.
		 */
		public String lastProfileTabText = "INFO";

		/**
		 * Amount of barrages casted.
		 */
		public int barragesCasted;

		/**
		 * Store the npc id and aggression started time.
		 */
		public ArrayList<String> npcAggressionData = new ArrayList<String>();

		/**
		 * Store the time of when the player cannot use protect item for 10 minutes.
		 */
		public long protectItemDisabledTime;

		/**
		 * True, if the player has the same dialogue opened before opening the same dialogue again.
		 */
		public boolean hasDialogueOptionOpened;

		/**
		 * True, if the player has last used a manual combat spell. This is to determine weather the last magic spell was an autocast or a manual cast.
		 */
		public boolean lastUsedManualSpell;

		/**
		 * Slayer tower doors, they do not update when going up a height level, so have to manually update it when using the door.
		 */
		public long doorAntiSpam;

		/**
		 * X coordinate of the fishing spot before it got moved, this is used to check if the fishing spot still exists in the spot
		 * that i'm fishing in.
		 */
		public int lastFishingSpotX;

		/**
		 * Y coordinate of the fishing spot before it got moved, this is used to check if the fishing spot still exists in the spot
		 * that i'm fishing in.
		 */
		public int lastFishingSpotY;

		/**
		 * True, to not send a message when using an achievement item.
		 */
		public boolean doNotSendMessage;

		/**
		 * Time used stall.
		 */
		public long stoleFromStallTime;

		/**
		 * True, if anti-fire event is being used.
		 */
		public boolean antiFireEvent;

		/**
		 * Save the last dialogue sent, used for shop 'back' button.
		 */
		public int lastDialogueSelected;

		/**
		 * Save npc damage mask time.
		 */
		public long npcDamageMaskTime;

		/**
		 * True, if the special attack used is a ranged weapon. Used for npcs.
		 */
		public boolean rangedSpecialAttackOnNpc;

		/**
		 * The x-coordinate of the player when the agility action ends.
		 */
		public int agilityEndX;

		/**
		 * The y-coordinate of the player when the agility action ends.
		 */
		public int agilityEndY;

		/**
		 * True to not call the playerTurn method.
		 */
		public boolean ignorePlayerTurn;

		/**
		 * The skill being used for the Skill cape master.
		 */
		public int skillCapeMasterSkill;

		/**
		 * The expression of the Skill cape master.
		 */
		public int skillCapeMasterExpression = 9850;

		/**
		 * Show option, attack, trade, follow etc
		 **/
		public String optionType = "null";

		/**
		 * True, to enable no-clipping for the player, for certain movements such as through doors.
		 */
		public boolean forceNoClip;

		/**
		 * Used to know what action the player is doing before summoning the amount interface, that is used for x amount for the bank and skill editing.
		 */
		private String amountInterface = "";

		/**
		 * Store the name of the skill that is being used for this interface.
		 */
		public String skillingInterface = "";

		/**
		 * Store the skilling data, used with skillingInterface String.
		 */
		public int[] skillingData = new int[10];

		/**
		 * Store the experience gained in a skill after reaching maxing it out. 
		 * <p>
		 * Used for saving the extra experience after 99 for when quick set-up is used.
		 */
		public int[] combatExperienceGainedAfterMaxed = new int[7];

		/**
		 * Used for rapid heal.
		 */
		public int hitPointsRegenerationCount;

		/**
		 * True, if the Hit points regeneration event is running.
		 */
		public boolean hitPointsRegenerationEvent;

		/**
		 * Time left for the anti fire potion effect.
		 */
		private int antiFirePotionTimer;

		public int getAntiFirePotionTimer()
		{
				return antiFirePotionTimer;
		}

		public void setAntiFirePotionTimer(int antiFirePotionTimer)
		{
				this.antiFirePotionTimer = antiFirePotionTimer;
		}

		/**
		 * True, if the anti fire potion effect is on.
		 */
		public boolean antiFirePotion;

		/**
		 * True, if the player is currently travelling to the item on object action.
		 */
		public boolean itemOnObjectEvent;

		/**
		 * True, if the player has the OSRS xp orb bar opened.
		 */
		public boolean useBottomRightWildInterface;

		/**
		 * Amount of agility points.
		 */
		public int agilityPoints;


		/**
		 * @return
		 * 			The amount of agility points.
		 */
		public int getAgilityPoints()
		{
				return agilityPoints;
		}

		/**
		 * Change te amount of agilityPoints variable.
		 * @param value
		 * 			The amount to set agilityPoints to.
		 */
		public void setAgilityPoints(int value)
		{
				agilityPoints = value;
		}

		/**
		 * Amount of merit points.
		 */
		public int meritPoints;

		/**
		 * @return
		 * 			The amount of merit points.
		 */
		public int getMeritPoints()
		{
				return meritPoints;
		}

		/**
		 * Set the amount of merit points.
		 * @param value
		 * 			The amount to change merit points to.
		 */
		public void setMeritPoints(int value)
		{
				meritPoints = value;
		}

		/**
		 * These are to be used for creating delays, using
		 * System.currentTimeMillis(), If there is a few objects close to the
		 * player, use a different delay variable for each one. The different
		 * variables are objectDelay1/2/3/4/5
		 */
		public long objectDelay1, objectDelay2, objectDelay3, objectDelay4, objectDelay5;

		/**
		 * Stepping stone at Brimhaven Dungeon cycle event integer.
		 */
		public byte stoneTimer;


		/**
		 * The amount of game ticks, the crumbling wall cycle event will last for.
		 */
		public int firstCrumblingWallActionEvent;


		/**
		 * The amount of game ticks, the log balance cycle event will last for.
		 */
		public int logBalanceActionEvent;

		public long agility1;

		public long agility2;

		public long agility3;

		public long agility4;

		public long agility5;

		public long agility6;

		public long agility7;


		public int smithingItem;

		public int smithingExperience;

		public int smithingBarToRemove;

		public int smithingRemoveamount;

		public int amountToSmith;

		public long meleeOutOfDistanceTime;

		public long wildernessAgilityCourseImmunity;

		public int[] pouchesPure = new int[4];

		public int[] pouchesRune = new int[4];

		public int[] specialObjectActionPoint = new int[6];

		public String currentAgilityArea = "";

		private int agilityCourseCompletedMessage = -1;

		/**
		 * True, if Wilderness course obstacle pipe has been used.
		 */
		public boolean wildernessCourseObstaclePipe;

		/**
		 * True, if Wilderness course log balance has been used.
		 */
		public boolean wildernessCourseLogBalance;

		/**
		 * True, if Wilderness course stepping stone has been used.
		 */
		public boolean wildernessCourseSteppingStone;

		/**
		 * True, if Wilderness course rope swing has been used.
		 */
		public boolean wildernessCourseRopeSwing;

		/**
		 * The NPC type of the slayer task.
		 */
		public int slayerTaskNpcType;

		/**
		 * The amount of slayer monsters to kill.
		 */
		public int slayerTaskNpcAmount;

		/**
		 * 0: ore identity.<p>
		 * 1: level required to mine the ore.<p>
		 * 2: experience to gain from the ore.
		 */
		public int[] oreInformation = new int[3];

		/**
		 * Amount of game ticks untill the ore is mined.
		 */
		public int miningTimer = 0;


		/**
		 * Is the wcTimerEvent being used?
		 */
		public boolean isUsingWcTimerEvent;

		/**
		 * True, if the crafting event is being used.
		 */
		public boolean craftingEvent;

		/**
		 * True, if the herblore event is being used.
		 */
		public boolean usingHerbloreEvent;

		/**
		 * Is the miningTimerEvent being used?
		 */
		public boolean isUsingMiningTimerEvent;

		/**
		 * Is the fishTimerEvent being used?
		 */
		public boolean isUsingFishTimerEvent;

		/**
		 * The amount of game ticks, the wood cutting event will last for.
		 */
		public int woodCuttingEventTimer;

		/**
		 * The amount of game ticks, the fishing event will last for.
		 */
		public int fishTimerAmount = 0;

		/**
		 * True, if the player is using the Smithing cycle event.
		 */
		public boolean smithingEvent;

		/**
		 * @return
		 * The state of smithingEvent boolean.
		 */
		public boolean getSmithingEvent()
		{
				return smithingEvent;
		}

		/**
		 * Change the state of smithingEvent boolean.
		 * @param state
		 * 			The state to change the smithingEvent boolean to.
		 */
		public void setSmithingEvent(boolean state)
		{
				smithingEvent = state;
		}


		/**
		 * True, if the player is in the act of using bones on altar.
		 */
		public boolean usingBoneOnAltar;

		/**
		 * True, if the player is crafting.
		 */
		public boolean getCrafting;


		public boolean smeltInterface;

		public int[] farm = new int[2];

		public boolean playerFletch;

		public int[][] playerSkillProp = new int[20][15];

		public boolean isCookingEvent;

		public boolean seedPlanted;

		public boolean seedWatered;

		public boolean patchRaked;

		public boolean patchCleaned;

		public boolean logBalance;

		public boolean obstacleNetUp;

		public boolean treeBranchUp;

		public boolean balanceRope;

		public boolean treeBranchDown;

		public boolean obstacleNetOver;

		public boolean ropeSwing;

		public boolean logBalance1;

		public boolean obstacleNet;

		public boolean balancingLedge;

		public boolean Ladder;

		public boolean[] combatSkillsAnnounced = new boolean[7];

		/**
		 * Used to count how many players in Wilderness in the past 10 minutes on server print debug.
		 */
		public long wildernessEnteredTime;

		private boolean usingFightCaves;

		/**
		 * True, if the player can change edit combat stats.
		 */
		private boolean ableToEditCombat = true;

		private int meleeMainKills;

		private int hybridKills;

		private int berserkerPureKills;

		private int pureKills;

		private int rangedTankKills;

		public int f2pKills;

		/**
		 * Melee, hybrid, berserker, pure, ranged, f2p.
		 */
		public int[] deathTypes = new int[6];

		public long timeFightStarted;

		public String lastPlayerAttackedName = "";

		public long timeMeleeUsed;

		public long timeRangedUsed;

		public long timeMagicUsed;

		/**
		 * The game mode chosen.
		 */
		public String gameMode = "";

		public String gameModeTitle = "";

		public String playerTitle = "";

		public String titleColour = "<col=ED700E>";

		/**
		 * Used to restore special attack by 5%.
		 */
		public int specialAttackRestoreTimer;

		/**
		 * True, if music is being looped.
		 */
		public boolean isLoopingMusic;

		/**
		 * True, if auto music is turned on.
		 */
		public boolean autoMusic;

		/**
		 * Save the current song playing, to use when next song is to be played.
		 */
		public int currentMusicOrder = -1;


		/**
		 * True, if the Ranged ammo used will be dropped on the floor.
		 */
		public boolean ammoDropped;

		public boolean getAmmoDropped()
		{
				return ammoDropped;
		}

		public void setAmmoDropped(boolean state)
		{
				ammoDropped = state;
		}

		/**
		 * True, if the player is casting magic. This is used to confirm that the player is magic following.
		 * <p>
		 * We cannot use isUsingMagic() because isUsingMagic() turns to false after the hitsplat appears to stop the player from re-attacking.
		 */
		private boolean lastCastedMagic;

		/**
		 * True, to reset face update at the end of the tick.
		 * <p>
		 * Used to stop the player from permanently facing the target after being used once.
		 */
		private boolean faceResetAtEndOfTick;

		public boolean getFaceResetAtEndOfTick()
		{
				return faceResetAtEndOfTick;
		}

		public void setFaceResetAtEndOfTick(boolean state)
		{
				faceResetAtEndOfTick = state;
		}

		public boolean getInitiateCombatEvent()
		{
				return initiateCombatEvent;
		}

		public void setInitiateCombatEvent(boolean initiateCombatEvent)
		{
				this.initiateCombatEvent = initiateCombatEvent;
		}

		private boolean initiateCombatEvent;

		/**
		 * The NPC identity being attacked by the player.
		 */
		public int npcIdentityAttacking;

		/**
		 * @return The NPC identity being attacked by the player.
		 */
		public int getNpcIdAttacking()
		{
				return npcIdentityAttacking;
		}

		/**
		 * @param value
		 *        The value to change npcIdentityAttacking into.
		 */
		public void setNpcIdentityAttacking(int value)
		{
				npcIdentityAttacking = value;
		}

		/**
		 * Reset npcIdentityAttacking to 0.
		 */
		public void resetNpcIdentityAttacking()
		{
				npcIdentityAttacking = 0;
		}

		/**
		 * The player identity to follow.
		 */
		public int playerIdToFollow;

		/**
		 * @return The player identity to follow.
		 */
		public int getPlayerIdToFollow()
		{
				return playerIdToFollow;
		}

		/**
		 * @param value
		 *        The value to change the playerIdToFollow into.
		 */
		public void setPlayerIdToFollow(int value)
		{
				playerIdToFollow = value;
		}

		public void resetPlayerIdToFollow()
		{
				playerIdToFollow = 0;
		}

		/**
		 * The NPC identity to follow.
		 */
		public int npcIdToFollow;

		/**
		 * @return The NPC identity to follow.
		 */
		public int getNpcIdToFollow()
		{
				return npcIdToFollow;
		}

		/**
		 * @param value
		 *        The value to change the npcIdToFollow into.
		 */
		public void setNpcIdToFollow(int value)
		{
				npcIdToFollow = value;
		}

		public void resetNpcIdToFollow()
		{
				npcIdToFollow = 0;
		}

		/**
		 * True, if vengeance is casted.
		 */
		public boolean vengeance;

		/**
		 * @return The state of vengeance.
		 */
		public boolean getVengeance()
		{
				return vengeance;
		}

		/**
		 * @param state
		 *        The state of vengeance to change to.
		 */
		public void setVengeance(boolean state)
		{
				vengeance = state;
		}

		/**
		 * True, if administrator tank mode.
		 */
		public boolean tank;

		public boolean getTank()
		{
				return tank;
		}

		public void setTank(boolean state)
		{
				tank = state;
		}

		/**
		 * Last animation identity used.
		 */
		private int lastAnimation;

		/**
		 * @return Last animation identity used.
		 */
		public int getLastAnimation()
		{
				return lastAnimation;
		}

		/**
		 * @param lastAnimation
		 *        The value to change lastAnimation into.
		 */
		public void setLastAnimation(int lastAnimation)
		{
				this.lastAnimation = lastAnimation;
		}


		/**
		 * Store the time of when a skill was last boosted/drained.
		 */
		public long[] boostedTime = new long[7];

		/**
		 * True, if the drain skill event is being used.
		 */
		private boolean drainEvent;

		/**
		 * @return True, if the drain skill event is being used.
		 */
		public boolean isDrainEvent()
		{
				return drainEvent;
		}

		/**
		 * @param drainEvent
		 *        The state to change drainEvent into.
		 */
		public void setDrainEvent(boolean drainEvent)
		{
				this.drainEvent = drainEvent;
		}

		/**
		 * True, to show tutorial arrows when player moves/accepts clothes.
		 */
		public boolean showTutorialArrows;

		/**
		 * Last time a quick set-up button was used.
		 */
		public long lastQuickSetUpClicked;

		/**
		 * Store the time of when magic was used.
		 */
		private long lastUsedMagic;

		/**
		 * @return The time of when magic was last used.
		 */
		public long getLastUsedMagic()
		{
				return lastUsedMagic;
		}

		public long timeUsedBarrage;

		public long timeAttackReduced;

		/**
		 * @param lastUsedMagic
		 *        The time of when magic was used.
		 */
		public void setLastUsedMagic(long lastUsedMagic)
		{
				this.lastUsedMagic = lastUsedMagic;
		}

		/**
		 * Which difficulty tab was last clicked on the achievement interface.
		 */
		public String lastAchievementDifficulty = "EASY";

		/**
		 * Which difficulty tab was last clicked on the rewardsachievement interface.
		 */
		public String lastAchievementRewardsDifficulty = "EASY";

		/**
		 * Which column on the achievement interface was last clicked.
		 */
		public int lastAchievementColumn;

		/**
		 * Name of the last achievement that was clicked on.
		 */
		public String lastAchievementName;

		/**
		 * Amount of bosses the player killed.
		 */
		public int bossKills;

		/**
		 * @return Get the amount of bosses the player killed.
		 */
		public int getBossKills()
		{
				return bossKills;
		}

		/**
		 * Set the amount of bossKills.
		 * 
		 * @param value
		 *        The value of bossKills to change to.
		 */
		public void setBossKills(int value)
		{
				bossKills = value;
		}

		/**
		 * Amount of food ate.
		 */
		public int foodAte;

		/**
		 * @return Get the amount of food eaten.
		 */
		public int getFoodAte()
		{
				return foodAte;
		}

		/**
		 * Set the amount of foodAte.
		 * 
		 * @param value
		 *        The value of foodAte to change to.
		 */
		public void setFoodAte(int value)
		{
				foodAte = value;
		}

		/**
		 * Current kill streak.
		 */
		public int currentKillStreak;

		/**
		 * The record kill streak.
		 */
		public int killStreaksRecord;

		/**
		 * Save time of when account was created.
		 */
		public long timeOfAccountCreation;

		/**
		 * Store time of when leech animation was last used.
		 */
		public long lastLeechAnimation;

		/**
		 * Last time the player drank a potion.
		 */
		public long lastPotionSip = System.currentTimeMillis();

		/**
		 * Amount of tiles player has ran across.
		 */
		public int tilesWalked;

		/**
		 * True, if redemption or wrath has been activated.
		 */
		public boolean redemptionOrWrathActivated;

		/**
		 * Used to determine the last player who started the duo following for Runescape follow dancing.
		 */
		public boolean followLeader;

		/**
		 * Last time the player throwed a snowball.
		 */
		private long lastSnowBallThrowTime = System.currentTimeMillis();

		/**
		 * True, if the player is using the overload event
		 */
		public boolean overloadEvent;

		/**
		 * Used for timing the overload damage hitsplat.
		 */
		public int overloadTicks;

		/**
		 * Used for timing the overload reboosting time frame.
		 */
		public int overloadReboostTicks;

		/**
		 * Old x position of the player i'm following.
		 */
		public int oldX;

		/**
		 * Old y position of the player i'm following.
		 */
		public int oldY;

		/**
		 * True, if the player successfully drank a potion.
		 */
		public boolean showPotionMessage = true;

		/**
		 * True, if player is drinking an Extreme magic potion.
		 */
		public boolean extremeMagic;

		/**
		 * Store the time of when the dialgueAction integer was last changed.
		 */
		public long lastDialogueAction;

		/**
		 * 1 is normal interface, 2 is combat spells first, 3 is teleports spells first.
		 */
		public int ancientsInterfaceType = 1;

		/**
		 * True, if the player is following with melee.
		 */
		private boolean meleeFollow;

		/**
		 * Amount of Ring of recoil charges left.
		 */
		public int recoilCharges = 40;

		/**
		 * @return The amount of recoilCharges integer.
		 */
		public int getRecoilCharges()
		{
				return recoilCharges;
		}

		/**
		 * set the recoilCharges integer to the specified amount
		 * 
		 * @param amount
		 *        The amount to change the recoilCharges integer to.
		 */
		public void setRecoilCharges(int amount)
		{
				recoilCharges = amount;
		}

		/**
		 * True, if the player is using bone on altar cycle event.
		 */
		public boolean usingBoneOnAltarEvent;

		/**
		 * Record the time of when the bone on altar animation was last used.
		 */
		public long boneOnAltarAnimation;

		/**
		 * True, to change what equipment and death icon on equipment tab does.
		 */
		public boolean clipping;

		/**
		 * True, if the player has auto-casting turned on.
		 */
		public boolean autoCasting;

		/**
		 * Get the autocasting state.
		 * 
		 * @return The state of autocasting.
		 */
		public boolean getAutoCasting()
		{
				return autoCasting;
		}

		/**
		 * Change the state of autoCasting.
		 * 
		 * @param state
		 *        The state of autoCasting.
		 */
		public void setAutoCasting(Boolean state)
		{
				autoCasting = state;
		}

		/**
		 * Each special attack weapon has its own slot, if slot is 1 means it's a single hitsplat weapon, 2 means double hitsplat.
		 * 29 and above is free. When using double damage weapon, leave the next index after it free.
		 */
		public int[] specialAttackWeaponUsed = new int[35];

		/**
		 * Store the maximum hit of a special attack weapon, same order as specialAttackWeaponUsed array.
		 */
		public int[] maximumSpecialAttack = new int[35];

		public int[] maximumSpecialAttackNpc = new int[35];

		/**
		 * Store the amount of times a special attack weapon was used, same order as specialAttackWeaponUsed array.
		 */
		public int[] weaponAmountUsed = new int[35];

		public int[] weaponAmountUsedNpc = new int[35];

		/**
		 * Add to the amount a weapon has been used.
		 * @param index
		 * 			The index of the weapon.
		 */
		public void setWeaponAmountUsed(int index)
		{
				if (againstPlayer)
				{
						weaponAmountUsed[index]++;
				}
				else
				{
						weaponAmountUsedNpc[index]++;
				}
		}

		/**
		 * True if player is against player, used to save special attack amount used.
		 */
		public boolean againstPlayer;

		/**
		 * Melee, ranged, magic, vengeance, recoil, dfs.
		 * Pvp only, it is in the Pking tab on the Profile.
		 */
		public int[] totalDamage = new int[6];

		/**
		 * Store the time of when the last saveMaximumDamage method was used.
		 */
		public long lastSpecialAttackSaved;

		/**
		 * Store the single hitsplat damage of a special attack weapon.
		 */
		public int firstHitSplatDamage;

		/**
		 * The name of the last clan chat joined.
		 */
		public String lastClanChatJoined = "ShatterScape";

		/**
		 * Amount of targets killed.
		 */
		public int targetsKilled;

		/**
		 * Amount of times died from target.
		 */
		public int targetDeaths;

		/**
		 * Amount of Barrows brothers killed.
		 */
		public int barrowsKillCount;

		/**
		 * 0 if the player is not transformed.
		 */
		private int transformed;

		/**
		 * 5 is easter egg only, 1 is monkey.
		 */
		public int getTransformed()
		{
				return transformed;
		}

		/**
		 * True, if the player has transformed into an Egg.
		 */
		public boolean isAnEgg;

		/**
		 * The total amount of votes the player has accumulated.
		 */
		public int voteTotalPoints;

		/**
		 * This is used to extinguish different Clue scroll quests.
		 */
		public int clueScrollType = -1;

		/**
		 * True, if the player is using the summon pet cycle event.
		 */
		public boolean isUsingSummonPetEvent;

		/**
		 * Used to stop block emote being used an instant after the attack emote is called. Which ends up cancelling the attack emote.
		 */
		public long lastAttackAnimationTimer;

		/**
		 * Potion sip timer.
		 */
		public byte timer;

		/**
		 * The amount of ticks the startInterfaceEvent will last for.
		 */
		public int extraTime;

		/**
		 * True, if the startSkullTimerEvent is active.
		 */
		public boolean isUsingSkullTimerEvent;

		/**
		 * True, if the player is logging out manually.
		 */
		public boolean manualLogOut;

		/**
		 * The last bank tab the player had opened.
		 */
		public byte lastBankTabOpened;

		/**
		 * Change the amount of lastBankTabOpened
		 * 
		 * @param value
		 *        The amount of lastbankTabOpened
		 */
		public void setLastBankTabOpened(byte value)
		{
				lastBankTabOpened = value;
		}

		/**
		 * @return The amount of lastBankTabOpened.
		 */
		public byte getLastBankTabOpened()
		{
				return lastBankTabOpened;
		}

		/**
		 * Set the state of inventoryUpdate.
		 * 
		 * @param state
		 *        State of inventoryUpdate.
		 */
		public void setInventoryUpdate(boolean state)
		{
				inventoryUpdate = state;
		}

		/**
		 * @return The state of inventoryUpdate.
		 */
		public boolean getInventoryUpdate()
		{
				return inventoryUpdate;
		}

		/**
		 * True, if the inventory needs visual updating.
		 */
		private boolean inventoryUpdate;

		/**
		 * Store the time of when the casket was last opened.
		 */
		public long casketTime;

		/**
		 * The total amount of the item scanned in the bank and inventory.
		 */
		public int quantityOfItem;

		/**
		 * The time the player will be un-muted.
		 */
		public long timeUnMuted;

		/**
		 * True, if the player is using Hand cannon special attack.
		 */
		public boolean handCannonSpecialAttack;

		/**
		 * The damage of the Morrigan's javelin special attack to deal, either 5 or less, depending on the victim's hitpoints.
		 */
		public int morrigansJavelinDamageToDeal;

		/**
		 * Amount of Morrigan's javelin special attack damages to apply.
		 */
		public int amountOfDamages;

		/**
		 * True, if Morrigan's javelin special attack is being used.
		 */
		public boolean morrigansJavelinSpecialAttack;

		/**
		 * The time of when the last anti-poison potion was taken.
		 */
		public long lastPoisonSip;

		/**
		 * The amount of time of immunity to poison.
		 */
		public long poisonImmune;

		/**
		 * The last ip connected to the account.
		 */
		public String lastSavedIpAddress = "";

		/**
		 * The time of when the player logged out.
		 */
		public long logOutTime = System.currentTimeMillis();

		/**
		 * True if Saradomin special attack is activated.
		 */
		public boolean saradominSwordSpecialAttack;

		/**
		 * True if player is wearing full Guthan's.
		 */
		public boolean wearingFullGuthan;

		/**
		 * True if player is wearing full Verac's.
		 */
		public boolean wearingFullVerac;

		public boolean veracEffectActivated;

		/**
		 * True, if the player is using a special attack that causes multiple damage. e.g: Dragon dagger, Dragon claws and Dragon halberd.
		 */
		public boolean multipleDamageSpecialAttack;

		/**
		 * Get the state of multipleDamageSpecialAttack.
		 * 
		 * @return The state of multipleDamageSpecialAttack.
		 */
		public boolean getMultipleDamageSpecialAttack()
		{
				return multipleDamageSpecialAttack;
		}

		/**
		 * Change the state of multipleDamageSpecialAttack.
		 * 
		 * @param state
		 *        The state of multipleDamageSpecialAttack.
		 */
		public void setMultipleDamageSpecialAttack(boolean state)
		{
				multipleDamageSpecialAttack = state;
		}

		/**
		 * True, if the player is using Dragon claws special attack.
		 */
		public boolean dragonClawsSpecialAttack;

		/**
		 * Change the state of dragonClawsSpecialAttack.
		 * 
		 * @param state
		 *        The state of dragonClawsSpecialAttack.
		 */
		public void setDragonClawsSpecialAttack(boolean state)
		{
				dragonClawsSpecialAttack = state;
		}

		/**
		 * Get the state of dragonClawsSpecialAttack.
		 * 
		 * @return The state of dragonClawsSpecialAttack.
		 */
		public boolean getDragonClawsSpecialAttack()
		{
				return dragonClawsSpecialAttack;
		}

		/**
		 * True, if the player is using the dragon claws special attack event.
		 */
		public boolean usingDragonClawsSpecialAttackEvent;

		/**
		 * Get the state of usingDragonClawsSpecialAttackEvent.
		 * 
		 * @return The state of usingDragonClawsSpecialAttackEvent.
		 */
		public boolean getUsingDragonClawsSpecialAttackEvent()
		{
				return usingDragonClawsSpecialAttackEvent;
		}

		/**
		 * Set the state of usingDragonClawsSpecialAttackEvent.
		 * 
		 * @param state
		 *        The state of usingDragonClawsSpecialAttackEvent.
		 */
		public void setUsingDragonClawsSpecialAttackEvent(boolean state)
		{
				usingDragonClawsSpecialAttackEvent = state;
		}

		/**
		 * True, if the player has a red skull.
		 */
		public boolean redSkull;

		/**
		 * Change the state of redSkull.
		 * 
		 * @param state
		 *        The state of redSkull.
		 */
		public void setRedSkull(boolean state)
		{
				redSkull = state;
		}

		/**
		 * Get the state of redSkull.
		 * 
		 * @return The state of redSkull.
		 */
		public boolean getRedSkull()
		{
				return redSkull;
		}

		/**
		 * True, if the player has a white skull.
		 */
		public boolean whiteSkull;

		/**
		 * Change the state of whiteSkull.
		 * 
		 * @param state
		 *        The state of whiteSkull.
		 */
		public void setWhiteSkull(boolean state)
		{
				whiteSkull = state;
		}

		/**
		 * Get the state of whiteSkull.
		 * 
		 * @return The state of whiteSkull.
		 */
		public boolean getWhiteSkull()
		{
				return whiteSkull;
		}

		/**
		 * Amount of milliseconds, the player has been online for, in this session.
		 */
		public long millisecondsOnline;

		/**
		 * The amount of seconds, the player has been online for.
		 */
		public int secondsBeenOnline;

		public int gameTicksOnline;

		/**
		 * The date of when the account was created.
		 */
		public String accountDateCreated = "";

		/**
		 * True, if the special attack event is being used.
		 */
		public boolean specialAttackEvent;

		/**
		 * Store the time of the last Wolpertinger special attack used.
		 */
		public long lastWolpertingerSpecialAttack;

		/**
		 * @param amount
		 *        Change the player's Hitpoints to this.
		 */
		public void setHitPoints(int amount)
		{
				if (this.getDead())
				{
						return;
				}
				currentCombatSkillLevel[ServerConstants.HITPOINTS] = amount;
				Skilling.updateSkillTabFrontTextMain(this, ServerConstants.HITPOINTS);
		}

		/**
		 * Duel arena stakes done.
		 */
		public int duelArenaStakes;

		/**
		 * Trades completed.
		 */
		public int tradesCompleted;

		/**
		 * Amount of times used teleport.
		 */
		public int teleportsUsed;

		/**
		 * Amount of times the player has died in a safe area.
		 */
		public int safeDeaths;

		/**
		 * Amount of times the player has killed another player in a safe area.
		 */
		public int safeKills;

		/**
		 * Amount of potion doses drank.
		 */
		public int potionDrank;

		/**
		 * KDR of the player, used for highscores.
		 */
		public int kdr;

		/**
		 * True, if the resting event is active.
		 */
		public boolean restingEvent;

		/**
		 * True, if the player is resting.
		 */
		public boolean resting;

		/**
		 * The delay in milliseconds to gain energy.
		 */
		public int agilityRestoreDelay = 3000;

		/**
		 * Run energy remaining.
		 */
		public double runEnergy = 100;

		public boolean energyGainEvent;

		/**
		 * Store the time of the last time the player had their run energy restored.
		 */
		public long lastRunRecovery;

		/**
		 * True, if the player is running.
		 */
		public boolean isRunning()
		{
				return isNewWalkCmdIsRunning() || (runModeOn && isMoving());
		}

		/**
		 * Store the time of when a clan chat message was last sent by the player.
		 */
		public long clanChatMessageTime;

		/**
		 * True, if the player is dead.
		 */
		public boolean dead;

		public boolean getDead()
		{
				return dead;
		}

		/**
		 * Change the state of isDead.
		 */
		public void setDead(boolean state)
		{
				dead = state;
		}

		/**
		 * True, to call the familiar.
		 */
		public boolean forceCallFamiliar;

		/**
		 * Last time familiar called.
		 */
		public long callFamiliarTimer = System.currentTimeMillis();

		/**
		 * True, if the player has summoned a pet.
		 */
		public boolean petSummoned;

		/**
		 * The state of petSummoned.
		 * 
		 * @return The state of petSummoned.
		 */
		public boolean getPetSummoned()
		{
				return petSummoned;
		}

		/**
		 * Change the state of petSummoned.
		 * 
		 * @param state
		 *        The state of petSummoned.
		 */
		public void setPetSummoned(boolean state)
		{
				petSummoned = state;
		}

		/**
		 * The NPC type of the pet that the player currently has summoned.
		 */
		private int petId;

		/**
		 * True if the ladder event is being used.
		 */
		public boolean ladderEvent;

		/**
		 * True, if the idle Event is in use.
		 */
		public boolean idleEventUsed;

		/**
		 * This will keep increasing by +1 when the player is not sending any packets to the server. If this is 3 or more, then the player is not sending any connections to the server.
		 * <p>
		 * This is used to disconnect the player, if in-combat, after 40 seconds.
		 */
		private int timeOutCounter = 0;

		/**
		 * Is the clickNpcTypeEvent1 being used?
		 */
		public boolean usingClickNpcType1Event;

		/**
		 * Is the clickNpcTypeEvent2 being used?
		 */
		public boolean usingClickNpcType2Event;

		/**
		 * Is the clickNpcTypeEvent3 being used?
		 */
		public boolean usingClickNpcType3Event;

		/**
		 * Is the clickNpcTypeEvent4 being used?
		 */
		public boolean usingClickNpcType4Event;

		/**
		 * Is the clickObject1Event active?
		 */
		public boolean doingClickObjectType1Event;

		/**
		 * Is the clickObject2Event active?
		 */
		public boolean doingClickObjectType2Event;

		/**
		 * Is the clickObject3Event active?
		 */
		public boolean doingClickObjectType3Event;

		/**
		 * if true, the player cannot perform any action and is performing agility.
		 */
		public boolean doingAgility;

		/**
		 * The state of doingAgility.
		 * 
		 * @return The state of doingAgility.
		 */
		public boolean getDoingAgility()
		{
				return doingAgility;
		}

		/**
		 * Change the state of doingAgility.
		 * 
		 * @param state
		 *        The state of doingAgility.
		 */
		public void setDoingAgility(boolean state)
		{
				doingAgility = state;
		}

		/**
		 * True, if the interface Cycle Event is being used.
		 */
		public boolean isUsingInterfaceEvent;

		/**
		 * The range damage for a single hit.
		 */
		public int rangedFirstDamage;

		/**
		 * The range damage for a double hit.
		 */
		public int rangedSecondDamage;

		/**
		 * True, to show the Diamond bolts (e) GFX during the hitsplat.
		 */
		public boolean showDiamondBoltGFX;

		/**
		 * True, to show the Dragon bolts (e) GFX during the hitsplat.
		 */
		public boolean showDragonBoltGFX;

		/**
		 * True, to show the Ruby bolts (e) GFX during the hitsplat.
		 */
		public boolean showRubyBoltGFX;

		/**
		 * Store the maximum damage of the player.
		 */
		public int maximumDamageRanged;

		/**
		 * * Store the normal single hit damage of a melee weapon..
		 */
		public int meleeFirstDamage;

		public int graniteMaulSpecialDamage;

		public int graniteMaulSpecialCriticalDamage;

		public boolean isGraniteMaulSpecial;

		/**
		 * * Store the second hit of a Dragon dagger or Halbred special attack.
		 */
		public int meleeSecondDamage;

		/**
		 * Store the third hit of a Dragon claw special attack.
		 */
		public int meleeThirdDamage;

		/**
		 * Store the fourth hit of a Dragon claw special attack.
		 */
		public int meleeFourthDamage;

		/**
		 * Maximum damage of Melee.
		 */
		public int maximumDamageMelee;

		/**
		 * Magic damage.
		 */
		private int magicDamage;

		/**
		 * Maximum damage of Magic.
		 */
		private int maximumDamageMagic;

		/**
		 * Amount of 600ms cycles untill the teleport finishes.
		 */
		public byte teleportCycle;

		/**
		 * True, if the player is teleporting. Use this to check if a player is teleporting.
		 */
		public boolean isTeleporting()
		{
				return teleporting;
		}

		/**
		 * True, if Magic bow special attack is being used.
		 */
		private boolean magicBowSpecialAttack;

		/**
		 * True, if using Dark bow to start a normal attack.
		 */
		private boolean usingDarkBowNormalAttack;

		/**
		 * True if magic will splash.
		 */
		private boolean magicSplash;

		public int[] baseSkillLevel = new int[21];

		public int[] currentCombatSkillLevel = new int[7];

		public int getCurrentCombatSkillLevel(int combatSkill)
		{
				return currentCombatSkillLevel[combatSkill];
		}

		/**
		 * Experience in a skill.
		 */
		public int[] skillExperience = new int[21];

		public int getBaseDefenceLevel()
		{
				return baseSkillLevel[ServerConstants.DEFENCE];
		}

		public int getBaseAttackLevel()
		{
				return baseSkillLevel[ServerConstants.ATTACK];
		}

		public int getBaseStrengthLevel()
		{
				return baseSkillLevel[ServerConstants.STRENGTH];
		}

		public int getBaseRangedLevel()
		{
				return baseSkillLevel[ServerConstants.RANGED];
		}

		public int getBasePrayerLevel()
		{
				return baseSkillLevel[ServerConstants.PRAYER];
		}

		public int getBaseMagicLevel()
		{
				return baseSkillLevel[ServerConstants.MAGIC];
		}

		/**
		 * @return The player's maximum hitpoints.
		 */
		public int getBaseHitPointsLevel()
		{
				return baseSkillLevel[ServerConstants.HITPOINTS];
		}

		/**
		 * The other player that is being attacked by this player.
		 */
		public int playerIdAttacking;

		public void setPlayerIdAttacking(int value)
		{
				playerIdAttacking = value;
		}

		public void resetPlayerIdAttacking()
		{
				playerIdAttacking = 0;
		}

		public int getPlayerIdAttacking()
		{
				return playerIdAttacking;
		}

		/**
		 * True, if the player is teleporting.
		 */
		private boolean teleporting;

		/**
		 * True, if the player is using range.
		 */
		public boolean usingRanged;

		/**
		 * The state of usingRange.
		 * 
		 * @return The state of usingRange.
		 */
		public boolean getUsingRanged()
		{
				return usingRanged;
		}

		/**
		 * Change the state of usingRange.
		 * 
		 * @param state
		 *        The state of usingRange.
		 */
		public void setUsingRanged(boolean state)
		{
				usingRanged = state;
		}

		/**
		 * True if the player is a Normal player.
		 */
		public boolean isNormalRank()
		{
				return playerRights == 0;
		}

		/**
		 * True if the player is a Moderator.
		 */
		public boolean isModeratorRank()
		{
				return playerRights == 1 || playerRights == 2;
		}

		/**
		 * True if the player is a Support.
		 */
		public boolean isSupport()
		{
				return playerRights == 10;
		}

		/**
		 * True if the player is an Administrator.
		 */
		public boolean isAdministratorRank()
		{
				return playerRights == 2;
		}

		/**
		 * Has the player finished logging in?
		 */
		public boolean loggingInFinished;

		/**
		 * True if the player is doing an action.
		 *
		 */
		public boolean doingAction()
		{
				if (doingActionTimer > 0)
				{
						return true;
				}
				return false;
		}

		/**
		 * if 1 or more, the player cannot do anything..
		 */
		public int doingActionTimer;

		/**
		 * Is the doingActionEvent being used?
		 */
		public boolean isUsingDoingActionEvent;

		/**
		 * True, if the player has finished the new player tutorial.
		 */
		private boolean tutorialComplete;

		/**
		 * Store the time of when this player has attacked another player.
		 */
		public long timeAttackedAnotherPlayer;

		public long getTimeAttackedAnotherPlayer()
		{
				return timeAttackedAnotherPlayer;
		}

		public void setTimeAttackedAnotherPlayer(long value)
		{
				timeAttackedAnotherPlayer = value;
		}

		/**
		 * Store the time of when this player has been under attack by another player.
		 */
		public long timeUnderAttackByAnotherPlayer;

		public long getTimeUnderAttackByAnotherPlayer()
		{
				return timeUnderAttackByAnotherPlayer;
		}

		public void setTimeUnderAttackByAnotherPlayer(long value)
		{
				timeUnderAttackByAnotherPlayer = value;
		}

		/**
		 * Used for log out timer, this only resets upon death.
		 */
		public long timeNpcAttackedPlayerLogOutTimer;

		/**
		 * Store the time of when the NPC last attacked the player.
		 */
		public long timeNpcAttackedPlayer;

		public long getTimeNpcAttackedPlayer()
		{
				return timeNpcAttackedPlayer;
		}

		public void setTimeNpcAttackedPlayer(long value)
		{
				timeNpcAttackedPlayer = value;
		}

		/**
		 * The players i have recently attacked. Used for skulling.
		 */
		public ArrayList<Integer> attackedPlayers = new ArrayList<Integer>();

		/**
		 * The poison damage to appear.
		 */
		public int poisonDamage;

		/**
		 * The amount of poison hitsplats left of the current poisonDamage.
		 */
		public int poisonHitsplatsLeft;

		/**
		 * Amount of game ticks left untill the next poison hitsplat damage.
		 */
		public int poisonTicksUntillDamage;

		/**
		 * Amount of achievement points.
		 */
		public int achievementPoint;

		/**
		 * Amount of achievement points history.
		 */
		public int achievementPointHistory;

		/**
		 * Amount of times the player died in a dangerous area.
		 */
		public int wildernessDeaths;

		/**
		 * Change wildernessDeaths integer.
		 * 
		 * @param value
		 *        The value to set the wildernessDeaths integer to.
		 */
		public void setWildernessDeaths(int value)
		{
				wildernessDeaths = value;
		}

		/**
		 * @return The wildernessDeaths integer value.
		 */
		public int getWildernessDeaths()
		{
				return wildernessDeaths;
		}

		/**
		 * Amount of players killed in a dangeorus area.
		 */
		public int wildernessKills;

		/**
		 * Change wildernessKills integer.
		 * 
		 * @param value
		 *        The value to set the wildernessKills integer to.
		 */
		public void setWildernessKills(int value)
		{
				wildernessKills = value;
		}

		/**
		 * @return The wildernessKills integer value.
		 */
		public int getWildernessKills()
		{
				return wildernessKills;
		}

		/**
		 * Total level.
		 */
		private int totalLevel;

		/**
		 * Total experience in all skills.
		 */
		private long xpTotal;

		/**
		 * Quick prayers of normal prayers.
		 */
		public boolean[] quickPrayers = new boolean[QuickPrayers.MAX_PRAYERS];

		/**
		 * True if quick prayers of normal prayers are active.
		 */
		public boolean quickPray;

		/**
		 * The part of the Completionist cape that is currently being changed.
		 */
		public String partOfCape = "";

		/**
		 * The top detail colour of the Completionist cape.
		 */
		public int compColor1 = -1364;

		/**
		 * The top colour of the Completionist cape.
		 */
		public int compColor2 = -1364;

		/**
		 * The bottom detail colour of the Completionist cape.
		 */
		public int compColor3 = -1364;

		/**
		 * The bottom colour of the Completionist cape.
		 */
		public int compColor4 = -1364;

		/**
		 * The identity of the latest attacker (other player) that attacked this player.
		 * This is only reset when the player dies.
		 */
		private int lastAttackedBy;

		/**
		 * The identity of an NPC, in order of which NPC was spawned first. So, 
		 * the first NPC spawned in spawns.cfg gets the value +1. When an NPC is 
		 * created by summoning a pet for example, it gets the next unoccupied lowest number. 
		 * This integer is used to identify the exact NPC the player clicked on and once 
		 * all the data is put to use by knowing the exact NPC the player clicked on, the value is returned to 0.
		 */
		private int npcClickIndex;

		private int animationRequest = -1;

		public int getAnimationRequest()
		{
				return animationRequest;
		}

		public void setAnimationRequest(int animationRequest)
		{
				this.animationRequest = animationRequest;
		}

		private int FocusPointX = -1;

		public int getFocusPointX()
		{
				return FocusPointX;
		}

		public void setFocusPointX(int focusPointX)
		{
				FocusPointX = focusPointX;
		}

		private int FocusPointY = -1;

		public int getFocusPointY()
		{
				return FocusPointY;
		}

		public void setFocusPointY(int focusPointY)
		{
				FocusPointY = focusPointY;
		}

		private boolean faceUpdateRequired;

		protected boolean isFaceUpdateRequired()
		{
				return faceUpdateRequired;
		}

		protected void setFaceUpdateRequired(boolean faceUpdateRequired)
		{
				this.faceUpdateRequired = faceUpdateRequired;
		}

		private int face = -1;

		public int getFace()
		{
				return face;
		}

		public void setFace(int face)
		{
				this.face = face;
		}

		private int duelStatus;

		/**
		 *  public final static int NOT_DUELING = 0;
		 *  <br>
		 public final static int IN_DUEL_INTERFACE = 1;
		 *  <br>
		 public final static int ON_FIRST_SCREEN_ACCEPTED = 2;
		 *  <br>
		 public final static int ON_SECOND_SCREEN = 3;
		 *  <br>
		 public final static int ON_SECOND_SCREEN_ACCEPTED = 4;
		 *  <br>
		 public final static int DUEL_STARTED = 5;
		 *  <br>
		 public final static int DUEL_WON = 6;
		 * @return
		 */
		public int getDuelStatus()
		{
				return duelStatus;
		}

		/**
		 *  public final static int NOT_DUELING = 0;
		 public final static int IN_DUEL_INTERFACE = 1;
		 public final static int ON_FIRST_SCREEN_ACCEPTED = 2;
		 public final static int ON_SECOND_SCREEN = 3;
		 public final static int ON_SECOND_SCREEN_ACCEPTED = 4;
		 public final static int DUEL_STARTED = 5;
		 * @return
		 */
		public void setDuelStatus(int duelStatus)
		{
				this.duelStatus = duelStatus;
		}

		public int getDuelCount()
		{
				return duelCount;
		}

		public void setDuelCount(int duelCount)
		{
				this.duelCount = duelCount;
		}

		/**
		 * True, if the player is casting magic. This is used to confirm that the player is magic following.
		 * <p>
		 * We cannot use isUsingMagic() because it turns to false after the hitsplat appears to stop the player from re-attacking.
		 */
		public boolean hasLastCastedMagic()
		{
				return lastCastedMagic;
		}

		/**
		 * True, if the player is casting magic. This is used to confirm that the player is magic following.
		 * <p>
		 * We cannot use isUsingMagic() because isUsingMagic() turns to false after the hitsplat appears to stop the player from re-attacking.
		 */
		public void setLastCastedMagic(boolean lastCastedMagic)
		{
				this.lastCastedMagic = lastCastedMagic;
		}

		public boolean isUsingMediumRangeRangedWeapon()
		{
				return usingMediumRangeRangedWeapon;
		}

		public void setUsingMediumRangeRangedWeapon(boolean state)
		{
				this.usingMediumRangeRangedWeapon = state;
		}

		public boolean isUsingShortRangeRangedWeapon()
		{
				return usingShortRangeRangedWeapon;
		}

		public void setUsingShortRangeRangedWeapon(boolean state)
		{
				this.usingShortRangeRangedWeapon = state;
		}

		/**
		 * @return True, if the player is following with melee.
		 */
		public boolean isMeleeFollow()
		{
				return meleeFollow;
		}

		/**
		 * True, if the player is following with melee.
		 */
		public void setMeleeFollow(boolean meleeFollow)
		{
				this.meleeFollow = meleeFollow;
		}

		public void setTransformed(int transformed)
		{
				this.transformed = transformed;
		}

		public void setTeleporting(boolean teleporting)
		{
				this.teleporting = teleporting;
		}

		public void setHeight(int height)
		{
				this.height = height;
		}

		public int getPlayerId()
		{
				return playerId;
		}

		public void setPlayerId(int playerId)
		{
				this.playerId = playerId;
		}

		public long getLastSnowBallThrowTime()
		{
				return lastSnowBallThrowTime;
		}

		public void setLastSnowBallThrowTime(long lastSnowBallThrowTime)
		{
				this.lastSnowBallThrowTime = lastSnowBallThrowTime;
		}

		public int getAttackTimer()
		{
				return attackTimer;
		}

		public void setAttackTimer(int attackTimer)
		{
				this.attackTimer = attackTimer;
		}

		public int getSpecEffect()
		{
				return specEffect;
		}

		public void setSpecEffect(int specEffect)
		{
				this.specEffect = specEffect;
		}

		public int getDroppedRangedWeaponUsed()
		{
				return droppedRangedWeaponUsed;
		}

		public void setDroppedRangedItemUsed(int rangeItemUsed)
		{
				this.droppedRangedWeaponUsed = rangeItemUsed;
		}

		public int getProjectileStage()
		{
				return projectileStage;
		}

		public void setProjectileStage(int projectileStage)
		{
				this.projectileStage = projectileStage;
		}

		public void setX(int playerX)
		{
				this.playerX = playerX;
		}

		public int setY(int playerY)
		{
				this.playerY = playerY;
				return playerY;
		}

		/**
		 * The time the player was frozen.
		 */
		public long timeFrozen = 0;

		/**
		 * The amount of time the player won't be able to move for.
		 * 8000 means 8 seconds.
		 */
		private long frozenLength = 0;

		public boolean isFrozen()
		{
				if (System.currentTimeMillis() - timeFrozen >= getFrozenLength())
				{
						return false;
				}
				return true;
		}

		/**
		 * True if the player can be frozen.
		 */
		public boolean canBeFrozen()
		{
				if (System.currentTimeMillis() - timeFrozen >= (getFrozenLength() + 3500))
				{
						return true;
				}
				return false;
		}

		public int getAutocastId()
		{
				return autocastId;
		}

		public void setAutocastId(int autocastId)
		{
				this.autocastId = autocastId;
		}

		public boolean isUsingSpecial()
		{
				return usingSpecial;
		}

		public void setUsingSpecial(boolean usingSpecial)
		{
				this.usingSpecial = usingSpecial;
		}

		public boolean isMagicBowSpecialAttack()
		{
				return magicBowSpecialAttack;
		}

		public void setMagicBowSpecialAttack(boolean magicBowSpecialAttack)
		{
				this.magicBowSpecialAttack = magicBowSpecialAttack;
		}

		public boolean isUsingDarkBowSpecialAttack()
		{
				return usingDarkBowSpecialAttack;
		}

		public void setUsingDarkBowSpecialAttack(boolean usingDarkBowSpecialAttack)
		{
				this.usingDarkBowSpecialAttack = usingDarkBowSpecialAttack;
		}

		/**
		 * Store the last ranged weapon used by the player. This is used to drop the arrow on the floor during the hitsplat.
		 * 
		 * @return The value of lastRangedWeaponUsed.
		 */
		public int getLastRangedWeaponUsed()
		{
				return lastRangedWeaponUsed;
		}

		/**
		 * Store the last ranged weapon used by the player. This is used to drop the arrow on the floor during the hitsplat.
		 * 
		 * @param value
		 *        The value to change lastRangedWeaponUsed into.
		 */
		public void setLastRangedWeaponUsed(int value)
		{
				this.lastRangedWeaponUsed = value;
		}

		public boolean isUsingDarkBowNormalAttack()
		{
				return usingDarkBowNormalAttack;
		}

		public void setUsingDarkBowNormalAttack(boolean usingDarkBowNormalAttack)
		{
				this.usingDarkBowNormalAttack = usingDarkBowNormalAttack;
		}

		public boolean isMagicSplash()
		{
				return magicSplash;
		}

		public void setMagicSplash(boolean magicSplash)
		{
				this.magicSplash = magicSplash;
		}

		public boolean isBarrageDelay()
		{
				return barrageDelay;
		}

		public void setBarrageDelay(boolean barrageDelay)
		{
				this.barrageDelay = barrageDelay;
		}

		public int getMagicDamage()
		{
				return magicDamage;
		}

		public int setMagicDamage(int magicDamage)
		{
				this.magicDamage = magicDamage;
				return magicDamage;
		}

		public int getUnderAttackBy()
		{
				return underAttackBy;
		}

		public void setUnderAttackBy(int underAttackBy)
		{
				this.underAttackBy = underAttackBy;
		}

		public int getLastAttackedBy()
		{
				return lastAttackedBy;
		}

		public void setLastAttackedBy(int lastAttackedBy)
		{
				this.lastAttackedBy = lastAttackedBy;
		}

		public int getHitDelay()
		{
				return hitDelay;
		}

		public void setHitDelay(int hitDelay)
		{
				this.hitDelay = hitDelay;
		}

		public int getOldPlayerIndex()
		{
				return oldPlayerIndex;
		}

		public void setOldPlayerIndex(int oldPlayerIndex)
		{
				this.oldPlayerIndex = oldPlayerIndex;
		}

		public int getCombatStyle()
		{
				return combatStyle;
		}

		public void setCombatStyle(int combatStyle)
		{
				this.combatStyle = combatStyle;
		}

		public int getOldSpellId()
		{
				return oldSpellId;
		}

		public void setOldSpellId(int oldSpellId)
		{
				this.oldSpellId = oldSpellId;
		}

		public boolean isMoving()
		{
				return isMoving;
		}

		public void setMoving(boolean isMoving)
		{
				this.isMoving = isMoving;
		}

		public boolean isOrb()
		{
				return orb;
		}

		public void setOrb(boolean orb)
		{
				this.orb = orb;
		}

		public int getDuelingWith()
		{
				return duelingWith;
		}

		public void setDuelingWith(int duelingWith)
		{
				this.duelingWith = duelingWith;
		}

		private boolean usingMagic;

		public boolean isUsingMagic()
		{
				return usingMagic;
		}

		public void setUsingMagic(boolean state)
		{
				usingMagic = state;
		}

		public int getSpellId()
		{
				return spellId;
		}

		public void setSpellId(int spellId)
		{
				this.spellId = spellId;
		}

		public int doAmount;

		public long lastFire;

		public long lastLockPick;

		public boolean playerIsFiremaking;

		public int privateChat;

		private int specEffect;

		public int specBarId;

		public int[] itemRequirement = new int[7];

		public int switches;

		public int skullTimer;

		public int votingPoints;

		public int nextDialogue;

		private int dialogueAction;

		public int randomCoffin;

		private int autocastId;

		public int barrageCount;

		private int autoRetaliate;

		public int getAutoRetaliate()
		{
				return autoRetaliate;
		}

		public void setAutoRetaliate(int autoRetaliate)
		{
				this.autoRetaliate = autoRetaliate;
		}

		public int xInterfaceId;

		public int xRemoveId;

		public int xRemoveSlot;

		public int coinsPile;

		public int magicAltar;

		public int bonusAttack;

		public int lastNpcAttackedIndex;

		public int bankWithdraw;

		public int destroyItem;

		public int npcId2;

		public int dragonFireShieldCharges;

		public int lastChatId = 1;

		private int clanId = -1;

		public int itemDestroyedId = -1;

		public boolean splitChat;

		public boolean usedGmaul;

		private boolean jailed;

		public boolean initialized;

		private boolean disconnected;

		public boolean rebuildNpcList;

		private boolean isActive;

		public boolean hasMultiSign;

		public boolean saveCharacter;

		public boolean mouseButton;

		public boolean chatEffects = true;

		public boolean adminAttack;

		public boolean acceptAid;

		private boolean usingDarkBowSpecialAttack;

		public boolean isUsingDeathInterface;

		public String bankPin = "";

		public int attempts = 3;

		public boolean hasEnteredPin;

		public String enteredPin = "";

		public boolean setPin;

		public String fullPin = "";

		private boolean orb;

		public int getObjectX()
		{
				return objectX;
		}

		public int getObjectY()
		{
				return objectY;
		}

		private boolean barrageDelay;

		public int freezeDelay;

		private int oldPlayerIndex;

		/**
		 * Store the last ranged weapon used by the player. This is used to drop the arrow on the floor during the hitsplat.
		 */
		private int lastRangedWeaponUsed;

		private int projectileStage;

		public String spellBook = "MODERN";

		public int teleGfx;

		public int teleEndGfx;

		public int teleEndAnimation;

		public int teleHeight;

		public int teleX;

		public int teleY;

		/**
		 * The last ranged weapon used that will be dropped on floor, such as knifes/arrows etc..
		 */
		private int droppedRangedWeaponUsed;

		public int killingNpcIndex;

		private int totalDamageDealt;

		private int oldNpcIndex;

		private int attackTimer;

		private int npcType;

		public int castingSpellId;

		public int oldSpellId;

		private int spellId;

		private int hitDelay;

		public int hitDelay2;

		public int bowSpecShot;

		private int clickNpcType;

		public int clickObjectType;

		private int objectId;

		private int objectX;

		private int objectY;

		public int objectXOffset;

		public int objectYOffset;

		public int objectDistance;

		public int itemPickedUpX, itemPickedUpY, itemPickedUpId;

		private boolean isMoving;

		public boolean walkingToItem;

		public boolean walkingToItemEvent;

		public boolean magicOnFloor;

		public int shopId;

		private int tradeStatus;

		private int tradeWith;

		public boolean ignoreTradeMessage;

		public boolean forcedChatUpdateRequired, inDuel, tradeAccepted, goodTrade;

		private boolean inTrade;

		public boolean tradeRequested;

		public boolean tradeResetNeeded;

		public boolean tradeConfirmed;

		public boolean tradeConfirmed2;

		public boolean canOffer;

		public boolean acceptTrade;

		public boolean acceptedTrade;

		public int attackAnim;

		public int animationWaitCycles;

		public int[] playerBonus = new int[12];

		public boolean runModeOn = true;

		public boolean takeAsNote;

		private int combatLevel;

		public boolean saveFile;

		public int playerAppearance[] = new int[13];

		public int tempItems[] = new int[ServerConstants.BANK_SIZE];

		public int tempItemsN[] = new int[ServerConstants.BANK_SIZE];

		public int tempItemsT[] = new int[ServerConstants.BANK_SIZE];

		public int tempItemsS[] = new int[ServerConstants.BANK_SIZE];

		public boolean[] invSlot = new boolean[28], equipSlot = new boolean[14];

		public long friends[] = new long[200];

		public long ignores[] = new long[200];

		private double specialAttackAmount = 10;

		private double specialAttackAccuracyMultiplier = 1;

		public double specDamage = 1.0;

		public double prayerPoint = 1.0;

		public int teleGrabItem;

		public int teleGrabX;

		public int teleGrabY;

		private int duelCount;

		/**
		 * The attacker(other player) identity that is attacking this player.
		 */
		private int underAttackBy;

		private int npcIndexAttackingPlayer;

		public int wildernessLevel;

		private int teleTimer;

		public int getTeleTimer()
		{
				return teleTimer;
		}

		public void setTeleTimer(int teleTimer)
		{
				this.teleTimer = teleTimer;
		}

		public long teleBlockEndTime;

		public int poisonDelay;

		public int vengTimer;

		public long lastPlayerMove;

		public long dfsDelay;

		public long lastVeng;

		public long teleGrabDelay;

		public long lastWebCut;

		public long alchDelay;

		public long reduceStat;

		public long buryDelay;

		public long foodDelay;

		public long potDelay;

		public long karambwanDelay;

		public long diceDelay;

		public boolean canChangeAppearance;

		public byte poisonMask = 0, duelForceChatCount = 4;

		public int reduceSpellId;

		public int headIcon = -1;

		public int duelTimer, duelTeleX, duelTeleY, duelSlot, duelSpaceReq, duelOption;

		private int duelingWith;

		public int headIconPk = -1, headIconHints;

		public boolean duelRequested;

		public boolean doubleHit;

		private boolean usingSpecial;

		private boolean usingShortRangeRangedWeapon;

		private boolean usingMediumRangeRangedWeapon;

		private int combatStyle = ServerConstants.AGGRESSIVE;

		public boolean[] duelRule = new boolean[22];

		public final int[] ARROWS = {882, 884, 886, 888, 890, 892, 4740, 11212, 9140, 9141, 4142, 9143, 9144, 9240, 9241, 9242, 9243, 9244, 9245, 15243, 9337, 9338, 9339, 9340, 9341, 9342};

		public long[] reduceSpellDelay = new long[6];

		public boolean[] canUseReducingSpell = {true, true, true, true, true, true};

		public long stopPrayerDelay;

		public boolean[] prayerActive = {false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false};

		public long[] timePrayerActivated = new long[prayerActive.length];

		public void setPrayerActive(int index, boolean state)
		{
				prayerActive[index] = state;
				PrayerBook.handlePrayerDrain(this);
		}

		public boolean prayerEvent;

		public boolean getCombatStyle(int type)
		{
				return getCombatStyle() == type;
		}

		private Stream outStream = null;

		public Stream getOutStream()
		{
				return outStream;
		}

		public String addressIp = "";

		/**
		 * The player's unique identity number.
		 */
		private int playerId = -1;

		public String getPlayerName()
		{
				return playerName;
		}

		public void setPlayerName(String string)
		{
				playerName = string;
		}

		private String playerName = null;

		public String playerPass = null;

		public int playerRights;

		public boolean forceX1ExperienceRate;

		public PlayerHandler handler = null;

		public int playerItems[] = new int[28];

		public int playerItemsN[] = new int[28];

		public int playerStandIndex = 0x328;

		public int playerTurnIndex = 0x337;

		public int playerWalkIndex = 0x333;

		public int playerTurn180Index = 0x334;

		public int playerTurn90CWIndex = 0x335;

		public int playerTurn90CCWIndex = 0x336;

		public int playerRunIndex = 0x338;

		public int[] playerEquipment = new int[14];

		public int getWieldedWeapon()
		{
				return playerEquipment[ServerConstants.WEAPON_SLOT];
		}

		public int[] playerEquipmentN = new int[14];

		/**
		 * This array will store the items of the current bank tab being viewed into this array.
		 */
		public int bankingItems[] = new int[ServerConstants.BANK_SIZE];

		public int bankingItemsN[] = new int[ServerConstants.BANK_SIZE];

		public int bankingTab = 0; // -1 = bank closed

		public boolean doNotSendTabs;

		public int bankItems[] = new int[ServerConstants.BANK_SIZE];

		public int bankItemsN[] = new int[ServerConstants.BANK_SIZE];

		public int bankItems1[] = new int[ServerConstants.BANK_SIZE];

		public int bankItems1N[] = new int[ServerConstants.BANK_SIZE];

		public int bankItems2[] = new int[ServerConstants.BANK_SIZE];

		public int bankItems2N[] = new int[ServerConstants.BANK_SIZE];

		public int bankItems3[] = new int[ServerConstants.BANK_SIZE];

		public int bankItems3N[] = new int[ServerConstants.BANK_SIZE];

		public int bankItems4[] = new int[ServerConstants.BANK_SIZE];

		public int bankItems4N[] = new int[ServerConstants.BANK_SIZE];

		public int bankItems5[] = new int[ServerConstants.BANK_SIZE];

		public int bankItems5N[] = new int[ServerConstants.BANK_SIZE];

		public int bankItems6[] = new int[ServerConstants.BANK_SIZE];

		public int bankItems6N[] = new int[ServerConstants.BANK_SIZE];

		public int bankItems7[] = new int[ServerConstants.BANK_SIZE];

		public int bankItems7N[] = new int[ServerConstants.BANK_SIZE];

		public int bankItems8[] = new int[ServerConstants.BANK_SIZE];

		public int bankItems8N[] = new int[ServerConstants.BANK_SIZE];

		public static final int maxPlayerListSize = ServerConstants.MAXIMUM_PLAYERS;

		public Player playerList[] = new Player[maxPlayerListSize];

		public int playerListSize = 0;

		public byte playerInListBitmap[] = new byte[(ServerConstants.MAXIMUM_PLAYERS + 7) >> 3];

		public static final int maxNPCListSize = NpcHandler.NPC_INDEX_OPEN_MAXIMUM;

		public Npc npcList[] = new Npc[maxNPCListSize];

		public int npcListSize = 0;

		public byte npcInListBitmap[] = new byte[(NpcHandler.NPC_INDEX_OPEN_MAXIMUM + 7) >> 3];

		public int mapRegionX, mapRegionY;

		private int playerX;

		private int playerY;

		public int currentX, currentY;

		private boolean updateRequired = true;

		public final int walkingQueueSize = 50;

		public int walkingQueueX[] = new int[walkingQueueSize], walkingQueueY[] = new int[walkingQueueSize];

		public int wQueueReadPtr = 0;

		public int wQueueWritePtr = 0;

		public boolean isRunning = true;

		public int teleportToX = -1, teleportToY = -1;

		public boolean didTeleport;

		public boolean mapRegionDidChange;

		public int dir1 = -1, dir2 = -1;

		/**
		 * The timer used for Completionist cape emote.
		 */
		public int dungTime = 16;

		public int DirectionCount = 0;

		public boolean appearanceUpdateRequired = true;

		public boolean appearanceUpdateRequired2 = true;

		public boolean isNpc;

		protected int hitDiff2;

		private int hitDiff = 0;

		public boolean cycleEventDamageRunning;

		protected boolean hitUpdateRequired2;

		private boolean hitUpdateRequired;

		protected static Stream playerProps;

		static
		{
				playerProps = new Stream(new byte[100]);
		}

		public int[][] barrowsNpcs = {
				{2030, 0}, //verac
				{2029, 0}, //toarg
				{2028, 0}, // karil
				{2027, 0}, // guthan
				{2026, 0}, // dharok
				{2025, 0}
						// ahrim
		};

		public int soakDamage, soakDamage2 = 0;

		public int[] damageTaken = new int[ServerConstants.MAXIMUM_PLAYERS];

		public int hitMask;

		public int hitIcon;

		public int hitMask2;

		public int hitIcon2;

		private boolean chatTextUpdateRequired;

		private byte chatText[] = new byte[4096];

		private byte chatTextSize = 0;

		private int chatTextColor = 0;

		private int chatTextEffects = 0;

		public String forcedText = "null";

		public int mask100var1 = 0;

		public int mask100var2 = 0;

		protected boolean mask100update;

		public int newWalkCmdX[] = new int[walkingQueueSize];

		public int newWalkCmdY[] = new int[walkingQueueSize];

		private int newWalkCmdSteps = 0;

		public boolean newWalkCmdIsRunning;

		public int travelBackX[] = new int[walkingQueueSize];

		public int travelBackY[] = new int[walkingQueueSize];

		public int numTravelBackSteps = 0;

		public void preProcessing()
		{
				setNewWalkCmdSteps(0);
		}

		public int getMapRegionX()
		{
				return mapRegionX;
		}

		public int getMapRegionY()
		{
				return mapRegionY;
		}

		public int getLocalX()
		{
				return getX() - 8 * getMapRegionX();
		}

		public int getLocalY()
		{
				return getY() - 8 * getMapRegionY();
		}

		/**
		 * The player's x-coordinate.
		 * 
		 * @return Player's x-cooridnate.
		 */
		public int getX()
		{
				return playerX;
		}

		/**
		 * The player's y-coordinate.
		 * 
		 * @return Player's y-cooridnate.
		 */
		public int getY()
		{
				return playerY;
		}

		/**
		 * Get the player's height level.
		 * 
		 * @return The height level.
		 */
		public int getHeight()
		{
				return height;
		}

		private int height;

		public int getHitDiff()
		{
				return hitDiff;
		}

		public void setHitUpdateRequired(boolean hitUpdateRequired)
		{
				this.hitUpdateRequired = hitUpdateRequired;
		}

		public boolean isHitUpdateRequired()
		{
				return hitUpdateRequired;
		}

		public void setAppearanceUpdateRequired(boolean appearanceUpdateRequired)
		{
				this.appearanceUpdateRequired = appearanceUpdateRequired;
		}

		public boolean isAppearanceUpdateRequired()
		{
				return appearanceUpdateRequired;
		}

		public void setAppearanceUpdateRequired2(boolean appearanceUpdateRequired2)
		{
				this.appearanceUpdateRequired2 = appearanceUpdateRequired2;
		}

		public boolean isAppearanceUpdateRequired2()
		{
				return appearanceUpdateRequired2;
		}

		public void setChatTextEffects(int chatTextEffects)
		{
				this.chatTextEffects = chatTextEffects;
		}

		public int getChatTextEffects()
		{
				return chatTextEffects;
		}

		public void setChatTextSize(byte chatTextSize)
		{
				this.chatTextSize = chatTextSize;
		}

		public byte getChatTextSize()
		{
				return chatTextSize;
		}

		public void setChatTextUpdateRequired(boolean chatTextUpdateRequired)
		{
				this.chatTextUpdateRequired = chatTextUpdateRequired;
		}

		public boolean isChatTextUpdateRequired()
		{
				return chatTextUpdateRequired;
		}

		public byte[] getChatText()
		{
				return chatText;
		}

		public void setChatTextColor(int chatTextColor)
		{
				this.chatTextColor = chatTextColor;
		}

		public int getChatTextColor()
		{
				return chatTextColor;
		}

		public int[] getNewWalkCmdX()
		{
				return newWalkCmdX;
		}

		public void setNewWalkCmdY(int newWalkCmdY[])
		{
				this.newWalkCmdY = newWalkCmdY;
		}

		public int[] getNewWalkCmdY()
		{
				return newWalkCmdY;
		}

		public void setNewWalkCmdIsRunning(boolean newWalkCmdIsRunning)
		{
				this.newWalkCmdIsRunning = newWalkCmdIsRunning;
		}

		public boolean isNewWalkCmdIsRunning()
		{
				return newWalkCmdIsRunning;
		}

		public void setInStreamDecryption(ISAACRandomGen inStreamDecryption)
		{
		}

		public void setOutStreamDecryption(ISAACRandomGen outStreamDecryption)
		{
		}

		public byte buffer[] = null;

		public int packetSize = 0, packetType = -1;

		public IoSession session;

		public IoSession getSession()
		{
				return session;
		}

		public Queue<Packet> queuedPackets = new LinkedList<Packet>();

		public Stream inStream = null;

		public Stream getInStream()
		{
				return inStream;
		}

		private ShopAssistant shopAssistant = new ShopAssistant(this);

		public ShopAssistant getShops()
		{
				return shopAssistant;
		}

		private TradeAndDuel tradeAndDuel = new TradeAndDuel(this);

		public TradeAndDuel getTradeAndDuel()
		{
				return tradeAndDuel;
		}

		public PlayerAssistant playerAssistant = new PlayerAssistant(this);

		public PlayerAssistant getPA()
		{
				return playerAssistant;
		}

		private DialogueHandler dialogueHandler = new DialogueHandler(this);

		public long timeUnderAttackByAnotherPlayerAchievement;

		public long timeAttackedAnotherPlayerAchievement;

		public boolean noClip;

		public int itemDestroyedSlot;

		public String lastDialogueOptionString = "";

		public boolean soundSent;

		public long lastSpammedSoundTime;

		public long timeSentFoodSound;

		public long timeSentDrinkSound;

		public boolean itemWorn;

		public long lastThieve;

		public long playerInNpcCombat;

		public long timeReloadedItems;

		public boolean armadylCrossBowSpecial;

		public boolean doingClickObjectType4Event;

		public DialogueHandler getDH()
		{
				return dialogueHandler;
		}

		public boolean isJailed()
		{
				return jailed;
		}

		public void setJailed(boolean jailed)
		{
				this.jailed = jailed;
		}

		public int getClickNpcType()
		{
				return clickNpcType;
		}

		public void setClickNpcType(int clickNpcType)
		{
				this.clickNpcType = clickNpcType;
		}

		public int getOldNpcIndex()
		{
				return oldNpcIndex;
		}

		public void setOldNpcIndex(int oldNpcIndex)
		{
				this.oldNpcIndex = oldNpcIndex;
		}

		public boolean isDisconnected()
		{
				return disconnected;
		}

		public void setDisconnected(boolean disconnected)
		{
				this.disconnected = disconnected;
		}

		public int getTimeOutCounter()
		{
				return timeOutCounter;
		}

		public void setTimeOutCounter(int timeOutCounter)
		{
				this.timeOutCounter = timeOutCounter;
		}

		public boolean isUpdateRequired()
		{
				return updateRequired;
		}

		public void setUpdateRequired(boolean updateRequired)
		{
				this.updateRequired = updateRequired;
		}

		public double getSpecialAttackAccuracyMultiplier()
		{
				return specialAttackAccuracyMultiplier;
		}

		public void setSpecialAttackAccuracyMultiplier(double specialAttackAccuracyMultiplier)
		{
				this.specialAttackAccuracyMultiplier = specialAttackAccuracyMultiplier;
		}

		public boolean isTutorialComplete()
		{
				return tutorialComplete;
		}

		public void setTutorialComplete(boolean tutorialComplete)
		{
				this.tutorialComplete = tutorialComplete;
		}

		public int getNewWalkCmdSteps()
		{
				return newWalkCmdSteps;
		}

		public int setNewWalkCmdSteps(int newWalkCmdSteps)
		{
				this.newWalkCmdSteps = newWalkCmdSteps;
				return newWalkCmdSteps;
		}

		public int getMeleeMainKills()
		{
				return meleeMainKills;
		}

		public void setMeleeMainKills(int meleeKills)
		{
				this.meleeMainKills = meleeKills;
		}

		public int getHybridKills()
		{
				return hybridKills;
		}

		public void setHybridKills(int hybridKills)
		{
				this.hybridKills = hybridKills;
		}

		public int getBerserkerPureKills()
		{
				return berserkerPureKills;
		}

		public void setBerserkerPureKills(int berserkerPureKills)
		{
				this.berserkerPureKills = berserkerPureKills;
		}

		public int getPureKills()
		{
				return pureKills;
		}

		public void setPureKills(int pureKills)
		{
				this.pureKills = pureKills;
		}

		public int getRangedTankKills()
		{
				return rangedTankKills;
		}

		public void setRangedTankKills(int rangedTankKills)
		{
				this.rangedTankKills = rangedTankKills;
		}

		public int getCombatLevel()
		{
				return combatLevel;
		}

		public void setCombatLevel(int combatLevel)
		{
				this.combatLevel = combatLevel;
		}

		public int getNpcType()
		{
				return npcType;
		}

		public void setNpcType(int npcType)
		{
				this.npcType = npcType;
		}

		public boolean isUsingFightCaves()
		{
				return usingFightCaves;
		}

		public void setUsingFightCaves(boolean isPreparingForFightCaves)
		{
				this.usingFightCaves = isPreparingForFightCaves;
		}

		public long getXpTotal()
		{
				return xpTotal;
		}

		public void setXpTotal(long xpTotal)
		{
				this.xpTotal = xpTotal;
		}

		public int getObjectId()
		{
				return objectId;
		}

		public int setObjectId(int objectId)
		{
				this.objectId = objectId;
				return objectId;
		}

		public int getAgilityCourseCompletedMessage()
		{
				return agilityCourseCompletedMessage;
		}

		public void setAgilityCourseCompletedMessage(int agilityCourseCompletedMessage)
		{
				this.agilityCourseCompletedMessage = agilityCourseCompletedMessage;
		}

		public String getAmountInterface()
		{
				return amountInterface;
		}

		public void setAmountInterface(String amountInterface)
		{
				this.amountInterface = amountInterface;
				this.xInterfaceId = 0; // Resetting it to stop it from also withdrawing from other interfaces like the bank.
		}

		public int getNpcClickIndex()
		{
				return npcClickIndex;
		}

		public void setNpcClickIndex(int npcClickIndex)
		{
				this.npcClickIndex = npcClickIndex;
		}

		public int setObjectX(int objectX)
		{
				this.objectX = objectX;
				return objectX;
		}

		public int getMaximumDamageMagic()
		{
				return maximumDamageMagic;
		}

		public void setMaximumDamageMagic(int maximumDamageMagic)
		{
				this.maximumDamageMagic = maximumDamageMagic;
		}

		public int getTradeWith()
		{
				return tradeWith;
		}

		public void setTradeWith(int tradeWith)
		{
				this.tradeWith = tradeWith;
		}

		public boolean getAbleToEditCombat()
		{
				return ableToEditCombat;
		}

		public void setAbleToEditCombat(boolean canEditCombatStats)
		{
				this.ableToEditCombat = canEditCombatStats;
		}

		public boolean isUsingBankInterface()
		{
				return usingBankInterface;
		}

		public void setUsingBankInterface(boolean usingBankInterface)
		{
				this.usingBankInterface = usingBankInterface;
		}

		public int getTotalLevel()
		{
				return totalLevel;
		}

		public void setTotalLevel(int totalLevel)
		{
				this.totalLevel = totalLevel;
		}

		public int getBarrowsRunCompleted()
		{
				return barrowsRunCompleted;
		}

		public void setBarrowsRunCompleted(int barrowsRunCompleted)
		{
				this.barrowsRunCompleted = barrowsRunCompleted;
		}

		public int getClueScrollsCompleted()
		{
				return clueScrollsCompleted;
		}

		public void setClueScrollsCompleted(int clueScrollsCompleted)
		{
				this.clueScrollsCompleted = clueScrollsCompleted;
		}

		public int getDawntainPoints()
		{
				return dawntainPoints;
		}

		public void setDawntainPoints(int slayerPoints)
		{
				this.dawntainPoints = slayerPoints;
		}

		public void setOutStream(Stream outStream)
		{
				this.outStream = outStream;
		}

		public boolean isUsingBankSearch()
		{
				return usingBankSearch;
		}

		public void setUsingBankSearch(boolean usingBankSearch)
		{
				if (!usingBankSearch)
				{

						this.bankSearchString = "";
				}
				this.usingBankSearch = usingBankSearch;
		}



		public boolean processQueuedPackets()
		{
				Packet p = null;
				synchronized (queuedPackets)
				{
						p = queuedPackets.poll();
				}
				if (p == null)
				{
						return false;
				}

				// NOTE: if editing anything under this, must also update at ConnectionHandler class in method messageReceived for packet 41 instant item switching.
				inStream.currentOffset = 0;
				packetType = p.getId();
				packetSize = p.getLength();
				inStream.buffer = p.getData();
				setTimeOutCounter(0);
				// Uncomment if player is stuck logged in.
				//Misc.print("Player is active: " + getPlayerName() + ", Packet type: " + packetType);
				if (PacketHandler.showIndividualPackets)
				{
						Misc.print("Player is active: " + getPlayerName() + ", Packet type: " + packetType);
				}
				if (packetType > 0)
				{
						PacketHandler.processPacket(this, packetType, packetSize);
				}
				return true;
		}

		public void flushOutStream()
		{
				if (!this.canFlush)
				{
						return;
				}
				if (this.isBot)
				{
						return;
				}
				if (isDisconnected() || getOutStream().currentOffset == 0)
				{
						return;
				}
				StaticPacketBuilder out = new StaticPacketBuilder().setBare(true);
				byte[] temp = new byte[getOutStream().currentOffset];
				System.arraycopy(getOutStream().buffer, 0, temp, 0, temp.length);
				out.addBytes(temp);
				session.write(out.toPacket());
				getOutStream().currentOffset = 0;
		}

		public void update()
		{
				handler.updatePlayer(this, getOutStream());
				handler.updateNpc(this, getOutStream());

				canFlush = true;
				flushOutStream();
				canFlush = false;
		}

		public void queueMessage(Packet arg1)
		{
				synchronized (queuedPackets)
				{
						queuedPackets.add(arg1);
				}
		}

		public void clearUpdateFlags()
		{
				setUpdateRequired(false);
				setChatTextUpdateRequired(false);
				setAppearanceUpdateRequired(false);
				setHitUpdateRequired(false);
				hitUpdateRequired2 = false;
				forcedChatUpdateRequired = false;
				mask100update = false;
				forceMovementUpdate = false;
				setAnimationRequest(-1);
				resetPlayerTurn();
				poisonMask = -1;
				setFaceUpdateRequired(false);
				setFace(65535);
				if (getFaceResetAtEndOfTick())
				{
						resetFaceUpdate();
						setFaceResetAtEndOfTick(false);
				}
		}

		public void appendMask100Update(Stream str)
		{
				str.writeWordBigEndian(mask100var1);
				str.writeDWord(mask100var2);
		}

		/**
		 * Gfx will be launched from the middle of the body.
		 */
		public void gfx100(int gfx)
		{
				mask100var1 = gfx;
				mask100var2 = 6553600;
				mask100update = true;
				setUpdateRequired(true);
		}

		/**
		 * Gfx will be launched from bottom of the body.
		 * 
		 * @param gfx
		 */
		public void gfx0(int gfx)
		{
				mask100var1 = gfx;
				mask100var2 = 65536;
				mask100update = true;
				setUpdateRequired(true);
		}

		public void gfx(int gfx, int height)
		{
				mask100var1 = gfx;
				mask100var2 = 65536 * height;
				mask100update = true;
				setUpdateRequired(true);
		}

		/**
		 * Perform an animation.
		 *
		 * @param animId
		 *        The animation identity number.
		 */
		public void startAnimation(int animId)
		{
				if (this.getTransformed() > 0)
				{
						return;
				}
				setLastAnimation(animId);
				setAnimationRequest(animId);
				animationWaitCycles = 0;
				setUpdateRequired(true);
		}

		public void appendAnimationRequest(Stream str)
		{
				str.writeWordBigEndian((getAnimationRequest() == -1) ? 65535 : getAnimationRequest());
				str.writeByteC(animationWaitCycles);
		}

		/**
		 * Face Update
		 **/
		public void faceUpdate(int index)
		{
				if (this.dead)
				{
						resetFaceUpdate();
						return;
				}
				setFace(index);
				setFaceUpdateRequired(true);
				setUpdateRequired(true);
		}

		public void appendFaceUpdate(Stream str)
		{
				str.writeWordBigEndian(getFace());
		}

		public void resetPlayerTurn()
		{
				setFocusPointX(-1);
				setFocusPointY(-1);
				setUpdateRequired(true);
		}

		public void resetFaceUpdate()
		{
				setFace(-1);
				setFaceUpdateRequired(true);
				setUpdateRequired(true);
		}

		/**
		 * Turn the player's characterto face the given coordinates.
		 */
		public void turnPlayerTo(int pointX, int pointY)
		{
				if (this.getDead())
				{
						resetPlayerTurn();
						return;
				}
				setFocusPointX(2 * pointX + 1);
				setFocusPointY(2 * pointY + 1);
				setUpdateRequired(true);
		}

		private void appendSetFocusDestination(Stream str)
		{
				str.writeWordBigEndianA(getFocusPointX());
				str.writeWordBigEndian(getFocusPointY());
		}

		protected void appendHitUpdate(Stream str)
		{
				str.writeWordA(getHitDiff());
				str.writeByte(hitMask);
				str.writeByte(hitIcon);
				str.writeWordA(soakDamage);
				str.writeWordA(currentCombatSkillLevel[ServerConstants.HITPOINTS]);
				str.writeWordA(getBaseHitPointsLevel());
		}

		protected void appendHitUpdate2(Stream str)
		{
				str.writeWordA(hitDiff2);
				str.writeByte(hitMask2);
				str.writeByte(hitIcon2);
				str.writeWordA(soakDamage2);
				str.writeWordA(currentCombatSkillLevel[ServerConstants.HITPOINTS]);
				str.writeWordA(getBaseHitPointsLevel());
		}

		public void appendPlayerUpdateBlock(Stream str)
		{
				if (!isUpdateRequired() && !chatTextUpdateRequired)
				{
						return;
				}
				int updateMask = 0;
				if (forceMovementUpdate)
				{
						updateMask |= 0x400;
				}
				if (mask100update)
				{
						updateMask |= 0x100;
				}
				if (getAnimationRequest() != -1)
				{
						updateMask |= 8;
				}
				if (forcedChatUpdateRequired)
				{
						updateMask |= 4;
				}
				if (isChatTextUpdateRequired())
				{
						updateMask |= 0x80;
				}
				if (isAppearanceUpdateRequired())
				{
						updateMask |= 0x10;
				}
				if (isFaceUpdateRequired())
				{
						updateMask |= 1;
				}
				if (getFocusPointX() != -1)
				{
						updateMask |= 2;
				}
				if (isHitUpdateRequired())
				{
						updateMask |= 0x20;
				}

				if (hitUpdateRequired2)
				{
						updateMask |= 0x200;
				}

				if (updateMask >= 0x100)
				{
						updateMask |= 0x40;
						str.writeByte(updateMask & 0xFF);
						str.writeByte(updateMask >> 8);
				}
				else
				{
						str.writeByte(updateMask);
				}

				// now writing the various update blocks itself - note that their order crucial.

				if (forceMovementUpdate)
				{
						appendForceMovement(str);
				}
				if (mask100update)
				{
						appendMask100Update(str);
				}
				if (getAnimationRequest() != -1)
				{
						appendAnimationRequest(str);
				}
				if (forcedChatUpdateRequired)
				{
						appendForcedChat(str);
				}
				if (isChatTextUpdateRequired())
				{
						appendPlayerChatText(str);
				}
				if (isFaceUpdateRequired())
				{
						appendFaceUpdate(str);
				}
				if (isAppearanceUpdateRequired())
				{
						appendPlayerAppearance(str);
				}
				if (getFocusPointX() != -1)
				{
						appendSetFocusDestination(str);
				}
				if (isHitUpdateRequired())
				{
						appendHitUpdate(str);
				}
				if (hitUpdateRequired2)
				{
						appendHitUpdate2(str);
				}
		}

		public int forceMovementLocalXStart = -1;

		public int forceMovementLocalYStart = -1;

		public int forceMovementLocalXEnd = -1;

		public int forceMovementLocalYEnd = -1;

		public int forceMovementSpeedFromCurrentToStart = -1;

		public int forceMovementSpeedFromStartToEnd = -1;

		public int forceMovementPlayerFace = -1;

		/**
		 * The direction the player is facing depending on the last path walked.
		 */
		public int directionFacingPath = 8;

		public boolean teleportUpdateNeeded;

		public int frozenBy;

		public long cannotEatDelay;

		/**
		 * 
		 * @param x
		 * 	x tiles to move.
		 * @param y
		 * 	y tiles to move
		 * @param updateCoordinateTicks
		 * 	How many ticks later to update the player's coordinate.
		 */
		public void setForceMovement(int animation, int x, int y, int forceMovementSpeedFromCurrentToStart, int forceMovementSpeedFromStartToEnd, int forceMovementPlayerFace, int updateCoordinateTicks)
		{
				this.startAnimation(animation);
				this.forceMovementLocalXStart = getLocalX();
				this.forceMovementLocalYStart = getLocalY();
				this.forceMovementLocalXEnd = getLocalX() + x;
				this.forceMovementLocalYEnd = getLocalY() + y;
				this.forceMovementSpeedFromCurrentToStart = forceMovementSpeedFromCurrentToStart;
				this.forceMovementSpeedFromStartToEnd = forceMovementSpeedFromStartToEnd;
				this.forceMovementPlayerFace = forceMovementPlayerFace;
				this.forceMovementUpdate = true;
				this.updateRequired = true;
				this.forceNoClip = true;
				CycleEventHandler.getSingleton().addEvent(this, new CycleEvent()
				{
						@Override
						public void execute(CycleEventContainer container)
						{
								container.stop();
						}

						@Override
						public void stop()
						{
								getPA().movePlayer(getX() + x, getY() + y, getHeight());
								setDoingAgility(false);
						}
				}, updateCoordinateTicks);
		}

		public void appendForceMovement(Stream str)
		{
				str.writeByteS(forceMovementLocalXStart);
				str.writeByteS(forceMovementLocalYStart);
				str.writeByteS(forceMovementLocalXEnd);
				str.writeByteS(forceMovementLocalYEnd);
				str.writeWordBigEndianA(forceMovementSpeedFromCurrentToStart);
				str.writeWordA(forceMovementSpeedFromStartToEnd);
				str.writeByteS(forceMovementPlayerFace);
		}

		protected void appendPlayerChatText(Stream str)
		{
				str.writeWordBigEndian(((getChatTextColor() & 0xFF) << 8) + (getChatTextEffects() & 0xFF));
				str.writeByte(playerRights);
				str.writeByteC(getChatTextSize());
				str.writeBytes_reverse(getChatText(), getChatTextSize(), 0);
		}

		public void forcedChat(String text)
		{
				forcedText = text;
				forcedChatUpdateRequired = true;
				setUpdateRequired(true);
				setAppearanceUpdateRequired(true);
		}

		public void appendForcedChat(Stream str)
		{
				str.writeString(forcedText);
		}

		public void dealDamage(int damage)
		{
				if (getCurrentCombatSkillLevel(ServerConstants.HITPOINTS) - damage < 0)
				{
						damage = currentCombatSkillLevel[ServerConstants.HITPOINTS];
				}
				if (!this.getTank())
				{
						this.subtractFromHitPoints(damage);
				}
				BotContent.damaged(this);
				if (getCurrentCombatSkillLevel(ServerConstants.HITPOINTS) == 0 && !this.getDead())
				{
						Death.deathStage(this);
						return;
				}
				Combat.appendRedemption(this, damage);
				Effects.phoenixNecklace(this, damage);
				HitPointsRegeneration.startHitPointsRegeneration(this);
		}

		/**
		 * @param damage
		 * 			The damage
		 * @param hitSplatColour
		 * 			The colour of the hitsplat.
		 * @param icon
		 * 			The icon to show next to the hitsplat.
		 */
		public void handleHitMask(int damage, int hitSplatColour, int icon, int soak, boolean maxHit)
		{
				if (!hitUpdateRequired)
				{
						// If this event is not added, then the msb to dds spec to gmaul bug will appear.
						if (this.cycleEventDamageRunning && !hitUpdateRequired2)
						{
								hitDiff2 = damage;
								hitMask2 = maxHit ? 1 : hitSplatColour;
								hitIcon2 = icon;
								soakDamage2 = soak;
								hitUpdateRequired2 = true;
								setUpdateRequired(true);
						}
						else
						{
								hitDiff = damage;
								hitMask = maxHit ? 1 : hitSplatColour;
								hitIcon = icon;
								soakDamage = soak;
								hitUpdateRequired = true;
						}
				}
				else if (!hitUpdateRequired2)
				{

						hitDiff2 = damage;
						hitMask2 = maxHit ? 1 : hitSplatColour;
						hitIcon2 = icon;
						soakDamage2 = soak;
						hitUpdateRequired2 = true;
				}
				else
				{
						if (cycleEventDamageRunning)
						{
								CycleEventHandler.getSingleton().addEvent(this, new CycleEvent()
								{
										@Override
										public void execute(CycleEventContainer container)
										{
												container.stop();
										}

										@Override
										public void stop()
										{
												hitDiff2 = damage;
												hitMask2 = maxHit ? 1 : hitSplatColour;
												hitIcon2 = icon;
												soakDamage2 = soak;
												hitUpdateRequired2 = true;
												setUpdateRequired(true);
										}
								}, 1);
						}
						else
						{
								cycleEventDamageRunning = true;
								CycleEventHandler.getSingleton().addEvent(this, new CycleEvent()
								{

										@Override
										public void execute(CycleEventContainer container)
										{
												container.stop();
										}

										@Override
										public void stop()
										{
												hitDiff = damage;
												hitMask = maxHit ? 1 : hitSplatColour;
												hitIcon = icon;
												soakDamage = soak;
												hitUpdateRequired = true;
												setUpdateRequired(true);
												cycleEventDamageRunning = false;
										}

								}, 1);
						}
				}
				setUpdateRequired(true);
		}

		protected void appendPlayerAppearance(Stream str)
		{
				playerProps.currentOffset = 0;
				playerProps.writeByte(playerAppearance[0]);
				playerProps.writeByte(headIcon);
				playerProps.writeByte(headIconPk);
				playerProps.writeWord(compColor1);
				playerProps.writeWord(compColor2);
				playerProps.writeWord(compColor3);
				playerProps.writeWord(compColor4);
				playerProps.writeWord(playerAppearance[6]);
				if (npcId2 <= 0)
				{

						if (playerEquipment[ServerConstants.HEAD_SLOT] > 1)
						{
								playerProps.writeWord(0x200 + playerEquipment[ServerConstants.HEAD_SLOT]);
						}
						else
						{
								playerProps.writeByte(0);
						}

						if (playerEquipment[ServerConstants.CAPE_SLOT] > 1)
						{
								playerProps.writeWord(0x200 + playerEquipment[ServerConstants.CAPE_SLOT]);
						}
						else
						{
								playerProps.writeByte(0);
						}

						if (playerEquipment[ServerConstants.AMULET_SLOT] > 1)
						{
								playerProps.writeWord(0x200 + playerEquipment[ServerConstants.AMULET_SLOT]);
						}
						else
						{
								playerProps.writeByte(0);
						}

						if (getWieldedWeapon() > 1)
						{
								playerProps.writeWord(0x200 + getWieldedWeapon());
						}
						else
						{
								playerProps.writeByte(0);
						}

						if (playerEquipment[ServerConstants.BODY_SLOT] > 1)
						{
								playerProps.writeWord(0x200 + playerEquipment[ServerConstants.BODY_SLOT]);
						}
						else
						{
								playerProps.writeWord(0x100 + playerAppearance[2]);
						}

						if (playerEquipment[ServerConstants.SHIELD_SLOT] > 1)
						{
								playerProps.writeWord(0x200 + playerEquipment[ServerConstants.SHIELD_SLOT]);
						}
						else
						{
								playerProps.writeByte(0);
						}

						if (!Item.isFullBody(playerEquipment[ServerConstants.BODY_SLOT]))
						{
								playerProps.writeWord(0x100 + playerAppearance[3]);
						}
						else
						{
								playerProps.writeByte(0);
						}

						if (playerEquipment[ServerConstants.LEG_SLOT] > 1)
						{
								playerProps.writeWord(0x200 + playerEquipment[ServerConstants.LEG_SLOT]);
						}
						else
						{
								playerProps.writeWord(0x100 + playerAppearance[5]);
						}

						if (!Item.isNormalHelm(playerEquipment[ServerConstants.HEAD_SLOT]) && !Item.isFullMask(playerEquipment[ServerConstants.HEAD_SLOT]))
						{
								playerProps.writeWord(0x100 + playerAppearance[1]);
						}
						else
						{
								playerProps.writeByte(0);
						}
						if (playerEquipment[ServerConstants.HAND_SLOT] > 1)
						{
								playerProps.writeWord(0x200 + playerEquipment[ServerConstants.HAND_SLOT]);
						}
						else
						{
								playerProps.writeWord(0x100 + playerAppearance[4]);
						}
						if (playerEquipment[ServerConstants.FEET_SLOT] > 1)
						{
								playerProps.writeWord(0x200 + playerEquipment[ServerConstants.FEET_SLOT]);
						}
						else
						{
								playerProps.writeWord(0x100 + playerAppearance[6]);
						}
						if (playerAppearance[0] != 1 && !Item.isFullMask(playerEquipment[ServerConstants.HEAD_SLOT]))
						{
								playerProps.writeWord(0x100 + playerAppearance[7]);
						}
						else
						{
								playerProps.writeByte(0);
						}
				}
				else
				{
						playerProps.writeWord(-1);
						playerProps.writeWord(npcId2);
				}
				playerProps.writeByte(playerAppearance[8]);
				playerProps.writeByte(playerAppearance[9]);
				playerProps.writeByte(playerAppearance[10]);
				playerProps.writeByte(playerAppearance[11]);
				playerProps.writeByte(playerAppearance[12]);
				playerProps.writeWord(playerStandIndex); // standAnimIndex
				playerProps.writeWord(playerTurnIndex); // standTurnAnimIndex
				playerProps.writeWord(playerWalkIndex); // walkAnimIndex
				playerProps.writeWord(playerTurn180Index); // turn180AnimIndex
				playerProps.writeWord(playerTurn90CWIndex); // turn90CWAnimIndex
				playerProps.writeWord(playerTurn90CCWIndex); // turn90CCWAnimIndex
				playerProps.writeWord(playerRunIndex); // runAnimIndex
				playerProps.writeWord(currentCombatSkillLevel[ServerConstants.HITPOINTS]);
				playerProps.writeWord(getBaseHitPointsLevel());
				playerProps.writeString(getPlayerName());
				this.playerAssistant.calculateCombatLevel();
				playerProps.writeByte(getCombatLevel());
				playerProps.writeString(this.gameModeTitle);
				playerProps.writeString(this.playerTitle);
				playerProps.writeString(this.titleColour);
				playerProps.writeByte(this.titleSwap);
				playerProps.writeByte(this.playerRights);
				str.writeByteC(playerProps.currentOffset);
				str.writeBytes(playerProps.buffer, playerProps.currentOffset, 0);
		}

		public void updateThisPlayerMovement(Stream str)
		{
				if (this.isBot)
				{
						if (didTeleport)
						{
								return;
						}
						if (dir1 == -1)
						{

								setMoving(false);
						}
						else
						{
								if (dir2 == -1)
								{
										setMoving(true);
								}
								else
								{
										setMoving(true);
								}
						}
						return;
				}
				if (mapRegionDidChange)
				{
						str.createFrame(73);
						str.writeWordA(mapRegionX + 6);
						str.writeWord(mapRegionY + 6);
				}
				if (didTeleport)
				{
						str.createFrameVarSizeWord(81);
						str.initBitAccess();
						str.writeBits(1, 1);
						str.writeBits(2, 3);
						str.writeBits(2, getHeight());
						str.writeBits(1, 1);
						str.writeBits(1, (isUpdateRequired()) ? 1 : 0);
						str.writeBits(7, currentY);
						str.writeBits(7, currentX);
						return;
				}
				if (dir1 == -1)
				{
						// don't have to update the character position, because we're
						// just standing
						str.createFrameVarSizeWord(81);
						str.initBitAccess();
						setMoving(false);
						tempMoving = false;
						if (isUpdateRequired())
						{
								// tell client there's an update block appended at the end
								str.writeBits(1, 1);
								str.writeBits(2, 0);
						}
						else
						{
								str.writeBits(1, 0);
						}
				}
				else
				{
						str.createFrameVarSizeWord(81);
						str.initBitAccess();
						str.writeBits(1, 1);
						if (dir2 == -1)
						{
								setMoving(true);
								tempMoving = true;
								str.writeBits(2, 1);
								str.writeBits(3, Misc.xlateDirectionToClient[dir1]);
								if (isUpdateRequired())
										str.writeBits(1, 1);
								else
										str.writeBits(1, 0);
						}
						else
						{
								setMoving(true);
								tempMoving = true;
								str.writeBits(2, 2);
								str.writeBits(3, Misc.xlateDirectionToClient[dir1]);
								str.writeBits(3, Misc.xlateDirectionToClient[dir2]);
								if (isUpdateRequired())
										str.writeBits(1, 1);
								else
										str.writeBits(1, 0);
						}
				}
		}

		public void updatePlayerMovement(Stream str)
		{
				if (dir1 == -1)
				{
						if (isUpdateRequired() || isChatTextUpdateRequired())
						{
								str.writeBits(1, 1);
								str.writeBits(2, 0);
						}
						else
								str.writeBits(1, 0);
				}
				else if (dir2 == -1)
				{
						str.writeBits(1, 1);
						str.writeBits(2, 1);
						str.writeBits(3, Misc.xlateDirectionToClient[dir1]);
						str.writeBits(1, (isUpdateRequired() || isChatTextUpdateRequired()) ? 1 : 0);
				}
				else
				{
						str.writeBits(1, 1);
						str.writeBits(2, 2);
						str.writeBits(3, Misc.xlateDirectionToClient[dir1]);
						str.writeBits(3, Misc.xlateDirectionToClient[dir2]);
						str.writeBits(1, (isUpdateRequired() || isChatTextUpdateRequired()) ? 1 : 0);
				}
		}

		public void addNewNpc(Npc npc, Stream str, Stream updateBlock)
		{
				int id = npc.npcIndex;
				npcInListBitmap[id >> 3] |= 1 << (id & 7);
				npcList[npcListSize++] = npc;
				str.writeBits(14, id);
				int z = npc.getY() - getY();
				if (z < 0)
						z += 32;
				str.writeBits(5, z);
				z = npc.getX() - getX();
				if (z < 0)
						z += 32;
				str.writeBits(5, z);
				str.writeBits(1, 0);
				str.writeBits(18, npc.npcType);
				boolean savedUpdateRequired = npc.updateRequired;
				npc.updateRequired = true;
				npc.appendNpcUpdateBlock(updateBlock, this);
				npc.updateRequired = savedUpdateRequired;
				str.writeBits(1, 1);
		}

		public void addNewPlayer(Player plr, Stream str, Stream updateBlock)
		{
				if (playerListSize >= 255)
				{
						return;
				}
				int id = plr.getPlayerId();
				playerInListBitmap[id >> 3] |= 1 << (id & 7);
				playerList[playerListSize++] = plr;
				str.writeBits(11, id);
				str.writeBits(1, 1);
				boolean savedFlag = plr.isAppearanceUpdateRequired();
				boolean savedUpdateRequired = plr.isUpdateRequired();
				plr.setAppearanceUpdateRequired(true);
				plr.setUpdateRequired(true);
				plr.appendPlayerUpdateBlock(updateBlock);
				plr.setAppearanceUpdateRequired(savedFlag);
				plr.setUpdateRequired(savedUpdateRequired);
				str.writeBits(1, 1);
				int z = plr.getY() - getY();
				if (z < 0)
						z += 32;
				str.writeBits(5, z);
				z = plr.getX() - getX();
				if (z < 0)
						z += 32;
				str.writeBits(5, z);
		}

		public Player(int _playerId, boolean isBot)
		{
				setPlayerId(_playerId);

				this.isBot = isBot;

				playerRights = 0;
				for (int i = 0; i < playerItems.length; i++)
				{
						playerItems[i] = 0;
				}
				for (int i = 0; i < playerItemsN.length; i++)
				{
						playerItemsN[i] = 0;
				}
				for (int i = 0; i < baseSkillLevel.length; i++)
				{
						baseSkillLevel[i] = 1;
				}
				for (int i = 0; i < currentCombatSkillLevel.length; i++)
				{
						currentCombatSkillLevel[i] = 1;
				}
				for (int i = 0; i < skillExperience.length; i++)
				{
						skillExperience[i] = 0;
				}
				for (int i = 0; i < ServerConstants.BANK_SIZE; i++)
				{
						bankItems[i] = 0;
				}
				for (int i = 0; i < ServerConstants.BANK_SIZE; i++)
				{
						bankItemsN[i] = 0;
				}
				skillExperience[ServerConstants.HITPOINTS] = 1154;
				baseSkillLevel[ServerConstants.HITPOINTS] = 10;
				currentCombatSkillLevel[ServerConstants.HITPOINTS] = 10;
				playerAppearance[0] = 0; // gender
				playerAppearance[1] = 0; // head
				playerAppearance[2] = 18; // Torso
				playerAppearance[3] = 26; // arms
				playerAppearance[4] = 33; // hands
				playerAppearance[5] = 36; // legs
				playerAppearance[6] = 42; // feet
				playerAppearance[7] = 10; // beard
				playerAppearance[8] = 0; // hair colour
				playerAppearance[9] = 0; // torso colour
				playerAppearance[10] = 0; // legs colour
				playerAppearance[11] = 0; // feet colour
				playerAppearance[12] = 0; // skin colour
				playerEquipment[ServerConstants.HEAD_SLOT] = -1;
				playerEquipment[ServerConstants.CAPE_SLOT] = -1;
				playerEquipment[ServerConstants.AMULET_SLOT] = -1;
				playerEquipment[ServerConstants.BODY_SLOT] = -1;
				playerEquipment[ServerConstants.SHIELD_SLOT] = -1;
				playerEquipment[ServerConstants.LEG_SLOT] = -1;
				playerEquipment[ServerConstants.HAND_SLOT] = -1;
				playerEquipment[ServerConstants.FEET_SLOT] = -1;
				playerEquipment[ServerConstants.RING_SLOT] = -1;
				playerEquipment[ServerConstants.ARROW_SLOT] = -1;
				playerEquipment[ServerConstants.WEAPON_SLOT] = -1;
				setHeight(0);
				teleportToX = 3087; // Starter co-ord for new players
				teleportToY = 3517;
				setX(setY(-1));
				mapRegionX = mapRegionY = -1;
				currentX = currentY = 0;
				Movement.resetWalkingQueue(this);
		}

		/**
		 *
		 * True, if the player is doing an action.
		 * 
		 * @param player
		 *        The associated player.
		 */
		public boolean doingAnAction()
		{
				if (this.isBot)
				{
						return false;
				}
				if (this.playerIsFiremaking || this.doingAction() || this.getDoingAgility() || !this.isTutorialComplete() || this.isTeleporting() || this.isAnEgg || this.usingPreachingEvent)
				{
						return true;
				}
				if (this.dragonSpearEvent)
				{
						return true;
				}
				return false;
		}

		public String getCapitalizedName()
		{
				return Misc.capitalize(getPlayerName());
		}

		/**
		 * Decrease the doingAction variable untill it reaches 0.
		 *
		 * @param time
		 *        The amount of cycles the player will be doing an action.
		 */
		public void doingActionEvent(int time)
		{ /* Check if this event is being used, if it is, then stop */
				if (isUsingDoingActionEvent)
				{
						return;
				}
				playerAssistant.stopAllActions();
				isUsingDoingActionEvent = true;
				doingActionTimer = time;
				/*
									 * The event is continious untill doingAction reaches 0.
									 */
				CycleEventHandler.getSingleton().addEvent(this, new CycleEvent()
				{
						@Override
						public void execute(CycleEventContainer container)
						{
								if (doingActionTimer > 0)
								{
										doingActionTimer--;
								}
								if (doingActionTimer == 0)
								{
										container.stop();
								}
						}

						@Override
						public void stop()
						{
								isUsingDoingActionEvent = false;
						}
				}, 1);
		}

		/**
		 * @param amount
		 *        The amount to add to the Hitpoints of the player.
		 */
		public void addToHitPoints(int amount)
		{
				if (this.getDead())
				{
						return;
				}
				if (getCurrentCombatSkillLevel(ServerConstants.HITPOINTS) > getBaseHitPointsLevel())
				{
						return;
				}
				if (getCurrentCombatSkillLevel(ServerConstants.HITPOINTS) + amount > getBaseHitPointsLevel())
				{
						int extraAmount = (getCurrentCombatSkillLevel(ServerConstants.HITPOINTS) + amount) - getBaseHitPointsLevel();
						amount -= extraAmount;
				}
				currentCombatSkillLevel[ServerConstants.HITPOINTS] += amount;
				Skilling.updateSkillTabFrontTextMain(this, ServerConstants.HITPOINTS);
		}

		/**
		 * @param amount
		 *        The amount to subtract from the Hitpoints of the player.
		 */
		public void subtractFromHitPoints(int amount)
		{
				if (this.getDead())
				{
						return;
				}
				if (getCurrentCombatSkillLevel(ServerConstants.HITPOINTS) - amount < 0)
				{
						amount = getCurrentCombatSkillLevel(ServerConstants.HITPOINTS);
				}
				else
				{
						currentCombatSkillLevel[ServerConstants.HITPOINTS] -= amount;
				}
		}

		public int getNpcIndexAttackingPlayer()
		{
				return npcIndexAttackingPlayer;
		}

		public void setNpcIndexAttackingPlayer(int npcIndexAttackingPlayer)
		{
				this.npcIndexAttackingPlayer = npcIndexAttackingPlayer;
		}

		public String getBotStatus()
		{
				return botStatus;
		}

		public void setBotStatus(String botStatus)
		{
				this.botStatus = botStatus;
		}

		public int setObjectY(int objectY)
		{
				this.objectY = objectY;
				return objectY;
		}

		public boolean isActive()
		{
				return isActive;
		}

		public void setActive(boolean isActive)
		{
				this.isActive = isActive;
		}

		public double getSpecialAttackAmount()
		{
				return specialAttackAmount;
		}

		public void setSpecialAttackAmount(double specialAttackAmount, boolean startEvent)
		{
				this.specialAttackAmount = specialAttackAmount;
				if (startEvent)
				{
						Combat.restoreSpecialAttackEvent(this);
				}
		}

		public int getTotalDamageDealt()
		{
				return totalDamageDealt;
		}

		public void setTotalDamageDealt(int totalDamageDealt)
		{
				this.totalDamageDealt = totalDamageDealt;
		}

		public int getClanId()
		{
				return clanId;
		}

		public void setClanId(int clanId)
		{
				this.clanId = clanId;
		}

		public int getZombiePartnerId()
		{
				return zombiePartnerId;
		}

		public void setZombiePartnerId(int zombiePartnerId)
		{
				this.zombiePartnerId = zombiePartnerId;
		}

		public boolean isInZombiesMinigame()
		{
				return inZombiesMinigame;
		}

		public void setInZombiesMinigame(boolean inZombiesMinigame)
		{
				this.inZombiesMinigame = inZombiesMinigame;
		}

		public String getProfileNameSearched()
		{
				return profileNameSearched;
		}

		public void setProfileNameSearched(String profileNameSearched)
		{
				profileNameSearched = Misc.capitalize(profileNameSearched);
				this.profileNameSearched = profileNameSearched;
		}

		public int getProfileSearchOnlinePlayerId()
		{
				return profileSearchOnlinePlayerId;
		}

		public void setProfileSearchOnlinePlayerId(int profileSearchOnlinePlayerId)
		{
				this.profileSearchOnlinePlayerId = profileSearchOnlinePlayerId;
		}

		public boolean isBotActionApplied()
		{
				return botActionApplied;
		}

		public void setBotActionApplied(boolean botActionApplied)
		{
				this.botActionApplied = botActionApplied;
		}

		public long getFrozenLength()
		{
				return frozenLength;
		}

		public void setFrozenLength(long frozenLength)
		{
				if (frozenLength > 0)
				{
						timeFrozen = System.currentTimeMillis();
				}
				this.frozenLength = frozenLength;
		}

		public int getActionIdUsed()
		{
				return actionIdUsed;
		}

		public void setActionIdUsed(int teleportNpcId)
		{
				this.actionIdUsed = teleportNpcId;
		}

		public void resetActionIdUsed()
		{
				this.actionIdUsed = 0;
		}

		public boolean isInTrade()
		{
				return inTrade;
		}

		public void setInTrade(boolean inTrade)
		{
				this.inTrade = inTrade;
		}

		public int getPetId()
		{
				return petId;
		}

		public void setPetId(int petId)
		{
				this.petId = petId;
		}

		public int getTradeStatus()
		{
				return tradeStatus;
		}

		public void setTradeStatus(int tradeStatus)
		{
				this.tradeStatus = tradeStatus;
		}

		public int getDialogueAction()
		{
				return dialogueAction;
		}

		public void setDialogueAction(int dialogueAction)
		{
				this.dialogueAction = dialogueAction;
		}

}