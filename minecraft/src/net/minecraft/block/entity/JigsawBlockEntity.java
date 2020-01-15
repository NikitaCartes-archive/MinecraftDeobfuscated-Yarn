package net.minecraft.block.entity;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.packet.BlockEntityUpdateS2CPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;

public class JigsawBlockEntity extends BlockEntity {
	private Identifier attachmentType = new Identifier("empty");
	private Identifier targetPool = new Identifier("empty");
	private String finalState = "minecraft:air";

	public JigsawBlockEntity(BlockEntityType<?> blockEntityType) {
		super(blockEntityType);
	}

	public JigsawBlockEntity() {
		this(BlockEntityType.JIGSAW);
	}

	@Environment(EnvType.CLIENT)
	public Identifier getAttachmentType() {
		return this.attachmentType;
	}

	@Environment(EnvType.CLIENT)
	public Identifier getTargetPool() {
		return this.targetPool;
	}

	@Environment(EnvType.CLIENT)
	public String getFinalState() {
		return this.finalState;
	}

	public void setAttachmentType(Identifier value) {
		this.attachmentType = value;
	}

	public void setTargetPool(Identifier value) {
		this.targetPool = value;
	}

	public void setFinalState(String value) {
		this.finalState = value;
	}

	@Override
	public CompoundTag toTag(CompoundTag tag) {
		super.toTag(tag);
		tag.putString("attachement_type", this.attachmentType.toString());
		tag.putString("target_pool", this.targetPool.toString());
		tag.putString("final_state", this.finalState);
		return tag;
	}

	@Override
	public void fromTag(CompoundTag tag) {
		super.fromTag(tag);
		this.attachmentType = new Identifier(tag.getString("attachement_type"));
		this.targetPool = new Identifier(tag.getString("target_pool"));
		this.finalState = tag.getString("final_state");
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
}
