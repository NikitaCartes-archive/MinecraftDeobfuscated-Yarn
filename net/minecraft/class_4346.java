/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4344;
import net.minecraft.class_4354;

@Environment(value=EnvType.CLIENT)
public abstract class class_4346<T extends class_4346<T>> {
    protected HttpURLConnection field_19596;
    private boolean field_19598;
    protected String field_19597;

    public class_4346(String string, int i, int j) {
        try {
            this.field_19597 = string;
            Proxy proxy = class_4344.method_21034();
            this.field_19596 = proxy != null ? (HttpURLConnection)new URL(string).openConnection(proxy) : (HttpURLConnection)new URL(string).openConnection();
            this.field_19596.setConnectTimeout(i);
            this.field_19596.setReadTimeout(j);
        } catch (MalformedURLException malformedURLException) {
            throw new class_4354(malformedURLException.getMessage(), malformedURLException);
        } catch (IOException iOException) {
            throw new class_4354(iOException.getMessage(), iOException);
        }
    }

    public void method_21042(String string, String string2) {
        class_4346.method_21046(this.field_19596, string, string2);
    }

    public static void method_21046(HttpURLConnection httpURLConnection, String string, String string2) {
        String string3 = httpURLConnection.getRequestProperty("Cookie");
        if (string3 == null) {
            httpURLConnection.setRequestProperty("Cookie", string + "=" + string2);
        } else {
            httpURLConnection.setRequestProperty("Cookie", string3 + ";" + string + "=" + string2);
        }
    }

    public int method_21038() {
        return class_4346.method_21044(this.field_19596);
    }

    public static int method_21044(HttpURLConnection httpURLConnection) {
        String string = httpURLConnection.getHeaderField("Retry-After");
        try {
            return Integer.valueOf(string);
        } catch (Exception exception) {
            return 5;
        }
    }

    public int method_21047() {
        try {
            this.method_21054();
            return this.field_19596.getResponseCode();
        } catch (Exception exception) {
            throw new class_4354(exception.getMessage(), exception);
        }
    }

    public String method_21051() {
        try {
            this.method_21054();
            String string = null;
            string = this.method_21047() >= 400 ? this.method_21039(this.field_19596.getErrorStream()) : this.method_21039(this.field_19596.getInputStream());
            this.method_21056();
            return string;
        } catch (IOException iOException) {
            throw new class_4354(iOException.getMessage(), iOException);
        }
    }

