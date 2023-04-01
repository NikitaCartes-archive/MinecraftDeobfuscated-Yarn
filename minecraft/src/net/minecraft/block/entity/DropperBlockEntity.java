package net.minecraft.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class DropperBlockEntity extends DispenserBlockEntity {
	private boolean field_44244 = false;

	public DropperBlockEntity(BlockPos blockPos, BlockState blockState) {
		super(BlockEntityType.DROPPER, blockPos, blockState);
	}

	@Override
	protected Text getContainerName() {
		return Text.translatable("container.dropper");
	}

	@Override
	public void readNbt(NbtCompound nbt) {
		super.readNbt(nbt);
		if (nbt.contains("Lunar", NbtElement.BYTE_TYPE)) {
			this.field_44244 = true;
		}
	}

	public void method_50888() {
		this.field_44244 = true;
	}

	public boolean method_50889() {
		return this.field_44244;
	}

	@Override
	protected void writeNbt(NbtCompound nbt) {
		super.writeNbt(nbt);
		if (this.field_44244) {
			nbt.putBoolean("Lunar", true);
		}
	}
}
