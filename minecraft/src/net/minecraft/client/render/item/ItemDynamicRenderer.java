package net.minecraft.client.render.item;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Arrays;
import java.util.Comparator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
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
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.SkullBlockEntityRenderer;
import net.minecraft.client.render.entity.model.ShieldEntityModel;
import net.minecraft.client.render.entity.model.TridentEntityModel;
import net.minecraft.client.texture.TextureCache;
import net.minecraft.item.BannerItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ShieldItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.DyeColor;
import net.minecraft.util.TagHelper;
import org.apache.commons.lang3.StringUtils;

@Environment(EnvType.CLIENT)
public class ItemDynamicRenderer {
	private static final ShulkerBoxBlockEntity[] RENDER_SHULKER_BOX_DYED = (ShulkerBoxBlockEntity[])Arrays.stream(DyeColor.values())
		.sorted(Comparator.comparingInt(DyeColor::getId))
		.map(ShulkerBoxBlockEntity::new)
		.toArray(ShulkerBoxBlockEntity[]::new);
	private static final ShulkerBoxBlockEntity RENDER_SHULKER_BOX = new ShulkerBoxBlockEntity(null);
	public static final ItemDynamicRenderer INSTANCE = new ItemDynamicRenderer();
	private final ChestBlockEntity renderChestNormal = new ChestBlockEntity();
	private final ChestBlockEntity renderChestTrapped = new TrappedChestBlockEntity();
	private final EnderChestBlockEntity renderChestEnder = new EnderChestBlockEntity();
	private final BannerBlockEntity renderBanner = new BannerBlockEntity();
	private final BedBlockEntity renderBed = new BedBlockEntity();
	private final SkullBlockEntity renderSkull = new SkullBlockEntity();
	private final ConduitBlockEntity renderConduit = new ConduitBlockEntity();
	private final ShieldEntityModel modelShield = new ShieldEntityModel();
	private final TridentEntityModel modelTrident = new TridentEntityModel();

	public void render(ItemStack itemStack) {
		Item item = itemStack.getItem();
		if (item instanceof BannerItem) {
			this.renderBanner.deserialize(itemStack, ((BannerItem)item).getColor());
			BlockEntityRenderDispatcher.INSTANCE.renderEntity(this.renderBanner);
		} else if (item instanceof BlockItem && ((BlockItem)item).getBlock() instanceof BedBlock) {
			this.renderBed.setColor(((BedBlock)((BlockItem)item).getBlock()).getColor());
			BlockEntityRenderDispatcher.INSTANCE.renderEntity(this.renderBed);
		} else if (item == Items.field_8255) {
			if (itemStack.getSubCompoundTag("BlockEntityTag") != null) {
				this.renderBanner.deserialize(itemStack, ShieldItem.getColor(itemStack));
				MinecraftClient.getInstance()
					.getTextureManager()
					.bindTexture(TextureCache.SHIELD.get(this.renderBanner.getPatternCacheKey(), this.renderBanner.getPatterns(), this.renderBanner.getPatternColors()));
			} else {
				MinecraftClient.getInstance().getTextureManager().bindTexture(TextureCache.DEFAULT_SHIELD);
			}

			GlStateManager.pushMatrix();
			GlStateManager.scalef(1.0F, -1.0F, -1.0F);
			this.modelShield.renderItem();
			if (itemStack.hasEnchantmentGlint()) {
				this.renderEnchantmentGlint(this.modelShield::renderItem);
			}

			GlStateManager.popMatrix();
		} else if (item instanceof BlockItem && ((BlockItem)item).getBlock() instanceof AbstractSkullBlock) {
			GameProfile gameProfile = null;
			if (itemStack.hasTag()) {
				CompoundTag compoundTag = itemStack.getTag();
				if (compoundTag.containsKey("SkullOwner", 10)) {
					gameProfile = TagHelper.deserializeProfile(compoundTag.getCompound("SkullOwner"));
				} else if (compoundTag.containsKey("SkullOwner", 8) && !StringUtils.isBlank(compoundTag.getString("SkullOwner"))) {
					GameProfile var6 = new GameProfile(null, compoundTag.getString("SkullOwner"));
					gameProfile = SkullBlockEntity.loadProperties(var6);
					compoundTag.remove("SkullOwner");
					compoundTag.put("SkullOwner", TagHelper.serializeProfile(new CompoundTag(), gameProfile));
				}
			}

			if (SkullBlockEntityRenderer.INSTANCE != null) {
				GlStateManager.pushMatrix();
				GlStateManager.disableCull();
				SkullBlockEntityRenderer.INSTANCE
					.render(0.0F, 0.0F, 0.0F, null, 180.0F, ((AbstractSkullBlock)((BlockItem)item).getBlock()).getSkullType(), gameProfile, -1, 0.0F);
				GlStateManager.enableCull();
				GlStateManager.popMatrix();
			}
		} else if (item == Items.field_8547) {
			MinecraftClient.getInstance().getTextureManager().bindTexture(TridentEntityModel.TEXTURE);
			GlStateManager.pushMatrix();
			GlStateManager.scalef(1.0F, -1.0F, -1.0F);
			this.modelTrident.renderItem();
			if (itemStack.hasEnchantmentGlint()) {
				this.renderEnchantmentGlint(this.modelTrident::renderItem);
			}

			GlStateManager.popMatrix();
		} else if (item instanceof BlockItem && ((BlockItem)item).getBlock() == Blocks.field_10502) {
			BlockEntityRenderDispatcher.INSTANCE.renderEntity(this.renderConduit);
		} else if (item == Blocks.field_10443.asItem()) {
			BlockEntityRenderDispatcher.INSTANCE.renderEntity(this.renderChestEnder);
		} else if (item == Blocks.field_10380.asItem()) {
			BlockEntityRenderDispatcher.INSTANCE.renderEntity(this.renderChestTrapped);
		} else if (Block.getBlockFromItem(item) instanceof ShulkerBoxBlock) {
			DyeColor dyeColor = ShulkerBoxBlock.getColor(item);
			if (dyeColor == null) {
				BlockEntityRenderDispatcher.INSTANCE.renderEntity(RENDER_SHULKER_BOX);
			} else {
				BlockEntityRenderDispatcher.INSTANCE.renderEntity(RENDER_SHULKER_BOX_DYED[dyeColor.getId()]);
			}
		} else {
			BlockEntityRenderDispatcher.INSTANCE.renderEntity(this.renderChestNormal);
		}
	}

	private void renderEnchantmentGlint(Runnable runnable) {
		GlStateManager.color3f(0.5019608F, 0.2509804F, 0.8F);
		MinecraftClient.getInstance().getTextureManager().bindTexture(ItemRenderer.ENCHANTMENT_GLINT_TEX);
		ItemRenderer.renderGlint(MinecraftClient.getInstance().getTextureManager(), runnable, 1);
	}
}
