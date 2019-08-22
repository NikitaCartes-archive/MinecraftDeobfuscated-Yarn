package net.minecraft;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.test.GameTestBatch;
import net.minecraft.test.TestFunction;
import net.minecraft.util.math.BlockPos;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_4515 {
	private static final Logger LOGGER = LogManager.getLogger();
	private final List<GameTestBatch> field_20547;
	private final BlockPos field_20548;
	private final ServerWorld field_20549;
	private final class_4521 field_20550;
	private final List<class_4517> field_20551 = Lists.<class_4517>newArrayList();
	private final Map<String, Collection<class_4517>> field_20552 = Maps.<String, Collection<class_4517>>newHashMap();
	private class_4524 field_20553;
	private int field_20554 = 0;
	private BlockPos.Mutable field_20555;
	private int field_20556 = 0;

	public class_4515(Collection<GameTestBatch> collection, BlockPos blockPos, ServerWorld serverWorld, class_4521 arg) {
		this.field_20547 = Lists.<GameTestBatch>newArrayList(collection);
		this.field_20555 = new BlockPos.Mutable(blockPos);
		this.field_20548 = blockPos;
		this.field_20549 = serverWorld;
		this.field_20550 = arg;
	}

	private void method_22163() {
		this.field_20547.forEach(gameTestBatch -> {
			Collection<class_4517> collection = Lists.<class_4517>newArrayList();

			for (TestFunction testFunction : gameTestBatch.method_22154()) {
				BlockPos blockPos = new BlockPos(this.field_20555);
				class_4525.method_22257(testFunction.method_22298(), blockPos, 2, this.field_20549);
				class_4517 lv = new class_4517(testFunction, blockPos, this.field_20549);
				collection.add(lv);
				this.field_20551.add(lv);
				this.field_20552.put(gameTestBatch.method_22152(), collection);
				BlockPos blockPos2 = lv.method_22174();
				int i = blockPos2 == null ? 1 : blockPos2.getX();
				int j = blockPos2 == null ? 1 : blockPos2.getZ();
				this.field_20556 = Math.max(this.field_20556, j);
				this.field_20555.setOffset(i + 4, 0, 0);
				if (this.field_20551.size() % 8 == 0) {
					this.field_20555.setOffset(0, 0, this.field_20556 + 5);
					this.field_20555.setX(this.field_20548.getX());
					this.field_20556 = 0;
				}
			}
		});
	}

	public List<class_4517> method_22155() {
		return this.field_20551;
	}

	public void method_22160() {
		this.method_22163();
		this.method_22156(0);
	}

	private void method_22156(int i) {
		this.field_20554 = i;
		this.field_20553 = new class_4524();
		if (i < this.field_20547.size()) {
			GameTestBatch gameTestBatch = (GameTestBatch)this.field_20547.get(this.field_20554);
			gameTestBatch.method_22153(this.field_20549);
			this.method_22157(gameTestBatch);
			String string = gameTestBatch.method_22152();
			Collection<class_4517> collection = (Collection<class_4517>)this.field_20552.get(string);
			LOGGER.info("Running test batch '" + string + "' (" + collection.size() + " tests)...");
			collection.forEach(arg -> {
				this.field_20553.method_22230(arg);
				this.field_20553.method_22231(new class_4518() {
					@Override
					public void method_22188(class_4517 arg) {
					}

					@Override
					public void method_22189(class_4517 arg) {
						class_4515.this.method_22159(arg);
					}

					@Override
					public void method_22190(class_4517 arg) {
						class_4515.this.method_22159(arg);
					}
				});
				class_4520.method_22203(arg, this.field_20550);
			});
		}
	}

	private void method_22159(class_4517 arg) {
		if (this.field_20553.method_22239()) {
			this.method_22156(this.field_20554 + 1);
		}
	}

	private void method_22157(GameTestBatch gameTestBatch) {
		Collection<class_4517> collection = (Collection<class_4517>)this.field_20552.get(gameTestBatch.method_22152());
		collection.forEach(arg -> arg.method_22170(2));
	}
}
