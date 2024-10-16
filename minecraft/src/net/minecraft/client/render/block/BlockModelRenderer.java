package net.minecraft.client.render.block;

import it.unimi.dsi.fastutil.longs.Long2FloatLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2IntLinkedOpenHashMap;
import java.util.BitSet;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Util;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockRenderView;

@Environment(EnvType.CLIENT)
public class BlockModelRenderer {
	private static final int field_32782 = 0;
	private static final int field_32783 = 1;
	static final Direction[] DIRECTIONS = Direction.values();
	private final BlockColors colors;
	private static final int BRIGHTNESS_CACHE_MAX_SIZE = 100;
	static final ThreadLocal<BlockModelRenderer.BrightnessCache> BRIGHTNESS_CACHE = ThreadLocal.withInitial(BlockModelRenderer.BrightnessCache::new);

	public BlockModelRenderer(BlockColors colors) {
		this.colors = colors;
	}

	public void render(
		BlockRenderView world,
		BakedModel model,
		BlockState state,
		BlockPos pos,
		MatrixStack matrices,
		VertexConsumer vertexConsumer,
		boolean cull,
		Random random,
		long seed,
		int overlay
	) {
		boolean bl = MinecraftClient.isAmbientOcclusionEnabled() && state.getLuminance() == 0 && model.useAmbientOcclusion();
		matrices.translate(state.getModelOffset(pos));

		try {
			if (bl) {
				this.renderSmooth(world, model, state, pos, matrices, vertexConsumer, cull, random, seed, overlay);
			} else {
				this.renderFlat(world, model, state, pos, matrices, vertexConsumer, cull, random, seed, overlay);
			}
		} catch (Throwable var16) {
			CrashReport crashReport = CrashReport.create(var16, "Tesselating block model");
			CrashReportSection crashReportSection = crashReport.addElement("Block model being tesselated");
			CrashReportSection.addBlockInfo(crashReportSection, world, pos, state);
			crashReportSection.add("Using AO", bl);
			throw new CrashException(crashReport);
		}
	}

	public void renderSmooth(
		BlockRenderView world,
		BakedModel model,
		BlockState state,
		BlockPos pos,
		MatrixStack matrices,
		VertexConsumer vertexConsumer,
		boolean cull,
		Random random,
		long seed,
		int overlay
	) {
		float[] fs = new float[DIRECTIONS.length * 2];
		BitSet bitSet = new BitSet(3);
		BlockModelRenderer.AmbientOcclusionCalculator ambientOcclusionCalculator = new BlockModelRenderer.AmbientOcclusionCalculator();
		BlockPos.Mutable mutable = pos.mutableCopy();

		for (Direction direction : DIRECTIONS) {
			random.setSeed(seed);
			List<BakedQuad> list = model.getQuads(state, direction, random);
			if (!list.isEmpty()) {
				mutable.set(pos, direction);
				if (!cull || Block.shouldDrawSide(state, world.getBlockState(mutable), direction)) {
					this.renderQuadsSmooth(world, state, pos, matrices, vertexConsumer, list, fs, bitSet, ambientOcclusionCalculator, overlay);
				}
			}
		}

		random.setSeed(seed);
		List<BakedQuad> list2 = model.getQuads(state, null, random);
		if (!list2.isEmpty()) {
			this.renderQuadsSmooth(world, state, pos, matrices, vertexConsumer, list2, fs, bitSet, ambientOcclusionCalculator, overlay);
		}
	}

	public void renderFlat(
		BlockRenderView world,
		BakedModel model,
		BlockState state,
		BlockPos pos,
		MatrixStack matrices,
		VertexConsumer vertexConsumer,
		boolean cull,
		Random random,
		long seed,
		int overlay
	) {
		BitSet bitSet = new BitSet(3);
		BlockPos.Mutable mutable = pos.mutableCopy();

		for (Direction direction : DIRECTIONS) {
			random.setSeed(seed);
			List<BakedQuad> list = model.getQuads(state, direction, random);
			if (!list.isEmpty()) {
				mutable.set(pos, direction);
				if (!cull || Block.shouldDrawSide(state, world.getBlockState(mutable), direction)) {
					int i = WorldRenderer.getLightmapCoordinates(world, state, mutable);
					this.renderQuadsFlat(world, state, pos, i, overlay, false, matrices, vertexConsumer, list, bitSet);
				}
			}
		}

		random.setSeed(seed);
		List<BakedQuad> list2 = model.getQuads(state, null, random);
		if (!list2.isEmpty()) {
			this.renderQuadsFlat(world, state, pos, -1, overlay, true, matrices, vertexConsumer, list2, bitSet);
		}
	}

	private void renderQuadsSmooth(
		BlockRenderView world,
		BlockState state,
		BlockPos pos,
		MatrixStack matrices,
		VertexConsumer vertexConsumer,
		List<BakedQuad> quads,
		float[] box,
		BitSet flags,
		BlockModelRenderer.AmbientOcclusionCalculator ambientOcclusionCalculator,
		int overlay
	) {
		for (BakedQuad bakedQuad : quads) {
			this.getQuadDimensions(world, state, pos, bakedQuad.getVertexData(), bakedQuad.getFace(), box, flags);
			ambientOcclusionCalculator.apply(world, state, pos, bakedQuad.getFace(), box, flags, bakedQuad.hasShade());
			this.renderQuad(
				world,
				state,
				pos,
				vertexConsumer,
				matrices.peek(),
				bakedQuad,
				ambientOcclusionCalculator.brightness[0],
				ambientOcclusionCalculator.brightness[1],
				ambientOcclusionCalculator.brightness[2],
				ambientOcclusionCalculator.brightness[3],
				ambientOcclusionCalculator.light[0],
				ambientOcclusionCalculator.light[1],
				ambientOcclusionCalculator.light[2],
				ambientOcclusionCalculator.light[3],
				overlay
			);
		}
	}

