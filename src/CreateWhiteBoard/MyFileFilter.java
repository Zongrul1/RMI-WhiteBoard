package CreateWhiteBoard;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class MyFileFilter extends FileFilter{
	  String ends; 
	  String description; 

	  public MyFileFilter(String ends, String description) { 
	    this.ends = ends; 
	    this.description = description; 
	  }

	  @Override
	  public boolean accept(File file) {
	    if (file.isDirectory()) return true;
	    String fileName = file.getName();
	    if (fileName.toUpperCase().endsWith(this.ends.toUpperCase())) return true;
	    return false;
	  }

	  @Override
	  public String getDescription() {
	    return this.description;
	  }
	  
	  public String getEnds() {
	    return this.ends;
	  }
}
