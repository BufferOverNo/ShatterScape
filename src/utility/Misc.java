package utility;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import core.ServerConfiguration;
import core.ServerConstants;

public class Misc
{
		private static final char[] validChars = {'_', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};

		public static String anA(String targetWord)
		{
				String anA = "a";
				String before = targetWord;
				targetWord = targetWord.toLowerCase();
				if (targetWord.startsWith("a") || targetWord.startsWith("e") || targetWord.startsWith("i") || targetWord.startsWith("o") || targetWord.startsWith("u"))
				{
						anA = "an";
				}
				return anA + " " + before;
		}

		public static String nameForLong(long l)
		{
				try
				{
						if (l <= 0L || l >= 0x5b5b57f8a98a5dd1L)
								return "invalid_name";
						if (l % 37L == 0L)
								return "invalid_name";
						int i = 0;
						char ac[] = new char[12];
						while (l != 0L)
						{
								long l1 = l;
								l /= 37L;
								ac[11 - i++] = validChars[(int) (l1 - l * 37L)];
						}
						return new String(ac, 12 - i, i);
				}
				catch (RuntimeException runtimeexception)
				{
				}
				throw new RuntimeException();
		}

		public static String getDate()
		{
				DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy, hh:mm: a");
				Calendar cal = Calendar.getInstance();
				return dateFormat.format(cal.getTime());
		}

		/**
		 * Format number into for example: 357,555
		 */
		public static String formatNumber(int number)
		{
				// Do not use return NumberFormat.getIntegerInstance().format(number);. It is 9 times slower.
				String string = Integer.toString(number);
				if (number < 1000)
				{
						return string;
				}
				if (number >= 1000 && number < 10000)
				{
						return string.substring(0, 1) + "," + string.substring(1);
				}
				if (number < 100000)
				{
						return string.substring(0, 2) + "," + string.substring(2);
				}
				if (number < 1000000)
				{
						return string.substring(0, 3) + "," + string.substring(3);
				}
				if (number < 10000000)
				{
						return string.substring(0, 1) + "," + string.substring(1, 4) + "," + string.substring(4, 7);
				}
				if (number < 100000000)
				{
						return string.substring(0, 2) + "," + string.substring(2, 5) + "," + string.substring(5, 8);
				}
				if (number < 1000000000)
				{
						return string.substring(0, 3) + "," + string.substring(3, 6) + "," + string.substring(6, 9);
				}
				if (number < Integer.MAX_VALUE)
				{
						return string.substring(0, 1) + "," + string.substring(1, 4) + "," + string.substring(4, 7) + "," + string.substring(7, 10);
				}
				return string;
		}

		/**
		 * % chance of this boolean turning true.
		 * @param chance
		 * 			The % amount
		 * @return
		 * 			chance is true
		 */
		public static boolean hasPercentageChance(int chance)
		{
				if (chance > 100)
				{
						chance = 100;
				}
				if (chance <= 0)
				{
						return false;
				}
				if (random(99) < chance)
				{
						return true;
				}
				return false;
		}

		/**
		 * Check if the String contains any offensive content.
		 * @param chat
		 * 			The chat to check for offensive content.
		 * @return
		 * 			True, if the chat contained offensive content.
		 */
		public static boolean checkForOffensive(String chat)
		{
				for (int i = 0; i < ServerConstants.offensiveLanguage.length; i++)
				{
						if (chat.toLowerCase().contains(ServerConstants.offensiveLanguage[i]))
						{
								return true;
						}
				}
				return false;
		}

		public static int random(int min, int max)
		{
				return min + (int) (Math.random() * ((max - min) + 1));
		}

		/**
		 * 1 out of the given integer to turn true.
		 * So if i put 150, then it means, there is a 1 in 150 chance to turn true.
		 */
		public static boolean hasOneOutOf(int chance)
		{
				if (chance <= 0)
				{
						chance = 1;
				}
				int min = 1;
				int result = min + (int) (Math.random() * ((chance - min) + 1));
				return result == 1 ? true : false;
		}

