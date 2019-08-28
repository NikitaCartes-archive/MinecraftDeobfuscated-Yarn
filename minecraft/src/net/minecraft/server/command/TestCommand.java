package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
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
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.client.network.DebugRendererInfoManager;
import net.minecraft.command.arguments.BlockStateArgument;
import net.minecraft.command.arguments.TestClassArgumentType;
import net.minecraft.command.arguments.TestFunctionArgumentType;
import net.minecraft.data.dev.NbtProvider;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.test.GameTest;
import net.minecraft.test.StructureTestUtil;
import net.minecraft.test.TestFunction;
import net.minecraft.test.TestFunctions;
import net.minecraft.test.TestListener;
import net.minecraft.test.TestManager;
import net.minecraft.test.TestSet;
import net.minecraft.test.TestUtil;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.Heightmap;
import org.apache.commons.io.IOUtils;

public class TestCommand {
	public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
		commandDispatcher.register(
			CommandManager.literal("test")
				.then(CommandManager.literal("runthis").executes(commandContext -> executeRunThis(commandContext.getSource())))
				.then(CommandManager.literal("runthese").executes(commandContext -> executeRunThese(commandContext.getSource())))
				.then(
					CommandManager.literal("run")
						.then(
							CommandManager.argument("testName", TestFunctionArgumentType.method_22371())
								.executes(commandContext -> executeRun(commandContext.getSource(), TestFunctionArgumentType.getFunction(commandContext, "testName")))
						)
				)
				.then(
					CommandManager.literal("runall")
						.executes(commandContext -> executeRunAll(commandContext.getSource()))
						.then(
							CommandManager.argument("testClassName", TestClassArgumentType.method_22370())
								.executes(commandContext -> executeRunAll(commandContext.getSource(), TestClassArgumentType.getTestClass(commandContext, "testClassName")))
						)
				)
				.then(
					CommandManager.literal("export")
						.then(
							CommandManager.argument("testName", StringArgumentType.word())
								.executes(commandContext -> executeExport(commandContext.getSource(), StringArgumentType.getString(commandContext, "testName")))
						)
				)
				.then(
					CommandManager.literal("import")
						.then(
							CommandManager.argument("testName", StringArgumentType.word())
								.executes(commandContext -> executeImport(commandContext.getSource(), StringArgumentType.getString(commandContext, "testName")))
						)
				)
				.then(CommandManager.literal("pos").executes(commandContext -> executePos(commandContext.getSource())))
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

	private static int executeCreate(ServerCommandSource serverCommandSource, String string, int i, int j, int k) {
		if (i <= 32 && j <= 32 && k <= 32) {
			ServerWorld serverWorld = serverCommandSource.getWorld();
			BlockPos blockPos = new BlockPos(serverCommandSource.getPosition());
			BlockPos blockPos2 = new BlockPos(
				blockPos.getX(), serverCommandSource.getWorld().getTopPosition(Heightmap.Type.WORLD_SURFACE, blockPos).getY(), blockPos.getZ() + 3
			);
			StructureTestUtil.createTestArea(string.toLowerCase(), blockPos2, new BlockPos(i, j, k), 2, serverWorld);

			for (int l = 0; l < i; l++) {
				for (int m = 0; m < k; m++) {
					BlockPos blockPos3 = new BlockPos(blockPos2.getX() + l, blockPos2.getY() + 1, blockPos2.getZ() + m);
					Block block = Blocks.POLISHED_ANDESITE;
					BlockStateArgument blockStateArgument = new BlockStateArgument(block.getDefaultState(), Collections.EMPTY_SET, null);
					blockStateArgument.setBlockState(serverWorld, blockPos3, 2);
				}
			}

			StructureTestUtil.placeStartButton(blockPos2.add(1, 0, -1), serverWorld);
			return 0;
		} else {
			throw new IllegalArgumentException("The structure must be less than 32 blocks big in each axis");
		}
	}

	private static int executePos(ServerCommandSource serverCommandSource) throws CommandSyntaxException {
		BlockHitResult blockHitResult = (BlockHitResult)serverCommandSource.getPlayer().rayTrace(10.0, 1.0F, false);
		BlockPos blockPos = blockHitResult.getBlockPos();
		ServerWorld serverWorld = serverCommandSource.getWorld();
		Optional<BlockPos> optional = StructureTestUtil.findContainingStructureBlock(blockPos, 15, serverWorld);
		if (!optional.isPresent()) {
			optional = StructureTestUtil.findContainingStructureBlock(blockPos, 200, serverWorld);
		}

		if (!optional.isPresent()) {
			serverCommandSource.sendError(new LiteralText("Can't find a structure block that contains the targeted pos " + blockPos));
			return 0;
		} else {
			StructureBlockBlockEntity structureBlockBlockEntity = (StructureBlockBlockEntity)serverWorld.getBlockEntity((BlockPos)optional.get());
			BlockPos blockPos2 = blockPos.subtract((Vec3i)optional.get());
			String string = blockPos2.getX() + ", " + blockPos2.getY() + ", " + blockPos2.getZ();
			String string2 = structureBlockBlockEntity.getStructurePath();
			sendMessage(serverCommandSource, "Position relative to " + string2 + ":");
			sendMessage(serverCommandSource, string);
			DebugRendererInfoManager.addGameTestMarker(serverWorld, new BlockPos(blockPos), string, -2147418368, 10000);
			return 1;
		}
	}

	private static int executeRunThis(ServerCommandSource serverCommandSource) {
		BlockPos blockPos = new BlockPos(serverCommandSource.getPosition());
		ServerWorld serverWorld = serverCommandSource.getWorld();
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

	private static int executeRunThese(ServerCommandSource serverCommandSource) {
		BlockPos blockPos = new BlockPos(serverCommandSource.getPosition());
		ServerWorld serverWorld = serverCommandSource.getWorld();
		Collection<BlockPos> collection = StructureTestUtil.findStructureBlocks(blockPos, 200, serverWorld);
		if (collection.isEmpty()) {
			sendMessage(serverWorld, "Couldn't find any structure blocks within 200 block radius", Formatting.RED);
			return 1;
		} else {
			TestUtil.clearDebugMarkers(serverWorld);
			sendMessage(serverCommandSource, "Running " + collection.size() + " tests...");
			TestSet testSet = new TestSet();
			collection.forEach(blockPosx -> run(serverWorld, blockPosx, testSet));
			return 1;
		}
	}

	private static void run(ServerWorld serverWorld, BlockPos blockPos, @Nullable TestSet testSet) {
		StructureBlockBlockEntity structureBlockBlockEntity = (StructureBlockBlockEntity)serverWorld.getBlockEntity(blockPos);
		String string = structureBlockBlockEntity.getStructurePath();
		TestFunction testFunction = TestFunctions.getTestFunctionOrThrow(string);
		GameTest gameTest = new GameTest(testFunction, blockPos, serverWorld);
		if (testSet != null) {
			testSet.add(gameTest);
			gameTest.addListener(new TestCommand.Listener(serverWorld, testSet));
		}

		TestUtil.startTest(gameTest, TestManager.INSTANCE);
	}

	private static void onCompletion(ServerWorld serverWorld, TestSet testSet) {
		if (testSet.isDone()) {
			sendMessage(serverWorld, "GameTest done! " + testSet.getTestCount() + " tests were run", Formatting.WHITE);
			if (testSet.failed()) {
				sendMessage(serverWorld, "" + testSet.getFailedRequiredTestCount() + " required tests failed :(", Formatting.RED);
			} else {
				sendMessage(serverWorld, "All required tests passed :)", Formatting.GREEN);
			}

			if (testSet.hasFailedOptionalTests()) {
				sendMessage(serverWorld, "" + testSet.getFailedOptionalTestCount() + " optional tests failed", Formatting.GRAY);
			}
		}
	}

	private static int executeClearAll(ServerCommandSource serverCommandSource, int i) {
		ServerWorld serverWorld = serverCommandSource.getWorld();
		TestUtil.clearDebugMarkers(serverWorld);
		BlockPos blockPos = new BlockPos(
			serverCommandSource.getPosition().x,
			(double)serverCommandSource.getWorld().getTopPosition(Heightmap.Type.WORLD_SURFACE, new BlockPos(serverCommandSource.getPosition())).getY(),
			serverCommandSource.getPosition().z
		);
		TestUtil.clearTests(serverWorld, blockPos, TestManager.INSTANCE, MathHelper.clamp(i, 0, 1024));
		return 1;
	}

	private static int executeRun(ServerCommandSource serverCommandSource, TestFunction testFunction) {
		ServerWorld serverWorld = serverCommandSource.getWorld();
		BlockPos blockPos = new BlockPos(serverCommandSource.getPosition());
		BlockPos blockPos2 = new BlockPos(
			blockPos.getX(), serverCommandSource.getWorld().getTopPosition(Heightmap.Type.WORLD_SURFACE, blockPos).getY(), blockPos.getZ() + 3
		);
		TestUtil.clearDebugMarkers(serverWorld);
		GameTest gameTest = new GameTest(testFunction, blockPos2, serverWorld);
		TestUtil.startTest(gameTest, TestManager.INSTANCE);
		return 1;
	}

	private static int executeRunAll(ServerCommandSource serverCommandSource) {
		TestUtil.clearDebugMarkers(serverCommandSource.getWorld());
		run(serverCommandSource, TestFunctions.getTestFunctions());
		return 1;
	}

	private static int executeRunAll(ServerCommandSource serverCommandSource, String string) {
		Collection<TestFunction> collection = TestFunctions.getTestFunctions(string);
		TestUtil.clearDebugMarkers(serverCommandSource.getWorld());
		run(serverCommandSource, collection);
		return 1;
	}

	private static void run(ServerCommandSource serverCommandSource, Collection<TestFunction> collection) {
		BlockPos blockPos = new BlockPos(serverCommandSource.getPosition());
		BlockPos blockPos2 = new BlockPos(
			blockPos.getX(), serverCommandSource.getWorld().getTopPosition(Heightmap.Type.WORLD_SURFACE, blockPos).getY(), blockPos.getZ() + 3
		);
		ServerWorld serverWorld = serverCommandSource.getWorld();
		sendMessage(serverCommandSource, "Running " + collection.size() + " tests...");
		Collection<GameTest> collection2 = TestUtil.runTestFunctions(collection, blockPos2, serverWorld, TestManager.INSTANCE);
		TestSet testSet = new TestSet(collection2);
		testSet.addListener(new TestCommand.Listener(serverWorld, testSet));
	}

	private static void sendMessage(ServerCommandSource serverCommandSource, String string) {
		serverCommandSource.sendFeedback(new LiteralText(string), false);
	}

	private static int executeExport(ServerCommandSource serverCommandSource, String string) {
		Path path = Paths.get(StructureTestUtil.testStructuresDirectoryName);
		Identifier identifier = new Identifier("minecraft", string);
		Path path2 = serverCommandSource.getWorld().getStructureManager().getStructurePath(identifier, ".nbt");
		Path path3 = NbtProvider.method_10493(path2, string, path);
		if (path3 == null) {
			sendMessage(serverCommandSource, "Failed to export " + path2);
			return 1;
		} else {
			try {
				Files.createDirectories(path3.getParent());
			} catch (IOException var7) {
				sendMessage(serverCommandSource, "Could not create folder " + path3.getParent());
				var7.printStackTrace();
				return 1;
			}

			sendMessage(serverCommandSource, "Exported to " + path3.toAbsolutePath());
			return 0;
		}
	}

	private static int executeImport(ServerCommandSource serverCommandSource, String string) {
		Path path = Paths.get(StructureTestUtil.testStructuresDirectoryName, string + ".snbt");
		Identifier identifier = new Identifier("minecraft", string);
		Path path2 = serverCommandSource.getWorld().getStructureManager().getStructurePath(identifier, ".nbt");

		try {
			BufferedReader bufferedReader = Files.newBufferedReader(path);
			String string2 = IOUtils.toString(bufferedReader);
			Files.createDirectories(path2.getParent());
			OutputStream outputStream = Files.newOutputStream(path2);
			NbtIo.writeCompressed(StringNbtReader.parse(string2), outputStream);
			sendMessage(serverCommandSource, "Imported to " + path2.toAbsolutePath());
			return 0;
		} catch (CommandSyntaxException | IOException var8) {
			System.err.println("Failed to load structure " + string);
			var8.printStackTrace();
			return 1;
		}
	}

	private static void sendMessage(ServerWorld serverWorld, String string, Formatting formatting) {
		serverWorld.getPlayers(serverPlayerEntity -> true).forEach(serverPlayerEntity -> serverPlayerEntity.sendMessage(new LiteralText(formatting + string)));
	}

	static class Listener implements TestListener {
		private final ServerWorld world;
		private final TestSet tests;

		public Listener(ServerWorld serverWorld, TestSet testSet) {
			this.world = serverWorld;
			this.tests = testSet;
		}

		@Override
		public void onStarted(GameTest gameTest) {
		}

		@Override
		public void onPassed(GameTest gameTest) {
			TestCommand.onCompletion(this.world, this.tests);
		}

		@Override
		public void onFailed(GameTest gameTest) {
			TestCommand.onCompletion(this.world, this.tests);
		}
	}
}
