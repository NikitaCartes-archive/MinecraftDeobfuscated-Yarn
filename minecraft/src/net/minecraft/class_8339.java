package net.minecraft;

import java.util.stream.Stream;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.util.math.random.Random;

public class class_8339 extends class_8266 {
	public static final int field_43904 = 3;

	public class_8339() {
		super(0);
	}

	public double method_50394() {
		return (double)this.method_50124() / 3.0;
	}

	public boolean method_50395() {
		return this.method_50394() != 0.0;
	}

	@Override
	protected Text method_50126(int i, int j) {
		i = Math.min(i, 3);
		j = Math.min(j, 3);
		return j > i ? Text.translatable("rule.moon." + j) : Text.translatable("rule.moon." + i);
	}

	@Override
	public Stream<class_8291> method_50118(MinecraftServer minecraftServer, Random random, int i) {
		return this.method_50124() == 3 ? Stream.empty() : Stream.of(this.method_50125(1));
	}
}
