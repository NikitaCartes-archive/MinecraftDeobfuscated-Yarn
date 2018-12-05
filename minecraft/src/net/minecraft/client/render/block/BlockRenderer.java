package net.minecraft.client.render.block;

import com.mojang.blaze3d.platform.GlStateManager;
import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import java.util.BitSet;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexBuffer;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportElement;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.ExtendedBlockView;

@Environment(EnvType.CLIENT)
public class BlockRenderer {
	private final BlockColorMap colorMap;
	private static final ThreadLocal<Object2IntLinkedOpenHashMap<BlockPos>> field_4177 = ThreadLocal.withInitial(() -> {
		Object2IntLinkedOpenHashMap<BlockPos> object2IntLinkedOpenHashMap = new Object2IntLinkedOpenHashMap<BlockPos>(50) {
			@Override
			protected void rehash(int i) {
			}
		};
		object2IntLinkedOpenHashMap.defaultReturnValue(Integer.MAX_VALUE);
		return object2IntLinkedOpenHashMap;
	});
	private static final ThreadLocal<Boolean> field_4179 = ThreadLocal.withInitial(() -> false);

	public BlockRenderer(BlockColorMap blockColorMap) {
		this.colorMap = blockColorMap;
	}

	public boolean tesselate(
		ExtendedBlockView extendedBlockView,
		BakedModel bakedModel,
		BlockState blockState,
		BlockPos blockPos,
		VertexBuffer vertexBuffer,
		boolean bl,
		Random random,
		long l
	) {
		boolean bl2 = MinecraftClient.isAmbientOcclusionEnabled() && blockState.getLuminance() == 0 && bakedModel.useAmbientOcclusion();

		try {
			return bl2
				? this.tesselateSmooth(extendedBlockView, bakedModel, blockState, blockPos, vertexBuffer, bl, random, l)
				: this.tesselateFlat(extendedBlockView, bakedModel, blockState, blockPos, vertexBuffer, bl, random, l);
		} catch (Throwable var14) {
			CrashReport crashReport = CrashReport.create(var14, "Tesselating block model");
			CrashReportElement crashReportElement = crashReport.addElement("Block model being tesselated");
			CrashReportElement.addBlockInfo(crashReportElement, blockPos, blockState);
			crashReportElement.add("Using AO", bl2);
			throw new CrashException(crashReport);
		}
	}

	public boolean tesselateSmooth(
		ExtendedBlockView extendedBlockView,
		BakedModel bakedModel,
		BlockState blockState,
		BlockPos blockPos,
		VertexBuffer vertexBuffer,
		boolean bl,
		Random random,
		long l
	) {
		boolean bl2 = false;
		float[] fs = new float[Direction.values().length * 2];
		BitSet bitSet = new BitSet(3);
		BlockRenderer.AmbientOcclusionCalculator ambientOcclusionCalculator = new BlockRenderer.AmbientOcclusionCalculator();

		for (Direction direction : Direction.values()) {
			random.setSeed(l);
			List<BakedQuad> list = bakedModel.method_4707(blockState, direction, random);
			if (!list.isEmpty() && (!bl || Block.method_9607(blockState, extendedBlockView, blockPos, direction))) {
				this.tesselateQuadsSmooth(extendedBlockView, blockState, blockPos, vertexBuffer, list, fs, bitSet, ambientOcclusionCalculator);
				bl2 = true;
			}
		}

		random.setSeed(l);
		List<BakedQuad> list2 = bakedModel.method_4707(blockState, null, random);
		if (!list2.isEmpty()) {
			this.tesselateQuadsSmooth(extendedBlockView, blockState, blockPos, vertexBuffer, list2, fs, bitSet, ambientOcclusionCalculator);
			bl2 = true;
		}

		return bl2;
	}

	public boolean tesselateFlat(
		ExtendedBlockView extendedBlockView,
		BakedModel bakedModel,
		BlockState blockState,
		BlockPos blockPos,
		VertexBuffer vertexBuffer,
		boolean bl,
		Random random,
		long l
	) {
		boolean bl2 = false;
		BitSet bitSet = new BitSet(3);

		for (Direction direction : Direction.values()) {
			random.setSeed(l);
			List<BakedQuad> list = bakedModel.method_4707(blockState, direction, random);
			if (!list.isEmpty() && (!bl || Block.method_9607(blockState, extendedBlockView, blockPos, direction))) {
				int i = blockState.getBlockBrightness(extendedBlockView, blockPos.method_10093(direction));
				this.tesselateQuadsFlat(extendedBlockView, blockState, blockPos, i, false, vertexBuffer, list, bitSet);
				bl2 = true;
			}
		}

		random.setSeed(l);
		List<BakedQuad> list2 = bakedModel.method_4707(blockState, null, random);
		if (!list2.isEmpty()) {
			this.tesselateQuadsFlat(extendedBlockView, blockState, blockPos, -1, true, vertexBuffer, list2, bitSet);
			bl2 = true;
		}

		return bl2;
	}

