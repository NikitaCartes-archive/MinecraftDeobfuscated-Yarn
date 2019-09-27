package net.minecraft;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_4589 implements class_4588 {
	private final Iterable<class_4588> field_20899;

	public class_4589(ImmutableList<class_4588> immutableList) {
		for (int i = 0; i < immutableList.size(); i++) {
			for (int j = i + 1; j < immutableList.size(); j++) {
				if (immutableList.get(i) == immutableList.get(j)) {
					throw new IllegalArgumentException("Duplicate delegates");
				}
			}
		}

		this.field_20899 = immutableList;
	}

	@Override
	public class_4588 vertex(double d, double e, double f) {
		this.field_20899.forEach(arg -> arg.vertex(d, e, f));
		return this;
	}

	@Override
	public class_4588 color(int i, int j, int k, int l) {
		this.field_20899.forEach(arg -> arg.color(i, j, k, l));
		return this;
	}

	@Override
	public class_4588 texture(float f, float g) {
		this.field_20899.forEach(arg -> arg.texture(f, g));
		return this;
	}

	@Override
	public class_4588 method_22917(int i, int j) {
		this.field_20899.forEach(arg -> arg.method_22921(i, j));
		return this;
	}

	@Override
	public class_4588 method_22921(int i, int j) {
		this.field_20899.forEach(arg -> arg.method_22921(i, j));
		return this;
	}

	@Override
	public class_4588 method_22914(float f, float g, float h) {
		this.field_20899.forEach(arg -> arg.method_22914(f, g, h));
		return this;
	}

	@Override
	public void next() {
		this.field_20899.forEach(class_4588::next);
	}

	@Override
	public void method_22922(int i, int j) {
		this.field_20899.forEach(arg -> arg.method_22922(i, j));
	}

	@Override
	public void method_22923() {
		this.field_20899.forEach(class_4588::method_22923);
	}
}
