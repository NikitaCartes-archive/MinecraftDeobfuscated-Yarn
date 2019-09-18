/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.loot.context;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.class_4570;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.world.loot.LootSupplier;
import net.minecraft.world.loot.context.LootContextParameter;
import net.minecraft.world.loot.context.LootContextParameters;
import net.minecraft.world.loot.context.LootContextType;
import org.jetbrains.annotations.Nullable;

public class LootContext {
    private final Random random;
    private final float luck;
    private final ServerWorld world;
    private final Function<Identifier, LootSupplier> manager;
    private final Set<LootSupplier> suppliers = Sets.newLinkedHashSet();
    private final Function<Identifier, class_4570> field_20750;
    private final Set<class_4570> field_20751 = Sets.newLinkedHashSet();
    private final Map<LootContextParameter<?>, Object> parameters;
    private final Map<Identifier, Dropper> drops;

    private LootContext(Random random, float f, ServerWorld serverWorld, Function<Identifier, LootSupplier> function, Function<Identifier, class_4570> function2, Map<LootContextParameter<?>, Object> map, Map<Identifier, Dropper> map2) {
        this.random = random;
        this.luck = f;
        this.world = serverWorld;
        this.manager = function;
        this.field_20750 = function2;
        this.parameters = ImmutableMap.copyOf(map);
        this.drops = ImmutableMap.copyOf(map2);
    }

    public boolean hasParameter(LootContextParameter<?> lootContextParameter) {
        return this.parameters.containsKey(lootContextParameter);
    }

    public void drop(Identifier identifier, Consumer<ItemStack> consumer) {
        Dropper dropper = this.drops.get(identifier);
        if (dropper != null) {
            dropper.add(this, consumer);
        }
    }

    @Nullable
    public <T> T get(LootContextParameter<T> lootContextParameter) {
        return (T)this.parameters.get(lootContextParameter);
    }

    public boolean addDrop(LootSupplier lootSupplier) {
        return this.suppliers.add(lootSupplier);
    }

    public void removeDrop(LootSupplier lootSupplier) {
        this.suppliers.remove(lootSupplier);
    }

    public boolean method_22555(class_4570 arg) {
        return this.field_20751.add(arg);
    }

    public void method_22557(class_4570 arg) {
        this.field_20751.remove(arg);
    }

    public LootSupplier method_22556(Identifier identifier) {
        return this.manager.apply(identifier);
    }

    public class_4570 method_22558(Identifier identifier) {
        return this.field_20750.apply(identifier);
    }

    public Random getRandom() {
        return this.random;
    }

    public float getLuck() {
        return this.luck;
    }

    public ServerWorld getWorld() {
        return this.world;
    }

    public static enum EntityTarget {
        THIS("this", LootContextParameters.THIS_ENTITY),
        KILLER("killer", LootContextParameters.KILLER_ENTITY),
        DIRECT_KILLER("direct_killer", LootContextParameters.DIRECT_KILLER_ENTITY),
        KILLER_PLAYER("killer_player", LootContextParameters.LAST_DAMAGE_PLAYER);

        private final String type;
        private final LootContextParameter<? extends Entity> identifier;

        private EntityTarget(String string2, LootContextParameter<? extends Entity> lootContextParameter) {
            this.type = string2;
            this.identifier = lootContextParameter;
        }

        public LootContextParameter<? extends Entity> getIdentifier() {
            return this.identifier;
        }

        public static EntityTarget fromString(String string) {
            for (EntityTarget entityTarget : EntityTarget.values()) {
                if (!entityTarget.type.equals(string)) continue;
                return entityTarget;
            }
            throw new IllegalArgumentException("Invalid entity target " + string);
        }

        public static class Serializer
        extends TypeAdapter<EntityTarget> {
            public void method_318(JsonWriter jsonWriter, EntityTarget entityTarget) throws IOException {
                jsonWriter.value(entityTarget.type);
            }

            public EntityTarget method_317(JsonReader jsonReader) throws IOException {
                return EntityTarget.fromString(jsonReader.nextString());
            }

            @Override
            public /* synthetic */ Object read(JsonReader jsonReader) throws IOException {
                return this.method_317(jsonReader);
            }

            @Override
            public /* synthetic */ void write(JsonWriter jsonWriter, Object object) throws IOException {
                this.method_318(jsonWriter, (EntityTarget)((Object)object));
            }
        }
    }

    public static class Builder {
        private final ServerWorld world;
        private final Map<LootContextParameter<?>, Object> parameters = Maps.newIdentityHashMap();
        private final Map<Identifier, Dropper> drops = Maps.newHashMap();
        private Random random;
        private float luck;

        public Builder(ServerWorld serverWorld) {
            this.world = serverWorld;
        }

        public Builder setRandom(Random random) {
            this.random = random;
            return this;
        }

        public Builder setRandom(long l) {
            if (l != 0L) {
                this.random = new Random(l);
            }
            return this;
        }

        public Builder setRandom(long l, Random random) {
            this.random = l == 0L ? random : new Random(l);
            return this;
        }

        public Builder setLuck(float f) {
            this.luck = f;
            return this;
        }

        public <T> Builder put(LootContextParameter<T> lootContextParameter, T object) {
            this.parameters.put(lootContextParameter, object);
            return this;
        }

        public <T> Builder putNullable(LootContextParameter<T> lootContextParameter, @Nullable T object) {
            if (object == null) {
                this.parameters.remove(lootContextParameter);
            } else {
                this.parameters.put(lootContextParameter, object);
            }
            return this;
        }

        public Builder putDrop(Identifier identifier, Dropper dropper) {
            Dropper dropper2 = this.drops.put(identifier, dropper);
            if (dropper2 != null) {
                throw new IllegalStateException("Duplicated dynamic drop '" + this.drops + "'");
            }
            return this;
        }

        public ServerWorld getWorld() {
            return this.world;
        }

        public <T> T get(LootContextParameter<T> lootContextParameter) {
            Object object = this.parameters.get(lootContextParameter);
            if (object == null) {
                throw new IllegalArgumentException("No parameter " + lootContextParameter);
            }
            return (T)object;
        }

        @Nullable
        public <T> T getNullable(LootContextParameter<T> lootContextParameter) {
            return (T)this.parameters.get(lootContextParameter);
        }

        public LootContext build(LootContextType lootContextType) {
            Sets.SetView<LootContextParameter<?>> set = Sets.difference(this.parameters.keySet(), lootContextType.getAllowed());
            if (!set.isEmpty()) {
                throw new IllegalArgumentException("Parameters not allowed in this parameter set: " + set);
            }
            Sets.SetView<LootContextParameter<?>> set2 = Sets.difference(lootContextType.getRequired(), this.parameters.keySet());
            if (!set2.isEmpty()) {
                throw new IllegalArgumentException("Missing required parameters: " + set2);
            }
            Random random = this.random;
            if (random == null) {
                random = new Random();
            }
            MinecraftServer minecraftServer = this.world.getServer();
            return new LootContext(random, this.luck, this.world, minecraftServer.getLootManager()::getSupplier, minecraftServer.method_22828()::method_22564, this.parameters, this.drops);
        }
    }

    @FunctionalInterface
    public static interface Dropper {
        public void add(LootContext var1, Consumer<ItemStack> var2);
    }
}

