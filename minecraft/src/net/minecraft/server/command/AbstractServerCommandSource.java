package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.ResultConsumer;
import com.mojang.brigadier.exceptions.CommandExceptionType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.function.IntConsumer;
import javax.annotation.Nullable;
import net.minecraft.command.ResultStorer;
import net.minecraft.server.function.Tracer;

public interface AbstractServerCommandSource<T extends AbstractServerCommandSource<T>> {
	boolean hasPermissionLevel(int level);

	void consumeResult(boolean success, int result);

	void consumeResult(int result);

	T withReturnValueConsumer(IntConsumer returnValueConsumer);

	T withResultStorer(ResultStorer<T> resultStorer);

	T withDummyResultStorer();

	CommandDispatcher<T> getDispatcher();

	void handleException(CommandExceptionType type, Message message, boolean silent, @Nullable Tracer tracer);

	default void handleException(CommandSyntaxException exception, boolean silent, @Nullable Tracer tracer) {
		this.handleException(exception.getType(), exception.getRawMessage(), silent, tracer);
	}

	static <T extends AbstractServerCommandSource<T>> ResultConsumer<T> asResultConsumer() {
		return (context, success, result) -> context.getSource().consumeResult(success, result);
	}
}
