package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.command.argument.BlockStateArgument;
import net.minecraft.command.argument.TestClassArgumentType;
import net.minecraft.command.argument.TestFunctionArgumentType;
import net.minecraft.data.dev.NbtProvider;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.test.GameTestState;
import net.minecraft.test.StructureTestUtil;
import net.minecraft.test.TestFunction;
import net.minecraft.test.TestFunctions;
import net.minecraft.test.TestListener;
import net.minecraft.test.TestManager;
import net.minecraft.test.TestSet;
import net.minecraft.test.TestUtil;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.Heightmap;
import org.apache.commons.io.IOUtils;

public class TestCommand {
	private static final int field_33178 = 200;
	private static final int field_33179 = 1024;
	private static final int field_33180 = 15;
	private static final int field_33181 = 200;
	private static final int field_33182 = 3;
	private static final int field_33183 = 10000;
	private static final int field_33184 = 5;
	private static final int field_33185 = 5;
	private static final int field_33186 = 5;

	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(
			CommandManager.literal("test")
				.then(CommandManager.literal("runthis").executes(context -> executeRunThis(context.getSource())))
				.then(CommandManager.literal("runthese").executes(context -> executeRunThese(context.getSource())))
				.then(
					CommandManager.literal("runfailed")
						.executes(context -> executeRerunFailed(context.getSource(), false, 0, 8))
						.then(
							CommandManager.argument("onlyRequiredTests", BoolArgumentType.bool())
								.executes(context -> executeRerunFailed(context.getSource(), BoolArgumentType.getBool(context, "onlyRequiredTests"), 0, 8))
								.then(
									CommandManager.argument("rotationSteps", IntegerArgumentType.integer())
										.executes(
											context -> executeRerunFailed(
													context.getSource(), BoolArgumentType.getBool(context, "onlyRequiredTests"), IntegerArgumentType.getInteger(context, "rotationSteps"), 8
												)
										)
										.then(
											CommandManager.argument("testsPerRow", IntegerArgumentType.integer())
												.executes(
													context -> executeRerunFailed(
															context.getSource(),
															BoolArgumentType.getBool(context, "onlyRequiredTests"),
															IntegerArgumentType.getInteger(context, "rotationSteps"),
															IntegerArgumentType.getInteger(context, "testsPerRow")
														)
												)
										)
								)
						)
				)
				.then(
					CommandManager.literal("run")
						.then(
							CommandManager.argument("testName", TestFunctionArgumentType.testFunction())
								.executes(context -> executeRun(context.getSource(), TestFunctionArgumentType.getFunction(context, "testName"), 0))
								.then(
									CommandManager.argument("rotationSteps", IntegerArgumentType.integer())
										.executes(
											context -> executeRun(
													context.getSource(), TestFunctionArgumentType.getFunction(context, "testName"), IntegerArgumentType.getInteger(context, "rotationSteps")
												)
										)
								)
						)
				)
				.then(
					CommandManager.literal("runall")
						.executes(context -> executeRunAll(context.getSource(), 0, 8))
						.then(
							CommandManager.argument("testClassName", TestClassArgumentType.testClass())
								.executes(context -> executeRunAll(context.getSource(), TestClassArgumentType.getTestClass(context, "testClassName"), 0, 8))
								.then(
									CommandManager.argument("rotationSteps", IntegerArgumentType.integer())
										.executes(
											context -> executeRunAll(
													context.getSource(), TestClassArgumentType.getTestClass(context, "testClassName"), IntegerArgumentType.getInteger(context, "rotationSteps"), 8
												)
										)
										.then(
											CommandManager.argument("testsPerRow", IntegerArgumentType.integer())
												.executes(
													context -> executeRunAll(
															context.getSource(),
															TestClassArgumentType.getTestClass(context, "testClassName"),
															IntegerArgumentType.getInteger(context, "rotationSteps"),
															IntegerArgumentType.getInteger(context, "testsPerRow")
														)
												)
										)
								)
						)
						.then(
							CommandManager.argument("rotationSteps", IntegerArgumentType.integer())
								.executes(context -> executeRunAll(context.getSource(), IntegerArgumentType.getInteger(context, "rotationSteps"), 8))
								.then(
									CommandManager.argument("testsPerRow", IntegerArgumentType.integer())
										.executes(
											context -> executeRunAll(
													context.getSource(), IntegerArgumentType.getInteger(context, "rotationSteps"), IntegerArgumentType.getInteger(context, "testsPerRow")
												)
										)
								)
						)
				)
				.then(
					CommandManager.literal("export")
						.then(
							CommandManager.argument("testName", StringArgumentType.word())
								.executes(context -> executeExport(context.getSource(), StringArgumentType.getString(context, "testName")))
						)
				)
				.then(CommandManager.literal("exportthis").executes(context -> executeExport(context.getSource())))
				.then(
					CommandManager.literal("import")
						.then(
							CommandManager.argument("testName", StringArgumentType.word())
								.executes(context -> executeImport(context.getSource(), StringArgumentType.getString(context, "testName")))
						)
				)
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
				.then(
					CommandManager.literal("clearall")
						.executes(context -> executeClearAll(context.getSource(), 200))
						.then(
							CommandManager.argument("radius", IntegerArgumentType.integer())
								.executes(context -> executeClearAll(context.getSource(), IntegerArgumentType.getInteger(context, "radius")))
						)
				)
		);
	}

	private static int executeCreate(ServerCommandSource source, String structure, int x, int y, int z) {
		if (x <= 48 && y <= 48 && z <= 48) {
			ServerWorld serverWorld = source.getWorld();
			BlockPos blockPos = new BlockPos(source.getPosition());
			BlockPos blockPos2 = new BlockPos(blockPos.getX(), source.getWorld().getTopPosition(Heightmap.Type.WORLD_SURFACE, blockPos).getY(), blockPos.getZ() + 3);
			StructureTestUtil.createTestArea(structure.toLowerCase(), blockPos2, new Vec3i(x, y, z), BlockRotation.NONE, serverWorld);

			for (int i = 0; i < x; i++) {
				for (int j = 0; j < z; j++) {
					BlockPos blockPos3 = new BlockPos(blockPos2.getX() + i, blockPos2.getY() + 1, blockPos2.getZ() + j);
					Block block = Blocks.POLISHED_ANDESITE;
					BlockStateArgument blockStateArgument = new BlockStateArgument(block.getDefaultState(), Collections.emptySet(), null);
					blockStateArgument.setBlockState(serverWorld, blockPos3, Block.NOTIFY_LISTENERS);
				}
			}

			StructureTestUtil.placeStartButton(blockPos2, new BlockPos(1, 0, -1), BlockRotation.NONE, serverWorld);
			return 0;
		} else {
			throw new IllegalArgumentException("The structure must be less than 48 blocks big in each axis");
		}
	}

	private static int executePos(ServerCommandSource source, String variableName) throws CommandSyntaxException {
		BlockHitResult blockHitResult = (BlockHitResult)source.getPlayer().raycast(10.0, 1.0F, false);
		BlockPos blockPos = blockHitResult.getBlockPos();
		ServerWorld serverWorld = source.getWorld();
		Optional<BlockPos> optional = StructureTestUtil.findContainingStructureBlock(blockPos, 15, serverWorld);
		if (!optional.isPresent()) {
			optional = StructureTestUtil.findContainingStructureBlock(blockPos, 200, serverWorld);
		}

		if (!optional.isPresent()) {
			source.sendError(Text.literal("Can't find a structure block that contains the targeted pos " + blockPos));
			return 0;
		} else {
			StructureBlockBlockEntity structureBlockBlockEntity = (StructureBlockBlockEntity)serverWorld.getBlockEntity((BlockPos)optional.get());
			BlockPos blockPos2 = blockPos.subtract((Vec3i)optional.get());
			String string = blockPos2.getX() + ", " + blockPos2.getY() + ", " + blockPos2.getZ();
			String string2 = structureBlockBlockEntity.getStructurePath();
			Text text = Text.literal(string)
				.setStyle(
					Style.EMPTY
						.withBold(true)
						.withColor(Formatting.GREEN)
						.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("Click to copy to clipboard")))
						.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, "final BlockPos " + variableName + " = new BlockPos(" + string + ");"))
				);
			source.sendFeedback(Text.literal("Position relative to " + string2 + ": ").append(text), false);
			DebugInfoSender.addGameTestMarker(serverWorld, new BlockPos(blockPos), string, -2147418368, 10000);
			return 1;
		}
	}

	private static int executeRunThis(ServerCommandSource source) {
		BlockPos blockPos = new BlockPos(source.getPosition());
		ServerWorld serverWorld = source.getWorld();
		BlockPos blockPos2 = StructureTestUtil.findNearestStructureBlock(blockPos, 15, serverWorld);
		if (blockPos2 == null) {
			sendMessage(serverWorld, "Couldn't find any structure block within 15 radius", Formatting.RED);
			return 0;
		} else {
			TestUtil.clearDebugMarkers(serverWorld);
			run(serverWorld, blockPos2, null);
			return 1;
		}
	}

	private static int executeRunThese(ServerCommandSource source) {
		BlockPos blockPos = new BlockPos(source.getPosition());
		ServerWorld serverWorld = source.getWorld();
		Collection<BlockPos> collection = StructureTestUtil.findStructureBlocks(blockPos, 200, serverWorld);
		if (collection.isEmpty()) {
			sendMessage(serverWorld, "Couldn't find any structure blocks within 200 block radius", Formatting.RED);
			return 1;
		} else {
			TestUtil.clearDebugMarkers(serverWorld);
			sendMessage(source, "Running " + collection.size() + " tests...");
			TestSet testSet = new TestSet();
			collection.forEach(pos -> run(serverWorld, pos, testSet));
			return 1;
		}
	}

	private static void run(ServerWorld world, BlockPos pos, @Nullable TestSet tests) {
		StructureBlockBlockEntity structureBlockBlockEntity = (StructureBlockBlockEntity)world.getBlockEntity(pos);
		String string = structureBlockBlockEntity.getStructurePath();
		TestFunction testFunction = TestFunctions.getTestFunctionOrThrow(string);
		GameTestState gameTestState = new GameTestState(testFunction, structureBlockBlockEntity.getRotation(), world);
		if (tests != null) {
			tests.add(gameTestState);
			gameTestState.addListener(new TestCommand.Listener(world, tests));
		}

		beforeBatch(testFunction, world);
		Box box = StructureTestUtil.getStructureBoundingBox(structureBlockBlockEntity);
		BlockPos blockPos = new BlockPos(box.minX, box.minY, box.minZ);
		TestUtil.startTest(gameTestState, blockPos, TestManager.INSTANCE);
	}

	static void onCompletion(ServerWorld world, TestSet tests) {
		if (tests.isDone()) {
			sendMessage(world, "GameTest done! " + tests.getTestCount() + " tests were run", Formatting.WHITE);
			if (tests.failed()) {
				sendMessage(world, tests.getFailedRequiredTestCount() + " required tests failed :(", Formatting.RED);
			} else {
				sendMessage(world, "All required tests passed :)", Formatting.GREEN);
			}

			if (tests.hasFailedOptionalTests()) {
				sendMessage(world, tests.getFailedOptionalTestCount() + " optional tests failed", Formatting.GRAY);
			}
		}
	}

	private static int executeClearAll(ServerCommandSource source, int radius) {
		ServerWorld serverWorld = source.getWorld();
		TestUtil.clearDebugMarkers(serverWorld);
		BlockPos blockPos = new BlockPos(
			source.getPosition().x,
			(double)source.getWorld().getTopPosition(Heightmap.Type.WORLD_SURFACE, new BlockPos(source.getPosition())).getY(),
			source.getPosition().z
		);
		TestUtil.clearTests(serverWorld, blockPos, TestManager.INSTANCE, MathHelper.clamp(radius, 0, 1024));
		return 1;
	}

	private static int executeRun(ServerCommandSource source, TestFunction testFunction, int rotationSteps) {
		ServerWorld serverWorld = source.getWorld();
		BlockPos blockPos = new BlockPos(source.getPosition());
		int i = source.getWorld().getTopPosition(Heightmap.Type.WORLD_SURFACE, blockPos).getY();
		BlockPos blockPos2 = new BlockPos(blockPos.getX(), i, blockPos.getZ() + 3);
		TestUtil.clearDebugMarkers(serverWorld);
		beforeBatch(testFunction, serverWorld);
		BlockRotation blockRotation = StructureTestUtil.getRotation(rotationSteps);
		GameTestState gameTestState = new GameTestState(testFunction, blockRotation, serverWorld);
		TestUtil.startTest(gameTestState, blockPos2, TestManager.INSTANCE);
		return 1;
	}

	private static void beforeBatch(TestFunction testFunction, ServerWorld world) {
		Consumer<ServerWorld> consumer = TestFunctions.getBeforeBatchConsumer(testFunction.getBatchId());
		if (consumer != null) {
			consumer.accept(world);
		}
	}

	private static int executeRunAll(ServerCommandSource source, int rotationSteps, int sizeZ) {
		TestUtil.clearDebugMarkers(source.getWorld());
		Collection<TestFunction> collection = TestFunctions.getTestFunctions();
		sendMessage(source, "Running all " + collection.size() + " tests...");
		TestFunctions.clearFailedTestFunctions();
		run(source, collection, rotationSteps, sizeZ);
		return 1;
	}

	private static int executeRunAll(ServerCommandSource source, String testClass, int rotationSteps, int sizeZ) {
		Collection<TestFunction> collection = TestFunctions.getTestFunctions(testClass);
		TestUtil.clearDebugMarkers(source.getWorld());
		sendMessage(source, "Running " + collection.size() + " tests from " + testClass + "...");
		TestFunctions.clearFailedTestFunctions();
		run(source, collection, rotationSteps, sizeZ);
		return 1;
	}

	private static int executeRerunFailed(ServerCommandSource source, boolean requiredOnly, int rotationSteps, int sizeZ) {
		Collection<TestFunction> collection;
		if (requiredOnly) {
			collection = (Collection<TestFunction>)TestFunctions.getFailedTestFunctions().stream().filter(TestFunction::isRequired).collect(Collectors.toList());
		} else {
			collection = TestFunctions.getFailedTestFunctions();
		}

		if (collection.isEmpty()) {
			sendMessage(source, "No failed tests to rerun");
			return 0;
		} else {
			TestUtil.clearDebugMarkers(source.getWorld());
			sendMessage(source, "Rerunning " + collection.size() + " failed tests (" + (requiredOnly ? "only required tests" : "including optional tests") + ")");
			run(source, collection, rotationSteps, sizeZ);
			return 1;
		}
	}

	private static void run(ServerCommandSource source, Collection<TestFunction> testFunctions, int rotationSteps, int i) {
		BlockPos blockPos = new BlockPos(source.getPosition());
		BlockPos blockPos2 = new BlockPos(blockPos.getX(), source.getWorld().getTopPosition(Heightmap.Type.WORLD_SURFACE, blockPos).getY(), blockPos.getZ() + 3);
		ServerWorld serverWorld = source.getWorld();
		BlockRotation blockRotation = StructureTestUtil.getRotation(rotationSteps);
		Collection<GameTestState> collection = TestUtil.runTestFunctions(testFunctions, blockPos2, blockRotation, serverWorld, TestManager.INSTANCE, i);
		TestSet testSet = new TestSet(collection);
		testSet.addListener(new TestCommand.Listener(serverWorld, testSet));
		testSet.addListener(test -> TestFunctions.addFailedTestFunction(test.getTestFunction()));
	}

	private static void sendMessage(ServerCommandSource source, String message) {
		source.sendFeedback(Text.literal(message), false);
	}

	private static int executeExport(ServerCommandSource source) {
		BlockPos blockPos = new BlockPos(source.getPosition());
		ServerWorld serverWorld = source.getWorld();
		BlockPos blockPos2 = StructureTestUtil.findNearestStructureBlock(blockPos, 15, serverWorld);
		if (blockPos2 == null) {
			sendMessage(serverWorld, "Couldn't find any structure block within 15 radius", Formatting.RED);
			return 0;
		} else {
			StructureBlockBlockEntity structureBlockBlockEntity = (StructureBlockBlockEntity)serverWorld.getBlockEntity(blockPos2);
			String string = structureBlockBlockEntity.getStructurePath();
			return executeExport(source, string);
		}
	}

	private static int executeExport(ServerCommandSource source, String structure) {
		Path path = Paths.get(StructureTestUtil.testStructuresDirectoryName);
		Identifier identifier = new Identifier("minecraft", structure);
		Path path2 = source.getWorld().getStructureManager().getStructurePath(identifier, ".nbt");
		Path path3 = NbtProvider.convertNbtToSnbt(path2, structure, path);
		if (path3 == null) {
			sendMessage(source, "Failed to export " + path2);
			return 1;
		} else {
			try {
				Files.createDirectories(path3.getParent());
			} catch (IOException var7) {
				sendMessage(source, "Could not create folder " + path3.getParent());
				var7.printStackTrace();
				return 1;
			}

			sendMessage(source, "Exported " + structure + " to " + path3.toAbsolutePath());
			return 0;
		}
	}

	private static int executeImport(ServerCommandSource source, String structure) {
		Path path = Paths.get(StructureTestUtil.testStructuresDirectoryName, structure + ".snbt");
		Identifier identifier = new Identifier("minecraft", structure);
		Path path2 = source.getWorld().getStructureManager().getStructurePath(identifier, ".nbt");

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

			sendMessage(source, "Imported to " + path2.toAbsolutePath());
			return 0;
		} catch (CommandSyntaxException | IOException var12) {
			System.err.println("Failed to load structure " + structure);
			var12.printStackTrace();
			return 1;
		}
	}

	private static void sendMessage(ServerWorld world, String message, Formatting formatting) {
		world.getPlayers(player -> true).forEach(player -> player.sendMessage(Text.literal(formatting + message)));
	}

	static class Listener implements TestListener {
		private final ServerWorld world;
		private final TestSet tests;

		public Listener(ServerWorld world, TestSet tests) {
			this.world = world;
			this.tests = tests;
		}

		@Override
		public void onStarted(GameTestState test) {
		}

		@Override
		public void onPassed(GameTestState test) {
			TestCommand.onCompletion(this.world, this.tests);
		}

		@Override
		public void onFailed(GameTestState test) {
			TestCommand.onCompletion(this.world, this.tests);
		}
	}
}
