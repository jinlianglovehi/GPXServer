package com.huami.irun;

import android.content.Context;
import android.os.Environment;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.huami.irun", appContext.getPackageName());

        JSONToGPXWriter jsonToGPXWriter = new JSONToGPXWriter() ;

    }

    /**
     * huqou test data
     */
    private void test(){
        try {
            if (Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED)) {
                // 获取SD卡的目录
                File sdCardDir = Environment.getExternalStorageDirectory();
                String path = "/testgpx/";
                File dir = new File(path);
                if (!dir.exists()) {
                    dir.mkdirs();
                }

                    File targetFile = new File(sdCardDir.getCanonicalPath() + path + "aaa.txt");

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
