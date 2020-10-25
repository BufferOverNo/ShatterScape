package game.npc;

import java.util.ArrayList;

import core.ServerConstants;
import game.content.combat.Combat;
import game.content.combat.Poison;
import game.content.combat.vsnpc.CombatNpc;
import game.content.combat.vsplayer.Effects;
import game.content.combat.vsplayer.magic.MagicFormula;
import game.content.combat.vsplayer.melee.MeleeFormula;
import game.content.combat.vsplayer.range.RangedFormula;
import game.content.minigame.WarriorsGuild;
import game.content.minigame.barrows.Barrows;
import game.content.minigame.zombie.Zombie;
import game.content.miscellaneous.FightCaves;
import game.content.miscellaneous.GameTimeSpent;
import game.content.miscellaneous.GodWarsDungeonInterface;
import game.content.miscellaneous.SpecialAttackTracker;
import game.content.music.SoundSystem;
import game.content.profile.NpcKillTracker;
import game.content.skilling.Skilling;
import game.content.skilling.Slayer;
import game.content.skilling.thieving.PickPocket;
import game.item.ItemAssistant;
import game.log.GameTickLog;
import game.npc.data.NpcDefinition;
import game.npc.data.NpcDefinitionCleverJSON;
import game.npc.data.NpcDefinitionCombatJSON;
import game.npc.data.NpcDefinitionNonCombatJSON;
import game.npc.data.NpcSpawnBossJSON;
import game.npc.data.NpcSpawnCombatJSON;
import game.npc.data.NpcSpawnNonCombatJSON;
import game.npc.pet.BossPetDrops;
import game.npc.pet.Pet;
import game.object.clip.Region;
import game.player.Area;
import game.player.Player;
import game.player.PlayerHandler;
import game.player.movement.Movement;
import utility.Misc;

public class NpcHandler
{
		/**
		 *  Last checked, i have 862 npc spawned on server start up.
		 *  I also need to take in account pets spawned and barrows brothers spawned, Jad minigame, Zombies minigame.
		 */
		public final static int NPC_INDEX_OPEN_MAXIMUM = 1500;

		public static Npc npcs[] = new Npc[NPC_INDEX_OPEN_MAXIMUM];

		public static int random;

		/**
		 * True, if the 'Teleporter' NPC is performing an emote/action.
		 */
		public boolean teleporterInAction;

		private int[] rarelyWalkNpcs = {2234 // Banker at edgeville.
		};


		private boolean rarelyWalkNpcs(int npcType)
		{
				for (int i = 0; i < rarelyWalkNpcs.length; i++)
				{
						if (npcType == rarelyWalkNpcs[i])
						{
								return Misc.hasPercentageChance(5);
						}
				}
				for (PickPocket.PickPocketData data : PickPocket.PickPocketData.values())
				{
						if (npcType == data.getNpcId())
						{
								return Misc.hasPercentageChance(5);
						}
				}
				if (Misc.hasPercentageChance(30))
				{
						return true;
				}
				return false;
		}

		// How long the delay is then the damage hitsplat appears
		public int getHitDelay(Npc npc)
		{

				switch (npc.attackType)
				{
						case ServerConstants.MAGIC_ICON:
								if (NpcDefinition.getDefinitions()[npc.npcType].magicHitsplatDelay == 0)
								{
										return 4;
								}
								return NpcDefinition.getDefinitions()[npc.npcType].magicHitsplatDelay;
						case ServerConstants.MELEE_ICON:

								if (NpcDefinition.getDefinitions()[npc.npcType].meleeHitsplatDelay == 0)
								{
										return 2;
								}
								return NpcDefinition.getDefinitions()[npc.npcType].meleeHitsplatDelay;
						case ServerConstants.RANGED_ICON:
								if (NpcDefinition.getDefinitions()[npc.npcType].rangedHitsplatDelay == 0)
								{
										return 4;
								}
								return NpcDefinition.getDefinitions()[npc.npcType].rangedHitsplatDelay;
				}
				return 2;
		}

		// / Fight caves Npc
		public boolean isFightCaveNpc(Npc npc)
		{
				switch (npc.npcType)
				{
						case 2745:
								// TzTok-Jad
								return true;
				}
				return false;

		}

		/**
		 * Npc cannot attack with melee if added here to attack with melee . Npc will hit a max of 1 with melee unless i add it into maxhit
		 **/
		public boolean multiAttacks(Npc npc)
		{
				switch (npc.npcType)
				{
						case 3200:
								// Chaos Elemental
						case 5666: // Barrelchest
						case 8133: // Corporeal beast.
						case 4043: // Venenatis.
						case 6222:// Kree'arra.
						case 6247: // Commander Zilyana.
								if (npc.attackType == ServerConstants.MAGIC_ICON)
								{
										return true;
								}
								break;

						case 6260: // General Graardor.
								if (npc.attackType == ServerConstants.RANGED_ICON)
								{
										return true;
								}
								break;

				}
				return false;

		}

