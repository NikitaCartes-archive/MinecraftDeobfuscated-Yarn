package net.minecraft.client.render.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.StainedGlassBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
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
		SpriteAtlasTexture spriteAtlasTexture = MinecraftClient.getInstance().getSpriteAtlas();
		this.lavaSprites[0] = MinecraftClient.getInstance().getBakedModelManager().getBlockModels().getModel(Blocks.LAVA.getDefaultState()).getSprite();
		this.lavaSprites[1] = spriteAtlasTexture.getSprite(ModelLoader.LAVA_FLOW);
		this.waterSprites[0] = MinecraftClient.getInstance().getBakedModelManager().getBlockModels().getModel(Blocks.WATER.getDefaultState()).getSprite();
		this.waterSprites[1] = spriteAtlasTexture.getSprite(ModelLoader.WATER_FLOW);
		this.waterOverlaySprite = spriteAtlasTexture.getSprite(ModelLoader.WATER_OVERLAY);
	}

	private static boolean isSameFluid(BlockView world, BlockPos pos, Direction side, FluidState state) {
		BlockPos blockPos = pos.offset(side);
		FluidState fluidState = world.getFluidState(blockPos);
		return fluidState.getFluid().matchesType(state.getFluid());
	}

	private static boolean method_3344(BlockView blockView, BlockPos blockPos, Direction direction, float f) {
		BlockPos blockPos2 = blockPos.offset(direction);
		BlockState blockState = blockView.getBlockState(blockPos2);
		if (blockState.isOpaque()) {
			VoxelShape voxelShape = VoxelShapes.cuboid(0.0, 0.0, 0.0, 1.0, (double)f, 1.0);
			VoxelShape voxelShape2 = blockState.getCullingShape(blockView, blockPos2);
			return VoxelShapes.method_1083(voxelShape, voxelShape2, direction);
		} else {
			return false;
		}
	}

	public boolean tesselate(BlockRenderView world, BlockPos pos, BufferBuilder bufferBuilder, FluidState state) {
		boolean bl = state.matches(FluidTags.LAVA);
		Sprite[] sprites = bl ? this.lavaSprites : this.waterSprites;
		int i = bl ? 16777215 : BiomeColors.getWaterColor(world, pos);
		float f = (float)(i >> 16 & 0xFF) / 255.0F;
		float g = (float)(i >> 8 & 0xFF) / 255.0F;
		float h = (float)(i & 0xFF) / 255.0F;
		boolean bl2 = !isSameFluid(world, pos, Direction.UP, state);
		boolean bl3 = !isSameFluid(world, pos, Direction.DOWN, state) && !method_3344(world, pos, Direction.DOWN, 0.8888889F);
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
			double d = (double)pos.getX();
			double e = (double)pos.getY();
			double r = (double)pos.getZ();
			float s = 0.001F;
			if (bl2 && !method_3344(world, pos, Direction.UP, Math.min(Math.min(n, o), Math.min(p, q)))) {
				bl8 = true;
				n -= 0.001F;
				o -= 0.001F;
				p -= 0.001F;
				q -= 0.001F;
				Vec3d vec3d = state.getVelocity(world, pos);
				float t;
				float v;
				float x;
				float z;
				float u;
				float w;
				float y;
				float aa;
				if (vec3d.x == 0.0 && vec3d.z == 0.0) {
					Sprite sprite = sprites[0];
					t = sprite.getFrameU(0.0);
					u = sprite.getFrameV(0.0);
					v = t;
					w = sprite.getFrameV(16.0);
					x = sprite.getFrameU(16.0);
					y = w;
					z = x;
					aa = u;
				} else {
					Sprite sprite = sprites[1];
					float ab = (float)MathHelper.atan2(vec3d.z, vec3d.x) - (float) (Math.PI / 2);
					float ac = MathHelper.sin(ab) * 0.25F;
					float ad = MathHelper.cos(ab) * 0.25F;
					float ae = 8.0F;
					t = sprite.getFrameU((double)(8.0F + (-ad - ac) * 16.0F));
					u = sprite.getFrameV((double)(8.0F + (-ad + ac) * 16.0F));
					v = sprite.getFrameU((double)(8.0F + (-ad + ac) * 16.0F));
					w = sprite.getFrameV((double)(8.0F + (ad + ac) * 16.0F));
					x = sprite.getFrameU((double)(8.0F + (ad + ac) * 16.0F));
					y = sprite.getFrameV((double)(8.0F + (ad - ac) * 16.0F));
					z = sprite.getFrameU((double)(8.0F + (ad - ac) * 16.0F));
					aa = sprite.getFrameV((double)(8.0F + (-ad - ac) * 16.0F));
				}

				float af = (t + v + x + z) / 4.0F;
				float ab = (u + w + y + aa) / 4.0F;
				float ac = (float)sprites[0].getWidth() / (sprites[0].getMaxU() - sprites[0].getMinU());
				float ad = (float)sprites[0].getHeight() / (sprites[0].getMaxV() - sprites[0].getMinV());
				float ae = 4.0F / Math.max(ad, ac);
				t = MathHelper.lerp(ae, t, af);
				v = MathHelper.lerp(ae, v, af);
				x = MathHelper.lerp(ae, x, af);
				z = MathHelper.lerp(ae, z, af);
				u = MathHelper.lerp(ae, u, ab);
				w = MathHelper.lerp(ae, w, ab);
				y = MathHelper.lerp(ae, y, ab);
				aa = MathHelper.lerp(ae, aa, ab);
				int ag = this.method_3343(world, pos);
				int ah = ag >> 16 & 65535;
				int ai = ag & 65535;
				float aj = 1.0F * f;
				float ak = 1.0F * g;
				float al = 1.0F * h;
				bufferBuilder.vertex(d + 0.0, e + (double)n, r + 0.0).color(aj, ak, al, 1.0F).texture((double)t, (double)u).texture(ah, ai).next();
				bufferBuilder.vertex(d + 0.0, e + (double)o, r + 1.0).color(aj, ak, al, 1.0F).texture((double)v, (double)w).texture(ah, ai).next();
				bufferBuilder.vertex(d + 1.0, e + (double)p, r + 1.0).color(aj, ak, al, 1.0F).texture((double)x, (double)y).texture(ah, ai).next();
				bufferBuilder.vertex(d + 1.0, e + (double)q, r + 0.0).color(aj, ak, al, 1.0F).texture((double)z, (double)aa).texture(ah, ai).next();
				if (state.method_15756(world, pos.up())) {
					bufferBuilder.vertex(d + 0.0, e + (double)n, r + 0.0).color(aj, ak, al, 1.0F).texture((double)t, (double)u).texture(ah, ai).next();
					bufferBuilder.vertex(d + 1.0, e + (double)q, r + 0.0).color(aj, ak, al, 1.0F).texture((double)z, (double)aa).texture(ah, ai).next();
					bufferBuilder.vertex(d + 1.0, e + (double)p, r + 1.0).color(aj, ak, al, 1.0F).texture((double)x, (double)y).texture(ah, ai).next();
					bufferBuilder.vertex(d + 0.0, e + (double)o, r + 1.0).color(aj, ak, al, 1.0F).texture((double)v, (double)w).texture(ah, ai).next();
				}
			}

			if (bl3) {
				float tx = sprites[0].getMinU();
				float vx = sprites[0].getMaxU();
				float xx = sprites[0].getMinV();
				float zx = sprites[0].getMaxV();
				int am = this.method_3343(world, pos.down());
				int an = am >> 16 & 65535;
				int ao = am & 65535;
				float aax = 0.5F * f;
				float ap = 0.5F * g;
				float af = 0.5F * h;
				bufferBuilder.vertex(d, e, r + 1.0).color(aax, ap, af, 1.0F).texture((double)tx, (double)zx).texture(an, ao).next();
				bufferBuilder.vertex(d, e, r).color(aax, ap, af, 1.0F).texture((double)tx, (double)xx).texture(an, ao).next();
				bufferBuilder.vertex(d + 1.0, e, r).color(aax, ap, af, 1.0F).texture((double)vx, (double)xx).texture(an, ao).next();
				bufferBuilder.vertex(d + 1.0, e, r + 1.0).color(aax, ap, af, 1.0F).texture((double)vx, (double)zx).texture(an, ao).next();
				bl8 = true;
			}

			for (int aq = 0; aq < 4; aq++) {
				float vx;
				float xx;
				double ar;
				double at;
				double as;
				double au;
				Direction direction;
				boolean bl9;
				if (aq == 0) {
					vx = n;
					xx = q;
					ar = d;
					as = d + 1.0;
					at = r + 0.001F;
					au = r + 0.001F;
					direction = Direction.NORTH;
					bl9 = bl4;
				} else if (aq == 1) {
					vx = p;
					xx = o;
					ar = d + 1.0;
					as = d;
					at = r + 1.0 - 0.001F;
					au = r + 1.0 - 0.001F;
					direction = Direction.SOUTH;
					bl9 = bl5;
				} else if (aq == 2) {
					vx = o;
					xx = n;
					ar = d + 0.001F;
					as = d + 0.001F;
					at = r + 1.0;
					au = r;
					direction = Direction.WEST;
					bl9 = bl6;
				} else {
					vx = q;
					xx = p;
					ar = d + 1.0 - 0.001F;
					as = d + 1.0 - 0.001F;
					at = r;
					au = r + 1.0;
					direction = Direction.EAST;
					bl9 = bl7;
				}

				if (bl9 && !method_3344(world, pos, direction, Math.max(vx, xx))) {
					bl8 = true;
					BlockPos blockPos = pos.offset(direction);
					Sprite sprite2 = sprites[1];
					if (!bl) {
						Block block = world.getBlockState(blockPos).getBlock();
						if (block == Blocks.GLASS || block instanceof StainedGlassBlock) {
							sprite2 = this.waterOverlaySprite;
						}
					}

					float av = sprite2.getFrameU(0.0);
					float aw = sprite2.getFrameU(8.0);
					float aj = sprite2.getFrameV((double)((1.0F - vx) * 16.0F * 0.5F));
					float ak = sprite2.getFrameV((double)((1.0F - xx) * 16.0F * 0.5F));
					float al = sprite2.getFrameV(8.0);
					int ax = this.method_3343(world, blockPos);
					int ay = ax >> 16 & 65535;
					int az = ax & 65535;
					float ba = aq < 2 ? 0.8F : 0.6F;
					float bb = 1.0F * ba * f;
					float bc = 1.0F * ba * g;
					float bd = 1.0F * ba * h;
					bufferBuilder.vertex(ar, e + (double)vx, at).color(bb, bc, bd, 1.0F).texture((double)av, (double)aj).texture(ay, az).next();
					bufferBuilder.vertex(as, e + (double)xx, au).color(bb, bc, bd, 1.0F).texture((double)aw, (double)ak).texture(ay, az).next();
					bufferBuilder.vertex(as, e + 0.0, au).color(bb, bc, bd, 1.0F).texture((double)aw, (double)al).texture(ay, az).next();
					bufferBuilder.vertex(ar, e + 0.0, at).color(bb, bc, bd, 1.0F).texture((double)av, (double)al).texture(ay, az).next();
					if (sprite2 != this.waterOverlaySprite) {
						bufferBuilder.vertex(ar, e + 0.0, at).color(bb, bc, bd, 1.0F).texture((double)av, (double)al).texture(ay, az).next();
						bufferBuilder.vertex(as, e + 0.0, au).color(bb, bc, bd, 1.0F).texture((double)aw, (double)al).texture(ay, az).next();
						bufferBuilder.vertex(as, e + (double)xx, au).color(bb, bc, bd, 1.0F).texture((double)aw, (double)ak).texture(ay, az).next();
						bufferBuilder.vertex(ar, e + (double)vx, at).color(bb, bc, bd, 1.0F).texture((double)av, (double)aj).texture(ay, az).next();
					}
				}
			}

			return bl8;
		}
	}

	private int method_3343(BlockRenderView blockRenderView, BlockPos blockPos) {
		int i = blockRenderView.getLightmapIndex(blockPos, 0);
		int j = blockRenderView.getLightmapIndex(blockPos.up(), 0);
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
