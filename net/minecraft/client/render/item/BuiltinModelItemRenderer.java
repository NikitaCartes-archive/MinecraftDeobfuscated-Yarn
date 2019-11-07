/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.item;

import com.mojang.authlib.GameProfile;
import java.util.Arrays;
import java.util.Comparator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AbstractBannerBlock;
import net.minecraft.block.AbstractSkullBlock;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.block.entity.BedBlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.ConduitBlockEntity;
import net.minecraft.block.entity.EnderChestBlockEntity;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.block.entity.TrappedChestBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BannerBlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.SkullBlockEntityRenderer;
import net.minecraft.client.render.entity.model.ShieldEntityModel;
import net.minecraft.client.render.entity.model.TridentEntityModel;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ShieldItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.util.DyeColor;
import org.apache.commons.lang3.StringUtils;

@Environment(value=EnvType.CLIENT)
public class BuiltinModelItemRenderer {
    private static final ShulkerBoxBlockEntity[] RENDER_SHULKER_BOX_DYED = (ShulkerBoxBlockEntity[])Arrays.stream(DyeColor.values()).sorted(Comparator.comparingInt(DyeColor::getId)).map(ShulkerBoxBlockEntity::new).toArray(ShulkerBoxBlockEntity[]::new);
    private static final ShulkerBoxBlockEntity RENDER_SHULKER_BOX = new ShulkerBoxBlockEntity(null);
    public static final BuiltinModelItemRenderer INSTANCE = new BuiltinModelItemRenderer();
    private final ChestBlockEntity renderChestNormal = new ChestBlockEntity();
    private final ChestBlockEntity renderChestTrapped = new TrappedChestBlockEntity();
    private final EnderChestBlockEntity renderChestEnder = new EnderChestBlockEntity();
    private final BannerBlockEntity renderBanner = new BannerBlockEntity();
    private final BedBlockEntity renderBed = new BedBlockEntity();
    private final ConduitBlockEntity renderConduit = new ConduitBlockEntity();
    private final ShieldEntityModel modelShield = new ShieldEntityModel();
    private final TridentEntityModel modelTrident = new TridentEntityModel();

    public void render(ItemStack itemStack, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j) {
        Item item = itemStack.getItem();
        if (item instanceof BlockItem) {
            Block block = ((BlockItem)item).getBlock();
            if (block instanceof AbstractBannerBlock) {
                this.renderBanner.readFrom(itemStack, ((AbstractBannerBlock)block).getColor());
                BlockEntityRenderDispatcher.INSTANCE.renderEntity(this.renderBanner, matrixStack, vertexConsumerProvider, i, j);
            } else if (block instanceof BedBlock) {
                this.renderBed.setColor(((BedBlock)block).getColor());
                BlockEntityRenderDispatcher.INSTANCE.renderEntity(this.renderBed, matrixStack, vertexConsumerProvider, i, j);
            } else if (block instanceof AbstractSkullBlock) {
                GameProfile gameProfile = null;
                if (itemStack.hasTag()) {
                    CompoundTag compoundTag = itemStack.getTag();
                    if (compoundTag.contains("SkullOwner", 10)) {
                        gameProfile = NbtHelper.toGameProfile(compoundTag.getCompound("SkullOwner"));
                    } else if (compoundTag.contains("SkullOwner", 8) && !StringUtils.isBlank(compoundTag.getString("SkullOwner"))) {
                        gameProfile = new GameProfile(null, compoundTag.getString("SkullOwner"));
                        gameProfile = SkullBlockEntity.loadProperties(gameProfile);
                        compoundTag.remove("SkullOwner");
                        compoundTag.put("SkullOwner", NbtHelper.fromGameProfile(new CompoundTag(), gameProfile));
                    }
                }
                SkullBlockEntityRenderer.render(null, 180.0f, ((AbstractSkullBlock)block).getSkullType(), gameProfile, 0.0f, matrixStack, vertexConsumerProvider, i);
            } else if (block == Blocks.CONDUIT) {
                BlockEntityRenderDispatcher.INSTANCE.renderEntity(this.renderConduit, matrixStack, vertexConsumerProvider, i, j);
            } else if (block == Blocks.CHEST) {
                BlockEntityRenderDispatcher.INSTANCE.renderEntity(this.renderChestNormal, matrixStack, vertexConsumerProvider, i, j);
            } else if (block == Blocks.ENDER_CHEST) {
                BlockEntityRenderDispatcher.INSTANCE.renderEntity(this.renderChestEnder, matrixStack, vertexConsumerProvider, i, j);
            } else if (block == Blocks.TRAPPED_CHEST) {
                BlockEntityRenderDispatcher.INSTANCE.renderEntity(this.renderChestTrapped, matrixStack, vertexConsumerProvider, i, j);
            } else if (block instanceof ShulkerBoxBlock) {
                DyeColor dyeColor = ShulkerBoxBlock.getColor(item);
                if (dyeColor == null) {
                    BlockEntityRenderDispatcher.INSTANCE.renderEntity(RENDER_SHULKER_BOX, matrixStack, vertexConsumerProvider, i, j);
                } else {
                    BlockEntityRenderDispatcher.INSTANCE.renderEntity(RENDER_SHULKER_BOX_DYED[dyeColor.getId()], matrixStack, vertexConsumerProvider, i, j);
                }
            }
            return;
        }
        if (item == Items.SHIELD) {
            boolean bl = itemStack.getSubTag("BlockEntityTag") != null;
            SpriteAtlasTexture spriteAtlasTexture = MinecraftClient.getInstance().getSpriteAtlas();
            matrixStack.push();
            matrixStack.scale(1.0f, -1.0f, -1.0f);
            VertexConsumer vertexConsumer = ItemRenderer.getArmorVertexConsumer(vertexConsumerProvider, this.modelShield.getLayer(SpriteAtlasTexture.BLOCK_ATLAS_TEX), false, itemStack.hasEnchantmentGlint());
            Sprite sprite = spriteAtlasTexture.getSprite(bl ? ModelLoader.field_21557 : ModelLoader.field_21558);
            this.modelShield.method_23775().render(matrixStack, vertexConsumer, i, j, sprite, 1.0f, 1.0f, 1.0f);
            this.modelShield.method_23774().render(matrixStack, vertexConsumer, i, j, sprite, 1.0f, 1.0f, 1.0f);
            if (bl) {
                this.renderBanner.readFrom(itemStack, ShieldItem.getColor(itemStack));
                BannerBlockEntityRenderer.method_23802(this.renderBanner, matrixStack, vertexConsumerProvider, i, j, this.modelShield.method_23774(), false);
            }
            matrixStack.pop();
        } else if (item == Items.TRIDENT) {
            matrixStack.push();
            matrixStack.scale(1.0f, -1.0f, -1.0f);
            VertexConsumer vertexConsumer2 = ItemRenderer.getArmorVertexConsumer(vertexConsumerProvider, this.modelTrident.getLayer(TridentEntityModel.TEXTURE), false, itemStack.hasEnchantmentGlint());
            this.modelTrident.render(matrixStack, vertexConsumer2, i, j, 1.0f, 1.0f, 1.0f);
            matrixStack.pop();
        }
    }
}

