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
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.Matrix4f;
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
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

@Environment(EnvType.CLIENT)
public class EntityRenderDispatcher {
	private static final Identifier field_21009 = new Identifier("textures/misc/shadow.png");
	private final Map<EntityType<?>, EntityRenderer<?>> renderers = Maps.<EntityType<?>, EntityRenderer<?>>newHashMap();
	private final Map<String, PlayerEntityRenderer> modelRenderers = Maps.<String, PlayerEntityRenderer>newHashMap();
	private final PlayerEntityRenderer playerRenderer;
	private final TextRenderer textRenderer;
	public final TextureManager textureManager;
	private World world;
	public Camera camera;
	public Entity targetedEntity;
	public final GameOptions gameOptions;
	private boolean renderShadows = true;
	private boolean renderHitboxes;

	public static int method_23839(Entity entity) {
		return LightmapTextureManager.method_23687(entity.getLightmapCoordinates(), entity.world.getLightLevel(LightType.SKY, new BlockPos(entity)));
	}

	private <T extends Entity> void register(EntityType<T> entityType, EntityRenderer<? super T> entityRenderer) {
		this.renderers.put(entityType, entityRenderer);
	}

	private void registerRenderers(ItemRenderer itemRenderer, ReloadableResourceManager reloadableResourceManager) {
		this.register(EntityType.AREA_EFFECT_CLOUD, new AreaEffectCloudEntityRenderer(this));
		this.register(EntityType.ARMOR_STAND, new ArmorStandEntityRenderer(this));
		this.register(EntityType.ARROW, new ArrowEntityRenderer(this));
		this.register(EntityType.BAT, new BatEntityRenderer(this));
		this.register(EntityType.BEE, new BeeEntityRenderer(this));
		this.register(EntityType.BLAZE, new BlazeEntityRenderer(this));
		this.register(EntityType.BOAT, new BoatEntityRenderer(this));
		this.register(EntityType.CAT, new CatEntityRenderer(this));
		this.register(EntityType.CAVE_SPIDER, new CaveSpiderEntityRenderer(this));
		this.register(EntityType.CHEST_MINECART, new MinecartEntityRenderer<>(this));
		this.register(EntityType.CHICKEN, new ChickenEntityRenderer(this));
		this.register(EntityType.COD, new CodEntityRenderer(this));
		this.register(EntityType.COMMAND_BLOCK_MINECART, new MinecartEntityRenderer<>(this));
		this.register(EntityType.COW, new CowEntityRenderer(this));
		this.register(EntityType.CREEPER, new CreeperEntityRenderer(this));
		this.register(EntityType.DOLPHIN, new DolphinEntityRenderer(this));
		this.register(EntityType.DONKEY, new DonkeyEntityRenderer<>(this, 0.87F));
		this.register(EntityType.DRAGON_FIREBALL, new DragonFireballEntityRenderer(this));
		this.register(EntityType.DROWNED, new DrownedEntityRenderer(this));
		this.register(EntityType.EGG, new FlyingItemEntityRenderer<>(this, itemRenderer));
		this.register(EntityType.ELDER_GUARDIAN, new ElderGuardianEntityRenderer(this));
		this.register(EntityType.END_CRYSTAL, new EnderCrystalEntityRenderer(this));
		this.register(EntityType.ENDER_DRAGON, new EnderDragonEntityRenderer(this));
		this.register(EntityType.ENDERMAN, new EndermanEntityRenderer(this));
		this.register(EntityType.ENDERMITE, new EndermiteEntityRenderer(this));
		this.register(EntityType.ENDER_PEARL, new FlyingItemEntityRenderer<>(this, itemRenderer));
		this.register(EntityType.EVOKER_FANGS, new EvokerFangsEntityRenderer(this));
		this.register(EntityType.EVOKER, new EvokerIllagerEntityRenderer<>(this));
		this.register(EntityType.EXPERIENCE_BOTTLE, new FlyingItemEntityRenderer<>(this, itemRenderer));
		this.register(EntityType.EXPERIENCE_ORB, new ExperienceOrbEntityRenderer(this));
		this.register(EntityType.EYE_OF_ENDER, new FlyingItemEntityRenderer<>(this, itemRenderer));
		this.register(EntityType.FALLING_BLOCK, new FallingBlockEntityRenderer(this));
		this.register(EntityType.FIREBALL, new FlyingItemEntityRenderer<>(this, itemRenderer, 3.0F));
		this.register(EntityType.FIREWORK_ROCKET, new FireworkEntityRenderer(this, itemRenderer));
		this.register(EntityType.FISHING_BOBBER, new FishingBobberEntityRenderer(this));
		this.register(EntityType.FOX, new FoxEntityRenderer(this));
		this.register(EntityType.FURNACE_MINECART, new MinecartEntityRenderer<>(this));
		this.register(EntityType.GHAST, new GhastEntityRenderer(this));
		this.register(EntityType.GIANT, new GiantEntityRenderer(this, 6.0F));
		this.register(EntityType.GUARDIAN, new GuardianEntityRenderer(this));
		this.register(EntityType.HOPPER_MINECART, new MinecartEntityRenderer<>(this));
		this.register(EntityType.HORSE, new HorseEntityRenderer(this));
		this.register(EntityType.HUSK, new HuskEntityRenderer(this));
		this.register(EntityType.ILLUSIONER, new IllusionerEntityRenderer(this));
		this.register(EntityType.IRON_GOLEM, new IronGolemEntityRenderer(this));
		this.register(EntityType.ITEM_FRAME, new ItemFrameEntityRenderer(this, itemRenderer));
		this.register(EntityType.ITEM, new ItemEntityRenderer(this, itemRenderer));
		this.register(EntityType.LEASH_KNOT, new LeashKnotEntityRenderer(this));
		this.register(EntityType.LIGHTNING_BOLT, new LightningEntityRenderer(this));
		this.register(EntityType.LLAMA, new LlamaEntityRenderer(this));
		this.register(EntityType.LLAMA_SPIT, new LlamaSpitEntityRenderer(this));
		this.register(EntityType.MAGMA_CUBE, new MagmaCubeEntityRenderer(this));
		this.register(EntityType.MINECART, new MinecartEntityRenderer<>(this));
		this.register(EntityType.MOOSHROOM, new MooshroomEntityRenderer(this));
		this.register(EntityType.MULE, new DonkeyEntityRenderer<>(this, 0.92F));
		this.register(EntityType.OCELOT, new OcelotEntityRenderer(this));
		this.register(EntityType.PAINTING, new PaintingEntityRenderer(this));
		this.register(EntityType.PANDA, new PandaEntityRenderer(this));
		this.register(EntityType.PARROT, new ParrotEntityRenderer(this));
		this.register(EntityType.PHANTOM, new PhantomEntityRenderer(this));
		this.register(EntityType.PIG, new PigEntityRenderer(this));
		this.register(EntityType.PILLAGER, new PillagerEntityRenderer(this));
		this.register(EntityType.POLAR_BEAR, new PolarBearEntityRenderer(this));
		this.register(EntityType.POTION, new FlyingItemEntityRenderer<>(this, itemRenderer));
		this.register(EntityType.PUFFERFISH, new PufferfishEntityRenderer(this));
		this.register(EntityType.RABBIT, new RabbitEntityRenderer(this));
		this.register(EntityType.RAVAGER, new RavagerEntityRenderer(this));
		this.register(EntityType.SALMON, new SalmonEntityRenderer(this));
		this.register(EntityType.SHEEP, new SheepEntityRenderer(this));
		this.register(EntityType.SHULKER_BULLET, new ShulkerBulletEntityRenderer(this));
		this.register(EntityType.SHULKER, new ShulkerEntityRenderer(this));
		this.register(EntityType.SILVERFISH, new SilverfishEntityRenderer(this));
		this.register(EntityType.SKELETON_HORSE, new ZombieHorseEntityRenderer(this));
		this.register(EntityType.SKELETON, new SkeletonEntityRenderer(this));
		this.register(EntityType.SLIME, new SlimeEntityRenderer(this));
		this.register(EntityType.SMALL_FIREBALL, new FlyingItemEntityRenderer<>(this, itemRenderer, 0.75F));
		this.register(EntityType.SNOWBALL, new FlyingItemEntityRenderer<>(this, itemRenderer));
		this.register(EntityType.SNOW_GOLEM, new SnowGolemEntityRenderer(this));
		this.register(EntityType.SPAWNER_MINECART, new MinecartEntityRenderer<>(this));
		this.register(EntityType.SPECTRAL_ARROW, new SpectralArrowEntityRenderer(this));
		this.register(EntityType.SPIDER, new SpiderEntityRenderer<>(this));
		this.register(EntityType.SQUID, new SquidEntityRenderer(this));
		this.register(EntityType.STRAY, new StrayEntityRenderer(this));
		this.register(EntityType.TNT_MINECART, new TntMinecartEntityRenderer(this));
		this.register(EntityType.TNT, new TntEntityRenderer(this));
		this.register(EntityType.TRADER_LLAMA, new LlamaEntityRenderer(this));
		this.register(EntityType.TRIDENT, new TridentEntityRenderer(this));
		this.register(EntityType.TROPICAL_FISH, new TropicalFishEntityRenderer(this));
		this.register(EntityType.TURTLE, new TurtleEntityRenderer(this));
		this.register(EntityType.VEX, new VexEntityRenderer(this));
		this.register(EntityType.VILLAGER, new VillagerEntityRenderer(this, reloadableResourceManager));
		this.register(EntityType.VINDICATOR, new VindicatorEntityRenderer(this));
		this.register(EntityType.WANDERING_TRADER, new WanderingTraderEntityRenderer(this));
		this.register(EntityType.WITCH, new WitchEntityRenderer(this));
		this.register(EntityType.WITHER, new WitherEntityRenderer(this));
		this.register(EntityType.WITHER_SKELETON, new WitherSkeletonEntityRenderer(this));
		this.register(EntityType.WITHER_SKULL, new WitherSkullEntityRenderer(this));
		this.register(EntityType.WOLF, new WolfEntityRenderer(this));
		this.register(EntityType.ZOMBIE_HORSE, new ZombieHorseEntityRenderer(this));
		this.register(EntityType.ZOMBIE, new ZombieEntityRenderer(this));
		this.register(EntityType.ZOMBIE_PIGMAN, new ZombiePigmanEntityRenderer(this));
		this.register(EntityType.ZOMBIE_VILLAGER, new ZombieVillagerEntityRenderer(this, reloadableResourceManager));
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
			if (entityType != EntityType.PLAYER && !this.renderers.containsKey(entityType)) {
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

	public void configure(World world, Camera camera, Entity entity) {
		this.world = world;
		this.camera = camera;
		this.targetedEntity = entity;
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

	public <E extends Entity> boolean shouldRender(E entity, Frustum frustum, double d, double e, double f) {
		EntityRenderer<? super E> entityRenderer = this.getRenderer(entity);
		return entityRenderer.isVisible(entity, frustum, d, e, f);
	}

	public <E extends Entity> void render(
		E entity, double x, double y, double z, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumerProvider, int i
	) {
		EntityRenderer<? super E> entityRenderer = this.getRenderer(entity);

		try {
			Vec3d vec3d = entityRenderer.getPositionOffset(entity, tickDelta);
			double d = x + vec3d.getX();
			double e = y + vec3d.getY();
			double f = z + vec3d.getZ();
			matrices.push();
			matrices.translate(d, e, f);
			entityRenderer.render(entity, yaw, tickDelta, matrices, vertexConsumerProvider, i);
			if (entity.doesRenderOnFire()) {
				this.renderFire(matrices, vertexConsumerProvider, entity);
			}

			matrices.translate(-vec3d.getX(), -vec3d.getY(), -vec3d.getZ());
			if (this.gameOptions.entityShadows && this.renderShadows && entityRenderer.field_4673 > 0.0F && !entity.isInvisible()) {
				double g = this.getSquaredDistanceToCamera(entity.getX(), entity.getY(), entity.getZ());
				float h = (float)((1.0 - g / 256.0) * (double)entityRenderer.field_4672);
				if (h > 0.0F) {
					method_23166(matrices, vertexConsumerProvider, entity, h, tickDelta, this.world, entityRenderer.field_4673);
				}
			}

			if (this.renderHitboxes && !entity.isInvisible() && !MinecraftClient.getInstance().hasReducedDebugInfo()) {
				this.renderHitbox(matrices, vertexConsumerProvider.getBuffer(RenderLayer.getLines()), entity, tickDelta);
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

	private void renderHitbox(MatrixStack matrixStack, VertexConsumer vertexConsumer, Entity entity, float f) {
		float g = entity.getWidth() / 2.0F;
		this.method_23164(matrixStack, vertexConsumer, entity, 1.0F, 1.0F, 1.0F);
		if (entity instanceof EnderDragonEntity) {
			double d = entity.getX() - MathHelper.lerp((double)f, entity.prevRenderX, entity.getX());
			double e = entity.getY() - MathHelper.lerp((double)f, entity.prevRenderY, entity.getY());
			double h = entity.getZ() - MathHelper.lerp((double)f, entity.prevRenderZ, entity.getZ());

			for (EnderDragonPart enderDragonPart : ((EnderDragonEntity)entity).method_5690()) {
				matrixStack.push();
				double i = d + MathHelper.lerp((double)f, enderDragonPart.prevRenderX, enderDragonPart.getX());
				double j = e + MathHelper.lerp((double)f, enderDragonPart.prevRenderY, enderDragonPart.getY());
				double k = h + MathHelper.lerp((double)f, enderDragonPart.prevRenderZ, enderDragonPart.getZ());
				matrixStack.translate(i, j, k);
				this.method_23164(matrixStack, vertexConsumer, enderDragonPart, 0.25F, 1.0F, 0.0F);
				matrixStack.pop();
			}
		}

		if (entity instanceof LivingEntity) {
			float l = 0.01F;
			WorldRenderer.drawBox(
				matrixStack,
				vertexConsumer,
				(double)(-g),
				(double)(entity.getStandingEyeHeight() - 0.01F),
				(double)(-g),
				(double)g,
				(double)(entity.getStandingEyeHeight() + 0.01F),
				(double)g,
				1.0F,
				0.0F,
				0.0F,
				1.0F
			);
		}

		Vec3d vec3d = entity.getRotationVec(f);
		Matrix4f matrix4f = matrixStack.peek().getModel();
		vertexConsumer.vertex(matrix4f, 0.0F, entity.getStandingEyeHeight(), 0.0F).color(0, 0, 255, 255).next();
		vertexConsumer.vertex(matrix4f, (float)(vec3d.x * 2.0), (float)((double)entity.getStandingEyeHeight() + vec3d.y * 2.0), (float)(vec3d.z * 2.0))
			.color(0, 0, 255, 255)
			.next();
	}

	private void method_23164(MatrixStack matrixStack, VertexConsumer vertexConsumer, Entity entity, float f, float g, float h) {
		Box box = entity.getBoundingBox().offset(-entity.getX(), -entity.getY(), -entity.getZ());
		WorldRenderer.drawBox(matrixStack, vertexConsumer, box, f, g, h, 1.0F);
	}

	private void renderFire(MatrixStack matrix, VertexConsumerProvider vertexConsumerProvider, Entity entity) {
		SpriteAtlasTexture spriteAtlasTexture = MinecraftClient.getInstance().getSpriteAtlas();
		Sprite sprite = spriteAtlasTexture.getSprite(ModelLoader.FIRE_0);
		Sprite sprite2 = spriteAtlasTexture.getSprite(ModelLoader.FIRE_1);
		matrix.push();
		float f = entity.getWidth() * 1.4F;
		matrix.scale(f, f, f);
		float g = 0.5F;
		float h = 0.0F;
		float i = entity.getHeight() / f;
		float j = 0.0F;
		matrix.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(-this.camera.getYaw()));
		matrix.translate(0.0, 0.0, (double)(-0.3F + (float)((int)i) * 0.02F));
		float k = 0.0F;
		int l = 0;
		VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.method_23947());

		for (MatrixStack.Entry entry = matrix.peek(); i > 0.0F; l++) {
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

			method_23161(entry, vertexConsumer, g - 0.0F, 0.0F - j, k, o, p);
			method_23161(entry, vertexConsumer, -g - 0.0F, 0.0F - j, k, m, p);
			method_23161(entry, vertexConsumer, -g - 0.0F, 1.4F - j, k, m, n);
			method_23161(entry, vertexConsumer, g - 0.0F, 1.4F - j, k, o, n);
			i -= 0.45F;
			j -= 0.45F;
			g *= 0.9F;
			k += 0.03F;
		}

		matrix.pop();
	}

	private static void method_23161(MatrixStack.Entry entry, VertexConsumer vertexConsumer, float f, float g, float h, float i, float j) {
		vertexConsumer.vertex(entry.getModel(), f, g, h)
			.color(255, 255, 255, 255)
			.texture(i, j)
			.overlay(0, 10)
			.light(240)
			.method_23763(entry.getNormal(), 0.0F, 1.0F, 0.0F)
			.next();
	}

	private static void method_23166(
		MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, Entity entity, float f, float g, WorldView worldView, float h
	) {
		float i = h;
		if (entity instanceof MobEntity) {
			MobEntity mobEntity = (MobEntity)entity;
			if (mobEntity.isBaby()) {
				i = h * 0.5F;
			}
		}

		double d = MathHelper.lerp((double)g, entity.prevRenderX, entity.getX());
		double e = MathHelper.lerp((double)g, entity.prevRenderY, entity.getY());
		double j = MathHelper.lerp((double)g, entity.prevRenderZ, entity.getZ());
		int k = MathHelper.floor(d - (double)i);
		int l = MathHelper.floor(d + (double)i);
		int m = MathHelper.floor(e - (double)i);
		int n = MathHelper.floor(e);
		int o = MathHelper.floor(j - (double)i);
		int p = MathHelper.floor(j + (double)i);
		MatrixStack.Entry entry = matrixStack.peek();
		VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getEntityNoOutline(field_21009));

		for (BlockPos blockPos : BlockPos.iterate(new BlockPos(k, m, o), new BlockPos(l, n, p))) {
			method_23163(entry, vertexConsumer, worldView, blockPos, d, e, j, i, f);
		}
	}

	private static void method_23163(
		MatrixStack.Entry entry, VertexConsumer vertexConsumer, WorldView worldView, BlockPos blockPos, double d, double e, double f, float g, float h
	) {
		BlockPos blockPos2 = blockPos.method_10074();
		BlockState blockState = worldView.getBlockState(blockPos2);
		if (blockState.getRenderType() != BlockRenderType.INVISIBLE && worldView.getLightLevel(blockPos) > 3) {
			if (blockState.isFullCube(worldView, blockPos2)) {
				VoxelShape voxelShape = blockState.getOutlineShape(worldView, blockPos.method_10074());
				if (!voxelShape.isEmpty()) {
					float i = (float)(((double)h - (e - (double)blockPos.getY()) / 2.0) * 0.5 * (double)worldView.getBrightness(blockPos));
					if (i >= 0.0F) {
						if (i > 1.0F) {
							i = 1.0F;
						}

						Box box = voxelShape.getBoundingBox();
						double j = (double)blockPos.getX() + box.x1;
						double k = (double)blockPos.getX() + box.x2;
						double l = (double)blockPos.getY() + box.y1;
						double m = (double)blockPos.getZ() + box.z1;
						double n = (double)blockPos.getZ() + box.z2;
						float o = (float)(j - d);
						float p = (float)(k - d);
						float q = (float)(l - e + 0.015625);
						float r = (float)(m - f);
						float s = (float)(n - f);
						float t = -o / 2.0F / g + 0.5F;
						float u = -p / 2.0F / g + 0.5F;
						float v = -r / 2.0F / g + 0.5F;
						float w = -s / 2.0F / g + 0.5F;
						method_23162(entry, vertexConsumer, i, o, q, r, t, v);
						method_23162(entry, vertexConsumer, i, o, q, s, t, w);
						method_23162(entry, vertexConsumer, i, p, q, s, u, w);
						method_23162(entry, vertexConsumer, i, p, q, r, u, v);
					}
				}
			}
		}
	}

	private static void method_23162(MatrixStack.Entry entry, VertexConsumer vertexConsumer, float f, float g, float h, float i, float j, float k) {
		vertexConsumer.vertex(entry.getModel(), g, h, i)
			.color(1.0F, 1.0F, 1.0F, f)
			.texture(j, k)
			.overlay(OverlayTexture.DEFAULT_UV)
			.light(15728880)
			.method_23763(entry.getNormal(), 0.0F, 1.0F, 0.0F)
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

	public TextRenderer getTextRenderer() {
		return this.textRenderer;
	}
}
