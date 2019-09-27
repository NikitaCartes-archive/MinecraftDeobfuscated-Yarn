package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_4597;
import net.minecraft.class_4608;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.PaintingManager;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.decoration.painting.PaintingEntity;
import net.minecraft.entity.decoration.painting.PaintingMotive;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class PaintingEntityRenderer extends EntityRenderer<PaintingEntity> {
	public PaintingEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher);
	}

	public void method_4075(PaintingEntity paintingEntity, double d, double e, double f, float g, float h, class_4587 arg, class_4597 arg2) {
		arg.method_22903();
		arg.method_22907(Vector3f.field_20705.method_23214(180.0F - g, true));
		PaintingMotive paintingMotive = paintingEntity.motive;
		float i = 0.0625F;
		arg.method_22905(0.0625F, 0.0625F, 0.0625F);
		class_4588 lv = arg2.getBuffer(BlockRenderLayer.method_23017(this.method_4077(paintingEntity)));
		class_4608.method_23211(lv);
		PaintingManager paintingManager = MinecraftClient.getInstance().getPaintingManager();
		this.method_4074(
			arg.method_22910(),
			lv,
			paintingEntity,
			paintingMotive.getWidth(),
			paintingMotive.getHeight(),
			paintingManager.getPaintingSprite(paintingMotive),
			paintingManager.getBackSprite()
		);
		lv.method_22923();
		arg.method_22909();
		super.render(paintingEntity, d, e, f, g, h, arg, arg2);
	}

	public Identifier method_4077(PaintingEntity paintingEntity) {
		return SpriteAtlasTexture.PAINTING_ATLAS_TEX;
	}

	private void method_4074(Matrix4f matrix4f, class_4588 arg, PaintingEntity paintingEntity, int i, int j, Sprite sprite, Sprite sprite2) {
		float f = (float)(-i) / 2.0F;
		float g = (float)(-j) / 2.0F;
		float h = 0.5F;
		float k = sprite2.getMinU();
		float l = sprite2.getMaxU();
		float m = sprite2.getMinV();
		float n = sprite2.getMaxV();
		float o = sprite2.getMinU();
		float p = sprite2.getMaxU();
		float q = sprite2.getMinV();
		float r = sprite2.getV(1.0);
		float s = sprite2.getMinU();
		float t = sprite2.getU(1.0);
		float u = sprite2.getMinV();
		float v = sprite2.getMaxV();
		int w = i / 16;
		int x = j / 16;
		double d = 16.0 / (double)w;
		double e = 16.0 / (double)x;

		for (int y = 0; y < w; y++) {
			for (int z = 0; z < x; z++) {
				float aa = f + (float)((y + 1) * 16);
				float ab = f + (float)(y * 16);
				float ac = g + (float)((z + 1) * 16);
				float ad = g + (float)(z * 16);
				int ae = MathHelper.floor(paintingEntity.x);
				int af = MathHelper.floor(paintingEntity.y + (double)((ac + ad) / 2.0F / 16.0F));
				int ag = MathHelper.floor(paintingEntity.z);
				Direction direction = paintingEntity.getHorizontalFacing();
				if (direction == Direction.NORTH) {
					ae = MathHelper.floor(paintingEntity.x + (double)((aa + ab) / 2.0F / 16.0F));
				}

				if (direction == Direction.WEST) {
					ag = MathHelper.floor(paintingEntity.z - (double)((aa + ab) / 2.0F / 16.0F));
				}

				if (direction == Direction.SOUTH) {
					ae = MathHelper.floor(paintingEntity.x - (double)((aa + ab) / 2.0F / 16.0F));
				}

				if (direction == Direction.EAST) {
					ag = MathHelper.floor(paintingEntity.z + (double)((aa + ab) / 2.0F / 16.0F));
				}

				int ah = paintingEntity.world.getLightmapIndex(new BlockPos(ae, af, ag));
				float ai = sprite.getU(d * (double)(w - y));
				float aj = sprite.getU(d * (double)(w - (y + 1)));
				float ak = sprite.getV(e * (double)(x - z));
				float al = sprite.getV(e * (double)(x - (z + 1)));
				this.method_23188(matrix4f, arg, aa, ad, aj, ak, -0.5F, 0, 0, -1, ah);
				this.method_23188(matrix4f, arg, ab, ad, ai, ak, -0.5F, 0, 0, -1, ah);
				this.method_23188(matrix4f, arg, ab, ac, ai, al, -0.5F, 0, 0, -1, ah);
				this.method_23188(matrix4f, arg, aa, ac, aj, al, -0.5F, 0, 0, -1, ah);
				this.method_23188(matrix4f, arg, aa, ac, k, m, 0.5F, 0, 0, 1, ah);
				this.method_23188(matrix4f, arg, ab, ac, l, m, 0.5F, 0, 0, 1, ah);
				this.method_23188(matrix4f, arg, ab, ad, l, n, 0.5F, 0, 0, 1, ah);
				this.method_23188(matrix4f, arg, aa, ad, k, n, 0.5F, 0, 0, 1, ah);
				this.method_23188(matrix4f, arg, aa, ac, o, q, -0.5F, 0, 1, 0, ah);
				this.method_23188(matrix4f, arg, ab, ac, p, q, -0.5F, 0, 1, 0, ah);
				this.method_23188(matrix4f, arg, ab, ac, p, r, 0.5F, 0, 1, 0, ah);
				this.method_23188(matrix4f, arg, aa, ac, o, r, 0.5F, 0, 1, 0, ah);
				this.method_23188(matrix4f, arg, aa, ad, o, q, 0.5F, 0, -1, 0, ah);
				this.method_23188(matrix4f, arg, ab, ad, p, q, 0.5F, 0, -1, 0, ah);
				this.method_23188(matrix4f, arg, ab, ad, p, r, -0.5F, 0, -1, 0, ah);
				this.method_23188(matrix4f, arg, aa, ad, o, r, -0.5F, 0, -1, 0, ah);
				this.method_23188(matrix4f, arg, aa, ac, t, u, 0.5F, -1, 0, 0, ah);
				this.method_23188(matrix4f, arg, aa, ad, t, v, 0.5F, -1, 0, 0, ah);
				this.method_23188(matrix4f, arg, aa, ad, s, v, -0.5F, -1, 0, 0, ah);
				this.method_23188(matrix4f, arg, aa, ac, s, u, -0.5F, -1, 0, 0, ah);
				this.method_23188(matrix4f, arg, ab, ac, t, u, -0.5F, 1, 0, 0, ah);
				this.method_23188(matrix4f, arg, ab, ad, t, v, -0.5F, 1, 0, 0, ah);
				this.method_23188(matrix4f, arg, ab, ad, s, v, 0.5F, 1, 0, 0, ah);
				this.method_23188(matrix4f, arg, ab, ac, s, u, 0.5F, 1, 0, 0, ah);
			}
		}
	}

	private void method_23188(Matrix4f matrix4f, class_4588 arg, float f, float g, float h, float i, float j, int k, int l, int m, int n) {
		arg.method_22918(matrix4f, f, g, j).color(255, 255, 255, 255).texture(h, i).method_22916(n).method_22914((float)k, (float)l, (float)m).next();
	}
}
