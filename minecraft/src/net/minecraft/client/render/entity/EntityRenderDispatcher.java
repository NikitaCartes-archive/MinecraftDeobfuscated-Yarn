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
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.ModelLoader;
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
import net.minecraft.util.Identifier;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.chunk.Chunk;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

@Environment(EnvType.CLIENT)
public class EntityRenderDispatcher implements SynchronousResourceReloader {
	private static final RenderLayer SHADOW_LAYER = RenderLayer.getEntityShadow(new Identifier("textures/misc/shadow.png"));
	private static final float field_43377 = 32.0F;
	private static final float field_43378 = 0.5F;
	private Map<EntityType<?>, EntityRenderer<?>> renderers = ImmutableMap.of();
	private Map<SkinTextures.Model, EntityRenderer<? extends PlayerEntity>> modelRenderers = Map.of();
	public final TextureManager textureManager;
	private World world;
	public Camera camera;
	private Quaternionf rotation;
	public Entity targetedEntity;
	private final ItemRenderer itemRenderer;
	private final BlockRenderManager blockRenderManager;
	private final HeldItemRenderer heldItemRenderer;
	private final TextRenderer textRenderer;
	public final GameOptions gameOptions;
	private final EntityModelLoader modelLoader;
	private boolean renderShadows = true;
	private boolean renderHitboxes;

	public <E extends Entity> int getLight(E entity, float tickDelta) {
		return this.getRenderer(entity).getLight(entity, tickDelta);
	}

	public EntityRenderDispatcher(
		MinecraftClient client,
		TextureManager textureManager,
		ItemRenderer itemRenderer,
		BlockRenderManager blockRenderManager,
		TextRenderer textRenderer,
		GameOptions gameOptions,
		EntityModelLoader modelLoader
	) {
		this.textureManager = textureManager;
		this.itemRenderer = itemRenderer;
		this.heldItemRenderer = new HeldItemRenderer(client, this, itemRenderer);
		this.blockRenderManager = blockRenderManager;
		this.textRenderer = textRenderer;
		this.gameOptions = gameOptions;
		this.modelLoader = modelLoader;
	}

