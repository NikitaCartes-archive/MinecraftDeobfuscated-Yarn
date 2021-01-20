/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.AbstractDecorationEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.map.MapState;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;

@Environment(value=EnvType.CLIENT)
public class ItemFrameEntityRenderer<T extends ItemFrameEntity>
extends EntityRenderer<T> {
    private static final ModelIdentifier NORMAL_FRAME = new ModelIdentifier("item_frame", "map=false");
    private static final ModelIdentifier MAP_FRAME = new ModelIdentifier("item_frame", "map=true");
    private static final ModelIdentifier GLOW_FRAME = new ModelIdentifier("glow_item_frame", "map=false");
    private static final ModelIdentifier MAP_GLOW_FRAME = new ModelIdentifier("glow_item_frame", "map=true");
    private final MinecraftClient client = MinecraftClient.getInstance();
    private final ItemRenderer itemRenderer;

    public ItemFrameEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    protected int getBlockLight(T itemFrameEntity, BlockPos blockPos) {
        return ((ItemFrameEntity)itemFrameEntity).isGlowItemFrame() ? 5 : super.getBlockLight(itemFrameEntity, blockPos);
    }

    @Override
    public void render(T itemFrameEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        super.render(itemFrameEntity, f, g, matrixStack, vertexConsumerProvider, i);
        matrixStack.push();
        Direction direction = ((AbstractDecorationEntity)itemFrameEntity).getHorizontalFacing();
        Vec3d vec3d = this.getPositionOffset(itemFrameEntity, g);
        matrixStack.translate(-vec3d.getX(), -vec3d.getY(), -vec3d.getZ());
        double d = 0.46875;
        matrixStack.translate((double)direction.getOffsetX() * 0.46875, (double)direction.getOffsetY() * 0.46875, (double)direction.getOffsetZ() * 0.46875);
        matrixStack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(((ItemFrameEntity)itemFrameEntity).pitch));
        matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180.0f - ((ItemFrameEntity)itemFrameEntity).yaw));
        boolean bl = ((Entity)itemFrameEntity).isInvisible();
        ItemStack itemStack = ((ItemFrameEntity)itemFrameEntity).getHeldItemStack();
        if (!bl) {
            BlockRenderManager blockRenderManager = this.client.getBlockRenderManager();
            BakedModelManager bakedModelManager = blockRenderManager.getModels().getModelManager();
            ModelIdentifier modelIdentifier = this.getModelId(itemFrameEntity, itemStack);
            matrixStack.push();
            matrixStack.translate(-0.5, -0.5, -0.5);
            blockRenderManager.getModelRenderer().render(matrixStack.peek(), vertexConsumerProvider.getBuffer(TexturedRenderLayers.getEntitySolid()), null, bakedModelManager.getModel(modelIdentifier), 1.0f, 1.0f, 1.0f, i, OverlayTexture.DEFAULT_UV);
            matrixStack.pop();
        }
        if (!itemStack.isEmpty()) {
            boolean bl2 = itemStack.isOf(Items.FILLED_MAP);
            if (bl) {
                matrixStack.translate(0.0, 0.0, 0.5);
            } else {
                matrixStack.translate(0.0, 0.0, 0.4375);
            }
            int j = bl2 ? ((ItemFrameEntity)itemFrameEntity).getRotation() % 4 * 2 : ((ItemFrameEntity)itemFrameEntity).getRotation();
            matrixStack.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion((float)j * 360.0f / 8.0f));
            if (bl2) {
                matrixStack.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(180.0f));
                float h = 0.0078125f;
                matrixStack.scale(0.0078125f, 0.0078125f, 0.0078125f);
                matrixStack.translate(-64.0, -64.0, 0.0);
                Integer integer = FilledMapItem.getMapId(itemStack);
                MapState mapState = FilledMapItem.getMapState(integer, ((ItemFrameEntity)itemFrameEntity).world);
                matrixStack.translate(0.0, 0.0, -1.0);
                if (mapState != null) {
                    int k = this.method_33433(itemFrameEntity, 15728850, i);
                    this.client.gameRenderer.getMapRenderer().draw(matrixStack, vertexConsumerProvider, integer, mapState, true, k);
                }
            } else {
                int l = this.method_33433(itemFrameEntity, 0xF000F0, i);
                matrixStack.scale(0.5f, 0.5f, 0.5f);
                this.itemRenderer.renderItem(itemStack, ModelTransformation.Mode.FIXED, l, OverlayTexture.DEFAULT_UV, matrixStack, vertexConsumerProvider, ((Entity)itemFrameEntity).getId());
            }
        }
        matrixStack.pop();
    }

    private int method_33433(T itemFrameEntity, int i, int j) {
        return ((Entity)itemFrameEntity).getType() == EntityType.GLOW_ITEM_FRAME ? i : j;
    }

    private ModelIdentifier getModelId(T entity, ItemStack stack) {
        boolean bl = ((ItemFrameEntity)entity).isGlowItemFrame();
        if (stack.isOf(Items.FILLED_MAP)) {
            return bl ? MAP_GLOW_FRAME : MAP_FRAME;
        }
        return bl ? GLOW_FRAME : NORMAL_FRAME;
    }

    @Override
    public Vec3d getPositionOffset(T itemFrameEntity, float f) {
        return new Vec3d((float)((AbstractDecorationEntity)itemFrameEntity).getHorizontalFacing().getOffsetX() * 0.3f, -0.25, (float)((AbstractDecorationEntity)itemFrameEntity).getHorizontalFacing().getOffsetZ() * 0.3f);
    }

    @Override
    public Identifier getTexture(T itemFrameEntity) {
        return SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE;
    }

    @Override
    protected boolean hasLabel(T itemFrameEntity) {
        if (!MinecraftClient.isHudEnabled() || ((ItemFrameEntity)itemFrameEntity).getHeldItemStack().isEmpty() || !((ItemFrameEntity)itemFrameEntity).getHeldItemStack().hasCustomName() || this.dispatcher.targetedEntity != itemFrameEntity) {
            return false;
        }
        double d = this.dispatcher.getSquaredDistanceToCamera((Entity)itemFrameEntity);
        float f = ((Entity)itemFrameEntity).isSneaky() ? 32.0f : 64.0f;
        return d < (double)(f * f);
    }

    @Override
    protected void renderLabelIfPresent(T itemFrameEntity, Text text, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        super.renderLabelIfPresent(itemFrameEntity, ((ItemFrameEntity)itemFrameEntity).getHeldItemStack().getName(), matrixStack, vertexConsumerProvider, i);
    }
}