		/**
		 * Capitalize the first letter and other letters that before it is a space.
		 * @param s
		 * @return
		 */
		public static String capitalize(String s)
		{
				s = s.toLowerCase();
				for (int i = 0; i < s.length(); i++)
				{
						if (i == 0)
						{
								s = String.format("%s%s", Character.toUpperCase(s.charAt(0)), s.substring(1));
						}
						if (!Character.isLetterOrDigit(s.charAt(i)))
						{
								if (i + 1 < s.length())
								{
										s = String.format("%s%s%s", s.subSequence(0, i + 1), Character.toUpperCase(s.charAt(i + 1)), s.substring(i + 2));
								}
						}
				}
				return s;
		}

		/**
		 * Capitalize first letter.
		 */
		public static String optimize(String s)
		{
				if (s.length() == 0)
						return s;
				return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
		}

		public static String longToPlayerName(long l)
		{
				int i = 0;
				char ac[] = new char[12];

				while (l != 0L)
				{
						long l1 = l;

						l /= 37L;
						ac[11 - i++] = xlateTable[(int) (l1 - l * 37L)];
				}
				return new String(ac, 12 - i, i);
		}

		public static final char playerNameXlateTable[] = {
				'_',
				'a',
				'b',
				'c',
				'd',
				'e',
				'f',
				'g',
				'h',
				'i',
				'j',
				'k',
				'l',
				'm',
				'n',
				'o',
				'p',
				'q',
				'r',
				's',
				't',
				'u',
				'v',
				'w',
				'x',
				'y',
				'z',
				'0',
				'1',
				'2',
				'3',
				'4',
				'5',
				'6',
				'7',
				'8',
				'9',
				'[',
				']',
				'/',
				'-',
				' '};

		public static String longToPlayerName2(long l)
		{
				int i = 0;
				char ac[] = new char[99];
				while (l != 0L)
				{
						long l1 = l;
						l /= 37L;
						ac[11 - i++] = playerNameXlateTable[(int) (l1 - l * 37L)];
				}
				return new String(ac, 12 - i, i);
		}

		/**
		 * This one shows 7.2m 15.3 153.6m instead of 150m
		 * @param num
		 * @return
		 */
		public static String formatRunescapeStyle(int num)
		{
				int length = String.valueOf(num).length();
				String number = Integer.toString(num);
				if (length == 4)
				{
						return number.substring(0, 1) + "k";
				}
				else if (length == 5)
				{
						return number.substring(0, 2) + "k";
				}
				else if (length == 6)
				{
						return number.substring(0, 3) + "k";
				}
				else if (length == 7)
				{
						return number.substring(0, 1) + "." + number.substring(1, 2) + "m";
				}
				else if (length == 8)
				{
						return number.substring(0, 2) + "." + number.substring(2, 3) + "m";
				}
				else if (length == 9)
				{
						return number.substring(0, 3) + "." + number.substring(3, 4) + "m";
				}
				else if (length == 10)
				{
						return number.substring(0, 4) + "m";
				}
				return number;
		}

		public static String formatRunescapeStyle(long num)
		{
				int length = String.valueOf(num).length();
				String number = Long.toString(num);
				if (length == 4)
				{
						return number.substring(0, 1) + "k";
				}
				else if (length == 5)
				{
						return number.substring(0, 2) + "k";
				}
				else if (length == 6)
				{
						return number.substring(0, 3) + "k";
				}
				else if (length == 7)
				{
						return number.substring(0, 1) + "." + number.substring(1, 2) + "m";
				}
				else if (length == 8)
				{
						return number.substring(0, 2) + "." + number.substring(2, 3) + "m";
				}
				else if (length == 9)
				{
						return number.substring(0, 3) + "." + number.substring(3, 4) + "m";
				}
				else if (length == 10)
				{
						return number.substring(0, 4) + "m";
				}
				return number;
		}

		public static String ucFirst(String str)
		{
				str = str.toLowerCase();
				if (str.length() > 1)
				{
						str = str.substring(0, 1).toUpperCase() + str.substring(1);
				}
				else
				{
						return str.toUpperCase();
				}
				return str;
		}

