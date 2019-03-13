package net.minecraft.util.profiler;

import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public interface ReadableProfiler extends Profiler {
	@Override
	void push(String string);

	@Override
	void push(Supplier<String> supplier);

	@Override
	void pop();

	@Override
	void swap(String string);

	@Environment(EnvType.CLIENT)
	@Override
	void swap(Supplier<String> supplier);

	ProfileResult method_16064();
}
