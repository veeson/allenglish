package com.lws.allenglish.util;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URISyntaxException;
import java.nio.charset.Charset;

/**
 * Created by Wilson on 2016/6/19.
 */
public class FileUtils {

    /**
     * 获取文件路径
     *
     * @param context
     * @param uri
     * @return
     * @throws URISyntaxException
     */
    public static String getPath(Context context, Uri uri) throws URISyntaxException {
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {"_data"};
            Cursor cursor = null;

            try {
                cursor = context.getContentResolver().query(uri, projection, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
                // Eat it
            } finally {
                if (cursor != null && !cursor.isClosed()) {
                    cursor.close();
                }
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static String readFile(String file, String csName)
            throws IOException {
        Charset cs = Charset.forName(csName);
        return readFile(file, cs);
    }

    /**
     * 读取文件里面的内容并返回String
     *
     * @param file
     * @param cs
     * @return
     * @throws IOException
     */
    public static String readFile(String file, Charset cs)
            throws IOException {
        // No real need to close the BufferedReader/InputStreamReader
        // as they're only wrapping the stream
        FileInputStream stream = new FileInputStream(file);
        try {
            Reader reader = new BufferedReader(new InputStreamReader(stream, cs));
            StringBuilder builder = new StringBuilder();
            char[] buffer = new char[8192];
            int read;
            while ((read = reader.read(buffer, 0, buffer.length)) > 0) {
                builder.append(buffer, 0, read);
            }
            return builder.toString();
        } finally {
            // Potential issue here: if this throws an IOException,
            // it will mask any others. Normally I'd use a utility
            // method which would log exceptions and swallow them
            stream.close();
        }
    }

    /**
     * 判断.txt文本的编码格式
     *
     * @param path
     * @return
     * @throws Exception
     */
    public static String resolveCode(String path) throws Exception {
        InputStream inputStream = new FileInputStream(path);
        byte[] head = new byte[3];
        inputStream.read(head);
        String code = "gb2312";  //或GBK
        if (head[0] == -1 && head[1] == -2)
            code = "UTF-16";
        else if (head[0] == -2 && head[1] == -1)
            code = "Unicode";
        else if (head[0] == -17 && head[1] == -69 && head[2] == -65)
            code = "UTF-8";

        inputStream.close();
        return code;
    }
}
