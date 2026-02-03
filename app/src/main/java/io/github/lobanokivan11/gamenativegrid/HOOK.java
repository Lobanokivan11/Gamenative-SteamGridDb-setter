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

        XposedHelpers.findAndHookMethod(Application.class, "onCreate", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                try {
                    Application app = (Application) param.thisObject;
                    ClassLoader cl = app.getClassLoader();
                    
                    Class<?> buildConfigClass = null;
                    String[] possiblePaths = {"app.gamenative.BuildConfig", "app.gamenative.base.BuildConfig"};
                    
                    for (String path : possiblePaths) {
                        try {
                            buildConfigClass = XposedHelpers.findClass(path, cl);
                            break;
                        } catch (XposedHelpers.ClassNotFoundError ignored) {}
                    }

                    if (buildConfigClass == null) {
                        XposedBridge.log("BuildConfig not found in any known path");
                        return;
                    }

                    BufferedReader br = new BufferedReader(new FileReader("/sdcard/steamgriddb_key.txt"));
                    String secretKey = br.readLine();
                    br.close();

                    if (secretKey != null && !secretKey.isEmpty()) {
                        XposedHelpers.setStaticObjectField(buildConfigClass, "STEAMGRIDDB_API_KEY", secretKey.trim());
                        XposedBridge.log("SteamGridDB API Key successfully set!");
                    }
                } catch (XposedHelpers.ClassNotFoundError e) {
                    XposedBridge.log("BuildConfig class not found");
                } catch (IOException e) {
                    XposedBridge.log("Error reading API Key file: " + e.getMessage());
                } catch (NoSuchFieldError e) {
                    XposedBridge.log("Field STEAMGRIDDB_API_KEY not found in BuildConfig");
                } catch (Exception e) {
                    XposedBridge.log("Hook error: " + e.getMessage());
                }
            }
        });
    }
}
