import java.io.*;
import java.util.*;
import java.util.List;

/*
 * File: GreenhouseControls.java
 * Student: Abu Adel
 * StudentID: 3344799
 * Date: 2019-04-28
 * 
 * Requirements:
 * - Provide the means to create the event classes from their names (see the Constructor class in 
 *   java.lang.reflect and getConstructor() in the Class Class). 
 * - able to add new Event classes and modify Event classes without recompiling GreenhouseControls.java
 *
 * Run:
 * 	N/A (No entry point)
 */

// GreenhouseController is a child of Controller and implements Serializable for
// debugging and log file purposes
public class GreenhouseControls extends Controller implements Serializable {

	// Create a copy of the GUI for the purpose of exchanging state info
	private transient GuiController gui;

	// Used by GUI to check state
	private volatile boolean isSuspended;

	private int errorCode;

	// Log the time that a crash has happened
	private long crashErrorTimeMSec = 0;
	private long repeatingEventDelayMSec = 1000;

	private boolean aborted;

	// Constructor
	public GreenhouseControls(GuiController gui) {
		this.isSuspended = false;
		this.gui = gui;
		this.aborted = false;
	}

	// Add the passed event to event collection
	public void addEvent(String event, long duration) {
		rawEventMap.put(event, duration);
		super.addEvent(event, duration);
	}

	// If the event is a repeating such as the bell event then we add the event but
	// loop through the number of times its supposed to repeat
	public void addRepeatingEvent(String event, long duration, int repeatAmount) {
		for (int i = 0; i < repeatAmount; i++) {
			rawEventMap.put(event, duration);
			super.addEvent(event, duration);
			duration += repeatingEventDelayMSec;
		}
	}

	// This couples Greenhouse gui and GreenhouseControls
	// so they can send information back and forth.
	public void setGui(GuiController gui) {
		this.gui = gui;
	}

	public boolean isAborted() {
		return aborted;
	}

	// Call parent class start method.
	public void start() {
		super.start();
	}

	// Call parent class restart() method and resume greenhouse controller.
	public void restart() {
		aborted = false;
		isSuspended = false;
		super.restart();
	}

	// Getter and Setter for suspended boolean, used to suspend an event while
	// keeping run method looping.
	public boolean isSuspended() {
		return isSuspended;
	}

	public void suspend() {
		isSuspended = true;
	}

	// Call parent class resume() and resume greenhouse controller.
	public void resume() {
		isSuspended = false;
		super.resume();
	}

	public void shutdown() {
		aborted = true;
		super.shutdownController();
	}

	// Outputs the current state of what is inside the greenhouse to the gui.
	public void outputGreenhouseStates() {
		Iterator<ControllerState> itr = getControllerState().iterator();
		while (itr.hasNext()) {
			gui.updateEventText(itr.next().toString() + "\n\r");
		}
		gui.updateEventText("\n\r");
	}

	// Sets the state of a particular greenhouse item, or "event".
	public <E, S> void setVariable(E event, S state) {
		Iterator<ControllerState> itr = getControllerState().iterator();
		while (itr.hasNext()) {
			ControllerState controllerState = itr.next();
			if (controllerState.equals(event)) {
				controllerState.setState(state);
				return;
			}
		}
		getControllerState().add(new ControllerState(event, state));
		return;
	}

	// Generic tuple class used to keep track of the state of
	// the items within a greenhouse. For example the Lights, or
	// Temperature setting. Allows for the creation of any type
	// of greenhouse internal object and any type of state, whether
	// it be on/off, true/false, up/down, etc.
	public static class ControllerState<E, S> implements Serializable {
		// The event cannot change, it's the constant in the controller state.
		// Used for updating the state of the object as the controller moves across
		// time.
		public final E event;
		public S state;

		public ControllerState(E e, S s) {
			event = e;
			state = s;
		}

		// A Controller State is equal to another if their event types
		// are the same.
		public boolean equals(Object o) {
			return event.equals(o);
		}

		// As time moves forward the object that is being controlled has it's value
		// changed
		public void setState(S newState) {
			state = newState;
		}

		// Output the state of the entire controller
		public String toString() {
			return "[" + event + " is " + state + "]";
		}

		public E getCSEvent() {
			return event;
		}

		public S getCSState() {
			return state;
		}
	}

	// A collection of states of the various objects within the greenhouse.
	private List<ControllerState> greenhouseStates = new ArrayList<>();
	// An internal collection of the raw events as read from the event file. Used
	// to determine what events need to be re-added after an emergency crash.
	private Map<String, Long> rawEventMap = new HashMap<>();

	// In order to ensure that the collection of controller states is accurate
	// and the wrong order of events wasn't recorded we use a object locking
	// mechanism
	public List<ControllerState> getControllerState() {
		return greenhouseStates;
	}

