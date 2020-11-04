package net.minecraft;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_5606 {
	private final List<class_5604> field_27719 = Lists.<class_5604>newArrayList();
	private int field_27720;
	private int field_27721;
	private boolean field_27722;

	public class_5606 method_32101(int i, int j) {
		this.field_27720 = i;
		this.field_27721 = j;
		return this;
	}

	public class_5606 method_32096() {
		return this.method_32106(true);
	}

	public class_5606 method_32106(boolean bl) {
		this.field_27722 = bl;
		return this;
	}

	public class_5606 method_32105(String string, float f, float g, float h, int i, int j, int k, class_5605 arg, int l, int m) {
		this.method_32101(l, m);
		this.field_27719
			.add(new class_5604(string, (float)this.field_27720, (float)this.field_27721, f, g, h, (float)i, (float)j, (float)k, arg, this.field_27722, 1.0F, 1.0F));
		return this;
	}

	public class_5606 method_32104(String string, float f, float g, float h, int i, int j, int k, int l, int m) {
		this.method_32101(l, m);
		this.field_27719
			.add(
				new class_5604(
					string, (float)this.field_27720, (float)this.field_27721, f, g, h, (float)i, (float)j, (float)k, class_5605.field_27715, this.field_27722, 1.0F, 1.0F
				)
			);
		return this;
	}

	public class_5606 method_32097(float f, float g, float h, float i, float j, float k) {
		this.field_27719
			.add(new class_5604(null, (float)this.field_27720, (float)this.field_27721, f, g, h, i, j, k, class_5605.field_27715, this.field_27722, 1.0F, 1.0F));
		return this;
	}

	public class_5606 method_32102(String string, float f, float g, float h, float i, float j, float k) {
		this.field_27719
			.add(new class_5604(string, (float)this.field_27720, (float)this.field_27721, f, g, h, i, j, k, class_5605.field_27715, this.field_27722, 1.0F, 1.0F));
		return this;
	}

	public class_5606 method_32103(String string, float f, float g, float h, float i, float j, float k, class_5605 arg) {
		this.field_27719.add(new class_5604(string, (float)this.field_27720, (float)this.field_27721, f, g, h, i, j, k, arg, this.field_27722, 1.0F, 1.0F));
		return this;
	}

	public class_5606 method_32100(float f, float g, float h, float i, float j, float k, boolean bl) {
		this.field_27719.add(new class_5604(null, (float)this.field_27720, (float)this.field_27721, f, g, h, i, j, k, class_5605.field_27715, bl, 1.0F, 1.0F));
		return this;
	}

	public class_5606 method_32099(float f, float g, float h, float i, float j, float k, class_5605 arg, float l, float m) {
		this.field_27719.add(new class_5604(null, (float)this.field_27720, (float)this.field_27721, f, g, h, i, j, k, arg, this.field_27722, l, m));
		return this;
	}

	public class_5606 method_32098(float f, float g, float h, float i, float j, float k, class_5605 arg) {
		this.field_27719.add(new class_5604(null, (float)this.field_27720, (float)this.field_27721, f, g, h, i, j, k, arg, this.field_27722, 1.0F, 1.0F));
		return this;
	}

	public List<class_5604> method_32107() {
		return ImmutableList.copyOf(this.field_27719);
	}

	public static class_5606 method_32108() {
		return new class_5606();
	}
}
