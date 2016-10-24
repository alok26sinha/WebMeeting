package type;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;


public class BeanUtils {

	public static void copyProperties(Object source, Object target, String[] ignoreFields){
		String[] fullIgnoreFields = new String[ignoreFields.length + 2];
		for( int i =0; i < ignoreFields.length; i++){
			fullIgnoreFields[i] = ignoreFields[i];
		}
		fullIgnoreFields[ignoreFields.length + 0] = "id";
		fullIgnoreFields[ignoreFields.length + 1] = "company";
		org.springframework.beans.BeanUtils.copyProperties(source, target, fullIgnoreFields);
	}
	
	public static boolean hasGetProperty(Class clazz, String methodName){
		Method method =  org.springframework.beans.BeanUtils.findMethod(clazz, methodName, (Class<?>[])null);
		if( method != null){
			return true;
		}
		else{
			return false;
		}
	}
	
	public static boolean hasField(Class clazz, String name){
		Field[] fields =  clazz.getFields();
		
		for(Field field: fields){
			if(field.getName().equals(name))
				return true;
		}
		
		return false;
		
	}
}
