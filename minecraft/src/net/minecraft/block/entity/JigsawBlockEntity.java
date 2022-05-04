package net.minecraft.block.entity;

import java.util.Arrays;
import java.util.Optional;
import net.minecraft.block.BlockState;
import net.minecraft.block.JigsawBlock;
import net.minecraft.block.enums.JigsawOrientation;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolBasedGenerator;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;

public class JigsawBlockEntity extends BlockEntity {
	public static final String TARGET_KEY = "target";
	public static final String POOL_KEY = "pool";
	public static final String JOINT_KEY = "joint";
	public static final String NAME_KEY = "name";
	public static final String FINAL_STATE_KEY = "final_state";
	private Identifier name = new Identifier("empty");
	private Identifier target = new Identifier("empty");
	private RegistryKey<StructurePool> pool = RegistryKey.of(Registry.STRUCTURE_POOL_KEY, new Identifier("empty"));
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

	public RegistryKey<StructurePool> getPool() {
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

	public void setPool(RegistryKey<StructurePool> pool) {
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
		this.pool = RegistryKey.of(Registry.STRUCTURE_POOL_KEY, new Identifier(nbt.getString("pool")));
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
		BlockPos blockPos = this.getPos().offset(((JigsawOrientation)this.getCachedState().get(JigsawBlock.ORIENTATION)).getFacing());
		Registry<StructurePool> registry = world.getRegistryManager().get(Registry.STRUCTURE_POOL_KEY);
		RegistryEntry<StructurePool> registryEntry = registry.entryOf(this.pool);
		StructurePoolBasedGenerator.generate(world, registryEntry, this.target, maxDepth, blockPos, keepJigsaws);
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
