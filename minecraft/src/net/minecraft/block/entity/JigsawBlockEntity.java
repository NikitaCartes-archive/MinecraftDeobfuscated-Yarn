package net.minecraft.block.entity;

import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.JigsawBlock;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.PoolStructurePiece;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.pool.SinglePoolElement;
import net.minecraft.structure.pool.StructurePoolBasedGenerator;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;

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

	public void setAttachmentType(Identifier value) {
		this.name = value;
	}

	public void setTargetPool(Identifier target) {
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
		Random random = world.getRandom();
		BlockPos blockPos = this.getPos();
		List<PoolStructurePiece> list = Lists.<PoolStructurePiece>newArrayList();
		Structure structure = new Structure();
		structure.saveFromWorld(world, blockPos, new Vec3i(1, 1, 1), false, null);
		StructurePoolElement structurePoolElement = new SinglePoolElement(structure);
		PoolStructurePiece poolStructurePiece = new PoolStructurePiece(
			structureManager, structurePoolElement, blockPos, 1, BlockRotation.NONE, new BlockBox(blockPos)
		);
		StructurePoolBasedGenerator.generate(
			world.getRegistryManager(), poolStructurePiece, maxDepth, PoolStructurePiece::new, chunkGenerator, structureManager, list, random, world
		);

		for (PoolStructurePiece poolStructurePiece2 : list) {
			poolStructurePiece2.generate(world, structureAccessor, chunkGenerator, random, BlockBox.infinite(), blockPos, keepJigsaws);
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
			return new TranslatableText("jigsaw_block.joint." + this.name);
		}
	}
}
