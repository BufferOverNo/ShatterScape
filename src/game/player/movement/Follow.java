package game.player.movement;

import game.content.combat.Combat;
import game.content.combat.CombatConstants;
import game.content.combat.vsplayer.melee.MeleeData;
import game.content.combat.vsplayer.range.RangedData;
import game.npc.Npc;
import game.npc.NpcHandler;
import game.npc.data.NpcDefinition;
import game.object.clip.Region;
import game.player.Player;
import game.player.PlayerHandler;

/**
 * Handle following.
 * 
 * @author MGT Madness, created on 27-03-2015.
 */
public class Follow
{

		/**
		 * Follow the player.
		 * 
		 * @param player
		 *        The associated player.
		 *        @param secondCalling
		 *        True if this is the second time follow is called in the same game tick.
		 */
		public static void followPlayer(Player player, boolean secondCalling)
		{
				Player target = PlayerHandler.players[player.getPlayerIdToFollow()];
				if (player.getTeleTimer() > 3)
				{
						player.resetPlayerIdToFollow();
						return;
				}
				if (target == null || target.getDead())
				{
						player.resetPlayerIdToFollow();
						return;
				}
				if (player.isFrozen() || player.getDead())
				{
						player.resetPlayerIdToFollow();
						return;
				}
				if (player.doingAnAction())
				{
						player.resetPlayerIdToFollow();
						return;
				}
				int otherX = target.getX();
				int otherY = target.getY();
				if (target.getPlayerIdToFollow() == player.getPlayerId() && player.playerAssistant.withInDistance(otherX, otherY, player.getX(), player.getY(), 1) && !Combat.inCombat(player) && !Combat.inCombat(target))
				{
						if (secondCalling)
						{
								return;
						}
				}
				if (!player.playerAssistant.withinDistanceOfTargetPlayer(target, CombatConstants.OUT_OF_VIEW_DISTANCE))
				{
						player.resetPlayerIdToFollow();
						return;
				}
				if (Combat.stopMovement(player, target, true))
				{
						return;
				}
				player.faceUpdate(player.getPlayerIdToFollow() + 32768);
				Player other = target;
				player.oldX = player.getX();
				player.oldY = player.getY();
				if (target.getPlayerIdToFollow() == player.getPlayerId() && player.playerAssistant.withInDistance(otherX, otherY, player.getX(), player.getY(), 1) && !Combat.inCombat(player) && !Combat.inCombat(other))
				{
						if (!player.followLeader && !other.followLeader)
						{
								Movement.playerWalk(player, other.oldX, other.oldY);
						}
						else
						{
								Movement.stopMovementDifferent(player);
								if (player.getX() == other.getX() && player.getY() != other.getY())
								{
										Movement.playerWalk(player, player.getX() + 1, player.getY());
								}
								else if (player.getX() != other.getX() && player.getY() == other.getY())
								{
										Movement.playerWalk(player, player.getX(), player.getY() + 1);
								}
								else if (player.getX() != other.getX() && player.getY() != other.getY())
								{
										if (player.getY() < other.getY())
										{
												Movement.playerWalk(player, player.getX(), player.getY() + 1);
										}
										else
										{
												Movement.playerWalk(player, player.getX(), player.getY() - 1);
										}
								}
								player.followLeader = false;
						}
						return;
				}
				// Following target with ranged/magic in combat.
				if (!player.isMeleeFollow() && player.getPlayerIdAttacking() > 0 && player.playerAssistant.withinDistanceOfTargetPlayer(target, CombatConstants.getAttackDistance(player)) && !Region.isStraightPathUnblockedProjectiles(player.getX(), player.getY(), target.getX(), target.getY(), target.getHeight(), 1, 1, true))
				{
						projectileFollowing(otherX, otherY, player);
						return;
				}
				if (player.isMeleeFollow())
				{
						if (otherX == player.getX() && otherY > player.getY())
						{
								meleeFollow(player, "SOUTH", otherX, otherY);
						}

						else if (otherX == player.getX() && otherY < player.getY())
						{
								meleeFollow(player, "NORTH", otherX, otherY);
						}

						else if (otherX > player.getX() && otherY == player.getY())
						{
								meleeFollow(player, "WEST", otherX, otherY);
						}

						else if (otherX < player.getX() && otherY == player.getY())
						{
								meleeFollow(player, "EAST", otherX, otherY);
						}

						else if (otherX < player.getX() && otherY < player.getY())
						{
								meleeFollow(player, "NORTH", otherX, otherY);
						}

						else if (otherX > player.getX() && otherY > player.getY())
						{
								meleeFollow(player, "SOUTH", otherX, otherY);
						}

						else if (otherX < player.getX() && otherY > player.getY())
						{
								meleeFollow(player, "SOUTH", otherX, otherY);
						}

						else if (otherX > player.getX() && otherY < player.getY())
						{
								meleeFollow(player, "WEST", otherX, otherY);
						}
				}
				else
				{

						// Position the player behind the player, as if i am following a human, so i am following their back.
						switch (target.directionFacingPath)
						{
								// North.
								case 0:
										pathFocusOrder("SOUTH SOUTH-WEST SOUTH-EAST WEST EAST NORTH-WEST NORTH-EAST NORTH", player, otherX, otherY);
										break;
								// North West.
								case 14:
										pathFocusOrder("SOUTH-EAST SOUTH EAST SOUTH-WEST NORTH-EAST WEST NORTH NORTH-WEST", player, otherX, otherY);
										break;
								// North East.
								case 2:
										pathFocusOrder("SOUTH-WEST WEST SOUTH NORTH-WEST SOUTH-EAST NORTH EAST NORTH-EAST", player, otherX, otherY);
										break;
								// West.
								case 12:
										pathFocusOrder("EAST SOUTH-EAST NORTH-EAST SOUTH NORTH SOUTH-WEST NORTH-WEST WEST", player, otherX, otherY);
										break;

								// East.
								case 4:
										pathFocusOrder("WEST SOUTH-WEST NORTH-WEST SOUTH NORTH SOUTH-EAST NORTH-EAST EAST", player, otherX, otherY);
										break;

								// South.
								case 8:
										pathFocusOrder("NORTH NORTH-WEST NORTH-EAST WEST EAST SOUTH-WEST SOUTH-EAST SOUTH", player, otherX, otherY);
										break;
								// South West.
								case 10:
										pathFocusOrder("NORTH-EAST NORTH EAST NORTH-WEST SOUTH-EAST WEST SOUTH SOUTH-WEST", player, otherX, otherY);
										break;
								// South East.
								case 6:
										pathFocusOrder("NORTH-WEST WEST NORTH SOUTH-WEST NORTH-EAST SOUTH EAST SOUTH-EAST", player, otherX, otherY);
										break;
						}
				}
				player.faceUpdate(player.getPlayerIdToFollow() + 32768);
		}

