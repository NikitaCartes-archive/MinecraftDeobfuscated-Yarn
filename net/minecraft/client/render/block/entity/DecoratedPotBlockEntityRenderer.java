/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.block.entity;

import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.DecoratedPotPatterns;
import net.minecraft.block.entity.DecoratedPotBlockEntity;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class DecoratedPotBlockEntityRenderer
implements BlockEntityRenderer<DecoratedPotBlockEntity> {
    private static final String NECK = "neck";
    private static final String FRONT = "front";
    private static final String BACK = "back";
    private static final String LEFT = "left";
    private static final String RIGHT = "right";
    private static final String TOP = "top";
    private static final String BOTTOM = "bottom";
    private final ModelPart neck;
    private final ModelPart front;
    private final ModelPart back;
    private final ModelPart left;
    private final ModelPart right;
    private final ModelPart top;
    private final ModelPart bottom;
    private final SpriteIdentifier baseTexture = Objects.requireNonNull(TexturedRenderLayers.getDecoratedPotPatternTextureId(DecoratedPotPatterns.DECORATED_POT_BASE_KEY));

    public DecoratedPotBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        ModelPart modelPart = context.getLayerModelPart(EntityModelLayers.DECORATED_POT_BASE);
        this.neck = modelPart.getChild(EntityModelPartNames.NECK);
        this.top = modelPart.getChild(TOP);
        this.bottom = modelPart.getChild(BOTTOM);
        ModelPart modelPart2 = context.getLayerModelPart(EntityModelLayers.DECORATED_POT_SIDES);
        this.front = modelPart2.getChild(FRONT);
        this.back = modelPart2.getChild(BACK);
        this.left = modelPart2.getChild(LEFT);
        this.right = modelPart2.getChild(RIGHT);
    }

    public static TexturedModelData getTopBottomNeckTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        Dilation dilation = new Dilation(0.2f);
        Dilation dilation2 = new Dilation(-0.1f);
        modelPartData.addChild(EntityModelPartNames.NECK, ModelPartBuilder.create().uv(0, 0).cuboid(4.0f, 17.0f, 4.0f, 8.0f, 3.0f, 8.0f, dilation2).uv(0, 5).cuboid(5.0f, 20.0f, 5.0f, 6.0f, 1.0f, 6.0f, dilation), ModelTransform.of(0.0f, 37.0f, 16.0f, (float)Math.PI, 0.0f, 0.0f));
        ModelPartBuilder modelPartBuilder = ModelPartBuilder.create().uv(-14, 13).cuboid(0.0f, 0.0f, 0.0f, 14.0f, 0.0f, 14.0f);
        modelPartData.addChild(TOP, modelPartBuilder, ModelTransform.of(1.0f, 16.0f, 1.0f, 0.0f, 0.0f, 0.0f));
        modelPartData.addChild(BOTTOM, modelPartBuilder, ModelTransform.of(1.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f));
        return TexturedModelData.of(modelData, 32, 32);
    }

    public static TexturedModelData getSidesTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartBuilder modelPartBuilder = ModelPartBuilder.create().uv(1, 0).cuboid(0.0f, 0.0f, 0.0f, 14.0f, 16.0f, 0.0f, EnumSet.of(Direction.NORTH));
        modelPartData.addChild(BACK, modelPartBuilder, ModelTransform.of(15.0f, 16.0f, 1.0f, 0.0f, 0.0f, (float)Math.PI));
        modelPartData.addChild(LEFT, modelPartBuilder, ModelTransform.of(1.0f, 16.0f, 1.0f, 0.0f, -1.5707964f, (float)Math.PI));
        modelPartData.addChild(RIGHT, modelPartBuilder, ModelTransform.of(15.0f, 16.0f, 15.0f, 0.0f, 1.5707964f, (float)Math.PI));
        modelPartData.addChild(FRONT, modelPartBuilder, ModelTransform.of(1.0f, 16.0f, 15.0f, (float)Math.PI, 0.0f, 0.0f));
        return TexturedModelData.of(modelData, 16, 16);
    }

    @Nullable
    private static SpriteIdentifier getTextureIdFromShard(Item item) {
        SpriteIdentifier spriteIdentifier = TexturedRenderLayers.getDecoratedPotPatternTextureId(DecoratedPotPatterns.fromShard(item));
        if (spriteIdentifier == null) {
            spriteIdentifier = TexturedRenderLayers.getDecoratedPotPatternTextureId(DecoratedPotPatterns.fromShard(Items.BRICK));
        }
        return spriteIdentifier;
    }

    @Override
    public void render(DecoratedPotBlockEntity decoratedPotBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j) {
        matrixStack.push();
        Direction direction = decoratedPotBlockEntity.getHorizontalFacing();
        matrixStack.translate(0.5, 0.0, 0.5);
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0f - direction.asRotation()));
        matrixStack.translate(-0.5, 0.0, -0.5);
        VertexConsumer vertexConsumer = this.baseTexture.getVertexConsumer(vertexConsumerProvider, RenderLayer::getEntitySolid);
        this.neck.render(matrixStack, vertexConsumer, i, j);
        this.top.render(matrixStack, vertexConsumer, i, j);
        this.bottom.render(matrixStack, vertexConsumer, i, j);
        List<Item> list = decoratedPotBlockEntity.getShards();
        this.renderDecoratedSide(this.front, matrixStack, vertexConsumerProvider, i, j, DecoratedPotBlockEntityRenderer.getTextureIdFromShard(list.get(3)));
        this.renderDecoratedSide(this.back, matrixStack, vertexConsumerProvider, i, j, DecoratedPotBlockEntityRenderer.getTextureIdFromShard(list.get(0)));
        this.renderDecoratedSide(this.left, matrixStack, vertexConsumerProvider, i, j, DecoratedPotBlockEntityRenderer.getTextureIdFromShard(list.get(1)));
        this.renderDecoratedSide(this.right, matrixStack, vertexConsumerProvider, i, j, DecoratedPotBlockEntityRenderer.getTextureIdFromShard(list.get(2)));
        matrixStack.pop();
    }

    private void renderDecoratedSide(ModelPart part, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, @Nullable SpriteIdentifier textureId) {
        if (textureId == null) {
            textureId = DecoratedPotBlockEntityRenderer.getTextureIdFromShard(Items.BRICK);
        }
        if (textureId != null) {
            part.render(matrices, textureId.getVertexConsumer(vertexConsumers, RenderLayer::getEntitySolid), light, overlay);
        }
    }
}

