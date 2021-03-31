package net.minecraft.util.profiler;

import java.util.function.Supplier;

public interface Profiler {
	String field_29925 = "root";

	void startTick();

	void endTick();

	void push(String location);

	void push(Supplier<String> locationGetter);

	void pop();

	void swap(String location);

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

	static Profiler union(Profiler profiler, Profiler profiler2) {
		if (profiler == DummyProfiler.INSTANCE) {
			return profiler2;
		} else {
			return profiler2 == DummyProfiler.INSTANCE ? profiler : new Profiler() {
				@Override
				public void startTick() {
					profiler.startTick();
					profiler2.startTick();
				}

				@Override
				public void endTick() {
					profiler.endTick();
					profiler2.endTick();
				}

				@Override
				public void push(String location) {
					profiler.push(location);
					profiler2.push(location);
				}

				@Override
				public void push(Supplier<String> locationGetter) {
					profiler.push(locationGetter);
					profiler2.push(locationGetter);
				}

				@Override
				public void pop() {
					profiler.pop();
					profiler2.pop();
				}

				@Override
				public void swap(String location) {
					profiler.swap(location);
					profiler2.swap(location);
				}

				@Override
				public void swap(Supplier<String> locationGetter) {
					profiler.swap(locationGetter);
					profiler2.swap(locationGetter);
				}

				@Override
				public void visit(String marker) {
					profiler.visit(marker);
					profiler2.visit(marker);
				}

				@Override
				public void visit(Supplier<String> markerGetter) {
					profiler.visit(markerGetter);
					profiler2.visit(markerGetter);
				}
			};
		}
	}
}
