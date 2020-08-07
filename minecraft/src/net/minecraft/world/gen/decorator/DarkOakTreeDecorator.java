package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;

public class DarkOakTreeDecorator extends AbstractHeightmapDecorator<NopeDecoratorConfig> {
	public DarkOakTreeDecorator(Codec<NopeDecoratorConfig> codec) {
		super(codec);
	}

	protected Heightmap.Type method_30452(NopeDecoratorConfig nopeDecoratorConfig) {
		return Heightmap.Type.field_13197;
	}

	public Stream<BlockPos> method_14524(DecoratorContext decoratorContext, Random random, NopeDecoratorConfig nopeDecoratorConfig, BlockPos blockPos) {
		return IntStream.range(0, 16).mapToObj(i -> {
			int j = i / 4;
			int k = i % 4;
			int l = j * 4 + 1 + random.nextInt(3) + blockPos.getX();
			int m = k * 4 + 1 + random.nextInt(3) + blockPos.getZ();
			int n = decoratorContext.getTopY(this.method_30452(nopeDecoratorConfig), l, m);
			return new BlockPos(l, n, m);
		});
	}
}
