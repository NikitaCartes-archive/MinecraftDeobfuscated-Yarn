/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.class_4538;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VisibleRegion;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
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
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public abstract class EntityRenderer<T extends Entity> {
    private static final Identifier SHADOW_TEX = new Identifier("textures/misc/shadow.png");
    protected final EntityRenderDispatcher renderManager;
    protected float field_4673;
    protected float field_4672 = 1.0f;
    protected boolean renderOutlines;

    protected EntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        this.renderManager = entityRenderDispatcher;
    }

    public void setRenderOutlines(boolean bl) {
        this.renderOutlines = bl;
    }

    public boolean isVisible(T entity, VisibleRegion visibleRegion, double d, double e, double f) {
        if (!((Entity)entity).shouldRenderFrom(d, e, f)) {
            return false;
        }
        if (((Entity)entity).ignoreCameraFrustum) {
            return true;
        }
        Box box = ((Entity)entity).getVisibilityBoundingBox().expand(0.5);
        if (box.isValid() || box.getAverageSideLength() == 0.0) {
            box = new Box(((Entity)entity).x - 2.0, ((Entity)entity).y - 2.0, ((Entity)entity).z - 2.0, ((Entity)entity).x + 2.0, ((Entity)entity).y + 2.0, ((Entity)entity).z + 2.0);
        }
        return visibleRegion.intersects(box);
    }

    public void render(T entity, double d, double e, double f, float g, float h) {
        if (!this.renderOutlines) {
            this.renderLabelIfPresent(entity, d, e, f);
        }
    }

    protected int getOutlineColor(T entity) {
        Team team = (Team)((Entity)entity).getScoreboardTeam();
        if (team != null && team.getColor().getColorValue() != null) {
            return team.getColor().getColorValue();
        }
        return 0xFFFFFF;
    }

    protected void renderLabelIfPresent(T entity, double d, double e, double f) {
        if (!this.hasLabel(entity)) {
            return;
        }
        this.renderLabel(entity, ((Entity)entity).getDisplayName().asFormattedString(), d, e, f, 64);
    }

    protected boolean hasLabel(T entity) {
        return ((Entity)entity).shouldRenderName() && ((Entity)entity).hasCustomName();
    }

    protected void renderLabel(T entity, double d, double e, double f, String string, double g) {
        this.renderLabel(entity, string, d, e, f, 64);
    }

    @Nullable
    protected abstract Identifier getTexture(T var1);

    protected boolean bindEntityTexture(T entity) {
        Identifier identifier = this.getTexture(entity);
        if (identifier == null) {
            return false;
        }
        this.bindTexture(identifier);
        return true;
    }

    public void bindTexture(Identifier identifier) {
        this.renderManager.textureManager.bindTexture(identifier);
    }

    private void renderEntityOnFire(Entity entity, double d, double e, double f, float g) {
        RenderSystem.disableLighting();
        SpriteAtlasTexture spriteAtlasTexture = MinecraftClient.getInstance().getSpriteAtlas();
        Sprite sprite = spriteAtlasTexture.getSprite(ModelLoader.FIRE_0);
        Sprite sprite2 = spriteAtlasTexture.getSprite(ModelLoader.FIRE_1);
        RenderSystem.pushMatrix();
        RenderSystem.translatef((float)d, (float)e, (float)f);
        float h = entity.getWidth() * 1.4f;
        RenderSystem.scalef(h, h, h);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
        float i = 0.5f;
        float j = 0.0f;
        float k = entity.getHeight() / h;
        float l = (float)(entity.y - entity.getBoundingBox().minY);
        RenderSystem.rotatef(-this.renderManager.cameraYaw, 0.0f, 1.0f, 0.0f);
        RenderSystem.translatef(0.0f, 0.0f, -0.3f + (float)((int)k) * 0.02f);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        float m = 0.0f;
        int n = 0;
        bufferBuilder.begin(7, VertexFormats.POSITION_UV);
        while (k > 0.0f) {
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
            bufferBuilder.vertex(i - 0.0f, 0.0f - l, m).texture(q, r).next();
            bufferBuilder.vertex(-i - 0.0f, 0.0f - l, m).texture(o, r).next();
            bufferBuilder.vertex(-i - 0.0f, 1.4f - l, m).texture(o, p).next();
            bufferBuilder.vertex(i - 0.0f, 1.4f - l, m).texture(q, p).next();
            k -= 0.45f;
            l -= 0.45f;
            i *= 0.9f;
            m += 0.03f;
            ++n;
        }
        tessellator.draw();
        RenderSystem.popMatrix();
        RenderSystem.enableLighting();
    }

    private void renderShadow(Entity entity, double d, double e, double f, float g, float h) {
        MobEntity mobEntity;
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.class_4535.SRC_ALPHA, GlStateManager.class_4534.ONE_MINUS_SRC_ALPHA);
        this.renderManager.textureManager.bindTexture(SHADOW_TEX);
        class_4538 lv = this.getWorld();
        RenderSystem.depthMask(false);
        float i = this.field_4673;
        if (entity instanceof MobEntity && (mobEntity = (MobEntity)entity).isBaby()) {
            i *= 0.5f;
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
            BlockState blockState = lv.getBlockState(blockPos2);
            if (blockState.getRenderType() == BlockRenderType.INVISIBLE || lv.getLightLevel(blockPos) <= 3) continue;
            this.projectShadow(blockState, lv, blockPos2, d, e, f, blockPos, g, i, s, t, u);
        }
        tessellator.draw();
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.disableBlend();
        RenderSystem.depthMask(true);
    }

    private class_4538 getWorld() {
        return this.renderManager.world;
    }

    private void projectShadow(BlockState blockState, class_4538 arg, BlockPos blockPos, double d, double e, double f, BlockPos blockPos2, float g, float h, double i, double j, double k) {
        if (!blockState.method_21743(arg, blockPos)) {
            return;
        }
        VoxelShape voxelShape = blockState.getOutlineShape(this.getWorld(), blockPos2.down());
        if (voxelShape.isEmpty()) {
            return;
        }
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
        double l = ((double)g - (e - ((double)blockPos2.getY() + j)) / 2.0) * 0.5 * (double)this.getWorld().getBrightness(blockPos2);
        if (l < 0.0) {
            return;
        }
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
        bufferBuilder.vertex(m, o, p).texture(r, t).color(1.0f, 1.0f, 1.0f, (float)l).next();
        bufferBuilder.vertex(m, o, q).texture(r, u).color(1.0f, 1.0f, 1.0f, (float)l).next();
        bufferBuilder.vertex(n, o, q).texture(s, u).color(1.0f, 1.0f, 1.0f, (float)l).next();
        bufferBuilder.vertex(n, o, p).texture(s, t).color(1.0f, 1.0f, 1.0f, (float)l).next();
    }

    public static void renderBox(Box box, double d, double e, double f) {
        RenderSystem.disableTexture();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        bufferBuilder.setOffset(d, e, f);
        bufferBuilder.begin(7, VertexFormats.POSITION_NORMAL);
        bufferBuilder.vertex(box.minX, box.maxY, box.minZ).normal(0.0f, 0.0f, -1.0f).next();
        bufferBuilder.vertex(box.maxX, box.maxY, box.minZ).normal(0.0f, 0.0f, -1.0f).next();
        bufferBuilder.vertex(box.maxX, box.minY, box.minZ).normal(0.0f, 0.0f, -1.0f).next();
        bufferBuilder.vertex(box.minX, box.minY, box.minZ).normal(0.0f, 0.0f, -1.0f).next();
        bufferBuilder.vertex(box.minX, box.minY, box.maxZ).normal(0.0f, 0.0f, 1.0f).next();
        bufferBuilder.vertex(box.maxX, box.minY, box.maxZ).normal(0.0f, 0.0f, 1.0f).next();
        bufferBuilder.vertex(box.maxX, box.maxY, box.maxZ).normal(0.0f, 0.0f, 1.0f).next();
        bufferBuilder.vertex(box.minX, box.maxY, box.maxZ).normal(0.0f, 0.0f, 1.0f).next();
        bufferBuilder.vertex(box.minX, box.minY, box.minZ).normal(0.0f, -1.0f, 0.0f).next();
        bufferBuilder.vertex(box.maxX, box.minY, box.minZ).normal(0.0f, -1.0f, 0.0f).next();
        bufferBuilder.vertex(box.maxX, box.minY, box.maxZ).normal(0.0f, -1.0f, 0.0f).next();
        bufferBuilder.vertex(box.minX, box.minY, box.maxZ).normal(0.0f, -1.0f, 0.0f).next();
        bufferBuilder.vertex(box.minX, box.maxY, box.maxZ).normal(0.0f, 1.0f, 0.0f).next();
        bufferBuilder.vertex(box.maxX, box.maxY, box.maxZ).normal(0.0f, 1.0f, 0.0f).next();
        bufferBuilder.vertex(box.maxX, box.maxY, box.minZ).normal(0.0f, 1.0f, 0.0f).next();
        bufferBuilder.vertex(box.minX, box.maxY, box.minZ).normal(0.0f, 1.0f, 0.0f).next();
        bufferBuilder.vertex(box.minX, box.minY, box.maxZ).normal(-1.0f, 0.0f, 0.0f).next();
        bufferBuilder.vertex(box.minX, box.maxY, box.maxZ).normal(-1.0f, 0.0f, 0.0f).next();
        bufferBuilder.vertex(box.minX, box.maxY, box.minZ).normal(-1.0f, 0.0f, 0.0f).next();
        bufferBuilder.vertex(box.minX, box.minY, box.minZ).normal(-1.0f, 0.0f, 0.0f).next();
        bufferBuilder.vertex(box.maxX, box.minY, box.minZ).normal(1.0f, 0.0f, 0.0f).next();
        bufferBuilder.vertex(box.maxX, box.maxY, box.minZ).normal(1.0f, 0.0f, 0.0f).next();
        bufferBuilder.vertex(box.maxX, box.maxY, box.maxZ).normal(1.0f, 0.0f, 0.0f).next();
        bufferBuilder.vertex(box.maxX, box.minY, box.maxZ).normal(1.0f, 0.0f, 0.0f).next();
        tessellator.draw();
        bufferBuilder.setOffset(0.0, 0.0, 0.0);
        RenderSystem.enableTexture();
    }

    public void postRender(Entity entity, double d, double e, double f, float g, float h) {
        double i;
        float j;
        if (this.renderManager.gameOptions == null) {
            return;
        }
        if (this.renderManager.gameOptions.entityShadows && this.field_4673 > 0.0f && !entity.isInvisible() && this.renderManager.shouldRenderShadows() && (j = (float)((1.0 - (i = this.renderManager.squaredDistanceToCamera(entity.x, entity.y, entity.z)) / 256.0) * (double)this.field_4672)) > 0.0f) {
            this.renderShadow(entity, d, e, f, j, h);
        }
        if (entity.doesRenderOnFire() && !entity.isSpectator()) {
            this.renderEntityOnFire(entity, d, e, f, h);
        }
    }

    public TextRenderer getFontRenderer() {
        return this.renderManager.getTextRenderer();
    }

    protected void renderLabel(T entity, String string, double d, double e, double f, int i) {
        double g = ((Entity)entity).squaredDistanceTo(this.renderManager.camera.getPos());
        if (g > (double)(i * i)) {
            return;
        }
        float h = this.renderManager.cameraYaw;
        float j = this.renderManager.cameraPitch;
        float k = ((Entity)entity).getHeight() + 0.5f - (((Entity)entity).isInSneakingPose() ? 0.25f : 0.0f);
        int l = "deadmau5".equals(string) ? -10 : 0;
        GameRenderer.renderFloatingText(this.getFontRenderer(), string, (float)d, (float)e + k, (float)f, l, h, j, ((Entity)entity).method_21751());
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
        int i = ((Entity)entity).getLightmapCoordinates();
        int j = i % 65536;
        int k = i / 65536;
        RenderSystem.glMultiTexCoord2f(33985, j, k);
    }
}

