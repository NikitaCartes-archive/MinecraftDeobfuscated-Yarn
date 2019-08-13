package net.minecraft.block.entity;

import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.network.packet.BlockEntityUpdateS2CPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.MobSpawnerEntry;
import net.minecraft.world.MobSpawnerLogic;
import net.minecraft.world.World;

public class MobSpawnerBlockEntity extends BlockEntity implements Tickable {
	private final MobSpawnerLogic logic = new MobSpawnerLogic() {
		@Override
		public void sendStatus(int i) {
			MobSpawnerBlockEntity.this.world.addBlockAction(MobSpawnerBlockEntity.this.pos, Blocks.field_10260, i, 0);
		}

		@Override
		public World getWorld() {
			return MobSpawnerBlockEntity.this.world;
		}

		@Override
		public BlockPos getPos() {
			return MobSpawnerBlockEntity.this.pos;
		}

		@Override
		public void setSpawnEntry(MobSpawnerEntry mobSpawnerEntry) {
			super.setSpawnEntry(mobSpawnerEntry);
			if (this.getWorld() != null) {
				BlockState blockState = this.getWorld().getBlockState(this.getPos());
				this.getWorld().updateListeners(MobSpawnerBlockEntity.this.pos, blockState, blockState, 4);
			}
		}
	};

	public MobSpawnerBlockEntity() {
		super(BlockEntityType.field_11889);
	}

	@Override
	public void fromTag(CompoundTag compoundTag) {
		super.fromTag(compoundTag);
		this.logic.deserialize(compoundTag);
	}

	@Override
	public CompoundTag toTag(CompoundTag compoundTag) {
		super.toTag(compoundTag);
		this.logic.serialize(compoundTag);
		return compoundTag;
	}

	@Override
	public void tick() {
		this.logic.update();
	}

	@Nullable
	@Override
	public BlockEntityUpdateS2CPacket toUpdatePacket() {
		return new BlockEntityUpdateS2CPacket(this.pos, 1, this.toInitialChunkDataTag());
	}

	@Override
	public CompoundTag toInitialChunkDataTag() {
		CompoundTag compoundTag = this.toTag(new CompoundTag());
		compoundTag.remove("SpawnPotentials");
		return compoundTag;
	}

	@Override
	public boolean onBlockAction(int i, int j) {
		return this.logic.method_8275(i) ? true : super.onBlockAction(i, j);
	}

	@Override
	public boolean shouldNotCopyTagFromItem() {
		return true;
	}

	public MobSpawnerLogic getLogic() {
		return this.logic;
	}
}