	private void renderQuad(
		BlockRenderView world,
		BlockState state,
		BlockPos pos,
		VertexConsumer vertexConsumer,
		MatrixStack.Entry matrixEntry,
		BakedQuad quad,
		float brightness0,
		float brightness1,
		float brightness2,
		float brightness3,
		int light0,
		int light1,
		int light2,
		int light3,
		int overlay
	) {
		float f;
		float g;
		float h;
		if (quad.hasColor()) {
			int i = this.colors.getColor(state, world, pos, quad.getColorIndex());
			f = (float)(i >> 16 & 0xFF) / 255.0F;
			g = (float)(i >> 8 & 0xFF) / 255.0F;
			h = (float)(i & 0xFF) / 255.0F;
		} else {
			f = 1.0F;
			g = 1.0F;
			h = 1.0F;
		}

		vertexConsumer.quad(
			matrixEntry, quad, new float[]{brightness0, brightness1, brightness2, brightness3}, f, g, h, 1.0F, new int[]{light0, light1, light2, light3}, overlay, true
		);
	}

	private void getQuadDimensions(BlockRenderView world, BlockState state, BlockPos pos, int[] vertexData, Direction face, @Nullable float[] box, BitSet flags) {
		float f = 32.0F;
		float g = 32.0F;
		float h = 32.0F;
		float i = -32.0F;
		float j = -32.0F;
		float k = -32.0F;

		for (int l = 0; l < 4; l++) {
			float m = Float.intBitsToFloat(vertexData[l * 8]);
			float n = Float.intBitsToFloat(vertexData[l * 8 + 1]);
			float o = Float.intBitsToFloat(vertexData[l * 8 + 2]);
			f = Math.min(f, m);
			g = Math.min(g, n);
			h = Math.min(h, o);
			i = Math.max(i, m);
			j = Math.max(j, n);
			k = Math.max(k, o);
		}

		if (box != null) {
			box[Direction.WEST.getId()] = f;
			box[Direction.EAST.getId()] = i;
			box[Direction.DOWN.getId()] = g;
			box[Direction.UP.getId()] = j;
			box[Direction.NORTH.getId()] = h;
			box[Direction.SOUTH.getId()] = k;
			int l = DIRECTIONS.length;
			box[Direction.WEST.getId() + l] = 1.0F - f;
			box[Direction.EAST.getId() + l] = 1.0F - i;
			box[Direction.DOWN.getId() + l] = 1.0F - g;
			box[Direction.UP.getId() + l] = 1.0F - j;
			box[Direction.NORTH.getId() + l] = 1.0F - h;
			box[Direction.SOUTH.getId() + l] = 1.0F - k;
		}

		float p = 1.0E-4F;
		float m = 0.9999F;
		switch (face) {
			case DOWN:
				flags.set(1, f >= 1.0E-4F || h >= 1.0E-4F || i <= 0.9999F || k <= 0.9999F);
				flags.set(0, g == j && (g < 1.0E-4F || state.isFullCube(world, pos)));
				break;
			case UP:
				flags.set(1, f >= 1.0E-4F || h >= 1.0E-4F || i <= 0.9999F || k <= 0.9999F);
				flags.set(0, g == j && (j > 0.9999F || state.isFullCube(world, pos)));
				break;
			case NORTH:
				flags.set(1, f >= 1.0E-4F || g >= 1.0E-4F || i <= 0.9999F || j <= 0.9999F);
				flags.set(0, h == k && (h < 1.0E-4F || state.isFullCube(world, pos)));
				break;
			case SOUTH:
				flags.set(1, f >= 1.0E-4F || g >= 1.0E-4F || i <= 0.9999F || j <= 0.9999F);
				flags.set(0, h == k && (k > 0.9999F || state.isFullCube(world, pos)));
				break;
			case WEST:
				flags.set(1, g >= 1.0E-4F || h >= 1.0E-4F || j <= 0.9999F || k <= 0.9999F);
				flags.set(0, f == i && (f < 1.0E-4F || state.isFullCube(world, pos)));
				break;
			case EAST:
				flags.set(1, g >= 1.0E-4F || h >= 1.0E-4F || j <= 0.9999F || k <= 0.9999F);
				flags.set(0, f == i && (i > 0.9999F || state.isFullCube(world, pos)));
		}
	}

	private void renderQuadsFlat(
		BlockRenderView world,
		BlockState state,
		BlockPos pos,
		int light,
		int overlay,
		boolean useWorldLight,
		MatrixStack matrices,
		VertexConsumer vertexConsumer,
		List<BakedQuad> quads,
		BitSet flags
	) {
		for (BakedQuad bakedQuad : quads) {
			if (useWorldLight) {
				this.getQuadDimensions(world, state, pos, bakedQuad.getVertexData(), bakedQuad.getFace(), null, flags);
				BlockPos blockPos = flags.get(0) ? pos.offset(bakedQuad.getFace()) : pos;
				light = WorldRenderer.getLightmapCoordinates(world, state, blockPos);
			}

			float f = world.getBrightness(bakedQuad.getFace(), bakedQuad.hasShade());
			this.renderQuad(world, state, pos, vertexConsumer, matrices.peek(), bakedQuad, f, f, f, f, light, light, light, light, overlay);
		}
	}

