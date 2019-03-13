package net.minecraft.entity.projectile;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1675;
import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.network.packet.EntitySpawnS2CPacket;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.tag.FluidTags;
import net.minecraft.tag.ItemTags;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RayTraceContext;
import net.minecraft.world.World;
import net.minecraft.world.loot.LootSupplier;
import net.minecraft.world.loot.LootTables;
import net.minecraft.world.loot.context.LootContext;
import net.minecraft.world.loot.context.LootContextParameters;
import net.minecraft.world.loot.context.LootContextTypes;

public class FishHookEntity extends Entity {
	private static final TrackedData<Integer> field_7170 = DataTracker.registerData(FishHookEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private boolean field_7176;
	private int field_7167;
	private final PlayerEntity field_7177;
	private int field_7166;
	private int field_7173;
	private int field_7174;
	private int field_7172;
	private float field_7169;
	public Entity hookedEntity;
	private FishHookEntity.State state = FishHookEntity.State.field_7180;
	private final int field_7171;
	private final int field_7168;

	private FishHookEntity(World world, PlayerEntity playerEntity, int i, int j) {
		super(EntityType.FISHING_BOBBER, world);
		this.ignoreCameraFrustum = true;
		this.field_7177 = playerEntity;
		this.field_7177.fishHook = this;
		this.field_7171 = Math.max(0, i);
		this.field_7168 = Math.max(0, j);
	}

	@Environment(EnvType.CLIENT)
	public FishHookEntity(World world, PlayerEntity playerEntity, double d, double e, double f) {
		this(world, playerEntity, 0, 0);
		this.setPosition(d, e, f);
		this.prevX = this.x;
		this.prevY = this.y;
		this.prevZ = this.z;
	}

	public FishHookEntity(PlayerEntity playerEntity, World world, int i, int j) {
		this(world, playerEntity, i, j);
		float f = this.field_7177.pitch;
		float g = this.field_7177.yaw;
		float h = MathHelper.cos(-g * (float) (Math.PI / 180.0) - (float) Math.PI);
		float k = MathHelper.sin(-g * (float) (Math.PI / 180.0) - (float) Math.PI);
		float l = -MathHelper.cos(-f * (float) (Math.PI / 180.0));
		float m = MathHelper.sin(-f * (float) (Math.PI / 180.0));
		double d = this.field_7177.x - (double)k * 0.3;
		double e = this.field_7177.y + (double)this.field_7177.getStandingEyeHeight();
		double n = this.field_7177.z - (double)h * 0.3;
		this.setPositionAndAngles(d, e, n, g, f);
		Vec3d vec3d = new Vec3d((double)(-k), (double)MathHelper.clamp(-(m / l), -5.0F, 5.0F), (double)(-h));
		double o = vec3d.length();
		vec3d = vec3d.multiply(
			0.6 / o + 0.5 + this.random.nextGaussian() * 0.0045,
			0.6 / o + 0.5 + this.random.nextGaussian() * 0.0045,
			0.6 / o + 0.5 + this.random.nextGaussian() * 0.0045
		);
		this.method_18799(vec3d);
		this.yaw = (float)(MathHelper.atan2(vec3d.x, vec3d.z) * 180.0F / (float)Math.PI);
		this.pitch = (float)(MathHelper.atan2(vec3d.y, (double)MathHelper.sqrt(method_17996(vec3d))) * 180.0F / (float)Math.PI);
		this.prevYaw = this.yaw;
		this.prevPitch = this.pitch;
	}

	@Override
	protected void initDataTracker() {
		this.method_5841().startTracking(field_7170, 0);
	}

	@Override
	public void method_5674(TrackedData<?> trackedData) {
		if (field_7170.equals(trackedData)) {
			int i = this.method_5841().get(field_7170);
			this.hookedEntity = i > 0 ? this.field_6002.getEntityById(i - 1) : null;
		}

		super.method_5674(trackedData);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean shouldRenderAtDistance(double d) {
		double e = 64.0;
		return d < 4096.0;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void setPositionAndRotations(double d, double e, double f, float g, float h, int i, boolean bl) {
	}

	@Override
	public void update() {
		super.update();
		if (this.field_7177 == null) {
			this.invalidate();
		} else if (this.field_6002.isClient || !this.method_6959()) {
			if (this.field_7176) {
				this.field_7167++;
				if (this.field_7167 >= 1200) {
					this.invalidate();
					return;
				}
			}

			float f = 0.0F;
			BlockPos blockPos = new BlockPos(this);
			FluidState fluidState = this.field_6002.method_8316(blockPos);
			if (fluidState.method_15767(FluidTags.field_15517)) {
				f = fluidState.method_15763(this.field_6002, blockPos);
			}

			if (this.state == FishHookEntity.State.field_7180) {
				if (this.hookedEntity != null) {
					this.method_18799(Vec3d.ZERO);
					this.state = FishHookEntity.State.field_7178;
					return;
				}

				if (f > 0.0F) {
					this.method_18799(this.method_18798().multiply(0.3, 0.2, 0.3));
					this.state = FishHookEntity.State.field_7179;
					return;
				}

				if (!this.field_6002.isClient) {
					this.method_6958();
				}

				if (!this.field_7176 && !this.onGround && !this.horizontalCollision) {
					this.field_7166++;
				} else {
					this.field_7166 = 0;
					this.method_18799(Vec3d.ZERO);
				}
			} else {
				if (this.state == FishHookEntity.State.field_7178) {
					if (this.hookedEntity != null) {
						if (this.hookedEntity.invalid) {
							this.hookedEntity = null;
							this.state = FishHookEntity.State.field_7180;
						} else {
							this.x = this.hookedEntity.x;
							this.y = this.hookedEntity.method_5829().minY + (double)this.hookedEntity.getHeight() * 0.8;
							this.z = this.hookedEntity.z;
							this.setPosition(this.x, this.y, this.z);
						}
					}

					return;
				}

				if (this.state == FishHookEntity.State.field_7179) {
					Vec3d vec3d = this.method_18798();
					double d = this.y + vec3d.y - (double)blockPos.getY() - (double)f;
					if (Math.abs(d) < 0.01) {
						d += Math.signum(d) * 0.1;
					}

					this.setVelocity(vec3d.x * 0.9, vec3d.y - d * (double)this.random.nextFloat() * 0.2, vec3d.z * 0.9);
					if (!this.field_6002.isClient && f > 0.0F) {
						this.method_6949(blockPos);
					}
				}
			}

			if (!fluidState.method_15767(FluidTags.field_15517)) {
				this.method_18799(this.method_18798().add(0.0, -0.03, 0.0));
			}

			this.method_5784(MovementType.field_6308, this.method_18798());
			this.method_6952();
			double e = 0.92;
			this.method_18799(this.method_18798().multiply(0.92));
			this.setPosition(this.x, this.y, this.z);
		}
	}

	private boolean method_6959() {
		ItemStack itemStack = this.field_7177.method_6047();
		ItemStack itemStack2 = this.field_7177.method_6079();
		boolean bl = itemStack.getItem() == Items.field_8378;
		boolean bl2 = itemStack2.getItem() == Items.field_8378;
		if (!this.field_7177.invalid && this.field_7177.isValid() && (bl || bl2) && !(this.squaredDistanceTo(this.field_7177) > 1024.0)) {
			return false;
		} else {
			this.invalidate();
			return true;
		}
	}

	private void method_6952() {
		Vec3d vec3d = this.method_18798();
		float f = MathHelper.sqrt(method_17996(vec3d));
		this.yaw = (float)(MathHelper.atan2(vec3d.x, vec3d.z) * 180.0F / (float)Math.PI);
		this.pitch = (float)(MathHelper.atan2(vec3d.y, (double)f) * 180.0F / (float)Math.PI);

		while (this.pitch - this.prevPitch < -180.0F) {
			this.prevPitch -= 360.0F;
		}

		while (this.pitch - this.prevPitch >= 180.0F) {
			this.prevPitch += 360.0F;
		}

		while (this.yaw - this.prevYaw < -180.0F) {
			this.prevYaw -= 360.0F;
		}

		while (this.yaw - this.prevYaw >= 180.0F) {
			this.prevYaw += 360.0F;
		}

		this.pitch = MathHelper.lerp(0.2F, this.prevPitch, this.pitch);
		this.yaw = MathHelper.lerp(0.2F, this.prevYaw, this.yaw);
	}

	private void method_6958() {
		HitResult hitResult = class_1675.method_18074(
			this,
			this.method_5829().method_18804(this.method_18798()).expand(1.0),
			entity -> !entity.isSpectator() && (entity.doesCollide() || entity instanceof ItemEntity) && (entity != this.field_7177 || this.field_7166 >= 5),
			RayTraceContext.ShapeType.field_17558,
			true
		);
		if (hitResult.getType() != HitResult.Type.NONE) {
			if (hitResult.getType() == HitResult.Type.ENTITY) {
				this.hookedEntity = ((EntityHitResult)hitResult).getEntity();
				this.method_6951();
			} else {
				this.field_7176 = true;
			}
		}
	}

	private void method_6951() {
		this.method_5841().set(field_7170, this.hookedEntity.getEntityId() + 1);
	}

	private void method_6949(BlockPos blockPos) {
		ServerWorld serverWorld = (ServerWorld)this.field_6002;
		int i = 1;
		BlockPos blockPos2 = blockPos.up();
		if (this.random.nextFloat() < 0.25F && this.field_6002.method_8520(blockPos2)) {
			i++;
		}

		if (this.random.nextFloat() < 0.5F && !this.field_6002.method_8311(blockPos2)) {
			i--;
		}

		if (this.field_7173 > 0) {
			this.field_7173--;
			if (this.field_7173 <= 0) {
				this.field_7174 = 0;
				this.field_7172 = 0;
			} else {
				this.method_18799(this.method_18798().add(0.0, -0.2 * (double)this.random.nextFloat() * (double)this.random.nextFloat(), 0.0));
			}
		} else if (this.field_7172 > 0) {
			this.field_7172 -= i;
			if (this.field_7172 > 0) {
				this.field_7169 = (float)((double)this.field_7169 + this.random.nextGaussian() * 4.0);
				float f = this.field_7169 * (float) (Math.PI / 180.0);
				float g = MathHelper.sin(f);
				float h = MathHelper.cos(f);
				double d = this.x + (double)(g * (float)this.field_7172 * 0.1F);
				double e = (double)((float)MathHelper.floor(this.method_5829().minY) + 1.0F);
				double j = this.z + (double)(h * (float)this.field_7172 * 0.1F);
				Block block = serverWorld.method_8320(new BlockPos(d, e - 1.0, j)).getBlock();
				if (block == Blocks.field_10382) {
					if (this.random.nextFloat() < 0.15F) {
						serverWorld.method_14199(ParticleTypes.field_11247, d, e - 0.1F, j, 1, (double)g, 0.1, (double)h, 0.0);
					}

					float k = g * 0.04F;
					float l = h * 0.04F;
					serverWorld.method_14199(ParticleTypes.field_11244, d, e, j, 0, (double)l, 0.01, (double)(-k), 1.0);
					serverWorld.method_14199(ParticleTypes.field_11244, d, e, j, 0, (double)(-l), 0.01, (double)k, 1.0);
				}
			} else {
				Vec3d vec3d = this.method_18798();
				this.setVelocity(vec3d.x, (double)(-0.4F * MathHelper.nextFloat(this.random, 0.6F, 1.0F)), vec3d.z);
				this.method_5783(SoundEvents.field_14660, 0.25F, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F);
				double m = this.method_5829().minY + 0.5;
				serverWorld.method_14199(
					ParticleTypes.field_11247, this.x, m, this.z, (int)(1.0F + this.getWidth() * 20.0F), (double)this.getWidth(), 0.0, (double)this.getWidth(), 0.2F
				);
				serverWorld.method_14199(
					ParticleTypes.field_11244, this.x, m, this.z, (int)(1.0F + this.getWidth() * 20.0F), (double)this.getWidth(), 0.0, (double)this.getWidth(), 0.2F
				);
				this.field_7173 = MathHelper.nextInt(this.random, 20, 40);
			}
		} else if (this.field_7174 > 0) {
			this.field_7174 -= i;
			float f = 0.15F;
			if (this.field_7174 < 20) {
				f = (float)((double)f + (double)(20 - this.field_7174) * 0.05);
			} else if (this.field_7174 < 40) {
				f = (float)((double)f + (double)(40 - this.field_7174) * 0.02);
			} else if (this.field_7174 < 60) {
				f = (float)((double)f + (double)(60 - this.field_7174) * 0.01);
			}

			if (this.random.nextFloat() < f) {
				float g = MathHelper.nextFloat(this.random, 0.0F, 360.0F) * (float) (Math.PI / 180.0);
				float h = MathHelper.nextFloat(this.random, 25.0F, 60.0F);
				double d = this.x + (double)(MathHelper.sin(g) * h * 0.1F);
				double e = (double)((float)MathHelper.floor(this.method_5829().minY) + 1.0F);
				double j = this.z + (double)(MathHelper.cos(g) * h * 0.1F);
				Block block = serverWorld.method_8320(new BlockPos((int)d, (int)e - 1, (int)j)).getBlock();
				if (block == Blocks.field_10382) {
					serverWorld.method_14199(ParticleTypes.field_11202, d, e, j, 2 + this.random.nextInt(2), 0.1F, 0.0, 0.1F, 0.0);
				}
			}

			if (this.field_7174 <= 0) {
				this.field_7169 = MathHelper.nextFloat(this.random, 0.0F, 360.0F);
				this.field_7172 = MathHelper.nextInt(this.random, 20, 80);
			}
		} else {
			this.field_7174 = MathHelper.nextInt(this.random, 100, 600);
			this.field_7174 = this.field_7174 - this.field_7168 * 20 * 5;
		}
	}

	@Override
	public void method_5652(CompoundTag compoundTag) {
	}

	@Override
	public void method_5749(CompoundTag compoundTag) {
	}

	public int method_6957(ItemStack itemStack) {
		if (!this.field_6002.isClient && this.field_7177 != null) {
			int i = 0;
			if (this.hookedEntity != null) {
				this.method_6954();
				Criterions.FISHING_ROD_HOOKED.method_8939((ServerPlayerEntity)this.field_7177, itemStack, this, Collections.emptyList());
				this.field_6002.summonParticle(this, (byte)31);
				i = this.hookedEntity instanceof ItemEntity ? 3 : 5;
			} else if (this.field_7173 > 0) {
				LootContext.Builder builder = new LootContext.Builder((ServerWorld)this.field_6002)
					.method_312(LootContextParameters.field_1232, new BlockPos(this))
					.method_312(LootContextParameters.field_1229, itemStack)
					.setRandom(this.random)
					.setLuck((float)this.field_7171 + this.field_7177.getLuck());
				LootSupplier lootSupplier = this.field_6002.getServer().getLootManager().method_367(LootTables.field_353);
				List<ItemStack> list = lootSupplier.getDrops(builder.method_309(LootContextTypes.FISHING));
				Criterions.FISHING_ROD_HOOKED.method_8939((ServerPlayerEntity)this.field_7177, itemStack, this, list);

				for (ItemStack itemStack2 : list) {
					ItemEntity itemEntity = new ItemEntity(this.field_6002, this.x, this.y, this.z, itemStack2);
					double d = this.field_7177.x - this.x;
					double e = this.field_7177.y - this.y;
					double f = this.field_7177.z - this.z;
					double g = 0.1;
					itemEntity.setVelocity(d * 0.1, e * 0.1 + Math.sqrt(Math.sqrt(d * d + e * e + f * f)) * 0.08, f * 0.1);
					this.field_6002.spawnEntity(itemEntity);
					this.field_7177
						.field_6002
						.spawnEntity(
							new ExperienceOrbEntity(this.field_7177.field_6002, this.field_7177.x, this.field_7177.y + 0.5, this.field_7177.z + 0.5, this.random.nextInt(6) + 1)
						);
					if (itemStack2.getItem().method_7855(ItemTags.field_15527)) {
						this.field_7177.method_7339(Stats.field_15391, 1);
					}
				}

				i = 1;
			}

			if (this.field_7176) {
				i = 2;
			}

			this.invalidate();
			return i;
		} else {
			return 0;
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_5711(byte b) {
		if (b == 31 && this.field_6002.isClient && this.hookedEntity instanceof PlayerEntity && ((PlayerEntity)this.hookedEntity).method_7340()) {
			this.method_6954();
		}

		super.method_5711(b);
	}

	protected void method_6954() {
		if (this.field_7177 != null) {
			Vec3d vec3d = new Vec3d(this.field_7177.x - this.x, this.field_7177.y - this.y, this.field_7177.z - this.z).multiply(0.1);
			this.hookedEntity.method_18799(this.hookedEntity.method_18798().add(vec3d));
		}
	}

	@Override
	protected boolean method_5658() {
		return false;
	}

	@Override
	public void invalidate() {
		super.invalidate();
		if (this.field_7177 != null) {
			this.field_7177.fishHook = null;
		}
	}

	@Nullable
	public PlayerEntity method_6947() {
		return this.field_7177;
	}

	@Override
	public boolean canUsePortals() {
		return false;
	}

	@Override
	public Packet<?> method_18002() {
		Entity entity = this.method_6947();
		return new EntitySpawnS2CPacket(this, entity == null ? this.getEntityId() : entity.getEntityId());
	}

	static enum State {
		field_7180,
		field_7178,
		field_7179;
	}
}
