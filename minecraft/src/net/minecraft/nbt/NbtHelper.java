package net.minecraft.nbt;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableMap;
import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.Dynamic;
import java.util.Optional;
import java.util.UUID;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.datafixers.DataFixTypes;
import net.minecraft.datafixers.NbtOps;
import net.minecraft.state.State;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import net.minecraft.util.ChatUtil;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class NbtHelper {
	private static final Logger LOGGER = LogManager.getLogger();

	@Nullable
	public static GameProfile toGameProfile(CompoundTag compoundTag) {
		String string = null;
		String string2 = null;
		if (compoundTag.contains("Name", 8)) {
			string = compoundTag.getString("Name");
		}

		if (compoundTag.contains("Id", 8)) {
			string2 = compoundTag.getString("Id");
		}

		try {
			UUID uUID;
			try {
				uUID = UUID.fromString(string2);
			} catch (Throwable var12) {
				uUID = null;
			}

			GameProfile gameProfile = new GameProfile(uUID, string);
			if (compoundTag.contains("Properties", 10)) {
				CompoundTag compoundTag2 = compoundTag.getCompound("Properties");

				for (String string3 : compoundTag2.getKeys()) {
					ListTag listTag = compoundTag2.getList(string3, 10);

					for (int i = 0; i < listTag.size(); i++) {
						CompoundTag compoundTag3 = listTag.getCompound(i);
						String string4 = compoundTag3.getString("Value");
						if (compoundTag3.contains("Signature", 8)) {
							gameProfile.getProperties().put(string3, new com.mojang.authlib.properties.Property(string3, string4, compoundTag3.getString("Signature")));
						} else {
							gameProfile.getProperties().put(string3, new com.mojang.authlib.properties.Property(string3, string4));
						}
					}
				}
			}

			return gameProfile;
		} catch (Throwable var13) {
			return null;
		}
	}

	public static CompoundTag fromGameProfile(CompoundTag compoundTag, GameProfile gameProfile) {
		if (!ChatUtil.isEmpty(gameProfile.getName())) {
			compoundTag.putString("Name", gameProfile.getName());
		}

		if (gameProfile.getId() != null) {
			compoundTag.putString("Id", gameProfile.getId().toString());
		}

		if (!gameProfile.getProperties().isEmpty()) {
			CompoundTag compoundTag2 = new CompoundTag();

			for (String string : gameProfile.getProperties().keySet()) {
				ListTag listTag = new ListTag();

				for (com.mojang.authlib.properties.Property property : gameProfile.getProperties().get(string)) {
					CompoundTag compoundTag3 = new CompoundTag();
					compoundTag3.putString("Value", property.getValue());
					if (property.hasSignature()) {
						compoundTag3.putString("Signature", property.getSignature());
					}

					listTag.add(compoundTag3);
				}

				compoundTag2.put(string, listTag);
			}

			compoundTag.put("Properties", compoundTag2);
		}

		return compoundTag;
	}

	@VisibleForTesting
	public static boolean matches(@Nullable Tag tag, @Nullable Tag tag2, boolean bl) {
		if (tag == tag2) {
			return true;
		} else if (tag == null) {
			return true;
		} else if (tag2 == null) {
			return false;
		} else if (!tag.getClass().equals(tag2.getClass())) {
			return false;
		} else if (tag instanceof CompoundTag) {
			CompoundTag compoundTag = (CompoundTag)tag;
			CompoundTag compoundTag2 = (CompoundTag)tag2;

			for (String string : compoundTag.getKeys()) {
				Tag tag3 = compoundTag.get(string);
				if (!matches(tag3, compoundTag2.get(string), bl)) {
					return false;
				}
			}

			return true;
		} else if (tag instanceof ListTag && bl) {
			ListTag listTag = (ListTag)tag;
			ListTag listTag2 = (ListTag)tag2;
			if (listTag.isEmpty()) {
				return listTag2.isEmpty();
			} else {
				for (int i = 0; i < listTag.size(); i++) {
					Tag tag4 = listTag.method_10534(i);
					boolean bl2 = false;

					for (int j = 0; j < listTag2.size(); j++) {
						if (matches(tag4, listTag2.method_10534(j), bl)) {
							bl2 = true;
							break;
						}
					}

					if (!bl2) {
						return false;
					}
				}

				return true;
			}
		} else {
			return tag.equals(tag2);
		}
	}

	public static CompoundTag fromUuid(UUID uUID) {
		CompoundTag compoundTag = new CompoundTag();
		compoundTag.putLong("M", uUID.getMostSignificantBits());
		compoundTag.putLong("L", uUID.getLeastSignificantBits());
		return compoundTag;
	}

	public static UUID toUuid(CompoundTag compoundTag) {
		return new UUID(compoundTag.getLong("M"), compoundTag.getLong("L"));
	}

	public static BlockPos toBlockPos(CompoundTag compoundTag) {
		return new BlockPos(compoundTag.getInt("X"), compoundTag.getInt("Y"), compoundTag.getInt("Z"));
	}

	public static CompoundTag fromBlockPos(BlockPos blockPos) {
		CompoundTag compoundTag = new CompoundTag();
		compoundTag.putInt("X", blockPos.getX());
		compoundTag.putInt("Y", blockPos.getY());
		compoundTag.putInt("Z", blockPos.getZ());
		return compoundTag;
	}

	public static BlockState toBlockState(CompoundTag compoundTag) {
		if (!compoundTag.contains("Name", 8)) {
			return Blocks.AIR.getDefaultState();
		} else {
			Block block = Registry.BLOCK.get(new Identifier(compoundTag.getString("Name")));
			BlockState blockState = block.getDefaultState();
			if (compoundTag.contains("Properties", 10)) {
				CompoundTag compoundTag2 = compoundTag.getCompound("Properties");
				StateManager<Block, BlockState> stateManager = block.getStateFactory();

				for (String string : compoundTag2.getKeys()) {
					Property<?> property = stateManager.getProperty(string);
					if (property != null) {
						blockState = withProperty(blockState, property, string, compoundTag2, compoundTag);
					}
				}
			}

			return blockState;
		}
	}

	private static <S extends State<S>, T extends Comparable<T>> S withProperty(
		S state, Property<T> property, String string, CompoundTag compoundTag, CompoundTag compoundTag2
	) {
		Optional<T> optional = property.parse(compoundTag.getString(string));
		if (optional.isPresent()) {
			return state.with(property, (Comparable)optional.get());
		} else {
			LOGGER.warn("Unable to read property: {} with value: {} for blockstate: {}", string, compoundTag.getString(string), compoundTag2.toString());
			return state;
		}
	}

	public static CompoundTag fromBlockState(BlockState blockState) {
		CompoundTag compoundTag = new CompoundTag();
		compoundTag.putString("Name", Registry.BLOCK.getId(blockState.getBlock()).toString());
		ImmutableMap<Property<?>, Comparable<?>> immutableMap = blockState.getEntries();
		if (!immutableMap.isEmpty()) {
			CompoundTag compoundTag2 = new CompoundTag();

			for (Entry<Property<?>, Comparable<?>> entry : immutableMap.entrySet()) {
				Property<?> property = (Property<?>)entry.getKey();
				compoundTag2.putString(property.getName(), nameValue(property, (Comparable<?>)entry.getValue()));
			}

			compoundTag.put("Properties", compoundTag2);
		}

		return compoundTag;
	}

	private static <T extends Comparable<T>> String nameValue(Property<T> property, Comparable<?> comparable) {
		return property.name((T)comparable);
	}

	public static CompoundTag update(DataFixer dataFixer, DataFixTypes dataFixTypes, CompoundTag compoundTag, int i) {
		return update(dataFixer, dataFixTypes, compoundTag, i, SharedConstants.getGameVersion().getWorldVersion());
	}

	public static CompoundTag update(DataFixer dataFixer, DataFixTypes dataFixTypes, CompoundTag compoundTag, int i, int j) {
		return (CompoundTag)dataFixer.update(dataFixTypes.getTypeReference(), new Dynamic<>(NbtOps.INSTANCE, compoundTag), i, j).getValue();
	}
}
