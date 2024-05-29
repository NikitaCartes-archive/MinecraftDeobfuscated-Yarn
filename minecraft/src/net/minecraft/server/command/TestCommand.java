package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import java.util.stream.Stream;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.command.argument.TestClassArgumentType;
import net.minecraft.command.argument.TestFunctionArgumentType;
import net.minecraft.data.DataWriter;
import net.minecraft.data.dev.NbtProvider;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.test.BatchListener;
import net.minecraft.test.Batches;
import net.minecraft.test.GameTestBatch;
import net.minecraft.test.GameTestState;
import net.minecraft.test.StructureBlockFinder;
import net.minecraft.test.StructureTestUtil;
import net.minecraft.test.TestAttemptConfig;
import net.minecraft.test.TestFunction;
import net.minecraft.test.TestFunctionFinder;
import net.minecraft.test.TestFunctions;
import net.minecraft.test.TestListener;
import net.minecraft.test.TestManager;
import net.minecraft.test.TestRunContext;
import net.minecraft.test.TestSet;
import net.minecraft.test.TestStructurePlacer;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.PathUtil;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.Heightmap;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.commons.lang3.mutable.MutableInt;
import org.slf4j.Logger;

public class TestCommand {
	public static final int field_33180 = 15;
	public static final int field_33181 = 200;
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final int field_33178 = 200;
	private static final int field_33179 = 1024;
	private static final int field_33182 = 3;
	private static final int field_33183 = 10000;
	private static final int field_33184 = 5;
	private static final int field_33185 = 5;
	private static final int field_33186 = 5;
	private static final String BLOCK_ENTITY_NOT_FOUND_TEXT = "Structure block entity could not be found";
	private static final TestFinder.Runners<TestCommand.Runner> RUNNERS = new TestFinder.Runners<>(TestCommand.Runner::new);

	private static ArgumentBuilder<ServerCommandSource, ?> testAttemptConfig(
		ArgumentBuilder<ServerCommandSource, ?> builder,
		Function<CommandContext<ServerCommandSource>, TestCommand.Runner> callback,
		Function<ArgumentBuilder<ServerCommandSource, ?>, ArgumentBuilder<ServerCommandSource, ?>> extraConfigAdder
	) {
		return builder.executes(context -> ((TestCommand.Runner)callback.apply(context)).runOnce())
			.then(
				CommandManager.argument("numberOfTimes", IntegerArgumentType.integer(0))
					.executes(
						context -> ((TestCommand.Runner)callback.apply(context)).run(new TestAttemptConfig(IntegerArgumentType.getInteger(context, "numberOfTimes"), false))
					)
					.then(
						(ArgumentBuilder<ServerCommandSource, ?>)extraConfigAdder.apply(
							CommandManager.argument("untilFailed", BoolArgumentType.bool())
								.executes(
									context -> ((TestCommand.Runner)callback.apply(context))
											.run(new TestAttemptConfig(IntegerArgumentType.getInteger(context, "numberOfTimes"), BoolArgumentType.getBool(context, "untilFailed")))
								)
						)
					)
			);
	}

	private static ArgumentBuilder<ServerCommandSource, ?> testAttemptConfig(
		ArgumentBuilder<ServerCommandSource, ?> builder, Function<CommandContext<ServerCommandSource>, TestCommand.Runner> callback
	) {
		return testAttemptConfig(builder, callback, extraConfigAdder -> extraConfigAdder);
	}

