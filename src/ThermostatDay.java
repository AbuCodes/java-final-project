import java.io.Serializable;

public class ThermostatDay extends Event implements Serializable {
	private GreenhouseControls greenhouseControls;

	public ThermostatDay(GreenhouseControls greenhouseControls, long eventTime) {
		super(greenhouseControls, eventTime);
		this.greenhouseControls = greenhouseControls;
	}

	public void action() {
		greenhouseControls.addEventString(toString());
		greenhouseControls.setVariable("Thermostat", "Day");
	}

	public void updateController(GreenhouseControls greenhouseControls) {
		this.greenhouseControls = greenhouseControls;
	}

	public String toString() {
		return "Thermostat on day setting\n\r";
	}
}
