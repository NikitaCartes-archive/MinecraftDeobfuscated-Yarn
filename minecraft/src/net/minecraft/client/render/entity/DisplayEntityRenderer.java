package net.minecraft.client.render.entity;

import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.state.BlockDisplayEntityRenderState;
import net.minecraft.client.render.entity.state.DisplayEntityRenderState;
import net.minecraft.client.render.entity.state.ItemDisplayEntityRenderState;
import net.minecraft.client.render.entity.state.TextDisplayEntityRenderState;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.AffineTransformation;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

@Environment(EnvType.CLIENT)
public abstract class DisplayEntityRenderer<T extends DisplayEntity, S, ST extends DisplayEntityRenderState> extends EntityRenderer<T, ST> {
	private final EntityRenderDispatcher renderDispatcher;

	protected DisplayEntityRenderer(EntityRendererFactory.Context context) {
		super(context);
		this.renderDispatcher = context.getRenderDispatcher();
	}

	protected Box getBoundingBox(T displayEntity) {
		return displayEntity.getVisibilityBoundingBox();
	}

	protected boolean canBeCulled(T displayEntity) {
		return displayEntity.shouldRender();
	}

	public Identifier getTexture(DisplayEntityRenderState displayEntityRenderState) {
		return SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE;
	}

	public void render(ST displayEntityRenderState, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
		DisplayEntity.RenderState renderState = displayEntityRenderState.displayRenderState;
		if (renderState != null) {
			if (displayEntityRenderState.canRender()) {
				float f = displayEntityRenderState.lerpProgress;
				this.shadowRadius = renderState.shadowRadius().lerp(f);
				this.shadowOpacity = renderState.shadowStrength().lerp(f);
				int j = renderState.brightnessOverride();
				int k = j != -1 ? j : i;
				super.render(displayEntityRenderState, matrixStack, vertexConsumerProvider, k);
				matrixStack.push();
				matrixStack.multiply(this.getBillboardRotation(renderState, displayEntityRenderState, new Quaternionf()));
				AffineTransformation affineTransformation = renderState.transformation().interpolate(f);
				matrixStack.multiplyPositionMatrix(affineTransformation.getMatrix());
				this.render(displayEntityRenderState, matrixStack, vertexConsumerProvider, k, f);
				matrixStack.pop();
			}
		}
	}

	private Quaternionf getBillboardRotation(DisplayEntity.RenderState renderState, ST state, Quaternionf quaternionf) {
		Camera camera = this.renderDispatcher.camera;

		return switch (renderState.billboardConstraints()) {
			case FIXED -> quaternionf.rotationYXZ((float) (-Math.PI / 180.0) * state.yaw, (float) (Math.PI / 180.0) * state.pitch, 0.0F);
			case HORIZONTAL -> quaternionf.rotationYXZ((float) (-Math.PI / 180.0) * state.yaw, (float) (Math.PI / 180.0) * getNegatedPitch(camera), 0.0F);
			case VERTICAL -> quaternionf.rotationYXZ((float) (-Math.PI / 180.0) * getBackwardsYaw(camera), (float) (Math.PI / 180.0) * state.pitch, 0.0F);
			case CENTER -> quaternionf.rotationYXZ((float) (-Math.PI / 180.0) * getBackwardsYaw(camera), (float) (Math.PI / 180.0) * getNegatedPitch(camera), 0.0F);
		};
	}

	private static float getBackwardsYaw(Camera camera) {
		return camera.getYaw() - 180.0F;
	}

	private static float getNegatedPitch(Camera camera) {
		return -camera.getPitch();
	}

	private static <T extends DisplayEntity> float lerpYaw(T entity, float delta) {
		return entity.getLerpedYaw(delta);
	}

	private static <T extends DisplayEntity> float lerpPitch(T entity, float delta) {
		return entity.getLerpedPitch(delta);
	}

	protected abstract void render(ST state, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, float tickDelta);

