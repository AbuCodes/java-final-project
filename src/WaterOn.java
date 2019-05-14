import java.io.Serializable;

public class WaterOn extends Event implements Serializable {
	private GreenhouseControls greenhouseControls;

	public WaterOn(GreenhouseControls greenhouseControls, long eventTime) {
		super(greenhouseControls, eventTime);
		this.greenhouseControls = greenhouseControls;
	}

	public void action() {
		greenhouseControls.addEventString(toString());
		greenhouseControls.setVariable("Water", true);
	}

	public void updateController(GreenhouseControls greenhouseControls) {
		this.greenhouseControls = greenhouseControls;
	}

	public String toString() {
		return "Greenhouse water is on\n\r";
	}
}