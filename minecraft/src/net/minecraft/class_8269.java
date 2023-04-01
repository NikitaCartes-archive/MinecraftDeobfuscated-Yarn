package net.minecraft;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.stream.Stream;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.random.Random;

public abstract class class_8269<T> extends class_8275<T, Integer> {
	private final int field_43448;
	private final int field_43449;
	private final Object2IntMap<T> field_43450 = new Object2IntOpenHashMap<>();

	protected class_8269(Codec<T> codec, int i, int j) {
		super(codec, Codec.INT);
		this.field_43449 = i;
		this.field_43448 = j;
	}

	protected void method_50138(T object, Integer integer) {
		this.field_43450.put(object, integer.intValue());
	}

	@Override
	protected void method_50136(T object) {
		this.field_43450.removeInt(object);
	}

	public int method_50141(T object) {
		return this.field_43450.getInt(object);
	}

	public float method_50142(T object) {
		int i = this.field_43450.getInt(object);
		return (float)Math.pow(2.0, (double)i);
	}

	protected static String method_50134(int i) {
		return i < 0 ? "1/" + (1 << -i) : String.valueOf(1 << i);
	}

	@Override
	public Stream<class_8291> method_50119() {
		return this.field_43450.object2IntEntrySet().stream().map(entry -> new class_8275.class_8276(entry.getKey(), entry.getIntValue()));
	}

	protected abstract Stream<T> method_50140(MinecraftServer minecraftServer, Random random);

	@Override
	public Stream<class_8291> method_50118(MinecraftServer minecraftServer, Random random, int i) {
		return this.method_50140(minecraftServer, random).limit((long)i * 3L).mapMulti((object, consumer) -> {
			int ix = this.field_43450.getInt(object);
			if (ix < this.field_43448) {
				consumer.accept(new class_8275.class_8276(object, ix + 1));
			}

			if (this.field_43449 < ix) {
				consumer.accept(new class_8275.class_8276(object, ix - 1));
			}
		}).limit((long)i);
	}
}
