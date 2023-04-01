package net.minecraft;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.stream.Stream;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.util.math.random.Random;

public class class_8264 implements class_8289 {
	final Text field_43436;
	boolean field_43437;
	private final class_8264.class_8265 field_43438 = new class_8264.class_8265();
	private final Codec<class_8291> field_43439 = RecordCodecBuilder.create(instance -> instance.point(this.field_43438));

	public class_8264(Text text) {
		this.field_43436 = text;
	}

	public boolean method_50116() {
		return this.field_43437;
	}

	@Override
	public Stream<class_8291> method_50119() {
		return this.field_43437 ? Stream.of(this.field_43438) : Stream.empty();
	}

	@Override
	public Stream<class_8291> method_50118(MinecraftServer minecraftServer, Random random, int i) {
		return !this.field_43437 && i > 0 ? Stream.of(this.field_43438) : Stream.empty();
	}

	@Override
	public Codec<class_8291> method_50120() {
		return this.field_43439;
	}

	class class_8265 implements class_8291.class_8292 {
		@Override
		public class_8289 method_50121() {
			return class_8264.this;
		}

		@Override
		public Text method_50123() {
			return class_8264.this.field_43436;
		}

		@Override
		public void method_50122(class_8290 arg) {
			class_8264.this.field_43437 = arg == class_8290.APPROVE;
		}
	}
}