	private void tesselateQuadsSmooth(
		ExtendedBlockView extendedBlockView,
		BlockState blockState,
		BlockPos blockPos,
		VertexBuffer vertexBuffer,
		List<BakedQuad> list,
		float[] fs,
		BitSet bitSet,
		BlockRenderer.AmbientOcclusionCalculator ambientOcclusionCalculator
	) {
		Vec3d vec3d = blockState.getOffsetPos(extendedBlockView, blockPos);
		double d = (double)blockPos.getX() + vec3d.x;
		double e = (double)blockPos.getY() + vec3d.y;
		double f = (double)blockPos.getZ() + vec3d.z;
		int i = 0;

		for (int j = list.size(); i < j; i++) {
			BakedQuad bakedQuad = (BakedQuad)list.get(i);
			this.method_3364(extendedBlockView, blockState, blockPos, bakedQuad.getVertexData(), bakedQuad.method_3358(), fs, bitSet);
			ambientOcclusionCalculator.method_3388(extendedBlockView, blockState, blockPos, bakedQuad.method_3358(), fs, bitSet);
			vertexBuffer.putVertexData(bakedQuad.getVertexData());
			vertexBuffer.brightness(
				ambientOcclusionCalculator.brightness[0],
				ambientOcclusionCalculator.brightness[1],
				ambientOcclusionCalculator.brightness[2],
				ambientOcclusionCalculator.brightness[3]
			);
			if (bakedQuad.hasColor()) {
				int k = this.colorMap.getRenderColor(blockState, extendedBlockView, blockPos, bakedQuad.getColorIndex());
				float g = (float)(k >> 16 & 0xFF) / 255.0F;
				float h = (float)(k >> 8 & 0xFF) / 255.0F;
				float l = (float)(k & 0xFF) / 255.0F;
				vertexBuffer.multiplyColor(
					ambientOcclusionCalculator.colorMultiplier[0] * g, ambientOcclusionCalculator.colorMultiplier[0] * h, ambientOcclusionCalculator.colorMultiplier[0] * l, 4
				);
				vertexBuffer.multiplyColor(
					ambientOcclusionCalculator.colorMultiplier[1] * g, ambientOcclusionCalculator.colorMultiplier[1] * h, ambientOcclusionCalculator.colorMultiplier[1] * l, 3
				);
				vertexBuffer.multiplyColor(
					ambientOcclusionCalculator.colorMultiplier[2] * g, ambientOcclusionCalculator.colorMultiplier[2] * h, ambientOcclusionCalculator.colorMultiplier[2] * l, 2
				);
				vertexBuffer.multiplyColor(
					ambientOcclusionCalculator.colorMultiplier[3] * g, ambientOcclusionCalculator.colorMultiplier[3] * h, ambientOcclusionCalculator.colorMultiplier[3] * l, 1
				);
			} else {
				vertexBuffer.multiplyColor(
					ambientOcclusionCalculator.colorMultiplier[0], ambientOcclusionCalculator.colorMultiplier[0], ambientOcclusionCalculator.colorMultiplier[0], 4
				);
				vertexBuffer.multiplyColor(
					ambientOcclusionCalculator.colorMultiplier[1], ambientOcclusionCalculator.colorMultiplier[1], ambientOcclusionCalculator.colorMultiplier[1], 3
				);
				vertexBuffer.multiplyColor(
					ambientOcclusionCalculator.colorMultiplier[2], ambientOcclusionCalculator.colorMultiplier[2], ambientOcclusionCalculator.colorMultiplier[2], 2
				);
				vertexBuffer.multiplyColor(
					ambientOcclusionCalculator.colorMultiplier[3], ambientOcclusionCalculator.colorMultiplier[3], ambientOcclusionCalculator.colorMultiplier[3], 1
				);
			}

			vertexBuffer.postPosition(d, e, f);
		}
	}

