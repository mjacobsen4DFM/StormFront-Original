package com.DFM.StormFront.Util;

import org.apache.commons.lang.SystemUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ResourceBundle;

/**
 * Created by Mick on 4/16/2016.
 */
public class FileUtil {
    public static String getXsltDir() {
        ResourceBundle config = ResourceBundle.getBundle("config");
        return (SystemUtils.IS_OS_LINUX) ? config.getString("xsltDir_linux") : config.getString("xsltDir_windows");
    }

    public static String getLogDir() {
        ResourceBundle config = ResourceBundle.getBundle("config");
        return (SystemUtils.IS_OS_LINUX) ? config.getString("logDir_linux") : config.getString("logDir_windows");
    }

    public static String readFile(String inputFile) throws Exception {
        String text = "";
        BufferedReader br = null;
        StringBuilder sb;
        try {
            br = new BufferedReader(new FileReader(inputFile));
            sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
            text = sb.toString();
        } catch (IOException e) {
            throw new Exception(ExceptionUtil.getFullStackTrace(e));
        }
        finally {
           if(null != br){  br.close(); }
        }
        return text.trim();
    }

    public static void printFile(String debugdir, String basename, String uniquename, String category, String extension, String content) throws Exception {
        try {
            long nt = System.nanoTime();
            //PrintWriter out = new PrintWriter(debugdir + _subscriberMap.get("catname") + "_" + wpp.getGuid().replace("\\", "_").replace("/", "_").replace(":", "_").replaceAll("\\s", "-") + "_" + tt + "_sStory.xml");
            PrintWriter out = new PrintWriter(debugdir + basename + "_" + uniquename.replace("\\", "_").replace("/", "_").replace(":", "_").replaceAll("\\s", "-") + "_" + category + "_" + nt + "." + extension);
            out.println(content);
            out.close();
        } catch (Exception e) {
            throw new Exception(ExceptionUtil.getFullStackTrace(e));
        }
    }
}
