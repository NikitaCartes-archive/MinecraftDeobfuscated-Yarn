package net.minecraft.entity.passive;

import java.util.UUID;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_3730;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.sortme.Living;
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

public abstract class AnimalEntity extends PassiveEntity implements Living {
	protected Block spawningGround = Blocks.field_10219;
	private int field_6745;
	private UUID field_6744;

	protected AnimalEntity(EntityType<?> entityType, World world) {
		super(entityType, world);
	}

	@Override
	protected void mobTick() {
		if (this.getBreedingAge() != 0) {
			this.field_6745 = 0;
		}

		super.mobTick();
	}

	@Override
	public void updateMovement() {
		super.updateMovement();
		if (this.getBreedingAge() != 0) {
			this.field_6745 = 0;
		}

		if (this.field_6745 > 0) {
			this.field_6745--;
			if (this.field_6745 % 10 == 0) {
				double d = this.random.nextGaussian() * 0.02;
				double e = this.random.nextGaussian() * 0.02;
				double f = this.random.nextGaussian() * 0.02;
				this.world
					.method_8406(
						ParticleTypes.field_11201,
						this.x + (double)(this.random.nextFloat() * this.width * 2.0F) - (double)this.width,
						this.y + 0.5 + (double)(this.random.nextFloat() * this.height),
						this.z + (double)(this.random.nextFloat() * this.width * 2.0F) - (double)this.width,
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
			this.field_6745 = 0;
			return super.damage(damageSource, f);
		}
	}

	@Override
	public float method_6144(BlockPos blockPos, ViewableWorld viewableWorld) {
		return viewableWorld.getBlockState(blockPos.down()).getBlock() == this.spawningGround ? 10.0F : viewableWorld.method_8610(blockPos) - 0.5F;
	}

	@Override
	public void writeCustomDataToTag(CompoundTag compoundTag) {
		super.writeCustomDataToTag(compoundTag);
		compoundTag.putInt("InLove", this.field_6745);
		if (this.field_6744 != null) {
			compoundTag.putUuid("LoveCause", this.field_6744);
		}
	}

	@Override
	public double getHeightOffset() {
		return 0.14;
	}

	@Override
	public void readCustomDataFromTag(CompoundTag compoundTag) {
		super.readCustomDataFromTag(compoundTag);
		this.field_6745 = compoundTag.getInt("InLove");
		this.field_6744 = compoundTag.hasUuid("LoveCause") ? compoundTag.getUuid("LoveCause") : null;
	}

	@Override
	public boolean method_5979(IWorld iWorld, class_3730 arg) {
		int i = MathHelper.floor(this.x);
		int j = MathHelper.floor(this.getBoundingBox().minY);
		int k = MathHelper.floor(this.z);
		BlockPos blockPos = new BlockPos(i, j, k);
		return iWorld.getBlockState(blockPos.down()).getBlock() == this.spawningGround && iWorld.method_8624(blockPos, 0) > 8 && super.method_5979(iWorld, arg);
	}

	@Override
	public int method_5970() {
		return 120;
	}

	@Override
	public boolean method_5974(double d) {
		return false;
	}

	@Override
	protected int method_6110(PlayerEntity playerEntity) {
		return 1 + this.world.random.nextInt(3);
	}

	public boolean method_6481(ItemStack itemStack) {
		return itemStack.getItem() == Items.field_8861;
	}

	@Override
	public boolean interactMob(PlayerEntity playerEntity, Hand hand) {
		ItemStack itemStack = playerEntity.getStackInHand(hand);
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

		return super.interactMob(playerEntity, hand);
	}

	protected void method_6475(PlayerEntity playerEntity, ItemStack itemStack) {
		if (!playerEntity.abilities.creativeMode) {
			itemStack.subtractAmount(1);
		}
	}

	public boolean method_6482() {
		return this.field_6745 <= 0;
	}

	public void method_6480(@Nullable PlayerEntity playerEntity) {
		this.field_6745 = 600;
		if (playerEntity != null) {
			this.field_6744 = playerEntity.getUuid();
		}

		this.world.method_8421(this, (byte)18);
	}

	public void method_6476(int i) {
		this.field_6745 = i;
	}

	@Nullable
	public ServerPlayerEntity method_6478() {
		if (this.field_6744 == null) {
			return null;
		} else {
			PlayerEntity playerEntity = this.world.getPlayerByUuid(this.field_6744);
			return playerEntity instanceof ServerPlayerEntity ? (ServerPlayerEntity)playerEntity : null;
		}
	}

	public boolean method_6479() {
		return this.field_6745 > 0;
	}

	public void method_6477() {
		this.field_6745 = 0;
	}

	public boolean method_6474(AnimalEntity animalEntity) {
		if (animalEntity == this) {
			return false;
		} else {
			return animalEntity.getClass() != this.getClass() ? false : this.method_6479() && animalEntity.method_6479();
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
				this.world
					.method_8406(
						ParticleTypes.field_11201,
						this.x + (double)(this.random.nextFloat() * this.width * 2.0F) - (double)this.width,
						this.y + 0.5 + (double)(this.random.nextFloat() * this.height),
						this.z + (double)(this.random.nextFloat() * this.width * 2.0F) - (double)this.width,
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
