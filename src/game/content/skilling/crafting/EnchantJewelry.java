package game.content.skilling.crafting;

import java.util.HashMap;
import java.util.Map;

import core.ServerConstants;
import game.content.skilling.Skilling;
import game.item.ItemAssistant;
import game.player.Player;
import game.player.event.CycleEvent;
import game.player.event.CycleEventContainer;
import game.player.event.CycleEventHandler;

public class EnchantJewelry
{

		public static enum BoltEnchantData
		{
				// Use any enchant on a bolt and interface will be opening
				SAPPHIRE(76103, 1176, 7, new int[][] {{558, 1}, {555, 1}, {564, 1}}, 9337, 9240, 50),
				EMERALD(76104, 1176, 27, new int[][] {{556, 3}, {561, 1}, {564, 1}}, 9338, 9241, 55),
				RUBY(76099, 1176, 49, new int[][] {{564, 1}, {554, 5}, {565, 1}}, 9339, 9242, 59),
				DIAMOND(76100, 1180, 57, new int[][] {{564, 1}, {557, 10}, {563, 2}}, 9340, 9243, 67),
				DRAGONSTONE(76101, 1187, 68, new int[][] {{564, 1}, {557, 15}, {566, 1}}, 9341, 9244, 78),
				ONYX(76102, 6003, 87, new int[][] {{564, 1}, {554, 20}, {560, 1}}, 9342, 9245, 97);

				private int[][] runeReq;

				private int buttonId, spellId, levelReq, startProduct, finProduct;

				private double expEarned;

				private BoltEnchantData(int buttonId, int spellId, int levelReq, int[][] runeReq, int startProduct, int finProduct, double expEarned)
				{
						this.buttonId = buttonId;
						this.spellId = spellId;
						this.levelReq = levelReq;
						this.runeReq = runeReq;
						this.startProduct = startProduct;
						this.finProduct = finProduct;
						this.expEarned = expEarned;
				}

				public int getButtonId()
				{
						return buttonId;
				}

				public int getSpellId()
				{
						return spellId;
				}

				public double getExp()
				{
						return expEarned;
				}

				public int getLevelRequirement()
				{
						return levelReq;
				}

				public int getEnchantedBolt()
				{
						return finProduct;
				}

				public int getUnEnchantedBolt()
				{
						return startProduct;
				}

				public int[][] getRuneRequirement()
				{
						return runeReq;
				}
		}

		public static void enchantBolt(Player player, int spellId, int itemId)
		{
				for (BoltEnchantData data : BoltEnchantData.values())
				{
						if (itemId == data.getUnEnchantedBolt())
						{
								player.getPA().displayInterface(19530);
								return;
						}
				}
		}

		public static boolean isEnchantBoltButton(Player player, int buttonId)
		{
				for (BoltEnchantData data : BoltEnchantData.values())
				{
						if (data.getButtonId() == buttonId)
						{
								if (System.currentTimeMillis() - player.enchantDelay <= 1200)
								{
										return true;
								}
								enchantEvent(player, data);
								return true;
						}
				}
				return false;
		}

		private static void enchantEvent(final Player player, final BoltEnchantData data)
		{
				if (player.enchantEvent)
				{
						return;
				}
				player.enchantEvent = true;
				player.getPA().closeInterfaces();
				CycleEventHandler.getSingleton().addEvent(player, new CycleEvent()
				{
						@Override
						public void execute(CycleEventContainer container)
						{
								if (!player.enchantEvent)
								{
										container.stop();
										return;
								}
								if (!ItemAssistant.hasItemAmountInInventory(player, data.getRuneRequirement()[0][0], data.getRuneRequirement()[0][1]) || !ItemAssistant.hasItemAmountInInventory(player, data.getRuneRequirement()[1][0], data.getRuneRequirement()[1][1]) || !ItemAssistant.hasItemAmountInInventory(player, data.getRuneRequirement()[2][0], data.getRuneRequirement()[2][1]))
								{
										container.stop();
										player.getPA().sendMessage("You need " + data.getRuneRequirement()[0][1] + " " + ItemAssistant.getItemName(data.getRuneRequirement()[0][0]) + ", " + data.getRuneRequirement()[1][1] + " " + ItemAssistant.getItemName(data.getRuneRequirement()[1][0]) + " and " + data.getRuneRequirement()[2][1] + " " + ItemAssistant.getItemName(data.getRuneRequirement()[2][0]) + ".");
										return;
								}
								if (!ItemAssistant.hasItemInInventory(player, data.getUnEnchantedBolt()))
								{
										player.getPA().sendMessage("You have run out of " + ItemAssistant.getItemName(data.getUnEnchantedBolt()) + ".");
										container.stop();
										return;
								}
								int amount = 10;
								int boltAmount = ItemAssistant.getItemAmount(player, data.getUnEnchantedBolt());
								if (amount > boltAmount)
								{
										amount = boltAmount;
								}
								if (amount == 0)
								{
										container.stop();
										return;
								}
								player.startAnimation(720);
								player.gfx100(114);
								ItemAssistant.deleteItemFromInventory(player, data.getUnEnchantedBolt(), amount);
								ItemAssistant.addItem(player, data.getEnchantedBolt(), amount);
								Skilling.addSkillExperience(player, (int) data.getExp(), ServerConstants.MAGIC);
								ItemAssistant.deleteItemFromInventory(player, data.getRuneRequirement()[0][0], data.getRuneRequirement()[0][1]);
								ItemAssistant.deleteItemFromInventory(player, data.getRuneRequirement()[1][0], data.getRuneRequirement()[1][1]);
								ItemAssistant.deleteItemFromInventory(player, data.getRuneRequirement()[2][0], data.getRuneRequirement()[2][1]);
								player.enchantDelay = System.currentTimeMillis();
						}

						@Override
						public void stop()
						{
								player.enchantEvent = false;
								player.getPA().stopAllActions();
						}
				}, 4);

		}

