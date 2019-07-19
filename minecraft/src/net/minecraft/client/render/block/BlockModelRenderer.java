package net.minecraft.client.render.block;

import com.mojang.blaze3d.platform.GlStateManager;
import it.unimi.dsi.fastutil.longs.Long2FloatLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2IntLinkedOpenHashMap;
import java.util.BitSet;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.util.Util;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.BlockRenderView;

@Environment(EnvType.CLIENT)
public class BlockModelRenderer {
	private final BlockColors colorMap;
	private static final ThreadLocal<BlockModelRenderer.BrightnessCache> brightnessCache = ThreadLocal.withInitial(() -> new BlockModelRenderer.BrightnessCache());

	public BlockModelRenderer(BlockColors blockColors) {
		this.colorMap = blockColors;
	}

	public boolean tesselate(
		BlockRenderView view, BakedModel model, BlockState state, BlockPos pos, BufferBuilder buffer, boolean testSides, Random random, long l
	) {
		boolean bl = MinecraftClient.isAmbientOcclusionEnabled() && state.getLuminance() == 0 && model.useAmbientOcclusion();

		try {
			return bl
				? this.tesselateSmooth(view, model, state, pos, buffer, testSides, random, l)
				: this.tesselateFlat(view, model, state, pos, buffer, testSides, random, l);
		} catch (Throwable var14) {
			CrashReport crashReport = CrashReport.create(var14, "Tesselating block model");
			CrashReportSection crashReportSection = crashReport.addElement("Block model being tesselated");
			CrashReportSection.addBlockInfo(crashReportSection, pos, state);
			crashReportSection.add("Using AO", bl);
			throw new CrashException(crashReport);
		}
	}

	public boolean tesselateSmooth(
		BlockRenderView view, BakedModel model, BlockState state, BlockPos pos, BufferBuilder buffer, boolean testSides, Random random, long l
	) {
		boolean bl = false;
		float[] fs = new float[Direction.values().length * 2];
		BitSet bitSet = new BitSet(3);
		BlockModelRenderer.AmbientOcclusionCalculator ambientOcclusionCalculator = new BlockModelRenderer.AmbientOcclusionCalculator();

		for (Direction direction : Direction.values()) {
			random.setSeed(l);
			List<BakedQuad> list = model.getQuads(state, direction, random);
			if (!list.isEmpty() && (!testSides || Block.shouldDrawSide(state, view, pos, direction))) {
				this.tesselateQuadsSmooth(view, state, pos, buffer, list, fs, bitSet, ambientOcclusionCalculator);
				bl = true;
			}
		}

		random.setSeed(l);
		List<BakedQuad> list2 = model.getQuads(state, null, random);
		if (!list2.isEmpty()) {
			this.tesselateQuadsSmooth(view, state, pos, buffer, list2, fs, bitSet, ambientOcclusionCalculator);
			bl = true;
		}

		return bl;
	}

	public boolean tesselateFlat(
		BlockRenderView view, BakedModel model, BlockState state, BlockPos pos, BufferBuilder buffer, boolean testSides, Random random, long seed
	) {
		boolean bl = false;
		BitSet bitSet = new BitSet(3);

		for (Direction direction : Direction.values()) {
			random.setSeed(seed);
			List<BakedQuad> list = model.getQuads(state, direction, random);
			if (!list.isEmpty() && (!testSides || Block.shouldDrawSide(state, view, pos, direction))) {
				int i = state.getBlockBrightness(view, pos.offset(direction));
				this.tesselateQuadsFlat(view, state, pos, i, false, buffer, list, bitSet);
				bl = true;
			}
		}

		random.setSeed(seed);
		List<BakedQuad> list2 = model.getQuads(state, null, random);
		if (!list2.isEmpty()) {
			this.tesselateQuadsFlat(view, state, pos, -1, true, buffer, list2, bitSet);
			bl = true;
		}

		return bl;
	}

