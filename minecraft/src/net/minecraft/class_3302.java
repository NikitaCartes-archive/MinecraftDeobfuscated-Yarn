package net.minecraft;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public interface class_3302 {
	CompletableFuture<Void> reload(class_3302.class_4045 arg, class_3300 arg2, class_3695 arg3, class_3695 arg4, Executor executor, Executor executor2);

	public interface class_4045 {
		<T> CompletableFuture<T> method_18352(T object);
	}
}
