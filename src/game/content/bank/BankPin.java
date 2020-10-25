package game.content.bank;

import game.player.Player;
import utility.Misc;

public class BankPin
{

	private enum State
	{
		ONE,
		TWO,
		THREE,
		FOUR,
	}

	private static State state = State.ONE;

	private static int bankPins[] =
	{
		-1, -1, -1, -1, -1, -1, -1, -1, -1, -1
	};

	private static int actionButtons[] =
	{
		58025, 58026, 58027, 58028, 58029, 58030, 58031, 58032, 58033, 58034
	};

	public static void close(Player player)
	{
		player.enteredPin = "";
		player.getPA().closeInterfaces();
		state = State.ONE;
	}

	private static void enterPin(Player player, int button, State which)
	{
		for (int i = 0; i < getActionButtons().length; i++)
		{
			if (getActionButtons()[i] == button)
			{
				player.enteredPin += getBankPins()[i] + "";
			}
		}
		switch (which)
		{
			case ONE:
				state = State.TWO;
				resend(player);
				break;
			case TWO:
				state = State.THREE;
				resend(player);
				break;
			case THREE:
				state = State.FOUR;
				resend(player);
				break;
			case FOUR:
				if (!player.setPin)
				{
					player.bankPin = player.enteredPin.trim();
					player.fullPin = player.enteredPin.trim();
					player.setPin = true;
					player.playerAssistant.sendMessage("You have successfully set your bankpin to <col=255>" + player.bankPin + ".");
					player.playerAssistant.sendMessage("Please do not forget your bank pin, write it down on your computer or write it on a");
					player.playerAssistant.sendMessage("piece of paper.");
					resend(player);
				}
				else
				{
					if (player.bankPin.equalsIgnoreCase(player.enteredPin.trim()))
					{
						player.playerAssistant.sendMessage("You have successfully entered your bank pin which is <col=255>" + player.bankPin + ".");
						player.fullPin = player.enteredPin.trim();
						player.hasEnteredPin = true;
						resend(player);
					}
					else
					{
						player.playerAssistant.sendMessage("The pin you entered is incorrect.");
						close(player);
					}
				}
				state = State.ONE;
				break;
		}
	}

	private static int[] getActionButtons()
	{
		return actionButtons;
	}

	private static int[] getBankPins()
	{
		return bankPins;
	}

	public static String getFullPin(Player player)
	{
		return player.fullPin;
	}

	private static void mixNumbers(Player player)
	{
		for (int i = 0; i < bankPins.length; i++)
		{
			bankPins[i] = -1;
		}
		for (int i = 0; i < bankPins.length; i++)
		{
			for (int i2 = 0; i2 < 9999; i2++)
			{
				boolean can = true;
				int random = Misc.random(9);
				for (int i3 = 0; i3 < bankPins.length; i3++)
				{
					if (random == bankPins[i3])
					{
						can = false;
						random = Misc.random(9);
					}
				}
				if (!can)
				{
					continue;
				}
				else
				{
					bankPins[i] = random;
					break;
				}
			}
		}
		sendPins(player);
	}

	public static void open(Player player)
	{
		player.setUsingBankInterface(true);
		if (!(player.fullPin.equalsIgnoreCase("")))
		{
			Bank.openUpBank(player, player.bankingTab, true, true);
			return;
		}
		player.getPA().displayInterface(7424);
		resend(player);
		state = State.ONE;
	}

	public static void pinEnter(Player player, int button)
	{
		if (!Bank.hasBankingRequirements(player, false))
		{
			return;
		}
		switch (state)
		{
			case ONE:
			case TWO:
			case THREE:
			case FOUR:
				enterPin(player, button, state);
				break;
		}
	}

	private static void resend(Player player)
	{
		if (!(player.fullPin.equalsIgnoreCase("")))
		{
			Bank.openUpBank(player, player.bankingTab, true, true);
			return;
		}
		mixNumbers(player);
		switch (state)
		{
			case ONE:
				player.getPA().sendFrame126("First click the FIRST digit", 15313);
				break;
			case TWO:
				player.getPA().sendFrame126("Then click the SECOND digit", 15313);
				player.getPA().sendFrame126("*", 14913);
				break;
			case THREE:
				player.getPA().sendFrame126("Then click the THIRD digit", 15313);
				player.getPA().sendFrame126("*", 14914);
				break;
			case FOUR:
				player.getPA().sendFrame126("And lastly click the FOURTH digit", 15313);
				player.getPA().sendFrame126("*", 14915);
				break;
		}
		sendPins(player);
	}

	public void reset(Player player)
	{
		player.bankPin = "";
		player.attempts = 3;
		player.enteredPin = "";
		player.fullPin = "";
	}

	private static void sendPins(Player player)
	{
		if (!(player.fullPin.equalsIgnoreCase("")))
		{
			Bank.openUpBank(player, player.bankingTab, true, true);
			return;
		}
		for (int i = 0; i < getBankPins().length; i++)
		{
			player.getPA().sendFrame126("" + getBankPins()[i], 14883 + i);
		}
	}
}