	public void render(
		MatrixStack.Entry entry,
		VertexConsumer vertexConsumer,
		@Nullable BlockState state,
		BakedModel bakedModel,
		float red,
		float green,
		float blue,
		int light,
		int overlay
	) {
		Random random = Random.create();
		long l = 42L;

		for (Direction direction : DIRECTIONS) {
			random.setSeed(42L);
			renderQuads(entry, vertexConsumer, red, green, blue, bakedModel.getQuads(state, direction, random), light, overlay);
		}

		random.setSeed(42L);
		renderQuads(entry, vertexConsumer, red, green, blue, bakedModel.getQuads(state, null, random), light, overlay);
	}

	private static void renderQuads(
		MatrixStack.Entry entry, VertexConsumer vertexConsumer, float red, float green, float blue, List<BakedQuad> quads, int light, int overlay
	) {
		for (BakedQuad bakedQuad : quads) {
			float f;
			float g;
			float h;
			if (bakedQuad.hasColor()) {
				f = MathHelper.clamp(red, 0.0F, 1.0F);
				g = MathHelper.clamp(green, 0.0F, 1.0F);
				h = MathHelper.clamp(blue, 0.0F, 1.0F);
			} else {
				f = 1.0F;
				g = 1.0F;
				h = 1.0F;
			}

			vertexConsumer.quad(entry, bakedQuad, f, g, h, 1.0F, light, overlay);
		}
	}

	public static void enableBrightnessCache() {
		((BlockModelRenderer.BrightnessCache)BRIGHTNESS_CACHE.get()).enable();
	}

	public static void disableBrightnessCache() {
		((BlockModelRenderer.BrightnessCache)BRIGHTNESS_CACHE.get()).disable();
	}

	@Environment(EnvType.CLIENT)
	static class AmbientOcclusionCalculator {
		final float[] brightness = new float[4];
		final int[] light = new int[4];

		public AmbientOcclusionCalculator() {
		}

