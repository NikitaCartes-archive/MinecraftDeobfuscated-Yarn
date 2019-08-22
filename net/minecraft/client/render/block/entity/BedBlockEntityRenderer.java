/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.block.entity;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Arrays;
import java.util.Comparator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BedBlockEntity;
import net.minecraft.block.enums.BedPart;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.entity.model.BedEntityModel;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

@Environment(value=EnvType.CLIENT)
public class BedBlockEntityRenderer
extends BlockEntityRenderer<BedBlockEntity> {
    private static final Identifier[] TEXTURES = (Identifier[])Arrays.stream(DyeColor.values()).sorted(Comparator.comparingInt(DyeColor::getId)).map(dyeColor -> new Identifier("textures/entity/bed/" + dyeColor.getName() + ".png")).toArray(Identifier[]::new);
    private final BedEntityModel model = new BedEntityModel();

    public void method_3557(BedBlockEntity bedBlockEntity, double d, double e, double f, float g, int i) {
        if (i >= 0) {
            this.bindTexture(DESTROY_STAGE_TEXTURES[i]);
            RenderSystem.matrixMode(5890);
            RenderSystem.pushMatrix();
            RenderSystem.scalef(4.0f, 4.0f, 1.0f);
            RenderSystem.translatef(0.0625f, 0.0625f, 0.0625f);
            RenderSystem.matrixMode(5888);
        } else {
            Identifier identifier = TEXTURES[bedBlockEntity.getColor().getId()];
            if (identifier != null) {
                this.bindTexture(identifier);
            }
        }
        if (bedBlockEntity.hasWorld()) {
            BlockState blockState = bedBlockEntity.getCachedState();
            this.method_3558(blockState.get(BedBlock.PART) == BedPart.HEAD, d, e, f, blockState.get(BedBlock.FACING));
        } else {
            this.method_3558(true, d, e, f, Direction.SOUTH);
            this.method_3558(false, d, e, f - 1.0, Direction.SOUTH);
        }
        if (i >= 0) {
            RenderSystem.matrixMode(5890);
            RenderSystem.popMatrix();
            RenderSystem.matrixMode(5888);
        }
    }

    private void method_3558(boolean bl, double d, double e, double f, Direction direction) {
        this.model.setVisible(bl);
        RenderSystem.pushMatrix();
        RenderSystem.translatef((float)d, (float)e + 0.5625f, (float)f);
        RenderSystem.rotatef(90.0f, 1.0f, 0.0f, 0.0f);
        RenderSystem.translatef(0.5f, 0.5f, 0.5f);
        RenderSystem.rotatef(180.0f + direction.asRotation(), 0.0f, 0.0f, 1.0f);
        RenderSystem.translatef(-0.5f, -0.5f, -0.5f);
        RenderSystem.enableRescaleNormal();
        this.model.render();
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.popMatrix();
    }
}