	private void notifyGuiEmergencyShutdown() {
		String emergencyShutdownText = "**********************************************************\n\r";
		emergencyShutdownText += "                      Emergency Shutdown                  \n\r";
		emergencyShutdownText += "**********************************************************\n\r";
		emergencyShutdownText += "\n\r";
		emergencyShutdownText += "Displaying Current Greenhouse Status\n\n\r";

		gui.updateEventText(emergencyShutdownText);
		outputGreenhouseStates();
	}

	// The crashing event calls this and gives it it's error code
	// and relative time in which the crash occurred. This method constructs
	// an error report. Calls the parent class to shutdown. Notifies the gui
	// event text window with information. This method also serializes this
	// controller
	// class and writes that information to a .out file which can be read later.
	public void emergencyShutdown(int errorCode, long errorTime) {
		Date errorDate = new Date(System.currentTimeMillis());
		String errorMessage = "Shutting down Greenhouse Controls. Contact System Administrator\n" + "Error code: "
				+ errorCode + " - " + " occurred " + errorDate;
		// Call parent class to start shutdown procedure
		this.aborted = true;
		this.shutdownController();
		this.errorCode = errorCode;
		crashErrorTimeMSec = errorTime;

		notifyGuiEmergencyShutdown();
		generateDebugLog(errorMessage);
		try {
			File outputFile = new File("dump.out");
			outputFile.createNewFile();
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(outputFile, false));
			objectOutputStream.writeObject(this);
			objectOutputStream.close();
		} catch (IOException e) {
			System.out.println("Exception during serialized file creation: " + e.toString());
		}
	}

	public void generateDebugLog(String eventError) {
		try {
			PrintStream out = new PrintStream(new BufferedOutputStream(new FileOutputStream("error.log")));
			out.println(eventError);
			out.close();
		} catch (FileNotFoundException e) {
			System.out.println("Exception during debug log generation: " + e.toString());
		}
	}

	// Helper function to send text to the gui's event text area.
	public void addEventString(String event) {
		gui.updateEventText(event);
	}

	// The fix methods set a new value in the ControllerState collection and reset
	// the error code.
	public void fixPower() {
		for (ControllerState cs : greenhouseStates) {
			if (cs.getCSEvent().equals("Power")) {
				if (cs.getCSState().equals(false)) {
					cs.setState(true);
					try {
						PrintStream out = new PrintStream(new BufferedOutputStream(new FileOutputStream("fix.log")));
						out.println("Power Back On. Resetting error code.");
						errorCode = 0;
						out.close();

					} catch (FileNotFoundException e) {
						System.out.println("Exception during Power Fix: " + e.toString());
					}
				}
			}
		}
	}

	public void repairWindow() {
		for (ControllerState cs : greenhouseStates) {
			if (cs.getCSEvent().equals("Window")) {
				if (cs.getCSState().equals("Malfunctioned")) {
					cs.setState("Functional");
					try {
						PrintStream out = new PrintStream(new BufferedOutputStream(new FileOutputStream("fix.log")));
						out.println("Window Repaired. Resetting error code.");
						errorCode = 0;
						out.close();

					} catch (FileNotFoundException e) {
						System.out.println("Exception during Window Fix: " + e.toString());
					}
				}
			}
		}
	}

	// Check the error code that was passed to the controller from the event.
	// Based off that it determines what needs to be fixed and creates a log item.
	// If an event has been created by the user that doesn't have a corresponding
	// fix event then an exception is thrown. This was done instead of creating a
	// Fixable class that requires a fix() method. That was a design choice.
	public void fixGreenhouseController() {
		if (errorCode == 1) {
			fixPower();
			gui.updateEventText("Power Fixed.\n\r");
		} else if (errorCode == 2) {
			repairWindow();
			gui.updateEventText("Window Fixed.\n\r");
		} else {
			try {
				PrintStream out = new PrintStream(new BufferedOutputStream(new FileOutputStream("fix_fail.log")));
				out.println("Unable to repair issue. Please contact system admin. The crashing Event does not have"
						+ "a corresponding error code and fix method.");
				errorCode = 0;
				out.close();
			} catch (FileNotFoundException e) {
				System.out.println("Exception fix attempt: " + e.toString());
			}
		}
	}

	// Go through the raw event as read by the event file and see which
	// haven't happened yet because there was a crash. We determine this based off
	// what time a crash had occurred.
	public void resumeAfterCrash() {
		aborted = false;
		Iterator eventMapIterator = rawEventMap.entrySet().iterator();
		while (eventMapIterator.hasNext()) {
			Map.Entry<String, Long> eventPair = (Map.Entry) eventMapIterator.next();
			if (eventPair.getValue() > crashErrorTimeMSec) {
				this.addEvent(eventPair.getKey(), eventPair.getValue() - crashErrorTimeMSec);
			}
		}
		resume();
	}

} /// :~