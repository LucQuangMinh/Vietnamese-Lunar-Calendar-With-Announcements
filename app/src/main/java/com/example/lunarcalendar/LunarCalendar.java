package com.example.lunarcalendar;

public class LunarCalendar {
    
    // Lunar month data from 1900 to 2100
    // Each element represents a year's lunar calendar information
    private static final int[] LUNAR_MONTH_DAYS = {
        0x04bd8, 0x04ae0, 0x0a570, 0x054d5, 0x0d260, 0x0d950, 0x16554, 0x056a0, 0x09ad0, 0x055d2,
        0x04ae0, 0x0a5b6, 0x0a4d0, 0x0d250, 0x1d255, 0x0b540, 0x0d6a0, 0x0ada2, 0x095b0, 0x14977,
        0x04970, 0x0a4b0, 0x0b4b5, 0x06a50, 0x06d40, 0x1ab54, 0x02b60, 0x09570, 0x052f2, 0x04970,
        0x06566, 0x0d4a0, 0x0ea50, 0x06e95, 0x05ad0, 0x02b60, 0x186e3, 0x092e0, 0x1c8d7, 0x0c950,
        0x0d4a0, 0x1d8a6, 0x0b550, 0x056a0, 0x1a5b4, 0x025d0, 0x092d0, 0x0d2b2, 0x0a950, 0x0b557,
        0x06ca0, 0x0b550, 0x15355, 0x04da0, 0x0a5d0, 0x14573, 0x052d0, 0x0a9a8, 0x0e950, 0x06aa0,
        0x0aea6, 0x0ab50, 0x04b60, 0x0aae4, 0x0a570, 0x05260, 0x0f263, 0x0d950, 0x05b57, 0x056a0,
        0x096d0, 0x04dd5, 0x04ad0, 0x0a4d0, 0x0d4d4, 0x0d250, 0x0d558, 0x0b540, 0x0b5a0, 0x195a6,
        0x095b0, 0x049b0, 0x0a974, 0x0a4b0, 0x0b27a, 0x06a50, 0x06d40, 0x0af46, 0x0ab60, 0x09570,
        0x04af5, 0x04970, 0x064b0, 0x074a3, 0x0ea50, 0x06b58, 0x055c0, 0x0ab60, 0x096d5, 0x092e0,
        0x0c960, 0x0d954, 0x0d4a0, 0x0da50, 0x07552, 0x056a0, 0x0abb7, 0x025d0, 0x092d0, 0x0cab5,
        0x0a950, 0x0b4a0, 0x0baa4, 0x0ad50, 0x055d9, 0x04ba0, 0x0a5b0, 0x15176, 0x052b0, 0x0a930,
        0x07954, 0x06aa0, 0x0ad50, 0x05b52, 0x04b60, 0x0a6e6, 0x0a4e0, 0x0d260, 0x0ea65, 0x0d530,
        0x05aa0, 0x076a3, 0x096d0, 0x04afb, 0x04ad0, 0x0a4d0, 0x1d0b6, 0x0d250, 0x0d520, 0x0dd45,
        0x0b5a0, 0x056d0, 0x055b2, 0x049b0, 0x0a577, 0x0a4b0, 0x0aa50, 0x1b255, 0x06d20, 0x0ada0,
        0x14b63, 0x09370, 0x049f8, 0x04970, 0x064b0, 0x168a6, 0x0ea50, 0x06b20, 0x1a6c4, 0x0aae0,
        0x0a2e0, 0x0d2e3, 0x0c960, 0x0d557, 0x0d4a0, 0x0da50, 0x05d55, 0x056a0, 0x0a6d0, 0x055d4,
        0x052d0, 0x0a9b8, 0x0a950, 0x0b4a0, 0x0b6a6, 0x0ad50, 0x055a0, 0x0aba4, 0x0a5b0, 0x052b0,
        0x0b273, 0x06930, 0x07337, 0x06aa0, 0x0ad50, 0x14b55, 0x04b60, 0x0a570, 0x054e4, 0x0d160,
        0x0e968, 0x0d520, 0x0daa0, 0x16aa6, 0x056d0, 0x04ae0, 0x0a9d4, 0x0a2d0, 0x0d150, 0x0f252,
        0x0d520
    };

    private static final int[] SOLAR_TERMS = {
        0, 21208, 42467, 63836, 85337, 107014, 128867, 150921, 173149, 195551, 218072, 240693,
        263343, 285989, 308563, 331033, 353350, 375494, 397447, 419210, 440795, 462224, 483532,
        504758
    };

