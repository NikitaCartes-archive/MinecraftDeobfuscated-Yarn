package net.minecraft;

import com.google.common.annotations.VisibleForTesting;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.BlockPositionSource;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.PositionSource;
import net.minecraft.world.event.listener.GameEventListener;

public class class_7338 extends BlockEntity implements GameEventListener {
	private final BlockPositionSource field_38607 = new BlockPositionSource(this.pos);
	private final class_7334 field_38608 = new class_7334();

	public class_7338(BlockPos blockPos, BlockState blockState) {
		super(BlockEntityType.SCULK_CATALYST, blockPos, blockState);
	}

	@Override
	public PositionSource getPositionSource() {
		return this.field_38607;
	}

	@Override
	public int getRange() {
		return 8;
	}

	@Override
	public boolean listen(World world, GameEvent event, @Nullable Entity entity, BlockPos pos) {
		if (!world.isClient() && event == GameEvent.ENTITY_DYING && entity instanceof LivingEntity livingEntity) {
			if (!livingEntity.method_42798()) {
				this.field_38608.method_42930(pos, livingEntity.getXpToDrop());
				livingEntity.method_42797();
				class_7333.method_42925((ServerWorld)world, this.pos, this.getCachedState(), world.getRandom());
			}

			return true;
		} else {
			return false;
		}
	}

	public static void method_42957(World world, BlockPos blockPos, BlockState blockState, class_7338 arg) {
		arg.field_38608.method_42927(world, blockPos, world.getRandom());
	}

	@Override
	public void readNbt(NbtCompound nbt) {
		super.readNbt(nbt);
		this.field_38608.method_42931(nbt);
	}

	@Override
	protected void writeNbt(NbtCompound nbt) {
		this.field_38608.method_42933(nbt);
		super.writeNbt(nbt);
	}

	@VisibleForTesting
	public class_7334 method_42959() {
		return this.field_38608;
	}
}
