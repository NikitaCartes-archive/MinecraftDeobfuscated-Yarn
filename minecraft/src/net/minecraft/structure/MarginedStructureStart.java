package net.minecraft.structure;

import com.mojang.serialization.Codec;
import net.minecraft.class_6621;
import net.minecraft.class_6622;
import net.minecraft.util.math.BlockBox;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;

public abstract class MarginedStructureStart<C extends FeatureConfig> extends StructureFeature<C> {
	public MarginedStructureStart(Codec<C> codec, class_6622<C> arg) {
		super(codec, arg);
	}

	public MarginedStructureStart(Codec<C> codec, class_6622<C> arg, class_6621 arg2) {
		super(codec, arg, arg2);
	}

	@Override
	public BlockBox calculateBoundingBox(BlockBox blockBox) {
		return super.calculateBoundingBox(blockBox).expand(12);
	}
}
