package game.player;

import core.Server;
import core.ServerConfiguration;
import core.ServerConstants;
import game.bot.BotContent;
import game.content.bank.Bank;
import game.content.clanchat.DefaultClanChat;
import game.content.combat.Combat;
import game.content.combat.CombatInterface;
import game.content.combat.Poison;
import game.content.combat.vsplayer.magic.AutoCast;
import game.content.consumable.Potions;
import game.content.consumable.RegenerateSkill;
import game.content.highscores.HighscoresDaily;
import game.content.interfaces.AreaInterface;
import game.content.interfaces.InterfaceAssistant;
import game.content.minigame.barrows.Barrows;
import game.content.minigame.zombie.Zombie;
import game.content.miscellaneous.CommunityEvent;
import game.content.miscellaneous.GodWarsDungeonInterface;
import game.content.miscellaneous.PlayerGameTime;
import game.content.miscellaneous.QuestTab;
import game.content.miscellaneous.RunePouch;
import game.content.miscellaneous.Skull;
import game.content.miscellaneous.WelcomeMessage;
import game.content.miscellaneous.Wolpertinger;
import game.content.music.Music;
import game.content.prayer.PrayerBook;
import game.content.profile.ProfileRank;
import game.content.skilling.HitPointsRegeneration;
import game.content.skilling.Skilling;
import game.content.skilling.agility.AgilityAssistant;
import game.content.starter.NewPlayerContent;
import game.content.worldevent.Tournament;
import game.content.worldevent.WorldEvent;
import game.item.ItemAssistant;
import game.npc.pet.Pet;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;
import game.player.movement.Follow;
import game.player.punishment.Blacklist;
import game.player.punishment.IpMute;
import game.player.punishment.RagBan;
import network.connection.HostList;

/**
 * Update the game for the player that just logged in.
 * @author MGT Madness, created on 13-12-2013.
 */
public class LogInUpdate
{

		/**
		 * Update game for the player on login.
		 */
		public static void update(Player player)
		{
				updateBeforeNotice(player);
				if (!player.isBot)
				{
						player.getOutStream().createFrame(249);
						player.getOutStream().writeByteA(1);
						player.getOutStream().writeWordBigEndianA(player.getPlayerId());
				}
				player.saveCharacter = true;
				NewPlayerContent.giveStarterPackage(player);
				Skilling.updateAllSkillTabFrontText(player);
				interfaceFramesUpdate(player);
				PrayerBook.resetAllPrayerGlows(player);
				player.getPA().sendFrame107(); // Reset screen
				player.getPA().setChatOptions(0, 0, 0); // Reset private messaging options
				InterfaceAssistant.splitPrivateChat(player);
				InterfaceAssistant.showTabs(player);
				AutoCast.resetAutocast(player);
				Combat.resetPrayers(player);
				player.getPA().showOption(4, 0, "Follow", 4);
				player.getPA().showOption(5, 0, "Trade With", 3);
				ItemAssistant.resetItems(player, 3214);//this is the former initialize method in the client class
				ItemAssistant.calculateEquipmentBonuses(player);
				ItemAssistant.updateEquipmentInterface(player);
				player.getPA().logIntoPM();
				CombatInterface.addSpecialBar(player, player.getWieldedWeapon());
				Follow.resetFollow(player);
				player.getPA().sendFrame36(172, player.getAutoRetaliate());
				Skull.startSkullTimerEvent(player);
				Pet.ownerLoggedIn(player);
				player.getPA().sendFrame214();
				PlayerGameTime.startMilliSecondsOnline(player);
				WelcomeMessage.sendWelcomeMessage(player);
				DefaultClanChat.createDefaultClanChat(player);
				Combat.restoreSpecialAttackEvent(player);
				Combat.updatePlayerStance(player); // Added here to update the player's client.
				player.handler.updatePlayer(player, player.getOutStream());
				player.handler.updateNpc(player, player.getOutStream());
				player.flushOutStream();
				Server.objectManager.changeRegionPacketClientObjectUpdate(player, false);
				InterfaceAssistant.updateCombatLevel(player); // Must be after updatePlayer.
				ItemAssistant.updateEquipmentLogIn(player);
				Skilling.updateTotalLevel(player);
				ItemAssistant.updateEquipmentBonusInterface(player); // Must be after updatePlayer.
				Combat.updatePlayerStance(player); // Added here to update for other players.
				player.setAppearanceUpdateRequired(true);
				Potions.overloadReBoostEvent(player);
				RegenerateSkill.logInUpdate(player);
				Poison.appendPoison(null, player, true, 0);
				Potions.antiFirePotionEvent(player);
				Skilling.updateTotalSkillExperience(player, Skilling.getExperienceTotal(player));
				HitPointsRegeneration.startHitPointsRegeneration(player);
				updateAppearanceForOtherPlayers(player);
				ProfileRank.saveCurrentRanks(player);
				Skilling.sendXpToDisplay(player);
				BotContent.gearUp(player, true);
				Bank.updateClientLastXAmount(player, player.lastXAmount);
				Zombie.logInUpdate(player);
				AgilityAssistant.agilityGain(player);
				RunePouch.updateRunePouchMainStorage(player, true);
				HighscoresDaily.getInstance().informHighscores(player);
				WorldEvent.logInUpdate(player);
				Tournament.logOutUpdate(player, true);
				CommunityEvent.checkOnLogIn(player);
				IpMute.ipMuteLogInUpdate(player);
				RagBan.loggedIn(player);
				player.getPA().alertNotSameIp();
				player.getPA().saveGameEvent();
				player.getPA().loopedSave();
				player.getPA().sendFrame126("Aby whip & Dds only", 669);
				player.loggingInFinished = true;
		}

