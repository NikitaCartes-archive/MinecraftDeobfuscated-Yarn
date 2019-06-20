package net.minecraft;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_1735 {
	private final int field_7875;
	public final class_1263 field_7871;
	public int field_7874;
	public int field_7873;
	public int field_7872;

	public class_1735(class_1263 arg, int i, int j, int k) {
		this.field_7871 = arg;
		this.field_7875 = i;
		this.field_7873 = j;
		this.field_7872 = k;
	}

	public void method_7670(class_1799 arg, class_1799 arg2) {
		int i = arg2.method_7947() - arg.method_7947();
		if (i > 0) {
			this.method_7678(arg2, i);
		}
	}

	protected void method_7678(class_1799 arg, int i) {
	}

	protected void method_7672(int i) {
	}

	protected void method_7669(class_1799 arg) {
	}

	public class_1799 method_7667(class_1657 arg, class_1799 arg2) {
		this.method_7668();
		return arg2;
	}

	public boolean method_7680(class_1799 arg) {
		return true;
	}

	public class_1799 method_7677() {
		return this.field_7871.method_5438(this.field_7875);
	}

	public boolean method_7681() {
		return !this.method_7677().method_7960();
	}

	public void method_7673(class_1799 arg) {
		this.field_7871.method_5447(this.field_7875, arg);
		this.method_7668();
	}

	public void method_7668() {
		this.field_7871.method_5431();
	}

	public int method_7675() {
		return this.field_7871.method_5444();
	}

	public int method_7676(class_1799 arg) {
		return this.method_7675();
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	public String method_7679() {
		return null;
	}

	public class_1799 method_7671(int i) {
		return this.field_7871.method_5434(this.field_7875, i);
	}

	public boolean method_7674(class_1657 arg) {
		return true;
	}

	@Environment(EnvType.CLIENT)
	public boolean method_7682() {
		return true;
	}
}
