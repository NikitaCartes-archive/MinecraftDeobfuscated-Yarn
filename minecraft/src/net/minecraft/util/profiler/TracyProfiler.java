package net.minecraft.util.profiler;

import com.mojang.jtracy.Plot;
import com.mojang.jtracy.TracyClient;
import com.mojang.jtracy.Zone;
import com.mojang.logging.LogUtils;
import java.lang.StackWalker.Option;
import java.lang.StackWalker.StackFrame;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.SharedConstants;
import org.slf4j.Logger;

public class TracyProfiler implements Profiler {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final StackWalker STACK_WALKER = StackWalker.getInstance(Set.of(Option.RETAIN_CLASS_REFERENCE), 5);
	private final List<Zone> zones = new ArrayList();
	private final Map<String, TracyProfiler.Marker> markers = new HashMap();
	private final String threadName = Thread.currentThread().getName();

	@Override
	public void startTick() {
	}

	@Override
	public void endTick() {
		for (TracyProfiler.Marker marker : this.markers.values()) {
			marker.setCount(0);
		}
	}

	@Override
	public void push(String location) {
		String string = "";
		String string2 = "";
		int i = 0;
		if (SharedConstants.isDevelopment) {
			Optional<StackFrame> optional = (Optional<StackFrame>)STACK_WALKER.walk(
				stream -> stream.filter(frame -> frame.getDeclaringClass() != TracyProfiler.class && frame.getDeclaringClass() != Profiler.UnionProfiler.class).findFirst()
			);
			if (optional.isPresent()) {
				StackFrame stackFrame = (StackFrame)optional.get();
				string = stackFrame.getMethodName();
				string2 = stackFrame.getFileName();
				i = stackFrame.getLineNumber();
			}
		}

		Zone zone = TracyClient.beginZone(location, string, string2, i);
		this.zones.add(zone);
	}

	@Override
	public void push(Supplier<String> locationGetter) {
		this.push((String)locationGetter.get());
	}

	@Override
	public void pop() {
		if (this.zones.isEmpty()) {
			LOGGER.error("Tried to pop one too many times! Mismatched push() and pop()?");
		} else {
			Zone zone = (Zone)this.zones.removeLast();
			zone.close();
		}
	}

	@Override
	public void swap(String location) {
		this.pop();
		this.push(location);
	}

	@Override
	public void swap(Supplier<String> locationGetter) {
		this.pop();
		this.push((String)locationGetter.get());
	}

	@Override
	public void markSampleType(SampleType type) {
	}

	@Override
	public void visit(String marker, int num) {
		((TracyProfiler.Marker)this.markers.computeIfAbsent(marker, markerName -> new TracyProfiler.Marker(this.threadName + " " + marker))).increment(num);
	}

	@Override
	public void visit(Supplier<String> markerGetter, int num) {
		this.visit((String)markerGetter.get(), num);
	}

	private Zone getCurrentZone() {
		return (Zone)this.zones.getLast();
	}

	@Override
	public void addZoneText(String label) {
		this.getCurrentZone().addText(label);
	}

	@Override
	public void addZoneValue(long value) {
		this.getCurrentZone().addValue(value);
	}

	@Override
	public void setZoneColor(int color) {
		this.getCurrentZone().setColor(color);
	}

	static final class Marker {
		private final Plot plot;
		private int count;

		Marker(String name) {
			this.plot = TracyClient.createPlot(name);
			this.count = 0;
		}

		void setCount(int count) {
			this.count = count;
			this.plot.setValue((double)count);
		}

		void increment(int count) {
			this.setCount(this.count + count);
		}
	}
}
