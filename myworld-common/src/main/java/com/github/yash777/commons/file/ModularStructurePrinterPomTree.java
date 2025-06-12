package com.github.yash777.commons.file;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * ModularStructurePrinterPomTree is a utility program that prints a tree-structured view
 * of a multi-module Maven project directory, displaying only folders
 * that contain a {@code pom.xml} file or have subfolders that do.
 * 
 * <p>It skips Maven build output folders such as {@code target} and
 * compiled class folders like {@code bin}.
 *
 * <h2>Example Directory:</h2>
 * <pre>
 * D:\MySpringMultiModules\MyWorld
 * ├── myworld-api
 * │   └── pom.xml
 * ├── myworld-db
 * │   └── pom.xml
 * ├── myworld-common
 * │   └── pom.xml
 * ├── target  (skipped)
 * └── bin	 (skipped)
 * </pre>
 *
 * <h2>Sample Output:</h2>
 * <pre>
 * 📦 MyWorld
 * ├── 📦 myworld-api
 * ├── 📦 myworld-db
 * └── 📦 myworld-common
 * </pre>
 *
 * @author 🔐 Yash
 */
public class ModularStructurePrinterPomTree {

	/**
	 * Entry point of the application. This method determines the root path for the modular Maven project:
	 * <ul>
	 *   <li>If a command-line argument is provided, it uses that as the project root path.</li>
	 *   <li>If no argument is provided, it falls back to a hardcoded default path.</li>
	 * </ul>
	 * Then it recursively prints a tree structure of folders that contain {@code pom.xml} files,
	 * excluding Maven build folders like {@code target} and {@code bin}.
	 *
	 * @param args Optional. The first argument can be the root directory path for the multi-module project.
	 *             If omitted, the default path {@code D:\MySpringMultiModules\MyWorld} is used.
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		String rootPath;
		if (args.length == 0) {
			// Hardcoded path to the root directory of the multi-module project
			rootPath = PathUtility.getParentProjectPath().toString();
			System.out.println("Current Project - Parent Path: " + rootPath);
		} else {
			rootPath = args[0];
		}
		File rootDir = new File(rootPath);

		if (!rootDir.exists() || !rootDir.isDirectory()) {
			System.out.println("Invalid directory: " + rootPath);
			return;
		}

		System.out.println("Maven Modules Containing pom.xml: \n📦 " + rootDir.getName());
		printPomTree(rootDir, "", true);
	}

	/**
	 * Recursively prints a directory tree structure starting from the given folder.
	 * Only includes folders that contain a {@code pom.xml} or have descendants that do.
	 * Skips folders named {@code target} or {@code bin}.
	 *
	 * @param dir	The current directory to process
	 * @param prefix Prefix for formatting the output tree structure
	 * @param isLast True if this is the last item in the current directory
	 */
	private static void printPomTree(File dir, String prefix, boolean isLast) {
		File[] children = dir.listFiles(File::isDirectory);
		if (children == null || children.length == 0) return;

		List<File> pomFolders = new ArrayList<>();
		for (File child : children) {
			String folderName = child.getName();
			if (folderName.equalsIgnoreCase("target") || folderName.equalsIgnoreCase("bin")) {
				continue; // Skip build output folders
			}

			if (containsPom(child)) {
				pomFolders.add(child);
			} else if (hasPomDescendant(child)) {
				pomFolders.add(child);
			}
		}

		for (int i = 0; i < pomFolders.size(); i++) {
			File child = pomFolders.get(i);
			boolean last = (i == pomFolders.size() - 1);

			System.out.println(prefix + (last ? "└── " : "├── ") + "📦 " + child.getName());

			printPomTree(child, prefix + (last ? "	" : "│   "), last);
		}
	}

	/**
	 * Checks if the specified folder contains a {@code pom.xml} file.
	 *
	 * @param folder The directory to check
	 * @return true if the folder contains {@code pom.xml}, false otherwise
	 */
	private static boolean containsPom(File folder) {
		return new File(folder, "pom.xml").exists();
	}

	/**
	 * Recursively checks whether any child directory (excluding {@code target} and {@code bin})
	 * contains a {@code pom.xml}.
	 *
	 * @param folder The root folder to start checking from
	 * @return true if a descendant contains {@code pom.xml}, false otherwise
	 */
	private static boolean hasPomDescendant(File folder) {
		String folderName = folder.getName();
		if (folderName.equalsIgnoreCase("target") || folderName.equalsIgnoreCase("bin")) {
			return false;
		}

		File[] files = folder.listFiles();
		if (files == null) return false;

		for (File file : files) {
			if (file.isDirectory()) {
				if (containsPom(file) || hasPomDescendant(file)) {
					return true;
				}
			}
		}
		return false;
	}
}