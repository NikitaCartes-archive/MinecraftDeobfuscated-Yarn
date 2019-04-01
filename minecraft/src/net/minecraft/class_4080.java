package net.minecraft;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public abstract class class_4080<T> implements class_3302 {
	@Override
	public final CompletableFuture<Void> reload(
		class_3302.class_4045 arg, class_3300 arg2, class_3695 arg3, class_3695 arg4, Executor executor, Executor executor2
	) {
		return CompletableFuture.supplyAsync(() -> this.method_18789(arg2, arg3), executor)
			.thenCompose(arg::method_18352)
			.thenAcceptAsync(object -> this.method_18788((T)object, arg2, arg4), executor2);
	}

	protected abstract T method_18789(class_3300 arg, class_3695 arg2);

	protected abstract void method_18788(T object, class_3300 arg, class_3695 arg2);
}
