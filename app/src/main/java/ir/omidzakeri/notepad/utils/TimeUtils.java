package ir.omidzakeri.notepad.utils;

import java.util.Date;
import org.ocpsoft.prettytime.PrettyTime;

/**
 * Created by Omid on 8/21/2016.
 */
public class TimeUtils{
	public static final PrettyTime prettyTime;

	static{
		prettyTime = new PrettyTime();
	}

	public static String getHumanReadableTimeDiff(Date lastTime){
		if (lastTime == null) return "";
		prettyTime.setReference(new Date());
		return prettyTime.format(lastTime);
	}
}
