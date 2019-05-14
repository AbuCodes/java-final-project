import java.io.Serializable;

public class ThermostatNight extends Event implements Serializable {
	private GreenhouseControls greenhouseControls;

	public ThermostatNight(GreenhouseControls greenhouseControls, long eventTime) {
		super(greenhouseControls, eventTime);
		this.greenhouseControls = greenhouseControls;
	}

	public void action() {
		greenhouseControls.addEventString(toString());
		greenhouseControls.setVariable("Thermostat", "night");
	}

	public void updateController(GreenhouseControls greenhouseControls) {
		this.greenhouseControls = greenhouseControls;
	}

	public String toString() {
		return "Thermostat on night setting\n\r";
	}
}