package eu.lighthouselabs.obd.reader.command;

import eu.lighthouselabs.obd.reader.config.ObdConfig;

public class FuelEconomyObdCommand2 extends ObdCommand {

	public static final double AIR_FUEL_RATIO = 14.64;
	public static final double FUEL_DENSITY_GRAMS_PER_LITER = 720.0;
	protected double fuelEcon = -9999.0;

	public FuelEconomyObdCommand2(String cmd, String desc, String resType, String impType) {
		super(cmd,desc,resType,impType);
	}
	public FuelEconomyObdCommand2() {
		super("",ObdConfig.FUEL_ECON,"kml","mpg");
	}
	public FuelEconomyObdCommand2(FuelEconomyObdCommand2 other) {
		super(other);
	}
	public void run() {
		try {
			MAFAirFlowObdCommand maf = new MAFAirFlowObdCommand();
			SpeedObdCommand speed = new SpeedObdCommand();
			runCmd(maf);
			maf.formatResult();
			double mafV = maf.getMAF();
			if (mafV == -9999.0) {
				fuelEcon = -9999.0;
			}
			runCmd(speed);
			speed.formatResult();
			double speedV = (double)speed.getInt();
//			fuelEcon = (14.7  * 6.17 * 454.0 * speedV * 0.621371) / (3600.0 * mafV);
			fuelEcon = (speedV * 7.718) / mafV;
		} catch (Exception e) {
			setError(e);
		}
	}
	public void runCmd(ObdCommand cmd) {
		cmd.setInputStream(inStream);
		cmd.setOutputStream(outStream);
		cmd.start();
		try {
			cmd.join();
		} catch (InterruptedException e) {
			setError(e);
		}
	}
	public String formatResult() {
		if (fuelEcon < 0) {
			return "NODATA";
		}
		if (!isImperial()) {
			double kml = fuelEcon * 0.425143;
			return String.format("%.1f %s", kml, resType);
		}
		return String.format("%.1f %s", fuelEcon, impType);
	}

}
