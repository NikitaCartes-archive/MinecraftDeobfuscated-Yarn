package net.minecraft.structure;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;

public class StructurePlacementData {
	private BlockMirror mirror = BlockMirror.NONE;
	private BlockRotation rotation = BlockRotation.NONE;
	private BlockPos position = BlockPos.ORIGIN;
	private boolean ignoreEntities;
	@Nullable
	private ChunkPos chunkPosition;
	@Nullable
	private BlockBox boundingBox;
	private boolean placeFluids = true;
	@Nullable
	private Random random;
	@Nullable
	private Integer field_15572;
	private int field_15575;
	private final List<StructureProcessor> processors = Lists.<StructureProcessor>newArrayList();
	private boolean field_16587;

	public StructurePlacementData copy() {
		StructurePlacementData structurePlacementData = new StructurePlacementData();
		structurePlacementData.mirror = this.mirror;
		structurePlacementData.rotation = this.rotation;
		structurePlacementData.position = this.position;
		structurePlacementData.ignoreEntities = this.ignoreEntities;
		structurePlacementData.chunkPosition = this.chunkPosition;
		structurePlacementData.boundingBox = this.boundingBox;
		structurePlacementData.placeFluids = this.placeFluids;
		structurePlacementData.random = this.random;
		structurePlacementData.field_15572 = this.field_15572;
		structurePlacementData.field_15575 = this.field_15575;
		structurePlacementData.processors.addAll(this.processors);
		structurePlacementData.field_16587 = this.field_16587;
		return structurePlacementData;
	}

	public StructurePlacementData setMirrored(BlockMirror blockMirror) {
		this.mirror = blockMirror;
		return this;
	}

	public StructurePlacementData setRotation(BlockRotation blockRotation) {
		this.rotation = blockRotation;
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

	public StructurePlacementData setBoundingBox(BlockBox blockBox) {
		this.boundingBox = blockBox;
		return this;
	}

	public StructurePlacementData setRandom(@Nullable Random random) {
		this.random = random;
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

	public StructurePlacementData addProcessor(StructureProcessor structureProcessor) {
		this.processors.add(structureProcessor);
		return this;
	}

	public StructurePlacementData removeProcessor(StructureProcessor structureProcessor) {
		this.processors.remove(structureProcessor);
		return this;
	}

	public BlockMirror getMirror() {
		return this.mirror;
	}

	public BlockRotation getRotation() {
		return this.rotation;
	}

	public BlockPos getPosition() {
		return this.position;
	}

	public Random getRandom(@Nullable BlockPos blockPos) {
		if (this.random != null) {
			return this.random;
		} else {
			return blockPos == null ? new Random(SystemUtil.getMeasuringTimeMs()) : new Random(MathHelper.hashCode(blockPos));
		}
	}

	public boolean shouldIgnoreEntities() {
		return this.ignoreEntities;
	}

	@Nullable
	public BlockBox method_15124() {
		if (this.boundingBox == null && this.chunkPosition != null) {
			this.method_15132();
		}

		return this.boundingBox;
	}

	public boolean method_16444() {
		return this.field_16587;
	}

	public List<StructureProcessor> getProcessors() {
		return this.processors;
	}

	void method_15132() {
		if (this.chunkPosition != null) {
			this.boundingBox = this.method_15117(this.chunkPosition);
		}
	}

	public boolean shouldPlaceFluids() {
		return this.placeFluids;
	}

	public List<Structure.StructureBlockInfo> method_15121(List<List<Structure.StructureBlockInfo>> list, @Nullable BlockPos blockPos) {
		this.field_15572 = 8;
		if (this.field_15572 != null && this.field_15572 >= 0 && this.field_15572 < list.size()) {
			return (List<Structure.StructureBlockInfo>)list.get(this.field_15572);
		} else {
			this.field_15572 = this.getRandom(blockPos).nextInt(list.size());
			return (List<Structure.StructureBlockInfo>)list.get(this.field_15572);
		}
	}

	@Nullable
	private BlockBox method_15117(@Nullable ChunkPos chunkPos) {
		if (chunkPos == null) {
			return this.boundingBox;
		} else {
			int i = chunkPos.x * 16;
			int j = chunkPos.z * 16;
			return new BlockBox(i, 0, j, i + 16 - 1, 255, j + 16 - 1);
		}
	}
}
