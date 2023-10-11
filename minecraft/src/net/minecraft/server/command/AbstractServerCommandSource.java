package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ResultConsumer;
import java.util.function.IntConsumer;
import net.minecraft.command.ResultStorer;

public interface AbstractServerCommandSource<T extends AbstractServerCommandSource<T>> {
	boolean hasPermissionLevel(int level);

	void consumeResult(boolean success, int result);

	void consumeResult(int result);

	T withReturnValueConsumer(IntConsumer returnValueConsumer);

	T withResultStorer(ResultStorer<T> resultStorer);

	T withDummyResultStorer();

	CommandDispatcher<T> getDispatcher();

	static <T extends AbstractServerCommandSource<T>> ResultConsumer<T> asResultConsumer() {
		return (context, success, result) -> context.getSource().consumeResult(success, result);
	}
}
