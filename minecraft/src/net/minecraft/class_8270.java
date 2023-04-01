package net.minecraft;

import java.util.stream.Stream;
import java.util.stream.Stream.Builder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.util.math.random.Random;

public abstract class class_8270 extends class_8266.class_8268 {
	private final int field_43451;
	private final int field_43452;

	public class_8270(int i, int j, int k, Text text, Text text2) {
		super(i);
		this.field_43451 = j;
		this.field_43452 = k;
	}

	public int method_50143() {
		return 1 << this.method_50124();
	}

	public float method_50144() {
		return (float)Math.pow(2.0, (double)this.method_50124());
	}

	@Override
	public Stream<class_8291> method_50118(MinecraftServer minecraftServer, Random random, int i) {
		Builder<class_8291> builder = Stream.builder();
		int j = this.method_50124();
		if (j < this.field_43452) {
			builder.add(this.method_50125(1));
		}

		if (j > this.field_43451) {
			builder.add(this.method_50125(-1));
		}

		return builder.build();
	}
}
