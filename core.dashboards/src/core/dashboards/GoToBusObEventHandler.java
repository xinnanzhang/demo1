package core.dashboards;

import Siteview.IVirtualKeyList;
import Siteview.Xml.GridAction;

public interface GoToBusObEventHandler {

	void handler(IVirtualKeyList iVirtualKeyList, String strBusObName,
			String strRecId, GridAction gridAction, String strGridActionDetail);

	
}