		public void loadSpell(Npc npc)
		{
				Player player = PlayerHandler.players[npc.getKillerId()];
				Npc n = npc;
				npc.bottomGfx = false;
				npc.projectileId = 0;
				npc.hitThroughPrayerAmount = 0.0;
				npc.endGfx = -1;
				int npcX = npc.getVisualX();
				int npcY = npc.getVisualY();

				switch (npc.npcType)
				{

						// Cerberus.
						case 4045:
								if (withInMeleeDistance(npcX, npcY, player.getX(), player.getY(), npc.npcType))
								{
										random = Misc.random(1);
								}
								else
								{

										random = 0;
								}
								if (random == 1)
								{
										npc.attackType = ServerConstants.MELEE_ICON;
								}
								else
								{
										npc.attackType = ServerConstants.MAGIC_ICON;
										npc.projectileId = 2618;
								}
								break;

						// Infernal mage.
						case 1643:
						case 172:
								npc.attackType = ServerConstants.MAGIC_ICON;
								npc.projectileId = 99;
								break;

						// Zamorak boss mage npc.
						case 6208:
								npc.attackType = ServerConstants.MAGIC_ICON;
								npc.projectileId = 156;
								break;

						// Zamorak boss ranged npc.
						case 6206:
								npc.attackType = ServerConstants.RANGED_ICON;
								npc.projectileId = 12;
								break;

						// Zombies.
						case 3956:
								npc.attackType = ServerConstants.MAGIC_ICON;
								npc.projectileId = 159;
								break;
						case 3957:
								npc.attackType = ServerConstants.MAGIC_ICON;
								npc.projectileId = 162;
								break;
						case 3958:
								npc.attackType = ServerConstants.MAGIC_ICON;
								npc.projectileId = 156;
								break;
						case 3959:
						case 3960:
								npc.attackType = ServerConstants.RANGED_ICON;
								npc.projectileId = 1889;
								break;


						case 3961:
								npc.attackType = ServerConstants.RANGED_ICON;
								npc.projectileId = 12;
								break;
						// Karamel.
						case 3495:
								npc.endGfx = 281;
								npc.attackType = ServerConstants.MAGIC_ICON;
								player.gfx0(369);
								Combat.resetPlayerAttack(player);
								Movement.stopMovement(player);
								if (player.canBeFrozen())
								{
										player.playerAssistant.sendFilterableMessage("You have been Frozen!");
										player.setFrozenLength(20000);
								}
								break;

						case 401:
								if (Misc.hasPercentageChance(10))
								{
										player.startAnimation(404);
										player.gfx(254, 70);
										player.doingActionEvent(6);
										player.getPA().movePlayer(player.getX() - Misc.random(4, 7), player.getY(), 0);
								}
								break;
						case 913:
								npc.endGfx = 76;
								npc.bottomGfx = true;
								npc.attackType = ServerConstants.MAGIC_ICON;
								if (Misc.hasPercentageChance(25))
								{
										npc.forceChat("Hail Saradomin!");
								}
								break;

						case 912:
								npc.endGfx = 78;
								npc.bottomGfx = true;
								npc.attackType = ServerConstants.MAGIC_ICON;
								if (Misc.hasPercentageChance(25))
								{
										npc.forceChat("Hail Zamorak!");
								}
								break;

						case 914:
								npc.endGfx = 77;
								npc.bottomGfx = true;
								npc.attackType = ServerConstants.MAGIC_ICON;
								if (Misc.hasPercentageChance(25))
								{
										npc.forceChat("Hail Guthix!");
								}
								break;

						case 8349:
						case 8350:
						case 8351:

								if (withInMeleeDistance(npcX, npcY, player.getX(), player.getY(), npc.npcType))
								{
										random = Misc.random(2);
								}
								else
								{

										random = Misc.random(1);
								}
								if (random == 0)
								{
										npc.attackType = ServerConstants.MAGIC_ICON;
										npc.projectileId = 1884;
								}
								else if (random == 1)
								{
										npc.attackType = ServerConstants.RANGED_ICON;
										npc.projectileId = 1889;
								}
								else if (random == 2)
								{
										npc.attackType = ServerConstants.MELEE_ICON;
										npc.gfx100(1886);
										npc.projectileId = -1;
								}
								break;

						case 9463:
								// Ice Strykewyrm
								random = Misc.random(2);
								if (random <= 1)
								{
										npc.attackType = ServerConstants.MAGIC_ICON;
								}
								else if (random == 2)
								{
										npc.attackType = ServerConstants.RANGED_ICON;
								}
								break;

						case 5666:
								// Barrelchest
								random = Misc.random(1);
								if (random == 0)
								{
										npc.attackType = ServerConstants.MELEE_ICON;
								}
								else if (random == 1)
								{
										npc.attackType = ServerConstants.MAGIC_ICON;
								}
								break;

						case 8133:
								// Corporeal Beast

								if (withInMeleeDistance(npcX, npcY, player.getX(), player.getY(), npc.npcType))
								{
										random = Misc.random(2);
								}
								else
								{

										random = Misc.random(1);
								}
								npc.projectileId = -1;
								if (random <= 1)
								{
										npc.attackType = ServerConstants.MAGIC_ICON;
										npc.endGfx = -1;
										npc.projectileId = 1828;
										npc.hitThroughPrayerAmount = 0.4;
								}
								else if (random == 2)
								{
										npc.attackType = ServerConstants.MELEE_ICON;
										npc.hitThroughPrayerAmount = 0.4;
										npc.gfx100(1834);
								}
								break;

						// Venenatis.
						case 4043://
								int styles = Misc.random(10);
								if (styles <= 5)
								{
										npc.attackType = ServerConstants.MAGIC_ICON;
										npc.gfx100(164);
										npc.projectileId = 165;
										npc.endGfx = 166;
								}
								else if (styles <= 9)
								{
										npc.attackType = ServerConstants.MAGIC_ICON;
										npc.gfx100(170);
										npc.projectileId = 171;
										npc.endGfx = 172;
								}
								else
								{
										npc.attackType = ServerConstants.MELEE_ICON;
										npc.gfx100(-1);
										npc.projectileId = -1;
										npc.endGfx = -1;
										player.gfx(254, 70);
										player.doingActionEvent(6);
										Poison.appendPoison(null, player, false, 10);
										player.getPA().sendFilterableMessage("Venenatis hurls her web at you, sticking you to the ground.");
								}
								break;

						case 4040: // Callisto
								npc.hitThroughPrayerAmount = 0.4;
								break;

						case 2030: // Verac the Defiled.
								npc.hitThroughPrayerAmount = 1.0;
								break;

						case 6222:
								// Kree'Arra
								if (withInMeleeDistance(npcX, npcY, player.getX(), player.getY(), npc.npcType))
								{
										random = Misc.random(2);
								}
								else
								{
										random = Misc.random(1);
								}
								if (random == 0)
								{
										npc.attackType = ServerConstants.MAGIC_ICON;
										npc.projectileId = 1197;
								}
								else if (random == 1)
								{
										npc.attackType = ServerConstants.RANGED_ICON;
										npc.projectileId = 1198;
								}
								else if (random == 2)
								{
										npc.attackType = ServerConstants.MELEE_ICON;
										npc.projectileId = 1198;
								}
								break;

						case 6223:
								// Wingman Skree
								npc.attackType = ServerConstants.MAGIC_ICON;
								break;
						case 6247:
								// Commander Zilyana

								if (withInMeleeDistance(npcX, npcY, player.getX(), player.getY(), npc.npcType))
								{
										random = Misc.random(2);
								}
								else
								{
										random = Misc.random(1);
								}
								if (random <= 1)
								{
										npc.attackType = ServerConstants.MAGIC_ICON;
										npc.endGfx = 1224;
										npc.projectileId = -1;
								}
								else if (random == 2)
								{
										npc.attackType = ServerConstants.MELEE_ICON;
										npc.endGfx = 1224;
										npc.projectileId = -1;
								}
								break;
						case 6248:
								// Startlight
								npc.attackType = ServerConstants.MELEE_ICON;
								break;
						case 6225:
								// Flockleader Geerin
								npc.attackType = ServerConstants.RANGED_ICON;
								break;
						case 6250:
								// Growler
								npc.attackType = ServerConstants.MAGIC_ICON;
								npc.projectileId = 1203;
								break;
						case 6252:
								// Bree
								npc.attackType = ServerConstants.RANGED_ICON;
								npc.projectileId = 9;
								break;
						case 6261:
								// Sergeant Strongstack
								npc.attackType = ServerConstants.MELEE_ICON;
								break;
						case 6263:
								// Sergeant Steelwill
								npc.attackType = ServerConstants.MAGIC_ICON;
								npc.projectileId = 1203;
								npc.endGfx = 1211;
								break;
						case 6265:
								// Sergeant Grimspike
								npc.attackType = ServerConstants.RANGED_ICON;
								npc.projectileId = 1206;
								break;
						case 6203:
								// K'ril Tsutsaroth
								if (withInMeleeDistance(npcX, npcY, player.getX(), player.getY(), npc.npcType))
								{
										random = Misc.random(2);
								}
								else
								{
										random = Misc.random(1);
								}
								if (random <= 1)
								{
										npc.attackType = ServerConstants.MAGIC_ICON;
										npc.projectileId = 1211;
								}
								else if (random == 2)
								{
										npc.attackType = ServerConstants.MELEE_ICON;
										npc.projectileId = -1;
								}
								break;
						case 2892:
								// Spinolyp (mage)
								n.projectileId = 94;
								n.attackType = ServerConstants.MAGIC_ICON;
								n.endGfx = 95;
								break;
						case 2894:
								// Spinolyp (ranged)
								npc.projectileId = 298;
								npc.attackType = ServerConstants.RANGED_ICON;
								break;
						case 50:
								// King Black Dragon
								if (withInMeleeDistance(npcX, npcY, player.getX(), player.getY(), npc.npcType))
								{
										random = Misc.random(4);
								}
								else
								{
										random = Misc.random(3);
								}
								if (random == 0)
								{
										npc.projectileId = 393; // red
										npc.endGfx = 430;
										npc.attackType = 3;
										npc.hitThroughPrayerAmount = 0.2;
								}
								else if (random == 1)
								{
										npc.projectileId = 394; // green
										npc.endGfx = 429;
										npc.attackType = 3;
										npc.hitThroughPrayerAmount = 0.2;
								}
								else if (random == 2)
								{
										npc.projectileId = 395; // white
										npc.endGfx = 431;
										npc.attackType = 3;
										npc.hitThroughPrayerAmount = 0.2;
								}
								else if (random == 3)
								{
										npc.projectileId = 396; // blue
										npc.endGfx = 428;
										npc.attackType = 3;
										npc.hitThroughPrayerAmount = 0.2;
								}
								else if (random == 4)
								{
										npc.projectileId = -1; // melee
										npc.endGfx = -1;
										npc.attackType = ServerConstants.MELEE_ICON;
										npc.hitThroughPrayerAmount = 0.2;
								}
								break;

						case 2025:
								// Ahrim the Blighted
								npc.attackType = ServerConstants.MAGIC_ICON;
								int r = Misc.random(3);
								if (r == 0)
								{
										npc.gfx100(158);
										npc.projectileId = 159;
										npc.endGfx = 160;
								}
								if (r == 1)
								{
										npc.gfx100(161);
										npc.projectileId = 162;
										npc.endGfx = 163;
								}
								if (r == 2)
								{
										npc.gfx100(164);
										npc.projectileId = 165;
										npc.endGfx = 166;
								}
								if (r == 3)
								{
										npc.gfx100(155);
										npc.projectileId = 156;
								}
								break;

						case 2881:
								// Dagannoth Supreme
								npc.attackType = ServerConstants.RANGED_ICON;
								npc.projectileId = 298;
								break;
						case 2882:
								// Dagannoth Prime
								npc.attackType = ServerConstants.MAGIC_ICON;
								npc.projectileId = 162;
								npc.endGfx = 477;
								break;
						case 2028:
								// Karil the Tainted
								npc.attackType = ServerConstants.RANGED_ICON;
								npc.projectileId = 27;
								break;
						case 3200:
								// Chaos Elemental
								random = Misc.random(1);
								if (random == 0)
								{
										npc.attackType = ServerConstants.RANGED_ICON;
										npc.gfx100(550);
										npc.projectileId = 551;
										npc.endGfx = 552;
								}
								else
								{
										npc.attackType = ServerConstants.MAGIC_ICON;
										npc.gfx100(553);
										npc.projectileId = 554;
										npc.endGfx = 555;
								}
								break;
						case 6260:
								// General Graardor
								if (withInMeleeDistance(npcX, npcY, player.getX(), player.getY(), npc.npcType))
								{
										random = Misc.random(2);
								}
								else
								{
										random = Misc.random(1);
								}
								if (random <= 1)
								{
										npc.attackType = ServerConstants.RANGED_ICON;
										npc.endGfx = -1;
										npc.projectileId = 288;
								}
								else if (random == 2)
								{
										npc.attackType = ServerConstants.MELEE_ICON;
										npc.endGfx = -1;
										npc.projectileId = -1;
								}
								break;
						case 2745:
								// TzTok-Jad
								if (withInMeleeDistance(npcX, npcY, player.getX(), player.getY(), npc.npcType))
								{
										random = Misc.random(2);
								}
								else
								{
										random = Misc.random(1);
								}
								if (random == 0)
								{
										npc.attackType = ServerConstants.MAGIC_ICON;
										npc.endGfx = 157;
										npc.projectileId = 448;
								}
								else if (random == 1)
								{
										npc.attackType = ServerConstants.RANGED_ICON;
										npc.endGfx = 451;
										npc.projectileId = -1;
								}
								else if (random == 2)
								{
										npc.attackType = ServerConstants.MELEE_ICON;
										npc.projectileId = -1;
								}
								break;

						case 5363:
								// Mithril-Dragon
						case 53:
								// Red dragon
						case 54:
								// Black dragon
						case 55:
								// Blue dragon
						case 941:
								// Green dragon
						case 1590:
								// Bronze dragon
						case 1591:
								// Iron dragon
						case 1592:
								// Steel dragon
						case 11260: // Lava dragon.
								random = Misc.random(2);
								if (random <= 1)
								{
										npc.projectileId = -1;
										npc.endGfx = -1;
										npc.attackType = ServerConstants.MELEE_ICON;
								}
								else
								{
										npc.projectileId = -1;
										npc.gfx100(1);
										npc.attackType = ServerConstants.DRAGONFIRE_ATTACK;
								}
								break;
				}

		}