		public void apply(BlockRenderView world, BlockState state, BlockPos pos, Direction direction, float[] fs, BitSet bitSet, boolean bl) {
			BlockPos blockPos = bitSet.get(0) ? pos.offset(direction) : pos;
			BlockModelRenderer.NeighborData neighborData = BlockModelRenderer.NeighborData.getData(direction);
			BlockPos.Mutable mutable = new BlockPos.Mutable();
			BlockModelRenderer.BrightnessCache brightnessCache = (BlockModelRenderer.BrightnessCache)BlockModelRenderer.BRIGHTNESS_CACHE.get();
			mutable.set(blockPos, neighborData.faces[0]);
			BlockState blockState = world.getBlockState(mutable);
			int i = brightnessCache.getInt(blockState, world, mutable);
			float f = brightnessCache.getFloat(blockState, world, mutable);
			mutable.set(blockPos, neighborData.faces[1]);
			BlockState blockState2 = world.getBlockState(mutable);
			int j = brightnessCache.getInt(blockState2, world, mutable);
			float g = brightnessCache.getFloat(blockState2, world, mutable);
			mutable.set(blockPos, neighborData.faces[2]);
			BlockState blockState3 = world.getBlockState(mutable);
			int k = brightnessCache.getInt(blockState3, world, mutable);
			float h = brightnessCache.getFloat(blockState3, world, mutable);
			mutable.set(blockPos, neighborData.faces[3]);
			BlockState blockState4 = world.getBlockState(mutable);
			int l = brightnessCache.getInt(blockState4, world, mutable);
			float m = brightnessCache.getFloat(blockState4, world, mutable);
			BlockState blockState5 = world.getBlockState(mutable.set(blockPos, neighborData.faces[0]).move(direction));
			boolean bl2 = !blockState5.shouldBlockVision(world, mutable) || blockState5.getOpacity() == 0;
			BlockState blockState6 = world.getBlockState(mutable.set(blockPos, neighborData.faces[1]).move(direction));
			boolean bl3 = !blockState6.shouldBlockVision(world, mutable) || blockState6.getOpacity() == 0;
			BlockState blockState7 = world.getBlockState(mutable.set(blockPos, neighborData.faces[2]).move(direction));
			boolean bl4 = !blockState7.shouldBlockVision(world, mutable) || blockState7.getOpacity() == 0;
			BlockState blockState8 = world.getBlockState(mutable.set(blockPos, neighborData.faces[3]).move(direction));
			boolean bl5 = !blockState8.shouldBlockVision(world, mutable) || blockState8.getOpacity() == 0;
			float n;
			int o;
			if (!bl4 && !bl2) {
				n = f;
				o = i;
			} else {
				mutable.set(blockPos, neighborData.faces[0]).move(neighborData.faces[2]);
				BlockState blockState9 = world.getBlockState(mutable);
				n = brightnessCache.getFloat(blockState9, world, mutable);
				o = brightnessCache.getInt(blockState9, world, mutable);
			}

			float p;
			int q;
			if (!bl5 && !bl2) {
				p = f;
				q = i;
			} else {
				mutable.set(blockPos, neighborData.faces[0]).move(neighborData.faces[3]);
				BlockState blockState9 = world.getBlockState(mutable);
				p = brightnessCache.getFloat(blockState9, world, mutable);
				q = brightnessCache.getInt(blockState9, world, mutable);
			}

			float r;
			int s;
			if (!bl4 && !bl3) {
				r = f;
				s = i;
			} else {
				mutable.set(blockPos, neighborData.faces[1]).move(neighborData.faces[2]);
				BlockState blockState9 = world.getBlockState(mutable);
				r = brightnessCache.getFloat(blockState9, world, mutable);
				s = brightnessCache.getInt(blockState9, world, mutable);
			}

			float t;
			int u;
			if (!bl5 && !bl3) {
				t = f;
				u = i;
			} else {
				mutable.set(blockPos, neighborData.faces[1]).move(neighborData.faces[3]);
				BlockState blockState9 = world.getBlockState(mutable);
				t = brightnessCache.getFloat(blockState9, world, mutable);
				u = brightnessCache.getInt(blockState9, world, mutable);
			}

			int v = brightnessCache.getInt(state, world, pos);
			mutable.set(pos, direction);
			BlockState blockState10 = world.getBlockState(mutable);
			if (bitSet.get(0) || !blockState10.isOpaqueFullCube()) {
				v = brightnessCache.getInt(blockState10, world, mutable);
			}

			float w = bitSet.get(0)
				? brightnessCache.getFloat(world.getBlockState(blockPos), world, blockPos)
				: brightnessCache.getFloat(world.getBlockState(pos), world, pos);
			BlockModelRenderer.Translation translation = BlockModelRenderer.Translation.getTranslations(direction);
			if (bitSet.get(1) && neighborData.nonCubicWeight) {
				float x = (m + f + p + w) * 0.25F;
				float y = (h + f + n + w) * 0.25F;
				float z = (h + g + r + w) * 0.25F;
				float aa = (m + g + t + w) * 0.25F;
				float ab = fs[neighborData.field_4192[0].shape] * fs[neighborData.field_4192[1].shape];
				float ac = fs[neighborData.field_4192[2].shape] * fs[neighborData.field_4192[3].shape];
				float ad = fs[neighborData.field_4192[4].shape] * fs[neighborData.field_4192[5].shape];
				float ae = fs[neighborData.field_4192[6].shape] * fs[neighborData.field_4192[7].shape];
				float af = fs[neighborData.field_4185[0].shape] * fs[neighborData.field_4185[1].shape];
				float ag = fs[neighborData.field_4185[2].shape] * fs[neighborData.field_4185[3].shape];
				float ah = fs[neighborData.field_4185[4].shape] * fs[neighborData.field_4185[5].shape];
				float ai = fs[neighborData.field_4185[6].shape] * fs[neighborData.field_4185[7].shape];
				float aj = fs[neighborData.field_4180[0].shape] * fs[neighborData.field_4180[1].shape];
				float ak = fs[neighborData.field_4180[2].shape] * fs[neighborData.field_4180[3].shape];
				float al = fs[neighborData.field_4180[4].shape] * fs[neighborData.field_4180[5].shape];
				float am = fs[neighborData.field_4180[6].shape] * fs[neighborData.field_4180[7].shape];
				float an = fs[neighborData.field_4188[0].shape] * fs[neighborData.field_4188[1].shape];
				float ao = fs[neighborData.field_4188[2].shape] * fs[neighborData.field_4188[3].shape];
				float ap = fs[neighborData.field_4188[4].shape] * fs[neighborData.field_4188[5].shape];
				float aq = fs[neighborData.field_4188[6].shape] * fs[neighborData.field_4188[7].shape];
				this.brightness[translation.firstCorner] = Math.clamp(x * ab + y * ac + z * ad + aa * ae, 0.0F, 1.0F);
				this.brightness[translation.secondCorner] = Math.clamp(x * af + y * ag + z * ah + aa * ai, 0.0F, 1.0F);
				this.brightness[translation.thirdCorner] = Math.clamp(x * aj + y * ak + z * al + aa * am, 0.0F, 1.0F);
				this.brightness[translation.fourthCorner] = Math.clamp(x * an + y * ao + z * ap + aa * aq, 0.0F, 1.0F);
				int ar = this.getAmbientOcclusionBrightness(l, i, q, v);
				int as = this.getAmbientOcclusionBrightness(k, i, o, v);
				int at = this.getAmbientOcclusionBrightness(k, j, s, v);
				int au = this.getAmbientOcclusionBrightness(l, j, u, v);
				this.light[translation.firstCorner] = this.getBrightness(ar, as, at, au, ab, ac, ad, ae);
				this.light[translation.secondCorner] = this.getBrightness(ar, as, at, au, af, ag, ah, ai);
				this.light[translation.thirdCorner] = this.getBrightness(ar, as, at, au, aj, ak, al, am);
				this.light[translation.fourthCorner] = this.getBrightness(ar, as, at, au, an, ao, ap, aq);
			} else {
				float x = (m + f + p + w) * 0.25F;
				float y = (h + f + n + w) * 0.25F;
				float z = (h + g + r + w) * 0.25F;
				float aa = (m + g + t + w) * 0.25F;
				this.light[translation.firstCorner] = this.getAmbientOcclusionBrightness(l, i, q, v);
				this.light[translation.secondCorner] = this.getAmbientOcclusionBrightness(k, i, o, v);
				this.light[translation.thirdCorner] = this.getAmbientOcclusionBrightness(k, j, s, v);
				this.light[translation.fourthCorner] = this.getAmbientOcclusionBrightness(l, j, u, v);
				this.brightness[translation.firstCorner] = x;
				this.brightness[translation.secondCorner] = y;
				this.brightness[translation.thirdCorner] = z;
				this.brightness[translation.fourthCorner] = aa;
			}

			float x = world.getBrightness(direction, bl);

			for (int av = 0; av < this.brightness.length; av++) {
				this.brightness[av] = this.brightness[av] * x;
			}
		}

		private int getAmbientOcclusionBrightness(int i, int j, int k, int l) {
			if (i == 0) {
				i = l;
			}

			if (j == 0) {
				j = l;
			}

			if (k == 0) {
				k = l;
			}

			return i + j + k + l >> 2 & 16711935;
		}

