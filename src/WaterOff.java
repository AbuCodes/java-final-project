import java.io.Serializable;

public class WaterOff extends Event implements Serializable {
	private GreenhouseControls greenhouseControls;

	public WaterOff(GreenhouseControls greenhouseControls, long eventTime) {
		super(greenhouseControls, eventTime);
		this.greenhouseControls = greenhouseControls;
	}

	public void action() {
		greenhouseControls.addEventString(toString());
		greenhouseControls.setVariable("Water", false);
	}

	public void updateController(GreenhouseControls greenhouseControls) {
		this.greenhouseControls = greenhouseControls;
	}

	public String toString() {
		return "Greenhouse water is off\n\r";
	}
}