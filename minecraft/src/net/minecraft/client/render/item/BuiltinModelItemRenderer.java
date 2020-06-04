package net.minecraft.client.render.item;

import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.util.Pair;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AbstractBannerBlock;
import net.minecraft.block.AbstractSkullBlock;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.block.entity.BedBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.ConduitBlockEntity;
import net.minecraft.block.entity.EnderChestBlockEntity;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.block.entity.TrappedChestBlockEntity;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BannerBlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.SkullBlockEntityRenderer;
import net.minecraft.client.render.entity.model.ShieldEntityModel;
import net.minecraft.client.render.entity.model.TridentEntityModel;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.SpriteIdentifier;
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

@Environment(EnvType.CLIENT)
public class BuiltinModelItemRenderer {
	private static final ShulkerBoxBlockEntity[] RENDER_SHULKER_BOX_DYED = (ShulkerBoxBlockEntity[])Arrays.stream(DyeColor.values())
		.sorted(Comparator.comparingInt(DyeColor::getId))
		.map(ShulkerBoxBlockEntity::new)
		.toArray(ShulkerBoxBlockEntity[]::new);
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

	public void render(ItemStack stack, ModelTransformation.Mode mode, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j) {
		Item item = stack.getItem();
		if (item instanceof BlockItem) {
			Block block = ((BlockItem)item).getBlock();
			if (block instanceof AbstractSkullBlock) {
				GameProfile gameProfile = null;
				if (stack.hasTag()) {
					CompoundTag compoundTag = stack.getTag();
					if (compoundTag.contains("SkullOwner", 10)) {
						gameProfile = NbtHelper.toGameProfile(compoundTag.getCompound("SkullOwner"));
					} else if (compoundTag.contains("SkullOwner", 8) && !StringUtils.isBlank(compoundTag.getString("SkullOwner"))) {
						GameProfile var16 = new GameProfile(null, compoundTag.getString("SkullOwner"));
						gameProfile = SkullBlockEntity.loadProperties(var16);
						compoundTag.remove("SkullOwner");
						compoundTag.put("SkullOwner", NbtHelper.fromGameProfile(new CompoundTag(), gameProfile));
					}
				}

				SkullBlockEntityRenderer.render(null, 180.0F, ((AbstractSkullBlock)block).getSkullType(), gameProfile, 0.0F, matrixStack, vertexConsumerProvider, i);
			} else {
				BlockEntity blockEntity;
				if (block instanceof AbstractBannerBlock) {
					this.renderBanner.readFrom(stack, ((AbstractBannerBlock)block).getColor());
					blockEntity = this.renderBanner;
				} else if (block instanceof BedBlock) {
					this.renderBed.setColor(((BedBlock)block).getColor());
					blockEntity = this.renderBed;
				} else if (block == Blocks.CONDUIT) {
					blockEntity = this.renderConduit;
				} else if (block == Blocks.CHEST) {
					blockEntity = this.renderChestNormal;
				} else if (block == Blocks.ENDER_CHEST) {
					blockEntity = this.renderChestEnder;
				} else if (block == Blocks.TRAPPED_CHEST) {
					blockEntity = this.renderChestTrapped;
				} else {
					if (!(block instanceof ShulkerBoxBlock)) {
						return;
					}

					DyeColor dyeColor = ShulkerBoxBlock.getColor(item);
					if (dyeColor == null) {
						blockEntity = RENDER_SHULKER_BOX;
					} else {
						blockEntity = RENDER_SHULKER_BOX_DYED[dyeColor.getId()];
					}
				}

				BlockEntityRenderDispatcher.INSTANCE.renderEntity(blockEntity, matrixStack, vertexConsumerProvider, i, j);
			}
		} else {
			if (item == Items.SHIELD) {
				boolean bl = stack.getSubTag("BlockEntityTag") != null;
				matrixStack.push();
				matrixStack.scale(1.0F, -1.0F, -1.0F);
				SpriteIdentifier spriteIdentifier = bl ? ModelLoader.SHIELD_BASE : ModelLoader.SHIELD_BASE_NO_PATTERN;
				VertexConsumer vertexConsumer = spriteIdentifier.getSprite()
					.getTextureSpecificVertexConsumer(
						ItemRenderer.method_29711(vertexConsumerProvider, this.modelShield.getLayer(spriteIdentifier.getAtlasId()), false, stack.hasEnchantmentGlint())
					);
				this.modelShield.method_23775().render(matrixStack, vertexConsumer, i, j, 1.0F, 1.0F, 1.0F, 1.0F);
				if (bl) {
					List<Pair<BannerPattern, DyeColor>> list = BannerBlockEntity.method_24280(ShieldItem.getColor(stack), BannerBlockEntity.getPatternListTag(stack));
					BannerBlockEntityRenderer.renderCanvas(matrixStack, vertexConsumerProvider, i, j, this.modelShield.method_23774(), spriteIdentifier, false, list);
				} else {
					this.modelShield.method_23774().render(matrixStack, vertexConsumer, i, j, 1.0F, 1.0F, 1.0F, 1.0F);
				}

				matrixStack.pop();
			} else if (item == Items.TRIDENT) {
				matrixStack.push();
				matrixStack.scale(1.0F, -1.0F, -1.0F);
				VertexConsumer vertexConsumer2;
				if (mode != ModelTransformation.Mode.GUI
					&& mode != ModelTransformation.Mode.FIRST_PERSON_LEFT_HAND
					&& mode != ModelTransformation.Mode.FIRST_PERSON_RIGHT_HAND
					&& mode != ModelTransformation.Mode.FIXED) {
					vertexConsumer2 = ItemRenderer.getArmorVertexConsumer(
						vertexConsumerProvider, this.modelTrident.getLayer(TridentEntityModel.TEXTURE), false, stack.hasEnchantmentGlint()
					);
				} else {
					vertexConsumer2 = ItemRenderer.method_29711(
						vertexConsumerProvider, this.modelTrident.getLayer(TridentEntityModel.TEXTURE), false, stack.hasEnchantmentGlint()
					);
				}

				this.modelTrident.render(matrixStack, vertexConsumer2, i, j, 1.0F, 1.0F, 1.0F, 1.0F);
				matrixStack.pop();
			}
		}
	}
}
