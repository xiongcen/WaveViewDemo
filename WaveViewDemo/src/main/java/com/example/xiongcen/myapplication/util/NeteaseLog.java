package com.example.xiongcen.myapplication.util;


import android.text.TextUtils;
import android.util.Log;

import com.example.xiongcen.myapplication.BuildConfig;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 日志工具
 * 1.添加日志输出选项.控制日志输出位置
 * 2.添加文件日志功能.(因进程问题.现UI与Service只能打到不同的文件中)
 * 3.控制单个日志文件最大限制.由LOG_MAXSIZE常量控制,保留两个最新日志文件
 * 4.文件日志输出目标  /data/data/%packetname%/files/
 */
public class NeteaseLog {

    private static final String TAG = NeteaseLog.class.getName();
    private static final int TO_CONSOLE = 0x1;
    private static final int TO_SCREEN = 0x10;
    private static final int TO_FILE = 0x100;
    private static final String LOG_TEMP_FILE = "netease_log.temp";
    private static final String LOG_LAST_FILE = "netease_log_last.txt";
    private static final String LOG_NOW_FILE = "netease_log_now.txt";
    /**
     * log文件路径
     */
    private static final String LOG_PATH = "/data/data/%packetname%/files/";
    private final static boolean mUseTask = true;
    private static final Object mLockObj = new Object();
    private static final Calendar mDate = Calendar.getInstance();
    private static final StringBuffer mBuffer = new StringBuffer();
    public static boolean DEBUG = true;
    private static int WRITE_TO = BuildConfig.DEBUG ? TO_CONSOLE | TO_FILE : TO_FILE;
    private static int LOG_LEVEL = BuildConfig.DEBUG ? Log.VERBOSE : Log.INFO;
    private static long LOG_MAXSIZE = 1024 * 1024 * 4; //double the size temporarily
    private static boolean mOpenSystemLog = false;
    private static ExecutorService mExecutorService = Executors.newFixedThreadPool(1);

    private static OutputStream mLogStream;

    private static long mFileSize;

    private static String mPackagePath;

    private static PaintLogThread mPaintLogThread = null;

    private static Process mProcess = null;

    public static void initAppPath(String packetName) {
        mPackagePath = LOG_PATH.replaceFirst("%packetname%", packetName);
    }

    public static void v(String tag, String msg) {
        log(tag, msg, WRITE_TO, Log.VERBOSE);
    }

    public static void d(String tag, String msg) {
        log(tag, msg, WRITE_TO, Log.DEBUG);
    }

    public static void i(String tag, String msg) {
        log(tag, msg, WRITE_TO, Log.INFO);
    }

    public static void w(String tag, String msg) {
        log(tag, msg, WRITE_TO, Log.WARN);
    }

    public static void e(String tag, String msg) {
        log(tag, msg, WRITE_TO, Log.ERROR);
    }

    public static void e(String tag, Throwable throwable) {
        StringWriter sw = new StringWriter();
        throwable.printStackTrace(new PrintWriter(sw));
        e(tag, sw.toString());
    }

    private static void log(String tag, String msg, int outdest, int level) {
        if (TextUtils.isEmpty(mPackagePath)) {
            //未初始化
            return;
        }

        if (TextUtils.isEmpty(tag))
            tag = "TAG_NULL";
        if (TextUtils.isEmpty(msg))
            msg = "MSG_NULL";

        if (level >= LOG_LEVEL) {

            if ((outdest & TO_CONSOLE) != 0) {
                logToConsole(tag, msg, level);
            }

            if ((outdest & TO_SCREEN) != 0) {
                logToScreen(tag, msg, level);
            }

            if ((outdest & TO_FILE) != 0) {
                if (mUseTask) {
                    final String Tag = tag;
                    final String Msg = msg;
                    final int Level = level;
                    try {
                        if (mExecutorService != null) {
                            mExecutorService.submit(new Runnable() {
                                @Override
                                public void run() {
                                    logToFile(Tag, Msg, Level);
                                }
                            });
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "log -> " + e.toString());
                    }
                } else {
                    logToFile(tag, msg, level);
                }
            }

            if (mOpenSystemLog) {
                startSystemLog();
            }
        }
    }

    /**
     * 组成Log字符串.添加时间信息.
     *
     * @param tag
     * @param msg
     * @return
     */
    private static String getFormatLog(String tag, String msg) {

        mDate.setTimeInMillis(System.currentTimeMillis());

        mBuffer.setLength(0);
        mBuffer.append("[");
        mBuffer.append(tag);
        mBuffer.append(" : ");
        mBuffer.append(mDate.get(Calendar.MONTH) + 1);
        mBuffer.append("-");
        mBuffer.append(mDate.get(Calendar.DATE));
        mBuffer.append(" ");
        mBuffer.append(mDate.get(Calendar.HOUR_OF_DAY));
        mBuffer.append(":");
        mBuffer.append(mDate.get(Calendar.MINUTE));
        mBuffer.append(":");
        mBuffer.append(mDate.get(Calendar.SECOND));
        mBuffer.append(":");
        mBuffer.append(mDate.get(Calendar.MILLISECOND));
        mBuffer.append("] ");
        mBuffer.append(msg);

        return mBuffer.toString();
    }

