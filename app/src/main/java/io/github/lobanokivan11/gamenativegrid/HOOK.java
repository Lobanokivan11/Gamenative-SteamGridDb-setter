package io.github.lobanokivan11.gamenativegrid;

import android.app.AndroidAppHelper;
import android.content.Context;
import android.os.Environment;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class HOOK implements IXposedHookLoadPackage {
    private static final String TARGET_PKG = "app.gamenative";
    private String cachedKey = null;

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) {
        if (!lpparam.packageName.equals(TARGET_PKG)) return;

        XposedHelpers.findAndHookMethod("app.gamenative.utils.SteamGridDB", lpparam.classLoader, 
            "getApiKey", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) {
                String key = getOrReadKey();
                if (key != null) {
                    param.setResult(key);
                }
            }
        });
    }

    private String getOrReadKey() {
        if (cachedKey != null) return cachedKey;

        try {
            File keyFile = new File(Environment.getExternalStorageDirectory(), "steamgriddb_key.txt");
            
            if (keyFile.exists()) {
                byte[] encoded = Files.readAllBytes(keyFile.toPath());
                cachedKey = new String(encoded, StandardCharsets.UTF_8).trim();
                XposedBridge.log("GamenativeGrid: Key loaded from SDCard");
            }
        } catch (Exception e) {
            XposedBridge.log("GamenativeGrid: Read error: " + e.getMessage());
        }
        return cachedKey;
    }
}
