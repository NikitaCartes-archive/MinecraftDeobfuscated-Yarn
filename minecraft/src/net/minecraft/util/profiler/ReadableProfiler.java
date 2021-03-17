package net.minecraft.util.profiler;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public interface ReadableProfiler extends Profiler {
	ProfileResult getResult();

	@Nullable
	@Environment(EnvType.CLIENT)
	ProfilerSystem.LocatedInfo method_34696(String string);
}
