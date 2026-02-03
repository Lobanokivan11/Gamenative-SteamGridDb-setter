package io.github.lobanokivan11.gamenativegrid;

import android.annotation.SuppressLint;
import android.app.Application;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

@SuppressLint("DiscouragedPrivateApi")
public class HOOK implements IXposedHookLoadPackage {

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (!lpparam.packageName.equals("app.gamenative")) return;
        String keyFromFile = null;
        try {
            BufferedReader br = new BufferedReader(new FileReader("/sdcard/steamgriddb_key.txt"));
            keyFromFile = br.readLine();
            br.close();
            if (keyFromFile != null) keyFromFile = keyFromFile.trim();
        } catch (Exception e) {
            XposedBridge.log("GamenativeGrid: Error reading file: " + e.getMessage());
        }
        if (keyFromFile == null || keyFromFile.isEmpty()) return;
        final String finalKey = keyFromFile;
        XposedHelpers.findAndHookMethod("app.gamenative.utils.SteamGridDB", lpparam.classLoader, "getApiKey", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                param.setResult(finalKey);
                XposedBridge.log("GamenativeGrid: Key injected via getApiKey()");
            }
        });
    }
}
