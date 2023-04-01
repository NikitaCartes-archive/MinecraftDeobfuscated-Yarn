package net.minecraft.client.render.entity;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_8464;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.AffineTransformation;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

@Environment(EnvType.CLIENT)
public abstract class DisplayEntityRenderer<T extends DisplayEntity, S> extends EntityRenderer<T> {
	private final EntityRenderDispatcher renderDispatcher;

	protected DisplayEntityRenderer(EntityRendererFactory.Context context) {
		super(context);
		this.renderDispatcher = context.getRenderDispatcher();
	}

	public Identifier getTexture(T displayEntity) {
		return SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE;
	}

	public void render(T displayEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
		DisplayEntity.RenderState renderState = displayEntity.getRenderState();
		if (renderState != null) {
			S object = this.getData(displayEntity);
			if (object != null) {
				float h = displayEntity.getLerpProgress(g);
				this.shadowRadius = renderState.shadowRadius().lerp(h);
				this.shadowOpacity = renderState.shadowStrength().lerp(h);
				int j = renderState.brightnessOverride();
				int k = j != -1 ? j : i;
				super.render(displayEntity, f, g, matrixStack, vertexConsumerProvider, k);
				matrixStack.push();
				matrixStack.multiply(this.getBillboardRotation(renderState, displayEntity));
				AffineTransformation affineTransformation = renderState.transformation().interpolate(h);
				matrixStack.multiplyPositionMatrix(affineTransformation.getMatrix());
				matrixStack.peek().getNormalMatrix().rotate(affineTransformation.getLeftRotation()).rotate(affineTransformation.getRightRotation());
				this.render(displayEntity, object, matrixStack, vertexConsumerProvider, k, h);
				matrixStack.pop();
			}
		}
	}

	private Quaternionf getBillboardRotation(DisplayEntity.RenderState renderState, T entity) {
		Camera camera = this.renderDispatcher.camera;

		return switch (renderState.billboardConstraints()) {
			case FIXED -> entity.getFixedRotation();
			case HORIZONTAL -> new Quaternionf().rotationYXZ((float) (-Math.PI / 180.0) * entity.getYaw(), (float) (-Math.PI / 180.0) * camera.getPitch(), 0.0F);
			case VERTICAL -> new Quaternionf()
			.rotationYXZ((float) Math.PI - (float) (Math.PI / 180.0) * camera.getYaw(), (float) (Math.PI / 180.0) * entity.getPitch(), 0.0F);
			case CENTER -> new Quaternionf()
			.rotationYXZ((float) Math.PI - (float) (Math.PI / 180.0) * camera.getYaw(), (float) (-Math.PI / 180.0) * camera.getPitch(), 0.0F);
		};
	}

	@Nullable
	protected abstract S getData(T entity);

	protected abstract void render(T entity, S data, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int brightness, float lerpProgress);

	@Environment(EnvType.CLIENT)
	public static class BlockDisplayEntityRenderer extends DisplayEntityRenderer<DisplayEntity.BlockDisplayEntity, DisplayEntity.BlockDisplayEntity.Data> {
		private final BlockRenderManager blockRenderManager;

		protected BlockDisplayEntityRenderer(EntityRendererFactory.Context context) {
			super(context);
			this.blockRenderManager = context.getBlockRenderManager();
		}

		@Nullable
		protected DisplayEntity.BlockDisplayEntity.Data getData(DisplayEntity.BlockDisplayEntity blockDisplayEntity) {
			return blockDisplayEntity.getData();
		}

		public void render(
			DisplayEntity.BlockDisplayEntity blockDisplayEntity,
			DisplayEntity.BlockDisplayEntity.Data data,
			MatrixStack matrixStack,
			VertexConsumerProvider vertexConsumerProvider,
			int i,
			float f
		) {
			this.blockRenderManager.renderBlockAsEntity(data.blockState(), matrixStack, vertexConsumerProvider, i, OverlayTexture.DEFAULT_UV);
		}
	}

	@Environment(EnvType.CLIENT)
	public static class ItemDisplayEntityRenderer extends DisplayEntityRenderer<DisplayEntity.ItemDisplayEntity, DisplayEntity.ItemDisplayEntity.Data> {
		private final ItemRenderer itemRenderer;

		protected ItemDisplayEntityRenderer(EntityRendererFactory.Context context) {
			super(context);
			this.itemRenderer = context.getItemRenderer();
		}

		@Nullable
		protected DisplayEntity.ItemDisplayEntity.Data getData(DisplayEntity.ItemDisplayEntity itemDisplayEntity) {
			return itemDisplayEntity.getData();
		}

