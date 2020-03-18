package net.minecraft.entity;

import java.util.Map.Entry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.ExperienceOrbSpawnS2CPacket;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ExperienceOrbEntity extends Entity {
	public int renderTicks;
	public int orbAge;
	public int pickupDelay;
	private int health = 5;
	private int amount;
	private PlayerEntity target;
	private int lastTargetUpdateTick;

	public ExperienceOrbEntity(World world, double x, double y, double z, int amount) {
		this(EntityType.EXPERIENCE_ORB, world);
		this.updatePosition(x, y, z);
		this.yaw = (float)(this.random.nextDouble() * 360.0);
		this.setVelocity((this.random.nextDouble() * 0.2F - 0.1F) * 2.0, this.random.nextDouble() * 0.2 * 2.0, (this.random.nextDouble() * 0.2F - 0.1F) * 2.0);
		this.amount = amount;
	}

	public ExperienceOrbEntity(EntityType<? extends ExperienceOrbEntity> entityType, World world) {
		super(entityType, world);
	}

	@Override
	protected boolean canClimb() {
		return false;
	}

	@Override
	protected void initDataTracker() {
	}

	@Override
	public void tick() {
		super.tick();
		if (this.pickupDelay > 0) {
			this.pickupDelay--;
		}

		this.prevX = this.getX();
		this.prevY = this.getY();
		this.prevZ = this.getZ();
		if (this.isSubmergedIn(FluidTags.WATER)) {
			this.applyWaterMovement();
		} else if (!this.hasNoGravity()) {
			this.setVelocity(this.getVelocity().add(0.0, -0.03, 0.0));
		}

		if (this.world.getFluidState(this.getBlockPos()).matches(FluidTags.LAVA)) {
			this.setVelocity(
				(double)((this.random.nextFloat() - this.random.nextFloat()) * 0.2F), 0.2F, (double)((this.random.nextFloat() - this.random.nextFloat()) * 0.2F)
			);
			this.playSound(SoundEvents.ENTITY_GENERIC_BURN, 0.4F, 2.0F + this.random.nextFloat() * 0.4F);
		}

		if (!this.world.doesNotCollide(this.getBoundingBox())) {
			this.pushOutOfBlocks(this.getX(), (this.getBoundingBox().y1 + this.getBoundingBox().y2) / 2.0, this.getZ());
		}

		double d = 8.0;
		if (this.lastTargetUpdateTick < this.renderTicks - 20 + this.getEntityId() % 100) {
			if (this.target == null || this.target.squaredDistanceTo(this) > 64.0) {
				this.target = this.world.getClosestPlayer(this, 8.0);
			}

			this.lastTargetUpdateTick = this.renderTicks;
		}

		if (this.target != null && this.target.isSpectator()) {
			this.target = null;
		}

		if (this.target != null) {
			Vec3d vec3d = new Vec3d(
				this.target.getX() - this.getX(), this.target.getY() + (double)this.target.getStandingEyeHeight() / 2.0 - this.getY(), this.target.getZ() - this.getZ()
			);
			double e = vec3d.lengthSquared();
			if (e < 64.0) {
				double f = 1.0 - Math.sqrt(e) / 8.0;
				this.setVelocity(this.getVelocity().add(vec3d.normalize().multiply(f * f * 0.1)));
			}
		}

		this.move(MovementType.SELF, this.getVelocity());
		float g = 0.98F;
		if (this.onGround) {
			g = this.world.getBlockState(new BlockPos(this.getX(), this.getY() - 1.0, this.getZ())).getBlock().getSlipperiness() * 0.98F;
		}

		this.setVelocity(this.getVelocity().multiply((double)g, 0.98, (double)g));
		if (this.onGround) {
			this.setVelocity(this.getVelocity().multiply(1.0, -0.9, 1.0));
		}

		this.renderTicks++;
		this.orbAge++;
		if (this.orbAge >= 6000) {
			this.remove();
		}
	}

	private void applyWaterMovement() {
		Vec3d vec3d = this.getVelocity();
		this.setVelocity(vec3d.x * 0.99F, Math.min(vec3d.y + 5.0E-4F, 0.06F), vec3d.z * 0.99F);
	}

	@Override
	protected void onSwimmingStart() {
	}

	@Override
	public boolean damage(DamageSource source, float amount) {
		if (this.isInvulnerableTo(source)) {
			return false;
		} else {
			this.scheduleVelocityUpdate();
			this.health = (int)((float)this.health - amount);
			if (this.health <= 0) {
				this.remove();
			}

			return false;
		}
	}

	@Override
	public void writeCustomDataToTag(CompoundTag tag) {
		tag.putShort("Health", (short)this.health);
		tag.putShort("Age", (short)this.orbAge);
		tag.putShort("Value", (short)this.amount);
	}

	@Override
	public void readCustomDataFromTag(CompoundTag tag) {
		this.health = tag.getShort("Health");
		this.orbAge = tag.getShort("Age");
		this.amount = tag.getShort("Value");
	}

	@Override
	public void onPlayerCollision(PlayerEntity player) {
		if (!this.world.isClient) {
			if (this.pickupDelay == 0 && player.experiencePickUpDelay == 0) {
				player.experiencePickUpDelay = 2;
				player.sendPickup(this, 1);
				Entry<EquipmentSlot, ItemStack> entry = EnchantmentHelper.chooseEquipmentWith(Enchantments.MENDING, player, ItemStack::isDamaged);
				if (entry != null) {
					ItemStack itemStack = (ItemStack)entry.getValue();
					if (!itemStack.isEmpty() && itemStack.isDamaged()) {
						int i = Math.min(this.getMendingRepairAmount(this.amount), itemStack.getDamage());
						this.amount = this.amount - this.getMendingRepairCost(i);
						itemStack.setDamage(itemStack.getDamage() - i);
					}
				}

				if (this.amount > 0) {
					player.addExperience(this.amount);
				}

				this.remove();
			}
		}
	}

	private int getMendingRepairCost(int repairAmount) {
		return repairAmount / 2;
	}

	private int getMendingRepairAmount(int experienceAmount) {
		return experienceAmount * 2;
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

	public static int roundToOrbSize(int value) {
		if (value >= 2477) {
			return 2477;
		} else if (value >= 1237) {
			return 1237;
		} else if (value >= 617) {
			return 617;
		} else if (value >= 307) {
			return 307;
		} else if (value >= 149) {
			return 149;
		} else if (value >= 73) {
			return 73;
		} else if (value >= 37) {
			return 37;
		} else if (value >= 17) {
			return 17;
		} else if (value >= 7) {
			return 7;
		} else {
			return value >= 3 ? 3 : 1;
		}
	}

	@Override
	public boolean isAttackable() {
		return false;
	}

	@Override
	public Packet<?> createSpawnPacket() {
		return new ExperienceOrbSpawnS2CPacket(this);
	}
}