		/**
		 * Speed of gfx special attack of the Npc
		 **/
		public int getProjectileSpeed(Npc npc)
		{
				switch (npc.npcType)
				{
						case 2745:
								// TzTok-Jad
								return 140;

						case 53:
								// Red dragon
						case 54:
								// Black dragon
						case 55:
								// Blue dragon
						case 941:
								// Green dragon
						case 1589:
								// Baby red dragon
						case 1590:
								// Bronze dragon
						case 1591:
								// Iron dragon
						case 1592:
								// Steel dragon
						case 11260: // Lava dragon.
								return 120;
						default:
								return 100;
				}
		}

		public void multiAttackDamage(Npc npc)
		{
				for (int j = 0; j < PlayerHandler.players.length; j++)
				{
						if (PlayerHandler.players[j] != null)
						{
								Player player = PlayerHandler.players[j];
								if (player.getDead() || player.getHeight() != npc.getHeight())
								{
										continue;
								}
								if (PlayerHandler.players[j].playerAssistant.withInDistance(player.getX(), player.getY(), npc.getX(), npc.getY(), 15))
								{
										int damage = 0;
										if (npc.attackType == ServerConstants.MAGIC_ICON)
										{
												damage = Misc.random(NpcDefinition.getDefinitions()[npc.npcType].magicMaximumDamage);
												damage = Effects.victimWearingSpiritShield(player, damage);
												if (Misc.random(NpcDefinition.getDefinitions()[npc.npcType].attack) < Misc.random(MagicFormula.getMagicDefenceAdvantage(player)))
												{
														damage = 0;
												}

												if (player.prayerActive[ServerConstants.PROTECT_FROM_MAGIC])
												{
														damage = (int) (damage * npc.hitThroughPrayerAmount);
												}
										}

										else if (npc.attackType == ServerConstants.RANGED_ICON)
										{
												damage = Misc.random(NpcDefinition.getDefinitions()[npc.npcType].rangedMaximumDamage);
												damage = Effects.victimWearingSpiritShield(player, damage);
												if (Misc.random(NpcDefinition.getDefinitions()[npc.npcType].attack) < Misc.random(RangedFormula.getInvisibleRangedDefenceAdvantage(player)))
												{
														damage = 0;
												}

												if (player.prayerActive[ServerConstants.PROTECT_FROM_RANGED])
												{
														damage = (int) (damage * npc.hitThroughPrayerAmount);
												}
										}

										else if (npc.attackType == ServerConstants.MELEE_ICON)
										{
												damage = Misc.random(NpcDefinition.getDefinitions()[npc.npcType].maximumDamage);
												damage = Effects.victimWearingSpiritShield(player, damage);
												if (Misc.random(NpcDefinition.getDefinitions()[npc.npcType].attack) < Misc.random(MeleeFormula.getInvisibleMeleeDefenceAdvantage(player)))
												{
														damage = 0;
												}

												if (player.prayerActive[ServerConstants.PROTECT_FROM_MELEE])
												{
														damage = (int) (damage * npc.hitThroughPrayerAmount);
												}
										}
										Combat.appendHitFromNpcOrVengEtc(player, damage, ServerConstants.NORMAL_HITSPLAT_COLOUR, npc.attackType);
										playerHasVengeance(player, npc, damage);
										ringOfRecoilNpc(player, npc, damage);
										if (npc.endGfx > 0)
										{
												player.gfx0(npc.endGfx);
										}
								}
						}
				}
		}

		public int findPlayerToAttack(Npc npc)
		{
				ArrayList<Integer> players = new ArrayList<Integer>();
				for (int j = 0; j < PlayerHandler.players.length; j++)
				{
						Player player = PlayerHandler.players[j];
						if (player == null)
						{
								continue;
						}
						if (!player.playerAssistant.withInDistance(player.getX(), player.getY(), npc.getSpawnPositionX(), npc.getSpawnPositionY(), NpcDefinition.getDefinitions()[npc.npcType].attackDistance + NpcDefinition.getRoamDistance(npc.npcType)))
						{
								continue;
						}
						if (Combat.wasAttackedByNpc(player) && player.getNpcIndexAttackingPlayer() != npc.npcIndex && !Area.inMulti(player.getX(), player.getY()))
						{
								continue;
						}
						if (!canAggressOnPlayer(player, npc))
						{
								continue;
						}
						if (player.getHeight() == npc.getHeight())
						{
								players.add(j);
						}
				}
				if (players.size() > 0)
				{
						return players.get(Misc.random(players.size() - 1));
				}
				else
				{
						return 0;
				}
		}

		public void multiAttackGfx(Npc npc)
		{
				if (npc.projectileId < 0)
				{
						return;
				}
				for (int j = 0; j < PlayerHandler.players.length; j++)
				{
						if (PlayerHandler.players[j] != null)
						{
								Player player = PlayerHandler.players[j];
								if (player.getHeight() != npc.getHeight())
										continue;
								if (PlayerHandler.players[j].playerAssistant.withInDistance(player.getX(), player.getY(), npc.getX(), npc.getY(), 15))
								{
										int nX = npc.getVisualX();
										int nY = npc.getVisualY();
										int pX = player.getX();
										int pY = player.getY();
										int offX = (nY - pY) * -1;
										int offY = (nX - pX) * -1;
										player.getPA().createPlayersProjectile(nX, nY, offX, offY, 50, getProjectileSpeed(npc), npc.projectileId, 43, 31, -player.getPlayerId() - 1, 65, Combat.getProjectileSlope(player));
								}
						}
				}
		}

		/**
		 * Summon npc, barrows, etc
		 **/
		public static void spawnNpc(Player player, int npcType, int x, int y, int heightLevel, boolean attackPlayer, boolean headIcon)
		{
				int slot = -1;
				for (int i = 1; i < NPC_INDEX_OPEN_MAXIMUM; i++)
				{
						if (npcs[i] == null)
						{
								slot = i;
								break;
						}
				}
				if (slot == -1)
				{
						return;
				}
				Npc newNpc = new Npc(slot, npcType);

				npcs[slot] = newNpc;
				newNpc.name = NpcDefinition.getDefinitions()[npcType].name;
				newNpc.doNotRespawn = true;
				newNpc.setSpawnPositionX(x);
				newNpc.setSpawnPositionY(y);
				newNpc.setX(x);
				newNpc.setY(y);
				newNpc.setHeight(heightLevel);
				newNpc.faceAction = "ROAM";
				newNpc.currentHitPoints = NpcDefinition.getDefinitions()[npcType].hitPoints;
				newNpc.maximumHitPoints = newNpc.currentHitPoints;
				newNpc.setSpawnedBy(player == null ? 0 : player.getPlayerId());
				newNpc.summonedBy = player == null ? 0 : player.getPlayerId();
				if (headIcon)
				{
						player.getPA().drawHeadicon(1, slot, 0, 0);
				}
				if (attackPlayer)
				{
						newNpc.underAttack = true;
						newNpc.setKillerId(player.getPlayerId());
				}
				// Jad
				if (newNpc.npcType == 2745)
				{
						newNpc.attackType = Misc.random(1, 2);
				}

				for (int index = 0; index < WarriorsGuild.ARMOUR_DATA.length; index++)
				{
						if (npcType == WarriorsGuild.ARMOUR_DATA[index][3])
						{
								newNpc.forceChat("I'M ALIVE!!!!");
						}
				}

				for (int index = 0; index < Barrows.COFFIN_AND_BROTHERS.length; index++)
				{
						if (npcType == Barrows.COFFIN_AND_BROTHERS[index][1])
						{
								newNpc.forceChat("You dare disturb my rest!");
						}
				}
		}

		public static void spawnNpcZombie(Player player, int npcType, int x, int y, int heightLevel)
		{
				int slot = -1;
				for (int i = 1; i < NPC_INDEX_OPEN_MAXIMUM; i++)
				{
						if (npcs[i] == null)
						{
								slot = i;
								break;
						}
				}
				if (slot == -1)
				{
						return;
				}
				Npc newNPC = new Npc(slot, npcType);

				npcs[slot] = newNPC;
				newNPC.name = NpcDefinition.getDefinitions()[npcType].name;
				newNPC.setX(x);
				newNPC.zombieOwner = player.getPlayerName();
				newNPC.doNotRespawn = true;
				newNPC.setY(y);
				newNPC.setSpawnPositionX(x);
				newNPC.setSpawnPositionY(y);
				newNPC.setHeight(heightLevel);
				newNPC.faceAction = "ROAM";
				newNPC.currentHitPoints = NpcDefinition.getDefinitions()[npcType].hitPoints;
				newNPC.maximumHitPoints = newNPC.currentHitPoints;
				newNPC.setSpawnedBy(0);
				newNPC.underAttack = true;
				newNPC.setKillerId(player.getPlayerId());
		}

		public static int getAttackEmote(Npc npc)
		{

				switch (npc.npcType)
				{


						case 50:
								// King Black Dragon
						case 53:
								// Red dragon
						case 54:
								// Black dragon
						case 55:
								// Blue dragon
						case 941:
								// Green dragon
						case 1589:
								// Baby red dragon
						case 1590:
								// Bronze dragon
						case 1591:
								// Iron dragon
						case 1592:
								// Steel dragon

						case 11260: // Lava dragon.
								if (npc.attackType == ServerConstants.DRAGONFIRE_ATTACK)
								{
										return 81;
								}
				}

				return -1;
		}

