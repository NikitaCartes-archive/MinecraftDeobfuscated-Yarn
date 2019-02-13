package net.minecraft.client.render.entity;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Map;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.render.BufferBuilder;
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
import net.minecraft.entity.PrimedTntEntity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
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
import net.minecraft.entity.mob.PigZombieEntity;
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
import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.entity.parts.EntityPart;
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
import net.minecraft.entity.passive.SnowmanEntity;
import net.minecraft.entity.passive.SquidEntity;
import net.minecraft.entity.passive.TraderLlamaEntity;
import net.minecraft.entity.passive.TropicalFishEntity;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.passive.WanderingTraderEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.DragonFireballEntity;
import net.minecraft.entity.projectile.ExplodingWitherSkullEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.entity.projectile.FishHookEntity;
import net.minecraft.entity.projectile.LlamaSpitEntity;
import net.minecraft.entity.projectile.ShulkerBulletEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.entity.projectile.SpectralArrowEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.entity.thrown.SnowballEntity;
import net.minecraft.entity.thrown.ThrownEggEntity;
import net.minecraft.entity.thrown.ThrownEnderpearlEntity;
import net.minecraft.entity.thrown.ThrownExperienceBottleEntity;
import net.minecraft.entity.thrown.ThrownPotionEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.entity.vehicle.MobSpawnerMinecartEntity;
import net.minecraft.entity.vehicle.TNTMinecartEntity;
import net.minecraft.resource.ReloadableResourceManager;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class EntityRenderDispatcher {
	private final Map<Class<? extends Entity>, EntityRenderer<? extends Entity>> renderers = Maps.<Class<? extends Entity>, EntityRenderer<? extends Entity>>newHashMap();
	private final Map<String, PlayerEntityRenderer> skinMap = Maps.<String, PlayerEntityRenderer>newHashMap();
	private final PlayerEntityRenderer playerRenderer;
	private TextRenderer fontRenderer;
	private double renderPosX;
	private double renderPosY;
	private double renderPosZ;
	public final TextureManager textureManager;
	public World world;
	public Entity field_4686;
	public Entity field_4678;
	public float field_4679;
	public float field_4677;
	public GameOptions settings;
	public double field_4695;
	public double field_4694;
	public double field_4693;
	private boolean renderOutlines;
	private boolean field_4681 = true;
	private boolean field_4680;

	private <T extends Entity> void method_17145(Class<T> class_, EntityRenderer<? super T> entityRenderer) {
		this.renderers.put(class_, entityRenderer);
	}

	public EntityRenderDispatcher(TextureManager textureManager, ItemRenderer itemRenderer, ReloadableResourceManager reloadableResourceManager) {
		this.textureManager = textureManager;
		this.method_17145(CaveSpiderEntity.class, new CaveSpiderEntityRenderer(this));
		this.method_17145(SpiderEntity.class, new SpiderEntityRenderer(this));
		this.method_17145(PigEntity.class, new PigEntityRenderer(this));
		this.method_17145(SheepEntity.class, new SheepEntityRenderer(this));
		this.method_17145(CowEntity.class, new CowEntityRenderer(this));
		this.method_17145(MooshroomEntity.class, new MooshroomEntityRenderer(this));
		this.method_17145(WolfEntity.class, new WolfEntityRenderer(this));
		this.method_17145(ChickenEntity.class, new ChickenEntityRenderer(this));
		this.method_17145(OcelotEntity.class, new OcelotEntityRenderer(this));
		this.method_17145(RabbitEntity.class, new RabbitEntityRenderer(this));
		this.method_17145(ParrotEntity.class, new ParrotEntityRenderer(this));
		this.method_17145(TurtleEntity.class, new TurtleEntityRenderer(this));
		this.method_17145(SilverfishEntity.class, new SilverfishEntityRenderer(this));
		this.method_17145(EndermiteEntity.class, new EndermiteEntityRenderer(this));
		this.method_17145(CreeperEntity.class, new CreeperEntityRenderer(this));
		this.method_17145(EndermanEntity.class, new EndermanEntityRenderer(this));
		this.method_17145(SnowmanEntity.class, new SnowmanEntityRenderer(this));
		this.method_17145(SkeletonEntity.class, new SkeletonEntityRenderer(this));
		this.method_17145(WitherSkeletonEntity.class, new WitherSkeletonEntityRenderer(this));
		this.method_17145(StrayEntity.class, new StrayEntityRenderer(this));
		this.method_17145(WitchEntity.class, new WitchEntityRenderer(this));
		this.method_17145(BlazeEntity.class, new BlazeEntityRenderer(this));
		this.method_17145(PigZombieEntity.class, new PigZombieEntityRenderer(this));
		this.method_17145(ZombieEntity.class, new ZombieEntityRenderer(this));
		this.method_17145(ZombieVillagerEntity.class, new ZombieVillagerEntityRenderer(this, reloadableResourceManager));
		this.method_17145(HuskEntity.class, new HuskEntityRenderer(this));
		this.method_17145(DrownedEntity.class, new DrownedEntityRenderer(this));
		this.method_17145(SlimeEntity.class, new SlimeEntityRenderer(this));
		this.method_17145(MagmaCubeEntity.class, new MagmaCubeEntityRenderer(this));
		this.method_17145(GiantEntity.class, new GiantEntityRenderer(this, 6.0F));
		this.method_17145(GhastEntity.class, new GhastEntityRenderer(this));
		this.method_17145(SquidEntity.class, new SquidEntityRenderer(this));
		this.method_17145(VillagerEntity.class, new VillagerEntityRenderer(this, reloadableResourceManager));
		this.method_17145(WanderingTraderEntity.class, new WanderingTraderEntityRenderer(this));
		this.method_17145(IronGolemEntity.class, new IronGolemEntityRenderer(this));
		this.method_17145(BatEntity.class, new BatEntityRenderer(this));
		this.method_17145(GuardianEntity.class, new GuardianEntityRenderer(this));
		this.method_17145(ElderGuardianEntity.class, new ElderGuardianEntityRenderer(this));
		this.method_17145(ShulkerEntity.class, new ShulkerEntityRenderer(this));
		this.method_17145(PolarBearEntity.class, new PolarBearEntityRenderer(this));
		this.method_17145(EvokerEntity.class, new EvokerIllagerEntityRenderer(this));
		this.method_17145(VindicatorEntity.class, new VindicatorEntityRenderer(this));
		this.method_17145(PillagerEntity.class, new PillagerEntityRenderer(this));
		this.method_17145(RavagerEntity.class, new IllagerBeastEntityRenderer(this));
		this.method_17145(VexEntity.class, new VexEntityRenderer(this));
		this.method_17145(IllusionerEntity.class, new IllusionerEntityRenderer(this));
		this.method_17145(PhantomEntity.class, new PhantomEntityRenderer(this));
		this.method_17145(PufferfishEntity.class, new PufferfishEntityRenderer(this));
		this.method_17145(SalmonEntity.class, new SalmonEntityRenderer(this));
		this.method_17145(CodEntity.class, new CodEntityRenderer(this));
		this.method_17145(TropicalFishEntity.class, new TropicalFishEntityRenderer(this));
		this.method_17145(DolphinEntity.class, new DolphinEntityRenderer(this));
		this.method_17145(PandaEntity.class, new PandaEntityRenderer(this));
		this.method_17145(CatEntity.class, new CatEntityRenderer(this));
		this.method_17145(FoxEntity.class, new FoxEntityRenderer(this));
		this.method_17145(EnderDragonEntity.class, new EnderDragonEntityRenderer(this));
		this.method_17145(EnderCrystalEntity.class, new EnderCrystalEntityRenderer(this));
		this.method_17145(WitherEntity.class, new WitherEntityRenderer(this));
		this.method_17145(Entity.class, new DefaultEntityRenderer(this));
		this.method_17145(PaintingEntity.class, new PaintingEntityRenderer(this));
		this.method_17145(ItemFrameEntity.class, new ItemFrameEntityRenderer(this, itemRenderer));
		this.method_17145(LeadKnotEntity.class, new LeashKnotEntityRenderer(this));
		this.method_17145(ArrowEntity.class, new ArrowEntityRenderer(this));
		this.method_17145(SpectralArrowEntity.class, new SpectralArrowEntityRenderer(this));
		this.method_17145(TridentEntity.class, new TridentEntityRenderer(this));
		this.method_17145(SnowballEntity.class, new FlyingItemEntityRenderer(this, itemRenderer));
		this.method_17145(ThrownEnderpearlEntity.class, new FlyingItemEntityRenderer(this, itemRenderer));
		this.method_17145(EnderEyeEntity.class, new FlyingItemEntityRenderer(this, itemRenderer));
		this.method_17145(ThrownEggEntity.class, new FlyingItemEntityRenderer(this, itemRenderer));
		this.method_17145(ThrownPotionEntity.class, new FlyingItemEntityRenderer(this, itemRenderer));
		this.method_17145(ThrownExperienceBottleEntity.class, new FlyingItemEntityRenderer(this, itemRenderer));
		this.method_17145(FireworkEntity.class, new FireworkEntityRenderer(this, itemRenderer));
		this.method_17145(FireballEntity.class, new FlyingItemEntityRenderer(this, itemRenderer, 3.0F));
		this.method_17145(SmallFireballEntity.class, new FlyingItemEntityRenderer(this, itemRenderer, 0.75F));
		this.method_17145(DragonFireballEntity.class, new DragonFireballEntityRenderer(this));
		this.method_17145(ExplodingWitherSkullEntity.class, new ExplodingWitherSkullEntityRenderer(this));
		this.method_17145(ShulkerBulletEntity.class, new ShulkerBulletEntityRenderer(this));
		this.method_17145(ItemEntity.class, new ItemEntityRenderer(this, itemRenderer));
		this.method_17145(ExperienceOrbEntity.class, new ExperienceOrbEntityRenderer(this));
		this.method_17145(PrimedTntEntity.class, new TNTPrimedEntityRenderer(this));
		this.method_17145(FallingBlockEntity.class, new FallingBlockEntityRenderer(this));
		this.method_17145(ArmorStandEntity.class, new ArmorStandEntityRenderer(this));
		this.method_17145(EvokerFangsEntity.class, new EvokerFangsEntityRenderer(this));
		this.method_17145(TNTMinecartEntity.class, new MinecartTNTEntityRenderer(this));
		this.method_17145(MobSpawnerMinecartEntity.class, new MinecartEntityRenderer(this));
		this.method_17145(AbstractMinecartEntity.class, new MinecartEntityRenderer(this));
		this.method_17145(BoatEntity.class, new BoatEntityRenderer(this));
		this.method_17145(FishHookEntity.class, new FishHookEntityRenderer(this));
		this.method_17145(AreaEffectCloudEntity.class, new AreaEffectCloudEntityRenderer(this));
		this.method_17145(HorseEntity.class, new HorseEntityRenderer(this));
		this.method_17145(SkeletonHorseEntity.class, new ZombieHorseEntityRenderer(this));
		this.method_17145(ZombieHorseEntity.class, new ZombieHorseEntityRenderer(this));
		this.method_17145(MuleEntity.class, new DonkeyEntityRenderer(this, 0.92F));
		this.method_17145(DonkeyEntity.class, new DonkeyEntityRenderer(this, 0.87F));
		this.method_17145(LlamaEntity.class, new LlamaEntityRenderer(this));
		this.method_17145(TraderLlamaEntity.class, new LlamaEntityRenderer(this));
		this.method_17145(LlamaSpitEntity.class, new LlamaSpitEntityRenderer(this));
		this.method_17145(LightningEntity.class, new LightningEntityRenderer(this));
		this.playerRenderer = new PlayerEntityRenderer(this);
		this.skinMap.put("default", this.playerRenderer);
		this.skinMap.put("slim", new PlayerEntityRenderer(this, true));
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
			String string = ((AbstractClientPlayerEntity)entity).method_3121();
			PlayerEntityRenderer playerEntityRenderer = (PlayerEntityRenderer)this.skinMap.get(string);
			return (U)(playerEntityRenderer != null ? playerEntityRenderer : this.playerRenderer);
		} else {
			return this.getRenderer(entity.getClass());
		}
	}

	public void method_3941(World world, TextRenderer textRenderer, Entity entity, Entity entity2, GameOptions gameOptions, float f) {
		this.world = world;
		this.settings = gameOptions;
		this.field_4686 = entity;
		this.field_4678 = entity2;
		this.fontRenderer = textRenderer;
		if (entity instanceof LivingEntity && ((LivingEntity)entity).isSleeping()) {
			BlockState blockState = world.getBlockState(new BlockPos(entity));
			Block block = blockState.getBlock();
			if (block instanceof BedBlock) {
				int i = ((Direction)blockState.get(BedBlock.field_11177)).getHorizontal();
				this.field_4679 = (float)(i * 90 + 180);
				this.field_4677 = 0.0F;
			}
		} else {
			this.field_4679 = MathHelper.lerp(f, entity.prevYaw, entity.yaw);
			this.field_4677 = MathHelper.lerp(f, entity.prevPitch, entity.pitch);
		}

		if (gameOptions.perspective == 2) {
			this.field_4679 += 180.0F;
		}

		this.field_4695 = MathHelper.lerp((double)f, entity.prevRenderX, entity.x);
		this.field_4694 = MathHelper.lerp((double)f, entity.prevRenderY, entity.y);
		this.field_4693 = MathHelper.lerp((double)f, entity.prevRenderZ, entity.z);
	}

	public void method_3945(float f) {
		this.field_4679 = f;
	}

	public boolean method_3951() {
		return this.field_4681;
	}

	public void method_3948(boolean bl) {
		this.field_4681 = bl;
	}

	public void method_3955(boolean bl) {
		this.field_4680 = bl;
	}

	public boolean method_3958() {
		return this.field_4680;
	}

	public boolean hasSecondPass(Entity entity) {
		return this.getRenderer(entity).hasSecondPass();
	}

	public boolean method_3950(Entity entity, VisibleRegion visibleRegion, double d, double e, double f) {
		EntityRenderer<Entity> entityRenderer = this.getRenderer(entity);
		return entityRenderer != null && entityRenderer.method_3933(entity, visibleRegion, d, e, f);
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

				if (this.field_4680 && !entity.isInvisible() && !bl && !MinecraftClient.getInstance().hasReducedDebugInfo()) {
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
		BoundingBox boundingBox = entity.getBoundingBox();
		WorldRenderer.drawBoxOutline(
			boundingBox.minX - entity.x + d,
			boundingBox.minY - entity.y + e,
			boundingBox.minZ - entity.z + f,
			boundingBox.maxX - entity.x + d,
			boundingBox.maxY - entity.y + e,
			boundingBox.maxZ - entity.z + f,
			1.0F,
			1.0F,
			1.0F,
			1.0F
		);
		EntityPart[] entityParts = entity.getParts();
		if (entityParts != null) {
			for (EntityPart entityPart : entityParts) {
				double j = (entityPart.x - entityPart.prevX) * (double)h;
				double k = (entityPart.y - entityPart.prevY) * (double)h;
				double l = (entityPart.z - entityPart.prevZ) * (double)h;
				BoundingBox boundingBox2 = entityPart.getBoundingBox();
				WorldRenderer.drawBoxOutline(
					boundingBox2.minX - this.renderPosX + j,
					boundingBox2.minY - this.renderPosY + k,
					boundingBox2.minZ - this.renderPosZ + l,
					boundingBox2.maxX - this.renderPosX + j,
					boundingBox2.maxY - this.renderPosY + k,
					boundingBox2.maxZ - this.renderPosZ + l,
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
				e + (double)entity.getEyeHeight() - 0.01F,
				f - (double)i,
				d + (double)i,
				e + (double)entity.getEyeHeight() + 0.01F,
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
		bufferBuilder.vertex(d, e + (double)entity.getEyeHeight(), f).color(0, 0, 255, 255).next();
		bufferBuilder.vertex(d + vec3d.x * 2.0, e + (double)entity.getEyeHeight() + vec3d.y * 2.0, f + vec3d.z * 2.0).color(0, 0, 255, 255).next();
		tessellator.draw();
		GlStateManager.enableTexture();
		GlStateManager.enableLighting();
		GlStateManager.enableCull();
		GlStateManager.disableBlend();
		GlStateManager.depthMask(true);
	}

	public void method_3944(@Nullable World world) {
		this.world = world;
		if (world == null) {
			this.field_4686 = null;
		}
	}

	public double method_3959(double d, double e, double f) {
		double g = d - this.field_4695;
		double h = e - this.field_4694;
		double i = f - this.field_4693;
		return g * g + h * h + i * i;
	}

	public TextRenderer getFontRenderer() {
		return this.fontRenderer;
	}

	public void setRenderOutlines(boolean bl) {
		this.renderOutlines = bl;
	}
}
