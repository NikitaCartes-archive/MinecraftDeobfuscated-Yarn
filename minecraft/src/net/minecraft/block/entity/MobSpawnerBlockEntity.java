package net.minecraft.block.entity;

import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.MobSpawnerEntry;
import net.minecraft.world.MobSpawnerLogic;
import net.minecraft.world.World;

public class MobSpawnerBlockEntity extends BlockEntity {
	private final MobSpawnerLogic logic = new MobSpawnerLogic() {
		@Override
		public void sendStatus(World world, BlockPos pos, int status) {
			world.addSyncedBlockEvent(pos, Blocks.SPAWNER, status, 0);
		}

		@Override
		public void setSpawnEntry(@Nullable World world, BlockPos pos, MobSpawnerEntry spawnEntry) {
			super.setSpawnEntry(world, pos, spawnEntry);
			if (world != null) {
				BlockState blockState = world.getBlockState(pos);
				world.updateListeners(pos, blockState, blockState, Block.NO_REDRAW);
			}
		}
	};

	public MobSpawnerBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityType.MOB_SPAWNER, pos, state);
	}

	@Override
	public void readNbt(NbtCompound nbt) {
		super.readNbt(nbt);
		this.logic.readNbt(this.world, this.pos, nbt);
	}

	@Override
	protected void writeNbt(NbtCompound nbt) {
		super.writeNbt(nbt);
		this.logic.writeNbt(nbt);
	}

	public static void clientTick(World world, BlockPos pos, BlockState state, MobSpawnerBlockEntity blockEntity) {
		blockEntity.logic.clientTick(world, pos);
	}

	public static void serverTick(World world, BlockPos pos, BlockState state, MobSpawnerBlockEntity blockEntity) {
		blockEntity.logic.serverTick((ServerWorld)world, pos);
	}

	public BlockEntityUpdateS2CPacket toUpdatePacket() {
		return BlockEntityUpdateS2CPacket.create(this);
	}

	@Override
	public NbtCompound toInitialChunkDataNbt() {
		NbtCompound nbtCompound = this.createNbt();
		nbtCompound.remove("SpawnPotentials");
		return nbtCompound;
	}

	@Override
	public boolean onSyncedBlockEvent(int type, int data) {
		return this.logic.handleStatus(this.world, type) ? true : super.onSyncedBlockEvent(type, data);
	}

	@Override
	public boolean copyItemDataRequiresOperator() {
		return true;
	}

	public MobSpawnerLogic getLogic() {
		return this.logic;
	}
}
