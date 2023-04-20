package net.minecraft.client.render.debug;

import java.util.Optional;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LightType;
import org.joml.Matrix4f;

@Environment(EnvType.CLIENT)
public class DebugRenderer {
	public final PathfindingDebugRenderer pathfindingDebugRenderer = new PathfindingDebugRenderer();
	public final DebugRenderer.Renderer waterDebugRenderer;
	public final DebugRenderer.Renderer chunkBorderDebugRenderer;
	public final DebugRenderer.Renderer heightmapDebugRenderer;
	public final DebugRenderer.Renderer collisionDebugRenderer;
	public final DebugRenderer.Renderer neighborUpdateDebugRenderer;
	public final StructureDebugRenderer structureDebugRenderer;
	public final DebugRenderer.Renderer skyLightDebugRenderer;
	public final DebugRenderer.Renderer worldGenAttemptDebugRenderer;
	public final DebugRenderer.Renderer blockOutlineDebugRenderer;
	public final DebugRenderer.Renderer chunkLoadingDebugRenderer;
	public final VillageDebugRenderer villageDebugRenderer;
	public final VillageSectionsDebugRenderer villageSectionsDebugRenderer;
	public final BeeDebugRenderer beeDebugRenderer;
	public final RaidCenterDebugRenderer raidCenterDebugRenderer;
	public final GoalSelectorDebugRenderer goalSelectorDebugRenderer;
	public final GameTestDebugRenderer gameTestDebugRenderer;
	public final GameEventDebugRenderer gameEventDebugRenderer;
	public final LightDebugRenderer lightDebugRenderer;
	private boolean showChunkBorder;

	public DebugRenderer(MinecraftClient client) {
		this.waterDebugRenderer = new WaterDebugRenderer(client);
		this.chunkBorderDebugRenderer = new ChunkBorderDebugRenderer(client);
		this.heightmapDebugRenderer = new HeightmapDebugRenderer(client);
		this.collisionDebugRenderer = new CollisionDebugRenderer(client);
		this.neighborUpdateDebugRenderer = new NeighborUpdateDebugRenderer(client);
		this.structureDebugRenderer = new StructureDebugRenderer(client);
		this.skyLightDebugRenderer = new SkyLightDebugRenderer(client);
		this.worldGenAttemptDebugRenderer = new WorldGenAttemptDebugRenderer();
		this.blockOutlineDebugRenderer = new BlockOutlineDebugRenderer(client);
		this.chunkLoadingDebugRenderer = new ChunkLoadingDebugRenderer(client);
		this.villageDebugRenderer = new VillageDebugRenderer(client);
		this.villageSectionsDebugRenderer = new VillageSectionsDebugRenderer();
		this.beeDebugRenderer = new BeeDebugRenderer(client);
		this.raidCenterDebugRenderer = new RaidCenterDebugRenderer(client);
		this.goalSelectorDebugRenderer = new GoalSelectorDebugRenderer(client);
		this.gameTestDebugRenderer = new GameTestDebugRenderer();
		this.gameEventDebugRenderer = new GameEventDebugRenderer(client);
		this.lightDebugRenderer = new LightDebugRenderer(client, LightType.SKY);
	}

	public void reset() {
		this.pathfindingDebugRenderer.clear();
		this.waterDebugRenderer.clear();
		this.chunkBorderDebugRenderer.clear();
		this.heightmapDebugRenderer.clear();
		this.collisionDebugRenderer.clear();
		this.neighborUpdateDebugRenderer.clear();
		this.structureDebugRenderer.clear();
		this.skyLightDebugRenderer.clear();
		this.worldGenAttemptDebugRenderer.clear();
		this.blockOutlineDebugRenderer.clear();
		this.chunkLoadingDebugRenderer.clear();
		this.villageDebugRenderer.clear();
		this.villageSectionsDebugRenderer.clear();
		this.beeDebugRenderer.clear();
		this.raidCenterDebugRenderer.clear();
		this.goalSelectorDebugRenderer.clear();
		this.gameTestDebugRenderer.clear();
		this.gameEventDebugRenderer.clear();
		this.lightDebugRenderer.clear();
	}

	public boolean toggleShowChunkBorder() {
		this.showChunkBorder = !this.showChunkBorder;
		return this.showChunkBorder;
	}

	public void render(MatrixStack matrices, VertexConsumerProvider.Immediate vertexConsumers, double cameraX, double cameraY, double cameraZ) {
		if (this.showChunkBorder && !MinecraftClient.getInstance().hasReducedDebugInfo()) {
			this.chunkBorderDebugRenderer.render(matrices, vertexConsumers, cameraX, cameraY, cameraZ);
		}

		this.gameTestDebugRenderer.render(matrices, vertexConsumers, cameraX, cameraY, cameraZ);
	}

