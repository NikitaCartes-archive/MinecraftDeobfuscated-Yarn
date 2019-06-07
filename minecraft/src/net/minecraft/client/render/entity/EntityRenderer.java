package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VisibleRegion;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.ViewableWorld;

@Environment(EnvType.CLIENT)
public abstract class EntityRenderer<T extends Entity> {
	private static final Identifier SHADOW_TEX = new Identifier("textures/misc/shadow.png");
	protected final EntityRenderDispatcher renderManager;
	protected float field_4673;
	protected float field_4672 = 1.0F;
	protected boolean renderOutlines;

	protected EntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		this.renderManager = entityRenderDispatcher;
	}

	public void setRenderOutlines(boolean bl) {
		this.renderOutlines = bl;
	}

	public boolean isVisible(T entity, VisibleRegion visibleRegion, double d, double e, double f) {
		if (!entity.shouldRenderFrom(d, e, f)) {
			return false;
		} else if (entity.ignoreCameraFrustum) {
			return true;
		} else {
			Box box = entity.getVisibilityBoundingBox().expand(0.5);
			if (box.isValid() || box.averageDimension() == 0.0) {
				box = new Box(entity.x - 2.0, entity.y - 2.0, entity.z - 2.0, entity.x + 2.0, entity.y + 2.0, entity.z + 2.0);
			}

			return visibleRegion.intersects(box);
		}
	}

	public void render(T entity, double d, double e, double f, float g, float h) {
		if (!this.renderOutlines) {
			this.renderLabelIfPresent(entity, d, e, f);
		}
	}

	protected int getOutlineColor(T entity) {
		Team team = (Team)entity.getScoreboardTeam();
		return team != null && team.getColor().getColorValue() != null ? team.getColor().getColorValue() : 16777215;
	}

	protected void renderLabelIfPresent(T entity, double d, double e, double f) {
		if (this.hasLabel(entity)) {
			this.renderLabel(entity, entity.getDisplayName().asFormattedString(), d, e, f, 64);
		}
	}

	protected boolean hasLabel(T entity) {
		return entity.shouldRenderName() && entity.hasCustomName();
	}

	protected void renderLabel(T entity, double d, double e, double f, String string, double g) {
		this.renderLabel(entity, string, d, e, f, 64);
	}

	@Nullable
	protected abstract Identifier getTexture(T entity);

	protected boolean bindEntityTexture(T entity) {
		Identifier identifier = this.getTexture(entity);
		if (identifier == null) {
			return false;
		} else {
			this.bindTexture(identifier);
			return true;
		}
	}

	public void bindTexture(Identifier identifier) {
		this.renderManager.textureManager.bindTexture(identifier);
	}

	private void renderEntityOnFire(Entity entity, double d, double e, double f, float g) {
		GlStateManager.disableLighting();
		SpriteAtlasTexture spriteAtlasTexture = MinecraftClient.getInstance().getSpriteAtlas();
		Sprite sprite = spriteAtlasTexture.getSprite(ModelLoader.FIRE_0);
		Sprite sprite2 = spriteAtlasTexture.getSprite(ModelLoader.FIRE_1);
		GlStateManager.pushMatrix();
		GlStateManager.translatef((float)d, (float)e, (float)f);
		float h = entity.getWidth() * 1.4F;
		GlStateManager.scalef(h, h, h);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
		float i = 0.5F;
		float j = 0.0F;
		float k = entity.getHeight() / h;
		float l = (float)(entity.y - entity.getBoundingBox().minY);
		GlStateManager.rotatef(-this.renderManager.cameraYaw, 0.0F, 1.0F, 0.0F);
		GlStateManager.translatef(0.0F, 0.0F, -0.3F + (float)((int)k) * 0.02F);
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		float m = 0.0F;
		int n = 0;
		bufferBuilder.begin(7, VertexFormats.POSITION_UV);

		while (k > 0.0F) {
			Sprite sprite3 = n % 2 == 0 ? sprite : sprite2;
			this.bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
			float o = sprite3.getMinU();
			float p = sprite3.getMinV();
			float q = sprite3.getMaxU();
			float r = sprite3.getMaxV();
			if (n / 2 % 2 == 0) {
				float s = q;
				q = o;
				o = s;
			}

			bufferBuilder.vertex((double)(i - 0.0F), (double)(0.0F - l), (double)m).texture((double)q, (double)r).next();
			bufferBuilder.vertex((double)(-i - 0.0F), (double)(0.0F - l), (double)m).texture((double)o, (double)r).next();
			bufferBuilder.vertex((double)(-i - 0.0F), (double)(1.4F - l), (double)m).texture((double)o, (double)p).next();
			bufferBuilder.vertex((double)(i - 0.0F), (double)(1.4F - l), (double)m).texture((double)q, (double)p).next();
			k -= 0.45F;
			l -= 0.45F;
			i *= 0.9F;
			m += 0.03F;
			n++;
		}

		tessellator.draw();
		GlStateManager.popMatrix();
		GlStateManager.enableLighting();
	}

	private void renderShadow(Entity entity, double d, double e, double f, float g, float h) {
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		this.renderManager.textureManager.bindTexture(SHADOW_TEX);
		ViewableWorld viewableWorld = this.getWorld();
		GlStateManager.depthMask(false);
		float i = this.field_4673;
		if (entity instanceof MobEntity) {
			MobEntity mobEntity = (MobEntity)entity;
			if (mobEntity.isBaby()) {
				i *= 0.5F;
			}
		}

		double j = MathHelper.lerp((double)h, entity.prevRenderX, entity.x);
		double k = MathHelper.lerp((double)h, entity.prevRenderY, entity.y);
		double l = MathHelper.lerp((double)h, entity.prevRenderZ, entity.z);
		int m = MathHelper.floor(j - (double)i);
		int n = MathHelper.floor(j + (double)i);
		int o = MathHelper.floor(k - (double)i);
		int p = MathHelper.floor(k);
		int q = MathHelper.floor(l - (double)i);
		int r = MathHelper.floor(l + (double)i);
		double s = d - j;
		double t = e - k;
		double u = f - l;
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
		bufferBuilder.begin(7, VertexFormats.POSITION_UV_COLOR);

		for (BlockPos blockPos : BlockPos.iterate(new BlockPos(m, o, q), new BlockPos(n, p, r))) {
			BlockPos blockPos2 = blockPos.down();
			BlockState blockState = viewableWorld.getBlockState(blockPos2);
			if (blockState.getRenderType() != BlockRenderType.field_11455 && viewableWorld.getLightLevel(blockPos) > 3) {
				this.projectShadow(blockState, viewableWorld, blockPos2, d, e, f, blockPos, g, i, s, t, u);
			}
		}

		tessellator.draw();
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.disableBlend();
		GlStateManager.depthMask(true);
	}

	private ViewableWorld getWorld() {
		return this.renderManager.world;
	}

	private void projectShadow(
		BlockState blockState,
		ViewableWorld viewableWorld,
		BlockPos blockPos,
		double d,
		double e,
		double f,
		BlockPos blockPos2,
		float g,
		float h,
		double i,
		double j,
		double k
	) {
		if (Block.isShapeFullCube(blockState.getCollisionShape(viewableWorld, blockPos))) {
			VoxelShape voxelShape = blockState.getOutlineShape(this.getWorld(), blockPos2.down());
			if (!voxelShape.isEmpty()) {
				Tessellator tessellator = Tessellator.getInstance();
				BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
				double l = ((double)g - (e - ((double)blockPos2.getY() + j)) / 2.0) * 0.5 * (double)this.getWorld().getBrightness(blockPos2);
				if (!(l < 0.0)) {
					if (l > 1.0) {
						l = 1.0;
					}

					Box box = voxelShape.getBoundingBox();
					double m = (double)blockPos2.getX() + box.minX + i;
					double n = (double)blockPos2.getX() + box.maxX + i;
					double o = (double)blockPos2.getY() + box.minY + j + 0.015625;
					double p = (double)blockPos2.getZ() + box.minZ + k;
					double q = (double)blockPos2.getZ() + box.maxZ + k;
					float r = (float)((d - m) / 2.0 / (double)h + 0.5);
					float s = (float)((d - n) / 2.0 / (double)h + 0.5);
					float t = (float)((f - p) / 2.0 / (double)h + 0.5);
					float u = (float)((f - q) / 2.0 / (double)h + 0.5);
					bufferBuilder.vertex(m, o, p).texture((double)r, (double)t).color(1.0F, 1.0F, 1.0F, (float)l).next();
					bufferBuilder.vertex(m, o, q).texture((double)r, (double)u).color(1.0F, 1.0F, 1.0F, (float)l).next();
					bufferBuilder.vertex(n, o, q).texture((double)s, (double)u).color(1.0F, 1.0F, 1.0F, (float)l).next();
					bufferBuilder.vertex(n, o, p).texture((double)s, (double)t).color(1.0F, 1.0F, 1.0F, (float)l).next();
				}
			}
		}
	}

	public static void renderBox(Box box, double d, double e, double f) {
		GlStateManager.disableTexture();
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		bufferBuilder.setOffset(d, e, f);
		bufferBuilder.begin(7, VertexFormats.POSITION_NORMAL);
		bufferBuilder.vertex(box.minX, box.maxY, box.minZ).normal(0.0F, 0.0F, -1.0F).next();
		bufferBuilder.vertex(box.maxX, box.maxY, box.minZ).normal(0.0F, 0.0F, -1.0F).next();
		bufferBuilder.vertex(box.maxX, box.minY, box.minZ).normal(0.0F, 0.0F, -1.0F).next();
		bufferBuilder.vertex(box.minX, box.minY, box.minZ).normal(0.0F, 0.0F, -1.0F).next();
		bufferBuilder.vertex(box.minX, box.minY, box.maxZ).normal(0.0F, 0.0F, 1.0F).next();
		bufferBuilder.vertex(box.maxX, box.minY, box.maxZ).normal(0.0F, 0.0F, 1.0F).next();
		bufferBuilder.vertex(box.maxX, box.maxY, box.maxZ).normal(0.0F, 0.0F, 1.0F).next();
		bufferBuilder.vertex(box.minX, box.maxY, box.maxZ).normal(0.0F, 0.0F, 1.0F).next();
		bufferBuilder.vertex(box.minX, box.minY, box.minZ).normal(0.0F, -1.0F, 0.0F).next();
		bufferBuilder.vertex(box.maxX, box.minY, box.minZ).normal(0.0F, -1.0F, 0.0F).next();
		bufferBuilder.vertex(box.maxX, box.minY, box.maxZ).normal(0.0F, -1.0F, 0.0F).next();
		bufferBuilder.vertex(box.minX, box.minY, box.maxZ).normal(0.0F, -1.0F, 0.0F).next();
		bufferBuilder.vertex(box.minX, box.maxY, box.maxZ).normal(0.0F, 1.0F, 0.0F).next();
		bufferBuilder.vertex(box.maxX, box.maxY, box.maxZ).normal(0.0F, 1.0F, 0.0F).next();
		bufferBuilder.vertex(box.maxX, box.maxY, box.minZ).normal(0.0F, 1.0F, 0.0F).next();
		bufferBuilder.vertex(box.minX, box.maxY, box.minZ).normal(0.0F, 1.0F, 0.0F).next();
		bufferBuilder.vertex(box.minX, box.minY, box.maxZ).normal(-1.0F, 0.0F, 0.0F).next();
		bufferBuilder.vertex(box.minX, box.maxY, box.maxZ).normal(-1.0F, 0.0F, 0.0F).next();
		bufferBuilder.vertex(box.minX, box.maxY, box.minZ).normal(-1.0F, 0.0F, 0.0F).next();
		bufferBuilder.vertex(box.minX, box.minY, box.minZ).normal(-1.0F, 0.0F, 0.0F).next();
		bufferBuilder.vertex(box.maxX, box.minY, box.minZ).normal(1.0F, 0.0F, 0.0F).next();
		bufferBuilder.vertex(box.maxX, box.maxY, box.minZ).normal(1.0F, 0.0F, 0.0F).next();
		bufferBuilder.vertex(box.maxX, box.maxY, box.maxZ).normal(1.0F, 0.0F, 0.0F).next();
		bufferBuilder.vertex(box.maxX, box.minY, box.maxZ).normal(1.0F, 0.0F, 0.0F).next();
		tessellator.draw();
		bufferBuilder.setOffset(0.0, 0.0, 0.0);
		GlStateManager.enableTexture();
	}

	public void postRender(Entity entity, double d, double e, double f, float g, float h) {
		if (this.renderManager.gameOptions != null) {
			if (this.renderManager.gameOptions.entityShadows && this.field_4673 > 0.0F && !entity.isInvisible() && this.renderManager.shouldRenderShadows()) {
				double i = this.renderManager.squaredDistanceToCamera(entity.x, entity.y, entity.z);
				float j = (float)((1.0 - i / 256.0) * (double)this.field_4672);
				if (j > 0.0F) {
					this.renderShadow(entity, d, e, f, j, h);
				}
			}

			if (entity.doesRenderOnFire() && !entity.isSpectator()) {
				this.renderEntityOnFire(entity, d, e, f, h);
			}
		}
	}

	public TextRenderer getFontRenderer() {
		return this.renderManager.getTextRenderer();
	}

	protected void renderLabel(T entity, String string, double d, double e, double f, int i) {
		double g = entity.squaredDistanceTo(this.renderManager.camera.getPos());
		if (!(g > (double)(i * i))) {
			boolean bl = entity.isInSneakingPose();
			float h = this.renderManager.cameraYaw;
			float j = this.renderManager.cameraPitch;
			float k = entity.getHeight() + 0.5F - (bl ? 0.25F : 0.0F);
			int l = "deadmau5".equals(string) ? -10 : 0;
			GameRenderer.renderFloatingText(this.getFontRenderer(), string, (float)d, (float)e + k, (float)f, l, h, j, bl);
		}
	}

	public EntityRenderDispatcher getRenderManager() {
		return this.renderManager;
	}

	public boolean hasSecondPass() {
		return false;
	}

	public void renderSecondPass(T entity, double d, double e, double f, float g, float h) {
	}

	public void applyLightmapCoordinates(T entity) {
		int i = entity.getLightmapCoordinates();
		int j = i % 65536;
		int k = i / 65536;
		GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, (float)j, (float)k);
	}
}