		public static void spawnDefaultNpc(int npcType, String name, int x, int y, int heightLevel, String faceAction)
		{
				int slot = -1;
				for (int i = 1; i < NPC_INDEX_OPEN_MAXIMUM; i++)
				{
						if (npcs[i] == null)
						{
								slot = i;
								break;
						}
				}
				if (slot == -1)
				{
						return;
				}
				Npc newNPC = new Npc(slot, npcType);
				npcs[slot] = newNPC;
				newNPC.name = name;
				newNPC.setX(x);
				newNPC.setY(y);
				newNPC.setSpawnPositionX(x);
				newNPC.setSpawnPositionY(y);
				newNPC.setHeight(heightLevel);
				newNPC.faceAction = faceAction;
				newNPC.currentHitPoints = NpcDefinition.getDefinitions()[npcType].hitPoints;
				newNPC.maximumHitPoints = newNPC.currentHitPoints;
		}

		public static void spawnDefaultNpcSheep(int npcType, String name, int x, int y, int heightLevel, String faceAction, int ticks, int newNpcId)
		{
				int slot = -1;
				for (int i = 1; i < NPC_INDEX_OPEN_MAXIMUM; i++)
				{
						if (npcs[i] == null)
						{
								slot = i;
								break;
						}
				}
				if (slot == -1)
				{
						return;
				}
				Npc newNpc = new Npc(slot, npcType);
				npcs[slot] = newNpc;
				newNpc.name = name;
				newNpc.transformTimer = ticks;
				newNpc.transformIntoId = newNpcId;
				newNpc.setX(x);
				newNpc.setY(y);
				newNpc.setSpawnPositionX(x);
				newNpc.setSpawnPositionY(y);
				newNpc.setHeight(heightLevel);
				newNpc.faceAction = faceAction;
				newNpc.currentHitPoints = NpcDefinition.getDefinitions()[npcType].hitPoints;
				newNpc.maximumHitPoints = newNpc.currentHitPoints;
		}

		public static void spawnDifferentTormentedDemon(Player player, int npcId, int currentHp, int oldX, int oldY, int tormentedDemonAttackType, boolean tormentedDemonShield, long tormentedDemonTimeChangedPrayer, long tormentedDemonTimeWeakened)
		{
				int slot = -1;
				for (int i = 1; i < NPC_INDEX_OPEN_MAXIMUM; i++)
				{
						if (npcs[i] == null)
						{
								slot = i;
								break;
						}
				}
				if (slot == -1)
				{
						return;
				}
				Npc newNpc = new Npc(slot, npcId);
				npcs[slot] = newNpc;
				newNpc.name = "Tormented demon";
				newNpc.setX(oldX);
				newNpc.setY(oldY);
				newNpc.setSpawnPositionX(oldX);
				newNpc.setSpawnPositionY(oldY);
				newNpc.setHeight(0);
				newNpc.faceAction = "Roam";
				newNpc.currentHitPoints = currentHp;
				newNpc.maximumHitPoints = NpcDefinition.getDefinitions()[8349].hitPoints;
				newNpc.attackType = tormentedDemonAttackType;
				newNpc.tormentedDemonShield = tormentedDemonShield;
				newNpc.tormentedDemonTimeChangedPrayer = tormentedDemonTimeChangedPrayer;
				newNpc.tormentedDemonTimeWeakened = tormentedDemonTimeWeakened;
				newNpc.tormentedDemonPrayerChangeRandom = Misc.random(6, 20);
				newNpc.facePlayer(player.getPlayerId());
				player.lastNpcAttackedIndex = newNpc.npcIndex;
				player.setNpcIdentityAttacking(newNpc.npcIndex);
		}

