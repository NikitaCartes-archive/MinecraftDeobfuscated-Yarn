/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.dynamic;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenCustomHashMap;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.Collectors;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.dynamic.ForwardingDynamicOps;
import net.minecraft.util.dynamic.RegistryReadingOps;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RegistryOps<T>
extends ForwardingDynamicOps<T> {
    private static final Logger LOGGER = LogManager.getLogger();
    private final class_5506 field_26738;
    private final DynamicRegistryManager.Impl registryManager;
    private final Map<RegistryKey<? extends Registry<?>>, ValueHolder<?>> valueHolders;
    private final RegistryOps<JsonElement> field_26739;

    public static <T> RegistryOps<T> of(DynamicOps<T> delegate, ResourceManager resourceManager, DynamicRegistryManager.Impl impl) {
        return RegistryOps.method_31150(delegate, class_5506.method_31154(resourceManager), impl);
    }

    public static <T> RegistryOps<T> method_31150(DynamicOps<T> dynamicOps, class_5506 arg, DynamicRegistryManager.Impl impl) {
        RegistryOps<T> registryOps = new RegistryOps<T>(dynamicOps, arg, impl, Maps.newIdentityHashMap());
        DynamicRegistryManager.load(impl, registryOps);
        return registryOps;
    }

    private RegistryOps(DynamicOps<T> delegate, class_5506 arg, DynamicRegistryManager.Impl impl, IdentityHashMap<RegistryKey<? extends Registry<?>>, ValueHolder<?>> identityHashMap) {
        super(delegate);
        this.field_26738 = arg;
        this.registryManager = impl;
        this.valueHolders = identityHashMap;
        this.field_26739 = delegate == JsonOps.INSTANCE ? this : new RegistryOps<JsonElement>(JsonOps.INSTANCE, arg, impl, identityHashMap);
    }

    /**
     * Encode an id for a registry element than a full object if possible.
     * 
     * <p>This method is called by casting an arbitrary dynamic ops to a registry
     * reading ops.</p>
     * 
     * @see RegistryReadingOps#encodeOrId(Object, Object, RegistryKey, MapCodec)
     */
    protected <E> DataResult<Pair<java.util.function.Supplier<E>, T>> decodeOrId(T object, RegistryKey<? extends Registry<E>> registryKey, Codec<E> codec, boolean bl) {
        Optional optional = this.registryManager.getOptional(registryKey);
        if (!optional.isPresent()) {
            return DataResult.error("Unknown registry: " + registryKey);
        }
        MutableRegistry mutableRegistry = optional.get();
        DataResult dataResult = Identifier.CODEC.decode(this.delegate, object);
        if (!dataResult.result().isPresent()) {
            if (!bl) {
                return DataResult.error("Inline definitions not allowed here");
            }
            return codec.decode(this, object).map(pair -> pair.mapFirst(object -> () -> object));
        }
        Pair pair2 = dataResult.result().get();
        Identifier identifier = (Identifier)pair2.getFirst();
        return this.readSupplier(registryKey, mutableRegistry, codec, identifier).map(supplier -> Pair.of(supplier, pair2.getSecond()));
    }

    /**
     * Loads elements into a registry just loaded from a decoder.
     */
    public <E> DataResult<SimpleRegistry<E>> loadToRegistry(SimpleRegistry<E> registry, RegistryKey<? extends Registry<E>> registryKey, Codec<E> codec) {
        Collection<Identifier> collection = this.field_26738.method_31156(registryKey);
        DataResult<SimpleRegistry<Object>> dataResult = DataResult.success(registry, Lifecycle.stable());
        String string = registryKey.getValue().getPath() + "/";
        for (Identifier identifier : collection) {
            String string2 = identifier.getPath();
            if (!string2.endsWith(".json")) {
                LOGGER.warn("Skipping resource {} since it is not a json file", (Object)identifier);
                continue;
            }
            if (!string2.startsWith(string)) {
                LOGGER.warn("Skipping resource {} since it does not have a registry name prefix", (Object)identifier);
                continue;
            }
            String string3 = string2.substring(string.length(), string2.length() - ".json".length());
            Identifier identifier2 = new Identifier(identifier.getNamespace(), string3);
            dataResult = dataResult.flatMap(simpleRegistry -> this.readSupplier(registryKey, (MutableRegistry)simpleRegistry, codec, identifier2).map(supplier -> simpleRegistry));
        }
        return dataResult.setPartial(registry);
    }

    /**
     * Reads a supplier for a registry element.
     * 
     * <p>This logic is used by both {@code decodeOrId} and {@code loadToRegistry}.</p>
     */
    private <E> DataResult<java.util.function.Supplier<E>> readSupplier(RegistryKey<? extends Registry<E>> registryKey, MutableRegistry<E> mutableRegistry, Codec<E> codec, Identifier elementId) {
        RegistryKey registryKey2 = RegistryKey.of(registryKey, elementId);
        ValueHolder<E> valueHolder = this.getValueHolder(registryKey);
        DataResult dataResult = (DataResult)((ValueHolder)valueHolder).values.get(registryKey2);
        if (dataResult != null) {
            return dataResult;
        }
        Supplier<Object> supplier = Suppliers.memoize(() -> {
            Object object = mutableRegistry.get(registryKey2);
            if (object == null) {
                throw new RuntimeException("Error during recursive registry parsing, element resolved too early: " + registryKey2);
            }
            return object;
        });
        ((ValueHolder)valueHolder).values.put(registryKey2, DataResult.success(supplier));
        DataResult<Pair<java.util.function.Supplier, OptionalInt>> dataResult2 = this.field_26738.method_31155(this.field_26739, registryKey, registryKey2, codec);
        Optional optional = dataResult2.result();
        if (optional.isPresent()) {
            Pair pair2 = optional.get();
            mutableRegistry.method_31062(pair2.getSecond(), registryKey2, pair2.getFirst(), dataResult2.lifecycle());
        }
        DataResult<java.util.function.Supplier<Object>> dataResult3 = !optional.isPresent() && mutableRegistry.get(registryKey2) != null ? DataResult.success(() -> mutableRegistry.get(registryKey2), Lifecycle.stable()) : dataResult2.map(pair -> () -> mutableRegistry.get(registryKey2));
        ((ValueHolder)valueHolder).values.put(registryKey2, dataResult3);
        return dataResult3;
    }

    private <E> ValueHolder<E> getValueHolder(RegistryKey<? extends Registry<E>> registryRef) {
        return this.valueHolders.computeIfAbsent(registryRef, registryKey -> new ValueHolder());
    }

    protected <E> DataResult<Registry<E>> method_31152(RegistryKey<? extends Registry<E>> registryKey) {
        return this.registryManager.getOptional(registryKey).map(mutableRegistry -> DataResult.success(mutableRegistry, mutableRegistry.method_31138())).orElseGet(() -> DataResult.error("Unknown registry: " + registryKey));
    }

    public static interface class_5506 {
        public Collection<Identifier> method_31156(RegistryKey<? extends Registry<?>> var1);

        public <E> DataResult<Pair<E, OptionalInt>> method_31155(DynamicOps<JsonElement> var1, RegistryKey<? extends Registry<E>> var2, RegistryKey<E> var3, Decoder<E> var4);

        public static class_5506 method_31154(final ResourceManager resourceManager) {
            return new class_5506(){

                @Override
                public Collection<Identifier> method_31156(RegistryKey<? extends Registry<?>> registryKey) {
                    return resourceManager.findResources(registryKey.getValue().getPath(), string -> string.endsWith(".json"));
                }

                /*
                 * Exception decompiling
                 */
                @Override
                public <E> DataResult<Pair<E, OptionalInt>> method_31155(DynamicOps<JsonElement> dynamicOps, RegistryKey<? extends Registry<E>> registryKey, RegistryKey<E> registryKey2, Decoder<E> decoder) {
                    /*
                     * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
                     * 
                     * org.benf.cfr.reader.util.ConfusedCFRException: Started 2 blocks at once
                     *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.getStartingBlocks(Op04StructuredStatement.java:412)
                     *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:487)
                     *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:736)
                     *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:850)
                     *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
                     *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
                     *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
                     *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:538)
                     *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
                     *     at org.benf.cfr.reader.entities.ClassFile.analyseInnerClassesPass1(ClassFile.java:923)
                     *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1035)
                     *     at org.benf.cfr.reader.entities.ClassFile.analyseInnerClassesPass1(ClassFile.java:923)
                     *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1035)
                     *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
                     *     at org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:261)
                     *     at org.benf.cfr.reader.Driver.doJar(Driver.java:143)
                     *     at net.fabricmc.loom.decompilers.cfr.LoomCFRDecompiler.decompile(LoomCFRDecompiler.java:89)
                     *     at net.fabricmc.loom.task.GenerateSourcesTask$DecompileAction.doDecompile(GenerateSourcesTask.java:269)
                     *     at net.fabricmc.loom.task.GenerateSourcesTask$DecompileAction.execute(GenerateSourcesTask.java:234)
                     *     at org.gradle.workers.internal.DefaultWorkerServer.execute(DefaultWorkerServer.java:63)
                     *     at org.gradle.workers.internal.AbstractClassLoaderWorker$1.create(AbstractClassLoaderWorker.java:49)
                     *     at org.gradle.workers.internal.AbstractClassLoaderWorker$1.create(AbstractClassLoaderWorker.java:43)
                     *     at org.gradle.internal.classloader.ClassLoaderUtils.executeInClassloader(ClassLoaderUtils.java:100)
                     *     at org.gradle.workers.internal.AbstractClassLoaderWorker.executeInClassLoader(AbstractClassLoaderWorker.java:43)
                     *     at org.gradle.workers.internal.IsolatedClassloaderWorker.run(IsolatedClassloaderWorker.java:49)
                     *     at org.gradle.workers.internal.IsolatedClassloaderWorker.run(IsolatedClassloaderWorker.java:30)
                     *     at org.gradle.workers.internal.WorkerDaemonServer.run(WorkerDaemonServer.java:87)
                     *     at org.gradle.workers.internal.WorkerDaemonServer.run(WorkerDaemonServer.java:56)
                     *     at org.gradle.process.internal.worker.request.WorkerAction$1.call(WorkerAction.java:138)
                     *     at org.gradle.process.internal.worker.child.WorkerLogEventListener.withWorkerLoggingProtocol(WorkerLogEventListener.java:41)
                     *     at org.gradle.process.internal.worker.request.WorkerAction.run(WorkerAction.java:135)
                     *     at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
                     *     at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:77)
                     *     at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
                     *     at java.base/java.lang.reflect.Method.invoke(Method.java:568)
                     *     at org.gradle.internal.dispatch.ReflectionDispatch.dispatch(ReflectionDispatch.java:36)
                     *     at org.gradle.internal.dispatch.ReflectionDispatch.dispatch(ReflectionDispatch.java:24)
                     *     at org.gradle.internal.remote.internal.hub.MessageHubBackedObjectConnection$DispatchWrapper.dispatch(MessageHubBackedObjectConnection.java:182)
                     *     at org.gradle.internal.remote.internal.hub.MessageHubBackedObjectConnection$DispatchWrapper.dispatch(MessageHubBackedObjectConnection.java:164)
                     *     at org.gradle.internal.remote.internal.hub.MessageHub$Handler.run(MessageHub.java:414)
                     *     at org.gradle.internal.concurrent.ExecutorPolicy$CatchAndRecordFailures.onExecute(ExecutorPolicy.java:64)
                     *     at org.gradle.internal.concurrent.ManagedExecutorImpl$1.run(ManagedExecutorImpl.java:49)
                     *     at java.base/java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1136)
                     *     at java.base/java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:635)
                     *     at java.base/java.lang.Thread.run(Thread.java:833)
                     */
                    throw new IllegalStateException("Decompilation failed");
                }

                public String toString() {
                    return "ResourceAccess[" + resourceManager + "]";
                }

                private static /* synthetic */ Pair method_31157(Object object) {
                    return Pair.of(object, OptionalInt.empty());
                }
            };
        }

        public static final class class_5507
        implements class_5506 {
            private final Map<RegistryKey<?>, JsonElement> field_26741 = Maps.newIdentityHashMap();
            private final Object2IntMap<RegistryKey<?>> field_26742 = new Object2IntOpenCustomHashMap(Util.identityHashStrategy());
            private final Map<RegistryKey<?>, Lifecycle> field_26743 = Maps.newIdentityHashMap();

            public <E> void method_31159(DynamicRegistryManager.Impl impl, RegistryKey<E> registryKey, Encoder<E> encoder, int i, E object, Lifecycle lifecycle) {
                DataResult<JsonElement> dataResult = encoder.encodeStart(RegistryReadingOps.of(JsonOps.INSTANCE, impl), object);
                Optional<DataResult.PartialResult<JsonElement>> optional = dataResult.error();
                if (optional.isPresent()) {
                    LOGGER.error("Error adding element: {}", (Object)optional.get().message());
                    return;
                }
                this.field_26741.put(registryKey, dataResult.result().get());
                this.field_26742.put((RegistryKey<?>)registryKey, i);
                this.field_26743.put(registryKey, lifecycle);
            }

            @Override
            public Collection<Identifier> method_31156(RegistryKey<? extends Registry<?>> registryKey) {
                return this.field_26741.keySet().stream().filter(registryKey2 -> registryKey2.method_31163(registryKey)).map(registryKey2 -> new Identifier(registryKey2.getValue().getNamespace(), registryKey.getValue().getPath() + "/" + registryKey2.getValue().getPath() + ".json")).collect(Collectors.toList());
            }

            @Override
            public <E> DataResult<Pair<E, OptionalInt>> method_31155(DynamicOps<JsonElement> dynamicOps, RegistryKey<? extends Registry<E>> registryKey, RegistryKey<E> registryKey2, Decoder<E> decoder) {
                JsonElement jsonElement = this.field_26741.get(registryKey2);
                if (jsonElement == null) {
                    return DataResult.error("Unknown element: " + registryKey2);
                }
                return decoder.parse(dynamicOps, jsonElement).setLifecycle(this.field_26743.get(registryKey2)).map(object -> Pair.of(object, OptionalInt.of(this.field_26742.getInt(registryKey2))));
            }
        }
    }

    static final class ValueHolder<E> {
        private final Map<RegistryKey<E>, DataResult<java.util.function.Supplier<E>>> values = Maps.newIdentityHashMap();

        private ValueHolder() {
        }
    }
}

