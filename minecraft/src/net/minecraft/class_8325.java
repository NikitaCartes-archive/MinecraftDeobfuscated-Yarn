package net.minecraft;

import java.util.stream.Stream;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.util.math.random.Random;

public class class_8325 extends class_8266 {
	public static final int field_43869 = 11;

	public class_8325() {
		super(0);
	}

	public boolean method_50356() {
		return this.method_50124() >= 11;
	}

	@Override
	protected Text method_50126(int i, int j) {
		i = Math.min(i, 11);
		j = Math.min(j, 11);
		return i == j
			? Text.translatable("rule.footprint." + i)
			: Text.translatable("rule.footprint", Text.translatable("rule.footprint." + i), Text.translatable("rule.footprint." + j));
	}

	@Override
	public Stream<class_8291> method_50118(MinecraftServer minecraftServer, Random random, int i) {
		return this.method_50124() == 11 ? Stream.empty() : Stream.of(this.method_50125(1));
	}
}
