package net.minecraft.client.render.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeavesBlock;
import net.minecraft.block.TransparentBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.texture.Sprite;
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
	private static final float field_32781 = 0.8888889F;
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

	private static boolean method_29710(BlockView blockView, Direction direction, float f, BlockPos blockPos, BlockState blockState) {
		if (blockState.isOpaque()) {
			VoxelShape voxelShape = VoxelShapes.cuboid(0.0, 0.0, 0.0, 1.0, (double)f, 1.0);
			VoxelShape voxelShape2 = blockState.getCullingShape(blockView, blockPos);
			return VoxelShapes.isSideCovered(voxelShape, voxelShape2, direction);
		} else {
			return false;
		}
	}

	private static boolean isSideCovered(BlockView world, BlockPos pos, Direction direction, float maxDeviation) {
		BlockPos blockPos = pos.offset(direction);
		BlockState blockState = world.getBlockState(blockPos);
		return method_29710(world, direction, maxDeviation, blockPos, blockState);
	}

	private static boolean method_29709(BlockView blockView, BlockPos blockPos, BlockState blockState, Direction direction) {
		return method_29710(blockView, direction.getOpposite(), 1.0F, blockPos, blockState);
	}

	public static boolean method_29708(BlockRenderView blockRenderView, BlockPos blockPos, FluidState fluidState, BlockState blockState, Direction direction) {
		return !method_29709(blockRenderView, blockPos, blockState, direction) && !isSameFluid(blockRenderView, blockPos, direction, fluidState);
	}

	public boolean render(BlockRenderView world, BlockPos pos, VertexConsumer vertexConsumer, FluidState state) {
		boolean bl = state.isIn(FluidTags.LAVA);
		Sprite[] sprites = bl ? this.lavaSprites : this.waterSprites;
		BlockState blockState = world.getBlockState(pos);
		int i = bl ? 16777215 : BiomeColors.getWaterColor(world, pos);
		float f = (float)(i >> 16 & 0xFF) / 255.0F;
		float g = (float)(i >> 8 & 0xFF) / 255.0F;
		float h = (float)(i & 0xFF) / 255.0F;
		boolean bl2 = !isSameFluid(world, pos, Direction.UP, state);
		boolean bl3 = method_29708(world, pos, state, blockState, Direction.DOWN) && !isSideCovered(world, pos, Direction.DOWN, 0.8888889F);
		boolean bl4 = method_29708(world, pos, state, blockState, Direction.NORTH);
		boolean bl5 = method_29708(world, pos, state, blockState, Direction.SOUTH);
		boolean bl6 = method_29708(world, pos, state, blockState, Direction.WEST);
		boolean bl7 = method_29708(world, pos, state, blockState, Direction.EAST);
		if (!bl2 && !bl3 && !bl7 && !bl6 && !bl4 && !bl5) {
			return false;
		} else {
			boolean bl8 = false;
			float j = world.getBrightness(Direction.DOWN, true);
			float k = world.getBrightness(Direction.UP, true);
			float l = world.getBrightness(Direction.NORTH, true);
			float m = world.getBrightness(Direction.WEST, true);
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
				float ai = k * f;
				float aj = k * g;
				float ak = k * h;
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
				float xx = j * f;
				float zx = j * g;
				float abx = j * h;
				this.vertex(vertexConsumer, d, e + (double)t, r + 1.0, xx, zx, abx, ux, aax, al);
				this.vertex(vertexConsumer, d, e + (double)t, r, xx, zx, abx, ux, yx, al);
				this.vertex(vertexConsumer, d + 1.0, e + (double)t, r, xx, zx, abx, wx, yx, al);
				this.vertex(vertexConsumer, d + 1.0, e + (double)t, r + 1.0, xx, zx, abx, wx, aax, al);
				bl8 = true;
			}

			int am = this.getLight(world, pos);

			for (int an = 0; an < 4; an++) {
				float yx;
				float aax;
				double ao;
				double aq;
				double ap;
				double ar;
				Direction direction;
				boolean bl9;
				if (an == 0) {
					yx = n;
					aax = q;
					ao = d;
					ap = d + 1.0;
					aq = r + 0.001F;
					ar = r + 0.001F;
					direction = Direction.NORTH;
					bl9 = bl4;
				} else if (an == 1) {
					yx = p;
					aax = o;
					ao = d + 1.0;
					ap = d;
					aq = r + 1.0 - 0.001F;
					ar = r + 1.0 - 0.001F;
					direction = Direction.SOUTH;
					bl9 = bl5;
				} else if (an == 2) {
					yx = o;
					aax = n;
					ao = d + 0.001F;
					ap = d + 0.001F;
					aq = r + 1.0;
					ar = r;
					direction = Direction.WEST;
					bl9 = bl6;
				} else {
					yx = q;
					aax = p;
					ao = d + 1.0 - 0.001F;
					ap = d + 1.0 - 0.001F;
					aq = r;
					ar = r + 1.0;
					direction = Direction.EAST;
					bl9 = bl7;
				}

				if (bl9 && !isSideCovered(world, pos, direction, Math.max(yx, aax))) {
					bl8 = true;
					BlockPos blockPos = pos.offset(direction);
					Sprite sprite2 = sprites[1];
					if (!bl) {
						Block block = world.getBlockState(blockPos).getBlock();
						if (block instanceof TransparentBlock || block instanceof LeavesBlock) {
							sprite2 = this.waterOverlaySprite;
						}
					}

					float aj = sprite2.getFrameU(0.0);
					float ak = sprite2.getFrameU(8.0);
					float as = sprite2.getFrameV((double)((1.0F - yx) * 16.0F * 0.5F));
					float at = sprite2.getFrameV((double)((1.0F - aax) * 16.0F * 0.5F));
					float au = sprite2.getFrameV(8.0);
					float av = an < 2 ? l : m;
					float aw = k * av * f;
					float ax = k * av * g;
					float ay = k * av * h;
					this.vertex(vertexConsumer, ao, e + (double)yx, aq, aw, ax, ay, aj, as, am);
					this.vertex(vertexConsumer, ap, e + (double)aax, ar, aw, ax, ay, ak, at, am);
					this.vertex(vertexConsumer, ap, e + (double)t, ar, aw, ax, ay, ak, au, am);
					this.vertex(vertexConsumer, ao, e + (double)t, aq, aw, ax, ay, aj, au, am);
					if (sprite2 != this.waterOverlaySprite) {
						this.vertex(vertexConsumer, ao, e + (double)t, aq, aw, ax, ay, aj, au, am);
						this.vertex(vertexConsumer, ap, e + (double)t, ar, aw, ax, ay, ak, au, am);
						this.vertex(vertexConsumer, ap, e + (double)aax, ar, aw, ax, ay, ak, at, am);
						this.vertex(vertexConsumer, ao, e + (double)yx, aq, aw, ax, ay, aj, as, am);
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
