package net.minecraft.block.entity;

import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.network.packet.BlockEntityUpdateS2CPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sortme.MobSpawnerEntry;
import net.minecraft.sortme.MobSpawnerLogic;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MobSpawnerBlockEntity extends BlockEntity implements Tickable {
	private final MobSpawnerLogic logic = new MobSpawnerLogic() {
		@Override
		public void method_8273(int i) {
			MobSpawnerBlockEntity.this.world.method_8427(MobSpawnerBlockEntity.this.field_11867, Blocks.field_10260, i, 0);
		}

		@Override
		public World method_8271() {
			return MobSpawnerBlockEntity.this.world;
		}

		@Override
		public BlockPos method_8276() {
			return MobSpawnerBlockEntity.this.field_11867;
		}

		@Override
		public void method_8277(MobSpawnerEntry mobSpawnerEntry) {
			super.method_8277(mobSpawnerEntry);
			if (this.method_8271() != null) {
				BlockState blockState = this.method_8271().method_8320(this.method_8276());
				this.method_8271().method_8413(MobSpawnerBlockEntity.this.field_11867, blockState, blockState, 4);
			}
		}
	};

	public MobSpawnerBlockEntity() {
		super(BlockEntityType.MOB_SPAWNER);
	}

	@Override
	public void method_11014(CompoundTag compoundTag) {
		super.method_11014(compoundTag);
		this.logic.method_8280(compoundTag);
	}

	@Override
	public CompoundTag method_11007(CompoundTag compoundTag) {
		super.method_11007(compoundTag);
		this.logic.method_8272(compoundTag);
		return compoundTag;
	}

	@Override
	public void tick() {
		this.logic.update();
	}

	@Nullable
	@Override
	public BlockEntityUpdateS2CPacket method_16886() {
		return new BlockEntityUpdateS2CPacket(this.field_11867, 1, this.method_16887());
	}

	@Override
	public CompoundTag method_16887() {
		CompoundTag compoundTag = this.method_11007(new CompoundTag());
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
