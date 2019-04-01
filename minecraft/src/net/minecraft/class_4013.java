package net.minecraft;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public interface class_4013 extends class_3302 {
	@Override
	default CompletableFuture<Void> reload(class_3302.class_4045 arg, class_3300 arg2, class_3695 arg3, class_3695 arg4, Executor executor, Executor executor2) {
		return arg.method_18352(class_3902.field_17274).thenRunAsync(() -> this.method_14491(arg2), executor2);
	}

	void method_14491(class_3300 arg);
}
