package com.example.xiongcen.myapplication.util;


import android.support.annotation.NonNull;
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

    private final static boolean bUseTask = true;
    private static final String TAG = NeteaseLog.class.getName();

    public static boolean DEBUG = true;

    private static final int TO_CONSOLE = 0x1;

    private static final int TO_SCREEN = 0x10;

    private static final int TO_FILE = 0x100;

    private static final int FROM_LOGCAT = 0x1000;

    private static final int DEBUG_ALL = TO_CONSOLE | TO_SCREEN | TO_FILE /*| FROM_LOGCAT*/;

    private static final String LOG_TEMP_FILE = "netease_log.temp";
    private static final String LOG_LAST_FILE = "netease_log_last.txt";
    private static final String LOG_NOW_FILE = "netease_log_now.txt";

    private static final int LOG_LEVEL = BuildConfig.DEBUG ? Log.VERBOSE : Log.INFO;

    private static final int LOG_MAXSIZE = 1024 * 1024 * 4; //double the size temporarily

    private static final Object mLockObj = new Object();

    private static String mPackagePath;

    private static final Calendar mDate = Calendar.getInstance();
    private static final StringBuffer mBuffer = new StringBuffer();
    private static ExecutorService mExecutorService = Executors.newFixedThreadPool(1);


    private static OutputStream mLogStream;
    private static long mFileSize;

    /**
     * log文件路径
     */
    private static final String LOG_PATH = "/data/data/%packetname%/files/";


    private static PaintLogThread mPaintLogThread = null;

    public static String getAppPath() {
        if (mPackagePath != null) {
            NeteaseLog.d("mAppPath lockObj:", "" + mLockObj.hashCode());
            NeteaseLog.d("mAppPath hashCode:", "" + mPackagePath.hashCode());
        }
        return mPackagePath;
    }

    public static void initAppPath(String packetName) {
        mPackagePath = LOG_PATH.replaceFirst("%packetname%", packetName);
    }




    public static void log(String msg) {
        d(TAG, msg);
    }

    public static void log(Throwable throwable) {
        StringWriter sw = new StringWriter();
        throwable.printStackTrace(new PrintWriter(sw));
        NeteaseLog.w("Exception", sw.toString());
    }

    public static void d(String tag, String msg) {
        log(tag, msg, DEBUG_ALL, Log.DEBUG);
    }

    public static void v(String tag, String msg) {
        log(tag, msg, DEBUG_ALL, Log.VERBOSE);
    }

    public static void e(String tag, String msg) {
        log(tag, msg, DEBUG_ALL, Log.ERROR);
    }

    public static void i(String tag, String msg) {
        log(tag, msg, DEBUG_ALL, Log.INFO);
    }

    public static void w(String tag, String msg) {
        log(tag, msg, DEBUG_ALL, Log.WARN);
    }


    public static void d( String msg) {
        log(TAG, msg, DEBUG_ALL, Log.DEBUG);
    }

    public static void v( String msg) {
        log(TAG, msg, DEBUG_ALL, Log.VERBOSE);
    }

    public static void e(String msg) {
        log(TAG, msg, DEBUG_ALL, Log.ERROR);
    }

    public static void i(String msg) {
        log(TAG, msg, DEBUG_ALL, Log.INFO);
    }

    public static void w( String msg) {
        log(TAG, msg, DEBUG_ALL, Log.WARN);
    }



    private static void log(String tag, String msg, int outdest, int level) {
        if (mPackagePath == null) {//未初始化
            return;
        }

        if (tag == null)
            tag = "TAG_NULL";
        if (msg == null)
            msg = "MSG_NULL";
        if (level >= LOG_LEVEL) {
            if ((outdest & TO_CONSOLE) != 0) {
                logToConsole(tag, msg, level);
            }

            if ((outdest & TO_SCREEN) != 0) {
                logToScreen(tag, msg, level);
            }

            if ((outdest & FROM_LOGCAT) != 0) {
//                if (mPaintLogThread == null) {
//                    NeteaseLog log = new NeteaseLog();
//                    mPaintLogThread = log.new PaintLogThread();
//                    mPaintLogThread.start();
//                }
                start();

            }

            if ((outdest & TO_FILE) != 0) {
                if (bUseTask) {
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
                        e.printStackTrace();
                    } catch (Error e) {
                        e.printStackTrace();
                    }
                } else {
                    logToFile(tag, msg, level);
                }
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
            case Log.DEBUG:
                Log.d(tag, msg);
                break;
            case Log.ERROR:
                Log.e(tag, msg);
                break;
            case Log.INFO:
                Log.i(tag, msg);
                break;
            case Log.VERBOSE:
                Log.v(tag, msg);
                break;
            case Log.WARN:
                Log.w(tag, msg);
                break;
            default:
                break;
        }
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
                    byte[] d = getFormatLog(tag, msg).getBytes("utf-8");

                    if (mFileSize < LOG_MAXSIZE) {
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
                    e.printStackTrace();
                }

            } else {
                Log.w(TAG, "Log File open fail: [AppPath]=" + mPackagePath + ",[LogName]:");
            }
        }
    }

    private static void logToScreen(String tag, String msg, int level) {

    }

    /**
     * 获取日志临时文件输入流
     *
     * @return
     */
    @SuppressWarnings("hiding")
    private static OutputStream openLogFileOutStream() {
        if (mLogStream == null) {
            try {
                if (mPackagePath == null || mPackagePath.length() == 0) {
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
                e.printStackTrace();
            }
        }
        return mLogStream;
    }


    private static File openAbsoluteFile(String name) {
        if (mPackagePath == null || mPackagePath.length() == 0) {
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
            e.printStackTrace();
        }
    }


    /**
     * rename log file
     */
    private static void renameLogFile() {

        synchronized (mLockObj) {

            File file = openAbsoluteFile(LOG_TEMP_FILE);
            File destFile = openAbsoluteFile(LOG_LAST_FILE);

            if (destFile.exists()) {
                destFile.delete();
            }
            file.renameTo(destFile);
        }
    }

    public static void start() {
        synchronized (mLockObj) {
            if (mPaintLogThread == null) {
                NeteaseLog log = new NeteaseLog();
                mPaintLogThread = log.new PaintLogThread();
                mPaintLogThread.start();
            }
        }
    }

    public static void close() {

        if (mPaintLogThread != null) {
            mPaintLogThread.shutdown();
            mPaintLogThread = null;
        }

        if (null != mExecutorService) {
            mExecutorService.shutdown();
            mExecutorService = null;
        }
    }


    class PaintLogThread extends Thread {

        Process mProcess;
        boolean mStop = false;

        public void shutdown() {
            NeteaseLog.i("PaintLogThread:", "shutdown");
            mStop = true;
            if (mProcess != null) {
                mProcess.destroy();
                mProcess = null;
            }
        }

        public void run() {
            try {

                    ArrayList<String> cmdLine=new ArrayList<String>();   //设置命令   logcat -d 读取日志
                    cmdLine.add("logcat");
                    cmdLine.add("-d");

                    ArrayList<String> clearLog=new ArrayList<String>();  //设置命令  logcat -c 清除日志
                    clearLog.add("logcat");
                    clearLog.add("-c");

                     mProcess= Runtime.getRuntime().exec(cmdLine.toArray(new String[cmdLine.size()]));   //捕获日志
                    BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(mProcess.getInputStream()));    //将捕获内容转换为BufferedReader


                        String line = null;
                        while (!mStop) {
                            line = bufferedReader.readLine();
                            if (line != null && mPackagePath != null) {
                                logToFile("SysLog", line, Log.VERBOSE);
//                                Runtime.getRuntime().exec(clearLog.toArray(new String[clearLog.size()]));
                            } else {
                                if (line == null) {
                                    Log.i("PaintLogThread:", "readLine==null");
                                    break;
                                }


                            }
                        }


            } catch (Exception e) {

                e.printStackTrace();
                Log.d(TAG, "logcatToFile Exception:" + e.toString());
            }
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

                if (destFile.exists()) {
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
                e.printStackTrace();
                Log.w("RpmmsLog", "backLogFile fail:" + e.toString());
            }
        }
    }

    /**
     * 导出log文件
     * 耗时操作
     * @param zipFileName
     * @return
     */
    public static boolean zipLogFile(String zipFileName) {
        //backup ui log file
        backLogFile();

        File destFile = new File(zipFileName);
        if (destFile.exists()) {
            destFile.delete();
        }

        try {
            destFile.createNewFile();
        } catch (IOException e1) {
            e1.printStackTrace();
            return false;
        }

        File srcFile = openAbsoluteFile(LOG_NOW_FILE);
        boolean ret = FileUtil.zip(srcFile, destFile);


        destFile = null;
        srcFile = null;
        return ret;
    }

    public static void logStackTrace(@NonNull String tag, int maxLine) {
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        for (int i = 2, size = Math.min(stackTraceElements.length, maxLine); i < size; i++) {
            if (stackTraceElements[i] != null) {
                i(tag, stackTraceElements[i].toString());
            }
        }
    }
}