		/**
		 * The player already follows the target with normal findRoute method if the path is blocked. All this method does is see if there is a tile that i can move to
		 * and that tile i can use it to attack target straight away, then move to it.
		 */
		private static void projectileFollowing(int otherX, int otherY, Player player)
		{
				// If player can move 1 tile and can instantly attack target, then do that instead.
				if (otherX == player.getX() && otherY > player.getY())
				{
						Follow.setPathFocusOrderForProjectiles("WEST EAST NORTH-WEST NORTH-EAST SOUTH-WEST SOUTH-EAST SOUTH", player, player.getX(), player.getY(), otherX, otherY);
				}

				else if (otherX == player.getX() && otherY < player.getY())
				{
						Follow.setPathFocusOrderForProjectiles("WEST EAST SOUTH-EAST SOUTH-WEST NORTH-WEST NORTH-EAST NORTH", player, player.getX(), player.getY(), otherX, otherY);
				}

				else if (otherX > player.getX() && otherY == player.getY())
				{
						Follow.setPathFocusOrderForProjectiles("NORTH SOUTH SOUTH-EAST NORTH-EAST SOUTH-WEST NORTH-WEST WEST", player, player.getX(), player.getY(), otherX, otherY);
				}

				else if (otherX < player.getX() && otherY == player.getY())
				{
						Follow.setPathFocusOrderForProjectiles("SOUTH NORTH NORTH-WEST SOUTH-WEST NORTH-EAST SOUTH-EAST EAST", player, player.getX(), player.getY(), otherX, otherY);
				}

				else if (otherX < player.getX() && otherY < player.getY())
				{
						Follow.setPathFocusOrderForProjectiles("SOUTH WEST SOUTH-EAST NORTH-WEST EAST NORTH NORTH-EAST", player, player.getX(), player.getY(), otherX, otherY);
				}

				else if (otherX > player.getX() && otherY > player.getY())
				{
						Follow.setPathFocusOrderForProjectiles("NORTH EAST NORTH-WEST SOUTH-EAST SOUTH SOUTH-WEST WEST", player, player.getX(), player.getY(), otherX, otherY);
				}

				else if (otherX < player.getX() && otherY > player.getY())
				{
						Follow.setPathFocusOrderForProjectiles("NORTH WEST NORTH-EAST SOUTH-WEST SOUTH EAST SOUTH-EAST", player, player.getX(), player.getY(), otherX, otherY);
				}

				else if (otherX > player.getX() && otherY < player.getY())
				{
						Follow.setPathFocusOrderForProjectiles("EAST SOUTH NORTH-EAST SOUTH-WEST NORTH WEST NORTH-WEST", player, player.getX(), player.getY(), otherX, otherY);
				}

		}

