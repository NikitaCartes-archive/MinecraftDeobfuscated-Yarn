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
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.EnderDragonPart;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SynchronousResourceReloadListener;
import net.minecraft.util.Identifier;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

@Environment(EnvType.CLIENT)
public class EntityRenderDispatcher implements SynchronousResourceReloadListener {
	private static final RenderLayer SHADOW_LAYER = RenderLayer.getEntityShadow(new Identifier("textures/misc/shadow.png"));
	private Map<EntityType<?>, EntityRenderer<?>> renderers = ImmutableMap.of();
	private Map<String, EntityRenderer<? extends PlayerEntity>> modelRenderers = ImmutableMap.of();
	public final TextureManager textureManager;
	private World world;
	public Camera camera;
	private Quaternion rotation;
	public Entity targetedEntity;
	private final ItemRenderer itemRenderer;
	private final TextRenderer textRenderer;
	public final GameOptions gameOptions;
	private final EntityModelLoader modelLoader;
	private boolean renderShadows = true;
	private boolean renderHitboxes;

	public <E extends Entity> int getLight(E entity, float tickDelta) {
		return this.getRenderer(entity).getLight(entity, tickDelta);
	}

	public EntityRenderDispatcher(
		TextureManager textureManager, ItemRenderer itemRenderer, TextRenderer textRenderer, GameOptions gameOptions, EntityModelLoader modelLoader
	) {
		this.textureManager = textureManager;
		this.itemRenderer = itemRenderer;
		this.textRenderer = textRenderer;
		this.gameOptions = gameOptions;
		this.modelLoader = modelLoader;
	}

	public <T extends Entity> EntityRenderer<? super T> getRenderer(T entity) {
		if (entity instanceof AbstractClientPlayerEntity) {
			String string = ((AbstractClientPlayerEntity)entity).getModel();
			EntityRenderer<? extends PlayerEntity> entityRenderer = (EntityRenderer<? extends PlayerEntity>)this.modelRenderers.get(string);
			return (EntityRenderer<? super T>)(entityRenderer != null ? entityRenderer : (EntityRenderer)this.modelRenderers.get("default"));
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

	public void setRotation(Quaternion rotation) {
		this.rotation = rotation;
	}

	public void setRenderShadows(boolean value) {
		this.renderShadows = value;
	}

	public void setRenderHitboxes(boolean value) {
		this.renderHitboxes = value;
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
				this.renderFire(matrices, vertexConsumers, entity);
			}

			matrices.translate(-vec3d.getX(), -vec3d.getY(), -vec3d.getZ());
			if (this.gameOptions.entityShadows && this.renderShadows && entityRenderer.shadowRadius > 0.0F && !entity.isInvisible()) {
				double g = this.getSquaredDistanceToCamera(entity.getX(), entity.getY(), entity.getZ());
				float h = (float)((1.0 - g / 256.0) * (double)entityRenderer.shadowOpacity);
				if (h > 0.0F) {
					renderShadow(matrices, vertexConsumers, entity, h, tickDelta, this.world, entityRenderer.shadowRadius);
				}
			}

			if (this.renderHitboxes && !entity.isInvisible() && !MinecraftClient.getInstance().hasReducedDebugInfo()) {
				this.renderHitbox(matrices, vertexConsumers.getBuffer(RenderLayer.getLines()), entity, tickDelta);
			}

			matrices.pop();
		} catch (Throwable var24) {
			CrashReport crashReport = CrashReport.create(var24, "Rendering entity in world");
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

	private void renderHitbox(MatrixStack matrices, VertexConsumer vertices, Entity entity, float tickDelta) {
		float f = entity.getWidth() / 2.0F;
		this.drawBox(matrices, vertices, entity, 1.0F, 1.0F, 1.0F);
		if (entity instanceof EnderDragonEntity) {
			double d = -MathHelper.lerp((double)tickDelta, entity.lastRenderX, entity.getX());
			double e = -MathHelper.lerp((double)tickDelta, entity.lastRenderY, entity.getY());
			double g = -MathHelper.lerp((double)tickDelta, entity.lastRenderZ, entity.getZ());

			for (EnderDragonPart enderDragonPart : ((EnderDragonEntity)entity).getBodyParts()) {
				matrices.push();
				double h = d + MathHelper.lerp((double)tickDelta, enderDragonPart.lastRenderX, enderDragonPart.getX());
				double i = e + MathHelper.lerp((double)tickDelta, enderDragonPart.lastRenderY, enderDragonPart.getY());
				double j = g + MathHelper.lerp((double)tickDelta, enderDragonPart.lastRenderZ, enderDragonPart.getZ());
				matrices.translate(h, i, j);
				this.drawBox(matrices, vertices, enderDragonPart, 0.25F, 1.0F, 0.0F);
				matrices.pop();
			}
		}

		if (entity instanceof LivingEntity) {
			float k = 0.01F;
			WorldRenderer.drawBox(
				matrices,
				vertices,
				(double)(-f),
				(double)(entity.getStandingEyeHeight() - 0.01F),
				(double)(-f),
				(double)f,
				(double)(entity.getStandingEyeHeight() + 0.01F),
				(double)f,
				1.0F,
				0.0F,
				0.0F,
				1.0F
			);
		}

		Vec3d vec3d = entity.getRotationVec(tickDelta);
		Matrix4f matrix4f = matrices.peek().getModel();
		vertices.vertex(matrix4f, 0.0F, entity.getStandingEyeHeight(), 0.0F).color(0, 0, 255, 255).next();
		vertices.vertex(matrix4f, (float)(vec3d.x * 2.0), (float)((double)entity.getStandingEyeHeight() + vec3d.y * 2.0), (float)(vec3d.z * 2.0))
			.color(0, 0, 255, 255)
			.next();
	}

	private void drawBox(MatrixStack matrix, VertexConsumer vertices, Entity entity, float red, float green, float blue) {
		Box box = entity.getBoundingBox().offset(-entity.getX(), -entity.getY(), -entity.getZ());
		WorldRenderer.drawBox(matrix, vertices, box, red, green, blue, 1.0F);
	}

	private void renderFire(MatrixStack matrices, VertexConsumerProvider vertexConsumers, Entity entity) {
		Sprite sprite = ModelLoader.FIRE_0.getSprite();
		Sprite sprite2 = ModelLoader.FIRE_1.getSprite();
		matrices.push();
		float f = entity.getWidth() * 1.4F;
		matrices.scale(f, f, f);
		float g = 0.5F;
		float h = 0.0F;
		float i = entity.getHeight() / f;
		float j = 0.0F;
		matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-this.camera.getYaw()));
		matrices.translate(0.0, 0.0, (double)(-0.3F + (float)((int)i) * 0.02F));
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
		vertices.vertex(entry.getModel(), x, y, z)
			.color(255, 255, 255, 255)
			.texture(u, v)
			.overlay(0, 10)
			.light(240)
			.normal(entry.getNormal(), 0.0F, 1.0F, 0.0F)
			.next();
	}

