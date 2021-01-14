/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.test;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.test.GameTestBatch;
import net.minecraft.test.GameTestState;
import net.minecraft.test.StructureTestUtil;
import net.minecraft.test.TestFunction;
import net.minecraft.test.TestListener;
import net.minecraft.test.TestManager;
import net.minecraft.test.TestSet;
import net.minecraft.test.TestUtil;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TestRunner {
    private static final Logger LOGGER = LogManager.getLogger();
    private final BlockPos pos;
    private final ServerWorld world;
    private final TestManager testManager;
    private final int sizeZ;
    private final List<GameTestState> tests = Lists.newArrayList();
    private final Map<GameTestState, BlockPos> field_25300 = Maps.newHashMap();
    private final List<Pair<GameTestBatch, Collection<GameTestState>>> batches = Lists.newArrayList();
    private TestSet currentBatchTests;
    private int currentBatchIndex = 0;
    private BlockPos.Mutable reusablePos;

    public TestRunner(Collection<GameTestBatch> batches, BlockPos pos, BlockRotation rotation, ServerWorld world, TestManager testManager, int sizeZ) {
        this.reusablePos = pos.mutableCopy();
        this.pos = pos;
        this.world = world;
        this.testManager = testManager;
        this.sizeZ = sizeZ;
        batches.forEach(gameTestBatch -> {
            ArrayList<GameTestState> collection = Lists.newArrayList();
            Collection<TestFunction> collection2 = gameTestBatch.getTestFunctions();
            for (TestFunction testFunction : collection2) {
                GameTestState gameTestState = new GameTestState(testFunction, rotation, world);
                collection.add(gameTestState);
                this.tests.add(gameTestState);
            }
            this.batches.add(Pair.of(gameTestBatch, collection));
        });
    }

    public List<GameTestState> getTests() {
        return this.tests;
    }

    public void run() {
        this.runBatch(0);
    }

    private void runBatch(int index) {
        this.currentBatchIndex = index;
        this.currentBatchTests = new TestSet();
        if (index >= this.batches.size()) {
            return;
        }
        Pair<GameTestBatch, Collection<GameTestState>> pair = this.batches.get(this.currentBatchIndex);
        GameTestBatch gameTestBatch = pair.getFirst();
        Collection<GameTestState> collection = pair.getSecond();
        this.method_29401(collection);
        gameTestBatch.startBatch(this.world);
        String string = gameTestBatch.getId();
        LOGGER.info("Running test batch '" + string + "' (" + collection.size() + " tests)...");
        collection.forEach(gameTestState -> {
            this.currentBatchTests.add((GameTestState)gameTestState);
            this.currentBatchTests.addListener(new TestListener(){

                @Override
                public void onStarted(GameTestState test) {
                }

                @Override
                public void onFailed(GameTestState test) {
                    TestRunner.this.onTestCompleted(test);
                }
            });
            BlockPos blockPos = this.field_25300.get(gameTestState);
            TestUtil.startTest(gameTestState, blockPos, this.testManager);
        });
    }

    private void onTestCompleted(GameTestState test) {
        if (this.currentBatchTests.isDone()) {
            this.runBatch(this.currentBatchIndex + 1);
        }
    }

    private void method_29401(Collection<GameTestState> collection) {
        int i = 0;
        Box box = new Box(this.reusablePos);
        for (GameTestState gameTestState : collection) {
            BlockPos blockPos = new BlockPos(this.reusablePos);
            StructureBlockBlockEntity structureBlockBlockEntity = StructureTestUtil.method_22250(gameTestState.getStructureName(), blockPos, gameTestState.method_29402(), 2, this.world, true);
            Box box2 = StructureTestUtil.getStructureBoundingBox(structureBlockBlockEntity);
            gameTestState.setPos(structureBlockBlockEntity.getPos());
            this.field_25300.put(gameTestState, new BlockPos(this.reusablePos));
            box = box.union(box2);
            this.reusablePos.move((int)box2.getXLength() + 5, 0, 0);
            if (i++ % this.sizeZ != this.sizeZ - 1) continue;
            this.reusablePos.move(0, 0, (int)box.getZLength() + 6);
            this.reusablePos.setX(this.pos.getX());
            box = new Box(this.reusablePos);
        }
    }
}