	private void method_3364(
		ExtendedBlockView extendedBlockView, BlockState blockState, BlockPos blockPos, int[] is, Direction direction, @Nullable float[] fs, BitSet bitSet
	) {
		float f = 32.0F;
		float g = 32.0F;
		float h = 32.0F;
		float i = -32.0F;
		float j = -32.0F;
		float k = -32.0F;

		for (int l = 0; l < 4; l++) {
			float m = Float.intBitsToFloat(is[l * 7]);
			float n = Float.intBitsToFloat(is[l * 7 + 1]);
			float o = Float.intBitsToFloat(is[l * 7 + 2]);
			f = Math.min(f, m);
			g = Math.min(g, n);
			h = Math.min(h, o);
			i = Math.max(i, m);
			j = Math.max(j, n);
			k = Math.max(k, o);
		}

		if (fs != null) {
			fs[Direction.WEST.getId()] = f;
			fs[Direction.EAST.getId()] = i;
			fs[Direction.DOWN.getId()] = g;
			fs[Direction.UP.getId()] = j;
			fs[Direction.NORTH.getId()] = h;
			fs[Direction.SOUTH.getId()] = k;
			int l = Direction.values().length;
			fs[Direction.WEST.getId() + l] = 1.0F - f;
			fs[Direction.EAST.getId() + l] = 1.0F - i;
			fs[Direction.DOWN.getId() + l] = 1.0F - g;
			fs[Direction.UP.getId() + l] = 1.0F - j;
			fs[Direction.NORTH.getId() + l] = 1.0F - h;
			fs[Direction.SOUTH.getId() + l] = 1.0F - k;
		}

		float p = 1.0E-4F;
		float m = 0.9999F;
		switch (direction) {
			case DOWN:
				bitSet.set(1, f >= 1.0E-4F || h >= 1.0E-4F || i <= 0.9999F || k <= 0.9999F);
				bitSet.set(0, (g < 1.0E-4F || blockState.method_11604(extendedBlockView, blockPos)) && g == j);
				break;
			case UP:
				bitSet.set(1, f >= 1.0E-4F || h >= 1.0E-4F || i <= 0.9999F || k <= 0.9999F);
				bitSet.set(0, (j > 0.9999F || blockState.method_11604(extendedBlockView, blockPos)) && g == j);
				break;
			case NORTH:
				bitSet.set(1, f >= 1.0E-4F || g >= 1.0E-4F || i <= 0.9999F || j <= 0.9999F);
				bitSet.set(0, (h < 1.0E-4F || blockState.method_11604(extendedBlockView, blockPos)) && h == k);
				break;
			case SOUTH:
				bitSet.set(1, f >= 1.0E-4F || g >= 1.0E-4F || i <= 0.9999F || j <= 0.9999F);
				bitSet.set(0, (k > 0.9999F || blockState.method_11604(extendedBlockView, blockPos)) && h == k);
				break;
			case WEST:
				bitSet.set(1, g >= 1.0E-4F || h >= 1.0E-4F || j <= 0.9999F || k <= 0.9999F);
				bitSet.set(0, (f < 1.0E-4F || blockState.method_11604(extendedBlockView, blockPos)) && f == i);
				break;
			case EAST:
				bitSet.set(1, g >= 1.0E-4F || h >= 1.0E-4F || j <= 0.9999F || k <= 0.9999F);
				bitSet.set(0, (i > 0.9999F || blockState.method_11604(extendedBlockView, blockPos)) && f == i);
		}
	}

	private void tesselateQuadsFlat(
		ExtendedBlockView extendedBlockView,
		BlockState blockState,
		BlockPos blockPos,
		int i,
		boolean bl,
		VertexBuffer vertexBuffer,
		List<BakedQuad> list,
		BitSet bitSet
	) {
		Vec3d vec3d = blockState.getOffsetPos(extendedBlockView, blockPos);
		double d = (double)blockPos.getX() + vec3d.x;
		double e = (double)blockPos.getY() + vec3d.y;
		double f = (double)blockPos.getZ() + vec3d.z;
		int j = 0;

		for (int k = list.size(); j < k; j++) {
			BakedQuad bakedQuad = (BakedQuad)list.get(j);
			if (bl) {
				this.method_3364(extendedBlockView, blockState, blockPos, bakedQuad.getVertexData(), bakedQuad.method_3358(), null, bitSet);
				BlockPos blockPos2 = bitSet.get(0) ? blockPos.method_10093(bakedQuad.method_3358()) : blockPos;
				i = blockState.getBlockBrightness(extendedBlockView, blockPos2);
			}

			vertexBuffer.putVertexData(bakedQuad.getVertexData());
			vertexBuffer.brightness(i, i, i, i);
			if (bakedQuad.hasColor()) {
				int l = this.colorMap.getRenderColor(blockState, extendedBlockView, blockPos, bakedQuad.getColorIndex());
				float g = (float)(l >> 16 & 0xFF) / 255.0F;
				float h = (float)(l >> 8 & 0xFF) / 255.0F;
				float m = (float)(l & 0xFF) / 255.0F;
				vertexBuffer.multiplyColor(g, h, m, 4);
				vertexBuffer.multiplyColor(g, h, m, 3);
				vertexBuffer.multiplyColor(g, h, m, 2);
				vertexBuffer.multiplyColor(g, h, m, 1);
			}

			vertexBuffer.postPosition(d, e, f);
		}
	}

	public void render(BakedModel bakedModel, float f, float g, float h, float i) {
		this.render(null, bakedModel, f, g, h, i);
	}

	public void render(@Nullable BlockState blockState, BakedModel bakedModel, float f, float g, float h, float i) {
		Random random = new Random();
		long l = 42L;

		for (Direction direction : Direction.values()) {
			random.setSeed(42L);
			this.renderQuad(f, g, h, i, bakedModel.method_4707(blockState, direction, random));
		}

		random.setSeed(42L);
		this.renderQuad(f, g, h, i, bakedModel.method_4707(blockState, null, random));
	}

