package science.logarithmic.stayfit;


// This class contains constants used for running a Foreground activity

public class Constants {
    public interface ACTION {
        public static String MAIN_ACTION = "science.logarithmic.action.main";
        public static String STARTFOREGROUND_ACTION = "science.logarithmic.action.startforeground";
        public static String STOPFOREGROUND_ACTION = "science.logarithmic.action.stopforeground";
    }

    public interface NOTIFICATION_ID {
        public static int FOREGROUND_SERVICE = 101;
    }
}
