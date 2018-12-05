package net.minecraft;

import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Profiler;

public interface class_3693 extends Profiler {
	@Override
	void begin(String string);

	@Override
	void begin(Supplier<String> supplier);

	@Override
	void end();

	@Override
	void endBegin(String string);

	@Environment(EnvType.CLIENT)
	@Override
	void endBegin(Supplier<String> supplier);

	class_3696 method_16064();
}
