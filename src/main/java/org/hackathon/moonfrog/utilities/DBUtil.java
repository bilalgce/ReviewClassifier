package org.hackathon.moonfrog.utilities;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.hackathon.moonfrog.models.db.Reviews;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

public class DBUtil {
	private SessionFactory factory;
	private static DBUtil dbUtil;

	private DBUtil() {

	}

	public static synchronized DBUtil getInstance() {
		if (dbUtil == null) {
			dbUtil = new DBUtil();
		}
		return dbUtil;

	}

	public void start() {
		try {
			factory = new Configuration().configure().buildSessionFactory();
		} catch (Throwable ex) {
			System.err.println("Failed to create sessionFactory object." + ex);
			throw new ExceptionInInitializerError(ex);
		}

	}

	public void insertUserReview(String name, Date reviewDate, int rating,
			String userReview, double aggregate, String negativeSentiments) {
		Session session = factory.openSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			Reviews reviews = new Reviews();
			reviews.setName(name);
			reviews.setReviewDate(reviewDate);
			reviews.setRating(rating);
			reviews.setUserReview(userReview);
			reviews.setAggregate(aggregate);
			reviews.setNegativeSentiments(negativeSentiments);
			session.save(reviews);
			tx.commit();

		} catch (HibernateException e1) {
			if (tx != null)
				tx.rollback();
			e1.printStackTrace();
		} finally {
			session.close();
		}

	}

	public List<Reviews> getReviews(int rating) {
		Session session = factory.openSession();
		Transaction tx = null;
		String hql = "from Reviews r where r.rating=" + rating;
		List<Reviews> reviews = new ArrayList<Reviews>();
		try {
			tx = session.beginTransaction();

			@SuppressWarnings("unchecked")
			List<Reviews> results = session.createQuery(hql).list();
			for (Iterator iterator = results.iterator(); iterator.hasNext();) {
				Reviews review = (Reviews) iterator.next();
				reviews.add(review);
			}
			tx.commit();
		} catch (HibernateException e) {
			if (tx != null)
				tx.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
		factory.close();
		return reviews;
	}
	

	public void close() {
		factory.close();
	}
}
