package eu.lighthouselabs.obd.reader.command;

import eu.lighthouselabs.obd.reader.config.ObdConfig;

public class FuelPressureObdCommand extends PressureObdCommand {

	public FuelPressureObdCommand() {
		super("010A", ObdConfig.FUEL_PRESSURE, "kPa", "atm");
	}

	public FuelPressureObdCommand(FuelPressureObdCommand other) {
		super(other);
	}

	public int transform(int b) {
		return b * 3;
	}
}