		private int getBrightness(int i, int j, int k, int l, float f, float g, float h, float m) {
			int n = (int)((float)(i >> 16 & 0xFF) * f + (float)(j >> 16 & 0xFF) * g + (float)(k >> 16 & 0xFF) * h + (float)(l >> 16 & 0xFF) * m) & 0xFF;
			int o = (int)((float)(i & 0xFF) * f + (float)(j & 0xFF) * g + (float)(k & 0xFF) * h + (float)(l & 0xFF) * m) & 0xFF;
			return n << 16 | o;
		}
	}

	@Environment(EnvType.CLIENT)
	static class BrightnessCache {
		private boolean enabled;
		private final Long2IntLinkedOpenHashMap intCache = Util.make(() -> {
			Long2IntLinkedOpenHashMap long2IntLinkedOpenHashMap = new Long2IntLinkedOpenHashMap(100, 0.25F) {
				@Override
				protected void rehash(int newN) {
				}
			};
			long2IntLinkedOpenHashMap.defaultReturnValue(Integer.MAX_VALUE);
			return long2IntLinkedOpenHashMap;
		});
		private final Long2FloatLinkedOpenHashMap floatCache = Util.make(() -> {
			Long2FloatLinkedOpenHashMap long2FloatLinkedOpenHashMap = new Long2FloatLinkedOpenHashMap(100, 0.25F) {
				@Override
				protected void rehash(int newN) {
				}
			};
			long2FloatLinkedOpenHashMap.defaultReturnValue(Float.NaN);
			return long2FloatLinkedOpenHashMap;
		});

		private BrightnessCache() {
		}

		public void enable() {
			this.enabled = true;
		}

		public void disable() {
			this.enabled = false;
			this.intCache.clear();
			this.floatCache.clear();
		}

		public int getInt(BlockState state, BlockRenderView world, BlockPos pos) {
			long l = pos.asLong();
			if (this.enabled) {
				int i = this.intCache.get(l);
				if (i != Integer.MAX_VALUE) {
					return i;
				}
			}

			int i = WorldRenderer.getLightmapCoordinates(world, state, pos);
			if (this.enabled) {
				if (this.intCache.size() == 100) {
					this.intCache.removeFirstInt();
				}

				this.intCache.put(l, i);
			}

			return i;
		}

		public float getFloat(BlockState state, BlockRenderView blockView, BlockPos pos) {
			long l = pos.asLong();
			if (this.enabled) {
				float f = this.floatCache.get(l);
				if (!Float.isNaN(f)) {
					return f;
				}
			}

			float f = state.getAmbientOcclusionLightLevel(blockView, pos);
			if (this.enabled) {
				if (this.floatCache.size() == 100) {
					this.floatCache.removeFirstFloat();
				}

				this.floatCache.put(l, f);
			}

			return f;
		}
	}

