/*
 *
 *
 *  * Copyright (c) 2017 Leo Lee(lichl.1980@163.com).
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 *  * use this file except in compliance with the License. You may obtain a copy
 *  * of the License at
 *  *
 *  *   http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 *  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 *  * License for the specific language governing permissions and limitations
 *  * under the License.
 *  *
 *
 */

package org.sunyata.quark.util;

/**
 * Created by leo on 17/3/28.
 */

import org.sunyata.quark.ioc.ScanFilter;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class PackageUtils {
    private static final String JAR_FILE_EXTENSION = ".jar";
    private static final String ZIP_SLASH = "/";
    private static final String BLACK = "";
    private static final String CLASS_EXTENSION = ".class";
    private static final ScanFilter NULL_CLASS_FILTER = null;
    private static FileFilter fileFilter = pathname -> isClass(pathname.getName()) || isDirectory(pathname) ||
            isJarFile(pathname.getName());

    private static void ckeckPackageName(String packageName) {
        if (packageName == null || packageName.trim().length() == 0)
            throw new NullPointerException("package Name canot be null");
    }

    private static String getWellFormedPackageName(String packageName) {
        return packageName.lastIndexOf(".") != packageName.length() - 1 ? packageName + "." : packageName;
    }

    public static List<Class> scan(String packageName, ScanFilter scanFilter) {

        ckeckPackageName(packageName);

        final List<Class> classes = new ArrayList<Class>();

        for (String classPath : getClassPathArray()) {
            selectClasses(new File(classPath), getWellFormedPackageName(packageName), scanFilter, classes);
        }
        return classes;
    }

    public static List<Class> scan(String packageName) {
        return scan(packageName, NULL_CLASS_FILTER);
    }


    private static void selectClasses(File file, String packageName, ScanFilter scanFilter, List<Class> classes) {
        if (isDirectory(file)) {
            processDirectory(file, packageName, scanFilter, classes);
        } else if (isClass(file.getName())) {
            processClassFile(file, packageName, scanFilter, classes);
        } else if (isJarFile(file.getName())) {
            processJarFile(file, packageName, scanFilter, classes);
        }
    }


    private static void processDirectory(File directory, String packageName, ScanFilter scanFilter, List<Class>
            classes) {
        for (File file : directory.listFiles(fileFilter)) {
            selectClasses(file, packageName, scanFilter, classes);
        }
    }

    private static void processClassFile(File file, String packageName, ScanFilter scanFilter, List<Class> classes) {
        final String filePathWithDot = file.getAbsolutePath().replace(File.separator, ".");
        int subIndex = -1;
        if ((subIndex = filePathWithDot.indexOf(packageName)) != -1) {
            final String className = filePathWithDot.substring(subIndex).replace(CLASS_EXTENSION, BLACK);
            selectClass(className, packageName, classes, scanFilter);
        }
    }


    private static void processJarFile(File file, String packageName, ScanFilter scanFilter, List<Class> classes) {
        try {
            for (ZipEntry entry : Collections.list(new ZipFile(file).entries())) {
                if (isClass(entry.getName())) {
                    final String className = entry.getName().replace(ZIP_SLASH, ".").replace(CLASS_EXTENSION, BLACK);
                    selectClass(className, packageName, classes, scanFilter);
                }
            }
        } catch (Throwable ex) {
        }
    }


    private static void selectClass(String className, String packageName, List<Class> classes, ScanFilter scanFilter) {
        if (checkClassName(className, packageName)) {
            try {
                final Class clazz = Class.forName(className, Boolean.FALSE, PackageUtils.class.getClassLoader());
                if (checkScanFilter(scanFilter, clazz)) {
                    classes.add(clazz);
                }
            } catch (Throwable ex) {
            }
        }
    }

    private static String[] getClassPathArray() {
        return System.getProperty("java.class.path").split(System.getProperty("path.separator"));
    }

    private static boolean checkClassName(String className, String packageName) {
        return className.indexOf(packageName) == 0;
    }

    private static boolean checkScanFilter(ScanFilter scanFilter, Class clazz) {
        return scanFilter == NULL_CLASS_FILTER || scanFilter.accept(clazz);
    }

    private static boolean isClass(String fileName) {
        return fileName.endsWith(CLASS_EXTENSION);
    }

    private static boolean isDirectory(File file) {
        return file.isDirectory();
    }

    private static boolean isJarFile(String fileName) {
        return fileName.endsWith(JAR_FILE_EXTENSION);
    }
}