	private void tesselateQuadsSmooth(
		BlockRenderView view,
		BlockState state,
		BlockPos pos,
		BufferBuilder buffer,
		List<BakedQuad> quads,
		float[] faceShape,
		BitSet shapeState,
		BlockModelRenderer.AmbientOcclusionCalculator ambientOcclusionCalculator
	) {
		Vec3d vec3d = state.getOffsetPos(view, pos);
		double d = (double)pos.getX() + vec3d.x;
		double e = (double)pos.getY() + vec3d.y;
		double f = (double)pos.getZ() + vec3d.z;
		int i = 0;

		for (int j = quads.size(); i < j; i++) {
			BakedQuad bakedQuad = (BakedQuad)quads.get(i);
			this.getQuadDimensions(view, state, pos, bakedQuad.getVertexData(), bakedQuad.getFace(), faceShape, shapeState);
			ambientOcclusionCalculator.apply(view, state, pos, bakedQuad.getFace(), faceShape, shapeState);
			buffer.putVertexData(bakedQuad.getVertexData());
			buffer.brightness(
				ambientOcclusionCalculator.brightness[0],
				ambientOcclusionCalculator.brightness[1],
				ambientOcclusionCalculator.brightness[2],
				ambientOcclusionCalculator.brightness[3]
			);
			if (bakedQuad.hasColor()) {
				int k = this.colorMap.getColor(state, view, pos, bakedQuad.getColorIndex());
				float g = (float)(k >> 16 & 0xFF) / 255.0F;
				float h = (float)(k >> 8 & 0xFF) / 255.0F;
				float l = (float)(k & 0xFF) / 255.0F;
				buffer.multiplyColor(
					ambientOcclusionCalculator.colorMultiplier[0] * g, ambientOcclusionCalculator.colorMultiplier[0] * h, ambientOcclusionCalculator.colorMultiplier[0] * l, 4
				);
				buffer.multiplyColor(
					ambientOcclusionCalculator.colorMultiplier[1] * g, ambientOcclusionCalculator.colorMultiplier[1] * h, ambientOcclusionCalculator.colorMultiplier[1] * l, 3
				);
				buffer.multiplyColor(
					ambientOcclusionCalculator.colorMultiplier[2] * g, ambientOcclusionCalculator.colorMultiplier[2] * h, ambientOcclusionCalculator.colorMultiplier[2] * l, 2
				);
				buffer.multiplyColor(
					ambientOcclusionCalculator.colorMultiplier[3] * g, ambientOcclusionCalculator.colorMultiplier[3] * h, ambientOcclusionCalculator.colorMultiplier[3] * l, 1
				);
			} else {
				buffer.multiplyColor(
					ambientOcclusionCalculator.colorMultiplier[0], ambientOcclusionCalculator.colorMultiplier[0], ambientOcclusionCalculator.colorMultiplier[0], 4
				);
				buffer.multiplyColor(
					ambientOcclusionCalculator.colorMultiplier[1], ambientOcclusionCalculator.colorMultiplier[1], ambientOcclusionCalculator.colorMultiplier[1], 3
				);
				buffer.multiplyColor(
					ambientOcclusionCalculator.colorMultiplier[2], ambientOcclusionCalculator.colorMultiplier[2], ambientOcclusionCalculator.colorMultiplier[2], 2
				);
				buffer.multiplyColor(
					ambientOcclusionCalculator.colorMultiplier[3], ambientOcclusionCalculator.colorMultiplier[3], ambientOcclusionCalculator.colorMultiplier[3], 1
				);
			}

			buffer.postPosition(d, e, f);
		}
	}

