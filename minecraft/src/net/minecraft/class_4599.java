package net.minecraft;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import java.util.SortedMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.chunk.BlockLayeredBufferBuilder;
import net.minecraft.util.SystemUtil;

@Environment(EnvType.CLIENT)
public class class_4599 {
	private final BlockLayeredBufferBuilder field_20956 = new BlockLayeredBufferBuilder();
	private final SortedMap<BlockRenderLayer, BufferBuilder> field_20957 = SystemUtil.consume(
		new Object2ObjectLinkedOpenHashMap<>(), object2ObjectLinkedOpenHashMap -> {
			for (BlockRenderLayer blockRenderLayer : BlockRenderLayer.method_22720()) {
				object2ObjectLinkedOpenHashMap.put(blockRenderLayer, this.field_20956.get(blockRenderLayer));
			}

			object2ObjectLinkedOpenHashMap.put(BlockRenderLayer.TRANSLUCENT_NO_CRUMBLING, new BufferBuilder(BlockRenderLayer.TRANSLUCENT_NO_CRUMBLING.method_22722()));
			object2ObjectLinkedOpenHashMap.put(BlockRenderLayer.GLINT, new BufferBuilder(BlockRenderLayer.GLINT.method_22722()));
			object2ObjectLinkedOpenHashMap.put(BlockRenderLayer.ENTITY_GLINT, new BufferBuilder(BlockRenderLayer.ENTITY_GLINT.method_22722()));
		}
	);
	private final class_4597.class_4598 field_20958 = class_4597.method_22992(this.field_20957, new BufferBuilder(256));
	private final class_4597.class_4598 field_20959 = class_4597.method_22991(new BufferBuilder(256));
	private final BufferBuilder field_20960 = new BufferBuilder(BlockRenderLayer.OUTLINE.method_22722());
	private final class_4586 field_20961 = new class_4586(this.field_20960);
	private final class_4597 field_20962 = blockRenderLayer -> {
		class_4588 lv = this.field_20958.getBuffer(blockRenderLayer);
		return (class_4588)(blockRenderLayer.method_23035() ? new class_4589(ImmutableList.of(this.field_20961, lv)) : lv);
	};

	public BlockLayeredBufferBuilder method_22997() {
		return this.field_20956;
	}

	public class_4597.class_4598 method_23000() {
		return this.field_20958;
	}

	public class_4597.class_4598 method_23001() {
		return this.field_20959;
	}

	public BufferBuilder method_23002() {
		return this.field_20960;
	}

	public class_4586 method_23003() {
		return this.field_20961;
	}

	public class_4597 method_23004() {
		return this.field_20962;
	}
}
