/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.dynamic;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.ForwardingDynamicOps;
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
    private final ResourceManager resourceManager;
    private final DynamicRegistryManager registryManager;
    private final Map<RegistryKey<? extends Registry<?>>, ValueHolder<?>> valueHolders = Maps.newIdentityHashMap();

    public static <T> RegistryOps<T> of(DynamicOps<T> delegate, ResourceManager resourceManager, DynamicRegistryManager registryTracker) {
        return new RegistryOps<T>(delegate, resourceManager, registryTracker);
    }

    private RegistryOps(DynamicOps<T> delegate, ResourceManager resourceManager, DynamicRegistryManager registryTracker) {
        super(delegate);
        this.resourceManager = resourceManager;
        this.registryManager = registryTracker;
    }

    /**
     * Encode an id for a registry element than a full object if possible.
     * 
     * <p>This method is called by casting an arbitrary dynamic ops to a registry
     * reading ops.</p>
     * 
     * @see RegistryReadingOps#encodeOrId(Object, Object, RegistryKey, Codec)
     */
    protected <E> DataResult<Pair<java.util.function.Supplier<E>, T>> decodeOrId(T object, RegistryKey<? extends Registry<E>> registryKey, MapCodec<E> mapCodec) {
        Optional optional = this.registryManager.getOptional(registryKey);
        if (!optional.isPresent()) {
            return DataResult.error("Unknown registry: " + registryKey);
        }
        MutableRegistry mutableRegistry = optional.get();
        DataResult dataResult = Identifier.CODEC.decode(this.delegate, object);
        if (!dataResult.result().isPresent()) {
            return SimpleRegistry.method_30516(registryKey, mapCodec).codec().decode(this.delegate, object).map(pair2 -> pair2.mapFirst(pair -> {
                mutableRegistry.add((RegistryKey)pair.getFirst(), pair.getSecond());
                mutableRegistry.markLoaded((RegistryKey)pair.getFirst());
                return pair::getSecond;
            }));
        }
        Pair pair = dataResult.result().get();
        Identifier identifier = (Identifier)pair.getFirst();
        return this.readSupplier(registryKey, mutableRegistry, mapCodec, identifier).map(supplier -> Pair.of(supplier, pair.getSecond()));
    }

    /**
     * Loads elements into a registry just loaded from a decoder.
     */
    public <E> DataResult<SimpleRegistry<E>> loadToRegistry(SimpleRegistry<E> registry, RegistryKey<? extends Registry<E>> registryRef, MapCodec<E> mapCodec) {
        Identifier identifier = registryRef.getValue();
        Collection<Identifier> collection = this.resourceManager.findResources(identifier, string -> string.endsWith(".json"));
        DataResult<SimpleRegistry<Object>> dataResult = DataResult.success(registry, Lifecycle.stable());
        for (Identifier identifier2 : collection) {
            String string2 = identifier2.getPath();
            if (!string2.endsWith(".json")) {
                LOGGER.warn("Skipping resource {} since it is not a json file", (Object)identifier2);
                continue;
            }
            if (!string2.startsWith(identifier.getPath() + "/")) {
                LOGGER.warn("Skipping resource {} since it does not have a registry name prefix", (Object)identifier2);
                continue;
            }
            String string22 = string2.substring(0, string2.length() - ".json".length()).substring(identifier.getPath().length() + 1);
            int i = string22.indexOf(47);
            if (i < 0) {
                LOGGER.warn("Skipping resource {} since it does not have a namespace", (Object)identifier2);
                continue;
            }
            String string3 = string22.substring(0, i);
            String string4 = string22.substring(i + 1);
            Identifier identifier3 = new Identifier(string3, string4);
            dataResult = dataResult.flatMap(simpleRegistry -> this.readSupplier(registryRef, (MutableRegistry)simpleRegistry, mapCodec, identifier3).map(supplier -> simpleRegistry));
        }
        return dataResult.setPartial(registry);
    }

    /**
     * Reads a supplier for a registry element.
     * 
     * <p>This logic is used by both {@code decodeOrId} and {@code loadToRegistry}.</p>
     */
    private <E> DataResult<java.util.function.Supplier<E>> readSupplier(RegistryKey<? extends Registry<E>> registryRef, MutableRegistry<E> registry, MapCodec<E> mapCodec, Identifier elementId) {
        RegistryKey registryKey = RegistryKey.of(registryRef, elementId);
        Object object2 = registry.get(registryKey);
        if (object2 != null) {
            return DataResult.success(() -> object2, Lifecycle.stable());
        }
        ValueHolder<E> valueHolder = this.getValueHolder(registryRef);
        DataResult dataResult = (DataResult)((ValueHolder)valueHolder).values.get(registryKey);
        if (dataResult != null) {
            return dataResult;
        }
        Supplier<Object> supplier = Suppliers.memoize(() -> {
            Object object = registry.get(registryKey);
            if (object == null) {
                throw new RuntimeException("Error during recursive registry parsing, element resolved too early: " + registryKey);
            }
            return object;
        });
        ((ValueHolder)valueHolder).values.put(registryKey, DataResult.success(supplier));
        DataResult<java.util.function.Supplier> dataResult2 = this.readElement(registryRef, registryKey, mapCodec);
        dataResult2.result().ifPresent(object -> registry.add(registryKey, object));
        DataResult<java.util.function.Supplier<E>> dataResult3 = dataResult2.map(object -> () -> object);
        ((ValueHolder)valueHolder).values.put(registryKey, dataResult3);
        return dataResult3;
    }

    /*
     * Exception decompiling
     */
    /**
     * Reads the actual element.
     */
    private <E> DataResult<E> readElement(RegistryKey<? extends Registry<E>> registryRef, RegistryKey<E> elementRef, MapCodec<E> mapCodec) {
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

    private <E> ValueHolder<E> getValueHolder(RegistryKey<? extends Registry<E>> registryRef) {
        return this.valueHolders.computeIfAbsent(registryRef, registryKey -> new ValueHolder());
    }

    static final class ValueHolder<E> {
        private final Map<RegistryKey<E>, DataResult<java.util.function.Supplier<E>>> values = Maps.newIdentityHashMap();

        private ValueHolder() {
        }
    }
}

