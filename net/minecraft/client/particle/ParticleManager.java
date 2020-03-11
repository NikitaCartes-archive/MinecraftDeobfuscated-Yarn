/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.particle;

import com.google.common.base.Charsets;
import com.google.common.collect.EvictingQueue;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.particle.AshParticle;
import net.minecraft.client.particle.BarrierParticle;
import net.minecraft.client.particle.BlockDustParticle;
import net.minecraft.client.particle.BlockFallingDustParticle;
import net.minecraft.client.particle.BlockLeakParticle;
import net.minecraft.client.particle.BubbleColumnUpParticle;
import net.minecraft.client.particle.BubblePopParticle;
import net.minecraft.client.particle.CampfireSmokeParticle;
import net.minecraft.client.particle.CloudParticle;
import net.minecraft.client.particle.CrackParticle;
import net.minecraft.client.particle.CurrentDownParticle;
import net.minecraft.client.particle.DamageParticle;
import net.minecraft.client.particle.DragonBreathParticle;
import net.minecraft.client.particle.ElderGuardianAppearanceParticle;
import net.minecraft.client.particle.EmitterParticle;
import net.minecraft.client.particle.EmotionParticle;
import net.minecraft.client.particle.EnchantGlyphParticle;
import net.minecraft.client.particle.EndRodParticle;
import net.minecraft.client.particle.ExplosionEmitterParticle;
import net.minecraft.client.particle.ExplosionLargeParticle;
import net.minecraft.client.particle.ExplosionSmokeParticle;
import net.minecraft.client.particle.FireSmokeParticle;
import net.minecraft.client.particle.FireworksSparkParticle;
import net.minecraft.client.particle.FishingParticle;
import net.minecraft.client.particle.FlameParticle;
import net.minecraft.client.particle.LargeFireSmokeParticle;
import net.minecraft.client.particle.LavaEmberParticle;
import net.minecraft.client.particle.NoteParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.ParticleTextureData;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.PortalParticle;
import net.minecraft.client.particle.RainSplashParticle;
import net.minecraft.client.particle.RedDustParticle;
import net.minecraft.client.particle.SoulParticle;
import net.minecraft.client.particle.SpellParticle;
import net.minecraft.client.particle.SpitParticle;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.particle.SquidInkParticle;
import net.minecraft.client.particle.SuspendParticle;
import net.minecraft.client.particle.SweepAttackParticle;
import net.minecraft.client.particle.TotemParticle;
import net.minecraft.client.particle.WaterBubbleParticle;
import net.minecraft.client.particle.WaterSplashParticle;
import net.minecraft.client.particle.WaterSuspendParticle;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloadListener;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class ParticleManager
implements ResourceReloadListener {
    private static final List<ParticleTextureSheet> PARTICLE_TEXTURE_SHEETS = ImmutableList.of(ParticleTextureSheet.TERRAIN_SHEET, ParticleTextureSheet.PARTICLE_SHEET_OPAQUE, ParticleTextureSheet.PARTICLE_SHEET_LIT, ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT, ParticleTextureSheet.CUSTOM);
    protected World world;
    private final Map<ParticleTextureSheet, Queue<Particle>> particles = Maps.newIdentityHashMap();
    private final Queue<EmitterParticle> newEmitterParticles = Queues.newArrayDeque();
    private final TextureManager textureManager;
    private final Random random = new Random();
    private final Int2ObjectMap<ParticleFactory<?>> factories = new Int2ObjectOpenHashMap();
    private final Queue<Particle> newParticles = Queues.newArrayDeque();
    private final Map<Identifier, SimpleSpriteProvider> spriteAwareFactories = Maps.newHashMap();
    private final SpriteAtlasTexture particleAtlasTexture = new SpriteAtlasTexture(SpriteAtlasTexture.PARTICLE_ATLAS_TEX);

    public ParticleManager(World world, TextureManager textureManager) {
        textureManager.registerTexture(this.particleAtlasTexture.getId(), this.particleAtlasTexture);
        this.world = world;
        this.textureManager = textureManager;
        this.registerDefaultFactories();
    }

    private void registerDefaultFactories() {
        this.registerFactory(ParticleTypes.AMBIENT_ENTITY_EFFECT, SpellParticle.EntityAmbientFactory::new);
        this.registerFactory(ParticleTypes.ANGRY_VILLAGER, EmotionParticle.AngryVillagerFactory::new);
        this.registerFactory(ParticleTypes.BARRIER, new BarrierParticle.Factory());
        this.registerFactory(ParticleTypes.BLOCK, new BlockDustParticle.Factory());
        this.registerFactory(ParticleTypes.BUBBLE, BubbleColumnUpParticle.Factory::new);
        this.registerFactory(ParticleTypes.BUBBLE_COLUMN_UP, WaterBubbleParticle.Factory::new);
        this.registerFactory(ParticleTypes.BUBBLE_POP, BubblePopParticle.Factory::new);
        this.registerFactory(ParticleTypes.CAMPFIRE_COSY_SMOKE, CampfireSmokeParticle.CosySmokeFactory::new);
        this.registerFactory(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE, CampfireSmokeParticle.SignalSmokeFactory::new);
        this.registerFactory(ParticleTypes.CLOUD, CloudParticle.CloudFactory::new);
        this.registerFactory(ParticleTypes.COMPOSTER, SuspendParticle.Factory::new);
        this.registerFactory(ParticleTypes.CRIT, DamageParticle.Factory::new);
        this.registerFactory(ParticleTypes.CURRENT_DOWN, CurrentDownParticle.Factory::new);
        this.registerFactory(ParticleTypes.DAMAGE_INDICATOR, DamageParticle.DefaultFactory::new);
        this.registerFactory(ParticleTypes.DRAGON_BREATH, DragonBreathParticle.Factory::new);
        this.registerFactory(ParticleTypes.DOLPHIN, SuspendParticle.DolphinFactory::new);
        this.registerFactory(ParticleTypes.DRIPPING_LAVA, BlockLeakParticle.DrippingLavaFactory::new);
        this.registerFactory(ParticleTypes.FALLING_LAVA, BlockLeakParticle.FallingLavaFactory::new);
        this.registerFactory(ParticleTypes.LANDING_LAVA, BlockLeakParticle.LandingLavaFactory::new);
        this.registerFactory(ParticleTypes.DRIPPING_WATER, BlockLeakParticle.DrippingWaterFactory::new);
        this.registerFactory(ParticleTypes.FALLING_WATER, BlockLeakParticle.FallingWaterFactory::new);
        this.registerFactory(ParticleTypes.DUST, RedDustParticle.Factory::new);
        this.registerFactory(ParticleTypes.EFFECT, SpellParticle.DefaultFactory::new);
        this.registerFactory(ParticleTypes.ELDER_GUARDIAN, new ElderGuardianAppearanceParticle.Factory());
        this.registerFactory(ParticleTypes.ENCHANTED_HIT, DamageParticle.EnchantedHitFactory::new);
        this.registerFactory(ParticleTypes.ENCHANT, EnchantGlyphParticle.EnchantFactory::new);
        this.registerFactory(ParticleTypes.END_ROD, EndRodParticle.Factory::new);
        this.registerFactory(ParticleTypes.ENTITY_EFFECT, SpellParticle.EntityFactory::new);
        this.registerFactory(ParticleTypes.EXPLOSION_EMITTER, new ExplosionEmitterParticle.Factory());
        this.registerFactory(ParticleTypes.EXPLOSION, ExplosionLargeParticle.Factory::new);
        this.registerFactory(ParticleTypes.FALLING_DUST, BlockFallingDustParticle.Factory::new);
        this.registerFactory(ParticleTypes.FIREWORK, FireworksSparkParticle.ExplosionFactory::new);
        this.registerFactory(ParticleTypes.FISHING, FishingParticle.Factory::new);
        this.registerFactory(ParticleTypes.FLAME, FlameParticle.Factory::new);
        this.registerFactory(ParticleTypes.SOUL, SoulParticle.Factory::new);
        this.registerFactory(ParticleTypes.SOUL_FIRE_FLAME, FlameParticle.Factory::new);
        this.registerFactory(ParticleTypes.FLASH, FireworksSparkParticle.FlashFactory::new);
        this.registerFactory(ParticleTypes.HAPPY_VILLAGER, SuspendParticle.HappyVillagerFactory::new);
        this.registerFactory(ParticleTypes.HEART, EmotionParticle.HeartFactory::new);
        this.registerFactory(ParticleTypes.INSTANT_EFFECT, SpellParticle.InstantFactory::new);
        this.registerFactory(ParticleTypes.ITEM, new CrackParticle.ItemFactory());
        this.registerFactory(ParticleTypes.ITEM_SLIME, new CrackParticle.SlimeballFactory());
        this.registerFactory(ParticleTypes.ITEM_SNOWBALL, new CrackParticle.SnowballFactory());
        this.registerFactory(ParticleTypes.LARGE_SMOKE, LargeFireSmokeParticle.Factory::new);
        this.registerFactory(ParticleTypes.LAVA, LavaEmberParticle.Factory::new);
        this.registerFactory(ParticleTypes.MYCELIUM, SuspendParticle.MyceliumFactory::new);
        this.registerFactory(ParticleTypes.NAUTILUS, EnchantGlyphParticle.NautilusFactory::new);
        this.registerFactory(ParticleTypes.NOTE, NoteParticle.Factory::new);
        this.registerFactory(ParticleTypes.POOF, ExplosionSmokeParticle.Factory::new);
        this.registerFactory(ParticleTypes.PORTAL, PortalParticle.Factory::new);
        this.registerFactory(ParticleTypes.RAIN, RainSplashParticle.Factory::new);
        this.registerFactory(ParticleTypes.SMOKE, FireSmokeParticle.Factory::new);
        this.registerFactory(ParticleTypes.SNEEZE, CloudParticle.SneezeFactory::new);
        this.registerFactory(ParticleTypes.SPIT, SpitParticle.Factory::new);
        this.registerFactory(ParticleTypes.SWEEP_ATTACK, SweepAttackParticle.Factory::new);
        this.registerFactory(ParticleTypes.TOTEM_OF_UNDYING, TotemParticle.Factory::new);
        this.registerFactory(ParticleTypes.SQUID_INK, SquidInkParticle.Factory::new);
        this.registerFactory(ParticleTypes.UNDERWATER, WaterSuspendParticle.UnderwaterFactory::new);
        this.registerFactory(ParticleTypes.SPLASH, WaterSplashParticle.SplashFactory::new);
        this.registerFactory(ParticleTypes.WITCH, SpellParticle.WitchFactory::new);
        this.registerFactory(ParticleTypes.DRIPPING_HONEY, BlockLeakParticle.DrippingHoneyFactory::new);
        this.registerFactory(ParticleTypes.FALLING_HONEY, BlockLeakParticle.FallingHoneyFactory::new);
        this.registerFactory(ParticleTypes.LANDING_HONEY, BlockLeakParticle.LandingHoneyFactory::new);
        this.registerFactory(ParticleTypes.FALLING_NECTAR, BlockLeakParticle.FallingNectarFactory::new);
        this.registerFactory(ParticleTypes.ASH, AshParticle.Factory::new);
        this.registerFactory(ParticleTypes.CRIMSON_SPORE, WaterSuspendParticle.CrimsonSporeFactory::new);
        this.registerFactory(ParticleTypes.WARPED_SPORE, WaterSuspendParticle.WarpedSporeFactory::new);
        this.registerFactory(ParticleTypes.DRIPPING_OBSIDIAN_TEAR, BlockLeakParticle.DrippingObsidianTearFactory::new);
        this.registerFactory(ParticleTypes.FALLING_OBSIDIAN_TEAR, BlockLeakParticle.FallingObsidianTearFactory::new);
        this.registerFactory(ParticleTypes.LANDING_OBSIDIAN_TEAR, BlockLeakParticle.LandingObsidianTearFactory::new);
    }

    private <T extends ParticleEffect> void registerFactory(ParticleType<T> type, ParticleFactory<T> factory) {
        this.factories.put(Registry.PARTICLE_TYPE.getRawId(type), (ParticleFactory<?>)factory);
    }

    private <T extends ParticleEffect> void registerFactory(ParticleType<T> particleType, SpriteAwareFactory<T> spriteAwareFactory) {
        SimpleSpriteProvider simpleSpriteProvider = new SimpleSpriteProvider();
        this.spriteAwareFactories.put(Registry.PARTICLE_TYPE.getId(particleType), simpleSpriteProvider);
        this.factories.put(Registry.PARTICLE_TYPE.getRawId(particleType), (ParticleFactory<?>)spriteAwareFactory.create(simpleSpriteProvider));
    }

    @Override
    public CompletableFuture<Void> reload(ResourceReloadListener.Synchronizer synchronizer, ResourceManager manager, Profiler prepareProfiler, Profiler applyProfiler, Executor prepareExecutor, Executor applyExecutor) {
        ConcurrentMap map = Maps.newConcurrentMap();
        CompletableFuture[] completableFutures = (CompletableFuture[])Registry.PARTICLE_TYPE.getIds().stream().map(identifier -> CompletableFuture.runAsync(() -> this.loadTextureList(manager, (Identifier)identifier, map), prepareExecutor)).toArray(CompletableFuture[]::new);
        return ((CompletableFuture)((CompletableFuture)CompletableFuture.allOf(completableFutures).thenApplyAsync(void_ -> {
            prepareProfiler.startTick();
            prepareProfiler.push("stitching");
            SpriteAtlasTexture.Data data = this.particleAtlasTexture.stitch(manager, map.values().stream().flatMap(Collection::stream), prepareProfiler, 0);
            prepareProfiler.pop();
            prepareProfiler.endTick();
            return data;
        }, prepareExecutor)).thenCompose(synchronizer::whenPrepared)).thenAcceptAsync(data -> {
            this.particles.clear();
            applyProfiler.startTick();
            applyProfiler.push("upload");
            this.particleAtlasTexture.upload((SpriteAtlasTexture.Data)data);
            applyProfiler.swap("bindSpriteSets");
            Sprite sprite = this.particleAtlasTexture.getSprite(MissingSprite.getMissingSpriteId());
            map.forEach((identifier, list) -> {
                ImmutableList<Sprite> immutableList = list.isEmpty() ? ImmutableList.of(sprite) : list.stream().map(this.particleAtlasTexture::getSprite).collect(ImmutableList.toImmutableList());
                this.spriteAwareFactories.get(identifier).setSprites(immutableList);
            });
            applyProfiler.pop();
            applyProfiler.endTick();
        }, applyExecutor);
    }

    public void clearAtlas() {
        this.particleAtlasTexture.clear();
    }

    private void loadTextureList(ResourceManager resourceManager, Identifier id, Map<Identifier, List<Identifier>> result) {
        Identifier identifier2 = new Identifier(id.getNamespace(), "particles/" + id.getPath() + ".json");
        try (Resource resource = resourceManager.getResource(identifier2);
             InputStreamReader reader = new InputStreamReader(resource.getInputStream(), Charsets.UTF_8);){
            ParticleTextureData particleTextureData = ParticleTextureData.load(JsonHelper.deserialize(reader));
            List<Identifier> list = particleTextureData.getTextureList();
            boolean bl = this.spriteAwareFactories.containsKey(id);
            if (list == null) {
                if (bl) {
                    throw new IllegalStateException("Missing texture list for particle " + id);
                }
            } else {
                if (!bl) {
                    throw new IllegalStateException("Redundant texture list for particle " + id);
                }
                result.put(id, list.stream().map(identifier -> new Identifier(identifier.getNamespace(), "particle/" + identifier.getPath())).collect(Collectors.toList()));
            }
        } catch (IOException iOException) {
            throw new IllegalStateException("Failed to load description for particle " + id, iOException);
        }
    }

    public void addEmitter(Entity entity, ParticleEffect parameters) {
        this.newEmitterParticles.add(new EmitterParticle(this.world, entity, parameters));
    }

    public void addEmitter(Entity entity, ParticleEffect parameters, int maxAge) {
        this.newEmitterParticles.add(new EmitterParticle(this.world, entity, parameters, maxAge));
    }

    @Nullable
    public Particle addParticle(ParticleEffect parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        Particle particle = this.createParticle(parameters, x, y, z, velocityX, velocityY, velocityZ);
        if (particle != null) {
            this.addParticle(particle);
            return particle;
        }
        return null;
    }

    @Nullable
    private <T extends ParticleEffect> Particle createParticle(T parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        ParticleFactory particleFactory = (ParticleFactory)this.factories.get(Registry.PARTICLE_TYPE.getRawId(parameters.getType()));
        if (particleFactory == null) {
            return null;
        }
        return particleFactory.createParticle(parameters, this.world, x, y, z, velocityX, velocityY, velocityZ);
    }

    public void addParticle(Particle particle) {
        this.newParticles.add(particle);
    }

    public void tick() {
        this.particles.forEach((particleTextureSheet, queue) -> {
            this.world.getProfiler().push(particleTextureSheet.toString());
            this.tickParticles((Collection<Particle>)queue);
            this.world.getProfiler().pop();
        });
        if (!this.newEmitterParticles.isEmpty()) {
            ArrayList<EmitterParticle> list = Lists.newArrayList();
            for (EmitterParticle emitterParticle : this.newEmitterParticles) {
                emitterParticle.tick();
                if (emitterParticle.isAlive()) continue;
                list.add(emitterParticle);
            }
            this.newEmitterParticles.removeAll(list);
        }
        if (!this.newParticles.isEmpty()) {
            Particle particle;
            while ((particle = this.newParticles.poll()) != null) {
                this.particles.computeIfAbsent(particle.getType(), particleTextureSheet -> EvictingQueue.create(16384)).add(particle);
            }
        }
    }

    private void tickParticles(Collection<Particle> collection) {
        if (!collection.isEmpty()) {
            Iterator<Particle> iterator = collection.iterator();
            while (iterator.hasNext()) {
                Particle particle = iterator.next();
                this.tickParticle(particle);
                if (particle.isAlive()) continue;
                iterator.remove();
            }
        }
    }

    private void tickParticle(Particle particle) {
        try {
            particle.tick();
        } catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.create(throwable, "Ticking Particle");
            CrashReportSection crashReportSection = crashReport.addElement("Particle being ticked");
            crashReportSection.add("Particle", particle::toString);
            crashReportSection.add("Particle Type", particle.getType()::toString);
            throw new CrashException(crashReport);
        }
    }

    public void renderParticles(MatrixStack matrixStack, VertexConsumerProvider.Immediate immediate, LightmapTextureManager lightmapTextureManager, Camera camera, float f) {
        lightmapTextureManager.enable();
        RenderSystem.enableAlphaTest();
        RenderSystem.defaultAlphaFunc();
        RenderSystem.enableDepthTest();
        RenderSystem.enableFog();
        RenderSystem.pushMatrix();
        RenderSystem.multMatrix(matrixStack.peek().getModel());
        for (ParticleTextureSheet particleTextureSheet : PARTICLE_TEXTURE_SHEETS) {
            Iterable iterable = this.particles.get(particleTextureSheet);
            if (iterable == null) continue;
            RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferBuilder = tessellator.getBuffer();
            particleTextureSheet.begin(bufferBuilder, this.textureManager);
            for (Particle particle : iterable) {
                try {
                    particle.buildGeometry(bufferBuilder, camera, f);
                } catch (Throwable throwable) {
                    CrashReport crashReport = CrashReport.create(throwable, "Rendering Particle");
                    CrashReportSection crashReportSection = crashReport.addElement("Particle being rendered");
                    crashReportSection.add("Particle", particle::toString);
                    crashReportSection.add("Particle Type", particleTextureSheet::toString);
                    throw new CrashException(crashReport);
                }
            }
            particleTextureSheet.draw(tessellator);
        }
        RenderSystem.popMatrix();
        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
        RenderSystem.defaultAlphaFunc();
        lightmapTextureManager.disable();
        RenderSystem.disableFog();
    }

    public void setWorld(@Nullable World world) {
        this.world = world;
        this.particles.clear();
        this.newEmitterParticles.clear();
    }

    public void addBlockBreakParticles(BlockPos pos, BlockState state) {
        if (state.isAir()) {
            return;
        }
        VoxelShape voxelShape = state.getOutlineShape(this.world, pos);
        double d2 = 0.25;
        voxelShape.forEachBox((d, e, f, g, h, i) -> {
            double j = Math.min(1.0, g - d);
            double k = Math.min(1.0, h - e);
            double l = Math.min(1.0, i - f);
            int m = Math.max(2, MathHelper.ceil(j / 0.25));
            int n = Math.max(2, MathHelper.ceil(k / 0.25));
            int o = Math.max(2, MathHelper.ceil(l / 0.25));
            for (int p = 0; p < m; ++p) {
                for (int q = 0; q < n; ++q) {
                    for (int r = 0; r < o; ++r) {
                        double s = ((double)p + 0.5) / (double)m;
                        double t = ((double)q + 0.5) / (double)n;
                        double u = ((double)r + 0.5) / (double)o;
                        double v = s * j + d;
                        double w = t * k + e;
                        double x = u * l + f;
                        this.addParticle(new BlockDustParticle(this.world, (double)pos.getX() + v, (double)pos.getY() + w, (double)pos.getZ() + x, s - 0.5, t - 0.5, u - 0.5, state).setBlockPos(pos));
                    }
                }
            }
        });
    }

    public void addBlockBreakingParticles(BlockPos blockPos, Direction direction) {
        BlockState blockState = this.world.getBlockState(blockPos);
        if (blockState.getRenderType() == BlockRenderType.INVISIBLE) {
            return;
        }
        int i = blockPos.getX();
        int j = blockPos.getY();
        int k = blockPos.getZ();
        float f = 0.1f;
        Box box = blockState.getOutlineShape(this.world, blockPos).getBoundingBox();
        double d = (double)i + this.random.nextDouble() * (box.x2 - box.x1 - (double)0.2f) + (double)0.1f + box.x1;
        double e = (double)j + this.random.nextDouble() * (box.y2 - box.y1 - (double)0.2f) + (double)0.1f + box.y1;
        double g = (double)k + this.random.nextDouble() * (box.z2 - box.z1 - (double)0.2f) + (double)0.1f + box.z1;
        if (direction == Direction.DOWN) {
            e = (double)j + box.y1 - (double)0.1f;
        }
        if (direction == Direction.UP) {
            e = (double)j + box.y2 + (double)0.1f;
        }
        if (direction == Direction.NORTH) {
            g = (double)k + box.z1 - (double)0.1f;
        }
        if (direction == Direction.SOUTH) {
            g = (double)k + box.z2 + (double)0.1f;
        }
        if (direction == Direction.WEST) {
            d = (double)i + box.x1 - (double)0.1f;
        }
        if (direction == Direction.EAST) {
            d = (double)i + box.x2 + (double)0.1f;
        }
        this.addParticle(new BlockDustParticle(this.world, d, e, g, 0.0, 0.0, 0.0, blockState).setBlockPos(blockPos).move(0.2f).scale(0.6f));
    }

    public String getDebugString() {
        return String.valueOf(this.particles.values().stream().mapToInt(Collection::size).sum());
    }

    @Environment(value=EnvType.CLIENT)
    class SimpleSpriteProvider
    implements SpriteProvider {
        private List<Sprite> sprites;

        private SimpleSpriteProvider() {
        }

        @Override
        public Sprite getSprite(int i, int j) {
            return this.sprites.get(i * (this.sprites.size() - 1) / j);
        }

        @Override
        public Sprite getSprite(Random random) {
            return this.sprites.get(random.nextInt(this.sprites.size()));
        }

        public void setSprites(List<Sprite> sprites) {
            this.sprites = ImmutableList.copyOf(sprites);
        }
    }

    @FunctionalInterface
    @Environment(value=EnvType.CLIENT)
    static interface SpriteAwareFactory<T extends ParticleEffect> {
        public ParticleFactory<T> create(SpriteProvider var1);
    }
}

