package com.campusevent.util;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * Reusable Hibernate SessionFactory Utility.
 * Manages thread-safe application-level SessionFactory lifecycle for Hibernate ORM operations.
 */
public class HibernateUtil {

    private static final SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        try {
            // Create the SessionFactory from hibernate.cfg.xml
            return new Configuration().configure("hibernate.cfg.xml").buildSessionFactory();
        } catch (Throwable ex) {
            System.err.println("Hibernate Initial SessionFactory Creation Failed! Error: " + ex.getMessage());
            ex.printStackTrace();
            throw new ExceptionInInitializerError(ex);
        }
    }

    /**
     * Get the singleton application-level SessionFactory instance.
     * @return SessionFactory object
     */
    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    /**
     * Safely shutdown and release Hibernate SessionFactory resources upon web application stop.
     */
    public static void shutdown() {
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            sessionFactory.close();
            System.out.println("Hibernate SessionFactory closed successfully.");
        }
    }
}
