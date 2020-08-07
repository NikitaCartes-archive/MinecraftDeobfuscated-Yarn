package net.minecraft.client.render;

import com.google.common.collect.Maps;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeavesBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Util;

@Environment(EnvType.CLIENT)
public class RenderLayers {
	private static final Map<Block, RenderLayer> BLOCKS = Util.make(Maps.<Block, RenderLayer>newHashMap(), hashMap -> {
		RenderLayer renderLayer = RenderLayer.getTripwire();
		hashMap.put(Blocks.field_10589, renderLayer);
		RenderLayer renderLayer2 = RenderLayer.getCutoutMipped();
		hashMap.put(Blocks.field_10219, renderLayer2);
		hashMap.put(Blocks.field_10576, renderLayer2);
		hashMap.put(Blocks.field_10285, renderLayer2);
		hashMap.put(Blocks.field_10348, renderLayer2);
		hashMap.put(Blocks.field_10312, renderLayer2);
		hashMap.put(Blocks.field_23985, renderLayer2);
		hashMap.put(Blocks.field_10335, renderLayer2);
		hashMap.put(Blocks.field_10503, renderLayer2);
		hashMap.put(Blocks.field_9988, renderLayer2);
		hashMap.put(Blocks.field_10098, renderLayer2);
		hashMap.put(Blocks.field_10539, renderLayer2);
		hashMap.put(Blocks.field_10035, renderLayer2);
		RenderLayer renderLayer3 = RenderLayer.getCutout();
		hashMap.put(Blocks.field_10394, renderLayer3);
		hashMap.put(Blocks.field_10217, renderLayer3);
		hashMap.put(Blocks.field_10575, renderLayer3);
		hashMap.put(Blocks.field_10276, renderLayer3);
		hashMap.put(Blocks.field_10385, renderLayer3);
		hashMap.put(Blocks.field_10160, renderLayer3);
		hashMap.put(Blocks.field_10033, renderLayer3);
		hashMap.put(Blocks.field_10120, renderLayer3);
		hashMap.put(Blocks.field_10410, renderLayer3);
		hashMap.put(Blocks.field_10230, renderLayer3);
		hashMap.put(Blocks.field_10621, renderLayer3);
		hashMap.put(Blocks.field_10356, renderLayer3);
		hashMap.put(Blocks.field_10180, renderLayer3);
		hashMap.put(Blocks.field_10610, renderLayer3);
		hashMap.put(Blocks.field_10141, renderLayer3);
		hashMap.put(Blocks.field_10326, renderLayer3);
		hashMap.put(Blocks.field_10109, renderLayer3);
		hashMap.put(Blocks.field_10019, renderLayer3);
		hashMap.put(Blocks.field_10527, renderLayer3);
		hashMap.put(Blocks.field_10288, renderLayer3);
		hashMap.put(Blocks.field_10561, renderLayer3);
		hashMap.put(Blocks.field_10069, renderLayer3);
		hashMap.put(Blocks.field_10461, renderLayer3);
		hashMap.put(Blocks.field_10425, renderLayer3);
		hashMap.put(Blocks.field_10025, renderLayer3);
		hashMap.put(Blocks.field_10343, renderLayer3);
		hashMap.put(Blocks.field_10479, renderLayer3);
		hashMap.put(Blocks.field_10112, renderLayer3);
		hashMap.put(Blocks.field_10428, renderLayer3);
		hashMap.put(Blocks.field_10376, renderLayer3);
		hashMap.put(Blocks.field_10238, renderLayer3);
		hashMap.put(Blocks.field_10182, renderLayer3);
		hashMap.put(Blocks.field_10449, renderLayer3);
		hashMap.put(Blocks.field_10086, renderLayer3);
		hashMap.put(Blocks.field_10226, renderLayer3);
		hashMap.put(Blocks.field_10573, renderLayer3);
		hashMap.put(Blocks.field_10270, renderLayer3);
		hashMap.put(Blocks.field_10048, renderLayer3);
		hashMap.put(Blocks.field_10156, renderLayer3);
		hashMap.put(Blocks.field_10315, renderLayer3);
		hashMap.put(Blocks.field_10554, renderLayer3);
		hashMap.put(Blocks.field_9995, renderLayer3);
		hashMap.put(Blocks.field_10606, renderLayer3);
		hashMap.put(Blocks.field_10548, renderLayer3);
		hashMap.put(Blocks.field_10251, renderLayer3);
		hashMap.put(Blocks.field_10559, renderLayer3);
		hashMap.put(Blocks.field_10336, renderLayer3);
		hashMap.put(Blocks.field_10099, renderLayer3);
		hashMap.put(Blocks.field_22092, renderLayer3);
		hashMap.put(Blocks.field_22093, renderLayer3);
		hashMap.put(Blocks.field_10036, renderLayer3);
		hashMap.put(Blocks.field_22089, renderLayer3);
		hashMap.put(Blocks.field_10260, renderLayer3);
		hashMap.put(Blocks.field_10091, renderLayer3);
		hashMap.put(Blocks.field_10293, renderLayer3);
		hashMap.put(Blocks.field_10149, renderLayer3);
		hashMap.put(Blocks.field_9983, renderLayer3);
		hashMap.put(Blocks.field_10167, renderLayer3);
		hashMap.put(Blocks.field_9973, renderLayer3);
		hashMap.put(Blocks.field_10523, renderLayer3);
		hashMap.put(Blocks.field_10301, renderLayer3);
		hashMap.put(Blocks.field_10029, renderLayer3);
		hashMap.put(Blocks.field_10424, renderLayer3);
		hashMap.put(Blocks.field_10450, renderLayer3);
		hashMap.put(Blocks.field_10137, renderLayer3);
		hashMap.put(Blocks.field_10323, renderLayer3);
		hashMap.put(Blocks.field_10486, renderLayer3);
		hashMap.put(Blocks.field_10017, renderLayer3);
		hashMap.put(Blocks.field_10608, renderLayer3);
		hashMap.put(Blocks.field_10246, renderLayer3);
		hashMap.put(Blocks.field_22094, renderLayer3);
		hashMap.put(Blocks.field_22095, renderLayer3);
		hashMap.put(Blocks.field_10331, renderLayer3);
		hashMap.put(Blocks.field_10150, renderLayer3);
		hashMap.put(Blocks.field_9984, renderLayer3);
		hashMap.put(Blocks.field_10168, renderLayer3);
		hashMap.put(Blocks.field_10597, renderLayer3);
		hashMap.put(Blocks.field_10588, renderLayer3);
		hashMap.put(Blocks.field_9974, renderLayer3);
		hashMap.put(Blocks.field_10333, renderLayer3);
		hashMap.put(Blocks.field_10302, renderLayer3);
		hashMap.put(Blocks.field_10327, renderLayer3);
		hashMap.put(Blocks.field_10495, renderLayer3);
		hashMap.put(Blocks.field_10468, renderLayer3);
		hashMap.put(Blocks.field_10192, renderLayer3);
		hashMap.put(Blocks.field_10577, renderLayer3);
		hashMap.put(Blocks.field_10304, renderLayer3);
		hashMap.put(Blocks.field_10564, renderLayer3);
		hashMap.put(Blocks.field_10076, renderLayer3);
		hashMap.put(Blocks.field_10128, renderLayer3);
		hashMap.put(Blocks.field_10354, renderLayer3);
		hashMap.put(Blocks.field_10151, renderLayer3);
		hashMap.put(Blocks.field_9981, renderLayer3);
		hashMap.put(Blocks.field_10162, renderLayer3);
		hashMap.put(Blocks.field_10365, renderLayer3);
		hashMap.put(Blocks.field_10598, renderLayer3);
		hashMap.put(Blocks.field_10249, renderLayer3);
		hashMap.put(Blocks.field_10400, renderLayer3);
		hashMap.put(Blocks.field_10061, renderLayer3);
		hashMap.put(Blocks.field_10074, renderLayer3);
		hashMap.put(Blocks.field_10358, renderLayer3);
		hashMap.put(Blocks.field_10273, renderLayer3);
		hashMap.put(Blocks.field_9998, renderLayer3);
		hashMap.put(Blocks.field_10138, renderLayer3);
		hashMap.put(Blocks.field_10324, renderLayer3);
		hashMap.put(Blocks.field_10487, renderLayer3);
		hashMap.put(Blocks.field_10018, renderLayer3);
		hashMap.put(Blocks.field_10609, renderLayer3);
		hashMap.put(Blocks.field_10247, renderLayer3);
		hashMap.put(Blocks.field_10377, renderLayer3);
		hashMap.put(Blocks.field_10546, renderLayer3);
		hashMap.put(Blocks.field_10453, renderLayer3);
		hashMap.put(Blocks.field_10583, renderLayer3);
		hashMap.put(Blocks.field_10378, renderLayer3);
		hashMap.put(Blocks.field_10430, renderLayer3);
		hashMap.put(Blocks.field_10003, renderLayer3);
		hashMap.put(Blocks.field_10214, renderLayer3);
		hashMap.put(Blocks.field_10313, renderLayer3);
		hashMap.put(Blocks.field_10521, renderLayer3);
		hashMap.put(Blocks.field_10352, renderLayer3);
		hashMap.put(Blocks.field_10627, renderLayer3);
		hashMap.put(Blocks.field_10232, renderLayer3);
		hashMap.put(Blocks.field_10403, renderLayer3);
		hashMap.put(Blocks.field_10455, renderLayer3);
		hashMap.put(Blocks.field_10021, renderLayer3);
		hashMap.put(Blocks.field_10528, renderLayer3);
		hashMap.put(Blocks.field_10341, renderLayer3);
		hashMap.put(Blocks.field_9993, renderLayer3);
		hashMap.put(Blocks.field_10463, renderLayer3);
		hashMap.put(Blocks.field_10195, renderLayer3);
		hashMap.put(Blocks.field_10082, renderLayer3);
		hashMap.put(Blocks.field_10572, renderLayer3);
		hashMap.put(Blocks.field_10296, renderLayer3);
		hashMap.put(Blocks.field_10579, renderLayer3);
		hashMap.put(Blocks.field_10032, renderLayer3);
		hashMap.put(Blocks.field_10125, renderLayer3);
		hashMap.put(Blocks.field_10339, renderLayer3);
		hashMap.put(Blocks.field_10134, renderLayer3);
		hashMap.put(Blocks.field_10618, renderLayer3);
		hashMap.put(Blocks.field_10169, renderLayer3);
		hashMap.put(Blocks.field_10448, renderLayer3);
		hashMap.put(Blocks.field_10097, renderLayer3);
		hashMap.put(Blocks.field_10047, renderLayer3);
		hashMap.put(Blocks.field_10568, renderLayer3);
		hashMap.put(Blocks.field_10221, renderLayer3);
		hashMap.put(Blocks.field_10053, renderLayer3);
		hashMap.put(Blocks.field_10079, renderLayer3);
		hashMap.put(Blocks.field_10427, renderLayer3);
		hashMap.put(Blocks.field_10551, renderLayer3);
		hashMap.put(Blocks.field_10005, renderLayer3);
		hashMap.put(Blocks.field_10347, renderLayer3);
		hashMap.put(Blocks.field_10116, renderLayer3);
		hashMap.put(Blocks.field_10094, renderLayer3);
		hashMap.put(Blocks.field_10557, renderLayer3);
		hashMap.put(Blocks.field_10239, renderLayer3);
		hashMap.put(Blocks.field_10584, renderLayer3);
		hashMap.put(Blocks.field_10186, renderLayer3);
		hashMap.put(Blocks.field_10447, renderLayer3);
		hashMap.put(Blocks.field_10498, renderLayer3);
		hashMap.put(Blocks.field_9976, renderLayer3);
		hashMap.put(Blocks.field_10476, renderLayer3);
		hashMap.put(Blocks.field_10502, renderLayer3);
		hashMap.put(Blocks.field_10108, renderLayer3);
		hashMap.put(Blocks.field_10211, renderLayer3);
		hashMap.put(Blocks.field_10586, renderLayer3);
		hashMap.put(Blocks.field_16492, renderLayer3);
		hashMap.put(Blocks.field_16335, renderLayer3);
		hashMap.put(Blocks.field_16541, renderLayer3);
		hashMap.put(Blocks.field_22110, renderLayer3);
		hashMap.put(Blocks.field_17350, renderLayer3);
		hashMap.put(Blocks.field_23860, renderLayer3);
		hashMap.put(Blocks.field_16999, renderLayer3);
		hashMap.put(Blocks.field_22123, renderLayer3);
		hashMap.put(Blocks.field_22124, renderLayer3);
		hashMap.put(Blocks.field_23078, renderLayer3);
		hashMap.put(Blocks.field_23079, renderLayer3);
		hashMap.put(Blocks.field_22117, renderLayer3);
		hashMap.put(Blocks.field_22121, renderLayer3);
		hashMap.put(Blocks.field_22114, renderLayer3);
		hashMap.put(Blocks.field_22125, renderLayer3);
		hashMap.put(Blocks.field_22116, renderLayer3);
		hashMap.put(Blocks.field_22424, renderLayer3);
		hashMap.put(Blocks.field_22425, renderLayer3);
		hashMap.put(Blocks.field_22426, renderLayer3);
		hashMap.put(Blocks.field_22427, renderLayer3);
		hashMap.put(Blocks.field_22102, renderLayer3);
		hashMap.put(Blocks.field_22103, renderLayer3);
		RenderLayer renderLayer4 = RenderLayer.getTranslucent();
		hashMap.put(Blocks.field_10295, renderLayer4);
		hashMap.put(Blocks.field_10316, renderLayer4);
		hashMap.put(Blocks.field_10087, renderLayer4);
		hashMap.put(Blocks.field_10227, renderLayer4);
		hashMap.put(Blocks.field_10574, renderLayer4);
		hashMap.put(Blocks.field_10271, renderLayer4);
		hashMap.put(Blocks.field_10049, renderLayer4);
		hashMap.put(Blocks.field_10157, renderLayer4);
		hashMap.put(Blocks.field_10317, renderLayer4);
		hashMap.put(Blocks.field_10555, renderLayer4);
		hashMap.put(Blocks.field_9996, renderLayer4);
		hashMap.put(Blocks.field_10248, renderLayer4);
		hashMap.put(Blocks.field_10399, renderLayer4);
		hashMap.put(Blocks.field_10060, renderLayer4);
		hashMap.put(Blocks.field_10073, renderLayer4);
		hashMap.put(Blocks.field_10357, renderLayer4);
		hashMap.put(Blocks.field_10272, renderLayer4);
		hashMap.put(Blocks.field_9997, renderLayer4);
		hashMap.put(Blocks.field_9991, renderLayer4);
		hashMap.put(Blocks.field_10496, renderLayer4);
		hashMap.put(Blocks.field_10469, renderLayer4);
		hashMap.put(Blocks.field_10193, renderLayer4);
		hashMap.put(Blocks.field_10578, renderLayer4);
		hashMap.put(Blocks.field_10305, renderLayer4);
		hashMap.put(Blocks.field_10565, renderLayer4);
		hashMap.put(Blocks.field_10077, renderLayer4);
		hashMap.put(Blocks.field_10129, renderLayer4);
		hashMap.put(Blocks.field_10355, renderLayer4);
		hashMap.put(Blocks.field_10152, renderLayer4);
		hashMap.put(Blocks.field_9982, renderLayer4);
		hashMap.put(Blocks.field_10163, renderLayer4);
		hashMap.put(Blocks.field_10419, renderLayer4);
		hashMap.put(Blocks.field_10118, renderLayer4);
		hashMap.put(Blocks.field_10070, renderLayer4);
		hashMap.put(Blocks.field_10030, renderLayer4);
		hashMap.put(Blocks.field_21211, renderLayer4);
		hashMap.put(Blocks.field_10110, renderLayer4);
		hashMap.put(Blocks.field_10422, renderLayer4);
	});
	private static final Map<Fluid, RenderLayer> FLUIDS = Util.make(Maps.<Fluid, RenderLayer>newHashMap(), hashMap -> {
		RenderLayer renderLayer = RenderLayer.getTranslucent();
		hashMap.put(Fluids.FLOWING_WATER, renderLayer);
		hashMap.put(Fluids.WATER, renderLayer);
	});
	private static boolean fancyGraphicsOrBetter;

