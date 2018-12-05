package net.minecraft.util;

import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public interface Profiler {
	void method_16065();

	void method_16066();

	void begin(String string);

	void begin(Supplier<String> supplier);

	void end();

	void endBegin(String string);

	@Environment(EnvType.CLIENT)
	void endBegin(Supplier<String> supplier);
}
