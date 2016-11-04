package com.DFM.StormFront.Util;

import org.apache.commons.lang.SystemUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 * Created by Mick on 4/16/2016.
 */
public class FileUtil {
    public static String getXsltSourcePath(String location) {
        ResourceBundle config = ResourceBundle.getBundle("config");
        String xsltPath_name = String.format("xsltPath_%s", location.toLowerCase());
        return config.getString(xsltPath_name);
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

    public static String setImageFileExtension(String imageName, String mimeType) {
        List<String> mimeExtEntry = Collections.singletonList("unk");
        Map<String, List<String>> mimeExtensions = new HashMap<>();
        mimeExtensions.put("image/jpeg", Arrays.asList("jpg", "jpeg", "jpe"));
        mimeExtensions.put("image/gif", Collections.singletonList("gif"));
        mimeExtensions.put("image/png", Collections.singletonList("png"));
        mimeExtensions.put("image/pipeg", Collections.singletonList("jfif"));
        mimeExtensions.put("image/svg+xml", Collections.singletonList("svg"));
        mimeExtensions.put("image/tiff", Arrays.asList("tif", "tiff"));
        mimeExtensions.put("image/x-rgb", Collections.singletonList("rgb"));
        mimeExtensions.put("image/x-xbitmap", Collections.singletonList("xbm"));
        mimeExtensions.put("image/x-xpixmap", Collections.singletonList("xpm"));

        if(StringUtil.isNotNullOrEmpty(mimeType) && mimeExtensions.containsKey(mimeType)) {
            mimeExtEntry = mimeExtensions.get(mimeType);
        }

        int p = imageName.lastIndexOf(".");
        String ext = imageName.substring(p + 1);
        if (p == -1 || !ext.matches("\\w+")) {
            // file has no extension; add the default for this mime-type
            return String.format("%s.%s", imageName, mimeExtEntry.get(0));
        } else {
            // file has extension
            if(mimeExtEntry.contains(ext)){
                // the filename reflects the mime-type
                return imageName;
            } else if(mimeExtEntry.contains(ext.toLowerCase())){
                // the filename reflects the mime-type
                return imageName;
            } else{
                // the filename seems to be different from the mime-type; trust the mime-type
                String filename = imageName.substring(0, p);
                return String.format("%s.%s", filename, mimeExtEntry.get(0));
            }

        }
    }
}
