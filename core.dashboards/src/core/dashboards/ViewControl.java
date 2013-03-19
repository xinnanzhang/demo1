package core.dashboards;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.GridLayout;

public class ViewControl extends Composite{

	private Label m_label;
	private Combo m_combo;
	
	public ViewControl(Composite parent, int style) {
		super(parent, style);
		setLayout(new FillLayout(SWT.HORIZONTAL));
		Composite composite = new Composite(this, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		m_label = new Label(composite,SWT.NONE);
		m_label.setAlignment(SWT.CENTER);
		m_label.setText("ÈÕÆÚ·¶Î§");
		m_label.pack();
		m_combo = new Combo(composite, SWT.READ_ONLY);
	}
	
	public Label get_Label(){
		return m_label;
	}
	
	public Combo get_Combo(){
		return m_combo;
	}

	public int get_Right() {
		return this.getBounds().x + this.getBounds().width;
	}

	public int get_Width() {
		return this.getBounds().width;
	}

	public int get_Left() {
		return this.getBounds().x;
	}

	public int get_Top() {
		return this.getBounds().y;
	}

}
