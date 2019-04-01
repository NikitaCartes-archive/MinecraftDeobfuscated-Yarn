package net.minecraft;

import java.io.IOException;
import java.util.function.BooleanSupplier;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public abstract class class_2802 implements class_2823, AutoCloseable {
	@Nullable
	public class_2818 method_12126(int i, int j, boolean bl) {
		return (class_2818)this.method_12121(i, j, class_2806.field_12803, bl);
	}

	@Nullable
	@Override
	public class_1922 method_12246(int i, int j) {
		return this.method_12121(i, j, class_2806.field_12798, false);
	}

	public boolean method_12123(int i, int j) {
		return this.method_12121(i, j, class_2806.field_12803, false) != null;
	}

	@Nullable
	public abstract class_2791 method_12121(int i, int j, class_2806 arg, boolean bl);

	@Environment(EnvType.CLIENT)
	public abstract void method_12127(BooleanSupplier booleanSupplier);

	public abstract String method_12122();

	public abstract class_2794<?> method_12129();

	public void close() throws IOException {
	}

	public abstract class_3568 method_12130();

	public void method_12128(boolean bl, boolean bl2) {
	}

	public void method_12124(class_1923 arg, boolean bl) {
	}

	public boolean method_12125(class_1297 arg) {
		return true;
	}
}
