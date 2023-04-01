package net.minecraft;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.util.math.random.Random;

public class class_8313 extends class_8275<Integer, Integer> {
	private final Int2IntMap field_43767 = new Int2IntOpenHashMap();

	public class_8313() {
		super(Codec.INT, Codec.INT);
	}

	public int method_50321(int i) {
		return this.field_43767.getOrDefault(i, i);
	}

	protected Text method_50162(Integer integer, Integer integer2) {
		return Text.translatable("rule.codepoint_replace", Character.toString(integer), Character.toString(integer2));
	}

	protected void method_50138(Integer integer, Integer integer2) {
		this.field_43767.put(integer.intValue(), integer2.intValue());
	}

	protected void method_50136(Integer integer) {
		this.field_43767.remove(integer.intValue());
	}

	@Override
	public Stream<class_8291> method_50119() {
		return this.field_43767.entrySet().stream().map(entry -> new class_8275.class_8276((Integer)entry.getKey(), (Integer)entry.getValue()));
	}

	@Override
	public Stream<class_8291> method_50118(MinecraftServer minecraftServer, Random random, int i) {
		return Stream.generate(() -> {
			if ((double)random.nextFloat() < 0.7) {
				int ix;
				int j;
				if (random.nextBoolean()) {
					ix = random.nextBetween(65, 90);
					j = Character.toLowerCase(ix);
				} else {
					ix = random.nextBetween(97, 122);
					j = Character.toUpperCase(ix);
				}

				return this.field_43767.containsKey(ix) ? Optional.empty() : Optional.of(new class_8275.class_8276(ix, j));
			} else {
				int ixx = random.nextBetween(32, 126);
				if (this.field_43767.containsKey(ixx)) {
					return Optional.empty();
				} else {
					int j = random.nextBetween(32, 126);
					return ixx == j ? Optional.empty() : Optional.of(new class_8275.class_8276(ixx, j));
				}
			}
		}).flatMap(Optional::stream).limit((long)i).map(arg -> arg);
	}
}
