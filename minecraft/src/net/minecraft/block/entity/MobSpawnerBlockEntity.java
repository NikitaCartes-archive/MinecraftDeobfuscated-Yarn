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
		public void sendStatus(World world, BlockPos pos, int i) {
			world.addSyncedBlockEvent(pos, Blocks.SPAWNER, i, 0);
		}

		@Override
		public void setSpawnEntry(@Nullable World world, BlockPos pos, MobSpawnerEntry spawnEntry) {
			super.setSpawnEntry(world, pos, spawnEntry);
			if (world != null) {
				BlockState blockState = world.getBlockState(pos);
				world.updateListeners(pos, blockState, blockState, 4);
			}
		}
	};

	public MobSpawnerBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityType.MOB_SPAWNER, pos, state);
	}

	@Override
	public void readNbt(CompoundTag tag) {
		super.readNbt(tag);
		this.logic.readNbt(this.world, this.pos, tag);
	}

	@Override
	public CompoundTag writeNbt(CompoundTag tag) {
		super.writeNbt(tag);
		this.logic.writeNbt(this.world, this.pos, tag);
		return tag;
	}

	public static void clientTick(World world, BlockPos pos, BlockState state, MobSpawnerBlockEntity blockEntity) {
		blockEntity.logic.clientTick(world, pos);
	}

	public static void serverTick(World world, BlockPos pos, BlockState state, MobSpawnerBlockEntity blockEntity) {
		blockEntity.logic.serverTick((ServerWorld)world, pos);
	}

	@Nullable
	@Override
	public BlockEntityUpdateS2CPacket toUpdatePacket() {
		return new BlockEntityUpdateS2CPacket(this.pos, 1, this.toInitialChunkDataNbt());
	}

	@Override
	public CompoundTag toInitialChunkDataNbt() {
		CompoundTag compoundTag = this.writeNbt(new CompoundTag());
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
