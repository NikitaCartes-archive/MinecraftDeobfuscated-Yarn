/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.block.entity;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.ConduitBlockEntity;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class ConduitBlockEntityRenderer
extends BlockEntityRenderer<ConduitBlockEntity> {
    private static final Identifier BASE_TEX = new Identifier("textures/entity/conduit/base.png");
    private static final Identifier CAGE_TEX = new Identifier("textures/entity/conduit/cage.png");
    private static final Identifier WIND_TEX = new Identifier("textures/entity/conduit/wind.png");
    private static final Identifier WIND_VERTICAL_TEX = new Identifier("textures/entity/conduit/wind_vertical.png");
    private static final Identifier OPEN_EYE_TEX = new Identifier("textures/entity/conduit/open_eye.png");
    private static final Identifier CLOSED_EYE_TEX = new Identifier("textures/entity/conduit/closed_eye.png");
    private final BaseModel baseModel = new BaseModel();
    private final CageModel cageModel = new CageModel();
    private final WindModel windModel = new WindModel();
    private final EyeModel eyeModel = new EyeModel();

    public void method_3572(ConduitBlockEntity conduitBlockEntity, double d, double e, double f, float g, int i) {
        float h = (float)conduitBlockEntity.ticks + g;
        if (!conduitBlockEntity.isActive()) {
            float j = conduitBlockEntity.getRotation(0.0f);
            this.bindTexture(BASE_TEX);
            RenderSystem.pushMatrix();
            RenderSystem.translatef((float)d + 0.5f, (float)e + 0.5f, (float)f + 0.5f);
            RenderSystem.rotatef(j, 0.0f, 1.0f, 0.0f);
            this.baseModel.render(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0625f);
            RenderSystem.popMatrix();
        } else if (conduitBlockEntity.isActive()) {
            float j = conduitBlockEntity.getRotation(g) * 57.295776f;
            float k = MathHelper.sin(h * 0.1f) / 2.0f + 0.5f;
            k = k * k + k;
            this.bindTexture(CAGE_TEX);
            RenderSystem.disableCull();
            RenderSystem.pushMatrix();
            RenderSystem.translatef((float)d + 0.5f, (float)e + 0.3f + k * 0.2f, (float)f + 0.5f);
            RenderSystem.rotatef(j, 0.5f, 1.0f, 0.5f);
            this.cageModel.render(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0625f);
            RenderSystem.popMatrix();
            int l = 3;
            int m = conduitBlockEntity.ticks / 3 % 22;
            this.windModel.method_3573(m);
            int n = conduitBlockEntity.ticks / 66 % 3;
            switch (n) {
                case 0: {
                    this.bindTexture(WIND_TEX);
                    RenderSystem.pushMatrix();
                    RenderSystem.translatef((float)d + 0.5f, (float)e + 0.5f, (float)f + 0.5f);
                    this.windModel.render(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0625f);
                    RenderSystem.popMatrix();
                    RenderSystem.pushMatrix();
                    RenderSystem.translatef((float)d + 0.5f, (float)e + 0.5f, (float)f + 0.5f);
                    RenderSystem.scalef(0.875f, 0.875f, 0.875f);
                    RenderSystem.rotatef(180.0f, 1.0f, 0.0f, 0.0f);
                    RenderSystem.rotatef(180.0f, 0.0f, 0.0f, 1.0f);
                    this.windModel.render(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0625f);
                    RenderSystem.popMatrix();
                    break;
                }
                case 1: {
                    this.bindTexture(WIND_VERTICAL_TEX);
                    RenderSystem.pushMatrix();
                    RenderSystem.translatef((float)d + 0.5f, (float)e + 0.5f, (float)f + 0.5f);
                    RenderSystem.rotatef(90.0f, 1.0f, 0.0f, 0.0f);
                    this.windModel.render(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0625f);
                    RenderSystem.popMatrix();
                    RenderSystem.pushMatrix();
                    RenderSystem.translatef((float)d + 0.5f, (float)e + 0.5f, (float)f + 0.5f);
                    RenderSystem.scalef(0.875f, 0.875f, 0.875f);
                    RenderSystem.rotatef(180.0f, 1.0f, 0.0f, 0.0f);
                    RenderSystem.rotatef(180.0f, 0.0f, 0.0f, 1.0f);
                    this.windModel.render(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0625f);
                    RenderSystem.popMatrix();
                    break;
                }
                case 2: {
                    this.bindTexture(WIND_TEX);
                    RenderSystem.pushMatrix();
                    RenderSystem.translatef((float)d + 0.5f, (float)e + 0.5f, (float)f + 0.5f);
                    RenderSystem.rotatef(90.0f, 0.0f, 0.0f, 1.0f);
                    this.windModel.render(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0625f);
                    RenderSystem.popMatrix();
                    RenderSystem.pushMatrix();
                    RenderSystem.translatef((float)d + 0.5f, (float)e + 0.5f, (float)f + 0.5f);
                    RenderSystem.scalef(0.875f, 0.875f, 0.875f);
                    RenderSystem.rotatef(180.0f, 1.0f, 0.0f, 0.0f);
                    RenderSystem.rotatef(180.0f, 0.0f, 0.0f, 1.0f);
                    this.windModel.render(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0625f);
                    RenderSystem.popMatrix();
                }
            }
            Camera camera = this.renderManager.cameraEntity;
            if (conduitBlockEntity.isEyeOpen()) {
                this.bindTexture(OPEN_EYE_TEX);
            } else {
                this.bindTexture(CLOSED_EYE_TEX);
            }
            RenderSystem.pushMatrix();
            RenderSystem.translatef((float)d + 0.5f, (float)e + 0.3f + k * 0.2f, (float)f + 0.5f);
            RenderSystem.scalef(0.5f, 0.5f, 0.5f);
            RenderSystem.rotatef(-camera.getYaw(), 0.0f, 1.0f, 0.0f);
            RenderSystem.rotatef(camera.getPitch(), 1.0f, 0.0f, 0.0f);
            RenderSystem.rotatef(180.0f, 0.0f, 0.0f, 1.0f);
            this.eyeModel.render(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.083333336f);
            RenderSystem.popMatrix();
        }
        super.render(conduitBlockEntity, d, e, f, g, i);
    }

    @Environment(value=EnvType.CLIENT)
    static class EyeModel
    extends Model {
        private final ModelPart cuboid;

        public EyeModel() {
            this.textureWidth = 8;
            this.textureHeight = 8;
            this.cuboid = new ModelPart(this, 0, 0);
            this.cuboid.addCuboid(-4.0f, -4.0f, 0.0f, 8, 8, 0, 0.01f);
        }

        public void render(float f, float g, float h, float i, float j, float k) {
            this.cuboid.render(k);
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class WindModel
    extends Model {
        private final ModelPart[] cuboids = new ModelPart[22];
        private int field_4384;

        public WindModel() {
            this.textureWidth = 64;
            this.textureHeight = 1024;
            for (int i = 0; i < 22; ++i) {
                this.cuboids[i] = new ModelPart(this, 0, 32 * i);
                this.cuboids[i].addCuboid(-8.0f, -8.0f, -8.0f, 16, 16, 16);
            }
        }

        public void render(float f, float g, float h, float i, float j, float k) {
            this.cuboids[this.field_4384].render(k);
        }

        public void method_3573(int i) {
            this.field_4384 = i;
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class CageModel
    extends Model {
        private final ModelPart cuboid;

        public CageModel() {
            this.textureWidth = 32;
            this.textureHeight = 16;
            this.cuboid = new ModelPart(this, 0, 0);
            this.cuboid.addCuboid(-4.0f, -4.0f, -4.0f, 8, 8, 8);
        }

        public void render(float f, float g, float h, float i, float j, float k) {
            this.cuboid.render(k);
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class BaseModel
    extends Model {
        private final ModelPart cuboid;

        public BaseModel() {
            this.textureWidth = 32;
            this.textureHeight = 16;
            this.cuboid = new ModelPart(this, 0, 0);
            this.cuboid.addCuboid(-3.0f, -3.0f, -3.0f, 6, 6, 6);
        }

        public void render(float f, float g, float h, float i, float j, float k) {
            this.cuboid.render(k);
        }
    }
}

