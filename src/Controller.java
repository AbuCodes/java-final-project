import java.lang.reflect.Constructor;
import java.util.*;
import java.util.concurrent.*;

public class Controller {

	// Controllers internal collection of Runnable eventsCollection.
	private List<Event> eventsCollection = new ArrayList<>();
	// Thread pool from which event threads are created.
	private ExecutorService eventExecutor = Executors.newCachedThreadPool();
	// Thread executor service.
	private ThreadPoolExecutor eventThreadPoolExecutor = (ThreadPoolExecutor) eventExecutor;

	// Used to determine whether or not to if we can close/exit
	// Out dialog
	private boolean isRunning = false;

	public boolean isRunning() {
		return isRunning;
	}

	// Add event method. this is done through reflection as it was required by the
	// Assignment
	public void addEvent(String className, long duration) {
		try {
			Class<?> eventClass = Class.forName(className);
			Constructor<?> eventClassConstructor = eventClass.getConstructor(this.getClass(), long.class);
			Object instance = eventClassConstructor.newInstance(this, duration);

			addEventToExecutor((Event) instance);
			eventsCollection.add((Event) instance);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void addEventToExecutor(Event event) {
		eventThreadPoolExecutor.execute(event);
	}

	public void start() {
		for (Event event : eventsCollection) {
			event.startEvent(true);
		}
		isRunning = true;
	}

	// Restart method will lets all events execute then them to
	// The even executor to restart.
	public void restart() {
		for (Event event : eventsCollection) {
			event.eventRunOut();
		}
		for (Event event : eventsCollection) {
			eventExecutor = Executors.newCachedThreadPool();
			eventThreadPoolExecutor = (ThreadPoolExecutor) eventExecutor;
			eventThreadPoolExecutor.execute(event);
			event.startEvent(false);
		}
		isRunning = true;
	}

	// Resumes events
	public void resume() {
		for (Event event : eventsCollection) {
			event.startEvent(false);
		}
	}

	// Shuts down events by letting them by making each events
	// run method return from its state
	public void shutdownController() {
		for (Event event : eventsCollection) {
			event.eventRunOut();
		}
		try {
			eventExecutor.shutdownNow();
		} catch (Exception e) {
			e.printStackTrace();
		}
		isRunning = false;
	}

}
