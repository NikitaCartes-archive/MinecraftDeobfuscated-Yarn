package net.minecraft.world.explosion;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
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
import net.minecraft.entity.projectile.ExplosiveProjectileEntity;
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
import net.minecraft.world.RayTraceContext;
import net.minecraft.world.World;

public class Explosion {
	private final boolean createFire;
	private final Explosion.DestructionType blockDestructionType;
	private final Random random = new Random();
	private final World world;
	private final double x;
	private final double y;
	private final double z;
	@Nullable
	private final Entity entity;
	private final float power;
	private DamageSource damageSource;
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

	public Explosion(
		World world, @Nullable Entity entity, double x, double y, double z, float power, boolean createFire, Explosion.DestructionType blockDestructionType
	) {
		this.world = world;
		this.entity = entity;
		this.power = power;
		this.x = x;
		this.y = y;
		this.z = z;
		this.createFire = createFire;
		this.blockDestructionType = blockDestructionType;
		this.damageSource = DamageSource.explosion(this);
	}

	public static float getExposure(Vec3d source, Entity entity) {
		Box box = entity.getBoundingBox();
		double d = 1.0 / ((box.x2 - box.x1) * 2.0 + 1.0);
		double e = 1.0 / ((box.y2 - box.y1) * 2.0 + 1.0);
		double f = 1.0 / ((box.z2 - box.z1) * 2.0 + 1.0);
		double g = (1.0 - Math.floor(1.0 / d) * d) / 2.0;
		double h = (1.0 - Math.floor(1.0 / f) * f) / 2.0;
		if (!(d < 0.0) && !(e < 0.0) && !(f < 0.0)) {
			int i = 0;
			int j = 0;

			for (float k = 0.0F; k <= 1.0F; k = (float)((double)k + d)) {
				for (float l = 0.0F; l <= 1.0F; l = (float)((double)l + e)) {
					for (float m = 0.0F; m <= 1.0F; m = (float)((double)m + f)) {
						double n = MathHelper.lerp((double)k, box.x1, box.x2);
						double o = MathHelper.lerp((double)l, box.y1, box.y2);
						double p = MathHelper.lerp((double)m, box.z1, box.z2);
						Vec3d vec3d = new Vec3d(n + g, o, p + h);
						if (entity.world.rayTrace(new RayTraceContext(vec3d, source, RayTraceContext.ShapeType.OUTLINE, RayTraceContext.FluidHandling.NONE, entity)).getType()
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
							if (!blockState.isAir() || !fluidState.isEmpty()) {
								float q = Math.max(blockState.getBlock().getBlastResistance(), fluidState.getBlastResistance());
								if (this.entity != null) {
									q = this.entity.getEffectiveExplosionResistance(this, this.world, blockPos, blockState, fluidState, q);
								}

								h -= (q + 0.3F) * 0.3F;
							}

							if (h > 0.0F && (this.entity == null || this.entity.canExplosionDestroyBlock(this, this.world, blockPos, blockState, h))) {
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
		float r = this.power * 2.0F;
		int k = MathHelper.floor(this.x - (double)r - 1.0);
		int lx = MathHelper.floor(this.x + (double)r + 1.0);
		int s = MathHelper.floor(this.y - (double)r - 1.0);
		int t = MathHelper.floor(this.y + (double)r + 1.0);
		int u = MathHelper.floor(this.z - (double)r - 1.0);
		int v = MathHelper.floor(this.z + (double)r + 1.0);
		List<Entity> list = this.world.getEntities(this.entity, new Box((double)k, (double)s, (double)u, (double)lx, (double)t, (double)v));
		Vec3d vec3d = new Vec3d(this.x, this.y, this.z);

		for (int w = 0; w < list.size(); w++) {
			Entity entity = (Entity)list.get(w);
			if (!entity.isImmuneToExplosion()) {
				double x = (double)(MathHelper.sqrt(entity.squaredDistanceTo(vec3d)) / r);
				if (x <= 1.0) {
					double y = entity.getX() - this.x;
					double z = entity.getEyeY() - this.y;
					double aa = entity.getZ() - this.z;
					double ab = (double)MathHelper.sqrt(y * y + z * z + aa * aa);
					if (ab != 0.0) {
						y /= ab;
						z /= ab;
						aa /= ab;
						double ac = (double)getExposure(vec3d, entity);
						double ad = (1.0 - x) * ac;
						entity.damage(this.getDamageSource(), (float)((int)((ad * ad + ad) / 2.0 * 7.0 * (double)r + 1.0)));
						double ae = ad;
						if (entity instanceof LivingEntity) {
							ae = ProtectionEnchantment.transformExplosionKnockback((LivingEntity)entity, ad);
						}

						entity.setVelocity(entity.getVelocity().add(y * ae, z * ae, aa * ae));
						if (entity instanceof PlayerEntity) {
							PlayerEntity playerEntity = (PlayerEntity)entity;
							if (!playerEntity.isSpectator() && (!playerEntity.isCreative() || !playerEntity.abilities.flying)) {
								this.affectedPlayers.put(playerEntity, new Vec3d(y * ad, z * ad, aa * ad));
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

		boolean bl2 = this.blockDestructionType != Explosion.DestructionType.NONE;
		if (bl) {
			if (!(this.power < 2.0F) && bl2) {
				this.world.addParticle(ParticleTypes.EXPLOSION_EMITTER, this.x, this.y, this.z, 1.0, 0.0, 0.0);
			} else {
				this.world.addParticle(ParticleTypes.EXPLOSION, this.x, this.y, this.z, 1.0, 0.0, 0.0);
			}
		}

		if (bl2) {
			ObjectArrayList<ItemStack> objectArrayList = new ObjectArrayList<>();
			List<BlockPos> list = Lists.<BlockPos>newArrayList();

			for (BlockPos blockPos : this.affectedBlocks) {
				BlockState blockState = this.world.getBlockState(blockPos);
				Block block = blockState.getBlock();
				if (!blockState.isAir()) {
					list.add(blockPos.toImmutable());
					this.world.getProfiler().push("explosion_blocks");
					if (block.shouldDropItemsOnExplosion(this) && this.world instanceof ServerWorld) {
						BlockEntity blockEntity = block.hasBlockEntity() ? this.world.getBlockEntity(blockPos) : null;
						LootContext.Builder builder = new LootContext.Builder((ServerWorld)this.world)
							.setRandom(this.world.random)
							.put(LootContextParameters.POSITION, blockPos)
							.put(LootContextParameters.TOOL, ItemStack.EMPTY)
							.putNullable(LootContextParameters.BLOCK_ENTITY, blockEntity)
							.putNullable(LootContextParameters.THIS_ENTITY, this.entity);
						if (this.blockDestructionType == Explosion.DestructionType.DESTROY) {
							builder.put(LootContextParameters.EXPLOSION_RADIUS, this.power);
						}

						blockState.getDroppedStacks(builder).forEach(itemStackx -> method_24023(objectArrayList, itemStackx));
					}

					this.world.setBlockState(blockPos, Blocks.AIR.getDefaultState(), 3);
					block.onDestroyedByExplosion(this.world, blockPos, this);
					this.world.getProfiler().pop();
				}
			}

			int i = list.size();

			for (ItemStack itemStack : objectArrayList) {
				Block.dropStack(this.world, (BlockPos)list.get(this.random.nextInt(i)), itemStack);
			}
		}

		if (this.createFire) {
			for (BlockPos blockPos2 : this.affectedBlocks) {
				if (this.random.nextInt(3) == 0
					&& this.world.getBlockState(blockPos2).isAir()
					&& this.world.getBlockState(blockPos2.down()).isFullOpaque(this.world, blockPos2.down())) {
					this.world.setBlockState(blockPos2, Blocks.FIRE.getDefaultState());
				}
			}
		}
	}

	private static void method_24023(ObjectArrayList<ItemStack> objectArrayList, ItemStack itemStack) {
		int i = objectArrayList.size();

		for (int j = 0; j < i; j++) {
			ItemStack itemStack2 = objectArrayList.get(j);
			if (ItemEntity.method_24017(itemStack2, itemStack)) {
				ItemStack itemStack3 = ItemEntity.method_24018(itemStack2, itemStack, 16);
				objectArrayList.set(j, itemStack3);
				if (itemStack.isEmpty()) {
					return;
				}
			}
		}

		objectArrayList.add(itemStack);
	}

	public DamageSource getDamageSource() {
		return this.damageSource;
	}

	public void setDamageSource(DamageSource damageSource) {
		this.damageSource = damageSource;
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
			return this.entity instanceof ExplosiveProjectileEntity ? ((ExplosiveProjectileEntity)this.entity).owner : null;
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
