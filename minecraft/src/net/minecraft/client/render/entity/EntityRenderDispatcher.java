package net.minecraft.client.render.entity;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.MapRenderer;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexRendering;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.render.entity.equipment.EquipmentModelLoader;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.ModelBaker;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.EnderDragonPart;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SynchronousResourceReloader;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.chunk.Chunk;
import org.joml.Quaternionf;
import org.joml.Vector3f;

@Environment(EnvType.CLIENT)
public class EntityRenderDispatcher implements SynchronousResourceReloader {
	private static final RenderLayer SHADOW_LAYER = RenderLayer.getEntityShadow(Identifier.ofVanilla("textures/misc/shadow.png"));
	private static final float field_43377 = 32.0F;
	private static final float field_43378 = 0.5F;
	private Map<EntityType<?>, EntityRenderer<?, ?>> renderers = ImmutableMap.of();
	private Map<SkinTextures.Model, EntityRenderer<? extends PlayerEntity, ?>> modelRenderers = Map.of();
	public final TextureManager textureManager;
	private World world;
	public Camera camera;
	private Quaternionf rotation;
	public Entity targetedEntity;
	private final ItemRenderer itemRenderer;
	private final MapRenderer mapRenderer;
	private final BlockRenderManager blockRenderManager;
	private final HeldItemRenderer heldItemRenderer;
	private final TextRenderer textRenderer;
	public final GameOptions gameOptions;
	private final EntityModelLoader modelLoader;
	private final EquipmentModelLoader equipmentModelLoader;
	private boolean renderShadows = true;
	private boolean renderHitboxes;

	public <E extends Entity> int getLight(E entity, float tickDelta) {
		return this.getRenderer(entity).getLight(entity, tickDelta);
	}

	public EntityRenderDispatcher(
		MinecraftClient client,
		TextureManager textureManager,
		ItemRenderer itemRenderer,
		MapRenderer mapRenderer,
		BlockRenderManager blockRenderManager,
		TextRenderer textRenderer,
		GameOptions gameOptions,
		EntityModelLoader modelLoader,
		EquipmentModelLoader equipmentModelLoader
	) {
		this.textureManager = textureManager;
		this.itemRenderer = itemRenderer;
		this.mapRenderer = mapRenderer;
		this.heldItemRenderer = new HeldItemRenderer(client, this, itemRenderer);
		this.blockRenderManager = blockRenderManager;
		this.textRenderer = textRenderer;
		this.gameOptions = gameOptions;
		this.modelLoader = modelLoader;
		this.equipmentModelLoader = equipmentModelLoader;
	}

	public <T extends Entity> EntityRenderer<? super T, ?> getRenderer(T entity) {
		if (entity instanceof AbstractClientPlayerEntity abstractClientPlayerEntity) {
			SkinTextures.Model model = abstractClientPlayerEntity.getSkinTextures().model();
			EntityRenderer<? extends PlayerEntity, ?> entityRenderer = (EntityRenderer<? extends PlayerEntity, ?>)this.modelRenderers.get(model);
			return (EntityRenderer<? super T, ?>)(entityRenderer != null ? entityRenderer : (EntityRenderer)this.modelRenderers.get(SkinTextures.Model.WIDE));
		} else {
			return (EntityRenderer<? super T, ?>)this.renderers.get(entity.getType());
		}
	}

	public void configure(World world, Camera camera, Entity target) {
		this.world = world;
		this.camera = camera;
		this.rotation = camera.getRotation();
		this.targetedEntity = target;
	}

	public void setRotation(Quaternionf rotation) {
		this.rotation = rotation;
	}

	public void setRenderShadows(boolean renderShadows) {
		this.renderShadows = renderShadows;
	}

	public void setRenderHitboxes(boolean renderHitboxes) {
		this.renderHitboxes = renderHitboxes;
	}

	public boolean shouldRenderHitboxes() {
		return this.renderHitboxes;
	}

	public <E extends Entity> boolean shouldRender(E entity, Frustum frustum, double x, double y, double z) {
		EntityRenderer<? super E, ?> entityRenderer = this.getRenderer(entity);
		return entityRenderer.shouldRender(entity, frustum, x, y, z);
	}

	public <E extends Entity> void render(
		E entity, double x, double y, double z, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light
	) {
		EntityRenderer<? super E, ?> entityRenderer = this.getRenderer(entity);
		this.render(entity, x, y, z, tickDelta, matrices, vertexConsumers, light, entityRenderer);
	}

