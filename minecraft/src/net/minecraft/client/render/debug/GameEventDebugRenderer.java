package net.minecraft.client.render.debug;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Colors;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.PositionSource;
import net.minecraft.world.event.listener.GameEventListener;

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
			this.listeners.removeIf(listenerx -> listenerx.isTooFar(world, vec3d));
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
							WorldRenderer.drawShapeOutline(
								matrices, vertexConsumer, VoxelShapes.cuboid(new Box(gx, hx, ix, jx, k, l)), -cameraX, -cameraY, -cameraZ, 1.0F, 1.0F, 0.0F, 0.35F, true
							);
						}
					);
			}

			VertexConsumer vertexConsumer2 = vertexConsumers.getBuffer(RenderLayer.getDebugFilledBox());

			for (GameEventDebugRenderer.Listener listener2 : this.listeners) {
				listener2.getPos(world)
					.ifPresent(
						pos -> WorldRenderer.renderFilledBox(
								matrices,
								vertexConsumer2,
								pos.getX() - 0.25 - cameraX,
								pos.getY() - cameraY,
								pos.getZ() - 0.25 - cameraZ,
								pos.getX() + 0.25 - cameraX,
								pos.getY() - cameraY + 1.0,
								pos.getZ() + 0.25 - cameraZ,
								1.0F,
								1.0F,
								0.0F,
								0.35F
							)
					);
			}

			for (GameEventDebugRenderer.Listener listener2 : this.listeners) {
				listener2.getPos(world).ifPresent(pos -> {
					DebugRenderer.drawString(matrices, vertexConsumers, "Listener Origin", pos.getX(), pos.getY() + 1.8F, pos.getZ(), Colors.WHITE, 0.025F);
					DebugRenderer.drawString(matrices, vertexConsumers, BlockPos.ofFloored(pos).toString(), pos.getX(), pos.getY() + 1.5, pos.getZ(), -6959665, 0.025F);
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
				drawBoxIfCameraReady(matrices, vertexConsumers, new Box(e, f, g, h, i, j), 1.0F, 1.0F, 1.0F, 0.2F);
				DebugRenderer.drawString(matrices, vertexConsumers, entry.event.getValue().toString(), vec3d2.x, vec3d2.y + 0.85F, vec3d2.z, -7564911, 0.0075F);
			}
		}
	}

	private static void drawBoxIfCameraReady(
		MatrixStack matrices, VertexConsumerProvider vertexConsumers, Box box, float red, float green, float blue, float alpha
	) {
		Camera camera = MinecraftClient.getInstance().gameRenderer.getCamera();
		if (camera.isReady()) {
			Vec3d vec3d = camera.getPos().negate();
			DebugRenderer.drawBox(matrices, vertexConsumers, box.offset(vec3d), red, green, blue, alpha);
		}
	}

	public void addEvent(RegistryKey<GameEvent> eventKey, Vec3d pos) {
		this.entries.add(new GameEventDebugRenderer.Entry(Util.getMeasuringTimeMs(), eventKey, pos));
	}

	public void addListener(PositionSource positionSource, int range) {
		this.listeners.add(new GameEventDebugRenderer.Listener(positionSource, range));
	}

	@Environment(EnvType.CLIENT)
	static record Entry(long startingMs, RegistryKey<GameEvent> event, Vec3d pos) {

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
		public boolean listen(ServerWorld world, RegistryEntry<GameEvent> event, GameEvent.Emitter emitter, Vec3d emitterPos) {
			return false;
		}
	}
}
