package net.minecraft.client.world;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BrushableBlock;
import net.minecraft.block.ComposterBlock;
import net.minecraft.block.MultifaceGrowthBlock;
import net.minecraft.block.PointedDripstoneBlock;
import net.minecraft.block.SculkShriekerBlock;
import net.minecraft.block.entity.VaultBlockEntity;
import net.minecraft.block.jukebox.JukeboxSong;
import net.minecraft.block.spawner.TrialSpawnerLogic;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BoneMealItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.particle.ParticleUtil;
import net.minecraft.particle.SculkChargeParticleEffect;
import net.minecraft.particle.ShriekParticleEffect;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;

@Environment(EnvType.CLIENT)
public class WorldEventHandler {
	private final MinecraftClient client;
	private final World world;
	private final WorldRenderer worldRenderer;
	private final Map<BlockPos, SoundInstance> playingSongs = new HashMap();

	public WorldEventHandler(MinecraftClient client, World world, WorldRenderer worldRenderer) {
		this.client = client;
		this.world = world;
		this.worldRenderer = worldRenderer;
	}

	public void processGlobalEvent(int eventId, BlockPos pos, int data) {
		switch (eventId) {
			case 1023:
			case 1028:
			case 1038:
				Camera camera = this.client.gameRenderer.getCamera();
				if (camera.isReady()) {
					Vec3d vec3d = Vec3d.ofCenter(pos).subtract(camera.getPos()).normalize();
					Vec3d vec3d2 = camera.getPos().add(vec3d.multiply(2.0));
					if (eventId == WorldEvents.WITHER_SPAWNS) {
						this.world.playSound(vec3d2.x, vec3d2.y, vec3d2.z, SoundEvents.ENTITY_WITHER_SPAWN, SoundCategory.HOSTILE, 1.0F, 1.0F, false);
					} else if (eventId == WorldEvents.END_PORTAL_OPENED) {
						this.world.playSound(vec3d2.x, vec3d2.y, vec3d2.z, SoundEvents.BLOCK_END_PORTAL_SPAWN, SoundCategory.HOSTILE, 1.0F, 1.0F, false);
					} else {
						this.world.playSound(vec3d2.x, vec3d2.y, vec3d2.z, SoundEvents.ENTITY_ENDER_DRAGON_DEATH, SoundCategory.HOSTILE, 5.0F, 1.0F, false);
					}
				}
		}
	}

