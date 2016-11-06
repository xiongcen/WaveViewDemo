package com.example.xiongcen.myapplication.util;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FileUtil {

    private static final int BUFFERSIZE = 1024;


    /**
     * Return the file size by a recursion method.
     *
     * @param f: file name
     * @return size this file
     * @throws Exception
     */
    public static long getFileSize(File f) throws Exception {
        long size = 0;
        if (!f.exists()) return size;
        File flist[] = f.listFiles();
        for (int i = 0; i < flist.length; i++) {
            if (flist[i].isDirectory()) {
                size = size + getFileSize(flist[i]);
            } else {
                size = size + flist[i].length();
            }
        }
        return size;
    }


    /**
     * 当缓存大于100M时，显示>100M，不显示具体的数字
     *
     * @param fileS
     * @return
     */
    public static String FormetCacheFileSize(Context context, long fileS) {//转换文件大小
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        if (fileS < 1024) {
            fileSizeString = "0K";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "K";
        } else if (fileS < 1048576 * 100) {
            fileSizeString = df.format((double) fileS / 1048576) + "M";
        } else {
            fileSizeString = "大于100M";
        }
        return fileSizeString;
    }


    /**
     * Return true if the given filename is exits.
     *
     * @param filename should be an absolute path
     * @return
     */
    public static boolean isFileExit(String filename) {
        boolean s = false;

        try {
            File file = new File(filename);
            s = file.exists();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return s;
    }

    /**
     * Delete the file tree.
     *
     * @param file
     * @return true if file does not exist or delete successfully. false if there is an Exception during operation or sdcard is not mounted.
     */
    public static boolean deleteFile(File file) {
//		if(SystemUtils.isSDCardMounted()) {
        if (file.exists()) {
            try {
                if (file.isFile()) {
                    file.delete();
                } else if (file.isDirectory()) {
                    File files[] = file.listFiles();
                    for (int i = 0; i < files.length; i++) {
                        deleteFile(files[i]);
                    }
                    file.delete();
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            return true;
        } else {
            return true;
        }
//		} else return false;

    }


    /**
     * Create a file by given file path
     *
     * @param filepath
     * @return true if file exist or create successfully, otherwise return false.
     */
    public static boolean createFile(String filepath) {

        File file = new File(filepath);


        if ((SystemUtils.isSDCardMounted() || SystemUtils.isSDCardMountedReadOnly()) && file.exists()) {
            return true;
        } else {
            try {
                if (SystemUtils.isSDCardMounted()) {
                    file.getParentFile().mkdirs();
                    return file.createNewFile();
                } else return false;

            } catch (Exception e) {
                return false;
            }
        }
    }


    /**
     * Delete the file with spec file name
     *
     * @param strFileName
     */
    public static void delFile(String strFileName) {
        if (SystemUtils.isSDCardMounted()) {
            try {
                File myFile = new File(strFileName);
                if (myFile.exists()) {
                    myFile.delete();
                }
            } catch (Exception e) {
            }
        }

    }


    /**
     * 复制文件
     *
     * @param fromFile
     * @param toFile
     * @return
     */
    public static boolean copyFile(String fromFile, String toFile) {
        if (TextUtils.isEmpty(fromFile) || TextUtils.isEmpty(toFile)) {
            return false;
        }

        try {
            BufferedInputStream fosFrom = new BufferedInputStream(new FileInputStream(fromFile));
            createFile(toFile);
            BufferedOutputStream fosTo = new BufferedOutputStream(new FileOutputStream(toFile));
            byte bt[] = new byte[1024];
            int c;
            while ((c = fosFrom.read(bt)) > 0) {
                fosTo.write(bt, 0, c);
            }
            fosFrom.close();
            fosTo.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static File getExternalCacheDir(Context context) {

        if (hasExternalCacheDir()) {
            File cacheDir = context.getExternalCacheDir();
            if (cacheDir != null) {
                return cacheDir;
            }
        }


        // Before Froyo we need to construct the external cache dir ourselves
        final String cacheDir = "/Android/data/" + context.getPackageName() + "/cache/";
        return new File(Environment.getExternalStorageDirectory().getPath() + cacheDir);
    }

    public static boolean hasExternalCacheDir() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO;
    }

    /**
     * 以行为单位读取文件，常用于读面向行的格式化文件
     */
    public static String readFileByLines(String fileName) {
        File file = new File(fileName);
        BufferedReader reader = null;
        StringBuilder stringBuilder = new StringBuilder();
        try {
//            System.out.println("以行为单位读取文件内容，一次读一整行：");
            reader = new BufferedReader(new FileReader(file));
            int line = 1;
            // 一次读入一行，直到读入null为文件结束
            String tem;
            while ((tem = reader.readLine()) != null) {
                // 显示行号
//                System.out.println("line " + line + ": " + content);
                stringBuilder.append(tem).append("\n");
                line++;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
        return stringBuilder.toString();
    }

    /**
     * 将src1 & src2的内容合并到dest文件内
     *
     * @param src1
     * @param src2
     * @param dest
     * @param destAppend
     * @throws IOException
     */
    public static void mergerFile(File src1, File src2, File dest, boolean destAppend) throws IOException {

        if (dest.exists()) {
            dest.delete();
        }
        FileOutputStream out = new FileOutputStream(dest);
        byte[] temp = new byte[1024 * 10];

        if (src1.exists()) {
            writeData(src1, out, temp);
        }

        if (src2.exists()) {

            writeData(src2, out, temp);
        }

        out.close();
        out = null;

    }

    private static void writeData(File src2, FileOutputStream out, byte[] temp) throws IOException {
        long total;
        FileInputStream in;
        total = src2.length();
        in = new FileInputStream(src2);
        long count = 0;
        while (count < total) {
            int size = in.read(temp);
            out.write(temp, 0, size);
            count += size;
        }
        in.close();
    }

    public static boolean zip(File unZip, File zip) {
        if (!unZip.exists())
            return false;
        if (!zip.getParentFile().exists())
            zip.getParentFile().mkdir();

        try {
            FileInputStream in = new FileInputStream(unZip);
            FileOutputStream out = new FileOutputStream(zip);

            ZipOutputStream zipOut = new ZipOutputStream(out);

            // for buffer
            byte[] buf = new byte[BUFFERSIZE];

            int readCnt = 0;

            zipOut.putNextEntry(new ZipEntry(unZip.getName()));
            while ((readCnt = in.read(buf)) > 0) {
                zipOut.write(buf, 0, readCnt);
            }
            zipOut.closeEntry();

            in.close();
            zipOut.close();

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * 实现将多个文件进行压缩
     *
     * @param list
     * @param destZip
     * @return
     */
    public static boolean createZip(List<File> list, File destZip) {
        if (list == null || list.isEmpty() || destZip == null) {
            return false;
        }
        InputStream input = null;
        ZipOutputStream zipOut = null;
        try {
            if (destZip.exists()) {
                destZip.delete();
            }
            try {
                destZip.createNewFile();
            } catch (IOException e1) {
                e1.printStackTrace();
                NeteaseLog.d("FileUtil", "create destZip fail");
            }
            if (!destZip.getParentFile().exists()) {
                destZip.getParentFile().mkdir();
            }

            zipOut = new ZipOutputStream(new FileOutputStream(destZip));
            int length = list.size();
            File file = null;
            byte[] buf = new byte[4096];
            for (int i = 0; i < length; i++) {
                file = list.get(i);
                if (!file.isDirectory()) {
                    input = new FileInputStream(file);
                    zipOut.putNextEntry(new ZipEntry(file.getParentFile().getName() + File.separator + file.getName()));
                    int readCount = 0;
                    while ((readCount = input.read(buf)) > 0) {
                        zipOut.write(buf, 0, readCount);
                    }
                    zipOut.closeEntry();
                    input.close();
                }
            }
            zipOut.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            NeteaseLog.d("FileUtil", "createZip fail");
            return false;
        } finally {
            try {
                if (input != null) {
                    input.close();
                }
                if (zipOut != null) {
                    zipOut.close();
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }
}