package net.minecraft.util.profiler;

import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public interface Profiler {
	void startTick();

	void endTick();

	void push(String string);

	void push(Supplier<String> supplier);

	void pop();

	void swap(String string);

	@Environment(EnvType.CLIENT)
	void swap(Supplier<String> supplier);

	void method_24270(String string);

	void method_24271(Supplier<String> supplier);
}
