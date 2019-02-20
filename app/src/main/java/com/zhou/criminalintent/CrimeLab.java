package com.zhou.criminalintent;


import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CrimeLab {
    private static CrimeLab sCrimeLab;
    private List<Crime> mCrimes;


    public static CrimeLab getCrimeLab(Context context) {
        if (sCrimeLab == null) {
            sCrimeLab = new CrimeLab(context);
        }
        return sCrimeLab;
    }

    private CrimeLab(Context context) {
        mCrimes = new ArrayList<>();

//        for (int i = 0; i <= 100; i++) {
//            Crime crime = new Crime();
//            crime.setmTitle("Crime # " + i);
//            crime.setmSolved(i % 2 == 0);
//            mCrimes.add(crime);
//        }

    }

    public void addCrime(Crime crime) {
        mCrimes.add(crime);
    }

    public void removeCrime(Crime crime) {
        mCrimes.remove(crime);
    }


    public List<Crime> getCrimes() {
        return mCrimes;
    }

    public Crime getCrime(UUID uuid) {
        for (Crime crime : mCrimes) {
            if (crime.getmId().equals(uuid)) {
                return crime;
            }
        }

        return null;
    }

}
