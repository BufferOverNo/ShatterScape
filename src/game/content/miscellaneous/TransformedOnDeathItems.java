package game.content.miscellaneous;

/**
 * Imbued rings.
 * @author MGT Madness, created on 19-04-2016.
 */
public class TransformedOnDeathItems
{

		public static enum TransformedOnDeathData
		{
				BERSERKER(6737, 15220),
				ARCHER(6733, 15019),
				SEER(6731, 15018),
				WARRIOR(6735, 15020),
				TREASONOUS(18667, 18672),
				RING_OF_THE_GODS(18669, 18673),
				TYRANNICAL(18665, 18671),
				RING_OF_SUFFERING_I(18811, 18813),
				RING_OF_SUFFERING_RI(18811, 18814),
				SARA_BLESSED_SWORD(11730, 18660),
				GRANITE_MAUL_OR(4153, 18662);

				private int normalId;

				private int specialId;


				private TransformedOnDeathData(int normalRingId, int imbuedRingId)
				{
						this.normalId = normalRingId;
						this.specialId = imbuedRingId;
				}

				public int getNormalId()
				{
						return normalId;
				}

				public int getSpecialId()
				{
						return specialId;
				}


		}
}
