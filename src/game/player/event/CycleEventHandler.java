package game.player.event;

import java.util.ArrayList;
import java.util.List;

import game.log.GameTickLog;

/**
 * Handles all of our cycle based events
 * 
 * @author Stuart <RogueX>
 * 
 */
public class CycleEventHandler
{

		/**
		 * The instance of this class
		 */
		private static CycleEventHandler instance;

		/**
		 * Returns the instance of this class
		 * 
		 * @return
		 */
		public static CycleEventHandler getSingleton()
		{
				if (instance == null)
				{
						instance = new CycleEventHandler();
				}
				return instance;
		}

		/**
		 * Holds all of our events currently being ran
		 */
		private List<CycleEventContainer> events;

		/**
		 * Creates a new instance of this class
		 */
		public CycleEventHandler()
		{
				this.events = new ArrayList<CycleEventContainer>();
		}

		/**
		 * Add an event to the list
		 * 
		 * @param owner
		 * @param event
		 * @param cycles
		 */
		public void addEvent(Object owner, CycleEvent event, int cycles)
		{
				this.events.add(new CycleEventContainer(owner, event, cycles));
		}

		/**
		 * Execute and remove events
		 */
		public void cycleEventGameTick()
		{
				long time = System.currentTimeMillis();
				List<CycleEventContainer> eventsCopy = new ArrayList<CycleEventContainer>(events);
				List<CycleEventContainer> remove = new ArrayList<CycleEventContainer>();
				for (CycleEventContainer c : eventsCopy)
				{
						if (c != null)
						{
								if (c.needsExecution() && c.isRunning())
								{
										c.execute();
										if (!c.isRunning())
										{
												remove.add(c);
										}
								}
						}
				}
				for (CycleEventContainer c : remove)
				{
						events.remove(c);
				}
				GameTickLog.cycleEventTickDuration = System.currentTimeMillis() - time;
		}

		/**
		 * Returns the amount of events currently running
		 * @return amount
		 */
		public int getEventsCount()
		{
				return this.events.size();
		}

		/**
		 * Stops all events which are being ran by the given owner
		 * @param owner
		 */

		public void stopEvents(Object owner)
		{
				List<CycleEventContainer> eventsCopy = new ArrayList<CycleEventContainer>(events);
				for (CycleEventContainer c : eventsCopy)
				{
						if (c.getOwner() == owner)
						{
								events.remove(c);
						}
				}

		}

		/*
		 * this old one have this error
		 * java.util.ConcurrentModificationException
		at java.util.ArrayList$Itr.checkForComodification(Unknown Source)
		at java.util.ArrayList$Itr.next(Unknown Source)
		at a.g.a.c.a(CycleEventHandler.java:105)
		at a.g.d.b(LogOutUpdate.java:83)
		at a.g.d.a(LogOutUpdate.java:134)
		at a.g.h.d(PlayerHandler.java:195)
		at core.Server$1.a(Server.java:265)
		at core.a.a.c(Task.java:126)
		at core.a.b.run(TaskScheduler.java:110)
		at java.util.concurrent.Executors$RunnableAdapter.call(Unknown Source)
		at java.util.concurrent.FutureTask.runAndReset(Unknown Source)
		at java.util.concurrent.ScheduledThreadPoolExecutor$ScheduledFutureTask.access$301(Unknown Source)
		at java.util.concurrent.ScheduledThreadPoolExecutor$ScheduledFutureTask.run(Unknown Source)
		at java.util.concurrent.ThreadPoolExecutor.runWorker(Unknown Source)
		at java.util.concurrent.ThreadPoolExecutor$Worker.run(Unknown Source)
		at java.lang.Thread.run(Unknown Source)
		public void stopEvents(Object owner)
		{
				for (CycleEventContainer c : events)
				{
						if (c.getOwner() == owner)
						{
								Misc.printDontSave("Event removed.");
								c.stop();
						}
				}
		}
				 */

}