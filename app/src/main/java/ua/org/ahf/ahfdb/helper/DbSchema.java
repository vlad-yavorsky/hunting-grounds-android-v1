package ua.org.ahf.ahfdb.helper;

public class DbSchema {
    public class CompanyTable {
        public static final String NAME = "company";
        public static final String CREATE_SQL = "create table " + NAME +
                "(" +
                Column.ID + " integer primary key, " +
                Column.IS_MEMBER + " integer, " +
                Column.IS_HUNTING_GROUND + " integer, " +
                Column.IS_FISHING_GROUND + " integer, " +
                Column.IS_POND_FARM + " integer, " +
                Column.AREA + " real, " +
                Column.LAT + " real, " +
                Column.LNG + " real, " +
                Column.NAME + " text, " +
                Column.NAME_LOWERCASE + " text, " +
                Column.DESCRIPTION + " text," +
                Column.WEBSITE + " text," +
                Column.EMAIL + " text," +
                Column.JURIDICAL_ADDRESS + " text," +
                Column.ACTUAL_ADDRESS + " text," +
                Column.DIRECTOR + " text," +
                Column.IS_ENABLED + " integer," +
                Column.OBLAST_ID + " integer," +
                Column.LOCALE + " text," +
                Column.PHONE_1 + " text," +
                Column.PHONE_2 + " text," +
                Column.PHONE_3 + " text," +
                Column.FAVORITE + " integer" +
                ")";
        public static final String DELETE_SQL = "drop table if exists " + NAME;

        public class Column {
            public static final String ID = "_id";
            public static final String IS_MEMBER = "is_member";
            public static final String IS_HUNTING_GROUND = "is_hunting_ground";
            public static final String IS_FISHING_GROUND = "is_fishing_ground";
            public static final String IS_POND_FARM = "is_pond_farm";
            public static final String AREA = "area";
            public static final String LAT = "lat";
            public static final String LNG = "lng";
            public static final String NAME = "name";
            public static final String NAME_LOWERCASE = "name_lowercase";
            public static final String DESCRIPTION = "description";
            public static final String WEBSITE = "website";
            public static final String EMAIL = "email";
            public static final String JURIDICAL_ADDRESS = "juridical_address";
            public static final String ACTUAL_ADDRESS = "actual_address";
            public static final String DIRECTOR = "director";
            public static final String IS_ENABLED = "is_enabled";
            public static final String OBLAST_ID = "oblast_id";
            public static final String LOCALE = "locale";
            public static final String PHONE_1 = "phone_1";
            public static final String PHONE_2 = "phone_2";
            public static final String PHONE_3 = "phone_3";
            public static final String FAVORITE = "favorite";
        }
    }

    public class OblastTable {
        public static final String NAME = "oblast";
        public static final String CREATE_SQL = "create table " + NAME +
                "(" +
                Column.ID + " integer primary key, " +
                Column.NAME + " text" +
                ")";
        public static final String DELETE_SQL = "drop table if exists " + NAME;

        public class Column {
            public static final String ID = "_id";
            public static final String NAME = "name";
        }
    }
}