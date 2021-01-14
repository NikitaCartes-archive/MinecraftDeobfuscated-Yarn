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
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.LightmapCoordinatesRetriever;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.World;

@Environment(value=EnvType.CLIENT)
public class ChestBlockEntityRenderer<T extends BlockEntity>
extends BlockEntityRenderer<T> {
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

    public ChestBlockEntityRenderer(BlockEntityRenderDispatcher blockEntityRenderDispatcher) {
        super(blockEntityRenderDispatcher);
        Calendar calendar = Calendar.getInstance();
        if (calendar.get(2) + 1 == 12 && calendar.get(5) >= 24 && calendar.get(5) <= 26) {
            this.christmas = true;
        }
        this.singleChestBase = new ModelPart(64, 64, 0, 19);
        this.singleChestBase.addCuboid(1.0f, 0.0f, 1.0f, 14.0f, 10.0f, 14.0f, 0.0f);
        this.singleChestLid = new ModelPart(64, 64, 0, 0);
        this.singleChestLid.addCuboid(1.0f, 0.0f, 0.0f, 14.0f, 5.0f, 14.0f, 0.0f);
        this.singleChestLid.pivotY = 9.0f;
        this.singleChestLid.pivotZ = 1.0f;
        this.singleChestLatch = new ModelPart(64, 64, 0, 0);
        this.singleChestLatch.addCuboid(7.0f, -1.0f, 15.0f, 2.0f, 4.0f, 1.0f, 0.0f);
        this.singleChestLatch.pivotY = 8.0f;
        this.doubleChestRightBase = new ModelPart(64, 64, 0, 19);
        this.doubleChestRightBase.addCuboid(1.0f, 0.0f, 1.0f, 15.0f, 10.0f, 14.0f, 0.0f);
        this.doubleChestRightLid = new ModelPart(64, 64, 0, 0);
        this.doubleChestRightLid.addCuboid(1.0f, 0.0f, 0.0f, 15.0f, 5.0f, 14.0f, 0.0f);
        this.doubleChestRightLid.pivotY = 9.0f;
        this.doubleChestRightLid.pivotZ = 1.0f;
        this.doubleChestRightLatch = new ModelPart(64, 64, 0, 0);
        this.doubleChestRightLatch.addCuboid(15.0f, -1.0f, 15.0f, 1.0f, 4.0f, 1.0f, 0.0f);
        this.doubleChestRightLatch.pivotY = 8.0f;
        this.doubleChestLeftBase = new ModelPart(64, 64, 0, 19);
        this.doubleChestLeftBase.addCuboid(0.0f, 0.0f, 1.0f, 15.0f, 10.0f, 14.0f, 0.0f);
        this.doubleChestLeftLid = new ModelPart(64, 64, 0, 0);
        this.doubleChestLeftLid.addCuboid(0.0f, 0.0f, 0.0f, 15.0f, 5.0f, 14.0f, 0.0f);
        this.doubleChestLeftLid.pivotY = 9.0f;
        this.doubleChestLeftLid.pivotZ = 1.0f;
        this.doubleChestLeftLatch = new ModelPart(64, 64, 0, 0);
        this.doubleChestLeftLatch.addCuboid(0.0f, -1.0f, 15.0f, 1.0f, 4.0f, 1.0f, 0.0f);
        this.doubleChestLeftLatch.pivotY = 8.0f;
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
                this.render(matrices, vertexConsumer, this.doubleChestLeftLid, this.doubleChestLeftLatch, this.doubleChestLeftBase, g, i, overlay);
            } else {
                this.render(matrices, vertexConsumer, this.doubleChestRightLid, this.doubleChestRightLatch, this.doubleChestRightBase, g, i, overlay);
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

