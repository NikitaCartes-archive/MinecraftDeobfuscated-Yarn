package net.minecraft.client.render.entity;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Map;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_856;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.FontRenderer;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.Renderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexBuffer;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.settings.GameOptions;
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
import net.minecraft.entity.PrimedTNTEntity;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.decoration.EnderCrystalEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.decoration.LeadKnotEntity;
import net.minecraft.entity.decoration.PaintingEntity;
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
import net.minecraft.entity.mob.IllagerBeastEntity;
import net.minecraft.entity.mob.IllusionerEntity;
import net.minecraft.entity.mob.MagmaCubeEntity;
import net.minecraft.entity.mob.PhantomEntity;
import net.minecraft.entity.mob.PigZombieEntity;
import net.minecraft.entity.mob.PillagerEntity;
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
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.passive.CodEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.passive.DolphinEntity;
import net.minecraft.entity.passive.DonkeyEntity;
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
import net.minecraft.entity.passive.TropicalFishEntity;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.entity.passive.VillagerEntity;
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
import net.minecraft.item.Items;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportElement;
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
	private FontRenderer fontRenderer;
	private double renderPosX;
	private double renderPosY;
	private double renderPosZ;
	public TextureManager textureManager;
	public World world;
	public Entity field_4686;
	public Entity field_4678;
	public float field_4679;
	public float field_4677;
	public GameOptions settings;
	public double field_4695;
	public double field_4694;
	public double field_4693;
	private boolean field_4682;
	private boolean field_4681 = true;
	private boolean field_4680;

	public EntityRenderDispatcher(TextureManager textureManager, ItemRenderer itemRenderer) {
		this.textureManager = textureManager;
		this.renderers.put(CaveSpiderEntity.class, new CaveSpiderEntityRenderer(this));
		this.renderers.put(SpiderEntity.class, new SpiderEntityRenderer(this));
		this.renderers.put(PigEntity.class, new PigEntityRenderer(this));
		this.renderers.put(SheepEntity.class, new SheepEntityRenderer(this));
		this.renderers.put(CowEntity.class, new CowEntityRenderer(this));
		this.renderers.put(MooshroomEntity.class, new MooshroomEntityRenderer(this));
		this.renderers.put(WolfEntity.class, new WolfEntityRenderer(this));
		this.renderers.put(ChickenEntity.class, new ChickenEntityRenderer(this));
		this.renderers.put(OcelotEntity.class, new OcelotEntityRenderer(this));
		this.renderers.put(RabbitEntity.class, new RabbitEntityRenderer(this));
		this.renderers.put(ParrotEntity.class, new ParrotEntityRenderer(this));
		this.renderers.put(TurtleEntity.class, new TurtleEntityRenderer(this));
		this.renderers.put(SilverfishEntity.class, new SilverfishEntityRenderer(this));
		this.renderers.put(EndermiteEntity.class, new EndermiteEntityRenderer(this));
		this.renderers.put(CreeperEntity.class, new CreeperEntityRenderer(this));
		this.renderers.put(EndermanEntity.class, new EndermanEntityRenderer(this));
		this.renderers.put(SnowmanEntity.class, new SnowmanEntityRenderer(this));
		this.renderers.put(SkeletonEntity.class, new SkeletonEntityRenderer(this));
		this.renderers.put(WitherSkeletonEntity.class, new WitherSkeletonEntityRenderer(this));
		this.renderers.put(StrayEntity.class, new StrayEntityRenderer(this));
		this.renderers.put(WitchEntity.class, new WitchEntityRenderer(this));
		this.renderers.put(BlazeEntity.class, new BlazeEntityRenderer(this));
		this.renderers.put(PigZombieEntity.class, new PigZombieEntityRenderer(this));
		this.renderers.put(ZombieEntity.class, new ZombieEntityRenderer(this));
		this.renderers.put(ZombieVillagerEntity.class, new ZombieVillagerEntityRenderer(this));
		this.renderers.put(HuskEntity.class, new HuskEntityRenderer(this));
		this.renderers.put(DrownedEntity.class, new DrownedEntityRenderer(this));
		this.renderers.put(SlimeEntity.class, new SlimeEntityRenderer(this));
		this.renderers.put(MagmaCubeEntity.class, new MagmaCubeEntityRenderer(this));
		this.renderers.put(GiantEntity.class, new GiantEntityRenderer(this, 6.0F));
		this.renderers.put(GhastEntity.class, new GhastEntityRenderer(this));
		this.renderers.put(SquidEntity.class, new SquidEntityRenderer(this));
		this.renderers.put(VillagerEntity.class, new VillagerEntityRenderer(this));
		this.renderers.put(IronGolemEntity.class, new IronGolemEntityRenderer(this));
		this.renderers.put(BatEntity.class, new BatEntityRenderer(this));
		this.renderers.put(GuardianEntity.class, new GuardianEntityRenderer(this));
		this.renderers.put(ElderGuardianEntity.class, new ElderGuardianEntityRenderer(this));
		this.renderers.put(ShulkerEntity.class, new ShulkerEntityRenderer(this));
		this.renderers.put(PolarBearEntity.class, new PolarBearEntityRenderer(this));
		this.renderers.put(EvokerEntity.class, new EvokerIllagerEntityRenderer(this));
		this.renderers.put(VindicatorEntity.class, new VindicatorEntityRenderer(this));
		this.renderers.put(PillagerEntity.class, new PillagerEntityRenderer(this));
		this.renderers.put(IllagerBeastEntity.class, new IllagerBeastEntityRenderer(this));
		this.renderers.put(VexEntity.class, new VexEntityRenderer(this));
		this.renderers.put(IllusionerEntity.class, new IllusionerEntityRenderer(this));
		this.renderers.put(PhantomEntity.class, new PhantomEntityRenderer(this));
		this.renderers.put(PufferfishEntity.class, new PufferfishEntityRenderer(this));
		this.renderers.put(SalmonEntity.class, new SalmonEntityRenderer(this));
		this.renderers.put(CodEntity.class, new CodEntityRenderer(this));
		this.renderers.put(TropicalFishEntity.class, new TropicalFishEntityRenderer(this));
		this.renderers.put(DolphinEntity.class, new DolphinEntityRenderer(this));
		this.renderers.put(PandaEntity.class, new PandaEntityRenderer(this));
		this.renderers.put(CatEntity.class, new CatEntityRenderer(this));
		this.renderers.put(EnderDragonEntity.class, new EnderDragonEntityRenderer(this));
		this.renderers.put(EnderCrystalEntity.class, new EnderCrystalEntityRenderer(this));
		this.renderers.put(EntityWither.class, new WitherEntityRenderer(this));
		this.renderers.put(Entity.class, new DefaultEntityRenderer(this));
		this.renderers.put(PaintingEntity.class, new PaintingEntityRenderer(this));
		this.renderers.put(ItemFrameEntity.class, new ItemFrameEntityRenderer(this, itemRenderer));
		this.renderers.put(LeadKnotEntity.class, new LeashKnotEntityRenderer(this));
		this.renderers.put(ArrowEntity.class, new ArrowEntityRenderer(this));
		this.renderers.put(SpectralArrowEntity.class, new SpectralArrowEntityRenderer(this));
		this.renderers.put(TridentEntity.class, new TridentEntityRenderer(this));
		this.renderers.put(SnowballEntity.class, new SnowballEntityRenderer(this, Items.field_8543, itemRenderer));
		this.renderers.put(ThrownEnderpearlEntity.class, new SnowballEntityRenderer(this, Items.field_8634, itemRenderer));
		this.renderers.put(EnderEyeEntity.class, new SnowballEntityRenderer(this, Items.field_8449, itemRenderer));
		this.renderers.put(ThrownEggEntity.class, new SnowballEntityRenderer(this, Items.field_8803, itemRenderer));
		this.renderers.put(ThrownPotionEntity.class, new ThrownPotionEntityRenderer(this, itemRenderer));
		this.renderers.put(ThrownExperienceBottleEntity.class, new SnowballEntityRenderer(this, Items.field_8287, itemRenderer));
		this.renderers.put(FireworkEntity.class, new FireworkEntityRenderer(this, Items.field_8639, itemRenderer));
		this.renderers.put(FireballEntity.class, new ExplosiveProjectileEntityRenderer(this, 2.0F));
		this.renderers.put(SmallFireballEntity.class, new ExplosiveProjectileEntityRenderer(this, 0.5F));
		this.renderers.put(DragonFireballEntity.class, new DragonFireballEntityRenderer(this));
		this.renderers.put(ExplodingWitherSkullEntity.class, new ExplodingWitherSkullEntityRenderer(this));
		this.renderers.put(ShulkerBulletEntity.class, new ShulkerBulletEntityRenderer(this));
		this.renderers.put(ItemEntity.class, new ItemEntityRenderer(this, itemRenderer));
		this.renderers.put(ExperienceOrbEntity.class, new ExperienceOrbEntityRenderer(this));
		this.renderers.put(PrimedTNTEntity.class, new TNTPrimedEntityRenderer(this));
		this.renderers.put(FallingBlockEntity.class, new FallingBlockEntityRenderer(this));
		this.renderers.put(ArmorStandEntity.class, new ArmorStandEntityRenderer(this));
		this.renderers.put(EvokerFangsEntity.class, new EvokerFangsEntityRenderer(this));
		this.renderers.put(TNTMinecartEntity.class, new MinecartTNTEntityRenderer(this));
		this.renderers.put(MobSpawnerMinecartEntity.class, new MinecartSpawnerEntityRenderer(this));
		this.renderers.put(AbstractMinecartEntity.class, new MinecartEntityRenderer(this));
		this.renderers.put(BoatEntity.class, new BoatEntityRenderer(this));
		this.renderers.put(FishHookEntity.class, new FishHookEntityRenderer(this));
		this.renderers.put(AreaEffectCloudEntity.class, new AreaEffectCloudEntityRenderer(this));
		this.renderers.put(HorseEntity.class, new HorseEntityRenderer(this));
		this.renderers.put(SkeletonHorseEntity.class, new ZombieHorseEntityRenderer(this));
		this.renderers.put(ZombieHorseEntity.class, new ZombieHorseEntityRenderer(this));
		this.renderers.put(MuleEntity.class, new DonkeyEntityRenderer(this, 0.92F));
		this.renderers.put(DonkeyEntity.class, new DonkeyEntityRenderer(this, 0.87F));
		this.renderers.put(LlamaEntity.class, new LlamaEntityRenderer(this));
		this.renderers.put(LlamaSpitEntity.class, new LlamaSpitEntityRenderer(this));
		this.renderers.put(LightningEntity.class, new LightningEntityRenderer(this));
		this.playerRenderer = new PlayerEntityRenderer(this);
		this.skinMap.put("default", this.playerRenderer);
		this.skinMap.put("slim", new PlayerEntityRenderer(this, true));
	}

	public void setRenderPosition(double d, double e, double f) {
		this.renderPosX = d;
		this.renderPosY = e;
		this.renderPosZ = f;
	}

	public <T extends Entity> EntityRenderer<T> getRenderer(Class<? extends Entity> class_) {
		EntityRenderer<? extends Entity> entityRenderer = (EntityRenderer<? extends Entity>)this.renderers.get(class_);
		if (entityRenderer == null && class_ != Entity.class) {
			entityRenderer = this.getRenderer(class_.getSuperclass());
			this.renderers.put(class_, entityRenderer);
		}

		return (EntityRenderer<T>)entityRenderer;
	}

	@Nullable
	public <T extends Entity> EntityRenderer<T> getRenderer(Entity entity) {
		if (entity instanceof AbstractClientPlayerEntity) {
			String string = ((AbstractClientPlayerEntity)entity).method_3121();
			PlayerEntityRenderer playerEntityRenderer = (PlayerEntityRenderer)this.skinMap.get(string);
			return playerEntityRenderer != null ? playerEntityRenderer : this.playerRenderer;
		} else {
			return this.getRenderer(entity.getClass());
		}
	}

	public void method_3941(World world, FontRenderer fontRenderer, Entity entity, Entity entity2, GameOptions gameOptions, float f) {
		this.world = world;
		this.settings = gameOptions;
		this.field_4686 = entity;
		this.field_4678 = entity2;
		this.fontRenderer = fontRenderer;
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

		if (gameOptions.field_1850 == 2) {
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

	public boolean method_3950(Entity entity, class_856 arg, double d, double e, double f) {
		EntityRenderer<Entity> entityRenderer = this.getRenderer(entity);
		return entityRenderer != null && entityRenderer.method_3933(entity, arg, d, e, f);
	}

	public void method_3946(Entity entity, float f, boolean bl) {
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
		this.method_3954(entity, d - this.renderPosX, e - this.renderPosY, g - this.renderPosZ, h, f, bl);
	}

	public void method_3954(Entity entity, double d, double e, double f, float g, float h, boolean bl) {
		EntityRenderer<Entity> entityRenderer = null;

		try {
			entityRenderer = this.getRenderer(entity);
			if (entityRenderer != null && this.textureManager != null) {
				try {
					entityRenderer.method_3927(this.field_4682);
					entityRenderer.method_3936(entity, d, e, f, g, h);
				} catch (Throwable var17) {
					throw new CrashException(CrashReport.create(var17, "Rendering entity in world"));
				}

				try {
					if (!this.field_4682) {
						entityRenderer.method_3939(entity, d, e, f, g, h);
					}
				} catch (Throwable var18) {
					throw new CrashException(CrashReport.create(var18, "Post-rendering entity in world"));
				}

				if (this.field_4680 && !entity.isInvisible() && !bl && !MinecraftClient.getInstance().hasReducedDebugInfo()) {
					try {
						this.method_3956(entity, d, e, f, g, h);
					} catch (Throwable var16) {
						throw new CrashException(CrashReport.create(var16, "Rendering entity hitbox in world"));
					}
				}
			}
		} catch (Throwable var19) {
			CrashReport crashReport = CrashReport.create(var19, "Rendering entity in world");
			CrashReportElement crashReportElement = crashReport.addElement("Entity being rendered");
			entity.populateCrashReport(crashReportElement);
			CrashReportElement crashReportElement2 = crashReport.addElement("Renderer details");
			crashReportElement2.add("Assigned renderer", entityRenderer);
			crashReportElement2.add("Location", CrashReportElement.method_583(d, e, f));
			crashReportElement2.add("Rotation", g);
			crashReportElement2.add("Delta", h);
			throw new CrashException(crashReport);
		}
	}

	public void method_3947(Entity entity, float f) {
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
			entityRenderer.method_3937(entity, d - this.renderPosX, e - this.renderPosY, g - this.renderPosZ, h, f);
		}
	}

	private void method_3956(Entity entity, double d, double e, double f, float g, float h) {
		GlStateManager.depthMask(false);
		GlStateManager.disableTexture();
		GlStateManager.disableLighting();
		GlStateManager.disableCull();
		GlStateManager.disableBlend();
		float i = entity.width / 2.0F;
		BoundingBox boundingBox = entity.getBoundingBox();
		Renderer.method_3262(
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
		Entity[] entitys = entity.getParts();
		if (entitys != null) {
			for (Entity entity2 : entitys) {
				double j = (entity2.x - entity2.prevX) * (double)h;
				double k = (entity2.y - entity2.prevY) * (double)h;
				double l = (entity2.z - entity2.prevZ) * (double)h;
				BoundingBox boundingBox2 = entity2.getBoundingBox();
				Renderer.method_3262(
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
			Renderer.method_3262(
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
		VertexBuffer vertexBuffer = tessellator.getVertexBuffer();
		Vec3d vec3d = entity.getRotationVec(h);
		vertexBuffer.begin(3, VertexFormats.POSITION_COLOR);
		vertexBuffer.vertex(d, e + (double)entity.getEyeHeight(), f).color(0, 0, 255, 255).next();
		vertexBuffer.vertex(d + vec3d.x * 2.0, e + (double)entity.getEyeHeight() + vec3d.y * 2.0, f + vec3d.z * 2.0).color(0, 0, 255, 255).next();
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

	public FontRenderer getFontRenderer() {
		return this.fontRenderer;
	}

	public void method_3943(boolean bl) {
		this.field_4682 = bl;
	}
}
