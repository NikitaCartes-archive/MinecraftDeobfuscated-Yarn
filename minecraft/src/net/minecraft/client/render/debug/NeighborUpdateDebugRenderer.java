package net.minecraft.client.render.debug;

import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BoundingBox;

@Environment(EnvType.CLIENT)
public class NeighborUpdateDebugRenderer implements DebugRenderer.Renderer {
	private final MinecraftClient field_4622;
	private final Map<Long, Map<BlockPos, Integer>> field_4623 = Maps.newTreeMap(Ordering.natural().reverse());

	NeighborUpdateDebugRenderer(MinecraftClient minecraftClient) {
		this.field_4622 = minecraftClient;
	}

	public void method_3870(long l, BlockPos blockPos) {
		Map<BlockPos, Integer> map = (Map<BlockPos, Integer>)this.field_4623.get(l);
		if (map == null) {
			map = Maps.<BlockPos, Integer>newHashMap();
			this.field_4623.put(l, map);
		}

		Integer integer = (Integer)map.get(blockPos);
		if (integer == null) {
			integer = 0;
		}

		map.put(blockPos, integer + 1);
	}

	@Override
	public void render(long l) {
		long m = this.field_4622.world.getTime();
		Camera camera = this.field_4622.gameRenderer.getCamera();
		double d = camera.getPos().x;
		double e = camera.getPos().y;
		double f = camera.getPos().z;
		GlStateManager.enableBlend();
		GlStateManager.blendFuncSeparate(
			GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO
		);
		GlStateManager.lineWidth(2.0F);
		GlStateManager.disableTexture();
		GlStateManager.depthMask(false);
		int i = 200;
		double g = 0.0025;
		Set<BlockPos> set = Sets.<BlockPos>newHashSet();
		Map<BlockPos, Integer> map = Maps.<BlockPos, Integer>newHashMap();
		Iterator<Entry<Long, Map<BlockPos, Integer>>> iterator = this.field_4623.entrySet().iterator();

		while (iterator.hasNext()) {
			Entry<Long, Map<BlockPos, Integer>> entry = (Entry<Long, Map<BlockPos, Integer>>)iterator.next();
			Long long_ = (Long)entry.getKey();
			Map<BlockPos, Integer> map2 = (Map<BlockPos, Integer>)entry.getValue();
			long n = m - long_;
			if (n > 200L) {
				iterator.remove();
			} else {
				for (Entry<BlockPos, Integer> entry2 : map2.entrySet()) {
					BlockPos blockPos = (BlockPos)entry2.getKey();
					Integer integer = (Integer)entry2.getValue();
					if (set.add(blockPos)) {
						WorldRenderer.drawBoxOutline(
							new BoundingBox(BlockPos.ORIGIN)
								.expand(0.002)
								.contract(0.0025 * (double)n)
								.offset((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ())
								.offset(-d, -e, -f),
							1.0F,
							1.0F,
							1.0F,
							1.0F
						);
						map.put(blockPos, integer);
					}
				}
			}
		}

		for (Entry<BlockPos, Integer> entry : map.entrySet()) {
			BlockPos blockPos2 = (BlockPos)entry.getKey();
			Integer integer2 = (Integer)entry.getValue();
			DebugRenderer.method_3711(String.valueOf(integer2), blockPos2.getX(), blockPos2.getY(), blockPos2.getZ(), -1);
		}

		GlStateManager.depthMask(true);
		GlStateManager.enableTexture();
		GlStateManager.disableBlend();
	}
}
