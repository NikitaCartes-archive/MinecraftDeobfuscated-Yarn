/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.feature;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.SheepEntityModel;
import net.minecraft.client.render.entity.model.SheepWoolEntityModel;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class SheepWoolFeatureRenderer
extends FeatureRenderer<SheepEntity, SheepWoolEntityModel<SheepEntity>> {
    private static final Identifier SKIN = new Identifier("textures/entity/sheep/sheep_fur.png");
    private final SheepEntityModel<SheepEntity> model = new SheepEntityModel();

    public SheepWoolFeatureRenderer(FeatureRendererContext<SheepEntity, SheepWoolEntityModel<SheepEntity>> featureRendererContext) {
        super(featureRendererContext);
    }

    public void method_4198(SheepEntity sheepEntity, float f, float g, float h, float i, float j, float k, float l) {
        if (sheepEntity.isSheared() || sheepEntity.isInvisible()) {
            return;
        }
        this.bindTexture(SKIN);
        if (sheepEntity.hasCustomName() && "jeb_".equals(sheepEntity.getName().asString())) {
            int m = 25;
            int n = sheepEntity.age / 25 + sheepEntity.getEntityId();
            int o = DyeColor.values().length;
            int p = n % o;
            int q = (n + 1) % o;
            float r = ((float)(sheepEntity.age % 25) + h) / 25.0f;
            float[] fs = SheepEntity.getRgbColor(DyeColor.byId(p));
            float[] gs = SheepEntity.getRgbColor(DyeColor.byId(q));
            GlStateManager.color3f(fs[0] * (1.0f - r) + gs[0] * r, fs[1] * (1.0f - r) + gs[1] * r, fs[2] * (1.0f - r) + gs[2] * r);
        } else {
            float[] hs = SheepEntity.getRgbColor(sheepEntity.getColor());
            GlStateManager.color3f(hs[0], hs[1], hs[2]);
        }
        ((SheepWoolEntityModel)this.getModel()).copyStateTo(this.model);
        this.model.method_17118(sheepEntity, f, g, h);
        this.model.render(sheepEntity, f, g, i, j, k, l);
    }

    @Override
    public boolean hasHurtOverlay() {
        return true;
    }
}

