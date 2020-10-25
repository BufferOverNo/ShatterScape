package game.object.custom;

import game.content.skilling.Farming;
import game.object.clip.Region;

public class CustomClippedTiles
{


		private static void addSpecificDirectionClipping(int x, int y, int height, int direction)
		{
				Region.addClipping(x, y, height, direction, "different1", 0, 0);
		}

		public static void removeClippedTileCompleted()
		{
				new Object(29300, 3327, 4757, 0, 0, 10, 0, -1); // Box of health.
				new Object(3194, 3328, 4757, 0, 0, 10, 0, -1); // Chest.

				for (int index = 0; index < Region.removedObjectCoordinates.size(); index++)
				{
						String[] parse = Region.removedObjectCoordinates.get(index).split(" ");
						int parseX = Integer.parseInt(parse[0]);
						int parseY = Integer.parseInt(parse[1]);
						int parseHeight = Integer.parseInt(parse[2]);
						Region.removeClipping(parseX, parseY, parseHeight);
				}
				int CANNOT_ENTER_FROM_ANY_WEST_DIRECTION = 128;
				int CANNOT_ENTER_FROM_ANY_EAST_DIRECTION = 8;
				int CANNOT_ENTER_FROM_ANY_SOUTH_DIRECTION = 32;
				int CANNOT_ENTER_FROM_ANY_NORTH_DIRECTION = 2;

				// Clipping fixes for Edgeville general store.
				addSpecificDirectionClipping(3084, 3512, 0, CANNOT_ENTER_FROM_ANY_EAST_DIRECTION);
				addSpecificDirectionClipping(3077, 3507, 0, CANNOT_ENTER_FROM_ANY_SOUTH_DIRECTION);
				addSpecificDirectionClipping(3079, 3507, 0, CANNOT_ENTER_FROM_ANY_SOUTH_DIRECTION);
				addSpecificDirectionClipping(3083, 3507, 0, CANNOT_ENTER_FROM_ANY_SOUTH_DIRECTION);
				addSpecificDirectionClipping(3076, 3509, 0, CANNOT_ENTER_FROM_ANY_WEST_DIRECTION);
				addSpecificDirectionClipping(3076, 3510, 0, CANNOT_ENTER_FROM_ANY_WEST_DIRECTION);
				addSpecificDirectionClipping(3076, 3511, 0, CANNOT_ENTER_FROM_ANY_WEST_DIRECTION);
				addSpecificDirectionClipping(3076, 3512, 0, CANNOT_ENTER_FROM_ANY_WEST_DIRECTION);

				// Clipping fixes for Edgeville building, that is north of Edgeville bank.
				addSpecificDirectionClipping(3091, 3507, 0, CANNOT_ENTER_FROM_ANY_WEST_DIRECTION);
				addSpecificDirectionClipping(3091, 3511, 0, CANNOT_ENTER_FROM_ANY_WEST_DIRECTION);
				addSpecificDirectionClipping(3091, 3513, 0, CANNOT_ENTER_FROM_ANY_WEST_DIRECTION);
				addSpecificDirectionClipping(3091, 3508, 0, CANNOT_ENTER_FROM_ANY_WEST_DIRECTION);
				addSpecificDirectionClipping(3101, 3510, 0, CANNOT_ENTER_FROM_ANY_EAST_DIRECTION);
				addSpecificDirectionClipping(3101, 3509, 0, CANNOT_ENTER_FROM_ANY_EAST_DIRECTION);
				addSpecificDirectionClipping(3101, 3510, 0, CANNOT_ENTER_FROM_ANY_WEST_DIRECTION);
				addSpecificDirectionClipping(3101, 3509, 0, CANNOT_ENTER_FROM_ANY_WEST_DIRECTION);
				addSpecificDirectionClipping(3093, 3507, 0, CANNOT_ENTER_FROM_ANY_SOUTH_DIRECTION);
				addSpecificDirectionClipping(3096, 3507, 0, CANNOT_ENTER_FROM_ANY_SOUTH_DIRECTION);
				addSpecificDirectionClipping(3097, 3507, 0, CANNOT_ENTER_FROM_ANY_SOUTH_DIRECTION);
				addSpecificDirectionClipping(3098, 3507, 0, CANNOT_ENTER_FROM_ANY_SOUTH_DIRECTION);
				addSpecificDirectionClipping(3099, 3507, 0, CANNOT_ENTER_FROM_ANY_SOUTH_DIRECTION);
				addSpecificDirectionClipping(3100, 3507, 0, CANNOT_ENTER_FROM_ANY_SOUTH_DIRECTION);
				addSpecificDirectionClipping(3095, 3507, 0, CANNOT_ENTER_FROM_ANY_SOUTH_DIRECTION);
				addSpecificDirectionClipping(3094, 3507, 0, CANNOT_ENTER_FROM_ANY_SOUTH_DIRECTION);
				addSpecificDirectionClipping(3091, 3507, 0, CANNOT_ENTER_FROM_ANY_SOUTH_DIRECTION);
				addSpecificDirectionClipping(3100, 3510, 0, CANNOT_ENTER_FROM_ANY_EAST_DIRECTION);
				addSpecificDirectionClipping(3100, 3509, 0, CANNOT_ENTER_FROM_ANY_EAST_DIRECTION);
				addSpecificDirectionClipping(3100, 3507, 0, CANNOT_ENTER_FROM_ANY_EAST_DIRECTION);
				addSpecificDirectionClipping(3100, 3512, 0, CANNOT_ENTER_FROM_ANY_EAST_DIRECTION);
				addSpecificDirectionClipping(3100, 3513, 0, CANNOT_ENTER_FROM_ANY_EAST_DIRECTION);
				addSpecificDirectionClipping(3100, 3508, 0, CANNOT_ENTER_FROM_ANY_EAST_DIRECTION);
				addSpecificDirectionClipping(3090, 3508, 0, CANNOT_ENTER_FROM_ANY_EAST_DIRECTION);
				addSpecificDirectionClipping(3091, 3513, 0, CANNOT_ENTER_FROM_ANY_NORTH_DIRECTION);
				addSpecificDirectionClipping(3092, 3513, 0, CANNOT_ENTER_FROM_ANY_NORTH_DIRECTION);
				addSpecificDirectionClipping(3093, 3513, 0, CANNOT_ENTER_FROM_ANY_NORTH_DIRECTION);
				addSpecificDirectionClipping(3094, 3513, 0, CANNOT_ENTER_FROM_ANY_NORTH_DIRECTION);
				addSpecificDirectionClipping(3099, 3513, 0, CANNOT_ENTER_FROM_ANY_NORTH_DIRECTION);
				addSpecificDirectionClipping(3096, 3513, 0, CANNOT_ENTER_FROM_ANY_NORTH_DIRECTION);
				addSpecificDirectionClipping(3097, 3513, 0, CANNOT_ENTER_FROM_ANY_NORTH_DIRECTION);
				addSpecificDirectionClipping(3098, 3513, 0, CANNOT_ENTER_FROM_ANY_NORTH_DIRECTION);
				addSpecificDirectionClipping(3095, 3513, 0, CANNOT_ENTER_FROM_ANY_NORTH_DIRECTION);
				addSpecificDirectionClipping(3100, 3513, 0, CANNOT_ENTER_FROM_ANY_NORTH_DIRECTION);

				// Box of health.
				Region.addObjectActionTile(29300, 3090, 3512, 0, 1, 1);

				// Blood key chest.
				Region.addObjectActionTile(27277, 3097, 3513, 0, 1, 1);

				// Edgeville building objects
				// Altar of the occult.
				Region.addObjectActionTile(29150, 3099, 3507, 0, 2, 2);
				// Completionist cape stand.
				Region.addObjectActionTile(7800, 3095, 3507, 0, 2, 1);
				// Highscores stand.
				Region.addObjectActionTile(563, 3093, 3507, 0, 1, 1);
				// Altar.
				Region.addObjectActionTile(2640, 3093, 3513, 0, 2, 1);
				// Ladder
				Region.addObjectActionTile(11, 3097, 3507, 0, 2, 1);

				// Duel arena chest opened.
				Region.addObjectActionTile(3194, 3381, 3269, 0, 1, 1);
				Region.addObjectActionTile(3194, 3382, 3270, 0, 1, 1);
				Region.addObjectActionTile(29300, 3327, 4757, 0, 1, 1); // Box of health at clan wars
				Region.addObjectActionTile(3194, 3328, 4757, 0, 1, 1); // Opened chest at clan wars

				// Gate at resource wilderness.
				Region.addObjectActionTile(26760, 3184, 3944, 0, 0, 0);

				// Oak tree at Entrana.
				Region.addObjectActionTile(1751, 2852, 3332, 0, 3, 3);
				// Yew tree at Entrana.
				Region.addObjectActionTile(1753, 2848, 3332, 0, 3, 3);
				// Willow tree at Entrana.
				Region.addObjectActionTile(1760, 2850, 3338, 0, 2, 2);
				// Magic tree at Entrana.
				Region.addObjectActionTile(1761, 2853, 3338, 0, 1, 1);

				// Anvil at Entrana
				Region.addObjectActionTile(2031, 2833, 3349, 0, 1, 1);

				// Altar at Entrana for Runecrafting
				Region.addObjectActionTile(14897, 2857, 3380, 0, 3, 3);

				// Bank booths at Entrana bank.
				Region.addObjectActionTile(6943, 2860, 3338, 0, 1, 1);
				Region.addObjectActionTile(6943, 2861, 3338, 0, 1, 1);

				// Bank booth at Donator zone
				Region.addObjectActionTile(6943, 2192, 3250, 0, 1, 1);

				// Bank booth at Zombie waiting room.
				Region.addObjectActionTile(6943, 3658, 3513, 0, 1, 1);

				// Deposit box at Rune essence mine.
				Region.addObjectActionTile(6948, 2911, 4832, 0, 1, 1);

				Region.addObjectActionTile(14841, 2148, 3864, 0, 1, 1); // Portal at Astral altar
				Region.addObjectActionTile(14841, 1727, 3825, 0, 1, 1); // Portal at Blood altar
				Region.addObjectActionTile(14841, 1820, 3862, 0, 1, 1); // Portal at Soul altar

				// Falador dwarf mine.
				Region.addObjectActionTile(2031, 3042, 9746, 0, 1, 1); // Anvil.
				Region.addObjectActionTile(24009, 3037, 9746, 0, 3, 3); // Furnace.
				Region.addObjectActionTile(6943, 3043, 9736, 0, 1, 1); // Bank booth.
				Region.addObjectActionTile(7494, 3044, 9753, 0, 1, 1); // Runite ore
				Region.addObjectActionTile(7494, 3048, 9754, 0, 1, 1); // Runite ore
				Region.addObjectActionTile(7494, 3048, 9747, 0, 1, 1); // Runite ore
				Region.addObjectActionTile(7494, 3045, 9744, 0, 1, 1); // Runite ore
				Region.addObjectActionTile(7494, 3048, 9743, 0, 1, 1); // Runite ore
				Region.addObjectActionTile(7493, 3046, 9735, 0, 1, 1); // Adamant ore
				Region.addObjectActionTile(7493, 3047, 9737, 0, 1, 1); // Adamant ore
				Region.addObjectActionTile(7493, 3049, 9735, 0, 1, 1); // Adamant ore
				Region.addObjectActionTile(7493, 3049, 9734, 0, 1, 1); // Adamant ore
				Region.addObjectActionTile(7493, 3049, 9740, 0, 1, 1); // Adamant ore
				Region.addObjectActionTile(7492, 3045, 9741, 0, 1, 1); // Mithril ore
				Region.addObjectActionTile(7492, 3045, 9738, 0, 1, 1); // Mithril ore
				Region.addObjectActionTile(7492, 3046, 9742, 0, 1, 1); // Mithril ore
				Region.addObjectActionTile(7492, 3043, 9743, 0, 1, 1); // Mithril ore
				Region.addObjectActionTile(7489, 3032, 9741, 0, 1, 1); // Coal ore
				Region.addObjectActionTile(7489, 3034, 9739, 0, 1, 1); // Coal ore
				Region.addObjectActionTile(7455, 3035, 9742, 0, 1, 1); // Iron ore
				Region.addObjectActionTile(7455, 3037, 9739, 0, 1, 1); // Iron ore
				Region.addObjectActionTile(7455, 3037, 9735, 0, 1, 1); // Iron ore
				Region.addObjectActionTile(7455, 3035, 9734, 0, 1, 1); // Iron ore
				Region.addObjectActionTile(7485, 3032, 9737, 0, 1, 1); // Tin ore
				Region.addObjectActionTile(7485, 3031, 9739, 0, 1, 1); // Tin ore
				Region.addObjectActionTile(7485, 3028, 9739, 0, 1, 1); // Tin ore
				Region.addObjectActionTile(7484, 3028, 9737, 0, 1, 1); // Copper ore
				Region.addObjectActionTile(7484, 3029, 9734, 0, 1, 1); // Copper ore
				Region.addObjectActionTile(7484, 3032, 9735, 0, 1, 1); // Copper ore

				for (int i = 0; i < 4; i++)
				{
						for (int a = 0; a < 4; a++)
						{
								Region.addObjectActionTile(Farming.PATCH_HERBS, Farming.patchX + i, Farming.patchY + a, 0, 1, 1); // Copper ore
						}
				}

				for (int i = 0; i < 4; i++)
				{
						for (int a = 0; a < 4; a++)
						{
								Region.addObjectActionTile(Farming.PATCH_CLEAN, Farming.patchX + i, Farming.patchY + a, 0, 1, 1); // Copper ore
						}
				}

				Region.addObject(3194, 3090, 3507, 0, 10, 3, false); // Chest.
				Region.addObject(3194, 3092, 3514, 0, 10, 0, false); // Chest.
				Region.addObject(3194, 3080, 3514, 0, 10, 0, false); // Chest.

				// Gates in the wilderness and double doors.
				Region.addObject(1728, 3008, 3849, 0, 0, 3, true);
				Region.addObject(1728, 3008, 3849, 0, 0, 3, true);
				Region.addObject(1727, 3008, 3850, 0, 0, 1, true);
				Region.addObject(1727, 2947, 3904, 0, 0, 0, true);
				Region.addObject(1728, 2948, 3904, 0, 0, 2, true);
				Region.addObject(1727, 3224, 3904, 0, 0, 0, true);
				Region.addObject(1728, 3225, 3904, 0, 0, 2, true);
				Region.addObject(1727, 3336, 3896, 0, 0, 0, true);
				Region.addObject(1728, 3337, 3896, 0, 0, 2, true);
				Region.addObject(1524, 2957, 3821, 0, 0, 1, true);
				Region.addObject(1521, 2957, 3820, 0, 0, 3, true);
				Region.addObject(1727, 3201, 3856, 0, 0, 0, true);
				Region.addObject(1728, 3202, 3856, 0, 0, 2, true);
				Region.addObject(14752, 3022, 3631, 0, 0, 3, true);
				Region.addObject(14751, 3022, 3632, 0, 0, 1, true);

				Region.addObject(1728, 3040, 10308, 0, 0, 1, true);
				Region.addObject(1727, 3040, 10307, 0, 0, 3, true);

				Region.addObject(1728, 3044, 10342, 0, 0, 1, true);
				Region.addObject(1727, 3044, 10341, 0, 0, 3, true);

				Region.addObject(1728, 3022, 10312, 0, 0, 1, true);
				Region.addObject(1727, 3022, 10311, 0, 0, 3, true);

				// Slayer tower no clip fix.
				Region.addObjectActionTile(1, 3433, 3534, 0, 1, 1);
				Region.addObjectActionTile(1, 3432, 3535, 0, 1, 1);
				Region.addObjectActionTile(1, 3425, 3534, 0, 1, 1);
				Region.addObjectActionTile(1, 3433, 3534, 1, 1, 1);
				Region.addObjectActionTile(1, 3432, 3535, 1, 1, 1);
				Region.addObjectActionTile(1, 3425, 3534, 1, 1, 1);
				Region.addObjectActionTile(1, 3434, 3532, 1, 1, 1);
				Region.addObjectActionTile(1, 3435, 3532, 1, 1, 1);

				// Edgeville dungeon gate
				Region.addObjectActionTile(1727, 3104, 9909, 0, 0, 0);
				Region.addObjectActionTile(1728, 3104, 9910, 0, 0, 0);
				Region.addObjectActionTile(1728, 3131, 9918, 0, 0, 0);
				Region.addObjectActionTile(1727, 3132, 9918, 0, 0, 0);
				Region.addObjectActionTile(1728, 3105, 9945, 0, 0, 0);
				Region.addObjectActionTile(1727, 3106, 9945, 0, 0, 0);
				Region.addObjectActionTile(1728, 3146, 9871, 0, 0, 0);
				Region.addObjectActionTile(1727, 3146, 9870, 0, 0, 0);

				// Ladder that leads down to black demon area inside Edgeville dungeon.
				Region.addObjectActionTile(16680, 3088, 3571, 0, 1, 1);

				// Lever in wilderness that leads to Ardougne
				Region.addObjectActionTile(1817, 3153, 3923, 0, 0, 0);

				// Resource wilderness area fence
				addSpecificDirectionClipping(3183, 3945, 0, CANNOT_ENTER_FROM_ANY_SOUTH_DIRECTION);
				addSpecificDirectionClipping(3183, 3944, 0, CANNOT_ENTER_FROM_ANY_NORTH_DIRECTION);
				addSpecificDirectionClipping(3184, 3945, 0, CANNOT_ENTER_FROM_ANY_SOUTH_DIRECTION);
				addSpecificDirectionClipping(3184, 3944, 0, CANNOT_ENTER_FROM_ANY_NORTH_DIRECTION);



				//1, cannot enter from north west
				//4, cannot enter from north east
				//16, cannot enter from south east
				//64, cannot enter from south west

				//130, cannot enter from all the north and west blocks.
				//10
				//40
				//160

				//65536
				//1024
				//4096
				//16384

				//512
				//2048
				//8192
				//32768
		}
}
