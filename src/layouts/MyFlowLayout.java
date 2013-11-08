package layouts;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.LayoutManager;

@SuppressWarnings("serial")
public class MyFlowLayout extends FlowLayout implements LayoutManager{
    
	
	public MyFlowLayout(int align){
		super(align);
	}
	 
	public Dimension preferredLayoutSize(Container target) {
	       synchronized (target.getTreeLock()) {
	     Dimension dim = new Dimension(0, 0);
	     int nmembers = target.getComponentCount();
	     boolean firstVisibleComponent = true;
        
         Insets insets = target.getInsets();
         int lines = 1;
         int testWidth = 0;
         int l=nmembers;

         Component s = target.getComponent(1);
       testWidth= (s.getWidth()*nmembers)+(nmembers)*getVgap()-1;
	     for (int i = 0 ; i < nmembers ; i++) {
	         Component m = target.getComponent(i);
	         if (m.isVisible()) {
	         Dimension d = m.getPreferredSize();
	         dim.height = Math.max(dim.height, d.height);
	         if (firstVisibleComponent) {
                 firstVisibleComponent = false;
             } else {
                 dim.width += getHgap();
             }
	                 dim.width += d.width;
	                  if(testWidth >= target.getWidth()) {
	                		  lines++;
	                		if(nmembers%lines!=0){
	                			l=nmembers/lines; l+=1;
	                		}
	                		else { l=nmembers/lines;}
	                		testWidth= (s.getWidth()*l)+(l)*getVgap()-1;	
	                  }
	              }
	          }

	          dim.width += insets.left + insets.right + getHgap()*2;
	          dim.height += insets.top + insets.bottom + getVgap()*2;
	       
	          dim.height *= lines;
	          return dim;
	       }
	    }
}