	@Environment(EnvType.CLIENT)
	protected static enum NeighborData {
		DOWN(
			new Direction[]{Direction.WEST, Direction.EAST, Direction.NORTH, Direction.SOUTH},
			0.5F,
			true,
			new BlockModelRenderer.NeighborOrientation[]{
				BlockModelRenderer.NeighborOrientation.FLIP_WEST,
				BlockModelRenderer.NeighborOrientation.SOUTH,
				BlockModelRenderer.NeighborOrientation.FLIP_WEST,
				BlockModelRenderer.NeighborOrientation.FLIP_SOUTH,
				BlockModelRenderer.NeighborOrientation.WEST,
				BlockModelRenderer.NeighborOrientation.FLIP_SOUTH,
				BlockModelRenderer.NeighborOrientation.WEST,
				BlockModelRenderer.NeighborOrientation.SOUTH
			},
			new BlockModelRenderer.NeighborOrientation[]{
				BlockModelRenderer.NeighborOrientation.FLIP_WEST,
				BlockModelRenderer.NeighborOrientation.NORTH,
				BlockModelRenderer.NeighborOrientation.FLIP_WEST,
				BlockModelRenderer.NeighborOrientation.FLIP_NORTH,
				BlockModelRenderer.NeighborOrientation.WEST,
				BlockModelRenderer.NeighborOrientation.FLIP_NORTH,
				BlockModelRenderer.NeighborOrientation.WEST,
				BlockModelRenderer.NeighborOrientation.NORTH
			},
			new BlockModelRenderer.NeighborOrientation[]{
				BlockModelRenderer.NeighborOrientation.FLIP_EAST,
				BlockModelRenderer.NeighborOrientation.NORTH,
				BlockModelRenderer.NeighborOrientation.FLIP_EAST,
				BlockModelRenderer.NeighborOrientation.FLIP_NORTH,
				BlockModelRenderer.NeighborOrientation.EAST,
				BlockModelRenderer.NeighborOrientation.FLIP_NORTH,
				BlockModelRenderer.NeighborOrientation.EAST,
				BlockModelRenderer.NeighborOrientation.NORTH
			},
			new BlockModelRenderer.NeighborOrientation[]{
				BlockModelRenderer.NeighborOrientation.FLIP_EAST,
				BlockModelRenderer.NeighborOrientation.SOUTH,
				BlockModelRenderer.NeighborOrientation.FLIP_EAST,
				BlockModelRenderer.NeighborOrientation.FLIP_SOUTH,
				BlockModelRenderer.NeighborOrientation.EAST,
				BlockModelRenderer.NeighborOrientation.FLIP_SOUTH,
				BlockModelRenderer.NeighborOrientation.EAST,
				BlockModelRenderer.NeighborOrientation.SOUTH
			}
		),
		UP(
			new Direction[]{Direction.EAST, Direction.WEST, Direction.NORTH, Direction.SOUTH},
			1.0F,
			true,
			new BlockModelRenderer.NeighborOrientation[]{
				BlockModelRenderer.NeighborOrientation.EAST,
				BlockModelRenderer.NeighborOrientation.SOUTH,
				BlockModelRenderer.NeighborOrientation.EAST,
				BlockModelRenderer.NeighborOrientation.FLIP_SOUTH,
				BlockModelRenderer.NeighborOrientation.FLIP_EAST,
				BlockModelRenderer.NeighborOrientation.FLIP_SOUTH,
				BlockModelRenderer.NeighborOrientation.FLIP_EAST,
				BlockModelRenderer.NeighborOrientation.SOUTH
			},
			new BlockModelRenderer.NeighborOrientation[]{
				BlockModelRenderer.NeighborOrientation.EAST,
				BlockModelRenderer.NeighborOrientation.NORTH,
				BlockModelRenderer.NeighborOrientation.EAST,
				BlockModelRenderer.NeighborOrientation.FLIP_NORTH,
				BlockModelRenderer.NeighborOrientation.FLIP_EAST,
				BlockModelRenderer.NeighborOrientation.FLIP_NORTH,
				BlockModelRenderer.NeighborOrientation.FLIP_EAST,
				BlockModelRenderer.NeighborOrientation.NORTH
			},
			new BlockModelRenderer.NeighborOrientation[]{
				BlockModelRenderer.NeighborOrientation.WEST,
				BlockModelRenderer.NeighborOrientation.NORTH,
				BlockModelRenderer.NeighborOrientation.WEST,
				BlockModelRenderer.NeighborOrientation.FLIP_NORTH,
				BlockModelRenderer.NeighborOrientation.FLIP_WEST,
				BlockModelRenderer.NeighborOrientation.FLIP_NORTH,
				BlockModelRenderer.NeighborOrientation.FLIP_WEST,
				BlockModelRenderer.NeighborOrientation.NORTH
			},
			new BlockModelRenderer.NeighborOrientation[]{
				BlockModelRenderer.NeighborOrientation.WEST,
				BlockModelRenderer.NeighborOrientation.SOUTH,
				BlockModelRenderer.NeighborOrientation.WEST,
				BlockModelRenderer.NeighborOrientation.FLIP_SOUTH,
				BlockModelRenderer.NeighborOrientation.FLIP_WEST,
				BlockModelRenderer.NeighborOrientation.FLIP_SOUTH,
				BlockModelRenderer.NeighborOrientation.FLIP_WEST,
				BlockModelRenderer.NeighborOrientation.SOUTH
			}
		),
		NORTH(
			new Direction[]{Direction.UP, Direction.DOWN, Direction.EAST, Direction.WEST},
			0.8F,
			true,
			new BlockModelRenderer.NeighborOrientation[]{
				BlockModelRenderer.NeighborOrientation.UP,
				BlockModelRenderer.NeighborOrientation.FLIP_WEST,
				BlockModelRenderer.NeighborOrientation.UP,
				BlockModelRenderer.NeighborOrientation.WEST,
				BlockModelRenderer.NeighborOrientation.FLIP_UP,
				BlockModelRenderer.NeighborOrientation.WEST,
				BlockModelRenderer.NeighborOrientation.FLIP_UP,
				BlockModelRenderer.NeighborOrientation.FLIP_WEST
			},
			new BlockModelRenderer.NeighborOrientation[]{
				BlockModelRenderer.NeighborOrientation.UP,
				BlockModelRenderer.NeighborOrientation.FLIP_EAST,
				BlockModelRenderer.NeighborOrientation.UP,
				BlockModelRenderer.NeighborOrientation.EAST,
				BlockModelRenderer.NeighborOrientation.FLIP_UP,
				BlockModelRenderer.NeighborOrientation.EAST,
				BlockModelRenderer.NeighborOrientation.FLIP_UP,
				BlockModelRenderer.NeighborOrientation.FLIP_EAST
			},
			new BlockModelRenderer.NeighborOrientation[]{
				BlockModelRenderer.NeighborOrientation.DOWN,
				BlockModelRenderer.NeighborOrientation.FLIP_EAST,
				BlockModelRenderer.NeighborOrientation.DOWN,
				BlockModelRenderer.NeighborOrientation.EAST,
				BlockModelRenderer.NeighborOrientation.FLIP_DOWN,
				BlockModelRenderer.NeighborOrientation.EAST,
				BlockModelRenderer.NeighborOrientation.FLIP_DOWN,
				BlockModelRenderer.NeighborOrientation.FLIP_EAST
			},
			new BlockModelRenderer.NeighborOrientation[]{
				BlockModelRenderer.NeighborOrientation.DOWN,
				BlockModelRenderer.NeighborOrientation.FLIP_WEST,
				BlockModelRenderer.NeighborOrientation.DOWN,
				BlockModelRenderer.NeighborOrientation.WEST,
				BlockModelRenderer.NeighborOrientation.FLIP_DOWN,
				BlockModelRenderer.NeighborOrientation.WEST,
				BlockModelRenderer.NeighborOrientation.FLIP_DOWN,
				BlockModelRenderer.NeighborOrientation.FLIP_WEST
			}
		),
		SOUTH(
			new Direction[]{Direction.WEST, Direction.EAST, Direction.DOWN, Direction.UP},
			0.8F,
			true,
			new BlockModelRenderer.NeighborOrientation[]{
				BlockModelRenderer.NeighborOrientation.UP,
				BlockModelRenderer.NeighborOrientation.FLIP_WEST,
				BlockModelRenderer.NeighborOrientation.FLIP_UP,
				BlockModelRenderer.NeighborOrientation.FLIP_WEST,
				BlockModelRenderer.NeighborOrientation.FLIP_UP,
				BlockModelRenderer.NeighborOrientation.WEST,
				BlockModelRenderer.NeighborOrientation.UP,
				BlockModelRenderer.NeighborOrientation.WEST
			},
			new BlockModelRenderer.NeighborOrientation[]{
				BlockModelRenderer.NeighborOrientation.DOWN,
				BlockModelRenderer.NeighborOrientation.FLIP_WEST,
				BlockModelRenderer.NeighborOrientation.FLIP_DOWN,
				BlockModelRenderer.NeighborOrientation.FLIP_WEST,
				BlockModelRenderer.NeighborOrientation.FLIP_DOWN,
				BlockModelRenderer.NeighborOrientation.WEST,
				BlockModelRenderer.NeighborOrientation.DOWN,
				BlockModelRenderer.NeighborOrientation.WEST
			},
			new BlockModelRenderer.NeighborOrientation[]{
				BlockModelRenderer.NeighborOrientation.DOWN,
				BlockModelRenderer.NeighborOrientation.FLIP_EAST,
				BlockModelRenderer.NeighborOrientation.FLIP_DOWN,
				BlockModelRenderer.NeighborOrientation.FLIP_EAST,
				BlockModelRenderer.NeighborOrientation.FLIP_DOWN,
				BlockModelRenderer.NeighborOrientation.EAST,
				BlockModelRenderer.NeighborOrientation.DOWN,
				BlockModelRenderer.NeighborOrientation.EAST
			},
			new BlockModelRenderer.NeighborOrientation[]{
				BlockModelRenderer.NeighborOrientation.UP,
				BlockModelRenderer.NeighborOrientation.FLIP_EAST,
				BlockModelRenderer.NeighborOrientation.FLIP_UP,
				BlockModelRenderer.NeighborOrientation.FLIP_EAST,
				BlockModelRenderer.NeighborOrientation.FLIP_UP,
				BlockModelRenderer.NeighborOrientation.EAST,
				BlockModelRenderer.NeighborOrientation.UP,
				BlockModelRenderer.NeighborOrientation.EAST
			}
		),
		WEST(
			new Direction[]{Direction.UP, Direction.DOWN, Direction.NORTH, Direction.SOUTH},
			0.6F,
			true,
			new BlockModelRenderer.NeighborOrientation[]{
				BlockModelRenderer.NeighborOrientation.UP,
				BlockModelRenderer.NeighborOrientation.SOUTH,
				BlockModelRenderer.NeighborOrientation.UP,
				BlockModelRenderer.NeighborOrientation.FLIP_SOUTH,
				BlockModelRenderer.NeighborOrientation.FLIP_UP,
				BlockModelRenderer.NeighborOrientation.FLIP_SOUTH,
				BlockModelRenderer.NeighborOrientation.FLIP_UP,
				BlockModelRenderer.NeighborOrientation.SOUTH
			},
			new BlockModelRenderer.NeighborOrientation[]{
				BlockModelRenderer.NeighborOrientation.UP,
				BlockModelRenderer.NeighborOrientation.NORTH,
				BlockModelRenderer.NeighborOrientation.UP,
				BlockModelRenderer.NeighborOrientation.FLIP_NORTH,
				BlockModelRenderer.NeighborOrientation.FLIP_UP,
				BlockModelRenderer.NeighborOrientation.FLIP_NORTH,
				BlockModelRenderer.NeighborOrientation.FLIP_UP,
				BlockModelRenderer.NeighborOrientation.NORTH
			},
			new BlockModelRenderer.NeighborOrientation[]{
				BlockModelRenderer.NeighborOrientation.DOWN,
				BlockModelRenderer.NeighborOrientation.NORTH,
				BlockModelRenderer.NeighborOrientation.DOWN,
				BlockModelRenderer.NeighborOrientation.FLIP_NORTH,
				BlockModelRenderer.NeighborOrientation.FLIP_DOWN,
				BlockModelRenderer.NeighborOrientation.FLIP_NORTH,
				BlockModelRenderer.NeighborOrientation.FLIP_DOWN,
				BlockModelRenderer.NeighborOrientation.NORTH
			},
			new BlockModelRenderer.NeighborOrientation[]{
				BlockModelRenderer.NeighborOrientation.DOWN,
				BlockModelRenderer.NeighborOrientation.SOUTH,
				BlockModelRenderer.NeighborOrientation.DOWN,
				BlockModelRenderer.NeighborOrientation.FLIP_SOUTH,
				BlockModelRenderer.NeighborOrientation.FLIP_DOWN,
				BlockModelRenderer.NeighborOrientation.FLIP_SOUTH,
				BlockModelRenderer.NeighborOrientation.FLIP_DOWN,
				BlockModelRenderer.NeighborOrientation.SOUTH
			}
		),
		EAST(
			new Direction[]{Direction.DOWN, Direction.UP, Direction.NORTH, Direction.SOUTH},
			0.6F,
			true,
			new BlockModelRenderer.NeighborOrientation[]{
				BlockModelRenderer.NeighborOrientation.FLIP_DOWN,
				BlockModelRenderer.NeighborOrientation.SOUTH,
				BlockModelRenderer.NeighborOrientation.FLIP_DOWN,
				BlockModelRenderer.NeighborOrientation.FLIP_SOUTH,
				BlockModelRenderer.NeighborOrientation.DOWN,
				BlockModelRenderer.NeighborOrientation.FLIP_SOUTH,
				BlockModelRenderer.NeighborOrientation.DOWN,
				BlockModelRenderer.NeighborOrientation.SOUTH
			},
			new BlockModelRenderer.NeighborOrientation[]{
				BlockModelRenderer.NeighborOrientation.FLIP_DOWN,
				BlockModelRenderer.NeighborOrientation.NORTH,
				BlockModelRenderer.NeighborOrientation.FLIP_DOWN,
				BlockModelRenderer.NeighborOrientation.FLIP_NORTH,
				BlockModelRenderer.NeighborOrientation.DOWN,
				BlockModelRenderer.NeighborOrientation.FLIP_NORTH,
				BlockModelRenderer.NeighborOrientation.DOWN,
				BlockModelRenderer.NeighborOrientation.NORTH
			},
			new BlockModelRenderer.NeighborOrientation[]{
				BlockModelRenderer.NeighborOrientation.FLIP_UP,
				BlockModelRenderer.NeighborOrientation.NORTH,
				BlockModelRenderer.NeighborOrientation.FLIP_UP,
				BlockModelRenderer.NeighborOrientation.FLIP_NORTH,
				BlockModelRenderer.NeighborOrientation.UP,
				BlockModelRenderer.NeighborOrientation.FLIP_NORTH,
				BlockModelRenderer.NeighborOrientation.UP,
				BlockModelRenderer.NeighborOrientation.NORTH
			},
			new BlockModelRenderer.NeighborOrientation[]{
				BlockModelRenderer.NeighborOrientation.FLIP_UP,
				BlockModelRenderer.NeighborOrientation.SOUTH,
				BlockModelRenderer.NeighborOrientation.FLIP_UP,
				BlockModelRenderer.NeighborOrientation.FLIP_SOUTH,
				BlockModelRenderer.NeighborOrientation.UP,
				BlockModelRenderer.NeighborOrientation.FLIP_SOUTH,
				BlockModelRenderer.NeighborOrientation.UP,
				BlockModelRenderer.NeighborOrientation.SOUTH
			}
		);

