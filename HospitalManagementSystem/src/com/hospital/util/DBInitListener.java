package com.hospital.util;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * On web app startup, configures the DB path against the servlet context's real
 * path and runs schema + seed initialization.
 */
@WebListener
public class DBInitListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        String webAppPath = sce.getServletContext().getRealPath("/");
        DBConnection.configure(webAppPath);
        try {
            DBConnection.initialize();
            sce.getServletContext().log("[HMS] Database initialized at " + webAppPath + "hospital.db");
        } catch (Exception e) {
            sce.getServletContext().log("[HMS] Database initialization failed", e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) { /* no-op */ }
}
