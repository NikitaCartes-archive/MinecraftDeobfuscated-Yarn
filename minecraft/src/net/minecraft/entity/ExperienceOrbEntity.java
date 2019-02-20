package net.minecraft.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.packet.ExperienceOrbSpawnS2CPacket;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class ExperienceOrbEntity extends Entity {
	public int field_6165;
	public int xpAge;
	public int field_6163;
	private int health = 5;
	private int amount;
	private PlayerEntity field_6162;
	private int field_6160;

	public ExperienceOrbEntity(World world, double d, double e, double f, int i) {
		this(EntityType.EXPERIENCE_ORB, world);
		this.setPosition(d, e, f);
		this.yaw = (float)(Math.random() * 360.0);
		this.velocityX = (double)((float)(Math.random() * 0.2F - 0.1F) * 2.0F);
		this.velocityY = (double)((float)(Math.random() * 0.2) * 2.0F);
		this.velocityZ = (double)((float)(Math.random() * 0.2F - 0.1F) * 2.0F);
		this.amount = i;
	}

	public ExperienceOrbEntity(EntityType<? extends ExperienceOrbEntity> entityType, World world) {
		super(entityType, world);
	}

	@Override
	protected boolean method_5658() {
		return false;
	}

	@Override
	protected void initDataTracker() {
	}

	@Environment(EnvType.CLIENT)
	@Override
	public int getLightmapCoordinates() {
		float f = 0.5F;
		f = MathHelper.clamp(f, 0.0F, 1.0F);
		int i = super.getLightmapCoordinates();
		int j = i & 0xFF;
		int k = i >> 16 & 0xFF;
		j += (int)(f * 15.0F * 16.0F);
		if (j > 240) {
			j = 240;
		}

		return j | k << 16;
	}

	@Override
	public void update() {
		super.update();
		if (this.field_6163 > 0) {
			this.field_6163--;
		}

		this.prevX = this.x;
		this.prevY = this.y;
		this.prevZ = this.z;
		if (this.isInFluid(FluidTags.field_15517)) {
			this.method_5921();
		} else if (!this.isUnaffectedByGravity()) {
			this.velocityY -= 0.03F;
		}

		if (this.world.getFluidState(new BlockPos(this)).matches(FluidTags.field_15518)) {
			this.velocityY = 0.2F;
			this.velocityX = (double)((this.random.nextFloat() - this.random.nextFloat()) * 0.2F);
			this.velocityZ = (double)((this.random.nextFloat() - this.random.nextFloat()) * 0.2F);
			this.playSound(SoundEvents.field_14821, 0.4F, 2.0F + this.random.nextFloat() * 0.4F);
		}

		if (!this.world.method_18026(this.getBoundingBox())) {
			this.method_5632(this.x, (this.getBoundingBox().minY + this.getBoundingBox().maxY) / 2.0, this.z);
		}

		double d = 8.0;
		if (this.field_6160 < this.field_6165 - 20 + this.getEntityId() % 100) {
			if (this.field_6162 == null || this.field_6162.squaredDistanceTo(this) > 64.0) {
				this.field_6162 = this.world.method_18460(this, 8.0);
			}

			this.field_6160 = this.field_6165;
		}

		if (this.field_6162 != null && this.field_6162.isSpectator()) {
			this.field_6162 = null;
		}

		if (this.field_6162 != null) {
			double e = (this.field_6162.x - this.x) / 8.0;
			double f = (this.field_6162.y + (double)this.field_6162.getEyeHeight() / 2.0 - this.y) / 8.0;
			double g = (this.field_6162.z - this.z) / 8.0;
			double h = Math.sqrt(e * e + f * f + g * g);
			double i = 1.0 - h;
			if (i > 0.0) {
				i *= i;
				this.velocityX += e / h * i * 0.1;
				this.velocityY += f / h * i * 0.1;
				this.velocityZ += g / h * i * 0.1;
			}
		}

		this.move(MovementType.field_6308, this.velocityX, this.velocityY, this.velocityZ);
		float j = 0.98F;
		if (this.onGround) {
			j = this.world.getBlockState(new BlockPos(this.x, this.getBoundingBox().minY - 1.0, this.z)).getBlock().getFrictionCoefficient() * 0.98F;
		}

		this.velocityX *= (double)j;
		this.velocityY *= 0.98F;
		this.velocityZ *= (double)j;
		if (this.onGround) {
			this.velocityY *= -0.9F;
		}

		this.field_6165++;
		this.xpAge++;
		if (this.xpAge >= 6000) {
			this.invalidate();
		}
	}

	private void method_5921() {
		this.velocityY += 5.0E-4F;
		this.velocityY = Math.min(this.velocityY, 0.06F);
		this.velocityX *= 0.99F;
		this.velocityZ *= 0.99F;
	}

	@Override
	protected void onSwimmingStart() {
	}

	@Override
	protected void burn(int i) {
		this.damage(DamageSource.IN_FIRE, (float)i);
	}

	@Override
	public boolean damage(DamageSource damageSource, float f) {
		if (this.isInvulnerableTo(damageSource)) {
			return false;
		} else {
			this.scheduleVelocityUpdate();
			this.health = (int)((float)this.health - f);
			if (this.health <= 0) {
				this.invalidate();
			}

			return false;
		}
	}

	@Override
	public void writeCustomDataToTag(CompoundTag compoundTag) {
		compoundTag.putShort("Health", (short)this.health);
		compoundTag.putShort("Age", (short)this.xpAge);
		compoundTag.putShort("Value", (short)this.amount);
	}

	@Override
	public void readCustomDataFromTag(CompoundTag compoundTag) {
		this.health = compoundTag.getShort("Health");
		this.xpAge = compoundTag.getShort("Age");
		this.amount = compoundTag.getShort("Value");
	}

	@Override
	public void onPlayerCollision(PlayerEntity playerEntity) {
		if (!this.world.isClient) {
			if (this.field_6163 == 0 && playerEntity.field_7504 == 0) {
				playerEntity.field_7504 = 2;
				playerEntity.pickUpEntity(this, 1);
				ItemStack itemStack = EnchantmentHelper.getRandomEnchantedEquipment(Enchantments.field_9101, playerEntity);
				if (!itemStack.isEmpty() && itemStack.isDamaged()) {
					int i = Math.min(this.method_5917(this.amount), itemStack.getDamage());
					this.amount = this.amount - this.method_5922(i);
					itemStack.setDamage(itemStack.getDamage() - i);
				}

				if (this.amount > 0) {
					playerEntity.addExperience(this.amount);
				}

				this.invalidate();
			}
		}
	}

	private int method_5922(int i) {
		return i / 2;
	}

	private int method_5917(int i) {
		return i * 2;
	}

	public int getExperienceAmount() {
		return this.amount;
	}

	@Environment(EnvType.CLIENT)
	public int getOrbSize() {
		if (this.amount >= 2477) {
			return 10;
		} else if (this.amount >= 1237) {
			return 9;
		} else if (this.amount >= 617) {
			return 8;
		} else if (this.amount >= 307) {
			return 7;
		} else if (this.amount >= 149) {
			return 6;
		} else if (this.amount >= 73) {
			return 5;
		} else if (this.amount >= 37) {
			return 4;
		} else if (this.amount >= 17) {
			return 3;
		} else if (this.amount >= 7) {
			return 2;
		} else {
			return this.amount >= 3 ? 1 : 0;
		}
	}

	public static int roundToOrbSize(int i) {
		if (i >= 2477) {
			return 2477;
		} else if (i >= 1237) {
			return 1237;
		} else if (i >= 617) {
			return 617;
		} else if (i >= 307) {
			return 307;
		} else if (i >= 149) {
			return 149;
		} else if (i >= 73) {
			return 73;
		} else if (i >= 37) {
			return 37;
		} else if (i >= 17) {
			return 17;
		} else if (i >= 7) {
			return 7;
		} else {
			return i >= 3 ? 3 : 1;
		}
	}

	@Override
	public boolean method_5732() {
		return false;
	}

	@Override
	public Packet<?> createSpawnPacket() {
		return new ExperienceOrbSpawnS2CPacket(this);
	}
}