    private String method_21039(InputStream inputStream) throws IOException {
        if (inputStream == null) {
            return "";
        }
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
        StringBuilder stringBuilder = new StringBuilder();
        int i = inputStreamReader.read();
        while (i != -1) {
            stringBuilder.append((char)i);
            i = inputStreamReader.read();
        }
        return stringBuilder.toString();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void method_21056() {
        byte[] bs = new byte[1024];
        try {
            int i = 0;
            InputStream inputStream = this.field_19596.getInputStream();
            while ((i = inputStream.read(bs)) > 0) {
            }
            inputStream.close();
        } catch (Exception exception) {
            int j;
            InputStream inputStream;
            block13: {
                inputStream = this.field_19596.getErrorStream();
                j = 0;
                if (inputStream != null) break block13;
                return;
            }
            try {
                while ((j = inputStream.read(bs)) > 0) {
                }
                inputStream.close();
            } catch (IOException iOException) {
                // empty catch block
            }
        } finally {
            if (this.field_19596 != null) {
                this.field_19596.disconnect();
            }
        }
    }

    protected T method_21054() {
        if (this.field_19598) {
            return (T)this;
        }
        T lv = this.method_21055();
        this.field_19598 = true;
        return lv;
    }

    protected abstract T method_21055();

    public static class_4346<?> method_21040(String string) {
        return new class_4348(string, 5000, 60000);
    }

    public static class_4346<?> method_21041(String string, int i, int j) {
        return new class_4348(string, i, j);
    }

    public static class_4346<?> method_21049(String string, String string2) {
        return new class_4349(string, string2, 5000, 60000);
    }

    public static class_4346<?> method_21043(String string, String string2, int i, int j) {
        return new class_4349(string, string2, i, j);
    }

    public static class_4346<?> method_21048(String string) {
        return new class_4347(string, 5000, 60000);
    }

    public static class_4346<?> method_21053(String string, String string2) {
        return new class_4350(string, string2, 5000, 60000);
    }

    public static class_4346<?> method_21050(String string, String string2, int i, int j) {
        return new class_4350(string, string2, i, j);
    }

    public String method_21052(String string) {
        return class_4346.method_21045(this.field_19596, string);
    }

    public static String method_21045(HttpURLConnection httpURLConnection, String string) {
        try {
            return httpURLConnection.getHeaderField(string);
        } catch (Exception exception) {
            return "";
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class class_4349
    extends class_4346<class_4349> {
        private final String field_19599;

        public class_4349(String string, String string2, int i, int j) {
            super(string, i, j);
            this.field_19599 = string2;
        }

        public class_4349 method_21059() {
            try {
                if (this.field_19599 != null) {
                    this.field_19596.setRequestProperty("Content-Type", "application/json; charset=utf-8");
                }
                this.field_19596.setDoInput(true);
                this.field_19596.setDoOutput(true);
                this.field_19596.setUseCaches(false);
                this.field_19596.setRequestMethod("POST");
                OutputStream outputStream = this.field_19596.getOutputStream();
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, "UTF-8");
                outputStreamWriter.write(this.field_19599);
                outputStreamWriter.close();
                outputStream.flush();
                return this;
            } catch (Exception exception) {
                throw new class_4354(exception.getMessage(), exception);
            }
        }

        @Override
        public /* synthetic */ class_4346 method_21055() {
            return this.method_21059();
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class class_4350
    extends class_4346<class_4350> {
        private final String field_19600;

        public class_4350(String string, String string2, int i, int j) {
            super(string, i, j);
            this.field_19600 = string2;
        }

        public class_4350 method_21060() {
            try {
                if (this.field_19600 != null) {
                    this.field_19596.setRequestProperty("Content-Type", "application/json; charset=utf-8");
                }
                this.field_19596.setDoOutput(true);
                this.field_19596.setDoInput(true);
                this.field_19596.setRequestMethod("PUT");
                OutputStream outputStream = this.field_19596.getOutputStream();
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, "UTF-8");
                outputStreamWriter.write(this.field_19600);
                outputStreamWriter.close();
                outputStream.flush();
                return this;
            } catch (Exception exception) {
                throw new class_4354(exception.getMessage(), exception);
            }
        }

        @Override
        public /* synthetic */ class_4346 method_21055() {
            return this.method_21060();
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class class_4348
    extends class_4346<class_4348> {
        public class_4348(String string, int i, int j) {
            super(string, i, j);
        }

        public class_4348 method_21058() {
            try {
                this.field_19596.setDoInput(true);
                this.field_19596.setDoOutput(true);
                this.field_19596.setUseCaches(false);
                this.field_19596.setRequestMethod("GET");
                return this;
            } catch (Exception exception) {
                throw new class_4354(exception.getMessage(), exception);
            }
        }

        @Override
        public /* synthetic */ class_4346 method_21055() {
            return this.method_21058();
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class class_4347
    extends class_4346<class_4347> {
        public class_4347(String string, int i, int j) {
            super(string, i, j);
        }

        public class_4347 method_21057() {
            try {
                this.field_19596.setDoOutput(true);
                this.field_19596.setRequestMethod("DELETE");
                this.field_19596.connect();
                return this;
            } catch (Exception exception) {
                throw new class_4354(exception.getMessage(), exception);
            }
        }

        @Override
        public /* synthetic */ class_4346 method_21055() {
            return this.method_21057();
        }
    }
}

