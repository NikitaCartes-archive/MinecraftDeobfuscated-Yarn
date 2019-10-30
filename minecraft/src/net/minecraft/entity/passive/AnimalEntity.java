package net.minecraft.entity.passive;

import java.util.Random;
import java.util.UUID;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
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
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public abstract class AnimalEntity extends PassiveEntity {
	private int loveTicks;
	private UUID lovingPlayer;

	protected AnimalEntity(EntityType<? extends AnimalEntity> type, World world) {
		super(type, world);
	}

	@Override
	protected void mobTick() {
		if (this.getBreedingAge() != 0) {
			this.loveTicks = 0;
		}

		super.mobTick();
	}

	@Override
	public void tickMovement() {
		super.tickMovement();
		if (this.getBreedingAge() != 0) {
			this.loveTicks = 0;
		}

		if (this.loveTicks > 0) {
			this.loveTicks--;
			if (this.loveTicks % 10 == 0) {
				double d = this.random.nextGaussian() * 0.02;
				double e = this.random.nextGaussian() * 0.02;
				double f = this.random.nextGaussian() * 0.02;
				this.world.addParticle(ParticleTypes.HEART, this.method_23322(1.0), this.method_23319() + 0.5, this.method_23325(1.0), d, e, f);
			}
		}
	}

	@Override
	public boolean damage(DamageSource source, float amount) {
		if (this.isInvulnerableTo(source)) {
			return false;
		} else {
			this.loveTicks = 0;
			return super.damage(source, amount);
		}
	}

	@Override
	public float getPathfindingFavor(BlockPos pos, WorldView worldView) {
		return worldView.getBlockState(pos.method_10074()).getBlock() == Blocks.GRASS_BLOCK ? 10.0F : worldView.getBrightness(pos) - 0.5F;
	}

	@Override
	public void writeCustomDataToTag(CompoundTag tag) {
		super.writeCustomDataToTag(tag);
		tag.putInt("InLove", this.loveTicks);
		if (this.lovingPlayer != null) {
			tag.putUuid("LoveCause", this.lovingPlayer);
		}
	}

	@Override
	public double getHeightOffset() {
		return 0.14;
	}

	@Override
	public void readCustomDataFromTag(CompoundTag tag) {
		super.readCustomDataFromTag(tag);
		this.loveTicks = tag.getInt("InLove");
		this.lovingPlayer = tag.containsUuid("LoveCause") ? tag.getUuid("LoveCause") : null;
	}

	public static boolean isValidNaturalSpawn(EntityType<? extends AnimalEntity> type, IWorld world, SpawnType spawnType, BlockPos pos, Random random) {
		return world.getBlockState(pos.method_10074()).getBlock() == Blocks.GRASS_BLOCK && world.getBaseLightLevel(pos, 0) > 8;
	}

	@Override
	public int getMinAmbientSoundDelay() {
		return 120;
	}

	@Override
	public boolean canImmediatelyDespawn(double distanceSquared) {
		return false;
	}

	@Override
	protected int getCurrentExperience(PlayerEntity player) {
		return 1 + this.world.random.nextInt(3);
	}

	public boolean isBreedingItem(ItemStack stack) {
		return stack.getItem() == Items.WHEAT;
	}

	@Override
	public boolean interactMob(PlayerEntity player, Hand hand) {
		ItemStack itemStack = player.getStackInHand(hand);
		if (this.isBreedingItem(itemStack)) {
			if (!this.world.isClient && this.getBreedingAge() == 0 && this.canEat()) {
				this.eat(player, itemStack);
				this.lovePlayer(player);
				player.swingHand(hand, true);
				return true;
			}

			if (this.isBaby()) {
				this.eat(player, itemStack);
				this.growUp((int)((float)(-this.getBreedingAge() / 20) * 0.1F), true);
				return true;
			}
		}

		return super.interactMob(player, hand);
	}

	protected void eat(PlayerEntity player, ItemStack stack) {
		if (!player.abilities.creativeMode) {
			stack.decrement(1);
		}
	}

	public boolean canEat() {
		return this.loveTicks <= 0;
	}

	public void lovePlayer(@Nullable PlayerEntity player) {
		this.loveTicks = 600;
		if (player != null) {
			this.lovingPlayer = player.getUuid();
		}

		this.world.sendEntityStatus(this, (byte)18);
	}

	public void setLoveTicks(int loveTicks) {
		this.loveTicks = loveTicks;
	}

	@Nullable
	public ServerPlayerEntity getLovingPlayer() {
		if (this.lovingPlayer == null) {
			return null;
		} else {
			PlayerEntity playerEntity = this.world.getPlayerByUuid(this.lovingPlayer);
			return playerEntity instanceof ServerPlayerEntity ? (ServerPlayerEntity)playerEntity : null;
		}
	}

	public boolean isInLove() {
		return this.loveTicks > 0;
	}

	public void resetLoveTicks() {
		this.loveTicks = 0;
	}

	public boolean canBreedWith(AnimalEntity other) {
		if (other == this) {
			return false;
		} else {
			return other.getClass() != this.getClass() ? false : this.isInLove() && other.isInLove();
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void handleStatus(byte status) {
		if (status == 18) {
			for (int i = 0; i < 7; i++) {
				double d = this.random.nextGaussian() * 0.02;
				double e = this.random.nextGaussian() * 0.02;
				double f = this.random.nextGaussian() * 0.02;
				this.world.addParticle(ParticleTypes.HEART, this.method_23322(1.0), this.method_23319() + 0.5, this.method_23325(1.0), d, e, f);
			}
		} else {
			super.handleStatus(status);
		}
	}
}