		/**
		* Melee following the entity.
		* @param focus
		* 		The direction to focus. The direction of the player to be with the entity. So south means 1 tile south of entity.
		* @param entityX
		* @param entityY
		*/
		private static void meleeFollow(Player player, String focus, int entityX, int entityY)
		{
				switch (focus)
				{
						case "SOUTH":
								pathFocusOrder("SOUTH WEST EAST NORTH", player, entityX, entityY);
								break;
						case "WEST":
								pathFocusOrder("WEST SOUTH NORTH EAST", player, entityX, entityY);
								break;
						case "EAST":
								pathFocusOrder("EAST SOUTH NORTH WEST", player, entityX, entityY);
								break;
						case "NORTH":
								pathFocusOrder("NORTH WEST EAST SOUTH", player, entityX, entityY);
								break;
				}

		}

		/**
		 * 
		 * @param focusOrder
		 * 		If focusOrder is south, walk player to south of entity if south of entity position is walkable to entity position.
		 * @param projectile
		 * 		True, to find the path where i can move once to it, then i can attack the entity from it.
		 */
		public static void pathFocusOrder(String focusOrder, Player player, int entityX, int entityY)
		{
				String[] data = focusOrder.split(" ");
				for (int index = 0; index < data.length; index++)
				{
						String focus = data[index];
						if (focus.equals("SOUTH"))
						{
								if (Region.isStraightPathUnblocked(entityX, entityY, entityX, entityY - 1, player.getHeight(), 1, 1))
								{
										Movement.playerWalk(player, entityX, entityY - 1);
										break;
								}
						}
						else if (focus.equals("WEST"))
						{
								if (Region.isStraightPathUnblocked(entityX, entityY, entityX - 1, entityY, player.getHeight(), 1, 1))
								{
										Movement.playerWalk(player, entityX - 1, entityY);
										break;
								}
						}
						else if (focus.equals("EAST"))
						{
								if (Region.isStraightPathUnblocked(entityX, entityY, entityX + 1, entityY, player.getHeight(), 1, 1))
								{
										Movement.playerWalk(player, entityX + 1, entityY);
										break;
								}
						}
						else if (focus.equals("NORTH"))
						{
								if (Region.isStraightPathUnblocked(entityX, entityY, entityX, entityY + 1, player.getHeight(), 1, 1))
								{
										Movement.playerWalk(player, entityX, entityY + 1);
										break;
								}
						}
						else if (focus.equals("NORTH-WEST"))
						{
								if (Region.isStraightPathUnblocked(entityX, entityY, entityX - 1, entityY + 1, player.getHeight(), 1, 1))
								{
										Movement.playerWalk(player, entityX - 1, entityY + 1);
										break;
								}
						}
						else if (focus.equals("NORTH-EAST"))
						{
								if (Region.isStraightPathUnblocked(entityX, entityY, entityX + 1, entityY + 1, player.getHeight(), 1, 1))
								{
										Movement.playerWalk(player, entityX + 1, entityY + 1);
										break;
								}
						}
						else if (focus.equals("SOUTH-WEST"))
						{
								if (Region.isStraightPathUnblocked(entityX, entityY, entityX - 1, entityY - 1, player.getHeight(), 1, 1))
								{
										Movement.playerWalk(player, entityX - 1, entityY - 1);
										break;
								}
						}
						else if (focus.equals("SOUTH-EAST"))
						{
								if (Region.isStraightPathUnblocked(entityX, entityY, entityX + 1, entityY - 1, player.getHeight(), 1, 1))
								{
										Movement.playerWalk(player, entityX + 1, entityY - 1);
										break;
								}
						}
				}

		}

