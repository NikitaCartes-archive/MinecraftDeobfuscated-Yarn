package net.minecraft.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CampfireBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.fluid.FluidState;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.particle.ParticlesMode;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

@Environment(EnvType.CLIENT)
public class WeatherRendering {
	private static final int field_53148 = 10;
	private static final int field_53149 = 21;
	private static final Identifier RAIN_TEXTURE = Identifier.ofVanilla("textures/environment/rain.png");
	private static final Identifier SNOW_TEXTURE = Identifier.ofVanilla("textures/environment/snow.png");
	private static final int field_53152 = 32;
	private static final int field_53153 = 16;
	private int field_53154;
	/**
	 * Given {@code -16 <= z < 16} and {@code -16 <= x < 16}, let {@code i = 32 * (z + 16) + (x + 16)}.
	 * Then {@code NORMAL_LINE_DX[i]} and {@code NORMAL_LINE_DZ[i]} describe the
	 * unit vector perpendicular to {@code (x, z)}.
	 * 
	 * These lookup tables are used for rendering rain and snow.
	 */
	private final float[] NORMAL_LINE_DX = new float[1024];
	private final float[] NORMAL_LINE_DZ = new float[1024];

	public WeatherRendering() {
		for (int i = 0; i < 32; i++) {
			for (int j = 0; j < 32; j++) {
				float f = (float)(j - 16);
				float g = (float)(i - 16);
				float h = MathHelper.hypot(f, g);
				this.NORMAL_LINE_DX[i * 32 + j] = -g / h;
				this.NORMAL_LINE_DZ[i * 32 + j] = f / h;
			}
		}
	}

	public void renderPrecipitation(World world, LightmapTextureManager lightmapTextureManager, int ticks, float delta, Vec3d pos) {
		float f = world.getRainGradient(delta);
		if (!(f <= 0.0F)) {
			int i = MinecraftClient.isFancyGraphicsOrBetter() ? 10 : 5;
			List<WeatherRendering.Piece> list = new ArrayList();
			List<WeatherRendering.Piece> list2 = new ArrayList();
			this.buildPrecipitationPieces(world, ticks, delta, pos, i, list, list2);
			if (!list.isEmpty() || !list2.isEmpty()) {
				this.renderPrecipitation(lightmapTextureManager, pos, i, f, list, list2);
			}
		}
	}

	private void buildPrecipitationPieces(
		World world, int ticks, float delta, Vec3d pos, int range, List<WeatherRendering.Piece> rainOut, List<WeatherRendering.Piece> snowOut
	) {
		int i = MathHelper.floor(pos.x);
		int j = MathHelper.floor(pos.y);
		int k = MathHelper.floor(pos.z);
		BlockPos.Mutable mutable = new BlockPos.Mutable();
		Random random = Random.create();

		for (int l = k - range; l <= k + range; l++) {
			for (int m = i - range; m <= i + range; m++) {
				int n = world.getTopY(Heightmap.Type.MOTION_BLOCKING, m, l);
				int o = Math.max(j - range, n);
				int p = Math.max(j + range, n);
				if (p - o != 0) {
					Biome.Precipitation precipitation = this.getPrecipitationAt(world, mutable.set(m, j, l));
					if (precipitation != Biome.Precipitation.NONE) {
						int q = m * m * 3121 + m * 45238971 ^ l * l * 418711 + l * 13761;
						random.setSeed((long)q);
						int r = Math.max(j, n);
						int s = WorldRenderer.getLightmapCoordinates(world, mutable.set(m, r, l));
						if (precipitation == Biome.Precipitation.RAIN) {
							rainOut.add(this.createRainPiece(random, ticks, m, o, p, l, s, delta));
						} else if (precipitation == Biome.Precipitation.SNOW) {
							snowOut.add(this.createSnowPiece(random, ticks, m, o, p, l, s, delta));
						}
					}
				}
			}
		}
	}