		public void npcGameTick()
		{
				long time = System.currentTimeMillis();
				for (int i = 0; i < NPC_INDEX_OPEN_MAXIMUM; i++)
				{
						Npc npc = npcs[i];
						if (npc == null)
						{
								continue;
						}
						/*
						 * Start of Pet. Leave this in process because this for loop can cause a problem when using events for 10 players for example. It's best to have this 'for loop' once every game tick than have 10 players doing it 10 times per game tick.
						 */
						Player petOwner = PlayerHandler.players[npc.summonedBy];
						if (petOwner != null)
						{
								//Basically pet owner is wrong, guy basically logged out, another one logged in and took place
								if (petOwner.getDead())
								{
										//	Pet.deletePet(npc);
								}
								if (petOwner.getPetSummoned() && npc.summoned && petOwner.getPetId() == npc.npcType)
								{
										if (petOwner.playerAssistant.withInDistance(npc.getX(), npc.getY(), petOwner.getX(), petOwner.getY(), 10) && petOwner.getHeight() == npc.getHeight())
										{
												followPlayer(i, petOwner.getPlayerId());
										}
										else
										{
												Pet.deletePet(npc);
												Pet.summonNpcOnValidTile(petOwner, petOwner.getPetId());
										}
										if (petOwner.forceCallFamiliar)
										{
												Pet.deletePet(npc);
												Pet.summonNpcOnValidTile(petOwner, petOwner.getPetId());
												petOwner.forceCallFamiliar = false;
										}
								}
						}
						else if (npc.summoned)
						{
								Pet.deletePet(npc);
						}

						/* End of Pet. */

						if (npc.transformIntoId > 0)
						{
								if (npc.transformTimer > 0)
								{
										npc.transformTimer--;
								}
								else if (npc.transformTimer == 0)
								{
										int oldX = npc.getX();
										int oldY = npc.getY();
										Pet.deletePet(npc);
										NpcHandler.spawnDefaultNpc(43, "Sheep", oldX, oldY, 0, "ROAM");
								}
						}

						if (npc.npcType == 5196 && teleporterInAction)
						{
								startAnimation(npc, 722);
								npc.forceChat("Senventior Disthine Molenko!");
								teleporterInAction = false;
								//npc.transform(1);
						}

						if (npc.respawnTimer > 0)
						{
								npc.respawnTimer--;
						}
						if (npc.hitDelayTimer > 0)
						{
								npc.hitDelayTimer--;
								if (npc.isDead)
								{
										npc.setKillerId(0);
										npc.hitDelayTimer = 0;
								}

								Player player = PlayerHandler.players[npc.oldIndex];
								if (player == null)
								{
										npc.setKillerId(0);
										npc.hitDelayTimer = 0;
								}
								else
								{

										if (player.isTeleporting())
										{
												npc.setKillerId(0);
												npc.hitDelayTimer = 0;
										}
										if (player.getDoingAgility())
										{
												npc.setKillerId(0);
												npc.hitDelayTimer = 0;
										}
								}

						}
						if (npc.hitDelayTimer == 1)
						{
								npc.hitDelayTimer = 0;
								applyDamageOnPlayerFromNPC(npc);
						}
						if (npc.attackTimer > 0)
						{
								npc.attackTimer--;
						}

						if (npc.getSpawnedBy() > 0 && npc.summonedBy <= 0)
						{
								if (PlayerHandler.players[npc.getSpawnedBy()] == null || PlayerHandler.players[npc.getSpawnedBy()].getHeight() != npc.getHeight() && npc.summonedBy <= 0 || PlayerHandler.players[npc.getSpawnedBy()].getDead() || !PlayerHandler.players[npc.getSpawnedBy()].playerAssistant.withInDistance(npc.getX(), npc.getY(), PlayerHandler.players[npc.getSpawnedBy()].getX(), PlayerHandler.players[npc.getSpawnedBy()].getY(), 20))
								{
										npc.setX(0);
										npc.setY(0);
										if (PlayerHandler.players[npc.getSpawnedBy()] != null)
										{
												for (int index = 0; index < WarriorsGuild.ARMOUR_DATA.length; index++)
												{
														if (WarriorsGuild.ARMOUR_DATA[index][3] == npc.npcType)
														{
																PlayerHandler.players[npc.getSpawnedBy()].summonedAnimator = false;
														}
												}
										}
										npcs[i] = null;
										continue;
								}
						}

						/**
						 * Attacking player
						 **/
						if (npc.getSpawnedBy() <= 0 && NpcDefinition.getAggressive(npc.npcType) && !npc.isDead && (npc.getKillerId() <= 0 || switchTargets(npc)))
						{
								int target = findPlayerToAttack(npc);
								npc.setKillerId(target);
						}
						if (System.currentTimeMillis() - npc.lastDamageTaken > 5000)
						{
								npc.underAttackBy = 0;
						}
						if ((npc.getKillerId() > 0 || npc.underAttack) && !npc.isWalkingHome())
						{
								if (!npc.isDead)
								{
										int p = npc.getKillerId();
										if (PlayerHandler.players[p] != null)
										{
												Player player = PlayerHandler.players[p];
												followPlayer(i, player.getPlayerId());
												stepAway(npc, player);
												if (npc.attackTimer == 0)
												{
														attackPlayer(player, i);
												}
										}
										else
										{
												npc.setKillerId(0);
												npc.underAttack = false;
												npc.facePlayer(0);
										}
								}
						}

						if (npc.npcType == 162 && Misc.random(20) == 1)
						{
								int random = Misc.random(5);
								if (random == 0)
								{
										npc.forceChat("You are too slow, keep moving!");
								}
								else if (random == 1)
								{
										npc.forceChat("My grand mother can move faster than this!");
								}
								else if (random == 2)
								{
										npc.forceChat("I said no breaks, don't stop!");
								}
								else if (random == 3)
								{
										npc.forceChat("Faster, faster, faster!");
								}
								else if (random == 4)
								{
										int value = Misc.random(500);
										npc.forceChat((value + 80) + " more laps to go!");
								}
								else if (random == 5)
								{
										npc.forceChat("Come on! Move your legs!");
								}
						}
						if (npc.getKillerId() == 0)
						{
								npc.randomWalk = true;
						}
						/**
						 * Random walking and walking home
						 **/
						if ((!npc.underAttack || npc.isWalkingHome()) && npc.randomWalk && !npc.isDead && System.currentTimeMillis() - npc.timeAttackedAPlayer > 10000 && !npc.summoned)
						{
								npc.facePlayer(0);
								npc.setKillerId(0);
								if (npc.getSpawnedBy() == 0)
								{
										if ((npc.getX() > npc.getSpawnPositionX() + NpcDefinition.getRoamDistance(npc.npcType)) || (npc.getX() < npc.getSpawnPositionX() - NpcDefinition.getRoamDistance(npc.npcType)) || (npc.getY() > npc.getSpawnPositionY() + NpcDefinition.getRoamDistance(npc.npcType)) || (npc.getY() < npc.getSpawnPositionY() - NpcDefinition.getRoamDistance(npc.npcType)))
										{
												npc.setWalkingHome(true);
										}
								}
								if (npc.isWalkingHome() && npc.getX() == npc.getSpawnPositionX() && npc.getY() == npc.getSpawnPositionY())
								{
										npc.setWalkingHome(false);
								}
								else if (npc.isWalkingHome() && npc.getKillerId() == 0 && !npc.isMoved())
								{
										npc.setMoveX(GetMove(npc.getX(), npc.getSpawnPositionX()));
										npc.setMoveY(GetMove(npc.getY(), npc.getSpawnPositionY()));
										npc.getNextNPCMovement(npc.npcIndex);
										npc.updateRequired = true;
										npc.setMoved(true);
								}
								if (!npc.faceAction.isEmpty())
								{
										switch (npc.faceAction)
										{

												case "ROAM":
														boolean roam = true;
														if (npc.getKillerId() > 0)
														{
																roam = false;
														}
														if (npc.isMoved())
														{
																roam = false;
														}
														if (roam)
														{
																if (System.currentTimeMillis() - npc.timeTurnedByPlayer < 10000)
																{
																		break;
																}
																if (rarelyWalkNpcs(npc.npcType) && !npc.isWalkingHome())
																{
																		int MoveX = 0;
																		int MoveY = 0;
																		int Rnd = Misc.random(9);
																		npc.setMoved(true);
																		if (Rnd == 1)
																		{
																				MoveX = 1;
																				MoveY = 1;
																		}
																		else if (Rnd == 2)
																		{
																				MoveX = -1;
																		}
																		else if (Rnd == 3)
																		{
																				MoveY = -1;
																		}
																		else if (Rnd == 4)
																		{
																				MoveX = 1;
																		}
																		else if (Rnd == 5)
																		{
																				MoveY = 1;
																		}
																		else if (Rnd == 6)
																		{
																				MoveX = -1;
																				MoveY = -1;
																		}
																		else if (Rnd == 7)
																		{
																				MoveX = -1;
																				MoveY = 1;
																		}
																		else if (Rnd == 8)
																		{
																				MoveX = 1;
																				MoveY = -1;
																		}
																		if (MoveX == 1)
																		{
																				if (npc.getX() + MoveX < npc.getSpawnPositionX() + 1)
																				{
																						npc.setMoveX(MoveX);
																				}
																				else
																				{
																						npc.setMoveX(0);
																				}
																		}
																		if (MoveX == -1)
																		{
																				if (npc.getX() - MoveX > npc.getSpawnPositionX() - 1)
																				{
																						npc.setMoveX(MoveX);
																				}
																				else
																				{
																						npc.setMoveX(0);
																				}
																		}
																		if (MoveY == 1)
																		{
																				if (npc.getY() + MoveY < npc.getSpawnPositionY() + 1)
																				{
																						npc.setMoveY(MoveY);
																				}
																				else
																				{
																						npc.setMoveY(0);
																				}
																		}
																		if (MoveY == -1)
																		{
																				if (npc.getY() - MoveY > npc.getSpawnPositionY() - 1)
																				{
																						npc.setMoveY(MoveY);
																				}
																				else
																				{
																						npc.setMoveY(0);
																				}
																		}
																		npc.getNextNPCMovement(npc.npcIndex);
																		npc.updateRequired = true;
																}
														}
														break;

												case "WEST":
														npc.turnNpc(npc.getX() - 1, npc.getY());
														break;

												case "EAST":
														npc.turnNpc(npc.getX() + 1, npc.getY());
														break;

												case "SOUTH":
														npc.turnNpc(npc.getX(), npc.getY() - 1);
														break;
												case "NORTH":
														npc.turnNpc(npc.getX(), npc.getY() + 1);
														break;
										}
								}
						}
						if (npc.isDead)
						{
								if (npc.respawnTimer == 0 && !npc.applyDead && !npc.needRespawn)
								{
										npc.facePlayer(0);
										npc.respawnTimer = NpcDefinition.getDefinitions()[npc.npcType].deathDeleteTicks;
										npc.killedBy = getNpcKillerId(i);
										Player player = PlayerHandler.players[npc.killedBy];
										startAnimation(npc, NpcDefinition.getDefinitions()[npc.npcType].deathAnimation);
										Slayer.slayerTaskNPCKilled(npcs[i].killedBy, npcs[i].npcType, NpcDefinition.getDefinitions()[npcs[i].npcType].hitPoints);
										FightCaves.fightCavesReward(npc.killedBy, npc.npcType);
										Barrows.killedBarrows(player, npc);
										npc.setFrozenLength(0);
										npc.applyDead = true;
										resetPlayersInCombat(i);
								}
								else if (npc.respawnTimer == 0 && npc.applyDead && !npc.needRespawn)
								{
										npc.needRespawn = true;
										int respawnTimer = NpcDefinition.getDefinitions()[npc.npcType].respawnTicks;
										if (NpcDefinition.getDefinitions()[npc.npcType].name.toLowerCase().contains("revenant"))
										{
												respawnTimer = 30;
										}
										npc.respawnTimer = respawnTimer;
										dropLoot(npc);
										npc.setX(0);
										npc.setY(0);
										npc.animNumber = 0x328;
										npc.updateRequired = true;
										npc.animUpdateRequired = true;
								}
								else if (npc.respawnTimer == 0 && npc.needRespawn == true)
								{
										if (npc.getSpawnedBy() > 0 || npc.doNotRespawn)
										{
												npcs[i] = null;
										}
										else
										{
												int old1 = npc.npcType;
												String old2 = npc.name;
												int old3 = npc.getSpawnPositionX();
												int old4 = npc.getSpawnPositionY();
												int old5 = npc.getHeight();
												String old6 = npc.faceAction;
												npcs[i] = null;
												spawnDefaultNpc(old1, old2, old3, old4, old5, old6);
										}
								}
						}
						npc.setMoved(false);
				}
				GameTickLog.npcTickDuration = System.currentTimeMillis() - time;
		}

		/**
		 * @return
		 * 			True, if the npc can switch targets when player is in a multi-zone.
		 */
		private boolean switchTargets(Npc npc)
		{
				if (NpcDefinition.getDefinitions()[npc.npcType].clever && System.currentTimeMillis() - npc.timeFoundNewTarget > 10000)
				{
						npc.timeFoundNewTarget = System.currentTimeMillis();
						return true;
				}
				return false;
		}

		/**
		* Npc killer id?
		**/
		public int getNpcKillerId(int npcId)
		{
				int oldDamage = 0;
				int killerId = 0;
				for (int p = 1; p < ServerConstants.MAXIMUM_PLAYERS; p++)
				{
						if (PlayerHandler.players[p] != null)
						{
								if (PlayerHandler.players[p].lastNpcAttackedIndex == npcId)
								{
										if (PlayerHandler.players[p].getTotalDamageDealt() > oldDamage)
										{
												oldDamage = PlayerHandler.players[p].getTotalDamageDealt();
												killerId = p;
										}
										PlayerHandler.players[p].setTimeNpcAttackedPlayer(0);
										PlayerHandler.players[p].setTotalDamageDealt(0);

										// Only reset if the last npc i attacked is the one i just killed.
										if (PlayerHandler.players[p].getNpcIdAttacking() == npcId)
										{
												PlayerHandler.players[p].resetNpcIdentityAttacking();
										}
								}
						}
				}
				return killerId;
		}

		public static boolean isBattleMageNpc(int npcType)
		{
				switch (npcType)
				{
						case 912:
						case 913:
						case 914:
								return true;
				}
				return false;
		}

		public void dropLoot(Npc npc)
		{
				Player player = PlayerHandler.players[npc.killedBy];
				if (player == null)
				{
						return;
				}
				player.resetFaceUpdate();
				if (Zombie.zombieKilled(player, npc))
				{
						return;
				}
				GodWarsDungeonInterface.addGwdKillCount(player, npc);
				NpcKillTracker.addNpcKill(player, npc.npcType);
				//ClueScroll.dropClueScroll(npc, player);
				BossPetDrops.bossPetDrop(player, npc);
				//WarriorsGuild.dropAnimatedTokens(player, npc);
				//WarriorsGuild.dropDefender(player, npc);
				NpcDrops.getConstantDrop(npc, player);
				NpcDrops.getNormalDrop(npc, player, false, false);
				NpcDrops.otherDrops(npc, player);
				//RecipeForDisaster.isRfdNpc(player, npc);
				//RecipeForDisaster.spawnNextWave(player, false, npc.npcType);
				NpcDrops.giveRareDrop(player, npc);
				Revenant.revanantLoot(player, npc);
		}

