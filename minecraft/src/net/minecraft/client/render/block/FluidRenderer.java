package net.minecraft.client.render.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.StainedGlassBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.BlockView;

@Environment(EnvType.CLIENT)
public class FluidRenderer {
	private final Sprite[] lavaSprites = new Sprite[2];
	private final Sprite[] waterSprites = new Sprite[2];
	private Sprite waterOverlaySprite;

	protected void onResourceReload() {
		this.lavaSprites[0] = MinecraftClient.getInstance().getBakedModelManager().getBlockModels().getModel(Blocks.LAVA.getDefaultState()).getSprite();
		this.lavaSprites[1] = ModelLoader.LAVA_FLOW.getSprite();
		this.waterSprites[0] = MinecraftClient.getInstance().getBakedModelManager().getBlockModels().getModel(Blocks.WATER.getDefaultState()).getSprite();
		this.waterSprites[1] = ModelLoader.WATER_FLOW.getSprite();
		this.waterOverlaySprite = ModelLoader.WATER_OVERLAY.getSprite();
	}

	private static boolean isSameFluid(BlockView world, BlockPos pos, Direction side, FluidState state) {
		BlockPos blockPos = pos.offset(side);
		FluidState fluidState = world.getFluidState(blockPos);
		return fluidState.getFluid().matchesType(state.getFluid());
	}

	private static boolean isSideCovered(BlockView world, BlockPos pos, Direction direction, float maxDeviation) {
		BlockPos blockPos = pos.offset(direction);
		BlockState blockState = world.getBlockState(blockPos);
		if (blockState.isOpaque()) {
			VoxelShape voxelShape = VoxelShapes.cuboid(0.0, 0.0, 0.0, 1.0, (double)maxDeviation, 1.0);
			VoxelShape voxelShape2 = blockState.getCullingShape(world, blockPos);
			return VoxelShapes.isSideCovered(voxelShape, voxelShape2, direction);
		} else {
			return false;
		}
	}

