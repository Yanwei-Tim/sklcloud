package com.skl.cloud.foundation.batchjob;

import org.apache.log4j.Logger;
import org.springframework.core.NestedRuntimeException;

/**
 * Exception thrown by pipeline components if they fail to process a message.
 * Four levels of severity, it is always up to upstream components how they want to
 * deal with exceptions thrown by downstream components.
 * <P>
 * Currently there are four levels of severity <UL>
 *  <LI> FATAL    ( == 0)
 *  <LI> RETRY    ( == 1)
 *  <LI> HOSPITAL ( == 2)
 *  <LI> IGNORE   ( == 3)
 * </UL>
 * <p>
 * <p>Creation Date and by: 2016/03/12 Author: liyangbin
 * 
 * @author $Author: liyangbin $
 * @version $Revision: 7143 $ $Date: 2016-03-16 17:01:30 +0800 (Wed, 16 Mar 2016) $
 */

public class JobException extends NestedRuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	static Logger logger = Logger.getLogger(JobException.class);

	// Fatal exception - usually irrecoverable by individual components.
	public final static int _FATAL = 0;

	// Transient exception - usually dealt with by the Source resubmitting the failed message
	public final static int _RETRY = 1;

	// Exception indicating that, while the component is OK, the message which it received cannot be successfully processed.
	public final static int _HOSPITAL = 2;

	// Exception indicating that nothing is wrong but that this component has IGNORED the message.
	public final static int _IGNORE = 3;

	// String representations of the severity levels.
	public final static String[] SEVERITY_STRINGS = { "FATAL", "RETRY", "HOSPITAL", "IGNORE" };

	int severity;

	/**
	 * Constructs an exception with the given text message and severity.
	 *
	 * The severity will default to FATAL if the supplied severity is
	 * less than 0 (FATAL) or greater than 3 (IGNORE)
	 *
	 * @param cause this exception
	 * @param severity the severity of this exception
	 */
	public JobException(Throwable cause, int severity) {
		super("",cause);
		if (severity < _FATAL || severity > _IGNORE) {
			logger.warn("JobException constructed with invalid severity " + severity + " - defaulting to _FATAL");
			setSeverity(_FATAL);
		} else {
			setSeverity(severity);
		}
	}

	/**
	 * Constructs an exception with the given text message and severity
	 * 
	 * @param msg
	 * @param severity
	 * 
	 */
	public JobException(String msg, int severity) {
		super(msg);
		setSeverity(severity);
	}
	
	/**
	 * Get the level of severity.
	 * @return value of severity.
	 */
	public int getSeverity() {
		return severity;
	}

	/**
	 * Set the level of severity.
	 * @param v  Value to assign to severity.
	 */
	public void setSeverity(int v) {
		this.severity = v;
	}

	/**
	 * Returns a String containing the text of the message suffixed with the string corresponding to the
	 * severity of the exception (eg FATAL, RETRY etc)
	 */
	public String toString() {
		return super.toString() + " Severity: " + SEVERITY_STRINGS[getSeverity()];
	}
}// JobException