    /**
     * 将log打到控制台
     *
     * @param tag
     * @param msg
     * @param level
     */
    private static void logToConsole(String tag, String msg, int level) {
        switch (level) {
            case Log.VERBOSE:
                Log.v(tag, getFormatLog(tag, msg));
                break;
            case Log.DEBUG:
                Log.d(tag, getFormatLog(tag, msg));
                break;
            case Log.INFO:
                Log.i(tag, getFormatLog(tag, msg));
                break;
            case Log.WARN:
                Log.w(tag, getFormatLog(tag, msg));
                break;
            case Log.ERROR:
                Log.e(tag, getFormatLog(tag, msg));
                break;
            default:
                break;
        }
    }

    private static void logToScreen(String tag, String msg, int level) {

    }

    /**
     * 将log打到文件日志
     *
     * @param tag
     * @param msg
     * @param level
     */
    private static void logToFile(String tag, String msg, int level) {
        synchronized (mLockObj) {
            OutputStream outStream = openLogFileOutStream();

            if (outStream != null) {
                try {
                    if (mFileSize < LOG_MAXSIZE) {
                        byte[] d = getFormatLog(tag, msg).getBytes("utf-8");
                        outStream.write(d);
                        outStream.write("\r\n".getBytes());
                        outStream.flush();
                        mFileSize += d.length;
                        closeLogFileOutStream();
                    } else {
                        closeLogFileOutStream();
                        renameLogFile();
                        logToFile(tag, msg, level);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "logToFile -> " + e.toString());
                }
            } else {
                Log.w(TAG, "Log File open fail: [AppPath]=" + mPackagePath + ",[LogName]:");
            }
        }
    }

    /**
     * 获取日志临时文件输入流
     *
     * @return
     */
    private static OutputStream openLogFileOutStream() {
        if (mLogStream == null) {
            try {
                if (TextUtils.isEmpty(mPackagePath)) {
                    return null;
                }
                File file = openAbsoluteFile(LOG_TEMP_FILE);

                if (file == null) {
                    return null;
                }

                File parent = file.getParentFile();
                if (!parent.exists()) {
                    parent.mkdirs();
                }

                if (file.exists()) {
                    mLogStream = new FileOutputStream(file, true);
                    mFileSize = file.length();
                } else {
                    //	file.createNewFile();
                    mLogStream = new FileOutputStream(file);
                    mFileSize = 0;
                }
            } catch (IOException e) {
                Log.e(TAG, "openLogFileOutStream -> " + e.toString());
            }
        }
        return mLogStream;
    }


    private static File openAbsoluteFile(String name) {
        if (TextUtils.isEmpty(mPackagePath)) {
            return null;
        } else {
            File file = new File(mPackagePath + name);
            return file;
        }
    }

    /**
     * 关闭日志输出流
     */
    private static void closeLogFileOutStream() {
        try {
            if (mLogStream != null) {
                mLogStream.close();
                mLogStream = null;
                mFileSize = 0;
            }
        } catch (Exception e) {
            Log.e(TAG, "closeLogFileOutStream -> " + e.toString());
        }
    }


    /**
     * rename log file
     */
    private static void renameLogFile() {
        synchronized (mLockObj) {
            File file = openAbsoluteFile(LOG_TEMP_FILE);
            File destFile = openAbsoluteFile(LOG_LAST_FILE);

            if (destFile != null && destFile.exists()) {
                destFile.delete();
            }
            if (file != null) {
                file.renameTo(destFile);
            }
        }
    }

    public static void startSystemLog() {
        synchronized (mLockObj) {
            initProcess();
            if (mPaintLogThread == null) {
                NeteaseLog log = new NeteaseLog();
                mPaintLogThread = log.new PaintLogThread();
                mPaintLogThread.start();
            }
        }
    }

    public static void closeSystemLog() {
        synchronized (mLockObj) {
            destroyProcess();
            if (null != mPaintLogThread) {
                mPaintLogThread = null;
            }
        }
    }

