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
import net.minecraft.entity.PrimedTNTEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.loot.context.LootContext;
import net.minecraft.world.loot.context.Parameters;

public class Explosion {
	private final boolean createFire;
	private final boolean destroyBlocks;
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
		this(world, entity, d, e, f, g, false, true, list);
	}

	@Environment(EnvType.CLIENT)
	public Explosion(World world, @Nullable Entity entity, double d, double e, double f, float g, boolean bl, boolean bl2, List<BlockPos> list) {
		this(world, entity, d, e, f, g, bl, bl2);
		this.affectedBlocks.addAll(list);
	}

	public Explosion(World world, @Nullable Entity entity, double d, double e, double f, float g, boolean bl, boolean bl2) {
		this.world = world;
		this.entity = entity;
		this.power = g;
		this.x = d;
		this.y = e;
		this.z = f;
		this.createFire = bl;
		this.destroyBlocks = bl2;
		this.damageSource = DamageSource.explosion(this);
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
		List<Entity> list = this.world.getVisibleEntities(this.entity, new BoundingBox((double)k, (double)s, (double)u, (double)lx, (double)t, (double)v));
		Vec3d vec3d = new Vec3d(this.x, this.y, this.z);

		for (int w = 0; w < list.size(); w++) {
			Entity entity = (Entity)list.get(w);
			if (!entity.isImmuneToExplosion()) {
				double x = entity.distanceTo(this.x, this.y, this.z) / (double)r;
				if (x <= 1.0) {
					double y = entity.x - this.x;
					double z = entity.y + (double)entity.getEyeHeight() - this.y;
					double aa = entity.z - this.z;
					double ab = (double)MathHelper.sqrt(y * y + z * z + aa * aa);
					if (ab != 0.0) {
						y /= ab;
						z /= ab;
						aa /= ab;
						double ac = (double)this.world.method_8542(vec3d, entity.getBoundingBox());
						double ad = (1.0 - x) * ac;
						entity.damage(this.getDamageSource(), (float)((int)((ad * ad + ad) / 2.0 * 7.0 * (double)r + 1.0)));
						double ae = ad;
						if (entity instanceof LivingEntity) {
							ae = ProtectionEnchantment.method_8237((LivingEntity)entity, ad);
						}

						entity.velocityX += y * ae;
						entity.velocityY += z * ae;
						entity.velocityZ += aa * ae;
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
		if (!(this.power < 2.0F) && this.destroyBlocks) {
			this.world.method_8406(ParticleTypes.field_11221, this.x, this.y, this.z, 1.0, 0.0, 0.0);
		} else {
			this.world.method_8406(ParticleTypes.field_11236, this.x, this.y, this.z, 1.0, 0.0, 0.0);
		}

		if (this.destroyBlocks) {
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
					this.world.method_8406(ParticleTypes.field_11203, (d + this.x) / 2.0, (e + this.y) / 2.0, (f + this.z) / 2.0, g, h, i);
					this.world.method_8406(ParticleTypes.field_11251, d, e, f, g, h, i);
				}

				if (!blockState.isAir()) {
					if (block.shouldDropItemsOnExplosion(this) && this.world instanceof ServerWorld) {
						BlockEntity blockEntity = block.hasBlockEntity() ? this.world.getBlockEntity(blockPos) : null;
						LootContext.Builder builder = new LootContext.Builder((ServerWorld)this.world)
							.setRandom(this.world.random)
							.put(Parameters.field_1232, blockPos)
							.put(Parameters.field_1229, ItemStack.EMPTY)
							.put(Parameters.field_1225, this.power)
							.putNullable(Parameters.field_1228, blockEntity);
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
					&& this.world.getBlockState(blockPos.down()).method_11598(this.world, blockPos.down())
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
		} else if (this.entity instanceof PrimedTNTEntity) {
			return ((PrimedTNTEntity)this.entity).getCausingEntity();
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
}