	private void getQuadDimensions(BlockRenderView world, BlockState state, BlockPos pos, int[] vertexData, Direction face, @Nullable float[] box, BitSet flags) {
		float f = 32.0F;
		float g = 32.0F;
		float h = 32.0F;
		float i = -32.0F;
		float j = -32.0F;
		float k = -32.0F;

		for (int l = 0; l < 4; l++) {
			float m = Float.intBitsToFloat(vertexData[l * 7]);
			float n = Float.intBitsToFloat(vertexData[l * 7 + 1]);
			float o = Float.intBitsToFloat(vertexData[l * 7 + 2]);
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
			int l = Direction.values().length;
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
				flags.set(0, g == j && (g < 1.0E-4F || state.method_21743(world, pos)));
				break;
			case UP:
				flags.set(1, f >= 1.0E-4F || h >= 1.0E-4F || i <= 0.9999F || k <= 0.9999F);
				flags.set(0, g == j && (j > 0.9999F || state.method_21743(world, pos)));
				break;
			case NORTH:
				flags.set(1, f >= 1.0E-4F || g >= 1.0E-4F || i <= 0.9999F || j <= 0.9999F);
				flags.set(0, h == k && (h < 1.0E-4F || state.method_21743(world, pos)));
				break;
			case SOUTH:
				flags.set(1, f >= 1.0E-4F || g >= 1.0E-4F || i <= 0.9999F || j <= 0.9999F);
				flags.set(0, h == k && (k > 0.9999F || state.method_21743(world, pos)));
				break;
			case WEST:
				flags.set(1, g >= 1.0E-4F || h >= 1.0E-4F || j <= 0.9999F || k <= 0.9999F);
				flags.set(0, f == i && (f < 1.0E-4F || state.method_21743(world, pos)));
				break;
			case EAST:
				flags.set(1, g >= 1.0E-4F || h >= 1.0E-4F || j <= 0.9999F || k <= 0.9999F);
				flags.set(0, f == i && (i > 0.9999F || state.method_21743(world, pos)));
		}
	}

	private void tesselateQuadsFlat(
		BlockRenderView view, BlockState state, BlockPos pos, int brightness, boolean checkBrightness, BufferBuilder buffer, List<BakedQuad> quads, BitSet bitSet
	) {
		Vec3d vec3d = state.getOffsetPos(view, pos);
		double d = (double)pos.getX() + vec3d.x;
		double e = (double)pos.getY() + vec3d.y;
		double f = (double)pos.getZ() + vec3d.z;
		int i = 0;

		for (int j = quads.size(); i < j; i++) {
			BakedQuad bakedQuad = (BakedQuad)quads.get(i);
			if (checkBrightness) {
				this.getQuadDimensions(view, state, pos, bakedQuad.getVertexData(), bakedQuad.getFace(), null, bitSet);
				BlockPos blockPos = bitSet.get(0) ? pos.offset(bakedQuad.getFace()) : pos;
				brightness = state.getBlockBrightness(view, blockPos);
			}

			buffer.putVertexData(bakedQuad.getVertexData());
			buffer.brightness(brightness, brightness, brightness, brightness);
			if (bakedQuad.hasColor()) {
				int k = this.colorMap.getColor(state, view, pos, bakedQuad.getColorIndex());
				float g = (float)(k >> 16 & 0xFF) / 255.0F;
				float h = (float)(k >> 8 & 0xFF) / 255.0F;
				float l = (float)(k & 0xFF) / 255.0F;
				buffer.multiplyColor(g, h, l, 4);
				buffer.multiplyColor(g, h, l, 3);
				buffer.multiplyColor(g, h, l, 2);
				buffer.multiplyColor(g, h, l, 1);
			}

			buffer.postPosition(d, e, f);
		}
	}

	public void render(BakedModel model, float colorMultiplier, float red, float green, float f) {
		this.render(null, model, colorMultiplier, red, green, f);
	}

	public void render(@Nullable BlockState state, BakedModel model, float colorMultiplier, float red, float green, float f) {
		Random random = new Random();
		long l = 42L;

		for (Direction direction : Direction.values()) {
			random.setSeed(42L);
			this.renderQuad(colorMultiplier, red, green, f, model.getQuads(state, direction, random));
		}

		random.setSeed(42L);
		this.renderQuad(colorMultiplier, red, green, f, model.getQuads(state, null, random));
	}

	public void render(BakedModel model, BlockState state, float colorMultiplier, boolean bl) {
		GlStateManager.rotatef(90.0F, 0.0F, 1.0F, 0.0F);
		int i = this.colorMap.getColor(state, null, null, 0);
		float f = (float)(i >> 16 & 0xFF) / 255.0F;
		float g = (float)(i >> 8 & 0xFF) / 255.0F;
		float h = (float)(i & 0xFF) / 255.0F;
		if (!bl) {
			GlStateManager.color4f(colorMultiplier, colorMultiplier, colorMultiplier, 1.0F);
		}

		this.render(state, model, colorMultiplier, f, g, h);
	}

