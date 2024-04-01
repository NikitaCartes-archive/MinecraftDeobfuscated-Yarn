package net.minecraft.entity.mob;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.ai.goal.ZombieAttackGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public class GiantEntity extends HostileEntity {
	public GiantEntity(EntityType<? extends GiantEntity> entityType, World world) {
		super(entityType, world);
	}

	public static DefaultAttributeContainer.Builder createGiantAttributes() {
		return HostileEntity.createHostileAttributes()
			.add(EntityAttributes.GENERIC_MAX_HEALTH, 100.0)
			.add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.5)
			.add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 20.0)
			.add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, 50.0);
	}

	@Override
	public float getPathfindingFavor(BlockPos pos, WorldView world) {
		return world.getPhototaxisFavor(pos);
	}

	@Override
	public boolean hasPotatoForm() {
		return true;
	}

	@Override
	protected void initGoals() {
		this.goalSelector.add(2, new ZombieAttackGoal<>(this, 1.0, false));
		this.goalSelector.add(7, new WanderAroundFarGoal(this, 1.0));
		this.targetSelector.add(1, new RevengeGoal(this).setGroupRevenge(ZombifiedPiglinEntity.class));
		this.targetSelector.add(2, new ActiveTargetGoal(this, PlayerEntity.class, true));
		this.targetSelector.add(3, new ActiveTargetGoal(this, MerchantEntity.class, false));
		this.targetSelector.add(3, new ActiveTargetGoal(this, IronGolemEntity.class, true));
		this.targetSelector.add(5, new ActiveTargetGoal(this, TurtleEntity.class, 10, true, false, TurtleEntity.BABY_TURTLE_ON_LAND_FILTER));
		this.goalSelector.add(8, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
		this.goalSelector.add(8, new LookAroundGoal(this));
	}

	@Override
	public boolean tryAttack(Entity target) {
		boolean bl = super.tryAttack(target);
		if (bl) {
			float f = this.getWorld().getLocalDifficulty(this.getBlockPos()).getLocalDifficulty();
			if (this.getMainHandStack().isEmpty() && this.isOnFire() && this.random.nextFloat() < f * 0.3F) {
				target.setOnFireFor(2 * (int)f);
			}
		}

		return bl;
	}
}
