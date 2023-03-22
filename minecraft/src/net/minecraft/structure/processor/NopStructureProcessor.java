package net.minecraft.structure.processor;

import com.mojang.serialization.Codec;
import java.util.function.Supplier;

public class NopStructureProcessor extends StructureProcessor {
	public static final Codec<NopStructureProcessor> CODEC = Codec.unit((Supplier<NopStructureProcessor>)(() -> NopStructureProcessor.INSTANCE));
	public static final NopStructureProcessor INSTANCE = new NopStructureProcessor();

	private NopStructureProcessor() {
	}

	@Override
	protected StructureProcessorType<?> getType() {
		return StructureProcessorType.NOP;
	}
}