	public static Optional<Entity> getTargetedEntity(@Nullable Entity entity, int maxDistance) {
		if (entity == null) {
			return Optional.empty();
		} else {
			Vec3d vec3d = entity.getEyePos();
			Vec3d vec3d2 = entity.getRotationVec(1.0F).multiply((double)maxDistance);
			Vec3d vec3d3 = vec3d.add(vec3d2);
			Box box = entity.getBoundingBox().stretch(vec3d2).expand(1.0);
			int i = maxDistance * maxDistance;
			Predicate<Entity> predicate = entityx -> !entityx.isSpectator() && entityx.canHit();
			EntityHitResult entityHitResult = ProjectileUtil.raycast(entity, vec3d, vec3d3, box, predicate, (double)i);
			if (entityHitResult == null) {
				return Optional.empty();
			} else {
				return vec3d.squaredDistanceTo(entityHitResult.getPos()) > (double)i ? Optional.empty() : Optional.of(entityHitResult.getEntity());
			}
		}
	}

	public static void drawBox(
		MatrixStack matrices, VertexConsumerProvider vertexConsumers, BlockPos pos1, BlockPos pos2, float red, float green, float blue, float alpha
	) {
		Camera camera = MinecraftClient.getInstance().gameRenderer.getCamera();
		if (camera.isReady()) {
			Vec3d vec3d = camera.getPos().negate();
			Box box = new Box(pos1, pos2).offset(vec3d);
			drawBox(matrices, vertexConsumers, box, red, green, blue, alpha);
		}
	}

	public static void drawBox(
		MatrixStack matrices, VertexConsumerProvider vertexConsumers, BlockPos pos, float expand, float red, float green, float blue, float alpha
	) {
		Camera camera = MinecraftClient.getInstance().gameRenderer.getCamera();
		if (camera.isReady()) {
			Vec3d vec3d = camera.getPos().negate();
			Box box = new Box(pos).offset(vec3d).expand((double)expand);
			drawBox(matrices, vertexConsumers, box, red, green, blue, alpha);
		}
	}

	public static void drawBox(MatrixStack matrices, VertexConsumerProvider vertexConsumers, Box box, float red, float green, float blue, float alpha) {
		drawBox(matrices, vertexConsumers, box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ, red, green, blue, alpha);
	}

	public static void drawBox(
		MatrixStack matrices,
		VertexConsumerProvider vertexConsumers,
		double minX,
		double minY,
		double minZ,
		double maxX,
		double maxY,
		double maxZ,
		float red,
		float green,
		float blue,
		float alpha
	) {
		VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getDebugFilledBox());
		WorldRenderer.method_3258(matrices, vertexConsumer, minX, minY, minZ, maxX, maxY, maxZ, red, green, blue, alpha);
	}

	public static void drawString(MatrixStack matrices, VertexConsumerProvider vertexConsumers, String string, int x, int y, int z, int color) {
		drawString(matrices, vertexConsumers, string, (double)x + 0.5, (double)y + 0.5, (double)z + 0.5, color);
	}

	public static void drawString(MatrixStack matrices, VertexConsumerProvider vertexConsumers, String string, double x, double y, double z, int color) {
		drawString(matrices, vertexConsumers, string, x, y, z, color, 0.02F);
	}

	public static void drawString(MatrixStack matrices, VertexConsumerProvider vertexConsumers, String string, double x, double y, double z, int color, float size) {
		drawString(matrices, vertexConsumers, string, x, y, z, color, size, true, 0.0F, false);
	}

	public static void drawString(
		MatrixStack matrices,
		VertexConsumerProvider vertexConsumers,
		String string,
		double x,
		double y,
		double z,
		int color,
		float size,
		boolean center,
		float offset,
		boolean visibleThroughObjects
	) {
		MinecraftClient minecraftClient = MinecraftClient.getInstance();
		Camera camera = minecraftClient.gameRenderer.getCamera();
		if (camera.isReady() && minecraftClient.getEntityRenderDispatcher().gameOptions != null) {
			TextRenderer textRenderer = minecraftClient.textRenderer;
			double d = camera.getPos().x;
			double e = camera.getPos().y;
			double f = camera.getPos().z;
			matrices.push();
			matrices.translate((float)(x - d), (float)(y - e) + 0.07F, (float)(z - f));
			matrices.multiplyPositionMatrix(new Matrix4f().rotation(camera.getRotation()));
			matrices.scale(-size, -size, size);
			float g = center ? (float)(-textRenderer.getWidth(string)) / 2.0F : 0.0F;
			g -= offset / size;
			textRenderer.draw(
				string,
				g,
				0.0F,
				color,
				false,
				matrices.peek().getPositionMatrix(),
				vertexConsumers,
				visibleThroughObjects ? TextRenderer.TextLayerType.SEE_THROUGH : TextRenderer.TextLayerType.NORMAL,
				0,
				15728880
			);
			matrices.pop();
		}
	}

	@Environment(EnvType.CLIENT)
	public interface Renderer {
		void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, double cameraX, double cameraY, double cameraZ);

		default void clear() {
		}
	}
}
