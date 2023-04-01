package net.minecraft;

import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.util.math.random.Random;

public abstract class class_8277 implements class_8289 {
	static final Text field_43477 = Text.literal("???");

	@Override
	public Stream<class_8291> method_50119() {
		return Stream.empty();
	}

	protected abstract class class_8278 implements class_8291 {
		@Override
		public class_8289 method_50121() {
			return class_8277.this;
		}

		protected abstract Text method_50166();

		@Override
		public Text method_50130(class_8290 arg) {
			return switch (arg) {
				case REPEAL -> class_8277.field_43477;
				case APPROVE -> this.method_50166();
			};
		}

		public abstract void method_50165(MinecraftServer minecraftServer);

		@Override
		public void method_50164(class_8290 arg, MinecraftServer minecraftServer) {
			class_8291.super.method_50164(arg, minecraftServer);
			if (arg == class_8290.APPROVE) {
				this.method_50165(minecraftServer);
			}
		}

		@Override
		public void method_50122(class_8290 arg) {
		}
	}

	public abstract static class class_8279 extends class_8277.class_8280 {
		protected abstract Optional<class_8291> method_50167(MinecraftServer minecraftServer, Random random);

		@Override
		public Stream<class_8291> method_50118(MinecraftServer minecraftServer, Random random, int i) {
			Optional<class_8291> optional = this.method_50167(minecraftServer, random);
			Stream<class_8291> stream = Stream.generate(() -> this.method_50169(minecraftServer, random)).flatMap(Optional::stream);
			return random.nextBoolean() ? optional.stream().limit((long)i) : stream.limit((long)i);
		}
	}

	public abstract static class class_8280 extends class_8277 {
		protected abstract Optional<class_8291> method_50169(MinecraftServer minecraftServer, Random random);

		@Override
		public Stream<class_8291> method_50118(MinecraftServer minecraftServer, Random random, int i) {
			return IntStream.range(0, i * 3).mapToObj(ix -> this.method_50169(minecraftServer, random)).flatMap(Optional::stream).limit((long)i);
		}
	}
}
