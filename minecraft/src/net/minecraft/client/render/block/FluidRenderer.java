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
		this.lavaSprites[0] = MinecraftClient.getInstance().getBakedModelManager().getBlockStateMaps().getModel(Blocks.LAVA.getDefaultState()).getSprite();
		this.lavaSprites[1] = spriteAtlasTexture.getSprite(ModelLoader.LAVA_FLOW);
		this.waterSprites[0] = MinecraftClient.getInstance().getBakedModelManager().getBlockStateMaps().getModel(Blocks.WATER.getDefaultState()).getSprite();
		this.waterSprites[1] = spriteAtlasTexture.getSprite(ModelLoader.WATER_FLOW);
		this.waterOverlaySprite = spriteAtlasTexture.getSprite(ModelLoader.WATER_OVERLAY);
	}

	private static boolean isSameFluid(BlockView blockView, BlockPos blockPos, Direction direction, FluidState fluidState) {
		BlockPos blockPos2 = blockPos.offset(direction);
		FluidState fluidState2 = blockView.getFluidState(blockPos2);
		return fluidState2.getFluid().matchesType(fluidState.getFluid());
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

	public boolean tesselate(BlockRenderView blockRenderView, BlockPos blockPos, VertexConsumer vertexConsumer, FluidState fluidState) {
		boolean bl = fluidState.matches(FluidTags.LAVA);
		Sprite[] sprites = bl ? this.lavaSprites : this.waterSprites;
		int i = bl ? 16777215 : BiomeColors.getWaterColor(blockRenderView, blockPos);
		float f = (float)(i >> 16 & 0xFF) / 255.0F;
		float g = (float)(i >> 8 & 0xFF) / 255.0F;
		float h = (float)(i & 0xFF) / 255.0F;
		boolean bl2 = !isSameFluid(blockRenderView, blockPos, Direction.UP, fluidState);
		boolean bl3 = !isSameFluid(blockRenderView, blockPos, Direction.DOWN, fluidState) && !method_3344(blockRenderView, blockPos, Direction.DOWN, 0.8888889F);
		boolean bl4 = !isSameFluid(blockRenderView, blockPos, Direction.NORTH, fluidState);
		boolean bl5 = !isSameFluid(blockRenderView, blockPos, Direction.SOUTH, fluidState);
		boolean bl6 = !isSameFluid(blockRenderView, blockPos, Direction.WEST, fluidState);
		boolean bl7 = !isSameFluid(blockRenderView, blockPos, Direction.EAST, fluidState);
		if (!bl2 && !bl3 && !bl7 && !bl6 && !bl4 && !bl5) {
			return false;
		} else {
			boolean bl8 = false;
			float j = 0.5F;
			float k = 1.0F;
			float l = 0.8F;
			float m = 0.6F;
			float n = this.getNorthWestCornerFluidHeight(blockRenderView, blockPos, fluidState.getFluid());
			float o = this.getNorthWestCornerFluidHeight(blockRenderView, blockPos.south(), fluidState.getFluid());
			float p = this.getNorthWestCornerFluidHeight(blockRenderView, blockPos.east().south(), fluidState.getFluid());
			float q = this.getNorthWestCornerFluidHeight(blockRenderView, blockPos.east(), fluidState.getFluid());
			double d = (double)(blockPos.getX() & 15);
			double e = (double)(blockPos.getY() & 15);
			double r = (double)(blockPos.getZ() & 15);
			float s = 0.001F;
			if (bl2 && !method_3344(blockRenderView, blockPos, Direction.UP, Math.min(Math.min(n, o), Math.min(p, q)))) {
				bl8 = true;
				n -= 0.001F;
				o -= 0.001F;
				p -= 0.001F;
				q -= 0.001F;
				Vec3d vec3d = fluidState.getVelocity(blockRenderView, blockPos);
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
					t = sprite.getU(0.0);
					u = sprite.getV(0.0);
					v = t;
					w = sprite.getV(16.0);
					x = sprite.getU(16.0);
					y = w;
					z = x;
					aa = u;
				} else {
					Sprite sprite = sprites[1];
					float ab = (float)MathHelper.atan2(vec3d.z, vec3d.x) - (float) (Math.PI / 2);
					float ac = MathHelper.sin(ab) * 0.25F;
					float ad = MathHelper.cos(ab) * 0.25F;
					float ae = 8.0F;
					t = sprite.getU((double)(8.0F + (-ad - ac) * 16.0F));
					u = sprite.getV((double)(8.0F + (-ad + ac) * 16.0F));
					v = sprite.getU((double)(8.0F + (-ad + ac) * 16.0F));
					w = sprite.getV((double)(8.0F + (ad + ac) * 16.0F));
					x = sprite.getU((double)(8.0F + (ad + ac) * 16.0F));
					y = sprite.getV((double)(8.0F + (ad - ac) * 16.0F));
					z = sprite.getU((double)(8.0F + (ad - ac) * 16.0F));
					aa = sprite.getV((double)(8.0F + (-ad - ac) * 16.0F));
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
				int ag = this.method_3343(blockRenderView, blockPos);
				float ah = 1.0F * f;
				float ai = 1.0F * g;
				float aj = 1.0F * h;
				this.method_23072(vertexConsumer, d + 0.0, e + (double)n, r + 0.0, ah, ai, aj, t, u, ag);
				this.method_23072(vertexConsumer, d + 0.0, e + (double)o, r + 1.0, ah, ai, aj, v, w, ag);
				this.method_23072(vertexConsumer, d + 1.0, e + (double)p, r + 1.0, ah, ai, aj, x, y, ag);
				this.method_23072(vertexConsumer, d + 1.0, e + (double)q, r + 0.0, ah, ai, aj, z, aa, ag);
				if (fluidState.method_15756(blockRenderView, blockPos.up())) {
					this.method_23072(vertexConsumer, d + 0.0, e + (double)n, r + 0.0, ah, ai, aj, t, u, ag);
					this.method_23072(vertexConsumer, d + 1.0, e + (double)q, r + 0.0, ah, ai, aj, z, aa, ag);
					this.method_23072(vertexConsumer, d + 1.0, e + (double)p, r + 1.0, ah, ai, aj, x, y, ag);
					this.method_23072(vertexConsumer, d + 0.0, e + (double)o, r + 1.0, ah, ai, aj, v, w, ag);
				}
			}

			if (bl3) {
				float tx = sprites[0].getMinU();
				float vx = sprites[0].getMaxU();
				float xx = sprites[0].getMinV();
				float zx = sprites[0].getMaxV();
				int ak = this.method_3343(blockRenderView, blockPos.method_10074());
				float wx = 0.5F * f;
				float yx = 0.5F * g;
				float aax = 0.5F * h;
				this.method_23072(vertexConsumer, d, e, r + 1.0, wx, yx, aax, tx, zx, ak);
				this.method_23072(vertexConsumer, d, e, r, wx, yx, aax, tx, xx, ak);
				this.method_23072(vertexConsumer, d + 1.0, e, r, wx, yx, aax, vx, xx, ak);
				this.method_23072(vertexConsumer, d + 1.0, e, r + 1.0, wx, yx, aax, vx, zx, ak);
				bl8 = true;
			}

			for (int al = 0; al < 4; al++) {
				float vx;
				float xx;
				double am;
				double ao;
				double an;
				double ap;
				Direction direction;
				boolean bl9;
				if (al == 0) {
					vx = n;
					xx = q;
					am = d;
					an = d + 1.0;
					ao = r + 0.001F;
					ap = r + 0.001F;
					direction = Direction.NORTH;
					bl9 = bl4;
				} else if (al == 1) {
					vx = p;
					xx = o;
					am = d + 1.0;
					an = d;
					ao = r + 1.0 - 0.001F;
					ap = r + 1.0 - 0.001F;
					direction = Direction.SOUTH;
					bl9 = bl5;
				} else if (al == 2) {
					vx = o;
					xx = n;
					am = d + 0.001F;
					an = d + 0.001F;
					ao = r + 1.0;
					ap = r;
					direction = Direction.WEST;
					bl9 = bl6;
				} else {
					vx = q;
					xx = p;
					am = d + 1.0 - 0.001F;
					an = d + 1.0 - 0.001F;
					ao = r;
					ap = r + 1.0;
					direction = Direction.EAST;
					bl9 = bl7;
				}

				if (bl9 && !method_3344(blockRenderView, blockPos, direction, Math.max(vx, xx))) {
					bl8 = true;
					BlockPos blockPos2 = blockPos.offset(direction);
					Sprite sprite2 = sprites[1];
					if (!bl) {
						Block block = blockRenderView.getBlockState(blockPos2).getBlock();
						if (block == Blocks.GLASS || block instanceof StainedGlassBlock) {
							sprite2 = this.waterOverlaySprite;
						}
					}

					float ah = sprite2.getU(0.0);
					float ai = sprite2.getU(8.0);
					float aj = sprite2.getV((double)((1.0F - vx) * 16.0F * 0.5F));
					float aq = sprite2.getV((double)((1.0F - xx) * 16.0F * 0.5F));
					float ar = sprite2.getV(8.0);
					int as = this.method_3343(blockRenderView, blockPos2);
					float at = al < 2 ? 0.8F : 0.6F;
					float au = 1.0F * at * f;
					float av = 1.0F * at * g;
					float aw = 1.0F * at * h;
					this.method_23072(vertexConsumer, am, e + (double)vx, ao, au, av, aw, ah, aj, as);
					this.method_23072(vertexConsumer, an, e + (double)xx, ap, au, av, aw, ai, aq, as);
					this.method_23072(vertexConsumer, an, e + 0.0, ap, au, av, aw, ai, ar, as);
					this.method_23072(vertexConsumer, am, e + 0.0, ao, au, av, aw, ah, ar, as);
					if (sprite2 != this.waterOverlaySprite) {
						this.method_23072(vertexConsumer, am, e + 0.0, ao, au, av, aw, ah, ar, as);
						this.method_23072(vertexConsumer, an, e + 0.0, ap, au, av, aw, ai, ar, as);
						this.method_23072(vertexConsumer, an, e + (double)xx, ap, au, av, aw, ai, aq, as);
						this.method_23072(vertexConsumer, am, e + (double)vx, ao, au, av, aw, ah, aj, as);
					}
				}
			}

			return bl8;
		}
	}

	private void method_23072(VertexConsumer vertexConsumer, double d, double e, double f, float g, float h, float i, float j, float k, int l) {
		vertexConsumer.vertex(d, e, f).color(g, h, i, 1.0F).texture(j, k).light(l).normal(0.0F, 1.0F, 0.0F).next();
	}

	private int method_3343(BlockRenderView blockRenderView, BlockPos blockPos) {
		int i = blockRenderView.getLightmapCoordinates(blockPos);
		int j = blockRenderView.getLightmapCoordinates(blockPos.up());
		int k = i & 0xFF;
		int l = j & 0xFF;
		int m = i >> 16 & 0xFF;
		int n = j >> 16 & 0xFF;
		return (k > l ? k : l) | (m > n ? m : n) << 16;
	}

	private float getNorthWestCornerFluidHeight(BlockView blockView, BlockPos blockPos, Fluid fluid) {
		int i = 0;
		float f = 0.0F;

		for (int j = 0; j < 4; j++) {
			BlockPos blockPos2 = blockPos.add(-(j & 1), 0, -(j >> 1 & 1));
			if (blockView.getFluidState(blockPos2.up()).getFluid().matchesType(fluid)) {
				return 1.0F;
			}

			FluidState fluidState = blockView.getFluidState(blockPos2);
			if (fluidState.getFluid().matchesType(fluid)) {
				float g = fluidState.getHeight(blockView, blockPos2);
				if (g >= 0.8F) {
					f += g * 10.0F;
					i += 10;
				} else {
					f += g;
					i++;
				}
			} else if (!blockView.getBlockState(blockPos2).getMaterial().isSolid()) {
				i++;
			}
		}

		return f / (float)i;
	}
}
