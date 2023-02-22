/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.nbt;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import net.minecraft.nbt.AbstractNbtList;
import net.minecraft.nbt.AbstractNbtNumber;
import net.minecraft.nbt.NbtByte;
import net.minecraft.nbt.NbtByteArray;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtDouble;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtEnd;
import net.minecraft.nbt.NbtFloat;
import net.minecraft.nbt.NbtInt;
import net.minecraft.nbt.NbtIntArray;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtLong;
import net.minecraft.nbt.NbtLongArray;
import net.minecraft.nbt.NbtShort;
import net.minecraft.nbt.NbtString;
import org.jetbrains.annotations.Nullable;

/**
 * Used to handle Minecraft NBTs within {@link com.mojang.serialization.Dynamic
 * dynamics} for DataFixerUpper, allowing generalized serialization logic
 * shared across different type of data structures. Use {@link NbtOps#INSTANCE}
 * for the ops singleton.
 * 
 * <p>For instance, dimension data may be stored as JSON in data packs, but
 * they will be transported in packets as NBT. DataFixerUpper allows
 * generalizing the dimension serialization logic to prevent duplicate code,
 * where the NBT ops allow the DataFixerUpper dimension serialization logic
 * to interact with Minecraft NBTs.
 * 
 * @see NbtOps#INSTANCE
 */
