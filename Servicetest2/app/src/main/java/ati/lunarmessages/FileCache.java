//package ati.lunarmessages;
//
//import android.content.Context;
//
//import java.io.File;
//
///**
// * Created by I021059 on 2015.08.02..
// */
//public class FileCache
//{
//    private File cacheDir;
//
//    public FileCache(Context context)
//    {
//        cacheDir = context.getCacheDir();
//        if (!cacheDir.exists())
//            cacheDir.mkdirs();
//    }
//
//    public FileCache(Context context, long evt)
//    {
//        //Find the dir to save cached images
//        cacheDir = context.getCacheDir();
//        if (!cacheDir.exists())
//            cacheDir.mkdirs();
//    }
//
//    public File getFile(String url)
//    {
//        return new File(cacheDir, String.valueOf(url.hashCode()));
//    }
//
//    public void clear()
//    {
//        File[] files = cacheDir.listFiles();
//        if (files == null)
//            return;
//        for (File f : files)
//            f.delete();
//    }
//}