	private <E extends Entity, S extends EntityRenderState> void render(
		E entity,
		double x,
		double y,
		double z,
		float tickDelta,
		MatrixStack matrices,
		VertexConsumerProvider vertexConsumers,
		int light,
		EntityRenderer<? super E, S> renderer
	) {
		try {
			S entityRenderState = renderer.getAndUpdateRenderState(entity, tickDelta);
			Vec3d vec3d = renderer.getPositionOffset(entityRenderState);
			double d = x + vec3d.getX();
			double e = y + vec3d.getY();
			double f = z + vec3d.getZ();
			matrices.push();
			matrices.translate(d, e, f);
			renderer.render(entityRenderState, matrices, vertexConsumers, light);
			if (entityRenderState.onFire) {
				this.renderFire(matrices, vertexConsumers, entityRenderState, MathHelper.rotateAround(MathHelper.Y_AXIS, this.rotation, new Quaternionf()));
			}

			if (entity instanceof PlayerEntity) {
				matrices.translate(-vec3d.getX(), -vec3d.getY(), -vec3d.getZ());
			}

			if (this.gameOptions.getEntityShadows().getValue() && this.renderShadows && !entityRenderState.invisible) {
				float g = renderer.getShadowRadius(entityRenderState);
				if (g > 0.0F) {
					double h = entityRenderState.squaredDistanceToCamera;
					float i = (float)((1.0 - h / 256.0) * (double)renderer.shadowOpacity);
					if (i > 0.0F) {
						renderShadow(matrices, vertexConsumers, entityRenderState, i, tickDelta, this.world, Math.min(g, 32.0F));
					}
				}
			}

			if (!(entity instanceof PlayerEntity)) {
				matrices.translate(-vec3d.getX(), -vec3d.getY(), -vec3d.getZ());
			}

			if (this.renderHitboxes && !entityRenderState.invisible && !MinecraftClient.getInstance().hasReducedDebugInfo()) {
				renderHitbox(matrices, vertexConsumers.getBuffer(RenderLayer.getLines()), entity, tickDelta, 1.0F, 1.0F, 1.0F);
			}

			matrices.pop();
		} catch (Throwable var25) {
			CrashReport crashReport = CrashReport.create(var25, "Rendering entity in world");
			CrashReportSection crashReportSection = crashReport.addElement("Entity being rendered");
			entity.populateCrashReport(crashReportSection);
			CrashReportSection crashReportSection2 = crashReport.addElement("Renderer details");
			crashReportSection2.add("Assigned renderer", renderer);
			crashReportSection2.add("Location", CrashReportSection.createPositionString(this.world, x, y, z));
			crashReportSection2.add("Delta", tickDelta);
			throw new CrashException(crashReport);
		}
	}

	private static void renderServerSideHitbox(MatrixStack matrices, Entity entity, VertexConsumerProvider vertexConsumers) {
		Entity entity2 = getIntegratedServerEntity(entity);
		if (entity2 == null) {
			DebugRenderer.drawString(matrices, vertexConsumers, "Missing", entity.getX(), entity.getBoundingBox().maxY + 1.5, entity.getZ(), Colors.RED);
		} else {
			matrices.push();
			matrices.translate(entity2.getX() - entity.getX(), entity2.getY() - entity.getY(), entity2.getZ() - entity.getZ());
			renderHitbox(matrices, vertexConsumers.getBuffer(RenderLayer.getLines()), entity2, 1.0F, 0.0F, 1.0F, 0.0F);
			VertexRendering.drawVector(matrices, vertexConsumers.getBuffer(RenderLayer.getLines()), new Vector3f(), entity2.getVelocity(), -256);
			matrices.pop();
		}
	}

	@Nullable
	private static Entity getIntegratedServerEntity(Entity entity) {
		IntegratedServer integratedServer = MinecraftClient.getInstance().getServer();
		if (integratedServer != null) {
			ServerWorld serverWorld = integratedServer.getWorld(entity.getWorld().getRegistryKey());
			if (serverWorld != null) {
				return serverWorld.getEntityById(entity.getId());
			}
		}

		return null;
	}

