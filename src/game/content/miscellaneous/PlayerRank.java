package game.content.miscellaneous;

/**
 * Easy way to edit player rank icons.
 * @author MGT Madness, created on 18-01-206.
 */
public class PlayerRank
{

		/**
		 * Icons for clan chat, yell and profile.
		 */
		public static String getIconText(String rankIcon, int rank, boolean space)
		{
				if (rank == 0)
				{
						return rankIcon;
				}
				else
				{
						return "<img=" + rank + ">" + (space ? " " : "");
				}
		}

		/**
		 * Rank name for profile.
		 */
		public static String getRankName(String rankIcon, int rank)
		{
				if (rank == 2)
				{
						return "Administrator";
				}
				else if (rank == 1)
				{
						return "Moderator";
				}
				else if (rank == 3)
				{
						return "Donator";
				}
				else if (rank == 4)
				{
						return "Super Donator";
				}
				else if (rank == 5)
				{
						return "Extreme Donator";
				}
				else if (rank == 6)
				{
						return "Legendary Donator";
				}
				else if (rank == 9)
				{
						return "Iron Man";
				}
				return rankIcon;
		}

}
