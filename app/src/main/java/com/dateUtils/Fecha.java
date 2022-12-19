package com.dateUtils;

public class Fecha{
    public final int day, month, year;

    public Fecha(int year, int month, int day){
        this.year = year;
        this.month = month;
        this.day = day;
    }

    public boolean greaterThan(Fecha fecha){
        if(this.year > fecha.year) return true;

        if(this.month > fecha.month) return true;

        return this.day > fecha.day;
    }

    public boolean lowerThan(Fecha fecha){
        if(this.year < fecha.year) return true;

        if(this.month < fecha.month) return true;

        return this.day < fecha.day;
    }

    public boolean equals(Fecha fecha){
        if(this.year != fecha.year) return false;

        if(this.month != fecha.month) return false;

        return this.day == fecha.day;
    }

}