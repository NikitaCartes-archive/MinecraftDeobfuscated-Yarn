package net.minecraft.client.render.block.entity;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_4597;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.block.entity.EndPortalBlockEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

@Environment(EnvType.CLIENT)
public class EndPortalBlockEntityRenderer<T extends EndPortalBlockEntity> extends BlockEntityRenderer<T> {
	public static final Identifier SKY_TEX = new Identifier("textures/environment/end_sky.png");
	public static final Identifier PORTAL_TEX = new Identifier("textures/entity/end_portal.png");
	private static final Random RANDOM = new Random(31100L);

	public EndPortalBlockEntityRenderer(BlockEntityRenderDispatcher blockEntityRenderDispatcher) {
		super(blockEntityRenderDispatcher);
	}

	public void method_3591(T endPortalBlockEntity, double d, double e, double f, float g, class_4587 arg, class_4597 arg2, int i) {
		RANDOM.setSeed(31100L);
		double h = d * d + e * e + f * f;
		int j = this.method_3592(h);
		float k = this.method_3594();
		this.method_23084(endPortalBlockEntity, d, e, f, k, 0.15F, arg2.getBuffer(BlockRenderLayer.method_23021(1)));

		for (int l = 1; l < j; l++) {
			this.method_23084(endPortalBlockEntity, d, e, f, k, 2.0F / (float)(18 - l), arg2.getBuffer(BlockRenderLayer.method_23021(l + 1)));
		}
	}

	private void method_23084(T endPortalBlockEntity, double d, double e, double f, float g, float h, class_4588 arg) {
		float i = (RANDOM.nextFloat() * 0.5F + 0.1F) * h;
		float j = (RANDOM.nextFloat() * 0.5F + 0.4F) * h;
		float k = (RANDOM.nextFloat() * 0.5F + 0.5F) * h;
		this.method_23085(endPortalBlockEntity, arg, Direction.SOUTH, d, d + 1.0, e, e + 1.0, f + 1.0, f + 1.0, f + 1.0, f + 1.0, i, j, k);
		this.method_23085(endPortalBlockEntity, arg, Direction.NORTH, d, d + 1.0, e + 1.0, e, f, f, f, f, i, j, k);
		this.method_23085(endPortalBlockEntity, arg, Direction.EAST, d + 1.0, d + 1.0, e + 1.0, e, f, f + 1.0, f + 1.0, f, i, j, k);
		this.method_23085(endPortalBlockEntity, arg, Direction.WEST, d, d, e, e + 1.0, f, f + 1.0, f + 1.0, f, i, j, k);
		this.method_23085(endPortalBlockEntity, arg, Direction.DOWN, d, d + 1.0, e, e, f, f, f + 1.0, f + 1.0, i, j, k);
		this.method_23085(endPortalBlockEntity, arg, Direction.UP, d, d + 1.0, e + (double)g, e + (double)g, f + 1.0, f + 1.0, f, f, i, j, k);
	}

	private void method_23085(
		T endPortalBlockEntity,
		class_4588 arg,
		Direction direction,
		double d,
		double e,
		double f,
		double g,
		double h,
		double i,
		double j,
		double k,
		float l,
		float m,
		float n
	) {
		if (endPortalBlockEntity.shouldDrawSide(direction)) {
			arg.vertex(d, f, h).method_22915(l, m, n, 1.0F).next();
			arg.vertex(e, f, i).method_22915(l, m, n, 1.0F).next();
			arg.vertex(e, g, j).method_22915(l, m, n, 1.0F).next();
			arg.vertex(d, g, k).method_22915(l, m, n, 1.0F).next();
		}
	}

	protected int method_3592(double d) {
		int i;
		if (d > 36864.0) {
			i = 1;
		} else if (d > 25600.0) {
			i = 3;
		} else if (d > 16384.0) {
			i = 5;
		} else if (d > 9216.0) {
			i = 7;
		} else if (d > 4096.0) {
			i = 9;
		} else if (d > 1024.0) {
			i = 11;
		} else if (d > 576.0) {
			i = 13;
		} else if (d > 256.0) {
			i = 14;
		} else {
			i = 15;
		}

		return i;
	}

	protected float method_3594() {
		return 0.75F;
	}
}
