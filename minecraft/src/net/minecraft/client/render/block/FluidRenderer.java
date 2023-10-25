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

	public void render(BlockRenderView world, BlockPos pos, VertexConsumer vertexConsumer, BlockState blockState, FluidState fluidState) {
		boolean bl = fluidState.isIn(FluidTags.LAVA);
		Sprite[] sprites = bl ? this.lavaSprites : this.waterSprites;
		int i = bl ? 16777215 : BiomeColors.getWaterColor(world, pos);
		float f = (float)(i >> 16 & 0xFF) / 255.0F;
		float g = (float)(i >> 8 & 0xFF) / 255.0F;
		float h = (float)(i & 0xFF) / 255.0F;
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
			float j = world.getBrightness(Direction.DOWN, true);
			float k = world.getBrightness(Direction.UP, true);
			float l = world.getBrightness(Direction.NORTH, true);
			float m = world.getBrightness(Direction.WEST, true);
			Fluid fluid = fluidState.getFluid();
			float n = this.getFluidHeight(world, fluid, pos, blockState, fluidState);
			float o;
			float p;
			float q;
			float r;
			if (n >= 1.0F) {
				o = 1.0F;
				p = 1.0F;
				q = 1.0F;
				r = 1.0F;
			} else {
				float s = this.getFluidHeight(world, fluid, pos.north(), blockState4, fluidState4);
				float t = this.getFluidHeight(world, fluid, pos.south(), blockState5, fluidState5);
				float u = this.getFluidHeight(world, fluid, pos.east(), blockState7, fluidState7);
				float v = this.getFluidHeight(world, fluid, pos.west(), blockState6, fluidState6);
				o = this.calculateFluidHeight(world, fluid, n, s, u, pos.offset(Direction.NORTH).offset(Direction.EAST));
				p = this.calculateFluidHeight(world, fluid, n, s, v, pos.offset(Direction.NORTH).offset(Direction.WEST));
				q = this.calculateFluidHeight(world, fluid, n, t, u, pos.offset(Direction.SOUTH).offset(Direction.EAST));
				r = this.calculateFluidHeight(world, fluid, n, t, v, pos.offset(Direction.SOUTH).offset(Direction.WEST));
			}

			double d = (double)(pos.getX() & 15);
			double e = (double)(pos.getY() & 15);
			double w = (double)(pos.getZ() & 15);
			float x = 0.001F;
			float y = bl3 ? 0.001F : 0.0F;
			if (bl2 && !isSideCovered(world, pos, Direction.UP, Math.min(Math.min(p, r), Math.min(q, o)), blockState3)) {
				p -= 0.001F;
				r -= 0.001F;
				q -= 0.001F;
				o -= 0.001F;
				Vec3d vec3d = fluidState.getVelocity(world, pos);
				float z;
				float ab;
				float ad;
				float af;
				float aa;
				float ac;
				float ae;
				float ag;
				if (vec3d.x == 0.0 && vec3d.z == 0.0) {
					Sprite sprite = sprites[0];
					z = sprite.getFrameU(0.0F);
					aa = sprite.getFrameV(0.0F);
					ab = z;
					ac = sprite.getFrameV(1.0F);
					ad = sprite.getFrameU(1.0F);
					ae = ac;
					af = ad;
					ag = aa;
				} else {
					Sprite sprite = sprites[1];
					float ah = (float)MathHelper.atan2(vec3d.z, vec3d.x) - (float) (Math.PI / 2);
					float ai = MathHelper.sin(ah) * 0.25F;
					float aj = MathHelper.cos(ah) * 0.25F;
					float ak = 0.5F;
					z = sprite.getFrameU(0.5F + (-aj - ai));
					aa = sprite.getFrameV(0.5F + -aj + ai);
					ab = sprite.getFrameU(0.5F + -aj + ai);
					ac = sprite.getFrameV(0.5F + aj + ai);
					ad = sprite.getFrameU(0.5F + aj + ai);
					ae = sprite.getFrameV(0.5F + (aj - ai));
					af = sprite.getFrameU(0.5F + (aj - ai));
					ag = sprite.getFrameV(0.5F + (-aj - ai));
				}

				float al = (z + ab + ad + af) / 4.0F;
				float ah = (aa + ac + ae + ag) / 4.0F;
				float ai = sprites[0].getAnimationFrameDelta();
				z = MathHelper.lerp(ai, z, al);
				ab = MathHelper.lerp(ai, ab, al);
				ad = MathHelper.lerp(ai, ad, al);
				af = MathHelper.lerp(ai, af, al);
				aa = MathHelper.lerp(ai, aa, ah);
				ac = MathHelper.lerp(ai, ac, ah);
				ae = MathHelper.lerp(ai, ae, ah);
				ag = MathHelper.lerp(ai, ag, ah);
				int am = this.getLight(world, pos);
				float ak = k * f;
				float an = k * g;
				float ao = k * h;
				this.vertex(vertexConsumer, d + 0.0, e + (double)p, w + 0.0, ak, an, ao, z, aa, am);
				this.vertex(vertexConsumer, d + 0.0, e + (double)r, w + 1.0, ak, an, ao, ab, ac, am);
				this.vertex(vertexConsumer, d + 1.0, e + (double)q, w + 1.0, ak, an, ao, ad, ae, am);
				this.vertex(vertexConsumer, d + 1.0, e + (double)o, w + 0.0, ak, an, ao, af, ag, am);
				if (fluidState.canFlowTo(world, pos.up())) {
					this.vertex(vertexConsumer, d + 0.0, e + (double)p, w + 0.0, ak, an, ao, z, aa, am);
					this.vertex(vertexConsumer, d + 1.0, e + (double)o, w + 0.0, ak, an, ao, af, ag, am);
					this.vertex(vertexConsumer, d + 1.0, e + (double)q, w + 1.0, ak, an, ao, ad, ae, am);
					this.vertex(vertexConsumer, d + 0.0, e + (double)r, w + 1.0, ak, an, ao, ab, ac, am);
				}
			}

			if (bl3) {
				float zx = sprites[0].getMinU();
				float abx = sprites[0].getMaxU();
				float adx = sprites[0].getMinV();
				float afx = sprites[0].getMaxV();
				int ap = this.getLight(world, pos.down());
				float acx = j * f;
				float aex = j * g;
				float agx = j * h;
				this.vertex(vertexConsumer, d, e + (double)y, w + 1.0, acx, aex, agx, zx, afx, ap);
				this.vertex(vertexConsumer, d, e + (double)y, w, acx, aex, agx, zx, adx, ap);
				this.vertex(vertexConsumer, d + 1.0, e + (double)y, w, acx, aex, agx, abx, adx, ap);
				this.vertex(vertexConsumer, d + 1.0, e + (double)y, w + 1.0, acx, aex, agx, abx, afx, ap);
			}

			int aq = this.getLight(world, pos);

			for (Direction direction : Direction.Type.HORIZONTAL) {
				float afx;
				float aax;
				double ar;
				double at;
				double as;
				double au;
				boolean bl8;
				switch (direction) {
					case NORTH:
						afx = p;
						aax = o;
						ar = d;
						as = d + 1.0;
						at = w + 0.001F;
						au = w + 0.001F;
						bl8 = bl4;
						break;
					case SOUTH:
						afx = q;
						aax = r;
						ar = d + 1.0;
						as = d;
						at = w + 1.0 - 0.001F;
						au = w + 1.0 - 0.001F;
						bl8 = bl5;
						break;
					case WEST:
						afx = r;
						aax = p;
						ar = d + 0.001F;
						as = d + 0.001F;
						at = w + 1.0;
						au = w;
						bl8 = bl6;
						break;
					default:
						afx = o;
						aax = q;
						ar = d + 1.0 - 0.001F;
						as = d + 1.0 - 0.001F;
						at = w;
						au = w + 1.0;
						bl8 = bl7;
				}

				if (bl8 && !isSideCovered(world, pos, direction, Math.max(afx, aax), world.getBlockState(pos.offset(direction)))) {
					BlockPos blockPos = pos.offset(direction);
					Sprite sprite2 = sprites[1];
					if (!bl) {
						Block block = world.getBlockState(blockPos).getBlock();
						if (block instanceof TranslucentBlock || block instanceof LeavesBlock) {
							sprite2 = this.waterOverlaySprite;
						}
					}

					float av = sprite2.getFrameU(0.0F);
					float aw = sprite2.getFrameU(0.5F);
					float ax = sprite2.getFrameV((1.0F - afx) * 0.5F);
					float ay = sprite2.getFrameV((1.0F - aax) * 0.5F);
					float az = sprite2.getFrameV(0.5F);
					float ba = direction.getAxis() == Direction.Axis.Z ? l : m;
					float bb = k * ba * f;
					float bc = k * ba * g;
					float bd = k * ba * h;
					this.vertex(vertexConsumer, ar, e + (double)afx, at, bb, bc, bd, av, ax, aq);
					this.vertex(vertexConsumer, as, e + (double)aax, au, bb, bc, bd, aw, ay, aq);
					this.vertex(vertexConsumer, as, e + (double)y, au, bb, bc, bd, aw, az, aq);
					this.vertex(vertexConsumer, ar, e + (double)y, at, bb, bc, bd, av, az, aq);
					if (sprite2 != this.waterOverlaySprite) {
						this.vertex(vertexConsumer, ar, e + (double)y, at, bb, bc, bd, av, az, aq);
						this.vertex(vertexConsumer, as, e + (double)y, au, bb, bc, bd, aw, az, aq);
						this.vertex(vertexConsumer, as, e + (double)aax, au, bb, bc, bd, aw, ay, aq);
						this.vertex(vertexConsumer, ar, e + (double)afx, at, bb, bc, bd, av, ax, aq);
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
