/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.test;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.test.GameTest;
import net.minecraft.test.GameTestBatch;
import net.minecraft.test.StructureTestUtil;
import net.minecraft.test.TestFunction;
import net.minecraft.test.TestListener;
import net.minecraft.test.TestManager;
import net.minecraft.test.TestSet;
import net.minecraft.test.TestUtil;
import net.minecraft.util.math.BlockPos;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TestRunner {
    private static final Logger LOGGER = LogManager.getLogger();
    private final List<GameTestBatch> batches;
    private final BlockPos pos;
    private final ServerWorld world;
    private final TestManager testManager;
    private final List<GameTest> tests = Lists.newArrayList();
    private final Map<String, Collection<GameTest>> testsByBatch = Maps.newHashMap();
    private TestSet currentBatchTests;
    private int currentBatchIndex = 0;
    private BlockPos.Mutable reusablePos;
    private int sizeZ = 0;

    public TestRunner(Collection<GameTestBatch> collection, BlockPos blockPos, ServerWorld serverWorld, TestManager testManager) {
        this.batches = Lists.newArrayList(collection);
        this.reusablePos = new BlockPos.Mutable(blockPos);
        this.pos = blockPos;
        this.world = serverWorld;
        this.testManager = testManager;
    }

    private void init() {
        this.batches.forEach(gameTestBatch -> {
            ArrayList<GameTest> collection = Lists.newArrayList();
            Collection<TestFunction> collection2 = gameTestBatch.getTestFunctions();
            for (TestFunction testFunction : collection2) {
                BlockPos blockPos = new BlockPos(this.reusablePos);
                StructureTestUtil.method_22250(testFunction.getStructureName(), blockPos, 2, this.world, true);
                GameTest gameTest = new GameTest(testFunction, blockPos, this.world);
                collection.add(gameTest);
                this.tests.add(gameTest);
                this.testsByBatch.put(gameTestBatch.getId(), collection);
                BlockPos blockPos2 = gameTest.getSize();
                int i = blockPos2 == null ? 1 : blockPos2.getX();
                int j = blockPos2 == null ? 1 : blockPos2.getZ();
                this.sizeZ = Math.max(this.sizeZ, j);
                this.reusablePos.setOffset(i + 4, 0, 0);
                if (this.tests.size() % 8 != 0) continue;
                this.reusablePos.setOffset(0, 0, this.sizeZ + 5);
                this.reusablePos.setX(this.pos.getX());
                this.sizeZ = 0;
            }
        });
    }

    public List<GameTest> getTests() {
        return this.tests;
    }

    public void run() {
        this.init();
        this.runBatch(0);
    }

    private void runBatch(int i) {
        this.currentBatchIndex = i;
        this.currentBatchTests = new TestSet();
        if (i >= this.batches.size()) {
            return;
        }
        GameTestBatch gameTestBatch = this.batches.get(this.currentBatchIndex);
        gameTestBatch.setWorld(this.world);
        this.runBatch(gameTestBatch);
        String string = gameTestBatch.getId();
        Collection<GameTest> collection = this.testsByBatch.get(string);
        LOGGER.info("Running test batch '" + string + "' (" + collection.size() + " tests)...");
        collection.forEach(gameTest -> {
            this.currentBatchTests.add((GameTest)gameTest);
            this.currentBatchTests.addListener(new TestListener(){

                @Override
                public void onStarted(GameTest gameTest) {
                }

                @Override
                public void onPassed(GameTest gameTest) {
                    TestRunner.this.onTestCompleted(gameTest);
                }

                @Override
                public void onFailed(GameTest gameTest) {
                    TestRunner.this.onTestCompleted(gameTest);
                }
            });
            TestUtil.startTest(gameTest, this.testManager);
        });
    }

    private void onTestCompleted(GameTest gameTest) {
        if (this.currentBatchTests.isDone()) {
            this.runBatch(this.currentBatchIndex + 1);
        }
    }

    private void runBatch(GameTestBatch gameTestBatch) {
        Collection<GameTest> collection = this.testsByBatch.get(gameTestBatch.getId());
        collection.forEach(gameTest -> gameTest.start(2));
    }
}

