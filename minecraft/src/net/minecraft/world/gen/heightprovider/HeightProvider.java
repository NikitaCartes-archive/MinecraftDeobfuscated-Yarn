package net.minecraft.world.gen.heightprovider;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.gen.HeightContext;
import net.minecraft.world.gen.YOffset;

public abstract class HeightProvider {
	private static final Codec<Either<YOffset, HeightProvider>> OFFSET_OR_HEIGHT_CODEC = Codec.either(
		YOffset.OFFSET_CODEC, Registries.HEIGHT_PROVIDER_TYPE.getCodec().dispatch(HeightProvider::getType, HeightProviderType::codec)
	);
	public static final Codec<HeightProvider> CODEC = OFFSET_OR_HEIGHT_CODEC.xmap(
		either -> either.map(ConstantHeightProvider::create, provider -> provider),
		provider -> provider.getType() == HeightProviderType.CONSTANT ? Either.left(((ConstantHeightProvider)provider).getOffset()) : Either.right(provider)
	);

	public abstract int get(Random random, HeightContext context);

	public abstract HeightProviderType<?> getType();
}
