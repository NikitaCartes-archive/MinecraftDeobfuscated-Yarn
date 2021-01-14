package net.minecraft.block.entity;

import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.MobSpawnerEntry;
import net.minecraft.world.MobSpawnerLogic;
import net.minecraft.world.World;

public class MobSpawnerBlockEntity extends BlockEntity implements Tickable {
	private final MobSpawnerLogic logic = new MobSpawnerLogic() {
		@Override
		public void sendStatus(int status) {
			MobSpawnerBlockEntity.this.world.addSyncedBlockEvent(MobSpawnerBlockEntity.this.pos, Blocks.SPAWNER, status, 0);
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
		public void setSpawnEntry(MobSpawnerEntry spawnEntry) {
			super.setSpawnEntry(spawnEntry);
			if (this.getWorld() != null) {
				BlockState blockState = this.getWorld().getBlockState(this.getPos());
				this.getWorld().updateListeners(MobSpawnerBlockEntity.this.pos, blockState, blockState, 4);
			}
		}
	};

	public MobSpawnerBlockEntity() {
		super(BlockEntityType.MOB_SPAWNER);
	}

	@Override
	public void fromTag(BlockState state, NbtCompound tag) {
		super.fromTag(state, tag);
		this.logic.fromTag(tag);
	}

	@Override
	public NbtCompound writeNbt(NbtCompound nbt) {
		super.writeNbt(nbt);
		this.logic.toTag(nbt);
		return nbt;
	}

	@Override
	public void tick() {
		this.logic.update();
	}

	@Nullable
	@Override
	public BlockEntityUpdateS2CPacket toUpdatePacket() {
		return new BlockEntityUpdateS2CPacket(this.pos, 1, this.toInitialChunkDataNbt());
	}

	@Override
	public NbtCompound toInitialChunkDataNbt() {
		NbtCompound nbtCompound = this.writeNbt(new NbtCompound());
		nbtCompound.remove("SpawnPotentials");
		return nbtCompound;
	}

	@Override
	public boolean onSyncedBlockEvent(int type, int data) {
		return this.logic.method_8275(type) ? true : super.onSyncedBlockEvent(type, data);
	}

	@Override
	public boolean copyItemDataRequiresOperator() {
		return true;
	}

	public MobSpawnerLogic getLogic() {
		return this.logic;
	}
}
