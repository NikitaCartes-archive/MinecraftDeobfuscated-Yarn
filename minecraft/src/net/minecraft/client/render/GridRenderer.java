package net.minecraft.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_9597;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.util.Window;
import net.minecraft.client.world.ClientGridCarrierView;
import net.minecraft.entity.GridCarrierEntity;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Grid;
import org.joml.Matrix4f;
import org.joml.Vector3f;

@Environment(EnvType.CLIENT)
public class GridRenderer implements AutoCloseable {
	private static final double field_51065 = 3.0;
	private final MinecraftClient client = MinecraftClient.getInstance();
	private final BlockRenderManager blockRenderManager;
	private final ClientGridCarrierView gridCarrierView;
	private final Reference2ObjectMap<RenderLayer, VertexBuffer> field_51069 = new Reference2ObjectArrayMap<>();
	@Nullable
	private CompletableFuture<class_9597.class_9599> field_51070;
	private boolean field_51071 = true;

	public GridRenderer(ClientGridCarrierView gridCarrierView) {
		this.gridCarrierView = gridCarrierView;
		this.blockRenderManager = this.client.getBlockRenderManager();
	}

	private void method_59314() {
		if (this.field_51070 != null) {
			if (this.field_51070.isDone()) {
				try (class_9597.class_9599 lv = (class_9597.class_9599)this.field_51070.join()) {
					lv.method_59313(this.field_51069);
					VertexBuffer.unbind();
				}

				this.field_51070 = null;
			}
		} else if (this.field_51071) {
			this.field_51071 = false;
			class_9597.class_9598 lv2 = class_9597.class_9598.method_59311(this.gridCarrierView);
			class_9597 lv3 = new class_9597(this.blockRenderManager, lv2);
			this.field_51070 = CompletableFuture.supplyAsync(lv3::method_59308, Util.getMainWorkerExecutor());
		}
	}

	public void render(float f, double d, double e, double g, Frustum frustum, Matrix4f matrix4f, Matrix4f matrix4f2, boolean bl) {
		GridCarrierEntity gridCarrierEntity = this.gridCarrierView.getGridCarrier();
		Vec3d vec3d = new Vec3d(
			MathHelper.lerp((double)f, gridCarrierEntity.lastRenderX, gridCarrierEntity.getX()),
			MathHelper.lerp((double)f, gridCarrierEntity.lastRenderY, gridCarrierEntity.getY()),
			MathHelper.lerp((double)f, gridCarrierEntity.lastRenderZ, gridCarrierEntity.getZ())
		);
		Grid grid = this.gridCarrierView.getGrid();
		if (frustum.isVisible(
			vec3d.x - 3.0,
			vec3d.y - 3.0,
			vec3d.z - 3.0,
			vec3d.x + (double)grid.getXSize() + 1.0 + 3.0,
			vec3d.y + (double)grid.getYSize() + 1.0 + 3.0,
			vec3d.z + (double)grid.getZSize() + 1.0 + 3.0
		)) {
			this.method_59314();
			if (!this.field_51069.isEmpty()) {
				Window window = this.client.getWindow();
				Vector3f vector3f = new Vector3f((float)(vec3d.x - d), (float)(vec3d.y - e), (float)(vec3d.z - g));
				if (bl) {
					this.renderLayer(RenderLayer.getTranslucent(), vector3f, matrix4f, matrix4f2, window);
					this.renderLayer(RenderLayer.getTripwire(), vector3f, matrix4f, matrix4f2, window);
				} else {
					this.renderLayer(RenderLayer.getSolid(), vector3f, matrix4f, matrix4f2, window);
					this.renderLayer(RenderLayer.getCutoutMipped(), vector3f, matrix4f, matrix4f2, window);
					this.renderLayer(RenderLayer.getCutout(), vector3f, matrix4f, matrix4f2, window);
				}
			}
		}
	}

	private void renderLayer(RenderLayer layer, Vector3f vector3f, Matrix4f matrix4f, Matrix4f matrix4f2, Window window) {
		VertexBuffer vertexBuffer = this.field_51069.get(layer);
		if (vertexBuffer != null) {
			layer.startDrawing();
			ShaderProgram shaderProgram = RenderSystem.getShader();
			shaderProgram.method_59363(VertexFormat.DrawMode.QUADS, matrix4f, matrix4f2, window);
			shaderProgram.chunkOffset.set(vector3f.x, vector3f.y, vector3f.z);
			shaderProgram.bind();
			vertexBuffer.bind();
			vertexBuffer.draw();
			VertexBuffer.unbind();
			shaderProgram.unbind();
			layer.endDrawing();
		}
	}

	public void close() {
		this.field_51069.values().forEach(VertexBuffer::close);
		this.field_51069.clear();
		if (this.field_51070 != null) {
			this.field_51070.thenAcceptAsync(class_9597.class_9599::close, runnable -> RenderSystem.recordRenderCall(runnable::run));
			this.field_51070 = null;
		}
	}
}
