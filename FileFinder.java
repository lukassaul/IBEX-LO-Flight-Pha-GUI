
import java.io.*;
import java.util.*;
import java.util.jar.*;
import java.util.zip.*;
import java.net.URL;
import javax.swing.ImageIcon;
import java.awt.*;

/*
* I thought theMain was getting to long, and shouldn't have this stuff in it
*
* This stuff is all static (do not instantiate!)
*
*  Designed to ease issues of multiple folders, jars, etc.
*
*  This doesn't look from the root, just from the user_dir
*  to look from root, use File.getRoot and recursing findInDirectory
*
*  9Elder Inc.
*  Sept. 2001
*/
public class FileFinder {
	public static final String FILE_SEP = System.getProperty("file.separator");
	public static final String USER_DIR = System.getProperty("user.dir");
	public static final String LINE_SEP = System.getProperty("line.separator");

	/**
	* Simplifying our procedure for dragging up images, html files, etc.
	* We want to get this shit from jars or recurse down directories
	*
	*  Don't get this stuff repeatedly!  No need to redrag shit from files.
	*
	*  Class context is passed in to enable pulling files from the currently
	*   loaded jar.   (this avoids security problems in applets, and lets us pack
	*    our images and stuff in the release jar)
	*/
	public static InputStream getInputStream(String fileName, Class context) {
		InputStream imgStream = null;

		imgStream = context.getResourceAsStream(fileName);
		try {
			if (imgStream.available() > 5) return imgStream;
		}
		catch (Exception e) {}

		// now try in a folder in the current class jar based on some extensions
		if ((fileName.indexOf(".gif")!=-1) ||
			(fileName.indexOf(".jpg")!=-1) ||
			(fileName.indexOf(".jpeg")!=-1) ||
			(fileName.indexOf(".png")!=-1) ||
			(fileName.indexOf(".tiff")!=-1) ||
			(fileName.indexOf(".img")!=-1) ) {

			try {
				imgStream = context.getResourceAsStream("/Images/"+fileName);
				if (imgStream.available() > 5) return imgStream;
			}
			catch (Exception e) {}
		}

		if ((fileName.indexOf(".txt")!=-1) ||
			(fileName.indexOf(".html")!=-1) ||
			(fileName.indexOf(".htm")!=-1) ||
			(fileName.indexOf(".doc")!=-1) ) {
			try {
				imgStream = context.getResourceAsStream("/Documents/"+fileName);
				if (imgStream.available() > 5) return imgStream;
			}
			catch (Exception e) {}
		}

		if ((fileName.indexOf(".wav")!=-1) ||
			(fileName.indexOf(".au")!=-1) ||
			(fileName.indexOf(".mp3")!=-1) ||
			(fileName.indexOf(".midi")!=-1) ) {
			try {
				imgStream = context.getResourceAsStream("/Sounds/"+fileName);
				if (imgStream.available() > 5) return imgStream;
			}
			catch (Exception e) {}
		}

		// OK, we are getting desperate looking for this freaking stream
		// try any dirNames we can think of that might be in the jar
		// (kind of sucks, but there is NO WAY to get a directory of this guy)
		//
		// in other words we have to guess now, doubles could screw us
		try {
			imgStream = context.getResourceAsStream("/Documents/"+fileName);
			if (imgStream.available() > 5) return imgStream;
		}
		catch (Exception e) {}

		try {
			imgStream = context.getResourceAsStream("/Images/"+fileName);
			if (imgStream.available() > 5) return imgStream;
		}
		catch (Exception e) {}

		try {
			imgStream = context.getResourceAsStream("/Documents/Images/"+fileName);
			if (imgStream.available() > 5) return imgStream;
		}
		catch (Exception e) {}
		try {
			imgStream = context.getResourceAsStream("/Skins/"+fileName);
			if (imgStream.available() > 5) return imgStream;
		}
		catch (Exception e) {}
		try {
			imgStream = context.getResourceAsStream("/Sounds/"+fileName);
			if (imgStream.available() > 5) return imgStream;
		}
		catch (Exception e) {}
		try {
			imgStream = context.getResourceAsStream("/Documents/"+fileName);
			if (imgStream.available() > 5) return imgStream;
		}
		catch (Exception e) {}


		// ok, we are giving up on the resourceAsStream
		// let's just look for a local file
		File f = getLocalFile(fileName);
		if (f!=null && f.exists()) {
			try {
		 		FileInputStream fis = new FileInputStream(f);
				return fis;
			}
			catch (Exception e) {}
		}


		// looks like we couldn't find it...
		return imgStream;
	}




