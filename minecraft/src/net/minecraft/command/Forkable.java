package net.minecraft.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.ContextChain;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Collection;
import java.util.List;

public interface Forkable<T> {
	void execute(T baseSource, List<T> sources, ContextChain<T> contextChain, ExecutionFlags flags, ExecutionControl<T> control);

	public interface RedirectModifier<T> extends com.mojang.brigadier.RedirectModifier<T>, Forkable<T> {
		@Override
		default Collection<T> apply(CommandContext<T> context) throws CommandSyntaxException {
			throw new UnsupportedOperationException("This function should not run");
		}
	}
}