		/**
		 * Resets players in combat
		 */
		public void resetPlayersInCombat(int npcIndex)
		{
				for (int j = 0; j < PlayerHandler.players.length; j++)
				{
						if (PlayerHandler.players[j] != null)
						{
								if (PlayerHandler.players[j].getNpcIndexAttackingPlayer() == npcIndex)
								{
										PlayerHandler.players[j].setTimeNpcAttackedPlayer(0);
								}
						}
				}
		}

		/**
		 * Npc Follow Player
		 **/
		public int GetMove(int Place1, int Place2)
		{
				if ((Place1 - Place2) == 0)
				{
						return 0;
				}
				else if ((Place1 - Place2) < 0)
				{
						return 1;
				}
				else if ((Place1 - Place2) > 0)
				{
						return -1;
				}
				return 0;
		}

		/**
		 * NPC will not move if added here.
		 */
		public boolean doNotfollowPlayer(Npc npc) // Npc will never move if added here
		{
				switch (npc.npcType)
				{
						case 2892:
								// Spinolyp
						case 2894:
								// Spinolyp
						case 2882:
								// Dagannoth Prime
						case 2881:
								// Dagannoth Supreme
						case 9463:
								// Ice Strykewyrm
								return true;
				}
				return false;
		}

		/**
		 * NPC will follow the player.
		 * 
		 * @param npcIndex
		 *        The NPC following the player
		 * @param playerId
		 *        The player the NPC will follow.
		 * @param pet
		 *        True, if the pet is the NPC. This will not make the pet face the player all the time.
		 */
		public void followPlayer(int npcIndex, int playerId)
		{
				Player player = PlayerHandler.players[playerId];
				Npc npc = npcs[npcIndex];
				if (npc.isMoved())
				{
						return;
				}
				if (player == null)
				{
						return;
				}
				if (player.getDead())
				{
						npc.facePlayer(0);
						npc.randomWalk = true;
						npc.underAttack = false;
						return;
				}
				if (npc.isFrozen())
				{
						return;
				}
				if (doNotfollowPlayer(npc))
				{
						npc.facePlayer(playerId);
						return;
				}
				int playerX = player.getX();
				int playerY = player.getY();
				int npcX = npc.getVisualX();
				int npcY = npc.getVisualY();
				if (!npc.summoned)
				{
						if (goodDistance(npcX, npcY, player.getX(), player.getY(), NpcDefinition.getDefinitions()[npc.npcType].attackDistance + NpcDefinition.getDefinitions()[npc.npcType].size - 1))
						{
								return;
						}
				}
				npc.setMoved(true);
				npc.randomWalk = false;
				npc.setWalkingHome(false);
				if (npcX == playerX && npc.getY() == playerY)
				{
						int o = Misc.random(3);
						switch (o)
						{
								case 0:
										npc.setMoveX(GetMove(npc.getX(), playerX));
										npc.setMoveY(GetMove(npc.getY(), playerY + 1));
										break;
								case 1:
										npc.setMoveX(GetMove(npc.getX(), playerX));
										npc.setMoveY(GetMove(npc.getY(), playerY - 1));
										break;
								case 2:
										npc.setMoveX(GetMove(npc.getX(), playerX + 1));
										npc.setMoveY(GetMove(npc.getY(), playerY));
										break;
								case 3:
										npc.setMoveX(GetMove(npc.getX(), playerX - 1));
										npc.setMoveY(GetMove(npc.getY(), playerY));
										break;
						}
						npc.getNextNPCMovement(npc.npcIndex);
						npc.facePlayer(playerId);
						npc.updateRequired = true;
				}
				if (goodDistance(npcX, npcY, playerX, playerY, npc.getSpawnedBy() > 0 ? 1 : NpcDefinition.getDefinitions()[npc.npcType].size))
				{
						return;
				}
				if (npc.getSpawnedBy() > 0 || ((npc.getX() < npc.getSpawnPositionX() + NpcDefinition.getRoamDistance(npc.npcType)) && (npc.getX() > npc.getSpawnPositionX() - NpcDefinition.getRoamDistance(npc.npcType)) && (npc.getY() < npc.getSpawnPositionY() + NpcDefinition.getRoamDistance(npc.npcType)) && (npc.getY() > npc.getSpawnPositionY() - NpcDefinition.getRoamDistance(npc.npcType))))
				{
						if (npc.getHeight() == player.getHeight())
						{
								if (player != null && npc != null)
								{
										if (playerY < npc.getY())
										{
												npc.setMoveX(GetMove(npc.getX(), playerX));
												npc.setMoveY(GetMove(npc.getY(), playerY));
										}
										else if (playerY > npc.getY())
										{
												npc.setMoveX(GetMove(npc.getX(), playerX));
												npc.setMoveY(GetMove(npc.getY(), playerY));
										}
										else if (playerX < npc.getX())
										{
												npc.setMoveX(GetMove(npc.getX(), playerX));
												npc.setMoveY(GetMove(npc.getY(), playerY));
										}
										else if (playerX > npc.getX())
										{
												npc.setMoveX(GetMove(npc.getX(), playerX));
												npc.setMoveY(GetMove(npc.getY(), playerY));
										}
										else if (playerX == npc.getX() || playerY == npc.getY())
										{
												int o = Misc.random(3);
												switch (o)
												{
														case 0:
																npc.setMoveX(GetMove(npc.getX(), playerX));
																npc.setMoveY(GetMove(npc.getY(), playerY + 1));
																break;
														case 1:
																npc.setMoveX(GetMove(npc.getX(), playerX));
																npc.setMoveY(GetMove(npc.getY(), playerY - 1));
																break;
														case 2:
																npc.setMoveX(GetMove(npc.getX(), playerX + 1));
																npc.setMoveY(GetMove(npc.getY(), playerY));
																break;
														case 3:
																npc.setMoveX(GetMove(npc.getX(), playerX - 1));
																npc.setMoveY(GetMove(npc.getY(), playerY));
																break;
												}
										}
										npc.getNextNPCMovement(npc.npcIndex);
										npc.facePlayer(playerId);
										npc.updateRequired = true;
								}
						}
				}
				else
				{
						npc.setKillerId(0);
						npc.facePlayer(0);
						npc.randomWalk = true;
						npc.underAttack = false;
				}
		}

		/**
		 * NPC Attacking Player
		 **/
		public void attackPlayer(final Player player, int i)
		{
				Npc npc = npcs[i];
				if (npc == null)
				{
						return;
				}
				if (npc.isDead)
				{
						return;
				}
				if (player.isTeleporting())
				{
						npc.setKillerId(0);
						return;
				}
				if (player.getDoingAgility())
				{
						npc.setKillerId(0);
						return;
				}
				if (!Area.inMulti(npc.getX(), npc.getY()) && npc.underAttackBy > 0 && npc.underAttackBy != player.getPlayerId())
				{
						npc.setKillerId(0);
						return;
				}
				if (!Area.inMulti(npc.getX(), npc.getY()) && player.getNpcIndexAttackingPlayer() > 0 && player.getNpcIndexAttackingPlayer() != npc.npcIndex && Combat.wasAttackedByNpc(player))
				{
						npc.setKillerId(0);
						return;
				}
				if (!Area.inMulti(npc.getX(), npc.getY()) && Combat.wasUnderAttackByAnotherPlayer(player, 5000))
				{
						npc.setKillerId(0);
						return;
				}
				if (npc.getHeight() != player.getHeight())
				{
						npc.setKillerId(0);
						return;
				}
				if (!player.playerAssistant.withInDistance(player.getX(), player.getY(), npc.getSpawnPositionX(), npc.getSpawnPositionY(), NpcDefinition.getDefinitions()[npc.npcType].attackDistance + NpcDefinition.getRoamDistance(npc.npcType)))
				{
						if (player.killingNpcIndex == npc.npcIndex)
						{
								npc.setWalkingHome(true);
						}
						npc.setKillerId(0);
						return;
				}
				if (npc.npcType == 913 && ItemAssistant.hasItemEquipped(player, 2412) && !npc.underAttack && System.currentTimeMillis() - npc.timeAttackedAPlayer > 10000)
				{
						npc.setKillerId(0);
						return;
				}
				if (npc.npcType == 914 && ItemAssistant.hasItemEquipped(player, 2413) && !npc.underAttack && System.currentTimeMillis() - npc.timeAttackedAPlayer > 10000)
				{
						npc.setKillerId(0);
						return;
				}
				if (npc.npcType == 914 && ItemAssistant.hasItemEquipped(player, 2414) && !npc.underAttack && System.currentTimeMillis() - npc.timeAttackedAPlayer > 10000)
				{
						npc.setKillerId(0);
						return;
				}

				int playerX = player.getX();
				int playerY = player.getY();
				int npcX = npc.getVisualX();
				int npcY = npc.getVisualY();
				if (npcX == playerX && npcY == playerY)
				{
						stepAway(npc, player);
						return;
				}

				if (!goodDistance(npcX, npcY, playerX, playerY, NpcDefinition.getDefinitions()[npc.npcType].attackDistance + (NpcDefinition.getDefinitions()[npc.npcType].size - 1)))
				{
						return;
				}

				if (player.getDead())
				{
						return;
				}
				npc.facePlayer(player.getPlayerId());

				int nextXtile = player.getX();
				int nextYtile = player.getY();

				npc.facePlayer(player.getPlayerId());
				npc.attackType = ServerConstants.MELEE_ICON;
				loadSpell(npc);

				// This clipping part has to be after loadSpell(). because npc.attackType changes within loadSpell.
				// Spinolyps at Dagannoth kings can shoot through anthing.
				if (npc.npcType != 2892)
				{
						if (npc.attackType == ServerConstants.MELEE_ICON)
						{
								if (!Region.isStraightPathUnblocked(nextXtile, nextYtile, npc.getVisualX(), npc.getVisualY(), npc.getHeight(), 1, 1))
								{
										return;
								}
						}
						else
						{
								// Has to be 1 size or sometimes when attacking bosses, you can safespot them beside rocks etc.
								if (!Region.isStraightPathUnblockedProjectiles(nextXtile, nextYtile, npc.getVisualX(), npc.getVisualY(), npc.getHeight(), 1, 1, true))
								{
										return;
								}
						}
				}



				npc.attackTimer = NpcDefinition.getDefinitions()[npc.npcType].attackSpeed;
				GameTimeSpent.increaseGameTime(player, GameTimeSpent.PVM);
				if (player.isInZombiesMinigame())
				{
						player.lastActivity = "ZOMBIES";
						player.lastActivityTime = System.currentTimeMillis();
				}
				npc.timeAttackedAPlayer = System.currentTimeMillis();
				startNewAggression(player, npc);
				npc.hitDelayTimer = getHitDelay(npc);
				usingSpecial = false;
				if (npc.attackType == 3)
				{
						npc.hitDelayTimer += 2;
				}
				player.setNpcIndexAttackingPlayer(i);
				npc.oldIndex = player.getPlayerId();
				player.setTimeNpcAttackedPlayer(System.currentTimeMillis());
				player.timeNpcAttackedPlayerLogOutTimer = System.currentTimeMillis();
				player.playerInNpcCombat = System.currentTimeMillis();
				performAttackAnimation(npc);
				player.doNotClosePmInterface = true;
				player.getPA().closeInterfaces();

				if (Combat.hasSerpentineHelm(player) && Misc.hasOneOutOf(6))
				{
						CombatNpc.applyPoisonOnNpc(player, npc, 10);
				}
				if (multiAttacks(npc))
				{
						multiAttackGfx(npc);
						return;
				}
				if (npc.projectileId > 0)
				{
						int nX = npc.getVisualX();
						int nY = npc.getVisualY();
						int pX = player.getX();
						int pY = player.getY();
						int offX = (nY - pY) * -1;
						int offY = (nX - pX) * -1;
						player.getPA().createPlayersProjectile(nX, nY, offX, offY, 50, getProjectileSpeed(npc), npc.projectileId, getGfxStartHeight(npc.npcType), 25, -player.getPlayerId() - 1, 70, 16);
				}
		}

