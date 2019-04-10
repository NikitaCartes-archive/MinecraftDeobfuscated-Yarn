package net.minecraft.world.explosion;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
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
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.PrimedTntEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RayTraceContext;
import net.minecraft.world.World;
import net.minecraft.world.loot.context.LootContext;
import net.minecraft.world.loot.context.LootContextParameters;

public class Explosion {
	private final boolean createFire;
	private final Explosion.DestructionType blockDestructionType;
	private final Random random = new Random();
	private final World world;
	private final double x;
	private final double y;
	private final double z;
	private final Entity entity;
	private final float power;
	private DamageSource damageSource;
	private final List<BlockPos> affectedBlocks = Lists.<BlockPos>newArrayList();
	private final Map<PlayerEntity, Vec3d> affectedPlayers = Maps.<PlayerEntity, Vec3d>newHashMap();

	@Environment(EnvType.CLIENT)
	public Explosion(World world, @Nullable Entity entity, double d, double e, double f, float g, List<BlockPos> list) {
		this(world, entity, d, e, f, g, false, Explosion.DestructionType.field_18687, list);
	}

	@Environment(EnvType.CLIENT)
	public Explosion(
		World world, @Nullable Entity entity, double d, double e, double f, float g, boolean bl, Explosion.DestructionType destructionType, List<BlockPos> list
	) {
		this(world, entity, d, e, f, g, bl, destructionType);
		this.affectedBlocks.addAll(list);
	}

	public Explosion(World world, @Nullable Entity entity, double d, double e, double f, float g, boolean bl, Explosion.DestructionType destructionType) {
		this.world = world;
		this.entity = entity;
		this.power = g;
		this.x = d;
		this.y = e;
		this.z = f;
		this.createFire = bl;
		this.blockDestructionType = destructionType;
		this.damageSource = DamageSource.explosion(this);
	}

