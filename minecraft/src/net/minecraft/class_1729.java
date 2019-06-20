package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public abstract class class_1729<C extends class_1263> extends class_1703 {
	public class_1729(class_3917<?> arg, int i) {
		super(arg, i);
	}

	public void method_17697(boolean bl, class_1860<?> arg, class_3222 arg2) {
		new class_2955<>(this).method_12826(arg2, (class_1860<C>)arg, bl);
	}

	public abstract void method_7654(class_1662 arg);

	public abstract void method_7657();

	public abstract boolean method_7652(class_1860<? super C> arg);

	public abstract int method_7655();

	public abstract int method_7653();

	public abstract int method_7656();

	@Environment(EnvType.CLIENT)
	public abstract int method_7658();
}
