package game.content.highscores;

import game.player.Player;

/**
 * Load all highscore systems here.
 * @author m g t
 *
 */
public class Highscores
{

		/**
		 * Initiate all highscores instances.
		 */
		public static void initiateHighscoresInstance()
		{
				HighscoresPure.getInstance().initiateHighscoresInstance();
				//HighscoresTotalLevelVeteran.getInstance().initiateHighscoresInstance();
				HighscoresTotalLevel.getInstance().initiateHighscoresInstance();
				//HighscoresTotalLevelIronMan.getInstance().initiateHighscoresInstance();
				HighscoresBerserker.getInstance().initiateHighscoresInstance();
				HighscoresMelee.getInstance().initiateHighscoresInstance();
				HighscoresHybrid.getInstance().initiateHighscoresInstance();
				HighscoresRangedTank.getInstance().initiateHighscoresInstance();
				//HighscoresAdventurer.getInstance().initiateHighscoresInstance();
				HighscoresPker.getInstance().initiateHighscoresInstance();
				HighscoresPvm.getInstance().initiateHighscoresInstance();
				//HighscoresBarrowsRun.getInstance().initiateHighscoresInstance();
				//HighscoresZombie.getInstance().initiateHighscoresInstance();
				HighscoresTournament.getInstance().initiateHighscoresInstance();
				HighscoresDaily.getInstance().initiateHighscoresInstance();
				HighscoresF2p.getInstance().initiateHighscoresInstance();
		}

		/**
		 * Save all highscores files.
		 */
		public static void saveHighscoresFiles()
		{
				HighscoresPure.getInstance().saveFile();
				//HighscoresTotalLevelVeteran.getInstance().saveFile();
				HighscoresTotalLevel.getInstance().saveFile();
				//HighscoresTotalLevelIronMan.getInstance().saveFile();
				HighscoresBerserker.getInstance().saveFile();
				HighscoresMelee.getInstance().saveFile();
				HighscoresHybrid.getInstance().saveFile();
				HighscoresRangedTank.getInstance().saveFile();
				//HighscoresAdventurer.getInstance().saveFile();
				HighscoresPker.getInstance().saveFile();
				HighscoresPvm.getInstance().saveFile();
				//HighscoresBarrowsRun.getInstance().saveFile();
				//HighscoresZombie.getInstance().saveFile();
				HighscoresTournament.getInstance().saveFile();
				HighscoresDaily.getInstance().saveFile();
				HighscoresF2p.getInstance().saveFile();
		}

		/**
		 * Sort all highscores depending on variable changed.
		 * @param player
		 * 			The associated player.
		 */
		public static void sortHighscoresOnLogOut(Player player)
		{
				//HighscoresTotalLevelVeteran.getInstance().sortHighscores(player);
				HighscoresTotalLevel.getInstance().sortHighscores(player);
				//HighscoresTotalLevelIronMan.getInstance().sortHighscores(player);
				//HighscoresAdventurer.getInstance().sortHighscores(player);
				HighscoresPvm.getInstance().sortHighscores(player);
		}

}