package net.minecraft.entity;

import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.network.packet.EntitySpawnGlobalS2CPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

public class LightningEntity extends Entity {
	private int ambientTick;
	public long seed;
	private int remainingActions;
	private final boolean cosmetic;
	@Nullable
	private ServerPlayerEntity channeller;

	public LightningEntity(World world, double d, double e, double f, boolean bl) {
		super(EntityType.field_6112, world);
		this.ignoreCameraFrustum = true;
		this.setPositionAndAngles(d, e, f, 0.0F, 0.0F);
		this.ambientTick = 2;
		this.seed = this.random.nextLong();
		this.remainingActions = this.random.nextInt(3) + 1;
		this.cosmetic = bl;
		Difficulty difficulty = world.getDifficulty();
		if (difficulty == Difficulty.field_5802 || difficulty == Difficulty.field_5807) {
			this.spawnFire(4);
		}
	}

	@Override
	public SoundCategory getSoundCategory() {
		return SoundCategory.field_15252;
	}

	public void setChanneller(@Nullable ServerPlayerEntity serverPlayerEntity) {
		this.channeller = serverPlayerEntity;
	}

	@Override
	public void tick() {
		super.tick();
		if (this.ambientTick == 2) {
			this.field_6002.playSound(null, this.x, this.y, this.z, SoundEvents.field_14865, SoundCategory.field_15252, 10000.0F, 0.8F + this.random.nextFloat() * 0.2F);
			this.field_6002.playSound(null, this.x, this.y, this.z, SoundEvents.field_14956, SoundCategory.field_15252, 2.0F, 0.5F + this.random.nextFloat() * 0.2F);
		}

		this.ambientTick--;
		if (this.ambientTick < 0) {
			if (this.remainingActions == 0) {
				this.remove();
			} else if (this.ambientTick < -this.random.nextInt(10)) {
				this.remainingActions--;
				this.ambientTick = 1;
				this.seed = this.random.nextLong();
				this.spawnFire(0);
			}
		}

		if (this.ambientTick >= 0) {
			if (this.field_6002.isClient) {
				this.field_6002.setTicksSinceLightning(2);
			} else if (!this.cosmetic) {
				double d = 3.0;
				List<Entity> list = this.field_6002
					.method_8333(this, new Box(this.x - 3.0, this.y - 3.0, this.z - 3.0, this.x + 3.0, this.y + 6.0 + 3.0, this.z + 3.0), Entity::isAlive);

				for (Entity entity : list) {
					entity.onStruckByLightning(this);
				}

				if (this.channeller != null) {
					Criterions.CHANNELED_LIGHTNING.handle(this.channeller, list);
				}
			}
		}
	}

	private void spawnFire(int i) {
		if (!this.cosmetic && !this.field_6002.isClient && this.field_6002.getGameRules().getBoolean(GameRules.field_19387)) {
			BlockState blockState = Blocks.field_10036.method_9564();
			BlockPos blockPos = new BlockPos(this);
			if (this.field_6002.method_8320(blockPos).isAir() && blockState.canPlaceAt(this.field_6002, blockPos)) {
				this.field_6002.method_8501(blockPos, blockState);
			}

			for (int j = 0; j < i; j++) {
				BlockPos blockPos2 = blockPos.add(this.random.nextInt(3) - 1, this.random.nextInt(3) - 1, this.random.nextInt(3) - 1);
				if (this.field_6002.method_8320(blockPos2).isAir() && blockState.canPlaceAt(this.field_6002, blockPos2)) {
					this.field_6002.method_8501(blockPos2, blockState);
				}
			}
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean shouldRenderAtDistance(double d) {
		double e = 64.0 * getRenderDistanceMultiplier();
		return d < e * e;
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

	@Override
	public Packet<?> createSpawnPacket() {
		return new EntitySpawnGlobalS2CPacket(this);
	}
}