		final Direction[] faces;
		final boolean nonCubicWeight;
		final BlockModelRenderer.NeighborOrientation[] field_4192;
		final BlockModelRenderer.NeighborOrientation[] field_4185;
		final BlockModelRenderer.NeighborOrientation[] field_4180;
		final BlockModelRenderer.NeighborOrientation[] field_4188;
		private static final BlockModelRenderer.NeighborData[] VALUES = Util.make(new BlockModelRenderer.NeighborData[6], values -> {
			values[Direction.DOWN.getId()] = DOWN;
			values[Direction.UP.getId()] = UP;
			values[Direction.NORTH.getId()] = NORTH;
			values[Direction.SOUTH.getId()] = SOUTH;
			values[Direction.WEST.getId()] = WEST;
			values[Direction.EAST.getId()] = EAST;
		});

		private NeighborData(
			final Direction[] faces,
			final float f,
			final boolean nonCubicWeight,
			final BlockModelRenderer.NeighborOrientation[] neighborOrientations,
			final BlockModelRenderer.NeighborOrientation[] neighborOrientations2,
			final BlockModelRenderer.NeighborOrientation[] neighborOrientations3,
			final BlockModelRenderer.NeighborOrientation[] neighborOrientations4
		) {
			this.faces = faces;
			this.nonCubicWeight = nonCubicWeight;
			this.field_4192 = neighborOrientations;
			this.field_4185 = neighborOrientations2;
			this.field_4180 = neighborOrientations3;
			this.field_4188 = neighborOrientations4;
		}

