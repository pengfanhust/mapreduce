package util;

public class StringArrayConvertor {

	public static String intArrToString(int[] arr) {
		StringBuilder sb = new StringBuilder();
		for (int i : arr) {

			sb.append(i).append(MpRdcConstants.ATTR_SEPRATOR);
		}
		if (sb.length() > 1) {
			sb.deleteCharAt(sb.length() - 1);
		}

		return sb.toString();
	}
	

	public static  int[] stringToIntArr(String s) {
		String[] sarr = s.split(MpRdcConstants.ATTR_SEPRATOR);
		int[] arr = new int[sarr.length];
		for (int i = 0; i < arr.length; i++) {
			arr[i] = Integer.parseInt(sarr[i]);
		}
		return arr;
	}
}
