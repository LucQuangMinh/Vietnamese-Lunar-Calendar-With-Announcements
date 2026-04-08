package com.example.lunarcalendar;

public class CalendarCell {
    private int solarDay;
    private int lunarDay;
    private int lunarMonth;
    private boolean isCurrentDay;
    private boolean isCurrentMonth;

    public CalendarCell(int solarDay, int lunarDay, int lunarMonth, boolean isCurrentDay, boolean isCurrentMonth) {
        this.solarDay = solarDay;
        this.lunarDay = lunarDay;
        this.lunarMonth = lunarMonth;
        this.isCurrentDay = isCurrentDay;
        this.isCurrentMonth = isCurrentMonth;
    }

    public int getSolarDay() {
        return solarDay;
    }

    public void setSolarDay(int solarDay) {
        this.solarDay = solarDay;
    }

    public int getLunarDay() {
        return lunarDay;
    }

    public void setLunarDay(int lunarDay) {
        this.lunarDay = lunarDay;
    }

    public int getLunarMonth() {
        return lunarMonth;
    }

    public void setLunarMonth(int lunarMonth) {
        this.lunarMonth = lunarMonth;
    }

    public boolean isCurrentDay() {
        return isCurrentDay;
    }

    public void setCurrentDay(boolean currentDay) {
        isCurrentDay = currentDay;
    }

    public boolean isCurrentMonth() {
        return isCurrentMonth;
    }

    public void setCurrentMonth(boolean currentMonth) {
        isCurrentMonth = currentMonth;
    }
}
