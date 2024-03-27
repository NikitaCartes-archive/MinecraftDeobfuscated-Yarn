package net.minecraft.structure.processor;

import com.mojang.serialization.MapCodec;
import java.util.function.Supplier;

public class NopStructureProcessor extends StructureProcessor {
	public static final MapCodec<NopStructureProcessor> CODEC = MapCodec.unit((Supplier<NopStructureProcessor>)(() -> NopStructureProcessor.INSTANCE));
	public static final NopStructureProcessor INSTANCE = new NopStructureProcessor();

	private NopStructureProcessor() {
	}

	@Override
	protected StructureProcessorType<?> getType() {
		return StructureProcessorType.NOP;
	}
}
