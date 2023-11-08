package net.minecraft.world.explosion;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.ProtectionEnchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Util;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class Explosion {
	private static final ExplosionBehavior DEFAULT_BEHAVIOR = new ExplosionBehavior();
	private static final int field_30960 = 16;
	private final boolean createFire;
	private final Explosion.DestructionType destructionType;
	private final Random random = Random.create();
	private final World world;
	private final double x;
	private final double y;
	private final double z;
	@Nullable
	private final Entity entity;
	private final float power;
	@Nullable
	private final DamageSource damageSource;
	private final ExplosionBehavior behavior;
	private final ParticleEffect particle;
	private final ParticleEffect emitterParticle;
	private final SoundEvent soundEvent;
	private final ObjectArrayList<BlockPos> affectedBlocks = new ObjectArrayList<>();
	private final Map<PlayerEntity, Vec3d> affectedPlayers = Maps.<PlayerEntity, Vec3d>newHashMap();

	public static DamageSource createDamageSource(World world, @Nullable Entity source) {
		return world.getDamageSources().explosion(source, getCausingEntity(source));
	}

	public Explosion(
		World world,
		@Nullable Entity entity,
		double x,
		double y,
		double z,
		float power,
		List<BlockPos> affectedBlocks,
		Explosion.DestructionType destructionType,
		ParticleEffect particle,
		ParticleEffect emitterParticle,
		SoundEvent soundEvent
	) {
		this(world, entity, createDamageSource(world, entity), null, x, y, z, power, false, destructionType, particle, emitterParticle, soundEvent);
		this.affectedBlocks.addAll(affectedBlocks);
	}

	public Explosion(
		World world,
		@Nullable Entity entity,
		double x,
		double y,
		double z,
		float power,
		boolean createFire,
		Explosion.DestructionType destructionType,
		List<BlockPos> affectedBlocks
	) {
		this(world, entity, x, y, z, power, createFire, destructionType);
		this.affectedBlocks.addAll(affectedBlocks);
	}

	public Explosion(
		World world, @Nullable Entity entity, double x, double y, double z, float power, boolean createFire, Explosion.DestructionType destructionType
	) {
		this(
			world,
			entity,
			createDamageSource(world, entity),
			null,
			x,
			y,
			z,
			power,
			createFire,
			destructionType,
			ParticleTypes.EXPLOSION,
			ParticleTypes.EXPLOSION_EMITTER,
			SoundEvents.ENTITY_GENERIC_EXPLODE
		);
	}

	public Explosion(
		World world,
		@Nullable Entity entity,
		@Nullable DamageSource damageSource,
		@Nullable ExplosionBehavior behavior,
		double x,
		double y,
		double z,
		float power,
		boolean createFire,
		Explosion.DestructionType destructionType,
		ParticleEffect particle,
		ParticleEffect emitterParticle,
		SoundEvent soundEvent
	) {
		this.world = world;
		this.entity = entity;
		this.power = power;
		this.x = x;
		this.y = y;
		this.z = z;
		this.createFire = createFire;
		this.destructionType = destructionType;
		this.damageSource = damageSource;
		this.behavior = behavior == null ? this.chooseBehavior(entity) : behavior;
		this.particle = particle;
		this.emitterParticle = emitterParticle;
		this.soundEvent = soundEvent;
	}

	private ExplosionBehavior chooseBehavior(@Nullable Entity entity) {
		return (ExplosionBehavior)(entity == null ? DEFAULT_BEHAVIOR : new EntityExplosionBehavior(entity));
	}

	public static float getExposure(Vec3d source, Entity entity) {
		Box box = entity.getBoundingBox();
		double d = 1.0 / ((box.maxX - box.minX) * 2.0 + 1.0);
		double e = 1.0 / ((box.maxY - box.minY) * 2.0 + 1.0);
		double f = 1.0 / ((box.maxZ - box.minZ) * 2.0 + 1.0);
		double g = (1.0 - Math.floor(1.0 / d) * d) / 2.0;
		double h = (1.0 - Math.floor(1.0 / f) * f) / 2.0;
		if (!(d < 0.0) && !(e < 0.0) && !(f < 0.0)) {
			int i = 0;
			int j = 0;

			for (double k = 0.0; k <= 1.0; k += d) {
				for (double l = 0.0; l <= 1.0; l += e) {
					for (double m = 0.0; m <= 1.0; m += f) {
						double n = MathHelper.lerp(k, box.minX, box.maxX);
						double o = MathHelper.lerp(l, box.minY, box.maxY);
						double p = MathHelper.lerp(m, box.minZ, box.maxZ);
						Vec3d vec3d = new Vec3d(n + g, o, p + h);
						if (entity.getWorld().raycast(new RaycastContext(vec3d, source, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, entity)).getType()
							== HitResult.Type.MISS) {
							i++;
						}

						j++;
					}
				}
			}

			return (float)i / (float)j;
		} else {
			return 0.0F;
		}
	}

	public float getPower() {
		return this.power;
	}

	public Vec3d getPosition() {
		return new Vec3d(this.x, this.y, this.z);
	}

	public void collectBlocksAndDamageEntities() {
		this.world.emitGameEvent(this.entity, GameEvent.EXPLODE, new Vec3d(this.x, this.y, this.z));
		Set<BlockPos> set = Sets.<BlockPos>newHashSet();
		int i = 16;

		for (int j = 0; j < 16; j++) {
			for (int k = 0; k < 16; k++) {
				for (int l = 0; l < 16; l++) {
					if (j == 0 || j == 15 || k == 0 || k == 15 || l == 0 || l == 15) {
						double d = (double)((float)j / 15.0F * 2.0F - 1.0F);
						double e = (double)((float)k / 15.0F * 2.0F - 1.0F);
						double f = (double)((float)l / 15.0F * 2.0F - 1.0F);
						double g = Math.sqrt(d * d + e * e + f * f);
						d /= g;
						e /= g;
						f /= g;
						float h = this.power * (0.7F + this.world.random.nextFloat() * 0.6F);
						double m = this.x;
						double n = this.y;
						double o = this.z;

						for (float p = 0.3F; h > 0.0F; h -= 0.22500001F) {
							BlockPos blockPos = BlockPos.ofFloored(m, n, o);
							BlockState blockState = this.world.getBlockState(blockPos);
							FluidState fluidState = this.world.getFluidState(blockPos);
							if (!this.world.isInBuildLimit(blockPos)) {
								break;
							}

							Optional<Float> optional = this.behavior.getBlastResistance(this, this.world, blockPos, blockState, fluidState);
							if (optional.isPresent()) {
								h -= (optional.get() + 0.3F) * 0.3F;
							}

							if (h > 0.0F && this.behavior.canDestroyBlock(this, this.world, blockPos, blockState, h)) {
								set.add(blockPos);
							}

							m += d * 0.3F;
							n += e * 0.3F;
							o += f * 0.3F;
						}
					}
				}
			}
		}

		this.affectedBlocks.addAll(set);
		float q = this.power * 2.0F;
		int k = MathHelper.floor(this.x - (double)q - 1.0);
		int lx = MathHelper.floor(this.x + (double)q + 1.0);
		int r = MathHelper.floor(this.y - (double)q - 1.0);
		int s = MathHelper.floor(this.y + (double)q + 1.0);
		int t = MathHelper.floor(this.z - (double)q - 1.0);
		int u = MathHelper.floor(this.z + (double)q + 1.0);
		List<Entity> list = this.world.getOtherEntities(this.entity, new Box((double)k, (double)r, (double)t, (double)lx, (double)s, (double)u));
		Vec3d vec3d = new Vec3d(this.x, this.y, this.z);

		for (Entity entity : list) {
			if (!entity.isImmuneToExplosion(this)) {
				double v = Math.sqrt(entity.squaredDistanceTo(vec3d)) / (double)q;
				if (v <= 1.0) {
					double w = entity.getX() - this.x;
					double x = (entity instanceof TntEntity ? entity.getY() : entity.getEyeY()) - this.y;
					double y = entity.getZ() - this.z;
					double z = Math.sqrt(w * w + x * x + y * y);
					if (z != 0.0) {
						w /= z;
						x /= z;
						y /= z;
						if (this.damageSource != null) {
							entity.damage(this.damageSource, this.behavior.calculateDamage(this, entity));
						}

						double aa = (1.0 - v) * (double)getExposure(vec3d, entity);
						double ab;
						if (entity instanceof LivingEntity livingEntity) {
							ab = ProtectionEnchantment.transformExplosionKnockback(livingEntity, aa);
						} else {
							ab = aa;
						}

						w *= ab;
						x *= ab;
						y *= ab;
						Vec3d vec3d2 = new Vec3d(w, x, y);
						entity.setVelocity(entity.getVelocity().add(vec3d2));
						if (entity instanceof PlayerEntity) {
							PlayerEntity playerEntity = (PlayerEntity)entity;
							if (!playerEntity.isSpectator() && (!playerEntity.isCreative() || !playerEntity.getAbilities().flying)) {
								this.affectedPlayers.put(playerEntity, vec3d2);
							}
						}
					}
				}
			}
		}
	}

	/**
	 * @param particles whether this explosion should emit explosion or explosion emitter particles around the source of the explosion
	 */
	public void affectWorld(boolean particles) {
		if (this.world.isClient) {
			this.world
				.playSound(
					this.x,
					this.y,
					this.z,
					this.soundEvent,
					SoundCategory.BLOCKS,
					4.0F,
					(1.0F + (this.world.random.nextFloat() - this.world.random.nextFloat()) * 0.2F) * 0.7F,
					false
				);
		}

		boolean bl = this.shouldDestroy();
		if (particles) {
			ParticleEffect particleEffect;
			if (!(this.power < 2.0F) && bl) {
				particleEffect = this.emitterParticle;
			} else {
				particleEffect = this.particle;
			}

			this.world.addParticle(particleEffect, this.x, this.y, this.z, 1.0, 0.0, 0.0);
		}

		if (bl) {
			this.world.getProfiler().push("explosion_blocks");
			List<Pair<ItemStack, BlockPos>> list = new ArrayList();
			Util.shuffle(this.affectedBlocks, this.world.random);

			for (BlockPos blockPos : this.affectedBlocks) {
				this.world.getBlockState(blockPos).onExploded(this.world, blockPos, this, (stack, pos) -> tryMergeStack(list, stack, pos));
			}

			for (Pair<ItemStack, BlockPos> pair : list) {
				Block.dropStack(this.world, pair.getSecond(), pair.getFirst());
			}

			this.world.getProfiler().pop();
		}

		if (this.createFire) {
			for (BlockPos blockPos2 : this.affectedBlocks) {
				if (this.random.nextInt(3) == 0
					&& this.world.getBlockState(blockPos2).isAir()
					&& this.world.getBlockState(blockPos2.down()).isOpaqueFullCube(this.world, blockPos2.down())) {
					this.world.setBlockState(blockPos2, AbstractFireBlock.getState(this.world, blockPos2));
				}
			}
		}
	}

	private static void tryMergeStack(List<Pair<ItemStack, BlockPos>> stacks, ItemStack stack, BlockPos pos) {
		for (int i = 0; i < stacks.size(); i++) {
			Pair<ItemStack, BlockPos> pair = (Pair<ItemStack, BlockPos>)stacks.get(i);
			ItemStack itemStack = pair.getFirst();
			if (ItemEntity.canMerge(itemStack, stack)) {
				stacks.set(i, Pair.of(ItemEntity.merge(stack, itemStack, 16), pair.getSecond()));
				return;
			}
		}

		stacks.add(Pair.of(stack, pos));
	}

	public boolean shouldDestroy() {
		return this.destructionType != Explosion.DestructionType.KEEP;
	}

	public Map<PlayerEntity, Vec3d> getAffectedPlayers() {
		return this.affectedPlayers;
	}

	@Nullable
	private static LivingEntity getCausingEntity(@Nullable Entity from) {
		if (from == null) {
			return null;
		} else if (from instanceof TntEntity tntEntity) {
			return tntEntity.getOwner();
		} else if (from instanceof LivingEntity) {
			return (LivingEntity)from;
		} else {
			if (from instanceof ProjectileEntity projectileEntity) {
				Entity entity = projectileEntity.getOwner();
				if (entity instanceof LivingEntity) {
					return (LivingEntity)entity;
				}
			}

			return null;
		}
	}

	@Nullable
	public LivingEntity getCausingEntity() {
		return getCausingEntity(this.entity);
	}

	@Nullable
	public Entity getEntity() {
		return this.entity;
	}

	public void clearAffectedBlocks() {
		this.affectedBlocks.clear();
	}

	public List<BlockPos> getAffectedBlocks() {
		return this.affectedBlocks;
	}

	public Explosion.DestructionType getDestructionType() {
		return this.destructionType;
	}

	public ParticleEffect getParticle() {
		return this.particle;
	}

	public ParticleEffect getEmitterParticle() {
		return this.emitterParticle;
	}

	public SoundEvent getSoundEvent() {
		return this.soundEvent;
	}

	public static enum DestructionType {
		KEEP,
		DESTROY,
		DESTROY_WITH_DECAY,
		TRIGGER_BLOCK;
	}
}
