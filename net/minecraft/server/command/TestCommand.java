/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
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
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
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
import net.minecraft.world.Heightmap;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.Nullable;

public class TestCommand {
    public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("test").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.literal("runthis").executes(commandContext -> TestCommand.executeRunThis((ServerCommandSource)commandContext.getSource())))).then(CommandManager.literal("runthese").executes(commandContext -> TestCommand.executeRunThese((ServerCommandSource)commandContext.getSource())))).then(CommandManager.literal("run").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("testName", TestFunctionArgumentType.method_22371()).executes(commandContext -> TestCommand.executeRun((ServerCommandSource)commandContext.getSource(), TestFunctionArgumentType.getFunction(commandContext, "testName")))))).then(((LiteralArgumentBuilder)CommandManager.literal("runall").executes(commandContext -> TestCommand.executeRunAll((ServerCommandSource)commandContext.getSource()))).then(CommandManager.argument("testClassName", TestClassArgumentType.method_22370()).executes(commandContext -> TestCommand.executeRunAll((ServerCommandSource)commandContext.getSource(), TestClassArgumentType.getTestClass(commandContext, "testClassName")))))).then(CommandManager.literal("export").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("testName", StringArgumentType.word()).executes(commandContext -> TestCommand.executeExport((ServerCommandSource)commandContext.getSource(), StringArgumentType.getString(commandContext, "testName")))))).then(CommandManager.literal("import").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("testName", StringArgumentType.word()).executes(commandContext -> TestCommand.executeImport((ServerCommandSource)commandContext.getSource(), StringArgumentType.getString(commandContext, "testName")))))).then(CommandManager.literal("pos").executes(commandContext -> TestCommand.executePos((ServerCommandSource)commandContext.getSource())))).then(CommandManager.literal("create").then((ArgumentBuilder<ServerCommandSource, ?>)((RequiredArgumentBuilder)CommandManager.argument("testName", StringArgumentType.word()).executes(commandContext -> TestCommand.executeCreate((ServerCommandSource)commandContext.getSource(), StringArgumentType.getString(commandContext, "testName"), 5, 5, 5))).then(((RequiredArgumentBuilder)CommandManager.argument("width", IntegerArgumentType.integer()).executes(commandContext -> TestCommand.executeCreate((ServerCommandSource)commandContext.getSource(), StringArgumentType.getString(commandContext, "testName"), IntegerArgumentType.getInteger(commandContext, "width"), IntegerArgumentType.getInteger(commandContext, "width"), IntegerArgumentType.getInteger(commandContext, "width")))).then(CommandManager.argument("height", IntegerArgumentType.integer()).then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("depth", IntegerArgumentType.integer()).executes(commandContext -> TestCommand.executeCreate((ServerCommandSource)commandContext.getSource(), StringArgumentType.getString(commandContext, "testName"), IntegerArgumentType.getInteger(commandContext, "width"), IntegerArgumentType.getInteger(commandContext, "height"), IntegerArgumentType.getInteger(commandContext, "depth"))))))))).then(((LiteralArgumentBuilder)CommandManager.literal("clearall").executes(commandContext -> TestCommand.executeClearAll((ServerCommandSource)commandContext.getSource(), 200))).then(CommandManager.argument("radius", IntegerArgumentType.integer()).executes(commandContext -> TestCommand.executeClearAll((ServerCommandSource)commandContext.getSource(), IntegerArgumentType.getInteger(commandContext, "radius"))))));
    }

    private static int executeCreate(ServerCommandSource serverCommandSource, String string, int i, int j, int k) {
        if (i > 32 || j > 32 || k > 32) {
            throw new IllegalArgumentException("The structure must be less than 32 blocks big in each axis");
        }
        ServerWorld serverWorld = serverCommandSource.getWorld();
        BlockPos blockPos = new BlockPos(serverCommandSource.getPosition());
        BlockPos blockPos2 = new BlockPos(blockPos.getX(), serverCommandSource.getWorld().getTopPosition(Heightmap.Type.WORLD_SURFACE, blockPos).getY(), blockPos.getZ() + 3);
        StructureTestUtil.createTestArea(string.toLowerCase(), blockPos2, new BlockPos(i, j, k), 2, serverWorld);
        for (int l = 0; l < i; ++l) {
            for (int m = 0; m < k; ++m) {
                BlockPos blockPos3 = new BlockPos(blockPos2.getX() + l, blockPos2.getY() + 1, blockPos2.getZ() + m);
                Block block = Blocks.POLISHED_ANDESITE;
                BlockStateArgument blockStateArgument = new BlockStateArgument(block.getDefaultState(), Collections.EMPTY_SET, null);
                blockStateArgument.setBlockState(serverWorld, blockPos3, 2);
            }
        }
        StructureTestUtil.placeStartButton(blockPos2.add(1, 0, -1), serverWorld);
        return 0;
    }

    private static int executePos(ServerCommandSource serverCommandSource) throws CommandSyntaxException {
        ServerWorld serverWorld;
        BlockHitResult blockHitResult = (BlockHitResult)serverCommandSource.getPlayer().rayTrace(10.0, 1.0f, false);
        BlockPos blockPos = blockHitResult.getBlockPos();
        Optional<BlockPos> optional = StructureTestUtil.findContainingStructureBlock(blockPos, 15, serverWorld = serverCommandSource.getWorld());
        if (!optional.isPresent()) {
            optional = StructureTestUtil.findContainingStructureBlock(blockPos, 200, serverWorld);
        }
        if (!optional.isPresent()) {
            serverCommandSource.sendError(new LiteralText("Can't find a structure block that contains the targeted pos " + blockPos));
            return 0;
        }
        StructureBlockBlockEntity structureBlockBlockEntity = (StructureBlockBlockEntity)serverWorld.getBlockEntity(optional.get());
        BlockPos blockPos2 = blockPos.subtract(optional.get());
        String string = blockPos2.getX() + ", " + blockPos2.getY() + ", " + blockPos2.getZ();
        String string2 = structureBlockBlockEntity.getStructurePath();
        TestCommand.sendMessage(serverCommandSource, "Position relative to " + string2 + ":");
        TestCommand.sendMessage(serverCommandSource, string);
        DebugRendererInfoManager.addGameTestMarker(serverWorld, new BlockPos(blockPos), string, -2147418368, 10000);
        return 1;
    }

    private static int executeRunThis(ServerCommandSource serverCommandSource) {
        ServerWorld serverWorld;
        BlockPos blockPos = new BlockPos(serverCommandSource.getPosition());
        BlockPos blockPos2 = StructureTestUtil.findNearestStructureBlock(blockPos, 15, serverWorld = serverCommandSource.getWorld());
        if (blockPos2 == null) {
            TestCommand.sendMessage(serverWorld, "Couldn't find any structure block within 15 radius", Formatting.RED);
            return 0;
        }
        TestUtil.clearDebugMarkers(serverWorld);
        TestCommand.run(serverWorld, blockPos2, null);
        return 1;
    }

    private static int executeRunThese(ServerCommandSource serverCommandSource) {
        ServerWorld serverWorld;
        BlockPos blockPos2 = new BlockPos(serverCommandSource.getPosition());
        Collection<BlockPos> collection = StructureTestUtil.findStructureBlocks(blockPos2, 200, serverWorld = serverCommandSource.getWorld());
        if (collection.isEmpty()) {
            TestCommand.sendMessage(serverWorld, "Couldn't find any structure blocks within 200 block radius", Formatting.RED);
            return 1;
        }
        TestUtil.clearDebugMarkers(serverWorld);
        TestCommand.sendMessage(serverCommandSource, "Running " + collection.size() + " tests...");
        TestSet testSet = new TestSet();
        collection.forEach(blockPos -> TestCommand.run(serverWorld, blockPos, testSet));
        return 1;
    }

    private static void run(ServerWorld serverWorld, BlockPos blockPos, @Nullable TestSet testSet) {
        StructureBlockBlockEntity structureBlockBlockEntity = (StructureBlockBlockEntity)serverWorld.getBlockEntity(blockPos);
        String string = structureBlockBlockEntity.getStructurePath();
        TestFunction testFunction = TestFunctions.getTestFunctionOrThrow(string);
        GameTest gameTest = new GameTest(testFunction, blockPos, serverWorld);
        if (testSet != null) {
            testSet.add(gameTest);
            gameTest.addListener(new Listener(serverWorld, testSet));
        }
        TestUtil.startTest(gameTest, TestManager.INSTANCE);
    }

    private static void onCompletion(ServerWorld serverWorld, TestSet testSet) {
        if (testSet.isDone()) {
            TestCommand.sendMessage(serverWorld, "GameTest done! " + testSet.getTestCount() + " tests were run", Formatting.WHITE);
            if (testSet.failed()) {
                TestCommand.sendMessage(serverWorld, "" + testSet.getFailedRequiredTestCount() + " required tests failed :(", Formatting.RED);
            } else {
                TestCommand.sendMessage(serverWorld, "All required tests passed :)", Formatting.GREEN);
            }
            if (testSet.hasFailedOptionalTests()) {
                TestCommand.sendMessage(serverWorld, "" + testSet.getFailedOptionalTestCount() + " optional tests failed", Formatting.GRAY);
            }
        }
    }

    private static int executeClearAll(ServerCommandSource serverCommandSource, int i) {
        ServerWorld serverWorld = serverCommandSource.getWorld();
        TestUtil.clearDebugMarkers(serverWorld);
        BlockPos blockPos = new BlockPos(serverCommandSource.getPosition().x, (double)serverCommandSource.getWorld().getTopPosition(Heightmap.Type.WORLD_SURFACE, new BlockPos(serverCommandSource.getPosition())).getY(), serverCommandSource.getPosition().z);
        TestUtil.clearTests(serverWorld, blockPos, TestManager.INSTANCE, MathHelper.clamp(i, 0, 1024));
        return 1;
    }

    private static int executeRun(ServerCommandSource serverCommandSource, TestFunction testFunction) {
        ServerWorld serverWorld = serverCommandSource.getWorld();
        BlockPos blockPos = new BlockPos(serverCommandSource.getPosition());
        BlockPos blockPos2 = new BlockPos(blockPos.getX(), serverCommandSource.getWorld().getTopPosition(Heightmap.Type.WORLD_SURFACE, blockPos).getY(), blockPos.getZ() + 3);
        TestUtil.clearDebugMarkers(serverWorld);
        GameTest gameTest = new GameTest(testFunction, blockPos2, serverWorld);
        TestUtil.startTest(gameTest, TestManager.INSTANCE);
        return 1;
    }

    private static int executeRunAll(ServerCommandSource serverCommandSource) {
        TestUtil.clearDebugMarkers(serverCommandSource.getWorld());
        TestCommand.run(serverCommandSource, TestFunctions.getTestFunctions());
        return 1;
    }

    private static int executeRunAll(ServerCommandSource serverCommandSource, String string) {
        Collection<TestFunction> collection = TestFunctions.getTestFunctions(string);
        TestUtil.clearDebugMarkers(serverCommandSource.getWorld());
        TestCommand.run(serverCommandSource, collection);
        return 1;
    }

    private static void run(ServerCommandSource serverCommandSource, Collection<TestFunction> collection) {
        BlockPos blockPos = new BlockPos(serverCommandSource.getPosition());
        BlockPos blockPos2 = new BlockPos(blockPos.getX(), serverCommandSource.getWorld().getTopPosition(Heightmap.Type.WORLD_SURFACE, blockPos).getY(), blockPos.getZ() + 3);
        ServerWorld serverWorld = serverCommandSource.getWorld();
        TestCommand.sendMessage(serverCommandSource, "Running " + collection.size() + " tests...");
        Collection<GameTest> collection2 = TestUtil.runTestFunctions(collection, blockPos2, serverWorld, TestManager.INSTANCE);
        TestSet testSet = new TestSet(collection2);
        testSet.addListener(new Listener(serverWorld, testSet));
    }

    private static void sendMessage(ServerCommandSource serverCommandSource, String string) {
        serverCommandSource.sendFeedback(new LiteralText(string), false);
    }

    private static int executeExport(ServerCommandSource serverCommandSource, String string) {
        Path path = Paths.get(StructureTestUtil.testStructuresDirectoryName, new String[0]);
        Identifier identifier = new Identifier("minecraft", string);
        Path path2 = serverCommandSource.getWorld().getStructureManager().getStructurePath(identifier, ".nbt");
        Path path3 = NbtProvider.method_10493(path2, string, path);
        if (path3 == null) {
            TestCommand.sendMessage(serverCommandSource, "Failed to export " + path2);
            return 1;
        }
        try {
            Files.createDirectories(path3.getParent(), new FileAttribute[0]);
        } catch (IOException iOException) {
            TestCommand.sendMessage(serverCommandSource, "Could not create folder " + path3.getParent());
            iOException.printStackTrace();
            return 1;
        }
        TestCommand.sendMessage(serverCommandSource, "Exported to " + path3.toAbsolutePath());
        return 0;
    }

    private static int executeImport(ServerCommandSource serverCommandSource, String string) {
        Path path = Paths.get(StructureTestUtil.testStructuresDirectoryName, string + ".snbt");
        Identifier identifier = new Identifier("minecraft", string);
        Path path2 = serverCommandSource.getWorld().getStructureManager().getStructurePath(identifier, ".nbt");
        try {
            BufferedReader bufferedReader = Files.newBufferedReader(path);
            String string2 = IOUtils.toString(bufferedReader);
            Files.createDirectories(path2.getParent(), new FileAttribute[0]);
            OutputStream outputStream = Files.newOutputStream(path2, new OpenOption[0]);
            NbtIo.writeCompressed(StringNbtReader.parse(string2), outputStream);
            TestCommand.sendMessage(serverCommandSource, "Imported to " + path2.toAbsolutePath());
            return 0;
        } catch (CommandSyntaxException | IOException exception) {
            System.err.println("Failed to load structure " + string);
            exception.printStackTrace();
            return 1;
        }
    }

    private static void sendMessage(ServerWorld serverWorld, String string, Formatting formatting) {
        serverWorld.getPlayers(serverPlayerEntity -> true).forEach(serverPlayerEntity -> serverPlayerEntity.sendMessage(new LiteralText((Object)((Object)formatting) + string)));
    }

    static class Listener
    implements TestListener {
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

