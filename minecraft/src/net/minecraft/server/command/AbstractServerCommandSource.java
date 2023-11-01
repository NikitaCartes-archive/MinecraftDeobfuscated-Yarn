package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.ResultConsumer;
import com.mojang.brigadier.exceptions.CommandExceptionType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.command.ReturnValueConsumer;
import net.minecraft.server.function.Tracer;

public interface AbstractServerCommandSource<T extends AbstractServerCommandSource<T>> {
	boolean hasPermissionLevel(int level);

	T withReturnValueConsumer(ReturnValueConsumer returnValueConsumer);

	ReturnValueConsumer getReturnValueConsumer();

	default T withDummyResultStorer() {
		return this.withReturnValueConsumer(ReturnValueConsumer.EMPTY);
	}

	CommandDispatcher<T> getDispatcher();

	void handleException(CommandExceptionType type, Message message, boolean silent, @Nullable Tracer tracer);

	boolean isSilent();

	default void handleException(CommandSyntaxException exception, boolean silent, @Nullable Tracer tracer) {
		this.handleException(exception.getType(), exception.getRawMessage(), silent, tracer);
	}

	static <T extends AbstractServerCommandSource<T>> ResultConsumer<T> asResultConsumer() {
		return (context, success, result) -> context.getSource().getReturnValueConsumer().onResult(success, result);
	}
}
