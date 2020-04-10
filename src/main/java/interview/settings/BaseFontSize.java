package interview.settings;

public class BaseFontSize {

	private static float baseFontSizeScale = 2.2f;
	
	public BaseFontSize() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Get base font size
	 * @return
	 */
	public static float getBaseFontSize(int size) {
		
		return size*baseFontSizeScale;
	}
}
