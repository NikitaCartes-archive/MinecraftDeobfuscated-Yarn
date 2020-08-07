package net.minecraft.client.render.entity;

import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.EnderDragonPart;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.resource.ReloadableResourceManager;
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
import net.minecraft.util.registry.Registry;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

@Environment(EnvType.CLIENT)
public class EntityRenderDispatcher {
	private static final RenderLayer SHADOW_LAYER = RenderLayer.getEntityShadow(new Identifier("textures/misc/shadow.png"));
	private final Map<EntityType<?>, EntityRenderer<?>> renderers = Maps.<EntityType<?>, EntityRenderer<?>>newHashMap();
	private final Map<String, PlayerEntityRenderer> modelRenderers = Maps.<String, PlayerEntityRenderer>newHashMap();
	private final PlayerEntityRenderer playerRenderer;
	private final TextRenderer textRenderer;
	public final TextureManager textureManager;
	private World world;
	public Camera camera;
	private Quaternion rotation;
	public Entity targetedEntity;
	public final GameOptions gameOptions;
	private boolean renderShadows = true;
	private boolean renderHitboxes;

	public <E extends Entity> int getLight(E entity, float tickDelta) {
		return this.getRenderer(entity).getLight(entity, tickDelta);
	}

	private <T extends Entity> void register(EntityType<T> entityType, EntityRenderer<? super T> entityRenderer) {
		this.renderers.put(entityType, entityRenderer);
	}

