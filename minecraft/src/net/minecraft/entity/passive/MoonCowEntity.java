package net.minecraft.entity.passive;

import javax.annotation.Nullable;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public class MoonCowEntity extends CowEntity {
	public MoonCowEntity(EntityType<? extends MoonCowEntity> entityType, World world) {
		super(entityType, world);
		this.moveControl = new MoonCowEntity.MoonwalkMoveControl(this);
	}

	public static boolean canSpawn(EntityType<? extends AnimalEntity> entityType, WorldAccess world, SpawnReason reason, BlockPos pos, Random random) {
		return world.getBlockState(pos.down()).isOf(Blocks.CHEESE);
	}

	@Override
	public float getPathfindingFavor(BlockPos pos, WorldView world) {
		return world.getBlockState(pos.down()).isOf(Blocks.CHEESE) ? 10.0F : super.getPathfindingFavor(pos, world);
	}

	@Override
	protected float getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions) {
		return dimensions.height * 0.95F;
	}

	@Override
	public EntityData initialize(
		ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt
	) {
		this.equipStack(EquipmentSlot.HEAD, new ItemStack(Blocks.GLASS));
		return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
	}

	@Nullable
	@Override
	public CowEntity createChild(ServerWorld serverWorld, PassiveEntity passiveEntity) {
		CowEntity cowEntity = (CowEntity)this.getType().create(serverWorld);
		cowEntity.equipStack(EquipmentSlot.HEAD, new ItemStack(Blocks.GLASS));
		return cowEntity;
	}

	static class MoonwalkMoveControl extends MoveControl {
		public MoonwalkMoveControl(MobEntity mobEntity) {
			super(mobEntity);
		}

		@Override
		public void tick() {
			MoveControl.State state = this.state;
			super.tick();
			if (state == MoveControl.State.MOVE_TO || state == MoveControl.State.JUMPING) {
				this.entity.setForwardSpeed(-this.entity.getMovementSpeed());
			}
		}

		@Override
		protected float method_50672(double d, double e) {
			return super.method_50672(d, e) - 180.0F;
		}
	}
}