    /**
     * Convert solar date to Vietnamese lunar date
     * @param day Solar day
     * @param month Solar month (1-12)
     * @param year Solar year
     * @return int array [lunarDay, lunarMonth, lunarYear, isLeapMonth]
     */
    public static int[] convertSolarToLunar(int day, int month, int year) {
        int lunarDay, lunarMonth, lunarYear;
        int leapMonth = 0;
        int daysInMonth = 0;
        
        // Calculate offset days from 1900-01-31 (lunar 1900-01-01)
        int offset = getOffsetDays(day, month, year);
        
        // Find lunar year
        int daysInYear;
        for (lunarYear = 1900; lunarYear < 2101 && offset > 0; lunarYear++) {
            daysInYear = getLunarYearDays(lunarYear);
            offset -= daysInYear;
        }
        
        if (offset < 0) {
            offset += getLunarYearDays(--lunarYear);
        }
        
        // Find leap month
        leapMonth = getLeapMonth(lunarYear);
        
        // Find lunar month
        boolean isLeap = false;
        for (lunarMonth = 1; lunarMonth < 13 && offset > 0; lunarMonth++) {
            // Leap month
            if (leapMonth > 0 && lunarMonth == (leapMonth + 1) && !isLeap) {
                --lunarMonth;
                isLeap = true;
                daysInMonth = getLeapMonthDays(lunarYear);
            } else {
                daysInMonth = getLunarMonthDays(lunarYear, lunarMonth);
            }
            
            offset -= daysInMonth;
            
            // Reset leap month flag
            if (isLeap && lunarMonth == leapMonth) {
                isLeap = false;
            }
        }
        
        if (offset == 0 && leapMonth > 0 && lunarMonth == leapMonth + 1) {
            if (isLeap) {
                isLeap = false;
            } else {
                isLeap = true;
                --lunarMonth;
            }
        }
        
        if (offset < 0) {
            offset += daysInMonth;
            --lunarMonth;
        }
        
        lunarDay = offset + 1;
        
        return new int[]{lunarDay, lunarMonth, lunarYear, isLeap ? 1 : 0};
    }

    /**
     * Calculate offset days from base date (1900-01-31)
     */
    private static int getOffsetDays(int day, int month, int year) {
        int offset = 0;
        
        // Days from 1900 to target year
        for (int i = 1900; i < year; i++) {
            offset += isLeapYear(i) ? 366 : 365;
        }
        
        // Days from January to target month
        for (int i = 1; i < month; i++) {
            offset += getSolarMonthDays(year, i);
        }
        
        // Add target day
        offset += day;
        
        // Subtract base date offset (1900-01-31 = 30 days)
        // Note: 1900-01-31 is lunar 1900-01-01
        // But we need to adjust for the actual lunar calendar calculation
        offset -= 31;
        
        return offset;
    }

    /**
     * Check if solar year is leap year
     */
    private static boolean isLeapYear(int year) {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
    }

    /**
     * Get days in solar month
     */
    private static int getSolarMonthDays(int year, int month) {
        int[] monthDays = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        if (month == 2 && isLeapYear(year)) {
            return 29;
        }
        return monthDays[month - 1];
    }

    /**
     * Get total days in lunar year
     */
    private static int getLunarYearDays(int year) {
        int sum = 348; // 12 months * 29 days
        for (int i = 0x8000; i > 0x8; i >>= 1) {
            sum += (LUNAR_MONTH_DAYS[year - 1900] & i) != 0 ? 1 : 0;
        }
        return sum + getLeapMonthDays(year);
    }

    /**
     * Get leap month (0 = no leap month)
     */
    private static int getLeapMonth(int year) {
        return LUNAR_MONTH_DAYS[year - 1900] & 0xf;
    }

    /**
     * Get days in leap month
     */
    private static int getLeapMonthDays(int year) {
        if (getLeapMonth(year) == 0) {
            return 0;
        }
        return (LUNAR_MONTH_DAYS[year - 1900] & 0x10000) != 0 ? 30 : 29;
    }

    /**
     * Get days in lunar month
     */
    private static int getLunarMonthDays(int year, int month) {
        int daysInMonth = 0;
        daysInMonth = (LUNAR_MONTH_DAYS[year - 1900] & (0x10000 >> month)) != 0 ? 30 : 29;
        return daysInMonth;
    }
}
