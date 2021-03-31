package net.minecraft.util.profiler;

import java.util.function.Supplier;
import javax.annotation.Nullable;

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
}
