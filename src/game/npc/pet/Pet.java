package game.npc.pet;

import game.content.interfaces.InterfaceAssistant;
import game.content.miscellaneous.Wolpertinger;
import game.item.ItemAssistant;
import game.npc.Npc;
import game.npc.NpcHandler;
import game.object.clip.Region;
import game.player.Player;

/**
 *	@author Jordon
 *	@author Animeking1120
 *	@author Erick
 *  @author MGT Madness, started editing at 11-12-2013.
 *  <p>
 *  Pet system.
 */
public class Pet
{

		/**
		 * Dismiss the familiar.
		 * @param player
		 * 			The associated player.
		 * @param dismissed
		 * 			True, if the player died in Wilderness.
		 */
		public static void dismissFamiliar(Player player, boolean death)
		{
				if (!player.getPetSummoned() && !death)
				{
						player.playerAssistant.sendMessage("You do not have a familiar summoned.");
						return;
				}

				for (int index = 0; index < PetData.petData.length; index++)
				{
						if (player.getPetId() == PetData.petData[index][0])
						{
								for (int i = 0; i < BossPetDrops.NORMAL_BOSS_DATA.length; i++)
								{
										if (PetData.petData[index][1] == BossPetDrops.NORMAL_BOSS_DATA[i][1])
										{
												player.getPA().sendMessage("You cannot dismiss a boss pet!");
												return;
										}
								}
								break;
						}
				}
				if (player.getPetId() == 4002)
				{
						player.getPA().sendMessage("You cannot dismiss a boss pet!");
						return;

				}
				for (int i = 0; i < NpcHandler.NPC_INDEX_OPEN_MAXIMUM; i++)
				{
						if (NpcHandler.npcs[i] == null)
						{
								continue;
						}
						if (NpcHandler.npcs[i].summonedBy == player.getPlayerId())
						{
								Pet.deletePet(NpcHandler.npcs[i]);
								if (NpcHandler.npcs[i].summoned)
								{
										player.getPA().sendMessage(":packet:npcpetid:-1");
								}
								break;
						}
				}
				player.setPetSummoned(false);
				player.setPetId(0);
				Wolpertinger.informClientOff(player);
				if (!death)
				{
						player.playerAssistant.sendMessage("You have dismissed your familiar.");
				}
		}

		/**
		 * @param itemID
		 * 			The item identity to check if it matches any pet inventory item.
		 * @return
		 * 			True, if the itemID is a pet inventory item.
		 */
		public static boolean petItem(int itemID)
		{
				for (int j = 0; j < PetData.petData.length; j++)
				{
						if (itemID == PetData.petData[j][1])
						{
								return true;
						}
				}
				return false;
		}

		/**
		 * Find if this NPC is a pet to pickup.
		 * @param player
		 * 			The player interacting with the the NPC.
		 * @param npcType
		 * 			The NPC type being interacted with.
		 */
		public static void pickUpPetRequirements(Player player, int npcType)
		{
				for (int i = 0; i < PetData.petData.length; i++)
				{
						if (PetData.petData[i][0] == npcType)
						{
								if (NpcHandler.npcs[player.getNpcClickIndex()].summonedBy == player.getPlayerId())
								{
										if (ItemAssistant.getFreeInventorySlots(player) > 0)
										{
												pickUpPet(player, npcType);
										}
										else
										{
												player.playerAssistant.sendMessage("Not enough space in your inventory.");
										}
								}
								else
								{
										player.playerAssistant.sendMessage("This is not your pet.");
								}
						}
				}
		}

