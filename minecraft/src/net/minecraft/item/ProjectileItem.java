package net.minecraft.item;

import java.util.OptionalInt;
import net.minecraft.block.DispenserBlock;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public interface ProjectileItem {
	ProjectileEntity createEntity(World world, Position pos, ItemStack stack, Direction direction);

	default ProjectileItem.Settings getProjectileSettings() {
		return ProjectileItem.Settings.DEFAULT;
	}

	default void initializeProjectile(ProjectileEntity entity, double x, double y, double z, float power, float uncertainty) {
		entity.setVelocity(x, y, z, power, uncertainty);
	}

	@FunctionalInterface
	public interface PositionFunction {
		Position getDispensePosition(BlockPointer pointer, Direction facing);
	}

	public static record Settings(ProjectileItem.PositionFunction positionFunction, float uncertainty, float power, OptionalInt overrideDispenseEvent) {
		public static final ProjectileItem.Settings DEFAULT = builder().build();

		public static ProjectileItem.Settings.Builder builder() {
			return new ProjectileItem.Settings.Builder();
		}

		public static class Builder {
			private ProjectileItem.PositionFunction positionFunction = (pointer, direction) -> DispenserBlock.getOutputLocation(pointer, 0.7, new Vec3d(0.0, 0.1, 0.0));
			private float uncertainty = 6.0F;
			private float power = 1.1F;
			private OptionalInt overrideDispenserEvent = OptionalInt.empty();

			public ProjectileItem.Settings.Builder positionFunction(ProjectileItem.PositionFunction positionFunction) {
				this.positionFunction = positionFunction;
				return this;
			}

			public ProjectileItem.Settings.Builder uncertainty(float uncertainty) {
				this.uncertainty = uncertainty;
				return this;
			}

			public ProjectileItem.Settings.Builder power(float power) {
				this.power = power;
				return this;
			}

			public ProjectileItem.Settings.Builder overrideDispenseEvent(int overrideDispenseEvent) {
				this.overrideDispenserEvent = OptionalInt.of(overrideDispenseEvent);
				return this;
			}

			public ProjectileItem.Settings build() {
				return new ProjectileItem.Settings(this.positionFunction, this.uncertainty, this.power, this.overrideDispenserEvent);
			}
		}
	}
}
