import java.io.Serializable;

public class FansOn extends Event implements Serializable {
	GreenhouseControls greenhouseControls;

	public FansOn(GreenhouseControls greenhouseControls, long eventTime) {
		super(greenhouseControls, eventTime);
		this.greenhouseControls = greenhouseControls;
	}

	public void action() {
		greenhouseControls.addEventString(toString());
		greenhouseControls.setVariable("Fan", true);
	}

	public void updateController(GreenhouseControls greenhouseControls) {
		this.greenhouseControls = greenhouseControls;
	}

	public String toString() {
		return "Fan is on\n\r";
	}
}