		public enum Enchant
		{

				SAPPHIRERING(1637, 2550, 7, 18, 719, 114, 1),
				SAPPHIREAMULET(1694, 1727, 7, 18, 719, 114, 1),
				SAPPHIRENECKLACE(1656, 3853, 7, 18, 719, 114, 1),

				EMERALDRING(1639, 2552, 27, 37, 719, 114, 2),
				EMERALDAMULET(1696, 1729, 27, 37, 719, 114, 2),
				EMERALDNECKLACE(1658, 5521, 27, 37, 719, 114, 2),

				RUBYRING(1641, 2568, 47, 59, 720, 115, 3),
				RUBYAMULET(1698, 1725, 47, 59, 720, 115, 3),
				RUBYNECKLACE(1660, 11194, 47, 59, 720, 115, 3),

				DIAMONDRING(1643, 2570, 57, 67, 720, 115, 4),
				DIAMONDAMULET(1700, 1731, 57, 67, 720, 115, 4),
				DIAMONDNECKLACE(1662, 11090, 57, 67, 720, 115, 4),

				DRAGONSTONERING(1645, 2572, 68, 78, 721, 116, 5),
				DRAGONSTONEAMULET(1702, 1712, 68, 78, 721, 116, 5),
				DRAGONSTONENECKLACE(1664, 11105, 68, 78, 721, 116, 5),

				ONYXRING(6575, 6583, 87, 97, 721, 452, 6),
				ONYXAMULET(6581, 6585, 87, 97, 721, 452, 6),
				ONYXNECKLACE(6577, 11128, 87, 97, 721, 452, 6);

				int unenchanted, enchanted, levelReq, xpGiven, anim, gfx, reqEnchantmentLevel;

				private Enchant(int unenchanted, int enchanted, int levelReq, int xpGiven, int anim, int gfx, int reqEnchantmentLevel)
				{
						this.unenchanted = unenchanted;
						this.enchanted = enchanted;
						this.levelReq = levelReq;
						this.xpGiven = xpGiven;
						this.anim = anim;
						this.gfx = gfx;
						this.reqEnchantmentLevel = reqEnchantmentLevel;
				}

				public int getUnenchanted()
				{
						return unenchanted;
				}

				public int getEnchanted()
				{
						return enchanted;
				}

				public int getLevelReq()
				{
						return levelReq;
				}

				public int getXp()
				{
						return xpGiven;
				}

				public int getAnim()
				{
						return anim;
				}

				public int getGFX()
				{
						return gfx;
				}

				public int getELevel()
				{
						return reqEnchantmentLevel;
				}

				private static final Map<Integer, Enchant> enc = new HashMap<Integer, Enchant>();

				public static Enchant forId(int itemID)
				{
						return enc.get(itemID);
				}

				static
				{
						for (Enchant en : Enchant.values())
						{
								enc.put(en.getUnenchanted(), en);
						}
				}
		}

		private enum EnchantSpell
		{

				SAPPHIRE(1155, 555, 1, 564, 1, -1, 0),
				EMERALD(1165, 556, 3, 564, 1, -1, 0),
				RUBY(1176, 554, 5, 564, 1, -1, 0),
				DIAMOND(1180, 557, 10, 564, 1, -1, 0),
				DRAGONSTONE(1187, 555, 15, 557, 15, 564, 1),
				ONYX(6003, 557, 20, 554, 20, 564, 1);

				int spell, reqRune1, reqAmtRune1, reqRune2, reqAmtRune2, reqRune3, reqAmtRune3;

