package net.minecraft;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import it.unimi.dsi.fastutil.chars.Char2ObjectMap;
import it.unimi.dsi.fastutil.chars.Char2ObjectOpenHashMap;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.DirectionTransformation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;
import net.minecraft.world.gen.stateprovider.BlockStateProviderType;

public class class_5104 implements FeatureConfig {
	public final BlockStateProvider field_23574;
	public final char field_23575;
	public final DirectionTransformation field_23576;
	private static final Char2ObjectMap<byte[]> field_23577 = method_26600();
	private static final byte[] field_23578 = field_23577.get('�');

	public static Char2ObjectMap<byte[]> method_26600() {
		Char2ObjectOpenHashMap<byte[]> char2ObjectOpenHashMap = new Char2ObjectOpenHashMap<>();

		try {
			InputStream inputStream = class_5104.class.getResourceAsStream("/chars.bin");
			Throwable var2 = null;

			try {
				DataInputStream dataInputStream = new DataInputStream(inputStream);

				while (true) {
					char c = dataInputStream.readChar();
					byte[] bs = new byte[8];
					if (dataInputStream.read(bs) != 8) {
						break;
					}

					char2ObjectOpenHashMap.put(c, bs);
				}
			} catch (Throwable var15) {
				var2 = var15;
				throw var15;
			} finally {
				if (inputStream != null) {
					if (var2 != null) {
						try {
							inputStream.close();
						} catch (Throwable var14) {
							var2.addSuppressed(var14);
						}
					} else {
						inputStream.close();
					}
				}
			}
		} catch (EOFException var17) {
		} catch (IOException var18) {
		}

		return char2ObjectOpenHashMap;
	}

	@Nullable
	public byte[] method_26604() {
		return field_23577.getOrDefault(this.field_23575, field_23578);
	}

	public class_5104(BlockStateProvider blockStateProvider, char c, DirectionTransformation directionTransformation) {
		this.field_23574 = blockStateProvider;
		this.field_23575 = c;
		this.field_23576 = directionTransformation;
	}

	@Override
	public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
		return new Dynamic<>(
			ops,
			ops.createMap(
				ImmutableMap.of(
					ops.createString("material"),
					this.field_23574.serialize(ops),
					ops.createString("char"),
					ops.createInt(this.field_23575),
					ops.createString("orientation"),
					ops.createString(this.field_23576.asString())
				)
			)
		);
	}

	public static <T> class_5104 method_26601(Dynamic<T> dynamic) {
		BlockStateProvider blockStateProvider = (BlockStateProvider)dynamic.get("material")
			.map(
				dynamicx -> {
					Identifier identifier = (Identifier)dynamicx.get("type").asString().map(Identifier::new).get();
					BlockStateProviderType<?> blockStateProviderType = (BlockStateProviderType<?>)Registry.BLOCK_STATE_PROVIDER_TYPE
						.getOrEmpty(identifier)
						.orElseThrow(() -> new IllegalStateException(identifier.toString()));
					return blockStateProviderType.deserialize(dynamicx);
				}
			)
			.orElseThrow(IllegalStateException::new);
		char c = (char)dynamic.asInt(66);
		DirectionTransformation directionTransformation = (DirectionTransformation)dynamic.get("orientation")
			.asString()
			.flatMap(DirectionTransformation::method_26484)
			.orElse(DirectionTransformation.IDENTITY);
		return new class_5104(blockStateProvider, c, directionTransformation);
	}

	public static class_5104 method_26602(Random random) {
		BlockStateProvider blockStateProvider = BlockStateProvider.method_26659(random);
		char c = Util.<Character>method_26719(random, ImmutableList.copyOf(field_23577.keySet()));
		DirectionTransformation directionTransformation = Util.method_26721(random, DirectionTransformation.values());
		return new class_5104(blockStateProvider, c, directionTransformation);
	}
}