		public void render(
			DisplayEntity.ItemDisplayEntity itemDisplayEntity,
			DisplayEntity.ItemDisplayEntity.Data data,
			MatrixStack matrixStack,
			VertexConsumerProvider vertexConsumerProvider,
			int i,
			float f
		) {
			this.itemRenderer
				.renderItem(
					data.itemStack(),
					data.itemTransform(),
					i,
					OverlayTexture.DEFAULT_UV,
					matrixStack,
					vertexConsumerProvider,
					itemDisplayEntity.getWorld(),
					itemDisplayEntity.getId()
				);
		}
	}

	@Environment(EnvType.CLIENT)
	public static class StencilDisplayEntityRenderer extends DisplayEntityRenderer<DisplayEntity.StencilDisplayEntity, DisplayEntity.StencilDisplayEntity.Data> {
		private static final Int2ObjectMap<class_8464.class_8465[]> field_44408 = new Int2ObjectArrayMap<>();
		private static final Int2ObjectMap<class_8464.class_8465[]> field_44409 = new Int2ObjectArrayMap<>();

		protected StencilDisplayEntityRenderer(EntityRendererFactory.Context context) {
			super(context);
		}

		@Nullable
		protected DisplayEntity.StencilDisplayEntity.Data getData(DisplayEntity.StencilDisplayEntity stencilDisplayEntity) {
			return stencilDisplayEntity.getData();
		}

		protected void render(
			DisplayEntity.StencilDisplayEntity stencilDisplayEntity,
			DisplayEntity.StencilDisplayEntity.Data data,
			MatrixStack matrixStack,
			VertexConsumerProvider vertexConsumerProvider,
			int i,
			float f
		) {
			int j = data.color().lerp(f);
			int k = MathHelper.clamp(data.shape().lerp(f), 0, 1);
			int l = data.lod().lerp(f);
			class_8464.class_8465[] lvs;
			if (k == 1) {
				lvs = field_44408.computeIfAbsent(MathHelper.clamp(l + 3, 3, 64), class_8464::method_51055);
			} else {
				lvs = field_44409.computeIfAbsent(MathHelper.clamp(l, 0, 4), class_8464::method_51053);
			}

			class_8464.method_51054(lvs, matrixStack.peek().getPositionMatrix(), vertexConsumerProvider, j);
		}
	}

	@Environment(EnvType.CLIENT)
	public static class TextDisplayEntityRenderer extends DisplayEntityRenderer<DisplayEntity.TextDisplayEntity, DisplayEntity.TextDisplayEntity.Data> {
		private final TextRenderer displayTextRenderer;

		protected TextDisplayEntityRenderer(EntityRendererFactory.Context context) {
			super(context);
			this.displayTextRenderer = context.getTextRenderer();
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

		@Nullable
		protected DisplayEntity.TextDisplayEntity.Data getData(DisplayEntity.TextDisplayEntity textDisplayEntity) {
			return textDisplayEntity.getData();
		}

		public void render(
			DisplayEntity.TextDisplayEntity textDisplayEntity,
			DisplayEntity.TextDisplayEntity.Data data,
			MatrixStack matrixStack,
			VertexConsumerProvider vertexConsumerProvider,
			int i,
			float f
		) {
			byte b = data.flags();
			boolean bl = (b & 2) != 0;
			boolean bl2 = (b & 4) != 0;
			boolean bl3 = (b & 1) != 0;
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
			DisplayEntity.TextDisplayEntity.TextLines textLines = textDisplayEntity.splitLines(this::getLines);
			int k = 9 + 1;
			int l = textLines.width();
			int m = textLines.lines().size() * k;
			matrix4f.translate(1.0F - (float)l / 2.0F, (float)(-m), 0.0F);
			if (j != 0) {
				VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(bl ? RenderLayer.getTextBackgroundSeeThrough() : RenderLayer.getTextBackground());
				vertexConsumer.vertex(matrix4f, -1.0F, -1.0F, 0.0F).color(j).light(i).next();
				vertexConsumer.vertex(matrix4f, -1.0F, (float)m, 0.0F).color(j).light(i).next();
				vertexConsumer.vertex(matrix4f, (float)l, (float)m, 0.0F).color(j).light(i).next();
				vertexConsumer.vertex(matrix4f, (float)l, -1.0F, 0.0F).color(j).light(i).next();
			}

			for (DisplayEntity.TextDisplayEntity.TextLine textLine : textLines.lines()) {
				float h = switch (textAlignment) {
					case LEFT -> 0.0F;
					case RIGHT -> (float)(l - textLine.width());
					case CENTER -> (float)l / 2.0F - (float)textLine.width() / 2.0F;
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
				g += (float)k;
			}
		}
	}
}
