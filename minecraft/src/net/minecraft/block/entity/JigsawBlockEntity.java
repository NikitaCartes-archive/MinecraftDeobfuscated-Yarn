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
	private Identifier attachmentType = new Identifier("empty");
	private Identifier targetPool = new Identifier("empty");
	private Identifier field_23327 = new Identifier("empty");
	private JigsawBlockEntity.class_4991 field_23328 = JigsawBlockEntity.class_4991.field_23329;
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
	public Identifier method_26399() {
		return this.targetPool;
	}

	@Environment(EnvType.CLIENT)
	public Identifier getTargetPool() {
		return this.field_23327;
	}

	@Environment(EnvType.CLIENT)
	public String getFinalState() {
		return this.finalState;
	}

	@Environment(EnvType.CLIENT)
	public JigsawBlockEntity.class_4991 method_26400() {
		return this.field_23328;
	}

	public void setAttachmentType(Identifier value) {
		this.attachmentType = value;
	}

	public void setTargetPool(Identifier value) {
		this.targetPool = value;
	}

	public void method_26398(Identifier identifier) {
		this.field_23327 = identifier;
	}

	public void setFinalState(String value) {
		this.finalState = value;
	}

	public void method_26396(JigsawBlockEntity.class_4991 arg) {
		this.field_23328 = arg;
	}

	@Override
	public CompoundTag toTag(CompoundTag tag) {
		super.toTag(tag);
		tag.putString("name", this.attachmentType.toString());
		tag.putString("target", this.targetPool.toString());
		tag.putString("pool", this.field_23327.toString());
		tag.putString("final_state", this.finalState);
		tag.putString("joint", this.field_23328.asString());
		return tag;
	}

	@Override
	public void fromTag(BlockState blockState, CompoundTag compoundTag) {
		super.fromTag(blockState, compoundTag);
		this.attachmentType = new Identifier(compoundTag.getString("name"));
		this.targetPool = new Identifier(compoundTag.getString("target"));
		this.field_23327 = new Identifier(compoundTag.getString("pool"));
		this.finalState = compoundTag.getString("final_state");
		this.field_23328 = (JigsawBlockEntity.class_4991)JigsawBlockEntity.class_4991.method_26401(compoundTag.getString("joint"))
			.orElseGet(
				() -> JigsawBlock.method_26378(blockState).getAxis().isHorizontal() ? JigsawBlockEntity.class_4991.field_23330 : JigsawBlockEntity.class_4991.field_23329
			);
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

	public static enum class_4991 implements StringIdentifiable {
		field_23329("rollable"),
		field_23330("aligned");

		private final String field_23331;

		private class_4991(String string2) {
			this.field_23331 = string2;
		}

		@Override
		public String asString() {
			return this.field_23331;
		}

		public static Optional<JigsawBlockEntity.class_4991> method_26401(String string) {
			return Arrays.stream(values()).filter(arg -> arg.asString().equals(string)).findFirst();
		}
	}
}