	public void processWorldEvent(int eventId, BlockPos pos, int data) {
		Random random = this.world.random;
		switch (eventId) {
			case 1000:
				this.world.playSoundAtBlockCenter(pos, SoundEvents.BLOCK_DISPENSER_DISPENSE, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
				break;
			case 1001:
				this.world.playSoundAtBlockCenter(pos, SoundEvents.BLOCK_DISPENSER_FAIL, SoundCategory.BLOCKS, 1.0F, 1.2F, false);
				break;
			case 1002:
				this.world.playSoundAtBlockCenter(pos, SoundEvents.BLOCK_DISPENSER_LAUNCH, SoundCategory.BLOCKS, 1.0F, 1.2F, false);
				break;
			case 1004:
				this.world.playSoundAtBlockCenter(pos, SoundEvents.ENTITY_FIREWORK_ROCKET_SHOOT, SoundCategory.NEUTRAL, 1.0F, 1.2F, false);
				break;
			case 1009:
				if (data == 0) {
					this.world
						.playSoundAtBlockCenter(
							pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5F, 2.6F + (random.nextFloat() - random.nextFloat()) * 0.8F, false
						);
				} else if (data == 1) {
					this.world
						.playSoundAtBlockCenter(
							pos, SoundEvents.ENTITY_GENERIC_EXTINGUISH_FIRE, SoundCategory.BLOCKS, 0.7F, 1.6F + (random.nextFloat() - random.nextFloat()) * 0.4F, false
						);
				}
				break;
			case 1010:
				this.world.getRegistryManager().getOrThrow(RegistryKeys.JUKEBOX_SONG).getEntry(data).ifPresent(song -> this.playJukeboxSong(song, pos));
				break;
			case 1011:
				this.stopJukeboxSongAndUpdate(pos);
				break;
			case 1015:
				this.world
					.playSoundAtBlockCenter(pos, SoundEvents.ENTITY_GHAST_WARN, SoundCategory.HOSTILE, 10.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
				break;
			case 1016:
				this.world
					.playSoundAtBlockCenter(pos, SoundEvents.ENTITY_GHAST_SHOOT, SoundCategory.HOSTILE, 10.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
				break;
			case 1017:
				this.world
					.playSoundAtBlockCenter(
						pos, SoundEvents.ENTITY_ENDER_DRAGON_SHOOT, SoundCategory.HOSTILE, 10.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false
					);
				break;
			case 1018:
				this.world
					.playSoundAtBlockCenter(pos, SoundEvents.ENTITY_BLAZE_SHOOT, SoundCategory.HOSTILE, 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
				break;
			case 1019:
				this.world
					.playSoundAtBlockCenter(
						pos, SoundEvents.ENTITY_ZOMBIE_ATTACK_WOODEN_DOOR, SoundCategory.HOSTILE, 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false
					);
				break;
			case 1020:
				this.world
					.playSoundAtBlockCenter(
						pos, SoundEvents.ENTITY_ZOMBIE_ATTACK_IRON_DOOR, SoundCategory.HOSTILE, 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false
					);
				break;
			case 1021:
				this.world
					.playSoundAtBlockCenter(
						pos, SoundEvents.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, SoundCategory.HOSTILE, 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false
					);
				break;
			case 1022:
				this.world
					.playSoundAtBlockCenter(
						pos, SoundEvents.ENTITY_WITHER_BREAK_BLOCK, SoundCategory.HOSTILE, 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false
					);
				break;
			case 1024:
				this.world
					.playSoundAtBlockCenter(pos, SoundEvents.ENTITY_WITHER_SHOOT, SoundCategory.HOSTILE, 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
				break;
			case 1025:
				this.world
					.playSoundAtBlockCenter(pos, SoundEvents.ENTITY_BAT_TAKEOFF, SoundCategory.NEUTRAL, 0.05F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
				break;
			case 1026:
				this.world
					.playSoundAtBlockCenter(pos, SoundEvents.ENTITY_ZOMBIE_INFECT, SoundCategory.HOSTILE, 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
				break;
			case 1027:
				this.world
					.playSoundAtBlockCenter(
						pos, SoundEvents.ENTITY_ZOMBIE_VILLAGER_CONVERTED, SoundCategory.HOSTILE, 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false
					);
				break;
			case 1029:
				this.world.playSoundAtBlockCenter(pos, SoundEvents.BLOCK_ANVIL_DESTROY, SoundCategory.BLOCKS, 1.0F, random.nextFloat() * 0.1F + 0.9F, false);
				break;
			case 1030:
				this.world.playSoundAtBlockCenter(pos, SoundEvents.BLOCK_ANVIL_USE, SoundCategory.BLOCKS, 1.0F, random.nextFloat() * 0.1F + 0.9F, false);
				break;
			case 1031:
				this.world.playSoundAtBlockCenter(pos, SoundEvents.BLOCK_ANVIL_LAND, SoundCategory.BLOCKS, 0.3F, this.world.random.nextFloat() * 0.1F + 0.9F, false);
				break;
			case 1032:
				this.client.getSoundManager().play(PositionedSoundInstance.ambient(SoundEvents.BLOCK_PORTAL_TRAVEL, random.nextFloat() * 0.4F + 0.8F, 0.25F));
				break;
			case 1033:
				this.world.playSoundAtBlockCenter(pos, SoundEvents.BLOCK_CHORUS_FLOWER_GROW, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
				break;
			case 1034:
				this.world.playSoundAtBlockCenter(pos, SoundEvents.BLOCK_CHORUS_FLOWER_DEATH, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
				break;
			case 1035:
				this.world.playSoundAtBlockCenter(pos, SoundEvents.BLOCK_BREWING_STAND_BREW, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
				break;
			case 1039:
				this.world.playSoundAtBlockCenter(pos, SoundEvents.ENTITY_PHANTOM_BITE, SoundCategory.HOSTILE, 0.3F, this.world.random.nextFloat() * 0.1F + 0.9F, false);
				break;
			case 1040:
				this.world
					.playSoundAtBlockCenter(
						pos, SoundEvents.ENTITY_ZOMBIE_CONVERTED_TO_DROWNED, SoundCategory.HOSTILE, 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false
					);
				break;
			case 1041:
				this.world
					.playSoundAtBlockCenter(
						pos, SoundEvents.ENTITY_HUSK_CONVERTED_TO_ZOMBIE, SoundCategory.HOSTILE, 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false
					);
				break;
			case 1042:
				this.world.playSoundAtBlockCenter(pos, SoundEvents.BLOCK_GRINDSTONE_USE, SoundCategory.BLOCKS, 1.0F, this.world.random.nextFloat() * 0.1F + 0.9F, false);
				break;
			case 1043:
				this.world.playSoundAtBlockCenter(pos, SoundEvents.ITEM_BOOK_PAGE_TURN, SoundCategory.BLOCKS, 1.0F, this.world.random.nextFloat() * 0.1F + 0.9F, false);
				break;
			case 1044:
				this.world
					.playSoundAtBlockCenter(pos, SoundEvents.BLOCK_SMITHING_TABLE_USE, SoundCategory.BLOCKS, 1.0F, this.world.random.nextFloat() * 0.1F + 0.9F, false);
				break;
			case 1045:
				this.world
					.playSoundAtBlockCenter(pos, SoundEvents.BLOCK_POINTED_DRIPSTONE_LAND, SoundCategory.BLOCKS, 2.0F, this.world.random.nextFloat() * 0.1F + 0.9F, false);
				break;
			case 1046:
				this.world
					.playSoundAtBlockCenter(
						pos, SoundEvents.BLOCK_POINTED_DRIPSTONE_DRIP_LAVA_INTO_CAULDRON, SoundCategory.BLOCKS, 2.0F, this.world.random.nextFloat() * 0.1F + 0.9F, false
					);
				break;
			case 1047:
				this.world
					.playSoundAtBlockCenter(
						pos, SoundEvents.BLOCK_POINTED_DRIPSTONE_DRIP_WATER_INTO_CAULDRON, SoundCategory.BLOCKS, 2.0F, this.world.random.nextFloat() * 0.1F + 0.9F, false
					);
				break;
			case 1048:
				this.world
					.playSoundAtBlockCenter(
						pos, SoundEvents.ENTITY_SKELETON_CONVERTED_TO_STRAY, SoundCategory.HOSTILE, 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false
					);
				break;
			case 1049:
				this.world.playSoundAtBlockCenter(pos, SoundEvents.BLOCK_CRAFTER_CRAFT, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
				break;
			case 1050:
				this.world.playSoundAtBlockCenter(pos, SoundEvents.BLOCK_CRAFTER_FAIL, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
				break;
			case 1051:
				this.world
					.playSoundAtBlockCenter(
						pos, SoundEvents.ENTITY_WIND_CHARGE_THROW, SoundCategory.BLOCKS, 0.5F, 0.4F / (this.world.getRandom().nextFloat() * 0.4F + 0.8F), false
					);
				break;
			case 1500:
				ComposterBlock.playEffects(this.world, pos, data > 0);
				break;
			case 1501:
				this.world
					.playSoundAtBlockCenter(pos, SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.BLOCKS, 0.5F, 2.6F + (random.nextFloat() - random.nextFloat()) * 0.8F, false);

				for (int mx = 0; mx < 8; mx++) {
					this.world
						.addParticle(
							ParticleTypes.LARGE_SMOKE, (double)pos.getX() + random.nextDouble(), (double)pos.getY() + 1.2, (double)pos.getZ() + random.nextDouble(), 0.0, 0.0, 0.0
						);
				}
				break;
			case 1502:
				this.world
					.playSoundAtBlockCenter(
						pos, SoundEvents.BLOCK_REDSTONE_TORCH_BURNOUT, SoundCategory.BLOCKS, 0.5F, 2.6F + (random.nextFloat() - random.nextFloat()) * 0.8F, false
					);

				for (int mx = 0; mx < 5; mx++) {
					double g = (double)pos.getX() + random.nextDouble() * 0.6 + 0.2;
					double n = (double)pos.getY() + random.nextDouble() * 0.6 + 0.2;
					double o = (double)pos.getZ() + random.nextDouble() * 0.6 + 0.2;
					this.world.addParticle(ParticleTypes.SMOKE, g, n, o, 0.0, 0.0, 0.0);
				}
				break;
			case 1503:
				this.world.playSoundAtBlockCenter(pos, SoundEvents.BLOCK_END_PORTAL_FRAME_FILL, SoundCategory.BLOCKS, 1.0F, 1.0F, false);

				for (int mx = 0; mx < 16; mx++) {
					double g = (double)pos.getX() + (5.0 + random.nextDouble() * 6.0) / 16.0;
					double n = (double)pos.getY() + 0.8125;
					double o = (double)pos.getZ() + (5.0 + random.nextDouble() * 6.0) / 16.0;
					this.world.addParticle(ParticleTypes.SMOKE, g, n, o, 0.0, 0.0, 0.0);
				}
				break;
			case 1504:
				PointedDripstoneBlock.createParticle(this.world, pos, this.world.getBlockState(pos));
				break;
			case 1505:
				BoneMealItem.createParticles(this.world, pos, data);
				this.world.playSoundAtBlockCenter(pos, SoundEvents.ITEM_BONE_MEAL_USE, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
				break;
			case 2000:
				this.shootParticles(data, pos, random, ParticleTypes.SMOKE);
				break;
			case 2001:
				BlockState blockState = Block.getStateFromRawId(data);
				if (!blockState.isAir()) {
					BlockSoundGroup blockSoundGroup = blockState.getSoundGroup();
					this.world
						.playSoundAtBlockCenter(
							pos, blockSoundGroup.getBreakSound(), SoundCategory.BLOCKS, (blockSoundGroup.getVolume() + 1.0F) / 2.0F, blockSoundGroup.getPitch() * 0.8F, false
						);
				}

				this.world.addBlockBreakParticles(pos, blockState);
				break;
			case 2002:
			case 2007:
				Vec3d vec3d = Vec3d.ofBottomCenter(pos);

				for (int j = 0; j < 8; j++) {
					this.worldRenderer
						.addParticle(
							new ItemStackParticleEffect(ParticleTypes.ITEM, new ItemStack(Items.SPLASH_POTION)),
							vec3d.x,
							vec3d.y,
							vec3d.z,
							random.nextGaussian() * 0.15,
							random.nextDouble() * 0.2,
							random.nextGaussian() * 0.15
						);
				}

				float h = (float)(data >> 16 & 0xFF) / 255.0F;
				float k = (float)(data >> 8 & 0xFF) / 255.0F;
				float l = (float)(data >> 0 & 0xFF) / 255.0F;
				ParticleEffect particleEffect = eventId == WorldEvents.INSTANT_SPLASH_POTION_SPLASHED ? ParticleTypes.INSTANT_EFFECT : ParticleTypes.EFFECT;

				for (int mx = 0; mx < 100; mx++) {
					double g = random.nextDouble() * 4.0;
					double n = random.nextDouble() * Math.PI * 2.0;
					double o = Math.cos(n) * g;
					double p = 0.01 + random.nextDouble() * 0.5;
					double q = Math.sin(n) * g;
					Particle particle = this.worldRenderer
						.spawnParticle(particleEffect, particleEffect.getType().shouldAlwaysSpawn(), vec3d.x + o * 0.1, vec3d.y + 0.3, vec3d.z + q * 0.1, o, p, q);
					if (particle != null) {
						float r = 0.75F + random.nextFloat() * 0.25F;
						particle.setColor(h * r, k * r, l * r);
						particle.move((float)g);
					}
				}

				this.world.playSoundAtBlockCenter(pos, SoundEvents.ENTITY_SPLASH_POTION_BREAK, SoundCategory.NEUTRAL, 1.0F, random.nextFloat() * 0.1F + 0.9F, false);
				break;
			case 2003:
				double d = (double)pos.getX() + 0.5;
				double e = (double)pos.getY();
				double f = (double)pos.getZ() + 0.5;

				for (int i = 0; i < 8; i++) {
					this.worldRenderer
						.addParticle(
							new ItemStackParticleEffect(ParticleTypes.ITEM, new ItemStack(Items.ENDER_EYE)),
							d,
							e,
							f,
							random.nextGaussian() * 0.15,
							random.nextDouble() * 0.2,
							random.nextGaussian() * 0.15
						);
				}

				for (double g = 0.0; g < Math.PI * 2; g += Math.PI / 20) {
					this.worldRenderer.addParticle(ParticleTypes.PORTAL, d + Math.cos(g) * 5.0, e - 0.4, f + Math.sin(g) * 5.0, Math.cos(g) * -5.0, 0.0, Math.sin(g) * -5.0);
					this.worldRenderer.addParticle(ParticleTypes.PORTAL, d + Math.cos(g) * 5.0, e - 0.4, f + Math.sin(g) * 5.0, Math.cos(g) * -7.0, 0.0, Math.sin(g) * -7.0);
				}
				break;
			case 2004:
				for (int sx = 0; sx < 20; sx++) {
					double t = (double)pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 2.0;
					double u = (double)pos.getY() + 0.5 + (random.nextDouble() - 0.5) * 2.0;
					double v = (double)pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 2.0;
					this.world.addParticle(ParticleTypes.SMOKE, t, u, v, 0.0, 0.0, 0.0);
					this.world.addParticle(ParticleTypes.FLAME, t, u, v, 0.0, 0.0, 0.0);
				}
				break;
			case 2006:
				for (int m = 0; m < 200; m++) {
					float ab = random.nextFloat() * 4.0F;
					float ag = random.nextFloat() * (float) (Math.PI * 2);
					double n = (double)(MathHelper.cos(ag) * ab);
					double o = 0.01 + random.nextDouble() * 0.5;
					double p = (double)(MathHelper.sin(ag) * ab);
					Particle particle2 = this.worldRenderer
						.spawnParticle(ParticleTypes.DRAGON_BREATH, false, (double)pos.getX() + n * 0.1, (double)pos.getY() + 0.3, (double)pos.getZ() + p * 0.1, n, o, p);
					if (particle2 != null) {
						particle2.move(ab);
					}
				}

				if (data == 1) {
					this.world.playSoundAtBlockCenter(pos, SoundEvents.ENTITY_DRAGON_FIREBALL_EXPLODE, SoundCategory.HOSTILE, 1.0F, random.nextFloat() * 0.1F + 0.9F, false);
				}
				break;
			case 2008:
				this.world.addParticle(ParticleTypes.EXPLOSION, (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, 0.0, 0.0, 0.0);
				break;
			case 2009:
				for (int mx = 0; mx < 8; mx++) {
					this.world
						.addParticle(
							ParticleTypes.CLOUD, (double)pos.getX() + random.nextDouble(), (double)pos.getY() + 1.2, (double)pos.getZ() + random.nextDouble(), 0.0, 0.0, 0.0
						);
				}
				break;
			case 2010:
				this.shootParticles(data, pos, random, ParticleTypes.WHITE_SMOKE);
				break;
			case 2011:
				ParticleUtil.spawnParticlesAround(this.world, pos, data, ParticleTypes.HAPPY_VILLAGER);
				break;
			case 2012:
				ParticleUtil.spawnParticlesAround(this.world, pos, data, ParticleTypes.HAPPY_VILLAGER);
				break;
			case 2013:
				ParticleUtil.spawnSmashAttackParticles(this.world, pos, data);
				break;
			case 3000:
				this.world.addParticle(ParticleTypes.EXPLOSION_EMITTER, true, (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, 0.0, 0.0, 0.0);
				this.world
					.playSoundAtBlockCenter(
						pos,
						SoundEvents.BLOCK_END_GATEWAY_SPAWN,
						SoundCategory.BLOCKS,
						10.0F,
						(1.0F + (this.world.random.nextFloat() - this.world.random.nextFloat()) * 0.2F) * 0.7F,
						false
					);
				break;
			case 3001:
				this.world
					.playSoundAtBlockCenter(pos, SoundEvents.ENTITY_ENDER_DRAGON_GROWL, SoundCategory.HOSTILE, 64.0F, 0.8F + this.world.random.nextFloat() * 0.3F, false);
				break;
			case 3002:
				if (data >= 0 && data < Direction.Axis.VALUES.length) {
					ParticleUtil.spawnParticle(Direction.Axis.VALUES[data], this.world, pos, 0.125, ParticleTypes.ELECTRIC_SPARK, UniformIntProvider.create(10, 19));
				} else {
					ParticleUtil.spawnParticle(this.world, pos, ParticleTypes.ELECTRIC_SPARK, UniformIntProvider.create(3, 5));
				}
				break;
			case 3003:
				ParticleUtil.spawnParticle(this.world, pos, ParticleTypes.WAX_ON, UniformIntProvider.create(3, 5));
				this.world.playSoundAtBlockCenter(pos, SoundEvents.ITEM_HONEYCOMB_WAX_ON, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
				break;
			case 3004:
				ParticleUtil.spawnParticle(this.world, pos, ParticleTypes.WAX_OFF, UniformIntProvider.create(3, 5));
				break;
			case 3005:
				ParticleUtil.spawnParticle(this.world, pos, ParticleTypes.SCRAPE, UniformIntProvider.create(3, 5));
				break;
			case 3006:
				int s = data >> 6;
				if (s > 0) {
					if (random.nextFloat() < 0.3F + (float)s * 0.1F) {
						float l = 0.15F + 0.02F * (float)s * (float)s * random.nextFloat();
						float w = 0.4F + 0.3F * (float)s * random.nextFloat();
						this.world.playSoundAtBlockCenter(pos, SoundEvents.BLOCK_SCULK_CHARGE, SoundCategory.BLOCKS, l, w, false);
					}

					byte b = (byte)(data & 63);
					IntProvider intProvider = UniformIntProvider.create(0, s);
					float x = 0.005F;
					Supplier<Vec3d> supplier = () -> new Vec3d(
							MathHelper.nextDouble(random, -0.005F, 0.005F), MathHelper.nextDouble(random, -0.005F, 0.005F), MathHelper.nextDouble(random, -0.005F, 0.005F)
						);
					if (b == 0) {
						for (Direction direction : Direction.values()) {
							float y = direction == Direction.DOWN ? (float) Math.PI : 0.0F;
							double p = direction.getAxis() == Direction.Axis.Y ? 0.65 : 0.57;
							ParticleUtil.spawnParticles(this.world, pos, new SculkChargeParticleEffect(y), intProvider, direction, supplier, p);
						}
					} else {
						for (Direction direction2 : MultifaceGrowthBlock.flagToDirections(b)) {
							float z = direction2 == Direction.UP ? (float) Math.PI : 0.0F;
							double o = 0.35;
							ParticleUtil.spawnParticles(this.world, pos, new SculkChargeParticleEffect(z), intProvider, direction2, supplier, 0.35);
						}
					}
				} else {
					this.world.playSoundAtBlockCenter(pos, SoundEvents.BLOCK_SCULK_CHARGE, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
					boolean bl = this.world.getBlockState(pos).isFullCube(this.world, pos);
					int aa = bl ? 40 : 20;
					float x = bl ? 0.45F : 0.25F;
					float ab = 0.07F;

					for (int ac = 0; ac < aa; ac++) {
						float ad = 2.0F * random.nextFloat() - 1.0F;
						float z = 2.0F * random.nextFloat() - 1.0F;
						float ae = 2.0F * random.nextFloat() - 1.0F;
						this.world
							.addParticle(
								ParticleTypes.SCULK_CHARGE_POP,
								(double)pos.getX() + 0.5 + (double)(ad * x),
								(double)pos.getY() + 0.5 + (double)(z * x),
								(double)pos.getZ() + 0.5 + (double)(ae * x),
								(double)(ad * 0.07F),
								(double)(z * 0.07F),
								(double)(ae * 0.07F)
							);
					}
				}
				break;
			case 3007:
				for (int af = 0; af < 10; af++) {
					this.world
						.addParticle(
							new ShriekParticleEffect(af * 5), false, (double)pos.getX() + 0.5, (double)pos.getY() + SculkShriekerBlock.TOP, (double)pos.getZ() + 0.5, 0.0, 0.0, 0.0
						);
				}

				BlockState blockState3 = this.world.getBlockState(pos);
				boolean bl2 = blockState3.contains(Properties.WATERLOGGED) && (Boolean)blockState3.get(Properties.WATERLOGGED);
				if (!bl2) {
					this.world
						.playSound(
							(double)pos.getX() + 0.5,
							(double)pos.getY() + SculkShriekerBlock.TOP,
							(double)pos.getZ() + 0.5,
							SoundEvents.BLOCK_SCULK_SHRIEKER_SHRIEK,
							SoundCategory.BLOCKS,
							2.0F,
							0.6F + this.world.random.nextFloat() * 0.4F,
							false
						);
				}
				break;
			case 3008:
				BlockState blockState2 = Block.getStateFromRawId(data);
				if (blockState2.getBlock() instanceof BrushableBlock brushableBlock) {
					this.world.playSoundAtBlockCenter(pos, brushableBlock.getBrushingCompleteSound(), SoundCategory.PLAYERS, 1.0F, 1.0F, false);
				}

				this.world.addBlockBreakParticles(pos, blockState2);
				break;
			case 3009:
				ParticleUtil.spawnParticle(this.world, pos, ParticleTypes.EGG_CRACK, UniformIntProvider.create(3, 6));
				break;
			case 3011:
				TrialSpawnerLogic.addMobSpawnParticles(this.world, pos, random, TrialSpawnerLogic.Type.fromIndex(data).particle);
				break;
			case 3012:
				this.world
					.playSoundAtBlockCenter(
						pos, SoundEvents.BLOCK_TRIAL_SPAWNER_SPAWN_MOB, SoundCategory.BLOCKS, 1.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, true
					);
				TrialSpawnerLogic.addMobSpawnParticles(this.world, pos, random, TrialSpawnerLogic.Type.fromIndex(data).particle);
				break;
			case 3013:
				this.world
					.playSoundAtBlockCenter(
						pos, SoundEvents.BLOCK_TRIAL_SPAWNER_DETECT_PLAYER, SoundCategory.BLOCKS, 1.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, true
					);
				TrialSpawnerLogic.addDetectionParticles(this.world, pos, random, data, ParticleTypes.TRIAL_SPAWNER_DETECTION);
				break;
			case 3014:
				this.world
					.playSoundAtBlockCenter(
						pos, SoundEvents.BLOCK_TRIAL_SPAWNER_EJECT_ITEM, SoundCategory.BLOCKS, 1.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, true
					);
				TrialSpawnerLogic.addEjectItemParticles(this.world, pos, random);
				break;
			case 3015:
				if (this.world.getBlockEntity(pos) instanceof VaultBlockEntity vaultBlockEntity) {
					VaultBlockEntity.Client.spawnActivateParticles(
						this.world,
						vaultBlockEntity.getPos(),
						vaultBlockEntity.getCachedState(),
						vaultBlockEntity.getSharedData(),
						data == 0 ? ParticleTypes.SMALL_FLAME : ParticleTypes.SOUL_FIRE_FLAME
					);
					this.world
						.playSoundAtBlockCenter(pos, SoundEvents.BLOCK_VAULT_ACTIVATE, SoundCategory.BLOCKS, 1.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, true);
				}
				break;
			case 3016:
				VaultBlockEntity.Client.spawnDeactivateParticles(this.world, pos, data == 0 ? ParticleTypes.SMALL_FLAME : ParticleTypes.SOUL_FIRE_FLAME);
				this.world
					.playSoundAtBlockCenter(pos, SoundEvents.BLOCK_VAULT_DEACTIVATE, SoundCategory.BLOCKS, 1.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, true);
				break;
			case 3017:
				TrialSpawnerLogic.addEjectItemParticles(this.world, pos, random);
				break;
			case 3018:
				for (int sx = 0; sx < 10; sx++) {
					double t = random.nextGaussian() * 0.02;
					double u = random.nextGaussian() * 0.02;
					double v = random.nextGaussian() * 0.02;
					this.world
						.addParticle(
							ParticleTypes.POOF,
							(double)pos.getX() + random.nextDouble(),
							(double)pos.getY() + random.nextDouble(),
							(double)pos.getZ() + random.nextDouble(),
							t,
							u,
							v
						);
				}

				this.world
					.playSoundAtBlockCenter(pos, SoundEvents.BLOCK_COBWEB_PLACE, SoundCategory.BLOCKS, 1.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, true);
				break;
			case 3019:
				this.world
					.playSoundAtBlockCenter(
						pos, SoundEvents.BLOCK_TRIAL_SPAWNER_DETECT_PLAYER, SoundCategory.BLOCKS, 1.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, true
					);
				TrialSpawnerLogic.addDetectionParticles(this.world, pos, random, data, ParticleTypes.TRIAL_SPAWNER_DETECTION_OMINOUS);
				break;
			case 3020:
				this.world
					.playSoundAtBlockCenter(
						pos,
						SoundEvents.BLOCK_TRIAL_SPAWNER_OMINOUS_ACTIVATE,
						SoundCategory.BLOCKS,
						data == 0 ? 0.3F : 1.0F,
						(random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F,
						true
					);
				TrialSpawnerLogic.addDetectionParticles(this.world, pos, random, 0, ParticleTypes.TRIAL_SPAWNER_DETECTION_OMINOUS);
				TrialSpawnerLogic.addTrialOmenParticles(this.world, pos, random);
				break;
			case 3021:
				this.world
					.playSoundAtBlockCenter(
						pos, SoundEvents.BLOCK_TRIAL_SPAWNER_SPAWN_ITEM, SoundCategory.BLOCKS, 1.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, true
					);
				TrialSpawnerLogic.addMobSpawnParticles(this.world, pos, random, TrialSpawnerLogic.Type.fromIndex(data).particle);
		}
	}

	private void shootParticles(int direction, BlockPos pos, Random random, SimpleParticleType particleType) {
		Direction direction2 = Direction.byId(direction);
		int i = direction2.getOffsetX();
		int j = direction2.getOffsetY();
		int k = direction2.getOffsetZ();

		for (int l = 0; l < 10; l++) {
			double d = random.nextDouble() * 0.2 + 0.01;
			double e = (double)pos.getX() + (double)i * 0.6 + 0.5 + (double)i * 0.01 + (random.nextDouble() - 0.5) * (double)k * 0.5;
			double f = (double)pos.getY() + (double)j * 0.6 + 0.5 + (double)j * 0.01 + (random.nextDouble() - 0.5) * (double)j * 0.5;
			double g = (double)pos.getZ() + (double)k * 0.6 + 0.5 + (double)k * 0.01 + (random.nextDouble() - 0.5) * (double)i * 0.5;
			double h = (double)i * d + random.nextGaussian() * 0.01;
			double m = (double)j * d + random.nextGaussian() * 0.01;
			double n = (double)k * d + random.nextGaussian() * 0.01;
			this.worldRenderer.addParticle(particleType, e, f, g, h, m, n);
		}
	}

	private void playJukeboxSong(RegistryEntry<JukeboxSong> song, BlockPos jukeboxPos) {
		this.stopJukeboxSong(jukeboxPos);
		JukeboxSong jukeboxSong = song.value();
		SoundEvent soundEvent = jukeboxSong.soundEvent().value();
		SoundInstance soundInstance = PositionedSoundInstance.record(soundEvent, Vec3d.ofCenter(jukeboxPos));
		this.playingSongs.put(jukeboxPos, soundInstance);
		this.client.getSoundManager().play(soundInstance);
		this.client.inGameHud.setRecordPlayingOverlay(jukeboxSong.description());
		this.updateEntitiesForSong(this.world, jukeboxPos, true);
	}

	private void stopJukeboxSong(BlockPos jukeboxPos) {
		SoundInstance soundInstance = (SoundInstance)this.playingSongs.remove(jukeboxPos);
		if (soundInstance != null) {
			this.client.getSoundManager().stop(soundInstance);
		}
	}

	private void stopJukeboxSongAndUpdate(BlockPos jukeboxPos) {
		this.stopJukeboxSong(jukeboxPos);
		this.updateEntitiesForSong(this.world, jukeboxPos, false);
	}

	private void updateEntitiesForSong(World world, BlockPos pos, boolean playing) {
		for (LivingEntity livingEntity : world.getNonSpectatingEntities(LivingEntity.class, new Box(pos).expand(3.0))) {
			livingEntity.setNearbySongPlaying(pos, playing);
		}
	}
}
