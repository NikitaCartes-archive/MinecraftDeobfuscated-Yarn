package net.minecraft.structure;

import com.google.common.collect.Lists;
import com.mojang.serialization.Dynamic;
import java.util.List;
import java.util.Random;
import net.fabricmc.yarn.constants.NbtTypeIds;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.pool.EmptyPoolElement;
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
		super(StructurePieceType.JIGSAW, 0);
		this.structureManager = structureManager;
		this.poolElement = poolElement;
		this.pos = pos;
		this.groundLevelDelta = groundLevelDelta;
		this.rotation = rotation;
		this.boundingBox = boundingBox;
	}

	public PoolStructurePiece(ServerWorld serverWorld, NbtCompound tag) {
		super(StructurePieceType.JIGSAW, tag);
		this.structureManager = serverWorld.getStructureManager();
		this.pos = new BlockPos(tag.getInt("PosX"), tag.getInt("PosY"), tag.getInt("PosZ"));
		this.groundLevelDelta = tag.getInt("ground_level_delta");
		RegistryOps<NbtElement> registryOps = RegistryOps.of(NbtOps.INSTANCE, serverWorld.getServer().method_34864(), serverWorld.getServer().getRegistryManager());
		this.poolElement = (StructurePoolElement)StructurePoolElement.CODEC
			.parse(registryOps, tag.getCompound("pool_element"))
			.resultOrPartial(LOGGER::error)
			.orElse(EmptyPoolElement.INSTANCE);
		this.rotation = BlockRotation.valueOf(tag.getString("rotation"));
		this.boundingBox = this.poolElement.getBoundingBox(this.structureManager, this.pos, this.rotation);
		NbtList nbtList = tag.getList("junctions", NbtTypeIds.COMPOUND);
		this.junctions.clear();
		nbtList.forEach(nbtElement -> this.junctions.add(JigsawJunction.method_28873(new Dynamic<>(registryOps, nbtElement))));
	}

	@Override
	protected void writeNbt(ServerWorld world, NbtCompound nbt) {
		nbt.putInt("PosX", this.pos.getX());
		nbt.putInt("PosY", this.pos.getY());
		nbt.putInt("PosZ", this.pos.getZ());
		nbt.putInt("ground_level_delta", this.groundLevelDelta);
		RegistryReadingOps<NbtElement> registryReadingOps = RegistryReadingOps.of(NbtOps.INSTANCE, world.getServer().getRegistryManager());
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
	public boolean generate(
		StructureWorldAccess world,
		StructureAccessor structureAccessor,
		ChunkGenerator chunkGenerator,
		Random random,
		BlockBox boundingBox,
		ChunkPos chunkPos,
		BlockPos pos
	) {
		return this.generate(world, structureAccessor, chunkGenerator, random, boundingBox, pos, false);
	}

	public boolean generate(
		StructureWorldAccess world,
		StructureAccessor structureAccessor,
		ChunkGenerator chunkGenerator,
		Random random,
		BlockBox boundingBox,
		BlockPos pos,
		boolean keepJigsaws
	) {
		return this.poolElement
			.generate(this.structureManager, world, structureAccessor, chunkGenerator, this.pos, pos, this.rotation, boundingBox, random, keepJigsaws);
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