	private static void renderShadow(
		MatrixStack matrices, VertexConsumerProvider vertexConsumers, Entity entity, float opacity, float tickDelta, WorldView world, float radius
	) {
		float f = radius;
		if (entity instanceof MobEntity) {
			MobEntity mobEntity = (MobEntity)entity;
			if (mobEntity.isBaby()) {
				f = radius * 0.5F;
			}
		}

		double d = MathHelper.lerp((double)tickDelta, entity.lastRenderX, entity.getX());
		double e = MathHelper.lerp((double)tickDelta, entity.lastRenderY, entity.getY());
		double g = MathHelper.lerp((double)tickDelta, entity.lastRenderZ, entity.getZ());
		int i = MathHelper.floor(d - (double)f);
		int j = MathHelper.floor(d + (double)f);
		int k = MathHelper.floor(e - (double)f);
		int l = MathHelper.floor(e);
		int m = MathHelper.floor(g - (double)f);
		int n = MathHelper.floor(g + (double)f);
		MatrixStack.Entry entry = matrices.peek();
		VertexConsumer vertexConsumer = vertexConsumers.getBuffer(SHADOW_LAYER);

		for (BlockPos blockPos : BlockPos.iterate(new BlockPos(i, k, m), new BlockPos(j, l, n))) {
			renderShadowPart(entry, vertexConsumer, world, blockPos, d, e, g, f, opacity);
		}
	}

	private static void renderShadowPart(
		MatrixStack.Entry entry, VertexConsumer vertices, WorldView world, BlockPos pos, double x, double y, double z, float radius, float opacity
	) {
		BlockPos blockPos = pos.down();
		BlockState blockState = world.getBlockState(blockPos);
		if (blockState.getRenderType() != BlockRenderType.INVISIBLE && world.getLightLevel(pos) > 3) {
			if (blockState.isFullCube(world, blockPos)) {
				VoxelShape voxelShape = blockState.getOutlineShape(world, pos.down());
				if (!voxelShape.isEmpty()) {
					float f = (float)(((double)opacity - (y - (double)pos.getY()) / 2.0) * 0.5 * (double)world.getBrightness(pos));
					if (f >= 0.0F) {
						if (f > 1.0F) {
							f = 1.0F;
						}

						Box box = voxelShape.getBoundingBox();
						double d = (double)pos.getX() + box.minX;
						double e = (double)pos.getX() + box.maxX;
						double g = (double)pos.getY() + box.minY;
						double h = (double)pos.getZ() + box.minZ;
						double i = (double)pos.getZ() + box.maxZ;
						float j = (float)(d - x);
						float k = (float)(e - x);
						float l = (float)(g - y);
						float m = (float)(h - z);
						float n = (float)(i - z);
						float o = -j / 2.0F / radius + 0.5F;
						float p = -k / 2.0F / radius + 0.5F;
						float q = -m / 2.0F / radius + 0.5F;
						float r = -n / 2.0F / radius + 0.5F;
						drawShadowVertex(entry, vertices, f, j, l, m, o, q);
						drawShadowVertex(entry, vertices, f, j, l, n, o, r);
						drawShadowVertex(entry, vertices, f, k, l, n, p, r);
						drawShadowVertex(entry, vertices, f, k, l, m, p, q);
					}
				}
			}
		}
	}

	private static void drawShadowVertex(MatrixStack.Entry entry, VertexConsumer vertices, float alpha, float x, float y, float z, float u, float v) {
		vertices.vertex(entry.getModel(), x, y, z)
			.color(1.0F, 1.0F, 1.0F, alpha)
			.texture(u, v)
			.overlay(OverlayTexture.DEFAULT_UV)
			.light(15728880)
			.normal(entry.getNormal(), 0.0F, 1.0F, 0.0F)
			.next();
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

	public Quaternion getRotation() {
		return this.rotation;
	}

	@Override
	public void apply(ResourceManager manager) {
		EntityRendererFactory.Context context = new EntityRendererFactory.Context(this, this.itemRenderer, manager, this.modelLoader, this.textRenderer);
		this.renderers = EntityRenderers.reloadEntityRenderers(context);
		this.modelRenderers = EntityRenderers.reloadPlayerRenderers(context);
	}
}
