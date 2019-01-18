package net.minecraft.sortme;

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

public class StructurePlacementData {
	private Mirror mirror = Mirror.NONE;
	private Rotation rotation = Rotation.ROT_0;
	private BlockPos position = BlockPos.ORIGIN;
	private boolean ignoreEntities;
	@Nullable
	private ChunkPos chunkPosition;
	@Nullable
	private MutableIntBoundingBox boundingBox;
	private boolean field_15567 = true;
	@Nullable
	private Random field_15570;
	@Nullable
	private Long field_15574;
	@Nullable
	private Integer field_15572;
	private int field_15575;
	private final List<AbstractStructureProcessor> processors = Lists.<AbstractStructureProcessor>newArrayList();
	private boolean field_16587;

	public StructurePlacementData copy() {
		StructurePlacementData structurePlacementData = new StructurePlacementData();
		structurePlacementData.mirror = this.mirror;
		structurePlacementData.rotation = this.rotation;
		structurePlacementData.position = this.position;
		structurePlacementData.ignoreEntities = this.ignoreEntities;
		structurePlacementData.chunkPosition = this.chunkPosition;
		structurePlacementData.boundingBox = this.boundingBox;
		structurePlacementData.field_15567 = this.field_15567;
		structurePlacementData.field_15570 = this.field_15570;
		structurePlacementData.field_15574 = this.field_15574;
		structurePlacementData.field_15572 = this.field_15572;
		structurePlacementData.field_15575 = this.field_15575;
		structurePlacementData.processors.addAll(this.processors);
		structurePlacementData.field_16587 = this.field_16587;
		return structurePlacementData;
	}

	public StructurePlacementData setMirrored(Mirror mirror) {
		this.mirror = mirror;
		return this;
	}

	public StructurePlacementData setRotation(Rotation rotation) {
		this.rotation = rotation;
		return this;
	}

	public StructurePlacementData setPosition(BlockPos blockPos) {
		this.position = blockPos;
		return this;
	}

	public StructurePlacementData setIgnoreEntities(boolean bl) {
		this.ignoreEntities = bl;
		return this;
	}

	public StructurePlacementData setChunkPosition(ChunkPos chunkPos) {
		this.chunkPosition = chunkPos;
		return this;
	}

	public StructurePlacementData setBoundingBox(MutableIntBoundingBox mutableIntBoundingBox) {
		this.boundingBox = mutableIntBoundingBox;
		return this;
	}

	public StructurePlacementData method_15118(@Nullable Long long_) {
		this.field_15574 = long_;
		return this;
	}

	public StructurePlacementData method_15112(@Nullable Random random) {
		this.field_15570 = random;
		return this;
	}

	public StructurePlacementData method_15131(boolean bl) {
		this.field_16587 = bl;
		return this;
	}

	public StructurePlacementData clearProcessors() {
		this.processors.clear();
		return this;
	}

	public StructurePlacementData addProcessor(AbstractStructureProcessor abstractStructureProcessor) {
		this.processors.add(abstractStructureProcessor);
		return this;
	}

	public StructurePlacementData removeProcessor(AbstractStructureProcessor abstractStructureProcessor) {
		this.processors.remove(abstractStructureProcessor);
		return this;
	}

	public Mirror getMirror() {
		return this.mirror;
	}

	public Rotation getRotation() {
		return this.rotation;
	}

	public BlockPos getPosition() {
		return this.position;
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
		return this.ignoreEntities;
	}

	@Nullable
	public MutableIntBoundingBox method_15124() {
		if (this.boundingBox == null && this.chunkPosition != null) {
			this.method_15132();
		}

		return this.boundingBox;
	}

	public boolean method_16444() {
		return this.field_16587;
	}

	public List<AbstractStructureProcessor> getProcessors() {
		return this.processors;
	}

	void method_15132() {
		if (this.chunkPosition != null) {
			this.boundingBox = this.method_15117(this.chunkPosition);
		}
	}

	public boolean method_15120() {
		return this.field_15567;
	}

	public List<Structure.StructureBlockInfo> method_15121(List<List<Structure.StructureBlockInfo>> list, @Nullable BlockPos blockPos) {
		this.field_15572 = 8;
		if (this.field_15572 != null && this.field_15572 >= 0 && this.field_15572 < list.size()) {
			return (List<Structure.StructureBlockInfo>)list.get(this.field_15572);
		} else {
			this.field_15572 = this.method_15115(blockPos).nextInt(list.size());
			return (List<Structure.StructureBlockInfo>)list.get(this.field_15572);
		}
	}

	@Nullable
	private MutableIntBoundingBox method_15117(@Nullable ChunkPos chunkPos) {
		if (chunkPos == null) {
			return this.boundingBox;
		} else {
			int i = chunkPos.x * 16;
			int j = chunkPos.z * 16;
			return new MutableIntBoundingBox(i, 0, j, i + 16 - 1, 255, j + 16 - 1);
		}
	}
}
