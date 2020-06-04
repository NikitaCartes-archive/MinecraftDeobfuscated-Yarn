/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

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
import net.minecraft.class_5379;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.world.dimension.DimensionTracker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_5382<T>
extends class_5379<T> {
    private static final Logger field_25509 = LogManager.getLogger();
    private final ResourceManager field_25510;
    private final DimensionTracker field_25511;
    private final Map<RegistryKey<? extends Registry<?>>, class_5383<?>> field_25512 = Maps.newIdentityHashMap();

    public static <T> class_5382<T> method_29753(DynamicOps<T> dynamicOps, ResourceManager resourceManager, DimensionTracker dimensionTracker) {
        return new class_5382<T>(dynamicOps, resourceManager, dimensionTracker);
    }

    private class_5382(DynamicOps<T> dynamicOps, ResourceManager resourceManager, DimensionTracker dimensionTracker) {
        super(dynamicOps);
        this.field_25510 = resourceManager;
        this.field_25511 = dimensionTracker;
    }

    protected <E> DataResult<Pair<java.util.function.Supplier<E>, T>> method_29759(T object, RegistryKey<Registry<E>> registryKey, Codec<E> codec) {
        DataResult dataResult = Identifier.field_25139.decode(this.field_25503, object);
        if (!dataResult.result().isPresent()) {
            return codec.decode(this.field_25503, object).map(pair -> pair.mapFirst(object -> () -> object));
        }
        Optional<MutableRegistry<E>> optional = this.field_25511.method_29726(registryKey);
        if (!optional.isPresent()) {
            return DataResult.error("Unknown registry: " + registryKey);
        }
        Pair pair2 = dataResult.result().get();
        Identifier identifier = (Identifier)pair2.getFirst();
        return this.method_29763(registryKey, optional.get(), codec, identifier).map(supplier -> Pair.of(supplier, pair2.getSecond()));
    }

    public <E> DataResult<SimpleRegistry<E>> method_29755(SimpleRegistry<E> simpleRegistry2, RegistryKey<Registry<E>> registryKey, Codec<E> codec) {
        Identifier identifier = registryKey.getValue();
        Collection<Identifier> collection = this.field_25510.method_29489(identifier, string -> string.endsWith(".json"));
        DataResult<SimpleRegistry<Object>> dataResult = DataResult.success(simpleRegistry2, Lifecycle.stable());
        for (Identifier identifier2 : collection) {
            String string2 = identifier2.getPath();
            if (!string2.endsWith(".json")) {
                field_25509.warn("Skipping resource {} since it is not a json file", (Object)identifier2);
                continue;
            }
            if (!string2.startsWith(identifier.getPath() + "/")) {
                field_25509.warn("Skipping resource {} since it does not have a registry name prefix", (Object)identifier2);
                continue;
            }
            String string22 = string2.substring(0, string2.length() - ".json".length()).substring(identifier.getPath().length() + 1);
            int i = string22.indexOf(47);
            if (i < 0) {
                field_25509.warn("Skipping resource {} since it does not have a namespace", (Object)identifier2);
                continue;
            }
            String string3 = string22.substring(0, i);
            String string4 = string22.substring(i + 1);
            Identifier identifier3 = new Identifier(string3, string4);
            dataResult = dataResult.flatMap(simpleRegistry -> this.method_29763(registryKey, (MutableRegistry)simpleRegistry, codec, identifier3).map(supplier -> simpleRegistry));
        }
        return dataResult.setPartial(simpleRegistry2);
    }

    private <E> DataResult<java.util.function.Supplier<E>> method_29763(RegistryKey<Registry<E>> registryKey, MutableRegistry<E> mutableRegistry, Codec<E> codec, Identifier identifier) {
        RegistryKey registryKey2 = RegistryKey.of(registryKey, identifier);
        Object object2 = mutableRegistry.get(registryKey2);
        if (object2 != null) {
            return DataResult.success(() -> object2, Lifecycle.stable());
        }
        class_5383<E> lv = this.method_29761(registryKey);
        DataResult dataResult = (DataResult)((class_5383)lv).field_25513.get(registryKey2);
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
        ((class_5383)lv).field_25513.put(registryKey2, DataResult.success(supplier));
        DataResult<E> dataResult2 = this.method_29764(registryKey, registryKey2, codec);
        dataResult2.result().ifPresent(object -> mutableRegistry.add(registryKey2, object));
        DataResult<java.util.function.Supplier<E>> dataResult3 = dataResult2.map(object -> () -> object);
        ((class_5383)lv).field_25513.put(registryKey2, dataResult3);
        return dataResult3;
    }

    /*
     * Exception decompiling
     */
    private <E> DataResult<E> method_29764(RegistryKey<Registry<E>> registryKey, RegistryKey<E> registryKey2, Codec<E> codec) {
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

    private <E> class_5383<E> method_29761(RegistryKey<Registry<E>> registryKey2) {
        return this.field_25512.computeIfAbsent(registryKey2, registryKey -> new class_5383());
    }

    static final class class_5383<E> {
        private final Map<RegistryKey<E>, DataResult<java.util.function.Supplier<E>>> field_25513 = Maps.newIdentityHashMap();

        private class_5383() {
        }
    }
}