	private void renderQuad(float colorMultiplier, float red, float green, float blue, List<BakedQuad> list) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		int i = 0;

		for (int j = list.size(); i < j; i++) {
			BakedQuad bakedQuad = (BakedQuad)list.get(i);
			bufferBuilder.begin(7, VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL);
			bufferBuilder.putVertexData(bakedQuad.getVertexData());
			if (bakedQuad.hasColor()) {
				bufferBuilder.setQuadColor(red * colorMultiplier, green * colorMultiplier, blue * colorMultiplier);
			} else {
				bufferBuilder.setQuadColor(colorMultiplier, colorMultiplier, colorMultiplier);
			}

			Vec3i vec3i = bakedQuad.getFace().getVector();
			bufferBuilder.postNormal((float)vec3i.getX(), (float)vec3i.getY(), (float)vec3i.getZ());
			tessellator.draw();
		}
	}

	public static void enableBrightnessCache() {
		((BlockModelRenderer.BrightnessCache)brightnessCache.get()).enable();
	}

	public static void disableBrightnessCache() {
		((BlockModelRenderer.BrightnessCache)brightnessCache.get()).disable();
	}

	@Environment(EnvType.CLIENT)
	class AmbientOcclusionCalculator {
		private final float[] colorMultiplier = new float[4];
		private final int[] brightness = new int[4];

		public AmbientOcclusionCalculator() {
		}

		public void apply(BlockRenderView blockRenderView, BlockState blockState, BlockPos blockPos, Direction direction, float[] fs, BitSet bitSet) {
			BlockPos blockPos2 = bitSet.get(0) ? blockPos.offset(direction) : blockPos;
			BlockModelRenderer.NeighborData neighborData = BlockModelRenderer.NeighborData.getData(direction);
			BlockPos.Mutable mutable = new BlockPos.Mutable();
			BlockModelRenderer.BrightnessCache brightnessCache = (BlockModelRenderer.BrightnessCache)BlockModelRenderer.brightnessCache.get();
			mutable.set(blockPos2).setOffset(neighborData.faces[0]);
			BlockState blockState2 = blockRenderView.getBlockState(mutable);
			int i = brightnessCache.getInt(blockState2, blockRenderView, mutable);
			float f = brightnessCache.getFloat(blockState2, blockRenderView, mutable);
			mutable.set(blockPos2).setOffset(neighborData.faces[1]);
			BlockState blockState3 = blockRenderView.getBlockState(mutable);
			int j = brightnessCache.getInt(blockState3, blockRenderView, mutable);
			float g = brightnessCache.getFloat(blockState3, blockRenderView, mutable);
			mutable.set(blockPos2).setOffset(neighborData.faces[2]);
			BlockState blockState4 = blockRenderView.getBlockState(mutable);
			int k = brightnessCache.getInt(blockState4, blockRenderView, mutable);
			float h = brightnessCache.getFloat(blockState4, blockRenderView, mutable);
			mutable.set(blockPos2).setOffset(neighborData.faces[3]);
			BlockState blockState5 = blockRenderView.getBlockState(mutable);
			int l = brightnessCache.getInt(blockState5, blockRenderView, mutable);
			float m = brightnessCache.getFloat(blockState5, blockRenderView, mutable);
			mutable.set(blockPos2).setOffset(neighborData.faces[0]).setOffset(direction);
			boolean bl = blockRenderView.getBlockState(mutable).getOpacity(blockRenderView, mutable) == 0;
			mutable.set(blockPos2).setOffset(neighborData.faces[1]).setOffset(direction);
			boolean bl2 = blockRenderView.getBlockState(mutable).getOpacity(blockRenderView, mutable) == 0;
			mutable.set(blockPos2).setOffset(neighborData.faces[2]).setOffset(direction);
			boolean bl3 = blockRenderView.getBlockState(mutable).getOpacity(blockRenderView, mutable) == 0;
			mutable.set(blockPos2).setOffset(neighborData.faces[3]).setOffset(direction);
			boolean bl4 = blockRenderView.getBlockState(mutable).getOpacity(blockRenderView, mutable) == 0;
			float n;
			int o;
			if (!bl3 && !bl) {
				n = f;
				o = i;
			} else {
				mutable.set(blockPos2).setOffset(neighborData.faces[0]).setOffset(neighborData.faces[2]);
				BlockState blockState6 = blockRenderView.getBlockState(mutable);
				n = brightnessCache.getFloat(blockState6, blockRenderView, mutable);
				o = brightnessCache.getInt(blockState6, blockRenderView, mutable);
			}

			float p;
			int q;
			if (!bl4 && !bl) {
				p = f;
				q = i;
			} else {
				mutable.set(blockPos2).setOffset(neighborData.faces[0]).setOffset(neighborData.faces[3]);
				BlockState blockState6 = blockRenderView.getBlockState(mutable);
				p = brightnessCache.getFloat(blockState6, blockRenderView, mutable);
				q = brightnessCache.getInt(blockState6, blockRenderView, mutable);
			}

			float r;
			int s;
			if (!bl3 && !bl2) {
				r = f;
				s = i;
			} else {
				mutable.set(blockPos2).setOffset(neighborData.faces[1]).setOffset(neighborData.faces[2]);
				BlockState blockState6 = blockRenderView.getBlockState(mutable);
				r = brightnessCache.getFloat(blockState6, blockRenderView, mutable);
				s = brightnessCache.getInt(blockState6, blockRenderView, mutable);
			}

			float t;
			int u;
			if (!bl4 && !bl2) {
				t = f;
				u = i;
			} else {
				mutable.set(blockPos2).setOffset(neighborData.faces[1]).setOffset(neighborData.faces[3]);
				BlockState blockState6 = blockRenderView.getBlockState(mutable);
				t = brightnessCache.getFloat(blockState6, blockRenderView, mutable);
				u = brightnessCache.getInt(blockState6, blockRenderView, mutable);
			}

			int v = brightnessCache.getInt(blockState, blockRenderView, blockPos);
			mutable.set(blockPos).setOffset(direction);
			BlockState blockState7 = blockRenderView.getBlockState(mutable);
			if (bitSet.get(0) || !blockState7.isFullOpaque(blockRenderView, mutable)) {
				v = brightnessCache.getInt(blockState7, blockRenderView, mutable);
			}

			float w = bitSet.get(0)
				? brightnessCache.getFloat(blockRenderView.getBlockState(blockPos2), blockRenderView, blockPos2)
				: brightnessCache.getFloat(blockRenderView.getBlockState(blockPos), blockRenderView, blockPos);
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
				this.colorMultiplier[translation.firstCorner] = x * ab + y * ac + z * ad + aa * ae;
				this.colorMultiplier[translation.secondCorner] = x * af + y * ag + z * ah + aa * ai;
				this.colorMultiplier[translation.thirdCorner] = x * aj + y * ak + z * al + aa * am;
				this.colorMultiplier[translation.fourthCorner] = x * an + y * ao + z * ap + aa * aq;
				int ar = this.getAmbientOcclusionBrightness(l, i, q, v);
				int as = this.getAmbientOcclusionBrightness(k, i, o, v);
				int at = this.getAmbientOcclusionBrightness(k, j, s, v);
				int au = this.getAmbientOcclusionBrightness(l, j, u, v);
				this.brightness[translation.firstCorner] = this.getBrightness(ar, as, at, au, ab, ac, ad, ae);
				this.brightness[translation.secondCorner] = this.getBrightness(ar, as, at, au, af, ag, ah, ai);
				this.brightness[translation.thirdCorner] = this.getBrightness(ar, as, at, au, aj, ak, al, am);
				this.brightness[translation.fourthCorner] = this.getBrightness(ar, as, at, au, an, ao, ap, aq);
			} else {
				float x = (m + f + p + w) * 0.25F;
				float y = (h + f + n + w) * 0.25F;
				float z = (h + g + r + w) * 0.25F;
				float aa = (m + g + t + w) * 0.25F;
				this.brightness[translation.firstCorner] = this.getAmbientOcclusionBrightness(l, i, q, v);
				this.brightness[translation.secondCorner] = this.getAmbientOcclusionBrightness(k, i, o, v);
				this.brightness[translation.thirdCorner] = this.getAmbientOcclusionBrightness(k, j, s, v);
				this.brightness[translation.fourthCorner] = this.getAmbientOcclusionBrightness(l, j, u, v);
				this.colorMultiplier[translation.firstCorner] = x;
				this.colorMultiplier[translation.secondCorner] = y;
				this.colorMultiplier[translation.thirdCorner] = z;
				this.colorMultiplier[translation.fourthCorner] = aa;
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
				protected void rehash(int i) {
				}
			};
			long2IntLinkedOpenHashMap.defaultReturnValue(Integer.MAX_VALUE);
			return long2IntLinkedOpenHashMap;
		});
		private final Long2FloatLinkedOpenHashMap floatCache = Util.make(() -> {
			Long2FloatLinkedOpenHashMap long2FloatLinkedOpenHashMap = new Long2FloatLinkedOpenHashMap(100, 0.25F) {
				@Override
				protected void rehash(int i) {
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

		public int getInt(BlockState state, BlockRenderView blockView, BlockPos pos) {
			long l = pos.asLong();
			if (this.enabled) {
				int i = this.intCache.get(l);
				if (i != Integer.MAX_VALUE) {
					return i;
				}
			}

			int i = state.getBlockBrightness(blockView, pos);
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
	public static enum NeighborData {
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

		private final Direction[] faces;
		private final boolean nonCubicWeight;
		private final BlockModelRenderer.NeighborOrientation[] field_4192;
		private final BlockModelRenderer.NeighborOrientation[] field_4185;
		private final BlockModelRenderer.NeighborOrientation[] field_4180;
		private final BlockModelRenderer.NeighborOrientation[] field_4188;
		private static final BlockModelRenderer.NeighborData[] field_4190 = Util.make(new BlockModelRenderer.NeighborData[6], neighborDatas -> {
			neighborDatas[Direction.DOWN.getId()] = DOWN;
			neighborDatas[Direction.UP.getId()] = UP;
			neighborDatas[Direction.NORTH.getId()] = NORTH;
			neighborDatas[Direction.SOUTH.getId()] = SOUTH;
			neighborDatas[Direction.WEST.getId()] = WEST;
			neighborDatas[Direction.EAST.getId()] = EAST;
		});

		private NeighborData(
			Direction[] directions,
			float f,
			boolean bl,
			BlockModelRenderer.NeighborOrientation[] neighborOrientations,
			BlockModelRenderer.NeighborOrientation[] neighborOrientations2,
			BlockModelRenderer.NeighborOrientation[] neighborOrientations3,
			BlockModelRenderer.NeighborOrientation[] neighborOrientations4
		) {
			this.faces = directions;
			this.nonCubicWeight = bl;
			this.field_4192 = neighborOrientations;
			this.field_4185 = neighborOrientations2;
			this.field_4180 = neighborOrientations3;
			this.field_4188 = neighborOrientations4;
		}

		public static BlockModelRenderer.NeighborData getData(Direction direction) {
			return field_4190[direction.getId()];
		}
	}

	@Environment(EnvType.CLIENT)
	public static enum NeighborOrientation {
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

		private final int shape;

		private NeighborOrientation(Direction direction, boolean bl) {
			this.shape = direction.getId() + (bl ? Direction.values().length : 0);
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

		private final int firstCorner;
		private final int secondCorner;
		private final int thirdCorner;
		private final int fourthCorner;
		private static final BlockModelRenderer.Translation[] VALUES = Util.make(new BlockModelRenderer.Translation[6], translations -> {
			translations[Direction.DOWN.getId()] = DOWN;
			translations[Direction.UP.getId()] = UP;
			translations[Direction.NORTH.getId()] = NORTH;
			translations[Direction.SOUTH.getId()] = SOUTH;
			translations[Direction.WEST.getId()] = WEST;
			translations[Direction.EAST.getId()] = EAST;
		});

		private Translation(int j, int k, int l, int m) {
			this.firstCorner = j;
			this.secondCorner = k;
			this.thirdCorner = l;
			this.fourthCorner = m;
		}

		public static BlockModelRenderer.Translation getTranslations(Direction direction) {
			return VALUES[direction.getId()];
		}
	}
}
