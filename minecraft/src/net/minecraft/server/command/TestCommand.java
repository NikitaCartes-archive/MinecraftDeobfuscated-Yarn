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
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.StringNbtReader;
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
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.Heightmap;
import org.apache.commons.io.IOUtils;

public class TestCommand {
	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(
			CommandManager.literal("test")
				.then(CommandManager.literal("runthis").executes(commandContext -> executeRunThis(commandContext.getSource())))
				.then(CommandManager.literal("runthese").executes(commandContext -> executeRunThese(commandContext.getSource())))
				.then(
					CommandManager.literal("runfailed")
						.executes(commandContext -> method_29411(commandContext.getSource(), false, 0, 8))
						.then(
							CommandManager.argument("onlyRequiredTests", BoolArgumentType.bool())
								.executes(commandContext -> method_29411(commandContext.getSource(), BoolArgumentType.getBool(commandContext, "onlyRequiredTests"), 0, 8))
								.then(
									CommandManager.argument("rotationSteps", IntegerArgumentType.integer())
										.executes(
											commandContext -> method_29411(
													commandContext.getSource(),
													BoolArgumentType.getBool(commandContext, "onlyRequiredTests"),
													IntegerArgumentType.getInteger(commandContext, "rotationSteps"),
													8
												)
										)
										.then(
											CommandManager.argument("testsPerRow", IntegerArgumentType.integer())
												.executes(
													commandContext -> method_29411(
															commandContext.getSource(),
															BoolArgumentType.getBool(commandContext, "onlyRequiredTests"),
															IntegerArgumentType.getInteger(commandContext, "rotationSteps"),
															IntegerArgumentType.getInteger(commandContext, "testsPerRow")
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
								.executes(commandContext -> executeRun(commandContext.getSource(), TestFunctionArgumentType.getFunction(commandContext, "testName"), 0))
								.then(
									CommandManager.argument("rotationSteps", IntegerArgumentType.integer())
										.executes(
											commandContext -> executeRun(
													commandContext.getSource(),
													TestFunctionArgumentType.getFunction(commandContext, "testName"),
													IntegerArgumentType.getInteger(commandContext, "rotationSteps")
												)
										)
								)
						)
				)
				.then(
					CommandManager.literal("runall")
						.executes(commandContext -> executeRunAll(commandContext.getSource(), 0, 8))
						.then(
							CommandManager.argument("testClassName", TestClassArgumentType.testClass())
								.executes(commandContext -> executeRunAll(commandContext.getSource(), TestClassArgumentType.getTestClass(commandContext, "testClassName"), 0, 8))
								.then(
									CommandManager.argument("rotationSteps", IntegerArgumentType.integer())
										.executes(
											commandContext -> executeRunAll(
													commandContext.getSource(),
													TestClassArgumentType.getTestClass(commandContext, "testClassName"),
													IntegerArgumentType.getInteger(commandContext, "rotationSteps"),
													8
												)
										)
										.then(
											CommandManager.argument("testsPerRow", IntegerArgumentType.integer())
												.executes(
													commandContext -> executeRunAll(
															commandContext.getSource(),
															TestClassArgumentType.getTestClass(commandContext, "testClassName"),
															IntegerArgumentType.getInteger(commandContext, "rotationSteps"),
															IntegerArgumentType.getInteger(commandContext, "testsPerRow")
														)
												)
										)
								)
						)
						.then(
							CommandManager.argument("rotationSteps", IntegerArgumentType.integer())
								.executes(commandContext -> executeRunAll(commandContext.getSource(), IntegerArgumentType.getInteger(commandContext, "rotationSteps"), 8))
								.then(
									CommandManager.argument("testsPerRow", IntegerArgumentType.integer())
										.executes(
											commandContext -> executeRunAll(
													commandContext.getSource(),
													IntegerArgumentType.getInteger(commandContext, "rotationSteps"),
													IntegerArgumentType.getInteger(commandContext, "testsPerRow")
												)
										)
								)
						)
				)
				.then(
					CommandManager.literal("export")
						.then(
							CommandManager.argument("testName", StringArgumentType.word())
								.executes(commandContext -> executeExport(commandContext.getSource(), StringArgumentType.getString(commandContext, "testName")))
						)
				)
				.then(CommandManager.literal("exportthis").executes(commandContext -> method_29413(commandContext.getSource())))
				.then(
					CommandManager.literal("import")
						.then(
							CommandManager.argument("testName", StringArgumentType.word())
								.executes(commandContext -> executeImport(commandContext.getSource(), StringArgumentType.getString(commandContext, "testName")))
						)
				)
				.then(
					CommandManager.literal("pos")
						.executes(commandContext -> executePos(commandContext.getSource(), "pos"))
						.then(
							CommandManager.argument("var", StringArgumentType.word())
								.executes(commandContext -> executePos(commandContext.getSource(), StringArgumentType.getString(commandContext, "var")))
						)
				)
				.then(
					CommandManager.literal("create")
						.then(
							CommandManager.argument("testName", StringArgumentType.word())
								.executes(commandContext -> executeCreate(commandContext.getSource(), StringArgumentType.getString(commandContext, "testName"), 5, 5, 5))
								.then(
									CommandManager.argument("width", IntegerArgumentType.integer())
										.executes(
											commandContext -> executeCreate(
													commandContext.getSource(),
													StringArgumentType.getString(commandContext, "testName"),
													IntegerArgumentType.getInteger(commandContext, "width"),
													IntegerArgumentType.getInteger(commandContext, "width"),
													IntegerArgumentType.getInteger(commandContext, "width")
												)
										)
										.then(
											CommandManager.argument("height", IntegerArgumentType.integer())
												.then(
													CommandManager.argument("depth", IntegerArgumentType.integer())
														.executes(
															commandContext -> executeCreate(
																	commandContext.getSource(),
																	StringArgumentType.getString(commandContext, "testName"),
																	IntegerArgumentType.getInteger(commandContext, "width"),
																	IntegerArgumentType.getInteger(commandContext, "height"),
																	IntegerArgumentType.getInteger(commandContext, "depth")
																)
														)
												)
										)
								)
						)
				)
				.then(
					CommandManager.literal("clearall")
						.executes(commandContext -> executeClearAll(commandContext.getSource(), 200))
						.then(
							CommandManager.argument("radius", IntegerArgumentType.integer())
								.executes(commandContext -> executeClearAll(commandContext.getSource(), IntegerArgumentType.getInteger(commandContext, "radius")))
						)
				)
		);
	}

	private static int executeCreate(ServerCommandSource source, String structure, int x, int y, int z) {
		if (x <= 48 && y <= 48 && z <= 48) {
			ServerWorld serverWorld = source.getWorld();
			BlockPos blockPos = new BlockPos(source.getPosition());
			BlockPos blockPos2 = new BlockPos(blockPos.getX(), source.getWorld().getTopPosition(Heightmap.Type.WORLD_SURFACE, blockPos).getY(), blockPos.getZ() + 3);
			StructureTestUtil.createTestArea(structure.toLowerCase(), blockPos2, new BlockPos(x, y, z), BlockRotation.NONE, serverWorld);

			for (int i = 0; i < x; i++) {
				for (int j = 0; j < z; j++) {
					BlockPos blockPos3 = new BlockPos(blockPos2.getX() + i, blockPos2.getY() + 1, blockPos2.getZ() + j);
					Block block = Blocks.POLISHED_ANDESITE;
					BlockStateArgument blockStateArgument = new BlockStateArgument(block.getDefaultState(), Collections.EMPTY_SET, null);
					blockStateArgument.setBlockState(serverWorld, blockPos3, 2);
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
			source.sendError(new LiteralText("Can't find a structure block that contains the targeted pos " + blockPos));
			return 0;
		} else {
			StructureBlockBlockEntity structureBlockBlockEntity = (StructureBlockBlockEntity)serverWorld.getBlockEntity((BlockPos)optional.get());
			BlockPos blockPos2 = blockPos.subtract((Vec3i)optional.get());
			String string = blockPos2.getX() + ", " + blockPos2.getY() + ", " + blockPos2.getZ();
			String string2 = structureBlockBlockEntity.getStructurePath();
			Text text = new LiteralText(string)
				.setStyle(
					Style.EMPTY
						.withBold(true)
						.withColor(Formatting.GREEN)
						.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new LiteralText("Click to copy to clipboard")))
						.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, "final BlockPos " + variableName + " = new BlockPos(" + string + ");"))
				);
			source.sendFeedback(new LiteralText("Position relative to " + string2 + ": ").append(text), false);
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
			collection.forEach(blockPosx -> run(serverWorld, blockPosx, testSet));
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

		setWorld(testFunction, world);
		Box box = StructureTestUtil.getStructureBoundingBox(structureBlockBlockEntity);
		BlockPos blockPos = new BlockPos(box.minX, box.minY, box.minZ);
		TestUtil.startTest(gameTestState, blockPos, TestManager.INSTANCE);
	}

	private static void onCompletion(ServerWorld world, TestSet tests) {
		if (tests.isDone()) {
			sendMessage(world, "GameTest done! " + tests.getTestCount() + " tests were run", Formatting.WHITE);
			if (tests.failed()) {
				sendMessage(world, "" + tests.getFailedRequiredTestCount() + " required tests failed :(", Formatting.RED);
			} else {
				sendMessage(world, "All required tests passed :)", Formatting.GREEN);
			}

			if (tests.hasFailedOptionalTests()) {
				sendMessage(world, "" + tests.getFailedOptionalTestCount() + " optional tests failed", Formatting.GRAY);
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
		setWorld(testFunction, serverWorld);
		BlockRotation blockRotation = StructureTestUtil.method_29408(rotationSteps);
		GameTestState gameTestState = new GameTestState(testFunction, blockRotation, serverWorld);
		TestUtil.startTest(gameTestState, blockPos2, TestManager.INSTANCE);
		return 1;
	}

	private static void setWorld(TestFunction testFunction, ServerWorld world) {
		Consumer<ServerWorld> consumer = TestFunctions.getAfterBatchConsumer(testFunction.getBatchId());
		if (consumer != null) {
			consumer.accept(world);
		}
	}

	private static int executeRunAll(ServerCommandSource source, int rotationSteps, int sizeZ) {
		TestUtil.clearDebugMarkers(source.getWorld());
		Collection<TestFunction> collection = TestFunctions.getTestFunctions();
		sendMessage(source, "Running all " + collection.size() + " tests...");
		TestFunctions.method_29406();
		run(source, collection, rotationSteps, sizeZ);
		return 1;
	}

	private static int executeRunAll(ServerCommandSource source, String testClass, int rotationSteps, int sizeZ) {
		Collection<TestFunction> collection = TestFunctions.getTestFunctions(testClass);
		TestUtil.clearDebugMarkers(source.getWorld());
		sendMessage(source, "Running " + collection.size() + " tests from " + testClass + "...");
		TestFunctions.method_29406();
		run(source, collection, rotationSteps, sizeZ);
		return 1;
	}

	private static int method_29411(ServerCommandSource serverCommandSource, boolean bl, int i, int j) {
		Collection<TestFunction> collection;
		if (bl) {
			collection = (Collection<TestFunction>)TestFunctions.method_29405().stream().filter(TestFunction::isRequired).collect(Collectors.toList());
		} else {
			collection = TestFunctions.method_29405();
		}

		if (collection.isEmpty()) {
			sendMessage(serverCommandSource, "No failed tests to rerun");
			return 0;
		} else {
			TestUtil.clearDebugMarkers(serverCommandSource.getWorld());
			sendMessage(serverCommandSource, "Rerunning " + collection.size() + " failed tests (" + (bl ? "only required tests" : "including optional tests") + ")");
			run(serverCommandSource, collection, i, j);
			return 1;
		}
	}

	private static void run(ServerCommandSource source, Collection<TestFunction> testFunctions, int rotationSteps, int i) {
		BlockPos blockPos = new BlockPos(source.getPosition());
		BlockPos blockPos2 = new BlockPos(blockPos.getX(), source.getWorld().getTopPosition(Heightmap.Type.WORLD_SURFACE, blockPos).getY(), blockPos.getZ() + 3);
		ServerWorld serverWorld = source.getWorld();
		BlockRotation blockRotation = StructureTestUtil.method_29408(rotationSteps);
		Collection<GameTestState> collection = TestUtil.runTestFunctions(testFunctions, blockPos2, blockRotation, serverWorld, TestManager.INSTANCE, i);
		TestSet testSet = new TestSet(collection);
		testSet.addListener(new TestCommand.Listener(serverWorld, testSet));
		testSet.method_29407(gameTestState -> TestFunctions.method_29404(gameTestState.method_29403()));
	}

	private static void sendMessage(ServerCommandSource source, String message) {
		source.sendFeedback(new LiteralText(message), false);
	}

	private static int method_29413(ServerCommandSource serverCommandSource) {
		BlockPos blockPos = new BlockPos(serverCommandSource.getPosition());
		ServerWorld serverWorld = serverCommandSource.getWorld();
		BlockPos blockPos2 = StructureTestUtil.findNearestStructureBlock(blockPos, 15, serverWorld);
		if (blockPos2 == null) {
			sendMessage(serverWorld, "Couldn't find any structure block within 15 radius", Formatting.RED);
			return 0;
		} else {
			StructureBlockBlockEntity structureBlockBlockEntity = (StructureBlockBlockEntity)serverWorld.getBlockEntity(blockPos2);
			String string = structureBlockBlockEntity.getStructurePath();
			return executeExport(serverCommandSource, string);
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

	private static int executeImport(ServerCommandSource serverCommandSource, String structure) {
		Path path = Paths.get(StructureTestUtil.testStructuresDirectoryName, structure + ".snbt");
		Identifier identifier = new Identifier("minecraft", structure);
		Path path2 = serverCommandSource.getWorld().getStructureManager().getStructurePath(identifier, ".nbt");

		try {
			BufferedReader bufferedReader = Files.newBufferedReader(path);
			String string = IOUtils.toString(bufferedReader);
			Files.createDirectories(path2.getParent());
			OutputStream outputStream = Files.newOutputStream(path2);
			Throwable var8 = null;

			try {
				NbtIo.writeCompressed(StringNbtReader.parse(string), outputStream);
			} catch (Throwable var18) {
				var8 = var18;
				throw var18;
			} finally {
				if (outputStream != null) {
					if (var8 != null) {
						try {
							outputStream.close();
						} catch (Throwable var17) {
							var8.addSuppressed(var17);
						}
					} else {
						outputStream.close();
					}
				}
			}

			sendMessage(serverCommandSource, "Imported to " + path2.toAbsolutePath());
			return 0;
		} catch (CommandSyntaxException | IOException var20) {
			System.err.println("Failed to load structure " + structure);
			var20.printStackTrace();
			return 1;
		}
	}

	private static void sendMessage(ServerWorld world, String message, Formatting formatting) {
		world.getPlayers(serverPlayerEntity -> true)
			.forEach(serverPlayerEntity -> serverPlayerEntity.sendSystemMessage(new LiteralText(formatting + message), Util.NIL_UUID));
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
		public void onFailed(GameTestState test) {
			TestCommand.onCompletion(this.world, this.tests);
		}
	}
}
