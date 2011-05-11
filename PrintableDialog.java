import java.awt.*;
import java.beans.*;
import java.awt.print.*;
import javax.swing.*;
import java.io.*;
/**
* Allows printing of documents displayed in JDialogs
*/
class PrintableDialog extends JDialog implements Printable,Serializable {
	 /**
	  * The method @print@ must be implemented for @Printable@ interface.
	  * Parameters are supplied by system.
	  */
	public int print(Graphics g, PageFormat pf, int pageIndex) throws PrinterException {
    	Graphics2D g2 = (Graphics2D)g;
    	g2.setColor(Color.black);    //set default foreground color to black
        //for faster printing, turn off double buffering
        //RepaintManager.currentManager(this).setDoubleBufferingEnabled(false);
        Dimension d = this.getSize();    //get size of document
        double panelWidth  = d.width;    //width in pixels
        double panelHeight = d.height;   //height in pixels
        double pageHeight = pf.getImageableHeight();   //height of printer page
        double pageWidth  = pf.getImageableWidth();    //width of printer page
        double scale = pageWidth/panelWidth;
        int totalNumPages = (int)Math.ceil(scale * panelHeight / pageHeight);
            //make sure not print empty pages
        if(pageIndex >= totalNumPages) {
             return Printable.NO_SUCH_PAGE;
        }
       //shift Graphic to line up with beginning of print-imageable region
        g2.translate(pf.getImageableX(), pf.getImageableY());
       //shift Graphic to line up with beginning of next page to print
        g2.translate(0f, -pageIndex*pageHeight);
       //scale the page so the width fits...
        g2.scale(scale, scale);
        this.paint(g2);   //repaint the page for printing
        return Printable.PAGE_EXISTS;
   }
}