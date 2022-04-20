package net.minecraft.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import java.nio.ByteBuffer;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.VertexBuffer;

@Environment(EnvType.CLIENT)
public class BufferRenderer {
	@Nullable
	private static VertexBuffer field_38982;

	public static void unbindAll() {
		if (field_38982 != null) {
			method_43436();
			VertexBuffer.unbind();
		}
	}

	public static void method_43436() {
		field_38982 = null;
	}

	public static void method_43433(BufferBuilder bufferBuilder) {
		if (!RenderSystem.isOnRenderThreadOrInit()) {
			RenderSystem.recordRenderCall(() -> method_43438(bufferBuilder));
		} else {
			method_43438(bufferBuilder);
		}
	}

	private static void method_43438(BufferBuilder bufferBuilder) {
		VertexBuffer vertexBuffer = method_43439(bufferBuilder);
		if (vertexBuffer != null) {
			vertexBuffer.setShader(RenderSystem.getModelViewMatrix(), RenderSystem.getProjectionMatrix(), RenderSystem.getShader());
		}
	}

	public static void method_43437(BufferBuilder bufferBuilder) {
		VertexBuffer vertexBuffer = method_43439(bufferBuilder);
		if (vertexBuffer != null) {
			vertexBuffer.drawElements();
		}
	}

	@Nullable
	private static VertexBuffer method_43439(BufferBuilder bufferBuilder) {
		RenderSystem.assertOnRenderThread();
		Pair<BufferBuilder.DrawArrayParameters, ByteBuffer> pair = bufferBuilder.popData();
		BufferBuilder.DrawArrayParameters drawArrayParameters = pair.getFirst();
		ByteBuffer byteBuffer = pair.getSecond();
		byteBuffer.clear();
		if (drawArrayParameters.getCount() <= 0) {
			return null;
		} else {
			VertexBuffer vertexBuffer = method_43435(drawArrayParameters.getVertexFormat());
			vertexBuffer.method_43441(drawArrayParameters, byteBuffer);
			return vertexBuffer;
		}
	}

	private static VertexBuffer method_43435(VertexFormat vertexFormat) {
		VertexBuffer vertexBuffer = vertexFormat.method_43446();
		method_43434(vertexBuffer);
		return vertexBuffer;
	}

	private static void method_43434(VertexBuffer vertexBuffer) {
		if (vertexBuffer != field_38982) {
			vertexBuffer.bind();
			field_38982 = vertexBuffer;
		}
	}
}
