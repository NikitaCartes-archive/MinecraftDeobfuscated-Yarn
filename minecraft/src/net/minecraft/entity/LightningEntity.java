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
		super(EntityType.LIGHTNING_BOLT, world);
		this.ignoreCameraFrustum = true;
		this.setPositionAndAngles(d, e, f, 0.0F, 0.0F);
		this.ambientTick = 2;
		this.seed = this.random.nextLong();
		this.remainingActions = this.random.nextInt(3) + 1;
		this.cosmetic = bl;
		Difficulty difficulty = world.getDifficulty();
		if (difficulty == Difficulty.NORMAL || difficulty == Difficulty.HARD) {
			this.spawnFire(4);
		}
	}

	@Override
	public SoundCategory getSoundCategory() {
		return SoundCategory.WEATHER;
	}

	public void setChanneller(@Nullable ServerPlayerEntity serverPlayerEntity) {
		this.channeller = serverPlayerEntity;
	}

	@Override
	public void tick() {
		super.tick();
		if (this.ambientTick == 2) {
			this.world
				.playSound(
					null,
					this.getX(),
					this.getY(),
					this.getZ(),
					SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER,
					SoundCategory.WEATHER,
					10000.0F,
					0.8F + this.random.nextFloat() * 0.2F
				);
			this.world
				.playSound(
					null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_LIGHTNING_BOLT_IMPACT, SoundCategory.WEATHER, 2.0F, 0.5F + this.random.nextFloat() * 0.2F
				);
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
			if (this.world.isClient) {
				this.world.setTicksSinceLightning(2);
			} else if (!this.cosmetic) {
				double d = 3.0;
				List<Entity> list = this.world
					.getEntities(
						this, new Box(this.getX() - 3.0, this.getY() - 3.0, this.getZ() - 3.0, this.getX() + 3.0, this.getY() + 6.0 + 3.0, this.getZ() + 3.0), Entity::isAlive
					);

				for (Entity entity : list) {
					entity.onStruckByLightning(this);
				}

				if (this.channeller != null) {
					Criterions.CHANNELED_LIGHTNING.trigger(this.channeller, list);
				}
			}
		}
	}

	private void spawnFire(int i) {
		if (!this.cosmetic && !this.world.isClient && this.world.getGameRules().getBoolean(GameRules.DO_FIRE_TICK)) {
			BlockState blockState = Blocks.FIRE.getDefaultState();
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