	public static float method_17752(Vec3d vec3d, Entity entity) {
		BoundingBox boundingBox = entity.getBoundingBox();
		double d = 1.0 / ((boundingBox.maxX - boundingBox.minX) * 2.0 + 1.0);
		double e = 1.0 / ((boundingBox.maxY - boundingBox.minY) * 2.0 + 1.0);
		double f = 1.0 / ((boundingBox.maxZ - boundingBox.minZ) * 2.0 + 1.0);
		double g = (1.0 - Math.floor(1.0 / d) * d) / 2.0;
		double h = (1.0 - Math.floor(1.0 / f) * f) / 2.0;
		if (!(d < 0.0) && !(e < 0.0) && !(f < 0.0)) {
			int i = 0;
			int j = 0;

			for (float k = 0.0F; k <= 1.0F; k = (float)((double)k + d)) {
				for (float l = 0.0F; l <= 1.0F; l = (float)((double)l + e)) {
					for (float m = 0.0F; m <= 1.0F; m = (float)((double)m + f)) {
						double n = MathHelper.lerp((double)k, boundingBox.minX, boundingBox.maxX);
						double o = MathHelper.lerp((double)l, boundingBox.minY, boundingBox.maxY);
						double p = MathHelper.lerp((double)m, boundingBox.minZ, boundingBox.maxZ);
						Vec3d vec3d2 = new Vec3d(n + g, o, p + h);
						if (entity.world
								.rayTrace(new RayTraceContext(vec3d2, vec3d, RayTraceContext.ShapeType.field_17559, RayTraceContext.FluidHandling.NONE, entity))
								.getType()
							== HitResult.Type.NONE) {
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
		List<Entity> list = this.world.getEntities(this.entity, new BoundingBox((double)k, (double)s, (double)u, (double)lx, (double)t, (double)v));
		Vec3d vec3d = new Vec3d(this.x, this.y, this.z);

		for (int w = 0; w < list.size(); w++) {
			Entity entity = (Entity)list.get(w);
			if (!entity.isImmuneToExplosion()) {
				double x = (double)(MathHelper.sqrt(entity.squaredDistanceTo(new Vec3d(this.x, this.y, this.z))) / r);
				if (x <= 1.0) {
					double y = entity.x - this.x;
					double z = entity.y + (double)entity.getStandingEyeHeight() - this.y;
					double aa = entity.z - this.z;
					double ab = (double)MathHelper.sqrt(y * y + z * z + aa * aa);
					if (ab != 0.0) {
						y /= ab;
						z /= ab;
						aa /= ab;
						double ac = (double)method_17752(vec3d, entity);
						double ad = (1.0 - x) * ac;
						entity.damage(this.getDamageSource(), (float)((int)((ad * ad + ad) / 2.0 * 7.0 * (double)r + 1.0)));
						double ae = ad;
						if (entity instanceof LivingEntity) {
							ae = ProtectionEnchantment.method_8237((LivingEntity)entity, ad);
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
		this.world
			.playSound(
				null,
				this.x,
				this.y,
				this.z,
				SoundEvents.field_15152,
				SoundCategory.field_15245,
				4.0F,
				(1.0F + (this.world.random.nextFloat() - this.world.random.nextFloat()) * 0.2F) * 0.7F
			);
		boolean bl2 = this.blockDestructionType != Explosion.DestructionType.field_18685;
		if (!(this.power < 2.0F) && bl2) {
			this.world.addParticle(ParticleTypes.field_11221, this.x, this.y, this.z, 1.0, 0.0, 0.0);
		} else {
			this.world.addParticle(ParticleTypes.field_11236, this.x, this.y, this.z, 1.0, 0.0, 0.0);
		}

		if (bl2) {
			for (BlockPos blockPos : this.affectedBlocks) {
				BlockState blockState = this.world.getBlockState(blockPos);
				Block block = blockState.getBlock();
				if (bl) {
					double d = (double)((float)blockPos.getX() + this.world.random.nextFloat());
					double e = (double)((float)blockPos.getY() + this.world.random.nextFloat());
					double f = (double)((float)blockPos.getZ() + this.world.random.nextFloat());
					double g = d - this.x;
					double h = e - this.y;
					double i = f - this.z;
					double j = (double)MathHelper.sqrt(g * g + h * h + i * i);
					g /= j;
					h /= j;
					i /= j;
					double k = 0.5 / (j / (double)this.power + 0.1);
					k *= (double)(this.world.random.nextFloat() * this.world.random.nextFloat() + 0.3F);
					g *= k;
					h *= k;
					i *= k;
					this.world.addParticle(ParticleTypes.field_11203, (d + this.x) / 2.0, (e + this.y) / 2.0, (f + this.z) / 2.0, g, h, i);
					this.world.addParticle(ParticleTypes.field_11251, d, e, f, g, h, i);
				}

				if (!blockState.isAir()) {
					if (block.shouldDropItemsOnExplosion(this) && this.world instanceof ServerWorld) {
						BlockEntity blockEntity = block.hasBlockEntity() ? this.world.getBlockEntity(blockPos) : null;
						LootContext.Builder builder = new LootContext.Builder((ServerWorld)this.world)
							.setRandom(this.world.random)
							.put(LootContextParameters.field_1232, blockPos)
							.put(LootContextParameters.field_1229, ItemStack.EMPTY)
							.putNullable(LootContextParameters.field_1228, blockEntity);
						if (this.blockDestructionType == Explosion.DestructionType.field_18687) {
							builder.put(LootContextParameters.field_1225, this.power);
						}

						Block.dropStacks(blockState, builder);
					}

					this.world.setBlockState(blockPos, Blocks.field_10124.getDefaultState(), 3);
					block.onDestroyedByExplosion(this.world, blockPos, this);
				}
			}
		}

		if (this.createFire) {
			for (BlockPos blockPos : this.affectedBlocks) {
				if (this.world.getBlockState(blockPos).isAir()
					&& this.world.getBlockState(blockPos.down()).isFullOpaque(this.world, blockPos.down())
					&& this.random.nextInt(3) == 0) {
					this.world.setBlockState(blockPos, Blocks.field_10036.getDefaultState());
				}
			}
		}
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
		} else if (this.entity instanceof PrimedTntEntity) {
			return ((PrimedTntEntity)this.entity).getCausingEntity();
		} else {
			return this.entity instanceof LivingEntity ? (LivingEntity)this.entity : null;
		}
	}

	public void clearAffectedBlocks() {
		this.affectedBlocks.clear();
	}

	public List<BlockPos> getAffectedBlocks() {
		return this.affectedBlocks;
	}

	public static enum DestructionType {
		field_18685,
		field_18686,
		field_18687;
	}
}
