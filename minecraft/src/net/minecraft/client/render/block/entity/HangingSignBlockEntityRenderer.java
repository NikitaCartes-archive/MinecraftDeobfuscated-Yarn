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
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.state.property.Properties;
import net.minecraft.util.SignType;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationPropertyHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;

@Environment(EnvType.CLIENT)
public class HangingSignBlockEntityRenderer extends SignBlockEntityRenderer {
	private static final String PLANK = "plank";
	private static final String V_CHAINS = "vChains";
	public static final String NORMAL_CHAINS = "normalChains";
	public static final String CHAIN_L1 = "chainL1";
	public static final String CHAIN_L2 = "chainL2";
	public static final String CHAIN_R1 = "chainR1";
	public static final String CHAIN_R2 = "chainR2";
	public static final String BOARD = "board";
	private final Map<SignType, HangingSignBlockEntityRenderer.HangingSignModel> MODELS;

	public HangingSignBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
		super(context);
		this.MODELS = (Map<SignType, HangingSignBlockEntityRenderer.HangingSignModel>)SignType.stream()
			.collect(
				ImmutableMap.toImmutableMap(
					signType -> signType, type -> new HangingSignBlockEntityRenderer.HangingSignModel(context.getLayerModelPart(EntityModelLayers.createHangingSign(type)))
				)
			);
	}

	@Override
	public void render(SignBlockEntity signBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j) {
		BlockState blockState = signBlockEntity.getCachedState();
		matrixStack.push();
		SignType signType = AbstractSignBlock.getSignType(blockState.getBlock());
		HangingSignBlockEntityRenderer.HangingSignModel hangingSignModel = (HangingSignBlockEntityRenderer.HangingSignModel)this.MODELS.get(signType);
		boolean bl = !(blockState.getBlock() instanceof HangingSignBlock);
		boolean bl2 = blockState.contains(Properties.ATTACHED) && (Boolean)blockState.get(Properties.ATTACHED);
		matrixStack.translate(0.5, 0.9375, 0.5);
		if (bl2) {
			float g = -RotationPropertyHelper.toDegrees((Integer)blockState.get(HangingSignBlock.ROTATION));
			matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(g));
		} else {
			matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(this.getRotationDegrees(blockState, bl)));
		}

		matrixStack.translate(0.0, -0.3125, 0.0);
		hangingSignModel.updateVisibleParts(blockState);
		float g = 1.0F;
		this.renderSign(matrixStack, vertexConsumerProvider, i, j, 1.0F, signType, hangingSignModel);
		this.renderText(signBlockEntity, matrixStack, vertexConsumerProvider, i, 1.0F);
	}

	private float getRotationDegrees(BlockState state, boolean wall) {
		return wall ? -((Direction)state.get(WallSignBlock.FACING)).asRotation() : -((float)((Integer)state.get(HangingSignBlock.ROTATION) * 360) / 16.0F);
	}

	@Override
	SpriteIdentifier getTextureId(SignType signType) {
		return TexturedRenderLayers.getHangingSignTextureId(signType);
	}

	@Override
	void renderSignModel(MatrixStack matrices, int light, int overlay, Model model, VertexConsumer vertices) {
		HangingSignBlockEntityRenderer.HangingSignModel hangingSignModel = (HangingSignBlockEntityRenderer.HangingSignModel)model;
		hangingSignModel.root.render(matrices, vertices, light, overlay);
	}

	@Override
	Vec3d getTextOffset(float scale) {
		return new Vec3d(0.0, (double)(-0.32F * scale), (double)(0.063F * scale));
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		modelPartData.addChild("board", ModelPartBuilder.create().uv(0, 12).cuboid(-7.0F, 0.0F, -1.0F, 14.0F, 10.0F, 2.0F), ModelTransform.NONE);
		modelPartData.addChild("plank", ModelPartBuilder.create().uv(0, 0).cuboid(-8.0F, -6.0F, -2.0F, 16.0F, 2.0F, 4.0F), ModelTransform.NONE);
		ModelPartData modelPartData2 = modelPartData.addChild("normalChains", ModelPartBuilder.create(), ModelTransform.NONE);
		modelPartData2.addChild(
			"chainL1",
			ModelPartBuilder.create().uv(0, 6).cuboid(-1.5F, 0.0F, 0.0F, 3.0F, 6.0F, 0.0F),
			ModelTransform.of(-5.0F, -6.0F, 0.0F, 0.0F, (float) (-Math.PI / 4), 0.0F)
		);
		modelPartData2.addChild(
			"chainL2",
			ModelPartBuilder.create().uv(6, 6).cuboid(-1.5F, 0.0F, 0.0F, 3.0F, 6.0F, 0.0F),
			ModelTransform.of(-5.0F, -6.0F, 0.0F, 0.0F, (float) (Math.PI / 4), 0.0F)
		);
		modelPartData2.addChild(
			"chainR1",
			ModelPartBuilder.create().uv(0, 6).cuboid(-1.5F, 0.0F, 0.0F, 3.0F, 6.0F, 0.0F),
			ModelTransform.of(5.0F, -6.0F, 0.0F, 0.0F, (float) (-Math.PI / 4), 0.0F)
		);
		modelPartData2.addChild(
			"chainR2",
			ModelPartBuilder.create().uv(6, 6).cuboid(-1.5F, 0.0F, 0.0F, 3.0F, 6.0F, 0.0F),
			ModelTransform.of(5.0F, -6.0F, 0.0F, 0.0F, (float) (Math.PI / 4), 0.0F)
		);
		modelPartData.addChild("vChains", ModelPartBuilder.create().uv(14, 6).cuboid(-6.0F, -6.0F, 0.0F, 12.0F, 6.0F, 0.0F), ModelTransform.NONE);
		return TexturedModelData.of(modelData, 64, 32);
	}

	@Environment(EnvType.CLIENT)
	public static final class HangingSignModel extends Model {
		public final ModelPart root;
		public final ModelPart plank;
		public final ModelPart vChains;
		public final ModelPart normalChains;

		public HangingSignModel(ModelPart root) {
			super(RenderLayer::getEntityCutoutNoCull);
			this.root = root;
			this.plank = root.getChild("plank");
			this.normalChains = root.getChild("normalChains");
			this.vChains = root.getChild("vChains");
		}

		public void updateVisibleParts(BlockState state) {
			boolean bl = !(state.getBlock() instanceof HangingSignBlock);
			this.plank.visible = bl;
			this.vChains.visible = false;
			this.normalChains.visible = true;
			if (!bl) {
				boolean bl2 = (Boolean)state.get(Properties.ATTACHED);
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
