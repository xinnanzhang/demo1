package core.dashboards;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

import siteview.windows.forms.ConvertUtil;


public class GradientPictureBox extends Composite{
	
	private PaintListener pl = null;
	
	private int width = 0;
	private int height = 0;
	private int linearGradientMode = 0;
	
	private Color startColor;
	private Color endColor;

	public GradientPictureBox(Composite parent, int style) {
		super(parent, style);
//		this.addPaintListener(new PaintListener(){
//
//			@Override
//			public void paintControl(PaintEvent e) {
//				
//				
//			}});
		//this.setBackground(new org.eclipse.swt.graphics.Color(Display.getCurrent(),255,0,0));
		//this.setBackgroundMode(SWT.INHERIT_FORCE);
		super.addControlListener(new ControlListener(){

			@Override
			public void controlMoved(ControlEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void controlResized(ControlEvent e) {
				width = ((Control)e.getSource()).getSize().x;
				height = ((Control)e.getSource()).getSize().y;
			}});
	}
	
	private void checkPL(){
		if (pl == null){
			pl = new PaintListener(){

				@Override
				public void paintControl(PaintEvent e) {
					if (startColor!=null)
						e.gc.setBackground(startColor);
					if (endColor!= null)
						e.gc.setForeground(endColor);
					if (startColor!=null && endColor!=null)
						e.gc.fillGradientRectangle(0, 0, width, height, false);
				}
				
			};
			
			//super.addPaintListener(pl);
		}
	}

	public void set_IsSolidColor(Boolean bIsSolidColor) {
		// TODO Auto-generated method stub
		
	}

	public void set_StartColor(system.Drawing.Color startColor) {
		// TODO Auto-generated method stub
		//this.checkPL();
		this.startColor = ConvertUtil.toSwtColor(Display.getDefault(), startColor);
		//this.setBackground(this.startColor);
	}

	public void set_EndColor(system.Drawing.Color endColor) {
		//this.checkPL();
		this.endColor = ConvertUtil.toSwtColor(Display.getDefault(), endColor);
	}

	public void set_LinearGradientMode(int horizontal) {
		//this.checkPL();
		
	}

}
