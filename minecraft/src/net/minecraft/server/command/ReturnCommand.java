package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.ContextChain;
import java.util.List;
import net.minecraft.command.ControlFlowAware;
import net.minecraft.command.ExecutionControl;
import net.minecraft.command.Forkable;
import net.minecraft.command.SingleCommandAction;

public class ReturnCommand {
	public static <T extends AbstractServerCommandSource<T>> void register(CommandDispatcher<T> dispatcher) {
		dispatcher.register(
			((LiteralArgumentBuilder)LiteralArgumentBuilder.literal("return").requires(source -> source.hasPermissionLevel(2)))
				.then(RequiredArgumentBuilder.<T, Integer>argument("value", IntegerArgumentType.integer()).executes(new ReturnCommand.Command<>()))
				.then(LiteralArgumentBuilder.<T>literal("run").forward(dispatcher.getRoot(), new ReturnCommand.ReturnRunRedirector<>(), false))
		);
	}

	static class Command<T extends AbstractServerCommandSource<T>> implements ControlFlowAware.Command<T> {
		public void execute(T abstractServerCommandSource, ContextChain<T> contextChain, boolean bl, ExecutionControl<T> executionControl) {
			executionControl.doReturn();
			int i = IntegerArgumentType.getInteger(contextChain.getTopContext(), "value");
			abstractServerCommandSource.consumeResult(i);
		}
	}

	static class ReturnRunRedirector<T extends AbstractServerCommandSource<T>> implements Forkable.RedirectModifier<T> {
		@Override
		public void execute(List<T> sources, ContextChain<T> contextChain, boolean forkedMode, ExecutionControl<T> control) {
			if (!sources.isEmpty()) {
				ContextChain<T> contextChain2 = contextChain.nextStage();
				String string = contextChain2.getTopContext().getInput();
				List<T> list = sources.stream().map(source -> source.withResultStorer((sourcex, success, result) -> {
						control.doReturn();
						sourcex.consumeResult(result);
					})).toList();
				control.enqueueAction(new SingleCommandAction.MultiSource<>(string, contextChain2, forkedMode, list));
			}
		}
	}
}
