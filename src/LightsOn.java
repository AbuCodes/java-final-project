import java.io.Serializable;

public class LightsOn extends Event implements Serializable {
	GreenhouseControls greenhouseControls;

	public LightsOn(GreenhouseControls greenhouseControls, long eventTime) {
		super(greenhouseControls, eventTime);
		this.greenhouseControls = greenhouseControls;
	}

	public void action() {
		greenhouseControls.addEventString(toString());
		greenhouseControls.setVariable("Lights", true);
	}

	public void updateController(GreenhouseControls greenhouseControls) {
		this.greenhouseControls = greenhouseControls;
	}

	public String toString() {
		return "Light is on\n\r";
	}
}