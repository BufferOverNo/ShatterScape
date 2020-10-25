package utility;

import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LoggerDate extends PrintStream
{
		private DateFormat dateFormat = new SimpleDateFormat();

		private Date cachedDate = new Date();

		private SimpleTimer refreshTimer = new SimpleTimer();

		private String getPrefix()
		{
				if (refreshTimer.elapsed() > 1000)
				{
						refreshTimer.reset();
						cachedDate = new Date();
				}
				return dateFormat.format(cachedDate);
		}

		public LoggerDate(PrintStream out)
		{
				super(out);
		}

		@Override
		public void print(String str)
		{
				if (str.startsWith("debug:"))
						super.print("[" + getPrefix() + "] DEBUG: " + str.substring(6));
				else
						super.print("[" + getPrefix() + "]: " + str);
		}
}