	public void render(BakedModel bakedModel, BlockState blockState, float f, boolean bl) {
		GlStateManager.rotatef(90.0F, 0.0F, 1.0F, 0.0F);
		int i = this.colorMap.getRenderColor(blockState, null, null, 0);
		float g = (float)(i >> 16 & 0xFF) / 255.0F;
		float h = (float)(i >> 8 & 0xFF) / 255.0F;
		float j = (float)(i & 0xFF) / 255.0F;
		if (!bl) {
			GlStateManager.color4f(f, f, f, 1.0F);
		}

		this.render(blockState, bakedModel, f, g, h, j);
	}

	private void renderQuad(float f, float g, float h, float i, List<BakedQuad> list) {
		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer vertexBuffer = tessellator.getVertexBuffer();
		int j = 0;

		for (int k = list.size(); j < k; j++) {
			BakedQuad bakedQuad = (BakedQuad)list.get(j);
			vertexBuffer.begin(7, VertexFormats.field_1590);
			vertexBuffer.putVertexData(bakedQuad.getVertexData());
			if (bakedQuad.hasColor()) {
				vertexBuffer.setQuadColor(g * f, h * f, i * f);
			} else {
				vertexBuffer.setQuadColor(f, f, f);
			}

			Vec3i vec3i = bakedQuad.method_3358().getVector();
			vertexBuffer.postNormal((float)vec3i.getX(), (float)vec3i.getY(), (float)vec3i.getZ());
			tessellator.draw();
		}
	}

	public static void method_3375() {
		field_4179.set(true);
	}

	public static void method_3376() {
		((Object2IntLinkedOpenHashMap)field_4177.get()).clear();
		field_4179.set(false);
	}

	private static int method_3371(BlockState blockState, ExtendedBlockView extendedBlockView, BlockPos blockPos) {
		Boolean boolean_ = (Boolean)field_4179.get();
		Object2IntLinkedOpenHashMap<BlockPos> object2IntLinkedOpenHashMap = null;
		if (boolean_) {
			object2IntLinkedOpenHashMap = (Object2IntLinkedOpenHashMap<BlockPos>)field_4177.get();
			int i = object2IntLinkedOpenHashMap.getInt(blockPos);
			if (i != Integer.MAX_VALUE) {
				return i;
			}
		}

		int i = blockState.getBlockBrightness(extendedBlockView, blockPos);
		if (object2IntLinkedOpenHashMap != null) {
			if (object2IntLinkedOpenHashMap.size() == 50) {
				object2IntLinkedOpenHashMap.removeFirstInt();
			}

			object2IntLinkedOpenHashMap.put(blockPos.toImmutable(), i);
		}

		return i;
	}

	@Environment(EnvType.CLIENT)
	class AmbientOcclusionCalculator {
		private final float[] colorMultiplier = new float[4];
		private final int[] brightness = new int[4];

		public AmbientOcclusionCalculator() {
		}

