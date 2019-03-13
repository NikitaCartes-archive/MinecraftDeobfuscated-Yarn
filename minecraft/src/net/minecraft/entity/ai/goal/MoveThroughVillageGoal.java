package net.minecraft.entity.ai.goal;

import com.google.common.collect.Lists;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import net.minecraft.class_1414;
import net.minecraft.class_4153;
import net.minecraft.class_4158;
import net.minecraft.entity.ai.pathing.EntityMobNavigation;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class MoveThroughVillageGoal extends Goal {
	protected final MobEntityWithAi field_6525;
	private final double field_6520;
	private Path field_6523;
	private BlockPos field_18412;
	private final boolean field_6524;
	private final List<BlockPos> field_18413 = Lists.<BlockPos>newArrayList();
	private final int field_18414;
	private final BooleanSupplier field_18415;

	public MoveThroughVillageGoal(MobEntityWithAi mobEntityWithAi, double d, boolean bl, int i, BooleanSupplier booleanSupplier) {
		this.field_6525 = mobEntityWithAi;
		this.field_6520 = d;
		this.field_6524 = bl;
		this.field_18414 = i;
		this.field_18415 = booleanSupplier;
		this.setControlBits(EnumSet.of(Goal.class_4134.field_18405));
		if (!(mobEntityWithAi.method_5942() instanceof EntityMobNavigation)) {
			throw new IllegalArgumentException("Unsupported mob for MoveThroughVillageGoal");
		}
	}

	@Override
	public boolean canStart() {
		this.method_6297();
		if (this.field_6524 && this.field_6525.field_6002.isDaylight()) {
			return false;
		} else {
			ServerWorld serverWorld = (ServerWorld)this.field_6525.field_6002;
			BlockPos blockPos = new BlockPos(this.field_6525);
			Vec3d vec3d = class_1414.method_19108(
				this.field_6525,
				15,
				7,
				blockPos2x -> {
					if (!serverWorld.method_19500(blockPos2x)) {
						return Double.NEGATIVE_INFINITY;
					} else {
						Optional<BlockPos> optionalx = serverWorld.method_19494()
							.method_19127(class_4158.field_18501, this::method_19052, blockPos2x, 10, class_4153.class_4155.field_18488);
						return !optionalx.isPresent() ? Double.NEGATIVE_INFINITY : -((BlockPos)optionalx.get()).squaredDistanceTo(blockPos);
					}
				}
			);
			if (vec3d == null) {
				return false;
			} else {
				Optional<BlockPos> optional = serverWorld.method_19494()
					.method_19127(class_4158.field_18501, this::method_19052, new BlockPos(vec3d), 10, class_4153.class_4155.field_18488);
				if (!optional.isPresent()) {
					return false;
				} else {
					this.field_18412 = ((BlockPos)optional.get()).toImmutable();
					EntityMobNavigation entityMobNavigation = (EntityMobNavigation)this.field_6525.method_5942();
					boolean bl = entityMobNavigation.canEnterOpenDoors();
					entityMobNavigation.setCanPathThroughDoors(this.field_18415.getAsBoolean());
					this.field_6523 = entityMobNavigation.method_6348(this.field_18412);
					entityMobNavigation.setCanPathThroughDoors(bl);
					if (this.field_6523 == null) {
						Vec3d vec3d2 = class_1414.method_6373(
							this.field_6525, 10, 7, new Vec3d((double)this.field_18412.getX(), (double)this.field_18412.getY(), (double)this.field_18412.getZ())
						);
						if (vec3d2 == null) {
							return false;
						}

						entityMobNavigation.setCanPathThroughDoors(this.field_18415.getAsBoolean());
						this.field_6523 = this.field_6525.method_5942().method_6352(vec3d2.x, vec3d2.y, vec3d2.z);
						entityMobNavigation.setCanPathThroughDoors(bl);
						if (this.field_6523 == null) {
							return false;
						}
					}

					for (int i = 0; i < this.field_6523.getLength(); i++) {
						PathNode pathNode = this.field_6523.getNode(i);
						BlockPos blockPos2 = new BlockPos(pathNode.x, pathNode.y + 1, pathNode.z);
						if (DoorInteractGoal.method_6254(this.field_6525.field_6002, blockPos2)) {
							this.field_6523 = this.field_6525.method_5942().method_6352((double)pathNode.x, (double)pathNode.y, (double)pathNode.z);
							break;
						}
					}

					return this.field_6523 != null;
				}
			}
		}
	}

	@Override
	public boolean shouldContinue() {
		if (this.field_6525.method_5942().isIdle()) {
			return false;
		} else {
			float f = this.field_6525.getWidth() + (float)this.field_18414;
			return this.field_6525.method_5831(this.field_18412) > (double)(f * f);
		}
	}

	@Override
	public void start() {
		this.field_6525.method_5942().method_6334(this.field_6523, this.field_6520);
	}

	@Override
	public void onRemove() {
		if (this.field_6525.method_5942().isIdle() || this.field_6525.method_5831(this.field_18412) < (double)(this.field_18414 * this.field_18414)) {
			this.field_18413.add(this.field_18412);
		}
	}

	private boolean method_19052(BlockPos blockPos) {
		for (BlockPos blockPos2 : this.field_18413) {
			if (Objects.equals(blockPos, blockPos2)) {
				return false;
			}
		}

		return true;
	}

	private void method_6297() {
		if (this.field_18413.size() > 15) {
			this.field_18413.remove(0);
		}
	}
}
