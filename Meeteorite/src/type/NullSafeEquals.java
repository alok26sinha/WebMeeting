package type;

public class NullSafeEquals {

	public static boolean equals( Object a, Object b ){
		if( a == null){
			if( b == null)
				return true;
			else 
				return false;
		}
		else
			return a.equals(b);
	}
	
	
}
