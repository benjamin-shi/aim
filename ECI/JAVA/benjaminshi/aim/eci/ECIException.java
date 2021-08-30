package benjaminshi.aim.eci;

/** The exception for whole ECI protocol
 * @author Benjamin Shi (SHI YU, shiyubnu@icloud.com)
 * 
 */
public class ECIException extends Exception {

	public ECIException() {
		super();
	}

	public ECIException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public ECIException(String message, Throwable cause) {
		super(message, cause);
	}

	public ECIException(String message) {
		super(message);
	}

	public ECIException(Throwable cause) {
		super(cause);
	}
}
