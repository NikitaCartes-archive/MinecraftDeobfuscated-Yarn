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

	public void method_62316(World world, LightmapTextureManager lightmapTextureManager, int ticks, float delta, Vec3d vec3d) {
		float f = world.getRainGradient(delta);
		if (!(f <= 0.0F)) {
			int i = MinecraftClient.isFancyGraphicsOrBetter() ? 10 : 5;
			List<WeatherRendering.Piece> list = new ArrayList();
			List<WeatherRendering.Piece> list2 = new ArrayList();
			this.method_62315(world, ticks, delta, vec3d, i, list, list2);
			if (!list.isEmpty() || !list2.isEmpty()) {
				this.method_62320(lightmapTextureManager, vec3d, i, f, list, list2);
			}
		}
	}

	private void method_62315(World world, int ticks, float f, Vec3d pos, int i, List<WeatherRendering.Piece> rainOut, List<WeatherRendering.Piece> snowOut) {
		int j = MathHelper.floor(pos.x);
		int k = MathHelper.floor(pos.y);
		int l = MathHelper.floor(pos.z);
		BlockPos.Mutable mutable = new BlockPos.Mutable();
		Random random = Random.create();

		for (int m = l - i; m <= l + i; m++) {
			for (int n = j - i; n <= j + i; n++) {
				int o = world.getTopY(Heightmap.Type.MOTION_BLOCKING, n, m);
				int p = Math.max(k - i, o);
				int q = Math.max(k + i, o);
				if (q - p != 0) {
					Biome.Precipitation precipitation = this.getPrecipitationAt(world, mutable.set(n, k, m));
					if (precipitation != Biome.Precipitation.NONE) {
						int r = n * n * 3121 + n * 45238971 ^ m * m * 418711 + m * 13761;
						random.setSeed((long)r);
						int s = Math.max(k, o);
						int t = WorldRenderer.getLightmapCoordinates(world, mutable.set(n, s, m));
						if (precipitation == Biome.Precipitation.RAIN) {
							rainOut.add(this.createRainPiece(random, ticks, n, p, q, m, t, f));
						} else if (precipitation == Biome.Precipitation.SNOW) {
							snowOut.add(this.createSnowPiece(random, ticks, n, p, q, m, t, f));
						}
					}
				}
			}
		}
	}

	private void method_62320(
		LightmapTextureManager lightmapTextureManager, Vec3d vec3d, int i, float f, List<WeatherRendering.Piece> list, List<WeatherRendering.Piece> list2
	) {
		lightmapTextureManager.enable();
		Tessellator tessellator = Tessellator.getInstance();
		RenderSystem.disableCull();
		RenderSystem.enableBlend();
		RenderSystem.enableDepthTest();
		RenderSystem.depthMask(MinecraftClient.isFabulousGraphicsOrBetter());
		RenderSystem.setShader(GameRenderer::getParticleProgram);
		if (!list.isEmpty()) {
			RenderSystem.setShaderTexture(0, RAIN_TEXTURE);
			this.method_62318(tessellator, list, vec3d, 1.0F, i, f);
		}

		if (!list2.isEmpty()) {
			RenderSystem.setShaderTexture(0, SNOW_TEXTURE);
			this.method_62318(tessellator, list2, vec3d, 0.8F, i, f);
		}

		RenderSystem.depthMask(true);
		RenderSystem.enableCull();
		RenderSystem.disableBlend();
		lightmapTextureManager.disable();
	}

	private WeatherRendering.Piece createRainPiece(Random random, int ticks, int x, int yMin, int yMax, int z, int light, float f) {
		int i = ticks & 131071;
		int j = x * x * 3121 + x * 45238971 + z * z * 418711 + z * 13761 & 0xFF;
		float g = 3.0F + random.nextFloat();
		float h = -((float)(i + j) + f) / 32.0F * g;
		float k = h % 32.0F;
		return new WeatherRendering.Piece(x, z, yMin, yMax, 0.0F, k, light);
	}

	private WeatherRendering.Piece createSnowPiece(Random random, int ticks, int x, int yMin, int yMax, int z, int light, float f) {
		float g = (float)ticks + f;
		float h = (float)(random.nextDouble() + (double)(g * 0.01F * (float)random.nextGaussian()));
		float i = (float)(random.nextDouble() + (double)(g * (float)random.nextGaussian() * 0.001F));
		float j = -((float)(ticks & 511) + f) / 512.0F;
		int k = LightmapTextureManager.pack(
			(LightmapTextureManager.getBlockLightCoordinates(light) * 3 + 15) / 4, (LightmapTextureManager.getSkyLightCoordinates(light) * 3 + 15) / 4
		);
		return new WeatherRendering.Piece(x, z, yMin, yMax, h, j + i, k);
	}

	private void method_62318(Tessellator tessellator, List<WeatherRendering.Piece> pieces, Vec3d pos, float f, int i, float g) {
		BufferBuilder bufferBuilder = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR_LIGHT);

		for (WeatherRendering.Piece piece : pieces) {
			float h = (float)((double)piece.x + 0.5 - pos.x);
			float j = (float)((double)piece.z + 0.5 - pos.z);
			float k = (float)MathHelper.squaredHypot((double)h, (double)j);
			float l = MathHelper.lerp(k / (float)(i * i), f, 0.5F) * g;
			int m = ColorHelper.getWhite(l);
			int n = (piece.z - MathHelper.floor(pos.z) + 16) * 32 + piece.x - MathHelper.floor(pos.x) + 16;
			float o = this.NORMAL_LINE_DX[n] / 2.0F;
			float p = this.NORMAL_LINE_DZ[n] / 2.0F;
			float q = h - o;
			float r = h + o;
			float s = (float)((double)piece.topY - pos.y);
			float t = (float)((double)piece.bottomY - pos.y);
			float u = j - p;
			float v = j + p;
			float w = piece.uOffset + 0.0F;
			float x = piece.uOffset + 1.0F;
			float y = (float)piece.bottomY * 0.25F + piece.vOffset;
			float z = (float)piece.topY * 0.25F + piece.vOffset;
			bufferBuilder.vertex(q, s, u).texture(w, y).color(m).light(piece.lightCoords);
			bufferBuilder.vertex(r, s, v).texture(x, y).color(m).light(piece.lightCoords);
			bufferBuilder.vertex(r, t, v).texture(x, z).color(m).light(piece.lightCoords);
			bufferBuilder.vertex(q, t, u).texture(w, z).color(m).light(piece.lightCoords);
		}

		BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
	}

	public void method_62319(ClientWorld world, Camera camera, int ticks, ParticlesMode particlesMode) {
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