		/**
		 * This is same as pathFocusOrder, except on this one, it will find if any tile can be moved to and on that tile moved to, i can attack the entity position instantly.
		 * If so, move to that tile.
		 * @param focusOrder
		 * 		If focusOrder is south, walk player to south of entity if south of entity position is walkable to entity position.
		 */
		public static void setPathFocusOrderForProjectiles(String focusOrder, Player player, int entityX, int entityY, int entityXPosition, int entityYPosition)
		{
				String[] data = focusOrder.split(" ");
				boolean applied = false;
				for (int index = 0; index < data.length; index++)
				{
						String focus = data[index];
						if (focus.equals("SOUTH"))
						{
								if (Region.isStraightPathUnblocked(entityX, entityY, entityX, entityY - 1, player.getHeight(), 1, 1) && Region.isStraightPathUnblockedProjectiles(entityX, entityY - 1, entityXPosition, entityYPosition, player.getHeight(), 1, 1, true))
								{
										if (player.playerAssistant.withInDistance(entityX, entityY - 1, entityXPosition, entityYPosition, CombatConstants.getAttackDistance(player)))
										{
												Movement.playerWalk(player, entityX, entityY - 1);
												applied = true;
												break;
										}
								}
						}
						else if (focus.equals("WEST"))
						{
								if (Region.isStraightPathUnblocked(entityX, entityY, entityX - 1, entityY, player.getHeight(), 1, 1) && Region.isStraightPathUnblockedProjectiles(entityX - 1, entityY, entityXPosition, entityYPosition, player.getHeight(), 1, 1, true))
								{
										if (player.playerAssistant.withInDistance(entityX - 1, entityY, entityXPosition, entityYPosition, CombatConstants.getAttackDistance(player)))
										{
												Movement.playerWalk(player, entityX - 1, entityY);
												applied = true;
												break;
										}
								}
						}
						else if (focus.equals("EAST"))
						{
								if (Region.isStraightPathUnblocked(entityX, entityY, entityX + 1, entityY, player.getHeight(), 1, 1) && Region.isStraightPathUnblockedProjectiles(entityX + 1, entityY, entityXPosition, entityYPosition, player.getHeight(), 1, 1, true))
								{
										if (player.playerAssistant.withInDistance(entityX + 1, entityY, entityXPosition, entityYPosition, CombatConstants.getAttackDistance(player)))
										{
												Movement.playerWalk(player, entityX + 1, entityY);
												applied = true;
												break;
										}
								}
						}
						else if (focus.equals("NORTH"))
						{
								if (Region.isStraightPathUnblocked(entityX, entityY, entityX, entityY + 1, player.getHeight(), 1, 1) && Region.isStraightPathUnblockedProjectiles(entityX, entityY + 1, entityXPosition, entityYPosition, player.getHeight(), 1, 1, true))
								{
										if (player.playerAssistant.withInDistance(entityX, entityY + 1, entityXPosition, entityYPosition, CombatConstants.getAttackDistance(player)))
										{
												Movement.playerWalk(player, entityX, entityY + 1);
												applied = true;
												break;
										}
								}
						}
						else if (focus.equals("NORTH-WEST"))
						{
								if (Region.isStraightPathUnblocked(entityX, entityY, entityX - 1, entityY + 1, player.getHeight(), 1, 1) && Region.isStraightPathUnblockedProjectiles(entityX - 1, entityY + 1, entityXPosition, entityYPosition, player.getHeight(), 1, 1, true))
								{
										if (player.playerAssistant.withInDistance(entityX - 1, entityY + 1, entityXPosition, entityYPosition, CombatConstants.getAttackDistance(player)))
										{
												Movement.playerWalk(player, entityX - 1, entityY + 1);
												applied = true;
												break;
										}
								}
						}
						else if (focus.equals("NORTH-EAST"))
						{
								if (Region.isStraightPathUnblocked(entityX, entityY, entityX + 1, entityY + 1, player.getHeight(), 1, 1) && Region.isStraightPathUnblockedProjectiles(entityX + 1, entityY + 1, entityXPosition, entityYPosition, player.getHeight(), 1, 1, true))
								{
										if (player.playerAssistant.withInDistance(entityX + 1, entityY + 1, entityXPosition, entityYPosition, CombatConstants.getAttackDistance(player)))
										{
												Movement.playerWalk(player, entityX + 1, entityY + 1);
												applied = true;
												break;
										}
								}
						}
						else if (focus.equals("SOUTH-WEST"))
						{
								if (Region.isStraightPathUnblocked(entityX, entityY, entityX - 1, entityY - 1, player.getHeight(), 1, 1) && Region.isStraightPathUnblockedProjectiles(entityX - 1, entityY - 1, entityXPosition, entityYPosition, player.getHeight(), 1, 1, true))
								{
										if (player.playerAssistant.withInDistance(entityX - 1, entityY - 1, entityXPosition, entityYPosition, CombatConstants.getAttackDistance(player)))
										{
												Movement.playerWalk(player, entityX - 1, entityY - 1);
												applied = true;
												break;
										}
								}
						}
						else if (focus.equals("SOUTH-EAST"))
						{
								if (Region.isStraightPathUnblocked(entityX, entityY, entityX + 1, entityY - 1, player.getHeight(), 1, 1) && Region.isStraightPathUnblockedProjectiles(entityX + 1, entityY - 1, entityXPosition, entityYPosition, player.getHeight(), 1, 1, true))
								{
										if (player.playerAssistant.withInDistance(entityX + 1, entityY - 1, entityXPosition, entityYPosition, CombatConstants.getAttackDistance(player)))
										{
												Movement.playerWalk(player, entityX + 1, entityY - 1);
												applied = true;
												break;
										}
								}
						}
				}
				if (applied)
				{
						return;
				}
				else
				{
						// findRoute. Player movement will be stopped automatically in other methods once player is in distance and is in straight path to target.
						Movement.playerWalk(player, entityXPosition, entityYPosition);
				}

		}

