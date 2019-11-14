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
		int i = bl ? 16777215 : BiomeColors.getWaterColor(world, pos);
		float f = (float)(i >> 16 & 0xFF) / 255.0F;
		float g = (float)(i >> 8 & 0xFF) / 255.0F;
		float h = (float)(i & 0xFF) / 255.0F;
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
			if (bl2 && !isSideCovered(world, pos, Direction.UP, Math.min(Math.min(n, o), Math.min(p, q)))) {
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
				int ag = this.getLight(world, pos);
				float ah = 1.0F * f;
				float ai = 1.0F * g;
				float aj = 1.0F * h;
				this.vertex(vertexConsumer, d + 0.0, e + (double)n, r + 0.0, ah, ai, aj, t, u, ag);
				this.vertex(vertexConsumer, d + 0.0, e + (double)o, r + 1.0, ah, ai, aj, v, w, ag);
				this.vertex(vertexConsumer, d + 1.0, e + (double)p, r + 1.0, ah, ai, aj, x, y, ag);
				this.vertex(vertexConsumer, d + 1.0, e + (double)q, r + 0.0, ah, ai, aj, z, aa, ag);
				if (state.method_15756(world, pos.up())) {
					this.vertex(vertexConsumer, d + 0.0, e + (double)n, r + 0.0, ah, ai, aj, t, u, ag);
					this.vertex(vertexConsumer, d + 1.0, e + (double)q, r + 0.0, ah, ai, aj, z, aa, ag);
					this.vertex(vertexConsumer, d + 1.0, e + (double)p, r + 1.0, ah, ai, aj, x, y, ag);
					this.vertex(vertexConsumer, d + 0.0, e + (double)o, r + 1.0, ah, ai, aj, v, w, ag);
				}
			}

			if (bl3) {
				float tx = sprites[0].getMinU();
				float vx = sprites[0].getMaxU();
				float xx = sprites[0].getMinV();
				float zx = sprites[0].getMaxV();
				int ak = this.getLight(world, pos.method_10074());
				float wx = 0.5F * f;
				float yx = 0.5F * g;
				float aax = 0.5F * h;
				this.vertex(vertexConsumer, d, e, r + 1.0, wx, yx, aax, tx, zx, ak);
				this.vertex(vertexConsumer, d, e, r, wx, yx, aax, tx, xx, ak);
				this.vertex(vertexConsumer, d + 1.0, e, r, wx, yx, aax, vx, xx, ak);
				this.vertex(vertexConsumer, d + 1.0, e, r + 1.0, wx, yx, aax, vx, zx, ak);
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

				if (bl9 && !isSideCovered(world, pos, direction, Math.max(vx, xx))) {
					bl8 = true;
					BlockPos blockPos = pos.offset(direction);
					Sprite sprite2 = sprites[1];
					if (!bl) {
						Block block = world.getBlockState(blockPos).getBlock();
						if (block == Blocks.GLASS || block instanceof StainedGlassBlock) {
							sprite2 = this.waterOverlaySprite;
						}
					}

					float ah = sprite2.getFrameU(0.0);
					float ai = sprite2.getFrameU(8.0);
					float aj = sprite2.getFrameV((double)((1.0F - vx) * 16.0F * 0.5F));
					float aq = sprite2.getFrameV((double)((1.0F - xx) * 16.0F * 0.5F));
					float ar = sprite2.getFrameV(8.0);
					int as = this.getLight(world, blockPos);
					float at = al < 2 ? 0.8F : 0.6F;
					float au = 1.0F * at * f;
					float av = 1.0F * at * g;
					float aw = 1.0F * at * h;
					this.vertex(vertexConsumer, am, e + (double)vx, ao, au, av, aw, ah, aj, as);
					this.vertex(vertexConsumer, an, e + (double)xx, ap, au, av, aw, ai, aq, as);
					this.vertex(vertexConsumer, an, e + 0.0, ap, au, av, aw, ai, ar, as);
					this.vertex(vertexConsumer, am, e + 0.0, ao, au, av, aw, ah, ar, as);
					if (sprite2 != this.waterOverlaySprite) {
						this.vertex(vertexConsumer, am, e + 0.0, ao, au, av, aw, ah, ar, as);
						this.vertex(vertexConsumer, an, e + 0.0, ap, au, av, aw, ai, ar, as);
						this.vertex(vertexConsumer, an, e + (double)xx, ap, au, av, aw, ai, aq, as);
						this.vertex(vertexConsumer, am, e + (double)vx, ao, au, av, aw, ah, aj, as);
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
		int i = WorldRenderer.method_23794(world, pos);
		int j = WorldRenderer.method_23794(world, pos.up());
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