	private void registerRenderers(ItemRenderer itemRenderer, ReloadableResourceManager reloadableResourceManager) {
		this.register(EntityType.field_6083, new AreaEffectCloudEntityRenderer(this));
		this.register(EntityType.field_6131, new ArmorStandEntityRenderer(this));
		this.register(EntityType.field_6122, new ArrowEntityRenderer(this));
		this.register(EntityType.field_6108, new BatEntityRenderer(this));
		this.register(EntityType.field_20346, new BeeEntityRenderer(this));
		this.register(EntityType.field_6099, new BlazeEntityRenderer(this));
		this.register(EntityType.field_6121, new BoatEntityRenderer(this));
		this.register(EntityType.field_16281, new CatEntityRenderer(this));
		this.register(EntityType.field_6084, new CaveSpiderEntityRenderer(this));
		this.register(EntityType.field_6126, new MinecartEntityRenderer<>(this));
		this.register(EntityType.field_6132, new ChickenEntityRenderer(this));
		this.register(EntityType.field_6070, new CodEntityRenderer(this));
		this.register(EntityType.field_6136, new MinecartEntityRenderer<>(this));
		this.register(EntityType.field_6085, new CowEntityRenderer(this));
		this.register(EntityType.field_6046, new CreeperEntityRenderer(this));
		this.register(EntityType.field_6087, new DolphinEntityRenderer(this));
		this.register(EntityType.field_6067, new DonkeyEntityRenderer<>(this, 0.87F));
		this.register(EntityType.field_6129, new DragonFireballEntityRenderer(this));
		this.register(EntityType.field_6123, new DrownedEntityRenderer(this));
		this.register(EntityType.field_6144, new FlyingItemEntityRenderer<>(this, itemRenderer));
		this.register(EntityType.field_6086, new ElderGuardianEntityRenderer(this));
		this.register(EntityType.field_6110, new EndCrystalEntityRenderer(this));
		this.register(EntityType.field_6116, new EnderDragonEntityRenderer(this));
		this.register(EntityType.field_6091, new EndermanEntityRenderer(this));
		this.register(EntityType.field_6128, new EndermiteEntityRenderer(this));
		this.register(EntityType.field_6082, new FlyingItemEntityRenderer<>(this, itemRenderer));
		this.register(EntityType.field_6060, new EvokerFangsEntityRenderer(this));
		this.register(EntityType.field_6090, new EvokerEntityRenderer<>(this));
		this.register(EntityType.field_6064, new FlyingItemEntityRenderer<>(this, itemRenderer));
		this.register(EntityType.field_6044, new ExperienceOrbEntityRenderer(this));
		this.register(EntityType.field_6061, new FlyingItemEntityRenderer<>(this, itemRenderer, 1.0F, true));
		this.register(EntityType.field_6089, new FallingBlockEntityRenderer(this));
		this.register(EntityType.field_6066, new FlyingItemEntityRenderer<>(this, itemRenderer, 3.0F, true));
		this.register(EntityType.field_6133, new FireworkEntityRenderer(this, itemRenderer));
		this.register(EntityType.field_6103, new FishingBobberEntityRenderer(this));
		this.register(EntityType.field_17943, new FoxEntityRenderer(this));
		this.register(EntityType.field_6080, new MinecartEntityRenderer<>(this));
		this.register(EntityType.field_6107, new GhastEntityRenderer(this));
		this.register(EntityType.field_6095, new GiantEntityRenderer(this, 6.0F));
		this.register(EntityType.field_6118, new GuardianEntityRenderer(this));
		this.register(EntityType.field_21973, new HoglinEntityRenderer(this));
		this.register(EntityType.field_6058, new MinecartEntityRenderer<>(this));
		this.register(EntityType.field_6139, new HorseEntityRenderer(this));
		this.register(EntityType.field_6071, new HuskEntityRenderer(this));
		this.register(EntityType.field_6065, new IllusionerEntityRenderer(this));
		this.register(EntityType.field_6147, new IronGolemEntityRenderer(this));
		this.register(EntityType.field_6052, new ItemEntityRenderer(this, itemRenderer));
		this.register(EntityType.field_6043, new ItemFrameEntityRenderer(this, itemRenderer));
		this.register(EntityType.field_6138, new LeashKnotEntityRenderer(this));
		this.register(EntityType.field_6112, new LightningEntityRenderer(this));
		this.register(EntityType.field_6074, new LlamaEntityRenderer(this));
		this.register(EntityType.field_6124, new LlamaSpitEntityRenderer(this));
		this.register(EntityType.field_6102, new MagmaCubeEntityRenderer(this));
		this.register(EntityType.field_6096, new MinecartEntityRenderer<>(this));
		this.register(EntityType.field_6143, new MooshroomEntityRenderer(this));
		this.register(EntityType.field_6057, new DonkeyEntityRenderer<>(this, 0.92F));
		this.register(EntityType.field_6081, new OcelotEntityRenderer(this));
		this.register(EntityType.field_6120, new PaintingEntityRenderer(this));
		this.register(EntityType.field_6146, new PandaEntityRenderer(this));
		this.register(EntityType.field_6104, new ParrotEntityRenderer(this));
		this.register(EntityType.field_6078, new PhantomEntityRenderer(this));
		this.register(EntityType.field_6093, new PigEntityRenderer(this));
		this.register(EntityType.field_22281, new PiglinEntityRenderer(this, false));
		this.register(EntityType.field_25751, new PiglinEntityRenderer(this, false));
		this.register(EntityType.field_6105, new PillagerEntityRenderer(this));
		this.register(EntityType.field_6042, new PolarBearEntityRenderer(this));
		this.register(EntityType.field_6045, new FlyingItemEntityRenderer<>(this, itemRenderer));
		this.register(EntityType.field_6062, new PufferfishEntityRenderer(this));
		this.register(EntityType.field_6140, new RabbitEntityRenderer(this));
		this.register(EntityType.field_6134, new RavagerEntityRenderer(this));
		this.register(EntityType.field_6073, new SalmonEntityRenderer(this));
		this.register(EntityType.field_6115, new SheepEntityRenderer(this));
		this.register(EntityType.field_6100, new ShulkerBulletEntityRenderer(this));
		this.register(EntityType.field_6109, new ShulkerEntityRenderer(this));
		this.register(EntityType.field_6125, new SilverfishEntityRenderer(this));
		this.register(EntityType.field_6075, new ZombieHorseEntityRenderer(this));
		this.register(EntityType.field_6137, new SkeletonEntityRenderer(this));
		this.register(EntityType.field_6069, new SlimeEntityRenderer(this));
		this.register(EntityType.field_6049, new FlyingItemEntityRenderer<>(this, itemRenderer, 0.75F, true));
		this.register(EntityType.field_6068, new FlyingItemEntityRenderer<>(this, itemRenderer));
		this.register(EntityType.field_6047, new SnowGolemEntityRenderer(this));
		this.register(EntityType.field_6142, new MinecartEntityRenderer<>(this));
		this.register(EntityType.field_6135, new SpectralArrowEntityRenderer(this));
		this.register(EntityType.field_6079, new SpiderEntityRenderer<>(this));
		this.register(EntityType.field_6114, new SquidEntityRenderer(this));
		this.register(EntityType.field_6098, new StrayEntityRenderer(this));
		this.register(EntityType.field_6053, new TntMinecartEntityRenderer(this));
		this.register(EntityType.field_6063, new TntEntityRenderer(this));
		this.register(EntityType.field_17714, new LlamaEntityRenderer(this));
		this.register(EntityType.field_6127, new TridentEntityRenderer(this));
		this.register(EntityType.field_6111, new TropicalFishEntityRenderer(this));
		this.register(EntityType.field_6113, new TurtleEntityRenderer(this));
		this.register(EntityType.field_6059, new VexEntityRenderer(this));
		this.register(EntityType.field_6077, new VillagerEntityRenderer(this, reloadableResourceManager));
		this.register(EntityType.field_6117, new VindicatorEntityRenderer(this));
		this.register(EntityType.field_17713, new WanderingTraderEntityRenderer(this));
		this.register(EntityType.field_6145, new WitchEntityRenderer(this));
		this.register(EntityType.field_6119, new WitherEntityRenderer(this));
		this.register(EntityType.field_6076, new WitherSkeletonEntityRenderer(this));
		this.register(EntityType.field_6130, new WitherSkullEntityRenderer(this));
		this.register(EntityType.field_6055, new WolfEntityRenderer(this));
		this.register(EntityType.field_23696, new ZoglinEntityRenderer(this));
		this.register(EntityType.field_6048, new ZombieHorseEntityRenderer(this));
		this.register(EntityType.field_6051, new ZombieEntityRenderer(this));
		this.register(EntityType.field_6050, new PiglinEntityRenderer(this, true));
		this.register(EntityType.field_6054, new ZombieVillagerEntityRenderer(this, reloadableResourceManager));
		this.register(EntityType.field_23214, new StriderEntityRenderer(this));
	}

