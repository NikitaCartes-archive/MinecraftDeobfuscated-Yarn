package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.DyeColor;

public class PotatoPeelsBlock extends Block {
	public static final MapCodec<PotatoPeelsBlock> CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(createSettingsCodec(), DyeColor.CODEC.fieldOf("color").forGetter(block -> block.color)).apply(instance, PotatoPeelsBlock::new)
	);
	private final DyeColor color;

	public PotatoPeelsBlock(AbstractBlock.Settings settings, DyeColor color) {
		super(settings);
		this.color = color;
	}

	@Override
	protected MapCodec<PotatoPeelsBlock> getCodec() {
		return CODEC;
	}

	public DyeColor getColor() {
		return this.color;
	}

	public Item getPeelsItem() {
		return Items.POTATO_PEELS.get(this.getColor());
	}
}