		public static int hexToInt(byte data[], int offset, int len)
		{
				int temp = 0;
				int i = 1000;
				for (int cntr = 0; cntr < len; cntr++)
				{
						int num = (data[offset + cntr] & 0xFF) * i;
						temp += num;
						if (i > 1)
								i = i / 1000;
				}
				return temp;
		}

		/**
		 * If range is 5, then it will give a random value of 1 to 5.
		 */
		public static int random2(int range)
		{
				return (int) ((java.lang.Math.random() * range) + 1);
		}

		/**
		 * If range is 5, then it will give a random value of 0 to 5.
		 */
		public static int random(int range)
		{
				return (int) (java.lang.Math.random() * (range + 1));
		}

		public static long playerNameToInt64(String s)
		{
				long l = 0L;
				for (int i = 0; i < s.length(); i++)
				{
						char c = s.charAt(i);
						l *= 37L;
						if (c >= 'A' && c <= 'Z')
								l += (1 + c) - 65;
						else if (c >= 'a' && c <= 'z')
								l += (1 + c) - 97;
						else if (c >= '0' && c <= '9')
								l += (27 + c) - 48;
				}
				while (l % 37L == 0L && l != 0L)
						l /= 37L;
				return l;
		}


		private static char decodeBuf[] = new char[4096];

		public static String textUnpack(byte packedData[], int size)
		{
				int idx = 0;
				for (int i = 0; i < size; i++)
				{
						int val = packedData[i];
						decodeBuf[idx++] = xlateTable[val];
				}


				return new String(decodeBuf, 0, idx);
		}

		public static String optimizeText(String text)
		{
				char buf[] = text.toCharArray();
				boolean endMarker = true;
				for (int i = 0; i < buf.length; i++)
				{
						char c = buf[i];
						if (endMarker && c >= 'a' && c <= 'z')
						{
								buf[i] -= 0x20;
								endMarker = false;
						}
						if (c == '.' || c == '!' || c == '?')
								endMarker = true;
				}
				return new String(buf, 0, buf.length);
		}

		public static void textPack(byte packedData[], java.lang.String text)
		{
				if (text.length() > 80)
						text = text.substring(0, 80);
				text = text.toLowerCase();

				int carryOverNibble = -1;
				int ofs = 0;
				for (int idx = 0; idx < text.length(); idx++)
				{
						char c = text.charAt(idx);
						int tableIdx = 0;
						for (int i = 0; i < xlateTable.length; i++)
						{
								if (c == xlateTable[i])
								{
										tableIdx = i;
										break;
								}
						}
						if (tableIdx > 12)
								tableIdx += 195;
						if (carryOverNibble == -1)
						{
								if (tableIdx < 13)
										carryOverNibble = tableIdx;
								else
										packedData[ofs++] = (byte) (tableIdx);
						}
						else if (tableIdx < 13)
						{
								packedData[ofs++] = (byte) ((carryOverNibble << 4) + tableIdx);
								carryOverNibble = -1;
						}
						else
						{
								packedData[ofs++] = (byte) ((carryOverNibble << 4) + (tableIdx >> 4));
								carryOverNibble = tableIdx & 0xf;
						}
				}

				if (carryOverNibble != -1)
						packedData[ofs++] = (byte) (carryOverNibble << 4);
		}

		public static char xlateTable[] = {
				' ',
				'e',
				't',
				'a',
				'o',
				'i',
				'h',
				'n',
				's',
				'r',
				'd',
				'l',
				'u',
				'm',
				'w',
				'c',
				'y',
				'f',
				'g',
				'p',
				'b',
				'v',
				'k',
				'x',
				'j',
				'q',
				'z',
				'0',
				'1',
				'2',
				'3',
				'4',
				'5',
				'6',
				'7',
				'8',
				'9',
				' ',
				'!',
				'?',
				'.',
				',',
				':',
				';',
				'(',
				')',
				'-',
				'&',
				'*',
				'\\',
				'\'',
				'@',
				'#',
				'+',
				'=',
				'\243',
				'$',
				'%',
				'"',
				'[',
				']',
				'>',
				'<',
				'^',
				'/'};


