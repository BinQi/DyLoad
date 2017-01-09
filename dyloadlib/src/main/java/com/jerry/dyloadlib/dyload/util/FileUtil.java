package com.jerry.dyloadlib.dyload.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Environment;
import android.text.TextUtils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Random;

/**
 * @author wubinqi
 */
public class FileUtil {
    /**
     * 将图片数据存入sd卡文件中
     *
     * @param iconByte
     *            待存入的文件路径
     * @param iconType
     *            图片类型，传入null，则默认以png为后缀
     * @return 存入后的文件路径，存入失败，则返回null
     */
    public static String saveIconToSDFile(final byte[] iconByte, String iconType, String dirPath) {
        Random random = new Random();
        int randomId = random.nextInt();
        String string2 = String.valueOf(randomId);
        String string3 = iconType;
        if (string3 == null) {
            string3 = ".png";
        }
        String pathString = dirPath + string2 + string3;
        boolean result = saveByteToSDFile(iconByte, pathString);
        if (result) {
            return pathString;
        } else {
            return null;
        }
    }

    /**
     * 保存位图到通用图片库中
     *
     * @param bitmap
     *            ：位图资源
     * @param fileName
     *            ：待保存文件名
     * @param iconFormat
     *            ：图片格式
     * @return true for 保存成功，false for 保存失败。
     */
    public static boolean saveBitmapToCommonIconSDFile(final Bitmap bitmap, final String fileName,
                                                       Bitmap.CompressFormat iconFormat, String dirPath) {
        String filePathName = dirPath;
        filePathName += fileName;
        return saveBitmapToSDFile(bitmap, filePathName, iconFormat);

    }

