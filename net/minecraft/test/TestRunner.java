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
import net.minecraft.test.GameTest;
import net.minecraft.test.GameTestBatch;
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
    private final List<GameTest> tests = Lists.newArrayList();
    private final Map<GameTest, BlockPos> field_25300 = Maps.newHashMap();
    private final List<Pair<GameTestBatch, Collection<GameTest>>> batches = Lists.newArrayList();
    private TestSet currentBatchTests;
    private int currentBatchIndex = 0;
    private BlockPos.Mutable reusablePos;

    public TestRunner(Collection<GameTestBatch> collection, BlockPos pos, BlockRotation blockRotation, ServerWorld serverWorld, TestManager testManager, int i) {
        this.reusablePos = pos.mutableCopy();
        this.pos = pos;
        this.world = serverWorld;
        this.testManager = testManager;
        this.sizeZ = i;
        collection.forEach(gameTestBatch -> {
            ArrayList<GameTest> collection = Lists.newArrayList();
            Collection<TestFunction> collection2 = gameTestBatch.getTestFunctions();
            for (TestFunction testFunction : collection2) {
                GameTest gameTest = new GameTest(testFunction, blockRotation, serverWorld);
                collection.add(gameTest);
                this.tests.add(gameTest);
            }
            this.batches.add(Pair.of(gameTestBatch, collection));
        });
    }

    public List<GameTest> getTests() {
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
        Pair<GameTestBatch, Collection<GameTest>> pair = this.batches.get(this.currentBatchIndex);
        GameTestBatch gameTestBatch = pair.getFirst();
        Collection<GameTest> collection = pair.getSecond();
        this.method_29401(collection);
        gameTestBatch.setWorld(this.world);
        String string = gameTestBatch.getId();
        LOGGER.info("Running test batch '" + string + "' (" + collection.size() + " tests)...");
        collection.forEach(gameTest -> {
            this.currentBatchTests.add((GameTest)gameTest);
            this.currentBatchTests.addListener(new TestListener(){

                @Override
                public void onStarted(GameTest test) {
                }

                @Override
                public void onFailed(GameTest test) {
                    TestRunner.this.onTestCompleted(test);
                }
            });
            BlockPos blockPos = this.field_25300.get(gameTest);
            TestUtil.startTest(gameTest, blockPos, this.testManager);
        });
    }

    private void onTestCompleted(GameTest test) {
        if (this.currentBatchTests.isDone()) {
            this.runBatch(this.currentBatchIndex + 1);
        }
    }

    private void method_29401(Collection<GameTest> collection) {
        int i = 0;
        Box box = new Box(this.reusablePos);
        for (GameTest gameTest : collection) {
            BlockPos blockPos = new BlockPos(this.reusablePos);
            StructureBlockBlockEntity structureBlockBlockEntity = StructureTestUtil.method_22250(gameTest.getStructureName(), blockPos, gameTest.method_29402(), 2, this.world, true);
            Box box2 = StructureTestUtil.getStructureBoundingBox(structureBlockBlockEntity);
            gameTest.setPos(structureBlockBlockEntity.getPos());
            this.field_25300.put(gameTest, new BlockPos(this.reusablePos));
            box = box.union(box2);
            this.reusablePos.move((int)box2.getXLength() + 5, 0, 0);
            if (i++ % this.sizeZ != this.sizeZ - 1) continue;
            this.reusablePos.move(0, 0, (int)box.getZLength() + 6);
            this.reusablePos.setX(this.pos.getX());
            box = new Box(this.reusablePos);
        }
    }
}

