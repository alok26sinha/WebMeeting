package type;

public class NullSafeComparator{
	public static  <T extends Comparable<T>> int compare(T a, T b){
		if (a == null && b == null) {
			return 0;
		} else if (a == null && b != null) {
			return -1;
		} else if (a != null && b == null) {
			return 1;
		} else {
			return a.compareTo(b);
		}
	}
}
