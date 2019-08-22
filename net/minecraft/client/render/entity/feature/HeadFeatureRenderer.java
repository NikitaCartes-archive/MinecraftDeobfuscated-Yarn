/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.feature;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AbstractSkullBlock;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.block.entity.SkullBlockEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.ModelWithHead;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.TagHelper;
import org.apache.commons.lang3.StringUtils;

@Environment(value=EnvType.CLIENT)
public class HeadFeatureRenderer<T extends LivingEntity, M extends EntityModel<T>>
extends FeatureRenderer<T, M> {
    public HeadFeatureRenderer(FeatureRendererContext<T, M> featureRendererContext) {
        super(featureRendererContext);
    }

    public void method_17159(T livingEntity, float f, float g, float h, float i, float j, float k, float l) {
        float m;
        boolean bl;
        ItemStack itemStack = ((LivingEntity)livingEntity).getEquippedStack(EquipmentSlot.HEAD);
        if (itemStack.isEmpty()) {
            return;
        }
        Item item = itemStack.getItem();
        RenderSystem.pushMatrix();
        if (((Entity)livingEntity).isInSneakingPose()) {
            RenderSystem.translatef(0.0f, 0.2f, 0.0f);
        }
        boolean bl2 = bl = livingEntity instanceof VillagerEntity || livingEntity instanceof ZombieVillagerEntity;
        if (((LivingEntity)livingEntity).isBaby() && !(livingEntity instanceof VillagerEntity)) {
            m = 2.0f;
            float n = 1.4f;
            RenderSystem.translatef(0.0f, 0.5f * l, 0.0f);
            RenderSystem.scalef(0.7f, 0.7f, 0.7f);
            RenderSystem.translatef(0.0f, 16.0f * l, 0.0f);
        }
        ((ModelWithHead)this.getModel()).setHeadAngle(0.0625f);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        if (item instanceof BlockItem && ((BlockItem)item).getBlock() instanceof AbstractSkullBlock) {
            m = 1.1875f;
            RenderSystem.scalef(1.1875f, -1.1875f, -1.1875f);
            if (bl) {
                RenderSystem.translatef(0.0f, 0.0625f, 0.0f);
            }
            GameProfile gameProfile = null;
            if (itemStack.hasTag()) {
                String string;
                CompoundTag compoundTag = itemStack.getTag();
                if (compoundTag.containsKey("SkullOwner", 10)) {
                    gameProfile = TagHelper.deserializeProfile(compoundTag.getCompound("SkullOwner"));
                } else if (compoundTag.containsKey("SkullOwner", 8) && !StringUtils.isBlank(string = compoundTag.getString("SkullOwner"))) {
                    gameProfile = SkullBlockEntity.loadProperties(new GameProfile(null, string));
                    compoundTag.put("SkullOwner", TagHelper.serializeProfile(new CompoundTag(), gameProfile));
                }
            }
            SkullBlockEntityRenderer.INSTANCE.render(-0.5f, 0.0f, -0.5f, null, 180.0f, ((AbstractSkullBlock)((BlockItem)item).getBlock()).getSkullType(), gameProfile, -1, f);
        } else if (!(item instanceof ArmorItem) || ((ArmorItem)item).getSlotType() != EquipmentSlot.HEAD) {
            m = 0.625f;
            RenderSystem.translatef(0.0f, -0.25f, 0.0f);
            RenderSystem.rotatef(180.0f, 0.0f, 1.0f, 0.0f);
            RenderSystem.scalef(0.625f, -0.625f, -0.625f);
            if (bl) {
                RenderSystem.translatef(0.0f, 0.1875f, 0.0f);
            }
            MinecraftClient.getInstance().getFirstPersonRenderer().renderItem((LivingEntity)livingEntity, itemStack, ModelTransformation.Type.HEAD);
        }
        RenderSystem.popMatrix();
    }

    @Override
    public boolean hasHurtOverlay() {
        return false;
    }
}

