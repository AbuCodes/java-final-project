import java.io.Serializable;

public class LightsOff extends Event implements Serializable {

	GreenhouseControls greenhouseControls;

	public LightsOff(GreenhouseControls greenhouseControls, long eventTime) {
		super(greenhouseControls, eventTime);
		this.greenhouseControls = greenhouseControls;
	}

	public void action() {
		greenhouseControls.addEventString(toString());
		greenhouseControls.setVariable("Lights", false);
	}

	public void updateController(GreenhouseControls greenhouseControls) {
		this.greenhouseControls = greenhouseControls;
	}

	public String toString() {
		return "Light is off\n\r";
	}
}