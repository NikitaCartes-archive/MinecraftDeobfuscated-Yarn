package net.minecraft.util.profiler;

import com.google.common.collect.ImmutableSet;
import java.util.Set;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.client.util.profiler.SamplingChannel;
import org.apache.commons.lang3.tuple.Pair;

public class DummyProfiler implements ReadableProfiler {
	public static final DummyProfiler INSTANCE = new DummyProfiler();

	private DummyProfiler() {
	}

	@Override
	public void startTick() {
	}

	@Override
	public void endTick() {
	}

	@Override
	public void push(String location) {
	}

	@Override
	public void push(Supplier<String> locationGetter) {
	}

	@Override
	public void method_37167(SamplingChannel samplingChannel) {
	}

	@Override
	public void pop() {
	}

	@Override
	public void swap(String location) {
	}

	@Override
	public void swap(Supplier<String> locationGetter) {
	}

	@Override
	public void visit(String marker) {
	}

	@Override
	public void visit(Supplier<String> markerGetter) {
	}

	@Override
	public ProfileResult getResult() {
		return EmptyProfileResult.INSTANCE;
	}

	@Nullable
	@Override
	public ProfilerSystem.LocatedInfo getInfo(String name) {
		return null;
	}

	@Override
	public Set<Pair<String, SamplingChannel>> method_37168() {
		return ImmutableSet.of();
	}
}