		/**
		 * Pick up the pet and place in inventory of the player.
		 * @param player
		 * 			The player picking up the pet.
		 * @param pet
		 * 			The identity of the pet being picked up
		 */
		public static void pickUpPet(final Player player, final int pet)
		{
				player.startAnimation(7270);
				player.playerAssistant.sendMessage("You pick up your pet.");
				for (int i = 0; i < PetData.petData.length; i++)
				{
						if (PetData.petData[i][0] == pet)
						{
								ItemAssistant.addItem(player, PetData.petData[i][1], 1);
						}
				}
				for (int i = 0; i < NpcHandler.NPC_INDEX_OPEN_MAXIMUM; i++)
				{
						if (NpcHandler.npcs[i] == null)
						{
								continue;
						}
						if (NpcHandler.npcs[i].summonedBy == player.getPlayerId())
						{
								if (NpcHandler.npcs[i].summoned)
								{
										player.getPA().sendMessage(":packet:npcpetid:-1");
								}
								deletePet(NpcHandler.npcs[i]);
						}
				}
				InterfaceAssistant.summoningOrbOff(player);
				player.setPetSummoned(false);
				player.setPetId(0);
		}

		public static void summonNpcOnValidTile(Player player, int npcType)
		{
				int x = player.getX();
				int y = player.getY();
				if (Region.pathUnblocked(x, y, player.getHeight(), "SOUTH"))
				{
						y--;
				}
				else if (Region.pathUnblocked(x, y, player.getHeight(), "WEST"))
				{
						x--;
				}
				else if (Region.pathUnblocked(x, y, player.getHeight(), "EAST"))
				{
						x++;
				}
				else if (Region.pathUnblocked(x, y, player.getHeight(), "NORTH"))
				{
						y++;
				}
				int height = player.getHeight();
				summonNpc(player, npcType, x, y, height, true);
		}

		/**
		 * Summon the pet.
		 * @param player
		 * 			The player who summoned the pet.
		 * @param npcType
		 * 			The pet being summoned.
		 * @param x
		 * 			The x coord of the pet.
		 * @param y
		 * 			The x coord of the pet.
		 * @param heightLevel
		 * 			The height of the pet.
		 */
		public static void summonNpc(Player player, int npcType, int x, int y, int heightLevel, boolean pet)
		{
				int slot = -1;
				for (int i = 1; i < NpcHandler.NPC_INDEX_OPEN_MAXIMUM; i++)
				{
						if (NpcHandler.npcs[i] == null)
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
				newNpc.setX(x);
				newNpc.setY(y);
				newNpc.setSpawnPositionX(x);
				newNpc.setSpawnPositionY(y);
				newNpc.setHeight(heightLevel);
				newNpc.faceAction = "";
				newNpc.underAttack = true;
				if (pet)
				{
						player.getPA().sendMessage(":packet:npcpetid:" + slot);
						newNpc.setSpawnedBy(player.getPlayerId());
						newNpc.facePlayer(player.getPlayerId());
						newNpc.summoned = true;
						newNpc.summonedBy = player.getPlayerId();
						boolean bigPet = false;
						for (int i = 0; i < PetData.bigPet.length; i++)
						{
								if (npcType == PetData.bigPet[i])
								{
										bigPet = true;
								}
						}
						newNpc.gfx0(bigPet ? 1315 : 1314);
						if (npcType == 6869)
						{
								newNpc.requestAnimation(8309);
						}
						player.setPetId(npcType);
						player.setPetSummoned(true);
				}
				NpcHandler.npcs[slot] = newNpc;
		}

		/**
		 * Delete the current pet to summon a new one.
		 * This is for cases such as the Player is too far away from Pet, so the pet gets deleted and summoned close to player. Or if pet is picked up.
		 */
		public static void deletePet(Npc pet)
		{
				pet.setX(-1);
				pet.setY(-1);
				pet.setHeight(-1);
				pet.faceAction = "";
				pet.currentHitPoints = -1;
				pet.maximumHitPoints = -1;
				pet.needRespawn = true;
				pet.setSpawnedBy(1); // So the npc can be permanently deleted from the npc array.
				pet.respawnTimer = 0;
				pet.isDead = true;
				pet.applyDead = true;
		}

		/**
		 * Spawn the pet for the player that just logged in
		 * @param player
		 * 			The associated player.
		 */
		public static void ownerLoggedIn(Player player)
		{
				if (player.getPetSummoned() && player.getPetId() > 0)
				{
						Pet.summonNpcOnValidTile(player, player.getPetId());
						player.playerAssistant.sendMessage("Your loyal pet finds you!");
				}
		}

}