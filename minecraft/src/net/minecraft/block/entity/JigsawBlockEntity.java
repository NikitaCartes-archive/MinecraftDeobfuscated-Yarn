package net.minecraft.block.entity;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.packet.BlockEntityUpdateClientPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;

public class JigsawBlockEntity extends BlockEntity {
	private Identifier field_16550 = new Identifier("empty");
	private Identifier field_16552 = new Identifier("empty");
	private String field_16551 = "minecraft:air";

	public JigsawBlockEntity(BlockEntityType<?> blockEntityType) {
		super(blockEntityType);
	}

	public JigsawBlockEntity() {
		this(BlockEntityType.JIGSAW);
	}

	@Environment(EnvType.CLIENT)
	public Identifier method_16381() {
		return this.field_16550;
	}

	@Environment(EnvType.CLIENT)
	public Identifier method_16382() {
		return this.field_16552;
	}

	@Environment(EnvType.CLIENT)
	public String method_16380() {
		return this.field_16551;
	}

	@Environment(EnvType.CLIENT)
	public void method_16379(Identifier identifier) {
		this.field_16550 = identifier;
	}

	@Environment(EnvType.CLIENT)
	public void method_16378(Identifier identifier) {
		this.field_16552 = identifier;
	}

	@Environment(EnvType.CLIENT)
	public void method_16377(String string) {
		this.field_16551 = string;
	}

	@Override
	public CompoundTag toTag(CompoundTag compoundTag) {
		super.toTag(compoundTag);
		compoundTag.putString("attachement_type", this.field_16550.toString());
		compoundTag.putString("target_pool", this.field_16552.toString());
		compoundTag.putString("final_state", this.field_16551);
		return compoundTag;
	}

	@Override
	public void fromTag(CompoundTag compoundTag) {
		super.fromTag(compoundTag);
		this.field_16550 = new Identifier(compoundTag.getString("attachement_type"));
		this.field_16552 = new Identifier(compoundTag.getString("target_pool"));
		this.field_16551 = compoundTag.getString("final_state");
	}

	@Nullable
	@Override
	public BlockEntityUpdateClientPacket toUpdatePacket() {
		return new BlockEntityUpdateClientPacket(this.pos, 12, this.toInitialChunkDataTag());
	}

	@Override
	public CompoundTag toInitialChunkDataTag() {
		return this.toTag(new CompoundTag());
	}
}
