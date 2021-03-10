package net.minecraft.world.explosion;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
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
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class Explosion {
	private static final ExplosionBehavior DEFAULT_BEHAVIOR = new ExplosionBehavior();
	private final boolean createFire;
	private final Explosion.DestructionType destructionType;
	private final Random random = new Random();
	private final World world;
	private final double x;
	private final double y;
	private final double z;
	@Nullable
	private final Entity entity;
	private final float power;
	private final DamageSource damageSource;
	private final ExplosionBehavior behavior;
	private final List<BlockPos> affectedBlocks = Lists.<BlockPos>newArrayList();
	private final Map<PlayerEntity, Vec3d> affectedPlayers = Maps.<PlayerEntity, Vec3d>newHashMap();

	@Environment(EnvType.CLIENT)
	public Explosion(World world, @Nullable Entity entity, double x, double y, double z, float power, List<BlockPos> affectedBlocks) {
		this(world, entity, x, y, z, power, false, Explosion.DestructionType.DESTROY, affectedBlocks);
	}

	@Environment(EnvType.CLIENT)
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

	@Environment(EnvType.CLIENT)
	public Explosion(
		World world, @Nullable Entity entity, double x, double y, double z, float power, boolean createFire, Explosion.DestructionType destructionType
	) {
		this(world, entity, null, null, x, y, z, power, createFire, destructionType);
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
		Explosion.DestructionType destructionType
	) {
		this.world = world;
		this.entity = entity;
		this.power = power;
		this.x = x;
		this.y = y;
		this.z = z;
		this.createFire = createFire;
		this.destructionType = destructionType;
		this.damageSource = damageSource == null ? DamageSource.explosion(this) : damageSource;
		this.behavior = behavior == null ? this.chooseBehavior(entity) : behavior;
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

			for (float k = 0.0F; k <= 1.0F; k = (float)((double)k + d)) {
				for (float l = 0.0F; l <= 1.0F; l = (float)((double)l + e)) {
					for (float m = 0.0F; m <= 1.0F; m = (float)((double)m + f)) {
						double n = MathHelper.lerp((double)k, box.minX, box.maxX);
						double o = MathHelper.lerp((double)l, box.minY, box.maxY);
						double p = MathHelper.lerp((double)m, box.minZ, box.maxZ);
						Vec3d vec3d = new Vec3d(n + g, o, p + h);
						if (entity.world.raycast(new RaycastContext(vec3d, source, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, entity)).getType()
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

	public void collectBlocksAndDamageEntities() {
		this.world.emitGameEvent(this.entity, GameEvent.EXPLODE, new BlockPos(this.x, this.y, this.z));
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
							BlockPos blockPos = new BlockPos(m, n, o);
							BlockState blockState = this.world.getBlockState(blockPos);
							FluidState fluidState = this.world.getFluidState(blockPos);
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

		for (int v = 0; v < list.size(); v++) {
			Entity entity = (Entity)list.get(v);
			if (!entity.isImmuneToExplosion()) {
				double w = (double)(MathHelper.sqrt(entity.squaredDistanceTo(vec3d)) / q);
				if (w <= 1.0) {
					double x = entity.getX() - this.x;
					double y = (entity instanceof TntEntity ? entity.getY() : entity.getEyeY()) - this.y;
					double z = entity.getZ() - this.z;
					double aa = (double)MathHelper.sqrt(x * x + y * y + z * z);
					if (aa != 0.0) {
						x /= aa;
						y /= aa;
						z /= aa;
						double ab = (double)getExposure(vec3d, entity);
						double ac = (1.0 - w) * ab;
						entity.damage(this.getDamageSource(), (float)((int)((ac * ac + ac) / 2.0 * 7.0 * (double)q + 1.0)));
						double ad = ac;
						if (entity instanceof LivingEntity) {
							ad = ProtectionEnchantment.transformExplosionKnockback((LivingEntity)entity, ac);
						}

						entity.setVelocity(entity.getVelocity().add(x * ad, y * ad, z * ad));
						if (entity instanceof PlayerEntity) {
							PlayerEntity playerEntity = (PlayerEntity)entity;
							if (!playerEntity.isSpectator() && (!playerEntity.isCreative() || !playerEntity.getAbilities().flying)) {
								this.affectedPlayers.put(playerEntity, new Vec3d(x * ac, y * ac, z * ac));
							}
						}
					}
				}
			}
		}
	}

	public void affectWorld(boolean bl) {
		if (this.world.isClient) {
			this.world
				.playSound(
					this.x,
					this.y,
					this.z,
					SoundEvents.ENTITY_GENERIC_EXPLODE,
					SoundCategory.BLOCKS,
					4.0F,
					(1.0F + (this.world.random.nextFloat() - this.world.random.nextFloat()) * 0.2F) * 0.7F,
					false
				);
		}

		boolean bl2 = this.destructionType != Explosion.DestructionType.NONE;
		if (bl) {
			if (!(this.power < 2.0F) && bl2) {
				this.world.addParticle(ParticleTypes.EXPLOSION_EMITTER, this.x, this.y, this.z, 1.0, 0.0, 0.0);
			} else {
				this.world.addParticle(ParticleTypes.EXPLOSION, this.x, this.y, this.z, 1.0, 0.0, 0.0);
			}
		}

		if (bl2) {
			ObjectArrayList<Pair<ItemStack, BlockPos>> objectArrayList = new ObjectArrayList<>();
			Collections.shuffle(this.affectedBlocks, this.world.random);

			for (BlockPos blockPos : this.affectedBlocks) {
				BlockState blockState = this.world.getBlockState(blockPos);
				Block block = blockState.getBlock();
				if (!blockState.isAir()) {
					BlockPos blockPos2 = blockPos.toImmutable();
					this.world.getProfiler().push("explosion_blocks");
					if (block.shouldDropItemsOnExplosion(this) && this.world instanceof ServerWorld) {
						BlockEntity blockEntity = blockState.hasBlockEntity() ? this.world.getBlockEntity(blockPos) : null;
						LootContext.Builder builder = new LootContext.Builder((ServerWorld)this.world)
							.random(this.world.random)
							.parameter(LootContextParameters.ORIGIN, Vec3d.ofCenter(blockPos))
							.parameter(LootContextParameters.TOOL, ItemStack.EMPTY)
							.optionalParameter(LootContextParameters.BLOCK_ENTITY, blockEntity)
							.optionalParameter(LootContextParameters.THIS_ENTITY, this.entity);
						if (this.destructionType == Explosion.DestructionType.DESTROY) {
							builder.parameter(LootContextParameters.EXPLOSION_RADIUS, this.power);
						}

						blockState.getDroppedStacks(builder).forEach(stack -> tryMergeStack(objectArrayList, stack, blockPos2));
					}

					this.world.setBlockState(blockPos, Blocks.AIR.getDefaultState(), 3);
					block.onDestroyedByExplosion(this.world, blockPos, this);
					this.world.getProfiler().pop();
				}
			}

			for (Pair<ItemStack, BlockPos> pair : objectArrayList) {
				Block.dropStack(this.world, pair.getSecond(), pair.getFirst());
			}
		}

		if (this.createFire) {
			for (BlockPos blockPos3 : this.affectedBlocks) {
				if (this.random.nextInt(3) == 0
					&& this.world.getBlockState(blockPos3).isAir()
					&& this.world.getBlockState(blockPos3.down()).isOpaqueFullCube(this.world, blockPos3.down())) {
					this.world.setBlockState(blockPos3, AbstractFireBlock.getState(this.world, blockPos3));
				}
			}
		}
	}

	private static void tryMergeStack(ObjectArrayList<Pair<ItemStack, BlockPos>> stacks, ItemStack stack, BlockPos pos) {
		int i = stacks.size();

		for (int j = 0; j < i; j++) {
			Pair<ItemStack, BlockPos> pair = stacks.get(j);
			ItemStack itemStack = pair.getFirst();
			if (ItemEntity.canMerge(itemStack, stack)) {
				ItemStack itemStack2 = ItemEntity.merge(itemStack, stack, 16);
				stacks.set(j, Pair.of(itemStack2, pair.getSecond()));
				if (stack.isEmpty()) {
					return;
				}
			}
		}

		stacks.add(Pair.of(stack, pos));
	}

	public DamageSource getDamageSource() {
		return this.damageSource;
	}

	public Map<PlayerEntity, Vec3d> getAffectedPlayers() {
		return this.affectedPlayers;
	}

	@Nullable
	public LivingEntity getCausingEntity() {
		if (this.entity == null) {
			return null;
		} else if (this.entity instanceof TntEntity) {
			return ((TntEntity)this.entity).getCausingEntity();
		} else if (this.entity instanceof LivingEntity) {
			return (LivingEntity)this.entity;
		} else {
			if (this.entity instanceof ProjectileEntity) {
				Entity entity = ((ProjectileEntity)this.entity).getOwner();
				if (entity instanceof LivingEntity) {
					return (LivingEntity)entity;
				}
			}

			return null;
		}
	}

	public void clearAffectedBlocks() {
		this.affectedBlocks.clear();
	}

	public List<BlockPos> getAffectedBlocks() {
		return this.affectedBlocks;
	}

	public static enum DestructionType {
		NONE,
		BREAK,
		DESTROY;
	}
}
