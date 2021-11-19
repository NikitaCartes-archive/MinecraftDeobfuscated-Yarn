package net.minecraft.structure;

import com.google.common.collect.Lists;
import com.mojang.serialization.Dynamic;
import java.util.List;
import java.util.Random;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtOps;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.dynamic.RegistryOps;
import net.minecraft.util.dynamic.RegistryReadingOps;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PoolStructurePiece extends StructurePiece {
	private static final Logger LOGGER = LogManager.getLogger();
	protected final StructurePoolElement poolElement;
	protected BlockPos pos;
	private final int groundLevelDelta;
	protected final BlockRotation rotation;
	private final List<JigsawJunction> junctions = Lists.<JigsawJunction>newArrayList();
	private final StructureManager structureManager;

	public PoolStructurePiece(
		StructureManager structureManager, StructurePoolElement poolElement, BlockPos pos, int groundLevelDelta, BlockRotation rotation, BlockBox boundingBox
	) {
		super(StructurePieceType.JIGSAW, 0, boundingBox);
		this.structureManager = structureManager;
		this.poolElement = poolElement;
		this.pos = pos;
		this.groundLevelDelta = groundLevelDelta;
		this.rotation = rotation;
	}

	public PoolStructurePiece(StructureContext context, NbtCompound nbt) {
		super(StructurePieceType.JIGSAW, nbt);
		this.structureManager = context.structureManager();
		this.pos = new BlockPos(nbt.getInt("PosX"), nbt.getInt("PosY"), nbt.getInt("PosZ"));
		this.groundLevelDelta = nbt.getInt("ground_level_delta");
		RegistryOps<NbtElement> registryOps = RegistryOps.of(NbtOps.INSTANCE, context.resourceManager(), context.registryManager());
		this.poolElement = (StructurePoolElement)StructurePoolElement.CODEC
			.parse(registryOps, nbt.getCompound("pool_element"))
			.resultOrPartial(LOGGER::error)
			.orElseThrow(() -> new IllegalStateException("Invalid pool element found"));
		this.rotation = BlockRotation.valueOf(nbt.getString("rotation"));
		this.boundingBox = this.poolElement.getBoundingBox(this.structureManager, this.pos, this.rotation);
		NbtList nbtList = nbt.getList("junctions", NbtElement.COMPOUND_TYPE);
		this.junctions.clear();
		nbtList.forEach(nbtElement -> this.junctions.add(JigsawJunction.deserialize(new Dynamic<>(registryOps, nbtElement))));
	}

	@Override
	protected void writeNbt(StructureContext context, NbtCompound nbt) {
		nbt.putInt("PosX", this.pos.getX());
		nbt.putInt("PosY", this.pos.getY());
		nbt.putInt("PosZ", this.pos.getZ());
		nbt.putInt("ground_level_delta", this.groundLevelDelta);
		RegistryReadingOps<NbtElement> registryReadingOps = RegistryReadingOps.of(NbtOps.INSTANCE, context.registryManager());
		StructurePoolElement.CODEC
			.encodeStart(registryReadingOps, this.poolElement)
			.resultOrPartial(LOGGER::error)
			.ifPresent(nbtElement -> nbt.put("pool_element", nbtElement));
		nbt.putString("rotation", this.rotation.name());
		NbtList nbtList = new NbtList();

		for (JigsawJunction jigsawJunction : this.junctions) {
			nbtList.add(jigsawJunction.serialize(registryReadingOps).getValue());
		}

		nbt.put("junctions", nbtList);
	}

	@Override
	public void generate(
		StructureWorldAccess world,
		StructureAccessor structureAccessor,
		ChunkGenerator chunkGenerator,
		Random random,
		BlockBox chunkBox,
		ChunkPos chunkPos,
		BlockPos pos
	) {
		this.generate(world, structureAccessor, chunkGenerator, random, chunkBox, pos, false);
	}

	public void generate(
		StructureWorldAccess world,
		StructureAccessor structureAccessor,
		ChunkGenerator chunkGenerator,
		Random random,
		BlockBox boundingBox,
		BlockPos pos,
		boolean keepJigsaws
	) {
		this.poolElement.generate(this.structureManager, world, structureAccessor, chunkGenerator, this.pos, pos, this.rotation, boundingBox, random, keepJigsaws);
	}

	@Override
	public void translate(int x, int y, int z) {
		super.translate(x, y, z);
		this.pos = this.pos.add(x, y, z);
	}

	@Override
	public BlockRotation getRotation() {
		return this.rotation;
	}

	public String toString() {
		return String.format("<%s | %s | %s | %s>", this.getClass().getSimpleName(), this.pos, this.rotation, this.poolElement);
	}

	public StructurePoolElement getPoolElement() {
		return this.poolElement;
	}

	public BlockPos getPos() {
		return this.pos;
	}

	public int getGroundLevelDelta() {
		return this.groundLevelDelta;
	}

	public void addJunction(JigsawJunction junction) {
		this.junctions.add(junction);
	}

	public List<JigsawJunction> getJunctions() {
		return this.junctions;
	}
}
