package game.content.music;

import game.player.Player;

public class SoundSystem
{

	public static void sendSound(Player player, Player victim, int id, int delay)
	{
		sendSound(player, id, 0, delay, 8);
		if (victim != null)
		{
			sendSound(victim, id, 0, delay, 8);
		}
	}

	static int[] nonSpammableSounds =
	{
		221, 464
	};

	public static void sendSound(Player player, int id, int delay)
	{
		for (int i = 0; i < nonSpammableSounds.length; i++)
		{
			if (id == nonSpammableSounds[i] && System.currentTimeMillis() - player.lastSpammedSoundTime < 2000)
			{
				return;
			}
			else if (id == nonSpammableSounds[i])
			{
				player.lastSpammedSoundTime = System.currentTimeMillis();
			}
		}
		if (id == 317)
		{
			if (System.currentTimeMillis() - player.timeSentFoodSound < 600)
			{
				return;
			}
			player.timeSentFoodSound = System.currentTimeMillis();
		}

		if (id == 334)
		{
			if (System.currentTimeMillis() - player.timeSentDrinkSound < 600)
			{
				return;
			}
			player.timeSentDrinkSound = System.currentTimeMillis();
		}
		sendSound(player, id, 0, delay, 8);
	}

	public static void sendSound(Player player, int id, int type, int delay, int volume)
	{
		if (player.isBot)
		{
			return;
		}
		if (player.getOutStream() != null && player != null && id != -1)
		{
			player.getOutStream().createFrame(174);
			player.getOutStream().writeWord(id);
			player.getOutStream().writeByte(type);
			player.getOutStream().writeWord(delay);
			player.getOutStream().writeWord(volume);
			player.flushOutStream();
		}
	}
}