	public void updateRenderState(T displayEntity, ST displayEntityRenderState, float f) {
		super.updateRenderState(displayEntity, displayEntityRenderState, f);
		displayEntityRenderState.displayRenderState = displayEntity.getRenderState();
		displayEntityRenderState.lerpProgress = displayEntity.getLerpProgress(f);
		displayEntityRenderState.yaw = lerpYaw(displayEntity, f);
		displayEntityRenderState.pitch = lerpPitch(displayEntity, f);
	}

	@Environment(EnvType.CLIENT)
	public static class BlockDisplayEntityRenderer
		extends DisplayEntityRenderer<DisplayEntity.BlockDisplayEntity, DisplayEntity.BlockDisplayEntity.Data, BlockDisplayEntityRenderState> {
		private final BlockRenderManager blockRenderManager;

		protected BlockDisplayEntityRenderer(EntityRendererFactory.Context context) {
			super(context);
			this.blockRenderManager = context.getBlockRenderManager();
		}

		public BlockDisplayEntityRenderState getRenderState() {
			return new BlockDisplayEntityRenderState();
		}

		public void updateRenderState(DisplayEntity.BlockDisplayEntity blockDisplayEntity, BlockDisplayEntityRenderState blockDisplayEntityRenderState, float f) {
			super.updateRenderState(blockDisplayEntity, blockDisplayEntityRenderState, f);
			blockDisplayEntityRenderState.data = blockDisplayEntity.getData();
		}

		public void render(
			BlockDisplayEntityRenderState blockDisplayEntityRenderState, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, float f
		) {
			this.blockRenderManager
				.renderBlockAsEntity(blockDisplayEntityRenderState.data.blockState(), matrixStack, vertexConsumerProvider, i, OverlayTexture.DEFAULT_UV);
		}
	}

