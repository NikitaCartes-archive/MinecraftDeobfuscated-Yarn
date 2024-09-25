package net.minecraft.world.explosion;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Util;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.profiler.Profilers;
import net.minecraft.world.GameRules;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.event.GameEvent;

public class ExplosionImpl implements Explosion {
	private static final ExplosionBehavior DEFAULT_BEHAVIOR = new ExplosionBehavior();
	private static final int field_52618 = 16;
	private static final float field_52619 = 2.0F;
	private final boolean createFire;
	private final Explosion.DestructionType destructionType;
	private final ServerWorld world;
	private final Vec3d pos;
	@Nullable
	private final Entity entity;
	private final float power;
	private final DamageSource damageSource;
	private final ExplosionBehavior behavior;
	private final Map<PlayerEntity, Vec3d> knockbackByPlayer = new HashMap();

	public ExplosionImpl(
		ServerWorld world,
		@Nullable Entity entity,
		@Nullable DamageSource damageSource,
		@Nullable ExplosionBehavior behavior,
		Vec3d pos,
		float power,
		boolean createFire,
		Explosion.DestructionType destructionType
	) {
		this.world = world;
		this.entity = entity;
		this.power = power;
		this.pos = pos;
		this.createFire = createFire;
		this.destructionType = destructionType;
		this.damageSource = damageSource == null ? world.getDamageSources().explosion(this) : damageSource;
		this.behavior = behavior == null ? this.makeBehavior(entity) : behavior;
	}

	private ExplosionBehavior makeBehavior(@Nullable Entity entity) {
		return (ExplosionBehavior)(entity == null ? DEFAULT_BEHAVIOR : new EntityExplosionBehavior(entity));
	}

