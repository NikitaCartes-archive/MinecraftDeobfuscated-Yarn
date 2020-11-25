/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.block.entity;

import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import java.util.Calendar;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AbstractChestBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.DoubleBlockProperties;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.ChestType;
import net.minecraft.client.block.ChestAnimationProgress;
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
import net.minecraft.client.render.block.entity.LightmapCoordinatesRetriever;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.World;

@Environment(value=EnvType.CLIENT)
public class ChestBlockEntityRenderer<T extends BlockEntity>
implements BlockEntityRenderer<T> {
    private final ModelPart singleChestLid;
    private final ModelPart singleChestBase;
    private final ModelPart singleChestLatch;
    private final ModelPart doubleChestRightLid;
    private final ModelPart doubleChestRightBase;
    private final ModelPart doubleChestRightLatch;
    private final ModelPart doubleChestLeftLid;
    private final ModelPart doubleChestLeftBase;
    private final ModelPart doubleChestLeftLatch;
    private boolean christmas;

    public ChestBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        Calendar calendar = Calendar.getInstance();
        if (calendar.get(2) + 1 == 12 && calendar.get(5) >= 24 && calendar.get(5) <= 26) {
            this.christmas = true;
        }
        ModelPart modelPart = context.getLayerModelPart(EntityModelLayers.CHEST);
        this.singleChestBase = modelPart.getChild("bottom");
        this.singleChestLid = modelPart.getChild("lid");
        this.singleChestLatch = modelPart.getChild("lock");
        ModelPart modelPart2 = context.getLayerModelPart(EntityModelLayers.DOUBLE_CHEST_LEFT);
        this.doubleChestRightBase = modelPart2.getChild("bottom");
        this.doubleChestRightLid = modelPart2.getChild("lid");
        this.doubleChestRightLatch = modelPart2.getChild("lock");
        ModelPart modelPart3 = context.getLayerModelPart(EntityModelLayers.DOUBLE_CHEST_RIGHT);
        this.doubleChestLeftBase = modelPart3.getChild("bottom");
        this.doubleChestLeftLid = modelPart3.getChild("lid");
        this.doubleChestLeftLatch = modelPart3.getChild("lock");
    }

    public static TexturedModelData getSingleTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild("bottom", ModelPartBuilder.create().uv(0, 19).cuboid(1.0f, 0.0f, 1.0f, 14.0f, 10.0f, 14.0f), ModelTransform.NONE);
        modelPartData.addChild("lid", ModelPartBuilder.create().uv(0, 0).cuboid(1.0f, 0.0f, 0.0f, 14.0f, 5.0f, 14.0f), ModelTransform.pivot(0.0f, 9.0f, 1.0f));
        modelPartData.addChild("lock", ModelPartBuilder.create().uv(0, 0).cuboid(7.0f, -1.0f, 15.0f, 2.0f, 4.0f, 1.0f), ModelTransform.pivot(0.0f, 8.0f, 0.0f));
        return TexturedModelData.of(modelData, 64, 64);
    }

    public static TexturedModelData getRightDoubleTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild("bottom", ModelPartBuilder.create().uv(0, 19).cuboid(1.0f, 0.0f, 1.0f, 15.0f, 10.0f, 14.0f), ModelTransform.NONE);
        modelPartData.addChild("lid", ModelPartBuilder.create().uv(0, 0).cuboid(1.0f, 0.0f, 0.0f, 15.0f, 5.0f, 14.0f), ModelTransform.pivot(0.0f, 9.0f, 1.0f));
        modelPartData.addChild("lock", ModelPartBuilder.create().uv(0, 0).cuboid(15.0f, -1.0f, 15.0f, 1.0f, 4.0f, 1.0f), ModelTransform.pivot(0.0f, 8.0f, 0.0f));
        return TexturedModelData.of(modelData, 64, 64);
    }

    public static TexturedModelData getLeftDoubleTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild("bottom", ModelPartBuilder.create().uv(0, 19).cuboid(0.0f, 0.0f, 1.0f, 15.0f, 10.0f, 14.0f), ModelTransform.NONE);
        modelPartData.addChild("lid", ModelPartBuilder.create().uv(0, 0).cuboid(0.0f, 0.0f, 0.0f, 15.0f, 5.0f, 14.0f), ModelTransform.pivot(0.0f, 9.0f, 1.0f));
        modelPartData.addChild("lock", ModelPartBuilder.create().uv(0, 0).cuboid(0.0f, -1.0f, 15.0f, 1.0f, 4.0f, 1.0f), ModelTransform.pivot(0.0f, 8.0f, 0.0f));
        return TexturedModelData.of(modelData, 64, 64);
    }

    @Override
    public void render(T entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        World world = ((BlockEntity)entity).getWorld();
        boolean bl = world != null;
        BlockState blockState = bl ? ((BlockEntity)entity).getCachedState() : (BlockState)Blocks.CHEST.getDefaultState().with(ChestBlock.FACING, Direction.SOUTH);
        ChestType chestType = blockState.contains(ChestBlock.CHEST_TYPE) ? blockState.get(ChestBlock.CHEST_TYPE) : ChestType.SINGLE;
        Block block = blockState.getBlock();
        if (!(block instanceof AbstractChestBlock)) {
            return;
        }
        AbstractChestBlock abstractChestBlock = (AbstractChestBlock)block;
        boolean bl2 = chestType != ChestType.SINGLE;
        matrices.push();
        float f = blockState.get(ChestBlock.FACING).asRotation();
        matrices.translate(0.5, 0.5, 0.5);
        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-f));
        matrices.translate(-0.5, -0.5, -0.5);
        DoubleBlockProperties.PropertySource<Object> propertySource = bl ? abstractChestBlock.getBlockEntitySource(blockState, world, ((BlockEntity)entity).getPos(), true) : DoubleBlockProperties.PropertyRetriever::getFallback;
        float g = propertySource.apply(ChestBlock.getAnimationProgressRetriever((ChestAnimationProgress)entity)).get(tickDelta);
        g = 1.0f - g;
        g = 1.0f - g * g * g;
        int i = ((Int2IntFunction)propertySource.apply(new LightmapCoordinatesRetriever())).applyAsInt(light);
        SpriteIdentifier spriteIdentifier = TexturedRenderLayers.getChestTexture(entity, chestType, this.christmas);
        VertexConsumer vertexConsumer = spriteIdentifier.getVertexConsumer(vertexConsumers, RenderLayer::getEntityCutout);
        if (bl2) {
            if (chestType == ChestType.LEFT) {
                this.render(matrices, vertexConsumer, this.doubleChestRightLid, this.doubleChestRightLatch, this.doubleChestRightBase, g, i, overlay);
            } else {
                this.render(matrices, vertexConsumer, this.doubleChestLeftLid, this.doubleChestLeftLatch, this.doubleChestLeftBase, g, i, overlay);
            }
        } else {
            this.render(matrices, vertexConsumer, this.singleChestLid, this.singleChestLatch, this.singleChestBase, g, i, overlay);
        }
        matrices.pop();
    }

    private void render(MatrixStack matrices, VertexConsumer vertices, ModelPart lid, ModelPart latch, ModelPart base, float openFactor, int light, int overlay) {
        latch.pitch = lid.pitch = -(openFactor * 1.5707964f);
        lid.render(matrices, vertices, light, overlay);
        latch.render(matrices, vertices, light, overlay);
        base.render(matrices, vertices, light, overlay);
    }
}

