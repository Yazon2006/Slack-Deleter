package com.deleter;

public class Properties {
    private static Properties instance;
    private Browsers browser = Browsers.CHROME;
    private boolean isRunLocally = true;

    public enum Browsers {
        CHROME, FIREFOX, HTMLUNIT
    }

    public static Properties getInstance() {
        if (instance == null) {
            instance = new Properties();
        }
        return instance;
    }

    public Browsers getBrowser() {
        return browser;
    }

    public boolean isRunLocally() {
        return isRunLocally;
    }

}