		public static int direction(int startX, int startY, int destinationX, int destinationY)
		{
				final int directionX = destinationX - startX, directionY = destinationY - startY;
				if (directionX < 0)
				{
						if (directionY < 0)
						{
								if (directionX < directionY)
										return 11;
								return directionX <= directionY ? 10 : 9;
						}
						if (directionY > 0)
						{
								if (-directionX < directionY)
										return 15;
								return -directionX <= directionY ? 14 : 13;
						}
						else
								return 12;
				}
				if (directionX > 0)
				{
						if (directionY < 0)
						{
								if (directionX < -directionY)
										return 7;
								return directionX <= -directionY ? 6 : 5;
						}
						if (directionY > 0)
						{
								if (directionX < directionY)
										return 1;
								return directionX <= directionY ? 2 : 3;
						}
						else
								return 4;
				}
				if (directionY < 0)
				{
						return 8;
				}
				int finalDirection = directionY <= 0 ? -1 : 0;
				/*
				if (finalDirection == -1)
				{
						switch (finalDirection)
						{
								// North.
								case 0:
										if (Region.foundDynamicClipping1("NORTH", startX, startY, 0).equals("BLOCK"))
										{
												return -1;
										}
										break;
								// North West.
								case 14:
										if (Region.foundDynamicClipping1("NORTH-WEST", startX, startY, 0).equals("BLOCK"))
										{
												return -1;
										}
										break;
								// North East.
								case 2:
										if (Region.foundDynamicClipping1("NORTH-EAST", startX, startY, 0).equals("BLOCK"))
										{
												return -1;
										}
										break;
								// West.
								case 12:
										if (Region.foundDynamicClipping1("WEST", startX, startY, 0).equals("BLOCK"))
										{
												return -1;
										}
										break;
				
								// East.
								case 4:
										if (Region.foundDynamicClipping1("EAST", startX, startY, 0).equals("BLOCK"))
										{
												return -1;
										}
										break;
				
								// South.
								case 8:
										if (Region.foundDynamicClipping1("SOUTH", startX, startY, 0).equals("BLOCK"))
										{
												return -1;
										}
										break;
								// South West.
								case 10:
										if (Region.foundDynamicClipping1("SOUTH-WEST", startX, startY, 0).equals("BLOCK"))
										{
												return -1;
										}
										break;
								// South East.
								case 6:
										if (Region.foundDynamicClipping1("SOUTH-EAST", startX, startY, 0).equals("BLOCK"))
										{
												return -1;
										}
										break;
						}
				}
				*/

				return finalDirection;
		}

		public static byte directionDeltaX[] = new byte[] {0, 1, 1, 1, 0, -1, -1, -1};

		public static byte directionDeltaY[] = new byte[] {1, 1, 0, -1, -1, -1, 0, 1};

		public static byte xlateDirectionToClient[] = new byte[] {1, 2, 4, 7, 6, 5, 3, 0};

		public static int getIndex(int id, int[] array)
		{
				for (int i = 0; i < array.length; i++)
				{
						if (id == array[i])
						{
								return i;
						}
				}
				return -1;
		}

		public static ArrayList<String> consolePrint = new ArrayList<String>();

		public static void print(String string)
		{
				System.out.println(string);
				if (ServerConfiguration.DEBUG_MODE)
				{
						return;
				}
				consolePrint.add("[" + Misc.getDate() + "] " + string);
		}

		public static void printDontSave(String string)
		{
				System.out.println(string);
		}

		public static String getTime(long timeInSeconds)
		{
				int seconds = (int) timeInSeconds;
				if (seconds < 60)
				{
						return seconds + " secs";
				}
				else if (seconds < 3600)
				{
						String secondsText = (seconds / 60) > 1 ? "mins" : "min";
						return (seconds / 60) + " " + secondsText;
				}
				else
				{
						int hour = (seconds / 3600);
						String hourText = hour > 1 ? "hours" : "hour";
						return hour + " " + hourText;
				}
		}
}
