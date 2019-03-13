package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.RayTraceContext;

@Environment(EnvType.CLIENT)
public class class_4184 {
	private boolean field_18709;
	private BlockView field_18710;
	private Entity field_18711;
	private Vec3d field_18712;
	private final BlockPos.Mutable field_18713 = new BlockPos.Mutable();
	private Vec3d field_18714;
	private Vec3d field_18715;
	private Vec3d field_18716;
	private float field_18717;
	private float field_18718;
	private boolean field_18719;
	private boolean field_18720;
	private float field_18721;
	private float field_18722;

	public void method_19321(BlockView blockView, Entity entity, boolean bl, boolean bl2, float f) {
		this.field_18709 = true;
		this.field_18710 = blockView;
		this.field_18711 = entity;
		this.field_18719 = bl;
		this.field_18720 = bl2;
		this.method_19325(entity.yaw, entity.pitch);
		this.method_19327(
			MathHelper.lerp((double)f, entity.prevX, entity.x),
			MathHelper.lerp((double)f, entity.prevY, entity.y) + (double)MathHelper.lerp(f, this.field_18722, this.field_18721),
			MathHelper.lerp((double)f, entity.prevZ, entity.z)
		);
		if (bl) {
			if (bl2) {
				this.method_19320(180.0F, 0.0F);
			}

			this.method_19324(-this.method_19318(4.0), 0.0, 0.0);
			this.method_19319(
				MathHelper.lerp((double)f, entity.prevX, entity.x),
				MathHelper.lerp((double)f, entity.prevY, entity.y) + (double)MathHelper.lerp(f, this.field_18722, this.field_18721),
				MathHelper.lerp((double)f, entity.prevZ, entity.z)
			);
		} else if (entity instanceof LivingEntity && ((LivingEntity)entity).isSleeping()) {
			Direction direction = ((LivingEntity)entity).method_18401();
			this.method_19325(direction != null ? direction.asRotation() - 180.0F : 0.0F, 0.0F);
			this.method_19324(0.0, 0.3, 0.0);
		} else {
			this.method_19324(-0.05F, 0.0, 0.0);
		}

		GlStateManager.rotatef(this.field_18717, 1.0F, 0.0F, 0.0F);
		GlStateManager.rotatef(this.field_18718 + 180.0F, 0.0F, 1.0F, 0.0F);
	}

	public void method_19317() {
		if (this.field_18711 != null) {
			this.field_18722 = this.field_18721;
			this.field_18721 = this.field_18721 + (this.field_18711.getStandingEyeHeight() - this.field_18721) * 0.5F;
		}
	}

	private double method_19318(double d) {
		for (int i = 0; i < 8; i++) {
			float f = (float)((i & 1) * 2 - 1);
			float g = (float)((i >> 1 & 1) * 2 - 1);
			float h = (float)((i >> 2 & 1) * 2 - 1);
			f *= 0.1F;
			g *= 0.1F;
			h *= 0.1F;
			Vec3d vec3d = this.field_18712.add((double)f, (double)g, (double)h);
			Vec3d vec3d2 = new Vec3d(
				this.field_18712.x - this.field_18714.x * d + (double)f + (double)h,
				this.field_18712.y - this.field_18714.y * d + (double)g,
				this.field_18712.z - this.field_18714.z * d + (double)h
			);
			HitResult hitResult = this.field_18710
				.method_17742(new RayTraceContext(vec3d, vec3d2, RayTraceContext.ShapeType.field_17558, RayTraceContext.FluidHandling.NONE, this.field_18711));
			if (hitResult.getType() != HitResult.Type.NONE) {
				double e = hitResult.method_17784().distanceTo(this.field_18712);
				if (e < d) {
					d = e;
				}
			}
		}

		return d;
	}

	protected void method_19319(double d, double e, double f) {
		double g = d - this.field_18712.x;
		double h = e - this.field_18712.y;
		double i = f - this.field_18712.z;
		double j = (double)MathHelper.sqrt(g * g + i * i);
		this.field_18717 = MathHelper.wrapDegrees((float)(-(MathHelper.atan2(h, j) * 180.0F / (float)Math.PI)));
		this.field_18718 = MathHelper.wrapDegrees((float)(MathHelper.atan2(i, g) * 180.0F / (float)Math.PI) - 90.0F);
		this.method_19323();
	}

	protected void method_19324(double d, double e, double f) {
		double g = this.field_18714.x * d + this.field_18715.x * e + this.field_18716.x * f;
		double h = this.field_18714.y * d + this.field_18715.y * e + this.field_18716.y * f;
		double i = this.field_18714.z * d + this.field_18715.z * e + this.field_18716.z * f;
		this.method_19322(new Vec3d(this.field_18712.x + g, this.field_18712.y + h, this.field_18712.z + i));
	}

	protected void method_19320(float f, float g) {
		this.field_18717 += g;
		this.field_18718 += f;
		this.method_19323();
	}

	protected void method_19323() {
		float f = MathHelper.cos((this.field_18718 + 90.0F) * (float) (Math.PI / 180.0));
		float g = MathHelper.sin((this.field_18718 + 90.0F) * (float) (Math.PI / 180.0));
		float h = MathHelper.cos(-this.field_18717 * (float) (Math.PI / 180.0));
		float i = MathHelper.sin(-this.field_18717 * (float) (Math.PI / 180.0));
		float j = MathHelper.cos((-this.field_18717 + 90.0F) * (float) (Math.PI / 180.0));
		float k = MathHelper.sin((-this.field_18717 + 90.0F) * (float) (Math.PI / 180.0));
		this.field_18714 = new Vec3d((double)(f * h), (double)i, (double)(g * h));
		this.field_18715 = new Vec3d((double)(f * j), (double)k, (double)(g * j));
		this.field_18716 = this.field_18714.crossProduct(this.field_18715).multiply(-1.0);
	}

	protected void method_19325(float f, float g) {
		this.field_18717 = g;
		this.field_18718 = f;
		this.method_19323();
	}

	protected void method_19327(double d, double e, double f) {
		this.method_19322(new Vec3d(d, e, f));
	}

	protected void method_19322(Vec3d vec3d) {
		this.field_18712 = vec3d;
		this.field_18713.set(vec3d.x, vec3d.y, vec3d.z);
	}

	public Vec3d method_19326() {
		return this.field_18712;
	}

	public BlockPos method_19328() {
		return this.field_18713;
	}

	public float method_19329() {
		return this.field_18717;
	}

	public float method_19330() {
		return this.field_18718;
	}

	public Entity method_19331() {
		return this.field_18711;
	}

	public boolean method_19332() {
		return this.field_18709;
	}

	public boolean method_19333() {
		return this.field_18719;
	}

	public FluidState method_19334() {
		if (!this.field_18709) {
			return Fluids.EMPTY.method_15785();
		} else {
			FluidState fluidState = this.field_18710.method_8316(this.field_18713);
			return !fluidState.isEmpty() && this.field_18712.y >= (double)((float)this.field_18713.getY() + fluidState.method_15763(this.field_18710, this.field_18713))
				? Fluids.EMPTY.method_15785()
				: fluidState;
		}
	}

	public final Vec3d method_19335() {
		return this.field_18714;
	}

	public final Vec3d method_19336() {
		return this.field_18715;
	}

	public void method_19337() {
		this.field_18710 = null;
		this.field_18711 = null;
		this.field_18709 = false;
	}
}
