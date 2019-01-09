package net.minecraft;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public abstract class class_349 extends class_339 {
	@Nullable
	private final class_315.class_316 field_2141;

	public class_349(int i, int j, int k, String string) {
		this(i, j, k, null, string);
	}

	public class_349(int i, int j, int k, @Nullable class_315.class_316 arg, String string) {
		this(i, j, k, 150, 20, arg, string);
	}

	public class_349(int i, int j, int k, int l, int m, @Nullable class_315.class_316 arg, String string) {
		super(i, j, k, l, m, string);
		this.field_2141 = arg;
	}

	@Nullable
	public class_315.class_316 method_1899() {
		return this.field_2141;
	}
}
