package net.minecraft.util.profiler;

import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public interface Profiler {
	void startTick();

	void endTick();

	void push(String location);

	void push(Supplier<String> locationGetter);

	void pop();

	void swap(String location);

	@Environment(EnvType.CLIENT)
	void swap(Supplier<String> locationGetter);

	/**
	 * Increment the visit count for a marker.
	 * 
	 * <p>This is useful to keep track of number of calls made to performance-
	 * wise expensive methods.
	 * 
	 * @param marker a unique marker
	 */
	void visit(String marker);

	/**
	 * Increment the visit count for a marker.
	 * 
	 * <p>This is useful to keep track of number of calls made to performance-
	 * wise expensive methods.
	 * 
	 * <p>This method is preferred if getting the marker is costly; the
	 * supplier won't be called if the profiler is disabled.
	 * 
	 * @param markerGetter the getter for a unique marker
	 */
	void visit(Supplier<String> markerGetter);
}
