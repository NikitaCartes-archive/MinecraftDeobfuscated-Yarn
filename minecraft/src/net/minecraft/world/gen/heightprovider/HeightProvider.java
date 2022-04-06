package net.minecraft.world.gen.heightprovider;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import net.minecraft.util.math.random.AbstractRandom;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.HeightContext;
import net.minecraft.world.gen.YOffset;

public abstract class HeightProvider {
	private static final Codec<Either<YOffset, HeightProvider>> field_31539 = Codec.either(
		YOffset.OFFSET_CODEC, Registry.HEIGHT_PROVIDER_TYPE.getCodec().dispatch(HeightProvider::getType, HeightProviderType::codec)
	);
	public static final Codec<HeightProvider> CODEC = field_31539.xmap(
		either -> either.map(ConstantHeightProvider::create, provider -> provider),
		provider -> provider.getType() == HeightProviderType.CONSTANT ? Either.left(((ConstantHeightProvider)provider).getOffset()) : Either.right(provider)
	);

	public abstract int get(AbstractRandom abstractRandom, HeightContext context);

	public abstract HeightProviderType<?> getType();
}
