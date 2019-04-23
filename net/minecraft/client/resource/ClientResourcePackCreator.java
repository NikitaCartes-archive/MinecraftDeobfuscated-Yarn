/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.resource;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Pattern;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.menu.WorkingScreen;
import net.minecraft.client.resource.ClientResourcePackContainer;
import net.minecraft.client.resource.DefaultClientResourcePack;
import net.minecraft.client.resource.ResourceIndex;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.util.NetworkUtils;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resource.DefaultResourcePack;
import net.minecraft.resource.ResourcePackCompatibility;
import net.minecraft.resource.ResourcePackContainer;
import net.minecraft.resource.ResourcePackCreator;
import net.minecraft.resource.ZipResourcePack;
import net.minecraft.resource.metadata.PackResourceMetadata;
import net.minecraft.util.Identifier;
import net.minecraft.util.SystemUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class ClientResourcePackCreator
implements ResourcePackCreator {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Pattern ALPHANUMERAL = Pattern.compile("^[a-fA-F0-9]{40}$");
    private final DefaultResourcePack pack;
    private final File serverPacksRoot;
    private final ReentrantLock lock = new ReentrantLock();
    private final ResourceIndex index;
    @Nullable
    private CompletableFuture<?> downloadTask;
    @Nullable
    private ClientResourcePackContainer serverContainer;

    public ClientResourcePackCreator(File file, ResourceIndex resourceIndex) {
        this.serverPacksRoot = file;
        this.index = resourceIndex;
        this.pack = new DefaultClientResourcePack(resourceIndex);
    }

    @Override
    public <T extends ResourcePackContainer> void registerContainer(Map<String, T> map, ResourcePackContainer.Factory<T> factory) {
        T resourcePackContainer2;
        File file;
        T resourcePackContainer = ResourcePackContainer.of("vanilla", true, () -> this.pack, factory, ResourcePackContainer.InsertionPosition.BOTTOM);
        if (resourcePackContainer != null) {
            map.put("vanilla", resourcePackContainer);
        }
        if (this.serverContainer != null) {
            map.put("server", this.serverContainer);
        }
        if ((file = this.index.getResource(new Identifier("resourcepacks/programmer_art.zip"))) != null && file.isFile() && (resourcePackContainer2 = ResourcePackContainer.of("programer_art", false, () -> new ZipResourcePack(file){

            @Override
            public String getName() {
                return "Programmer Art";
            }
        }, factory, ResourcePackContainer.InsertionPosition.TOP)) != null) {
            map.put("programer_art", resourcePackContainer2);
        }
    }

    public DefaultResourcePack getPack() {
        return this.pack;
    }

    public static Map<String, String> getDownloadHeaders() {
        HashMap<String, String> map = Maps.newHashMap();
        map.put("X-Minecraft-Username", MinecraftClient.getInstance().getSession().getUsername());
        map.put("X-Minecraft-UUID", MinecraftClient.getInstance().getSession().getUuid());
        map.put("X-Minecraft-Version", SharedConstants.getGameVersion().getName());
        map.put("X-Minecraft-Pack-Format", String.valueOf(SharedConstants.getGameVersion().getPackVersion()));
        map.put("User-Agent", "Minecraft Java/" + SharedConstants.getGameVersion().getName());
        return map;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public CompletableFuture<?> download(String string, String string2) {
        String string3 = DigestUtils.sha1Hex(string);
        String string4 = ALPHANUMERAL.matcher(string2).matches() ? string2 : "";
        this.lock.lock();
        try {
            CompletableFuture<String> completableFuture;
            this.clear();
            this.deleteOldServerPack();
            File file = new File(this.serverPacksRoot, string3);
            if (file.exists()) {
                completableFuture = CompletableFuture.completedFuture("");
            } else {
                WorkingScreen workingScreen = new WorkingScreen();
                Map<String, String> map = ClientResourcePackCreator.getDownloadHeaders();
                MinecraftClient minecraftClient = MinecraftClient.getInstance();
                minecraftClient.executeSync(() -> minecraftClient.openScreen(workingScreen));
                completableFuture = NetworkUtils.download(file, string, map, 0x3200000, workingScreen, minecraftClient.getNetworkProxy());
            }
            CompletableFuture<?> completableFuture2 = this.downloadTask = ((CompletableFuture)completableFuture.thenCompose(object -> {
                if (!this.verifyFile(string4, file)) {
                    return SystemUtil.completeExceptionally(new RuntimeException("Hash check failure for file " + file + ", see log"));
                }
                return this.loadServerPack(file);
            })).whenComplete((void_, throwable) -> {
                if (throwable != null) {
                    LOGGER.warn("Pack application failed: {}, deleting file {}", (Object)throwable.getMessage(), (Object)file);
                    ClientResourcePackCreator.method_19437(file);
                }
            });
            return completableFuture2;
        } finally {
            this.lock.unlock();
        }
    }

    private static void method_19437(File file) {
        try {
            Files.delete(file.toPath());
        } catch (IOException iOException) {
            LOGGER.warn("Failed to delete file {}: {}", (Object)file, (Object)iOException.getMessage());
        }
    }

    public void clear() {
        this.lock.lock();
        try {
            if (this.downloadTask != null) {
                this.downloadTask.cancel(true);
            }
            this.downloadTask = null;
            if (this.serverContainer != null) {
                this.serverContainer = null;
                MinecraftClient.getInstance().reloadResourcesConcurrently();
            }
        } finally {
            this.lock.unlock();
        }
    }

    private boolean verifyFile(String string, File file) {
        try {
            String string2;
            try (FileInputStream fileInputStream = new FileInputStream(file);){
                string2 = DigestUtils.sha1Hex(fileInputStream);
            }
            if (string.isEmpty()) {
                LOGGER.info("Found file {} without verification hash", (Object)file);
                return true;
            }
            if (string2.toLowerCase(Locale.ROOT).equals(string.toLowerCase(Locale.ROOT))) {
                LOGGER.info("Found file {} matching requested hash {}", (Object)file, (Object)string);
                return true;
            }
            LOGGER.warn("File {} had wrong hash (expected {}, found {}).", (Object)file, (Object)string, (Object)string2);
        } catch (IOException iOException) {
            LOGGER.warn("File {} couldn't be hashed.", (Object)file, (Object)iOException);
        }
        return false;
    }

    private void deleteOldServerPack() {
        try {
            ArrayList<File> list = Lists.newArrayList(FileUtils.listFiles(this.serverPacksRoot, TrueFileFilter.TRUE, null));
            list.sort(LastModifiedFileComparator.LASTMODIFIED_REVERSE);
            int i = 0;
            for (File file : list) {
                if (i++ < 10) continue;
                LOGGER.info("Deleting old server resource pack {}", (Object)file.getName());
                FileUtils.deleteQuietly(file);
            }
        } catch (IllegalArgumentException illegalArgumentException) {
            LOGGER.error("Error while deleting old server resource pack : {}", (Object)illegalArgumentException.getMessage());
        }
    }

    public CompletableFuture<Void> loadServerPack(File file) {
        PackResourceMetadata packResourceMetadata = null;
        NativeImage nativeImage = null;
        String string = null;
        try (ZipResourcePack zipResourcePack = new ZipResourcePack(file);){
            packResourceMetadata = zipResourcePack.parseMetadata(PackResourceMetadata.READER);
            try (InputStream inputStream = zipResourcePack.openRoot("pack.png");){
                nativeImage = NativeImage.fromInputStream(inputStream);
            } catch (IOException | IllegalArgumentException exception) {
                LOGGER.info("Could not read pack.png: {}", (Object)exception.getMessage());
            }
        } catch (IOException iOException) {
            string = iOException.getMessage();
        }
        if (string != null) {
            return SystemUtil.completeExceptionally(new RuntimeException(String.format("Invalid resourcepack at %s: %s", file, string)));
        }
        LOGGER.info("Applying server pack {}", (Object)file);
        this.serverContainer = new ClientResourcePackContainer("server", true, () -> new ZipResourcePack(file), new TranslatableComponent("resourcePack.server.name", new Object[0]), packResourceMetadata.getDescription(), ResourcePackCompatibility.from(packResourceMetadata.getPackFormat()), ResourcePackContainer.InsertionPosition.TOP, true, nativeImage);
        return MinecraftClient.getInstance().reloadResourcesConcurrently();
    }
}

