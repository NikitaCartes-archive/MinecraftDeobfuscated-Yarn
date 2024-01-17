package net.minecraft.server.command;

import com.mojang.brigadier.context.CommandContext;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.command.argument.TestFunctionArgumentType;
import net.minecraft.test.StructureBlockFinder;
import net.minecraft.test.StructureTestUtil;
import net.minecraft.test.TestFunction;
import net.minecraft.test.TestFunctionFinder;
import net.minecraft.test.TestFunctions;
import net.minecraft.util.math.BlockPos;

public class TestFinder<T> implements StructureBlockFinder, TestFunctionFinder {
	static final TestFunctionFinder NOOP_TEST_FUNCTION_FINDER = Stream::empty;
	static final StructureBlockFinder NOOP_STRUCTURE_BLOCK_FINDER = Stream::empty;
	private final TestFunctionFinder testFunctionFinder;
	private final StructureBlockFinder structureBlockPosFinder;
	private final ServerCommandSource commandSource;
	private final Function<TestFinder<T>, T> runnerFactory;

	@Override
	public Stream<BlockPos> findStructureBlockPos() {
		return this.structureBlockPosFinder.findStructureBlockPos();
	}

	TestFinder(
		ServerCommandSource commandSource,
		Function<TestFinder<T>, T> runnerFactory,
		TestFunctionFinder testFunctionFinder,
		StructureBlockFinder structureBlockPosFinder
	) {
		this.commandSource = commandSource;
		this.runnerFactory = runnerFactory;
		this.testFunctionFinder = testFunctionFinder;
		this.structureBlockPosFinder = structureBlockPosFinder;
	}

	T createRunner() {
		return (T)this.runnerFactory.apply(this);
	}

	public ServerCommandSource getCommandSource() {
		return this.commandSource;
	}

	@Override
	public Stream<TestFunction> findTestFunctions() {
		return this.testFunctionFinder.findTestFunctions();
	}

	public static class Runners<T> {
		private final Function<TestFinder<T>, T> runnerFactory;

		public Runners(Function<TestFinder<T>, T> runnerFactory) {
			this.runnerFactory = runnerFactory;
		}

		public T surface(CommandContext<ServerCommandSource> context, int radius) {
			ServerCommandSource serverCommandSource = context.getSource();
			return new TestFinder<T>(
					serverCommandSource,
					this.runnerFactory,
					TestFinder.NOOP_TEST_FUNCTION_FINDER,
					() -> StructureTestUtil.findSurfaceStructureBlocks(radius, serverCommandSource.getPosition(), serverCommandSource.getWorld())
				)
				.createRunner();
		}

		public T nearest(CommandContext<ServerCommandSource> context) {
			ServerCommandSource serverCommandSource = context.getSource();
			BlockPos blockPos = BlockPos.ofFloored(serverCommandSource.getPosition());
			return new TestFinder<T>(
					serverCommandSource,
					this.runnerFactory,
					TestFinder.NOOP_TEST_FUNCTION_FINDER,
					() -> StructureTestUtil.findNearestStructureBlock(blockPos, 15, serverCommandSource.getWorld()).stream()
				)
				.createRunner();
		}

		public T allStructures(CommandContext<ServerCommandSource> context) {
			ServerCommandSource serverCommandSource = context.getSource();
			BlockPos blockPos = BlockPos.ofFloored(serverCommandSource.getPosition());
			return new TestFinder<T>(
					serverCommandSource,
					this.runnerFactory,
					TestFinder.NOOP_TEST_FUNCTION_FINDER,
					() -> StructureTestUtil.findStructureBlocks(blockPos, 200, serverCommandSource.getWorld())
				)
				.createRunner();
		}

		public T targeted(CommandContext<ServerCommandSource> context) {
			ServerCommandSource serverCommandSource = context.getSource();
			return new TestFinder<T>(
					serverCommandSource,
					this.runnerFactory,
					TestFinder.NOOP_TEST_FUNCTION_FINDER,
					() -> StructureTestUtil.findTargetedStructureBlock(
							BlockPos.ofFloored(serverCommandSource.getPosition()), serverCommandSource.getPlayer().getCameraEntity(), serverCommandSource.getWorld()
						)
				)
				.createRunner();
		}

		public T allTestFunctions(CommandContext<ServerCommandSource> context) {
			return new TestFinder<T>(context.getSource(), this.runnerFactory, () -> TestFunctions.getTestFunctions().stream(), TestFinder.NOOP_STRUCTURE_BLOCK_FINDER)
				.createRunner();
		}

		public T in(CommandContext<ServerCommandSource> context, String testClass) {
			return new TestFinder<T>(context.getSource(), this.runnerFactory, () -> TestFunctions.getTestFunctions(testClass), TestFinder.NOOP_STRUCTURE_BLOCK_FINDER)
				.createRunner();
		}

		public T failed(CommandContext<ServerCommandSource> context, boolean onlyRequired) {
			return new TestFinder<T>(
					context.getSource(),
					this.runnerFactory,
					() -> TestFunctions.getFailedTestFunctions().filter(function -> !onlyRequired || function.required()),
					TestFinder.NOOP_STRUCTURE_BLOCK_FINDER
				)
				.createRunner();
		}

		public T named(CommandContext<ServerCommandSource> context, String name) {
			return new TestFinder<T>(
					context.getSource(), this.runnerFactory, () -> Stream.of(TestFunctionArgumentType.getFunction(context, name)), TestFinder.NOOP_STRUCTURE_BLOCK_FINDER
				)
				.createRunner();
		}

		public T failed(CommandContext<ServerCommandSource> context) {
			return this.failed(context, false);
		}
	}
}
