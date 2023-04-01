package net.minecraft;

import java.util.stream.Stream;
import java.util.stream.Stream.Builder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.util.math.random.Random;

public class class_8322 extends class_8266.class_8268 {
	public class_8322() {
		super(0);
	}

	@Override
	public Text method_50133(int i) {
		return Text.translatable("rule.explosion_power.set", i);
	}

	@Override
	public Text method_50132(int i, int j) {
		return Text.translatable("rule.explosion_power.change", i, j);
	}

	@Override
	public Stream<class_8291> method_50118(MinecraftServer minecraftServer, Random random, int i) {
		int j = this.method_50124();
		Builder<class_8291> builder = Stream.builder();
		builder.accept(this.method_50125(1));
		if (random.nextFloat() < 0.01F) {
			builder.accept(this.method_50125(3));
		}

		if (j > 0) {
			builder.accept(this.method_50125(-1));
		}

		return builder.build();
	}
}
