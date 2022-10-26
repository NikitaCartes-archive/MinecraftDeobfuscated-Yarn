package net.minecraft.client.render.debug;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.PositionSource;
import net.minecraft.world.event.listener.GameEventListener;
import org.joml.Vector3f;

@Environment(EnvType.CLIENT)
public class GameEventDebugRenderer implements DebugRenderer.Renderer {
	private final MinecraftClient client;
	private static final int field_32899 = 32;
	private static final float field_32900 = 1.0F;
	private final List<GameEventDebugRenderer.Entry> entries = Lists.<GameEventDebugRenderer.Entry>newArrayList();
	private final List<GameEventDebugRenderer.Listener> listeners = Lists.<GameEventDebugRenderer.Listener>newArrayList();

	public GameEventDebugRenderer(MinecraftClient client) {
		this.client = client;
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, double cameraX, double cameraY, double cameraZ) {
		World world = this.client.world;
		if (world == null) {
			this.entries.clear();
			this.listeners.clear();
		} else {
			Vec3d vec3d = new Vec3d(cameraX, 0.0, cameraZ);
			this.entries.removeIf(GameEventDebugRenderer.Entry::hasExpired);
			this.listeners.removeIf(listener -> listener.isTooFar(world, vec3d));
			RenderSystem.disableTexture();
			RenderSystem.enableDepthTest();
			RenderSystem.enableBlend();
			RenderSystem.defaultBlendFunc();
			VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getLines());

			for (GameEventDebugRenderer.Listener listener : this.listeners) {
				listener.getPos(world)
					.ifPresent(
						pos -> {
							double gx = pos.getX() - (double)listener.getRange();
							double hx = pos.getY() - (double)listener.getRange();
							double ix = pos.getZ() - (double)listener.getRange();
							double jx = pos.getX() + (double)listener.getRange();
							double k = pos.getY() + (double)listener.getRange();
							double l = pos.getZ() + (double)listener.getRange();
							Vector3f vector3f = new Vector3f(1.0F, 1.0F, 0.0F);
							WorldRenderer.drawShapeOutline(
								matrices,
								vertexConsumer,
								VoxelShapes.cuboid(new Box(gx, hx, ix, jx, k, l)),
								-cameraX,
								-cameraY,
								-cameraZ,
								vector3f.x(),
								vector3f.y(),
								vector3f.z(),
								0.35F
							);
						}
					);
			}

			RenderSystem.setShader(GameRenderer::getPositionColorShader);
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder bufferBuilder = tessellator.getBuffer();
			bufferBuilder.begin(VertexFormat.DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);

			for (GameEventDebugRenderer.Listener listener2 : this.listeners) {
				listener2.getPos(world)
					.ifPresent(
						pos -> {
							Vector3f vector3f = new Vector3f(1.0F, 1.0F, 0.0F);
							WorldRenderer.drawBox(
								bufferBuilder,
								pos.getX() - 0.25 - cameraX,
								pos.getY() - cameraY,
								pos.getZ() - 0.25 - cameraZ,
								pos.getX() + 0.25 - cameraX,
								pos.getY() - cameraY + 1.0,
								pos.getZ() + 0.25 - cameraZ,
								vector3f.x(),
								vector3f.y(),
								vector3f.z(),
								0.35F
							);
						}
					);
			}

			tessellator.draw();
			RenderSystem.enableBlend();
			RenderSystem.defaultBlendFunc();
			RenderSystem.lineWidth(2.0F);
			RenderSystem.depthMask(false);

			for (GameEventDebugRenderer.Listener listener2 : this.listeners) {
				listener2.getPos(world).ifPresent(pos -> {
					DebugRenderer.drawString("Listener Origin", pos.getX(), pos.getY() + 1.8F, pos.getZ(), -1, 0.025F);
					DebugRenderer.drawString(new BlockPos(pos).toString(), pos.getX(), pos.getY() + 1.5, pos.getZ(), -6959665, 0.025F);
				});
			}

			for (GameEventDebugRenderer.Entry entry : this.entries) {
				Vec3d vec3d2 = entry.pos;
				double d = 0.2F;
				double e = vec3d2.x - 0.2F;
				double f = vec3d2.y - 0.2F;
				double g = vec3d2.z - 0.2F;
				double h = vec3d2.x + 0.2F;
				double i = vec3d2.y + 0.2F + 0.5;
				double j = vec3d2.z + 0.2F;
				drawBoxIfCameraReady(new Box(e, f, g, h, i, j), 1.0F, 1.0F, 1.0F, 0.2F);
				DebugRenderer.drawString(entry.event.getId(), vec3d2.x, vec3d2.y + 0.85F, vec3d2.z, -7564911, 0.0075F);
			}

			RenderSystem.depthMask(true);
			RenderSystem.enableTexture();
			RenderSystem.disableBlend();
		}
	}

	private static void drawBoxIfCameraReady(Box box, float red, float green, float blue, float alpha) {
		Camera camera = MinecraftClient.getInstance().gameRenderer.getCamera();
		if (camera.isReady()) {
			RenderSystem.enableBlend();
			RenderSystem.defaultBlendFunc();
			Vec3d vec3d = camera.getPos().negate();
			DebugRenderer.drawBox(box.offset(vec3d), red, green, blue, alpha);
		}
	}

	public void addEvent(GameEvent event, Vec3d pos) {
		this.entries.add(new GameEventDebugRenderer.Entry(Util.getMeasuringTimeMs(), event, pos));
	}

	public void addListener(PositionSource positionSource, int range) {
		this.listeners.add(new GameEventDebugRenderer.Listener(positionSource, range));
	}

	@Environment(EnvType.CLIENT)
	static record Entry(long startingMs, GameEvent event, Vec3d pos) {

		public boolean hasExpired() {
			return Util.getMeasuringTimeMs() - this.startingMs > 3000L;
		}
	}

	@Environment(EnvType.CLIENT)
	static class Listener implements GameEventListener {
		public final PositionSource positionSource;
		public final int range;

		public Listener(PositionSource positionSource, int range) {
			this.positionSource = positionSource;
			this.range = range;
		}

		public boolean isTooFar(World world, Vec3d pos) {
			return this.positionSource.getPos(world).filter(pos2 -> pos2.squaredDistanceTo(pos) <= 1024.0).isPresent();
		}

		public Optional<Vec3d> getPos(World world) {
			return this.positionSource.getPos(world);
		}

		@Override
		public PositionSource getPositionSource() {
			return this.positionSource;
		}

		@Override
		public int getRange() {
			return this.range;
		}

		@Override
		public boolean listen(ServerWorld world, GameEvent event, GameEvent.Emitter emitter, Vec3d emitterPos) {
			return false;
		}
	}
}
