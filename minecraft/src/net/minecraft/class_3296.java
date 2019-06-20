package net.minecraft;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public interface class_3296 extends class_3300 {
	CompletableFuture<class_3902> method_14478(Executor executor, Executor executor2, List<class_3262> list, CompletableFuture<class_3902> completableFuture);

	@Environment(EnvType.CLIENT)
	class_4011 method_18230(Executor executor, Executor executor2, CompletableFuture<class_3902> completableFuture);

	@Environment(EnvType.CLIENT)
	class_4011 method_18232(Executor executor, Executor executor2, CompletableFuture<class_3902> completableFuture, List<class_3262> list);

	void method_14477(class_3302 arg);
}
