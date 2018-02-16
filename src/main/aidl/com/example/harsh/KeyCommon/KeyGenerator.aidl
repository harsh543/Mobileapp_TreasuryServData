// KeyGenerator.aidl
package com.example.harsh.KeyCommon;

// Declare any non-default types here with import statements
interface KeyGenerator {
    String monthlyCash(int year);

      String yearlyAvg(int year);
      String dailyCash(int day, int month, int year, int workingDays);
}