public class NbtOps
implements DynamicOps<NbtElement> {
    /**
     * An singleton of the NBT dynamic ops.
     * 
     * <p>This ops does not compress maps (replace field name to value pairs
     * with an ordered list of values in serialization). In fact, since
     * Minecraft NBT lists can only contain elements of the same type, this op
     * cannot compress maps.
     */
    public static final NbtOps INSTANCE = new NbtOps();
    private static final String MARKER_KEY = "";

    protected NbtOps() {
    }

    @Override
    public NbtElement empty() {
        return NbtEnd.INSTANCE;
    }

    @Override
    public <U> U convertTo(DynamicOps<U> dynamicOps, NbtElement nbtElement) {
        switch (nbtElement.getType()) {
            case 0: {
                return dynamicOps.empty();
            }
            case 1: {
                return dynamicOps.createByte(((AbstractNbtNumber)nbtElement).byteValue());
            }
            case 2: {
                return dynamicOps.createShort(((AbstractNbtNumber)nbtElement).shortValue());
            }
            case 3: {
                return dynamicOps.createInt(((AbstractNbtNumber)nbtElement).intValue());
            }
            case 4: {
                return dynamicOps.createLong(((AbstractNbtNumber)nbtElement).longValue());
            }
            case 5: {
                return dynamicOps.createFloat(((AbstractNbtNumber)nbtElement).floatValue());
            }
            case 6: {
                return dynamicOps.createDouble(((AbstractNbtNumber)nbtElement).doubleValue());
            }
            case 7: {
                return dynamicOps.createByteList(ByteBuffer.wrap(((NbtByteArray)nbtElement).getByteArray()));
            }
            case 8: {
                return dynamicOps.createString(nbtElement.asString());
            }
            case 9: {
                return this.convertList(dynamicOps, nbtElement);
            }
            case 10: {
                return this.convertMap(dynamicOps, nbtElement);
            }
            case 11: {
                return dynamicOps.createIntList(Arrays.stream(((NbtIntArray)nbtElement).getIntArray()));
            }
            case 12: {
                return dynamicOps.createLongList(Arrays.stream(((NbtLongArray)nbtElement).getLongArray()));
            }
        }
        throw new IllegalStateException("Unknown tag type: " + nbtElement);
    }

    @Override
    public DataResult<Number> getNumberValue(NbtElement nbtElement) {
        if (nbtElement instanceof AbstractNbtNumber) {
            AbstractNbtNumber abstractNbtNumber = (AbstractNbtNumber)nbtElement;
            return DataResult.success(abstractNbtNumber.numberValue());
        }
        return DataResult.error(() -> "Not a number");
    }

    @Override
    public NbtElement createNumeric(Number number) {
        return NbtDouble.of(number.doubleValue());
    }

    @Override
    public NbtElement createByte(byte b) {
        return NbtByte.of(b);
    }

    @Override
    public NbtElement createShort(short s) {
        return NbtShort.of(s);
    }

    @Override
    public NbtElement createInt(int i) {
        return NbtInt.of(i);
    }

    @Override
    public NbtElement createLong(long l) {
        return NbtLong.of(l);
    }

    @Override
    public NbtElement createFloat(float f) {
        return NbtFloat.of(f);
    }

    @Override
    public NbtElement createDouble(double d) {
        return NbtDouble.of(d);
    }

    @Override
    public NbtElement createBoolean(boolean bl) {
        return NbtByte.of(bl);
    }

    @Override
    public DataResult<String> getStringValue(NbtElement nbtElement) {
        if (nbtElement instanceof NbtString) {
            NbtString nbtString = (NbtString)nbtElement;
            return DataResult.success(nbtString.asString());
        }
        return DataResult.error(() -> "Not a string");
    }

    @Override
    public NbtElement createString(String string) {
        return NbtString.of(string);
    }

    @Override
    public DataResult<NbtElement> mergeToList(NbtElement nbtElement, NbtElement nbtElement2) {
        return NbtOps.createMerger(nbtElement).map(merger -> DataResult.success(merger.merge(nbtElement2).getResult())).orElseGet(() -> DataResult.error(() -> "mergeToList called with not a list: " + nbtElement, nbtElement));
    }

    @Override
    public DataResult<NbtElement> mergeToList(NbtElement nbtElement, List<NbtElement> list) {
        return NbtOps.createMerger(nbtElement).map(merger -> DataResult.success(merger.merge(list).getResult())).orElseGet(() -> DataResult.error(() -> "mergeToList called with not a list: " + nbtElement, nbtElement));
    }

    @Override
    public DataResult<NbtElement> mergeToMap(NbtElement nbtElement, NbtElement nbtElement2, NbtElement nbtElement3) {
        if (!(nbtElement instanceof NbtCompound) && !(nbtElement instanceof NbtEnd)) {
            return DataResult.error(() -> "mergeToMap called with not a map: " + nbtElement, nbtElement);
        }
        if (!(nbtElement2 instanceof NbtString)) {
            return DataResult.error(() -> "key is not a string: " + nbtElement2, nbtElement);
        }
        NbtCompound nbtCompound = new NbtCompound();
        if (nbtElement instanceof NbtCompound) {
            NbtCompound nbtCompound2 = (NbtCompound)nbtElement;
            nbtCompound2.getKeys().forEach(key -> nbtCompound.put((String)key, nbtCompound2.get((String)key)));
        }
        nbtCompound.put(nbtElement2.asString(), nbtElement3);
        return DataResult.success(nbtCompound);
    }

    @Override
    public DataResult<NbtElement> mergeToMap(NbtElement nbtElement, MapLike<NbtElement> mapLike) {
        if (!(nbtElement instanceof NbtCompound) && !(nbtElement instanceof NbtEnd)) {
            return DataResult.error(() -> "mergeToMap called with not a map: " + nbtElement, nbtElement);
        }
        NbtCompound nbtCompound = new NbtCompound();
        if (nbtElement instanceof NbtCompound) {
            NbtCompound nbtCompound2 = (NbtCompound)nbtElement;
            nbtCompound2.getKeys().forEach(key -> nbtCompound.put((String)key, nbtCompound2.get((String)key)));
        }
        ArrayList list = Lists.newArrayList();
        mapLike.entries().forEach(pair -> {
            NbtElement nbtElement = (NbtElement)pair.getFirst();
            if (!(nbtElement instanceof NbtString)) {
                list.add(nbtElement);
                return;
            }
            nbtCompound.put(nbtElement.asString(), (NbtElement)pair.getSecond());
        });
        if (!list.isEmpty()) {
            return DataResult.error(() -> "some keys are not strings: " + list, nbtCompound);
        }
        return DataResult.success(nbtCompound);
    }

    @Override
    public DataResult<Stream<Pair<NbtElement, NbtElement>>> getMapValues(NbtElement nbtElement) {
        if (nbtElement instanceof NbtCompound) {
            NbtCompound nbtCompound = (NbtCompound)nbtElement;
            return DataResult.success(nbtCompound.getKeys().stream().map(key -> Pair.of(this.createString((String)key), nbtCompound.get((String)key))));
        }
        return DataResult.error(() -> "Not a map: " + nbtElement);
    }

    @Override
    public DataResult<Consumer<BiConsumer<NbtElement, NbtElement>>> getMapEntries(NbtElement nbtElement) {
        if (nbtElement instanceof NbtCompound) {
            NbtCompound nbtCompound = (NbtCompound)nbtElement;
            return DataResult.success(entryConsumer -> nbtCompound.getKeys().forEach(key -> entryConsumer.accept(this.createString((String)key), nbtCompound.get((String)key))));
        }
        return DataResult.error(() -> "Not a map: " + nbtElement);
    }

    @Override
    public DataResult<MapLike<NbtElement>> getMap(NbtElement nbtElement) {
        if (nbtElement instanceof NbtCompound) {
            final NbtCompound nbtCompound = (NbtCompound)nbtElement;
            return DataResult.success(new MapLike<NbtElement>(){

                @Override
                @Nullable
                public NbtElement get(NbtElement nbtElement) {
                    return nbtCompound.get(nbtElement.asString());
                }

                @Override
                @Nullable
                public NbtElement get(String string) {
                    return nbtCompound.get(string);
                }

                @Override
                public Stream<Pair<NbtElement, NbtElement>> entries() {
                    return nbtCompound.getKeys().stream().map(key -> Pair.of(NbtOps.this.createString((String)key), nbtCompound.get((String)key)));
                }

                public String toString() {
                    return "MapLike[" + nbtCompound + "]";
                }

                @Override
                @Nullable
                public /* synthetic */ Object get(String key) {
                    return this.get(key);
                }

                @Override
                @Nullable
                public /* synthetic */ Object get(Object nbt) {
                    return this.get((NbtElement)nbt);
                }
            });
        }
        return DataResult.error(() -> "Not a map: " + nbtElement);
    }

    @Override
    public NbtElement createMap(Stream<Pair<NbtElement, NbtElement>> stream) {
        NbtCompound nbtCompound = new NbtCompound();
        stream.forEach(entry -> nbtCompound.put(((NbtElement)entry.getFirst()).asString(), (NbtElement)entry.getSecond()));
        return nbtCompound;
    }

    private static NbtElement unpackMarker(NbtCompound nbt) {
        NbtElement nbtElement;
        if (nbt.getSize() == 1 && (nbtElement = nbt.get(MARKER_KEY)) != null) {
            return nbtElement;
        }
        return nbt;
    }

    @Override
    public DataResult<Stream<NbtElement>> getStream(NbtElement nbtElement) {
        if (nbtElement instanceof NbtList) {
            NbtList nbtList = (NbtList)nbtElement;
            if (nbtList.getHeldType() == NbtElement.COMPOUND_TYPE) {
                return DataResult.success(nbtList.stream().map(nbt -> NbtOps.unpackMarker((NbtCompound)nbt)));
            }
            return DataResult.success(nbtList.stream());
        }
        if (nbtElement instanceof AbstractNbtList) {
            AbstractNbtList abstractNbtList = (AbstractNbtList)nbtElement;
            return DataResult.success(abstractNbtList.stream().map(nbt -> nbt));
        }
        return DataResult.error(() -> "Not a list");
    }

    @Override
    public DataResult<Consumer<Consumer<NbtElement>>> getList(NbtElement nbtElement) {
        if (nbtElement instanceof NbtList) {
            NbtList nbtList = (NbtList)nbtElement;
            if (nbtList.getHeldType() == NbtElement.COMPOUND_TYPE) {
                return DataResult.success(consumer -> nbtList.forEach(nbt -> consumer.accept(NbtOps.unpackMarker((NbtCompound)nbt))));
            }
            return DataResult.success(nbtList::forEach);
        }
        if (nbtElement instanceof AbstractNbtList) {
            AbstractNbtList abstractNbtList = (AbstractNbtList)nbtElement;
            return DataResult.success(abstractNbtList::forEach);
        }
        return DataResult.error(() -> "Not a list: " + nbtElement);
    }

    @Override
    public DataResult<ByteBuffer> getByteBuffer(NbtElement nbtElement) {
        if (nbtElement instanceof NbtByteArray) {
            NbtByteArray nbtByteArray = (NbtByteArray)nbtElement;
            return DataResult.success(ByteBuffer.wrap(nbtByteArray.getByteArray()));
        }
        return DynamicOps.super.getByteBuffer(nbtElement);
    }

    @Override
    public NbtElement createByteList(ByteBuffer byteBuffer) {
        return new NbtByteArray(DataFixUtils.toArray(byteBuffer));
    }

    @Override
    public DataResult<IntStream> getIntStream(NbtElement nbtElement) {
        if (nbtElement instanceof NbtIntArray) {
            NbtIntArray nbtIntArray = (NbtIntArray)nbtElement;
            return DataResult.success(Arrays.stream(nbtIntArray.getIntArray()));
        }
        return DynamicOps.super.getIntStream(nbtElement);
    }

    @Override
    public NbtElement createIntList(IntStream intStream) {
        return new NbtIntArray(intStream.toArray());
    }

    @Override
    public DataResult<LongStream> getLongStream(NbtElement nbtElement) {
        if (nbtElement instanceof NbtLongArray) {
            NbtLongArray nbtLongArray = (NbtLongArray)nbtElement;
            return DataResult.success(Arrays.stream(nbtLongArray.getLongArray()));
        }
        return DynamicOps.super.getLongStream(nbtElement);
    }

    @Override
    public NbtElement createLongList(LongStream longStream) {
        return new NbtLongArray(longStream.toArray());
    }

    @Override
    public NbtElement createList(Stream<NbtElement> stream) {
        return BasicMerger.EMPTY.merge(stream).getResult();
    }

    @Override
    public NbtElement remove(NbtElement nbtElement, String string) {
        if (nbtElement instanceof NbtCompound) {
            NbtCompound nbtCompound = (NbtCompound)nbtElement;
            NbtCompound nbtCompound2 = new NbtCompound();
            nbtCompound.getKeys().stream().filter(k -> !Objects.equals(k, string)).forEach(k -> nbtCompound2.put((String)k, nbtCompound.get((String)k)));
            return nbtCompound2;
        }
        return nbtElement;
    }

    public String toString() {
        return "NBT";
    }

    @Override
    public RecordBuilder<NbtElement> mapBuilder() {
        return new MapBuilder();
    }

    private static Optional<Merger> createMerger(NbtElement nbt) {
        if (nbt instanceof NbtEnd) {
            return Optional.of(BasicMerger.EMPTY);
        }
        if (nbt instanceof AbstractNbtList) {
            AbstractNbtList abstractNbtList = (AbstractNbtList)nbt;
            if (abstractNbtList.isEmpty()) {
                return Optional.of(BasicMerger.EMPTY);
            }
            if (abstractNbtList instanceof NbtList) {
                NbtList nbtList = (NbtList)abstractNbtList;
                return switch (nbtList.getHeldType()) {
                    case 0 -> Optional.of(BasicMerger.EMPTY);
                    case 10 -> Optional.of(new CompoundListMerger(nbtList));
                    default -> Optional.of(new ListMerger(nbtList));
                };
            }
            if (abstractNbtList instanceof NbtByteArray) {
                NbtByteArray nbtByteArray = (NbtByteArray)abstractNbtList;
                return Optional.of(new ByteArrayMerger(nbtByteArray.getByteArray()));
            }
            if (abstractNbtList instanceof NbtIntArray) {
                NbtIntArray nbtIntArray = (NbtIntArray)abstractNbtList;
                return Optional.of(new IntArrayMerger(nbtIntArray.getIntArray()));
            }
            if (abstractNbtList instanceof NbtLongArray) {
                NbtLongArray nbtLongArray = (NbtLongArray)abstractNbtList;
                return Optional.of(new LongArrayMerger(nbtLongArray.getLongArray()));
            }
        }
        return Optional.empty();
    }

    @Override
    public /* synthetic */ Object remove(Object element, String key) {
        return this.remove((NbtElement)element, key);
    }

    @Override
    public /* synthetic */ Object createLongList(LongStream stream) {
        return this.createLongList(stream);
    }

    @Override
    public /* synthetic */ DataResult getLongStream(Object element) {
        return this.getLongStream((NbtElement)element);
    }

    @Override
    public /* synthetic */ Object createIntList(IntStream stream) {
        return this.createIntList(stream);
    }

    @Override
    public /* synthetic */ DataResult getIntStream(Object element) {
        return this.getIntStream((NbtElement)element);
    }

    @Override
    public /* synthetic */ Object createByteList(ByteBuffer buf) {
        return this.createByteList(buf);
    }

    @Override
    public /* synthetic */ DataResult getByteBuffer(Object element) {
        return this.getByteBuffer((NbtElement)element);
    }

    @Override
    public /* synthetic */ Object createList(Stream stream) {
        return this.createList(stream);
    }

    @Override
    public /* synthetic */ DataResult getList(Object element) {
        return this.getList((NbtElement)element);
    }

    @Override
    public /* synthetic */ DataResult getStream(Object element) {
        return this.getStream((NbtElement)element);
    }

    @Override
    public /* synthetic */ DataResult getMap(Object element) {
        return this.getMap((NbtElement)element);
    }

    @Override
    public /* synthetic */ Object createMap(Stream entries) {
        return this.createMap(entries);
    }

    @Override
    public /* synthetic */ DataResult getMapEntries(Object element) {
        return this.getMapEntries((NbtElement)element);
    }

    @Override
    public /* synthetic */ DataResult getMapValues(Object element) {
        return this.getMapValues((NbtElement)element);
    }

    @Override
    public /* synthetic */ DataResult mergeToMap(Object element, MapLike map) {
        return this.mergeToMap((NbtElement)element, (MapLike<NbtElement>)map);
    }

    @Override
    public /* synthetic */ DataResult mergeToMap(Object map, Object key, Object value) {
        return this.mergeToMap((NbtElement)map, (NbtElement)key, (NbtElement)value);
    }

    @Override
    public /* synthetic */ DataResult mergeToList(Object list, List values) {
        return this.mergeToList((NbtElement)list, (List<NbtElement>)values);
    }

    @Override
    public /* synthetic */ DataResult mergeToList(Object list, Object value) {
        return this.mergeToList((NbtElement)list, (NbtElement)value);
    }

    @Override
    public /* synthetic */ Object createString(String string) {
        return this.createString(string);
    }

    @Override
    public /* synthetic */ DataResult getStringValue(Object element) {
        return this.getStringValue((NbtElement)element);
    }

    @Override
    public /* synthetic */ Object createBoolean(boolean value) {
        return this.createBoolean(value);
    }

    @Override
    public /* synthetic */ Object createDouble(double value) {
        return this.createDouble(value);
    }

    @Override
    public /* synthetic */ Object createFloat(float value) {
        return this.createFloat(value);
    }

    @Override
    public /* synthetic */ Object createLong(long value) {
        return this.createLong(value);
    }

    @Override
    public /* synthetic */ Object createInt(int value) {
        return this.createInt(value);
    }

    @Override
    public /* synthetic */ Object createShort(short value) {
        return this.createShort(value);
    }

    @Override
    public /* synthetic */ Object createByte(byte value) {
        return this.createByte(value);
    }

    @Override
    public /* synthetic */ Object createNumeric(Number value) {
        return this.createNumeric(value);
    }

    @Override
    public /* synthetic */ DataResult getNumberValue(Object element) {
        return this.getNumberValue((NbtElement)element);
    }

    @Override
    public /* synthetic */ Object convertTo(DynamicOps ops, Object element) {
        return this.convertTo(ops, (NbtElement)element);
    }

    @Override
    public /* synthetic */ Object empty() {
        return this.empty();
    }

    static class BasicMerger
    implements Merger {
        public static final BasicMerger EMPTY = new BasicMerger();

        private BasicMerger() {
        }

        @Override
        public Merger merge(NbtElement nbt) {
            if (nbt instanceof NbtCompound) {
                NbtCompound nbtCompound = (NbtCompound)nbt;
                return new CompoundListMerger().merge(nbtCompound);
            }
            if (nbt instanceof NbtByte) {
                NbtByte nbtByte = (NbtByte)nbt;
                return new ByteArrayMerger(nbtByte.byteValue());
            }
            if (nbt instanceof NbtInt) {
                NbtInt nbtInt = (NbtInt)nbt;
                return new IntArrayMerger(nbtInt.intValue());
            }
            if (nbt instanceof NbtLong) {
                NbtLong nbtLong = (NbtLong)nbt;
                return new LongArrayMerger(nbtLong.longValue());
            }
            return new ListMerger(nbt);
        }

        @Override
        public NbtElement getResult() {
            return new NbtList();
        }
    }

    static interface Merger {
        public Merger merge(NbtElement var1);

        default public Merger merge(Iterable<NbtElement> nbts) {
            Merger merger = this;
            for (NbtElement nbtElement : nbts) {
                merger = merger.merge(nbtElement);
            }
            return merger;
        }

        default public Merger merge(Stream<NbtElement> nbts) {
            return this.merge(nbts::iterator);
        }

        public NbtElement getResult();
    }

    class MapBuilder
    extends RecordBuilder.AbstractStringBuilder<NbtElement, NbtCompound> {
        protected MapBuilder() {
            super(NbtOps.this);
        }

        @Override
        protected NbtCompound initBuilder() {
            return new NbtCompound();
        }

        @Override
        protected NbtCompound append(String string, NbtElement nbtElement, NbtCompound nbtCompound) {
            nbtCompound.put(string, nbtElement);
            return nbtCompound;
        }

        @Override
        protected DataResult<NbtElement> build(NbtCompound nbtCompound, NbtElement nbtElement) {
            if (nbtElement == null || nbtElement == NbtEnd.INSTANCE) {
                return DataResult.success(nbtCompound);
            }
            if (nbtElement instanceof NbtCompound) {
                NbtCompound nbtCompound2 = (NbtCompound)nbtElement;
                NbtCompound nbtCompound3 = new NbtCompound(Maps.newHashMap(nbtCompound2.toMap()));
                for (Map.Entry<String, NbtElement> entry : nbtCompound.toMap().entrySet()) {
                    nbtCompound3.put(entry.getKey(), entry.getValue());
                }
                return DataResult.success(nbtCompound3);
            }
            return DataResult.error(() -> "mergeToMap called with not a map: " + nbtElement, nbtElement);
        }

        @Override
        protected /* synthetic */ Object append(String key, Object value, Object nbt) {
            return this.append(key, (NbtElement)value, (NbtCompound)nbt);
        }

        @Override
        protected /* synthetic */ DataResult build(Object nbt, Object mergedValue) {
            return this.build((NbtCompound)nbt, (NbtElement)mergedValue);
        }

        @Override
        protected /* synthetic */ Object initBuilder() {
            return this.initBuilder();
        }
    }

    static class CompoundListMerger
    implements Merger {
        private final NbtList list = new NbtList();

        public CompoundListMerger() {
        }

        public CompoundListMerger(Collection<NbtElement> nbts) {
            this.list.addAll(nbts);
        }

        public CompoundListMerger(IntArrayList list) {
            list.forEach(value -> this.list.add(CompoundListMerger.createMarkerNbt(NbtInt.of(value))));
        }

        public CompoundListMerger(ByteArrayList list) {
            list.forEach(value -> this.list.add(CompoundListMerger.createMarkerNbt(NbtByte.of(value))));
        }

        public CompoundListMerger(LongArrayList list) {
            list.forEach(value -> this.list.add(CompoundListMerger.createMarkerNbt(NbtLong.of(value))));
        }

        private static boolean isMarker(NbtCompound nbt) {
            return nbt.getSize() == 1 && nbt.contains(NbtOps.MARKER_KEY);
        }

        private static NbtElement makeMarker(NbtElement value) {
            NbtCompound nbtCompound;
            if (value instanceof NbtCompound && !CompoundListMerger.isMarker(nbtCompound = (NbtCompound)value)) {
                return nbtCompound;
            }
            return CompoundListMerger.createMarkerNbt(value);
        }

        private static NbtCompound createMarkerNbt(NbtElement value) {
            NbtCompound nbtCompound = new NbtCompound();
            nbtCompound.put(NbtOps.MARKER_KEY, value);
            return nbtCompound;
        }

        @Override
        public Merger merge(NbtElement nbt) {
            this.list.add(CompoundListMerger.makeMarker(nbt));
            return this;
        }

        @Override
        public NbtElement getResult() {
            return this.list;
        }
    }

    static class ListMerger
    implements Merger {
        private final NbtList list = new NbtList();

        ListMerger(NbtElement nbt) {
            this.list.add(nbt);
        }

        ListMerger(NbtList nbt) {
            this.list.addAll(nbt);
        }

        @Override
        public Merger merge(NbtElement nbt) {
            if (nbt.getType() != this.list.getHeldType()) {
                return new CompoundListMerger().merge(this.list).merge(nbt);
            }
            this.list.add(nbt);
            return this;
        }

        @Override
        public NbtElement getResult() {
            return this.list;
        }
    }

    static class ByteArrayMerger
    implements Merger {
        private final ByteArrayList list = new ByteArrayList();

        public ByteArrayMerger(byte value) {
            this.list.add(value);
        }

        public ByteArrayMerger(byte[] values) {
            this.list.addElements(0, values);
        }

        @Override
        public Merger merge(NbtElement nbt) {
            if (nbt instanceof NbtByte) {
                NbtByte nbtByte = (NbtByte)nbt;
                this.list.add(nbtByte.byteValue());
                return this;
            }
            return new CompoundListMerger(this.list).merge(nbt);
        }

        @Override
        public NbtElement getResult() {
            return new NbtByteArray(this.list.toByteArray());
        }
    }

    static class IntArrayMerger
    implements Merger {
        private final IntArrayList list = new IntArrayList();

        public IntArrayMerger(int value) {
            this.list.add(value);
        }

        public IntArrayMerger(int[] values) {
            this.list.addElements(0, values);
        }

        @Override
        public Merger merge(NbtElement nbt) {
            if (nbt instanceof NbtInt) {
                NbtInt nbtInt = (NbtInt)nbt;
                this.list.add(nbtInt.intValue());
                return this;
            }
            return new CompoundListMerger(this.list).merge(nbt);
        }

        @Override
        public NbtElement getResult() {
            return new NbtIntArray(this.list.toIntArray());
        }
    }

    static class LongArrayMerger
    implements Merger {
        private final LongArrayList list = new LongArrayList();

        public LongArrayMerger(long value) {
            this.list.add(value);
        }

        public LongArrayMerger(long[] values) {
            this.list.addElements(0, values);
        }

        @Override
        public Merger merge(NbtElement nbt) {
            if (nbt instanceof NbtLong) {
                NbtLong nbtLong = (NbtLong)nbt;
                this.list.add(nbtLong.longValue());
                return this;
            }
            return new CompoundListMerger(this.list).merge(nbt);
        }

        @Override
        public NbtElement getResult() {
            return new NbtLongArray(this.list.toLongArray());
        }
    }
}

