package core.dashboards;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

import siteview.windows.forms.CustomDashboardPartBase;

import Core.Dashboards.CustomPartDef;
import Core.Dashboards.DashboardPartDef;
import Core.Dashboards.PartRefDef;
import Siteview.Api.ISiteviewApi;

public class CustomPartControl extends DashboardPartControl {
	
	private static final long serialVersionUID = -2474973647610675094L;
	private CustomPartDef partDef;

	public CustomPartControl(DashboardControl parent, int style) {
		super(parent, style);
	}

	public CustomPartControl(ISiteviewApi iSiteviewApi, DashboardControl parent) {
		super(iSiteviewApi,parent,SWT.NONE);
		
		this.get_MainArea().setLayout(new FillLayout());
	}

	
	@Override
	protected void DefineFromDef(DashboardPartDef def, PartRefDef partRefDef) {
        super.DefineFromDef(def, partRefDef);
        
        partDef = (CustomPartDef) def;
        if (partDef != null) {
        	String strName = partDef.getExtensionName();
        	String className = "";
        	String bundleId = "";
        	
        	IExtensionPoint extPoint = Platform.getExtensionRegistry()
					.getExtensionPoint("Siteview.Forms.Common.DashboardPartExtension");
			
			for (IExtension ext : extPoint.getExtensions()) {
				for (IConfigurationElement c : ext.getConfigurationElements()) {
					if (c.getName().equals("CustomPart")) {
						String name =  c.getAttribute("name");
						if (name !=null && name.equals(strName)){
							bundleId = c.getContributor().getName();
							className = c.getAttribute("Class");
							break;
						}
					}
				}
			}
			
			if (!className.isEmpty()){
				try {
					Map<String, String> params = new HashMap<String,String>();
					for(int i = 0; i < partDef.get_ParamTypes().get_Count(); i++){
						params.put(partDef.get_ParamTypes().get_Item(i).toString(), partDef.get_ParamVals().get_Item(i).toString());
					}
					
					Class<?> cls = Platform.getBundle(bundleId).loadClass(className);
					Constructor<?> ctor = cls.getDeclaredConstructor(Composite.class,ISiteviewApi.class, Map.class);
					CustomDashboardPartBase cPart = (CustomDashboardPartBase) ctor.newInstance(this.get_MainArea(),super.getApi(),params);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (SecurityException e) {
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}
        }   
    }
	
}