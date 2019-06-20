package net.minecraft;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_4005 extends class_1049 {
	private CompletableFuture<class_1049.class_4006> field_17894;

	public class_4005(class_3300 arg, class_2960 arg2, Executor executor) {
		super(arg2);
		this.field_17894 = CompletableFuture.supplyAsync(() -> class_1049.class_4006.method_18156(arg, arg2), executor);
	}

	@Override
	protected class_1049.class_4006 method_18153(class_3300 arg) {
		if (this.field_17894 != null) {
			class_1049.class_4006 lv = (class_1049.class_4006)this.field_17894.join();
			this.field_17894 = null;
			return lv;
		} else {
			return class_1049.class_4006.method_18156(arg, this.field_5224);
		}
	}

	public CompletableFuture<Void> method_18148() {
		return this.field_17894 == null ? CompletableFuture.completedFuture(null) : this.field_17894.thenApply(arg -> null);
	}

	@Override
	public void method_18169(class_1060 arg, class_3300 arg2, class_2960 arg3, Executor executor) {
		this.field_17894 = CompletableFuture.supplyAsync(() -> class_1049.class_4006.method_18156(arg2, this.field_5224), class_156.method_18349());
		this.field_17894.thenRunAsync(() -> arg.method_4616(this.field_5224, this), executor);
	}
}