		private int getGfxStartHeight(int npcId)
		{
				switch (npcId)
				{
						case 1643:
						case 172:
								return 30;
				}
				return 43;
		}

		private boolean withInMeleeDistance(int npcX, int npcY, int x, int y, int npcType)
		{
				int attackDistance = NpcDefinition.getDefinitions()[npcType].attackDistance;

				// Kree'arra.
				if (npcType == 6222)
				{
						attackDistance = 2;
				}

				// General Graardor.
				if (npcType == 6260)
				{
						attackDistance = 2;
				}

				// Commander Zilyana.
				if (npcType == 6247)
				{
						attackDistance = 2;
				}

				// Corporeal Beast and Jad and Dagannoth Rex
				if (npcType == 8133 || npcType == 2745 || npcType == 2883)
				{
						attackDistance = 2;
				}

				// // K'ril Tsutsaroth.
				if (npcType == 6203)
				{
						attackDistance = 2;
				}

				// // King black dragon.
				if (npcType == 50)
				{
						attackDistance = 2;
				}

				// Tormented demon
				if (npcType == 8349 || npcType == 8350 || npcType == 8351)
				{
						attackDistance = 2;
				}

				if (goodDistance(npcX, npcY, x, y, attackDistance + (NpcDefinition.getDefinitions()[npcType].size - 1)))
				{
						return true;
				}
				return false;
		}

		public void performAttackAnimation(Npc npc)
		{
				switch (npc.attackType)
				{
						case ServerConstants.MELEE_ICON:
								startAnimation(npc, NpcDefinition.getDefinitions()[npc.npcType].attackAnimation);
								break;
						case ServerConstants.RANGED_ICON:
								int animation = NpcDefinition.getDefinitions()[npc.npcType].rangedAttackAnimation;
								if (animation == 0)
								{
										animation = NpcDefinition.getDefinitions()[npc.npcType].attackAnimation;
								}
								startAnimation(npc, animation);
								break;
						case ServerConstants.MAGIC_ICON:
								int animation1 = NpcDefinition.getDefinitions()[npc.npcType].magicAttackAnimation;
								if (animation1 == 0)
								{
										animation1 = NpcDefinition.getDefinitions()[npc.npcType].attackAnimation;
								}
								startAnimation(npc, animation1);
								break;
						case ServerConstants.DRAGONFIRE_ATTACK:
								startAnimation(npc, getAttackEmote(npc));
								break;
				}
		}

		public boolean usingSpecial;

		/**
		 * Apply the damage on the player.
		 */
		public void applyDamageOnPlayerFromNPC(Npc npc)
		{
				if (npc == null)
				{
						return;
				}
				Player player = PlayerHandler.players[npc.oldIndex];
				if (player == null)
				{
						npc.setKillerId(0);
						return;
				}
				if (player.getHeight() != npc.getHeight())
				{
						return;
				}
				if (player.isTeleporting())
				{
						npc.setKillerId(0);
						return;
				}
				if (player.getDoingAgility())
				{
						npc.setKillerId(0);
						return;
				}
				if (npc.isDead)
				{
						return;
				}
				if (player.getPlayerIdAttacking() <= 0 && player.getNpcIdAttacking() <= 0)
				{
						if (player.getAutoRetaliate() == 1 && !player.isMoving() && !player.doingAnAction())
						{
								player.setNpcIdentityAttacking(npc.npcIndex);
								player.setNpcIdToFollow(npc.npcIndex);
						}
				}
				if (player.getAttackTimer() <= 3 || player.getAttackTimer() == 0 && player.getNpcIdAttacking() == 0 && player.getOldNpcIndex() == 0 && !player.getDoingAgility())
				{
						player.startAnimation(Combat.getBlockAnimation(player));
						if (!player.soundSent)
						{
								SoundSystem.sendSound(player, 511, 450);
						}
				}
				if (multiAttacks(npc))
				{
						multiAttackDamage(npc);
						return;
				}
				if (!player.getDead())
				{
						int damage = 0;
						if (npc.attackType == ServerConstants.MELEE_ICON)
						{
								damage = Misc.random(NpcDefinition.getDefinitions()[npc.npcType].maximumDamage);

								// Dharok formula.
								if (npc.npcType == 2026)
								{
										damage = 30;
										damage += (npc.maximumHitPoints - npc.currentHitPoints) / 2;
										damage = Misc.random(damage);
								}
								if (10 + Misc.random(MeleeFormula.getInvisibleMeleeDefenceAdvantage(player)) > Misc.random(NpcDefinition.getDefinitions()[npc.npcType].attack))
								{
										damage = 0;
								}
								if (player.prayerActive[ServerConstants.PROTECT_FROM_MELEE])
								{
										damage = (int) (damage * npc.hitThroughPrayerAmount);
								}
								if (player.getCurrentCombatSkillLevel(ServerConstants.HITPOINTS) - damage < 0)
								{
										damage = player.getCurrentCombatSkillLevel(ServerConstants.HITPOINTS);
								}
						}
						if (npc.attackType == ServerConstants.RANGED_ICON)
						{
								damage = Misc.random(NpcDefinition.getDefinitions()[npc.npcType].rangedMaximumDamage);
								if (10 + Misc.random(RangedFormula.getInvisibleRangedDefenceAdvantage(player)) > Misc.random(NpcDefinition.getDefinitions()[npc.npcType].attack))
								{
										damage = 0;
								}
								if (player.prayerActive[ServerConstants.PROTECT_FROM_RANGED])
								{
										damage = (int) (damage * npc.hitThroughPrayerAmount);
								}
								if (player.getCurrentCombatSkillLevel(ServerConstants.HITPOINTS) - damage < 0)
								{
										damage = player.getCurrentCombatSkillLevel(ServerConstants.HITPOINTS);
								}
								if (npc.endGfx > 0)
								{
										if (npc.bottomGfx)
										{
												player.gfx0(npc.endGfx);
										}
										else
										{
												player.gfx100(npc.endGfx);
										}
								}
						}
						if (npc.attackType == ServerConstants.MAGIC_ICON)
						{
								damage = Misc.random(NpcDefinition.getDefinitions()[npc.npcType].magicMaximumDamage);
								boolean magicFailed = false;
								if (10 + Misc.random(MagicFormula.getMagicDefenceAdvantage(player)) > Misc.random(NpcDefinition.getDefinitions()[npc.npcType].attack))
								{
										damage = 0;
								}
								if (player.prayerActive[ServerConstants.PROTECT_FROM_MAGIC])
								{
										damage = (int) (damage * npc.hitThroughPrayerAmount);
								}
								if (damage == 0)
								{
										magicFailed = true;
								}
								if (player.getCurrentCombatSkillLevel(ServerConstants.HITPOINTS) - damage < 0)
								{
										damage = player.getCurrentCombatSkillLevel(ServerConstants.HITPOINTS);
								}
								if (npc.endGfx > 0 && (!magicFailed || isFightCaveNpc(npc)))
								{
										if (npc.bottomGfx)
										{
												player.gfx0(npc.endGfx);
										}
										else
										{
												player.gfx100(npc.endGfx);
										}
								}
								else if (magicFailed)
								{
										player.gfx100(85);
								}
						}
						if (npc.attackType == ServerConstants.DRAGONFIRE_ATTACK)
						{
								int anti = Combat.antiFire(player, false, true);
								switch (anti)
								{
										case 0:
												damage = Misc.random(57) + 2;
												player.playerAssistant.sendMessage("You are badly burnt by the dragon fire!");
												break;
										case 1:
												damage = Misc.random(8);
												break;
										case 2:
												damage = 0;
												break;
								}

								// King black dragon.
								if (anti >= 1 && npc.npcType == 50)
								{
										damage = Misc.random(10);
								}
								if (npc.endGfx > 0)
								{
										if (npc.bottomGfx)
										{
												player.gfx0(npc.endGfx);
										}
										else
										{
												player.gfx100(npc.endGfx);
										}
								}

						}
						damage = Effects.victimWearingSpiritShield(player, damage);
						handleSpecialEffects(player, npc, damage);
						if (npc.attackType != ServerConstants.DRAGONFIRE_ATTACK)
						{
								Combat.appendHitFromNpcOrVengEtc(player, damage, ServerConstants.NORMAL_HITSPLAT_COLOUR, npc.attackType);
						}
						else
						{
								Combat.appendHitFromNpcOrVengEtc(player, damage, ServerConstants.DARK_RED_HITSPLAT_COLOUR, ServerConstants.NO_ICON);
						}
						if (player.getDead())
						{
								player.deathsToNpc++;
								npc.setKillerId(0);
						}
				}
		}

