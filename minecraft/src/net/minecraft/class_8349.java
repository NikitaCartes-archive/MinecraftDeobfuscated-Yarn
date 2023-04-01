package net.minecraft;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.ServerStatHandler;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.math.random.Random;

public class class_8349 extends class_8277.class_8280 {
	private final Codec<class_8349.class_8351> field_43929 = RecordCodecBuilder.create(
		instance -> instance.group(
					Registries.ITEM.getCodec().fieldOf("source").forGetter(argx -> argx.field_43932),
					Registries.ITEM.getCodec().fieldOf("target").forGetter(argx -> argx.field_43933)
				)
				.apply(instance, (item, item2) -> new class_8349.class_8351(item, item2))
	);
	private final class_8349.class_8350 field_43930;

	public class_8349(class_8349.class_8350 arg) {
		this.field_43930 = arg;
	}

	@Override
	public Codec<class_8291> method_50120() {
		return class_8289.method_50202(this.field_43929);
	}

	@Override
	protected Optional<class_8291> method_50169(MinecraftServer minecraftServer, Random random) {
		Registry<Item> registry = minecraftServer.getRegistryManager().get(RegistryKeys.ITEM);
		Optional<Item> optional = method_50428(minecraftServer, random, registry);
		Optional<Item> optional2 = this.field_43930.get(registry, random);
		return optional.isPresent() && optional2.isPresent() && !optional.equals(optional2)
			? Optional.of(new class_8349.class_8351((Item)optional.get(), (Item)optional2.get()))
			: Optional.empty();
	}

	private static Optional<Item> method_50428(MinecraftServer minecraftServer, Random random, Registry<Item> registry) {
		if (random.nextInt(10) != 0) {
			List<Item> list = registry.stream()
				.filter(
					item -> {
						for (ServerPlayerEntity serverPlayerEntity : minecraftServer.getPlayerManager().getPlayerList()) {
							ServerStatHandler serverStatHandler = serverPlayerEntity.getStatHandler();
							if (serverStatHandler.getStat(Stats.PICKED_UP, item) > 0
								|| serverStatHandler.getStat(Stats.USED, item) > 0
								|| serverStatHandler.getStat(Stats.CRAFTED, item) > 0) {
								return true;
							}
						}

						return false;
					}
				)
				.toList();
			if (!list.isEmpty()) {
				return Util.getRandomOrEmpty(list, random);
			}
		}

		return registry.getRandom(random).map(RegistryEntry.Reference::value).filter(item -> item != Items.AIR);
	}

	public interface class_8350 {
		Optional<Item> get(Registry<Item> registry, Random random);
	}

	protected class class_8351 extends class_8277.class_8278 {
		final Item field_43932;
		final Item field_43933;
		private final Text field_43934;

		protected class_8351(Item item, Item item2) {
			this.field_43932 = item;
			this.field_43933 = item2;
			this.field_43934 = Text.translatable("rule.replace_items", item.getName(), item2.getName());
		}

		@Override
		protected Text method_50166() {
			return this.field_43934;
		}

		@Override
		public void method_50165(MinecraftServer minecraftServer) {
			for (ServerPlayerEntity serverPlayerEntity : minecraftServer.getPlayerManager().getPlayerList()) {
				serverPlayerEntity.getInventory().method_50711(this.field_43932, this.field_43933);
				serverPlayerEntity.currentScreenHandler.sendContentUpdates();
			}
		}
	}
}