	private static void renderHitbox(MatrixStack matrices, VertexConsumer vertices, Entity entity, float tickDelta, float red, float green, float blue) {
		Box box = entity.getBoundingBox().offset(-entity.getX(), -entity.getY(), -entity.getZ());
		VertexRendering.drawBox(matrices, vertices, box, red, green, blue, 1.0F);
		if (entity instanceof EnderDragonEntity) {
			double d = -MathHelper.lerp((double)tickDelta, entity.lastRenderX, entity.getX());
			double e = -MathHelper.lerp((double)tickDelta, entity.lastRenderY, entity.getY());
			double f = -MathHelper.lerp((double)tickDelta, entity.lastRenderZ, entity.getZ());

			for (EnderDragonPart enderDragonPart : ((EnderDragonEntity)entity).getBodyParts()) {
				matrices.push();
				double g = d + MathHelper.lerp((double)tickDelta, enderDragonPart.lastRenderX, enderDragonPart.getX());
				double h = e + MathHelper.lerp((double)tickDelta, enderDragonPart.lastRenderY, enderDragonPart.getY());
				double i = f + MathHelper.lerp((double)tickDelta, enderDragonPart.lastRenderZ, enderDragonPart.getZ());
				matrices.translate(g, h, i);
				VertexRendering.drawBox(
					matrices,
					vertices,
					enderDragonPart.getBoundingBox().offset(-enderDragonPart.getX(), -enderDragonPart.getY(), -enderDragonPart.getZ()),
					0.25F,
					1.0F,
					0.0F,
					1.0F
				);
				matrices.pop();
			}
		}

		if (entity instanceof LivingEntity) {
			float j = 0.01F;
			VertexRendering.drawBox(
				matrices,
				vertices,
				box.minX,
				(double)(entity.getStandingEyeHeight() - 0.01F),
				box.minZ,
				box.maxX,
				(double)(entity.getStandingEyeHeight() + 0.01F),
				box.maxZ,
				1.0F,
				0.0F,
				0.0F,
				1.0F
			);
		}

		Entity entity2 = entity.getVehicle();
		if (entity2 != null) {
			float k = Math.min(entity2.getWidth(), entity.getWidth()) / 2.0F;
			float l = 0.0625F;
			Vec3d vec3d = entity2.getPassengerRidingPos(entity).subtract(entity.getPos());
			VertexRendering.drawBox(
				matrices, vertices, vec3d.x - (double)k, vec3d.y, vec3d.z - (double)k, vec3d.x + (double)k, vec3d.y + 0.0625, vec3d.z + (double)k, 1.0F, 1.0F, 0.0F, 1.0F
			);
		}

		VertexRendering.drawVector(
			matrices, vertices, new Vector3f(0.0F, entity.getStandingEyeHeight(), 0.0F), entity.getRotationVec(tickDelta).multiply(2.0), -16776961
		);
	}

	private void renderFire(MatrixStack matrices, VertexConsumerProvider vertexConsumers, EntityRenderState renderState, Quaternionf rotation) {
		Sprite sprite = ModelBaker.FIRE_0.getSprite();
		Sprite sprite2 = ModelBaker.FIRE_1.getSprite();
		matrices.push();
		float f = renderState.width * 1.4F;
		matrices.scale(f, f, f);
		float g = 0.5F;
		float h = 0.0F;
		float i = renderState.height / f;
		float j = 0.0F;
		matrices.multiply(rotation);
		matrices.translate(0.0F, 0.0F, 0.3F - (float)((int)i) * 0.02F);
		float k = 0.0F;
		int l = 0;
		VertexConsumer vertexConsumer = vertexConsumers.getBuffer(TexturedRenderLayers.getEntityCutout());

		for (MatrixStack.Entry entry = matrices.peek(); i > 0.0F; l++) {
			Sprite sprite3 = l % 2 == 0 ? sprite : sprite2;
			float m = sprite3.getMinU();
			float n = sprite3.getMinV();
			float o = sprite3.getMaxU();
			float p = sprite3.getMaxV();
			if (l / 2 % 2 == 0) {
				float q = o;
				o = m;
				m = q;
			}

			drawFireVertex(entry, vertexConsumer, -g - 0.0F, 0.0F - j, k, o, p);
			drawFireVertex(entry, vertexConsumer, g - 0.0F, 0.0F - j, k, m, p);
			drawFireVertex(entry, vertexConsumer, g - 0.0F, 1.4F - j, k, m, n);
			drawFireVertex(entry, vertexConsumer, -g - 0.0F, 1.4F - j, k, o, n);
			i -= 0.45F;
			j -= 0.45F;
			g *= 0.9F;
			k -= 0.03F;
		}

		matrices.pop();
	}

	private static void drawFireVertex(MatrixStack.Entry entry, VertexConsumer vertices, float x, float y, float z, float u, float v) {
		vertices.vertex(entry, x, y, z)
			.color(Colors.WHITE)
			.texture(u, v)
			.overlay(0, 10)
			.light(LightmapTextureManager.MAX_BLOCK_LIGHT_COORDINATE)
			.normal(entry, 0.0F, 1.0F, 0.0F);
	}

