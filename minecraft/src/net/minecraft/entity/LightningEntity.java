package net.minecraft.entity;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.advancement.criterion.CriterionCriterions;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;

public class LightningEntity extends AbstractLightningEntity {
	private int field_7185;
	public long field_7186;
	private int field_7183;
	private final boolean field_7184;
	@Nullable
	private ServerPlayerEntity field_7182;

	public LightningEntity(World world, double d, double e, double f, boolean bl) {
		super(EntityType.LIGHTNING_BOLT, world);
		this.setPositionAndAngles(d, e, f, 0.0F, 0.0F);
		this.field_7185 = 2;
		this.field_7186 = this.random.nextLong();
		this.field_7183 = this.random.nextInt(3) + 1;
		this.field_7184 = bl;
		Difficulty difficulty = world.getDifficulty();
		if (difficulty == Difficulty.NORMAL || difficulty == Difficulty.HARD) {
			this.method_6960(4);
		}
	}

	@Override
	public SoundCategory getSoundCategory() {
		return SoundCategory.field_15252;
	}

	public void method_6961(@Nullable ServerPlayerEntity serverPlayerEntity) {
		this.field_7182 = serverPlayerEntity;
	}

	@Override
	public void update() {
		super.update();
		if (this.field_7185 == 2) {
			this.world.playSound(null, this.x, this.y, this.z, SoundEvents.field_14865, SoundCategory.field_15252, 10000.0F, 0.8F + this.random.nextFloat() * 0.2F);
			this.world.playSound(null, this.x, this.y, this.z, SoundEvents.field_14956, SoundCategory.field_15252, 2.0F, 0.5F + this.random.nextFloat() * 0.2F);
		}

		this.field_7185--;
		if (this.field_7185 < 0) {
			if (this.field_7183 == 0) {
				this.invalidate();
			} else if (this.field_7185 < -this.random.nextInt(10)) {
				this.field_7183--;
				this.field_7185 = 1;
				this.field_7186 = this.random.nextLong();
				this.method_6960(0);
			}
		}

		if (this.field_7185 >= 0) {
			if (this.world.isRemote) {
				this.world.setTicksSinceLightningClient(2);
			} else if (!this.field_7184) {
				double d = 3.0;
				List<Entity> list = this.world
					.getVisibleEntities(this, new BoundingBox(this.x - 3.0, this.y - 3.0, this.z - 3.0, this.x + 3.0, this.y + 6.0 + 3.0, this.z + 3.0));

				for (int i = 0; i < list.size(); i++) {
					Entity entity = (Entity)list.get(i);
					entity.onStruckByLightning(this);
				}

				if (this.field_7182 != null) {
					CriterionCriterions.CHANNELED_LIGHTNING.method_8803(this.field_7182, list);
				}
			}
		}
	}

	private void method_6960(int i) {
		if (!this.field_7184 && !this.world.isRemote && this.world.getGameRules().getBoolean("doFireTick")) {
			BlockState blockState = Blocks.field_10036.getDefaultState();
			BlockPos blockPos = new BlockPos(this);
			if (this.world.getBlockState(blockPos).isAir() && blockState.canPlaceAt(this.world, blockPos)) {
				this.world.setBlockState(blockPos, blockState);
			}

			for (int j = 0; j < i; j++) {
				BlockPos blockPos2 = blockPos.add(this.random.nextInt(3) - 1, this.random.nextInt(3) - 1, this.random.nextInt(3) - 1);
				if (this.world.getBlockState(blockPos2).isAir() && blockState.canPlaceAt(this.world, blockPos2)) {
					this.world.setBlockState(blockPos2, blockState);
				}
			}
		}
	}

	@Override
	protected void initDataTracker() {
	}

	@Override
	protected void readCustomDataFromTag(CompoundTag compoundTag) {
	}

	@Override
	protected void writeCustomDataToTag(CompoundTag compoundTag) {
	}
}
