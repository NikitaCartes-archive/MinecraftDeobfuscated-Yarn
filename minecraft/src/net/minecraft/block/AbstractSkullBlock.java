package net.minecraft.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.world.BlockView;

public abstract class AbstractSkullBlock extends BlockWithEntity {
	private final SkullBlock.SkullType type;

	public AbstractSkullBlock(SkullBlock.SkullType type, AbstractBlock.Settings settings) {
		super(settings);
		this.type = type;
	}

	@Override
	public BlockEntity createBlockEntity(BlockView world) {
		return new SkullBlockEntity();
	}

	@Environment(EnvType.CLIENT)
	public SkullBlock.SkullType getSkullType() {
		return this.type;
	}
}
