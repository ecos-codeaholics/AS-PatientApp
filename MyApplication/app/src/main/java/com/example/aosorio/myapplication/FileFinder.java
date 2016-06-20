/** Copyright or License
 *
 */

package com.example.aosorio.myapplication;

import java.io.File;
import java.util.ArrayList;

/**
 * Package: uniandes.ecos.psp
 *
 * Class: FileFinder FileFinder.java
 * 
 * Original Author: @author AOSORIO
 * 
 * Description: Given a directory PATH, return all files that are in this
 * directory according to some criteria
 * 
 * Implementation: Returns an ArrayList of string, containing file paths
 * 
 * Created: Feb 15, 2016 3:47:08 PM
 * 
 */
public class FileFinder {

	private String root;
	private ArrayList<String> allFiles;
	File[] files;
	
	public FileFinder(String given_root) {
		super();
		this.root = given_root;
		allFiles = new ArrayList<String>();
	}

	public String getRoot() {
		return root;
	}

	public ArrayList<String> getAllFiles() {
		return allFiles;
	}

	public void processRoot() {

		//Add here an exeption - 
		files = new File(root).listFiles();
		getSourceFiles(files, allFiles, ".json");
		
		for(int i=0; i<allFiles.size();++i){
			System.out.println(allFiles.get(i));
		}

	}

	public void getSourceFiles(File[] current_files, ArrayList<String> source, String fileExt) {
				
		for (File file : current_files) {
			if (file.isDirectory()) {
				//System.out.println("Directory: " + file.getName());
				getSourceFiles(file.listFiles(), source, fileExt); // Calls same
																	// method
																	// again.
			} else {
				if (file.getName().endsWith(fileExt)) {
					//System.out.println("File: " + file.getName());
					source.add(file.getAbsolutePath());
				}
			}
		}
	}
}
