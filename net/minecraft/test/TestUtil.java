/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.test;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Streams;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LecternBlock;
import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.client.network.DebugRendererInfoManager;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.test.FailureLoggingTestCompletionListener;
import net.minecraft.test.GameTest;
import net.minecraft.test.GameTestBatch;
import net.minecraft.test.PositionedException;
import net.minecraft.test.StructureTestUtil;
import net.minecraft.test.TestCompletionListener;
import net.minecraft.test.TestFunction;
import net.minecraft.test.TestFunctions;
import net.minecraft.test.TestListener;
import net.minecraft.test.TestManager;
import net.minecraft.test.TestRunner;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;

public class TestUtil {
    public static TestCompletionListener field_20573 = new FailureLoggingTestCompletionListener();

    public static void startTest(GameTest gameTest, TestManager testManager) {
        gameTest.startCountdown();
        testManager.start(gameTest);
        gameTest.addListener(new TestListener(){

            @Override
            public void onStarted(GameTest gameTest) {
                TestUtil.createBeacon(gameTest, Blocks.LIGHT_GRAY_STAINED_GLASS);
            }

            @Override
            public void onFailed(GameTest gameTest) {
                TestUtil.createBeacon(gameTest, gameTest.isRequired() ? Blocks.RED_STAINED_GLASS : Blocks.ORANGE_STAINED_GLASS);
                TestUtil.createLectern(gameTest, Util.getInnermostMessage(gameTest.getThrowable()));
                TestUtil.handleTestFail(gameTest);
            }
        });
        gameTest.init(2);
    }

    public static Collection<GameTest> runTestBatches(Collection<GameTestBatch> collection, BlockPos blockPos, ServerWorld serverWorld, TestManager testManager) {
        TestRunner testRunner = new TestRunner(collection, blockPos, serverWorld, testManager);
        testRunner.run();
        return testRunner.getTests();
    }

    public static Collection<GameTest> runTestFunctions(Collection<TestFunction> collection, BlockPos blockPos, ServerWorld serverWorld, TestManager testManager) {
        return TestUtil.runTestBatches(TestUtil.createBatches(collection), blockPos, serverWorld, testManager);
    }

    public static Collection<GameTestBatch> createBatches(Collection<TestFunction> collection) {
        HashMap map = Maps.newHashMap();
        collection.forEach(testFunction -> {
            String string2 = testFunction.getBatchId();
            Collection collection = map.computeIfAbsent(string2, string -> Lists.newArrayList());
            collection.add(testFunction);
        });
        return map.keySet().stream().flatMap(string -> {
            Collection collection = (Collection)map.get(string);
            Consumer<ServerWorld> consumer = TestFunctions.getWorldSetter(string);
            AtomicInteger atomicInteger = new AtomicInteger();
            return Streams.stream(Iterables.partition(collection, 100)).map(list -> new GameTestBatch(string + ":" + atomicInteger.incrementAndGet(), collection, consumer));
        }).collect(Collectors.toList());
    }

    private static void handleTestFail(GameTest gameTest) {
        Throwable throwable = gameTest.getThrowable();
        String string = gameTest.getStructurePath() + " failed! " + Util.getInnermostMessage(throwable);
        TestUtil.sendMessage(gameTest.getWorld(), Formatting.RED, string);
        if (throwable instanceof PositionedException) {
            PositionedException positionedException = (PositionedException)throwable;
            TestUtil.addDebugMarker(gameTest.getWorld(), positionedException.getPos(), positionedException.getDebugMessage());
        }
        field_20573.onTestFailed(gameTest);
    }

    private static void createBeacon(GameTest gameTest, Block block) {
        ServerWorld serverWorld = gameTest.getWorld();
        BlockPos blockPos = gameTest.getPos();
        BlockPos blockPos2 = blockPos.add(-1, -1, -1);
        serverWorld.setBlockState(blockPos2, Blocks.BEACON.getDefaultState());
        BlockPos blockPos3 = blockPos2.add(0, 1, 0);
        serverWorld.setBlockState(blockPos3, block.getDefaultState());
        for (int i = -1; i <= 1; ++i) {
            for (int j = -1; j <= 1; ++j) {
                BlockPos blockPos4 = blockPos2.add(i, -1, j);
                serverWorld.setBlockState(blockPos4, Blocks.IRON_BLOCK.getDefaultState());
            }
        }
    }

    private static void createLectern(GameTest gameTest, String string) {
        ServerWorld serverWorld = gameTest.getWorld();
        BlockPos blockPos = gameTest.getPos();
        BlockPos blockPos2 = blockPos.add(-1, 1, -1);
        serverWorld.setBlockState(blockPos2, Blocks.LECTERN.getDefaultState());
        BlockState blockState = serverWorld.getBlockState(blockPos2);
        ItemStack itemStack = TestUtil.createBook(gameTest.getStructurePath(), gameTest.isRequired(), string);
        LecternBlock.putBookIfAbsent(serverWorld, blockPos2, blockState, itemStack);
    }

    private static ItemStack createBook(String string2, boolean bl, String string22) {
        ItemStack itemStack = new ItemStack(Items.WRITABLE_BOOK);
        ListTag listTag = new ListTag();
        StringBuffer stringBuffer = new StringBuffer();
        Arrays.stream(string2.split("\\.")).forEach(string -> stringBuffer.append((String)string).append('\n'));
        if (!bl) {
            stringBuffer.append("(optional)\n");
        }
        stringBuffer.append("-------------------\n");
        listTag.add(StringTag.of(stringBuffer.toString() + string22));
        itemStack.putSubTag("pages", listTag);
        return itemStack;
    }

    private static void sendMessage(ServerWorld serverWorld, Formatting formatting, String string) {
        serverWorld.getPlayers(serverPlayerEntity -> true).forEach(serverPlayerEntity -> serverPlayerEntity.sendMessage(new LiteralText(string).formatted(formatting)));
    }

    public static void clearDebugMarkers(ServerWorld serverWorld) {
        DebugRendererInfoManager.clearGameTestMarkers(serverWorld);
    }

    private static void addDebugMarker(ServerWorld serverWorld, BlockPos blockPos, String string) {
        DebugRendererInfoManager.addGameTestMarker(serverWorld, blockPos, string, -2130771968, Integer.MAX_VALUE);
    }

    public static void clearTests(ServerWorld serverWorld, BlockPos blockPos2, TestManager testManager, int i) {
        testManager.clear();
        BlockPos blockPos22 = blockPos2.add(-i, 0, -i);
        BlockPos blockPos3 = blockPos2.add(i, 0, i);
        BlockPos.stream(blockPos22, blockPos3).filter(blockPos -> serverWorld.getBlockState((BlockPos)blockPos).getBlock() == Blocks.STRUCTURE_BLOCK).forEach(blockPos -> {
            StructureBlockBlockEntity structureBlockBlockEntity = (StructureBlockBlockEntity)serverWorld.getBlockEntity((BlockPos)blockPos);
            BlockPos blockPos2 = structureBlockBlockEntity.getPos();
            BlockBox blockBox = StructureTestUtil.method_23646(blockPos2, structureBlockBlockEntity.getSize(), 2);
            StructureTestUtil.clearArea(blockBox, blockPos2.getY(), serverWorld);
        });
    }
}

