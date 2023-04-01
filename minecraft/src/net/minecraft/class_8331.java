package net.minecraft;

import java.util.stream.Stream;
import java.util.stream.Stream.Builder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.util.StringHelper;
import net.minecraft.util.math.random.Random;

public class class_8331 extends class_8266.class_8268 {
	public class_8331() {
		super(6000);
	}

	@Override
	public Text method_50133(int i) {
		return Text.translatable("rule.item_despawn_time.set", StringHelper.formatTicks((long)i));
	}

	@Override
	public Text method_50132(int i, int j) {
		return Text.translatable("rule.item_despawn_time.change", StringHelper.formatTicks((long)i), StringHelper.formatTicks((long)j));
	}

	@Override
	public Stream<class_8291> method_50118(MinecraftServer minecraftServer, Random random, int i) {
		int j = this.method_50124();
		Builder<class_8291> builder = Stream.builder();
		if (j > 1200) {
			builder.accept(this.method_50125(-1200));
		}

		builder.accept(this.method_50125(1200));
		return builder.build();
	}
}
