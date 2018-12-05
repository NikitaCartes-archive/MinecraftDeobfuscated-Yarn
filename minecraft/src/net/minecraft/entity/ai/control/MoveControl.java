package net.minecraft.entity.ai.control;

import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.PathNodeMaker;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.MathHelper;

public class MoveControl {
	protected final MobEntity entity;
	protected double field_6370;
	protected double field_6369;
	protected double field_6367;
	protected double field_6372;
	protected float field_6368;
	protected float field_6373;
	protected MoveControl.class_1336 field_6374 = MoveControl.class_1336.field_6377;

	public MoveControl(MobEntity mobEntity) {
		this.entity = mobEntity;
	}

	public boolean method_6241() {
		return this.field_6374 == MoveControl.class_1336.field_6378;
	}

	public double method_6242() {
		return this.field_6372;
	}

	public void method_6239(double d, double e, double f, double g) {
		this.field_6370 = d;
		this.field_6369 = e;
		this.field_6367 = f;
		this.field_6372 = g;
		if (this.field_6374 != MoveControl.class_1336.field_6379) {
			this.field_6374 = MoveControl.class_1336.field_6378;
		}
	}

	public void method_6243(float f, float g) {
		this.field_6374 = MoveControl.class_1336.field_6376;
		this.field_6368 = f;
		this.field_6373 = g;
		this.field_6372 = 0.25;
	}

	public void tick() {
		if (this.field_6374 == MoveControl.class_1336.field_6376) {
			float f = (float)this.entity.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).getValue();
			float g = (float)this.field_6372 * f;
			float h = this.field_6368;
			float i = this.field_6373;
			float j = MathHelper.sqrt(h * h + i * i);
			if (j < 1.0F) {
				j = 1.0F;
			}

			j = g / j;
			h *= j;
			i *= j;
			float k = MathHelper.sin(this.entity.yaw * (float) (Math.PI / 180.0));
			float l = MathHelper.cos(this.entity.yaw * (float) (Math.PI / 180.0));
			float m = h * l - i * k;
			float n = i * l + h * k;
			EntityNavigation entityNavigation = this.entity.getNavigation();
			if (entityNavigation != null) {
				PathNodeMaker pathNodeMaker = entityNavigation.method_6342();
				if (pathNodeMaker != null
					&& pathNodeMaker.getPathNodeType(
							this.entity.world, MathHelper.floor(this.entity.x + (double)m), MathHelper.floor(this.entity.y), MathHelper.floor(this.entity.z + (double)n)
						)
						!= PathNodeType.NORMAL) {
					this.field_6368 = 1.0F;
					this.field_6373 = 0.0F;
					g = f;
				}
			}

			this.entity.method_6125(g);
			this.entity.method_5930(this.field_6368);
			this.entity.method_5938(this.field_6373);
			this.field_6374 = MoveControl.class_1336.field_6377;
		} else if (this.field_6374 == MoveControl.class_1336.field_6378) {
			this.field_6374 = MoveControl.class_1336.field_6377;
			double d = this.field_6370 - this.entity.x;
			double e = this.field_6367 - this.entity.z;
			double o = this.field_6369 - this.entity.y;
			double p = d * d + o * o + e * e;
			if (p < 2.5000003E-7F) {
				this.entity.method_5930(0.0F);
				return;
			}

			float n = (float)(MathHelper.atan2(e, d) * 180.0F / (float)Math.PI) - 90.0F;
			this.entity.yaw = this.method_6238(this.entity.yaw, n, 90.0F);
			this.entity.method_6125((float)(this.field_6372 * this.entity.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).getValue()));
			if (o > (double)this.entity.stepHeight && d * d + e * e < (double)Math.max(1.0F, this.entity.width)) {
				this.entity.getJumpControl().setActive();
				this.field_6374 = MoveControl.class_1336.field_6379;
			}
		} else if (this.field_6374 == MoveControl.class_1336.field_6379) {
			this.entity.method_6125((float)(this.field_6372 * this.entity.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).getValue()));
			if (this.entity.onGround) {
				this.field_6374 = MoveControl.class_1336.field_6377;
			}
		} else {
			this.entity.method_5930(0.0F);
		}
	}

	protected float method_6238(float f, float g, float h) {
		float i = MathHelper.wrapDegrees(g - f);
		if (i > h) {
			i = h;
		}

		if (i < -h) {
			i = -h;
		}

		float j = f + i;
		if (j < 0.0F) {
			j += 360.0F;
		} else if (j > 360.0F) {
			j -= 360.0F;
		}

		return j;
	}

	public double method_6236() {
		return this.field_6370;
	}

	public double method_6235() {
		return this.field_6369;
	}

	public double method_6237() {
		return this.field_6367;
	}

	public static enum class_1336 {
		field_6377,
		field_6378,
		field_6376,
		field_6379;
	}
}