	/**
	* Here we allow for EXTERNAL jar file specification (skins? filters? nugjars?)
	*
	*  (to speed up the search)
	*  (if we have external jar specified, our job is near over)
	*/
	public static InputStream getInputStream(String fileName, String jarName, Class context) {
		InputStream imgStream = null;
		// let's start by getting the jarFile, eh?  Maybe it's hidden...

		System.out.println("Checking for Input Stream!!!");
		
		File jarFile = getLocalFile(jarName);

		if (jarFile!=null && jarFile.exists()) {
			// at least we have a jarfile to look through!
			try {
				JarFile jf = new JarFile(jarFile);
				JarEntry je = jf.getJarEntry(fileName);
				if (je != null)
					return jf.getInputStream(je);

				System.out.println("\nDigging deaper!!");


				// ok, it wasn't there.  Let's dig deeper...
				Enumeration e = jf.entries();
				while (e.hasMoreElements()) {
					je = (JarEntry)e.nextElement();

					//System.out.println("Jar Entry: " + je.getName());

					if (je.isDirectory())  {
						JarEntry je2 = jf.getJarEntry(je.getName()+fileName);
						if (je2 != null) return jf.getInputStream(je2);
					}
				}
			}
			catch (Exception e) {
				//System.out.println("couldn't load from jar as asked: " + fileName + " " + jarName);
			}

		}
		// it wasn't in the jar, let's put it through the usual gamut:
		return getInputStream(fileName, context);

	}

	/**
	* find a local file, starting with the root
	*
	* this will not find directories
	*
	*/
	public static File getLocalFile(String fileName) {
		File f = new File(fileName);
		if (f!=null & f.exists() & !f.isDirectory()) return f;

		// let's check simple extensions first:
		if ((fileName.indexOf(".gif")!=-1) ||
			(fileName.indexOf(".jpg")!=-1) ||
			(fileName.indexOf(".jpeg")!=-1) ||
			(fileName.indexOf(".png")!=-1) ||
			(fileName.indexOf(".tiff")!=-1) ||
			(fileName.indexOf(".img")!=-1) ) {

			try {
				f = new File(USER_DIR + FILE_SEP + "Images" + FILE_SEP + fileName);
				if (f!=null & f.exists()) return f;
			}
			catch (Exception e) {}
		}

		if ((fileName.indexOf(".txt")!=-1) ||
			(fileName.indexOf(".html")!=-1) ||
			(fileName.indexOf(".htm")!=-1) ||
			(fileName.indexOf(".doc")!=-1) ) {
			try {
				f = new File(USER_DIR + FILE_SEP + "Documents" + FILE_SEP + fileName);
				if (f!=null & f.exists()) return f;
			}
			catch (Exception e) {}
		}

		if ((fileName.indexOf(".wav")!=-1) ||
			(fileName.indexOf(".au")!=-1) ||
			(fileName.indexOf(".mp3")!=-1) ||
			(fileName.indexOf(".midi")!=-1) ) {
			try {
				f = new File(USER_DIR + FILE_SEP + "Sounds" + FILE_SEP + fileName);
				if (f!=null & f.exists()) return f;
			}
			catch (Exception e) {}
		}

		// ok, now recurse through real subdirs
		File root = new File(USER_DIR);
		File tbr = findInDirectory(fileName,root);
		if (tbr!=null) return tbr;
		//File[] roots = File.listRoots();
		//File tbr = null;
		//for (int i=0; i<roots.length; i++) {
		//	if (findInDirectory(fileName,oots[i],tbr)) return tbr;
		//}

		// if we haven't found it yet give up
		return null;
	}

	/**
	* Here's the recursive subroutine for searching local files.
	*
	*  returns true if it has changed tbr to the right file
	*/
	public static File findInDirectory(String fileName, File dir) {
		//System.out.println("Recursing: " + dir.getPath());
		if (dir!= null && dir.isDirectory()) { // just in case...
			File[] subFiles = dir.listFiles();
			for (int i=0; i<subFiles.length; i++) {
				if (subFiles[i].getName().equals(fileName)) {
					return subFiles[i];
				}
				if (subFiles[i].isDirectory()) {
					File f8 = findInDirectory(fileName, subFiles[i]);
					if (f8!=null) return f8;
				}
			}
		}
		return null;
	}