	public static float calculateReceivedDamage(Vec3d pos, Entity entity) {
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
						if (entity.getWorld().raycast(new RaycastContext(vec3d, pos, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, entity)).getType()
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

	@Override
	public float getPower() {
		return this.power;
	}

	@Override
	public Vec3d getPosition() {
		return this.pos;
	}

	private List<BlockPos> getBlocksToDestroy() {
		Set<BlockPos> set = new HashSet();
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
						double m = this.pos.x;
						double n = this.pos.y;
						double o = this.pos.z;

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

		return new ObjectArrayList<>(set);
	}

	private void damageEntities() {
		float f = this.power * 2.0F;
		int i = MathHelper.floor(this.pos.x - (double)f - 1.0);
		int j = MathHelper.floor(this.pos.x + (double)f + 1.0);
		int k = MathHelper.floor(this.pos.y - (double)f - 1.0);
		int l = MathHelper.floor(this.pos.y + (double)f + 1.0);
		int m = MathHelper.floor(this.pos.z - (double)f - 1.0);
		int n = MathHelper.floor(this.pos.z + (double)f + 1.0);

		for (Entity entity : this.world.getOtherEntities(this.entity, new Box((double)i, (double)k, (double)m, (double)j, (double)l, (double)n))) {
			if (!entity.isImmuneToExplosion(this)) {
				double d = Math.sqrt(entity.squaredDistanceTo(this.pos)) / (double)f;
				if (d <= 1.0) {
					double e = entity.getX() - this.pos.x;
					double g = (entity instanceof TntEntity ? entity.getY() : entity.getEyeY()) - this.pos.y;
					double h = entity.getZ() - this.pos.z;
					double o = Math.sqrt(e * e + g * g + h * h);
					if (o != 0.0) {
						e /= o;
						g /= o;
						h /= o;
						boolean bl = this.behavior.shouldDamage(this, entity);
						float p = this.behavior.getKnockbackModifier(entity);
						float q = !bl && p == 0.0F ? 0.0F : calculateReceivedDamage(this.pos, entity);
						if (bl) {
							entity.damage(this.world, this.damageSource, this.behavior.calculateDamage(this, entity, q));
						}

						double r = (1.0 - d) * (double)q * (double)p;
						double s;
						if (entity instanceof LivingEntity livingEntity) {
							s = r * (1.0 - livingEntity.getAttributeValue(EntityAttributes.EXPLOSION_KNOCKBACK_RESISTANCE));
						} else {
							s = r;
						}

						e *= s;
						g *= s;
						h *= s;
						Vec3d vec3d = new Vec3d(e, g, h);
						entity.setVelocity(entity.getVelocity().add(vec3d));
						if (entity instanceof PlayerEntity) {
							PlayerEntity playerEntity = (PlayerEntity)entity;
							if (!playerEntity.isSpectator() && (!playerEntity.isCreative() || !playerEntity.getAbilities().flying)) {
								this.knockbackByPlayer.put(playerEntity, vec3d);
							}
						}

						entity.onExplodedBy(this.entity);
					}
				}
			}
		}
	}

	private void destroyBlocks(List<BlockPos> positions) {
		List<ExplosionImpl.DroppedItem> list = new ArrayList();
		Util.shuffle(positions, this.world.random);

		for (BlockPos blockPos : positions) {
			this.world.getBlockState(blockPos).onExploded(this.world, blockPos, this, (item, pos) -> addDroppedItem(list, item, pos));
		}

		for (ExplosionImpl.DroppedItem droppedItem : list) {
			Block.dropStack(this.world, droppedItem.pos, droppedItem.item);
		}
	}

	private void createFire(List<BlockPos> positions) {
		for (BlockPos blockPos : positions) {
			if (this.world.random.nextInt(3) == 0 && this.world.getBlockState(blockPos).isAir() && this.world.getBlockState(blockPos.down()).isOpaqueFullCube()) {
				this.world.setBlockState(blockPos, AbstractFireBlock.getState(this.world, blockPos));
			}
		}
	}

	public void explode() {
		this.world.emitGameEvent(this.entity, GameEvent.EXPLODE, this.pos);
		List<BlockPos> list = this.getBlocksToDestroy();
		this.damageEntities();
		if (this.shouldDestroyBlocks()) {
			Profiler profiler = Profilers.get();
			profiler.push("explosion_blocks");
			this.destroyBlocks(list);
			profiler.pop();
		}

		if (this.createFire) {
			this.createFire(list);
		}
	}

	private static void addDroppedItem(List<ExplosionImpl.DroppedItem> droppedItemsOut, ItemStack item, BlockPos pos) {
		for (ExplosionImpl.DroppedItem droppedItem : droppedItemsOut) {
			droppedItem.merge(item);
			if (item.isEmpty()) {
				return;
			}
		}

		droppedItemsOut.add(new ExplosionImpl.DroppedItem(pos, item));
	}

	private boolean shouldDestroyBlocks() {
		return this.destructionType != Explosion.DestructionType.KEEP;
	}

	public Map<PlayerEntity, Vec3d> getKnockbackByPlayer() {
		return this.knockbackByPlayer;
	}

	@Override
	public ServerWorld getWorld() {
		return this.world;
	}

	@Nullable
	@Override
	public LivingEntity getCausingEntity() {
		return Explosion.getCausingEntity(this.entity);
	}

	@Nullable
	@Override
	public Entity getEntity() {
		return this.entity;
	}

	@Override
	public Explosion.DestructionType getDestructionType() {
		return this.destructionType;
	}

	@Override
	public boolean canTriggerBlocks() {
		if (this.destructionType != Explosion.DestructionType.TRIGGER_BLOCK) {
			return false;
		} else {
			return this.entity != null && this.entity.getType() == EntityType.BREEZE_WIND_CHARGE
				? this.world.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING)
				: true;
		}
	}

	@Override
	public boolean preservesDecorativeEntities() {
		boolean bl = this.world.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING);
		boolean bl2 = this.entity == null || !this.entity.isTouchingWater();
		boolean bl3 = this.entity == null || this.entity.getType() != EntityType.BREEZE_WIND_CHARGE && this.entity.getType() != EntityType.WIND_CHARGE;
		return bl ? bl2 && bl3 : this.destructionType.destroysBlocks() && bl2 && bl3;
	}

	public boolean isSmall() {
		return this.power < 2.0F || !this.shouldDestroyBlocks();
	}

	static class DroppedItem {
		final BlockPos pos;
		ItemStack item;

		DroppedItem(BlockPos pos, ItemStack item) {
			this.pos = pos;
			this.item = item;
		}

		public void merge(ItemStack other) {
			if (ItemEntity.canMerge(this.item, other)) {
				this.item = ItemEntity.merge(this.item, other, 16);
			}
		}
	}
}
