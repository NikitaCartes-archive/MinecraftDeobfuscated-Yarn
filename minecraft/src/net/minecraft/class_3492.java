package net.minecraft;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.sortme.structures.processor.AbstractStructureProcessor;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.world.gen.ChunkRandom;

public class class_3492 {
	private Mirror field_15564 = Mirror.NONE;
	private Rotation field_15569 = Rotation.ROT_0;
	private BlockPos field_15566 = BlockPos.ORIGIN;
	private boolean field_15571;
	@Nullable
	private ChunkPos field_15563;
	@Nullable
	private MutableIntBoundingBox field_15565;
	private boolean field_15567 = true;
	@Nullable
	private Random field_15570;
	@Nullable
	private Long field_15574;
	@Nullable
	private Integer field_15572;
	private int field_15575;
	private final List<AbstractStructureProcessor> field_16446 = Lists.<AbstractStructureProcessor>newArrayList();
	private boolean field_16587;

	public class_3492 method_15128() {
		class_3492 lv = new class_3492();
		lv.field_15564 = this.field_15564;
		lv.field_15569 = this.field_15569;
		lv.field_15566 = this.field_15566;
		lv.field_15571 = this.field_15571;
		lv.field_15563 = this.field_15563;
		lv.field_15565 = this.field_15565;
		lv.field_15567 = this.field_15567;
		lv.field_15570 = this.field_15570;
		lv.field_15574 = this.field_15574;
		lv.field_15572 = this.field_15572;
		lv.field_15575 = this.field_15575;
		lv.field_16446.addAll(this.field_16446);
		lv.field_16587 = this.field_16587;
		return lv;
	}

	public class_3492 method_15125(Mirror mirror) {
		this.field_15564 = mirror;
		return this;
	}

	public class_3492 method_15123(Rotation rotation) {
		this.field_15569 = rotation;
		return this;
	}

	public class_3492 method_15119(BlockPos blockPos) {
		this.field_15566 = blockPos;
		return this;
	}

	public class_3492 method_15133(boolean bl) {
		this.field_15571 = bl;
		return this;
	}

	public class_3492 method_15130(ChunkPos chunkPos) {
		this.field_15563 = chunkPos;
		return this;
	}

	public class_3492 method_15126(MutableIntBoundingBox mutableIntBoundingBox) {
		this.field_15565 = mutableIntBoundingBox;
		return this;
	}

	public class_3492 method_15118(@Nullable Long long_) {
		this.field_15574 = long_;
		return this;
	}

	public class_3492 method_15112(@Nullable Random random) {
		this.field_15570 = random;
		return this;
	}

	public class_3492 method_15131(boolean bl) {
		this.field_16587 = bl;
		return this;
	}

	public class_3492 method_16183() {
		this.field_16446.clear();
		return this;
	}

	public class_3492 method_16184(AbstractStructureProcessor abstractStructureProcessor) {
		this.field_16446.add(abstractStructureProcessor);
		return this;
	}

	public class_3492 method_16664(AbstractStructureProcessor abstractStructureProcessor) {
		this.field_16446.remove(abstractStructureProcessor);
		return this;
	}

	public Mirror method_15114() {
		return this.field_15564;
	}

	public Rotation method_15113() {
		return this.field_15569;
	}

	public BlockPos method_15134() {
		return this.field_15566;
	}

	public Random method_15115(@Nullable BlockPos blockPos) {
		if (this.field_15570 != null) {
			return this.field_15570;
		} else if (this.field_15574 != null) {
			return this.field_15574 == 0L ? new Random(SystemUtil.getMeasuringTimeMs()) : new Random(this.field_15574);
		} else {
			return blockPos == null ? new Random(SystemUtil.getMeasuringTimeMs()) : ChunkRandom.method_12662(blockPos.getX(), blockPos.getZ(), 0L, 987234911L);
		}
	}

	public boolean method_15135() {
		return this.field_15571;
	}

	@Nullable
	public MutableIntBoundingBox method_15124() {
		if (this.field_15565 == null && this.field_15563 != null) {
			this.method_15132();
		}

		return this.field_15565;
	}

	public boolean method_16444() {
		return this.field_16587;
	}

	public List<AbstractStructureProcessor> method_16182() {
		return this.field_16446;
	}

	void method_15132() {
		if (this.field_15563 != null) {
			this.field_15565 = this.method_15117(this.field_15563);
		}
	}

	public boolean method_15120() {
		return this.field_15567;
	}

	public List<class_3499.class_3501> method_15121(List<List<class_3499.class_3501>> list, @Nullable BlockPos blockPos) {
		this.field_15572 = 8;
		if (this.field_15572 != null && this.field_15572 >= 0 && this.field_15572 < list.size()) {
			return (List<class_3499.class_3501>)list.get(this.field_15572);
		} else {
			this.field_15572 = this.method_15115(blockPos).nextInt(list.size());
			return (List<class_3499.class_3501>)list.get(this.field_15572);
		}
	}

	@Nullable
	private MutableIntBoundingBox method_15117(@Nullable ChunkPos chunkPos) {
		if (chunkPos == null) {
			return this.field_15565;
		} else {
			int i = chunkPos.x * 16;
			int j = chunkPos.z * 16;
			return new MutableIntBoundingBox(i, 0, j, i + 16 - 1, 255, j + 16 - 1);
		}
	}
}
