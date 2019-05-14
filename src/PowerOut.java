import java.io.Serializable;

public class PowerOut extends Event implements Serializable {
	private GreenhouseControls greenhouseControls;

	public PowerOut(GreenhouseControls greenhouseControls, long eventTime) {
		super(greenhouseControls, eventTime);
		this.greenhouseControls = greenhouseControls;

	}

	public void action() {
		greenhouseControls.addEventString(toString());
		greenhouseControls.setVariable("Power", false);
		greenhouseControls.emergencyShutdown(1, getDelayTime());
	}

	public void updateController(GreenhouseControls greenhouseControls) {
		this.greenhouseControls = greenhouseControls;
	}

	public String toString() {
		return "Power has gone out!\n\r";
	}

}
