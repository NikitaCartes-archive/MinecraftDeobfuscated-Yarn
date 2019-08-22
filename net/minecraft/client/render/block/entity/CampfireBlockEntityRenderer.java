/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.block.entity;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.entity.CampfireBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.math.Direction;

@Environment(value=EnvType.CLIENT)
public class CampfireBlockEntityRenderer
extends BlockEntityRenderer<CampfireBlockEntity> {
    public void method_17581(CampfireBlockEntity campfireBlockEntity, double d, double e, double f, float g, int i) {
        Direction direction = campfireBlockEntity.getCachedState().get(CampfireBlock.FACING);
        DefaultedList<ItemStack> defaultedList = campfireBlockEntity.getItemsBeingCooked();
        for (int j = 0; j < defaultedList.size(); ++j) {
            ItemStack itemStack = defaultedList.get(j);
            if (itemStack == ItemStack.EMPTY) continue;
            RenderSystem.pushMatrix();
            RenderSystem.translatef((float)d + 0.5f, (float)e + 0.44921875f, (float)f + 0.5f);
            Direction direction2 = Direction.fromHorizontal((j + direction.getHorizontal()) % 4);
            RenderSystem.rotatef(-direction2.asRotation(), 0.0f, 1.0f, 0.0f);
            RenderSystem.rotatef(90.0f, 1.0f, 0.0f, 0.0f);
            RenderSystem.translatef(-0.3125f, -0.3125f, 0.0f);
            RenderSystem.scalef(0.375f, 0.375f, 0.375f);
            MinecraftClient.getInstance().getItemRenderer().renderItem(itemStack, ModelTransformation.Type.FIXED);
            RenderSystem.popMatrix();
        }
    }
}

