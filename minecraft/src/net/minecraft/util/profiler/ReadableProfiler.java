package net.minecraft.util.profiler;

import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public interface ReadableProfiler extends Profiler {
	@Override
	void push(String location);

	@Override
	void push(Supplier<String> locationGetter);

	@Override
	void pop();

	@Override
	void swap(String location);

	@Environment(EnvType.CLIENT)
	@Override
	void swap(Supplier<String> locationGetter);

	ProfileResult getResult();
}
