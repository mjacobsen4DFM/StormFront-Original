package com.DFM.StormFront.Util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ResourceBundle;

/**
 * Created by Mick on 4/22/2016.
 */
public class LogUtil {
    public Integer level = 0;
    public String logdir = "";

    public LogUtil() {
        ResourceBundle config = ResourceBundle.getBundle("config");
        this.level = Integer.parseInt(config.getString("loglevel"));
        this.logdir = FileUtil.getLogDir();
    }

    public static void log(String msg) {
        System.out.println(msg);
    }

    public void log(String msg, Integer importance) {
        if (this.level >= importance) {
            log(msg);
        }
    }

    public void log(String msg, String file, Integer importance) {
        if (this.level >= importance) {
            log(msg, file);
        }
    }

    public void log(String msg, String file) {
        log(msg);
        try (PrintWriter out = new PrintWriter(
                new FileOutputStream(
                        new File(String.format("%s%s", this.logdir, file)),
                        true)
        )
        ) {
            out.println(msg);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public enum levels {
        ERRORS(0), ESSENTIAL(1), INFO(2), DETAILS(3), NOISY(4);
        private Integer value;

        levels(Integer value) {
            this.value = value;
        }

        public Integer value() {
            return this.value;

        }
    }
}
