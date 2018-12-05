package net.minecraft.entity.ai.goal;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.entity.mob.SkeletonEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BoundingBox;

public class FollowTargetGoal<T extends LivingEntity> extends TrackTargetGoal {
	protected final Class<T> field_6643;
	private final int field_6641;
	protected final FollowTargetGoal.class_1401 field_6645;
	protected final Predicate<? super T> field_6642;
	protected T field_6644;

	public FollowTargetGoal(MobEntityWithAi mobEntityWithAi, Class<T> class_, boolean bl) {
		this(mobEntityWithAi, class_, bl, false);
	}

	public FollowTargetGoal(MobEntityWithAi mobEntityWithAi, Class<T> class_, boolean bl, boolean bl2) {
		this(mobEntityWithAi, class_, 10, bl, bl2, null);
	}

	public FollowTargetGoal(MobEntityWithAi mobEntityWithAi, Class<T> class_, int i, boolean bl, boolean bl2, @Nullable Predicate<? super T> predicate) {
		super(mobEntityWithAi, bl, bl2);
		this.field_6643 = class_;
		this.field_6641 = i;
		this.field_6645 = new FollowTargetGoal.class_1401(mobEntityWithAi);
		this.setControlBits(1);
		this.field_6642 = livingEntity -> {
			if (livingEntity == null) {
				return false;
			} else if (predicate != null && !predicate.test(livingEntity)) {
				return false;
			} else {
				return !EntityPredicates.EXCEPT_SPECTATOR.test(livingEntity) ? false : this.canTrack(livingEntity, false);
			}
		};
	}

	@Override
	public boolean canStart() {
		if (this.field_6641 > 0 && this.entity.getRand().nextInt(this.field_6641) != 0) {
			return false;
		} else if (this.field_6643 != PlayerEntity.class && this.field_6643 != ServerPlayerEntity.class) {
			List<T> list = this.entity.world.getEntities(this.field_6643, this.method_6321(this.getFollowRange()), this.field_6642);
			if (list.isEmpty()) {
				return false;
			} else {
				Collections.sort(list, this.field_6645);
				this.field_6644 = (T)list.get(0);
				return true;
			}
		} else {
			this.field_6644 = (T)this.entity
				.world
				.method_8439(
					this.entity.x,
					this.entity.y + (double)this.entity.getEyeHeight(),
					this.entity.z,
					this.getFollowRange(),
					this.getFollowRange(),
					new Function<PlayerEntity, Double>() {
						@Nullable
						public Double method_6323(@Nullable PlayerEntity playerEntity) {
							ItemStack itemStack = playerEntity.getEquippedStack(EquipmentSlot.HEAD);
							return (!(FollowTargetGoal.this.entity instanceof SkeletonEntity) || itemStack.getItem() != Items.field_8398)
									&& (!(FollowTargetGoal.this.entity instanceof ZombieEntity) || itemStack.getItem() != Items.field_8470)
									&& (!(FollowTargetGoal.this.entity instanceof CreeperEntity) || itemStack.getItem() != Items.field_8681)
								? 1.0
								: 0.5;
						}
					},
					this.field_6642
				);
			return this.field_6644 != null;
		}
	}

	protected BoundingBox method_6321(double d) {
		return this.entity.getBoundingBox().expand(d, 4.0, d);
	}

	@Override
	public void start() {
		this.entity.setTarget(this.field_6644);
		super.start();
	}

	public static class class_1401 implements Comparator<Entity> {
		private final Entity field_6646;

		public class_1401(Entity entity) {
			this.field_6646 = entity;
		}

		public int method_6322(Entity entity, Entity entity2) {
			double d = this.field_6646.squaredDistanceTo(entity);
			double e = this.field_6646.squaredDistanceTo(entity2);
			if (d < e) {
				return -1;
			} else {
				return d > e ? 1 : 0;
			}
		}
	}
}
