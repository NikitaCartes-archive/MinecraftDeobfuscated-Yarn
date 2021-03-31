/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.screen.pack;

import com.google.common.collect.Maps;
import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.screen.pack.PackListWidget;
import net.minecraft.client.gui.screen.pack.ResourcePackOrganizer;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class PackScreen
extends Screen {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final int field_32395 = 200;
    private static final Text DROP_INFO = new TranslatableText("pack.dropInfo").formatted(Formatting.GRAY);
    private static final Text FOLDER_INFO = new TranslatableText("pack.folderInfo");
    private static final int field_32396 = 20;
    private static final Identifier UNKNOWN_PACK = new Identifier("textures/misc/unknown_pack.png");
    private final ResourcePackOrganizer organizer;
    private final Screen parent;
    @Nullable
    private DirectoryWatcher directoryWatcher;
    private long field_25788;
    private PackListWidget availablePackList;
    private PackListWidget selectedPackList;
    private final File file;
    private ButtonWidget doneButton;
    private final Map<String, Identifier> iconTextures = Maps.newHashMap();

    public PackScreen(Screen parent, ResourcePackManager packManager, Consumer<ResourcePackManager> consumer, File file, Text title) {
        super(title);
        this.parent = parent;
        this.organizer = new ResourcePackOrganizer(this::updatePackLists, this::getPackIconTexture, packManager, consumer);
        this.file = file;
        this.directoryWatcher = DirectoryWatcher.create(file);
    }

    @Override
    public void onClose() {
        this.organizer.apply();
        this.client.openScreen(this.parent);
        this.closeDirectoryWatcher();
    }

    private void closeDirectoryWatcher() {
        if (this.directoryWatcher != null) {
            try {
                this.directoryWatcher.close();
                this.directoryWatcher = null;
            } catch (Exception exception) {
                // empty catch block
            }
        }
    }

    @Override
    protected void init() {
        this.doneButton = this.addButton(new ButtonWidget(this.width / 2 + 4, this.height - 48, 150, 20, ScreenTexts.DONE, buttonWidget -> this.onClose()));
        this.addButton(new ButtonWidget(this.width / 2 - 154, this.height - 48, 150, 20, new TranslatableText("pack.openFolder"), buttonWidget -> Util.getOperatingSystem().open(this.file), (buttonWidget, matrixStack, i, j) -> this.renderTooltip(matrixStack, FOLDER_INFO, i, j)));
        this.availablePackList = new PackListWidget(this.client, 200, this.height, new TranslatableText("pack.available.title"));
        this.availablePackList.setLeftPos(this.width / 2 - 4 - 200);
        this.children.add(this.availablePackList);
        this.selectedPackList = new PackListWidget(this.client, 200, this.height, new TranslatableText("pack.selected.title"));
        this.selectedPackList.setLeftPos(this.width / 2 + 4);
        this.children.add(this.selectedPackList);
        this.refresh();
    }

    @Override
    public void tick() {
        if (this.directoryWatcher != null) {
            try {
                if (this.directoryWatcher.pollForChange()) {
                    this.field_25788 = 20L;
                }
            } catch (IOException iOException) {
                LOGGER.warn("Failed to poll for directory {} changes, stopping", (Object)this.file);
                this.closeDirectoryWatcher();
            }
        }
        if (this.field_25788 > 0L && --this.field_25788 == 0L) {
            this.refresh();
        }
    }

    private void updatePackLists() {
        this.updatePackList(this.selectedPackList, this.organizer.getEnabledPacks());
        this.updatePackList(this.availablePackList, this.organizer.getDisabledPacks());
        this.doneButton.active = !this.selectedPackList.children().isEmpty();
    }

    private void updatePackList(PackListWidget widget, Stream<ResourcePackOrganizer.Pack> packs) {
        widget.children().clear();
        packs.forEach(pack -> widget.children().add(new PackListWidget.ResourcePackEntry(this.client, widget, this, (ResourcePackOrganizer.Pack)pack)));
    }

    private void refresh() {
        this.organizer.refresh();
        this.updatePackLists();
        this.field_25788 = 0L;
        this.iconTextures.clear();
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackgroundTexture(0);
        this.availablePackList.render(matrices, mouseX, mouseY, delta);
        this.selectedPackList.render(matrices, mouseX, mouseY, delta);
        PackScreen.drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 8, 0xFFFFFF);
        PackScreen.drawCenteredText(matrices, this.textRenderer, DROP_INFO, this.width / 2, 20, 0xFFFFFF);
        super.render(matrices, mouseX, mouseY, delta);
    }

    protected static void copyPacks(MinecraftClient client, List<Path> srcPaths, Path destPath) {
        MutableBoolean mutableBoolean = new MutableBoolean();
        srcPaths.forEach(src -> {
            try (Stream<Path> stream = Files.walk(src, new FileVisitOption[0]);){
                stream.forEach(toCopy -> {
                    try {
                        Util.relativeCopy(src.getParent(), destPath, toCopy);
                    } catch (IOException iOException) {
                        LOGGER.warn("Failed to copy datapack file  from {} to {}", toCopy, (Object)destPath, (Object)iOException);
                        mutableBoolean.setTrue();
                    }
                });
            } catch (IOException iOException) {
                LOGGER.warn("Failed to copy datapack file from {} to {}", src, (Object)destPath);
                mutableBoolean.setTrue();
            }
        });
        if (mutableBoolean.isTrue()) {
            SystemToast.addPackCopyFailure(client, destPath.toString());
        }
    }

    @Override
    public void filesDragged(List<Path> paths) {
        String string = paths.stream().map(Path::getFileName).map(Path::toString).collect(Collectors.joining(", "));
        this.client.openScreen(new ConfirmScreen(bl -> {
            if (bl) {
                PackScreen.copyPacks(this.client, paths, this.file.toPath());
                this.refresh();
            }
            this.client.openScreen(this);
        }, new TranslatableText("pack.dropConfirm"), new LiteralText(string)));
    }

    /*
     * Exception decompiling
     */
    private Identifier loadPackIcon(TextureManager textureManager, ResourcePackProfile resourcePackProfile) {
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

    private Identifier getPackIconTexture(ResourcePackProfile resourcePackProfile) {
        return this.iconTextures.computeIfAbsent(resourcePackProfile.getName(), string -> this.loadPackIcon(this.client.getTextureManager(), resourcePackProfile));
    }

    @Environment(value=EnvType.CLIENT)
    static class DirectoryWatcher
    implements AutoCloseable {
        private final WatchService watchService;
        private final Path path;

        public DirectoryWatcher(File file) throws IOException {
            this.path = file.toPath();
            this.watchService = this.path.getFileSystem().newWatchService();
            try {
                this.watchDirectory(this.path);
                try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(this.path);){
                    for (Path path : directoryStream) {
                        if (!Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS)) continue;
                        this.watchDirectory(path);
                    }
                }
            } catch (Exception exception) {
                this.watchService.close();
                throw exception;
            }
        }

        @Nullable
        public static DirectoryWatcher create(File file) {
            try {
                return new DirectoryWatcher(file);
            } catch (IOException iOException) {
                LOGGER.warn("Failed to initialize pack directory {} monitoring", (Object)file, (Object)iOException);
                return null;
            }
        }

        private void watchDirectory(Path path) throws IOException {
            path.register(this.watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);
        }

        public boolean pollForChange() throws IOException {
            WatchKey watchKey;
            boolean bl = false;
            while ((watchKey = this.watchService.poll()) != null) {
                List<WatchEvent<?>> list = watchKey.pollEvents();
                for (WatchEvent<?> watchEvent : list) {
                    Path path;
                    bl = true;
                    if (watchKey.watchable() != this.path || watchEvent.kind() != StandardWatchEventKinds.ENTRY_CREATE || !Files.isDirectory(path = this.path.resolve((Path)watchEvent.context()), LinkOption.NOFOLLOW_LINKS)) continue;
                    this.watchDirectory(path);
                }
                watchKey.reset();
            }
            return bl;
        }

        @Override
        public void close() throws IOException {
            this.watchService.close();
        }
    }
}

