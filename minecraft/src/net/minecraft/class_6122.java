package net.minecraft;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.HeightContext;
import net.minecraft.world.gen.YOffset;

public abstract class class_6122 {
	private static final Codec<Either<YOffset, class_6122>> field_31539 = Codec.either(
		YOffset.OFFSET_CODEC, Registry.HEIGHT_PROVIDER_TYPE.dispatch(class_6122::method_35388, class_6123::codec)
	);
	public static final Codec<class_6122> field_31540 = field_31539.xmap(
		either -> either.map(class_6121::method_35383, arg -> arg),
		arg -> arg.method_35388() == class_6123.field_31541 ? Either.left(((class_6121)arg).method_35385()) : Either.right(arg)
	);

	public abstract int method_35391(Random random, HeightContext heightContext);

	public abstract class_6123<?> method_35388();
}
