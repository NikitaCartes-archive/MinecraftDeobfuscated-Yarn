/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.feature;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class TridentRiptideFeatureRenderer<T extends LivingEntity>
extends FeatureRenderer<T, PlayerEntityModel<T>> {
    public static final Identifier TEXTURE = new Identifier("textures/entity/trident_riptide.png");
    private final TridentRiptideModel model = new TridentRiptideModel();

    public TridentRiptideFeatureRenderer(FeatureRendererContext<T, PlayerEntityModel<T>> featureRendererContext) {
        super(featureRendererContext);
    }

    public void method_4203(T livingEntity, float f, float g, float h, float i, float j, float k, float l) {
        if (!((LivingEntity)livingEntity).isUsingRiptide()) {
            return;
        }
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.bindTexture(TEXTURE);
        for (int m = 0; m < 3; ++m) {
            RenderSystem.pushMatrix();
            RenderSystem.rotatef(i * (float)(-(45 + m * 5)), 0.0f, 1.0f, 0.0f);
            float n = 0.75f * (float)m;
            RenderSystem.scalef(n, n, n);
            RenderSystem.translatef(0.0f, -0.2f + 0.6f * (float)m, 0.0f);
            this.model.method_17166(f, g, i, j, k, l);
            RenderSystem.popMatrix();
        }
    }

    @Override
    public boolean hasHurtOverlay() {
        return false;
    }

    @Environment(value=EnvType.CLIENT)
    static class TridentRiptideModel
    extends Model {
        private final ModelPart field_4900;

        public TridentRiptideModel() {
            this.textureWidth = 64;
            this.textureHeight = 64;
            this.field_4900 = new ModelPart(this, 0, 0);
            this.field_4900.addCuboid(-8.0f, -16.0f, -8.0f, 16.0f, 32.0f, 16.0f);
        }

        public void method_17166(float f, float g, float h, float i, float j, float k) {
            this.field_4900.render(k);
        }
    }
}

