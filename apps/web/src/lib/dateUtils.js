/**
 * Formats an ISO date string or Date object into a readable date and time string.
 * **Forces the display timezone to America/New_York (EST/EDT).**
 * Includes timezone abbreviation in the time string.
 * 
 * @param {string|Date} dateTimeInput - The date/time to format.
 * @returns {{ date: string, time: string, tz: string }} - Object with formatted date, time strings, and the forced timezone.
 */
export const formatDateTimeForDisplay = (dateTimeInput) => {
  if (!dateTimeInput) return { date: "Date N/A", time: "Time N/A", tz: "N/A" };

  try {
    const dateObj = new Date(dateTimeInput); // Use the original input directly

    // Check for invalid date
    if (isNaN(dateObj.getTime())) {
      console.warn("[formatDateTimeForDisplay] Invalid date input:", dateTimeInput);
      return { date: "Invalid Date", time: "Invalid Time", tz: "Invalid" };
    }

    // --- Force Timezone to America/New_York ---
    const forcedTimezone = 'America/New_York'; 
    // --- End Force Timezone ---

    const dateOptions = { 
      month: "short", 
      day: "numeric", 
      year: "numeric",
      timeZone: forcedTimezone // Use the forced timezone
    };
    const timeOptions = { 
      hour: "2-digit", 
      minute: "2-digit", 
      timeZone: forcedTimezone, // Use the forced timezone
      timeZoneName: 'short' // Add timezone abbreviation (e.g., EDT, EST)
    };

    let formattedDate, formattedTime;

    try {
      // Use Intl.DateTimeFormat with the forced timezone
      // Pass empty array for locale to use browser default for language/date format
      formattedDate = new Intl.DateTimeFormat([], dateOptions).format(dateObj);
      formattedTime = new Intl.DateTimeFormat([], timeOptions).format(dateObj);
    } catch (intlError) {
      console.warn("[formatDateTimeForDisplay] Intl.DateTimeFormat failed, falling back.", intlError);
      // Fallback might not support timeZone option as reliably, but try
      try {
         // Pass empty array for locale to use browser default
         formattedDate = dateObj.toLocaleDateString([], dateOptions); 
         formattedTime = dateObj.toLocaleTimeString([], timeOptions); 
      } catch (fallbackError) {
         console.error("[formatDateTimeForDisplay] Fallback formatting with options failed:", fallbackError);
         // Final, simplest fallback if options cause errors
         formattedDate = dateObj.toLocaleDateString(); 
         formattedTime = dateObj.toLocaleTimeString([], { 
             hour: '2-digit', 
             minute: '2-digit', 
             timeZone: forcedTimezone, // Still try to apply timezone in simplest fallback
             timeZoneName: 'short' 
         }); 
      }
    }
    
    // Log the result for debugging
    console.log("[formatDateTimeForDisplay] Final Output (Forced TZ):", { 
        input: dateTimeInput, 
        interpretedUTC: dateObj.toISOString(), // Show the UTC interpretation
        forcedTimezone: forcedTimezone, 
        formattedDate, 
        formattedTime 
    });

    // Return including the forced timezone
    return { date: formattedDate, time: formattedTime, tz: forcedTimezone }; 
  } catch (error) {
    console.error("[formatDateTimeForDisplay] Error formatting date/time:", dateTimeInput, error);
    return { date: "Format Error", time: "Format Error", tz: "Error" };
  }
};

/**
 * Formats an ISO date string into "Month of Year" format
 * Example: "2024-07-17T01:05:01.499Z" -> "July 2024"
 *
 * @param {string} dateString - ISO date string
 * @param {object} options - Formatting options
 * @returns {string} Formatted date string
 */
export const formatJoinDate = (dateString, options = {}) => {
  if (!dateString) return 'Unknown date';

  try {
    const date = new Date(dateString);
    if (isNaN(date.getTime())) {
      return 'Invalid date';
    }

    const formatter = new Intl.DateTimeFormat('en-US', {
      month: 'long',
      year: 'numeric',
      ...options
    });

    return formatter.format(date);
  } catch (error) {
    console.error('Date formatting error:', error);
    return 'Error formatting date';
  }
};

/**
 * Formats an ISO date string into a relative time format (e.g., "3 months ago")
 *
 * @param {string} dateString - ISO date string
 * @returns {string} Relative time string
 */
export const formatRelativeTime = (dateString) => {
  if (!dateString) return 'Unknown time';

  try {
    const date = new Date(dateString);
    if (isNaN(date.getTime())) {
      return 'Invalid date';
    }

    const now = new Date();
    const diffInMs = now - date;
    const diffInSecs = Math.floor(diffInMs / 1000);
    const diffInMins = Math.floor(diffInSecs / 60);
    const diffInHours = Math.floor(diffInMins / 60);
    const diffInDays = Math.floor(diffInHours / 24);
    const diffInMonths = Math.floor(diffInDays / 30);
    const diffInYears = Math.floor(diffInMonths / 12);

    if (diffInYears > 0) {
      return `${diffInYears} year${diffInYears > 1 ? 's' : ''} ago`;
    } else if (diffInMonths > 0) {
      return `${diffInMonths} month${diffInMonths > 1 ? 's' : ''} ago`;
    } else if (diffInDays > 0) {
      return `${diffInDays} day${diffInDays > 1 ? 's' : ''} ago`;
    } else if (diffInHours > 0) {
      return `${diffInHours} hour${diffInHours > 1 ? 's' : ''} ago`;
    } else if (diffInMins > 0) {
      return `${diffInMins} minute${diffInMins > 1 ? 's' : ''} ago`;
    } else {
      return 'Just now';
    }
  } catch (error) {
    console.error('Date formatting error:', error);
    return 'Error formatting date';
  }
};

/**
 * Formats an ISO date string into a custom format
 *
 * @param {string} dateString - ISO date string
 * @param {object} options - Intl.DateTimeFormat options
 * @returns {string} Formatted date string
 */
export const formatDate = (dateString, options = {}) => {
  if (!dateString) return 'Unknown date';

  try {
    const date = new Date(dateString);
    if (isNaN(date.getTime())) {
      return 'Invalid date';
    }

    const defaultOptions = {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
    };

    const formatter = new Intl.DateTimeFormat('en-US', {
      ...defaultOptions,
      ...options
    });

    return formatter.format(date);
  } catch (error) {
    console.error('Date formatting error:', error);
    return 'Error formatting date';
  }
};