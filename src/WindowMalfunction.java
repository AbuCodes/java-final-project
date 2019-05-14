import java.io.Serializable;

public class WindowMalfunction extends Event implements Serializable {
	private GreenhouseControls greenhouseControls;

	public WindowMalfunction(GreenhouseControls greenhouseControls, long eventTime) {
		super(greenhouseControls, eventTime);
		this.greenhouseControls = greenhouseControls;
	}

	public void action() {
		greenhouseControls.addEventString(toString());
		greenhouseControls.setVariable("Window", "Malfunctioning");
		greenhouseControls.emergencyShutdown(1, getDelayTime());
	}

	public void updateController(GreenhouseControls greenhouseControls) {
		this.greenhouseControls = greenhouseControls;
	}

	public String toString() {
		return "Window Malfunctioning!\n\r";
	}
}