	public boolean render(BlockRenderView world, BlockPos pos, VertexConsumer vertexConsumer, FluidState state) {
		boolean bl = state.matches(FluidTags.LAVA);
		Sprite[] sprites = bl ? this.lavaSprites : this.waterSprites;
		Vector3f vector3f = world.method_26443(Blocks.WATER.getDefaultState(), pos);
		int i = bl ? 16777215 : BiomeColors.getWaterColor(world, pos);
		float f = vector3f.getX() * (float)(i >> 16 & 0xFF) / 255.0F;
		float g = vector3f.getX() * (float)(i >> 8 & 0xFF) / 255.0F;
		float h = vector3f.getX() * (float)(i & 0xFF) / 255.0F;
		boolean bl2 = !isSameFluid(world, pos, Direction.UP, state);
		boolean bl3 = !isSameFluid(world, pos, Direction.DOWN, state) && !isSideCovered(world, pos, Direction.DOWN, 0.8888889F);
		boolean bl4 = !isSameFluid(world, pos, Direction.NORTH, state);
		boolean bl5 = !isSameFluid(world, pos, Direction.SOUTH, state);
		boolean bl6 = !isSameFluid(world, pos, Direction.WEST, state);
		boolean bl7 = !isSameFluid(world, pos, Direction.EAST, state);
		if (!bl2 && !bl3 && !bl7 && !bl6 && !bl4 && !bl5) {
			return false;
		} else {
			boolean bl8 = false;
			float j = 0.5F;
			float k = 1.0F;
			float l = 0.8F;
			float m = 0.6F;
			float n = this.getNorthWestCornerFluidHeight(world, pos, state.getFluid());
			float o = this.getNorthWestCornerFluidHeight(world, pos.south(), state.getFluid());
			float p = this.getNorthWestCornerFluidHeight(world, pos.east().south(), state.getFluid());
			float q = this.getNorthWestCornerFluidHeight(world, pos.east(), state.getFluid());
			double d = (double)(pos.getX() & 15);
			double e = (double)(pos.getY() & 15);
			double r = (double)(pos.getZ() & 15);
			float s = 0.001F;
			float t = bl3 ? 0.001F : 0.0F;
			if (bl2 && !isSideCovered(world, pos, Direction.UP, Math.min(Math.min(n, o), Math.min(p, q)))) {
				bl8 = true;
				n -= 0.001F;
				o -= 0.001F;
				p -= 0.001F;
				q -= 0.001F;
				Vec3d vec3d = state.getVelocity(world, pos);
				float u;
				float w;
				float y;
				float aa;
				float v;
				float x;
				float z;
				float ab;
				if (vec3d.x == 0.0 && vec3d.z == 0.0) {
					Sprite sprite = sprites[0];
					u = sprite.getFrameU(0.0);
					v = sprite.getFrameV(0.0);
					w = u;
					x = sprite.getFrameV(16.0);
					y = sprite.getFrameU(16.0);
					z = x;
					aa = y;
					ab = v;
				} else {
					Sprite sprite = sprites[1];
					float ac = (float)MathHelper.atan2(vec3d.z, vec3d.x) - (float) (Math.PI / 2);
					float ad = MathHelper.sin(ac) * 0.25F;
					float ae = MathHelper.cos(ac) * 0.25F;
					float af = 8.0F;
					u = sprite.getFrameU((double)(8.0F + (-ae - ad) * 16.0F));
					v = sprite.getFrameV((double)(8.0F + (-ae + ad) * 16.0F));
					w = sprite.getFrameU((double)(8.0F + (-ae + ad) * 16.0F));
					x = sprite.getFrameV((double)(8.0F + (ae + ad) * 16.0F));
					y = sprite.getFrameU((double)(8.0F + (ae + ad) * 16.0F));
					z = sprite.getFrameV((double)(8.0F + (ae - ad) * 16.0F));
					aa = sprite.getFrameU((double)(8.0F + (ae - ad) * 16.0F));
					ab = sprite.getFrameV((double)(8.0F + (-ae - ad) * 16.0F));
				}

				float ag = (u + w + y + aa) / 4.0F;
				float ac = (v + x + z + ab) / 4.0F;
				float ad = (float)sprites[0].getWidth() / (sprites[0].getMaxU() - sprites[0].getMinU());
				float ae = (float)sprites[0].getHeight() / (sprites[0].getMaxV() - sprites[0].getMinV());
				float af = 4.0F / Math.max(ae, ad);
				u = MathHelper.lerp(af, u, ag);
				w = MathHelper.lerp(af, w, ag);
				y = MathHelper.lerp(af, y, ag);
				aa = MathHelper.lerp(af, aa, ag);
				v = MathHelper.lerp(af, v, ac);
				x = MathHelper.lerp(af, x, ac);
				z = MathHelper.lerp(af, z, ac);
				ab = MathHelper.lerp(af, ab, ac);
				int ah = this.getLight(world, pos);
				float ai = 1.0F * f;
				float aj = 1.0F * g;
				float ak = 1.0F * h;
				this.vertex(vertexConsumer, d + 0.0, e + (double)n, r + 0.0, ai, aj, ak, u, v, ah);
				this.vertex(vertexConsumer, d + 0.0, e + (double)o, r + 1.0, ai, aj, ak, w, x, ah);
				this.vertex(vertexConsumer, d + 1.0, e + (double)p, r + 1.0, ai, aj, ak, y, z, ah);
				this.vertex(vertexConsumer, d + 1.0, e + (double)q, r + 0.0, ai, aj, ak, aa, ab, ah);
				if (state.method_15756(world, pos.up())) {
					this.vertex(vertexConsumer, d + 0.0, e + (double)n, r + 0.0, ai, aj, ak, u, v, ah);
					this.vertex(vertexConsumer, d + 1.0, e + (double)q, r + 0.0, ai, aj, ak, aa, ab, ah);
					this.vertex(vertexConsumer, d + 1.0, e + (double)p, r + 1.0, ai, aj, ak, y, z, ah);
					this.vertex(vertexConsumer, d + 0.0, e + (double)o, r + 1.0, ai, aj, ak, w, x, ah);
				}
			}

			if (bl3) {
				float ux = sprites[0].getMinU();
				float wx = sprites[0].getMaxU();
				float yx = sprites[0].getMinV();
				float aax = sprites[0].getMaxV();
				int al = this.getLight(world, pos.down());
				float xx = 0.5F * f;
				float zx = 0.5F * g;
				float abx = 0.5F * h;
				this.vertex(vertexConsumer, d, e + (double)t, r + 1.0, xx, zx, abx, ux, aax, al);
				this.vertex(vertexConsumer, d, e + (double)t, r, xx, zx, abx, ux, yx, al);
				this.vertex(vertexConsumer, d + 1.0, e + (double)t, r, xx, zx, abx, wx, yx, al);
				this.vertex(vertexConsumer, d + 1.0, e + (double)t, r + 1.0, xx, zx, abx, wx, aax, al);
				bl8 = true;
			}

			for (int am = 0; am < 4; am++) {
				float wx;
				float yx;
				double an;
				double ap;
				double ao;
				double aq;
				Direction direction;
				boolean bl9;
				if (am == 0) {
					wx = n;
					yx = q;
					an = d;
					ao = d + 1.0;
					ap = r + 0.001F;
					aq = r + 0.001F;
					direction = Direction.NORTH;
					bl9 = bl4;
				} else if (am == 1) {
					wx = p;
					yx = o;
					an = d + 1.0;
					ao = d;
					ap = r + 1.0 - 0.001F;
					aq = r + 1.0 - 0.001F;
					direction = Direction.SOUTH;
					bl9 = bl5;
				} else if (am == 2) {
					wx = o;
					yx = n;
					an = d + 0.001F;
					ao = d + 0.001F;
					ap = r + 1.0;
					aq = r;
					direction = Direction.WEST;
					bl9 = bl6;
				} else {
					wx = q;
					yx = p;
					an = d + 1.0 - 0.001F;
					ao = d + 1.0 - 0.001F;
					ap = r;
					aq = r + 1.0;
					direction = Direction.EAST;
					bl9 = bl7;
				}

				if (bl9 && !isSideCovered(world, pos, direction, Math.max(wx, yx))) {
					bl8 = true;
					BlockPos blockPos = pos.offset(direction);
					Sprite sprite2 = sprites[1];
					if (!bl) {
						Block block = world.getBlockState(blockPos).getBlock();
						if (block == Blocks.GLASS || block instanceof StainedGlassBlock) {
							sprite2 = this.waterOverlaySprite;
						}
					}

					float ai = sprite2.getFrameU(0.0);
					float aj = sprite2.getFrameU(8.0);
					float ak = sprite2.getFrameV((double)((1.0F - wx) * 16.0F * 0.5F));
					float ar = sprite2.getFrameV((double)((1.0F - yx) * 16.0F * 0.5F));
					float as = sprite2.getFrameV(8.0);
					int at = this.getLight(world, blockPos);
					float au = am < 2 ? 0.8F : 0.6F;
					float av = 1.0F * au * f;
					float aw = 1.0F * au * g;
					float ax = 1.0F * au * h;
					this.vertex(vertexConsumer, an, e + (double)wx, ap, av, aw, ax, ai, ak, at);
					this.vertex(vertexConsumer, ao, e + (double)yx, aq, av, aw, ax, aj, ar, at);
					this.vertex(vertexConsumer, ao, e + (double)t, aq, av, aw, ax, aj, as, at);
					this.vertex(vertexConsumer, an, e + (double)t, ap, av, aw, ax, ai, as, at);
					if (sprite2 != this.waterOverlaySprite) {
						this.vertex(vertexConsumer, an, e + (double)t, ap, av, aw, ax, ai, as, at);
						this.vertex(vertexConsumer, ao, e + (double)t, aq, av, aw, ax, aj, as, at);
						this.vertex(vertexConsumer, ao, e + (double)yx, aq, av, aw, ax, aj, ar, at);
						this.vertex(vertexConsumer, an, e + (double)wx, ap, av, aw, ax, ai, ak, at);
					}
				}
			}

			return bl8;
		}
	}