		/**
		* On specific large npcs, they are visually on a different tile.
		* @param npcId
		* @return
		*/
		public static int isBigNpc(int npcId)
		{
				if (NpcDefinition.getDefinitions()[npcId] == null)
				{
						return 0;
				}
				return (NpcDefinition.getDefinitions()[npcId].size - 1);
		}

		public static void followNpc(Player player)
		{
				int npcIdToFollow = player.getNpcIdToFollow();
				Npc npc = NpcHandler.npcs[npcIdToFollow];
				if (npc == null || npc.isDead)
				{
						player.resetNpcIdToFollow();
						return;
				}
				if (player.isFrozen())
				{
						return;
				}
				if (player.getDead())
				{
						return;
				}
				if (player.doingAnAction())
				{
						player.resetNpcIdToFollow();
						return;
				}
				int npcX = npc.getVisualX();
				int npcY = npc.getVisualY();
				boolean usingShortRangedWeapon = false;
				int size = 1;
				if (NpcDefinition.getDefinitions()[npc.npcType] != null)
				{
						size = NpcDefinition.getDefinitions()[npc.npcType].size;
				}
				boolean withinDistance = player.playerAssistant.withInDistance(player.getX(), player.getY(), npcX, npcY, size);
				boolean hallyDistance = player.playerAssistant.withInDistance(npcX, npcY, player.getX(), player.getY(), 2);
				boolean bowDistance = player.playerAssistant.withInDistance(npcX, npcY, player.getX(), player.getY(), CombatConstants.getAttackDistance(player));
				boolean magicDistance = player.playerAssistant.withInDistance(npcX, npcY, player.getX(), player.getY(), 10);
				boolean rangeWeaponDistance = player.playerAssistant.withInDistance(npcX, npcY, player.getX(), player.getY(), CombatConstants.getAttackDistance(player));
				boolean sameSpot = player.getX() == npcX && player.getY() == npcY;
				boolean inside = false;
				if (NpcDefinition.getDefinitions()[npc.npcType] != null)
				{
						if (NpcDefinition.getDefinitions()[npc.npcType].size > 1 && player.playerAssistant.withInDistance(npcX, npcY, player.getX(), player.getY(), NpcDefinition.getDefinitions()[npc.npcType].size - 1))
						{
								inside = true;
						}
				}

				if (!player.playerAssistant.withInDistance(npcX, npcY, player.getX(), player.getY(), 25))
				{
						player.resetNpcIdToFollow();
						return;
				}
				if (RangedData.isWieldingShortRangeRangedWeapon(player) && player.getNpcIdAttacking() > 0)
				{
						usingShortRangedWeapon = true;
				}

				if (usingShortRangedWeapon && rangeWeaponDistance && !sameSpot && !player.hasLastCastedMagic() && player.getNpcIdAttacking() > 0)
				{
						if (!inside)
						{
								boolean isStraightFromNpc = followNpcClippedProjectile(player, npc);
								if (isStraightFromNpc)
								{
										Movement.stopMovement(player);
								}
								return;
						}
				}
				if (RangedData.isWieldingMediumRangeRangedWeapon(player) && player.getNpcIdAttacking() > 0 && bowDistance && !sameSpot)
				{
						if (!inside)
						{
								boolean isStraightFromNpc = followNpcClippedProjectile(player, npc);
								if (isStraightFromNpc)
								{
										Movement.stopMovement(player);
								}
								return;
						}
				}
				if ((player.hasLastCastedMagic() || player.getAutoCasting()) && magicDistance && (npcX != player.getX() || npcY != player.getY()))
				{
						if (!inside)
						{
								boolean isStraightFromNpc = followNpcClippedProjectile(player, npc);
								if (isStraightFromNpc)
								{
										Movement.stopMovement(player);
								}
								return;
						}
				}
				if (MeleeData.usingHalberd(player) && hallyDistance && !sameSpot && player.getNpcIdAttacking() > 0)
				{
						if (!inside)
						{
								followNpcClippedProjectile(player, npc);
								return;
						}
				}
				if (player.isUsingMediumRangeRangedWeapon() && rangeWeaponDistance && !sameSpot && player.getNpcIdAttacking() > 0)
				{
						if (!inside)
						{
								followNpcClippedProjectile(player, npc);
								return;
						}
				}
				if (npcX == player.getX() && npcY == player.getY())
				{
						Movement.movePlayerFromUnderEntity(player);

				}
				else if (size == 1)
				{
						if (npcX == player.getX() && npcY > player.getY())
						{
								meleeFollow(player, "SOUTH", npcX, npcY);
						}

						else if (npcX == player.getX() && npcY < player.getY())
						{
								meleeFollow(player, "NORTH", npcX, npcY);
						}

						else if (npcX > player.getX() && npcY == player.getY())
						{
								meleeFollow(player, "WEST", npcX, npcY);
						}

						else if (npcX < player.getX() && npcY == player.getY())
						{
								meleeFollow(player, "EAST", npcX, npcY);
						}

						else if (npcX < player.getX() && npcY < player.getY())
						{
								meleeFollow(player, "NORTH", npcX, npcY);
						}

						else if (npcX > player.getX() && npcY > player.getY())
						{
								meleeFollow(player, "SOUTH", npcX, npcY);
						}

						else if (npcX < player.getX() && npcY > player.getY())
						{
								meleeFollow(player, "SOUTH", npcX, npcY);
						}

						else if (npcX > player.getX() && npcY < player.getY())
						{
								meleeFollow(player, "WEST", npcX, npcY);
						}
				}

				// If withIn distance and big npc, this is to reposition player, if player is inside Npc.
				else if (!withinDistance || inside)
				{
						if (npcY > player.getY() && npcX == player.getX())
						{
								Movement.playerWalk(player, npcX, npcY - NpcDefinition.getDefinitions()[npc.npcType].size);
						}
						else if (npcY < player.getY() && npcX == player.getX())
						{
								Movement.playerWalk(player, npcX, npcY + NpcDefinition.getDefinitions()[npc.npcType].size);
						}
						else if (npcX > player.getX() && npcY == player.getY())
						{
								Movement.playerWalk(player, npcX - NpcDefinition.getDefinitions()[npc.npcType].size, npcY);
						}
						else if (npcX < player.getX() && npcY == player.getY())
						{
								Movement.playerWalk(player, npcX + NpcDefinition.getDefinitions()[npc.npcType].size, npcY);
						}
						else if (npcX < player.getX() && npcY < player.getY())
						{
								Movement.playerWalk(player, npcX, npcY + NpcDefinition.getDefinitions()[npc.npcType].size);
						}
						else if (npcX > player.getX() && npcY > player.getY())
						{
								Movement.playerWalk(player, npcX, npcY - NpcDefinition.getDefinitions()[npc.npcType].size);
						}
						else if (npcX < player.getX() && npcY > player.getY())
						{
								Movement.playerWalk(player, npcX + NpcDefinition.getDefinitions()[npc.npcType].size, npcY);
						}
						else if (npcX > player.getX() && npcY < player.getY())
						{
								Movement.playerWalk(player, npcX - NpcDefinition.getDefinitions()[npc.npcType].size, npcY);
						}
				}
				player.faceUpdate(npcIdToFollow);
		}

		private static boolean followNpcClippedProjectile(Player player, Npc npc)
		{
				boolean isStraightFromNpc = Region.isStraightPathUnblockedProjectiles(player.getX(), player.getY(), npc.getVisualX(), npc.getVisualY(), npc.getHeight(), 1, 1, true);
				if (!player.isMeleeFollow() && player.getNpcIdAttacking() > 0 && !isStraightFromNpc)
				{
						int otherX = npc.getVisualX();
						int otherY = npc.getVisualY();
						projectileFollowing(otherX, otherY, player);
				}
				return isStraightFromNpc;

		}

		public static void resetFollow(Player player)
		{
				player.resetPlayerIdToFollow();
				player.resetNpcIdToFollow();
				player.setLastCastedMagic(false);
		}

}