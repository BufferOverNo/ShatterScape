package game.content.wildernessbonus;

import java.util.ArrayList;

import core.ServerConfiguration;
import game.content.achievement.AchievementStatistics;
import game.content.achievement.Achievements;
import game.content.achievement.PlayerTitle;
import game.content.combat.CombatInterface;
import game.content.highscores.HighscoresDaily;
import game.content.highscores.HighscoresF2p;
import game.content.highscores.HighscoresHallOfFame;
import game.content.miscellaneous.Artefacts;
import game.content.miscellaneous.QuestTab;
import game.content.quicksetup.QuickSetUp;
import game.content.starter.GameMode;
import game.content.worldevent.WorldEvent;
import game.item.ItemAssistant;
import game.player.Area;
import game.player.Player;
import utility.Misc;

/**
 * Handle the bonus loot that depends on EP and or Target kill.
 * @author MGT Madness
 */

public class KillReward
{

		/**
		 * Drop the loot for the player depending on their EP and or target kill.
		 */
		public static void giveLoot(Player attacker, Player victim)
		{
				if (!requirements(attacker, victim))
				{
						killLog.add("No reward: [" + Misc.getDate() + "] =" + attacker.getCapitalizedName() + "= killed " + victim.getCapitalizedName() + " with " + victim.damageTaken[attacker.getPlayerId()] + " damage at " + attacker.getX() + ", " + attacker.getY() + ", " + attacker.getHeight());
						return;
				}
				killBonus(attacker, victim);
		}

		/**
		 * @param victim
		 * 			The player who died.
		 * @return
		 * 			True, if the attacker and victim have the requirements to continue.
		 */
		private static boolean requirements(Player attacker, Player victim)
		{

				if (ServerConfiguration.DEBUG_MODE)
				{
						return true;
				}
				if (victim.getLastAttackedBy() == victim.getPlayerId())
				{
						return false;
				}

				if (!ItemAssistant.hasEquipment(attacker) || !ItemAssistant.hasEquipment(victim))
				{
						return false;
				}
				if (attacker.addressIp.equals(victim.addressIp))
				{
						return false;
				}
				if (System.currentTimeMillis() - attacker.lastKillTime <= 90000)
				{
						return false;
				}

				return true;
		}


		public static ArrayList<String> killLog = new ArrayList<String>();

		/**
		 * Reward player for getting a kill
		 * @param victim
		 * 			The player who died.
		 */
		private static void killBonus(Player killer, Player victim)
		{
				if (!victim.isCombatBot() && killer != null)
				{
						killLog.add("Reward: [" + Misc.getDate() + "] =" + killer.getCapitalizedName() + "= killed " + victim.getCapitalizedName() + " with " + victim.damageTaken[killer.getPlayerId()] + " damage at " + killer.getX() + ", " + killer.getY() + ", " + killer.getHeight());
						updateKillStreak(killer, victim);
						PlayerTitle.checkCompletionMultiple(killer, "76 77 78 79", "");
						victim.setWildernessDeaths(victim.getWildernessDeaths() + 1);
						killer.setWildernessKills(killer.getWildernessKills() + 1);
						AchievementStatistics.updateAchievementStatistics(killer, true, victim);
						AchievementStatistics.updateAchievementStatistics(victim, false, null);
						Achievements.checkCompletionMultiple(killer, "76 77 78 79");
						Artefacts.dropArtefacts(killer, victim);
						Achievements.checkCompletionMultiple(killer, "1065 1123");
						killer.lastKillTime = System.currentTimeMillis();
						if (killer.getWildernessKills() == 300)
						{
								HighscoresHallOfFame.enterToHallOfFame(killer, "Kill 300 Players");
						}
						if (Area.inMulti(victim.getX(), victim.getY()))
						{
								killer.killsInMulti++;
						}
						boolean isF2p = QuickSetUp.isUsingF2pOnly(killer, false, true);
						if (isF2p)
						{
								victim.deathTypes[5]++;
								killer.f2pKills++;
								HighscoresF2p.getInstance().sortHighscores(killer);
								HighscoresF2p.getInstance().sortHighscores(victim);
						}
						if (WorldEvent.getActiveEvent("F2P PK"))
						{
								if (isF2p)
								{
										Artefacts.dropArtefactsAmount(killer, victim, 3);
								}
						}
						if (HighscoresDaily.getInstance().currentDailyHighscores.equals("F2p kills"))
						{
								if (isF2p)
								{
										HighscoresDaily.getInstance().sortHighscores(killer, 1, killer.f2pKills, QuestTab.getKDR(killer.f2pKills, killer.deathTypes[5]) + "");
								}
						}
				}
				else if (killer != null)
				{
						killedBotBonus(killer, victim);
				}
		}

		public final static int playerRarity = 105;

		public final static int botMultiplier = 3;

		private static void killedBotBonus(Player killer, Player victim)
		{
				killer.playerBotCurrentKillstreak++;
				killer.playerBotKills++;
				if (killer.playerBotCurrentKillstreak > killer.playerBotHighestKillstreak)
				{
						killer.playerBotHighestKillstreak = killer.playerBotCurrentKillstreak;
				}
				Achievements.checkCompletionSingle(killer, 1006);
				Achievements.checkCompletionMultiple(killer, "1032 1122");
				PlayerTitle.checkCompletionMultiple(killer, "72 73 74 75", "");
				killer.setSpecialAttackAmount(10.0, false);
				Artefacts.dropArtefacts(killer, victim);
				CombatInterface.addSpecialBar(killer, killer.getWieldedWeapon());
				if (GameMode.getGameMode(killer, "IRON MAN"))
				{
						Achievements.checkCompletionSingle(killer, 1053);
				}
		}


		/**
		 * Update kill streak statistics of the player.
		 * @param killer
		 * 			The player who got the kill.
		 * @param victim
		 * 			The player who died.
		 */
		private static void updateKillStreak(Player killer, Player victim)
		{
				victim.currentKillStreak = 0;
				victim.playerBotCurrentKillstreak = 0;
				killer.currentKillStreak++;
				if (killer.currentKillStreak > killer.killStreaksRecord && killer.currentKillStreak > 1)
				{
						killer.killStreaksRecord = killer.currentKillStreak;
						killer.playerAssistant.sendMessage("You have set a new kill streak of " + killer.killStreaksRecord + "!");
				}
		}

}