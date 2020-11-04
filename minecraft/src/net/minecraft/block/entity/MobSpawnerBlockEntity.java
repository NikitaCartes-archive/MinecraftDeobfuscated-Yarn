package net.minecraft.block.entity;

import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.MobSpawnerEntry;
import net.minecraft.world.MobSpawnerLogic;
import net.minecraft.world.World;

public class MobSpawnerBlockEntity extends BlockEntity {
	private final MobSpawnerLogic logic = new MobSpawnerLogic() {
		@Override
		public void sendStatus(World world, BlockPos blockPos, int i) {
			world.addSyncedBlockEvent(blockPos, Blocks.SPAWNER, i, 0);
		}

		@Override
		public void setSpawnEntry(@Nullable World world, BlockPos blockPos, MobSpawnerEntry mobSpawnerEntry) {
			super.setSpawnEntry(world, blockPos, mobSpawnerEntry);
			if (world != null) {
				BlockState blockState = world.getBlockState(blockPos);
				world.updateListeners(blockPos, blockState, blockState, 4);
			}
		}
	};

	public MobSpawnerBlockEntity(BlockPos blockPos, BlockState blockState) {
		super(BlockEntityType.MOB_SPAWNER, blockPos, blockState);
	}

	@Override
	public void fromTag(CompoundTag compoundTag) {
		super.fromTag(compoundTag);
		this.logic.fromTag(this.world, this.pos, compoundTag);
	}

	@Override
	public CompoundTag toTag(CompoundTag tag) {
		super.toTag(tag);
		this.logic.toTag(this.world, this.pos, tag);
		return tag;
	}

	public static void clientTick(World world, BlockPos blockPos, BlockState blockState, MobSpawnerBlockEntity mobSpawnerBlockEntity) {
		mobSpawnerBlockEntity.logic.method_31589(world, blockPos);
	}

	public static void serverTick(World world, BlockPos blockPos, BlockState blockState, MobSpawnerBlockEntity mobSpawnerBlockEntity) {
		mobSpawnerBlockEntity.logic.method_31588((ServerWorld)world, blockPos);
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
	public boolean onSyncedBlockEvent(int type, int data) {
		return this.logic.method_8275(this.world, type) ? true : super.onSyncedBlockEvent(type, data);
	}

	@Override
	public boolean copyItemDataRequiresOperator() {
		return true;
	}

	public MobSpawnerLogic getLogic() {
		return this.logic;
	}
}
