/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.dynamic;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
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
    private final DynamicRegistryManager.Impl registryManager;
    private final Map<RegistryKey<? extends Registry<?>>, ValueHolder<?>> valueHolders = Maps.newIdentityHashMap();

    public static <T> RegistryOps<T> of(DynamicOps<T> delegate, ResourceManager resourceManager, DynamicRegistryManager.Impl impl) {
        RegistryOps<T> registryOps = new RegistryOps<T>(delegate, resourceManager, impl);
        DynamicRegistryManager.load(impl, registryOps);
        return registryOps;
    }

    private RegistryOps(DynamicOps<T> delegate, ResourceManager resourceManager, DynamicRegistryManager.Impl impl) {
        super(delegate);
        this.resourceManager = resourceManager;
        this.registryManager = impl;
    }

    /**
     * Encode an id for a registry element than a full object if possible.
     * 
     * <p>This method is called by casting an arbitrary dynamic ops to a registry
     * reading ops.</p>
     * 
     * @see RegistryReadingOps#encodeOrId(Object, Object, RegistryKey, MapCodec)
     */
    protected <E> DataResult<Pair<java.util.function.Supplier<E>, T>> decodeOrId(T object, RegistryKey<? extends Registry<E>> registryKey, Codec<E> codec) {
        Optional optional = this.registryManager.getOptional(registryKey);
        if (!optional.isPresent()) {
            return DataResult.error("Unknown registry: " + registryKey);
        }
        MutableRegistry mutableRegistry = optional.get();
        DataResult dataResult = Identifier.CODEC.decode(this.delegate, object);
        if (!dataResult.result().isPresent()) {
            return codec.decode(this.delegate, object).map(pair -> pair.mapFirst(object -> () -> object));
        }
        Pair pair2 = dataResult.result().get();
        Identifier identifier = (Identifier)pair2.getFirst();
        return this.readSupplier(registryKey, mutableRegistry, codec, identifier).map(supplier -> Pair.of(supplier, pair2.getSecond()));
    }

    /**
     * Loads elements into a registry just loaded from a decoder.
     */
    public <E> DataResult<SimpleRegistry<E>> loadToRegistry(SimpleRegistry<E> registry, RegistryKey<? extends Registry<E>> registryRef, Codec<E> codec) {
        Identifier identifier = registryRef.getValue();
        Collection<Identifier> collection = this.resourceManager.findResources(identifier.getPath(), string -> string.endsWith(".json"));
        DataResult<SimpleRegistry<Object>> dataResult = DataResult.success(registry, Lifecycle.stable());
        String string2 = identifier.getPath() + "/";
        for (Identifier identifier2 : collection) {
            String string22 = identifier2.getPath();
            if (!string22.endsWith(".json")) {
                LOGGER.warn("Skipping resource {} since it is not a json file", (Object)identifier2);
                continue;
            }
            if (!string22.startsWith(string2)) {
                LOGGER.warn("Skipping resource {} since it does not have a registry name prefix", (Object)identifier2);
                continue;
            }
            String string3 = string22.substring(string2.length(), string22.length() - ".json".length());
            Identifier identifier3 = new Identifier(identifier2.getNamespace(), string3);
            dataResult = dataResult.flatMap(simpleRegistry -> this.readSupplier(registryRef, (MutableRegistry)simpleRegistry, codec, identifier3).map(supplier -> simpleRegistry));
        }
        return dataResult.setPartial(registry);
    }

    /**
     * Reads a supplier for a registry element.
     * 
     * <p>This logic is used by both {@code decodeOrId} and {@code loadToRegistry}.</p>
     */
    private <E> DataResult<java.util.function.Supplier<E>> readSupplier(RegistryKey<? extends Registry<E>> registryRef, MutableRegistry<E> mutableRegistry, Codec<E> codec, Identifier elementId) {
        DataResult dataResult3;
        RegistryKey registryKey = RegistryKey.of(registryRef, elementId);
        ValueHolder<E> valueHolder = this.getValueHolder(registryRef);
        DataResult dataResult = (DataResult)((ValueHolder)valueHolder).values.get(registryKey);
        if (dataResult != null) {
            return dataResult;
        }
        Supplier<Object> supplier = Suppliers.memoize(() -> {
            Object object = mutableRegistry.get(registryKey);
            if (object == null) {
                throw new RuntimeException("Error during recursive registry parsing, element resolved too early: " + registryKey);
            }
            return object;
        });
        ((ValueHolder)valueHolder).values.put(registryKey, DataResult.success(supplier));
        DataResult<java.util.function.Supplier> dataResult2 = this.readElement(registryRef, registryKey, codec);
        if (dataResult2.result().isPresent()) {
            mutableRegistry.method_31062(registryKey, dataResult2.result().get());
            dataResult3 = dataResult2;
        } else {
            Object object2 = mutableRegistry.get(registryKey);
            dataResult3 = object2 != null ? DataResult.success(object2, Lifecycle.stable()) : dataResult2;
        }
        DataResult<java.util.function.Supplier<E>> dataResult4 = dataResult3.map(object -> () -> object);
        ((ValueHolder)valueHolder).values.put(registryKey, dataResult4);
        return dataResult4;
    }

    /*
     * Exception decompiling
     */
    /**
     * Reads the actual element.
     */
    private <E> DataResult<E> readElement(RegistryKey<? extends Registry<E>> registryRef, RegistryKey<E> elementRef, Codec<E> codec) {
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