	private void vertex(VertexConsumer vertexConsumer, double x, double y, double z, float red, float green, float blue, float u, float v, int light) {
		vertexConsumer.vertex(x, y, z).color(red, green, blue, 1.0F).texture(u, v).light(light).normal(0.0F, 1.0F, 0.0F).next();
	}

	private int getLight(BlockRenderView world, BlockPos pos) {
		int i = WorldRenderer.getLightmapCoordinates(world, pos);
		int j = WorldRenderer.getLightmapCoordinates(world, pos.up());
		int k = i & 0xFF;
		int l = j & 0xFF;
		int m = i >> 16 & 0xFF;
		int n = j >> 16 & 0xFF;
		return (k > l ? k : l) | (m > n ? m : n) << 16;
	}

	private float getNorthWestCornerFluidHeight(BlockView world, BlockPos pos, Fluid fluid) {
		int i = 0;
		float f = 0.0F;

		for (int j = 0; j < 4; j++) {
			BlockPos blockPos = pos.add(-(j & 1), 0, -(j >> 1 & 1));
			if (world.getFluidState(blockPos.up()).getFluid().matchesType(fluid)) {
				return 1.0F;
			}

			FluidState fluidState = world.getFluidState(blockPos);
			if (fluidState.getFluid().matchesType(fluid)) {
				float g = fluidState.getHeight(world, blockPos);
				if (g >= 0.8F) {
					f += g * 10.0F;
					i += 10;
				} else {
					f += g;
					i++;
				}
			} else if (!world.getBlockState(blockPos).getMaterial().isSolid()) {
				i++;
			}
		}

		return f / (float)i;
	}
}
