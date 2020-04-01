package net.minecraft;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.spec.KeySpec;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.DirectionTransformation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.EmptyBlockView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.dimension.DimensionHashHelper;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.RandomDimension;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorType;
import net.minecraft.world.gen.stateprovider.SimpleBlockStateProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_5097 extends RandomDimension {
	private static final Logger field_23560 = LogManager.getLogger();
	private static final byte[] field_23561 = new byte[]{
		72,
		-71,
		33,
		-116,
		61,
		25,
		-105,
		61,
		69,
		-34,
		22,
		-96,
		83,
		-41,
		4,
		100,
		49,
		-120,
		-76,
		-32,
		-24,
		105,
		-103,
		57,
		-101,
		-101,
		114,
		39,
		45,
		-48,
		-58,
		106,
		-83,
		72,
		-120,
		-98,
		14,
		111,
		-73,
		38,
		-43,
		-29,
		-17,
		-64,
		-48,
		-21,
		-63,
		-14,
		7,
		-65,
		-115,
		61,
		-62,
		-121,
		108,
		-2,
		24,
		84,
		-62,
		117,
		115,
		52,
		-88,
		18,
		30,
		115,
		44,
		-113,
		-123,
		88,
		77,
		-122,
		15,
		-13,
		123,
		85,
		-118,
		-12,
		-30,
		7,
		104,
		-75,
		-84,
		-57,
		-124,
		-113,
		-38,
		84,
		52,
		6,
		-94,
		67,
		76,
		59,
		105,
		82,
		92,
		-65,
		-52,
		-26,
		-46,
		45,
		94,
		47,
		10,
		-14,
		-86,
		-12,
		1,
		111,
		-107,
		-119,
		-115,
		32,
		-60,
		-92,
		107,
		-2,
		73,
		109,
		-128,
		-107,
		-52,
		-7,
		-6,
		126,
		98,
		-71,
		-92,
		12,
		-41,
		83,
		-124,
		14,
		-51,
		-15,
		4,
		-3,
		-65,
		-36,
		99,
		63,
		119,
		64,
		46,
		21,
		10,
		30,
		-23,
		-10,
		-90,
		-36,
		-4,
		-106,
		-102,
		84,
		-17,
		58,
		59,
		-76,
		-103,
		-28,
		-95,
		4,
		112,
		18,
		3,
		-78,
		125,
		-79,
		11,
		120,
		-59,
		-64,
		-37,
		-47,
		19,
		-21,
		90,
		-9,
		-65,
		109,
		70,
		-83,
		-4,
		34,
		41,
		-109,
		27,
		-20,
		29,
		60,
		109,
		-117,
		74,
		-112,
		-58,
		76,
		96,
		9,
		-65,
		86,
		63,
		62,
		112,
		-88,
		96,
		-35,
		64,
		57,
		35,
		89,
		-24,
		-40,
		121,
		106,
		-102,
		-103,
		-24,
		-73,
		103,
		-110,
		56,
		97,
		-82,
		55,
		-53,
		-100,
		22,
		-68,
		104,
		8,
		98,
		-120,
		-65,
		-30,
		38,
		114,
		-59,
		30,
		66,
		-119,
		59,
		-93,
		107,
		-50,
		115,
		40,
		80,
		77,
		-61,
		-102,
		-62,
		-110,
		-80,
		-85,
		19,
		123,
		-120,
		70,
		-119,
		11,
		63,
		30,
		92,
		73,
		81,
		-19,
		-14,
		122,
		-103,
		-108,
		38,
		-116,
		-100,
		50,
		-121,
		-7,
		-125,
		61,
		-44,
		-38,
		-117,
		16,
		14,
		-101,
		79,
		-96,
		89,
		12,
		84,
		-36,
		42,
		-21,
		-109,
		-7,
		117,
		64,
		38,
		18,
		-97,
		-58,
		73,
		2,
		41,
		70,
		-85,
		75,
		6,
		123,
		76,
		-66,
		53,
		-41,
		25,
		-14,
		-104,
		-19,
		67,
		-28,
		-9,
		-111,
		59,
		-109,
		35,
		57,
		108,
		100,
		40,
		116,
		-106,
		-128,
		2,
		109,
		-75,
		3,
		19,
		87,
		-120,
		59,
		-20,
		-15,
		74,
		-40,
		106,
		-3,
		-122,
		19,
		-94,
		53,
		-103,
		-60,
		-36,
		2,
		52,
		31,
		63,
		17,
		-32,
		-61,
		-116,
		5,
		9,
		117,
		-72,
		-28,
		-125,
		99,
		-54,
		-126,
		96,
		21,
		29,
		38,
		35,
		90,
		-32,
		89,
		48,
		108,
		10,
		-52,
		-117,
		2,
		-74,
		-122,
		-21,
		119,
		126,
		-110,
		-115,
		57,
		-119,
		-53,
		43,
		-128,
		10,
		97,
		122,
		126,
		-111,
		103,
		113,
		90,
		101,
		44,
		9,
		5,
		102,
		88,
		-24,
		-108,
		-8,
		42,
		65,
		46
	};
	private static final byte[] field_23562 = new byte[]{-114, 123, -36, 36, 6, 2, 31, 116, -76, -125, -62, -61, -41, -121, 82, -106};

	public class_5097(World world, DimensionType dimensionType) {
		super(world, dimensionType, 1.0F);
	}

	@Override
	public ChunkGenerator<?> createChunkGenerator() {
		return new class_5097.class_5098(this.field_23566, method_26572(Biomes.THE_VOID), class_5099.field_23565, DimensionHashHelper.getLastSearchedName());
	}

	@Override
	public float getSkyAngle(long timeOfDay, float tickDelta) {
		return 12000.0F;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public Vec3d modifyFogColor(Vec3d vec3d, float tickDelta) {
		return vec3d;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean isFogThick(int x, int z) {
		return false;
	}

	public static class class_5098 extends ChunkGenerator<class_5099> {
		private final String[] field_23564;
		final SimpleBlockStateProvider field_23563 = new SimpleBlockStateProvider(Blocks.SPONGE.getDefaultState());

		public class_5098(IWorld iWorld, BiomeSource biomeSource, class_5099 arg) {
			this(iWorld, biomeSource, arg, "");
		}

		private class_5098(IWorld iWorld, BiomeSource biomeSource, class_5099 arg, String string) {
			super(iWorld, biomeSource, arg);
			this.field_23564 = method_26571(string);
		}

		private static String[] method_26571(String string) {
			try {
				SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
				KeySpec keySpec = new PBEKeySpec(string.toCharArray(), "pinch_of_salt".getBytes(StandardCharsets.UTF_8), 65536, 128);
				SecretKey secretKey = secretKeyFactory.generateSecret(keySpec);
				SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getEncoded(), "AES");
				Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
				IvParameterSpec ivParameterSpec = new IvParameterSpec(class_5097.field_23562);
				cipher.init(2, secretKeySpec, ivParameterSpec);
				byte[] bs = cipher.doFinal(class_5097.field_23561);
				return StandardCharsets.UTF_8.decode(ByteBuffer.wrap(bs)).toString().split("\n");
			} catch (Exception var8) {
				class_5097.field_23560.warn("No.", (Throwable)var8);
				return new String[]{"Uh uh uh! You didn't say the magic word!"};
			}
		}

		@Override
		public void buildSurface(ChunkRegion region, Chunk chunk) {
		}

		@Override
		public int getSpawnHeight() {
			return 30;
		}

		@Override
		public void populateNoise(IWorld world, Chunk chunk) {
			ChunkPos chunkPos = chunk.getPos();
			int i = chunkPos.x * 2;
			int j = chunkPos.z * 2;
			this.method_26569(chunk, j, i, 0, 0);
			this.method_26569(chunk, j, i, 1, 0);
			this.method_26569(chunk, j, i, 0, 1);
			this.method_26569(chunk, j, i, 1, 1);
		}

		private void method_26569(Chunk chunk, int i, int j, int k, int l) {
			int m = j + k;
			int n = i + l;
			if (n >= 0 && n < this.field_23564.length) {
				String string = this.field_23564[n];
				if (m >= 0 && m < string.length()) {
					char c = string.charAt(m);
					class_5102.method_26586(
						new BlockPos(8 * k, 20, 8 * l),
						new class_5104(this.field_23563, c, DirectionTransformation.ROT_90_X_NEG),
						blockPos -> chunk.setBlockState(blockPos, Blocks.GRASS_BLOCK.getDefaultState(), false)
					);
				}
			}
		}

		@Override
		public void carve(BiomeAccess biomeAccess, Chunk chunk, GenerationStep.Carver carver) {
		}

		@Override
		public void generateFeatures(ChunkRegion region) {
		}

		@Override
		public int getHeight(int x, int z, Heightmap.Type heightmapType) {
			return 0;
		}

		@Override
		public BlockView getColumnSample(int x, int z) {
			return EmptyBlockView.INSTANCE;
		}

		@Override
		public ChunkGeneratorType<?, ?> method_26490() {
			return ChunkGeneratorType.field_23449;
		}
	}
}
