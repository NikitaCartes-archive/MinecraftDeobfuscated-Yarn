package net.minecraft.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

public class TestStructurePlacer implements TestRunContext.TestStructureSpawner {
	private static final int MARGIN_X = 5;
	private static final int MARGIN_Z = 6;
	private final int testsPerRow;
	private int testsInCurrentRow;
	private Box box;
	private final BlockPos.Mutable mutablePos;
	private final BlockPos origin;
	private final boolean clearBeforeBatch;
	private float maxX = -1.0F;
	private final Collection<GameTestState> statesToClear = new ArrayList();

	public TestStructurePlacer(BlockPos origin, int testsPerRow, boolean clearBeforeBatch) {
		this.testsPerRow = testsPerRow;
		this.mutablePos = origin.mutableCopy();
		this.box = new Box(this.mutablePos);
		this.origin = origin;
		this.clearBeforeBatch = clearBeforeBatch;
	}

	@Override
	public void onBatch(ServerWorld world) {
		if (this.clearBeforeBatch) {
			this.statesToClear.forEach(state -> {
				BlockBox blockBox = StructureTestUtil.getStructureBlockBox(state.getStructureBlockBlockEntity());
				StructureTestUtil.clearArea(blockBox, world);
			});
			this.statesToClear.clear();
			this.box = new Box(this.origin);
			this.mutablePos.set(this.origin);
		}
	}

	@Override
	public Optional<GameTestState> spawnStructure(GameTestState gameTestState) {
		BlockPos blockPos = new BlockPos(this.mutablePos);
		gameTestState.setBoxMinPos(blockPos);
		gameTestState.init();
		Box box = StructureTestUtil.getStructureBoundingBox(gameTestState.getStructureBlockBlockEntity());
		this.box = this.box.union(box);
		this.mutablePos.move((int)box.getLengthX() + 5, 0, 0);
		if ((float)this.mutablePos.getX() > this.maxX) {
			this.maxX = (float)this.mutablePos.getX();
		}

		if (++this.testsInCurrentRow >= this.testsPerRow) {
			this.testsInCurrentRow = 0;
			this.mutablePos.move(0, 0, (int)this.box.getLengthZ() + 6);
			this.mutablePos.setX(this.origin.getX());
			this.box = new Box(this.mutablePos);
		}

		this.statesToClear.add(gameTestState);
		return Optional.of(gameTestState);
	}
}