				private EnchantSpell(int spell, int reqRune1, int reqAmtRune1, int reqRune2, int reqAmtRune2, int reqRune3, int reqAmtRune3)
				{
						this.spell = spell;
						this.reqRune1 = reqRune1;
						this.reqAmtRune1 = reqAmtRune1;
						this.reqRune2 = reqRune2;
						this.reqAmtRune2 = reqAmtRune2;
						this.reqRune3 = reqRune3;
						this.reqAmtRune3 = reqAmtRune3;
				}

				public int getSpell()
				{
						return spell;
				}

				public int getReq1()
				{
						return reqRune1;
				}

				public int getReqAmt1()
				{
						return reqAmtRune1;
				}

				public int getReq2()
				{
						return reqRune2;
				}

				public int getReqAmt2()
				{
						return reqAmtRune2;
				}

				public int getReq3()
				{
						return reqRune3;
				}

				public int getReqAmt3()
				{
						return reqAmtRune3;
				}


				public static final Map<Integer, EnchantSpell> ens = new HashMap<Integer, EnchantSpell>();

				public static EnchantSpell forId(int id)
				{
						return ens.get(id);
				}

				static
				{
						for (EnchantSpell en : EnchantSpell.values())
						{
								ens.put(en.getSpell(), en);
						}
				}

		}

		private static boolean hasRunes(Player c, int spellID)
		{
				EnchantSpell ens = EnchantSpell.forId(spellID);
				if (ens.getReq3() == 0)
				{
						return ItemAssistant.hasItemAmountInInventory(c, ens.getReq1(), ens.getReqAmt1()) && ItemAssistant.hasItemAmountInInventory(c, ens.getReq2(), ens.getReqAmt2()) && ItemAssistant.hasItemAmountInInventory(c, ens.getReq3(), ens.getReqAmt3());
				}
				else
				{
						return ItemAssistant.hasItemAmountInInventory(c, ens.getReq1(), ens.getReqAmt1()) && ItemAssistant.hasItemAmountInInventory(c, ens.getReq2(), ens.getReqAmt2());
				}
		}

		private static int getEnchantmentLevel(int spellID)
		{
				switch (spellID)
				{
						case 1155: //Lvl-1 enchant sapphire
								return 1;
						case 1165: //Lvl-2 enchant emerald
								return 2;
						case 1176: //Lvl-3 enchant ruby
								return 3;
						case 1180: //Lvl-4 enchant diamond
								return 4;
						case 1187: //Lvl-5 enchant dragonstone
								return 5;
						case 6003: //Lvl-6 enchant onyx
								return 6;
				}
				return 0;
		}

		public static void enchantItem(Player player, int itemID, int spellID, int itemSlot)
		{
				Enchant enc = Enchant.forId(itemID);
				EnchantSpell ens = EnchantSpell.forId(spellID);
				if (enc == null || ens == null)
				{
						return;
				}
				if (System.currentTimeMillis() - player.enchantDelay <= 1200)
				{
						return;
				}
				if (player.baseSkillLevel[ServerConstants.MAGIC] >= enc.getLevelReq())
				{
						if (ItemAssistant.hasItemAmountInInventory(player, enc.getUnenchanted(), 1))
						{
								if (hasRunes(player, spellID))
								{
										if (getEnchantmentLevel(spellID) == enc.getELevel())
										{
												ItemAssistant.deleteItemFromInventory(player, enc.getUnenchanted(), itemSlot, 1);
												ItemAssistant.addItemToInventory(player, enc.getEnchanted(), 1, itemSlot, false);
												Skilling.addSkillExperience(player, enc.getXp(), ServerConstants.MAGIC);
												ItemAssistant.deleteItemFromInventory(player, ens.getReq1(), ItemAssistant.getItemSlot(player, ens.getReq1()), ens.getReqAmt1());
												ItemAssistant.deleteItemFromInventory(player, ens.getReq2(), ItemAssistant.getItemSlot(player, ens.getReq2()), ens.getReqAmt2());
												player.startAnimation(enc.getAnim());
												player.gfx100(enc.getGFX());
												player.enchantDelay = System.currentTimeMillis();
												if (ens.getReq3() != -1)
												{
														ItemAssistant.deleteItemFromInventory(player, ens.getReq3(), ItemAssistant.getItemSlot(player, ens.getReq3()), ens.getReqAmt3());
												}
												player.getPA().sendFrame106(6);
										}
										else
										{
												player.getPA().sendMessage("You can only enchant this jewelry using a level-" + enc.getELevel() + " enchantment spell!");
										}
								}
								else
								{
										player.getPA().sendMessage("You do not have enough runes to cast this spell.");
								}
						}
				}
				else
				{
						player.getPA().sendMessage("You need a magic level of at least " + enc.getLevelReq() + " to cast this spell.");
				}
		}
}