	public <T extends Entity> EntityRenderer<? super T> getRenderer(T entity) {
		if (entity instanceof AbstractClientPlayerEntity abstractClientPlayerEntity) {
			SkinTextures.Model model = abstractClientPlayerEntity.getSkinTextures().model();
			EntityRenderer<? extends PlayerEntity> entityRenderer = (EntityRenderer<? extends PlayerEntity>)this.modelRenderers.get(model);
			return (EntityRenderer<? super T>)(entityRenderer != null ? entityRenderer : (EntityRenderer)this.modelRenderers.get(SkinTextures.Model.WIDE));
		} else {
			return (EntityRenderer<? super T>)this.renderers.get(entity.getType());
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
		EntityRenderer<? super E> entityRenderer = this.getRenderer(entity);
		return entityRenderer.shouldRender(entity, frustum, x, y, z);
	}

	public <E extends Entity> void render(
		E entity, double x, double y, double z, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light
	) {
		EntityRenderer<? super E> entityRenderer = this.getRenderer(entity);

		try {
			Vec3d vec3d = entityRenderer.getPositionOffset(entity, tickDelta);
			double d = x + vec3d.getX();
			double e = y + vec3d.getY();
			double f = z + vec3d.getZ();
			matrices.push();
			matrices.translate(d, e, f);
			entityRenderer.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
			if (entity.doesRenderOnFire()) {
				this.renderFire(matrices, vertexConsumers, entity, MathHelper.rotateAround(MathHelper.Y_AXIS, this.rotation, new Quaternionf()));
			}

			matrices.translate(-vec3d.getX(), -vec3d.getY(), -vec3d.getZ());
			if (this.gameOptions.getEntityShadows().getValue() && this.renderShadows && !entity.isInvisible()) {
				float g = entityRenderer.method_55831(entity);
				if (g > 0.0F) {
					double h = this.getSquaredDistanceToCamera(entity.getX(), entity.getY(), entity.getZ());
					float i = (float)((1.0 - h / 256.0) * (double)entityRenderer.shadowOpacity);
					if (i > 0.0F) {
						renderShadow(matrices, vertexConsumers, entity, i, tickDelta, this.world, Math.min(g, 32.0F));
					}
				}
			}

			if (this.renderHitboxes && !entity.isInvisible() && !MinecraftClient.getInstance().hasReducedDebugInfo()) {
				renderHitbox(matrices, vertexConsumers.getBuffer(RenderLayer.getLines()), entity, tickDelta);
			}

			matrices.pop();
		} catch (Throwable var25) {
			CrashReport crashReport = CrashReport.create(var25, "Rendering entity in world");
			CrashReportSection crashReportSection = crashReport.addElement("Entity being rendered");
			entity.populateCrashReport(crashReportSection);
			CrashReportSection crashReportSection2 = crashReport.addElement("Renderer details");
			crashReportSection2.add("Assigned renderer", entityRenderer);
			crashReportSection2.add("Location", CrashReportSection.createPositionString(this.world, x, y, z));
			crashReportSection2.add("Rotation", yaw);
			crashReportSection2.add("Delta", tickDelta);
			throw new CrashException(crashReport);
		}
	}

	private static void renderHitbox(MatrixStack matrices, VertexConsumer vertices, Entity entity, float tickDelta) {
		Box box = entity.getBoundingBox().offset(-entity.getX(), -entity.getY(), -entity.getZ());
		WorldRenderer.drawBox(matrices, vertices, box, 1.0F, 1.0F, 1.0F, 1.0F);
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
				WorldRenderer.drawBox(
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
			WorldRenderer.drawBox(
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
			WorldRenderer.drawBox(
				matrices, vertices, vec3d.x - (double)k, vec3d.y, vec3d.z - (double)k, vec3d.x + (double)k, vec3d.y + 0.0625, vec3d.z + (double)k, 1.0F, 1.0F, 0.0F, 1.0F
			);
		}

		Vec3d vec3d2 = entity.getRotationVec(tickDelta);
		Matrix4f matrix4f = matrices.peek().getPositionMatrix();
		Matrix3f matrix3f = matrices.peek().getNormalMatrix();
		vertices.vertex(matrix4f, 0.0F, entity.getStandingEyeHeight(), 0.0F)
			.color(0, 0, 255, 255)
			.normal(matrix3f, (float)vec3d2.x, (float)vec3d2.y, (float)vec3d2.z)
			.next();
		vertices.vertex(matrix4f, (float)(vec3d2.x * 2.0), (float)((double)entity.getStandingEyeHeight() + vec3d2.y * 2.0), (float)(vec3d2.z * 2.0))
			.color(0, 0, 255, 255)
			.normal(matrix3f, (float)vec3d2.x, (float)vec3d2.y, (float)vec3d2.z)
			.next();
	}

	private void renderFire(MatrixStack matrices, VertexConsumerProvider vertexConsumers, Entity entity, Quaternionf rotation) {
		Sprite sprite = ModelLoader.FIRE_0.getSprite();
		Sprite sprite2 = ModelLoader.FIRE_1.getSprite();
		matrices.push();
		float f = entity.getWidth() * 1.4F;
		matrices.scale(f, f, f);
		float g = 0.5F;
		float h = 0.0F;
		float i = entity.getHeight() / f;
		float j = 0.0F;
		matrices.multiply(rotation);
		matrices.translate(0.0F, 0.0F, -0.3F + (float)((int)i) * 0.02F);
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

			drawFireVertex(entry, vertexConsumer, g - 0.0F, 0.0F - j, k, o, p);
			drawFireVertex(entry, vertexConsumer, -g - 0.0F, 0.0F - j, k, m, p);
			drawFireVertex(entry, vertexConsumer, -g - 0.0F, 1.4F - j, k, m, n);
			drawFireVertex(entry, vertexConsumer, g - 0.0F, 1.4F - j, k, o, n);
			i -= 0.45F;
			j -= 0.45F;
			g *= 0.9F;
			k += 0.03F;
		}

		matrices.pop();
	}

	private static void drawFireVertex(MatrixStack.Entry entry, VertexConsumer vertices, float x, float y, float z, float u, float v) {
		vertices.vertex(entry.getPositionMatrix(), x, y, z)
			.color(255, 255, 255, 255)
			.texture(u, v)
			.overlay(0, 10)
			.light(LightmapTextureManager.MAX_BLOCK_LIGHT_COORDINATE)
			.normal(entry.getNormalMatrix(), 0.0F, 1.0F, 0.0F)
			.next();
	}

	private static void renderShadow(
		MatrixStack matrices, VertexConsumerProvider vertexConsumers, Entity entity, float opacity, float tickDelta, WorldView world, float radius
	) {
		double d = MathHelper.lerp((double)tickDelta, entity.lastRenderX, entity.getX());
		double e = MathHelper.lerp((double)tickDelta, entity.lastRenderY, entity.getY());
		double f = MathHelper.lerp((double)tickDelta, entity.lastRenderZ, entity.getZ());
		float g = Math.min(opacity / 0.5F, radius);
		int i = MathHelper.floor(d - (double)radius);
		int j = MathHelper.floor(d + (double)radius);
		int k = MathHelper.floor(e - (double)g);
		int l = MathHelper.floor(e);
		int m = MathHelper.floor(f - (double)radius);
		int n = MathHelper.floor(f + (double)radius);
		MatrixStack.Entry entry = matrices.peek();
		VertexConsumer vertexConsumer = vertexConsumers.getBuffer(SHADOW_LAYER);
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (int o = m; o <= n; o++) {
			for (int p = i; p <= j; p++) {
				mutable.set(p, 0, o);
				Chunk chunk = world.getChunk(mutable);

				for (int q = k; q <= l; q++) {
					mutable.setY(q);
					float h = opacity - (float)(e - (double)mutable.getY()) * 0.5F;
					renderShadowPart(entry, vertexConsumer, chunk, world, mutable, d, e, f, radius, h);
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

						Box box = voxelShape.getBoundingBox();
						double d = (double)pos.getX() + box.minX;
						double e = (double)pos.getX() + box.maxX;
						double h = (double)pos.getY() + box.minY;
						double i = (double)pos.getZ() + box.minZ;
						double j = (double)pos.getZ() + box.maxZ;
						float k = (float)(d - x);
						float l = (float)(e - x);
						float m = (float)(h - y);
						float n = (float)(i - z);
						float o = (float)(j - z);
						float p = -k / 2.0F / radius + 0.5F;
						float q = -l / 2.0F / radius + 0.5F;
						float r = -n / 2.0F / radius + 0.5F;
						float s = -o / 2.0F / radius + 0.5F;
						drawShadowVertex(entry, vertices, g, k, m, n, p, r);
						drawShadowVertex(entry, vertices, g, k, m, o, p, s);
						drawShadowVertex(entry, vertices, g, l, m, o, q, s);
						drawShadowVertex(entry, vertices, g, l, m, n, q, r);
					}
				}
			}
		}
	}

	private static void drawShadowVertex(MatrixStack.Entry entry, VertexConsumer vertices, float alpha, float x, float y, float z, float u, float v) {
		Vector3f vector3f = entry.getPositionMatrix().transformPosition(x, y, z, new Vector3f());
		vertices.vertex(
			vector3f.x(),
			vector3f.y(),
			vector3f.z(),
			1.0F,
			1.0F,
			1.0F,
			alpha,
			u,
			v,
			OverlayTexture.DEFAULT_UV,
			LightmapTextureManager.MAX_LIGHT_COORDINATE,
			0.0F,
			1.0F,
			0.0F
		);
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
			this, this.itemRenderer, this.blockRenderManager, this.heldItemRenderer, manager, this.modelLoader, this.textRenderer
		);
		this.renderers = EntityRenderers.reloadEntityRenderers(context);
		this.modelRenderers = EntityRenderers.reloadPlayerRenderers(context);
	}
}