	private void renderPrecipitation(
		LightmapTextureManager lightmapTextureManager,
		Vec3d pos,
		int range,
		float gradient,
		List<WeatherRendering.Piece> rainPieces,
		List<WeatherRendering.Piece> snowPieces
	) {
		lightmapTextureManager.enable();
		Tessellator tessellator = Tessellator.getInstance();
		RenderSystem.disableCull();
		RenderSystem.enableBlend();
		RenderSystem.enableDepthTest();
		RenderSystem.depthMask(MinecraftClient.isFabulousGraphicsOrBetter());
		RenderSystem.setShader(ShaderProgramKeys.PARTICLE);
		if (!rainPieces.isEmpty()) {
			RenderSystem.setShaderTexture(0, RAIN_TEXTURE);
			this.renderPieces(tessellator, rainPieces, pos, 1.0F, range, gradient);
		}

		if (!snowPieces.isEmpty()) {
			RenderSystem.setShaderTexture(0, SNOW_TEXTURE);
			this.renderPieces(tessellator, snowPieces, pos, 0.8F, range, gradient);
		}

		RenderSystem.depthMask(true);
		RenderSystem.enableCull();
		RenderSystem.disableBlend();
		lightmapTextureManager.disable();
	}

	private WeatherRendering.Piece createRainPiece(Random random, int ticks, int x, int yMin, int yMax, int z, int light, float tickDelta) {
		int i = ticks & 131071;
		int j = x * x * 3121 + x * 45238971 + z * z * 418711 + z * 13761 & 0xFF;
		float f = 3.0F + random.nextFloat();
		float g = -((float)(i + j) + tickDelta) / 32.0F * f;
		float h = g % 32.0F;
		return new WeatherRendering.Piece(x, z, yMin, yMax, 0.0F, h, light);
	}

	private WeatherRendering.Piece createSnowPiece(Random random, int ticks, int x, int yMin, int yMax, int z, int light, float tickDelta) {
		float f = (float)ticks + tickDelta;
		float g = (float)(random.nextDouble() + (double)(f * 0.01F * (float)random.nextGaussian()));
		float h = (float)(random.nextDouble() + (double)(f * (float)random.nextGaussian() * 0.001F));
		float i = -((float)(ticks & 511) + tickDelta) / 512.0F;
		int j = LightmapTextureManager.pack(
			(LightmapTextureManager.getBlockLightCoordinates(light) * 3 + 15) / 4, (LightmapTextureManager.getSkyLightCoordinates(light) * 3 + 15) / 4
		);
		return new WeatherRendering.Piece(x, z, yMin, yMax, g, i + h, j);
	}

	private void renderPieces(Tessellator tessellator, List<WeatherRendering.Piece> pieces, Vec3d pos, float intensity, int range, float gradient) {
		BufferBuilder bufferBuilder = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR_LIGHT);

		for (WeatherRendering.Piece piece : pieces) {
			float f = (float)((double)piece.x + 0.5 - pos.x);
			float g = (float)((double)piece.z + 0.5 - pos.z);
			float h = (float)MathHelper.squaredHypot((double)f, (double)g);
			float i = MathHelper.lerp(h / (float)(range * range), intensity, 0.5F) * gradient;
			int j = ColorHelper.getWhite(i);
			int k = (piece.z - MathHelper.floor(pos.z) + 16) * 32 + piece.x - MathHelper.floor(pos.x) + 16;
			float l = this.NORMAL_LINE_DX[k] / 2.0F;
			float m = this.NORMAL_LINE_DZ[k] / 2.0F;
			float n = f - l;
			float o = f + l;
			float p = (float)((double)piece.topY - pos.y);
			float q = (float)((double)piece.bottomY - pos.y);
			float r = g - m;
			float s = g + m;
			float t = piece.uOffset + 0.0F;
			float u = piece.uOffset + 1.0F;
			float v = (float)piece.bottomY * 0.25F + piece.vOffset;
			float w = (float)piece.topY * 0.25F + piece.vOffset;
			bufferBuilder.vertex(n, p, r).texture(t, v).color(j).light(piece.lightCoords);
			bufferBuilder.vertex(o, p, s).texture(u, v).color(j).light(piece.lightCoords);
			bufferBuilder.vertex(o, q, s).texture(u, w).color(j).light(piece.lightCoords);
			bufferBuilder.vertex(n, q, r).texture(t, w).color(j).light(piece.lightCoords);
		}

		BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
	}

	public void addParticlesAndSound(ClientWorld world, Camera camera, int ticks, ParticlesMode particlesMode) {
		float f = world.getRainGradient(1.0F) / (MinecraftClient.isFancyGraphicsOrBetter() ? 1.0F : 2.0F);
		if (!(f <= 0.0F)) {
			Random random = Random.create((long)ticks * 312987231L);
			BlockPos blockPos = BlockPos.ofFloored(camera.getPos());
			BlockPos blockPos2 = null;
			int i = (int)(100.0F * f * f) / (particlesMode == ParticlesMode.DECREASED ? 2 : 1);

			for (int j = 0; j < i; j++) {
				int k = random.nextInt(21) - 10;
				int l = random.nextInt(21) - 10;
				BlockPos blockPos3 = world.getTopPosition(Heightmap.Type.MOTION_BLOCKING, blockPos.add(k, 0, l));
				if (blockPos3.getY() > world.getBottomY()
					&& blockPos3.getY() <= blockPos.getY() + 10
					&& blockPos3.getY() >= blockPos.getY() - 10
					&& this.getPrecipitationAt(world, blockPos3) == Biome.Precipitation.RAIN) {
					blockPos2 = blockPos3.down();
					if (particlesMode == ParticlesMode.MINIMAL) {
						break;
					}

					double d = random.nextDouble();
					double e = random.nextDouble();
					BlockState blockState = world.getBlockState(blockPos2);
					FluidState fluidState = world.getFluidState(blockPos2);
					VoxelShape voxelShape = blockState.getCollisionShape(world, blockPos2);
					double g = voxelShape.getEndingCoord(Direction.Axis.Y, d, e);
					double h = (double)fluidState.getHeight(world, blockPos2);
					double m = Math.max(g, h);
					ParticleEffect particleEffect = !fluidState.isIn(FluidTags.LAVA) && !blockState.isOf(Blocks.MAGMA_BLOCK) && !CampfireBlock.isLitCampfire(blockState)
						? ParticleTypes.RAIN
						: ParticleTypes.SMOKE;
					world.addParticle(particleEffect, (double)blockPos2.getX() + d, (double)blockPos2.getY() + m, (double)blockPos2.getZ() + e, 0.0, 0.0, 0.0);
				}
			}

			if (blockPos2 != null && random.nextInt(3) < this.field_53154++) {
				this.field_53154 = 0;
				if (blockPos2.getY() > blockPos.getY() + 1
					&& world.getTopPosition(Heightmap.Type.MOTION_BLOCKING, blockPos).getY() > MathHelper.floor((float)blockPos.getY())) {
					world.playSoundAtBlockCenter(blockPos2, SoundEvents.WEATHER_RAIN_ABOVE, SoundCategory.WEATHER, 0.1F, 0.5F, false);
				} else {
					world.playSoundAtBlockCenter(blockPos2, SoundEvents.WEATHER_RAIN, SoundCategory.WEATHER, 0.2F, 1.0F, false);
				}
			}
		}
	}

	private Biome.Precipitation getPrecipitationAt(World world, BlockPos pos) {
		if (!world.getChunkManager().isChunkLoaded(ChunkSectionPos.getSectionCoord(pos.getX()), ChunkSectionPos.getSectionCoord(pos.getZ()))) {
			return Biome.Precipitation.NONE;
		} else {
			Biome biome = world.getBiome(pos).value();
			return biome.getPrecipitation(pos, world.getSeaLevel());
		}
	}

	@Environment(EnvType.CLIENT)
	static record Piece(int x, int z, int bottomY, int topY, float uOffset, float vOffset, int lightCoords) {
	}
}
