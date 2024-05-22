package net.minecraft.client.render.item;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AbstractBannerBlock;
import net.minecraft.block.AbstractSkullBlock;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.SkullBlock;
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.block.entity.BedBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.ConduitBlockEntity;
import net.minecraft.block.entity.DecoratedPotBlockEntity;
import net.minecraft.block.entity.EnderChestBlockEntity;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.block.entity.TrappedChestBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BannerBlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.SkullBlockEntityModel;
import net.minecraft.client.render.block.entity.SkullBlockEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.render.entity.model.ShieldEntityModel;
import net.minecraft.client.render.entity.model.TridentEntityModel;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BannerPatternsComponent;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SynchronousResourceReloader;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;

@Environment(EnvType.CLIENT)
public class BuiltinModelItemRenderer implements SynchronousResourceReloader {
	private static final ShulkerBoxBlockEntity[] RENDER_SHULKER_BOX_DYED = (ShulkerBoxBlockEntity[])Arrays.stream(DyeColor.values())
		.sorted(Comparator.comparingInt(DyeColor::getId))
		.map(color -> new ShulkerBoxBlockEntity(color, BlockPos.ORIGIN, Blocks.SHULKER_BOX.getDefaultState()))
		.toArray(ShulkerBoxBlockEntity[]::new);
	private static final ShulkerBoxBlockEntity RENDER_SHULKER_BOX = new ShulkerBoxBlockEntity(BlockPos.ORIGIN, Blocks.SHULKER_BOX.getDefaultState());
	private final ChestBlockEntity renderChestNormal = new ChestBlockEntity(BlockPos.ORIGIN, Blocks.CHEST.getDefaultState());
	private final ChestBlockEntity renderChestTrapped = new TrappedChestBlockEntity(BlockPos.ORIGIN, Blocks.TRAPPED_CHEST.getDefaultState());
	private final EnderChestBlockEntity renderChestEnder = new EnderChestBlockEntity(BlockPos.ORIGIN, Blocks.ENDER_CHEST.getDefaultState());
	private final BannerBlockEntity renderBanner = new BannerBlockEntity(BlockPos.ORIGIN, Blocks.WHITE_BANNER.getDefaultState());
	private final BedBlockEntity renderBed = new BedBlockEntity(BlockPos.ORIGIN, Blocks.RED_BED.getDefaultState());
	private final ConduitBlockEntity renderConduit = new ConduitBlockEntity(BlockPos.ORIGIN, Blocks.CONDUIT.getDefaultState());
	private final DecoratedPotBlockEntity renderDecoratedPot = new DecoratedPotBlockEntity(BlockPos.ORIGIN, Blocks.DECORATED_POT.getDefaultState());
	private ShieldEntityModel modelShield;
	private TridentEntityModel modelTrident;
	private Map<SkullBlock.SkullType, SkullBlockEntityModel> skullModels;
	private final BlockEntityRenderDispatcher blockEntityRenderDispatcher;
	private final EntityModelLoader entityModelLoader;

	public BuiltinModelItemRenderer(BlockEntityRenderDispatcher blockEntityRenderDispatcher, EntityModelLoader entityModelLoader) {
		this.blockEntityRenderDispatcher = blockEntityRenderDispatcher;
		this.entityModelLoader = entityModelLoader;
	}

	@Override
	public void reload(ResourceManager manager) {
		this.modelShield = new ShieldEntityModel(this.entityModelLoader.getModelPart(EntityModelLayers.SHIELD));
		this.modelTrident = new TridentEntityModel(this.entityModelLoader.getModelPart(EntityModelLayers.TRIDENT));
		this.skullModels = SkullBlockEntityRenderer.getModels(this.entityModelLoader);
	}

