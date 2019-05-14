import java.io.Serializable;

//Made Event implement Runnable so that each event handle its own timing.
public abstract class Event implements Runnable, Serializable {
	private boolean eventStarted;
	private boolean eventThreadRunning;
	private long eventTime;
	private long delay;
	private boolean newEvent = true;
	private GreenhouseControls greenhouseControls;

	public Event(GreenhouseControls greenhouseControls, long delayTime) {
		this.greenhouseControls = greenhouseControls;
		this.delay = delayTime;
		this.eventStarted = false;
		this.eventThreadRunning = true;
	}

	public abstract void updateController(GreenhouseControls gc);

	public long getDelayTime() {
		return delay;
	}

	public void run() {

		synchronized (this) {
			// Ensure that even though the thread is running at creation we
			// Sync events to Start when start button is clicked
			// Wait until we are notified to begin run method again.
			if (newEvent) {
				try {
					newEvent = false;
					wait();
				} catch (Exception e) {
					System.out.println("Exception called in event wait()" + e.toString());
				}
			}
			// This is the run within the run() loop.
			// The logic of what the event is supposed to do.
			// This loop will carry on as long as the thread is active.
			// If the thread shuts down this is passed and run() returns
			// and the thread ends.
			while (eventThreadRunning) {
				// If the controller client is suspended then wait.
				if (greenhouseControls.isSuspended()) {
					try {
						wait();
					} catch (Exception e) {
						System.out.println("Exception called in event wait()" + e.toString());
					}
				}

				if (greenhouseControls.isAborted()) {
					return;
				}

				// Check to see if start button has been clicked.
				// If true call event ready and try to run action method
				// else exit condition
				if (eventStarted) {
					if (eventReady()) {
						try {
							action();
							stopEvent();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	}

	// Initiates event sequence and makes sure that
	// We are running new events
	public void startEvent(boolean isNewEvent) {
		synchronized (this) {
			eventTime = System.currentTimeMillis() + delay;
			eventStarted = true;
			eventThreadRunning = true;
			if (isNewEvent) {
				this.newEvent = true;
			} else {
				this.newEvent = false;
			}
			notifyAll();
		}
	}

	// Allows the run method to return
	private void stopEvent() {
		eventThreadRunning = false;
		eventStarted = false;
		notifyAll();
	}

	// Helper method to determine if event is ready
	// to call action()
	private boolean eventReady() {
		if (System.currentTimeMillis() >= eventTime)
			return true;
		return false;
	}

	// Let the threads die off by allowing the run method to return.
	public void eventRunOut() {
		synchronized (this) {
			stopEvent();
			notifyAll();
		}
	}

	// The Event-inherited class must implement it's own action.
	public abstract void action();
}