	private static void renderShadow(
		MatrixStack matrices, VertexConsumerProvider vertexConsumers, EntityRenderState renderState, float opacity, float tickDelta, WorldView world, float radius
	) {
		float f = Math.min(opacity / 0.5F, radius);
		int i = MathHelper.floor(renderState.x - (double)radius);
		int j = MathHelper.floor(renderState.x + (double)radius);
		int k = MathHelper.floor(renderState.y - (double)f);
		int l = MathHelper.floor(renderState.y);
		int m = MathHelper.floor(renderState.z - (double)radius);
		int n = MathHelper.floor(renderState.z + (double)radius);
		MatrixStack.Entry entry = matrices.peek();
		VertexConsumer vertexConsumer = vertexConsumers.getBuffer(SHADOW_LAYER);
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (int o = m; o <= n; o++) {
			for (int p = i; p <= j; p++) {
				mutable.set(p, 0, o);
				Chunk chunk = world.getChunk(mutable);

				for (int q = k; q <= l; q++) {
					mutable.setY(q);
					float g = opacity - (float)(renderState.y - (double)mutable.getY()) * 0.5F;
					renderShadowPart(entry, vertexConsumer, chunk, world, mutable, renderState.x, renderState.y, renderState.z, radius, g);
				}
			}
		}
	}

	private static void renderShadowPart(
		MatrixStack.Entry entry, VertexConsumer vertices, Chunk chunk, WorldView world, BlockPos pos, double x, double y, double z, float radius, float opacity
	) {
		BlockPos blockPos = pos.down();
		BlockState blockState = chunk.getBlockState(blockPos);
		if (blockState.getRenderType() != BlockRenderType.INVISIBLE && world.getLightLevel(pos) > 3) {
			if (blockState.isFullCube(chunk, blockPos)) {
				VoxelShape voxelShape = blockState.getOutlineShape(chunk, blockPos);
				if (!voxelShape.isEmpty()) {
					float f = LightmapTextureManager.getBrightness(world.getDimension(), world.getLightLevel(pos));
					float g = opacity * 0.5F * f;
					if (g >= 0.0F) {
						if (g > 1.0F) {
							g = 1.0F;
						}

						int i = ColorHelper.getArgb(MathHelper.floor(g * 255.0F), 255, 255, 255);
						Box box = voxelShape.getBoundingBox();
						double d = (double)pos.getX() + box.minX;
						double e = (double)pos.getX() + box.maxX;
						double h = (double)pos.getY() + box.minY;
						double j = (double)pos.getZ() + box.minZ;
						double k = (double)pos.getZ() + box.maxZ;
						float l = (float)(d - x);
						float m = (float)(e - x);
						float n = (float)(h - y);
						float o = (float)(j - z);
						float p = (float)(k - z);
						float q = -l / 2.0F / radius + 0.5F;
						float r = -m / 2.0F / radius + 0.5F;
						float s = -o / 2.0F / radius + 0.5F;
						float t = -p / 2.0F / radius + 0.5F;
						drawShadowVertex(entry, vertices, i, l, n, o, q, s);
						drawShadowVertex(entry, vertices, i, l, n, p, q, t);
						drawShadowVertex(entry, vertices, i, m, n, p, r, t);
						drawShadowVertex(entry, vertices, i, m, n, o, r, s);
					}
				}
			}
		}
	}

	private static void drawShadowVertex(MatrixStack.Entry entry, VertexConsumer vertices, int i, float x, float y, float z, float u, float v) {
		Vector3f vector3f = entry.getPositionMatrix().transformPosition(x, y, z, new Vector3f());
		vertices.vertex(vector3f.x(), vector3f.y(), vector3f.z(), i, u, v, OverlayTexture.DEFAULT_UV, 15728880, 0.0F, 1.0F, 0.0F);
	}

	public void setWorld(@Nullable World world) {
		this.world = world;
		if (world == null) {
			this.camera = null;
		}
	}

	public double getSquaredDistanceToCamera(Entity entity) {
		return this.camera.getPos().squaredDistanceTo(entity.getPos());
	}

	public double getSquaredDistanceToCamera(double x, double y, double z) {
		return this.camera.getPos().squaredDistanceTo(x, y, z);
	}

	public Quaternionf getRotation() {
		return this.rotation;
	}

	public HeldItemRenderer getHeldItemRenderer() {
		return this.heldItemRenderer;
	}

	@Override
	public void reload(ResourceManager manager) {
		EntityRendererFactory.Context context = new EntityRendererFactory.Context(
			this, this.itemRenderer, this.mapRenderer, this.blockRenderManager, manager, this.modelLoader, this.equipmentModelLoader, this.textRenderer
		);
		this.renderers = EntityRenderers.reloadEntityRenderers(context);
		this.modelRenderers = EntityRenderers.reloadPlayerRenderers(context);
	}
}
