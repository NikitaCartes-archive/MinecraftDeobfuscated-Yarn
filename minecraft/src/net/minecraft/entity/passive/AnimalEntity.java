package net.minecraft.entity.passive;

import java.util.UUID;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;

public abstract class AnimalEntity extends PassiveEntity {
	protected Block field_6746 = Blocks.field_10219;
	private int loveTicks;
	private UUID lovingPlayer;

	protected AnimalEntity(EntityType<? extends AnimalEntity> entityType, World world) {
		super(entityType, world);
	}

	@Override
	protected void mobTick() {
		if (this.getBreedingAge() != 0) {
			this.loveTicks = 0;
		}

		super.mobTick();
	}

	@Override
	public void updateMovement() {
		super.updateMovement();
		if (this.getBreedingAge() != 0) {
			this.loveTicks = 0;
		}

		if (this.loveTicks > 0) {
			this.loveTicks--;
			if (this.loveTicks % 10 == 0) {
				double d = this.random.nextGaussian() * 0.02;
				double e = this.random.nextGaussian() * 0.02;
				double f = this.random.nextGaussian() * 0.02;
				this.field_6002
					.method_8406(
						ParticleTypes.field_11201,
						this.x + (double)(this.random.nextFloat() * this.getWidth() * 2.0F) - (double)this.getWidth(),
						this.y + 0.5 + (double)(this.random.nextFloat() * this.getHeight()),
						this.z + (double)(this.random.nextFloat() * this.getWidth() * 2.0F) - (double)this.getWidth(),
						d,
						e,
						f
					);
			}
		}
	}

	@Override
	public boolean damage(DamageSource damageSource, float f) {
		if (this.isInvulnerableTo(damageSource)) {
			return false;
		} else {
			this.loveTicks = 0;
			return super.damage(damageSource, f);
		}
	}

	@Override
	public float method_6144(BlockPos blockPos, ViewableWorld viewableWorld) {
		return viewableWorld.method_8320(blockPos.down()).getBlock() == this.field_6746 ? 10.0F : viewableWorld.method_8610(blockPos) - 0.5F;
	}

	@Override
	public void method_5652(CompoundTag compoundTag) {
		super.method_5652(compoundTag);
		compoundTag.putInt("InLove", this.loveTicks);
		if (this.lovingPlayer != null) {
			compoundTag.putUuid("LoveCause", this.lovingPlayer);
		}
	}

	@Override
	public double getHeightOffset() {
		return 0.14;
	}

	@Override
	public void method_5749(CompoundTag compoundTag) {
		super.method_5749(compoundTag);
		this.loveTicks = compoundTag.getInt("InLove");
		this.lovingPlayer = compoundTag.hasUuid("LoveCause") ? compoundTag.getUuid("LoveCause") : null;
	}

	@Override
	public boolean method_5979(IWorld iWorld, SpawnType spawnType) {
		int i = MathHelper.floor(this.x);
		int j = MathHelper.floor(this.method_5829().minY);
		int k = MathHelper.floor(this.z);
		BlockPos blockPos = new BlockPos(i, j, k);
		return iWorld.method_8320(blockPos.down()).getBlock() == this.field_6746 && iWorld.method_8624(blockPos, 0) > 8 && super.method_5979(iWorld, spawnType);
	}

	@Override
	public int getMinAmbientSoundDelay() {
		return 120;
	}

	@Override
	public boolean canImmediatelyDespawn(double d) {
		return false;
	}

	@Override
	protected int method_6110(PlayerEntity playerEntity) {
		return 1 + this.field_6002.random.nextInt(3);
	}

	public boolean method_6481(ItemStack itemStack) {
		return itemStack.getItem() == Items.field_8861;
	}

	@Override
	public boolean method_5992(PlayerEntity playerEntity, Hand hand) {
		ItemStack itemStack = playerEntity.method_5998(hand);
		if (this.method_6481(itemStack)) {
			if (this.getBreedingAge() == 0 && this.method_6482()) {
				this.method_6475(playerEntity, itemStack);
				this.method_6480(playerEntity);
				return true;
			}

			if (this.isChild()) {
				this.method_6475(playerEntity, itemStack);
				this.method_5620((int)((float)(-this.getBreedingAge() / 20) * 0.1F), true);
				return true;
			}
		}

		return super.method_5992(playerEntity, hand);
	}

	protected void method_6475(PlayerEntity playerEntity, ItemStack itemStack) {
		if (!playerEntity.abilities.creativeMode) {
			itemStack.subtractAmount(1);
		}
	}

	public boolean method_6482() {
		return this.loveTicks <= 0;
	}

	public void method_6480(@Nullable PlayerEntity playerEntity) {
		this.loveTicks = 600;
		if (playerEntity != null) {
			this.lovingPlayer = playerEntity.getUuid();
		}

		this.field_6002.summonParticle(this, (byte)18);
	}

	public void setLoveTicks(int i) {
		this.loveTicks = i;
	}

	@Nullable
	public ServerPlayerEntity method_6478() {
		if (this.lovingPlayer == null) {
			return null;
		} else {
			PlayerEntity playerEntity = this.field_6002.method_18470(this.lovingPlayer);
			return playerEntity instanceof ServerPlayerEntity ? (ServerPlayerEntity)playerEntity : null;
		}
	}

	public boolean isInLove() {
		return this.loveTicks > 0;
	}

	public void resetLoveTicks() {
		this.loveTicks = 0;
	}

	public boolean canBreedWith(AnimalEntity animalEntity) {
		if (animalEntity == this) {
			return false;
		} else {
			return animalEntity.getClass() != this.getClass() ? false : this.isInLove() && animalEntity.isInLove();
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_5711(byte b) {
		if (b == 18) {
			for (int i = 0; i < 7; i++) {
				double d = this.random.nextGaussian() * 0.02;
				double e = this.random.nextGaussian() * 0.02;
				double f = this.random.nextGaussian() * 0.02;
				this.field_6002
					.method_8406(
						ParticleTypes.field_11201,
						this.x + (double)(this.random.nextFloat() * this.getWidth() * 2.0F) - (double)this.getWidth(),
						this.y + 0.5 + (double)(this.random.nextFloat() * this.getHeight()),
						this.z + (double)(this.random.nextFloat() * this.getWidth() * 2.0F) - (double)this.getWidth(),
						d,
						e,
						f
					);
			}
		} else {
			super.method_5711(b);
		}
	}
}
