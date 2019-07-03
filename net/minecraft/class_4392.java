/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import com.google.common.util.concurrent.RateLimiter;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.realmsclient.dto.WorldDownload;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4333;
import net.minecraft.class_4396;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsDefaultVertexFormat;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.realms.Tezzelator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public class class_4392
extends RealmsScreen {
    private static final Logger field_19844 = LogManager.getLogger();
    private final RealmsScreen field_19845;
    private final WorldDownload field_19846;
    private final String field_19847;
    private final RateLimiter field_19848;
    private RealmsButton field_19849;
    private final String field_19850;
    private final class_4393 field_19851;
    private volatile String field_19852;
    private volatile String field_19853;
    private volatile String field_19854;
    private volatile boolean field_19855;
    private volatile boolean field_19856 = true;
    private volatile boolean field_19857;
    private volatile boolean field_19858;
    private Long field_19859;
    private Long field_19860;
    private long field_19861;
    private int field_19862;
    private static final String[] field_19863 = new String[]{"", ".", ". .", ". . ."};
    private int field_19864;
    private final int field_19865 = 100;
    private int field_19866 = -1;
    private boolean field_19867;
    private static final ReentrantLock field_19868 = new ReentrantLock();

    public class_4392(RealmsScreen realmsScreen, WorldDownload worldDownload, String string) {
        this.field_19845 = realmsScreen;
        this.field_19850 = string;
        this.field_19846 = worldDownload;
        this.field_19851 = new class_4393();
        this.field_19847 = class_4392.getLocalizedString("mco.download.title");
        this.field_19848 = RateLimiter.create(0.1f);
    }

    public void method_21254(int i) {
        this.field_19866 = i;
    }

    @Override
    public void init() {
        this.setKeyboardHandlerSendRepeatsToGui(true);
        this.field_19849 = new RealmsButton(0, this.width() / 2 - 100, this.height() - 42, 200, 20, class_4392.getLocalizedString("gui.cancel")){

            @Override
            public void onPress() {
                class_4392.this.field_19855 = true;
                class_4392.this.method_21269();
            }
        };
        this.buttonsAdd(this.field_19849);
        this.method_21265();
    }

    private void method_21265() {
        if (this.field_19857) {
            return;
        }
        if (!this.field_19867 && this.method_21259(this.field_19846.downloadLink) >= 0x140000000L) {
            String string = class_4392.getLocalizedString("mco.download.confirmation.line1", class_4392.method_21261(0x140000000L));
            String string2 = class_4392.getLocalizedString("mco.download.confirmation.line2");
            Realms.setScreen(new class_4396(this, class_4396.class_4397.WARNING, string, string2, false, 100));
        } else {
            this.method_21278();
        }
    }

    @Override
    public void confirmResult(boolean bl, int i) {
        this.field_19867 = true;
        Realms.setScreen(this);
        this.method_21278();
    }

    private long method_21259(String string) {
        class_4333 lv = new class_4333();
        return lv.method_20955(string);
    }

    @Override
    public void tick() {
        super.tick();
        ++this.field_19862;
        if (this.field_19853 != null && this.field_19848.tryAcquire(1)) {
            ArrayList<String> arrayList = new ArrayList<String>();
            arrayList.add(this.field_19847);
            arrayList.add(this.field_19853);
            if (this.field_19854 != null) {
                arrayList.add(this.field_19854 + "%");
                arrayList.add(class_4392.method_21255(this.field_19861));
            }
            if (this.field_19852 != null) {
                arrayList.add(this.field_19852);
            }
            String string = String.join((CharSequence)System.lineSeparator(), arrayList);
            Realms.narrateNow(string);
        }
    }

    @Override
    public boolean keyPressed(int i, int j, int k) {
        if (i == 256) {
            this.field_19855 = true;
            this.method_21269();
            return true;
        }
        return super.keyPressed(i, j, k);
    }

    private void method_21269() {
        if (this.field_19857 && this.field_19866 != -1 && this.field_19852 == null) {
            this.field_19845.confirmResult(true, this.field_19866);
        }
        Realms.setScreen(this.field_19845);
    }

    @Override
    public void render(int i, int j, float f) {
        this.renderBackground();
        if (this.field_19858 && !this.field_19857) {
            this.field_19853 = class_4392.getLocalizedString("mco.download.extracting");
        }
        this.drawCenteredString(this.field_19847, this.width() / 2, 20, 0xFFFFFF);
        this.drawCenteredString(this.field_19853, this.width() / 2, 50, 0xFFFFFF);
        if (this.field_19856) {
            this.method_21272();
        }
        if (this.field_19851.field_19871 != 0L && !this.field_19855) {
            this.method_21274();
            this.method_21276();
        }
        if (this.field_19852 != null) {
            this.drawCenteredString(this.field_19852, this.width() / 2, 110, 0xFF0000);
        }
        super.render(i, j, f);
    }

    private void method_21272() {
        int i = this.fontWidth(this.field_19853);
        if (this.field_19862 % 10 == 0) {
            ++this.field_19864;
        }
        this.drawString(field_19863[this.field_19864 % field_19863.length], this.width() / 2 + i / 2 + 5, 50, 0xFFFFFF);
    }

    private void method_21274() {
        double d = this.field_19851.field_19871.doubleValue() / this.field_19851.field_19872.doubleValue() * 100.0;
        this.field_19854 = String.format(Locale.ROOT, "%.1f", d);
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.disableTexture();
        Tezzelator tezzelator = Tezzelator.instance;
        tezzelator.begin(7, RealmsDefaultVertexFormat.POSITION_COLOR);
        double e = this.width() / 2 - 100;
        double f = 0.5;
        tezzelator.vertex(e - 0.5, 95.5, 0.0).color(217, 210, 210, 255).endVertex();
        tezzelator.vertex(e + 200.0 * d / 100.0 + 0.5, 95.5, 0.0).color(217, 210, 210, 255).endVertex();
        tezzelator.vertex(e + 200.0 * d / 100.0 + 0.5, 79.5, 0.0).color(217, 210, 210, 255).endVertex();
        tezzelator.vertex(e - 0.5, 79.5, 0.0).color(217, 210, 210, 255).endVertex();
        tezzelator.vertex(e, 95.0, 0.0).color(128, 128, 128, 255).endVertex();
        tezzelator.vertex(e + 200.0 * d / 100.0, 95.0, 0.0).color(128, 128, 128, 255).endVertex();
        tezzelator.vertex(e + 200.0 * d / 100.0, 80.0, 0.0).color(128, 128, 128, 255).endVertex();
        tezzelator.vertex(e, 80.0, 0.0).color(128, 128, 128, 255).endVertex();
        tezzelator.end();
        GlStateManager.enableTexture();
        this.drawCenteredString(this.field_19854 + " %", this.width() / 2, 84, 0xFFFFFF);
    }

    private void method_21276() {
        if (this.field_19862 % 20 == 0) {
            if (this.field_19859 != null) {
                long l = System.currentTimeMillis() - this.field_19860;
                if (l == 0L) {
                    l = 1L;
                }
                this.field_19861 = 1000L * (this.field_19851.field_19871 - this.field_19859) / l;
                this.method_21266(this.field_19861);
            }
            this.field_19859 = this.field_19851.field_19871;
            this.field_19860 = System.currentTimeMillis();
        } else {
            this.method_21266(this.field_19861);
        }
    }

    private void method_21266(long l) {
        if (l > 0L) {
            int i = this.fontWidth(this.field_19854);
            String string = "(" + class_4392.method_21255(l) + ")";
            this.drawString(string, this.width() / 2 + i / 2 + 15, 84, 0xFFFFFF);
        }
    }

    public static String method_21255(long l) {
        int i = 1024;
        if (l < 1024L) {
            return l + " B/s";
        }
        int j = (int)(Math.log(l) / Math.log(1024.0));
        String string = "KMGTPE".charAt(j - 1) + "";
        return String.format(Locale.ROOT, "%.1f %sB/s", (double)l / Math.pow(1024.0, j), string);
    }

    public static String method_21261(long l) {
        int i = 1024;
        if (l < 1024L) {
            return l + " B";
        }
        int j = (int)(Math.log(l) / Math.log(1024.0));
        String string = "KMGTPE".charAt(j - 1) + "";
        return String.format(Locale.ROOT, "%.0f %sB", (double)l / Math.pow(1024.0, j), string);
    }

    private void method_21278() {
        new Thread(){

            @Override
            public void run() {
                try {
                    if (!field_19868.tryLock(1L, TimeUnit.SECONDS)) {
                        return;
                    }
                    class_4392.this.field_19853 = RealmsScreen.getLocalizedString("mco.download.preparing");
                    if (class_4392.this.field_19855) {
                        class_4392.this.method_21279();
                        return;
                    }
                    class_4392.this.field_19853 = RealmsScreen.getLocalizedString("mco.download.downloading", class_4392.this.field_19850);
                    class_4333 lv = new class_4333();
                    lv.method_20955(((class_4392)class_4392.this).field_19846.downloadLink);
                    lv.method_20949(class_4392.this.field_19846, class_4392.this.field_19850, class_4392.this.field_19851, class_4392.this.getLevelStorageSource());
                    while (!lv.method_20957()) {
                        if (lv.method_20961()) {
                            lv.method_20948();
                            class_4392.this.field_19852 = RealmsScreen.getLocalizedString("mco.download.failed");
                            class_4392.this.field_19849.setMessage(RealmsScreen.getLocalizedString("gui.done"));
                            return;
                        }
                        if (lv.method_20964()) {
                            class_4392.this.field_19858 = true;
                        }
                        if (class_4392.this.field_19855) {
                            lv.method_20948();
                            class_4392.this.method_21279();
                            return;
                        }
                        try {
                            Thread.sleep(500L);
                        } catch (InterruptedException interruptedException) {
                            field_19844.error("Failed to check Realms backup download status");
                        }
                    }
                    class_4392.this.field_19857 = true;
                    class_4392.this.field_19853 = RealmsScreen.getLocalizedString("mco.download.done");
                    class_4392.this.field_19849.setMessage(RealmsScreen.getLocalizedString("gui.done"));
                } catch (InterruptedException interruptedException2) {
                    field_19844.error("Could not acquire upload lock");
                } catch (Exception exception) {
                    class_4392.this.field_19852 = RealmsScreen.getLocalizedString("mco.download.failed");
                    exception.printStackTrace();
                } finally {
                    if (!field_19868.isHeldByCurrentThread()) {
                        return;
                    }
                    field_19868.unlock();
                    class_4392.this.field_19856 = false;
                    class_4392.this.field_19857 = true;
                }
            }
        }.start();
    }

    private void method_21279() {
        this.field_19853 = class_4392.getLocalizedString("mco.download.cancelled");
    }

    @Environment(value=EnvType.CLIENT)
    public class class_4393 {
        public volatile Long field_19871 = 0L;
        public volatile Long field_19872 = 0L;
    }
}

