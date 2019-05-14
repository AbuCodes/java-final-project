import java.io.Serializable;

public class Bell extends Event implements Serializable {
	GreenhouseControls greenhouseControls;

	public Bell(GreenhouseControls greenhouseControls, long eventTime) {
		super(greenhouseControls, eventTime);
		this.greenhouseControls = greenhouseControls;
	}

	public void action() {
		greenhouseControls.addEventString(toString());
	}

	public void updateController(GreenhouseControls greenhouseControls) {
		this.greenhouseControls = greenhouseControls;
	}

	public String toString() {
		return "Bing!\n\r";
	}
}
