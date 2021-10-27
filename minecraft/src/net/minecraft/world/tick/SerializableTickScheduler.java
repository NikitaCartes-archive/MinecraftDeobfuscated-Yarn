package net.minecraft.world.tick;

import java.util.function.Function;
import net.minecraft.nbt.NbtElement;

public interface SerializableTickScheduler<T> {
	NbtElement toNbt(long time, Function<T, String> typeToNameFunction);
}
