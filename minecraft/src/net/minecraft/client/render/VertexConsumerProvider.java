package net.minecraft.client.render;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_9799;
import net.minecraft.class_9801;

/**
 * Manages rendering with multiple {@linkplain RenderLayer render layers}.
 */
@Environment(EnvType.CLIENT)
public interface VertexConsumerProvider {
	/**
	 * {@return a vertex consumer provider that immediately draws the current
	 * buffer builder when a different render layer is requested}.
	 */
	static VertexConsumerProvider.Immediate immediate(class_9799 buffer) {
		return immediate(ImmutableMap.of(), buffer);
	}

	/**
	 * {@return a vertex consumer provider that immediately draws the current
	 * buffer builder when a different render layer is requested except {@code
	 * layerBuffers}}.
	 * 
	 * <p>{@code layerBuffers} will not be drawn immediately after switching
	 * the current render layer. The caller of this method can control when to
	 * draw these render layers. For example, {@linkplain
	 * RenderLayer#TRANSLUCENT the translucent render layer} should be drawn in
	 * a later stage so the other things behind translucent objects are
	 * visible.
	 */
	static VertexConsumerProvider.Immediate immediate(Map<RenderLayer, class_9799> layerBuffers, class_9799 fallbackBuffer) {
		return new VertexConsumerProvider.Immediate(fallbackBuffer, layerBuffers);
	}

	/**
	 * Starts building a buffer that will be drawn with {@code layer}.
	 * 
	 * <p>The returned vertex consumer can only be safely used until this
	 * method is called with a different render layer.
	 * 
	 * <p>Note that the user of this method cannot modify states controlled
	 * by the render layer. Those states will be overridden by the render layer
	 * before drawing them. If you need to set states yourself, consider using
	 * {@link Tessellator} or making a custom render layer.
	 */
	VertexConsumer getBuffer(RenderLayer layer);

	/**
	 * A vertex consumer provider that immediately draws the current buffer
	 * builder when a different render layer is requested except for render
	 * layers specified in the constructor.
	 * 
	 * @see VertexConsumerProvider#immediate(BufferBuilder)
	 * @see VertexConsumerProvider#immediate(Map, BufferBuilder)
	 */
	@Environment(EnvType.CLIENT)
	public static class Immediate implements VertexConsumerProvider {
		protected final class_9799 field_52156;
		protected final Map<RenderLayer, class_9799> layerBuffers;
		protected final Map<RenderLayer, BufferBuilder> field_52157 = new HashMap();
		@Nullable
		protected RenderLayer field_52158;

		protected Immediate(class_9799 fallbackBuffer, Map<RenderLayer, class_9799> layerBuffers) {
			this.field_52156 = fallbackBuffer;
			this.layerBuffers = layerBuffers;
		}

		@Override
		public VertexConsumer getBuffer(RenderLayer renderLayer) {
			BufferBuilder bufferBuilder = (BufferBuilder)this.field_52157.get(renderLayer);
			if (bufferBuilder != null && !renderLayer.areVerticesNotShared()) {
				this.method_60893(renderLayer, bufferBuilder);
				bufferBuilder = null;
			}

			if (bufferBuilder != null) {
				return bufferBuilder;
			} else {
				class_9799 lv = (class_9799)this.layerBuffers.get(renderLayer);
				if (lv != null) {
					bufferBuilder = new BufferBuilder(lv, renderLayer.getDrawMode(), renderLayer.getVertexFormat());
				} else {
					if (this.field_52158 != null) {
						this.draw(this.field_52158);
					}

					bufferBuilder = new BufferBuilder(this.field_52156, renderLayer.getDrawMode(), renderLayer.getVertexFormat());
					this.field_52158 = renderLayer;
				}

				this.field_52157.put(renderLayer, bufferBuilder);
				return bufferBuilder;
			}
		}

		/**
		 * Draws the current render layer if it's not in {@code layerBuffers}
		 * specified in the constructor.
		 */
		public void drawCurrentLayer() {
			if (this.field_52158 != null && !this.layerBuffers.containsKey(this.field_52158)) {
				this.draw(this.field_52158);
			}

			this.field_52158 = null;
		}

		/**
		 * Draws all remaining render layers including {@code layerBuffers}
		 * specified in the constructor.
		 */
		public void draw() {
			this.field_52157.forEach(this::method_60893);
			this.field_52157.clear();
		}

		/**
		 * Draws the contents in the {@code layer}'s buffer.
		 */
		public void draw(RenderLayer layer) {
			BufferBuilder bufferBuilder = (BufferBuilder)this.field_52157.remove(layer);
			if (bufferBuilder != null) {
				this.method_60893(layer, bufferBuilder);
			}
		}

		private void method_60893(RenderLayer renderLayer, BufferBuilder bufferBuilder) {
			class_9801 lv = bufferBuilder.method_60794();
			if (lv != null) {
				if (renderLayer.method_60894()) {
					class_9799 lv2 = (class_9799)this.layerBuffers.getOrDefault(renderLayer, this.field_52156);
					lv.method_60819(lv2, RenderSystem.getVertexSorting());
				}

				renderLayer.method_60895(lv);
			}

			if (renderLayer.equals(this.field_52158)) {
				this.field_52158 = null;
			}
		}
	}
}