		private static void connectionCheck(Player player)
		{

				if (player.isBot)
				{
						return;
				}
				if (HostList.countConnections(player.addressIp) > (ServerConfiguration.DEBUG_MODE ? 2000 : ServerConstants.IPS_ALLOWED))
				{
						player.setDisconnected(true);
						player.setTimeOutCounter(ServerConstants.TIMEOUT + 1);
						player.hasTooManyConnections = true;
				}
				else
				{
						HostList.connections.add(player.addressIp);
				}
		}

		private static void updateAppearanceForOtherPlayers(final Player player)
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
								player.setAppearanceUpdateRequired(true);
						}
				}, 1);
		}

		private static void interfaceFramesUpdate(Player player)
		{
				QuestTab.updateQuestTab(player);
				Skilling.updateSkillTabExperienceHover(player, 0, true);
		}

		/**
		 * Update certain parts of the game for the player, before the player can see the update happening.
		 * @param player
		 * 			The assosciated player.
		 */
		private static void updateBeforeNotice(Player player)
		{
				connectionCheck(player);
				Blacklist.checkIfClientHasBlacklistFile(player);
				player.playerAssistant.sendMessage(":rights" + player.playerRights + ":"); // Tell client the player rights, to use on player name chat area crown.
				NewPlayerContent.logIn(player);
				player.getPA().sendFrame36(173, player.runModeOn ? 1 : 0); // Inform the client that running is on.
				InterfaceAssistant.informClientRestingState(player, "off");
				AgilityAssistant.updateRunEnergyInterface(player);
				AreaInterface.updateWalkableInterfaces(player);
				Wolpertinger.updateSummoningOrb(player);
				player.getTradeAndDuel().removeFromArena();
				Music.requestMusicStateFromClient(player);
				Barrows.updateSavedBarrowsProgress(player);
				PrayerBook.updateRigourAndAugury(player);
				Barrows.updateBarrowsInterface(player);
				GodWarsDungeonInterface.updateGwdInterface(player);
				player.getPA().updateDisplayBots();
		}

}