    /**
     * 保存位图到sd卡目录下
     *
     * @param bitmap
     *            ：位图资源
     * @param filePathName
     *            ：待保存的文件完整路径名
     * @param iconFormat
     *            ：图片格式
     * @return true for 保存成功，false for 保存失败。
     */
    public static boolean saveBitmapToSDFile(final Bitmap bitmap, final String filePathName,
                                             Bitmap.CompressFormat iconFormat) {
        boolean result = false;
        if (bitmap == null || bitmap.isRecycled()) {
            return result;
        }
        try {
            createNewFile(filePathName, false);
            OutputStream outputStream = new FileOutputStream(filePathName);
            result = bitmap.compress(iconFormat, 100, outputStream);
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * 保存输入流内容到SD卡文件
     * @param inputStream
     * @param filePathName 待保存的文件完整路径名
     * @return
     */
    public static boolean saveInputStreamToSDFile(InputStream inputStream,
                                                  String filePathName) {
        if (null == inputStream) {
            return false;
        }

        boolean result = false;
        OutputStream os = null;
        try {
            File file = createNewFile(filePathName, false);
            os = new FileOutputStream(file);
            byte buffer[] = new byte[4 * 1024];
            int len = 0;
            while ((len = inputStream.read(buffer)) != -1) {
                os.write(buffer, 0, len);
            }
            os.flush();
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                os.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 安全地（使用临时文件）保存输入流内容到SD卡文件
     * @param inputStream
     * @param filePathName 待保存的文件完整路径名
     * @return
     */
    public static boolean saveInputStreamToSDFileSafely(InputStream inputStream,
                                                        String filePathName) {
        if (null == inputStream) {
            return false;
        }

        boolean result = false;
        OutputStream os = null;
        try {
            String tempFilePathName = filePathName + "-temp";
            File file = createNewFile(tempFilePathName, false);
            os = new FileOutputStream(file);
            byte buffer[] = new byte[4 * 1024];
            int len = 0;
            while ((len = inputStream.read(buffer)) != -1) {
                os.write(buffer, 0, len);
            }
            os.flush();
            file.renameTo(new File(filePathName));
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                os.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     *
     * @param byteData
     * @param fileName
     * @return
     */
    public static boolean saveByteToCommonIconSDFile(final byte[] byteData, final String fileName, String dirPath) {
        String filePathName = dirPath;
        filePathName += fileName;
        return saveByteToSDFile(byteData, filePathName);
    }

    /**
     * 保存数据到指定文件
     *
     * @param byteData
     * @param filePathName
     * @return true for save successful, false for save failed.
     */
    public static boolean saveByteToSDFile(final byte[] byteData, final String filePathName) {
        boolean result = false;
        try {
            File newFile = createNewFile(filePathName, false);
            FileOutputStream fileOutputStream = new FileOutputStream(newFile);
            // BufferedOutputStream outputStream = new
            // BufferedOutputStream(fileOutputStream);
            fileOutputStream.write(byteData);
            fileOutputStream.flush();
            fileOutputStream.close();
            result = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static byte[] getByteFromSDFile(final String filePathName) {
        byte[] bs = null;
        try {
            File newFile = new File(filePathName);
            FileInputStream fileInputStream = new FileInputStream(newFile);
            DataInputStream dataInputStream = new DataInputStream(fileInputStream);
            BufferedInputStream inPutStream = new BufferedInputStream(dataInputStream);
            bs = new byte[(int) newFile.length()];
            inPutStream.read(bs);
            fileInputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bs;
    }

    /**
     *
     * @param path
     *            ：文件路径
     * @param append
     *            ：若存在是否插入原文件
     * @return 成功返回file文件，失败返回null
     */
    public static File createNewFile(String path, boolean append) {
        if (null == path) {
            return null;
        }
        File newFile = new File(path);
        if (!append) {
            if (newFile.exists()) {
                newFile.delete();
            }
        }
        if (!newFile.exists()) {
            try {
                File parent = newFile.getParentFile();
                if (parent != null && !parent.exists()) {
                    parent.mkdirs();
                }
                newFile.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return newFile.exists() && newFile.isFile() ? newFile : null;
    }

    /**
     * 创建文件夹
     * @param directoryPath 文件夹路径
     * @return
     */
    public static File createDirectory(String directoryPath) {
        if (null == directoryPath) {
            return null;
        }
        File directoryFile = new File(directoryPath);
        directoryFile.mkdirs();
        if (!directoryFile.exists() || !directoryFile.isDirectory()) {
            return null;
        }
        return directoryFile.exists() && directoryFile.isDirectory() ? directoryFile : null;
    }

    /**
     * sd卡是否可读写
     *
     * @return
     */
    public static boolean isSDCardAvaiable() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    /**
     * 指定路径文件是否存在
     *
     * @param filePath
     * @return
     */
    public static boolean isFileExist(String filePath) {
        boolean result = false;
        try {
            File file = new File(filePath);
            result = file.exists();
            file = null;
        } catch (Exception e) {
        }
        return result;
    }

    /**
     * 如果filePath表示的是一个文件，则删除文件。如果filePath表示的是一个目录，则删除目录及目录下的子目录和文件
     *
     * @param filePath
     *            文件路径
     */
    public static void delFile(String filePath) {
        if (null == filePath) {
            return;
        }
        File file = new File(filePath);
        if (file != null && file.exists()) { // 文件是否存在
            if (file.isFile()) { // 如果是文件
                file.delete();
            } else if (file.isDirectory()) { // 如果是目录
                File[] subFiles = file.listFiles();
                int length = subFiles != null ? subFiles.length : 0;
                for (int i = 0; i < length; i++) {
                    File subFile = subFiles[i];
                    if (subFile.isDirectory()) {
                        delFile(subFile.getAbsolutePath()); // 递归调用del方法删除子目录和子文件
                    }
                    subFile.delete();
                }
                file.delete();
            }
        }
    }

    /**
     * 获取文件属性
     * @param fileName
     * @return
     */
    public static String getFileOption(final String fileName) {
        String command = "ls -l " + fileName;
        StringBuffer sbResult = new StringBuffer();
        try {
            java.lang.Process proc = Runtime.getRuntime().exec(command);
            InputStream input = proc.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(input));
            String tmpStr = null;
            while ((tmpStr = br.readLine()) != null) {
                sbResult.append(tmpStr);
            }
            if (input != null) {
                input.close();
            }
            if (br != null) {
                br.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return sbResult.toString();
    }

    /**
     * 拷贝文件到指定目录
     * @param src
     * @param dstDir 目标目录， 尾部带路径分隔符
     */
    public static void copyFile2Dir(String src, String dstDir) {
        if (!isFileExist(src)) {
            return;
        }
        File srcFile = new File(src);
        String fileName = srcFile.getName();
        copyFile(src, dstDir + fileName);
    }

    /**
     * 拷贝文件
     * @param src
     * @param dst
     */
    public static void copyFile(String src, String dst) {
        if (!isFileExist(src)) {
            return;
        }

        FileInputStream fis = null;
        FileOutputStream fos = null;

        byte[] buffer = new byte[1024];
        int len = 0;
        try {
            fis = new FileInputStream(src);
            fos = new FileOutputStream(FileUtil.createNewFile(dst, false));

            while ((len = fis.read(buffer)) > 0) {
                fos.write(buffer, 0, len);
            }
            fos.flush();
        } catch (Exception e) {
            if (fis != null) {
                try {
                    fis.close();
                } catch (Exception e2) {
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (Exception e2) {
                }
            }
        }
    }

    /**
     *  读取res/raw目录下的txt文件
     *
     * @param context
     * @param rawResName raw文件名称(不带后缀)
     * @param defaultValue 默认值
     * @return
     */
    public static String readRawTxt(Context context, String rawResName, String defaultValue) {
        // 从资源获取流
        Resources res = context.getResources();
        int rawResId = res.getIdentifier(rawResName, "raw", context.getPackageName());
        return readRawTxt(context, rawResId, defaultValue);
    }

    /**
     *  读取res/raw目录下的txt文件
     *
     * @param context
     * @param rawResId raw文件资源id
     * @param defaultValue 默认值
     * @return
     */
    public static String readRawTxt(Context context, int rawResId, String defaultValue) {
        String rawTxtString = defaultValue;
        if (null == context) {
            return rawTxtString;
        }

        // 从资源获取流
        InputStream is = null;
        try {
            is = context.getResources().openRawResource(rawResId);
        } catch (Exception e) {
            e.printStackTrace();
            return rawTxtString;
        }
        try {
            byte[] buffer = new byte[64];
            int len = is.read(buffer); // 读取流内容
            if (len > 0) {
                rawTxtString = new String(buffer, 0, len).trim(); // 生成字符串
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return rawTxtString;
    }

    /**
     * 从sdcard读取文件
     * @param filePathName 文件路径
     * @return
     */
    public static byte[] readByteFromSDFile(final String filePathName) {
        byte[] bs = null;
        try {
            File newFile = new File(filePathName);
            FileInputStream fileInputStream = new FileInputStream(newFile);
            DataInputStream dataInputStream = new DataInputStream(fileInputStream);
            BufferedInputStream inPutStream = new BufferedInputStream(dataInputStream);
            bs = new byte[(int) newFile.length()];
            inPutStream.read(bs);
            fileInputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bs;
    }

    /**
     * 从sdcard读取文件
     *
     * @param filePath 文件路径
     * @return
     */
    public static String readFileToString(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return null;
        }
        File file = new File(filePath);
        if (!file.exists()) {
            return null;
        }
        try {
            InputStream inputStream = new FileInputStream(file);
            return readInputStream(inputStream, "UTF-8");
        } catch (Exception e) {
//			e.printStackTrace();
        }

        return null;
    }

    /**
     * 读取输入流,转为字符串
     *
     * @param in
     * @param charset 字符格式
     * @return
     * @throws IOException
     */
    public static String readInputStream(InputStream in, String charset) throws IOException {
        if (in == null) {
            return "";
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        final int bufferLength = 1024;
        byte[] data;
        try {
            byte[] buf = new byte[bufferLength];
            int len = 0;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            data = out.toByteArray();
            return new String(data, TextUtils.isEmpty(charset) ? "UTF-8" : charset);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
        }
        return null;
    }

    /**
     * 读取stream并返回一个前length长的string
     * @param in
     * @param charset
     * @param length
     * @return
     * @throws IOException
     */
    public static String readInputStreamWithLength(InputStream in, String charset, int length) throws IOException {
        if (in == null) {
            return "";
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        final int bufferLength = 1024;
        byte[] data;
        try {
            byte[] buf = new byte[bufferLength];
            int len = 0;
            int i = 0;
            while ((len = in.read(buf)) > 0 && i < length) {
                out.write(buf, 0, len);
                i++;
            }
            data = out.toByteArray();
            return new String(data, TextUtils.isEmpty(charset) ? "UTF-8" : charset);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
        }
        return null;
    }

    /**
     * 追加到文件末尾
     *
     * @param fileName
     * @param content
     */
    public static void append2File(String fileName, String content) {
        try {
            // 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
            FileWriter writer = new FileWriter(fileName, true);
            writer.write(content);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

