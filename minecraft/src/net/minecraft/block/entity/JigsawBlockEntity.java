package net.minecraft.block.entity;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.packet.BlockEntityUpdateS2CPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;

public class JigsawBlockEntity extends BlockEntity {
	private Identifier field_16550 = new Identifier("empty");
	private Identifier field_16552 = new Identifier("empty");
	private String finalState = "minecraft:air";

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
	public String getFinalState() {
		return this.finalState;
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
	public void setFinalState(String string) {
		this.finalState = string;
	}

	@Override
	public CompoundTag method_11007(CompoundTag compoundTag) {
		super.method_11007(compoundTag);
		compoundTag.putString("attachement_type", this.field_16550.toString());
		compoundTag.putString("target_pool", this.field_16552.toString());
		compoundTag.putString("final_state", this.finalState);
		return compoundTag;
	}

	@Override
	public void method_11014(CompoundTag compoundTag) {
		super.method_11014(compoundTag);
		this.field_16550 = new Identifier(compoundTag.getString("attachement_type"));
		this.field_16552 = new Identifier(compoundTag.getString("target_pool"));
		this.finalState = compoundTag.getString("final_state");
	}

	@Nullable
	@Override
	public BlockEntityUpdateS2CPacket method_16886() {
		return new BlockEntityUpdateS2CPacket(this.field_11867, 12, this.method_16887());
	}

	@Override
	public CompoundTag method_16887() {
		return this.method_11007(new CompoundTag());
	}
}