		private void playerHasVengeance(Player player, Npc npc, int damage)
		{
				if (player.getVengeance())
				{
						if (damage <= 0)
						{
								return;
						}

						player.specialAttackWeaponUsed[31] = 1;
						player.setWeaponAmountUsed(31);
						player.forcedText = "Taste vengeance!";
						player.forcedChatUpdateRequired = true;
						player.setUpdateRequired(true);
						player.setVengeance(false);
						int vengDamage = (int) (damage * 0.75);
						if (vengDamage > npc.currentHitPoints)
						{
								vengDamage = npc.currentHitPoints;
						}

						SpecialAttackTracker.saveMaximumDamage(player, vengDamage, "VENGEANCE", true);
						CombatNpc.applyHitSplatOnNpc(player, npc, vengDamage, ServerConstants.NORMAL_HITSPLAT_COLOUR, ServerConstants.NO_ICON, 1);
				}
		}

		public void handleSpecialEffects(Player player, Npc npc, int damage)
		{
				playerHasVengeance(player, npc, damage);
				ringOfRecoilNpc(player, npc, damage);

				// Spinolyps
				if (npc.npcType == 2892 || npc.npcType == 2894)
				{
						if (damage > 0)
						{
								if (player != null)
								{
										if (player.getCurrentCombatSkillLevel(ServerConstants.PRAYER) > 0)
										{
												player.currentCombatSkillLevel[ServerConstants.PRAYER]--;
												Skilling.updateSkillTabFrontTextMain(player, ServerConstants.PRAYER);
												Poison.appendPoison(null, player, false, 2);
										}
								}
						}
				}
		}

		private void ringOfRecoilNpc(Player player, Npc npc, int damage)
		{
				if (damage > 0 && player.getRecoilCharges() > 0 && (ItemAssistant.hasItemEquipped(player, 2550) || ItemAssistant.hasItemEquipped(player, 18814)))
				{
						int recoilDamage = damage / 10;
						if (recoilDamage < 1)
						{
								recoilDamage = 1;
						}
						player.setRecoilCharges(player.getRecoilCharges() - recoilDamage);
						if (player.getRecoilCharges() < 0)
						{
								recoilDamage += player.getRecoilCharges();
						}
						if (recoilDamage > 0)
						{
								if (recoilDamage > npc.currentHitPoints)
								{
										recoilDamage = npc.currentHitPoints;
								}
								CombatNpc.applyHitSplatOnNpc(player, npc, recoilDamage, ServerConstants.NORMAL_HITSPLAT_COLOUR, ServerConstants.NO_ICON, 1);
						}
						if (player.getRecoilCharges() <= 0)
						{
								player.setRecoilCharges(40);
								ItemAssistant.deleteEquipment(player, 2550, ServerConstants.RING_SLOT);
								player.playerAssistant.sendMessage("Your ring of recoil turns to dust.");
						}
				}

		}

		public static void startAnimation(Npc npc, int animId)
		{
				npc.animNumber = animId;
				npc.animUpdateRequired = true;
				npc.updateRequired = true;
		}

		public boolean goodDistance(int objectX, int objectY, int playerX, int playerY, int distance)
		{
				for (int i = 0; i <= distance; i++)
				{
						for (int j = 0; j <= distance; j++)
						{
								if ((objectX + i) == playerX && ((objectY + j) == playerY || (objectY - j) == playerY || objectY == playerY))
								{
										return true;
								}
								else if ((objectX - i) == playerX && ((objectY + j) == playerY || (objectY - j) == playerY || objectY == playerY))
								{
										return true;
								}
								else if (objectX == playerX && ((objectY + j) == playerY || (objectY - j) == playerY || objectY == playerY))
								{
										return true;
								}
						}
				}
				return false;
		}

		public NpcHandler()
		{
		}

		public static void loadNpcData()
		{
				for (int i = 0; i < NPC_INDEX_OPEN_MAXIMUM; i++)
				{
						npcs[i] = null;
				}
				try
				{


						new NpcDefinitionNonCombatJSON();
						new NpcDefinitionCombatJSON();
						new NpcDefinitionCleverJSON();
						new NpcSpawnNonCombatJSON();
						new NpcSpawnBossJSON();
						new NpcSpawnCombatJSON();
						NpcDrops.loadConstants();
						NpcDrops.loadRareDrops();



				}
				catch (Exception e)
				{
						e.printStackTrace();
				}
		}

		private void stepAway(Npc npc, Player player)
		{
				if (npc.isMoved())
				{
						return;
				}
				int npcX = npc.getVisualX();
				int npcY = npc.getVisualY();
				if (npcX == player.getX() && npcY == player.getY())
				{
						if (Region.pathUnblocked(player.getX(), player.getY(), player.getHeight(), "WEST"))
						{
								npc.setMoveX(-1);
						}
						else if (Region.pathUnblocked(player.getX(), player.getY(), player.getHeight(), "EAST"))
						{
								npc.setMoveX(1);
						}
						else if (Region.pathUnblocked(player.getX(), player.getY(), player.getHeight(), "SOUTH"))
						{
								npc.setMoveY(-1);
						}
						else if (Region.pathUnblocked(player.getX(), player.getY(), player.getHeight(), "NORTH"))
						{
								npc.setMoveY(1);
						}
						npc.setMoved(true);
						npc.getNextNPCMovement(npc.npcIndex);
						npc.updateRequired = true;
				}
		}

		public static void facePlayer(Player player, int npcIndex)
		{
				if (NpcHandler.npcs[npcIndex].npcType < 316 || NpcHandler.npcs[npcIndex].npcType > 334)
				{
						NpcHandler.npcs[npcIndex].facePlayer(player.getPlayerId());
						NpcHandler.npcs[npcIndex].timeTurnedByPlayer = System.currentTimeMillis();
				}
		}

		private boolean canAggressOnPlayer(Player player, Npc npc)
		{
				for (int i = 0; i < player.npcAggressionData.size(); i++)
				{
						// If npc type matches the type in the arraylist.
						if (npc.npcType == Integer.parseInt(player.npcAggressionData.get(i).substring(4, player.npcAggressionData.get(i).indexOf("time="))))
						{
								int minutes = 20;
								if (System.currentTimeMillis() - Long.parseLong(player.npcAggressionData.get(i).substring(player.npcAggressionData.get(i).indexOf("time=") + 5)) > minutes * 60000) //1200000
								{
										return false;
								}
						}
				}
				return true;
		}

		private void startNewAggression(Player player, Npc npc)
		{
				for (int i = 0; i < player.npcAggressionData.size(); i++)
				{
						// If npc type matches the type in the arraylist.
						if (npc.npcType == Integer.parseInt(player.npcAggressionData.get(i).substring(4, player.npcAggressionData.get(i).indexOf("time="))))
						{
								return;
						}
				}
				player.npcAggressionData.add("npc=" + npc.npcType + "time=" + System.currentTimeMillis());
		}

		public static void blockAnimation(Npc npc)
		{
				if (npc == null)
				{
						return;
				}
				if (npc.npcType != 2745 && npc.npcType != 8133)
				{
						if (NpcDefinition.getDefinitions()[npc.npcType] == null)
						{
								return;
						}
						startAnimation(npc, NpcDefinition.getDefinitions()[npc.npcType].blockAnimation);
				}
		}

		/**
		 * Has to be called before Cycle event, because cycle event is basically an action that can have variable effects etc..
		 * Also has to be before packet cycle.
		 */
		public void clearNpcFlags()
		{
				for (int i = 0; i < NPC_INDEX_OPEN_MAXIMUM; i++)
				{
						Npc npc = npcs[i];
						if (npc == null)
						{
								continue;
						}
						npcs[i].clearUpdateFlags();
				}

		}
}