import java.io.Serializable;

public class FansOff extends Event implements Serializable {
	GreenhouseControls greenhouseControls;

	public FansOff(GreenhouseControls greenhouseControls, long eventTime) {
		super(greenhouseControls, eventTime);
		this.greenhouseControls = greenhouseControls;
	}

	public void action() {
		greenhouseControls.addEventString(toString());
		greenhouseControls.setVariable("Fan", false);
	}

	public void updateController(GreenhouseControls greenhouseControls) {
		this.greenhouseControls = greenhouseControls;
	}

	public String toString() {
		return "Fan is off\n\r";
	}
}