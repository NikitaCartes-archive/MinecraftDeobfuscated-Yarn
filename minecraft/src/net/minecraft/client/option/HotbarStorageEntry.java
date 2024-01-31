package net.minecraft.client.option;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Util;
import net.minecraft.util.dynamic.Codecs;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class HotbarStorageEntry {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final int HOTBAR_SIZE = PlayerInventory.getHotbarSize();
	public static final Codec<HotbarStorageEntry> CODEC = Codecs.<List<Dynamic<?>>>validate(
			Codec.PASSTHROUGH.listOf(), list -> Util.decodeFixedLengthList(list, HOTBAR_SIZE)
		)
		.xmap(HotbarStorageEntry::new, hotbarStorageEntry -> hotbarStorageEntry.field_48947);
	private static final DynamicOps<NbtElement> field_48945 = NbtOps.INSTANCE;
	private static final Dynamic<?> field_48946 = new Dynamic<>(
		field_48945, Util.getResult(ItemStack.CODEC.encodeStart(field_48945, ItemStack.EMPTY), IllegalStateException::new)
	);
	private List<Dynamic<?>> field_48947;

	private HotbarStorageEntry(List<Dynamic<?>> list) {
		this.field_48947 = list;
	}

	public HotbarStorageEntry() {
		this(Collections.nCopies(HOTBAR_SIZE, field_48946));
	}

	public List<ItemStack> method_56839(RegistryWrapper.WrapperLookup registryLookup) {
		return this.field_48947
			.stream()
			.map(
				dynamic -> (ItemStack)ItemStack.CODEC
						.parse(RegistryOps.method_56622(dynamic, registryLookup))
						.resultOrPartial(string -> LOGGER.warn("Could not parse hotbar item: {}", string))
						.orElse(ItemStack.EMPTY)
			)
			.toList();
	}

	public void method_56836(PlayerInventory playerInventory, DynamicRegistryManager registryManager) {
		RegistryOps<NbtElement> registryOps = RegistryOps.of(field_48945, registryManager);
		Builder<Dynamic<?>> builder = ImmutableList.builderWithExpectedSize(HOTBAR_SIZE);

		for (int i = 0; i < HOTBAR_SIZE; i++) {
			ItemStack itemStack = playerInventory.getStack(i);
			Optional<Dynamic<?>> optional = ItemStack.CODEC
				.encodeStart(registryOps, itemStack)
				.resultOrPartial(string -> LOGGER.warn("Could not encode hotbar item: {}", string))
				.map(nbtElement -> new Dynamic<>(field_48945, nbtElement));
			builder.add((Dynamic<?>)optional.orElse(field_48946));
		}

		this.field_48947 = builder.build();
	}

	public boolean method_56835() {
		for (Dynamic<?> dynamic : this.field_48947) {
			if (!method_56837(dynamic)) {
				return false;
			}
		}

		return true;
	}

	private static boolean method_56837(Dynamic<?> dynamic) {
		return field_48946.equals(dynamic);
	}
}
