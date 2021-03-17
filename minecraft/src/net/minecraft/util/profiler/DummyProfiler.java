package net.minecraft.util.profiler;

import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

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

	@Environment(EnvType.CLIENT)
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
	@Environment(EnvType.CLIENT)
	@Override
	public ProfilerSystem.LocatedInfo method_34696(String string) {
		return null;
	}
}
