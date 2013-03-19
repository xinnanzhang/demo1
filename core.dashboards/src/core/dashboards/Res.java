package core.dashboards;
import java.io.IOException;
import java.util.Locale;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import Siteview.ResxReader;

import system.Reflection.Assembly;
import system.Resources.ResourceManager;

public class Res
{
    //private static ResourceManager m_defaultResources;

	private static ResxReader m_defaultResources;
	
    private Res (){
    }

//    public static ResourceManager get_Default(){
//    	
//        if(m_defaultResources == null){
//            m_defaultResources = ResourceUtils.GetNamedResourceManager("Default", Assembly.GetCallingAssembly());
//        }
//        return m_defaultResources;
//    }
    
    public static ResxReader get_Default(){
    	try {
    		if(m_defaultResources == null)
    			m_defaultResources = new ResxReader(core.dashboards.Res.class,"core/dashboards/Default",Locale.getDefault());
    		return m_defaultResources;
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			
		}
		return null;
    }

}