		public static BlockModelRenderer.NeighborData getData(Direction direction) {
			return VALUES[direction.getId()];
		}
	}

	@Environment(EnvType.CLIENT)
	protected static enum NeighborOrientation {
		DOWN(Direction.DOWN, false),
		UP(Direction.UP, false),
		NORTH(Direction.NORTH, false),
		SOUTH(Direction.SOUTH, false),
		WEST(Direction.WEST, false),
		EAST(Direction.EAST, false),
		FLIP_DOWN(Direction.DOWN, true),
		FLIP_UP(Direction.UP, true),
		FLIP_NORTH(Direction.NORTH, true),
		FLIP_SOUTH(Direction.SOUTH, true),
		FLIP_WEST(Direction.WEST, true),
		FLIP_EAST(Direction.EAST, true);

		final int shape;

		private NeighborOrientation(final Direction direction, final boolean flip) {
			this.shape = direction.getId() + (flip ? BlockModelRenderer.DIRECTIONS.length : 0);
		}
	}

	@Environment(EnvType.CLIENT)
	static enum Translation {
		DOWN(0, 1, 2, 3),
		UP(2, 3, 0, 1),
		NORTH(3, 0, 1, 2),
		SOUTH(0, 1, 2, 3),
		WEST(3, 0, 1, 2),
		EAST(1, 2, 3, 0);

		final int firstCorner;
		final int secondCorner;
		final int thirdCorner;
		final int fourthCorner;
		private static final BlockModelRenderer.Translation[] VALUES = Util.make(new BlockModelRenderer.Translation[6], values -> {
			values[Direction.DOWN.getId()] = DOWN;
			values[Direction.UP.getId()] = UP;
			values[Direction.NORTH.getId()] = NORTH;
			values[Direction.SOUTH.getId()] = SOUTH;
			values[Direction.WEST.getId()] = WEST;
			values[Direction.EAST.getId()] = EAST;
		});

		private Translation(final int firstCorner, final int secondCorner, final int thirdCorner, final int fourthCorner) {
			this.firstCorner = firstCorner;
			this.secondCorner = secondCorner;
			this.thirdCorner = thirdCorner;
			this.fourthCorner = fourthCorner;
		}

		public static BlockModelRenderer.Translation getTranslations(Direction direction) {
			return VALUES[direction.getId()];
		}
	}
}
