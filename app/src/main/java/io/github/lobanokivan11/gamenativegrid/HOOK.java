package io.github.lobanokivan11.gamenativegrid;

import android.annotation.SuppressLint;
import android.os.Build;
import android.util.Log;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.security.KeyStore;
import java.util.Arrays;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

@SuppressLint("DiscouragedPrivateApi")
@SuppressWarnings("ConstantConditions")
public class HOOK implements IXposedHookLoadPackage {

    private static final String TAG = HOOK.class.getSimpleName();

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
	    if (!lpparam.packageName.equals("app.gamenative")) return;
	    try {
        	Class<?> buildConfigClass = XposedHelpers.findClass(
			lpparam.packageName + ".BuildConfig",
			lpparam.classLoader
  	        );
		BufferedReader br = new BufferedReader(new FileReader("/sdcard/steamgriddb_key.txt"));
		String secretKey = br.readLine();
		br.close();
		if (secretKey != null && !secretKey.isEmpty()) {
			XposedHelpers.setStaticObjectField(buildConfigClass, "STEAMGRIDDB_API_KEY", secretKey.trim());
		}
	    } catch (XposedHelpers.ClassNotFoundError e) {
	        XposedBridge.log("BuildConfig class not found for " + lpparam.packageName);
	    } catch (IOException e) {
	        XposedBridge.log("Error on Reading Api Key! Please Check /sdcard/steamgriddb_key.txt file for mistakes" + e.getMessage());
	    } catch (NoSuchFieldError e) {
	        XposedBridge.log("Field not found in BuildConfig");
	    }
	}
}
