package ru.gafi.animation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Michael
 * Date: 20.05.13
 * Time: 20:07
 */
public class EventTimeLine {
	private List<SEvent> events = new ArrayList<>();

	public void addEvent(SEvent e) {
		events.add(e);
//		events.sort(EventComparator);
	}

	private int EventComparator(SEvent x, SEvent y) {
		if (x.time < y.time) return -1;
		if (x.time > y.time) return 1;
		return 0;
	}

	public float length() {
		float len = 0;
		for (int i = 0; i < events.size(); i++) {
			len = Math.max(len, events.get(i).time);
		}
		return len;
	}

	public int getEventsCount() {
		return events.size();
	}

	public void executeEventsAtRange(float startTime, float finishTime, boolean includingStartTime) {
		if (events.isEmpty()) return;
		if (finishTime > startTime) {
			for (int i = 0; i < events.size(); i++) {
				SEvent evt = events.get(i);
				if (includingStartTime ? evt.time >= startTime : evt.time > startTime) {
					if (evt.time > finishTime) break;
					executeEvent(evt);
				}
			}
		} else {
			for (int i = events.size() - 1; i >= 0; i--) {
				SEvent evt = events.get(i);
				if (includingStartTime ? evt.time <= startTime : evt.time < startTime) {
					if (evt.time < finishTime) break;
					executeEvent(evt);
				}
			}
		}
	}

	private void executeEvent(SEvent evt) {
		if (evt.action != null) {
			evt.action.run();
		}
	}
}
