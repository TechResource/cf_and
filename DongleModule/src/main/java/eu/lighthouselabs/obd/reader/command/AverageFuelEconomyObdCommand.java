package eu.lighthouselabs.obd.reader.command;

import eu.lighthouselabs.obd.reader.config.ObdConfig;

public class AverageFuelEconomyObdCommand extends ObdCommand {

    //	public static final String FUEL_ECONOMY_KEY = "Fuel Economy";
    public static final String AVG_FUEL_ECONOMY_KEY = "Fuel Economy Average";
    public static final String AVG_FUEL_ECONOMY_COUNT_KEY = "Average Fuel Economy Count";
    private Double ampg = 0.0;

    public AverageFuelEconomyObdCommand() {
        super("", AVG_FUEL_ECONOMY_KEY, "kmpg", "mpg");
    }

    public AverageFuelEconomyObdCommand(AverageFuelEconomyObdCommand other) {
        super(other);
    }

    @Override
    public Object getRawValue() {
        return ampg;
    }

    public String formatResult() {

        Integer count = 0;
        Double mpg = 0.0;
        if (data.containsKey(AVG_FUEL_ECONOMY_KEY)) {
            try {
                ampg = (Double) data.get(AVG_FUEL_ECONOMY_KEY);
            } catch (ClassCastException ex) {
                ampg = (double) ((Integer) data.get(AVG_FUEL_ECONOMY_KEY)).intValue();
            }
            count = (Integer) data.get(AVG_FUEL_ECONOMY_COUNT_KEY);
        }
        if (data.containsKey(ObdConfig.FUEL_ECON)) {
            mpg = (Double) data.get(ObdConfig.FUEL_ECON);
        }
        if(count == null)
            count = 0;
        if(mpg == null)
            mpg = 0.0;
        if(ampg == null || ampg < 0)
            ampg = 0.0;

        if (mpg > 0) {
            ampg += mpg;
            count += 1;
            data.put(AVG_FUEL_ECONOMY_KEY, ampg);
            data.put(AVG_FUEL_ECONOMY_COUNT_KEY, count);
        }
        double cAmpg = ampg.doubleValue();
        if (count != null && count > 0) {
            cAmpg = ampg / (double) count;
        }
        return String.format("%.1f mpg", cAmpg);
    }
}
