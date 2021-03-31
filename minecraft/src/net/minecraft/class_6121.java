package net.minecraft;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import net.minecraft.world.gen.HeightContext;
import net.minecraft.world.gen.YOffset;

public class class_6121 extends class_6122 {
	public static final class_6121 field_31536 = new class_6121(YOffset.fixed(0));
	public static final Codec<class_6121> field_31537 = Codec.either(
			YOffset.OFFSET_CODEC,
			RecordCodecBuilder.create(
				instance -> instance.group(YOffset.OFFSET_CODEC.fieldOf("value").forGetter(arg -> arg.field_31538)).apply(instance, class_6121::new)
			)
		)
		.xmap(either -> either.map(class_6121::method_35383, arg -> arg), arg -> Either.left(arg.field_31538));
	private final YOffset field_31538;

	public static class_6121 method_35383(YOffset yOffset) {
		return new class_6121(yOffset);
	}

	private class_6121(YOffset yOffset) {
		this.field_31538 = yOffset;
	}

	public YOffset method_35385() {
		return this.field_31538;
	}

	@Override
	public int method_35391(Random random, HeightContext heightContext) {
		return this.field_31538.getY(heightContext);
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (object != null && this.getClass() == object.getClass()) {
			class_6121 lv = (class_6121)object;
			return this.field_31538.equals(lv.field_31538);
		} else {
			return false;
		}
	}

	public int hashCode() {
		return this.field_31538.hashCode();
	}

	@Override
	public class_6123<?> method_35388() {
		return class_6123.field_31541;
	}

	public String toString() {
		return this.field_31538.toString();
	}
}