	public void render(ItemStack stack, ModelTransformationMode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
		Item item = stack.getItem();
		if (item instanceof BlockItem) {
			Block block = ((BlockItem)item).getBlock();
			if (block instanceof AbstractSkullBlock abstractSkullBlock) {
				ProfileComponent profileComponent = stack.get(DataComponentTypes.PROFILE);
				if (profileComponent != null && !profileComponent.isCompleted()) {
					stack.remove(DataComponentTypes.PROFILE);
					profileComponent.getFuture().thenAcceptAsync(profile -> stack.set(DataComponentTypes.PROFILE, profile), MinecraftClient.getInstance());
					profileComponent = null;
				}

				SkullBlockEntityModel skullBlockEntityModel = (SkullBlockEntityModel)this.skullModels.get(abstractSkullBlock.getSkullType());
				RenderLayer renderLayer = SkullBlockEntityRenderer.getRenderLayer(abstractSkullBlock.getSkullType(), profileComponent);
				SkullBlockEntityRenderer.renderSkull(null, 180.0F, 0.0F, matrices, vertexConsumers, light, skullBlockEntityModel, renderLayer);
			} else {
				BlockState blockState = block.getDefaultState();
				BlockEntity blockEntity;
				if (block instanceof AbstractBannerBlock) {
					this.renderBanner.readFrom(stack, ((AbstractBannerBlock)block).getColor());
					blockEntity = this.renderBanner;
				} else if (block instanceof BedBlock) {
					this.renderBed.setColor(((BedBlock)block).getColor());
					blockEntity = this.renderBed;
				} else if (blockState.isOf(Blocks.CONDUIT)) {
					blockEntity = this.renderConduit;
				} else if (blockState.isOf(Blocks.CHEST)) {
					blockEntity = this.renderChestNormal;
				} else if (blockState.isOf(Blocks.ENDER_CHEST)) {
					blockEntity = this.renderChestEnder;
				} else if (blockState.isOf(Blocks.TRAPPED_CHEST)) {
					blockEntity = this.renderChestTrapped;
				} else if (blockState.isOf(Blocks.DECORATED_POT)) {
					this.renderDecoratedPot.readFrom(stack);
					blockEntity = this.renderDecoratedPot;
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

				this.blockEntityRenderDispatcher.renderEntity(blockEntity, matrices, vertexConsumers, light, overlay);
			}
		} else {
			if (stack.isOf(Items.SHIELD)) {
				BannerPatternsComponent bannerPatternsComponent = stack.getOrDefault(DataComponentTypes.BANNER_PATTERNS, BannerPatternsComponent.DEFAULT);
				DyeColor dyeColor2 = stack.get(DataComponentTypes.BASE_COLOR);
				boolean bl = !bannerPatternsComponent.layers().isEmpty() || dyeColor2 != null;
				matrices.push();
				matrices.scale(1.0F, -1.0F, -1.0F);
				SpriteIdentifier spriteIdentifier = bl ? ModelLoader.SHIELD_BASE : ModelLoader.SHIELD_BASE_NO_PATTERN;
				VertexConsumer vertexConsumer = spriteIdentifier.getSprite()
					.getTextureSpecificVertexConsumer(
						ItemRenderer.getDirectItemGlintConsumer(vertexConsumers, this.modelShield.getLayer(spriteIdentifier.getAtlasId()), true, stack.hasGlint())
					);
				this.modelShield.getHandle().render(matrices, vertexConsumer, light, overlay);
				if (bl) {
					BannerBlockEntityRenderer.renderCanvas(
						matrices,
						vertexConsumers,
						light,
						overlay,
						this.modelShield.getPlate(),
						spriteIdentifier,
						false,
						(DyeColor)Objects.requireNonNullElse(dyeColor2, DyeColor.WHITE),
						bannerPatternsComponent,
						stack.hasGlint()
					);
				} else {
					this.modelShield.getPlate().render(matrices, vertexConsumer, light, overlay);
				}

				matrices.pop();
			} else if (stack.isOf(Items.TRIDENT)) {
				matrices.push();
				matrices.scale(1.0F, -1.0F, -1.0F);
				VertexConsumer vertexConsumer2 = ItemRenderer.getDirectItemGlintConsumer(
					vertexConsumers, this.modelTrident.getLayer(TridentEntityModel.TEXTURE), false, stack.hasGlint()
				);
				this.modelTrident.render(matrices, vertexConsumer2, light, overlay);
				matrices.pop();
			}
		}
	}
}
