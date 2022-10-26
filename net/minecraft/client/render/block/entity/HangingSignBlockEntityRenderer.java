/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.block.entity;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AbstractSignBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.HangingSignBlock;
import net.minecraft.block.WallSignBlock;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.model.Model;
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
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.block.entity.SignBlockEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.state.property.Properties;
import net.minecraft.util.SignType;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.RotationPropertyHelper;
import net.minecraft.util.math.Vec3d;

@Environment(value=EnvType.CLIENT)
public class HangingSignBlockEntityRenderer
extends SignBlockEntityRenderer {
    private static final String PLANK = "plank";
    private static final String V_CHAINS = "vChains";
    public static final String NORMAL_CHAINS = "normalChains";
    public static final String CHAIN_L1 = "chainL1";
    public static final String CHAIN_L2 = "chainL2";
    public static final String CHAIN_R1 = "chainR1";
    public static final String CHAIN_R2 = "chainR2";
    public static final String BOARD = "board";
    private final Map<SignType, HangingSignModel> MODELS = SignType.stream().collect(ImmutableMap.toImmutableMap(signType -> signType, type -> new HangingSignModel(context.getLayerModelPart(EntityModelLayers.createHangingSign(type)))));

    public HangingSignBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    public void render(SignBlockEntity signBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j) {
        float g;
        BlockState blockState = signBlockEntity.getCachedState();
        matrixStack.push();
        SignType signType = AbstractSignBlock.getSignType(blockState.getBlock());
        HangingSignModel hangingSignModel = this.MODELS.get(signType);
        boolean bl = !(blockState.getBlock() instanceof HangingSignBlock);
        boolean bl2 = blockState.contains(Properties.ATTACHED) && blockState.get(Properties.ATTACHED) != false;
        matrixStack.translate(0.5, 0.9375, 0.5);
        if (bl2) {
            g = -RotationPropertyHelper.toDegrees(blockState.get(HangingSignBlock.ROTATION));
            matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(g));
        } else {
            matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(this.getRotationDegrees(blockState, bl)));
        }
        matrixStack.translate(0.0f, -0.3125f, 0.0f);
        hangingSignModel.updateVisibleParts(blockState);
        g = 1.0f;
        this.renderSign(matrixStack, vertexConsumerProvider, i, j, 1.0f, signType, hangingSignModel);
        this.renderText(signBlockEntity, matrixStack, vertexConsumerProvider, i, 1.0f);
    }

    private float getRotationDegrees(BlockState state, boolean wall) {
        return wall ? -state.get(WallSignBlock.FACING).asRotation() : -((float)(state.get(HangingSignBlock.ROTATION) * 360) / 16.0f);
    }

    @Override
    SpriteIdentifier getTextureId(SignType signType) {
        return TexturedRenderLayers.getHangingSignTextureId(signType);
    }

    @Override
    void renderSignModel(MatrixStack matrices, int light, int overlay, Model model, VertexConsumer vertices) {
        HangingSignModel hangingSignModel = (HangingSignModel)model;
        hangingSignModel.root.render(matrices, vertices, light, overlay);
    }

    @Override
    Vec3d getTextOffset(float scale) {
        return new Vec3d(0.0, -0.32f * scale, 0.063f * scale);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild(BOARD, ModelPartBuilder.create().uv(0, 12).cuboid(-7.0f, 0.0f, -1.0f, 14.0f, 10.0f, 2.0f), ModelTransform.NONE);
        modelPartData.addChild(PLANK, ModelPartBuilder.create().uv(0, 0).cuboid(-8.0f, -6.0f, -2.0f, 16.0f, 2.0f, 4.0f), ModelTransform.NONE);
        ModelPartData modelPartData2 = modelPartData.addChild(NORMAL_CHAINS, ModelPartBuilder.create(), ModelTransform.NONE);
        modelPartData2.addChild(CHAIN_L1, ModelPartBuilder.create().uv(0, 6).cuboid(-1.5f, 0.0f, 0.0f, 3.0f, 6.0f, 0.0f), ModelTransform.of(-5.0f, -6.0f, 0.0f, 0.0f, -0.7853982f, 0.0f));
        modelPartData2.addChild(CHAIN_L2, ModelPartBuilder.create().uv(6, 6).cuboid(-1.5f, 0.0f, 0.0f, 3.0f, 6.0f, 0.0f), ModelTransform.of(-5.0f, -6.0f, 0.0f, 0.0f, 0.7853982f, 0.0f));
        modelPartData2.addChild(CHAIN_R1, ModelPartBuilder.create().uv(0, 6).cuboid(-1.5f, 0.0f, 0.0f, 3.0f, 6.0f, 0.0f), ModelTransform.of(5.0f, -6.0f, 0.0f, 0.0f, -0.7853982f, 0.0f));
        modelPartData2.addChild(CHAIN_R2, ModelPartBuilder.create().uv(6, 6).cuboid(-1.5f, 0.0f, 0.0f, 3.0f, 6.0f, 0.0f), ModelTransform.of(5.0f, -6.0f, 0.0f, 0.0f, 0.7853982f, 0.0f));
        modelPartData.addChild(V_CHAINS, ModelPartBuilder.create().uv(14, 6).cuboid(-6.0f, -6.0f, 0.0f, 12.0f, 6.0f, 0.0f), ModelTransform.NONE);
        return TexturedModelData.of(modelData, 64, 32);
    }

    @Environment(value=EnvType.CLIENT)
    public static final class HangingSignModel
    extends Model {
        public final ModelPart root;
        public final ModelPart plank;
        public final ModelPart vChains;
        public final ModelPart normalChains;

        public HangingSignModel(ModelPart root) {
            super(RenderLayer::getEntityCutoutNoCull);
            this.root = root;
            this.plank = root.getChild(HangingSignBlockEntityRenderer.PLANK);
            this.normalChains = root.getChild(HangingSignBlockEntityRenderer.NORMAL_CHAINS);
            this.vChains = root.getChild(HangingSignBlockEntityRenderer.V_CHAINS);
        }

        public void updateVisibleParts(BlockState state) {
            boolean bl;
            this.plank.visible = bl = !(state.getBlock() instanceof HangingSignBlock);
            this.vChains.visible = false;
            this.normalChains.visible = true;
            if (!bl) {
                boolean bl2 = state.get(Properties.ATTACHED);
                this.normalChains.visible = !bl2;
                this.vChains.visible = bl2;
            }
        }

        @Override
        public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
            this.root.render(matrices, vertices, light, overlay, red, green, blue, alpha);
        }
    }
}

