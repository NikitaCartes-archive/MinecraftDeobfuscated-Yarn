package net.minecraft;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.gen.HeightContext;
import net.minecraft.world.gen.YOffset;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_6124 extends class_6122 {
	public static final Codec<class_6124> field_31544 = RecordCodecBuilder.create(
			instance -> instance.group(
						YOffset.OFFSET_CODEC.fieldOf("min_inclusive").forGetter(arg -> arg.field_31546),
						YOffset.OFFSET_CODEC.fieldOf("max_inclusive").forGetter(arg -> arg.field_31547)
					)
					.apply(instance, class_6124::new)
		)
		.comapFlatMap(DataResult::success, Function.identity());
	private static final Logger field_31545 = LogManager.getLogger();
	private final YOffset field_31546;
	private final YOffset field_31547;

	private class_6124(YOffset yOffset, YOffset yOffset2) {
		this.field_31546 = yOffset;
		this.field_31547 = yOffset2;
	}

	public static class_6124 method_35396(YOffset yOffset, YOffset yOffset2) {
		return new class_6124(yOffset, yOffset2);
	}

	@Override
	public int method_35391(Random random, HeightContext heightContext) {
		int i = this.field_31546.getY(heightContext);
		int j = this.field_31547.getY(heightContext);
		if (i > j) {
			field_31545.warn("Empty height range: {}", this);
			return i;
		} else {
			return MathHelper.nextBetween(random, i, j);
		}
	}

	@Override
	public class_6123<?> method_35388() {
		return class_6123.field_31542;
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (object != null && this.getClass() == object.getClass()) {
			class_6124 lv = (class_6124)object;
			return this.field_31546.equals(lv.field_31546) && this.field_31547.equals(lv.field_31547);
		} else {
			return false;
		}
	}

	public int hashCode() {
		return Objects.hash(new Object[]{this.field_31546, this.field_31547});
	}

	public String toString() {
		return "[" + this.field_31546 + '-' + this.field_31547 + ']';
	}
}
