package com.andreimironov.criminalintent.databse;

public class CrimeDbSchema {
    public static final class CrimeTable {
        public static final String NAME = "crimes";

        public static final class Cols {
            public static final String UUID = "uuid";
            public static final String TITLE = "title";
            public static final String DATE = "date";
            public static final String SOLVED = "solved";
            public static final String SUSPECT = "suspect";
            public static final String SUSPECT_ID = "suspect_id";
            public static final String HAS_PHONE_NUMBER = "has_phone_number";
            public static final String PHONE_NUMBER= "phone_number";
        }
    }
}