package net.minecraft.client.render.entity;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Map;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VisibleRegion;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.EnderEyeEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.FireworkEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.EnderDragonPart;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.decoration.EnderCrystalEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.decoration.LeadKnotEntity;
import net.minecraft.entity.decoration.painting.PaintingEntity;
import net.minecraft.entity.mob.BlazeEntity;
import net.minecraft.entity.mob.CaveSpiderEntity;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.DrownedEntity;
import net.minecraft.entity.mob.ElderGuardianEntity;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.mob.EndermiteEntity;
import net.minecraft.entity.mob.EvokerEntity;
import net.minecraft.entity.mob.EvokerFangsEntity;
import net.minecraft.entity.mob.GhastEntity;
import net.minecraft.entity.mob.GiantEntity;
import net.minecraft.entity.mob.GuardianEntity;
import net.minecraft.entity.mob.HuskEntity;
import net.minecraft.entity.mob.IllusionerEntity;
import net.minecraft.entity.mob.MagmaCubeEntity;
import net.minecraft.entity.mob.PhantomEntity;
import net.minecraft.entity.mob.PillagerEntity;
import net.minecraft.entity.mob.RavagerEntity;
import net.minecraft.entity.mob.ShulkerEntity;
import net.minecraft.entity.mob.SilverfishEntity;
import net.minecraft.entity.mob.SkeletonEntity;
import net.minecraft.entity.mob.SkeletonHorseEntity;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.entity.mob.SpiderEntity;
import net.minecraft.entity.mob.StrayEntity;
import net.minecraft.entity.mob.VexEntity;
import net.minecraft.entity.mob.VindicatorEntity;
import net.minecraft.entity.mob.WitchEntity;
import net.minecraft.entity.mob.WitherSkeletonEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.mob.ZombieHorseEntity;
import net.minecraft.entity.mob.ZombiePigmanEntity;
import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.passive.CodEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.passive.DolphinEntity;
import net.minecraft.entity.passive.DonkeyEntity;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.LlamaEntity;
import net.minecraft.entity.passive.MooshroomEntity;
import net.minecraft.entity.passive.MuleEntity;
import net.minecraft.entity.passive.OcelotEntity;
import net.minecraft.entity.passive.PandaEntity;
import net.minecraft.entity.passive.ParrotEntity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.passive.PolarBearEntity;
import net.minecraft.entity.passive.PufferfishEntity;
import net.minecraft.entity.passive.RabbitEntity;
import net.minecraft.entity.passive.SalmonEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.passive.SnowGolemEntity;
import net.minecraft.entity.passive.SquidEntity;
import net.minecraft.entity.passive.TraderLlamaEntity;
import net.minecraft.entity.passive.TropicalFishEntity;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.passive.WanderingTraderEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.DragonFireballEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.entity.projectile.LlamaSpitEntity;
import net.minecraft.entity.projectile.ShulkerBulletEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.entity.projectile.SpectralArrowEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.entity.projectile.WitherSkullEntity;
import net.minecraft.entity.thrown.SnowballEntity;
import net.minecraft.entity.thrown.ThrownEggEntity;
import net.minecraft.entity.thrown.ThrownEnderpearlEntity;
import net.minecraft.entity.thrown.ThrownExperienceBottleEntity;
import net.minecraft.entity.thrown.ThrownPotionEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.entity.vehicle.SpawnerMinecartEntity;
import net.minecraft.entity.vehicle.TntMinecartEntity;
import net.minecraft.resource.ReloadableResourceManager;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class EntityRenderDispatcher {
	private final Map<Class<? extends Entity>, EntityRenderer<? extends Entity>> renderers = Maps.<Class<? extends Entity>, EntityRenderer<? extends Entity>>newHashMap();
	private final Map<String, PlayerEntityRenderer> modelRenderers = Maps.<String, PlayerEntityRenderer>newHashMap();
	private final PlayerEntityRenderer playerRenderer;
	private TextRenderer textRenderer;
	private double renderPosX;
	private double renderPosY;
	private double renderPosZ;
	public final TextureManager textureManager;
	public World world;
	public Camera camera;
	public Entity targetedEntity;
	public float cameraYaw;
	public float cameraPitch;
	public GameOptions gameOptions;
	private boolean renderOutlines;
	private boolean renderShadows = true;
	private boolean renderHitboxes;

	private <T extends Entity> void register(Class<T> class_, EntityRenderer<? super T> entityRenderer) {
		this.renderers.put(class_, entityRenderer);
	}

	public EntityRenderDispatcher(TextureManager textureManager, ItemRenderer itemRenderer, ReloadableResourceManager reloadableResourceManager) {
		this.textureManager = textureManager;
		this.register(CaveSpiderEntity.class, new CaveSpiderEntityRenderer(this));
		this.register(SpiderEntity.class, new SpiderEntityRenderer(this));
		this.register(PigEntity.class, new PigEntityRenderer(this));
		this.register(SheepEntity.class, new SheepEntityRenderer(this));
		this.register(CowEntity.class, new CowEntityRenderer(this));
		this.register(MooshroomEntity.class, new MooshroomEntityRenderer(this));
		this.register(WolfEntity.class, new WolfEntityRenderer(this));
		this.register(ChickenEntity.class, new ChickenEntityRenderer(this));
		this.register(OcelotEntity.class, new OcelotEntityRenderer(this));
		this.register(RabbitEntity.class, new RabbitEntityRenderer(this));
		this.register(ParrotEntity.class, new ParrotEntityRenderer(this));
		this.register(TurtleEntity.class, new TurtleEntityRenderer(this));
		this.register(SilverfishEntity.class, new SilverfishEntityRenderer(this));
		this.register(EndermiteEntity.class, new EndermiteEntityRenderer(this));
		this.register(CreeperEntity.class, new CreeperEntityRenderer(this));
		this.register(EndermanEntity.class, new EndermanEntityRenderer(this));
		this.register(SnowGolemEntity.class, new SnowGolemEntityRenderer(this));
		this.register(SkeletonEntity.class, new SkeletonEntityRenderer(this));
		this.register(WitherSkeletonEntity.class, new WitherSkeletonEntityRenderer(this));
		this.register(StrayEntity.class, new StrayEntityRenderer(this));
		this.register(WitchEntity.class, new WitchEntityRenderer(this));
		this.register(BlazeEntity.class, new BlazeEntityRenderer(this));
		this.register(ZombiePigmanEntity.class, new ZombiePigmanEntityRenderer(this));
		this.register(ZombieEntity.class, new ZombieEntityRenderer(this));
		this.register(ZombieVillagerEntity.class, new ZombieVillagerEntityRenderer(this, reloadableResourceManager));
		this.register(HuskEntity.class, new HuskEntityRenderer(this));
		this.register(DrownedEntity.class, new DrownedEntityRenderer(this));
		this.register(SlimeEntity.class, new SlimeEntityRenderer(this));
		this.register(MagmaCubeEntity.class, new MagmaCubeEntityRenderer(this));
		this.register(GiantEntity.class, new GiantEntityRenderer(this, 6.0F));
		this.register(GhastEntity.class, new GhastEntityRenderer(this));
		this.register(SquidEntity.class, new SquidEntityRenderer(this));
		this.register(VillagerEntity.class, new VillagerEntityRenderer(this, reloadableResourceManager));
		this.register(WanderingTraderEntity.class, new WanderingTraderEntityRenderer(this));
		this.register(IronGolemEntity.class, new IronGolemEntityRenderer(this));
		this.register(BatEntity.class, new BatEntityRenderer(this));
		this.register(GuardianEntity.class, new GuardianEntityRenderer(this));
		this.register(ElderGuardianEntity.class, new ElderGuardianEntityRenderer(this));
		this.register(ShulkerEntity.class, new ShulkerEntityRenderer(this));
		this.register(PolarBearEntity.class, new PolarBearEntityRenderer(this));
		this.register(EvokerEntity.class, new EvokerIllagerEntityRenderer(this));
		this.register(VindicatorEntity.class, new VindicatorEntityRenderer(this));
		this.register(PillagerEntity.class, new PillagerEntityRenderer(this));
		this.register(RavagerEntity.class, new RavagerEntityRenderer(this));
		this.register(VexEntity.class, new VexEntityRenderer(this));
		this.register(IllusionerEntity.class, new IllusionerEntityRenderer(this));
		this.register(PhantomEntity.class, new PhantomEntityRenderer(this));
		this.register(PufferfishEntity.class, new PufferfishEntityRenderer(this));
		this.register(SalmonEntity.class, new SalmonEntityRenderer(this));
		this.register(CodEntity.class, new CodEntityRenderer(this));
		this.register(TropicalFishEntity.class, new TropicalFishEntityRenderer(this));
		this.register(DolphinEntity.class, new DolphinEntityRenderer(this));
		this.register(PandaEntity.class, new PandaEntityRenderer(this));
		this.register(CatEntity.class, new CatEntityRenderer(this));
		this.register(FoxEntity.class, new FoxEntityRenderer(this));
		this.register(EnderDragonEntity.class, new EnderDragonEntityRenderer(this));
		this.register(EnderCrystalEntity.class, new EnderCrystalEntityRenderer(this));
		this.register(WitherEntity.class, new WitherEntityRenderer(this));
		this.register(Entity.class, new DefaultEntityRenderer(this));
		this.register(PaintingEntity.class, new PaintingEntityRenderer(this));
		this.register(ItemFrameEntity.class, new ItemFrameEntityRenderer(this, itemRenderer));
		this.register(LeadKnotEntity.class, new LeashKnotEntityRenderer(this));
		this.register(ArrowEntity.class, new ArrowEntityRenderer(this));
		this.register(SpectralArrowEntity.class, new SpectralArrowEntityRenderer(this));
		this.register(TridentEntity.class, new TridentEntityRenderer(this));
		this.register(SnowballEntity.class, new FlyingItemEntityRenderer(this, itemRenderer));
		this.register(ThrownEnderpearlEntity.class, new FlyingItemEntityRenderer(this, itemRenderer));
		this.register(EnderEyeEntity.class, new FlyingItemEntityRenderer(this, itemRenderer));
		this.register(ThrownEggEntity.class, new FlyingItemEntityRenderer(this, itemRenderer));
		this.register(ThrownPotionEntity.class, new FlyingItemEntityRenderer(this, itemRenderer));
		this.register(ThrownExperienceBottleEntity.class, new FlyingItemEntityRenderer(this, itemRenderer));
		this.register(FireworkEntity.class, new FireworkEntityRenderer(this, itemRenderer));
		this.register(FireballEntity.class, new FlyingItemEntityRenderer(this, itemRenderer, 3.0F));
		this.register(SmallFireballEntity.class, new FlyingItemEntityRenderer(this, itemRenderer, 0.75F));
		this.register(DragonFireballEntity.class, new DragonFireballEntityRenderer(this));
		this.register(WitherSkullEntity.class, new WitherSkullEntityRenderer(this));
		this.register(ShulkerBulletEntity.class, new ShulkerBulletEntityRenderer(this));
		this.register(ItemEntity.class, new ItemEntityRenderer(this, itemRenderer));
		this.register(ExperienceOrbEntity.class, new ExperienceOrbEntityRenderer(this));
		this.register(TntEntity.class, new TntEntityRenderer(this));
		this.register(FallingBlockEntity.class, new FallingBlockEntityRenderer(this));
		this.register(ArmorStandEntity.class, new ArmorStandEntityRenderer(this));
		this.register(EvokerFangsEntity.class, new EvokerFangsEntityRenderer(this));
		this.register(TntMinecartEntity.class, new TntMinecartEntityRenderer(this));
		this.register(SpawnerMinecartEntity.class, new MinecartEntityRenderer(this));
		this.register(AbstractMinecartEntity.class, new MinecartEntityRenderer(this));
		this.register(BoatEntity.class, new BoatEntityRenderer(this));
		this.register(FishingBobberEntity.class, new FishingBobberEntityRenderer(this));
		this.register(AreaEffectCloudEntity.class, new AreaEffectCloudEntityRenderer(this));
		this.register(HorseEntity.class, new HorseEntityRenderer(this));
		this.register(SkeletonHorseEntity.class, new ZombieHorseEntityRenderer(this));
		this.register(ZombieHorseEntity.class, new ZombieHorseEntityRenderer(this));
		this.register(MuleEntity.class, new DonkeyEntityRenderer(this, 0.92F));
		this.register(DonkeyEntity.class, new DonkeyEntityRenderer(this, 0.87F));
		this.register(LlamaEntity.class, new LlamaEntityRenderer(this));
		this.register(TraderLlamaEntity.class, new LlamaEntityRenderer(this));
		this.register(LlamaSpitEntity.class, new LlamaSpitEntityRenderer(this));
		this.register(LightningEntity.class, new LightningEntityRenderer(this));
		this.playerRenderer = new PlayerEntityRenderer(this);
		this.modelRenderers.put("default", this.playerRenderer);
		this.modelRenderers.put("slim", new PlayerEntityRenderer(this, true));
	}

	public void setRenderPosition(double d, double e, double f) {
		this.renderPosX = d;
		this.renderPosY = e;
		this.renderPosZ = f;
	}

	public <T extends Entity, U extends EntityRenderer<T>> U getRenderer(Class<? extends Entity> class_) {
		EntityRenderer<? extends Entity> entityRenderer = (EntityRenderer<? extends Entity>)this.renderers.get(class_);
		if (entityRenderer == null && class_ != Entity.class) {
			entityRenderer = this.getRenderer(class_.getSuperclass());
			this.renderers.put(class_, entityRenderer);
		}

		return (U)entityRenderer;
	}

	@Nullable
	public <T extends Entity, U extends EntityRenderer<T>> U getRenderer(T entity) {
		if (entity instanceof AbstractClientPlayerEntity) {
			String string = ((AbstractClientPlayerEntity)entity).getModel();
			PlayerEntityRenderer playerEntityRenderer = (PlayerEntityRenderer)this.modelRenderers.get(string);
			return (U)(playerEntityRenderer != null ? playerEntityRenderer : this.playerRenderer);
		} else {
			return this.getRenderer(entity.getClass());
		}
	}

	public void configure(World world, TextRenderer textRenderer, Camera camera, Entity entity, GameOptions gameOptions) {
		this.world = world;
		this.gameOptions = gameOptions;
		this.camera = camera;
		this.targetedEntity = entity;
		this.textRenderer = textRenderer;
		if (camera.getFocusedEntity() instanceof LivingEntity && ((LivingEntity)camera.getFocusedEntity()).isSleeping()) {
			Direction direction = ((LivingEntity)camera.getFocusedEntity()).getSleepingDirection();
			if (direction != null) {
				this.cameraYaw = direction.getOpposite().asRotation();
				this.cameraPitch = 0.0F;
			}
		} else {
			this.cameraYaw = camera.getYaw();
			this.cameraPitch = camera.getPitch();
		}
	}

	public void method_3945(float f) {
		this.cameraYaw = f;
	}

	public boolean shouldRenderShadows() {
		return this.renderShadows;
	}

	public void setRenderShadows(boolean bl) {
		this.renderShadows = bl;
	}

	public void setRenderHitboxes(boolean bl) {
		this.renderHitboxes = bl;
	}

	public boolean shouldRenderHitboxes() {
		return this.renderHitboxes;
	}

	public boolean hasSecondPass(Entity entity) {
		return this.getRenderer(entity).hasSecondPass();
	}

	public boolean shouldRender(Entity entity, VisibleRegion visibleRegion, double d, double e, double f) {
		EntityRenderer<Entity> entityRenderer = this.getRenderer(entity);
		return entityRenderer != null && entityRenderer.isVisible(entity, visibleRegion, d, e, f);
	}

	public void render(Entity entity, float f, boolean bl) {
		if (entity.age == 0) {
			entity.prevRenderX = entity.x;
			entity.prevRenderY = entity.y;
			entity.prevRenderZ = entity.z;
		}

		double d = MathHelper.lerp((double)f, entity.prevRenderX, entity.x);
		double e = MathHelper.lerp((double)f, entity.prevRenderY, entity.y);
		double g = MathHelper.lerp((double)f, entity.prevRenderZ, entity.z);
		float h = MathHelper.lerp(f, entity.prevYaw, entity.yaw);
		int i = entity.getLightmapCoordinates();
		if (entity.isOnFire()) {
			i = 15728880;
		}

		int j = i % 65536;
		int k = i / 65536;
		GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, (float)j, (float)k);
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.render(entity, d - this.renderPosX, e - this.renderPosY, g - this.renderPosZ, h, f, bl);
	}

	public void render(Entity entity, double d, double e, double f, float g, float h, boolean bl) {
		EntityRenderer<Entity> entityRenderer = null;

		try {
			entityRenderer = this.getRenderer(entity);
			if (entityRenderer != null && this.textureManager != null) {
				try {
					entityRenderer.setRenderOutlines(this.renderOutlines);
					entityRenderer.render(entity, d, e, f, g, h);
				} catch (Throwable var17) {
					throw new CrashException(CrashReport.create(var17, "Rendering entity in world"));
				}

				try {
					if (!this.renderOutlines) {
						entityRenderer.postRender(entity, d, e, f, g, h);
					}
				} catch (Throwable var18) {
					throw new CrashException(CrashReport.create(var18, "Post-rendering entity in world"));
				}

				if (this.renderHitboxes && !entity.isInvisible() && !bl && !MinecraftClient.getInstance().hasReducedDebugInfo()) {
					try {
						this.renderHitbox(entity, d, e, f, g, h);
					} catch (Throwable var16) {
						throw new CrashException(CrashReport.create(var16, "Rendering entity hitbox in world"));
					}
				}
			}
		} catch (Throwable var19) {
			CrashReport crashReport = CrashReport.create(var19, "Rendering entity in world");
			CrashReportSection crashReportSection = crashReport.addElement("Entity being rendered");
			entity.populateCrashReport(crashReportSection);
			CrashReportSection crashReportSection2 = crashReport.addElement("Renderer details");
			crashReportSection2.add("Assigned renderer", entityRenderer);
			crashReportSection2.add("Location", CrashReportSection.createPositionString(d, e, f));
			crashReportSection2.add("Rotation", g);
			crashReportSection2.add("Delta", h);
			throw new CrashException(crashReport);
		}
	}

	public void renderSecondPass(Entity entity, float f) {
		if (entity.age == 0) {
			entity.prevRenderX = entity.x;
			entity.prevRenderY = entity.y;
			entity.prevRenderZ = entity.z;
		}

		double d = MathHelper.lerp((double)f, entity.prevRenderX, entity.x);
		double e = MathHelper.lerp((double)f, entity.prevRenderY, entity.y);
		double g = MathHelper.lerp((double)f, entity.prevRenderZ, entity.z);
		float h = MathHelper.lerp(f, entity.prevYaw, entity.yaw);
		int i = entity.getLightmapCoordinates();
		if (entity.isOnFire()) {
			i = 15728880;
		}

		int j = i % 65536;
		int k = i / 65536;
		GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, (float)j, (float)k);
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		EntityRenderer<Entity> entityRenderer = this.getRenderer(entity);
		if (entityRenderer != null && this.textureManager != null) {
			entityRenderer.renderSecondPass(entity, d - this.renderPosX, e - this.renderPosY, g - this.renderPosZ, h, f);
		}
	}

	private void renderHitbox(Entity entity, double d, double e, double f, float g, float h) {
		GlStateManager.depthMask(false);
		GlStateManager.disableTexture();
		GlStateManager.disableLighting();
		GlStateManager.disableCull();
		GlStateManager.disableBlend();
		float i = entity.getWidth() / 2.0F;
		Box box = entity.getBoundingBox();
		WorldRenderer.drawBoxOutline(
			box.minX - entity.x + d,
			box.minY - entity.y + e,
			box.minZ - entity.z + f,
			box.maxX - entity.x + d,
			box.maxY - entity.y + e,
			box.maxZ - entity.z + f,
			1.0F,
			1.0F,
			1.0F,
			1.0F
		);
		if (entity instanceof EnderDragonEntity) {
			for (EnderDragonPart enderDragonPart : ((EnderDragonEntity)entity).method_5690()) {
				double j = (enderDragonPart.x - enderDragonPart.prevX) * (double)h;
				double k = (enderDragonPart.y - enderDragonPart.prevY) * (double)h;
				double l = (enderDragonPart.z - enderDragonPart.prevZ) * (double)h;
				Box box2 = enderDragonPart.getBoundingBox();
				WorldRenderer.drawBoxOutline(
					box2.minX - this.renderPosX + j,
					box2.minY - this.renderPosY + k,
					box2.minZ - this.renderPosZ + l,
					box2.maxX - this.renderPosX + j,
					box2.maxY - this.renderPosY + k,
					box2.maxZ - this.renderPosZ + l,
					0.25F,
					1.0F,
					0.0F,
					1.0F
				);
			}
		}

		if (entity instanceof LivingEntity) {
			float m = 0.01F;
			WorldRenderer.drawBoxOutline(
				d - (double)i,
				e + (double)entity.getStandingEyeHeight() - 0.01F,
				f - (double)i,
				d + (double)i,
				e + (double)entity.getStandingEyeHeight() + 0.01F,
				f + (double)i,
				1.0F,
				0.0F,
				0.0F,
				1.0F
			);
		}

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
		Vec3d vec3d = entity.getRotationVec(h);
		bufferBuilder.begin(3, VertexFormats.POSITION_COLOR);
		bufferBuilder.vertex(d, e + (double)entity.getStandingEyeHeight(), f).color(0, 0, 255, 255).next();
		bufferBuilder.vertex(d + vec3d.x * 2.0, e + (double)entity.getStandingEyeHeight() + vec3d.y * 2.0, f + vec3d.z * 2.0).color(0, 0, 255, 255).next();
		tessellator.draw();
		GlStateManager.enableTexture();
		GlStateManager.enableLighting();
		GlStateManager.enableCull();
		GlStateManager.disableBlend();
		GlStateManager.depthMask(true);
	}

	public void setWorld(@Nullable World world) {
		this.world = world;
		if (world == null) {
			this.camera = null;
		}
	}

	public double squaredDistanceToCamera(double d, double e, double f) {
		return this.camera.getPos().squaredDistanceTo(d, e, f);
	}

	public TextRenderer getTextRenderer() {
		return this.textRenderer;
	}

	public void setRenderOutlines(boolean bl) {
		this.renderOutlines = bl;
	}
}
