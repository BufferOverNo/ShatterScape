package core;

/**
 * Game configurations.
 * @author MGT Madness, created on 20-12-2013.
 */
public class ServerConfiguration
{
		/**
		 * True, when developing.
		 */
		public static boolean DEBUG_MODE = true;

		/**
		 * True, to send a packet to the client informing it to force update items every game tick.
		 */
		public static final boolean FORCE_ITEM_UPDATE = false;

		/**
		 * True to enable bots.
		 */
		public final static boolean ENABLE_BOTS = false;

		/**
		 * True to enable discord integration.
		 */
		public final static boolean DISCORD = false;

		/**
		 * Unique identity of the server. If client matches this value, accept connection.
		 */
		public final static int UID = 1099070941;

		/**
		 * True, for stability testing.
		 */
		public final static boolean STABILITY_TEST = false;

		/**
		 * True, to print out packet identities in-game.
		 */
		public final static boolean SHOW_PACKETS = false;

		/**
		 * Maximum amount of packets received per game tick per player.
		 * Switching fast can be up to 10 packets per tick.
		 * This is used to prevent packet abusing where they send too many packets to crash the server.
		 */
		public final static int MAXIMUM_PACKETS_PER_TICK = 12;

		/**
		 * Instant switching packet.
		 */
		public static boolean INSTANT_SWITCHING = false;
}