    private static void initProcess() {
        synchronized (mLockObj) {
            mOpenSystemLog = true;
            try {
                if (mProcess == null) {
                    ArrayList<String> cmdLine = new ArrayList<String>();   //设置命令   logcat -d 读取日志
                    cmdLine.add("logcat");
                    cmdLine.add("-d");

                    mProcess = Runtime.getRuntime().exec(cmdLine.toArray(new String[cmdLine.size()]));   //捕获日志

//                        ArrayList<String> clearLog = new ArrayList<String>();  //设置命令  logcat -c 清除日志
//                        clearLog.add("logcat");
//                        clearLog.add("-c");
//                        Runtime.getRuntime().exec(clearLog.toArray(new String[clearLog.size()]));
                }
            } catch (Exception e) {
                Log.e(TAG, "initProcess -> " + e.toString());
            }
        }
    }

    private static void destroyProcess() {
        synchronized (mLockObj) {
            mOpenSystemLog = false;
            if (mProcess != null) {
                mProcess.destroy();
                mProcess = null;
            }
        }
    }

    public static void close() {
        if (null != mExecutorService) {
            mExecutorService.shutdown();
            mExecutorService = null;
        }
    }

    private static void printSystemLogToFile() {
        try {
            initProcess();

            if (mProcess == null || TextUtils.isEmpty(mPackagePath)) {
                return;
            }
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(mProcess.getInputStream()));    //将捕获内容转换为BufferedReader

            String line = null;
            while (mOpenSystemLog && (line = bufferedReader.readLine()) != null) {
                logToFile("SysLog", line, Log.VERBOSE);
            }
        } catch (Exception e) {
            Log.e(TAG, "printSystemLogToFile -> " + e.toString());
        }
    }

    /**
     * back now log file
     */
    private static void backLogFile() {
        synchronized (mLockObj) {
            try {
                closeLogFileOutStream();

                File destFile = openAbsoluteFile(LOG_NOW_FILE);

                if (destFile != null && destFile.exists()) {
                    destFile.delete();
                }

                try {
                    destFile.createNewFile();
                } catch (IOException e1) {
                    e1.printStackTrace();
                    return;
                }

                File srcFile1 = openAbsoluteFile(LOG_LAST_FILE);
                File srcFile2 = openAbsoluteFile(LOG_TEMP_FILE);

                FileUtil.mergerFile(srcFile1, srcFile2, destFile, true);

                openLogFileOutStream();

            } catch (IOException e) {
                Log.e(TAG, "backLogFile -> " + e.toString());
            }
        }
    }

    /**
     * 导出log文件
     *
     * @return
     */
    public static File exportLogFile() {
        initProcess();

        printSystemLogToFile();

        //backup ui log file
        backLogFile();

        destroyProcess();

        return openAbsoluteFile(LOG_NOW_FILE);
    }

    public static <K, V> void printMap(String tag, Map<K, V> map, String msgPrefix, boolean isDebug) {
        if (map == null || map.isEmpty()) {
            if (isDebug) {
                d(tag, "Map is null or empty!!");
            } else {
                i(tag, "Map is null or empty!!");
            }
        } else {
            if (msgPrefix == null) {
                msgPrefix = "";
            }
            Iterator<Map.Entry<K, V>> iterator = map.entrySet().iterator();
            while (iterator != null && iterator.hasNext()) {
                Map.Entry<K, V> entry = iterator.next();
                if (entry != null) {
                    if (isDebug) {
                        d(tag, msgPrefix + "(" + entry.getKey() + " : " + entry.getValue() + ")");
                    } else {
                        i(tag, msgPrefix + "(" + entry.getKey() + " : " + entry.getValue() + ")");
                    }
                }
            }
        }
    }

    public static void logStackTrace(String tag, int maxLine, boolean isDebug) {
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        for (int i = 2, size = Math.min(stackTraceElements.length, maxLine); i < size; i++) {
            if (stackTraceElements[i] != null) {
                if (isDebug) {
                    d(tag, stackTraceElements[i].toString());
                } else {
                    i(tag, stackTraceElements[i].toString());
                }
            }
        }
    }

    /**
     * 打开或关闭系统日志打印到文件
     *
     * @param openSystemLog
     */
    public static void systemLogOpenOrNot(boolean openSystemLog) {
        NeteaseLog.i(TAG, "systemLogOpenOrNot:" + openSystemLog);
        mOpenSystemLog = openSystemLog;
        if (openSystemLog) {
            startSystemLog();
        } else {
            closeSystemLog();
        }
    }

    /**
     * 修改日志打印等级
     *
     * @param level
     */
    public static void modifyLogLevel(int level) {
        if (level < LOG_LEVEL) {
            LOG_LEVEL = level;
        }
    }

    /**
     * 修改日志打印文件大小
     *
     * @param size
     */
    public static void modifyLogSize(long size) {
        synchronized (mLockObj) {
            if (size > LOG_MAXSIZE) {
                NeteaseLog.i(TAG, "modifyLogSize:" + size);
                LOG_MAXSIZE = size;
            }
        }
    }

    /**
     * 将系统日志打印到文件
     */
    class PaintLogThread extends Thread {

        public void run() {
            printSystemLogToFile();
        }
    }
}
