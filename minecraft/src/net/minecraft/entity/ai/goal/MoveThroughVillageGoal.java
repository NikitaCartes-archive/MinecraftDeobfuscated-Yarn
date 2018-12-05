package net.minecraft.entity.ai.goal;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.class_1414;
import net.minecraft.entity.ai.pathing.EntityMobNavigation;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.village.VillageDoor;
import net.minecraft.village.VillageProperties;

public class MoveThroughVillageGoal extends Goal {
	protected final MobEntityWithAi field_6525;
	private final double field_6520;
	private Path field_6523;
	protected VillageDoor field_6522;
	private final boolean field_6524;
	protected final List<VillageDoor> field_6521 = Lists.<VillageDoor>newArrayList();

	public MoveThroughVillageGoal(MobEntityWithAi mobEntityWithAi, double d, boolean bl) {
		this.field_6525 = mobEntityWithAi;
		this.field_6520 = d;
		this.field_6524 = bl;
		this.setControlBits(1);
		if (!(mobEntityWithAi.getNavigation() instanceof EntityMobNavigation)) {
			throw new IllegalArgumentException("Unsupported mob for MoveThroughVillageGoal");
		}
	}

	@Override
	public boolean canStart() {
		this.method_6297();
		if (this.field_6524 && this.field_6525.world.isDaylight()) {
			return false;
		} else {
			VillageProperties villageProperties = this.field_6525.world.getVillageManager().getNearestVillage(new BlockPos(this.field_6525), 0);
			if (villageProperties == null) {
				return false;
			} else {
				this.field_6522 = this.method_6298(villageProperties);
				if (this.field_6522 == null) {
					return false;
				} else {
					EntityMobNavigation entityMobNavigation = (EntityMobNavigation)this.field_6525.getNavigation();
					boolean bl = entityMobNavigation.canEnterOpenDoors();
					entityMobNavigation.setCanPathThroughDoors(false);
					this.field_6523 = entityMobNavigation.findPathTo(this.field_6522.getPosition());
					entityMobNavigation.setCanPathThroughDoors(bl);
					if (this.field_6523 != null) {
						return true;
					} else {
						Vec3d vec3d = class_1414.method_6373(
							this.field_6525,
							10,
							7,
							new Vec3d((double)this.field_6522.getPosition().getX(), (double)this.field_6522.getPosition().getY(), (double)this.field_6522.getPosition().getZ())
						);
						if (vec3d == null) {
							return false;
						} else {
							entityMobNavigation.setCanPathThroughDoors(false);
							this.field_6523 = this.field_6525.getNavigation().findPathTo(vec3d.x, vec3d.y, vec3d.z);
							entityMobNavigation.setCanPathThroughDoors(bl);
							return this.field_6523 != null;
						}
					}
				}
			}
		}
	}

	@Override
	public boolean shouldContinue() {
		if (this.field_6525.getNavigation().method_6357()) {
			return false;
		} else {
			float f = this.field_6525.width + 4.0F;
			return this.field_6525.squaredDistanceTo(this.field_6522.getPosition()) > (double)(f * f);
		}
	}

	@Override
	public void start() {
		this.field_6525.getNavigation().method_6334(this.field_6523, this.field_6520);
	}

	@Override
	public void onRemove() {
		if (this.field_6525.getNavigation().method_6357() || this.field_6525.squaredDistanceTo(this.field_6522.getPosition()) < 16.0) {
			this.field_6521.add(this.field_6522);
		}
	}

	private VillageDoor method_6298(VillageProperties villageProperties) {
		VillageDoor villageDoor = null;
		int i = Integer.MAX_VALUE;

		for (VillageDoor villageDoor2 : villageProperties.getDoors()) {
			int j = villageDoor2.squaredDistance(MathHelper.floor(this.field_6525.x), MathHelper.floor(this.field_6525.y), MathHelper.floor(this.field_6525.z));
			if (j < i && !this.method_6299(villageDoor2)) {
				villageDoor = villageDoor2;
				i = j;
			}
		}

		return villageDoor;
	}

	private boolean method_6299(VillageDoor villageDoor) {
		for (VillageDoor villageDoor2 : this.field_6521) {
			if (villageDoor.getPosition().equals(villageDoor2.getPosition())) {
				return true;
			}
		}

		return false;
	}

	private void method_6297() {
		if (this.field_6521.size() > 15) {
			this.field_6521.remove(0);
		}
	}
}