		public void method_3388(ExtendedBlockView extendedBlockView, BlockState blockState, BlockPos blockPos, Direction direction, float[] fs, BitSet bitSet) {
			BlockPos blockPos2 = bitSet.get(0) ? blockPos.method_10093(direction) : blockPos;
			BlockRenderer.NeighborData neighborData = BlockRenderer.NeighborData.method_3378(direction);
			BlockPos.Mutable mutable = new BlockPos.Mutable();
			mutable.set(blockPos2).method_10098(neighborData.field_4191[0]);
			int i = BlockRenderer.method_3371(blockState, extendedBlockView, mutable);
			float f = extendedBlockView.getBlockState(mutable).getAmbientOcclusionLightLevel(extendedBlockView, mutable);
			mutable.set(blockPos2).method_10098(neighborData.field_4191[1]);
			int j = BlockRenderer.method_3371(blockState, extendedBlockView, mutable);
			float g = extendedBlockView.getBlockState(mutable).getAmbientOcclusionLightLevel(extendedBlockView, mutable);
			mutable.set(blockPos2).method_10098(neighborData.field_4191[2]);
			int k = BlockRenderer.method_3371(blockState, extendedBlockView, mutable);
			float h = extendedBlockView.getBlockState(mutable).getAmbientOcclusionLightLevel(extendedBlockView, mutable);
			mutable.set(blockPos2).method_10098(neighborData.field_4191[3]);
			int l = BlockRenderer.method_3371(blockState, extendedBlockView, mutable);
			float m = extendedBlockView.getBlockState(mutable).getAmbientOcclusionLightLevel(extendedBlockView, mutable);
			mutable.set(blockPos2).method_10098(neighborData.field_4191[0]).method_10098(direction);
			boolean bl = extendedBlockView.getBlockState(mutable).method_11581(extendedBlockView, mutable) == 0;
			mutable.set(blockPos2).method_10098(neighborData.field_4191[1]).method_10098(direction);
			boolean bl2 = extendedBlockView.getBlockState(mutable).method_11581(extendedBlockView, mutable) == 0;
			mutable.set(blockPos2).method_10098(neighborData.field_4191[2]).method_10098(direction);
			boolean bl3 = extendedBlockView.getBlockState(mutable).method_11581(extendedBlockView, mutable) == 0;
			mutable.set(blockPos2).method_10098(neighborData.field_4191[3]).method_10098(direction);
			boolean bl4 = extendedBlockView.getBlockState(mutable).method_11581(extendedBlockView, mutable) == 0;
			float n;
			int o;
			if (!bl3 && !bl) {
				n = f;
				o = i;
			} else {
				mutable.set(blockPos2).method_10098(neighborData.field_4191[0]).method_10098(neighborData.field_4191[2]);
				n = extendedBlockView.getBlockState(mutable).getAmbientOcclusionLightLevel(extendedBlockView, mutable);
				o = BlockRenderer.method_3371(blockState, extendedBlockView, mutable);
			}

			float p;
			int q;
			if (!bl4 && !bl) {
				p = f;
				q = i;
			} else {
				mutable.set(blockPos2).method_10098(neighborData.field_4191[0]).method_10098(neighborData.field_4191[3]);
				p = extendedBlockView.getBlockState(mutable).getAmbientOcclusionLightLevel(extendedBlockView, mutable);
				q = BlockRenderer.method_3371(blockState, extendedBlockView, mutable);
			}

			float r;
			int s;
			if (!bl3 && !bl2) {
				r = g;
				s = j;
			} else {
				mutable.set(blockPos2).method_10098(neighborData.field_4191[1]).method_10098(neighborData.field_4191[2]);
				r = extendedBlockView.getBlockState(mutable).getAmbientOcclusionLightLevel(extendedBlockView, mutable);
				s = BlockRenderer.method_3371(blockState, extendedBlockView, mutable);
			}

			float t;
			int u;
			if (!bl4 && !bl2) {
				t = g;
				u = j;
			} else {
				mutable.set(blockPos2).method_10098(neighborData.field_4191[1]).method_10098(neighborData.field_4191[3]);
				t = extendedBlockView.getBlockState(mutable).getAmbientOcclusionLightLevel(extendedBlockView, mutable);
				u = BlockRenderer.method_3371(blockState, extendedBlockView, mutable);
			}

			int v = BlockRenderer.method_3371(blockState, extendedBlockView, blockPos);
			mutable.set(blockPos).method_10098(direction);
			if (bitSet.get(0) || !extendedBlockView.getBlockState(mutable).method_11598(extendedBlockView, mutable)) {
				v = BlockRenderer.method_3371(blockState, extendedBlockView, mutable);
			}

			float w = bitSet.get(0)
				? extendedBlockView.getBlockState(blockPos2).getAmbientOcclusionLightLevel(extendedBlockView, blockPos2)
				: extendedBlockView.getBlockState(blockPos).getAmbientOcclusionLightLevel(extendedBlockView, blockPos);
			BlockRenderer.Translation translation = BlockRenderer.Translation.method_3394(direction);
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
	public static enum NeighborData {
		DOWN(
			new Direction[]{Direction.WEST, Direction.EAST, Direction.NORTH, Direction.SOUTH},
			0.5F,
			true,
			new BlockRenderer.NeighborOrientation[]{
				BlockRenderer.NeighborOrientation.FLIP_WEST,
				BlockRenderer.NeighborOrientation.SOUTH,
				BlockRenderer.NeighborOrientation.FLIP_WEST,
				BlockRenderer.NeighborOrientation.FLIP_SOUTH,
				BlockRenderer.NeighborOrientation.WEST,
				BlockRenderer.NeighborOrientation.FLIP_SOUTH,
				BlockRenderer.NeighborOrientation.WEST,
				BlockRenderer.NeighborOrientation.SOUTH
			},
			new BlockRenderer.NeighborOrientation[]{
				BlockRenderer.NeighborOrientation.FLIP_WEST,
				BlockRenderer.NeighborOrientation.NORTH,
				BlockRenderer.NeighborOrientation.FLIP_WEST,
				BlockRenderer.NeighborOrientation.FLIP_NORTH,
				BlockRenderer.NeighborOrientation.WEST,
				BlockRenderer.NeighborOrientation.FLIP_NORTH,
				BlockRenderer.NeighborOrientation.WEST,
				BlockRenderer.NeighborOrientation.NORTH
			},
			new BlockRenderer.NeighborOrientation[]{
				BlockRenderer.NeighborOrientation.FLIP_EAST,
				BlockRenderer.NeighborOrientation.NORTH,
				BlockRenderer.NeighborOrientation.FLIP_EAST,
				BlockRenderer.NeighborOrientation.FLIP_NORTH,
				BlockRenderer.NeighborOrientation.EAST,
				BlockRenderer.NeighborOrientation.FLIP_NORTH,
				BlockRenderer.NeighborOrientation.EAST,
				BlockRenderer.NeighborOrientation.NORTH
			},
			new BlockRenderer.NeighborOrientation[]{
				BlockRenderer.NeighborOrientation.FLIP_EAST,
				BlockRenderer.NeighborOrientation.SOUTH,
				BlockRenderer.NeighborOrientation.FLIP_EAST,
				BlockRenderer.NeighborOrientation.FLIP_SOUTH,
				BlockRenderer.NeighborOrientation.EAST,
				BlockRenderer.NeighborOrientation.FLIP_SOUTH,
				BlockRenderer.NeighborOrientation.EAST,
				BlockRenderer.NeighborOrientation.SOUTH
			}
		),
		UP(
			new Direction[]{Direction.EAST, Direction.WEST, Direction.NORTH, Direction.SOUTH},
			1.0F,
			true,
			new BlockRenderer.NeighborOrientation[]{
				BlockRenderer.NeighborOrientation.EAST,
				BlockRenderer.NeighborOrientation.SOUTH,
				BlockRenderer.NeighborOrientation.EAST,
				BlockRenderer.NeighborOrientation.FLIP_SOUTH,
				BlockRenderer.NeighborOrientation.FLIP_EAST,
				BlockRenderer.NeighborOrientation.FLIP_SOUTH,
				BlockRenderer.NeighborOrientation.FLIP_EAST,
				BlockRenderer.NeighborOrientation.SOUTH
			},
			new BlockRenderer.NeighborOrientation[]{
				BlockRenderer.NeighborOrientation.EAST,
				BlockRenderer.NeighborOrientation.NORTH,
				BlockRenderer.NeighborOrientation.EAST,
				BlockRenderer.NeighborOrientation.FLIP_NORTH,
				BlockRenderer.NeighborOrientation.FLIP_EAST,
				BlockRenderer.NeighborOrientation.FLIP_NORTH,
				BlockRenderer.NeighborOrientation.FLIP_EAST,
				BlockRenderer.NeighborOrientation.NORTH
			},
			new BlockRenderer.NeighborOrientation[]{
				BlockRenderer.NeighborOrientation.WEST,
				BlockRenderer.NeighborOrientation.NORTH,
				BlockRenderer.NeighborOrientation.WEST,
				BlockRenderer.NeighborOrientation.FLIP_NORTH,
				BlockRenderer.NeighborOrientation.FLIP_WEST,
				BlockRenderer.NeighborOrientation.FLIP_NORTH,
				BlockRenderer.NeighborOrientation.FLIP_WEST,
				BlockRenderer.NeighborOrientation.NORTH
			},
			new BlockRenderer.NeighborOrientation[]{
				BlockRenderer.NeighborOrientation.WEST,
				BlockRenderer.NeighborOrientation.SOUTH,
				BlockRenderer.NeighborOrientation.WEST,
				BlockRenderer.NeighborOrientation.FLIP_SOUTH,
				BlockRenderer.NeighborOrientation.FLIP_WEST,
				BlockRenderer.NeighborOrientation.FLIP_SOUTH,
				BlockRenderer.NeighborOrientation.FLIP_WEST,
				BlockRenderer.NeighborOrientation.SOUTH
			}
		),
		NORTH(
			new Direction[]{Direction.UP, Direction.DOWN, Direction.EAST, Direction.WEST},
			0.8F,
			true,
			new BlockRenderer.NeighborOrientation[]{
				BlockRenderer.NeighborOrientation.UP,
				BlockRenderer.NeighborOrientation.FLIP_WEST,
				BlockRenderer.NeighborOrientation.UP,
				BlockRenderer.NeighborOrientation.WEST,
				BlockRenderer.NeighborOrientation.FLIP_UP,
				BlockRenderer.NeighborOrientation.WEST,
				BlockRenderer.NeighborOrientation.FLIP_UP,
				BlockRenderer.NeighborOrientation.FLIP_WEST
			},
			new BlockRenderer.NeighborOrientation[]{
				BlockRenderer.NeighborOrientation.UP,
				BlockRenderer.NeighborOrientation.FLIP_EAST,
				BlockRenderer.NeighborOrientation.UP,
				BlockRenderer.NeighborOrientation.EAST,
				BlockRenderer.NeighborOrientation.FLIP_UP,
				BlockRenderer.NeighborOrientation.EAST,
				BlockRenderer.NeighborOrientation.FLIP_UP,
				BlockRenderer.NeighborOrientation.FLIP_EAST
			},
			new BlockRenderer.NeighborOrientation[]{
				BlockRenderer.NeighborOrientation.DOWN,
				BlockRenderer.NeighborOrientation.FLIP_EAST,
				BlockRenderer.NeighborOrientation.DOWN,
				BlockRenderer.NeighborOrientation.EAST,
				BlockRenderer.NeighborOrientation.FLIP_DOWN,
				BlockRenderer.NeighborOrientation.EAST,
				BlockRenderer.NeighborOrientation.FLIP_DOWN,
				BlockRenderer.NeighborOrientation.FLIP_EAST
			},
			new BlockRenderer.NeighborOrientation[]{
				BlockRenderer.NeighborOrientation.DOWN,
				BlockRenderer.NeighborOrientation.FLIP_WEST,
				BlockRenderer.NeighborOrientation.DOWN,
				BlockRenderer.NeighborOrientation.WEST,
				BlockRenderer.NeighborOrientation.FLIP_DOWN,
				BlockRenderer.NeighborOrientation.WEST,
				BlockRenderer.NeighborOrientation.FLIP_DOWN,
				BlockRenderer.NeighborOrientation.FLIP_WEST
			}
		),
		SOUTH(
			new Direction[]{Direction.WEST, Direction.EAST, Direction.DOWN, Direction.UP},
			0.8F,
			true,
			new BlockRenderer.NeighborOrientation[]{
				BlockRenderer.NeighborOrientation.UP,
				BlockRenderer.NeighborOrientation.FLIP_WEST,
				BlockRenderer.NeighborOrientation.FLIP_UP,
				BlockRenderer.NeighborOrientation.FLIP_WEST,
				BlockRenderer.NeighborOrientation.FLIP_UP,
				BlockRenderer.NeighborOrientation.WEST,
				BlockRenderer.NeighborOrientation.UP,
				BlockRenderer.NeighborOrientation.WEST
			},
			new BlockRenderer.NeighborOrientation[]{
				BlockRenderer.NeighborOrientation.DOWN,
				BlockRenderer.NeighborOrientation.FLIP_WEST,
				BlockRenderer.NeighborOrientation.FLIP_DOWN,
				BlockRenderer.NeighborOrientation.FLIP_WEST,
				BlockRenderer.NeighborOrientation.FLIP_DOWN,
				BlockRenderer.NeighborOrientation.WEST,
				BlockRenderer.NeighborOrientation.DOWN,
				BlockRenderer.NeighborOrientation.WEST
			},
			new BlockRenderer.NeighborOrientation[]{
				BlockRenderer.NeighborOrientation.DOWN,
				BlockRenderer.NeighborOrientation.FLIP_EAST,
				BlockRenderer.NeighborOrientation.FLIP_DOWN,
				BlockRenderer.NeighborOrientation.FLIP_EAST,
				BlockRenderer.NeighborOrientation.FLIP_DOWN,
				BlockRenderer.NeighborOrientation.EAST,
				BlockRenderer.NeighborOrientation.DOWN,
				BlockRenderer.NeighborOrientation.EAST
			},
			new BlockRenderer.NeighborOrientation[]{
				BlockRenderer.NeighborOrientation.UP,
				BlockRenderer.NeighborOrientation.FLIP_EAST,
				BlockRenderer.NeighborOrientation.FLIP_UP,
				BlockRenderer.NeighborOrientation.FLIP_EAST,
				BlockRenderer.NeighborOrientation.FLIP_UP,
				BlockRenderer.NeighborOrientation.EAST,
				BlockRenderer.NeighborOrientation.UP,
				BlockRenderer.NeighborOrientation.EAST
			}
		),
		WEST(
			new Direction[]{Direction.UP, Direction.DOWN, Direction.NORTH, Direction.SOUTH},
			0.6F,
			true,
			new BlockRenderer.NeighborOrientation[]{
				BlockRenderer.NeighborOrientation.UP,
				BlockRenderer.NeighborOrientation.SOUTH,
				BlockRenderer.NeighborOrientation.UP,
				BlockRenderer.NeighborOrientation.FLIP_SOUTH,
				BlockRenderer.NeighborOrientation.FLIP_UP,
				BlockRenderer.NeighborOrientation.FLIP_SOUTH,
				BlockRenderer.NeighborOrientation.FLIP_UP,
				BlockRenderer.NeighborOrientation.SOUTH
			},
			new BlockRenderer.NeighborOrientation[]{
				BlockRenderer.NeighborOrientation.UP,
				BlockRenderer.NeighborOrientation.NORTH,
				BlockRenderer.NeighborOrientation.UP,
				BlockRenderer.NeighborOrientation.FLIP_NORTH,
				BlockRenderer.NeighborOrientation.FLIP_UP,
				BlockRenderer.NeighborOrientation.FLIP_NORTH,
				BlockRenderer.NeighborOrientation.FLIP_UP,
				BlockRenderer.NeighborOrientation.NORTH
			},
			new BlockRenderer.NeighborOrientation[]{
				BlockRenderer.NeighborOrientation.DOWN,
				BlockRenderer.NeighborOrientation.NORTH,
				BlockRenderer.NeighborOrientation.DOWN,
				BlockRenderer.NeighborOrientation.FLIP_NORTH,
				BlockRenderer.NeighborOrientation.FLIP_DOWN,
				BlockRenderer.NeighborOrientation.FLIP_NORTH,
				BlockRenderer.NeighborOrientation.FLIP_DOWN,
				BlockRenderer.NeighborOrientation.NORTH
			},
			new BlockRenderer.NeighborOrientation[]{
				BlockRenderer.NeighborOrientation.DOWN,
				BlockRenderer.NeighborOrientation.SOUTH,
				BlockRenderer.NeighborOrientation.DOWN,
				BlockRenderer.NeighborOrientation.FLIP_SOUTH,
				BlockRenderer.NeighborOrientation.FLIP_DOWN,
				BlockRenderer.NeighborOrientation.FLIP_SOUTH,
				BlockRenderer.NeighborOrientation.FLIP_DOWN,
				BlockRenderer.NeighborOrientation.SOUTH
			}
		),
		EAST(
			new Direction[]{Direction.DOWN, Direction.UP, Direction.NORTH, Direction.SOUTH},
			0.6F,
			true,
			new BlockRenderer.NeighborOrientation[]{
				BlockRenderer.NeighborOrientation.FLIP_DOWN,
				BlockRenderer.NeighborOrientation.SOUTH,
				BlockRenderer.NeighborOrientation.FLIP_DOWN,
				BlockRenderer.NeighborOrientation.FLIP_SOUTH,
				BlockRenderer.NeighborOrientation.DOWN,
				BlockRenderer.NeighborOrientation.FLIP_SOUTH,
				BlockRenderer.NeighborOrientation.DOWN,
				BlockRenderer.NeighborOrientation.SOUTH
			},
			new BlockRenderer.NeighborOrientation[]{
				BlockRenderer.NeighborOrientation.FLIP_DOWN,
				BlockRenderer.NeighborOrientation.NORTH,
				BlockRenderer.NeighborOrientation.FLIP_DOWN,
				BlockRenderer.NeighborOrientation.FLIP_NORTH,
				BlockRenderer.NeighborOrientation.DOWN,
				BlockRenderer.NeighborOrientation.FLIP_NORTH,
				BlockRenderer.NeighborOrientation.DOWN,
				BlockRenderer.NeighborOrientation.NORTH
			},
			new BlockRenderer.NeighborOrientation[]{
				BlockRenderer.NeighborOrientation.FLIP_UP,
				BlockRenderer.NeighborOrientation.NORTH,
				BlockRenderer.NeighborOrientation.FLIP_UP,
				BlockRenderer.NeighborOrientation.FLIP_NORTH,
				BlockRenderer.NeighborOrientation.UP,
				BlockRenderer.NeighborOrientation.FLIP_NORTH,
				BlockRenderer.NeighborOrientation.UP,
				BlockRenderer.NeighborOrientation.NORTH
			},
			new BlockRenderer.NeighborOrientation[]{
				BlockRenderer.NeighborOrientation.FLIP_UP,
				BlockRenderer.NeighborOrientation.SOUTH,
				BlockRenderer.NeighborOrientation.FLIP_UP,
				BlockRenderer.NeighborOrientation.FLIP_SOUTH,
				BlockRenderer.NeighborOrientation.UP,
				BlockRenderer.NeighborOrientation.FLIP_SOUTH,
				BlockRenderer.NeighborOrientation.UP,
				BlockRenderer.NeighborOrientation.SOUTH
			}
		);

		private final Direction[] field_4191;
		private final boolean nonCubicWeight;
		private final BlockRenderer.NeighborOrientation[] field_4192;
		private final BlockRenderer.NeighborOrientation[] field_4185;
		private final BlockRenderer.NeighborOrientation[] field_4180;
		private final BlockRenderer.NeighborOrientation[] field_4188;
		private static final BlockRenderer.NeighborData[] field_4190 = SystemUtil.consume(new BlockRenderer.NeighborData[6], neighborDatas -> {
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
			BlockRenderer.NeighborOrientation[] neighborOrientations,
			BlockRenderer.NeighborOrientation[] neighborOrientations2,
			BlockRenderer.NeighborOrientation[] neighborOrientations3,
			BlockRenderer.NeighborOrientation[] neighborOrientations4
		) {
			this.field_4191 = directions;
			this.nonCubicWeight = bl;
			this.field_4192 = neighborOrientations;
			this.field_4185 = neighborOrientations2;
			this.field_4180 = neighborOrientations3;
			this.field_4188 = neighborOrientations4;
		}

		public static BlockRenderer.NeighborData method_3378(Direction direction) {
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
		private static final BlockRenderer.Translation[] VALUES = SystemUtil.consume(new BlockRenderer.Translation[6], translations -> {
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

		public static BlockRenderer.Translation method_3394(Direction direction) {
			return VALUES[direction.getId()];
		}
	}
}
