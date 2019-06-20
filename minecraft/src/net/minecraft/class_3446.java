package net.minecraft;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public interface class_3446 {
	DecimalFormat field_16976 = class_156.method_654(
		new DecimalFormat("########0.00"), decimalFormat -> decimalFormat.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ROOT))
	);
	class_3446 field_16975 = NumberFormat.getIntegerInstance(Locale.US)::format;
	class_3446 field_16978 = i -> field_16976.format((double)i * 0.1);
	class_3446 field_16977 = i -> {
		double d = (double)i / 100.0;
		double e = d / 1000.0;
		if (e > 0.5) {
			return field_16976.format(e) + " km";
		} else {
			return d > 0.5 ? field_16976.format(d) + " m" : i + " cm";
		}
	};
	class_3446 field_16979 = i -> {
		double d = (double)i / 20.0;
		double e = d / 60.0;
		double f = e / 60.0;
		double g = f / 24.0;
		double h = g / 365.0;
		if (h > 0.5) {
			return field_16976.format(h) + " y";
		} else if (g > 0.5) {
			return field_16976.format(g) + " d";
		} else if (f > 0.5) {
			return field_16976.format(f) + " h";
		} else {
			return e > 0.5 ? field_16976.format(e) + " m" : d + " s";
		}
	};

	@Environment(EnvType.CLIENT)
	String format(int i);
}
