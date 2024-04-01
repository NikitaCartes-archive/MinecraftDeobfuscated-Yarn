package net.minecraft.client.render.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeavesBlock;
import net.minecraft.block.TranslucentBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.texture.Sprite;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.tag.FluidTags;
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
		this.lavaSprites[0] = MinecraftClient.getInstance().getBakedModelManager().getBlockModels().getModel(Blocks.LAVA.getDefaultState()).getParticleSprite();
		this.lavaSprites[1] = ModelLoader.LAVA_FLOW.getSprite();
		this.waterSprites[0] = MinecraftClient.getInstance().getBakedModelManager().getBlockModels().getModel(Blocks.WATER.getDefaultState()).getParticleSprite();
		this.waterSprites[1] = ModelLoader.WATER_FLOW.getSprite();
		this.waterOverlaySprite = ModelLoader.WATER_OVERLAY.getSprite();
	}

	private static boolean isSameFluid(FluidState a, FluidState b) {
		return b.getFluid().matchesType(a.getFluid());
	}

	private static boolean isSideCovered(BlockView world, Direction direction, float height, BlockPos pos, BlockState state) {
		if (state.isOpaque()) {
			VoxelShape voxelShape = VoxelShapes.cuboid(0.0, 0.0, 0.0, 1.0, (double)height, 1.0);
			VoxelShape voxelShape2 = state.getCullingShape(world, pos);
			return VoxelShapes.isSideCovered(voxelShape, voxelShape2, direction);
		} else {
			return false;
		}
	}

	private static boolean isSideCovered(BlockView world, BlockPos pos, Direction direction, float maxDeviation, BlockState state) {
		return isSideCovered(world, direction, maxDeviation, pos.offset(direction), state);
	}

	private static boolean isOppositeSideCovered(BlockView world, BlockPos pos, BlockState state, Direction direction) {
		return isSideCovered(world, direction.getOpposite(), 1.0F, pos, state);
	}

	public static boolean shouldRenderSide(
		BlockRenderView world, BlockPos pos, FluidState fluidState, BlockState blockState, Direction direction, FluidState neighborFluidState
	) {
		return !isOppositeSideCovered(world, pos, blockState, direction) && !isSameFluid(fluidState, neighborFluidState);
	}

	public void render(
		BlockRenderView world, BlockPos pos, VertexConsumer vertexConsumer, BlockState blockState, FluidState fluidState, double d, double e, double f
	) {
		boolean bl = fluidState.isIn(FluidTags.LAVA);
		Sprite[] sprites = bl ? this.lavaSprites : this.waterSprites;
		int i = bl ? 16777215 : BiomeColors.getWaterColor(world, pos);
		float g = (float)(i >> 16 & 0xFF) / 255.0F;
		float h = (float)(i >> 8 & 0xFF) / 255.0F;
		float j = (float)(i & 0xFF) / 255.0F;
		BlockState blockState2 = world.getBlockState(pos.offset(Direction.DOWN));
		FluidState fluidState2 = blockState2.getFluidState();
		BlockState blockState3 = world.getBlockState(pos.offset(Direction.UP));
		FluidState fluidState3 = blockState3.getFluidState();
		BlockState blockState4 = world.getBlockState(pos.offset(Direction.NORTH));
		FluidState fluidState4 = blockState4.getFluidState();
		BlockState blockState5 = world.getBlockState(pos.offset(Direction.SOUTH));
		FluidState fluidState5 = blockState5.getFluidState();
		BlockState blockState6 = world.getBlockState(pos.offset(Direction.WEST));
		FluidState fluidState6 = blockState6.getFluidState();
		BlockState blockState7 = world.getBlockState(pos.offset(Direction.EAST));
		FluidState fluidState7 = blockState7.getFluidState();
		boolean bl2 = !isSameFluid(fluidState, fluidState3);
		boolean bl3 = shouldRenderSide(world, pos, fluidState, blockState, Direction.DOWN, fluidState2)
			&& !isSideCovered(world, pos, Direction.DOWN, 0.8888889F, blockState2);
		boolean bl4 = shouldRenderSide(world, pos, fluidState, blockState, Direction.NORTH, fluidState4);
		boolean bl5 = shouldRenderSide(world, pos, fluidState, blockState, Direction.SOUTH, fluidState5);
		boolean bl6 = shouldRenderSide(world, pos, fluidState, blockState, Direction.WEST, fluidState6);
		boolean bl7 = shouldRenderSide(world, pos, fluidState, blockState, Direction.EAST, fluidState7);
		if (bl2 || bl3 || bl7 || bl6 || bl4 || bl5) {
			float k = world.getBrightness(Direction.DOWN, true);
			float l = world.getBrightness(Direction.UP, true);
			float m = world.getBrightness(Direction.NORTH, true);
			float n = world.getBrightness(Direction.WEST, true);
			Fluid fluid = fluidState.getFluid();
			float o = this.getFluidHeight(world, fluid, pos, blockState, fluidState);
			float p;
			float q;
			float r;
			float s;
			if (o >= 1.0F) {
				p = 1.0F;
				q = 1.0F;
				r = 1.0F;
				s = 1.0F;
			} else {
				float t = this.getFluidHeight(world, fluid, pos.north(), blockState4, fluidState4);
				float u = this.getFluidHeight(world, fluid, pos.south(), blockState5, fluidState5);
				float v = this.getFluidHeight(world, fluid, pos.east(), blockState7, fluidState7);
				float w = this.getFluidHeight(world, fluid, pos.west(), blockState6, fluidState6);
				p = this.calculateFluidHeight(world, fluid, o, t, v, pos.offset(Direction.NORTH).offset(Direction.EAST));
				q = this.calculateFluidHeight(world, fluid, o, t, w, pos.offset(Direction.NORTH).offset(Direction.WEST));
				r = this.calculateFluidHeight(world, fluid, o, u, v, pos.offset(Direction.SOUTH).offset(Direction.EAST));
				s = this.calculateFluidHeight(world, fluid, o, u, w, pos.offset(Direction.SOUTH).offset(Direction.WEST));
			}

			float t = 0.001F;
			float u = bl3 ? 0.001F : 0.0F;
			if (bl2 && !isSideCovered(world, pos, Direction.UP, Math.min(Math.min(q, s), Math.min(r, p)), blockState3)) {
				q -= 0.001F;
				s -= 0.001F;
				r -= 0.001F;
				p -= 0.001F;
				Vec3d vec3d = fluidState.getVelocity(world, pos);
				float z;
				float ab;
				float x;
				float y;
				float aa;
				float ac;
				float v;
				float w;
				if (vec3d.x == 0.0 && vec3d.z == 0.0) {
					Sprite sprite = sprites[0];
					v = sprite.getFrameU(0.0F);
					x = sprite.getFrameV(0.0F);
					w = v;
					y = sprite.getFrameV(1.0F);
					z = sprite.getFrameU(1.0F);
					aa = y;
					ab = z;
					ac = x;
				} else {
					Sprite sprite = sprites[1];
					float ad = (float)MathHelper.atan2(vec3d.z, vec3d.x) - (float) (Math.PI / 2);
					float ae = MathHelper.sin(ad) * 0.25F;
					float af = MathHelper.cos(ad) * 0.25F;
					float ag = 0.5F;
					v = sprite.getFrameU(0.5F + (-af - ae));
					x = sprite.getFrameV(0.5F + -af + ae);
					w = sprite.getFrameU(0.5F + -af + ae);
					y = sprite.getFrameV(0.5F + af + ae);
					z = sprite.getFrameU(0.5F + af + ae);
					aa = sprite.getFrameV(0.5F + (af - ae));
					ab = sprite.getFrameU(0.5F + (af - ae));
					ac = sprite.getFrameV(0.5F + (-af - ae));
				}

				float ah = (v + w + z + ab) / 4.0F;
				float ad = (x + y + aa + ac) / 4.0F;
				float ae = sprites[0].getAnimationFrameDelta();
				v = MathHelper.lerp(ae, v, ah);
				w = MathHelper.lerp(ae, w, ah);
				z = MathHelper.lerp(ae, z, ah);
				ab = MathHelper.lerp(ae, ab, ah);
				x = MathHelper.lerp(ae, x, ad);
				y = MathHelper.lerp(ae, y, ad);
				aa = MathHelper.lerp(ae, aa, ad);
				ac = MathHelper.lerp(ae, ac, ad);
				int ai = this.getLight(world, pos);
				float ag = l * g;
				float aj = l * h;
				float ak = l * j;
				this.vertex(vertexConsumer, d + 0.0, e + (double)q, f + 0.0, ag, aj, ak, v, x, ai);
				this.vertex(vertexConsumer, d + 0.0, e + (double)s, f + 1.0, ag, aj, ak, w, y, ai);
				this.vertex(vertexConsumer, d + 1.0, e + (double)r, f + 1.0, ag, aj, ak, z, aa, ai);
				this.vertex(vertexConsumer, d + 1.0, e + (double)p, f + 0.0, ag, aj, ak, ab, ac, ai);
				if (fluidState.canFlowTo(world, pos.up())) {
					this.vertex(vertexConsumer, d + 0.0, e + (double)q, f + 0.0, ag, aj, ak, v, x, ai);
					this.vertex(vertexConsumer, d + 1.0, e + (double)p, f + 0.0, ag, aj, ak, ab, ac, ai);
					this.vertex(vertexConsumer, d + 1.0, e + (double)r, f + 1.0, ag, aj, ak, z, aa, ai);
					this.vertex(vertexConsumer, d + 0.0, e + (double)s, f + 1.0, ag, aj, ak, w, y, ai);
				}
			}

			if (bl3) {
				float vx = sprites[0].getMinU();
				float wx = sprites[0].getMaxU();
				float zx = sprites[0].getMinV();
				float abx = sprites[0].getMaxV();
				int al = this.getLight(world, pos.down());
				float yx = k * g;
				float aax = k * h;
				float acx = k * j;
				this.vertex(vertexConsumer, d, e + (double)u, f + 1.0, yx, aax, acx, vx, abx, al);
				this.vertex(vertexConsumer, d, e + (double)u, f, yx, aax, acx, vx, zx, al);
				this.vertex(vertexConsumer, d + 1.0, e + (double)u, f, yx, aax, acx, wx, zx, al);
				this.vertex(vertexConsumer, d + 1.0, e + (double)u, f + 1.0, yx, aax, acx, wx, abx, al);
			}

			int am = this.getLight(world, pos);

			for (Direction direction : Direction.Type.HORIZONTAL) {
				float abx;
				float xx;
				double an;
				double ap;
				double ao;
				double aq;
				boolean bl8;
				switch (direction) {
					case NORTH:
						abx = q;
						xx = p;
						an = d;
						ao = d + 1.0;
						ap = f + 0.001F;
						aq = f + 0.001F;
						bl8 = bl4;
						break;
					case SOUTH:
						abx = r;
						xx = s;
						an = d + 1.0;
						ao = d;
						ap = f + 1.0 - 0.001F;
						aq = f + 1.0 - 0.001F;
						bl8 = bl5;
						break;
					case WEST:
						abx = s;
						xx = q;
						an = d + 0.001F;
						ao = d + 0.001F;
						ap = f + 1.0;
						aq = f;
						bl8 = bl6;
						break;
					default:
						abx = p;
						xx = r;
						an = d + 1.0 - 0.001F;
						ao = d + 1.0 - 0.001F;
						ap = f;
						aq = f + 1.0;
						bl8 = bl7;
				}

				if (bl8 && !isSideCovered(world, pos, direction, Math.max(abx, xx), world.getBlockState(pos.offset(direction)))) {
					BlockPos blockPos = pos.offset(direction);
					Sprite sprite2 = sprites[1];
					if (!bl) {
						Block block = world.getBlockState(blockPos).getBlock();
						if (block instanceof TranslucentBlock || block instanceof LeavesBlock) {
							sprite2 = this.waterOverlaySprite;
						}
					}

					float ar = sprite2.getFrameU(0.0F);
					float as = sprite2.getFrameU(0.5F);
					float at = sprite2.getFrameV((1.0F - abx) * 0.5F);
					float au = sprite2.getFrameV((1.0F - xx) * 0.5F);
					float av = sprite2.getFrameV(0.5F);
					float aw = direction.getAxis() == Direction.Axis.Z ? m : n;
					float ax = l * aw * g;
					float ay = l * aw * h;
					float az = l * aw * j;
					this.vertex(vertexConsumer, an, e + (double)abx, ap, ax, ay, az, ar, at, am);
					this.vertex(vertexConsumer, ao, e + (double)xx, aq, ax, ay, az, as, au, am);
					this.vertex(vertexConsumer, ao, e + (double)u, aq, ax, ay, az, as, av, am);
					this.vertex(vertexConsumer, an, e + (double)u, ap, ax, ay, az, ar, av, am);
					if (sprite2 != this.waterOverlaySprite) {
						this.vertex(vertexConsumer, an, e + (double)u, ap, ax, ay, az, ar, av, am);
						this.vertex(vertexConsumer, ao, e + (double)u, aq, ax, ay, az, as, av, am);
						this.vertex(vertexConsumer, ao, e + (double)xx, aq, ax, ay, az, as, au, am);
						this.vertex(vertexConsumer, an, e + (double)abx, ap, ax, ay, az, ar, at, am);
					}
				}
			}
		}
	}

	private float calculateFluidHeight(BlockRenderView world, Fluid fluid, float originHeight, float northSouthHeight, float eastWestHeight, BlockPos pos) {
		if (!(eastWestHeight >= 1.0F) && !(northSouthHeight >= 1.0F)) {
			float[] fs = new float[2];
			if (eastWestHeight > 0.0F || northSouthHeight > 0.0F) {
				float f = this.getFluidHeight(world, fluid, pos);
				if (f >= 1.0F) {
					return 1.0F;
				}

				this.addHeight(fs, f);
			}

			this.addHeight(fs, originHeight);
			this.addHeight(fs, eastWestHeight);
			this.addHeight(fs, northSouthHeight);
			return fs[0] / fs[1];
		} else {
			return 1.0F;
		}
	}

	private void addHeight(float[] weightedAverageHeight, float height) {
		if (height >= 0.8F) {
			weightedAverageHeight[0] += height * 10.0F;
			weightedAverageHeight[1] += 10.0F;
		} else if (height >= 0.0F) {
			weightedAverageHeight[0] += height;
			weightedAverageHeight[1]++;
		}
	}

	private float getFluidHeight(BlockRenderView world, Fluid fluid, BlockPos pos) {
		BlockState blockState = world.getBlockState(pos);
		return this.getFluidHeight(world, fluid, pos, blockState, blockState.getFluidState());
	}

	private float getFluidHeight(BlockRenderView world, Fluid fluid, BlockPos pos, BlockState blockState, FluidState fluidState) {
		if (fluid.matchesType(fluidState.getFluid())) {
			BlockState blockState2 = world.getBlockState(pos.up());
			return fluid.matchesType(blockState2.getFluidState().getFluid()) ? 1.0F : fluidState.getHeight();
		} else {
			return !blockState.isSolid() ? 0.0F : -1.0F;
		}
	}

	private void vertex(VertexConsumer vertexConsumer, double x, double y, double z, float red, float green, float blue, float u, float v, int light) {
		vertexConsumer.vertex(x, y, z).color(red, green, blue, 1.0F).texture(u, v).light(light).normal(0.0F, 1.0F, 0.0F).next();
	}

	private int getLight(BlockRenderView world, BlockPos pos) {
		int i = WorldRenderer.getLightmapCoordinates(world, pos);
		int j = WorldRenderer.getLightmapCoordinates(world, pos.up());
		int k = i & (LightmapTextureManager.MAX_BLOCK_LIGHT_COORDINATE | 15);
		int l = j & (LightmapTextureManager.MAX_BLOCK_LIGHT_COORDINATE | 15);
		int m = i >> 16 & (LightmapTextureManager.MAX_BLOCK_LIGHT_COORDINATE | 15);
		int n = j >> 16 & (LightmapTextureManager.MAX_BLOCK_LIGHT_COORDINATE | 15);
		return (k > l ? k : l) | (m > n ? m : n) << 16;
	}
}
