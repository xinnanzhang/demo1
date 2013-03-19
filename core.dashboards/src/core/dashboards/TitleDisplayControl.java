package core.dashboards;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.stream.ImageOutputStream;
import javax.swing.text.StyleConstants.FontConstants;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;

import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.jfree.chart.encoders.EncoderUtil;
import org.jfree.chart.encoders.ImageFormat;

import siteview.windows.forms.ConvertUtil;
import siteview.windows.forms.ImageHelper;
import system.Drawing.Drawing2D.LinearGradientMode;

public class TitleDisplayControl extends Composite{
	private String title = "";
	private Font titleFont = new Font(null,"simsun",SWT.NORMAL,10);
	private boolean bVertical = false;
	private boolean bIsSolid = false;
	private Color txtForeColor;
	private Color startColor;
	private Color endColor;
	
	private Button m_DrillBackButton;
	private Button m_RefreshButton;
	private Button m_PopupShowButton;
	
	private Label lblTitle;
	
	public TitleDisplayControl(Composite parent, int style) {
		super(parent, style);
		
		txtForeColor = Color.BLACK;
		startColor = Color.lightGray;
		endColor = Color.WHITE;
		this.setLayout(new FormLayout());
		this.setBackgroundMode(SWT.INHERIT_FORCE);
		
		lblTitle = new Label(this, SWT.NONE);
		lblTitle.setText(title);
		lblTitle.pack();

		
		FormData fd_lbl = new FormData();
		fd_lbl.left = new FormAttachment(0,5);
		fd_lbl.top = new FormAttachment(0,2);
		lblTitle.setLayoutData(fd_lbl);
		
		m_PopupShowButton = new Button(this,SWT.FLAT);
		//m_RefreshButton.setText("refresh");
		m_PopupShowButton.setImage(ImageHelper.LoadImage(Activator.PLUGIN_ID, "img/PopupShow.jpg"));
		m_PopupShowButton.setToolTipText("µ¯³ö");
		m_PopupShowButton.pack();
		FormData fd_popup = new FormData();
		fd_popup.right = new FormAttachment(100,-5);
		fd_popup.top = new FormAttachment(0,2);
		m_PopupShowButton.setLayoutData(fd_popup);
		
		m_DrillBackButton = new Button(this,SWT.FLAT);
		//m_DrillBackButton.setText("drillback");
		m_DrillBackButton.setImage(ImageHelper.LoadImage(Activator.PLUGIN_ID, "img/DrillBack.png"));
		m_DrillBackButton.setToolTipText("·µ»Ø");
		m_DrillBackButton.pack();
		
		FormData fd_drill = new FormData();
		fd_drill.right = new FormAttachment(m_PopupShowButton,-6);
		fd_drill.top = new FormAttachment(0,2);
		m_DrillBackButton.setLayoutData(fd_drill);

		
		m_RefreshButton = new Button(this,SWT.FLAT);
		//m_RefreshButton.setText("refresh");
		m_RefreshButton.setImage(ImageHelper.LoadImage(Activator.PLUGIN_ID, "img/Refresh.png"));
		m_RefreshButton.setToolTipText("Ë¢ÐÂ");
		m_RefreshButton.pack();
		FormData fd_refresh = new FormData();
		fd_refresh.right = new FormAttachment(m_DrillBackButton,-6);
		fd_refresh.top = new FormAttachment(0,2);
		m_RefreshButton.setLayoutData(fd_refresh);
		
		
	      this.addControlListener(new ControlListener(){

			@Override
			public void controlMoved(ControlEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void controlResized(ControlEvent e) {
				repaint();
				
			}});
		
	}
	
	private void repaint(){
		Image image = null;
		int width = this.getBounds().width;
		int height = this.getBounds().height;
		if (width >0 && height>0){
			BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
	
			Graphics2D ig2 = bi.createGraphics();
			if (bVertical)
				ig2.setPaint(new GradientPaint(0, 0, startColor, 0, height, endColor));
			else
				ig2.setPaint(new GradientPaint(0, 0, startColor, width, 0, endColor));
			ig2.fillRect(0, 0, width, height);
			
			//ig2.setPaint(txtForeColor);
			//ig2.drawString(title, 2, 15);
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ByteArrayInputStream bis = null;
			
			try {
				EncoderUtil.writeBufferedImage(bi, ImageFormat.PNG, bos);
				image = new Image(Display.getCurrent(),
						bis = new ByteArrayInputStream(bos.toByteArray()));
				this.setBackgroundImage(image);
			} catch (IOException e) {
				e.printStackTrace();
			}finally {
				try {
					bos.close();
				} catch (IOException e) {
				}

				if (bis != null) {
					try {
						bis.close();
					} catch (IOException e) {
					}
				}
			}
		}
	}

	public void set_Title(String strVal) {
		title = strVal;
		lblTitle.setText(title);
		lblTitle.pack();
	}

	public void set_TitleAlign(int get_TextAlign) {

		
	}

	public void set_ShowLinkIcon(boolean b) {
		
	}

	public void set_Image(Image image) {
		this.setBackgroundImage(image);
		
	}

	public void set_ImageAlign(int get_ImageAlign) {
		
	}

	public void set_TitleFont(Font font) {
		titleFont = font;
		
	}

	public void set_TitleForeColor(Color color) {
		txtForeColor = color;
		lblTitle.setForeground(ConvertUtil.toSwtColor(Display.getDefault(), txtForeColor));
		lblTitle.redraw();
	}

	public void set_StartColor(Color systemColor) {
		startColor = systemColor;
		repaint();
	}

	public void set_EndColor(Color systemColor) {
		endColor = systemColor;
		repaint();
	}

	public void set_IsSolidColor(Boolean isSolidColor) {
		bIsSolid = isSolidColor;
		
	}

	public boolean get_IsSolidColor() {
		return bIsSolid;
	}

	public void set_LinearGradientMode(int linearGradientMode) {
		if (linearGradientMode == LinearGradientMode.Vertical)
			this.bVertical = true;
		else
			this.bVertical = false;
		
	}
	
	public Button get_DrillBackButton(){
		return m_DrillBackButton;
	}
	
	public Button get_RefreshButton(){
		return m_RefreshButton;
	}
	
	public Button get_PopupButton(){
		return m_PopupShowButton;
	}
	
}
