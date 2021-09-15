package net.minecraft.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.Clearable;
import net.minecraft.util.math.BlockPos;

public class JukeboxBlockEntity extends BlockEntity implements Clearable {
	private ItemStack record = ItemStack.EMPTY;

	public JukeboxBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityType.JUKEBOX, pos, state);
	}

	@Override
	public void readNbt(NbtCompound nbt) {
		super.readNbt(nbt);
		if (nbt.contains("RecordItem", NbtElement.COMPOUND_TYPE)) {
			this.setRecord(ItemStack.fromNbt(nbt.getCompound("RecordItem")));
		}
	}

	@Override
	protected void writeNbt(NbtCompound nbt) {
		super.writeNbt(nbt);
		if (!this.getRecord().isEmpty()) {
			nbt.put("RecordItem", this.getRecord().writeNbt(new NbtCompound()));
		}
	}

	public ItemStack getRecord() {
		return this.record;
	}

	public void setRecord(ItemStack stack) {
		this.record = stack;
		this.markDirty();
	}

	@Override
	public void clear() {
		this.setRecord(ItemStack.EMPTY);
	}
}
