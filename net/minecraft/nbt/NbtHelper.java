/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.nbt;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.DataFixer;
import com.mojang.serialization.Dynamic;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.fabricmc.yarn.constants.NbtTypeIds;
import net.minecraft.SharedConstants;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtIntArray;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtString;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.nbt.visitor.NbtOrderedStringFormatter;
import net.minecraft.nbt.visitor.NbtTextFormatter;
import net.minecraft.state.State;
import net.minecraft.state.StateManager;
import net.minecraft.text.Text;
import net.minecraft.util.ChatUtil;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.DynamicSerializableUuid;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public final class NbtHelper {
    private static final Comparator<NbtList> field_27816 = Comparator.comparingInt(nbtList -> nbtList.getInt(1)).thenComparingInt(nbtList -> nbtList.getInt(0)).thenComparingInt(nbtList -> nbtList.getInt(2));
    private static final Comparator<NbtList> field_27817 = Comparator.comparingDouble(nbtList -> nbtList.getDouble(1)).thenComparingDouble(nbtList -> nbtList.getDouble(0)).thenComparingDouble(nbtList -> nbtList.getDouble(2));
    private static final Splitter COMMA_SPLITTER = Splitter.on(",");
    private static final Splitter field_27819 = Splitter.on(':').limit(2);
    private static final Logger LOGGER = LogManager.getLogger();

    @Nullable
    public static GameProfile toGameProfile(NbtCompound compound) {
        String string = null;
        UUID uUID = null;
        if (compound.contains("Name", NbtTypeIds.STRING)) {
            string = compound.getString("Name");
        }
        if (compound.containsUuid("Id")) {
            uUID = compound.getUuid("Id");
        }
        try {
            GameProfile gameProfile = new GameProfile(uUID, string);
            if (compound.contains("Properties", NbtTypeIds.COMPOUND)) {
                NbtCompound nbtCompound = compound.getCompound("Properties");
                for (String string2 : nbtCompound.getKeys()) {
                    NbtList nbtList = nbtCompound.getList(string2, NbtTypeIds.COMPOUND);
                    for (int i = 0; i < nbtList.size(); ++i) {
                        NbtCompound nbtCompound2 = nbtList.getCompound(i);
                        String string3 = nbtCompound2.getString("Value");
                        if (nbtCompound2.contains("Signature", NbtTypeIds.STRING)) {
                            gameProfile.getProperties().put(string2, new Property(string2, string3, nbtCompound2.getString("Signature")));
                            continue;
                        }
                        gameProfile.getProperties().put(string2, new Property(string2, string3));
                    }
                }
            }
            return gameProfile;
        } catch (Throwable throwable) {
            return null;
        }
    }

    public static NbtCompound writeGameProfile(NbtCompound compound, GameProfile profile) {
        if (!ChatUtil.isEmpty(profile.getName())) {
            compound.putString("Name", profile.getName());
        }
        if (profile.getId() != null) {
            compound.putUuid("Id", profile.getId());
        }
        if (!profile.getProperties().isEmpty()) {
            NbtCompound nbtCompound = new NbtCompound();
            for (String string : profile.getProperties().keySet()) {
                NbtList nbtList = new NbtList();
                for (Property property : profile.getProperties().get(string)) {
                    NbtCompound nbtCompound2 = new NbtCompound();
                    nbtCompound2.putString("Value", property.getValue());
                    if (property.hasSignature()) {
                        nbtCompound2.putString("Signature", property.getSignature());
                    }
                    nbtList.add(nbtCompound2);
                }
                nbtCompound.put(string, nbtList);
            }
            compound.put("Properties", nbtCompound);
        }
        return compound;
    }

    @VisibleForTesting
    public static boolean matches(@Nullable NbtElement standard, @Nullable NbtElement subject, boolean equalValue) {
        if (standard == subject) {
            return true;
        }
        if (standard == null) {
            return true;
        }
        if (subject == null) {
            return false;
        }
        if (!standard.getClass().equals(subject.getClass())) {
            return false;
        }
        if (standard instanceof NbtCompound) {
            NbtCompound nbtCompound = (NbtCompound)standard;
            NbtCompound nbtCompound2 = (NbtCompound)subject;
            for (String string : nbtCompound.getKeys()) {
                NbtElement nbtElement = nbtCompound.get(string);
                if (NbtHelper.matches(nbtElement, nbtCompound2.get(string), equalValue)) continue;
                return false;
            }
            return true;
        }
        if (standard instanceof NbtList && equalValue) {
            NbtList nbtList = (NbtList)standard;
            NbtList nbtList2 = (NbtList)subject;
            if (nbtList.isEmpty()) {
                return nbtList2.isEmpty();
            }
            for (int i = 0; i < nbtList.size(); ++i) {
                NbtElement nbtElement2 = nbtList.get(i);
                boolean bl = false;
                for (int j = 0; j < nbtList2.size(); ++j) {
                    if (!NbtHelper.matches(nbtElement2, nbtList2.get(j), equalValue)) continue;
                    bl = true;
                    break;
                }
                if (bl) continue;
                return false;
            }
            return true;
        }
        return standard.equals(subject);
    }

    /**
     * Serializes a {@link UUID} into its equivalent NBT representation.
     * 
     * @since 20w10a
     */
    public static NbtIntArray fromUuid(UUID uuid) {
        return new NbtIntArray(DynamicSerializableUuid.toIntArray(uuid));
    }

    /**
     * Deserializes an NBT element into a {@link UUID}.
     * The NBT element's data must have the same structure as the output of {@link #fromUuid}.
     * 
     * @throws IllegalArgumentException if {@code element} is not a valid representation of a UUID
     * @since 20w10a
     */
    public static UUID toUuid(NbtElement element) {
        if (element.getNbtType() != NbtIntArray.TYPE) {
            throw new IllegalArgumentException("Expected UUID-Tag to be of type " + NbtIntArray.TYPE.getCrashReportName() + ", but found " + element.getNbtType().getCrashReportName() + ".");
        }
        int[] is = ((NbtIntArray)element).getIntArray();
        if (is.length != 4) {
            throw new IllegalArgumentException("Expected UUID-Array to be of length 4, but found " + is.length + ".");
        }
        return DynamicSerializableUuid.toUuid(is);
    }

    public static BlockPos toBlockPos(NbtCompound compound) {
        return new BlockPos(compound.getInt("X"), compound.getInt("Y"), compound.getInt("Z"));
    }

    public static NbtCompound fromBlockPos(BlockPos pos) {
        NbtCompound nbtCompound = new NbtCompound();
        nbtCompound.putInt("X", pos.getX());
        nbtCompound.putInt("Y", pos.getY());
        nbtCompound.putInt("Z", pos.getZ());
        return nbtCompound;
    }

    public static BlockState toBlockState(NbtCompound compound) {
        if (!compound.contains("Name", NbtTypeIds.STRING)) {
            return Blocks.AIR.getDefaultState();
        }
        Block block = Registry.BLOCK.get(new Identifier(compound.getString("Name")));
        BlockState blockState = block.getDefaultState();
        if (compound.contains("Properties", NbtTypeIds.COMPOUND)) {
            NbtCompound nbtCompound = compound.getCompound("Properties");
            StateManager<Block, BlockState> stateManager = block.getStateManager();
            for (String string : nbtCompound.getKeys()) {
                net.minecraft.state.property.Property<?> property = stateManager.getProperty(string);
                if (property == null) continue;
                blockState = NbtHelper.withProperty(blockState, property, string, nbtCompound, compound);
            }
        }
        return blockState;
    }

    private static <S extends State<?, S>, T extends Comparable<T>> S withProperty(S state, net.minecraft.state.property.Property<T> property, String key, NbtCompound properties, NbtCompound root) {
        Optional<T> optional = property.parse(properties.getString(key));
        if (optional.isPresent()) {
            return (S)((State)state.with(property, (Comparable)((Comparable)optional.get())));
        }
        LOGGER.warn("Unable to read property: {} with value: {} for blockstate: {}", (Object)key, (Object)properties.getString(key), (Object)root.toString());
        return state;
    }

    public static NbtCompound fromBlockState(BlockState state) {
        NbtCompound nbtCompound = new NbtCompound();
        nbtCompound.putString("Name", Registry.BLOCK.getId(state.getBlock()).toString());
        ImmutableMap<net.minecraft.state.property.Property<?>, Comparable<?>> immutableMap = state.getEntries();
        if (!immutableMap.isEmpty()) {
            NbtCompound nbtCompound2 = new NbtCompound();
            for (Map.Entry entry : immutableMap.entrySet()) {
                net.minecraft.state.property.Property property = (net.minecraft.state.property.Property)entry.getKey();
                nbtCompound2.putString(property.getName(), NbtHelper.nameValue(property, (Comparable)entry.getValue()));
            }
            nbtCompound.put("Properties", nbtCompound2);
        }
        return nbtCompound;
    }

    private static <T extends Comparable<T>> String nameValue(net.minecraft.state.property.Property<T> property, Comparable<?> value) {
        return property.name(value);
    }

    /**
     * Uses the data fixer to update an NBT compound object to the latest data version.
     * 
     * @param fixer the data fixer
     * @param fixTypes the fix types
     * @param compound the NBT compound object to fix
     * @param oldVersion the data version of the NBT compound object
     */
    public static NbtCompound update(DataFixer fixer, DataFixTypes fixTypes, NbtCompound compound, int oldVersion) {
        return NbtHelper.update(fixer, fixTypes, compound, oldVersion, SharedConstants.getGameVersion().getWorldVersion());
    }

    /**
     * Uses the data fixer to update an NBT compound object.
     * 
     * @param fixer the data fixer
     * @param fixTypes the fix types
     * @param compound the NBT compound object to fix
     * @param oldVersion the data version of the NBT compound object
     * @param targetVersion the data version to update the NBT compound object to
     */
    public static NbtCompound update(DataFixer fixer, DataFixTypes fixTypes, NbtCompound compound, int oldVersion, int targetVersion) {
        return fixer.update(fixTypes.getTypeReference(), new Dynamic<NbtCompound>(NbtOps.INSTANCE, compound), oldVersion, targetVersion).getValue();
    }

    public static Text toPrettyPrintedText(NbtElement element) {
        return new NbtTextFormatter("", 0).apply(element);
    }

    public static String toPrettyPrintedString(NbtCompound compound) {
        return new NbtOrderedStringFormatter().apply(NbtHelper.method_32273(compound));
    }

    public static NbtCompound method_32260(String string) throws CommandSyntaxException {
        return NbtHelper.method_32275(StringNbtReader.parse(string));
    }

    @VisibleForTesting
    static NbtCompound method_32273(NbtCompound compound) {
        NbtList nbtList4;
        NbtList nbtList32;
        boolean bl = compound.contains("palettes", NbtTypeIds.LIST);
        NbtList nbtList = bl ? compound.getList("palettes", NbtTypeIds.LIST).getList(0) : compound.getList("palette", NbtTypeIds.COMPOUND);
        NbtList nbtList2 = nbtList.stream().map(NbtCompound.class::cast).map(NbtHelper::method_32277).map(NbtString::of).collect(Collectors.toCollection(NbtList::new));
        compound.put("palette", nbtList2);
        if (bl) {
            nbtList32 = new NbtList();
            nbtList4 = compound.getList("palettes", NbtTypeIds.LIST);
            nbtList4.stream().map(NbtList.class::cast).forEach(nbtList3 -> {
                NbtCompound nbtCompound = new NbtCompound();
                for (int i = 0; i < nbtList3.size(); ++i) {
                    nbtCompound.putString(nbtList2.getString(i), NbtHelper.method_32277(nbtList3.getCompound(i)));
                }
                nbtList32.add(nbtCompound);
            });
            compound.put("palettes", nbtList32);
        }
        if (compound.contains("entities", NbtTypeIds.COMPOUND)) {
            nbtList32 = compound.getList("entities", NbtTypeIds.COMPOUND);
            nbtList4 = nbtList32.stream().map(NbtCompound.class::cast).sorted(Comparator.comparing(nbtCompound -> nbtCompound.getList("pos", NbtTypeIds.DOUBLE), field_27817)).collect(Collectors.toCollection(NbtList::new));
            compound.put("entities", nbtList4);
        }
        nbtList32 = compound.getList("blocks", NbtTypeIds.COMPOUND).stream().map(NbtCompound.class::cast).sorted(Comparator.comparing(nbtCompound -> nbtCompound.getList("pos", NbtTypeIds.INT), field_27816)).peek(nbtCompound -> nbtCompound.putString("state", nbtList2.getString(nbtCompound.getInt("state")))).collect(Collectors.toCollection(NbtList::new));
        compound.put("data", nbtList32);
        compound.remove("blocks");
        return compound;
    }

    @VisibleForTesting
    static NbtCompound method_32275(NbtCompound compound) {
        NbtList nbtList = compound.getList("palette", NbtTypeIds.STRING);
        Map map = nbtList.stream().map(NbtString.class::cast).map(NbtString::asString).collect(ImmutableMap.toImmutableMap(Function.identity(), NbtHelper::method_32267));
        if (compound.contains("palettes", NbtTypeIds.LIST)) {
            compound.put("palettes", compound.getList("palettes", NbtTypeIds.COMPOUND).stream().map(NbtCompound.class::cast).map(nbtCompound -> map.keySet().stream().map(nbtCompound::getString).map(NbtHelper::method_32267).collect(Collectors.toCollection(NbtList::new))).collect(Collectors.toCollection(NbtList::new)));
            compound.remove("palette");
        } else {
            compound.put("palette", map.values().stream().collect(Collectors.toCollection(NbtList::new)));
        }
        if (compound.contains("data", NbtTypeIds.LIST)) {
            Object2IntOpenHashMap<String> object2IntMap = new Object2IntOpenHashMap<String>();
            object2IntMap.defaultReturnValue(-1);
            for (int i = 0; i < nbtList.size(); ++i) {
                object2IntMap.put(nbtList.getString(i), i);
            }
            NbtList nbtList2 = compound.getList("data", NbtTypeIds.COMPOUND);
            for (int j = 0; j < nbtList2.size(); ++j) {
                NbtCompound nbtCompound2 = nbtList2.getCompound(j);
                String string = nbtCompound2.getString("state");
                int k = object2IntMap.getInt(string);
                if (k == -1) {
                    throw new IllegalStateException("Entry " + string + " missing from palette");
                }
                nbtCompound2.putInt("state", k);
            }
            compound.put("blocks", nbtList2);
            compound.remove("data");
        }
        return compound;
    }

    @VisibleForTesting
    static String method_32277(NbtCompound compound) {
        StringBuilder stringBuilder = new StringBuilder(compound.getString("Name"));
        if (compound.contains("Properties", NbtTypeIds.COMPOUND)) {
            NbtCompound nbtCompound = compound.getCompound("Properties");
            String string2 = nbtCompound.getKeys().stream().sorted().map(string -> string + ':' + nbtCompound.get((String)string).asString()).collect(Collectors.joining(","));
            stringBuilder.append('{').append(string2).append('}');
        }
        return stringBuilder.toString();
    }

    @VisibleForTesting
    static NbtCompound method_32267(String string) {
        String string22;
        NbtCompound nbtCompound = new NbtCompound();
        int i = string.indexOf(123);
        if (i >= 0) {
            string22 = string.substring(0, i);
            NbtCompound nbtCompound2 = new NbtCompound();
            if (i + 2 <= string.length()) {
                String string3 = string.substring(i + 1, string.indexOf(125, i));
                COMMA_SPLITTER.split(string3).forEach(string2 -> {
                    List<String> list = field_27819.splitToList((CharSequence)string2);
                    if (list.size() == 2) {
                        nbtCompound2.putString(list.get(0), list.get(1));
                    } else {
                        LOGGER.error("Something went wrong parsing: '{}' -- incorrect gamedata!", (Object)string);
                    }
                });
                nbtCompound.put("Properties", nbtCompound2);
            }
        } else {
            string22 = string;
        }
        nbtCompound.putString("Name", string22);
        return nbtCompound;
    }
}

