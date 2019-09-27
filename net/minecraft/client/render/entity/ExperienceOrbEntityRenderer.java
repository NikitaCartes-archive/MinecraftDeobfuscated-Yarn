/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_4597;
import net.minecraft.class_4608;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class ExperienceOrbEntityRenderer
extends EntityRenderer<ExperienceOrbEntity> {
    private static final Identifier SKIN = new Identifier("textures/entity/experience_orb.png");

    public ExperienceOrbEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher);
        this.field_4673 = 0.15f;
        this.field_4672 = 0.75f;
    }

    public void method_3966(ExperienceOrbEntity experienceOrbEntity, double d, double e, double f, float g, float h, class_4587 arg, class_4597 arg2) {
        arg.method_22903();
        int i = experienceOrbEntity.getOrbSize();
        float j = (float)(i % 4 * 16 + 0) / 64.0f;
        float k = (float)(i % 4 * 16 + 16) / 64.0f;
        float l = (float)(i / 4 * 16 + 0) / 64.0f;
        float m = (float)(i / 4 * 16 + 16) / 64.0f;
        float n = 1.0f;
        float o = 0.5f;
        float p = 0.25f;
        float q = 255.0f;
        float r = ((float)experienceOrbEntity.renderTicks + h) / 2.0f;
        int s = (int)((MathHelper.sin(r + 0.0f) + 1.0f) * 0.5f * 255.0f);
        int t = 255;
        int u = (int)((MathHelper.sin(r + 4.1887903f) + 1.0f) * 0.1f * 255.0f);
        arg.method_22904(0.0, 0.1f, 0.0);
        arg.method_22907(Vector3f.field_20705.method_23214(180.0f - this.renderManager.cameraYaw, true));
        arg.method_22907(Vector3f.field_20703.method_23214((float)(this.renderManager.gameOptions.perspective == 2 ? -1 : 1) * -this.renderManager.cameraPitch, true));
        float v = 0.3f;
        arg.method_22905(0.3f, 0.3f, 0.3f);
        int w = experienceOrbEntity.getLightmapCoordinates();
        class_4588 lv = arg2.getBuffer(BlockRenderLayer.method_23017(SKIN));
        class_4608.method_23211(lv);
        Matrix4f matrix4f = arg.method_22910();
        ExperienceOrbEntityRenderer.method_23171(lv, matrix4f, -0.5f, -0.25f, s, 255, u, j, m, w);
        ExperienceOrbEntityRenderer.method_23171(lv, matrix4f, 0.5f, -0.25f, s, 255, u, k, m, w);
        ExperienceOrbEntityRenderer.method_23171(lv, matrix4f, 0.5f, 0.75f, s, 255, u, k, l, w);
        ExperienceOrbEntityRenderer.method_23171(lv, matrix4f, -0.5f, 0.75f, s, 255, u, j, l, w);
        lv.method_22923();
        arg.method_22909();
        super.render(experienceOrbEntity, d, e, f, g, h, arg, arg2);
    }

    private static void method_23171(class_4588 arg, Matrix4f matrix4f, float f, float g, int i, int j, int k, float h, float l, int m) {
        arg.method_22918(matrix4f, f, g, 0.0f).color(i, j, k, 128).texture(h, l).method_22916(m).method_22914(0.0f, 1.0f, 0.0f).next();
    }

    public Identifier method_3967(ExperienceOrbEntity experienceOrbEntity) {
        return SKIN;
    }
}

