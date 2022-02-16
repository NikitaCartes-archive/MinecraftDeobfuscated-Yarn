package net.minecraft;

import java.util.function.Function;
import net.minecraft.world.gen.chunk.Blender;

public interface class_6910 {
	double method_40464(class_6910.class_6912 arg);

	void method_40470(double[] ds, class_6910.class_6911 arg);

	class_6910 method_40469(class_6910.class_6915 arg);

	double minValue();

	double maxValue();

	default class_6910 method_40468(double d, double e) {
		return new class_6916.class_6922(this, d, e);
	}

	default class_6910 method_40471() {
		return class_6916.method_40490(this, class_6916.class_6925.class_6926.ABS);
	}

	default class_6910 method_40472() {
		return class_6916.method_40490(this, class_6916.class_6925.class_6926.SQUARE);
	}

	default class_6910 method_40473() {
		return class_6916.method_40490(this, class_6916.class_6925.class_6926.CUBE);
	}

	default class_6910 method_40474() {
		return class_6916.method_40490(this, class_6916.class_6925.class_6926.HALF_NEGATIVE);
	}

	default class_6910 method_40475() {
		return class_6916.method_40490(this, class_6916.class_6925.class_6926.QUARTER_NEGATIVE);
	}

	default class_6910 method_40476() {
		return class_6916.method_40490(this, class_6916.class_6925.class_6926.SQUEEZE);
	}

	public interface class_6911 {
		class_6910.class_6912 method_40477(int i);

		void method_40478(double[] ds, class_6910 arg);
	}

	public interface class_6912 {
		int blockX();

		int blockY();

		int blockZ();

		default Blender getBlender() {
			return Blender.getNoBlending();
		}
	}

	public interface class_6913 extends class_6910 {
		@Override
		default void method_40470(double[] ds, class_6910.class_6911 arg) {
			arg.method_40478(ds, this);
		}

		@Override
		default class_6910 method_40469(class_6910.class_6915 arg) {
			return (class_6910)arg.apply(this);
		}
	}

	public static record class_6914(int blockX, int blockY, int blockZ) implements class_6910.class_6912 {
	}

	public interface class_6915 extends Function<class_6910, class_6910> {
	}
}
