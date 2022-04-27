package net.minecraft.block.entity;

import java.util.Arrays;
import java.util.Optional;
import net.minecraft.block.BlockState;
import net.minecraft.block.JigsawBlock;
import net.minecraft.block.enums.JigsawOrientation;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.PoolStructurePiece;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructurePiecesCollector;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolBasedGenerator;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.random.AbstractRandom;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.structure.StructureType;

public class JigsawBlockEntity extends BlockEntity {
	public static final String TARGET_KEY = "target";
	public static final String POOL_KEY = "pool";
	public static final String JOINT_KEY = "joint";
	public static final String NAME_KEY = "name";
	public static final String FINAL_STATE_KEY = "final_state";
	private Identifier name = new Identifier("empty");
	private Identifier target = new Identifier("empty");
	private Identifier pool = new Identifier("empty");
	private JigsawBlockEntity.Joint joint = JigsawBlockEntity.Joint.ROLLABLE;
	private String finalState = "minecraft:air";

	public JigsawBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityType.JIGSAW, pos, state);
	}

	public Identifier getName() {
		return this.name;
	}

	public Identifier getTarget() {
		return this.target;
	}

	public Identifier getPool() {
		return this.pool;
	}

	public String getFinalState() {
		return this.finalState;
	}

	public JigsawBlockEntity.Joint getJoint() {
		return this.joint;
	}

	public void setName(Identifier name) {
		this.name = name;
	}

	public void setTarget(Identifier target) {
		this.target = target;
	}

	public void setPool(Identifier pool) {
		this.pool = pool;
	}

	public void setFinalState(String finalState) {
		this.finalState = finalState;
	}

	public void setJoint(JigsawBlockEntity.Joint joint) {
		this.joint = joint;
	}

	@Override
	protected void writeNbt(NbtCompound nbt) {
		super.writeNbt(nbt);
		nbt.putString("name", this.name.toString());
		nbt.putString("target", this.target.toString());
		nbt.putString("pool", this.pool.toString());
		nbt.putString("final_state", this.finalState);
		nbt.putString("joint", this.joint.asString());
	}

	@Override
	public void readNbt(NbtCompound nbt) {
		super.readNbt(nbt);
		this.name = new Identifier(nbt.getString("name"));
		this.target = new Identifier(nbt.getString("target"));
		this.pool = new Identifier(nbt.getString("pool"));
		this.finalState = nbt.getString("final_state");
		this.joint = (JigsawBlockEntity.Joint)JigsawBlockEntity.Joint.byName(nbt.getString("joint"))
			.orElseGet(() -> JigsawBlock.getFacing(this.getCachedState()).getAxis().isHorizontal() ? JigsawBlockEntity.Joint.ALIGNED : JigsawBlockEntity.Joint.ROLLABLE);
	}

	public BlockEntityUpdateS2CPacket toUpdatePacket() {
		return BlockEntityUpdateS2CPacket.create(this);
	}

	@Override
	public NbtCompound toInitialChunkDataNbt() {
		return this.createNbt();
	}

	public void generate(ServerWorld world, int maxDepth, boolean keepJigsaws) {
		ChunkGenerator chunkGenerator = world.getChunkManager().getChunkGenerator();
		StructureManager structureManager = world.getStructureManager();
		StructureAccessor structureAccessor = world.getStructureAccessor();
		AbstractRandom abstractRandom = world.getRandom();
		Registry<StructurePool> registry = world.getRegistryManager().get(Registry.STRUCTURE_POOL_KEY);
		RegistryKey<StructurePool> registryKey = RegistryKey.of(Registry.STRUCTURE_POOL_KEY, this.pool);
		RegistryEntry<StructurePool> registryEntry = registry.entryOf(registryKey);
		BlockPos blockPos = this.getPos().offset(((JigsawOrientation)this.getCachedState().get(JigsawBlock.ORIENTATION)).getFacing());
		StructureType.Context context = new StructureType.Context(
			world.getRegistryManager(),
			chunkGenerator,
			chunkGenerator.getBiomeSource(),
			world.getChunkManager().getNoiseConfig(),
			structureManager,
			world.getSeed(),
			new ChunkPos(blockPos),
			world,
			registryEntryx -> true
		);
		Optional<StructureType.StructurePosition> optional = StructurePoolBasedGenerator.generate(
			context, registryEntry, Optional.of(this.target), maxDepth, blockPos, false, Optional.empty(), 128
		);
		if (optional.isPresent()) {
			StructurePiecesCollector structurePiecesCollector = new StructurePiecesCollector();
			((StructureType.StructurePosition)optional.get()).generator().accept(structurePiecesCollector);

			for (StructurePiece structurePiece : structurePiecesCollector.toList().pieces()) {
				if (structurePiece instanceof PoolStructurePiece poolStructurePiece) {
					poolStructurePiece.generate(world, structureAccessor, chunkGenerator, abstractRandom, BlockBox.infinite(), blockPos, keepJigsaws);
				}
			}
		}
	}

	public static enum Joint implements StringIdentifiable {
		ROLLABLE("rollable"),
		ALIGNED("aligned");

		private final String name;

		private Joint(String name) {
			this.name = name;
		}

		@Override
		public String asString() {
			return this.name;
		}

		public static Optional<JigsawBlockEntity.Joint> byName(String name) {
			return Arrays.stream(values()).filter(joint -> joint.asString().equals(name)).findFirst();
		}

		public Text asText() {
			return Text.translatable("jigsaw_block.joint." + this.name);
		}
	}
}
