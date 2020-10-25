package game.npc.data;

public class NpcDefinition
{

		public static final NpcDefinition[] DEFINITIONS = new NpcDefinition[12000];

		public final int npcType;

		public final String name;

		public final int size;

		public final int roamDistance;

		public final int deathDeleteTicks;

		public final boolean aggressive;

		public final int attackAnimation;

		public final int blockAnimation;

		public final int deathAnimation;

		public final int attackSpeed;

		public final int attackDistance;

		public final int hitPoints;

		public final int attack;

		public final int meleeDefence;

		public final int rangedDefence;

		public final int magicDefence;

		public final int maximumDamage;

		public final int rangedMaximumDamage;

		public final int magicMaximumDamage;

		public final int respawnTicks;

		public final int rangedAttackAnimation;

		public final int magicAttackAnimation;

		public final int meleeHitsplatDelay;

		public final int rangedHitsplatDelay;

		public final int magicHitsplatDelay;

		public final boolean clever;


		public NpcDefinition(int npcType, String name, int size, int roamDistance, int deathDeleteTicks, boolean aggressive, int attackAnimation, int blockAnimation, int deathAnimation, int attackSpeed, int attackDistance, int hitPoints, int attack, int meleeDefence, int rangedDefence, int magicDefence, int maximumDamage, int rangedMaximumDamage, int magicMaximumDamage, int respawnTicks, int rangedAttackAnimation, int magicAttackAnimation, int meleeHitsplatDelay, int rangedHitsplatDelay, int magicHitsplatDelay, boolean clever)
		{
				this.npcType = npcType;
				this.name = name;
				this.size = size;
				this.roamDistance = roamDistance;
				this.deathDeleteTicks = deathDeleteTicks;
				this.aggressive = aggressive;
				this.attackAnimation = attackAnimation;
				this.blockAnimation = blockAnimation;
				this.deathAnimation = deathAnimation;
				this.attackSpeed = attackSpeed;
				this.attackDistance = attackDistance;
				this.hitPoints = hitPoints;
				this.attack = attack;
				this.meleeDefence = meleeDefence;
				this.rangedDefence = rangedDefence;
				this.magicDefence = magicDefence;
				this.maximumDamage = maximumDamage;
				this.rangedMaximumDamage = rangedMaximumDamage;
				this.magicMaximumDamage = magicMaximumDamage;
				this.respawnTicks = respawnTicks;
				this.rangedAttackAnimation = rangedAttackAnimation;
				this.magicAttackAnimation = magicAttackAnimation;
				this.meleeHitsplatDelay = meleeHitsplatDelay;
				this.rangedHitsplatDelay = rangedHitsplatDelay;
				this.magicHitsplatDelay = magicHitsplatDelay;
				this.clever = clever;

		}

		public static int getRoamDistance(int index)
		{
				if (getDefinitions()[index] == null)
				{
						return 1;
				}
				return getDefinitions()[index].roamDistance;
		}

		public static boolean getAggressive(int index)
		{
				if (getDefinitions()[index] == null)
				{
						return false;
				}
				return getDefinitions()[index].aggressive;
		}

		public static int getMagicDefence(int index)
		{
				if (getDefinitions()[index].magicDefence == 0)
				{
						return getDefinitions()[index].meleeDefence;
				}
				else
				{
						return getDefinitions()[index].magicDefence;
				}
		}

		public static int getRangedDefence(int index)
		{
				if (getDefinitions()[index].rangedDefence == 0)
				{
						return getDefinitions()[index].meleeDefence;
				}
				else
				{
						return getDefinitions()[index].rangedDefence;
				}
		}

		public static NpcDefinition[] getDefinitions()
		{
				return DEFINITIONS;
		}

}