	/**
	* Get a javax.swing.ImageIcon using the new utils above
	*/
	public static ImageIcon getImageIcon(String fileName, String jarName, Class context) {
		/*
		InputStream imgStream = null;
		ImageIcon img = null;
		if (jarName.length()>2) imgStream = getInputStream(fileName,jarName,context);
		else imgStream = getInputStream(fileName,context);
		try {
			byte imageBytes[]=new byte[imgStream.available()];
			imgStream.read(imageBytes);
			img = new ImageIcon(imageBytes);
			return img;
		}
		catch (Exception d) {}
		return null;
		*/

		Image MyImage = Toolkit.getDefaultToolkit().getImage(ClassLoader.getSystemResource(fileName));
		ImageIcon icon = new ImageIcon(MyImage);
		return icon;
	}

	public static ImageIcon getImageIcon(String fileName, Class context) {
		return getImageIcon(fileName, "", context);
	}


	/**
	* This one gets the text as a String object
	*
	*  we can use the inputStream methods from above for this one
	*/
	public static String getTextFromResourceFile(String fileName, String jarName, Class context) {
		InputStream imgStream = null;
		String tbr = "";
		if (jarName.length()>2)
			imgStream = getInputStream(fileName,jarName,context);
		else
			imgStream = getInputStream(fileName,context);

		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(imgStream));
			String line = "";
			StringBuffer buf = new StringBuffer();
			while ((line = br.readLine()) != null) {
				//tbr += line + LINE_SEP; // muy mal
				buf.append(line + LINE_SEP);
				//System.out.println("Line read in: " + line);
			}
			tbr = buf.toString();
		}
		catch (Exception d) {
			//System.out.println("Couldn't read input stream for text resource...");
		}
		return tbr;
	}
	public static String getTextFromResourceFile(String fileName, Class context) {
		return getTextFromResourceFile(fileName,"",context);
	}

	/**
	* now we're getting a URL, to display a html file
	* with a JEditorPane, or a PrintableEditorPane,
	*  usually fixed with PatchedHTMLEditorKit
	*
	*  currently we cannot load this from an external jar!
	*
	*  Note that URL Class.getResource(String s) will only work with appropriate
	*    file extensions, under Netscape.
	*/
	public static URL getURL(String fileName, Class context) {
		URL tbr = null;

		// well then, more guessing here...
		// first try documents folder
		tbr = context.getResource("/Documents/" + fileName);
		if (tbr != null) return tbr;

		// then try root folder
		tbr = context.getResource(fileName);
		if (tbr != null) return tbr;

		// now some others
		tbr = context.getResource("/Images/" + fileName);
		if (tbr != null) return tbr;

		tbr = context.getResource("/Skins/" + fileName);
		if (tbr != null) return tbr;

		tbr = context.getResource("/Documents/Images/" + fileName);
		if (tbr != null) return tbr;

		tbr = context.getResource("/Sounds/" + fileName);
		if (tbr != null) return tbr;


		// if that didn't work, it wasn't in the jar file.  Lets try a real path
		File f = getLocalFile(fileName);
		if (f!=null & f.exists()) {
			try {
				tbr = new URL("file:"+f.getAbsolutePath());
				if (tbr!=null) return tbr;
			}
			catch (Exception e) {}
		}

		 // if that didn't work, maybe its in the root
		try {
			tbr = new URL("file:"+fileName);
		}
		catch (Exception e) {
			// no more choices for now
		}
		return tbr;  // If we still don't have it, give u
	}

	/*public static void testRecurse(String fileName) {
		String s = "fuck";
		System.out.println(getTextFromResourceFile(fileName, s.getClass()));
	}*/

	/**
	* Testing only!  Lets figure out how jar files work here...
	*/
	/*public static void testJarShit(String fileName) {
		try {
			JarFile jf = new JarFile(fileName);
			Enumeration e = jf.entries();
			while (e.hasMoreElements()) {
				JarEntry je = (JarEntry)e.nextElement();
				if (je.isDirectory())	System.out.println(je.getName());
			}
		}
		catch (Exception e) {
		}
	}*/


	/**
	* Let's see what's going on here with some tests
	*
	*/
	//public static final void main(String[] args) {
	//	//testJarShit("xbrowser.jar");
	//	testRecurse("DOM.java");
	//}
}