	@Environment(EnvType.CLIENT)
	public static class ItemDisplayEntityRenderer
		extends DisplayEntityRenderer<DisplayEntity.ItemDisplayEntity, DisplayEntity.ItemDisplayEntity.Data, ItemDisplayEntityRenderState> {
		private final ItemRenderer itemRenderer;

		protected ItemDisplayEntityRenderer(EntityRendererFactory.Context context) {
			super(context);
			this.itemRenderer = context.getItemRenderer();
		}

		public ItemDisplayEntityRenderState getRenderState() {
			return new ItemDisplayEntityRenderState();
		}

		public void updateRenderState(DisplayEntity.ItemDisplayEntity itemDisplayEntity, ItemDisplayEntityRenderState itemDisplayEntityRenderState, float f) {
			super.updateRenderState(itemDisplayEntity, itemDisplayEntityRenderState, f);
			DisplayEntity.ItemDisplayEntity.Data data = itemDisplayEntity.getData();
			if (data != null) {
				itemDisplayEntityRenderState.data = data;
				itemDisplayEntityRenderState.model = this.itemRenderer
					.getModel(itemDisplayEntityRenderState.data.itemStack(), itemDisplayEntity.getWorld(), null, itemDisplayEntity.getId());
			} else {
				itemDisplayEntityRenderState.data = null;
				itemDisplayEntityRenderState.model = null;
			}
		}

		public void render(
			ItemDisplayEntityRenderState itemDisplayEntityRenderState, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, float f
		) {
			DisplayEntity.ItemDisplayEntity.Data data = itemDisplayEntityRenderState.data;
			BakedModel bakedModel = itemDisplayEntityRenderState.model;
			if (data != null && bakedModel != null) {
				matrixStack.multiply(RotationAxis.POSITIVE_Y.rotation((float) Math.PI));
				this.itemRenderer.renderItem(data.itemStack(), data.itemTransform(), false, matrixStack, vertexConsumerProvider, i, OverlayTexture.DEFAULT_UV, bakedModel);
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public static class TextDisplayEntityRenderer
		extends DisplayEntityRenderer<DisplayEntity.TextDisplayEntity, DisplayEntity.TextDisplayEntity.Data, TextDisplayEntityRenderState> {
		private final TextRenderer displayTextRenderer;

		protected TextDisplayEntityRenderer(EntityRendererFactory.Context context) {
			super(context);
			this.displayTextRenderer = context.getTextRenderer();
		}

		public TextDisplayEntityRenderState getRenderState() {
			return new TextDisplayEntityRenderState();
		}

		public void updateRenderState(DisplayEntity.TextDisplayEntity textDisplayEntity, TextDisplayEntityRenderState textDisplayEntityRenderState, float f) {
			super.updateRenderState(textDisplayEntity, textDisplayEntityRenderState, f);
			textDisplayEntityRenderState.data = textDisplayEntity.getData();
			textDisplayEntityRenderState.textLines = textDisplayEntity.splitLines(this::getLines);
		}

		private DisplayEntity.TextDisplayEntity.TextLines getLines(Text text, int width) {
			List<OrderedText> list = this.displayTextRenderer.wrapLines(text, width);
			List<DisplayEntity.TextDisplayEntity.TextLine> list2 = new ArrayList(list.size());
			int i = 0;

			for (OrderedText orderedText : list) {
				int j = this.displayTextRenderer.getWidth(orderedText);
				i = Math.max(i, j);
				list2.add(new DisplayEntity.TextDisplayEntity.TextLine(orderedText, j));
			}

			return new DisplayEntity.TextDisplayEntity.TextLines(list2, i);
		}

		public void render(
			TextDisplayEntityRenderState textDisplayEntityRenderState, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, float f
		) {
			DisplayEntity.TextDisplayEntity.Data data = textDisplayEntityRenderState.data;
			byte b = data.flags();
			boolean bl = (b & DisplayEntity.TextDisplayEntity.SEE_THROUGH_FLAG) != 0;
			boolean bl2 = (b & DisplayEntity.TextDisplayEntity.DEFAULT_BACKGROUND_FLAG) != 0;
			boolean bl3 = (b & DisplayEntity.TextDisplayEntity.SHADOW_FLAG) != 0;
			DisplayEntity.TextDisplayEntity.TextAlignment textAlignment = DisplayEntity.TextDisplayEntity.getAlignment(b);
			byte c = (byte)data.textOpacity().lerp(f);
			int j;
			if (bl2) {
				float g = MinecraftClient.getInstance().options.getTextBackgroundOpacity(0.25F);
				j = (int)(g * 255.0F) << 24;
			} else {
				j = data.backgroundColor().lerp(f);
			}

			float g = 0.0F;
			Matrix4f matrix4f = matrixStack.peek().getPositionMatrix();
			matrix4f.rotate((float) Math.PI, 0.0F, 1.0F, 0.0F);
			matrix4f.scale(-0.025F, -0.025F, -0.025F);
			DisplayEntity.TextDisplayEntity.TextLines textLines = textDisplayEntityRenderState.textLines;
			int k = 1;
			int l = 9 + 1;
			int m = textLines.width();
			int n = textLines.lines().size() * l - 1;
			matrix4f.translate(1.0F - (float)m / 2.0F, (float)(-n), 0.0F);
			if (j != 0) {
				VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(bl ? RenderLayer.getTextBackgroundSeeThrough() : RenderLayer.getTextBackground());
				vertexConsumer.vertex(matrix4f, -1.0F, -1.0F, 0.0F).color(j).light(i);
				vertexConsumer.vertex(matrix4f, -1.0F, (float)n, 0.0F).color(j).light(i);
				vertexConsumer.vertex(matrix4f, (float)m, (float)n, 0.0F).color(j).light(i);
				vertexConsumer.vertex(matrix4f, (float)m, -1.0F, 0.0F).color(j).light(i);
			}

			for (DisplayEntity.TextDisplayEntity.TextLine textLine : textLines.lines()) {
				float h = switch (textAlignment) {
					case LEFT -> 0.0F;
					case RIGHT -> (float)(m - textLine.width());
					case CENTER -> (float)m / 2.0F - (float)textLine.width() / 2.0F;
				};
				this.displayTextRenderer
					.draw(
						textLine.contents(),
						h,
						g,
						c << 24 | 16777215,
						bl3,
						matrix4f,
						vertexConsumerProvider,
						bl ? TextRenderer.TextLayerType.SEE_THROUGH : TextRenderer.TextLayerType.POLYGON_OFFSET,
						0,
						i
					);
				g += (float)l;
			}
		}
	}
}
