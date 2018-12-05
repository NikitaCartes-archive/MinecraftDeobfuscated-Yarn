package net.minecraft.entity.ai.goal;

import net.minecraft.entity.ai.pathing.EntityMobNavigation;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;

public class TemptGoal extends Goal {
	protected final MobEntityWithAi field_6616;
	private final double field_6615;
	private double field_6614;
	private double field_6611;
	private double field_6621;
	private double field_6619;
	private double field_6618;
	protected PlayerEntity field_6617;
	private int field_6612;
	private boolean field_6613;
	private final Ingredient field_6622;
	private final boolean field_6620;

	public TemptGoal(MobEntityWithAi mobEntityWithAi, double d, Ingredient ingredient, boolean bl) {
		this(mobEntityWithAi, d, bl, ingredient);
	}

	public TemptGoal(MobEntityWithAi mobEntityWithAi, double d, boolean bl, Ingredient ingredient) {
		this.field_6616 = mobEntityWithAi;
		this.field_6615 = d;
		this.field_6622 = ingredient;
		this.field_6620 = bl;
		this.setControlBits(3);
		if (!(mobEntityWithAi.getNavigation() instanceof EntityMobNavigation)) {
			throw new IllegalArgumentException("Unsupported mob type for TemptGoal");
		}
	}

	@Override
	public boolean canStart() {
		if (this.field_6612 > 0) {
			this.field_6612--;
			return false;
		} else {
			this.field_6617 = this.field_6616.world.getClosestPlayer(this.field_6616, 10.0);
			return this.field_6617 == null ? false : this.method_6312(this.field_6617.getMainHandStack()) || this.method_6312(this.field_6617.getOffHandStack());
		}
	}

	protected boolean method_6312(ItemStack itemStack) {
		return this.field_6622.matches(itemStack);
	}

	@Override
	public boolean shouldContinue() {
		if (this.method_16081()) {
			if (this.field_6616.squaredDistanceTo(this.field_6617) < 36.0) {
				if (this.field_6617.squaredDistanceTo(this.field_6614, this.field_6611, this.field_6621) > 0.010000000000000002) {
					return false;
				}

				if (Math.abs((double)this.field_6617.pitch - this.field_6619) > 5.0 || Math.abs((double)this.field_6617.yaw - this.field_6618) > 5.0) {
					return false;
				}
			} else {
				this.field_6614 = this.field_6617.x;
				this.field_6611 = this.field_6617.y;
				this.field_6621 = this.field_6617.z;
			}

			this.field_6619 = (double)this.field_6617.pitch;
			this.field_6618 = (double)this.field_6617.yaw;
		}

		return this.canStart();
	}

	protected boolean method_16081() {
		return this.field_6620;
	}

	@Override
	public void start() {
		this.field_6614 = this.field_6617.x;
		this.field_6611 = this.field_6617.y;
		this.field_6621 = this.field_6617.z;
		this.field_6613 = true;
	}

	@Override
	public void onRemove() {
		this.field_6617 = null;
		this.field_6616.getNavigation().method_6340();
		this.field_6612 = 100;
		this.field_6613 = false;
	}

	@Override
	public void tick() {
		this.field_6616.getLookControl().lookAt(this.field_6617, (float)(this.field_6616.method_5986() + 20), (float)this.field_6616.method_5978());
		if (this.field_6616.squaredDistanceTo(this.field_6617) < 6.25) {
			this.field_6616.getNavigation().method_6340();
		} else {
			this.field_6616.getNavigation().method_6335(this.field_6617, this.field_6615);
		}
	}

	public boolean method_6313() {
		return this.field_6613;
	}
}
