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
import net.minecraft.class_4604;
import net.minecraft.class_4608;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.EnderDragonEntityRenderer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.decoration.EnderCrystalEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;

@Environment(value=EnvType.CLIENT)
public class EnderCrystalEntityRenderer
extends EntityRenderer<EnderCrystalEntity> {
    private static final Identifier SKIN = new Identifier("textures/entity/end_crystal/end_crystal.png");
    public static final float field_21002 = (float)Math.sin(0.7853981633974483);
    private final ModelPart field_21003;
    private final ModelPart field_21004;
    private final ModelPart field_21005;

    public EnderCrystalEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher);
        this.field_4673 = 0.5f;
        this.field_21004 = new ModelPart(64, 32, 0, 0);
        this.field_21004.addCuboid(-4.0f, -4.0f, -4.0f, 8.0f, 8.0f, 8.0f);
        this.field_21003 = new ModelPart(64, 32, 32, 0);
        this.field_21003.addCuboid(-4.0f, -4.0f, -4.0f, 8.0f, 8.0f, 8.0f);
        this.field_21005 = new ModelPart(64, 32, 0, 16);
        this.field_21005.addCuboid(-6.0f, 0.0f, -6.0f, 12.0f, 4.0f, 12.0f);
    }

    public void method_3908(EnderCrystalEntity enderCrystalEntity, double d, double e, double f, float g, float h, class_4587 arg, class_4597 arg2) {
        arg.method_22903();
        float i = EnderCrystalEntityRenderer.method_23155(enderCrystalEntity, h);
        float j = 0.0625f;
        float k = ((float)enderCrystalEntity.field_7034 + h) * 3.0f;
        int l = enderCrystalEntity.getLightmapCoordinates();
        class_4588 lv = arg2.getBuffer(BlockRenderLayer.method_23017(this.method_3909(enderCrystalEntity)));
        class_4608.method_23211(lv);
        arg.method_22903();
        arg.method_22905(2.0f, 2.0f, 2.0f);
        arg.method_22904(0.0, -0.5, 0.0);
        if (enderCrystalEntity.getShowBottom()) {
            this.field_21005.method_22698(arg, lv, 0.0625f, l, null);
        }
        arg.method_22907(Vector3f.field_20705.method_23214(k, true));
        arg.method_22904(0.0, 1.5f + i / 2.0f, 0.0);
        arg.method_22907(new Quaternion(new Vector3f(field_21002, 0.0f, field_21002), 60.0f, true));
        this.field_21004.method_22698(arg, lv, 0.0625f, l, null);
        float m = 0.875f;
        arg.method_22905(0.875f, 0.875f, 0.875f);
        arg.method_22907(new Quaternion(new Vector3f(field_21002, 0.0f, field_21002), 60.0f, true));
        arg.method_22907(Vector3f.field_20705.method_23214(k, true));
        this.field_21004.method_22698(arg, lv, 0.0625f, l, null);
        arg.method_22905(0.875f, 0.875f, 0.875f);
        arg.method_22907(new Quaternion(new Vector3f(field_21002, 0.0f, field_21002), 60.0f, true));
        arg.method_22907(Vector3f.field_20705.method_23214(k, true));
        this.field_21003.method_22698(arg, lv, 0.0625f, l, null);
        arg.method_22909();
        arg.method_22909();
        lv.method_22923();
        BlockPos blockPos = enderCrystalEntity.getBeamTarget();
        if (blockPos != null) {
            float n = (float)blockPos.getX() + 0.5f;
            float o = (float)blockPos.getY() + 0.5f;
            float p = (float)blockPos.getZ() + 0.5f;
            float q = (float)((double)n - enderCrystalEntity.x);
            float r = (float)((double)o - enderCrystalEntity.y);
            float s = (float)((double)p - enderCrystalEntity.z);
            arg.method_22904(q, r, s);
            EnderDragonEntityRenderer.renderCrystalBeam(-q, -r + i, -s, h, enderCrystalEntity.field_7034, arg, arg2, l);
        }
        super.render(enderCrystalEntity, d, e, f, g, h, arg, arg2);
    }

    public static float method_23155(EnderCrystalEntity enderCrystalEntity, float f) {
        float g = (float)enderCrystalEntity.field_7034 + f;
        float h = MathHelper.sin(g * 0.2f) / 2.0f + 0.5f;
        h = (h * h + h) * 0.4f;
        return h - 1.4f;
    }

    public Identifier method_3909(EnderCrystalEntity enderCrystalEntity) {
        return SKIN;
    }

    public boolean method_3907(EnderCrystalEntity enderCrystalEntity, class_4604 arg, double d, double e, double f) {
        return super.isVisible(enderCrystalEntity, arg, d, e, f) || enderCrystalEntity.getBeamTarget() != null;
    }
}

