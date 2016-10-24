package type;

import java.util.HashMap;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A wrapper around a map that uses methods that are more Ognl fiendly
 *
 */
public class OgnlMap {
	private static Log log = LogFactory.getLog(OgnlMap.class);
	
	private HashMap<Long, String> map = new HashMap<Long, String>();
	
	public String getValue(Long key){
		if( log.isDebugEnabled())
			log.debug("Getting " + key +":"+ map.get(key));
		return map.get(key);
	}
	
	public void setValue(Long key, String value){
		if( log.isDebugEnabled())
			log.debug("Setting " + key + ":" + value);
		map.put(key, value);
	}
	
	public Set<Long> keySet(){
		return map.keySet();
	}
	
	public String toString(){
		StringBuilder builder = new StringBuilder();
		builder.append("QgnlMap: ");
		for(Long i: map.keySet()){
			builder.append("'" + i + "':'" + map.get(i)+ "',");
		}
		return builder.toString();
	}
}
