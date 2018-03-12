package ca.uhn.fhir.util;

import com.google.common.annotations.VisibleForTesting;
import org.apache.commons.lang3.time.DateUtils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/*
 * #%L
 * HAPI FHIR - Core Library
 * %%
 * Copyright (C) 2014 - 2018 University Health Network
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

/**
 * @since HAPI FHIR 3.3.0
 */
public class StopWatch {

	private static final NumberFormat DAY_FORMAT = new DecimalFormat("0.0");
	private static final NumberFormat TEN_DAY_FORMAT = new DecimalFormat("0");
	private static Long ourNowForUnitTest;
	private long myStarted = now();

	/**
	 * Constructor
	 */
	public StopWatch() {
		super();
	}

	/**
	 * Constructor
	 *
	 * @param theStart The time to record as the start for this timer
	 */
	public StopWatch(Date theStart) {
		myStarted = theStart.getTime();
	}

	public String formatThroughput(int theNumOperations, TimeUnit theUnit) {
		double throughput = getThroughput(theNumOperations, theUnit);
		return new DecimalFormat("0.0").format(throughput);
	}

	/**
	 * Given an amount of something completed so far, and a total amount, calculates how long it will take for something to complete
	 *
	 * @param theCompleteToDate The amount so far
	 * @param theTotal          The total (must be higher than theCompleteToDate
	 * @return A formatted amount of time
	 */
	public String getEstimatedTimeRemaining(double theCompleteToDate, double theTotal) {
		double millis = getMillis();
		long millisRemaining = (long) (((theTotal / theCompleteToDate) * millis) - (millis));
		return formatMillis(millisRemaining);
	}

	public long getMillis(Date theNow) {
		return theNow.getTime() - myStarted;
	}

	public long getMillis() {
		long now = now();
		return now - myStarted;
	}

	public long getMillisAndRestart() {
		long now = now();
		long retVal = now - myStarted;
		myStarted = now;
		return retVal;
	}

	/**
	 * @param theNumOperations Ok for this to be 0, it will be treated as 1
	 */
	public int getMillisPerOperation(int theNumOperations) {
		return (int) (((double) getMillis()) / Math.max(1.0, theNumOperations));
	}

	public Date getStartedDate() {
		return new Date(myStarted);
	}

	public double getThroughput(int theNumOperations, TimeUnit theUnit) {
		if (theNumOperations <= 0) {
			return 0.0f;
		}

		long millisElapsed = Math.max(1, getMillis());
		long periodMillis = theUnit.toMillis(1);

		double numerator = theNumOperations;
		double denominator = ((double) millisElapsed) / ((double) periodMillis);

		return numerator / denominator;
	}

	public void restart() {
		myStarted = now();
	}

	/**
	 * Formats value in an appropriate format. See {@link #formatMillis(long)}}
	 * for a description of the format
	 *
	 * @see #formatMillis(long)
	 */
	@Override
	public String toString() {
		return formatMillis(getMillis());
	}

	/**
	 * Append a right-aligned and zero-padded numeric value to a `StringBuilder`.
	 */
	static private void append(StringBuilder tgt, String pfx, int dgt, long val) {
		tgt.append(pfx);
		if (dgt > 1) {
			int pad = (dgt - 1);
			for (long xa = val; xa > 9 && pad > 0; xa /= 10) {
				pad--;
			}
			for (int xa = 0; xa < pad; xa++) {
				tgt.append('0');
			}
		}
		tgt.append(val);
	}

	/**
	 * Formats a number of milliseconds for display (e.g.
	 * in a log file), tailoring the output to how big
	 * the value actually is.
	 * <p>
	 * Example outputs:
	 * </p>
	 * <ul>
	 * <li>133ms</li>
	 * <li>00:00:10.223</li>
	 * <li>1.7 days</li>
	 * <li>64 days</li>
	 * </ul>
	 */
	public static String formatMillis(long val) {
		StringBuilder buf = new StringBuilder(20);
		if (val < (10 * DateUtils.MILLIS_PER_SECOND)) {
			buf.append(val);
			buf.append("ms");
		} else if (val >= DateUtils.MILLIS_PER_DAY) {
			double days = (double) val / DateUtils.MILLIS_PER_DAY;
			if (days >= 10) {
				buf.append(TEN_DAY_FORMAT.format(days));
				buf.append(" days");
			} else if (days != 1.0f) {
				buf.append(DAY_FORMAT.format(days));
				buf.append(" days");
			} else {
				buf.append(DAY_FORMAT.format(days));
				buf.append(" day");
			}
		} else {
			append(buf, "", 2, ((val % DateUtils.MILLIS_PER_DAY) / DateUtils.MILLIS_PER_HOUR));
			append(buf, ":", 2, ((val % DateUtils.MILLIS_PER_HOUR) / DateUtils.MILLIS_PER_MINUTE));
			append(buf, ":", 2, ((val % DateUtils.MILLIS_PER_MINUTE) / DateUtils.MILLIS_PER_SECOND));
			if (val <= DateUtils.MILLIS_PER_MINUTE) {
				append(buf, ".", 3, (val % DateUtils.MILLIS_PER_SECOND));
			}
		}
		return buf.toString();
	}

	private static long now() {
		if (ourNowForUnitTest != null) {
			return ourNowForUnitTest;
		}
		return System.currentTimeMillis();
	}

	@VisibleForTesting
	static void setNowForUnitTestForUnitTest(Long theNowForUnitTest) {
		ourNowForUnitTest = theNowForUnitTest;
	}

}
