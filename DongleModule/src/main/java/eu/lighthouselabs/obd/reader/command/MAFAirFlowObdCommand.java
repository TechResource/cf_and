package eu.lighthouselabs.obd.reader.command;

import android.util.Log;

import eu.lighthouselabs.obd.reader.config.ObdConfig;

public class MAFAirFlowObdCommand extends ObdCommand {

    private double maf = -9999.0;

    // grams/sec
    public MAFAirFlowObdCommand() {
        super("0110", ObdConfig.MAF_AIR_FLOW, "g/s", "g/s");
    }

    public MAFAirFlowObdCommand(MAFAirFlowObdCommand other) {
        super(other);
    }

    public String formatResult() {
        String res = super.formatResult();
        if ("NODATA".equals(res) || res.isEmpty()) {
            return "NODATA";
        }
        String A = res.substring(4, 6);
        String B = res.substring(6, 8);
        int a = Integer.parseInt(A, 16);
        int b = Integer.parseInt(B, 16);
        maf = ((256.0 * a) + b) / 100.0;
        return Integer.toString((int) maf);
    }

    double getMAF() {
        return maf;
    }
}
