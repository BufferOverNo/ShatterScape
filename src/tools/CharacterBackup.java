package tools;

import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import core.Server;
import core.ServerConfiguration;
import game.player.PlayerHandler;
import utility.Misc;

public class CharacterBackup implements Runnable
{

		public static String CHARACTER_FOLDER = "./backup/";

		public static String BACKUP_FOLDER = "";

		/**
		 * Gets the date.
		 * 
		 * @return the date
		 */
		public static String getDate()
		{
				String output = Misc.getDate();
				output = output.replaceAll(" ", "");
				output = output.replaceAll("/", "-");
				output = output.replaceAll(":", "-");
				output = output.replaceAll(",", " (");
				return output;
		}


		/**
		 * Store the time of when the backup was last done.
		 */
		@SuppressWarnings("unused")
		private static long timeBackedUp = System.currentTimeMillis();

		@SuppressWarnings("unused")
		private final static int HOURS_BACKUP = 24;

		public static void backUpSystem()
		{


				if (ServerConfiguration.DEBUG_MODE)
				{
						return;
				}
				if (PlayerHandler.canTakeAction)
				{
						if (PlayerHandler.restart)
						{
								Server.restart();
						}
						if (PlayerHandler.logOut)
						{
								System.exit(0);
						}
						PlayerHandler.canTakeAction = false;
				}
				/*// Disconnects all players for some reason.
				long time = System.currentTimeMillis();
				
				// 24 hours.
				if (System.currentTimeMillis() - timeBackedUp > (HOURS_BACKUP * 3600000))
				{
						Server.playerHandler.serverRestartContentUpdate(false, false);
				}
				GameTickLog.characterBackupTickDuration = System.currentTimeMillis() - time;
				*/
		}

		@Override
		public void run()
		{
				createBackUp();

		}

		public static void createBackUp()
		{
				timeBackedUp = System.currentTimeMillis();
				long time = System.currentTimeMillis();
				File folder = new File(CHARACTER_FOLDER);
				BACKUP_FOLDER = "archives/" + getDate() + ").zip";
				File zipped = new File(BACKUP_FOLDER);
				if (!zipped.exists())
				{
						try
						{
								if (folder.list().length == 0)
								{
										return;
								}
								zipDirectory(folder, zipped);

								PlayerHandler.canTakeAction = true;
								Misc.print("Backup time elapsed: " + (System.currentTimeMillis() - time));
						}
						catch (Exception e)
						{
								e.printStackTrace();
						}
				}
				else
				{
						PlayerHandler.canTakeAction = true;
						Misc.print("Backup already exists.");
				}
				File execute = new File("economy server only.bat");
				try
				{
						Desktop.getDesktop().open(execute);
				}
				catch (IOException e)
				{
						e.printStackTrace();
				}
		}

		/**
		 * Zip.
		 * 
		 * @param directory
		 *            the directory
		 * @param base
		 *            the base
		 * @param zos
		 *            the zos
		 * @throws IOException
		 *             Signals that an I/O exception has occurred.
		 */
		private static final void zip(File directory, File base, ZipOutputStream zos) throws IOException
		{
				File[] files = directory.listFiles();
				byte[] buffer = new byte[20000];
				int read = 0;
				for (int i = 0, n = files.length; i < n; i++)
				{
						if (files[i].isDirectory())
						{
								zip(files[i], base, zos);
						}
						else
						{
								FileInputStream in = new FileInputStream(files[i]);
								ZipEntry entry = new ZipEntry(files[i].getPath().substring(base.getPath().length() + 1));
								zos.putNextEntry(entry);
								while (-1 != (read = in.read(buffer)))
								{
										zos.write(buffer, 0, read);
								}
								in.close();
						}
				}
		}

		/**
		 * Zip directory.
		 * 
		 * @param folder
		 *            the f
		 * @param zf
		 *            the zf
		 * @throws IOException
		 *             Signals that an I/O exception has occurred.
		 */
		public static final void zipDirectory(File folder, File zf) throws IOException
		{
				ZipOutputStream zipped = new ZipOutputStream(new FileOutputStream(zf));
				zip(folder, folder, zipped);
				zipped.close();
		}
}