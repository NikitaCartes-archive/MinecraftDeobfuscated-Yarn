package net.minecraft.util.profiler;

import java.util.function.Supplier;

public interface Profiler {
	String ROOT_NAME = "root";

	void startTick();

	void endTick();

	void push(String location);

	void push(Supplier<String> locationGetter);

	void pop();

	void swap(String location);

	void swap(Supplier<String> locationGetter);

	void markSampleType(SampleType type);

	default void visit(String marker) {
		this.visit(marker, 1);
	}

	/**
	 * Increment the visit count for a marker.
	 * 
	 * <p>This is useful to keep track of number of calls made to performance-
	 * wise expensive methods.
	 * 
	 * @param marker a unique marker
	 */
	void visit(String marker, int num);

	default void visit(Supplier<String> markerGetter) {
		this.visit(markerGetter, 1);
	}

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
	void visit(Supplier<String> markerGetter, int num);

	static Profiler union(Profiler a, Profiler b) {
		if (a == DummyProfiler.INSTANCE) {
			return b;
		} else {
			return b == DummyProfiler.INSTANCE ? a : new Profiler() {
				@Override
				public void startTick() {
					a.startTick();
					b.startTick();
				}

				@Override
				public void endTick() {
					a.endTick();
					b.endTick();
				}

				@Override
				public void push(String location) {
					a.push(location);
					b.push(location);
				}

				@Override
				public void push(Supplier<String> locationGetter) {
					a.push(locationGetter);
					b.push(locationGetter);
				}

				@Override
				public void markSampleType(SampleType type) {
					a.markSampleType(type);
					b.markSampleType(type);
				}

				@Override
				public void pop() {
					a.pop();
					b.pop();
				}

				@Override
				public void swap(String location) {
					a.swap(location);
					b.swap(location);
				}

				@Override
				public void swap(Supplier<String> locationGetter) {
					a.swap(locationGetter);
					b.swap(locationGetter);
				}

				@Override
				public void visit(String marker, int num) {
					a.visit(marker, num);
					b.visit(marker, num);
				}

				@Override
				public void visit(Supplier<String> markerGetter, int num) {
					a.visit(markerGetter, num);
					b.visit(markerGetter, num);
				}
			};
		}
	}
}
