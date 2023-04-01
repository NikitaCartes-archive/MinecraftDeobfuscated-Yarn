package net.minecraft;

import java.util.stream.Stream;
import java.util.stream.Stream.Builder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.util.StringHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;

public class class_8319 extends class_8266.class_8268 {
	private static final int field_43805 = 100;

	public class_8319() {
		super(24000);
	}

	public int method_50343(Random random) {
		float f = 24000.0F / (float)this.method_50124();
		int i = MathHelper.floor(f);
		if (random.nextFloat() < MathHelper.fractionalPart(f)) {
			i++;
		}

		return i;
	}

	@Override
	public Text method_50133(int i) {
		return Text.translatable("rule.day_length.set", StringHelper.formatTicks((long)i));
	}

	@Override
	public Text method_50132(int i, int j) {
		return Text.translatable("rule.day_length.change", StringHelper.formatTicks((long)i), StringHelper.formatTicks((long)j));
	}

	@Override
	public Stream<class_8291> method_50118(MinecraftServer minecraftServer, Random random, int i) {
		int j = this.method_50124();
		Builder<class_8291> builder = Stream.builder();
		if (j > 100 && random.nextBoolean()) {
			builder.accept(this.method_50125(-j / 2));
		}

		builder.accept(this.method_50125(j));
		return builder.build();
	}
}