	public EntityRenderDispatcher(
		TextureManager textureManager,
		ItemRenderer itemRenderer,
		ReloadableResourceManager reloadableResourceManager,
		TextRenderer textRenderer,
		GameOptions gameOptions
	) {
		this.textureManager = textureManager;
		this.textRenderer = textRenderer;
		this.gameOptions = gameOptions;
		this.registerRenderers(itemRenderer, reloadableResourceManager);
		this.playerRenderer = new PlayerEntityRenderer(this);
		this.modelRenderers.put("default", this.playerRenderer);
		this.modelRenderers.put("slim", new PlayerEntityRenderer(this, true));

		for (EntityType<?> entityType : Registry.ENTITY_TYPE) {
			if (entityType != EntityType.field_6097 && !this.renderers.containsKey(entityType)) {
				throw new IllegalStateException("No renderer registered for " + Registry.ENTITY_TYPE.getId(entityType));
			}
		}
	}

	public <T extends Entity> EntityRenderer<? super T> getRenderer(T entity) {
		if (entity instanceof AbstractClientPlayerEntity) {
			String string = ((AbstractClientPlayerEntity)entity).getModel();
			PlayerEntityRenderer playerEntityRenderer = (PlayerEntityRenderer)this.modelRenderers.get(string);
			return playerEntityRenderer != null ? playerEntityRenderer : this.playerRenderer;
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
			crashReportSection2.add("Location", CrashReportSection.createPositionString(x, y, z));
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
		matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(-this.camera.getYaw()));
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
		BlockPos blockPos = pos.method_10074();
		BlockState blockState = world.getBlockState(blockPos);
		if (blockState.getRenderType() != BlockRenderType.field_11455 && world.getLightLevel(pos) > 3) {
			if (blockState.isFullCube(world, blockPos)) {
				VoxelShape voxelShape = blockState.getOutlineShape(world, pos.method_10074());
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

	public TextRenderer getTextRenderer() {
		return this.textRenderer;
	}
}
