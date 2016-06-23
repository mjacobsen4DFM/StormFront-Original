package com.DFM.StormFront.Util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by Mick on 4/16/2016.
 */
public class FileUtil {
    private static String _mainPathDev = "C:\\Users\\Mick\\Documents\\Cloud\\Google Drive\\mjacobsen@denverpost.com\\Dev\\mason\\share\\Projects\\Java\\StormFront\\Resources\\";
    private static String _xsltPathDev = String.format("%s%s", _mainPathDev, "xslt\\");
    private static String _mainPathLive = "/etc/storm/transactstorm/";
    private static String _xsltPathLive = String.format("%s%s",_mainPathLive, "conf/xslt/");

    public static String getXsltPath() {
        return (SystemUtil.getHostname().equalsIgnoreCase("jacobsen2016")) ? _xsltPathDev : _xsltPathLive;
    }

    public static String getLogPath() {
        return (SystemUtil.getHostname().equalsIgnoreCase("jacobsen2016")) ? _mainPathDev : _mainPathLive;
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