	private static ArgumentBuilder<ServerCommandSource, ?> testAttemptAndPlacementConfig(
		ArgumentBuilder<ServerCommandSource, ?> builder, Function<CommandContext<ServerCommandSource>, TestCommand.Runner> callback
	) {
		return testAttemptConfig(
			builder,
			callback,
			extraConfigAdder -> extraConfigAdder.then(
					CommandManager.argument("rotationSteps", IntegerArgumentType.integer())
						.executes(
							context -> ((TestCommand.Runner)callback.apply(context))
									.run(
										new TestAttemptConfig(IntegerArgumentType.getInteger(context, "numberOfTimes"), BoolArgumentType.getBool(context, "untilFailed")),
										IntegerArgumentType.getInteger(context, "rotationSteps")
									)
						)
						.then(
							CommandManager.argument("testsPerRow", IntegerArgumentType.integer())
								.executes(
									context -> ((TestCommand.Runner)callback.apply(context))
											.start(
												new TestAttemptConfig(IntegerArgumentType.getInteger(context, "numberOfTimes"), BoolArgumentType.getBool(context, "untilFailed")),
												IntegerArgumentType.getInteger(context, "rotationSteps"),
												IntegerArgumentType.getInteger(context, "testsPerRow")
											)
								)
						)
				)
		);
	}

	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		ArgumentBuilder<ServerCommandSource, ?> argumentBuilder = testAttemptAndPlacementConfig(
			CommandManager.argument("onlyRequiredTests", BoolArgumentType.bool()),
			context -> RUNNERS.failed(context, BoolArgumentType.getBool(context, "onlyRequiredTests"))
		);
		ArgumentBuilder<ServerCommandSource, ?> argumentBuilder2 = testAttemptAndPlacementConfig(
			CommandManager.argument("testClassName", TestClassArgumentType.testClass()),
			context -> RUNNERS.in(context, TestClassArgumentType.getTestClass(context, "testClassName"))
		);
		dispatcher.register(
			CommandManager.literal("test")
				.then(
					CommandManager.literal("run")
						.then(
							testAttemptAndPlacementConfig(
								CommandManager.argument("testName", TestFunctionArgumentType.testFunction()), context -> RUNNERS.functionNamed(context, "testName")
							)
						)
				)
				.then(
					CommandManager.literal("runmultiple")
						.then(
							CommandManager.argument("testName", TestFunctionArgumentType.testFunction())
								.executes(context -> RUNNERS.functionNamed(context, "testName").runOnce())
								.then(
									CommandManager.argument("amount", IntegerArgumentType.integer())
										.executes(context -> RUNNERS.repeat(IntegerArgumentType.getInteger(context, "amount")).functionNamed(context, "testName").runOnce())
								)
						)
				)
				.then(testAttemptAndPlacementConfig(CommandManager.literal("runall").then(argumentBuilder2), RUNNERS::allTestFunctions))
				.then(testAttemptConfig(CommandManager.literal("runthese"), RUNNERS::allStructures))
				.then(testAttemptConfig(CommandManager.literal("runclosest"), RUNNERS::nearest))
				.then(testAttemptConfig(CommandManager.literal("runthat"), RUNNERS::targeted))
				.then(testAttemptAndPlacementConfig(CommandManager.literal("runfailed").then(argumentBuilder), RUNNERS::failed))
				.then(
					CommandManager.literal("verify")
						.then(
							CommandManager.argument("testName", TestFunctionArgumentType.testFunction()).executes(context -> RUNNERS.functionNamed(context, "testName").verify())
						)
				)
				.then(
					CommandManager.literal("verifyclass")
						.then(
							CommandManager.argument("testClassName", TestClassArgumentType.testClass())
								.executes(context -> RUNNERS.in(context, TestClassArgumentType.getTestClass(context, "testClassName")).verify())
						)
				)
				.then(
					CommandManager.literal("locate")
						.then(
							CommandManager.argument("testName", TestFunctionArgumentType.testFunction())
								.executes(context -> RUNNERS.structureNamed(context, "minecraft:" + TestFunctionArgumentType.getFunction(context, "testName").templateName()).locate())
						)
				)
				.then(CommandManager.literal("resetclosest").executes(context -> RUNNERS.nearest(context).reset()))
				.then(CommandManager.literal("resetthese").executes(context -> RUNNERS.allStructures(context).reset()))
				.then(CommandManager.literal("resetthat").executes(context -> RUNNERS.targeted(context).reset()))
				.then(
					CommandManager.literal("export")
						.then(
							CommandManager.argument("testName", StringArgumentType.word())
								.executes(context -> executeExport(context.getSource(), "minecraft:" + StringArgumentType.getString(context, "testName")))
						)
				)
				.then(CommandManager.literal("exportclosest").executes(context -> RUNNERS.nearest(context).export()))
				.then(CommandManager.literal("exportthese").executes(context -> RUNNERS.allStructures(context).export()))
				.then(CommandManager.literal("exportthat").executes(context -> RUNNERS.targeted(context).export()))
				.then(CommandManager.literal("clearthat").executes(context -> RUNNERS.targeted(context).clear()))
				.then(CommandManager.literal("clearthese").executes(context -> RUNNERS.allStructures(context).clear()))
				.then(
					CommandManager.literal("clearall")
						.executes(context -> RUNNERS.surface(context, 200).clear())
						.then(
							CommandManager.argument("radius", IntegerArgumentType.integer())
								.executes(context -> RUNNERS.surface(context, MathHelper.clamp(IntegerArgumentType.getInteger(context, "radius"), 0, 1024)).clear())
						)
				)
				.then(
					CommandManager.literal("import")
						.then(
							CommandManager.argument("testName", StringArgumentType.word())
								.executes(context -> executeImport(context.getSource(), StringArgumentType.getString(context, "testName")))
						)
				)
				.then(CommandManager.literal("stop").executes(context -> stop()))
				.then(
					CommandManager.literal("pos")
						.executes(context -> executePos(context.getSource(), "pos"))
						.then(
							CommandManager.argument("var", StringArgumentType.word())
								.executes(context -> executePos(context.getSource(), StringArgumentType.getString(context, "var")))
						)
				)
				.then(
					CommandManager.literal("create")
						.then(
							CommandManager.argument("testName", StringArgumentType.word())
								.suggests(TestFunctionArgumentType::suggestTestNames)
								.executes(context -> executeCreate(context.getSource(), StringArgumentType.getString(context, "testName"), 5, 5, 5))
								.then(
									CommandManager.argument("width", IntegerArgumentType.integer())
										.executes(
											context -> executeCreate(
													context.getSource(),
													StringArgumentType.getString(context, "testName"),
													IntegerArgumentType.getInteger(context, "width"),
													IntegerArgumentType.getInteger(context, "width"),
													IntegerArgumentType.getInteger(context, "width")
												)
										)
										.then(
											CommandManager.argument("height", IntegerArgumentType.integer())
												.then(
													CommandManager.argument("depth", IntegerArgumentType.integer())
														.executes(
															context -> executeCreate(
																	context.getSource(),
																	StringArgumentType.getString(context, "testName"),
																	IntegerArgumentType.getInteger(context, "width"),
																	IntegerArgumentType.getInteger(context, "height"),
																	IntegerArgumentType.getInteger(context, "depth")
																)
														)
												)
										)
								)
						)
				)
		);
	}

	private static int reset(GameTestState state) {
		state.getWorld().getOtherEntities(null, state.getBoundingBox()).stream().forEach(entity -> entity.remove(Entity.RemovalReason.DISCARDED));
		state.getStructureBlockBlockEntity().loadAndPlaceStructure(state.getWorld());
		StructureTestUtil.clearBarrierBox(state.getBoundingBox(), state.getWorld());
		sendMessage(state.getWorld(), "Reset succeded for: " + state.getTemplatePath(), Formatting.GREEN);
		return 1;
	}

	static Stream<GameTestState> stream(ServerCommandSource source, TestAttemptConfig config, StructureBlockFinder finder) {
		return finder.findStructureBlockPos().map(pos -> find(pos, source.getWorld(), config)).flatMap(Optional::stream);
	}

	static Stream<GameTestState> stream(ServerCommandSource source, TestAttemptConfig config, TestFunctionFinder finder, int rotationSteps) {
		return finder.findTestFunctions()
			.filter(testFunction -> checkStructure(source.getWorld(), testFunction.templateName()))
			.map(testFunction -> new GameTestState(testFunction, StructureTestUtil.getRotation(rotationSteps), source.getWorld(), config));
	}

	private static Optional<GameTestState> find(BlockPos pos, ServerWorld world, TestAttemptConfig config) {
		StructureBlockBlockEntity structureBlockBlockEntity = (StructureBlockBlockEntity)world.getBlockEntity(pos);
		if (structureBlockBlockEntity == null) {
			sendMessage(world, "Structure block entity could not be found", Formatting.RED);
			return Optional.empty();
		} else {
			String string = structureBlockBlockEntity.getMetadata();
			Optional<TestFunction> optional = TestFunctions.getTestFunction(string);
			if (optional.isEmpty()) {
				sendMessage(world, "Test function for test " + string + " could not be found", Formatting.RED);
				return Optional.empty();
			} else {
				TestFunction testFunction = (TestFunction)optional.get();
				GameTestState gameTestState = new GameTestState(testFunction, structureBlockBlockEntity.getRotation(), world, config);
				gameTestState.setPos(pos);
				return !checkStructure(world, gameTestState.getTemplateName()) ? Optional.empty() : Optional.of(gameTestState);
			}
		}
	}

	private static int executeCreate(ServerCommandSource source, String testName, int x, int y, int z) {
		if (x <= 48 && y <= 48 && z <= 48) {
			ServerWorld serverWorld = source.getWorld();
			BlockPos blockPos = getStructurePos(source).down();
			StructureTestUtil.createTestArea(testName.toLowerCase(), blockPos, new Vec3i(x, y, z), BlockRotation.NONE, serverWorld);
			BlockPos blockPos2 = blockPos.up();
			BlockPos blockPos3 = blockPos2.add(x - 1, 0, z - 1);
			BlockPos.stream(blockPos2, blockPos3).forEach(pos -> serverWorld.setBlockState(pos, Blocks.BEDROCK.getDefaultState()));
			StructureTestUtil.placeStartButton(blockPos, new BlockPos(1, 0, -1), BlockRotation.NONE, serverWorld);
			return 0;
		} else {
			throw new IllegalArgumentException("The structure must be less than 48 blocks big in each axis");
		}
	}

	private static int executePos(ServerCommandSource source, String variableName) throws CommandSyntaxException {
		BlockHitResult blockHitResult = (BlockHitResult)source.getPlayerOrThrow().raycast(10.0, 1.0F, false);
		BlockPos blockPos = blockHitResult.getBlockPos();
		ServerWorld serverWorld = source.getWorld();
		Optional<BlockPos> optional = StructureTestUtil.findContainingStructureBlock(blockPos, 15, serverWorld);
		if (optional.isEmpty()) {
			optional = StructureTestUtil.findContainingStructureBlock(blockPos, 200, serverWorld);
		}

		if (optional.isEmpty()) {
			source.sendError(Text.literal("Can't find a structure block that contains the targeted pos " + blockPos));
			return 0;
		} else {
			StructureBlockBlockEntity structureBlockBlockEntity = (StructureBlockBlockEntity)serverWorld.getBlockEntity((BlockPos)optional.get());
			if (structureBlockBlockEntity == null) {
				sendMessage(serverWorld, "Structure block entity could not be found", Formatting.RED);
				return 0;
			} else {
				BlockPos blockPos2 = blockPos.subtract((Vec3i)optional.get());
				String string = blockPos2.getX() + ", " + blockPos2.getY() + ", " + blockPos2.getZ();
				String string2 = structureBlockBlockEntity.getMetadata();
				Text text = Text.literal(string)
					.setStyle(
						Style.EMPTY
							.withBold(true)
							.withColor(Formatting.GREEN)
							.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("Click to copy to clipboard")))
							.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, "final BlockPos " + variableName + " = new BlockPos(" + string + ");"))
					);
				source.sendFeedback(() -> Text.literal("Position relative to " + string2 + ": ").append(text), false);
				DebugInfoSender.addGameTestMarker(serverWorld, new BlockPos(blockPos), string, -2147418368, 10000);
				return 1;
			}
		}
	}

	static int stop() {
		TestManager.INSTANCE.clear();
		return 1;
	}

	static int start(ServerCommandSource source, ServerWorld world, TestRunContext context) {
		context.addBatchListener(new TestCommand.ReportingBatchListener(source));
		TestSet testSet = new TestSet(context.getStates());
		testSet.addListener(new TestCommand.Listener(world, testSet));
		testSet.addListener(state -> TestFunctions.addFailedTestFunction(state.getTestFunction()));
		context.start();
		return 1;
	}

	static int export(ServerCommandSource source, StructureBlockBlockEntity blockEntity) {
		String string = blockEntity.getTemplateName();
		if (!blockEntity.saveStructure(true)) {
			sendMessage(source, "Failed to save structure " + string);
		}

		return executeExport(source, string);
	}

	private static int executeExport(ServerCommandSource source, String testName) {
		Path path = Paths.get(StructureTestUtil.testStructuresDirectoryName);
		Identifier identifier = Identifier.of(testName);
		Path path2 = source.getWorld().getStructureTemplateManager().getTemplatePath(identifier, ".nbt");
		Path path3 = NbtProvider.convertNbtToSnbt(DataWriter.UNCACHED, path2, identifier.getPath(), path);
		if (path3 == null) {
			sendMessage(source, "Failed to export " + path2);
			return 1;
		} else {
			try {
				PathUtil.createDirectories(path3.getParent());
			} catch (IOException var7) {
				sendMessage(source, "Could not create folder " + path3.getParent());
				LOGGER.error("Could not create export folder", (Throwable)var7);
				return 1;
			}

			sendMessage(source, "Exported " + testName + " to " + path3.toAbsolutePath());
			return 0;
		}
	}

	private static boolean checkStructure(ServerWorld world, String templateId) {
		if (world.getStructureTemplateManager().getTemplate(Identifier.of(templateId)).isEmpty()) {
			sendMessage(world, "Test structure " + templateId + " could not be found", Formatting.RED);
			return false;
		} else {
			return true;
		}
	}

	static BlockPos getStructurePos(ServerCommandSource source) {
		BlockPos blockPos = BlockPos.ofFloored(source.getPosition());
		int i = source.getWorld().getTopPosition(Heightmap.Type.WORLD_SURFACE, blockPos).getY();
		return new BlockPos(blockPos.getX(), i + 1, blockPos.getZ() + 3);
	}

	static void sendMessage(ServerCommandSource source, String message) {
		source.sendFeedback(() -> Text.literal(message), false);
	}

	private static int executeImport(ServerCommandSource source, String testName) {
		Path path = Paths.get(StructureTestUtil.testStructuresDirectoryName, testName + ".snbt");
		Identifier identifier = Identifier.ofVanilla(testName);
		Path path2 = source.getWorld().getStructureTemplateManager().getTemplatePath(identifier, ".nbt");

		try {
			BufferedReader bufferedReader = Files.newBufferedReader(path);
			String string = IOUtils.toString(bufferedReader);
			Files.createDirectories(path2.getParent());
			OutputStream outputStream = Files.newOutputStream(path2);

			try {
				NbtIo.writeCompressed(NbtHelper.fromNbtProviderString(string), outputStream);
			} catch (Throwable var11) {
				if (outputStream != null) {
					try {
						outputStream.close();
					} catch (Throwable var10) {
						var11.addSuppressed(var10);
					}
				}

				throw var11;
			}

			if (outputStream != null) {
				outputStream.close();
			}

			source.getWorld().getStructureTemplateManager().unloadTemplate(identifier);
			sendMessage(source, "Imported to " + path2.toAbsolutePath());
			return 0;
		} catch (CommandSyntaxException | IOException var12) {
			LOGGER.error("Failed to load structure {}", testName, var12);
			return 1;
		}
	}

	static void sendMessage(ServerWorld world, String message, Formatting formatting) {
		world.getPlayers(player -> true).forEach(player -> player.sendMessage(Text.literal(message).formatted(formatting)));
	}

	public static record Listener(ServerWorld world, TestSet tests) implements TestListener {
		@Override
		public void onStarted(GameTestState test) {
		}

		@Override
		public void onPassed(GameTestState test, TestRunContext context) {
			onFinished(this.world, this.tests);
		}

		@Override
		public void onFailed(GameTestState test, TestRunContext context) {
			onFinished(this.world, this.tests);
		}

		@Override
		public void onRetry(GameTestState prevState, GameTestState nextState, TestRunContext context) {
			this.tests.add(nextState);
		}

		private static void onFinished(ServerWorld world, TestSet tests) {
			if (tests.isDone()) {
				TestCommand.sendMessage(world, "GameTest done! " + tests.getTestCount() + " tests were run", Formatting.WHITE);
				if (tests.failed()) {
					TestCommand.sendMessage(world, tests.getFailedRequiredTestCount() + " required tests failed :(", Formatting.RED);
				} else {
					TestCommand.sendMessage(world, "All required tests passed :)", Formatting.GREEN);
				}

				if (tests.hasFailedOptionalTests()) {
					TestCommand.sendMessage(world, tests.getFailedOptionalTestCount() + " optional tests failed", Formatting.GRAY);
				}
			}
		}
	}

	static record ReportingBatchListener(ServerCommandSource source) implements BatchListener {
		@Override
		public void onStarted(GameTestBatch batch) {
			TestCommand.sendMessage(this.source, "Starting batch: " + batch.id());
		}

		@Override
		public void onFinished(GameTestBatch batch) {
		}
	}

	public static class Runner {
		private final TestFinder<TestCommand.Runner> finder;

		public Runner(TestFinder<TestCommand.Runner> finder) {
			this.finder = finder;
		}

		public int reset() {
			TestCommand.stop();
			return TestCommand.stream(this.finder.getCommandSource(), TestAttemptConfig.once(), this.finder).map(TestCommand::reset).toList().isEmpty() ? 0 : 1;
		}

		private <T> void forEach(Stream<T> finder, ToIntFunction<T> consumer, Runnable emptyCallback, Consumer<Integer> finishCallback) {
			int i = finder.mapToInt(consumer).sum();
			if (i == 0) {
				emptyCallback.run();
			} else {
				finishCallback.accept(i);
			}
		}

		public int clear() {
			TestCommand.stop();
			ServerCommandSource serverCommandSource = this.finder.getCommandSource();
			ServerWorld serverWorld = serverCommandSource.getWorld();
			TestRunContext.clearDebugMarkers(serverWorld);
			this.forEach(
				this.finder.findStructureBlockPos(),
				pos -> {
					StructureBlockBlockEntity structureBlockBlockEntity = (StructureBlockBlockEntity)serverWorld.getBlockEntity(pos);
					if (structureBlockBlockEntity == null) {
						return 0;
					} else {
						BlockBox blockBox = StructureTestUtil.getStructureBlockBox(structureBlockBlockEntity);
						StructureTestUtil.clearArea(blockBox, serverWorld);
						return 1;
					}
				},
				() -> TestCommand.sendMessage(serverWorld, "Could not find any structures to clear", Formatting.RED),
				count -> TestCommand.sendMessage(serverCommandSource, "Cleared " + count + " structures")
			);
			return 1;
		}

		public int export() {
			MutableBoolean mutableBoolean = new MutableBoolean(true);
			ServerCommandSource serverCommandSource = this.finder.getCommandSource();
			ServerWorld serverWorld = serverCommandSource.getWorld();
			this.forEach(
				this.finder.findStructureBlockPos(),
				pos -> {
					StructureBlockBlockEntity structureBlockBlockEntity = (StructureBlockBlockEntity)serverWorld.getBlockEntity(pos);
					if (structureBlockBlockEntity == null) {
						TestCommand.sendMessage(serverWorld, "Structure block entity could not be found", Formatting.RED);
						mutableBoolean.setFalse();
						return 0;
					} else {
						if (TestCommand.export(serverCommandSource, structureBlockBlockEntity) != 0) {
							mutableBoolean.setFalse();
						}

						return 1;
					}
				},
				() -> TestCommand.sendMessage(serverWorld, "Could not find any structures to export", Formatting.RED),
				count -> TestCommand.sendMessage(serverCommandSource, "Exported " + count + " structures")
			);
			return mutableBoolean.getValue() ? 0 : 1;
		}

		int verify() {
			TestCommand.stop();
			ServerCommandSource serverCommandSource = this.finder.getCommandSource();
			ServerWorld serverWorld = serverCommandSource.getWorld();
			BlockPos blockPos = TestCommand.getStructurePos(serverCommandSource);
			Collection<GameTestState> collection = Stream.concat(
					TestCommand.stream(serverCommandSource, TestAttemptConfig.once(), this.finder),
					TestCommand.stream(serverCommandSource, TestAttemptConfig.once(), this.finder, 0)
				)
				.toList();
			int i = 10;
			TestRunContext.clearDebugMarkers(serverWorld);
			TestFunctions.clearFailedTestFunctions();
			Collection<GameTestBatch> collection2 = new ArrayList();

			for (GameTestState gameTestState : collection) {
				for (BlockRotation blockRotation : BlockRotation.values()) {
					Collection<GameTestState> collection3 = new ArrayList();

					for (int j = 0; j < 100; j++) {
						GameTestState gameTestState2 = new GameTestState(gameTestState.getTestFunction(), blockRotation, serverWorld, new TestAttemptConfig(1, true));
						collection3.add(gameTestState2);
					}

					GameTestBatch gameTestBatch = Batches.create(collection3, gameTestState.getTestFunction().batchId(), (long)blockRotation.ordinal());
					collection2.add(gameTestBatch);
				}
			}

			TestStructurePlacer testStructurePlacer = new TestStructurePlacer(blockPos, 10, true);
			TestRunContext testRunContext = TestRunContext.Builder.of(collection2, serverWorld)
				.batcher(Batches.batcher(100))
				.initialSpawner(testStructurePlacer)
				.reuseSpawner(testStructurePlacer)
				.stopAfterFailure(true)
				.build();
			return TestCommand.start(serverCommandSource, serverWorld, testRunContext);
		}

		public int start(TestAttemptConfig config, int rotationSteps, int testsPerRow) {
			TestCommand.stop();
			ServerCommandSource serverCommandSource = this.finder.getCommandSource();
			ServerWorld serverWorld = serverCommandSource.getWorld();
			BlockPos blockPos = TestCommand.getStructurePos(serverCommandSource);
			Collection<GameTestState> collection = Stream.concat(
					TestCommand.stream(serverCommandSource, config, this.finder), TestCommand.stream(serverCommandSource, config, this.finder, rotationSteps)
				)
				.toList();
			if (collection.isEmpty()) {
				TestCommand.sendMessage(serverCommandSource, "No tests found");
				return 0;
			} else {
				TestRunContext.clearDebugMarkers(serverWorld);
				TestFunctions.clearFailedTestFunctions();
				TestCommand.sendMessage(serverCommandSource, "Running " + collection.size() + " tests...");
				TestRunContext testRunContext = TestRunContext.Builder.ofStates(collection, serverWorld)
					.initialSpawner(new TestStructurePlacer(blockPos, testsPerRow, false))
					.build();
				return TestCommand.start(serverCommandSource, serverWorld, testRunContext);
			}
		}

		public int runOnce(int rotationSteps, int testsPerRow) {
			return this.start(TestAttemptConfig.once(), rotationSteps, testsPerRow);
		}

		public int runOnce(int rotationSteps) {
			return this.start(TestAttemptConfig.once(), rotationSteps, 8);
		}

		public int run(TestAttemptConfig config, int rotationSteps) {
			return this.start(config, rotationSteps, 8);
		}

		public int run(TestAttemptConfig config) {
			return this.start(config, 0, 8);
		}

		public int runOnce() {
			return this.run(TestAttemptConfig.once());
		}

		public int locate() {
			TestCommand.sendMessage(this.finder.getCommandSource(), "Started locating test structures, this might take a while..");
			MutableInt mutableInt = new MutableInt(0);
			BlockPos blockPos = BlockPos.ofFloored(this.finder.getCommandSource().getPosition());
			this.finder
				.findStructureBlockPos()
				.forEach(
					pos -> {
						StructureBlockBlockEntity structureBlockBlockEntity = (StructureBlockBlockEntity)this.finder.getCommandSource().getWorld().getBlockEntity(pos);
						if (structureBlockBlockEntity != null) {
							Direction direction = structureBlockBlockEntity.getRotation().rotate(Direction.NORTH);
							BlockPos blockPos2 = structureBlockBlockEntity.getPos().offset(direction, 2);
							int ix = (int)direction.getOpposite().asRotation();
							String string = String.format("/tp @s %d %d %d %d 0", blockPos2.getX(), blockPos2.getY(), blockPos2.getZ(), ix);
							int j = blockPos.getX() - pos.getX();
							int k = blockPos.getZ() - pos.getZ();
							int l = MathHelper.floor(MathHelper.sqrt((float)(j * j + k * k)));
							Text text = Texts.bracketed(Text.translatable("chat.coordinates", pos.getX(), pos.getY(), pos.getZ()))
								.styled(
									style -> style.withColor(Formatting.GREEN)
											.withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, string))
											.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.translatable("chat.coordinates.tooltip")))
								);
							Text text2 = Text.literal("Found structure at: ").append(text).append(" (distance: " + l + ")");
							this.finder.getCommandSource().sendFeedback(() -> text2, false);
							mutableInt.increment();
						}
					}
				);
			int i = mutableInt.intValue();
			if (i == 0) {
				TestCommand.sendMessage(this.finder.getCommandSource().getWorld(), "No such test structure found", Formatting.RED);
				return 0;
			} else {
				TestCommand.sendMessage(this.finder.getCommandSource().getWorld(), "Finished locating, found " + i + " structure(s)", Formatting.GREEN);
				return 1;
			}
		}
	}
}