	public static RenderLayer getBlockLayer(BlockState state) {
		Block block = state.getBlock();
		if (block instanceof LeavesBlock) {
			return fancyGraphicsOrBetter ? RenderLayer.getCutoutMipped() : RenderLayer.getSolid();
		} else {
			RenderLayer renderLayer = (RenderLayer)BLOCKS.get(block);
			return renderLayer != null ? renderLayer : RenderLayer.getSolid();
		}
	}

	public static RenderLayer getMovingBlockLayer(BlockState blockState) {
		Block block = blockState.getBlock();
		if (block instanceof LeavesBlock) {
			return fancyGraphicsOrBetter ? RenderLayer.getCutoutMipped() : RenderLayer.getSolid();
		} else {
			RenderLayer renderLayer = (RenderLayer)BLOCKS.get(block);
			if (renderLayer != null) {
				return renderLayer == RenderLayer.getTranslucent() ? RenderLayer.getTranslucentMovingBlock() : renderLayer;
			} else {
				return RenderLayer.getSolid();
			}
		}
	}

	public static RenderLayer getEntityBlockLayer(BlockState state, boolean direct) {
		RenderLayer renderLayer = getBlockLayer(state);
		if (renderLayer == RenderLayer.getTranslucent()) {
			if (!MinecraftClient.isFabulousGraphicsOrBetter()) {
				return TexturedRenderLayers.getEntityTranslucentCull();
			} else {
				return direct ? TexturedRenderLayers.getEntityTranslucentCull() : TexturedRenderLayers.getItemEntityTranslucentCull();
			}
		} else {
			return TexturedRenderLayers.getEntityCutout();
		}
	}

	public static RenderLayer getItemLayer(ItemStack stack, boolean direct) {
		Item item = stack.getItem();
		if (item instanceof BlockItem) {
			Block block = ((BlockItem)item).getBlock();
			return getEntityBlockLayer(block.getDefaultState(), direct);
		} else {
			return direct ? TexturedRenderLayers.getEntityTranslucentCull() : TexturedRenderLayers.getItemEntityTranslucentCull();
		}
	}

	public static RenderLayer getFluidLayer(FluidState state) {
		RenderLayer renderLayer = (RenderLayer)FLUIDS.get(state.getFluid());
		return renderLayer != null ? renderLayer : RenderLayer.getSolid();
	}

	public static void setFancyGraphicsOrBetter(boolean fancyGraphicsOrBetter) {
		RenderLayers.fancyGraphicsOrBetter = fancyGraphicsOrBetter;
	}
}
