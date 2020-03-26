package net.minecraft.block.entity;

import java.util.Arrays;
import java.util.Optional;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.JigsawBlock;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;

public class JigsawBlockEntity extends BlockEntity {
	private Identifier name = new Identifier("empty");
	private Identifier target = new Identifier("empty");
	private Identifier pool = new Identifier("empty");
	private JigsawBlockEntity.Joint joint = JigsawBlockEntity.Joint.ROLLABLE;
	private String finalState = "minecraft:air";

	public JigsawBlockEntity(BlockEntityType<?> blockEntityType) {
		super(blockEntityType);
	}

	public JigsawBlockEntity() {
		this(BlockEntityType.JIGSAW);
	}

	@Environment(EnvType.CLIENT)
	public Identifier getName() {
		return this.name;
	}

	@Environment(EnvType.CLIENT)
	public Identifier getTarget() {
		return this.target;
	}

	@Environment(EnvType.CLIENT)
	public Identifier getPool() {
		return this.pool;
	}

	@Environment(EnvType.CLIENT)
	public String getFinalState() {
		return this.finalState;
	}

	@Environment(EnvType.CLIENT)
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
	public CompoundTag toTag(CompoundTag tag) {
		super.toTag(tag);
		tag.putString("name", this.name.toString());
		tag.putString("target", this.target.toString());
		tag.putString("pool", this.pool.toString());
		tag.putString("final_state", this.finalState);
		tag.putString("joint", this.joint.asString());
		return tag;
	}

	@Override
	public void fromTag(BlockState state, CompoundTag tag) {
		super.fromTag(state, tag);
		this.name = new Identifier(tag.getString("name"));
		this.target = new Identifier(tag.getString("target"));
		this.pool = new Identifier(tag.getString("pool"));
		this.finalState = tag.getString("final_state");
		this.joint = (JigsawBlockEntity.Joint)JigsawBlockEntity.Joint.byName(tag.getString("joint"))
			.orElseGet(() -> JigsawBlock.method_26378(state).getAxis().isHorizontal() ? JigsawBlockEntity.Joint.ALIGNED : JigsawBlockEntity.Joint.ROLLABLE);
	}

	@Nullable
	@Override
	public BlockEntityUpdateS2CPacket toUpdatePacket() {
		return new BlockEntityUpdateS2CPacket(this.pos, 12, this.toInitialChunkDataTag());
	}

	@Override
	public CompoundTag toInitialChunkDataTag() {
		return this.toTag(new CompoundTag());
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
	}
}
